package com.baofeng.game.sdk.util;











import com.baofeng.game.sdk.BuildConfig;

import android.util.Log;


public class LogUtil {    

	private static final boolean IS_LOG = BuildConfig.DEBUG;                        

	private final static String LOG_TAG_STRING = "BXGameSDK==========================";

	public static void d(String tag, String msg) {      
		try {
			if (IS_LOG) {
				Log.d(LOG_TAG_STRING, tag + " : " + msg);
			}
		} catch (Throwable t) {
		}
	}

	public static void i(String tag, String msg) {
		try {
			if (IS_LOG) {
				Log.i(LOG_TAG_STRING, tag + " : " + msg);
			}
		} catch (Throwable t) {
		}
	}

	public static void e(String tag, String msg) {
		try {
			if (IS_LOG) {
				Log.e(LOG_TAG_STRING, tag + " : " + msg);
			}
		} catch (Throwable t) {
		}
	}
	
	public static void w(String tag, String msg) {
		try {
			if (IS_LOG) {
				Log.w(LOG_TAG_STRING, tag + " : " + msg);
			}
		} catch (Throwable t) {
		}
	}
}
