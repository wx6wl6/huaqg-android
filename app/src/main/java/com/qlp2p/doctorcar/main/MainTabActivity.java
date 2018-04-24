package com.qlp2p.doctorcar.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qlp2p.doctorcar.R;
import com.qlp2p.doctorcar.agent.AgentOrderActivity;
import com.qlp2p.doctorcar.agent.AgentPjOrderDetailActivity;
import com.qlp2p.doctorcar.agent.AgentWodeActivity;
import com.qlp2p.doctorcar.appraiser.AppriserOrderActivity;
import com.qlp2p.doctorcar.appraiser.AppriserWodeActivity;
import com.qlp2p.doctorcar.appraiser.JingZhenGuActivity;
import com.qlp2p.doctorcar.checker.CheckerOrderActivity;
import com.qlp2p.doctorcar.checker.CheckerWodeActivity;
import com.qlp2p.doctorcar.common.MyConstants;
import com.qlp2p.doctorcar.common.MyGlobal;
import com.qlp2p.doctorcar.common.Utils;
import com.qlp2p.doctorcar.data.MessageEvent;
import com.qlp2p.doctorcar.data.Province;
import com.qlp2p.doctorcar.db.LocalCityTable;
import com.qlp2p.doctorcar.home.HomeActivity;
import com.qlp2p.doctorcar.net.ServerUrl;
import com.wangli.FinalHttp;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

public class MainTabActivity extends TabActivity {
    private static final int REQ_PERMISSION = 2001;
    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;
    private boolean isLocating = false;


    protected MyGlobal myglobal;
    TabHost tabHost;
    Context mContext;
    boolean isFinish = false;
    boolean bFinish = false;

    public static final String LOGIN_SUCCESS = "loginSuccess";

    ImageView ivPublish;
    protected FinalHttp finalHttp;
    public ImageLoader imageLoader = ImageLoader.getInstance();
    public DisplayImageOptions optionsUser = null;

    MyBroadcastReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        mContext = this;

        myglobal = (MyGlobal)getApplicationContext();
        myglobal.mainActivity = this;
        finalHttp = FinalHttp.getInstance();
        Utils.putIntPreferences(mContext, "CURRENT_TAB_ID", 0);
        if(myglobal.SCR_WIDTH== 0 || savedInstanceState != null) {
            Utils.setCachePath(mContext);
            Utils.setupUnits(mContext);
            myglobal.user = Utils.getUserInfo(mContext);
        }
        initView();
        initBroadcast();

        initLocationService();

