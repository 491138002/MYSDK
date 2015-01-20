package com.baofeng.game.sdk.net;

import java.io.Serializable;

import android.content.Context;

public interface ThreadCallBack extends Serializable {
	public void onCallbackFromThread(String resultJson);
	public void onCallBackFromThread(String resultJson, int resultCode);
	public Context getContext();
}
