package com.baofeng.game.sdk.exception;

import com.baofeng.game.sdk.type.BFSDKStatusCode;

public class BFSDKStatusCodeException extends Exception {

	private static final String SUCCESS = "登陆成功，不会返回结果，成功则直接进入游戏";
	private static final String SYSTEM_ERROR = "系统错误";
	private static final String CP_NOT_EXIST = "提交的合作者编号不存在";
	private static final String IP_NOT_ALLOWED = "IP地址不允许访问";
	private static final String SIGNATURE_INVALID = "签名验证失败";
	private static final String SIGNATURE_VERITY_EXPIRED = "提交的数据己超过有效期 30秒";
	private static final String GAME_OR_SERVER_INVALID = "游戏或服务器不存在";
	private static final String USER_NOT_EXIST = "用户不存在";
	private static final String CHARGE_FAILED = "充值失败";
	private static final String ORDERNO_DUPLICATE = "订单号重复支付";
	private static final String CHARGE_MONEY_ERROR = "充值金额不正确";
	private static final String FROZEN_ERROR = "用户封禁错误";
	private static final String UNFROZEN_ERROR = "用户解封错误";
	private static final String PASSWORD_ERROR = "用户密码错误";
	private static final String LOGIN_ERROR = "用户登录错误 用户名不存在";
	private static final String TICKET_EXPIRED = "票据过期";
	private static final String USER_EXIST = "用户已经存在";
	private static final String BIND_HAS = "已经绑定手机号";
	private static final String BIND_UN = "没有绑定手机号";
	private static final String BIND_MISSMATCH = "绑定验证失败";
	private static final String BILL_NOT_EXIST = "订单不存在";
	private static final String PARAMETER_INVALID = "参数格式不正确";
	private static final String UNKNOWN_ERROR = "未知错误";

	private int statusCode;

	public BFSDKStatusCodeException(int code) {
		statusCode = code;
	}

	@Override
	public String getMessage() {
		return getStatusCodeMessage();
	}

	public String getStatusCodeMessage() {
		String message = null;
		switch (statusCode) {
		case BFSDKStatusCode.SUCCESS:
			message = SUCCESS;
			break;
		case BFSDKStatusCode.SYSTEM_ERROR:
			message = SYSTEM_ERROR;
			break;
		case BFSDKStatusCode.CP_NOT_EXIST:
			message = CP_NOT_EXIST;
			break;
		case BFSDKStatusCode.IP_NOT_ALLOWED:
			message = IP_NOT_ALLOWED;
			break;
		case BFSDKStatusCode.SIGNATURE_INVALID:
			message = SIGNATURE_INVALID;
			break;
		case BFSDKStatusCode.SIGNATURE_VERITY_EXPIRED:
			message = SIGNATURE_VERITY_EXPIRED;
			break;
		case BFSDKStatusCode.GAME_OR_SERVER_INVALID:
			message = GAME_OR_SERVER_INVALID;
			break;
		case BFSDKStatusCode.USER_NOT_EXIST:
			message = USER_NOT_EXIST;
			break;
		case BFSDKStatusCode.CHARGE_FAILED:
			message = CHARGE_FAILED;
			break;
		case BFSDKStatusCode.ORDERNO_DUPLICATE:
			message = ORDERNO_DUPLICATE;
			break;
		case BFSDKStatusCode.CHARGE_MONEY_ERROR:
			message = CHARGE_MONEY_ERROR;
			break;
		case BFSDKStatusCode.FROZEN_ERROR:
			message = FROZEN_ERROR;
			break;
		case BFSDKStatusCode.UNFROZEN_ERROR:
			message = UNFROZEN_ERROR;
			break;
		case BFSDKStatusCode.PASSWORD_ERROR:
			message = PASSWORD_ERROR;
			break;
		case BFSDKStatusCode.LOGIN_ERROR:
			message = LOGIN_ERROR;
			break;
		case BFSDKStatusCode.TICKET_EXPIRED:
			message = TICKET_EXPIRED;
			break;
		case BFSDKStatusCode.USER_EXIST:
			message = USER_EXIST;
			break;
		case BFSDKStatusCode.BIND_HAS:
			message = BIND_HAS;
			break;
		case BFSDKStatusCode.BIND_UN:
			message = BIND_UN;
			break;
		case BFSDKStatusCode.BIND_MISSMATCH:
			message = BIND_MISSMATCH;
			break;
		case BFSDKStatusCode.BILL_NOT_EXIST:
			message = BILL_NOT_EXIST;
			break;
		case BFSDKStatusCode.PARAMETER_INVALID:
			message = PARAMETER_INVALID;
			break;
		case BFSDKStatusCode.UNKNOWN_ERROR:
			message = UNKNOWN_ERROR;
			break;
		default:
			message = UNKNOWN_ERROR;
			break;
		}

		return message;
	}

}
