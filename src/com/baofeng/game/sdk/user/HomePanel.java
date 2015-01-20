package com.baofeng.game.sdk.user;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.activity.BFMainActivity;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.net.RequestParameter;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.type.OperateType;
import com.baofeng.game.sdk.ui.BFCustomDialog;
import com.baofeng.game.sdk.user.home.BFAccountInfoView;
import com.baofeng.game.sdk.user.home.BFBindPhoneAgainView;
import com.baofeng.game.sdk.user.home.BFBindPhoneView;
import com.baofeng.game.sdk.user.home.BFChangeBindPhoneAgainView;
import com.baofeng.game.sdk.user.home.BFChangePasswordView;
import com.baofeng.game.sdk.user.home.BFHomeWebView;
import com.baofeng.game.sdk.user.home.BFResetPasswordView;
import com.baofeng.game.sdk.util.AccountPersistenceUtil;
import com.baofeng.game.sdk.util.AccountPersistenceUtil.OnAccountFileReadComplete;
import com.baofeng.game.sdk.util.DeviceUtil;
import com.baofeng.game.sdk.util.ParseUtil;
import com.baofeng.game.sdk.util.StringUtil;
import com.baofeng.game.sdk.vo.BFUser;

public class HomePanel extends BFBasePanel implements MainBodyItem {
	private FrameLayout mCurrentView;
	private View mLoginView = null;
	private View mRegisterView = null;
	private View mQuickPlayView = null;
	private View mFindPasswordView = null;
	private View mResetPasswordView = null;
	private View mBaoFengWebView = null;
	private View mAccountInfoView = null;
	private View mBindPhoneAgainView = null;
	private View mBindPhoneView = null;
	private View mChangePasswordView = null;
	private View mChangeBindPhoneView = null;

	private Button mHomeBaofengLoginBtn;
	// private Button mHomeBackBtn;
	private EditText mHomeAccount;
	private EditText mHomePassword;
	private TextView mHomeFindPassword;
	private TextView mHomeOtherRegister;
	private Button mHomeLogin;
	private Button mHomeRegister;
	private ImageView mHomeSelectAccount;
	private LinearLayout ll_text;

	private Button mQuickStartPlay;
	private Button mQuickRegister;
	private Button mQuickSwitchAccount;
	private EditText mQuickAccount;
	private EditText mQuickPassword;
	private ImageButton mQuickSaveRandomAccount;

	private EditText mRegisterAccount;
	private EditText mRegisterPassword;
	private Button mRegisterNowRegister;
	private Button mRegisterBack;

	private Button mFindPasswordBack;
	private ImageView mFindPWDSelectAccount;
	private EditText mFindPasswordAccount;
	private TextView mFindPasswordType;
	private Button mFindPasswordSendButton;

	public static final int SHOW_HOME_PANEL = 1;
	public static final int SHOW_QUICK_PALY = 2;
	public static final int SHOW_ACCOUNT_REGISTER = 3;
	public static final int SHOW_FIND_PASSWORD = 4;
	public static final int SHOW_RESET_PASSWORD = 5;
	public static final int SHOW_BAOFENG_WEBVIEW = 6;
	public static final int SHOW_BIND_PHONE_AGAIN = 7;
	public static final int SHOW_BIND_PHONE = 8;
	public static final int SHOW_ACCOUNT_INFO = 9;
	public static final int SHOW_CHANGE_PASSWORD = 10;
	public static final int SHOW_CHANGE_BIND_PHONE = 11;

	private static final int REQUEST_LOGIN_KEY = 300;
	private static final int REQUEST_REGISTER_KEY = 301;
	private static final int REQUEST_QQ_REGISTER_KEY = 308;
	private static final int REQUEST_RESET_PASSWORD_KEY = 302;
	private static final int REQUEST_QUICK_PLAY_REGISTER_KEY = 303;
	private static final int REQUEST_QUICK_PLAY_LOGIN_KEY = 304;
	private static final int REQUEST_PHONENUMBER_BIND_CHECK_KEY = 305;
	private static final int REQUEST_SEND_PHONENUMBER_CODE = 309;
	private static final int REQUEST_CHECK_PHONENUMBER_CODE = 310;
	private static final int REQUEST_RANDOM_ACCOUNT_CHANGE_PASSWORD_KEY = 306;
	private static final int REQUEST_GET_GAME_VERSION = 307;

	private boolean isRememdAccount = true;// 是否记住账户 值从checkBox控件获取

	private String stringRegisterAccount;// 注册账户
	private String stringRegisterPassword;// 注册密码

	private String stringRandomAccount;// 随机账号
	private String stringRandomPassword;// 随机密码

	private String stringFindPasswordAccount;// 重置密码的账号
	private String stringResetPassword;// 重置后的密码

	private BFCustomDialog mForceChangePasswordDialog;// 强制修改密码的弹出对话框
	private String stringForceResetPassword;// 强制修改密码对话框中输入的密码

	public static final int QUICK_GAME = 1;
	public static final int COMMON_GAME = 0;
	private int registerType;// 0表示不是快速注册用户，1表示快速注册用户

	private ArrayList<HashMap<String, String>> loggedAccounts;// 之前在本机登录过的账号集合

	private BFMainActivity mContext;

	public String phone_Number = "";

	String yanZhengMa = "";

	public HomePanel(BFMainActivity context) {
		super(context);
		mContext = context;
	}

	@Override
	public View getView(Context context) {
		if (mCurrentView != null) {
			showViewType(SHOW_HOME_PANEL);
			return mCurrentView;
		}

		init();
		showViewType(SHOW_HOME_PANEL);
		setInitValue();
		return mCurrentView;

	}

	private void init() {
		// 只有第一次进入程序的时候调用这个方法，做一些静态变量的重置操作
		initStaticValue();
		initLoggedAccountData();
	}

