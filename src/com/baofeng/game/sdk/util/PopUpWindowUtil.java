package com.baofeng.game.sdk.util;

import android.app.Activity;
import android.widget.PopupWindow;

public class PopUpWindowUtil {

	// 分享弹出窗口
	private Activity act;
	private PopupWindow p;

	public PopUpWindowUtil(Activity act){
		this.act = act;
	}
	
	public void pDimiss() {
		if (p != null && p.isShowing()) {
			p.dismiss();
		}
	}
}
