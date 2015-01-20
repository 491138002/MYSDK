package com.baofeng.game.sdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baofeng.game.sdk.exception.BFCallbackListenerNullException;
import com.baofeng.game.sdk.listener.BFGameCallbackListener;
import com.baofeng.game.sdk.listener.BFPayCallbackListener;
import com.baofeng.game.sdk.type.BFOrientation;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.vo.BFGameParamInfo;
import com.baofeng.game.sdk.vo.BFGamePayParamInfo;

public class StartActivity extends Activity{

	private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	private Context context = this;
	
	private double money = 0.01;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		params.leftMargin = 20;
		params.rightMargin = 20;
		params.topMargin = 20;
		
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		Button mainBtn = new Button(this);
		Button pay2Btn = new Button(this);
		
		mainBtn.setText("启动首页");
		mainBtn.setGravity(Gravity.CENTER);
		mainBtn.setPadding(20, 20, 20, 20);
		layout.addView(mainBtn, params);
		
		pay2Btn.setText("启动支付页");
		pay2Btn.setGravity(Gravity.CENTER);
		pay2Btn.setPadding(20, 20, 20, 20);
		
		layout.addView(pay2Btn, params);
		
		ColorDrawable dw = new ColorDrawable(Color.WHITE);
//		layout.setBackgroundDrawable(dw);
		layout.setBackgroundResource(R.drawable.bg);
		setContentView(layout);
		
		// 测试用
		BFGameParamInfo param = new BFGameParamInfo();
		param.setCpId(10001);
		param.setCpKey("123456");
		param.setGameId(28);
		param.setServerId(2);
		param.setChannelId(4);// 渠道ID
		param.setScreenOrientation(BFOrientation.VERTICAL);

		try {
			BFGameSDK.defaultSDK().initSDK(StartActivity.this, param,
					new BFGameCallbackListener() {
						@Override
						public void callback(int resultCode, Object resultData) {
							if(resultCode == BFSDKStatusCode.SUCCESS){
							}
						}
					});
		} catch (BFCallbackListenerNullException e) {

		}
		
		mainBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				try {
					BFGameSDK.defaultSDK().login(StartActivity.this,new BFGameCallbackListener(){
						@Override
						public void callback(int resultCode, Object arg1) {
							if(resultCode == BFSDKStatusCode.SUCCESS){
								Toast.makeText(StartActivity.this, "login success" + (String)arg1, Toast.LENGTH_LONG).show();
							}else if(resultCode == BFSDKStatusCode.FAIL){
								Toast.makeText(StartActivity.this, "login faile", Toast.LENGTH_LONG).show();
							}else if(resultCode == BFSDKStatusCode.LOGIN_EXIT){
								Toast.makeText(StartActivity.this, "login exit", Toast.LENGTH_LONG).show();
							}
							
						}});
				} catch (BFCallbackListenerNullException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		pay2Btn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				try {
					// 测试设置支付订单号
					BFGamePayParamInfo payparam = new BFGamePayParamInfo();
					payparam.setCpBillNo("123456");// CP订单号
					payparam.setExtra("abc2013-05-24"); // 扩展参数 支付成功服务器通知接口将原样返回
					if(money > 0){
						payparam.setCpBillMoney(money);// 设置订单金额 可选项
					}
					payparam.setSubject("100元宝");
					
					// 如果不设置 请提供同一通知地址给我们 ，通知将统一发送到所提供的地址
					payparam.setNotifyUrl("http://test.bingxue.com/test/test"); // 本单据支付成功后的通知返回地址
																		// 用于多服务器不同通知地址

					BFGameSDK.defaultSDK().pay(StartActivity.this, new BFPayCallbackListener<String>() {
						public void callback(int statuscode, String data,
								int type) {
							if(statuscode ==  BFSDKStatusCode.SUCCESS){
								
								
								Toast.makeText(StartActivity.this, "pay success" + String.valueOf(statuscode), Toast.LENGTH_LONG).show();
							}else if(statuscode ==  BFSDKStatusCode.CHARGE_USER_EXIT){
								Toast.makeText(StartActivity.this, "pay exit", Toast.LENGTH_LONG).show();
							}
						}
						
					}, payparam);
				} catch (BFCallbackListenerNullException e) {

				}
			}
		});
	}
}
