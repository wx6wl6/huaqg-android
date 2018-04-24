package com.qlp2p.doctorcar.agent;

import android.os.Bundle;

import com.qlp2p.doctorcar.R;
import com.qlp2p.doctorcar.common.BaseActivity;

import butterknife.ButterKnife;

public class AgentMainActivity extends BaseActivity {

    /****/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_order);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
    }



}
