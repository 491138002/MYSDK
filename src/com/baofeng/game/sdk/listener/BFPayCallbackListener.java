package com.baofeng.game.sdk.listener;

public abstract interface BFPayCallbackListener<T> {

	public abstract void callback(int statuscode, T data, int type);
	
}
