package com.qlp2p.doctorcar.agent;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qlp2p.doctorcar.R;
import com.qlp2p.doctorcar.common.BaseActivity;
import com.qlp2p.doctorcar.common.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgentOrderActivity extends BaseActivity {

    @Bind(R.id.tv_waiting)
    TextView tvWaiting;
    @Bind(R.id.tv_finished)
    TextView tvFinished;
    @Bind(R.id.tv_order_count)
    TextView tvOrderCount;

    @Bind(R.id.fragment_container)
    FrameLayout fragmentContainer;

    private FragmentTransaction ft;
    BaseFragment waitingFragment = null;
    BaseFragment finishedFragment = null;

    int mSelType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_order);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {


        switchContainer(0);

    }

    private void switchContainer(int type) {
        mSelType = type;
        if (type == 0) {
            tvWaiting.setSelected(true);
            tvFinished.setSelected(false);
        } else {
            tvWaiting.setSelected(false);
            tvFinished.setSelected(true);
        }
        setSelect(type);
    }

    @SuppressLint("CommitTransaction")
    public void setSelect(int i) {

        FragmentManager fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        hideFragments();
        switch (i) {
            case 0:
                if (waitingFragment == null) {
                    waitingFragment = AgentOrderFragment.newInstance(i);
                    ft.add(R.id.fragment_container, waitingFragment);
                }
                ft.show(waitingFragment);
                break;
            case 1:
                if (finishedFragment == null) {
                    finishedFragment = AgentOrderFragment.newInstance(i);
                    ft.add(R.id.fragment_container, finishedFragment);
                }
                ft.show(finishedFragment);
                break;
        }
        ft.commit();
    }

    private void hideFragments() {
        if (waitingFragment != null) {
            ft.hide(waitingFragment);
        }
        if (finishedFragment != null) {
            ft.hide(finishedFragment);
        }
    }

    @OnClick({R.id.tv_waiting, R.id.tv_finished})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_waiting:
                switchContainer(0);
                break;
            case R.id.tv_finished:
                switchContainer(1);
                break;
        }
    }





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
