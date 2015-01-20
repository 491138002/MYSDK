package com.baofeng.game.sdk.user;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.net.AsyncHttpGet;
import com.baofeng.game.sdk.net.AsyncHttpPost;
import com.baofeng.game.sdk.net.BaseRequest;
import com.baofeng.game.sdk.net.DefaultThreadPool;
import com.baofeng.game.sdk.net.RequestParameter;
import com.baofeng.game.sdk.net.ThreadCallBack;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.util.CheckNetWorkUtil;
import com.baofeng.game.sdk.util.LogUtil;
import com.baofeng.game.sdk.util.MD5Util;

public class BFBasePanel implements ThreadCallBack {

	protected static String ID_TYPE = "id";
	protected static String ICON_TYPE = "drawable";
	protected static String LAYOUT_TYPE = "layout";
	protected static String STRING_TYPE = "string";
	protected static String ANIM_TYPE = "anim";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 上下文
	 */
	protected Context mContext;
	
	/**
	 * 布局获取控制器
	 */
	protected LayoutInflater mInflater;

	/**
	 * 当前activity所持有的所有请求
	 */
	List<BaseRequest> requestList = null;

	protected boolean isRegistQuestionReceiver = true;

	protected String accountFileSavePath;// 配置文件的保存路径
	protected static final String accountFileName = "account.dat";// 配置文件名

	public Context getContext() {
		return mContext;
	}

	public BFBasePanel(Context context) {
		this.mContext = context;
		requestList = new ArrayList<BaseRequest>();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initFilePath();
	}

	public void onDestory() {
		/**
		 * 在view销毁的时候同时设置停止请求，停止线程请求回调
		 */
		cancelRequest();
	}

	/**
	 * 对参数进行签名
	 * 
	 * @param original
	 *            明文
	 * @return MD5加密后的密文
	 */
	protected String generateSign(String original) {
		LogUtil.d("sign", original);
		String confidential = MD5Util.MD5(original);
		LogUtil.d("sign md5", confidential);
		return confidential.toUpperCase();
	}

	protected void showToast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

