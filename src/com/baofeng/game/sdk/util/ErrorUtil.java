package com.baofeng.game.sdk.util;

public class ErrorUtil {
	public static String errorJson(String resultCode,String message){
		return "{\"code\":\""+resultCode+"\",\"msg\":\""+message+"\"}";
	}
}
