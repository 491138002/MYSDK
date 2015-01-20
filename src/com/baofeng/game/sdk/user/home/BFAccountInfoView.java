package com.baofeng.game.sdk.user.home;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.activity.BFMainActivity;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.net.RequestParameter;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.type.OperateType;
import com.baofeng.game.sdk.user.BFBasePanel;
import com.baofeng.game.sdk.user.HomePanel;
import com.baofeng.game.sdk.util.AccountPersistenceUtil;
import com.baofeng.game.sdk.util.DeviceUtil;
import com.baofeng.game.sdk.util.ParseUtil;
import com.baofeng.game.sdk.util.StringUtil;
import com.baofeng.game.sdk.util.AccountPersistenceUtil.OnAccountFileReadComplete;
import com.baofeng.game.sdk.vo.BFUser;

public class BFAccountInfoView extends BFBasePanel {
	private static BFAccountInfoView mBXAccountInfoView;
	private ArrayList<HashMap<String, String>> loggedAccounts;// 之前在本机登录过的账号集合
	private HomePanel mHomePanel;
	private View mBaseView;

	private TextView mAccountUserTv;
	private ImageView mAccountSwitchBtn;
	private Button mAccountBindPhoneBtn;
	private Button mAccountChangePasswordBtn;
	private Button mAccountShowGameBtn;
	// private Button mBackBtn;

	private boolean isBinded = false;

	private final int REQUEST_LOGIN_KEY = 10002;
	private final int BIND_CHECK_PHONENUMBER_KEY = 10003;

	public synchronized static BFAccountInfoView getInstance(Context context) {
		if (mBXAccountInfoView == null) {
			mBXAccountInfoView = new BFAccountInfoView(context);
		}
		return mBXAccountInfoView;
	}

	private BFAccountInfoView(Context context) {
		super(context);

		init();
		initView();
		initListener();
		initValue();
	}

	public void init() {
		mBaseView = mInflater.inflate(BFResources.layout.bx_account_info, null);
	}

	public void initView() {
		mAccountUserTv = (TextView) mBaseView
				.findViewById(BFResources.id.bx_account_info_name);
		mAccountSwitchBtn = (ImageView) mBaseView
				.findViewById(BFResources.id.bx_account_switch_info_btn);
		mAccountBindPhoneBtn = (Button) mBaseView
				.findViewById(BFResources.id.bx_account_info_bind_phone);
		mAccountChangePasswordBtn = (Button) mBaseView
				.findViewById(BFResources.id.bx_account_info_change_password);
		mAccountShowGameBtn = (Button) mBaseView
				.findViewById(BFResources.id.bx_account_info_show_game);
		// mBackBtn = (Button) mBaseView.findViewById(BFResources.id.bx_back);
		if (StringUtil.isNotEmpty(BFGameConfig.ACCOUNT)) {
			mAccountUserTv.setText(BFGameConfig.ACCOUNT);
		}
	}

