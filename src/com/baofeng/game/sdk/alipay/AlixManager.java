package com.baofeng.game.sdk.alipay;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.baofeng.game.sdk.util.NumberUtils;
import com.baofeng.game.sdk.util.StringUtil;

public class AlixManager {

	private final String TAG = getClass().getSimpleName();

	/**
	 * 上下文
	 */
	private Activity act;
	/**
	 * 购买价格
	 */
	private String price;
	/**
	 * 标题
	 */
	private String SUJECT_1 = "";
	/**
	 * 加载框
	 */
	private ProgressDialog mProgress = null;
	/**
	 * 订单号
	 */
	private String bill_no;
	
	
	/**
	 * 通知地址
	 */
	private String notify_url;
	
	
	
	
	/**
	 * 支付监听
	 */
	private OnPayListener onPayListener;
	
	private static final int RQF_PAY = 1;
	private static final int RQF_LOGIN = 2;
	
	public AlixManager(Activity act, String suject, double price, String bill_no,String notify_url, OnPayListener onPayListener) {
		this.act = act;
		this.SUJECT_1 = suject;
		this.price = NumberUtils.round(price, 2, BigDecimal.ROUND_HALF_UP) + "";
		this.bill_no = bill_no;
		this.notify_url = notify_url;
		this.onPayListener = onPayListener;
		SUJECT_1 = suject;
		if(StringUtil.isEmpty(SUJECT_1)){
			SUJECT_1 = "Product" + System.currentTimeMillis();
		}
	}

	public void init() {
		try {
			String info = getNewOrderInfo();
			String sign = Rsa.sign(info, Keys.PRIVATE);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + getSignType();
			Log.i("ExternalPartner", "start pay");
			// start the pay.
			Log.i(TAG, "info = " + info);

			final String orderInfo = info;
			new Thread() {
				public void run() {
					PayTask alipay = new PayTask(act);
					
					//设置为沙箱模式，不设置默认为线上环境
					//alipay.setSandBox(true);
					String result = alipay.pay(orderInfo);
					Log.i(TAG, "result = " + result);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(act, "Failure calling remote service",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	private String getNewOrderInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(bill_no);
		sb.append("\"&subject=\"");
		sb.append(SUJECT_1);
		sb.append("\"&body=\"");
		sb.append(SUJECT_1);
		sb.append("\"&total_fee=\"");
		sb.append(price);
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode(notify_url));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}
	
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	public static String sign(String content, String privateKey) {
		String charset = "UTF-8";
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(charset));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 * @return
	 */
	String getSignType() {
		String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
		return getSignType;
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);
			result.parseResult();
			switch (msg.what) {
			case RQF_PAY:
				// 处理交易结果
				try { 
					// 验签失败
					if (!result.isSignOk) {
						if(onPayListener != null)
							onPayListener.payCallback(false, "0");
						
					} else {// 验签成功。验签成功后再判断交易状态码
						if (result.resultStatus.equals("9000")) {
							// 判断交易状态码，只有9000表示交易成功
							if(onPayListener != null)
								onPayListener.payCallback(true, result.resultStatus);
						} else {
							if(onPayListener != null)
								onPayListener.payCallback(false, result.resultStatus);
						}
					}
				} catch (Exception e) {
					if(onPayListener != null)
						onPayListener.payCallback(false, "-1");
				}
				break;
			case RQF_LOGIN: 
				break;
			default:
				break;
			}
		};
	};
	
	
	public interface OnPayListener{
		public void payCallback(boolean success, String statusCode);
	}
}
