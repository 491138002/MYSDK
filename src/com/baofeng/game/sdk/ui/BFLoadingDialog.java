package com.baofeng.game.sdk.ui;

import java.util.concurrent.TimeUnit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.net.DefaultThreadPool;
import com.baofeng.game.sdk.util.StringUtil;

public class BFLoadingDialog extends Dialog implements
		OnDismissListener {
	TextView loading_text;
	TextView loading_del_textview;
	String content = "";
	boolean isHideCloseBtn = false;
//	private Context context;

	public BFLoadingDialog(Context context, String content,
			boolean isHideCloseBtn) {
		super(context);
//		this.context = context;
		this.content = content;
		this.isHideCloseBtn = isHideCloseBtn;
		setOnDismissListener(this);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//去掉标题
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 //设置view样式
		 setContentView(BFResources.layout.bx_loading_dialog);	
		 loading_text= (TextView) findViewById(BFResources.id.loading_text);
		 
		 if(StringUtil.isNotEmpty(content)){
			 	loading_text.setText(content);
			}else{
				loading_text.setText("加载中...");
			}
	}

	// called when this dialog is dismissed
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		BFGameConfig.IS_STOP_REQUEST = false;

	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		try {
			DefaultThreadPool.pool.awaitTermination(1, TimeUnit.MICROSECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (isHideCloseBtn) {
//				BFGameConfig.IS_STOP_REQUEST = true;
//				dismiss();
//				return super.onKeyDown(keyCode, event);
//			} else {
//				BFGameConfig.IS_STOP_REQUEST = true;
//				return super.onKeyDown(keyCode, event);
//			}
			return true;

		}

		return super.onKeyDown(keyCode, event);

	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// Config.IS_STOP_REQUEST = true;
	}

}
