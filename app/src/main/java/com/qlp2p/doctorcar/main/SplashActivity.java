package com.qlp2p.doctorcar.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qlp2p.doctorcar.R;
import com.qlp2p.doctorcar.common.BaseActivity;
import com.qlp2p.doctorcar.common.Utils;
import com.qlp2p.doctorcar.net.ServerUrl;
import com.qlp2p.doctorcar.net.UpdateAppManager;

import java.util.HashMap;

public class SplashActivity extends BaseActivity {

    int isStartNewVersion = 0; // 0- no new version, 1- upgrade, 2- first install

    private static final int REQ_PERMISSION = 2001;
    private UpdateAppManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        isStartNewVersion = Utils.isStartNewVersion(mContext);

        initView();

        Utils.deleteUserinfo(mContext);
        myglobal.user = Utils.getUserInfo(mContext);

        autoLogin();
        //检查更新
//        manager = new UpdateAppManager(this);
//        manager.getUpdateMsg();

    }

    private void initView() {

    }

    private void nextPage() {
        goLoginPage1();
        finish();
    }

    private void goMainTab() {
        Intent it = new Intent(mContext, MainActivity.class);
        startActivity(it);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    public void autoLogin() {
        String strPhone = Utils.getLoginMobile(mContext);
        String strPwd = Utils.getLoginPwd(mContext);

        if (TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strPwd)) {
            nextPage();
            return;
        }

        if (ServerUrl.isNetworkConnected(mContext)) {
            try {
                HashMap<String, String> fields = new HashMap<String, String>();
                fields.put("phone", strPhone);
                fields.put("password", strPwd);
                postMap(ServerUrl.login, fields, autoLoginHandler);
            } catch (Exception e) {
                e.printStackTrace();
                nextPage();
            }
        } else {
            if (mContext != null)
                shortToast("网络连接不可用");
            nextPage();
        }

    }

    private Handler autoLoginHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> result = (HashMap<String, Object>) msg.obj;
                    try {
                        String status = result.get("result_code") + "";
                        if ("200".equals(status)) {
                            shortToast("登录成功！");
                            Gson gson = new Gson();
                            JSONObject data = JSON.parseObject(gson.toJson(result.get("data")));
                            JSONObject userInfo = data.getJSONObject("userInfo");
                            Utils.setUserInfo(mContext, userInfo.toJSONString());

                            myglobal.user = Utils.getUserInfo(mContext);

                            goMainTab();
                            return;

                        } else {
                            if (mContext != null) {
                                String message = result.get("message") + "";
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    if (mContext != null)
                        shortToast("网络不给力!");
                    break;
            }

            nextPage();
        }

        ;
    };

    private void requestPermission() {

        try {
            if (ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                final String[] tmp = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};
                ActivityCompat.requestPermissions(SplashActivity.this, tmp, REQ_PERMISSION);
                return;
            }
            nextPage();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                nextPage();
            } else {
            }
        }
    }

}
