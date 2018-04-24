package com.qlp2p.doctorcar.agent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qlp2p.doctorcar.R;
import com.qlp2p.doctorcar.common.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RequestSuccessActivity extends BaseActivity {

    @Bind(R.id.tvTopTitle)
    TextView tvTopTitle;
    @Bind(R.id.tv_content)
    TextView tvContent;

    String title="";
    String content="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_publish_success);
        ButterKnife.bind(this);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        initView();
    }

    private void initView() {
        tvTopTitle.setText(title);
        tvContent.setText(content);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @OnClick({R.id.ivTopBack, R.id.tv_set_price})
    public void onViewClicked(View view) {
        Intent it;
        switch (view.getId()) {
            case R.id.ivTopBack:
            case R.id.tv_set_price:
                finish();
                break;

        }
    }

}
