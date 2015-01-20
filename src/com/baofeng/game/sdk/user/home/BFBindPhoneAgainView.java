package com.baofeng.game.sdk.user.home;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;
import android.widget.Button;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.ui.BFLoadingDialog;
import com.baofeng.game.sdk.user.BFBasePanel;
import com.baofeng.game.sdk.user.HomePanel;

public class BFBindPhoneAgainView extends BFBasePanel {
	private static BFBindPhoneAgainView mBXBindPhoneAgainView;
	
	private HomePanel mHomePanel;
	private View mBaseView;
	
//	private Button mBackBtn;
	private Button mBindPhoneNowBtn;
	private Button mBindPhoneLaterBtn;
	
	public synchronized static BFBindPhoneAgainView getInstance(Context context){
		if(mBXBindPhoneAgainView == null){
			mBXBindPhoneAgainView = new BFBindPhoneAgainView(context);
		}
		return mBXBindPhoneAgainView;
	}
	
	private BFBindPhoneAgainView(Context context) {
		super(context);
		
		init();
		initView();
		initListener();
		initValue();
	}
	
	public void init() {
		mBaseView = mInflater.inflate(BFResources.layout.bx_bind_phone_again, null);
	}

	public void initView() {
//		mBackBtn = (Button) mBaseView.findViewById(BFResources.id.bx_back);
		mBindPhoneNowBtn = (Button) mBaseView.findViewById(BFResources.id.bx_bind_phone_way_now);
		mBindPhoneLaterBtn = (Button) mBaseView.findViewById(BFResources.id.bx_bind_phone_later);
	}

	public void initListener() {
//		mBackBtn.setOnClickListener(new OnClickListener() {
//			public void onClick(View arg0) {
//				mHomePanel.showViewType(HomePanel.SHOW_ACCOUNT_INFO);
//			}
//		});
		mBindPhoneNowBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mHomePanel.showViewType(HomePanel.SHOW_BIND_PHONE);
			}
		});
		mBindPhoneLaterBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (BFGameConfig.CALLBACK_LISTENER != null)
					BFGameConfig.CALLBACK_LISTENER.callback(
							BFSDKStatusCode.SUCCESS,
							String.valueOf(BFGameConfig.USER_ID));
				((Activity)mContext).finish();
			}
		});
	}

	public void initValue() {
	}

	public View getView(){
		return mBaseView;
	}
	
	public void setHomePanel(HomePanel homePanel){
		this.mHomePanel = homePanel;
	}
	
	public static void releaseViews(){
		mBXBindPhoneAgainView = null;
	}
	
	
}