        getCityInfo();
    }

    private void initBroadcast() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(MyConstants.LOGOUT);
        myReceiver = new MyBroadcastReceiver();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(myReceiver, myIntentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView(){
        ivPublish = (ImageView) findViewById(R.id.iv_publish);

        tabHost = getTabHost();
        if("4".equals(myglobal.user.userType)){//业务员
            addTab("订单", R.drawable.selector_tab1, AgentOrderActivity.class);
            addTab("我的", R.drawable.selector_tab3, AgentWodeActivity.class);
        }else if("5".equals(myglobal.user.userType)){//评估员
            addTab("订单", R.drawable.selector_tab1, AppriserOrderActivity.class);
            addTab("车开通", R.drawable.selector_tab2, HomeActivity.class);
            addTab("我的", R.drawable.selector_tab3, AppriserWodeActivity.class);

            ivPublish.setVisibility(View.GONE);
        }else if("6".equals(myglobal.user.userType)){//检测员
            addTab("订单", R.drawable.selector_tab1, CheckerOrderActivity.class);
            addTab("车开通", R.drawable.selector_tab2, HomeActivity.class);
            addTab("我的", R.drawable.selector_tab3, CheckerWodeActivity.class);

            ivPublish.setVisibility(View.GONE);
        }
        //-------------------------------此处为首次进入显示平台的---------------------------------//
//		tabHost.setCurrentTab(2);
//		findViewById(android.R.id.tabs).setVisibility(View.GONE);
        tabHost.getTabWidget().setDividerDrawable(null);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                int id = tabHost.getCurrentTab();
//                if(id == 2 ) {
//                    if(myglobal.user == null || !Utils.isValid(myglobal.user.id)) {
//                        setTab(Utils.getIntPreferences(mContext, "CURRENT_TAB_ID"));
//                        Intent it = new Intent(mContext, LoginActivity.class);
//                        startActivity(it);
//                        return;
//                    }
//                }
                Utils.putIntPreferences(mContext, "CURRENT_TAB_ID", id);
            }
        });

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

        findViewById(R.id.iv_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent it = new Intent(mContext, AgentPjOrderDetailActivity.class);
                Intent it = new Intent(mContext, JingZhenGuActivity.class);
                startActivity(it);
            }
        });
    }

    public void initLocationService(){

//        locationService = myglobal.locationService;
        if(!isLocating){
            getPersimmions();
        }
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }else{
                getLocation();
            }
        }else{
            getLocation();
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }
        }else{
            return true;
        }
    }


    protected void postMap(String url, HashMap<String, String> fields, Handler handler) {
        finalHttp.postMap(url, fields, handler);
    }
    private  void getCityInfo(){
        if (LocalCityTable.getInstance().getAllProvince().size() == 0 || Utils.getIntPreferences(mContext, "newVersion") == 1) {
            if (ServerUrl.isNetworkConnected(this)) {
                try {
                    HashMap<String, String> fields = new HashMap<String, String>();
                    postMap(ServerUrl.getRegionList, fields, cityHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
            }
        }
    }
    private Handler cityHandler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> result = (HashMap<String, Object>) msg.obj;
                    try {
                        String status = result.get("result_code") + "";
                        if ("200".equals(status)) {

                            Gson gson = new Gson();
                            JSONObject data = JSON.parseObject(gson.toJson(result.get("data")));

                            JSONArray list = data.getJSONArray("areas");
                            ArrayList<Province> areas = new ArrayList<>();
                            for (int i = 0; i < list.size(); i++) {
                                areas.add(new Province((JSONObject) list.get(i)));
                            }
                            LocalCityTable.getInstance().insertCityList(areas);
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

    };

    private void addTab(String tagId, int drawableId, Class<?> classId) {
        Intent intent = new Intent(this, classId);
        TabHost.TabSpec spec = tabHost.newTabSpec(tagId);
        LayoutInflater vi = (LayoutInflater) getBaseContext().getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);
        View tabIndicator = vi.inflate(R.layout.tab_indicator, null);
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.ivTabIcon);
        icon.setImageResource(drawableId);
        TextView title = (TextView) tabIndicator.findViewById(R.id.ivTabTitle);
        title.setText(tagId);
        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.addTab(spec);
    }

    private void setTab(int tabId) {
        tabHost.setCurrentTab(tabId);
    }

    @Override
    protected void onDestroy() {
        myglobal.mainActivity = null;
        if (myReceiver != null) {
            if (myReceiver != null)
                LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        }
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if( event.getAction() == KeyEvent.ACTION_DOWN ){
            if( keyCode == KeyEvent.KEYCODE_BACK ){
                if(isFinish){
                    bFinish = true;
                    finish();
                }
                else{
                    Toast.makeText(this, "再按一次退出"+getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                    isFinish = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isFinish = false;
                        }
                    }, 3000);
                }
                return false;
            }
        }
        return super.onKeyDown( keyCode, event );
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equals(MyConstants.LOGOUT)) {

                    Utils.setUserInfo(mContext, "token", "");
                    Utils.setUserInfo(mContext, "userId", "");
                    Utils.setUserInfo(mContext, "userPhone", "");

                    Utils.setLoginMobile(mContext, "");
                    Utils.setLoginPwd(mContext, "");

                    myglobal.user.userId = "";

                    Intent it = new Intent(mContext, LoginActivity.class);
                    startActivity(it);

                    finish();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MessageEvent event = new MessageEvent(MyConstants.PERMISSION_GRANTED);
                EventBus.getDefault().post(event);
            }
        }else if(requestCode == SDK_PERMISSION_REQUEST){
            getLocation();
        }
    }

    private void getLocation() {
//
//        option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
//        option.setCoorType(MyConstants.CoorType_GCJ02);
//        option.setScanSpan(3600000);
//        option.setIsNeedAddress(true);
//        option.setIsNeedLocationPoiList(false);
//        option.setIsNeedLocationDescribe(false);
//        option.setNeedDeviceDirect(false);
//        locationService.setLocationOption(option);
//
//        locationService.registerListener(mListener);
//        locationService.setLocationOption(locationService.getOption());
//
//        locationService.start();// 定位SDK
    }


    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
//        locationService.unregisterListener(mListener); //注销掉监听
//        locationService.stop(); //停止定位服务
        super.onStop();
    }
    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
//    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {
//
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            // TODO Auto-generated method stub
//            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
//                StringBuffer sb = new StringBuffer(256);
//                sb.append("time : ");
//                /**
//                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
//                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
//                 */
//                sb.append(location.getTime());
//                sb.append("\nlocType : ");// 定位类型
//                sb.append(location.getLocType());
//                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
//                sb.append(location.getLocTypeDescription());
//                sb.append("\nlatitude : ");// 纬度
//                sb.append(location.getLatitude());
//                sb.append("\nlontitude : ");// 经度
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");// 半径
//                sb.append(location.getRadius());
//                sb.append("\nCountryCode : ");// 国家码
//                sb.append(location.getCountryCode());
//                sb.append("\nCountry : ");// 国家名称
//                sb.append(location.getCountry());
//                sb.append("\ncitycode : ");// 城市编码
//                sb.append(location.getCityCode());
//                sb.append("\ncity : ");// 城市
//                sb.append(location.getCity());
//                sb.append("\nDistrict : ");// 区
//                sb.append(location.getDistrict());
//                sb.append("\nStreet : ");// 街道
//                sb.append(location.getStreet());
//                sb.append("\naddr : ");// 地址信息
//                sb.append(location.getAddrStr());
//
//                updateLocation(location.getLongitude(), location.getLatitude(), location.getCity() + location.getDistrict() + location.getStreet());
//            }
//        }
//    };

    private  void updateLocation(double lng, double lat, String addr){
        if(!"6".equals(myglobal.user.userType)) return;
        if ("1".equals(myglobal.user.isOnline)) {
            if (ServerUrl.isNetworkConnected(this)) {
                try {
                    HashMap<String, String> fields = new HashMap<String, String>();
                    fields.put("token", myglobal.user.token);
                    fields.put("lng", lng + "");
                    fields.put("lat", lat + "");
                    fields.put("location", addr);
                    postMap(ServerUrl.updateLocation, fields, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
            }
        }
    }
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> result = (HashMap<String, Object>) msg.obj;
                    try {
                        String status = result.get("result_code") + "";
                        if ("200".equals(status)) {

                        }else if("401".equals(status)){
                            String message = result.get("message") + "";
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(mContext, LoginActivity.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            it.putExtra("type", 1);
                            startActivity(it);
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

    };

}
