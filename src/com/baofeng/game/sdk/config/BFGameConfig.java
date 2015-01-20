package com.baofeng.game.sdk.config;

import android.content.Context;

import com.baofeng.game.sdk.listener.BFGameCallbackListener;
import com.baofeng.game.sdk.listener.BFPayCallbackListener;
import com.baofeng.game.sdk.type.BFOrientation;
import com.baofeng.game.sdk.vo.BFGameParamInfo;
import com.baofeng.game.sdk.vo.BFGamePayParamInfo;

public class BFGameConfig {
	
//		public static final String BASE_SERVICE_URL = "http://sdkapi.lt.com/";
	/** 接口地址 **/
	//测试服
//	public static final String BASE_SERVICE_URL = "http://14.17.126.90:8088/";
	//正式服
	public static final String BASE_SERVICE_URL = "http://sdk.gcenter.baofeng.com/";
	//"http://test.bingxue.com/";//"http://test.bingxue.com:9103/";//"http://service.fanjie.com/";//"http://test.bingxue.com:8050/";
	/** 登录 **/
	public static final String BX_SERVICE_URL_ACCOUNT_LOGIN = BASE_SERVICE_URL + "user/login";
	public static final String BX_SERVICE_URL_ACCOUNT_LOGIN_BYTOKEN = BASE_SERVICE_URL + "user/loginbytoken";
	public static final String BX_SERVICE_URL_ACCOUNT_INFO = BASE_SERVICE_URL + "user/getuserinfo";

	public static final String SERVER_KEY = BASE_SERVICE_URL + "user/ServerKey";
	/** 注册 **/
	public static final String BX_SERVICE_URL_ACCOUNT_REGISTER = BASE_SERVICE_URL + "user/register";

	/** 发送验证码，带验证码返回 **/
	public static final String BX_SERVICE_URL_SEND_CODE = BASE_SERVICE_URL + "sendpasswordcodesdk2";

	/** 验证验证码的正确性 **/
	public static final String BX_SERVICE_URL_CHECK_CODE = BASE_SERVICE_URL + "checkpasswordcode";

	/** 是否已绑定手机 **/
	public static final String BX_SERVICE_URL_ALREADY_BIND_PHONE = BASE_SERVICE_URL + "chkbindmobile";

	/** 是否已绑定手机,返回电话号码 **/
	public static final String BX_SERVICE_URL_ALREADY_BIND_PHONE_TWO = BASE_SERVICE_URL + "user/isbindmobile";

	/** 重置密码 **/
	public static final String BX_SERVICE_URL_RESET_PASSWORD = BASE_SERVICE_URL + "user/resetforgetpwd";

	/** 推荐游戏列表 **/
	public static final String BX_SERVICE_URL_GAME_LIST = BASE_SERVICE_URL + "games";

	/** 平台活动列表 **/
	public static final String BX_SERVICE_URL_NEWS_LIST = BASE_SERVICE_URL + "news";
	/** 平台活动详情 **/
	public static final String BX_SERVICE_URL_NEWS_DETAIL = BASE_SERVICE_URL + "activestatus";
	/** 获取游戏版本 **/
	public static final String BX_SERVICE_URL_GET_VERSION = BASE_SERVICE_URL + "getgameversion";

	/** 修改密码 **/
	public static final String BX_SERVICE_URL_CHANGE_PASSWORD = BASE_SERVICE_URL + "user/changepwd";

	/** 获取短信验证码 **/
	public static final String BX_SERVICE_URL_GET_VERITY_CODE = BASE_SERVICE_URL + "user/bindmobilesendcode";
	
	/** 获取找回密码短信验证码 **/
	public static final String BX_SERVICE_URL_GET_RESET_PASSWORD_VERITY_CODE = BASE_SERVICE_URL + "user/sendforgetpwdcode";

	/** 绑定手机号 **/
	public static final String BX_SERVICE_URL_BIND_PHONENUMBER = BASE_SERVICE_URL + "user/bindmobile";

	/** 快速注册用户修改密码 **/
	public static final String BX_QUICK_RESET_PASSWORD = BASE_SERVICE_URL + "quickresetpwd";

	/** 手机卡支付 **/
	public static final String BX_SERVICE_URL_PAY_BY_PHONE = BASE_SERVICE_URL + "wappay/pay";
	
	/** 支付宝支付 **/
	public static final String BX_SERVICE_URL_PAY_BY_ALIPAY = BASE_SERVICE_URL + "pay";

	/** 短信支付 **/
	public static final String BX_SERVICE_URL_PAY_BY_SMS = BASE_SERVICE_URL + "mobilepay";

