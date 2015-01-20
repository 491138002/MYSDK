package com.baofeng.game.sdk.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baofeng.game.sdk.BFResources;

public class BFCustomDialog extends Dialog{

	protected static String ID_TYPE = "id";
	protected static String ICON_TYPE = "drawable";
	protected static String LAYOUT_TYPE = "layout";
	protected static String STRING_TYPE = "string";
	protected static String ANIM_TYPE = "anim";
	
	private Context mContext;
	
	private View mDialogView;
	private EditText mPasswordInput;
	private Button mPasswordConfirm;
	
	private android.view.View.OnClickListener mListener;
	
	public BFCustomDialog(Context context) {
		super(context,BFResources.style.bx_custom_dialog);
		mContext = context;
		init();
	}
	
	public BFCustomDialog(Context context,int id) {
		super(context, id);
		mContext = context;
		init();
	}

	private void init() {
		mDialogView = LayoutInflater.from(mContext).inflate(BFResources.layout.bx_force_change_password, null);
		mPasswordInput = (EditText) mDialogView.findViewById(BFResources.id.bx_force_change_password_input);
		mPasswordConfirm = (Button) mDialogView.findViewById(BFResources.id.bx_force_change_passwod_confirm);
		mPasswordConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(mListener != null)
					mListener.onClick(v);
			}
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(mDialogView);
	}
	
	public String getPasswordContent(){
		if(mPasswordInput == null)
			return "";
		return mPasswordInput.getText().toString().trim();
	}
	
	public void setConfirmClickListener(View.OnClickListener listener){
		mListener = listener;
	}
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dismiss();
		}
		return super.onKeyDown(keyCode, event);
	}
	

}
