package com.qlp2p.doctorcar.common;

import android.app.Activity;

import com.qlp2p.doctorcar.data.MessageEvent;

import org.greenrobot.eventbus.EventBus;

public class PicPicker {

	private Activity mActivity;
	
	public PicPicker(Activity activity){
		mActivity = activity;
	}
	
	/**
	 * 拍照获取图片 
	 *  
	 */
	public void doTakePhoto(int position, int requestCode){
        MessageEvent event = new MessageEvent(MyConstants.REQUEST_TAKE_PIC);
        event.putExtra("position", position);
        event.putExtra("reqCode", requestCode);
        EventBus.getDefault().post(event);
	}

}
