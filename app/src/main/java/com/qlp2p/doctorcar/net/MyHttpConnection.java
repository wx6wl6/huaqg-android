package com.qlp2p.doctorcar.net;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qlp2p.doctorcar.common.MyGlobal;
import com.qlp2p.doctorcar.common.Utils;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;

public class MyHttpConnection {
	
	private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
	
	//-------------------------------APP版本，与配置文件中一致---------------－-------------------//
	public final static String version = "1.0.0";

	public static int isDebug = 0;
	
	public final static String server_url = "";
	public final static String server_api_url = server_url + "";

	static final public int  NET_RESULT_OK = 0;
	static final public int  NET_RESULT_ERROR_SERVER = 1;
	static final public int  NET_RESULT_ERROR_CONTENT = 2;

	static final public int  PasteRefresh = 502;

	
	static final public int  getPhoneCode 						= 1;

	static final public int  uploadImage						= 1001;

	Handler mHandler = null;
	
	//static public Handler level_handler = null;
	static Context mContext = null;
	int req_type = 0;
	static MyGlobal myglobal = null;
	long startTime, endTime;
	int retryCount = 0;
	public boolean bRefresh = false;
	public String param1 = "";
	public String param2 = "";
	public String param3 = "";
	public String param4 = "";
	public String param5 = "";
	public String relIdx = "";
	
	RequestParams mParams = null;
	Header[] headers = null;
	MyHttpConnection mCon = null;
	
