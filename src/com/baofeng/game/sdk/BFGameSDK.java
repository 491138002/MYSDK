package com.baofeng.game.sdk;

import java.net.URLEncoder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.baofeng.game.sdk.activity.BFMainActivity;
import com.baofeng.game.sdk.activity.BFPayActivity;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.exception.BFCallbackListenerNullException;
import com.baofeng.game.sdk.listener.BFGameCallbackListener;
import com.baofeng.game.sdk.listener.BFPayCallbackListener;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.util.DeviceUtil;
import com.baofeng.game.sdk.vo.BFGameParamInfo;
import com.baofeng.game.sdk.vo.BFGamePayParamInfo;

public class BFGameSDK {

	private static BFGameSDK SDK = null;

	public static synchronized BFGameSDK defaultSDK() {
		if (SDK == null) {
			SDK = new BFGameSDK();
		}
		return SDK;
	}

	public void initSDK(Context ctx, BFGameParamInfo gameParams,
			BFGameCallbackListener<String> listener)
			throws BFCallbackListenerNullException {
		if (ctx == null) {
			throw new BFCallbackListenerNullException(
					BFResources.ERROR_CONTEXT_NULL);
		}

		if (listener == null) {
			throw new BFCallbackListenerNullException(
					BFResources.ERROR_CALLBACKLISTENER_NULL);
		}
		BFGameConfig.PHONE_IMEI = DeviceUtil.getIMEI(ctx);
		int channel=0;
		ApplicationInfo appInfo;
		try {
			appInfo = ctx.getPackageManager().getApplicationInfo(
					ctx.getPackageName(), PackageManager.GET_META_DATA);
			
			if (appInfo.metaData!=null) {
				channel = appInfo.metaData.getInt("CX_CHANNEL");
				System.out.println("CX_CHANNEL=="+channel);
				if (channel!=0) {
					BFGameConfig.CHANNEL_ID=channel;
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		BFGameConfig.CONTEXT = ctx;
		BFGameConfig.GAMEPARAM = gameParams;
		BFGameConfig.is_hasSMS = gameParams.getSMS();

		// 初始化SDK成功
		listener.callback(BFSDKStatusCode.SUCCESS, BFResources.SUCCESS_INIT);
		
	}

	public void login(final Context context, BFGameCallbackListener listener)
			throws BFCallbackListenerNullException {
		if (context == null) {
			throw new BFCallbackListenerNullException(
					BFResources.ERROR_CONTEXT_NULL);
		}

		if (listener == null) {
			throw new BFCallbackListenerNullException(
					BFResources.ERROR_CALLBACKLISTENER_NULL);
		}

		BFGameConfig.CONTEXT = context;
		BFGameConfig.CALLBACK_LISTENER = listener;
		context.startActivity(new Intent(context, BFMainActivity.class));
	}

	public void pay(Context ctx, BFPayCallbackListener<String> listener,
			BFGamePayParamInfo paymentParams)
			throws BFCallbackListenerNullException {

		if (listener == null) {
			throw new BFCallbackListenerNullException("需要有回掉函数");
		}
		if (ctx == null) {
			throw new BFCallbackListenerNullException("需要应用程序上下文");
		}

		if (paymentParams == null) {
			throw new BFCallbackListenerNullException("需要支付参数");
		}

		// 存储回掉函数
		BFGameConfig.PAYCALLBACK_LISTENER = listener;
		BFGameConfig.CONTEXT = ctx;
		BFGameConfig.PAYMENT_PARAM = paymentParams;

		// 启动支付界面

		ctx.startActivity(new Intent(ctx, BFPayActivity.class));

	}

	public String getTicket() {
		return URLEncoder.encode(BFGameConfig.TICKET);
	}
}
