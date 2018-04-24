package com.qlp2p.doctorcar.data;

import android.widget.ImageView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import me.nereo.multi_image_selector.bean.Image;

public class CarImgInfo {

	public int id;
	public String imgUrl;
	public String title;

	public ImageView ivImg;
	public CarImgInfo() {
		recycle();
	}

	public CarImgInfo(JSONObject obj) {
		if(obj == null) return;
		
		try {
			imgUrl = obj.getString("imgUrl");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void recycle() {
		id = 0;
		imgUrl = "";
		title = "";
	}
}