	public void initListener() {
		mAccountSwitchBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mHomePanel.showViewType(HomePanel.SHOW_HOME_PANEL);
			}
		});
		mAccountBindPhoneBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (isBinded)
					mHomePanel.showViewType(HomePanel.SHOW_CHANGE_BIND_PHONE);
				else
					mHomePanel.showViewType(HomePanel.SHOW_BIND_PHONE);
			}
		});
		mAccountChangePasswordBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mHomePanel.showViewType(HomePanel.SHOW_CHANGE_PASSWORD);
			}
		});
		mAccountShowGameBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// 是否初始化
				if (BFGameConfig.GAMEPARAM != null) {
					if (StringUtil.isNotEmpty(BFGameConfig.TOKEN)) {
						accountLoginByToken(BFGameConfig.GAMEPARAM.getCpId(),
								BFGameConfig.GAMEPARAM.getGameId(),
								BFGameConfig.GAMEPARAM.getServerId(),
								BFGameConfig.TOKEN);
					} else {
						accountLogin(BFGameConfig.GAMEPARAM.getCpId(),
								BFGameConfig.ACCOUNT, BFGameConfig.PASSWORD,
								BFGameConfig.GAMEPARAM.getGameId(),
								BFGameConfig.GAMEPARAM.getServerId(),
								REQUEST_LOGIN_KEY, "0");

					}
				} else {

					((BFMainActivity) mContext).loginResult(
							OperateType.NO_INIT, OperateType.NO_INIT);

				}
			}
		});
		// mBackBtn.setOnClickListener(new OnClickListener() {
		// public void onClick(View arg0) {
		// // mHomePanel.showViewType(HomePanel.SHOW_HOME_PANEL);
		// ((BFMainActivity) mContext).loginResult(OperateType.LOGIN,
		// BFSDKStatusCode.LOGIN_EXIT);
		// }
		// });
	}

	public void initValue() {
		if (StringUtil.isNotEmpty(BFGameConfig.TOKEN)) {
			mAccountChangePasswordBtn.setClickable(false);
			mAccountChangePasswordBtn.setTextColor(Color.GRAY);
			mAccountUserTv.setTextColor(Color.WHITE);
			mAccountBindPhoneBtn.setClickable(false);
			mAccountBindPhoneBtn.setTextColor(Color.GRAY);
		} else {
			mAccountChangePasswordBtn.setClickable(true);
			mAccountChangePasswordBtn.setTextColor(Color.BLACK);
			mAccountUserTv.setTextColor(Color.WHITE);
			mAccountBindPhoneBtn.setClickable(true);
			mAccountBindPhoneBtn.setTextColor(Color.BLACK);
			if (StringUtil.isNotEmpty(BFGameConfig.ACCOUNT)
					&& StringUtil.isNotEmpty(BFGameConfig.SERVER_KEY)) {
				bindCheckPhoneNumber(BFGameConfig.ACCOUNT);
			}
		}
		if (StringUtil.isNotEmpty(BFGameConfig.ACCOUNT)) {
			mAccountUserTv.setText(BFGameConfig.ACCOUNT);
		}

	}

	// 异步读取以保存的账户数据
	private void initLoggedAccountData() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				loggedAccounts = (ArrayList<HashMap<String, String>>) msg.obj;
				if (loggedAccounts != null && loggedAccounts.size() > 0) {
					HashMap<String, String> account = loggedAccounts.get(0);
					BFGameConfig.ACCOUNT = account.get("name");
				}
			}
		};
		AccountPersistenceUtil.readAccountByFile(accountFileSavePath
				+ File.separator + accountFileName,
				new OnAccountFileReadComplete() {

					@Override
					public void onFileReadCompleteListener(
							ArrayList<HashMap<String, String>> accounts) {
						Message msg = handler.obtainMessage();
						msg.obj = accounts;
						msg.sendToTarget();

					}
				});

	}

	public View getView() {
		return mBaseView;
	}

	public void setHomePanel(HomePanel homePanel) {
		this.mHomePanel = homePanel;
	}

	public static void releaseViews() {
		mBXAccountInfoView = null;
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

	/**
	 * 用户登录(Token)
	 * 
	 * @param token
	 *            url返回标识
	 * @param cp_id
	 *            合作者唯一编号,由冰雪游戏 网游戏生成并提供
	 * @param game_id
	 *            联运游戏编号
	 * @param server_id
	 *            联运服务器编号:用户验证后需要跳转的服务器编号没有为0
	 */
	private void accountLoginByToken(int cp_id, int game_id, int server_id,
			String token) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("cp_id", String.valueOf(cp_id)));
		parameter.add(new RequestParameter("game_id", String.valueOf(game_id)));
		parameter.add(new RequestParameter("server_id", String
				.valueOf(server_id)));
		parameter.add(new RequestParameter("token", token));

		startHttpRequst(BFGameConfig.HTTP_GET,
				BFGameConfig.BX_SERVICE_URL_ACCOUNT_LOGIN_BYTOKEN, parameter,
				true, "", false, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, REQUEST_LOGIN_KEY);
	}

	/**
	 * 判断用户是否绑定了手机
	 * 
	 * @param account
	 *            用户账号
	 */
	private void bindCheckPhoneNumber(String account) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("account", account));
		startHttpRequst(BFGameConfig.HTTP_POST,
				BFGameConfig.BX_SERVICE_URL_ALREADY_BIND_PHONE_TWO, parameter,
				true, "", false, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, BIND_CHECK_PHONENUMBER_KEY);
	}

	@Override
	public void onCallBackFromThread(String resultJson, int resultCode) {
		// TODO Auto-generated method stub
		super.onCallBackFromThread(resultJson, resultCode);
		try {
			switch (resultCode) {
			case REQUEST_LOGIN_KEY:
				HashMap<String, Object> loginResult = ParseUtil
						.getLoginUserData(resultJson);
				if (loginResult != null) {
					int code = (Integer) loginResult.get("code");
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
			case BIND_CHECK_PHONENUMBER_KEY:
				// 是否这么判断 待确定
				int code = 0;
				String msg;
				JSONObject resultObject = new JSONObject(resultJson);
				if (!resultObject.isNull("code")) {
					code = resultObject.getInt("code");
				}
				if (!resultObject.isNull("msg")) {
					msg = resultObject.getString("msg");
				}
				if (code == BFSDKStatusCode.SUCCESS) {
					isBinded = true;
					JSONObject data = resultObject.getJSONObject("data");
					mHomePanel.phone_Number = data.optString("phone_number");
					if (StringUtil.isNotEmpty(mHomePanel.phone_Number)
							|| StringUtil.isNotEmpty(BFGameConfig.TOKEN)) {
						mAccountBindPhoneBtn.setClickable(false);
						mAccountBindPhoneBtn.setTextColor(Color.GRAY);
					} else {
						mAccountBindPhoneBtn.setClickable(true);
						mAccountBindPhoneBtn.setTextColor(Color.BLACK);
					}
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
