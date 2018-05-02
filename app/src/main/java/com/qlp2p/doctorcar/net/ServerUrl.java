package com.qlp2p.doctorcar.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 接口
 * @author Administrator
 *
 */
public class ServerUrl {

	//server
//	public final static String BASE_URL = "http://192.168.0.167:8080/doctorCar/";
//	public final static String BASE_URL = "http://192.168.13.35:8080/doctorCar/";
//	public final static String BASE_URL = "http://101.132.67.227:8080/doctorCar/";
	//public final static String BASE_URL = "http://47.100.173.217:10091/huaqgAdmin/";    //测试环境
	//public final static String BASE_URL = "http://www.huaqg.com:8080/huaqgAdmin/";
	public final static String BASE_URL ="http://admin.huaqg.com/";     //生产环境
//	public final static String BASE_URL = "http://192.168.31.78:8080/";

	/**
	 * 2.1	图书馆首页接口
	 */
	public final static String pingu 	                       = BASE_URL+"api/jingzhenggu";
	
	//3.1	登陆
	public final static String login 							= BASE_URL+"LoginApiController/login";
	//3.2	注册
	public final static String getSmsCode 						= BASE_URL+"getSmsCode";
	//3.3	密码找回
	public final static String resetPwd 						= BASE_URL+"LoginApiController/resetPassword";
	public final static String findPwd 							= BASE_URL+"LoginApiController/findPassword";
	// 退出
	public final static String logout 							= BASE_URL+"LoginApiController/logout";

	public final static String getBrandList 						= BASE_URL+"pj/getBrandList";
	public final static String getCarTypeList 					= BASE_URL+"pj/getCarTypeList";
	public final static String getCarList 						= BASE_URL+"pj/getCarList";
	public final static String getRegionList 					= BASE_URL+"getRegionList";
	public final static String updateLocation 					= BASE_URL+"updateLocation";
	public final static String updateUserInfo 					= BASE_URL+"updateUserInfo";
	public final static String setOnlineStatus 					= BASE_URL+"setOnlineStatus";
	//业务员
	public final static String getAgentOrderList 				= BASE_URL+"Agent/pj/getOrderList";
	public final static String submitOrder 					= BASE_URL+"Agent/pj/submitOrder";/*预估价格*/
	public final static String requestReport 				= BASE_URL+"Agent/pj/requestReport";/*申请报告*/
	public final static String pjGetOrderInfo 				= BASE_URL+"pj/getOrderInfo";/*评价订单信息*/
	public final static String pjSaveOrderInfo 				= BASE_URL+"Agent/pj/saveOrderInfo";/*保存订单*/
	public final static String pjMake2ReportOrder				= BASE_URL+"Agent/pj/make2ReportOrder";/*转到申请报告*/
	//评估师
	public final static String getAppraiserOrderList 			= BASE_URL+"Appraiser/pj/getOrderList";
	public final static String setPrice 							= BASE_URL+"Appraiser/pj/setPrice";
	//检测师
	public final static String getCheckerOrderList 			= BASE_URL+"Checker/jc/getOrderList";
	public final static String jcGetOrderInfo 				= BASE_URL+"jc/getOrderInfo";
	public final static String saveBasicInfo 				= BASE_URL+"jc/saveBasicInfo";
	public final static String saveBasicConf 				= BASE_URL+"jc/saveBasicConf";
	public final static String saveCheckRes 				= BASE_URL+"jc/saveCheckRes";
	public final static String savePhoto 					= BASE_URL+"jc/savePhoto";
	public final static String saveAfterService 			= BASE_URL+"jc/saveAfterService";
	public final static String finishCheck 					= BASE_URL+"jc/finishCheck";
	public final static String appraisalCar					= BASE_URL+"api/jingzhenggu";
	public final static String appraisalCarHtml				= BASE_URL+"api/jingzhengu.html";
	public final static String version_path				= BASE_URL+"api/get_data.json";
	public final static String apk_path 				= BASE_URL+"api/PLAYBULBX.apk";





	/**
	 * 获取加密字符串，并且按照key首字母排序
	 * @param params 生成加密字符串数据
	 * @param split 分隔符
	 * @return
	 */
	public static String getCodeStr(HashMap<String, String> params, String split) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            
            if(key.equals("sign")) continue;
            
            String value = params.get(key);
            
            if(value == null || value == "") continue;
            
            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        
        if(prestr.substring(prestr.length()-1).equals("&")) prestr = prestr.substring(0,prestr.length()-1);
        
        return prestr;
    }

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static String MD5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {

			if ((b & 0xFF) < 0x10) hex.append("0");

			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString();

	}
}
