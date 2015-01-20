package com.baofeng.game.sdk.user.home;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.net.RequestParameter;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.user.BFBasePanel;
import com.baofeng.game.sdk.user.HomePanel;

public class BFBindPhoneView extends BFBasePanel {
	private static BFBindPhoneView BXBindPhoneView;

	private HomePanel mHomePanel;
	private View mBaseView;

	private TextView mUserNameEt;
	private EditText mPhoneNumberEt;
	private EditText mVerifycodeEt;
	private TextView mGetVerifycodeBtn;
	private Button mBindPhoneBtn;
	private Button mBackBtn;

	private String mPhoneNumber;
	private String mPhoneVerityCode;
	private boolean isGetVerityCode = false;

	private final int GET_VERITY_CODE_KEY = 10001;
	private final int BIND_PHONENUMBER_KEY = 10002;

	public synchronized static BFBindPhoneView getInstance(Context context) {
		if (BXBindPhoneView == null) {
			BXBindPhoneView = new BFBindPhoneView(context);
		}
		return BXBindPhoneView;
	}

	private BFBindPhoneView(Context context) {
		super(context);

		init();
		initView();
		initListener();
		initValue();
	}

	public void init() {
		mBaseView = mInflater.inflate(BFResources.layout.bx_bind_phone, null);
	}