	 // GET method
    public void getWithHeader(Context context, int type, RequestParams params, Handler handler, Header[] headers) {
    	
    	this.mContext = context;
    	this.req_type = type;
    	this.mHandler = handler;
    	this.mParams = params;
    	this.mCon = this;
    	this.headers = headers;
    	
    	myglobal = (MyGlobal) mContext.getApplicationContext();
    	
    	String full_url = "";
    	switch(type){
	    	/*case GetPersonCount:
	    		full_url = getAbsoluteUrl(1, "GetPersonCount");
				break;*/
    	}
    	
    	if(isDebug==1) Log.w("<HTTP>", "full_url(GET)" + full_url);
    	client.get(mContext, full_url, headers, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] _response) {
				String response = new String(_response);
				
    			if(isDebug==1) Log.w("<HTTP>", "req_type:" + req_type);
		    	if(isDebug==1) Log.w("<HTTP>", "response:" + response);
                
                gotoHandler(NET_RESULT_OK, response);
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				gotoHandler(NET_RESULT_ERROR_SERVER, "");	
			}

			
            
        });
    }
    
    // GET method
    public void get(Context context, int type, RequestParams params, Handler handler) {
    	
    	this.mContext = context;
    	this.req_type = type;
    	this.mHandler = handler;
    	this.mParams = params;
    	this.mCon = this;
    	myglobal = (MyGlobal) mContext.getApplicationContext();
    	
    	String full_url = "";
    	switch(type){
	    	/*case GetPersonCount:
	    		full_url = getAbsoluteUrl(1, "GetPersonCount");
				break;
	    	case GetMusicList:
				full_url = "http://amob.acs86.com/api.htm";
				break;*/
    	}
    	
    	client.get(full_url, params, new AsyncHttpResponseHandler() {

    		@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] _response) {
    			String response = new String(_response);
                
    			if(isDebug==1) Log.w("<HTTP>", "req_type:" + req_type);
		    	if(isDebug==1) Log.w("<HTTP>", "response:" + response);
                
                gotoHandler(NET_RESULT_OK, response);
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				gotoHandler(NET_RESULT_ERROR_SERVER, "");
			}
        });
    }

    // POST method
 	public void post(Context context, int type, RequestParams _params, Handler handler, String relIdx) {
 		this.relIdx = relIdx;
 		post(context, type, _params, handler);
 	}
    // POST method
	public void post(Context context, int type, RequestParams _params, Handler handler) {
    	this.mContext = context;
    	this.req_type = type;
    	this.mHandler = handler;
    	RequestParams params;
    	if(_params == null) params = new RequestParams();
    	else params = _params;
    	this.mParams = params;
    	this.mCon = this;
    	
    	if(mContext == null){
    		gotoHandler(NET_RESULT_ERROR_CONTENT, "");
    		return;
    	}
    	
    	myglobal = (MyGlobal) mContext.getApplicationContext();
    	
    	//MyUtil.displayMemoryUsage("MyHttpConnection");
    	
    	
    	String full_url = "";
    	
    	switch(type){
	    	case getPhoneCode:
	    		full_url = getAbsoluteUrl(1, "getPhoneCode");
				break;

			case uploadImage:
	    		//HttpFileUpload(server_upload_url, param1, param2, param3, param4);
				return;
    	}
    	
    	if(isDebug==1) Log.w("<HTTP>", "full_url:" + full_url);
    	if(isDebug==1) Log.w("<HTTP>", "post_params:" + params.toString());
    	
    	client.setTimeout(30000);	
    	client.setUserAgent("weisanyun");
       
    	client.post(full_url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				gotoHandler(NET_RESULT_ERROR_SERVER, "");		
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] _response) {
				String response = new String(_response);
				
				if(isDebug==1) Log.w("<HTTP>", "req_type:" + req_type);
		    	if(isDebug==1) Log.w("<HTTP>", "response:" + response);
    			
				gotoHandler(NET_RESULT_OK, response);
			}
        });
    }
	
	private void HttpFileUpload(String urlString, String filePath, String newFileName, String fileType, String isGroup) {
		if( filePath== null || filePath.equals("") || !Utils.checkAlreadyDowned(filePath)) {
			gotoHandler(NET_RESULT_ERROR_CONTENT, "");
			return;
		}
		
		File uploadFile = new File(filePath);
		
		RequestParams params = new RequestParams();
		try {
//			FileInputStream mFileInputStream = new FileInputStream(uploadFile);
//			params.put("file", mFileInputStream);
			params.put("file", uploadFile, "image/png", System.currentTimeMillis() + Math.random() * 1000 + ".png");
			
			if(req_type == uploadImage){
//				params.put("userToken", myglobal.user.getUserToken());
//			    params.put("installId", Utils.readSecurePrefer(mContext, Macro.KEY_WSY_GETUI_CID));
			}
		    
		    AsyncHttpClient upload_client = new AsyncHttpClient();
		    upload_client.setUserAgent("meiliwenhua");
		    upload_client.post(urlString, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2,
						Throwable arg3) {
					gotoHandler(NET_RESULT_ERROR_SERVER, "");
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] _response) {
					String response = new String(_response);
					
					if(isDebug==1) Log.w("<HTTP>", "req_type:" + req_type);
			    	if(isDebug==1) Log.w("<HTTP>", "response:" + response);
			    	
			    	gotoHandler(NET_RESULT_OK, response);
				}
	    		
	        });
		} catch(FileNotFoundException e) {
			
		}
	}

    public static String getAbsoluteUrl(int type, String relativeUrl) {
    	String full_url = "";
    	if(type == 1)
    		full_url = server_api_url + relativeUrl;
    	else if(type == 2)
    		full_url = server_url + relativeUrl;
    	
    	return full_url;
    }
    
    public static String getOriginalUrl(int type, String full_url) {
    	String relativeUrl = "";
    	if(type == 1)
    		relativeUrl = full_url.replace(server_api_url, "");
    	else if(type == 2)
    		relativeUrl = full_url.replace(server_url, "");
    	
    	return relativeUrl;
    }
    
    public static String getCropeImgUrl(String relativeUrl, int w, int h) {
    	return String.format("%s?url=%s&w=%d&h=%d", server_url+"genaralAPI/getCropImage", server_url+relativeUrl, w, h);
    }
    
    private void gotoHandler(int state, String content) {
    	if(mHandler != null){
    		
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("type", this.req_type);
			b.putInt("state", state);
			b.putString("content", content);
			if (relIdx != null) b.putString("relIdx", relIdx);
			msg.setData(b);
			mHandler.sendMessage(msg);
    	}
	}
    
}
