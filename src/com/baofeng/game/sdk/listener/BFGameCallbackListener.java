package com.baofeng.game.sdk.listener;

public abstract interface BFGameCallbackListener<T> {

	public abstract void callback(int statuscode, T data);
}
