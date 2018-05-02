package com.qlp2p.doctorcar.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qlp2p.doctorcar.R;
import com.qlp2p.doctorcar.common.BaseActivity;
import com.qlp2p.doctorcar.common.Utils;
import com.qlp2p.doctorcar.net.ServerUrl;
import com.qlp2p.doctorcar.ui.ClickEffectImageView;
import com.qlp2p.doctorcar.ui.ScrollViewExt;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPwdActivity extends BaseActivity {

    String strPhone;
    String smsCode;

    int timeLimit = 120, leftTime = 0;


    @Bind(R.id.tvTopTitle)
    TextView tvTopTitle;
    @Bind(R.id.ivTopBack)
    ClickEffectImageView ivTopBack;
    @Bind(R.id.tvTopRight)
    TextView tvTopRight;
    @Bind(R.id.ivTopRight)
    ClickEffectImageView ivTopRight;
    @Bind(R.id.etPhone)
    EditText etPhone;
    @Bind(R.id.etConfirm)
    EditText etSms;
    @Bind(R.id.tvGetConfirm)
    TextView tvGetConfirm;
    @Bind(R.id.tvSendedConfirm)
    TextView tvSendedConfirm;
    @Bind(R.id.tvNext)
    Button tvNext;
    @Bind(R.id.svMain)
    ScrollViewExt svMain;
    @Bind(R.id.etPwd)
    EditText etPwd;
    @Bind(R.id.etPwdConfirm)
    EditText etPwdConfirm;

    int type = 0; //0-忘记密码 1-修改密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd1);
        ButterKnife.bind(this);

        type = getIntent().getIntExtra("type", 0);
        initView();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void initView() {
        tvTopTitle.setText("密码管理");
        if(type  == 1){
            etPhone.setText(myglobal.user.userPhone);
            etPhone.setEnabled(false);
        }
    }


    public void next() {
        String pass = etPwd.getText().toString().trim();
        String confirmpass = etPwdConfirm.getText().toString().trim();
        smsCode = etSms.getText().toString().trim();

        if (Utils.isEmpty(strPhone)) {
            shortToast("请输入手机号～～");
            return;
        }

        if (!Utils.checkMobileNO(strPhone)) {
            shortToast("手机号不正确～～");
            return;
        }

        if (Utils.isEmpty(smsCode)) {
            etSms.requestFocus();
            shortToast("请输入验证码～～");
            return;
        }
        if (Utils.isEmpty(pass)) {
            shortToast("请输入密码～～");
            etPwd.requestFocus();
            return;
        }
        if (Utils.isEmpty(confirmpass)) {
            shortToast("请再输入密码～～");
            etPwdConfirm.requestFocus();
            return;
        }
        if (!pass.equals(confirmpass)){
            shortToast("密码不匹配，请再输入密码～～");
            etPwdConfirm.requestFocus();
            return;
        }

        if(ServerUrl.isNetworkConnected(this)){
            try{
                HashMap<String, String> fields = new HashMap<String, String>();
                if(type == 1)
                    fields.put("token", myglobal.user.token);
                fields.put("phone", strPhone);
                fields.put("password", pass);
                fields.put("sms", smsCode);
                setThread_flag(true);
                if(type == 0)
                    postMap(ServerUrl.findPwd, fields, pwdHandler);
                else
                    postMap(ServerUrl.resetPwd, fields, pwdHandler);
                showProgress();
            }catch(Exception e){
                setThread_flag(false);
                e.printStackTrace();
            }
        }else{
            if(mContext!=null)
                shortToast("网络连接不可用");
        }
    }
    private Handler pwdHandler = new Handler() {

        public void handleMessage(Message msg) {
            setThread_flag(false);
            hideProgress();

            switch (msg.what) {
                case 0:
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> result = (HashMap<String, Object>) msg.obj;
                    try {
                        String status = result.get("result_code") + "";
                        if ("200".equals(status)) {
                            if (type == 0) {
                                shortToast("找回密码成功");
                                finish();
                            } else {
                                shortToast("修改密码成功");
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                intent.putExtra("type", 1);
                                startActivity(intent);
                                finish();
                            }
                        }else if("401".equals(status)){
                            String message = result.get("message") + "";
                            shortToast(message);
                            goLoginPage();
                        } else {
                            String message = result.get("message") + "";
                            shortToast(message);
                        }
                    } catch (Exception e) {
                        shortToast("抱歉数据异常");
                    }
                    break;
                default:
                    if (mContext != null)
                        shortToast("网络不给力!");
                    break;
            }
        }

    };


    public void getConfirm() {
        if (getThread_flag()) return;
        if (leftTime > 0) return;
        setThread_flag(true);
        getValidCode();
    }

    private void getValidCode() {
        strPhone = etPhone.getText().toString();

        if (Utils.isEmpty(strPhone)) {
            shortToast("请输入手机号～～");
            return;
        }

        if (!Utils.checkMobileNO(strPhone)) {
            shortToast("手机号不正确～～");
            return;
        }

        if(ServerUrl.isNetworkConnected(this)){
            try{
                showProgress();
                HashMap<String, String> fields = new HashMap<String, String>();
                fields.put("phone", strPhone);
                postMap(ServerUrl.getSmsCode, fields, getValidCodeHandler);
            }catch(Exception e){
                e.printStackTrace();
                setThread_flag(false);
            }
        }else{
            if(mContext!=null)
                shortToast("网络连接不可用");
            setThread_flag(false);
        }

    }

    private Handler getValidCodeHandler = new Handler() {

        public void handleMessage(Message msg) {
            setThread_flag(false);
            hideProgress();
            switch (msg.what) {
                case 0:
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> result = (HashMap<String, Object>) msg.obj;
                    try {
                        String status = result.get("result_code") + "";
                        if ("200".equals(status)) {
                            shortToast("验证码已发送～～");
                            tvSendedConfirm.setText("我们将发送验证码短信到号码：" + strPhone);
                            tvSendedConfirm.setVisibility(View.VISIBLE);
                            countTime(timeLimit);
                        } else {
                            if (mContext != null) {
                                String message = result.get("message") + "";
                                shortToast(message);
                            }
                        }
                    } catch (Exception e) {
                        shortToast("抱歉数据异常");
                    }
                    break;
                default:
                    if (mContext != null)
                        shortToast("网络不给力!");
                    break;
            }
        }

        ;
    };

    private void countTime(int time) {
        if (time < 1) {
            tvGetConfirm.setText("获取验证码");
            return;
        }

        tvGetConfirm.setText(time + "S");
        time--;
        leftTime = time;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                countTime(leftTime);
            }
        }, 1000);
    }

    @OnClick({R.id.ivTopBack, R.id.tvGetConfirm, R.id.tvNext})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivTopBack:
                finish();
                break;
            case R.id.tvGetConfirm:
                getConfirm();
                break;
            case R.id.tvNext:
                next();
                break;
        }
    }

}
