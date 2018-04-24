package com.qlp2p.doctorcar.common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.wangli.FinalHttp;
import com.qlp2p.doctorcar.R;
import com.qlp2p.doctorcar.net.MyHttpConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qlp2p.doctorcar.net.ServerUrl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

public class BaseFragmentActivity extends FragmentActivity implements View.OnClickListener {

    protected MyGlobal myglobal;
    protected Context mContext;

    protected FinalHttp finalHttp;

    // The top level content view.
    private ViewGroup m_contentView = null;

    private static ProgressDialog prog = null;

    public ImageLoader imageLoader = ImageLoader.getInstance();
    public DisplayImageOptions options = null;
    public DisplayImageOptions optionsEmpty = null;
    public DisplayImageOptions optionsUser = null;
    public DisplayImageOptions optionsBook = null;

    private boolean thread_flag = false;
    private final Object lock_thread_flag = new Object();
    public void setThread_flag(boolean value){
        synchronized (lock_thread_flag) {
            thread_flag = value;
        }
    }
    public boolean getThread_flag(){
        synchronized (lock_thread_flag) {
            return thread_flag;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myglobal = (MyGlobal)getApplicationContext();
        mContext = this;

        if(myglobal.SCR_WIDTH== 0) {
            Utils.setCachePath(mContext);
            Utils.setupUnits(mContext);
            myglobal.user = Utils.getUserInfo(mContext);
        }
        MyGlobal.wifiConnected = Utils.canConnect(mContext, true);
        MyGlobal.picOnlyOnWifi = Utils.getBooleanPreferences(mContext, "picMode3G");
        initImageOption();

        finalHttp = FinalHttp.getInstance();
    }

    @Override
    protected void onResume()
    {
        System.gc();
        if(myglobal == null) myglobal = (MyGlobal)getApplicationContext();
        if(myglobal.SCR_WIDTH== 0) {
            Utils.setCachePath(mContext);
            Utils.setupUnits(mContext);
            myglobal.user = Utils.getUserInfo(mContext);
        }
        MyGlobal.wifiConnected = Utils.canConnect(mContext, true);
        MyGlobal.picOnlyOnWifi = Utils.getBooleanPreferences(mContext, "picMode3G");
        initImageOption();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        System.gc();
    }

    @Override
    public void setContentView(int layoutResID) {
        View mainView = (View) LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(mainView);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        m_contentView = (ViewGroup)view;
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        m_contentView = (ViewGroup)view;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try {
            Utils.nullViewDrawablesRecursive(m_contentView);
            m_contentView = null;
            mContext = null;
        } catch (Exception e) {
            // TODO: handle exception
        }
        System.gc();
    }

    @Override
    public void finish() {
        try {
            Utils.hideKeyboard(mContext);
        } catch (Exception e) {
            // TODO: handle exception
        }

        super.finish();
    }

    @Override
    public void onClick(View v) {

    }

    protected void showWaitingView(){
        if(prog!=null)	prog.dismiss();
        prog = new ProgressDialog(mContext, AlertDialog.THEME_HOLO_LIGHT);
        prog.setTitle("");
        prog.setMessage("请稍等。。。");
        prog.setCancelable(false);
        prog.show();
    }

    protected void hideWaitingView(){
        if(prog!=null){
            prog.dismiss();
            prog = null;
        }
    }

    public Handler httpHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");
            int state = msg.getData().getInt("state");
            String response = msg.getData().getString("content");

            if(mContext == null) {
                HandlerDone(type, state, response);
                return;
            }

            JSONObject jsContentObj = null;
            if(state != MyHttpConnection.NET_RESULT_ERROR_SERVER){
                try {
                    jsContentObj = JSON.parseObject(response);
                    if(jsContentObj != null)
                        state = MyHttpConnection.NET_RESULT_OK;
                    else
                        state = MyHttpConnection.NET_RESULT_ERROR_CONTENT;
                } catch (Exception e) {
                    state = MyHttpConnection.NET_RESULT_ERROR_CONTENT;
                }
            }

            setThread_flag(false);
            hideWaitingView();
            if (state == MyHttpConnection.NET_RESULT_ERROR_SERVER) {
                Toast.makeText(mContext,	R.string.error_msg_network, Toast.LENGTH_SHORT).show();
            }else if(state == MyHttpConnection.NET_RESULT_ERROR_CONTENT){
                Toast.makeText(mContext,	R.string.error_msg_content, Toast.LENGTH_SHORT).show();
            }else if(state == MyHttpConnection.NET_RESULT_OK){
                String result = jsContentObj.getString("status");
                String message = jsContentObj.getString("message");

                if(result == null) {
                    HandlerDone(type, state, response);
                    return;
                }

                if(result.equals("1")){
                    HandlerCallBack(type, jsContentObj);
                } else {
                    Toast.makeText(mContext,	message, Toast.LENGTH_SHORT).show();
                }
            }

            HandlerDone(type, state, response);
        }
    };

    public void HandlerCallBack(int type, JSONObject jsContentObj) {}

    public void HandlerDone(int type, int state, String response) {}

    private void initImageOption(){
        if(options == null){
            if(options == null){
                options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.drawable.default_icon)
                        .showImageForEmptyUri(R.drawable.default_icon)
                        .showImageOnFail(R.drawable.default_icon)
                        .cacheInMemory(true)
                        .cacheOnDisc(true)
                        .considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
            }
        }

        if(optionsUser == null) {
            optionsUser = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.icon_user_def)
                    .showImageOnFail(R.drawable.icon_user_def)
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        if(optionsEmpty == null) {
            optionsEmpty = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.drawable.default_icon)
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

    }

    public void showImageByLoader(String imageUrl, ImageView imageview, DisplayImageOptions options, int default_type){

        if(MyGlobal.wifiConnected || !MyGlobal.picOnlyOnWifi)
            imageLoader.displayImage(imageUrl, imageview, options);
        else{
            File file = imageLoader.getDiscCache().get(imageUrl);
            if(file != null && file.exists() && file.isFile()){
                imageLoader.displayImage(imageUrl, imageview, options);
            }
            else{
                if(default_type == 1)
                    imageview.setImageResource(R.drawable.default_noimage);
                else if(default_type == 2)
                    imageview.setImageResource(R.drawable.default_icon);
                else if(default_type == 3)
                    imageview.setImageResource(R.drawable.icon_user_def);
            }
        }
    }

    public void showImageByLoader(String imageUrl, ImageView imageview, DisplayImageOptions options, int default_type, SimpleImageLoadingListener listener){
        if(MyGlobal.wifiConnected || !MyGlobal.picOnlyOnWifi)
            imageLoader.displayImage(imageUrl, imageview, options, listener);
        else{
            File file = imageLoader.getDiscCache().get(imageUrl);
            if(file != null && file.exists() && file.isFile()){
                imageLoader.displayImage(imageUrl, imageview, options, listener);
            }
            else{
                if(default_type == 1)
                    imageview.setImageResource(R.drawable.default_noimage);
                else
                    imageview.setImageResource(R.drawable.default_icon);
            }
        }
    }

    public void shortToast(String msg){
        if(mContext == null) return;
        Toast.makeText(mContext,	msg, Toast.LENGTH_SHORT).show();
    }

    public void longToast(String msg){
        if(mContext == null) return;
        Toast.makeText(mContext,	msg, Toast.LENGTH_LONG).show();
    }

    public boolean isLogin(){
        if (MyGlobal.getInstance().user == null) return false;
        return Utils.isValid(MyGlobal.getInstance().user.userId);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    protected void postMap(String url, HashMap<String, String> fields, Handler handler) {
        fields.put("timestamp", new Date().getTime()+""); //时间戳
        String preSignStr = ServerUrl.getCodeStr(fields, "&") + "ZFKJ_MLWH_TEST_APP_MD5_SIGN&KEY*()_+"; //获取待加密串
        fields.put("sign", ServerUrl.MD5(preSignStr));
        finalHttp.postMap(url, fields, handler);
    }
}
