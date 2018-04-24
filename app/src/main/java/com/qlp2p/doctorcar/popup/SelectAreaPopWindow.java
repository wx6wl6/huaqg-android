package com.qlp2p.doctorcar.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qlp2p.doctorcar.R;
import com.qlp2p.doctorcar.data.Province;
import com.qlp2p.doctorcar.db.LocalCityTable;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sx on 2017/1/9.
 */
public class SelectAreaPopWindow extends PopupWindow {


    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.tv_confirm)
    TextView tvConfirm;
    @Bind(R.id.wv_province)
    WheelView wvProvince;
    @Bind(R.id.wv_city)
    WheelView wvCity;
    private Context mContext;

    public SelectAreaPopWindow(final Context context) {
        super(context);
        mContext = context;
        //pop_bottom为自定义布局
        View view = LayoutInflater.from(context).inflate(R.layout.pop_select_area, null);
        //设置PopupWindow的View
        setContentView(view);
        ButterKnife.bind(this, view);

        initPopWindow();
        initWheelViewStyle();
        initWheelViewDate();
    }


    /**
     * 设置popwindow
     */
    private void initPopWindow() {

        //设置PopupWindow弹出窗体的宽
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置PopupWindow弹出窗体的高
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置PopupWindow弹出窗体可点击
        setFocusable(true);
        //设置PopupWindow弹出窗体动画效果
        setAnimationStyle(R.style.popupAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        getBackground().setAlpha(0);
        final Activity activity = (Activity) mContext;
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.alpha = 0.7f;
        activity.getWindow().setAttributes(params);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        setOutsideTouchable(true);

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = activity.getWindow().getAttributes();
                params.alpha = 1f;
                activity.getWindow().setAttributes(params);
            }
        });
    }

    /**
     * 初始化wheelview样式
     */
    private void initWheelViewStyle() {
        //样式配置
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.textColor = Color.BLACK;
        style.selectedTextSize = 18;////单位dp
        style.holoBorderColor = Color.parseColor("#f4f4f4");
        style.textColor = Color.parseColor("#999999");
        style.selectedTextColor = Color.BLACK;

        //设置样式
        wvProvince.setWheelAdapter(new ArrayWheelAdapter(mContext)); // 文本数据源
        wvProvince.setSkin(WheelView.Skin.Holo); // holo皮肤
        wvProvince.setStyle(style);

        wvCity.setWheelAdapter(new ArrayWheelAdapter(mContext)); // 文本数据源
        wvCity.setSkin(WheelView.Skin.Holo);
        wvCity.setStyle(style);

    }

    /**
     * 设置wheelview数据
     */
    private void initWheelViewDate() {
        List<String> provinces = createMainDatas();
        HashMap<String,List<String>> citys = createSubDatas();


        if(provinces.size() < 1 || citys.size() < 1)
            dismiss();


        wvProvince.setWheelData(provinces);

        //联动城市wheelview
        wvCity.setWheelData(citys.get(provinces.get(wvProvince.getSelection())));
        wvProvince.join(wvCity);
        wvProvince.joinDatas(citys);

    }

    @OnClick({R.id.tv_cancel, R.id.tv_confirm})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_confirm:
                if(onSelectListener != null){
                    onSelectListener.selectArea(wvProvince.getSelectionItem().toString(),wvCity
                            .getSelectionItem().toString());
                    dismiss();
                }
                break;
        }
    }

    /**
     * 省份数据
     *
     * @return
     */
    private List<String> createMainDatas() {
        List<Province> provinceList = LocalCityTable.getInstance().getAllProvince();
        List<String> list = new ArrayList<>();
        for(Province province:provinceList){
            list.add(province.getName());
        }
        return list;
    }

    /**
     * 城市数据
     *
     * @return
     */
    private HashMap<String, List<String>> createSubDatas() {
        return LocalCityTable.getInstance().getAllProvinceAndCity();
    }

    /**
     * 区域数据
     *
     * @return
     */
    public void setOnSelectListener(OnSelectListener onSelectListener){
            this.onSelectListener = onSelectListener;
    }
    private OnSelectListener onSelectListener;
    public interface OnSelectListener{
        void selectArea(String province, String city);
    }
}
