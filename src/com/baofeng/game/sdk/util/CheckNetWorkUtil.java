package com.baofeng.game.sdk.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 检查网络状态
 * 
 * @author sailor
 * 
 */
public class CheckNetWorkUtil {

	public static boolean checkNetWork(Context context) {
		// 判断网络是否可用，如果不可用，给出提示
		boolean isAvailable = netWorkIsAvailable(context);
		if (!isAvailable) {// 如果不可用
			openDialog(context);
			return false;
		}
		return true;
	}

	public static boolean netWorkIsAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null) {
			if (activeNetInfo.isAvailable()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static boolean gpsWorkIsAvailable(Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	private static void openDialog(final Context context) {
		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("没有可用的网络");
		builder.setMessage("请开启GPRS或WIFI网络连接");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
				context.startActivity(intent);

			}
		}).setNeutralButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		}).create().show();

	}
}