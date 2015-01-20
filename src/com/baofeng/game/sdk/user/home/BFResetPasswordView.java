package com.baofeng.game.sdk.user.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.activity.BFMainActivity;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.net.RequestParameter;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.type.OperateType;
import com.baofeng.game.sdk.user.BFBasePanel;
import com.baofeng.game.sdk.user.HomePanel;
import com.baofeng.game.sdk.util.DeviceUtil;
import com.baofeng.game.sdk.util.ParseUtil;
import com.baofeng.game.sdk.util.StringUtil;
import com.baofeng.game.sdk.vo.BFUser;

public class BFResetPasswordView extends BFBasePanel {
	private static BFResetPasswordView mBXResetPasswordView;

	private HomePanel mHomePanel;
	private View mBaseView;

	private EditText mResetPasswordVerifyCode;
	private EditText mResetPasswordInput;
	private Button mGetVerifyCodeBtn;
	private Button mRestPasswordSubmitBtn;
	private Button mBackBtn;

	private final int REQUEST_SEND_PHONENUMBER_CODE = 10001;
	private final int REQUEST_RESET_PASSWORD_KEY = 10003;
	private final int REQUEST_LOGIN_KEY = 10004;

	private boolean isGetVerityCode = false;

	public synchronized static BFResetPasswordView getInstance(Context context) {
		if (mBXResetPasswordView == null) {
			mBXResetPasswordView = new BFResetPasswordView(context);
		}
		return mBXResetPasswordView;
	}

	private BFResetPasswordView(Context context) {
		super(context);

		init();
		initView();
		initListener();
		initValue();
	}

	public void init() {
		mBaseView = mInflater.inflate(BFResources.layout.bx_reset_password,
				null);
	}

	public void initView() {
		mResetPasswordVerifyCode = (EditText) mBaseView
				.findViewById(BFResources.id.bx_reset_password_verifycode);
		mResetPasswordInput = (EditText) mBaseView
				.findViewById(BFResources.id.bx_reset_password_input);
		mGetVerifyCodeBtn = (Button) mBaseView
				.findViewById(BFResources.id.bx_reset_password_get_veryficode);
		mRestPasswordSubmitBtn = (Button) mBaseView
				.findViewById(BFResources.id.bx_reset_password_btn);
		mBackBtn = (Button) mBaseView.findViewById(BFResources.id.bx_back);
	}

