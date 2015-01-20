package com.baofeng.game.sdk.util;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.activity.BFPayActivity;

public class BXDialogUtil {
	/**
	 * 退出窗口
	 * @param context
	 * @return
	 */
	public static Dialog getTipDialog(final BFPayActivity context,String msg){
		final Dialog dialog = new Dialog(context, BFResources.style.bx_custom_dialog);
		
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(BFResources.layout.bx_tip_dialog, null);
		
		Button submit = (Button)view.findViewById(BFResources.id.bx_tip_submit);
		TextView content = (TextView)view.findViewById(BFResources.id.bx_tip_content);
		content.setText(msg);
		
		
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.setContentView(view);
		return dialog;
	}
	
	public interface OnAlertSelectId {
		void onClick(int whichButton, Object o);
	}
	
	/**
	 * 提示窗口
	 * @param context
	 * @param title
	 * @param content
	 * @param submtText
	 * @param cancelText
	 * @param selectListener
	 * @return
	 */
	public static Dialog showDialog(Context context, String title, String content, 
			String submtText, String cancelText, final OnAlertSelectId selectListener){
		View view = ((Activity) context).getLayoutInflater().inflate(BFResources.layout.bx_pay_center_transaction_dialog, null);
		
		TextView titleTv = (TextView) view.findViewById(BFResources.id.bx_dialog_title_tv);
		TextView contentTv = (TextView) view.findViewById(BFResources.id.bx_dialog_content_tv);
		Button submitBtn = (Button) view.findViewById(BFResources.id.bx_dialog_submit_btn);
		Button cancleBtn = (Button) view.findViewById(BFResources.id.bx_dialog_cancel_btn);
		
		final Dialog dialog = new Dialog(context, BFResources.style.bx_custom_dialog);
		
		dialog.setContentView(view);
		titleTv.setText(title);
		contentTv.setText(content);
		
		
		
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				 if (keyCode == KeyEvent.KEYCODE_BACK||keyCode == KeyEvent.KEYCODE_SEARCH)
				    {
				     return true;
				    }else {
						
				    	return false;
					}
			}
		});
		if(StringUtil.isNotEmpty(submtText)){
			submitBtn.setText(submtText);
			submitBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(selectListener != null){
						selectListener.onClick(0, null);
					}
					dialog.cancel();
				}
			});
		}else{
			submitBtn.setVisibility(View.GONE);
		}
		
		if(StringUtil.isNotEmpty(cancelText)){
			cancleBtn.setText(cancelText);
			cancleBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(selectListener != null){
						selectListener.onClick(1, null);
					}
					dialog.cancel();
				}
			});
		}else{
			cancleBtn.setVisibility(View.GONE);
		}
		
		return dialog;
	}
}