	protected void showToast(int code) {
		String msg = "";
		switch (code) {
		case BFSDKStatusCode.USER_NOT_EXIST:
			msg = "用户不存在";
			break;
		case BFSDKStatusCode.BIND_MOBILE_PASSWORD_CODE_MISMATCH:
			msg = "绑定用户短信验证码不正确";
			break;
		case BFSDKStatusCode.BIND_MOBILE_USER_NOT_EXIST:
			msg = "绑定用户不存在";
			break;
		case BFSDKStatusCode.BIND_MOBILE_PASSWORD_MISMATCH:
			msg = "与绑定手机不一致";
			break;
		case BFSDKStatusCode.BIND_MOBILE_UNBIND:
			msg = "没有绑定手机";
			break;
		case BFSDKStatusCode.BIND_MOBILE_HAS:
			msg = "已经绑定手机";
			break;
		case BFSDKStatusCode.REGISTER_USER_AUTH_FAIL_EXISTS:
			msg = "注册 用户名已经存在";
			break;
		case BFSDKStatusCode.LOGIN_USER_AUTH_FAIL:
			msg = "认证失败";
			break;
		case BFSDKStatusCode.FAIL:
			msg = "操作失败";
			break;
		case BFSDKStatusCode.UNKNOWN_ERROR:
			msg = "未知错误";
			break;
		case BFSDKStatusCode.PARAMETER_INVALID:
			msg = "参数格式不正确";
			break;
		case BFSDKStatusCode.BILL_NOT_EXIST:
			msg = "订单不存在";
			break;
		case BFSDKStatusCode.BIND_MISSMATCH:
			msg = "绑定验证失败";
			break;
		case BFSDKStatusCode.BIND_UN:
			msg = "未绑定手机号码，通过联系客服找回密码：400-6708-706";
			break;
		case BFSDKStatusCode.USER_EXIST:
			msg = "用户已经存在";
			break;
		case BFSDKStatusCode.CHARGE_FAILED:
			msg = " 充值失败";
			break;
		case BFSDKStatusCode.CHARGE_MONEY_ERROR:
			msg = "充值金额不正确";
			break;
		case BFSDKStatusCode.LOGIN_ERROR:
			msg = "用户名不存在";
			break;
		case BFSDKStatusCode.PARAMETER_PASSWORD_INVALID:
			msg = "原始密码输入错误";
			break;
		case BFSDKStatusCode.OLD_BIND_DEL_NEW_BIND_SUCCESS:
			msg = "原账号已解绑，绑定新账号成功";
			break;
		case BFSDKStatusCode.VERIFYCODE_ERROR:
			msg = "验证码不正确";
			break;
		default:
			msg = "操作失败";
		}
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	private void initFilePath() {

		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? Environment
				.getExternalStorageDirectory().getAbsolutePath() : mContext
				.getCacheDir().getPath();

		accountFileSavePath = cachePath + File.separator + "BFSDK"
				+ File.separator + "account";
		File file = new File(accountFileSavePath);
		if (!file.exists())
			file.mkdirs();
		File accountFile = new File(accountFileSavePath + File.separator
				+ accountFileName);
		if (!accountFile.exists()) {
			try {
				accountFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 验证是否全为字母
	 */
	protected boolean isAllCharacter(String value) {
		Pattern pattern = Pattern.compile("\\W");
		Matcher matcher = pattern.matcher(value);
		if (matcher.find()) {
			return false;
		}
		return true;
	}

	/**
	 * 验证是否为手机号
	 */
	protected boolean isPhoneNumber(String number) {
		Pattern pattern = Pattern.compile("[1][3578]\\d{9}");
		Matcher matcher = pattern.matcher(number);
		return matcher.matches();
	}

	public void cancelRequest() {
		if (requestList != null && requestList.size() > 0) {
			for (BaseRequest request : requestList) {
				if (request.getRequest() != null) {
					try {
						request.getRequest().abort();
						requestList.remove(request.getRequest());

					} catch (UnsupportedOperationException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void onCallbackFromThread(String resultJson) {
		// TODO Auto-generated method stub

	}

	protected void startHttpRequst(String requestType, String url,
			List<RequestParameter> parameter, boolean isShowLoadingDialog) {
		startHttpRequst(requestType, url, parameter, isShowLoadingDialog, -1);
	}

	protected void startHttpRequst(String requestType, String url,
			List<RequestParameter> parameter, boolean isShowLoadingDialog,
			int resultCode) {
		if (isShowLoadingDialog) {
			if (!CheckNetWorkUtil.checkNetWork(mContext)) {
				return;
			}
		}
		if (null != parameter) {
			 parameter.add(new RequestParameter("channel_id", String.valueOf(BFGameConfig.CHANNEL_ID)));
			 parameter.add(new RequestParameter("imei", BFGameConfig.PHONE_IMEI));
			 parameter.add(new RequestParameter("ver", BFGameConfig.BXSDK_VERSION));
			 }
		BaseRequest httpRequest = null;
		if ("POST".equalsIgnoreCase(requestType)) {
			httpRequest = new AsyncHttpPost(this, url, parameter,
					isShowLoadingDialog, "", false, resultCode);
		} else {
			httpRequest = new AsyncHttpGet(this, url, parameter,
					isShowLoadingDialog, "", false, resultCode);
		}
		DefaultThreadPool.getInstance().execute(httpRequest);
		this.requestList.add(httpRequest);
	}

	protected void startHttpRequst(String requestType, String url,
			List<RequestParameter> parameter, boolean isShowLoadingDialog,
			int connectTimeout, int readTimeout) {
		if (isShowLoadingDialog) {
			if (!CheckNetWorkUtil.checkNetWork(mContext)) {
				return;
			}
		}
		if (null != parameter) {
			 parameter.add(new RequestParameter("channel_id", String.valueOf(BFGameConfig.CHANNEL_ID)));
			 parameter.add(new RequestParameter("imei", BFGameConfig.PHONE_IMEI));
			 parameter.add(new RequestParameter("ver", BFGameConfig.BXSDK_VERSION));
			 }
		BaseRequest httpRequest = null;
		if ("POST".equalsIgnoreCase(requestType)) {
			httpRequest = new AsyncHttpPost(this, url, parameter,
					isShowLoadingDialog, connectTimeout, readTimeout);
		} else {
			httpRequest = new AsyncHttpGet(this, url, parameter,
					isShowLoadingDialog, connectTimeout, readTimeout);
		}
		DefaultThreadPool.getInstance().execute(httpRequest);
		this.requestList.add(httpRequest);
	}

	public void startHttpRequst(String requestType, String url,
			List<RequestParameter> parameter, boolean isShowLoadingDialog,
			String loadingDialogContent) {
		startHttpRequst(requestType, url, parameter, isShowLoadingDialog,
				loadingDialogContent, true, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT);
	}

	protected void startHttpRequst(String requestType, String url,
			List<RequestParameter> parameter, boolean isShowLoadingDialog,
			String loadingDialogContent, boolean isHideCloseBtn,
			int connectTimeout, int readTimeout) {
		startHttpRequst(requestType, url, parameter, isShowLoadingDialog,
				loadingDialogContent, isHideCloseBtn, connectTimeout,
				readTimeout, -1);
	}

	protected void startHttpRequst(String requestType, String url,
			List<RequestParameter> parameter, boolean isShowLoadingDialog,
			String loadingDialogContent, boolean isHideCloseBtn,
			int connectTimeout, int readTimeout, int resultCode) {
		if (isShowLoadingDialog) {
			if (!CheckNetWorkUtil.checkNetWork(mContext)) {
				return;
			}
		}
		if (null != parameter) {
			 parameter.add(new RequestParameter("channel_id", String.valueOf(BFGameConfig.CHANNEL_ID)));
			 parameter.add(new RequestParameter("imei", BFGameConfig.PHONE_IMEI));
			 parameter.add(new RequestParameter("ver", BFGameConfig.BXSDK_VERSION));
			 }
		BaseRequest httpRequest = null;
		if ("POST".equalsIgnoreCase(requestType)) {
			httpRequest = new AsyncHttpPost(this, url, parameter,
					isShowLoadingDialog, loadingDialogContent, isHideCloseBtn,
					connectTimeout, readTimeout, resultCode);
		} else {
			httpRequest = new AsyncHttpGet(this, url, parameter,
					isShowLoadingDialog, loadingDialogContent, isHideCloseBtn,
					connectTimeout, readTimeout, resultCode);
		}
		DefaultThreadPool.getInstance().execute(httpRequest);
		this.requestList.add(httpRequest);
	}

	@Override
	public void onCallBackFromThread(String resultJson, int resultCode) {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
