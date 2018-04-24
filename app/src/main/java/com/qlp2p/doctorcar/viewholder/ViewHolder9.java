package com.qlp2p.doctorcar.viewholder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qlp2p.doctorcar.R;


/**
 * 
 * @author raphael
 * 四选一打钩项（照）
 *
 */
public class ViewHolder9 {

	public View mView;
	public Activity mActivity;
	public LayoutInflater inflater;
	public TextView tv_name;
	
	public RadioGroup fadongji_rg;
	public RadioButton ok_rd;
	public RadioButton qingwei_rd;
	public RadioButton yanzhong_rd;
	public RadioButton uncheck_rd;
	public ImageButton camera_btn;
	
	public ViewHolder9(View view, Activity activity){
		mView = view;
		mActivity = activity;
		inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View setData(){
		mView = inflater.inflate(R.layout.four_one_pic, null);
		tv_name = (TextView) mView.findViewById(R.id.textView1);
		
		camera_btn = (ImageButton) mView.findViewById(R.id.fo_camera_btn);
		camera_btn.setVisibility(View.INVISIBLE);
		
		fadongji_rg = (RadioGroup) mView.findViewById(R.id.fadongji_rg);
		ok_rd = (RadioButton) mView.findViewById(R.id.ok_rd);
		qingwei_rd = (RadioButton) mView.findViewById(R.id.qingwei_rd);
		yanzhong_rd = (RadioButton) mView.findViewById(R.id.yanzhong_rd);
		uncheck_rd = (RadioButton) mView.findViewById(R.id.uncheck_rd);
		uncheck_rd.setChecked(true);
		return mView;
	}
}
