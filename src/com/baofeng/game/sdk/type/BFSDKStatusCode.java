package com.baofeng.game.sdk.type;

public class BFSDKStatusCode {

	// 操作成功
	public static final int SUCCESS = 1;
	// 系统错误
	public static final int SYSTEM_ERROR = 2;
	// 提交的合作者编号不存在
	public static final int CP_NOT_EXIST = 3;
	// IP地址不允许访问
	public static final int IP_NOT_ALLOWED = 4;
	// 签名验证失败
	public static final int SIGNATURE_INVALID = 5;

	// 提交的数据己超过有效期 30秒
	public static final int SIGNATURE_VERITY_EXPIRED = 6;

	// 游戏或服务器不存在
	public static final int GAME_OR_SERVER_INVALID = 7;

	// 用户不存在
	public static final int USER_NOT_EXIST = 8;

	// 充值失败
	public static final int CHARGE_FAILED = 9;

	// 订单号重复支付
	public static final int ORDERNO_DUPLICATE = 10;

	// 充值金额不正确
	public static final int CHARGE_MONEY_ERROR = 11;
	
	//充值退出
	public static final int CHARGE_USER_EXIT = 22;

	// 用户封禁错误
	public static final int FROZEN_ERROR = 12;

	// 用户解封错误
	public static final int UNFROZEN_ERROR = 13;

	// 用户密码错误
	public static final int PASSWORD_ERROR = 14;

	// 用户登录错误 用户名不存在
	public static final int LOGIN_ERROR = 15;

	// 票据过期
	public static final int TICKET_EXPIRED = 16;

	// 用户已经存在
	public static final int USER_EXIST = 17;

	// 已经绑定手机号
	public static final int BIND_HAS = 18;

	// 没有绑定手机号
	public static final int BIND_UN = 19;

	// 绑定验证失败
	public static final int BIND_MISSMATCH = 20;

	// 订单不存在
	public static final int BILL_NOT_EXIST = 21;

	// 参数格式不正确
	public static final int PARAMETER_INVALID = 100;
	
	// 参数格式不正确
	public static final int PARAMETER_PASSWORD_INVALID = 111;

	// 未知错误
	public static final int UNKNOWN_ERROR = 200;

	// 操作失败
	public static final int FAIL = -1; 

	// 没有初始化
	public static final int NO_INIT = -2;

	// 初始化失败
	public static final int INIT_FAIL = -3;

	// 无法联网
	public static final int NO_NETWORK = -50;

	// 没有登陆
	public static final int NO_LOGIN = -100;

	// 登陆/注册认证失败
	public static final int LOGIN_USER_AUTH_FAIL = -101;

	// 注册 用户名已经存在
	public static final int REGISTER_USER_AUTH_FAIL_EXISTS = -102;

	// 登陆被踢出
	public static final int LOGIN_EXIT = -103;

	// 已经绑定手机
	public static final int BIND_MOBILE_HAS = -200;      

	// 没有绑定手机
	public static final int BIND_MOBILE_UNBIND = -201;

	// 与绑定手机不一致
	public static final int BIND_MOBILE_PASSWORD_MISMATCH = -202;

	// 绑定用户不存在
	public static final int BIND_MOBILE_USER_NOT_EXIST = -203;

	// 绑定用户短信验证码不正确
	public static final int BIND_MOBILE_PASSWORD_CODE_MISMATCH = -204;
	
	//原账号已解绑，绑定新账号成功
	public static final int OLD_BIND_DEL_NEW_BIND_SUCCESS = 400;
	
	public static final int VERIFYCODE_ERROR = 23;

}
