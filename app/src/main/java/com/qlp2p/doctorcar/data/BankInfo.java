package com.qlp2p.doctorcar.data;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class BankInfo {


	public String bankId;
	public String bankName;
	public String bankCode;

	public BankInfo() {
		recycle();
	}

	public BankInfo(JSONObject obj) {
		if(obj == null) return;
		
		try {

			bankId = obj.getString("bankId");
			bankName = obj.getString("bankName");
			bankCode = obj.getString("bankCode");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void recycle() {
		bankId = "";
		bankName = "";
		bankCode = "";
	}
}