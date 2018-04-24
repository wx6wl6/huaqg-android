package com.qlp2p.doctorcar.appraiser;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qlp2p.doctorcar.R;
import com.qlp2p.doctorcar.agent.AgentPjOrderDetailActivity;
import com.qlp2p.doctorcar.agent.BrandListActivity;
import com.qlp2p.doctorcar.common.BaseActivity;
import com.qlp2p.doctorcar.common.ButCommonUtils;
import com.qlp2p.doctorcar.common.MyConstants;
import com.qlp2p.doctorcar.common.Utils;
import com.qlp2p.doctorcar.data.MessageEvent;
import com.qlp2p.doctorcar.data.Province;
import com.qlp2p.doctorcar.db.LocalCityTable;
import com.qlp2p.doctorcar.net.ServerUrl;
import com.qlp2p.doctorcar.popup.SelectAreaPopWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 123456 on 2018/3/7.
 */

public class JingZhenGuActivity extends BaseActivity implements SelectAreaPopWindow.OnSelectListener {

    @Bind(R.id.jz_car_type)
    TextView jzCarType;
    @Bind(R.id.jz_car_area)
    TextView tvCarArea;
    @Bind(R.id.ll_main)
    RelativeLayout llMain;
    @Bind(R.id.jz_date)
    TextView jzDate;
    @Bind(R.id.jz_mileage)
    EditText etMileage;

    String typeShort = "";
    int nYear = 0, nMonth = 0;

    String carId;
    private SelectAreaPopWindow selectAreaPopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jingzhengu);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.ivTopBack, R.id.jz_car_area, R.id.jz_ll_type, R.id.jz_date, R.id.btn_ok, R.id.btn_skip})
    public void onClick(View view) {
        Intent it;
        switch (view.getId()) {
            case R.id.ivTopBack:
                if (selectAreaPopWindow != null && selectAreaPopWindow.isShowing()) {
                    selectAreaPopWindow.dismiss();
                }
                finish();
                break;
            case R.id.jz_ll_type:
                it = new Intent(mContext, BrandListActivity.class);
                startActivity(it);
                break;
            case R.id.jz_date:
                setDate();
                break;
            case R.id.jz_car_area:
                if (!ButCommonUtils.isFastDoubleClick()) {
                    if (LocalCityTable.getInstance().getAllProvince().size() == 0) {
                        getCityInfo();
                    } else {
                        if (selectAreaPopWindow != null && selectAreaPopWindow.isShowing()) return;
                        selectAreaPopWindow = new SelectAreaPopWindow(mContext);
                        selectAreaPopWindow.setOnSelectListener(this);
                        selectAreaPopWindow.showAtLocation(llMain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    }
                }
                break;
            case R.id.btn_ok:
                submit();
                break;
            case R.id.btn_skip:
                it = new Intent(mContext, AgentPjOrderDetailActivity.class);
                startActivity(it);
                break;
        }
    }

    private void submit() {
        final String date = jzDate.getText().toString().trim();
        final String mileage = etMileage.getText().toString().trim();
        final String type = jzCarType.getText().toString().trim();
        final String city = tvCarArea.getText().toString().trim();
        if (type.length() == 0) {
            shortToast("请选择车型");
            return;
        }
        if (mileage.length() == 0) {
            shortToast("公里数不能为空");
            return;
        }
        if (date.length() == 0) {
            shortToast("请输入上牌日期");
            return;
        }
        if (city.length() == 0) {
            shortToast("请选择城市名称");
            return;
        }


        HashMap<String, String> fields = new HashMap() {
            {
                //TrimId:车型 Id BuyCarDate:上牌时间，格式 yyyy-MM-dd Mileage:行驶里程(公里)
                // ColorId:车辆颜色 Id CityId:城市 Id
                put("trimId", carId);
                put("buyCarDate", date);
                put("mileage", mileage);
//                put("colorId",String.valueOf(1));
                String cityId = LocalCityTable.getInstance().getCityIdByCityName(city);
                put("cityId", cityId);
                put("city", city);
                put("type", type);
            }
        };
        Intent it = new Intent(mContext, JingZhenGuResultActivity.class);
        it.putExtra("carInfo", fields);
        startActivity(it);
        //getMap(ServerUrl.appraisalCar,appraisalCallback);
        //postMap(ServerUrl.appraisalCar,fields,appraisalCallback);
    }


    @SuppressLint("NewApi")
    private void setDate() {
        if (nYear <= 0) {
            setToday();
        } else {
            JingZhenGuActivity.MyDatePicker dialogFragment = new JingZhenGuActivity.MyDatePicker(nYear, nMonth - 1);
            dialogFragment.show(getFragmentManager(), "date_picker");
        }
    }

    @SuppressLint("NewApi")
    private void setToday() {

        Calendar c = Calendar.getInstance();
        int startYear = c.get(Calendar.YEAR);
        int startMonth = c.get(Calendar.MONTH);

        JingZhenGuActivity.MyDatePicker dialogFragment = new JingZhenGuActivity.MyDatePicker(startYear, startMonth);
        dialogFragment.show(getFragmentManager(), "date_picker");
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public class MyDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private int year, month;

        @SuppressLint("ValidFragment")
        public MyDatePicker(int nYear, int nMonth) {
            this.year = nYear;
            this.month = nMonth;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            DatePickerDialog dialog = new DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog, this, year, month, 1);
            ((ViewGroup) ((ViewGroup) dialog.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            nYear = year;
            nMonth = monthOfYear + 1;

            Calendar c = Calendar.getInstance();
            int startYear = c.get(Calendar.YEAR);
            int startMonth = c.get(Calendar.MONTH);
            if (nYear > startYear || (nYear == startYear && nMonth > startMonth) || (nYear == startYear && nMonth == startMonth)) {
                nYear = startYear;
                nMonth = startMonth + 1;
            }

            String m_strStartDate = String.format("%4d-%02d", nYear, nMonth);
            jzDate.setText(m_strStartDate);
        }
    }

    private void getCityInfo() {
        HashMap<String, String> fields = new HashMap<String, String>();
//        postMap(ServerUrl.getRegionList, fields, cityHandler);
        if (LocalCityTable.getInstance().getAllProvince().size() == 0 || Utils.getIntPreferences(mContext, "newVersion") == 1) {
            if (ServerUrl.isNetworkConnected(this)) {
                try {
                    postMap(ServerUrl.getRegionList, fields, cityHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
            }
        }
    }


    private Handler appraisalCallback = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    HashMap<String, Object> result = (HashMap<String, Object>) msg.obj;
                    try {
                        Boolean success = Boolean.valueOf(result.get("success").toString());
                        if (success) {
                            Gson gson = new Gson();
                            JSONObject data = JSON.parseObject(gson.toJson(result.get("content")));
                            Intent it = new Intent(mContext, JingZhenGuResultActivity.class);
                            it.putExtra("carInfo", data.toJSONString());
                            startActivity(it);
                        } else {
                            shortToast("查询失败");
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

    @Override
    public void selectArea(String province, String city) {
        tvCarArea.setText(city);
    }

    class ViewHolder {

        @Bind(R.id.jz_car_type)
        TextView tvCarType;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.isMsgOf(MyConstants.SEL_CAR_NAME)) {
            typeShort = event.getStringExtra("fullName");
            String carName = event.getStringExtra("carName");
            int index = carName.indexOf("款");
            carId = event.getStringExtra("carId");
            if (index != -1) {
                carName = carName.replace("款 ", "款 " + typeShort + " ");
            } else {
                carName = typeShort + " " + carName;
            }
            jzCarType.setText(carName);
        }
    }

}
