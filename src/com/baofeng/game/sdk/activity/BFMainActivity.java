package com.baofeng.game.sdk.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.base.BFBaseActivity;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.net.RequestParameter;
import com.baofeng.game.sdk.type.BFOrientation;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.type.OperateType;
import com.baofeng.game.sdk.ui.BFLoadingDialog;
import com.baofeng.game.sdk.user.HomePanel;
import com.baofeng.game.sdk.user.MainBodyItem;
import com.baofeng.game.sdk.user.home.BFAccountInfoView;
import com.baofeng.game.sdk.user.home.BFBindPhoneAgainView;
import com.baofeng.game.sdk.user.home.BFBindPhoneView;
import com.baofeng.game.sdk.user.home.BFChangeBindPhoneAgainView;
import com.baofeng.game.sdk.user.home.BFChangePasswordView;
import com.baofeng.game.sdk.user.home.BFHomeWebView;
import com.baofeng.game.sdk.user.home.BFResetPasswordView;
import com.baofeng.game.sdk.util.BXDialogUtil.OnAlertSelectId;
import com.baofeng.game.sdk.util.DeviceUtil;
import com.baofeng.game.sdk.util.ParseUtil;
import com.baofeng.game.sdk.util.StringUtil;
import com.baofeng.game.sdk.util.Util;
import com.baofeng.game.sdk.vo.BFGameCommon;

public class BFMainActivity extends BFBaseActivity implements OnAlertSelectId {

	private static final int REQUEST_SERVER_KEY = 2;
	private static final int REQUEST_COMMON_KEY = 1;