	/** 支付 结果查询 **/
	public static final String BX_SERVICE_URL_PAY_BY_QUERY = BASE_SERVICE_URL + "pay/szfsearch";

	/** 版本升级 **/
	public static final String BX_SERVICE_URL_APP_UPGRADE = BASE_SERVICE_URL + "getgameversion";
	
	/** 验证用户名密码 **/
	public static final String BX_SERVICE_URL_CHECK_PASSWORD = BASE_SERVICE_URL + "checkPassword";
	
	/** 获取交易记录 **/
	public static final String BX_SERVICE_URL_TRANSACTION = BASE_SERVICE_URL + "pay";

	/**
	 * 获取配置信息
	 */
	public static final String COMMON = BASE_SERVICE_URL+"config.htm";
	public static final String BBS = "bbs";
	public static final String MAIN = "main";

	/** 屏幕方向,默认为横屏 **/
	public static BFOrientation SCREEN_ORIENTATION = BFOrientation.HORIZONTAL;
	/** 登陆或注册成功后ID **/
	public static int USER_ID = 0;
	/** 登陆或注册成功后 用户名 **/
	public static String USER_NAME;
	/** 游戏客户端登录后从SDK 取得的票据 **/
	public static String TICKET = "";

	/** 记录当前的账号密码，或者是没登录状态时的上次登录用户的账号密码 **/
	public static String ACCOUNT;
	public static String PASSWORD;
	public static String USERTYPE;
	public static String TOKEN = "";
	
	public static final String userTypeBX = "0";
	public static final String userTypeBF = "1";

	public static Context CONTEXT = null;

	/** 支付参数 **/
	public static BFGamePayParamInfo PAYMENT_PARAM = null;

	public static BFGameParamInfo GAMEPARAM = null;

	/** 是否是快速注册 **/
	public static boolean ISFASTREGISTER = false;

	/** 老用户登录Title **/
	public static String GAMETITLE = "";

	/** 记录手机号码 **/
	public static String PHONE_NUMBER = "";

	/** IMEI：International Mobile Equipment Identity 国际移动设备身份码 **/
	public static String PHONE_IMEI = "";

	public static String PACKAGE_NAME = "";

	public static int VERSION_CODE = 0;

	/** 记录回掉函数 **/
	public static BFGameCallbackListener<String> CALLBACK_LISTENER = null;

	/** 记录回掉函数 **/
	public static BFPayCallbackListener<String> PAYCALLBACK_LISTENER = null;

	public static boolean isDebug = false;

	public static final float SCREEN_PROPORTION = 0.8f;

	public static final int CONNECTION_COUNT = 3;// 超时重试次数
	public static final String ERROR_MESSAGE = "数据拉取失败，请重试";
	public static String LOADING_CONTENTS = "加载中，请稍候...";

	public static boolean isGzip;// 是否启用Gzip
	public static boolean IS_STOP_REQUEST = false;// 请求线程是否停止
	public static final int CONNECTION_SHORT_TIMEOUT = 10000;// 连接超时 10s
	public static final int READ_MIDDLE_TIMEOUT = 10000;// 读取超时 10s

	public static final String HTTP_GET = "GET";
	public static final String HTTP_POST = "POST";
	
	
	//客服qq 3095291930
	public static String QQ_QA = "";
	public static String TEL_QA = "";
	public static String GLOBAL_VER = "";
	public static String GAME_CARD = "0";
	//玩家群
	public static String QQ_GROUP = "";
	
	
	public static String SERVERKEY = "";
	/**
	 * 是否有短息支付
	 */
	public static boolean is_hasSMS = false;
	/**
	 * 当前短息支付是否成功
	 */
	public static boolean isSMSOK = false;
	/**
	 * 当前短息支付是否成功
	 */
	public static boolean isSMSOK_Online = true;
	/**
	 * 当前为短信支付返回的结果
	 */
	public static int payBackTypeSMS = 1;
	/**
	 * 当前为其他支付方式返回的结果
	 */
	public static int payBackTypeOther = 0;
	/**
	 * 当前返回的类型
	 */
	public static boolean isSMSCallBack;
	/**
	 * 是否为QQ登陆
	 */
	public static boolean isQQLogin;
	/**
	 * QQ用户昵称
	 */
	public static String qq_Nick_Name;
	public static int CHANNEL_ID=213;
	/**
	 * SDK版本码
	 */
	public static final int SDK_VERSION_CODE = 134;
	/**
	 * SDK版本号
	 */
	public static final String BXSDK_VERSION = "1.3.4";
	/**
	 * 暴风回调URL
	 */
	public static final String BF_LOGIN_NOTIFY_URL = "http://test.bingxue.com/user?service=user.loginsuccess";
	
}