	public void initView() {
		mUserNameEt = (TextView) mBaseView.findViewById(BFResources.id.bx_bind_phone_user);
		mPhoneNumberEt = (EditText) mBaseView.findViewById(BFResources.id.bx_bind_phone_input_phone);
		mVerifycodeEt = (EditText) mBaseView.findViewById(BFResources.id.bx_bind_phone_input_veritycode);
		mGetVerifycodeBtn = (TextView) mBaseView.findViewById(BFResources.id.bx_bind_phone_get_verifycode);
		mBindPhoneBtn = (Button) mBaseView.findViewById(BFResources.id.bx_bind_phone_now_bind);
		mBackBtn = (Button) mBaseView.findViewById(BFResources.id.bx_back);
		
//		mGetVerifycodeBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	}

	public void initListener() {
		mGetVerifycodeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mPhoneNumber = mPhoneNumberEt.getText().toString().trim();
				if (TextUtils.isEmpty(mPhoneNumber)) {
					showToast("请输入手机号");
					return;
				}
				if (!isPhoneNumber(mPhoneNumber)) {
					showToast("请输入正确格式的手机号");
					return;
				}

				if (BFGameConfig.GAMEPARAM != null) {
					getPhoneVerityCode(BFGameConfig.GAMEPARAM.getCpId(),
							BFGameConfig.GAMEPARAM.getGameId(),
							BFGameConfig.GAMEPARAM.getServerId(),
							BFGameConfig.ACCOUNT, mPhoneNumber);
				}
			}
		});

		mBindPhoneBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (isGetVerityCode) {
					// 请求过验证码，进行手机号绑定
					mPhoneVerityCode = mVerifycodeEt.getText().toString()
							.trim();
					if (TextUtils.isEmpty(mPhoneVerityCode)) {
						showToast("请输入短信验证码");
						return;
					}

					bindPhoneNumber(BFGameConfig.ACCOUNT, mPhoneNumber, mPhoneVerityCode);
				} else {
					// 没有请求过验证码，提示先请求验证码
					showToast("请先获取验证码");
				}
			}
		});
		
		mBackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mHomePanel.showViewType(HomePanel.SHOW_ACCOUNT_INFO);
				BFAccountInfoView.getInstance(mContext).initValue();
			}
		});
	}

	
	private class MyCount extends CountDownTimer {

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mGetVerifycodeBtn.setText("获取验证码");
			mGetVerifycodeBtn
					.setBackgroundResource(BFResources.drawable.bx_btn_selector_3);
			mGetVerifycodeBtn.setClickable(true);

		}

		@Override
		public void onTick(long millisUntilFinished) {
			mGetVerifycodeBtn.setClickable(false);
			mGetVerifycodeBtn
					.setBackgroundResource(BFResources.drawable.bx_btn_account_info_bind_phone);
			mGetVerifycodeBtn.setText(millisUntilFinished / 1000 + "s重新发送");
		}

	}
	public void initValue() {
		mUserNameEt.setText(BFGameConfig.ACCOUNT);
	}

	public View getView() {
		return mBaseView;
	}

	public void setHomePanel(HomePanel homePanel) {
		this.mHomePanel = homePanel;
	}

	/**
	 * 绑定手机号发送短信验证码 调用的前提是用户已经登录
	 * 
	 * @param cp_id
	 *            合作者编号，CP唯一编号
	 * @param game_id
	 *            游戏编号
	 * @param server_id
	 *            服务器编号
	 * @param account
	 *            用户账号
	 * @param phoneNumber
	 *            电话号码
	 */
	private void getPhoneVerityCode(int cp_id, int game_id, int server_id,
			String account, String phoneNumber) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("cp_id", String.valueOf(cp_id)));
		parameter.add(new RequestParameter("game_id", String.valueOf(game_id)));
		parameter.add(new RequestParameter("server_id", String
				.valueOf(server_id)));
		parameter.add(new RequestParameter("account", account));
		parameter.add(new RequestParameter("phone_number", phoneNumber));

		startHttpRequst(BFGameConfig.HTTP_POST,
				BFGameConfig.BX_SERVICE_URL_GET_VERITY_CODE, parameter, true,
				"", true, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, GET_VERITY_CODE_KEY);
	}

	/**
	 * 用户绑定手机 调用的前提是用户已经登录
	 * 
	 * @param account
	 *            用户账号
	 * @param phoneNumber
	 *            电话号码
	 * @param verityCode
	 *            验证码
	 */
	private void bindPhoneNumber(String account, String phoneNumber,
			String verityCode) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("account", account));
		parameter.add(new RequestParameter("phone_number", phoneNumber));
		parameter.add(new RequestParameter("code", verityCode));
		startHttpRequst(BFGameConfig.HTTP_POST,
				BFGameConfig.BX_SERVICE_URL_BIND_PHONENUMBER, parameter, true,
				"", true, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, BIND_PHONENUMBER_KEY);
	}

	@Override
	public void onCallBackFromThread(String resultJson, int resultCode) {
		// TODO Auto-generated method stub
		super.onCallBackFromThread(resultJson, resultCode);
		try {
			int code = 0;
			String msg = "";
			if (!TextUtils.isEmpty(resultJson)) {
				JSONObject resultObject = new JSONObject(resultJson);
				System.out.println("================resultObject============="
						+ resultObject);
				if (!resultObject.isNull("code")) {
					code = resultObject.getInt("code");
				}
				if (!resultObject.isNull("msg")) {
					msg = resultObject.getString("msg");
				}

				switch (resultCode) {
				case GET_VERITY_CODE_KEY:
					if (code == BFSDKStatusCode.SUCCESS) {
						isGetVerityCode = true;
						new MyCount(60000, 1000).start();
						showToast("验证码已发送，请稍等…");
					} else {
						showToast(code);
					}
					break;
				case BIND_PHONENUMBER_KEY:
					if (code == BFSDKStatusCode.SUCCESS) {
						showToast("手机号绑定成功");
						mHomePanel.showViewType(HomePanel.SHOW_ACCOUNT_INFO);
						BFAccountInfoView.getInstance(mContext).initValue();
					} else if (code == BFSDKStatusCode.OLD_BIND_DEL_NEW_BIND_SUCCESS) {
						showToast("原账号已解绑，绑定新账号成功");
						mHomePanel.showViewType(HomePanel.SHOW_ACCOUNT_INFO);
						BFAccountInfoView.getInstance(mContext).initValue();
					} else {
						showToast(code);
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void releaseViews(){
		BXBindPhoneView = null;
	}
}
