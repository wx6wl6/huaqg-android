package com.qlp2p.doctorcar.common;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.qlp2p.doctorcar.data.UserInfo;
import com.qlp2p.doctorcar.main.MainTabActivity;
//import com.qlp2p.doctorcar.service.LocationService;

public class MyGlobal extends Application{
	
	//百度地图
//	public LocationService locationService;
	public Vibrator mVibrator;


	private static MyGlobal mInstance = null;
    
    public static boolean wifiConnected = true;
    public static boolean picOnlyOnWifi = false;
    
    public MainTabActivity mainActivity = null;

	public UserInfo user = null;

	public static String cache_path = "";
	public static String temp_path = "";
	
	public int SCR_WIDTH = 0;
	public int SCR_HEIGHT = 0;
	public float SCR_DENSITY = 0.0f;


	@Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        initVariable();
        initImageLoader(getApplicationContext());

		try {
/*baidu map */
//			locationService = new LocationService(getApplicationContext());
			mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

//			SDKInitializer.initialize(getApplicationContext());

		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
    public void onTerminate() {
    	super.onTerminate();
    }

	public void initVariable() {
		user = new UserInfo();
	}
	
	public static MyGlobal getInstance() {
		return mInstance;
	}

	public static void initImageLoader(Context context){
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		    .memoryCacheExtraOptions(480, 800)          // default = device screen dimensions
		    //.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)  
		    .threadPoolSize(3)                          // default  
		    .threadPriority(Thread.NORM_PRIORITY - 1)   // default
		    .tasksProcessingOrder(QueueProcessingType.LIFO) // default
		    .denyCacheImageMultipleSizesInMemory()  
		    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		    .memoryCacheSize(2 * 1024 * 1024)  
		    .memoryCacheSizePercentage(13)              // default  
		    .discCacheSize(50 * 1024 * 1024)        // 缓冲大小  
		    //.discCacheFileCount(100)                // 缓冲文件数目  
		    .discCacheFileNameGenerator(new Md5FileNameGenerator()) // default
		    .imageDownloader(new BaseImageDownloader(context)) // default
		    .imageDecoder(new BaseImageDecoder(false)) // default
		    .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
		    //.writeDebugLogs()  
		    .build();  
		ImageLoader.getInstance().init(config);
	}

}
