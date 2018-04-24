package com.qlp2p.doctorcar.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qlp2p.doctorcar.R;
import com.qlp2p.doctorcar.common.BaseActivity;
import com.qlp2p.doctorcar.common.Utils;
import com.qlp2p.doctorcar.data.UserInfo;
import com.qlp2p.doctorcar.net.ServerUrl;
import com.qlp2p.doctorcar.ui.ScrollViewExt;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.etPhone)
    EditText etPhone;
    @Bind(R.id.etPwd)
    EditText etPwd;
    @Bind(R.id.tvLogin)
    Button tvLogin;
    @Bind(R.id.tvResetPwd)
    TextView tvResetPwd;
    @Bind(R.id.tvTopTitle)
    TextView tvTopTitle;
    @Bind(R.id.ll_top)
    LinearLayout llTop;
    @Bind(R.id.svMain)
    ScrollViewExt svMain;


    String strPhone, strPwd;


    int type = 0; //0-启动时，1-修改密码后

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        type = getIntent().getIntExtra("type", 0);

        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        if(type == 0){
            llTop.setVisibility(View.GONE);
        }else{
            tvTopTitle.setText("");
            llTop.setVisibility(View.VISIBLE);
            tvResetPwd.setVisibility(View.GONE);
        }
    }


    @OnClick({R.id.tvLogin, R.id.tvResetPwd, R.id.ivTopBack, R.id.ll_content})
    public void onClick(View view) {
        Intent it;
        switch (view.getId()) {
            case R.id.ivTopBack:
                finish();
                break;
            case R.id.tvLogin:
                login();
                break;
            case R.id.tvResetPwd:
                it = new Intent(mContext, ResetPwdActivity.class);
                startActivity(it);
                break;
            case R.id.ll_content:
                View v= this.getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                break;
        }
    }

    private void login() {
        strPhone = etPhone.getText().toString().trim();
        strPwd = etPwd.getText().toString().trim();

        if (Utils.isEmpty(strPhone)) {
            shortToast("请输入手机号！");
            return;
        }

        if (!Utils.checkMobileNO(strPhone)) {
            shortToast("输入的手机号不正确！");
            return;
        }

        if (Utils.isEmpty(strPwd)) {
            shortToast("请输入密码！");
            return;
        }

        if (ServerUrl.isNetworkConnected(mContext)) {
            try {
                showProgress();
                HashMap<String, String> fields = new HashMap<String, String>();
                fields.put("phone", strPhone);
                fields.put("password", strPwd);
                postMap(ServerUrl.login, fields, handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (mContext != null)
                shortToast("网络连接不可用");
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            hideProgress();
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

                            Utils.setLoginType(mContext, UserInfo.LOGIN_MOBILE);
                            Utils.setLoginMobile(mContext, strPhone);
                            Utils.setLoginPwd(mContext, strPwd);


                            goMainTab();

                        } else {
                            if (mContext != null) {
                                String message = result.get("message") + "";
                                shortToast(message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        shortToast("抱歉数据异常");
                    }
                    break;
                default:
                    if (mContext != null)
                        shortToast("网路不给力!");
                    break;
            }
        }

        ;
    };

    private void goMainTab() {
        Intent it = new Intent(mContext, MainTabActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
        finish();
    }


}
