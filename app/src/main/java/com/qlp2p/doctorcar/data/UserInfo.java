package com.qlp2p.doctorcar.data;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class UserInfo {
	public static final int LOGIN_NO 			= 0;
	public static final int LOGIN_MOBILE 		= 1;
	public static final int LOGIN_WEIXIN 		= 2;


	public String userAvatar;
	public String userPhone;
	public String userId;
	public String token;
	public String orgId;
	public String userName;
	public String userType; // 4-业务员,5-评估员,6-检测员
	public String isOnline;
	public String checkCount;
	public String setPriceCount;
	public String orgName;

	public UserInfo() {
		recycle();
	}
	
	public UserInfo(JSONObject obj) {
		if(obj == null) return;
		
		try {
			userAvatar = obj.getString("userAvatar");
			userType = obj.getString("userType");
			userPhone = obj.getString("userPhone");
			userId = obj.getString("userId");
			token = obj.getString("token");
			orgId = obj.getString("orgId");
			userName = obj.getString("userName");
			isOnline = obj.getString("isOnline");
			checkCount = obj.getString("checkCount");
			setPriceCount = obj.getString("setPriceCount");
			orgName = obj.getString("orgName");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void recycle() {
		userAvatar = "";
		userPhone = "";
		userId = "";
		orgId = "";
		userName = "";
		userType = "";
		isOnline = "";
		checkCount = "0";
		setPriceCount = "0";
		token = "";
		orgName = "";
	}
}