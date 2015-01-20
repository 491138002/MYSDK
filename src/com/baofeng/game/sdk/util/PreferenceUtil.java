package com.baofeng.game.sdk.util;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;

public class PreferenceUtil {

	public static String getSharedString(Context context, String key,
			String defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(key, defValue);
	}

	public static void putSharedString(Context context, String key, String value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(key, value).commit();   
	}

	public static int getSharedInt(Context context, String key, int defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(
				key, defValue);
	}

	public static void putSharedInt(Context context, String key, int value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putInt(key, value).commit();
	}

	public static long getSharedLong(Context context, String key, long defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getLong(
				key, defValue);
	}
 
	public static void putSharedLong(Context context, String key, long value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putLong(key, value).commit();
	}

	public static boolean getSharedBoolean(Context context, String key,
			boolean defValue) {    
		return PreferenceManager.getDefaultSharedPreferences(context)    
				.getBoolean(key, defValue);    
	
	}

	public static void putSharedBoolean(Context context, String key,
			boolean value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Activity ac, String key, boolean defValue) {
		return ac.getPreferences(Context.MODE_PRIVATE)
				.getBoolean(key, defValue);  
	}   

	public static void putBoolean(Activity ac, String key, boolean value) {
		ac.getPreferences(Context.MODE_PRIVATE).edit().putBoolean(key, value)  
				.commit();     
	}

	
}

	