	// 异步读取以保存的账户数据
	private void initLoggedAccountData() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				loggedAccounts = (ArrayList<HashMap<String, String>>) msg.obj;
				if (mHomeAccount != null && mHomePassword != null) {
					if (loggedAccounts != null && loggedAccounts.size() > 0) {
						setInitValue();
					}
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

	// 异步读取以保存的账户数据,找回密码处
	private void initAccountData() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				loggedAccounts = (ArrayList<HashMap<String, String>>) msg.obj;
				if (mFindPasswordAccount != null
						&& mFindPasswordAccount != null) {
					if (loggedAccounts != null && loggedAccounts.size() > 0) {
						// setInitValue();
					}
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

	private void initStaticValue() {
		BFGameConfig.USER_ID = 0;
		BFGameConfig.USER_NAME = "";
		BFGameConfig.TICKET = "";
		BFGameConfig.ACCOUNT = "";
		BFGameConfig.PASSWORD = "";
		BFGameConfig.USERTYPE = BFGameConfig.userTypeBX;
		BFGameConfig.isQQLogin = false;
	}

	private void initSubView(int type) {
		switch (type) {
		case SHOW_HOME_PANEL:
			if (mLoginView == null) {
				initLayoutByType(mContext, SHOW_HOME_PANEL);
				findLoginView();
				setSubListener(SHOW_HOME_PANEL);
			}
			break;
		case SHOW_QUICK_PALY:
			if (mQuickPlayView == null) {
				initLayoutByType(mContext, SHOW_QUICK_PALY);
				findQuickView();
				setSubListener(SHOW_QUICK_PALY);
			}
			break;
		case SHOW_FIND_PASSWORD:
			if (mFindPasswordView == null) {
				initLayoutByType(mContext, SHOW_FIND_PASSWORD);
				findFindPasswordView();
				setSubListener(SHOW_FIND_PASSWORD);
			}
			break;
		case SHOW_ACCOUNT_REGISTER:
			if (mRegisterView == null) {
				initLayoutByType(mContext, SHOW_ACCOUNT_REGISTER);
				findRegisterView();
				setSubListener(SHOW_ACCOUNT_REGISTER);
			}
			break;
		case SHOW_RESET_PASSWORD:
			if (mResetPasswordView == null) {
				initLayoutByType(mContext, SHOW_RESET_PASSWORD);
			}
			break;
		case SHOW_BAOFENG_WEBVIEW:
			if (mBaoFengWebView == null)
				initLayoutByType(mContext, SHOW_BAOFENG_WEBVIEW);
			break;
		case SHOW_BIND_PHONE_AGAIN:
			if (mBindPhoneAgainView == null)
				initLayoutByType(mContext, SHOW_BIND_PHONE_AGAIN);
			break;
		case SHOW_BIND_PHONE:
			if (mBindPhoneView == null)
				initLayoutByType(mContext, SHOW_BIND_PHONE);
			break;
		case SHOW_ACCOUNT_INFO:
			if (mAccountInfoView == null)
				initLayoutByType(mContext, SHOW_ACCOUNT_INFO);
			break;
		case SHOW_CHANGE_PASSWORD:
			initLayoutByType(mContext, SHOW_CHANGE_PASSWORD);
			break;
		case SHOW_CHANGE_BIND_PHONE:
			if (mChangeBindPhoneView == null)
				initLayoutByType(mContext, SHOW_CHANGE_BIND_PHONE);
			break;
		}
	}

	private void initLayoutByType(Context context, int type) {
		if (mCurrentView == null)
			mCurrentView = new FrameLayout(context);
		switch (type) {
		case SHOW_HOME_PANEL:
			mLoginView = LayoutInflater.from(context).inflate(
					BFResources.layout.bx_home_panel, null);

			mCurrentView.addView(mLoginView, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.FILL_PARENT));
			break;
		case SHOW_QUICK_PALY:
			mQuickPlayView = LayoutInflater.from(context).inflate(
					BFResources.layout.bx_quick_play, null);
			mCurrentView.addView(mQuickPlayView, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.FILL_PARENT));
			break;
		case SHOW_FIND_PASSWORD:
			mFindPasswordView = LayoutInflater.from(context).inflate(
					BFResources.layout.bx_find_new_password, null);
			mCurrentView.addView(mFindPasswordView,
					new FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.FILL_PARENT,
							FrameLayout.LayoutParams.FILL_PARENT));
			break;
		case SHOW_ACCOUNT_REGISTER:
			mRegisterView = LayoutInflater.from(context).inflate(
					BFResources.layout.bx_account_register, null);
			mCurrentView.addView(mRegisterView, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.FILL_PARENT));
			break;
		case SHOW_RESET_PASSWORD:
			mResetPasswordView = BFResetPasswordView.getInstance(mContext)
					.getView();
			mCurrentView.removeView(mResetPasswordView);
			mCurrentView.addView(mResetPasswordView,
					new FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.FILL_PARENT,
							FrameLayout.LayoutParams.FILL_PARENT));
			BFResetPasswordView.getInstance(mContext).setHomePanel(this);
			break;
		case SHOW_BAOFENG_WEBVIEW:
			mBaoFengWebView = BFHomeWebView.getInstance(mContext).getView();
			mCurrentView.removeView(mBaoFengWebView);
			mCurrentView.addView(mBaoFengWebView, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.FILL_PARENT));
			BFHomeWebView.getInstance(mContext).setHomePanel(this);
			break;
		case SHOW_BIND_PHONE_AGAIN:
			mBindPhoneAgainView = BFBindPhoneAgainView.getInstance(mContext)
					.getView();
			mCurrentView.removeView(mBindPhoneAgainView);
			mCurrentView.addView(mBindPhoneAgainView,
					new FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.FILL_PARENT,
							FrameLayout.LayoutParams.FILL_PARENT));
			BFBindPhoneAgainView.getInstance(mContext).setHomePanel(this);
			break;
		case SHOW_BIND_PHONE:
			mBindPhoneView = BFBindPhoneView.getInstance(mContext).getView();
			mCurrentView.removeView(mBindPhoneView);
			mCurrentView.addView(mBindPhoneView, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.FILL_PARENT));
			BFBindPhoneView.getInstance(mContext).setHomePanel(this);
			break;
		case SHOW_ACCOUNT_INFO:
			mAccountInfoView = BFAccountInfoView.getInstance(mContext)
					.getView();
			if (mAccountInfoView.getParent() != null) {
				((FrameLayout) mAccountInfoView.getParent())
						.removeView(mAccountInfoView);
			}
			mCurrentView.removeView(mAccountInfoView);
			mCurrentView.addView(mAccountInfoView,
					new FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.FILL_PARENT,
							FrameLayout.LayoutParams.FILL_PARENT));
			BFAccountInfoView.getInstance(mContext).setHomePanel(this);
			break;
		case SHOW_CHANGE_PASSWORD:
			mCurrentView.removeView(mChangePasswordView);
			BFChangePasswordView.releaseViews();
			mChangePasswordView = null;
			mChangePasswordView = BFChangePasswordView.getInstance(mContext)
					.getView();
			mCurrentView.addView(mChangePasswordView,
					new FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.FILL_PARENT,
							FrameLayout.LayoutParams.FILL_PARENT));
			BFChangePasswordView.getInstance(mContext).setHomePanel(this);
			break;
		case SHOW_CHANGE_BIND_PHONE:
			mChangeBindPhoneView = BFChangeBindPhoneAgainView.getInstance(
					mContext).getView();
			mCurrentView.removeView(mChangeBindPhoneView);
			mCurrentView.addView(mChangeBindPhoneView,
					new FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.FILL_PARENT,
							FrameLayout.LayoutParams.FILL_PARENT));
			BFChangeBindPhoneAgainView.getInstance(mContext).setHomePanel(this);
			break;
		}
	}

	private void findLoginView() {
		mHomeAccount = (EditText) mLoginView
				.findViewById(BFResources.id.bx_home_account);
		mHomePassword = (EditText) mLoginView
				.findViewById(BFResources.id.bx_home_password);
		mHomeOtherRegister = (TextView) mLoginView
				.findViewById(BFResources.id.bx_home_other_register);
		mHomeFindPassword = (TextView) mLoginView
				.findViewById(BFResources.id.bx_home_find_password);
		mHomeLogin = (Button) mLoginView
				.findViewById(BFResources.id.bx_home_login);
		mHomeRegister = (Button) mLoginView
				.findViewById(BFResources.id.bx_home_register);
		mHomeSelectAccount = (ImageView) mLoginView
				.findViewById(BFResources.id.bx_home_select_account);

		ll_text = (LinearLayout) mLoginView
				.findViewById(BFResources.id.ll_text);
		// mHomeBackBtn = (Button) mLoginView
		// .findViewById(BFResources.id.bx_home_back);
		mHomeBaofengLoginBtn = (Button) mLoginView
				.findViewById(BFResources.id.bx_home_baofeng_login);

		mHomeOtherRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mHomeFindPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	}

	private void findQuickView() {
		mQuickAccount = (EditText) mQuickPlayView
				.findViewById(BFResources.id.bx_quick_random_account);
		mQuickPassword = (EditText) mQuickPlayView
				.findViewById(BFResources.id.bx_quick_random_password);
		mQuickSaveRandomAccount = (ImageButton) mQuickPlayView
				.findViewById(BFResources.id.bx_quick_save_random_account);
		mQuickStartPlay = (Button) mQuickPlayView
				.findViewById(BFResources.id.bx_quick_start_play);
		mQuickRegister = (Button) mQuickPlayView
				.findViewById(BFResources.id.bx_quick_register_account);
		mQuickSwitchAccount = (Button) mQuickPlayView
				.findViewById(BFResources.id.bx_quick_switch_account);
	}

	private void findRegisterView() {
		mRegisterAccount = (EditText) mRegisterView
				.findViewById(BFResources.id.bx_register_account);
		mRegisterPassword = (EditText) mRegisterView
				.findViewById(BFResources.id.bx_register_password);
		mRegisterNowRegister = (Button) mRegisterView
				.findViewById(BFResources.id.bx_register_now_register);
		mRegisterBack = (Button) mRegisterView
				.findViewById(BFResources.id.bx_back);
	}

	private void findFindPasswordView() {
		mFindPasswordBack = (Button) mFindPasswordView
				.findViewById(BFResources.id.bx_back);
		mFindPWDSelectAccount = (ImageView) mFindPasswordView
				.findViewById(BFResources.id.bx_home_select_account);
		mFindPasswordAccount = (EditText) mFindPasswordView
				.findViewById(BFResources.id.bx_find_psw_account);
		mFindPasswordType = (TextView) mFindPasswordView
				.findViewById(BFResources.id.bx_find_password_type);
		mFindPasswordSendButton = (Button) mFindPasswordView
				.findViewById(BFResources.id.bx_find_psw_next);
	}

	private void setSubListener(int type) {
		switch (type) {
		case SHOW_HOME_PANEL:
			setHomeListener();
			break;
		case SHOW_QUICK_PALY:
			setQuickLisener();
			break;
		case SHOW_FIND_PASSWORD:
			setFindListener();
			break;
		case SHOW_ACCOUNT_REGISTER:
			setRegisterListener();
			break;
		}
	}

	private void setHomeListener() {

		mHomeLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BFGameConfig.ACCOUNT = mHomeAccount.getText().toString().trim();
				BFGameConfig.PASSWORD = mHomePassword.getText().toString()
						.trim();
				BFGameConfig.USERTYPE = isBfUser();

				if (TextUtils.isEmpty(BFGameConfig.ACCOUNT)
						|| TextUtils.isEmpty(BFGameConfig.PASSWORD)) {
					showToast("请输入账户和密码");
					return;
				}

				if (BFGameConfig.ACCOUNT.length() < 4
						|| BFGameConfig.ACCOUNT.length() > 16) {
					showToast("请输入4-16位账户");
					return;
				}
				if (BFGameConfig.PASSWORD.length() < 6
						|| BFGameConfig.PASSWORD.length() > 20) {
					showToast("请输入6-20位密码");
					return;
				}
				if (BFGameConfig.ACCOUNT.indexOf(" ") != -1) {
					showToast("用户名不能有空格");
				}
				if (BFGameConfig.PASSWORD.indexOf(" ") != -1) {
					showToast("密码不能有空格");
				}

				// 是否初始化
				if (BFGameConfig.GAMEPARAM != null) {
					accountLogin(BFGameConfig.GAMEPARAM.getCpId(),
							BFGameConfig.ACCOUNT, BFGameConfig.PASSWORD,
							BFGameConfig.GAMEPARAM.getGameId(),
							BFGameConfig.GAMEPARAM.getServerId(),
							REQUEST_LOGIN_KEY, isBfUser());

				} else {

					mContext.loginResult(OperateType.NO_INIT,
							OperateType.NO_INIT);

				}
			}
		});
		mHomeRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showViewType(SHOW_ACCOUNT_REGISTER);
			}
		});
		mHomeSelectAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (loggedAccounts != null && loggedAccounts.size() > 0) {

					List<String> tokenList = new ArrayList<String>();
					for (int i = 0; i < loggedAccounts.size(); i++) {
						HashMap<String, String> account = loggedAccounts.get(i);
						if (StringUtil.isNotEmpty(account.get("token"))) {
							tokenList.add(account.get("token"));
						}
					}
					if ((loggedAccounts.size() - tokenList.size())==0) {
						showToast("没有已记录的登录账户");
						return;
					} else {
						showAccountSelectDialot();
					}
				} else {
					showToast("没有已记录的登录账户");
				}
			}

		});

		mHomeOtherRegister.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				showViewType(SHOW_BAOFENG_WEBVIEW);
				BFHomeWebView.getInstance(mContext).setPage(
						BFHomeWebView.BF_WEB_URL_REGISTER);
			}
		});

		mHomeFindPassword.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				showViewType(SHOW_FIND_PASSWORD);
			}
		});

		// mHomeBackBtn.setOnClickListener(new OnClickListener() {
		// public void onClick(View arg0) {
		// mContext.loginResult(OperateType.LOGIN,
		// BFSDKStatusCode.LOGIN_EXIT);
		// }
		// });

		mHomeBaofengLoginBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				showViewType(SHOW_BAOFENG_WEBVIEW);
				BFHomeWebView.getInstance(mContext).setPage(
						BFHomeWebView.BF_WEB_URL_LOGIN);
			}
		});
	}

	private void setQuickLisener() {
		mQuickStartPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BFGameConfig.ACCOUNT = stringRandomAccount;
				BFGameConfig.PASSWORD = stringRandomPassword;
				BFGameConfig.USERTYPE = BFGameConfig.userTypeBX;
				// 注册接口
				if (BFGameConfig.GAMEPARAM != null) {
					accountQuickRegister(BFGameConfig.GAMEPARAM.getCpId(),
							BFGameConfig.ACCOUNT, BFGameConfig.PASSWORD,
							BFGameConfig.GAMEPARAM.getGameId(),
							BFGameConfig.GAMEPARAM.getServerId(), 1, "");
				} else {
					showToast("Config.GAMEPARAM 为空，无法注册");
				}

			}
		});
		mQuickRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showViewType(SHOW_ACCOUNT_REGISTER);
			}
		});
		mQuickSwitchAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showViewType(SHOW_HOME_PANEL);
			}
		});
		mQuickSaveRandomAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StringBuffer msgText = new StringBuffer();
				msgText.append("欢迎进入暴风游戏。您的账号");
				msgText.append(mQuickAccount.getText().toString().trim());
				msgText.append("，密码为");
				msgText.append(mQuickPassword.getText().toString().trim());
				msgText.append("请使用账号或手机号码进行登录。祝您游戏愉快。");

				Uri smsToUri = Uri.parse("smsto:");// 联系人地址
				Intent mIntent = new Intent(
						android.content.Intent.ACTION_SENDTO, smsToUri);
				mIntent.putExtra("sms_body", msgText.toString());// 短信的内容
				mContext.startActivity(mIntent);
			}
		});
	}

	private void setRegisterListener() {

		mRegisterNowRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stringRegisterAccount = mRegisterAccount.getText().toString()
						.trim();
				stringRegisterPassword = mRegisterPassword.getText().toString()
						.trim();
				if (TextUtils.isEmpty(stringRegisterAccount)
						|| TextUtils.isEmpty(stringRegisterPassword)) {
					showToast("请输入注册的账号和密码");
					return;
				} else if (stringRegisterAccount.length() < 4
						|| stringRegisterAccount.length() > 16
						|| !isAllCharacter(stringRegisterAccount)) {
					showToast("请输入4-16位的账号");
					return;
				} else if (stringRegisterPassword.length() < 6
						|| stringRegisterPassword.length() > 20) {
					showToast("请输入6-20位的密码");
					return;
				} else if (stringRegisterAccount.indexOf(" ") != -1) {
					showToast("用户名不能有空格");
					return;
				} else if (stringRegisterPassword.indexOf(" ") != -1) {
					showToast("密码不能有空格");
					return;
				}
				// 注册接口
				if (BFGameConfig.GAMEPARAM != null) {

					accountRegister(BFGameConfig.GAMEPARAM.getCpId(),
							stringRegisterAccount, stringRegisterPassword,
							BFGameConfig.GAMEPARAM.getGameId(),
							BFGameConfig.GAMEPARAM.getServerId(), 1, "");
				} else {
					showToast("Config.GAMEPARAM 为空，无法注册");
				}
			}
		});

		mRegisterBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// if (!TextUtils.isEmpty(stringRegisterAccount)
				// && !TextUtils.isEmpty(stringRegisterPassword)) {
				// BFGameConfig.ACCOUNT = stringRegisterAccount;
				// BFGameConfig.PASSWORD = stringRegisterPassword;
				// // BXGameConfig.USERTYPE =
				// // mRegisterTypeBaofengCheck.isChecked() ? "1" : "0";
				// }
				showViewType(SHOW_HOME_PANEL);
			}
		});
	}

	private void setFindListener() {

		mFindPasswordBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showViewType(SHOW_HOME_PANEL);
				mFindPasswordType.setText("");
			}
		});
		mFindPWDSelectAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (loggedAccounts != null && loggedAccounts.size() > 0)
					showFindPasswordDialot();
				else {
					showToast("没有已记录的登录账户");
				}
			}
		});
		mFindPasswordSendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String account = mFindPasswordAccount.getText().toString();
				if (StringUtil.isEmpty(account)) {
					showToast("请输入账号");
					return;
				}
				bindCheckPhoneNumber(account);

			}
		});
	}

	private void setInitValue() {

		// 如果之前有用户登录过，把上次的用户account,password填入输入框中
		if (loggedAccounts != null && loggedAccounts.size() > 0) {
			HashMap<String, String> account = loggedAccounts.get(0);
			if (mHomeAccount != null && mHomePassword != null) {
				BFGameConfig.ACCOUNT = account.get("name");
				BFGameConfig.PASSWORD = account.get("password");
				BFGameConfig.USERTYPE = account.get("userType");
				BFGameConfig.TOKEN = account.get("token");
				if (StringUtil.isNotEmpty(account.get("user_id"))
						&& StringUtil.isNumber(account.get("user_id")))
					BFGameConfig.USER_ID = Integer.parseInt(account
							.get("user_id"));
				showViewType(SHOW_ACCOUNT_INFO);
				if (StringUtil.isNotEmpty(BFGameConfig.PASSWORD)
						&& StringUtil.isEmpty(BFGameConfig.TOKEN)) {
					mHomeAccount.setText(BFGameConfig.ACCOUNT);
					mHomePassword.setText(BFGameConfig.PASSWORD);
					mHomeAccount.setSelection(BFGameConfig.ACCOUNT.trim()
							.length());
					mHomePassword.setSelection(BFGameConfig.PASSWORD.trim()
							.length());
				}
			}
		}

	}

	private void setFindPasswordValue() {

		// 如果之前有用户登录过，把上次的用户account,password填入输入框中
		if (loggedAccounts != null && loggedAccounts.size() > 0) {
			HashMap<String, String> account = loggedAccounts.get(0);
			if (mFindPasswordAccount != null) {
				// Config.ACCOUNT = account.get("name");
				if (StringUtil.isNotEmpty(BFGameConfig.TOKEN)) {
					mFindPasswordAccount.setText("");
				} else {

					mFindPasswordAccount.setText(BFGameConfig.ACCOUNT);
					mFindPasswordAccount.setSelection(BFGameConfig.ACCOUNT
							.trim().length());
				}
			}
		}

	}

	@Override
	public void onCallBackFromThread(String resultJson, int resultCode) {
		// TODO Auto-generated method stub
		super.onCallBackFromThread(resultJson, resultCode);
		if (!TextUtils.isEmpty(resultJson)) {
			try {
				// System.out.println("===============resultJson================="
				// + resultJson);
				switch (resultCode) {
				case REQUEST_LOGIN_KEY: {
					HashMap<String, Object> loginResult = ParseUtil
							.getLoginUserData(resultJson);
					if (loginResult != null) {
						int code = (Integer) loginResult.get("code");
						if (code == BFSDKStatusCode.SUCCESS) {
							BFUser user = (BFUser) loginResult.get("data");
							BFGameConfig.isSMSOK_Online = (Integer) user
									.getSMS() == 1 ? true : false;
							if (user != null) {
								if (!BFGameConfig.isQQLogin) {
									if (user.getQuick_game() == QUICK_GAME) {
										// 强制用户修改密码
										showChangePasswordDialgo();
									} else {
										BFGameConfig.TICKET = user.getTicket();
										BFGameConfig.USER_ID = user
												.getUser_id();
										BFGameConfig.USER_NAME = BFGameConfig.ACCOUNT;
										rememberAccount();
										mContext.loginResult(OperateType.LOGIN,
												code);
									}
								} else {
									BFGameConfig.TICKET = user.getTicket();
									BFGameConfig.USER_ID = user.getUser_id();
									BFGameConfig.USER_NAME = BFGameConfig.ACCOUNT;
									// rememberAccount();
									mContext.loginResult(OperateType.LOGIN,
											code);
								}

							}
						} else {
							mContext.loginResult(OperateType.LOGIN, code);
						}

					} else {
						mContext.loginResult(OperateType.LOGIN,
								BFSDKStatusCode.SYSTEM_ERROR);
					}
				}
					break;
				case REQUEST_REGISTER_KEY: {
					HashMap<String, Object> registerResult = ParseUtil
							.getLoginUserData(resultJson);
					if (registerResult != null) {
						int code = (Integer) registerResult.get("code");

						if (code == BFSDKStatusCode.SUCCESS) {
							BFUser user = (BFUser) registerResult.get("data");
							if (user != null) {
								showToast("注册成功");

								BFGameConfig.TICKET = user.getTicket();
								BFGameConfig.USER_ID = user.getUser_id();
								BFGameConfig.ACCOUNT = stringRegisterAccount;
								BFGameConfig.PASSWORD = stringRegisterPassword;
								BFGameConfig.TOKEN = "";
								rememberAccount();
								// 隐藏软键盘
								InputMethodManager imm = (InputMethodManager) mContext
										.getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(0,
										InputMethodManager.HIDE_NOT_ALWAYS);
								showViewType(SHOW_BIND_PHONE_AGAIN);
							}
						} else {
							showToast(code);
						}
					} else {

						showToast("注册失败 ");
					}

				}
					break;
				case REQUEST_PHONENUMBER_BIND_CHECK_KEY: {
					resultJson = resultJson.replace("\"data\":[]",
							"\"data\":{}");
					JSONObject resultObject = new JSONObject(resultJson);
					int code = 0;
					if (!resultObject.isNull("code")) {
						code = resultObject.getInt("code");
					}
					if (!resultObject.isNull("data")) {
						JSONObject data = resultObject.getJSONObject("data");
						if (!data.isNull("phone_number"))
							phone_Number = data.getString("phone_number");
					}
					if (code == BFSDKStatusCode.SUCCESS) {
						showViewType(SHOW_RESET_PASSWORD);
					} else {
						showToast("未绑定手机，请联系客服");
						if (StringUtil.isNotEmpty(BFGameConfig.QQ_QA)
								&& StringUtil.isNotEmpty(BFGameConfig.TEL_QA)) {
							mFindPasswordType.setText("未绑定手机号，请联系客服找回密码"
									+ "\n客服QQ:" + BFGameConfig.QQ_QA
									+ "\n客服电话：" + BFGameConfig.TEL_QA);
						} else if (StringUtil.isNotEmpty(BFGameConfig.QQ_QA)
								&& StringUtil.isEmpty(BFGameConfig.TEL_QA)) {
							mFindPasswordType.setText("未绑定手机号，请联系客服找回密码"
									+ "\n客服QQ:" + BFGameConfig.QQ_QA);
						} else if (StringUtil.isEmpty(BFGameConfig.QQ_QA)
								&& StringUtil.isNotEmpty(BFGameConfig.TEL_QA)) {
							mFindPasswordType.setText("未绑定手机号，请联系客服找回密码"
									+ "\n客服电话：" + BFGameConfig.TEL_QA);
						} else {
							mFindPasswordType.setText("未绑定手机号，请联系客服找回密码");
						}
					}

				}
					break;
				case REQUEST_RESET_PASSWORD_KEY: {
					JSONObject resultObject = new JSONObject(resultJson);
					int code = 0;
					if (!resultObject.isNull("code")) {
						code = resultObject.getInt("code");
					}
					if (code == BFSDKStatusCode.SUCCESS) {
						// 静态变量中设置当前密码 //FIXME
						BFGameConfig.PASSWORD = stringResetPassword;
						// 将新的密码写入账户文件中
						changeLocationAccountFile();
						// 是否初始化
						if (BFGameConfig.GAMEPARAM != null) {
							accountLogin(BFGameConfig.GAMEPARAM.getCpId(),
									BFGameConfig.ACCOUNT,
									BFGameConfig.PASSWORD,
									BFGameConfig.GAMEPARAM.getGameId(),
									BFGameConfig.GAMEPARAM.getServerId(),
									REQUEST_LOGIN_KEY, isBfUser());
						} else {

							mContext.loginResult(OperateType.NO_INIT,
									OperateType.NO_INIT);

						}

						// showViewType(SHOW_HOME_PANEL);

					} else {
						showToast(code);
					}
				}
					break;
				case REQUEST_QUICK_PLAY_REGISTER_KEY: {
					HashMap<String, Object> registerResult = ParseUtil
							.getLoginUserData(resultJson);
					if (registerResult != null) {
						BFUser user = (BFUser) registerResult.get("data");
						BFGameConfig.isSMSOK_Online = (Integer) user.getSMS() == 1 ? true
								: false;
						int code = (Integer) registerResult.get("code");

						if (code == BFSDKStatusCode.SUCCESS) {
							quickRegisterLogin(registerResult);
						} else if (code == BFSDKStatusCode.FAIL) {
							showToast("注册失败");
						} else if (code == BFSDKStatusCode.USER_EXIST) {
							// 已经用这个账号注册了，则直接登录
							quickRegisterLogin(registerResult);
						} else {
							showToast(code);
						}

					}
				}
					break;
				case REQUEST_QUICK_PLAY_LOGIN_KEY: {
					HashMap<String, Object> loginResult = ParseUtil
							.getLoginUserData(resultJson);
					if (loginResult != null) {
						int code = (Integer) loginResult.get("code");

						if (code == BFSDKStatusCode.SUCCESS) {
							BFUser user = (BFUser) loginResult.get("data");
							if (user != null) {
								BFGameConfig.TICKET = user.getTicket();
								BFGameConfig.USER_ID = user.getUser_id();
								BFGameConfig.USER_NAME = BFGameConfig.ACCOUNT;
								BFGameConfig.USERTYPE = BFGameConfig.userTypeBX;

								addAccount();
							}
						}
						mContext.loginResult(OperateType.QUICK_REGISTER, code);
					} else {
						mContext.loginResult(OperateType.QUICK_REGISTER,
								BFSDKStatusCode.SYSTEM_ERROR);
					}
				}
					break;
				case REQUEST_RANDOM_ACCOUNT_CHANGE_PASSWORD_KEY: {

					JSONObject resultObject = new JSONObject(resultJson);
					int code = 0;
					if (!resultObject.isNull("code")) {
						code = resultObject.getInt("code");
					}
					if (code == BFSDKStatusCode.SUCCESS) {
						showToast("修改密码成功");
						if (mForceChangePasswordDialog != null)
							mForceChangePasswordDialog.dismiss();
						// 静态变量中设置当前密码
						BFGameConfig.PASSWORD = stringForceResetPassword;
						// 将新的密码写入账户文件中
						changeLocationAccountFile();

						mHomePassword.setText(stringForceResetPassword);
					} else {
						showToast(code);
					}

				}
					break;
				case REQUEST_GET_GAME_VERSION:
					HashMap<String, Object> gameVersionResult = ParseUtil
							.getGameVersionData(resultJson);

					if (gameVersionResult != null) {
						int code = (Integer) gameVersionResult.get("code");

						if (code == BFSDKStatusCode.SUCCESS) {
							String downloadUrl = (String) gameVersionResult
									.get("downloadurl");
							String version = (String) gameVersionResult
									.get("version");
						}

					}
					break;
				case REQUEST_QQ_REGISTER_KEY: {
					HashMap<String, Object> registerResult = ParseUtil
							.getLoginUserData(resultJson);
					if (registerResult != null) {
						int code = (Integer) registerResult.get("code");
						if (code == BFSDKStatusCode.SUCCESS) {
							BFUser user = (BFUser) registerResult.get("data");
							if (user != null) {

								BFGameConfig.ACCOUNT = stringRegisterAccount;
								BFGameConfig.PASSWORD = stringRegisterPassword;

								showToast("注册成功");
								showViewType(SHOW_HOME_PANEL);

							}
						} else {
							// showToast(code);
						}
						// 是否初始化
						if (BFGameConfig.GAMEPARAM != null) {
							accountLogin(BFGameConfig.GAMEPARAM.getCpId(),
									stringRegisterAccount,
									stringRegisterPassword,
									BFGameConfig.GAMEPARAM.getGameId(),
									BFGameConfig.GAMEPARAM.getServerId(),
									REQUEST_LOGIN_KEY, isBfUser());
						} else {

							mContext.loginResult(OperateType.NO_INIT,
									OperateType.NO_INIT);

						}

					} else {

						showToast("注册失败 ");
					}

					break;
				}
				case REQUEST_SEND_PHONENUMBER_CODE:
					JSONObject resultObject = new JSONObject(resultJson);
					int code = 0;
					if (!resultObject.isNull("code")) {
						code = resultObject.getInt("code");
					}
					if (code == BFSDKStatusCode.SUCCESS) {
						JSONObject data = resultObject.optJSONObject("data");
						yanZhengMa = data.optString("validatecode");
						// mFindPasswrdYanZhengMa.setText(yanZhengMa);
					}
					break;
				case REQUEST_CHECK_PHONENUMBER_CODE:
					JSONObject checkObject = new JSONObject(resultJson);
					int check_code = 0;
					if (!checkObject.isNull("code")) {
						check_code = checkObject.getInt("code");
					}
					if (check_code == BFSDKStatusCode.SUCCESS) {
						// showToast("验明正身");
						showViewType(SHOW_RESET_PASSWORD);
					} else {
						showToast("验证码输入错误，请重新输入");
					}
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 随机生成的账户，修改密码
	 * 
	 * @param account
	 *            冰雪SDK平台账号
	 * @param cp_id
	 *            运营商id
	 * @param game_id
	 *            游戏id
	 * @param server_id
	 *            服务器id
	 * @param newPassword
	 *            用户新密码
	 */
	protected void randomAccountChangePassword(String account, int cp_id,
			int game_id, int server_id, String newPassword) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("account", account));
		parameter.add(new RequestParameter("cp_id", String.valueOf(cp_id)));
		parameter.add(new RequestParameter("game_id", String.valueOf(game_id)));
		parameter.add(new RequestParameter("server_id", String
				.valueOf(server_id)));
		parameter.add(new RequestParameter("password", newPassword));

		startHttpRequst(BFGameConfig.HTTP_POST,
				BFGameConfig.BX_QUICK_RESET_PASSWORD, parameter, true, "",
				false, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT,
				REQUEST_RANDOM_ACCOUNT_CHANGE_PASSWORD_KEY);
	}

	private void quickRegisterLogin(HashMap<String, Object> registerResult) {
		if (registerResult == null)
			return;
		BFGameConfig.ACCOUNT = stringRandomAccount;
		BFGameConfig.PASSWORD = stringRandomPassword;
		BFGameConfig.USERTYPE = isBfUser();
		// 是否初始化
		if (BFGameConfig.GAMEPARAM != null) {
			accountLogin(BFGameConfig.GAMEPARAM.getCpId(),
					BFGameConfig.ACCOUNT, BFGameConfig.PASSWORD,
					BFGameConfig.GAMEPARAM.getGameId(),
					BFGameConfig.GAMEPARAM.getServerId(),
					REQUEST_QUICK_PLAY_LOGIN_KEY, BFGameConfig.USERTYPE);
		} else {

		}

		rememberAccount();

	}

	/**
	 * 因为不改动isRememdAccount的值 所以写一个为快速游戏成功后单独调用保存账户的方法
	 */
	private void addAccount() {
		HashMap<String, String> account = new HashMap<String, String>();
		account.put("name", BFGameConfig.ACCOUNT);
		account.put("password", BFGameConfig.PASSWORD);
		account.put("userType", BFGameConfig.USERTYPE);
		account.put("token", BFGameConfig.TOKEN);
		if (loggedAccounts == null) {
			loggedAccounts = new ArrayList<HashMap<String, String>>();
		}
		boolean isExist = false;
		HashMap<String, String> existAccount = null;
		for (int i = 0; i < loggedAccounts.size(); i++) {
			existAccount = loggedAccounts.get(i);
			if (BFGameConfig.ACCOUNT.equals(existAccount.get("name"))) {
				// 如果之前已经保存过这个账户，则替换
				existAccount.put("name", BFGameConfig.ACCOUNT);
				existAccount.put("password", BFGameConfig.PASSWORD);
				existAccount.put("userType", BFGameConfig.USERTYPE);
				existAccount.put("token", BFGameConfig.TOKEN);
				isExist = true;
				break;
			}
		}
		if (!isExist) {
			// 之前没有保存过这个账户，添加进去
			loggedAccounts.add(0, account);
		} else {
			loggedAccounts.remove(existAccount);
			loggedAccounts.add(0, existAccount);
		}
		AccountPersistenceUtil.writerAccountToFile(accountFileSavePath
				+ File.separator + accountFileName, loggedAccounts);
	}

	public void rememberAccount() {
		// 记住登录状态
		if (isRememdAccount) {
			HashMap<String, String> account = new HashMap<String, String>();
			account.put("name", BFGameConfig.ACCOUNT);
			account.put("password", BFGameConfig.PASSWORD);
			account.put("token", BFGameConfig.TOKEN);
			account.put("user_id", String.valueOf(BFGameConfig.USER_ID));
			if (loggedAccounts == null) {
				loggedAccounts = new ArrayList<HashMap<String, String>>();
			}
			boolean isExist = false;
			HashMap<String, String> existAccount = null;
			for (int i = 0; i < loggedAccounts.size(); i++) {
				existAccount = loggedAccounts.get(i);
				if (BFGameConfig.ACCOUNT.equals(existAccount.get("name"))) {
					// 如果之前已经保存过这个账户，则替换
					existAccount.put("name", BFGameConfig.ACCOUNT);
					existAccount.put("password", BFGameConfig.PASSWORD);
					existAccount.put("token", BFGameConfig.TOKEN);
					existAccount.put("user_id",
							String.valueOf(BFGameConfig.USER_ID));
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				// 之前没有保存过这个账户，添加进去
				loggedAccounts.add(0, account);
			} else {
				loggedAccounts.remove(existAccount);
				loggedAccounts.add(0, existAccount);
			}
			AccountPersistenceUtil.writerAccountToFile(accountFileSavePath
					+ File.separator + accountFileName, loggedAccounts);
		} else {
			if (loggedAccounts == null) {
				return;
			}
			// 如果是从列表中选择的之前账号
			HashMap<String, String> account = new HashMap<String, String>();
			account.put("name", BFGameConfig.ACCOUNT);
			account.put("password", BFGameConfig.PASSWORD);
			account.put("token", BFGameConfig.TOKEN);
			account.put("user_id", String.valueOf(BFGameConfig.USER_ID));
			boolean isExist = false;
			HashMap<String, String> existAccount = null;
			for (int i = 0; i < loggedAccounts.size(); i++) {
				existAccount = loggedAccounts.get(i);
				if (BFGameConfig.ACCOUNT.equals(existAccount.get("name"))) {
					// 如果之前已经保存过这个账户，则替换
					existAccount.put("name", BFGameConfig.ACCOUNT);
					existAccount.put("password", BFGameConfig.PASSWORD);
					existAccount.put("token", BFGameConfig.TOKEN);
					existAccount.put("user_id",
							String.valueOf(BFGameConfig.USER_ID));
					isExist = true;
					break;
				}
			}
			if (isExist) {
				loggedAccounts.remove(existAccount);
				loggedAccounts.add(0, existAccount);
			}
			AccountPersistenceUtil.writerAccountToFile(accountFileSavePath
					+ File.separator + accountFileName, loggedAccounts);
		}

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
	 * 用户QQ登陆注册冰雪游戏 账号
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
	 * @param adult
	 *            是否成年 1为成年 0为未成年
	 * @param phone_number
	 *            用户手机号(可以为空)
	 * @param phone_imei
	 *            用户手机IMEI号(可以为空)
	 */

	private void accountQQRegister(int cp_id, String account, String password,
			int game_id, int server_id, int adult, String phone_number,
			String phone_imei) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("account", account));
		parameter.add(new RequestParameter("password", password));
		parameter.add(new RequestParameter("phone_number", phone_number));
		parameter.add(new RequestParameter("phone_imei", phone_imei));
		parameter.add(new RequestParameter("adult", String.valueOf(adult)));
		parameter.add(new RequestParameter("cp_id", String.valueOf(cp_id)));
		parameter.add(new RequestParameter("game_id", String.valueOf(game_id)));
		parameter.add(new RequestParameter("server_id", String
				.valueOf(server_id)));
		parameter.add(new RequestParameter("quick_game", String
				.valueOf(COMMON_GAME)));

		parameter.add(new RequestParameter("package_name",
				BFGameConfig.PACKAGE_NAME));

		parameter.add(new RequestParameter("game_version", String
				.valueOf(BFGameConfig.VERSION_CODE)));

		startHttpRequst(BFGameConfig.HTTP_POST,
				BFGameConfig.BX_SERVICE_URL_ACCOUNT_REGISTER, parameter, true,
				"", false, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, REQUEST_QQ_REGISTER_KEY);// FIXME
																			// 注册后直接登录
	}

	/**
	 * 用户注册冰雪游戏 账号
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
	 * @param adult
	 *            是否成年 1为成年 0为未成年
	 * @param phone_number
	 *            用户手机号(可以为空)
	 */
	private void accountRegister(int cp_id, String account, String password,
			int game_id, int server_id, int adult, String phone_number) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("account", account));
		parameter.add(new RequestParameter("password", password));
		parameter.add(new RequestParameter("phone_number", phone_number));
		parameter.add(new RequestParameter("adult", String.valueOf(adult)));
		parameter.add(new RequestParameter("cp_id", String.valueOf(cp_id)));
		parameter.add(new RequestParameter("game_id", String.valueOf(game_id)));
		parameter.add(new RequestParameter("server_id", String
				.valueOf(server_id)));
		parameter.add(new RequestParameter("quick_game", String
				.valueOf(COMMON_GAME)));

		parameter.add(new RequestParameter("package_name",
				BFGameConfig.PACKAGE_NAME));

		parameter.add(new RequestParameter("game_version", String
				.valueOf(BFGameConfig.VERSION_CODE)));

		startHttpRequst(BFGameConfig.HTTP_POST,
				BFGameConfig.BX_SERVICE_URL_ACCOUNT_REGISTER, parameter, true,
				"", false, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, REQUEST_REGISTER_KEY);// FIXME
		// 注册后直接登录
	}

	/**
	 * 参数同注册参数
	 */
	private void accountQuickRegister(int cp_id, String account,
			String password, int game_id, int server_id, int adult,
			String phone_number) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("account", account));
		parameter.add(new RequestParameter("password", password));
		parameter.add(new RequestParameter("phone_number", phone_number));
		parameter.add(new RequestParameter("adult", String.valueOf(adult)));
		parameter.add(new RequestParameter("cp_id", String.valueOf(cp_id)));
		parameter.add(new RequestParameter("game_id", String.valueOf(game_id)));
		parameter.add(new RequestParameter("server_id", String
				.valueOf(server_id)));
		parameter.add(new RequestParameter("quick_game", String
				.valueOf(QUICK_GAME)));
		parameter.add(new RequestParameter("package_name",
				BFGameConfig.PACKAGE_NAME));

		parameter.add(new RequestParameter("game_version", String
				.valueOf(BFGameConfig.VERSION_CODE)));
		startHttpRequst(BFGameConfig.HTTP_POST,
				BFGameConfig.BX_SERVICE_URL_ACCOUNT_REGISTER, parameter, true,
				"", false, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT,
				REQUEST_QUICK_PLAY_REGISTER_KEY);
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
				true, "", true, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT,
				REQUEST_PHONENUMBER_BIND_CHECK_KEY);
	}

	public void showViewType(int type) {
		switch (type) {
		case SHOW_HOME_PANEL:
			if (mLoginView == null)
				initSubView(SHOW_HOME_PANEL);
			if (mLoginView != null)
				mLoginView.setVisibility(View.VISIBLE);
			if (mQuickPlayView != null)
				mQuickPlayView.setVisibility(View.INVISIBLE);
			if (mRegisterView != null)
				mRegisterView.setVisibility(View.INVISIBLE);
			if (mFindPasswordView != null)
				mFindPasswordView.setVisibility(View.INVISIBLE);
			if (mResetPasswordView != null)
				mResetPasswordView.setVisibility(View.INVISIBLE);
			BFHomeWebView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFAccountInfoView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangePasswordView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangeBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);

			if (mHomeAccount != null && mHomePassword != null) {
				if (StringUtil.isEmpty(BFGameConfig.TOKEN)) {
					mHomeAccount.setText(BFGameConfig.ACCOUNT);
					mHomePassword.setText(BFGameConfig.PASSWORD);
					mHomeAccount.setSelection(BFGameConfig.ACCOUNT.length());
					mHomePassword.setSelection(BFGameConfig.PASSWORD.length());
				}
			}
			break;
		case SHOW_QUICK_PALY:
			if (mQuickPlayView == null)
				initSubView(SHOW_QUICK_PALY);
			if (mLoginView != null)
				mLoginView.setVisibility(View.INVISIBLE);
			if (mQuickPlayView != null)
				mQuickPlayView.setVisibility(View.VISIBLE);
			if (mRegisterView != null)
				mRegisterView.setVisibility(View.INVISIBLE);
			if (mFindPasswordView != null)
				mFindPasswordView.setVisibility(View.INVISIBLE);
			if (mResetPasswordView != null)
				mResetPasswordView.setVisibility(View.INVISIBLE);
			BFHomeWebView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFAccountInfoView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangePasswordView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangeBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			HashMap<String, String> randomAccount = generateRandomAccount();
			if (mQuickAccount != null && mQuickPassword != null) {
				mQuickAccount.setText(randomAccount.get("name"));
				mQuickPassword.setText(randomAccount.get("password"));
				mQuickAccount.setSelection(mQuickAccount.getText().toString()
						.trim().length());
				mQuickPassword.setSelection(mQuickPassword.getText().toString()
						.trim().length());
			}
			break;
		case SHOW_ACCOUNT_REGISTER:
			if (mRegisterView == null)
				initSubView(SHOW_ACCOUNT_REGISTER);
			if (mLoginView != null)
				mLoginView.setVisibility(View.INVISIBLE);
			if (mQuickPlayView != null)
				mQuickPlayView.setVisibility(View.INVISIBLE);
			if (mRegisterView != null)
				mRegisterView.setVisibility(View.VISIBLE);
			if (mFindPasswordView != null)
				mFindPasswordView.setVisibility(View.INVISIBLE);
			if (mResetPasswordView != null)
				mResetPasswordView.setVisibility(View.INVISIBLE);
			BFHomeWebView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFAccountInfoView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangePasswordView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangeBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			stringRegisterAccount = generateRandomAccount().get("name");
			stringRegisterPassword = "";
			if (mRegisterAccount != null && mRegisterPassword != null) {
				mRegisterAccount.setText(stringRegisterAccount);
				mRegisterPassword.setText(stringRegisterPassword);
			}
			break;
		case SHOW_FIND_PASSWORD:
			if (mFindPasswordView == null)
				initSubView(SHOW_FIND_PASSWORD);
			if (mLoginView != null)
				mLoginView.setVisibility(View.INVISIBLE);
			if (mQuickPlayView != null)
				mQuickPlayView.setVisibility(View.INVISIBLE);
			if (mRegisterView != null)
				mRegisterView.setVisibility(View.INVISIBLE);
			if (mFindPasswordView != null)
				mFindPasswordView.setVisibility(View.VISIBLE);
			if (mResetPasswordView != null)
				mResetPasswordView.setVisibility(View.INVISIBLE);
			BFHomeWebView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFAccountInfoView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangePasswordView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangeBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			stringFindPasswordAccount = "";
			if (mFindPasswordAccount != null) {
				mFindPasswordAccount.setText(BFGameConfig.ACCOUNT);
				mFindPasswordAccount
						.setSelection(BFGameConfig.ACCOUNT.length());
			}
			initAccountData();
			setFindPasswordValue();
			// bindCheckPhoneNumber(BXGameConfig.ACCOUNT);
			// mFindPasswordInput.setText(stringFindPasswordAccount);
			break;
		case SHOW_RESET_PASSWORD:
			if (mResetPasswordView == null)
				initSubView(SHOW_RESET_PASSWORD);
			if (mLoginView != null)
				mLoginView.setVisibility(View.INVISIBLE);
			if (mQuickPlayView != null)
				mQuickPlayView.setVisibility(View.INVISIBLE);
			if (mRegisterView != null)
				mRegisterView.setVisibility(View.INVISIBLE);
			if (mFindPasswordView != null)
				mFindPasswordView.setVisibility(View.INVISIBLE);
			if (mResetPasswordView != null)
				mResetPasswordView.setVisibility(View.VISIBLE);
			BFHomeWebView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFAccountInfoView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangePasswordView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangeBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			break;
		case SHOW_BAOFENG_WEBVIEW:
			if (mResetPasswordView == null)
				initSubView(SHOW_BAOFENG_WEBVIEW);
			if (mLoginView != null)
				mLoginView.setVisibility(View.INVISIBLE);
			if (mQuickPlayView != null)
				mQuickPlayView.setVisibility(View.INVISIBLE);
			if (mRegisterView != null)
				mRegisterView.setVisibility(View.INVISIBLE);
			if (mFindPasswordView != null)
				mFindPasswordView.setVisibility(View.INVISIBLE);
			if (mResetPasswordView != null)
				mResetPasswordView.setVisibility(View.INVISIBLE);
			BFHomeWebView.getInstance(mContext).getView()
					.setVisibility(View.VISIBLE);
			BFAccountInfoView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangePasswordView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangeBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			break;
		case SHOW_BIND_PHONE_AGAIN:
			if (mResetPasswordView == null)
				initSubView(SHOW_BIND_PHONE_AGAIN);
			if (mLoginView != null)
				mLoginView.setVisibility(View.INVISIBLE);
			if (mQuickPlayView != null)
				mQuickPlayView.setVisibility(View.INVISIBLE);
			if (mRegisterView != null)
				mRegisterView.setVisibility(View.INVISIBLE);
			if (mFindPasswordView != null)
				mFindPasswordView.setVisibility(View.INVISIBLE);
			if (mResetPasswordView != null)
				mResetPasswordView.setVisibility(View.INVISIBLE);
			BFHomeWebView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFAccountInfoView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.VISIBLE);
			BFChangePasswordView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangeBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			break;
		case SHOW_BIND_PHONE:
			if (mResetPasswordView == null)
				initSubView(SHOW_BIND_PHONE);
			if (mLoginView != null)
				mLoginView.setVisibility(View.INVISIBLE);
			if (mQuickPlayView != null)
				mQuickPlayView.setVisibility(View.INVISIBLE);
			if (mRegisterView != null)
				mRegisterView.setVisibility(View.INVISIBLE);
			if (mFindPasswordView != null)
				mFindPasswordView.setVisibility(View.INVISIBLE);
			if (mResetPasswordView != null)
				mResetPasswordView.setVisibility(View.INVISIBLE);
			BFHomeWebView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFAccountInfoView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneView.getInstance(mContext).getView()
					.setVisibility(View.VISIBLE);
			BFBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangePasswordView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangeBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneView.getInstance(mContext).initValue();
			break;
		case SHOW_ACCOUNT_INFO:
			if (mResetPasswordView == null)
				initSubView(SHOW_ACCOUNT_INFO);
			if (mLoginView != null)
				mLoginView.setVisibility(View.INVISIBLE);
			if (mQuickPlayView != null)
				mQuickPlayView.setVisibility(View.INVISIBLE);
			if (mRegisterView != null)
				mRegisterView.setVisibility(View.INVISIBLE);
			if (mFindPasswordView != null)
				mFindPasswordView.setVisibility(View.INVISIBLE);
			if (mResetPasswordView != null)
				mResetPasswordView.setVisibility(View.INVISIBLE);
			BFHomeWebView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFAccountInfoView.getInstance(mContext).getView()
					.setVisibility(View.VISIBLE);
			BFBindPhoneView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangePasswordView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangeBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			// BFAccountInfoView.getInstance(mContext).initValue();
			break;
		case SHOW_CHANGE_PASSWORD:
			if (mResetPasswordView == null)
				initSubView(SHOW_CHANGE_PASSWORD);
			if (mLoginView != null)
				mLoginView.setVisibility(View.INVISIBLE);
			if (mQuickPlayView != null)
				mQuickPlayView.setVisibility(View.INVISIBLE);
			if (mRegisterView != null)
				mRegisterView.setVisibility(View.INVISIBLE);
			if (mFindPasswordView != null)
				mFindPasswordView.setVisibility(View.INVISIBLE);
			if (mResetPasswordView != null)
				mResetPasswordView.setVisibility(View.INVISIBLE);
			BFHomeWebView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFAccountInfoView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangePasswordView.getInstance(mContext).getView()
					.setVisibility(View.VISIBLE);
			BFChangeBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			break;
		case SHOW_CHANGE_BIND_PHONE:
			if (mChangeBindPhoneView == null)
				initSubView(SHOW_CHANGE_BIND_PHONE);
			if (mLoginView != null)
				mLoginView.setVisibility(View.INVISIBLE);
			if (mQuickPlayView != null)
				mQuickPlayView.setVisibility(View.INVISIBLE);
			if (mRegisterView != null)
				mRegisterView.setVisibility(View.INVISIBLE);
			if (mFindPasswordView != null)
				mFindPasswordView.setVisibility(View.INVISIBLE);
			if (mResetPasswordView != null)
				mResetPasswordView.setVisibility(View.INVISIBLE);
			BFHomeWebView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFAccountInfoView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangePasswordView.getInstance(mContext).getView()
					.setVisibility(View.INVISIBLE);
			BFChangeBindPhoneAgainView.getInstance(mContext).getView()
					.setVisibility(View.VISIBLE);
			BFChangeBindPhoneAgainView.getInstance(mContext).initValue();
			break;
		}
	}

	/**
	 * 生成随机账号
	 * 
	 * @return
	 */
	private HashMap<String, String> generateRandomAccount() {
		if (TextUtils.isEmpty(stringRandomAccount)
				|| TextUtils.isEmpty(stringRandomPassword)) {
			DecimalFormat format = new DecimalFormat("######");
			format.setMinimumIntegerDigits(4);
			format.setMaximumIntegerDigits(4);
			Random random = new Random();
			String timestamp = String.valueOf(System.currentTimeMillis());
			stringRandomAccount = timestamp.substring(timestamp.length() - 8,
					timestamp.length() - 4)
					+ format.format(random.nextInt(10000));
			stringRandomPassword = timestamp.substring(timestamp.length() - 8,
					timestamp.length() - 4)
					+ format.format(random.nextInt(10000));
		}

		HashMap<String, String> account = new HashMap<String, String>();
		account.put("name", stringRandomAccount);
		account.put("password", stringRandomPassword);
		return account;
	}

	private ListView listView;
	private TextView textView;
	private PopupWindow popupWindow;

	/**
	 * 显示选择登录过的账户对话框
	 */
	private void showAccountSelectDialot() {
		final List<String> tokenList = new ArrayList<String>();
		for (int i = 0; i < loggedAccounts.size(); i++) {
			HashMap<String, String> account = loggedAccounts.get(i);
			if (StringUtil.isNotEmpty(account.get("token"))) {
				tokenList.add(account.get("token"));
			}
		}

		final String[] names = new String[loggedAccounts.size()
				- tokenList.size()];
		final String[] passwords = new String[loggedAccounts.size()
				- tokenList.size()];
		final String[] userTypes = new String[loggedAccounts.size()
				- tokenList.size()];
		final String[] tokens = new String[loggedAccounts.size()
				- tokenList.size()];
		for (int i = 0, j = 0; i < loggedAccounts.size(); i++) {
			HashMap<String, String> account = loggedAccounts.get(i);
			if (StringUtil.isEmpty(account.get("token"))) {
				names[j] = account.get("name");
				passwords[j] = account.get("password");
				userTypes[j] = account.get("userType");
				tokens[j] = account.get("token");
				j++;
			}
		}

		// 准备listView
		listView = new ListView(mContext);

		// 给listView设置背景
		listView.setBackgroundResource(BFResources.drawable.bx_rect_bottom_pressed);
		listView.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				textView = new TextView(mContext);
				textView.setGravity(Gravity.CENTER);
				textView.setTextColor(Color.BLACK);
				textView.setHeight(DeviceUtil.dip2px(mContext, 30));
				// 对内容进行设置
				textView.setText(names[position]);
				return textView;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return names.length;
			}
		});
		listView.setDividerHeight(0);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				BFGameConfig.ACCOUNT = names[position];
				BFGameConfig.PASSWORD = passwords[position];
				BFGameConfig.USERTYPE = userTypes[position];
				BFGameConfig.TOKEN = tokens[position];
				mHomeAccount.setText(BFGameConfig.ACCOUNT);
				mHomePassword.setText(BFGameConfig.PASSWORD);
				popupWindow.dismiss();

			}
		});

		// 本例中只有downArrow图片有点击事件，
		if (popupWindow == null) {
			popupWindow = new PopupWindow(mContext);
			// 设置弹出窗体的大小
			popupWindow.setWidth(ll_text.getWidth());
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			// 设置弹出窗体的内容
			popupWindow.setContentView(listView);
			// 设置popupwindow以外的区域是否能触摸，设为true,会自动收起popupWindow
			popupWindow.setOutsideTouchable(true);

			// popupWindow默认为不接收焦点
			popupWindow.setFocusable(true);
			popupWindow.setAnimationStyle(BFResources.style.animationPreview);

		}
		popupWindow.showAsDropDown(ll_text, 0, 0);

		// // 对话框
		// Builder builder = new Builder(mContext);
		// builder.setTitle("选择已登录的账户");
		// builder.setSingleChoiceItems(names, 0,
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// BFGameConfig.ACCOUNT = names[which];
		// BFGameConfig.PASSWORD = passwords[which];
		// BFGameConfig.USERTYPE = userTypes[which];
		// BFGameConfig.TOKEN = tokens[which];
		// // homeAccount.setText(Config.ACCOUNT);
		// // homePassword.setText(Config.PASSWORD);
		// }
		//
		// });
		//
		// // 添加一个确定按钮
		// builder.setPositiveButton(" 确 定 ",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// // Config.ACCOUNT = names[which];
		// // Config.PASSWORD = passwords[which];
		// mHomeAccount.setText(BFGameConfig.ACCOUNT);
		// mHomePassword.setText(BFGameConfig.PASSWORD);
		//
		// }
		// });
		// builder.create().show();
		// // 显示对话框是，默认选中第一项
		// Config.ACCOUNT = names[0];
		// Config.PASSWORD = passwords[0];
	}

	/**
	 * 显示找回密码处的dialog
	 */
	private void showFindPasswordDialot() {
		final String[] names = new String[loggedAccounts.size()];
		// final String[] passwords = new String[loggedAccounts.size()];
		for (int i = 0; i < loggedAccounts.size(); i++) {
			HashMap<String, String> account = loggedAccounts.get(i);
			names[i] = account.get("name");
			// passwords[i] = account.get("password");
		}

		// 对话框
		Builder builder = new Builder(mContext);
		builder.setTitle("选择已登录的账户");
		builder.setSingleChoiceItems(names, 0,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						BFGameConfig.ACCOUNT = names[which];
						// Config.PASSWORD = passwords[which];
						// homeAccount.setText(Config.ACCOUNT);
						// homePassword.setText(Config.PASSWORD);
					}

				});

		// 添加一个确定按钮
		builder.setPositiveButton(" 确 定 ",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Config.ACCOUNT = names[which];
						// Config.PASSWORD = passwords[which];
						mFindPasswordAccount.setText(BFGameConfig.ACCOUNT);
						// mHomePassword.setText(Config.PASSWORD);
						// bindCheckPhoneNumber(BXGameConfig.ACCOUNT);
					}
				});
		builder.create().show();
		// 显示对话框是，默认选中第一项
		// Config.ACCOUNT = names[0];
		// Config.PASSWORD = passwords[0];
	}

	/**
	 * 显示强制修改密码的dialog 只有在快速注册账号第二次登陆的时候强制修改密码
	 */
	public void showChangePasswordDialgo() {
		if (mForceChangePasswordDialog == null) {
			mForceChangePasswordDialog = new BFCustomDialog(mContext);
			mForceChangePasswordDialog
					.setConfirmClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							stringForceResetPassword = mForceChangePasswordDialog
									.getPasswordContent();
							if (TextUtils.isEmpty(stringForceResetPassword)) {
								showToast("请输入密码");
								return;
							}

							if (!isAllCharacter(stringForceResetPassword)
									|| stringForceResetPassword.length() < 6) {
								showToast("输入密码格式有误，请重新输入！");
								return;
							}

							randomAccountChangePassword(BFGameConfig.ACCOUNT,
									BFGameConfig.GAMEPARAM.getCpId(),
									BFGameConfig.GAMEPARAM.getGameId(),
									BFGameConfig.GAMEPARAM.getServerId(),
									stringForceResetPassword);
						}
					});
		}

		mForceChangePasswordDialog.show();

	}

	/**
	 * 修改sd卡中保存的账户记录文件
	 */
	public void changeLocationAccountFile() {
		final int READ_ACCOUNT_SUCCESS = 100;
		final Handler handler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == READ_ACCOUNT_SUCCESS) {
					ArrayList<HashMap<String, String>> accounts = (ArrayList<HashMap<String, String>>) msg.obj;
					if (accounts != null) {
						// 读取保存的账号记录
						boolean isExist = false;
						HashMap<String, String> currentAccount = null;
						for (int i = 0; i < accounts.size(); i++) {
							HashMap<String, String> existAccount = accounts
									.get(i);
							if (BFGameConfig.ACCOUNT.equals(existAccount
									.get("name"))) {
								currentAccount = existAccount;
								// // 如果之前已经保存过这个账户，则替换删除原位置的对象，添加到最后位置
								isExist = true;
								break;
							}
						}
						HashMap<String, String> account = new HashMap<String, String>();
						account.put("name", BFGameConfig.ACCOUNT);
						account.put("password", BFGameConfig.PASSWORD);
						account.put("token", BFGameConfig.TOKEN);
						if (!isExist) {
							// 之前没有保存过这个账户，添加进去
							accounts.add(account);
						} else {
							// 删除之前保存的账号，添加更改密码后的账号进去
							accounts.remove(currentAccount);
							accounts.add(account);
						}
						// 写入文件
						AccountPersistenceUtil.writerAccountToFile(
								accountFileSavePath + File.separator
										+ accountFileName, accounts);
					}

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
						msg.what = READ_ACCOUNT_SUCCESS;
						msg.obj = accounts;
						msg.sendToTarget();
					}
				});
	}

	private String isBfUser() {
		// if(mhomeUserTypeBaofeng != null)
		// return mhomeUserTypeBaofeng.isChecked() ? BXGameConfig.userTypeBF :
		// BXGameConfig.userTypeBX;
		// else
		// return BXGameConfig.userTypeBX;
		return "0";
	}
}
