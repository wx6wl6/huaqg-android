package com.qlp2p.doctorcar.data;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class BrandInfo {


	public String id;
	public String name;
	public String logo;
	public String initial;
	//车系
	public String fullname;
	//车型
	public String yeartype;

	public BrandInfo() {
		recycle();
	}

	public BrandInfo(JSONObject obj) {
		if(obj == null) return;
		
		try {
			id = obj.getString("id");
			name = obj.getString("name");
			logo = obj.getString("logo");
			initial = obj.getString("initial");
			fullname = obj.getString("fullname");
			yeartype = obj.getString("yeartype");

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void recycle() {
		id = "";
		name = "";
		logo = "";
		initial = "";
		fullname = "";
		yeartype = "";
	}
}