	private BFLoadingDialog bfLoadingDialog;
	private LinearLayout body;
	private MainBodyItem mHomeBody;
	private TextView service;
	private TextView qqGroup;
	private RelativeLayout.LayoutParams childLayoutParams = new RelativeLayout.LayoutParams(
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			int retCode = msg.getData().getInt("retCode");
			int retType = msg.getData().getInt("retType");

			if (retType == OperateType.LOGIN) {
				switch (retCode) {
				case BFSDKStatusCode.SUCCESS:
					if (BFGameConfig.CALLBACK_LISTENER != null)
						BFGameConfig.CALLBACK_LISTENER.callback(
								BFSDKStatusCode.SUCCESS,
								String.valueOf(BFGameConfig.USER_ID));
					finish();
					break;
				case BFSDKStatusCode.USER_NOT_EXIST:
					showToast("用户不存在");
					break;
				case BFSDKStatusCode.PASSWORD_ERROR:
					showToast("用户密码错误");
					break;
				case BFSDKStatusCode.LOGIN_ERROR:
					showToast("用户名密码错误");
					break;
				case BFSDKStatusCode.LOGIN_USER_AUTH_FAIL:
					showToast("认证失败");
					break;
				case BFSDKStatusCode.SYSTEM_ERROR:
					showToast("业务繁忙请稍后再试");
					break;
				case BFSDKStatusCode.FAIL:
					showToast("登陆失败");
					break;
				case BFSDKStatusCode.LOGIN_EXIT:
					if (BFGameConfig.CALLBACK_LISTENER != null)
						BFGameConfig.CALLBACK_LISTENER.callback(
								BFSDKStatusCode.LOGIN_EXIT, "");
					finish();
					break;
				}
			} else if (retType == OperateType.QUICK_REGISTER) {
				switch (retCode) {
				case BFSDKStatusCode.SUCCESS:
					showToast("登录成功");

					if (BFGameConfig.CALLBACK_LISTENER != null)
						BFGameConfig.CALLBACK_LISTENER.callback(
								BFSDKStatusCode.SUCCESS,
								String.valueOf(BFGameConfig.USER_ID));

					finish();

					break;
				case BFSDKStatusCode.LOGIN_ERROR:
					showToast("用户名密码错误");
					break;
				case BFSDKStatusCode.LOGIN_USER_AUTH_FAIL:
					showToast("认证失败");
					break;
				case BFSDKStatusCode.SYSTEM_ERROR:
					showToast("业务繁忙请稍后再试");
					break;
				case BFSDKStatusCode.FAIL:
					showToast("登陆失败");
					break;
				}
			} else if (retType == OperateType.NO_INIT) {
				if (BFGameConfig.CALLBACK_LISTENER != null) {
					BFGameConfig.CALLBACK_LISTENER.callback(
							BFSDKStatusCode.NO_INIT, "");
				}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		int width = Util.getScreenRect(this).right;
		int height = Util.getScreenRect(this).bottom;

		if (BFGameConfig.SCREEN_ORIENTATION == BFOrientation.VERTICAL) {
			if (width > height) {
				int temp = width;
				width = height;
				height = temp;
			}
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			if (DeviceUtil.isTablet(context)) {
				width = (int) (width * BFGameConfig.SCREEN_PROPORTION * 0.6);
				height = (int) (height * BFGameConfig.SCREEN_PROPORTION * 0.4);
			} else {
				width = (int) (width * BFGameConfig.SCREEN_PROPORTION * 1.0);
				height = (int) (height * BFGameConfig.SCREEN_PROPORTION * 0.6);
			}
		} else {
			if (width < height) {
				int temp = width;
				width = height;
				height = temp;
			}
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			if (DeviceUtil.isTablet(context)) {
				width = (int) (width * BFGameConfig.SCREEN_PROPORTION * 0.4);
				height = (int) (height * BFGameConfig.SCREEN_PROPORTION * 0.6);
			} else {
				width = (int) (width * BFGameConfig.SCREEN_PROPORTION * 0.6);
				height = (int) (height * BFGameConfig.SCREEN_PROPORTION * 1.1);
			}
		}

		View view = LayoutInflater.from(this).inflate(
				BFResources.layout.bx_main_activity, null);
		setContentView(view, new RelativeLayout.LayoutParams(width, height));

		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view
				.getLayoutParams();
		lp.gravity = Gravity.CENTER;
		view.setLayoutParams(lp);

		if (null != BFGameConfig.CONTEXT) {
			BFGameConfig.PACKAGE_NAME = BFGameConfig.CONTEXT.getPackageName();
		}

		PackageInfo pinfo;
		try {
			pinfo = getPackageManager().getPackageInfo(
					BFGameConfig.PACKAGE_NAME, 0);
			BFGameConfig.VERSION_CODE = pinfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		getServerKey();
		initView();
	}

	private void getServerKey() {
		bfLoadingDialog = new BFLoadingDialog(context, "请求登录...", false);
		bfLoadingDialog.show();
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		startHttpRequst(BFGameConfig.HTTP_GET, BFGameConfig.SERVER_KEY,
				parameter, false, "", false,
				BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, REQUEST_SERVER_KEY);
	}

	private void getCommon() {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		startHttpRequst(BFGameConfig.HTTP_GET, BFGameConfig.COMMON, parameter,
				false, "", false, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, REQUEST_COMMON_KEY);
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	private void initView() {
		service = (TextView) findViewById(BFResources.id.service);
		qqGroup = (TextView) findViewById(BFResources.id.qqgroup);
		body = (LinearLayout) findViewById(BFResources.id.body);
		mHomeBody = new HomePanel(this);
		setBody(mHomeBody.getView(BFMainActivity.this));

	}

	private void cancelMyRequest() {
		if (mHomeBody != null)
			((HomePanel) mHomeBody).onDestory();
	}

	public void loginResult(int retType, int retCode) {
		Message msg = Message.obtain();

		Bundle data = new Bundle();
		data.putInt("retType", retType);
		data.putInt("retCode", retCode);

		msg.setData(data);

		mHandler.sendMessage(msg);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// loginResult(OperateType.LOGIN, BFSDKStatusCode.LOGIN_EXIT);
			// BFResources.context = null;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCallBackFromThread(String resultJson, int resultCode) {
		// TODO Auto-generated method stub
		super.onCallBackFromThread(resultJson, resultCode);
		if (!TextUtils.isEmpty(resultJson)) {
			try {
				switch (resultCode) {

				case REQUEST_SERVER_KEY:

					HashMap<String, Object> result = ParseUtil
							.getData(resultJson);

					if (result != null) {
						int code = (Integer) result.get("code");
						if (code == BFSDKStatusCode.SUCCESS) {
							getCommon();
							BFGameConfig.SERVERKEY = String.valueOf(result
									.get("data"));
							if (StringUtil.isNotEmpty(BFGameConfig.ACCOUNT)) {
								BFAccountInfoView.getInstance(context)
										.initValue();
							}
						} else {
							bfLoadingDialog.dismiss();
							showToast("操作失败，请检查网络后重试");
							retry();
							return;
						}
					} else {
						bfLoadingDialog.dismiss();
						showToast("操作失败，请检查网络后重试");
						retry();
						return;
					}

					break;

				case REQUEST_COMMON_KEY:

					BFGameCommon res = ParseUtil.getCommData(resultJson);
					if (res != null && res.getVersion() != null) {

						BFGameConfig.GAME_CARD = res.getGame_card_open();
						if(StringUtil.isNotEmpty(res.getQq())){
							BFGameConfig.QQ_QA = res.getQq();
							service.setText("客服QQ:" + BFGameConfig.QQ_QA);
						}else {
							service.setText("");
						}
						if(StringUtil.isNotEmpty(res.getQqGroup())){
							BFGameConfig.QQ_GROUP = res.getQqGroup();
							qqGroup.setText("玩家QQ群:" + BFGameConfig.QQ_GROUP);
						}else {
							qqGroup.setText("");
						}
						BFGameConfig.TEL_QA = res.getPhone();
						bfLoadingDialog.dismiss();
					} else {
						bfLoadingDialog.dismiss();
						showToast("操作失败，请检查网络后重试");
						retry();
						return;
					}
					break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				bfLoadingDialog.dismiss();
				showToast("操作失败，请检查网络后重试");
				retry();
				return;
			}
		}

	}

	@Override
	protected void onDestroy() {
		cancelMyRequest();
		BFAccountInfoView.releaseViews();
		BFBindPhoneAgainView.releaseViews();
		BFBindPhoneView.releaseViews();
		BFChangePasswordView.releaseViews();
		BFHomeWebView.getInstance(context).releaseViews();
		BFResetPasswordView.releaseViews();
		BFChangeBindPhoneAgainView.releaseViews();
		super.onDestroy();
	}

	private void retry() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("请求失败，请检查网络后重试");
		builder.setPositiveButton("重试", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				getServerKey();
			}
		});
		builder.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						|| keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				}
				return false;
			}
		});
		builder.show();
	}

	private void setBody(View child) {
		body.removeAllViews();
		child.setLayoutParams(childLayoutParams);
		body.addView(child);
	}

	@Override
	public void onClick(int whichButton, Object o) {
		// TODO Auto-generated method stub
		if (whichButton == 1) {
			HashMap<String, Object> data = (HashMap<String, Object>) o;
			if (!TextUtils.isEmpty((String) data.get("downloadurl"))) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse((String) data.get("downloadurl"));
				intent.setData(content_url);
				context.startActivity(intent);
			} else {
				Toast.makeText(context, "下载地址错误", Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
}
