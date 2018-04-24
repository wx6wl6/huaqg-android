package com.qlp2p.doctorcar.common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.qlp2p.doctorcar.R;
import com.qlp2p.doctorcar.common.MyBaseDialog;
import com.qlp2p.doctorcar.common.MyGlobal;
import com.qlp2p.doctorcar.data.MessageEvent;
import com.qlp2p.doctorcar.main.LoginActivity;
import com.qlp2p.doctorcar.net.MyHttpConnection;
import com.wangli.FinalHttp;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private final  String mPageName = "AnalyticsHome";

    public static final int REQUEST_CODE_SELECT_AREA  = 0x20;
    public static final int REQUEST_CODE_GALLERY      = 0x21;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x22;
    public static final int REQUEST_CODE_CROP_IMAGE   = 0x23;
    public static final int REQUEST_VIEW_PHOT0        = 0x24;

    protected MyGlobal myglobal;
    protected Context mContext;
    private MyBaseDialog progress = null;

    protected FinalHttp finalHttp;

    // The top level content view.
    private ViewGroup m_contentView = null;

    private static ProgressDialog prog = null;

    public ImageLoader imageLoader = ImageLoader.getInstance();
    public DisplayImageOptions options = null;
    public DisplayImageOptions optionsEmpty = null;
    public DisplayImageOptions optionsUser = null;

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

        if(myglobal.SCR_WIDTH== 0 || savedInstanceState != null) {
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
        super.onResume();
        if(myglobal == null) myglobal = (MyGlobal)getApplicationContext();
        if(myglobal.SCR_WIDTH== 0) {
            Utils.setCachePath(mContext);
            Utils.setupUnits(mContext);
            myglobal.user = Utils.getUserInfo(mContext);
        }
        MyGlobal.wifiConnected = Utils.canConnect(mContext, true);
        MyGlobal.picOnlyOnWifi = Utils.getBooleanPreferences(mContext, "picMode3G");
        initImageOption();

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
        try {
            Utils.nullViewDrawablesRecursive(m_contentView);
            m_contentView = null;
            mContext = null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        super.onDestroy();
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

//    protected void showWaitingView(){
//        if(prog!=null)	prog.dismiss();
//        prog = new ProgressDialog(mContext, AlertDialog.THEME_HOLO_LIGHT);
//        prog.setTitle("");
//        prog.setMessage("请稍等。。。");
//        prog.setCancelable(true);
//        prog.show();
//    }
//
//    protected void hideWaitingView(){
//        if(prog!=null){
//            prog.dismiss();
//            prog = null;
//        }
//    }



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
                    .showImageOnLoading(R.drawable.icon_user_def)
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
//                    .showImageOnFail(R.drawable.default_noimage)
//                    .showImageOnLoading(R.drawable.default_noimage)
//                    .showImageForEmptyUri(R.drawable.default_noimage)
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
                else
                    imageview.setBackgroundColor(Color.WHITE);
            }
        }
    }

    public void showLocalImageByLoader(String imageUrl, ImageView imageview, DisplayImageOptions options, int default_type){

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
            else
                imageview.setBackgroundColor(Color.WHITE);
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
        return Utils.isValid(MyGlobal.getInstance().user.token);
    }

    boolean isfirst = true;

    public void goLoginPage1() {
        //if(isLogin()) return;
        Intent it = new Intent(mContext, LoginActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
    }
    public void goLoginPage() {
        //if(isfirst) {
            Intent it = new Intent(mContext, LoginActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra("type", 1);
            startActivity(it);
            isfirst = false;
//        }else {
//
//        }
    }

    protected  void showImgWithGlid(String url, ImageView imageView, int defRes){
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .placeholder(defRes)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }
    protected  void showImgWithGlid(String url, ImageView imageView){
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    protected void postEvent(MessageEvent event){
        EventBus.getDefault().post(event);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    protected void postMap(String url, HashMap<String, String> fields, Handler handler) {
        finalHttp.postMap(url, fields, handler);
    }

    protected void getMap(String url, Handler handler) {
        finalHttp.getMap(url, handler);
    }

    protected void uploadMap(String url, HashMap<String, String> fields, HashMap<String, File> files, Handler handler, int timeout) {
        finalHttp.uploadMap(url, fields, files, handler, timeout);
    }

    public void showProgress() {
        if(mContext == null) return;

        try {
            progress = new MyBaseDialog(mContext, R.style.Theme_Transparent, "dlgProgress", "请稍等!");
            progress.show();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void hideProgress(){
        try {
            if(progress != null){
                progress.dismiss();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
