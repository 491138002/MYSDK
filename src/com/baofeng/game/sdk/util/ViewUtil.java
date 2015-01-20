package com.baofeng.game.sdk.util;

import android.widget.CheckBox;

public class ViewUtil {

	public static void setChcekBoxPadding(CheckBox cb){
		if(DeviceUtil.getSDKVersionInt() >= 17){
			cb.setPadding(DeviceUtil.dip2px(cb.getContext(), 2), 0, 0, 0);
		}
	}
}