	public void initListener() {

		mGetVerifyCodeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (BFGameConfig.GAMEPARAM != null) {
					getPhoneVerityCode(BFGameConfig.GAMEPARAM.getCpId(),
							BFGameConfig.GAMEPARAM.getGameId(),
							BFGameConfig.GAMEPARAM.getServerId(),
							BFGameConfig.ACCOUNT, mHomePanel.phone_Number);
				}
			}
		});

		mRestPasswordSubmitBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				if (isGetVerityCode) {
					if (StringUtil.isEmpty(mResetPasswordInput.getText()
							.toString())) {
						showToast("密码不能为空");
					} else if (mResetPasswordInput.getText().toString()
							.length() < 6) {
						showToast("请填写6位以上数字与字母组合密码");
					} else if (StringUtil.isEmpty(mResetPasswordVerifyCode
							.getText().toString())) {
						showToast("验证码不正确");
					} else {
						resetPassword(BFGameConfig.ACCOUNT, mResetPasswordInput
								.getText().toString(), mHomePanel.phone_Number,
								mResetPasswordVerifyCode.getText().toString());
					}
				} else {
					// 没有请求过验证码，提示先请求验证码
					showToast("请先获取验证码");
				}

			}
		});

		mBackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mHomePanel.showViewType(HomePanel.SHOW_HOME_PANEL);
			}
		});
	}

	public void initValue() {
	}

	public View getView() {
		return mBaseView;
	}

	public void setHomePanel(HomePanel homePanel) {
		this.mHomePanel = homePanel;
	}

	public static void releaseViews() {
		mBXResetPasswordView = null;
	}

	/**
	 * 找回密码手机号发送短信验证码 调用的前提是用户已经登录
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
				BFGameConfig.BX_SERVICE_URL_GET_RESET_PASSWORD_VERITY_CODE,
				parameter, true, "", true,
				BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, REQUEST_SEND_PHONENUMBER_CODE);
	}

	/**
	 * 重置密码
	 * 
	 * @param account
	 *            用户账号
	 * @param phone_number
	 *            电话号码
	 * @param code
	 *            短信验证码
	 * @param password
	 *            用户新密码
	 */
	private void resetPassword(String account, String password,
			String phone_number, String code) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("account", account));
		parameter.add(new RequestParameter("phone_number", phone_number));
		parameter.add(new RequestParameter("code", code));
		parameter.add(new RequestParameter("new_pwd", password));
		startHttpRequst(BFGameConfig.HTTP_POST,
				BFGameConfig.BX_SERVICE_URL_RESET_PASSWORD, parameter, true,
				"", true, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, REQUEST_RESET_PASSWORD_KEY);
	}

	/**
	 * 用户登录
	 * 
	 * @param cp_id
	 *            合作者唯一编号,由冰雪游戏 网游戏生成并提供
	 * @param account
	 *            冰雪游戏 网账号
	 * @param password
	 *            冰雪游戏 网密码
	 * @param game_id
	 *            联运游戏编号
	 * @param server_id
	 *            联运服务器编号:用户验证后需要跳转的服务器编号没有为0
	 */
	private void accountLogin(int cp_id, String account, String password,
			int game_id, int server_id, int key, String userType) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("account", account));
		parameter.add(new RequestParameter("password", password));
		parameter.add(new RequestParameter("cp_id", String.valueOf(cp_id)));
		parameter.add(new RequestParameter("game_id", String.valueOf(game_id)));
		parameter.add(new RequestParameter("server_id", String
				.valueOf(server_id)));
		parameter.add(new RequestParameter("os_version", DeviceUtil
				.getSDKVersion()));
		parameter.add(new RequestParameter("os_sdk_version", String
				.valueOf(DeviceUtil.getSDKVersionInt())));
		parameter.add(new RequestParameter("is_bf_user", userType));

		startHttpRequst(BFGameConfig.HTTP_GET,
				BFGameConfig.BX_SERVICE_URL_ACCOUNT_LOGIN, parameter, true, "",
				false, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, key);
	}

	@Override
	public void onCallBackFromThread(String resultJson, int resultCode) {
		// TODO Auto-generated method stub
		super.onCallBackFromThread(resultJson, resultCode);
		try {
			switch (resultCode) {
			case REQUEST_SEND_PHONENUMBER_CODE:
				JSONObject resultObject = new JSONObject(resultJson);
				int code = 0;
				if (!resultObject.isNull("code")) {
					code = resultObject.getInt("code");
				}
				if (code == BFSDKStatusCode.SUCCESS) {
					isGetVerityCode = true;
					showToast("验证码已发送");
					new CountDownTimer(60000, 1000) {// 两个参数，前一个指倒计时的总时间，后一个指多长时间倒数一下。

						@Override
						public void onTick(long millisUntilFinished) {
							// TODO Auto-generated method stub
							mGetVerifyCodeBtn.setText("重新获取" + millisUntilFinished
									/ 1000 + "s");
							mGetVerifyCodeBtn.setClickable(false);
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							mGetVerifyCodeBtn.setText("获取验证码");
							mGetVerifyCodeBtn.setClickable(true);
						}
					}.start();
				}else {
					showToast("操作失败请重试");
				}

				break;
			case REQUEST_RESET_PASSWORD_KEY:
				resultObject = new JSONObject(resultJson);
				code = 0;
				if (!resultObject.isNull("code")) {
					code = resultObject.getInt("code");
				}
				if (code == BFSDKStatusCode.SUCCESS) {
					// 静态变量中设置当前密码 //FIXME
					BFGameConfig.PASSWORD = mResetPasswordInput.getText()
							.toString();
					// 将新的密码写入账户文件中
					mHomePanel.changeLocationAccountFile();
					// 是否初始化
					if (BFGameConfig.GAMEPARAM != null) {
						accountLogin(BFGameConfig.GAMEPARAM.getCpId(),
								BFGameConfig.ACCOUNT, BFGameConfig.PASSWORD,
								BFGameConfig.GAMEPARAM.getGameId(),
								BFGameConfig.GAMEPARAM.getServerId(),
								REQUEST_LOGIN_KEY, "0");
					} else {

						((BFMainActivity) mContext).loginResult(
								OperateType.NO_INIT, OperateType.NO_INIT);

					}

					// showViewType(SHOW_HOME_PANEL);

				} else {
					showToast(code);
				}
				break;
			case REQUEST_LOGIN_KEY:
				HashMap<String, Object> loginResult = ParseUtil
						.getLoginUserData(resultJson);
				if (loginResult != null) {
					code = (Integer) loginResult.get("code");
					if (code == BFSDKStatusCode.SUCCESS) {
						BFUser user = (BFUser) loginResult.get("data");
						BFGameConfig.isSMSOK_Online = (Integer) user.getSMS() == 1 ? true
								: false;
						if (user != null) {
							if (!BFGameConfig.isQQLogin) {
								if (user.getQuick_game() == HomePanel.QUICK_GAME) {
									// 强制用户修改密码
									mHomePanel.showChangePasswordDialgo();
								} else {
									BFGameConfig.TICKET = user.getTicket();
									BFGameConfig.USER_ID = user.getUser_id();
									BFGameConfig.USER_NAME = BFGameConfig.ACCOUNT;
									mHomePanel.rememberAccount();
									((BFMainActivity) mContext).loginResult(
											OperateType.LOGIN, code);
								}
							} else {
								BFGameConfig.TICKET = user.getTicket();
								BFGameConfig.USER_ID = user.getUser_id();
								BFGameConfig.USER_NAME = BFGameConfig.ACCOUNT;
								((BFMainActivity) mContext).loginResult(
										OperateType.LOGIN, code);
							}

						}
					} else {
						((BFMainActivity) mContext).loginResult(
								OperateType.LOGIN, code);
					}

				} else {
					((BFMainActivity) mContext).loginResult(OperateType.LOGIN,
							BFSDKStatusCode.SYSTEM_ERROR);
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			showToast("操作失败请重试");
		}
	}
}
