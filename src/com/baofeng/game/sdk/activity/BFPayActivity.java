package com.baofeng.game.sdk.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.adapter.BFPayGameCardAdpater;
import com.baofeng.game.sdk.adapter.BFPayLeftBarAdapter;
import com.baofeng.game.sdk.adapter.BFPayLeftBarAdapter.BXOnItemClickListener;
import com.baofeng.game.sdk.adapter.BFPayPrepaidCardAdpater;
import com.baofeng.game.sdk.adapter.BFSearchAdapter;
import com.baofeng.game.sdk.alipay.AlixManager;
import com.baofeng.game.sdk.alipay.AlixManager.OnPayListener;
import com.baofeng.game.sdk.base.BFBaseActivity;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.net.RequestParameter;
import com.baofeng.game.sdk.type.BFOrientation;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.type.OperateType;
import com.baofeng.game.sdk.ui.BFLoadingDialog;
import com.baofeng.game.sdk.util.BXDialogUtil;
import com.baofeng.game.sdk.util.BXDialogUtil.OnAlertSelectId;
import com.baofeng.game.sdk.util.LogUtil;
import com.baofeng.game.sdk.util.ParseUtil;
import com.baofeng.game.sdk.util.StringUtil;
import com.baofeng.game.sdk.util.Util;
import com.baofeng.game.sdk.vo.BFGameParamInfo;
import com.baofeng.game.sdk.vo.BFGamePayParamInfo;
import com.baofeng.game.sdk.vo.BFPayResult;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

public class BFPayActivity extends BFBaseActivity {

	/**
	 * 主页
	 */
	private ListView leftListView;
	private BFPayLeftBarAdapter leftBarAdapter;
	private ViewFlipper bodyVf;
	private Button backBtn;
	private TextView payPriceTv;
	private TextView payProductTv;
	private TextView payUsernameTv;

	private String price;

	private BFPayLeftBarAdapter.BXOnItemClickListener onItemClickListener = new BXOnItemClickListener() {
		public void onItemClick(int position) {
			bodyVf.setDisplayedChild(position);
		}
	};

	/**
	 * 信用卡
	 */
	private Button creditPayBtn;

	/**
	 * 支付宝
	 */
	private Button alipayPayBtn;

	/**
	 * 储蓄卡
	 */
	// private GridView savingsCardGridView;
	// private BFPaySavingsCardAdpater savingsCardAdpater;
	private Button savingsCardPayBtn;

	/**
	 * 充值卡
	 */
	private GridView prepaidCardGridView;
	private BFPayPrepaidCardAdpater prepaiCardGridAdapter;
	private EditText prepaidCardNumberEt;
	private Button prepaidCardNumberClearBtn;
	private EditText prepaidCardPwdEt;
	private Button prepaidCardPwdClearBtn;
	private Button prepaidCardPayBtn;
	private AutoCompleteTextView prepaidCardPayMoenyAct;
	private int mSelectedMoeny;

	/**
	 * 点卡
	 */
	private GridView gameCardGridView;
	private AutoCompleteTextView gameCardCardType;
	private BFPayGameCardAdpater gameCardGridAdapter;
	private EditText gameCardNumberEt;
	private Button gameCardNumberClearBtn;
	private EditText gameCardPwdEt;
	private Button gameCardPwdClearBtn;
	private Button gameCardPayBtn;
	private AutoCompleteTextView gameCardPayMoenyAct;

	/**
	 * 平台币
	 */

	/**
	 * 银联
	 */
	private Button cupPayBtn;

	/**
	 * TYPE_PAYMENT 充值界面 TYPE_GAME_CARD 游戏卡充值界面 TYPE_PHONE 手机充值卡界面 TYPE_ALIPAY
	 * 支付宝充值界面 TYPE_END 支付完成界面 TYPE_WEB 支付web界面 TYPE_PHONE_LT 手机支付-联通
	 * TYPE_PHONE_YD 手机支付-移动 TYPE_PHONE_DX 手机支付-电信 TYPE_UP 银联支付
	 */
	public static final int TYPE_PAYMENT = 0;
	public static final int TYPE_GAME_CARD = 1;
	public static final int TYPE_PHONE = 2;
	public static final int TYPE_ALIPAY = 3;
	public static final int TYPE_END = 4;
	public static final int TYPE_WEB = 5;
	public static final int TYPE_UP = 9;

	public static final int TYPE_PHONE_LT = 0;
	public static final int TYPE_PHONE_YD = 1;
	public static final int TYPE_PHONE_DX = 2;

	public static final int TYPE_GAME_CARD_JW = 0;
	public static final int TYPE_GAME_CARD_QQ = 1;
	public static final int TYPE_GAME_CARD_SD = 2;
	public static final int TYPE_GAME_CARD_WM = 3;
	public static final int TYPE_GAME_CARD_WY = 4;

	/**
	 * 手机卡支付
	 */
	private static final int PAY_BY_PHONE_KEY = 1001;
	/**
	 * 游戏卡支付
	 */
	private static final int PAY_BY_GAME_CARD_KEY = 1002;
	/**
	 * 银联支付
	 */
	private static final int PAY_BY_UP_KEY = 1003;
	/**
	 * 短信支付
	 */
	private static final int PAY_BY_SMS_KEY = 1005;
	/**
	 * 支付结果查询
	 */
	private static final int PAY_BY_RESULT_QUERY = 1004;
	/**
	 * 支付宝支付
	 */
	private static final int PAY_BY_ALIPAY_KEY = 1006;

	/**
	 * 银联是否安装
	 */
	private boolean isInstall = false;

	/**
	 * 充值返回状态吗
	 */
	public final int PAY_RESULT_LOADING = 0;
	public final int PAY_RESULT_SUCCESS = 1;
	public final int PAY_RESULT_ERROR = 2;
	public final int CARD_PASSWORD_ERROR = 325;
	public final int PASSWORD_ERROR = 301;
	public final int IS_USE_CARD = 302;

	/**
	 * 返回结果
	 */
	private BFPayResult result = null;
	private double recordMoney = 0;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			int retCode = msg.getData().getInt("retCode");
			int retType = msg.getData().getInt("retType");

			if (retType == OperateType.PAY) {
				switch (retCode) {
				case BFSDKStatusCode.SUCCESS:
					if (BFGameConfig.PAYCALLBACK_LISTENER != null) {
						if (BFGameConfig.isSMSCallBack) {
							BFGameConfig.PAYCALLBACK_LISTENER.callback(
									BFSDKStatusCode.SUCCESS,
									String.valueOf(recordMoney),
									BFGameConfig.payBackTypeSMS);
							BFGameConfig.isSMSCallBack = false;
						} else {
							BFGameConfig.PAYCALLBACK_LISTENER.callback(
									BFSDKStatusCode.SUCCESS,
									String.valueOf(recordMoney),
									BFGameConfig.payBackTypeOther);
						}

					}
					break;
				case BFSDKStatusCode.CHARGE_USER_EXIT:
					if (BFGameConfig.PAYCALLBACK_LISTENER != null) {
						BFGameConfig.isSMSCallBack = false;
						BFGameConfig.PAYCALLBACK_LISTENER.callback(
								BFSDKStatusCode.CHARGE_USER_EXIT, "",
								BFGameConfig.payBackTypeOther);
					}
					break;
				}
			}
		}

	};

	private ArrayList<String> meonyList = new ArrayList<String>();
	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return this;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (BFGameConfig.isDebug) {
			// 测试设置支付订单号
			BFGamePayParamInfo payparam = new BFGamePayParamInfo();
			payparam.setCpBillNo("123456");// CP订单号
			payparam.setExtra("abc2013-05-24"); // 扩展参数 支付成功服务器通知接口将原样返回
			payparam.setCpBillMoney(0.01);// 设置订单金额 可选项
			// 如果不设置 请提供同一通知地址给我们 ，通知将统一发送到所提供的地址
			payparam.setNotifyUrl("http://pay.zjszz.173.com/pay!finishOrder.action?aaa=bbb&ccc=ddd"); // 本单据支付成功后的通知返回地址
			// 用于多服务器不同通知地址
			payparam.setSubject("60仙玉");
			BFGameConfig.PAYMENT_PARAM = payparam;

			// 测试用
			BFGameParamInfo param = new BFGameParamInfo();
			param.setCpId(10001);
			param.setCpKey("123456");
			param.setGameId(101);
			param.setServerId(2);
			param.setChannelId(4);// 渠道ID
			param.setScreenOrientation(BFOrientation.HORIZONTAL);

			BFGameConfig.GAMEPARAM = param;

			BFGameConfig.USER_NAME = "jiaoshi";
		}

		if (StringUtil.isEmpty(BFGameConfig.USER_NAME)) {
			BFGameConfig.USER_NAME = BFGameConfig.ACCOUNT;
		}

		if (BFGameConfig.SCREEN_ORIENTATION == BFOrientation.HORIZONTAL) {
			setContentView(BFResources.layout.bx_pay_center);
		} else {
			setContentView(BFResources.layout.bx_pay_center_vertical);
		}

		initView();
	}

	public void initView() {
		
		backBtn = (Button) findViewById(BFResources.id.bx_pay_back);
		bodyVf = (ViewFlipper) findViewById(BFResources.id.bx_pay_content_vf);
		leftListView = (ListView) findViewById(BFResources.id.bx_pay_bar_list);
		payPriceTv = (TextView) findViewById(BFResources.id.bx_pay_price_tv);
		payProductTv = (TextView) findViewById(BFResources.id.bx_pay_product_tv);
		payUsernameTv = (TextView) findViewById(BFResources.id.bx_pay_username_tv);

		// init LeftBar
		leftListView.setDividerHeight(0);
		leftListView.setCacheColorHint(0);
		leftBarAdapter = new BFPayLeftBarAdapter(context);
		leftBarAdapter.setOnItemClickListener(onItemClickListener);
		leftListView.setAdapter(leftBarAdapter);


		// init back
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				payResult(OperateType.PAY, BFSDKStatusCode.CHARGE_USER_EXIT);
				finish();
			}
		});

		// init pay infos
		payPriceTv.setText("支付金额："
				+ BFGameConfig.PAYMENT_PARAM.getCpBillMoney() + "元");
		payProductTv.setText("商品：" + BFGameConfig.PAYMENT_PARAM.getSubject());
		payUsernameTv.setText("用户名：" + BFGameConfig.USER_NAME);

		// init layouts
		initCreditCardLayout();
		initAlipayLayout();
		initBankCardLayout();
		initPrepaidCardLayout();
		initGameCardLayout();
		initCUPLayout();
	}

	/**
	 * 初始化信用卡布局
	 */
	public void initCreditCardLayout() {
		View view = bodyVf.getChildAt(0);
		creditPayBtn = (Button) view.findViewById(BFResources.id.bx_pay_submit);
		creditPayBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				payByUP(BFGameConfig.PAYMENT_PARAM.getCpBillMoney());
			}
		});
	}

	/**
	 * 初始化支付宝布局
	 */
	public void initAlipayLayout() {
		View view = bodyVf.getChildAt(1);
		alipayPayBtn = (Button) view.findViewById(BFResources.id.bx_pay_submit);
		alipayPayBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				payAlipay(BFGameConfig.PAYMENT_PARAM.getCpBillMoney());
			}
		});
	}

	/**
	 * 初始化储蓄卡布局
	 */
	public void initBankCardLayout() {
		View view = bodyVf.getChildAt(2);
		// savingsCardGridView = (GridView)
		// view.findViewById(BFResources.id.bx_pay_card_type_gv);
		savingsCardPayBtn = (Button) view
				.findViewById(BFResources.id.bx_pay_submit);

		// savingsCardAdpater = new BFPaySavingsCardAdpater(context);
		// savingsCardGridView.setAdapter(savingsCardAdpater);
		// LinearLayout.LayoutParams gridParams = (LinearLayout.LayoutParams)
		// savingsCardGridView.getLayoutParams();
		// if(BFGameConfig.SCREEN_ORIENTATION == BFOrientation.VERTICAL){
		// gridParams.height = Util.dip2px(245,
		// context.getResources().getDisplayMetrics().density);
		// }else{
		// gridParams.height = Util.dip2px(100,
		// context.getResources().getDisplayMetrics().density);
		// }
		// savingsCardGridView.setLayoutParams(gridParams);
		savingsCardPayBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				payByUP(BFGameConfig.PAYMENT_PARAM.getCpBillMoney());
			}
		});
	}

	/**
	 * 初始化充值卡布局
	 */
	private int selectCardTypeIndex = 0;

	public void initPrepaidCardLayout() {
		View view = bodyVf.getChildAt(3);
		prepaidCardGridView = (GridView) view
				.findViewById(BFResources.id.bx_pay_card_type_gv);
		gameCardCardType = (AutoCompleteTextView) view
				.findViewById(BFResources.id.bx_pay_card_type_actv);
		prepaidCardNumberEt = (EditText) view
				.findViewById(BFResources.id.bx_pay_card_number_et);
		prepaidCardNumberClearBtn = (Button) view
				.findViewById(BFResources.id.bx_pay_card_number_clear_btn);
		prepaidCardPwdEt = (EditText) view
				.findViewById(BFResources.id.bx_pay_card_pwd_et);
		prepaidCardPwdClearBtn = (Button) view
				.findViewById(BFResources.id.bx_pay_card_pwd_clear_btn);
		prepaidCardPayBtn = (Button) view
				.findViewById(BFResources.id.bx_pay_submit);
		prepaidCardPayMoenyAct = (AutoCompleteTextView) view
				.findViewById(BFResources.id.bx_pay_card_moeny_et);

		if (BFGameConfig.SCREEN_ORIENTATION == BFOrientation.VERTICAL) {
			prepaiCardGridAdapter = new BFPayPrepaidCardAdpater(context);
			prepaidCardGridView.setAdapter(prepaiCardGridAdapter);
			LinearLayout.LayoutParams gridParams = (LinearLayout.LayoutParams) prepaidCardGridView
					.getLayoutParams();
			gridParams.height = Util.dip2px(120, context.getResources()
					.getDisplayMetrics().density);
			prepaidCardGridView.setLayoutParams(gridParams);
		} else {
			List<String> cardTypeList = new ArrayList<String>();
			cardTypeList.add("联通充值卡");
			cardTypeList.add("移动充值卡");
			cardTypeList.add("电信充值卡");
			gameCardCardType.setAdapter(new BFSearchAdapter(context,
					gameCardCardType, cardTypeList, new OnAlertSelectId() {
						public void onClick(int whichButton, Object o) {
							selectCardTypeIndex = whichButton;
							switch (whichButton) {
							case 0:
								if (context instanceof BFPayActivity) {
									((BFPayActivity) context).setLTTipList();
								}
								break;
							case 1:
								if (context instanceof BFPayActivity) {
									((BFPayActivity) context).setYDTipList();
								}
								break;
							case 2:
								if (context instanceof BFPayActivity) {
									((BFPayActivity) context).setDXTipList();
								}
								break;
							}
						}
					}));
			gameCardCardType.setText(cardTypeList.get(0));
		}

		prepaidCardPayMoenyAct.setAdapter(new BFSearchAdapter(context,
				prepaidCardPayMoenyAct, meonyList, new OnAlertSelectId() {
					public void onClick(int whichButton, Object o) {

					}
				}));

		prepaidCardNumberClearBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				prepaidCardNumberEt.setText("");
			}
		});

		prepaidCardPwdClearBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				prepaidCardPwdEt.setText("");
			}
		});

		prepaidCardPayBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String cardNumber = prepaidCardNumberEt.getText().toString();
				String cardPwd = prepaidCardPwdEt.getText().toString();
				int type = 0;
				if (BFGameConfig.SCREEN_ORIENTATION == BFOrientation.VERTICAL) {
					type = prepaiCardGridAdapter.getSelectIndex();
				} else {
					type = selectCardTypeIndex;
				}

				int cardMoney = Integer.parseInt(prepaidCardPayMoenyAct
						.getText().toString().replace("元", ""));

				if (cardMoney < BFGameConfig.PAYMENT_PARAM.getCpBillMoney()) {
					showToast("卡面额小于充值面额");
					return;
				}

				if (type == -1) {
					showToast("请选择卡类型");
					return;
				}

				if (StringUtil.isEmpty(cardNumber)) {
					showToast("卡号不能为空");
					return;
				}

				if (StringUtil.isEmpty(cardPwd)) {
					showToast("密码不能为空");
					return;
				}

				// 支付
				mSelectedMoeny = (int) BFGameConfig.PAYMENT_PARAM
						.getCpBillMoney();
				payByPhone(type, cardNumber, cardPwd, cardMoney);
			}
		});

		setLTTipList();
	}

	/**
	 * 初始化游戏卡布局（点卡）
	 */
	public void initGameCardLayout() {
		View view = bodyVf.getChildAt(4);
		gameCardGridView = (GridView) view
				.findViewById(BFResources.id.bx_pay_card_type_gv);
		gameCardCardType = (AutoCompleteTextView) view
				.findViewById(BFResources.id.bx_pay_card_type_actv);
		gameCardNumberEt = (EditText) view
				.findViewById(BFResources.id.bx_pay_card_number_et);
		gameCardNumberClearBtn = (Button) view
				.findViewById(BFResources.id.bx_pay_card_number_clear_btn);
		gameCardPwdEt = (EditText) view
				.findViewById(BFResources.id.bx_pay_card_pwd_et);
		gameCardPwdClearBtn = (Button) view
				.findViewById(BFResources.id.bx_pay_card_pwd_clear_btn);
		gameCardPayBtn = (Button) view
				.findViewById(BFResources.id.bx_pay_submit);
		gameCardPayMoenyAct = (AutoCompleteTextView) view
				.findViewById(BFResources.id.bx_pay_card_moeny_et);
		if (BFGameConfig.SCREEN_ORIENTATION == BFOrientation.VERTICAL) {
			gameCardGridAdapter = new BFPayGameCardAdpater(context);
			gameCardGridView.setAdapter(gameCardGridAdapter);
			LinearLayout.LayoutParams gridParams = (LinearLayout.LayoutParams) gameCardGridView
					.getLayoutParams();
			gridParams.height = Util.dip2px(180, context.getResources()
					.getDisplayMetrics().density);
			gameCardGridView.setLayoutParams(gridParams);
		} else {
			List<String> cardTypeList = new ArrayList<String>();
			cardTypeList.add("骏网点卡");
			cardTypeList.add("Q币点卡");
			cardTypeList.add("盛大点卡");
			cardTypeList.add("完美点卡");
			cardTypeList.add("网易点卡");
			gameCardCardType.setAdapter(new BFSearchAdapter(context,
					gameCardCardType, cardTypeList, new OnAlertSelectId() {
						public void onClick(int whichButton, Object o) {
							selectCardTypeIndex = whichButton;
							switch (whichButton) {
							case 0:
								if (context instanceof BFPayActivity) {
									((BFPayActivity) context)
											.setJunWangTipList();
								}
								break;
							case 1:
								if (context instanceof BFPayActivity) {
									((BFPayActivity) context).setQBiTipList();
								}
								break;
							case 2:
								if (context instanceof BFPayActivity) {
									((BFPayActivity) context)
											.setShengDaTipList();
								}
								break;
							case 3:
								if (context instanceof BFPayActivity) {
									((BFPayActivity) context)
											.setWanMeiTipList();
								}
								break;
							case 4:
								if (context instanceof BFPayActivity) {
									((BFPayActivity) context)
											.setWangYiTipList();
								}
								break;
							}
						}
					}));
			gameCardCardType.setText(cardTypeList.get(0));
		}

		gameCardPayMoenyAct.setAdapter(new BFSearchAdapter(context,
				gameCardPayMoenyAct, meonyList, new OnAlertSelectId() {
					public void onClick(int whichButton, Object o) {

					}
				}));

		gameCardNumberClearBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				gameCardNumberEt.setText("");
			}
		});

		gameCardPwdClearBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				gameCardPwdEt.setText("");
			}
		});

		gameCardPayBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String cardNumber = gameCardNumberEt.getText().toString();
				String cardPwd = gameCardPwdEt.getText().toString();
				int type = 0;
				if (BFGameConfig.SCREEN_ORIENTATION == BFOrientation.VERTICAL) {
					type = gameCardGridAdapter.getSelectIndex();
				} else {
					type = selectCardTypeIndex;
				}

				int cardMoney = Integer.parseInt(gameCardPayMoenyAct.getText()
						.toString().replace("元", ""));

				if (cardMoney < BFGameConfig.PAYMENT_PARAM.getCpBillMoney()) {
					showToast("卡面额小于充值面额");
					return;
				}

				if (type == -1) {
					showToast("请选择卡类型");
					return;
				}

				if (StringUtil.isEmpty(cardNumber)) {
					showToast("卡号不能为空");
					return;
				}

				if (StringUtil.isEmpty(cardPwd)) {
					showToast("密码不能为空");
					return;
				}

				// 支付
				mSelectedMoeny = (int) BFGameConfig.PAYMENT_PARAM
						.getCpBillMoney();
				payByGameCard(type, cardNumber, cardPwd, mSelectedMoeny,
						cardMoney);
			}
		});

		setJunWangTipList();
	}

	/**
	 * 初始化银联布局
	 */
	public void initCUPLayout() {
		View view = bodyVf.getChildAt(5);
		cupPayBtn = (Button) view.findViewById(BFResources.id.bx_pay_submit);
		cupPayBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				payByUP(BFGameConfig.PAYMENT_PARAM.getCpBillMoney());
			}
		});
	}

	public void getPayEndView(final int code) {

		String content = "";
		switch (code) {
		case PAY_RESULT_SUCCESS:
			content = "充值成功";
			break;
		case PAY_RESULT_ERROR:
			content = "充值失败";
			break;
		case CARD_PASSWORD_ERROR:
			content = "充值失败,账号密码输入错误";
			break;
		case PASSWORD_ERROR:
			content = "充值失败,卡密码输入错误";
			break;
		case IS_USE_CARD:
			content = "充值失败，此卡已使用";
			break;
		default:
			content = "充值失败";
			break;
		}

		BXDialogUtil.showDialog(context, "提示", content, "确定", null,
				new OnAlertSelectId() {
					public void onClick(int whichButton, Object o) {

						if (code == PAY_RESULT_SUCCESS) {
							finish();
						}
					}
				}).show();
	}

	/**
	 * 手机卡充值
	 */
	private void payByPhone(int position, String cardNum, String cardPwd,
			int card_price) {

		String gateway = "";
		switch (position) {
		case TYPE_PHONE_LT:
			gateway = "szf_unic";
			break;
		case TYPE_PHONE_YD:
			gateway = "szf_mobile";
			break;
		case TYPE_PHONE_DX:
			gateway = "szf_dianxin";
			break;
		}

		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("service", "pay.tele"));
		parameter.add(new RequestParameter("cp_id", String
				.valueOf(BFGameConfig.GAMEPARAM.getCpId())));
		parameter.add(new RequestParameter("game_id", String
				.valueOf(BFGameConfig.GAMEPARAM.getGameId())));
		parameter.add(new RequestParameter("server_id", String
				.valueOf(BFGameConfig.GAMEPARAM.getServerId())));
		parameter.add(new RequestParameter("user_id", String
				.valueOf(BFGameConfig.USER_ID)));
		parameter.add(new RequestParameter("gateway_name", "神州付支付"));
		parameter.add(new RequestParameter("type", "sz"));
		parameter.add(new RequestParameter("amount", String
				.valueOf(mSelectedMoeny)));
		parameter.add(new RequestParameter("card_price", String
				.valueOf(card_price)));
		parameter.add(new RequestParameter("gateway", gateway));
		parameter.add(new RequestParameter("sdk_flg", "1"));
		parameter.add(new RequestParameter("card_number", cardNum));
		parameter.add(new RequestParameter("card_pwd", cardPwd));
		parameter.add(new RequestParameter("cp_bill_no",
				BFGameConfig.PAYMENT_PARAM.getCpBillNo()));
		parameter.add(new RequestParameter("notify_url",
				BFGameConfig.PAYMENT_PARAM.getNotifyUrl()));
		parameter.add(new RequestParameter("extra", BFGameConfig.PAYMENT_PARAM
				.getExtra()));
		startHttpRequst(BFGameConfig.HTTP_GET,
				BFGameConfig.BX_SERVICE_URL_PAY_BY_ALIPAY, parameter, true, "",
				true, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, PAY_BY_PHONE_KEY);
	}

	/**
	 * 订单查询
	 */

	private BFLoadingDialog customLoadingDialog;
	private MyCount mc;
	private String orderId = "";

	private void orderInquiry(String orderNumber) {
		isTimeOver = false;
		orderId = orderNumber;
		mc = new MyCount(120000, 1000);
		mc.start();

		customLoadingDialog = new BFLoadingDialog(this, "正在查询结果…请稍后…", true);
		if (customLoadingDialog != null && !customLoadingDialog.isShowing()) {
			customLoadingDialog.show();
		}

		beginQuery(0);
	}

	private static final long SLEEP = 3000;

	private void beginQuery(int code) {// 数字0为等待状态

		try {
			Thread.sleep(SLEEP);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (isTimeOver || code == 1) {
			customLoadingDialog.dismiss();
			mc.cancel();

			if (code == 1) {
				getPayEndView(PAY_RESULT_SUCCESS);

				payResult(OperateType.PAY, BFSDKStatusCode.SUCCESS);
			}
		} else if (code == 3 || code == 4) {
			customLoadingDialog.dismiss();
			mc.cancel();
			getPayEndView(PAY_RESULT_ERROR);
		} else {

			List<RequestParameter> parameter = new ArrayList<RequestParameter>();
			parameter.add(new RequestParameter("order_id", orderId));

			startHttpRequst(BFGameConfig.HTTP_GET,
					BFGameConfig.BX_SERVICE_URL_PAY_BY_QUERY, parameter, false,
					"", true, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
					BFGameConfig.READ_MIDDLE_TIMEOUT, PAY_BY_RESULT_QUERY);

		}

	}

	private static boolean isTimeOver = false;

	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			isTimeOver = true;
		}

		@Override
		public void onTick(long arg0) {

		}

	}

	/**
	 * 游戏卡充值
	 */
	private void payByGameCard(int position, String cardNum, String cardPwd,
			double money, int card_price) {
		String gateway = "";
		String gatewayName = "";
		switch (position) {
		case TYPE_GAME_CARD_JW:
			gateway = "card_jcard";
			gatewayName = "骏网充值卡";
			break;
		case TYPE_GAME_CARD_QQ:
			gateway = "card_qq";
			gatewayName = "Q币卡充值";
			break;
		case TYPE_GAME_CARD_SD:
			gateway = "card_shanda";
			gatewayName = "盛大卡充值";
			break;
		case TYPE_GAME_CARD_WM:
			gateway = "card_prefect";
			gatewayName = "完美卡充值";
			break;
		case TYPE_GAME_CARD_WY:
			gateway = "card_netease";
			gatewayName = "网易卡充值";
			break;
		}
		gatewayName = "游戏点卡支付";

		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("service", "pay.card"));
		parameter.add(new RequestParameter("cp_id", String
				.valueOf(BFGameConfig.GAMEPARAM.getCpId())));
		parameter.add(new RequestParameter("game_id", String
				.valueOf(BFGameConfig.GAMEPARAM.getGameId())));
		parameter.add(new RequestParameter("server_id", String
				.valueOf(BFGameConfig.GAMEPARAM.getServerId())));
		parameter.add(new RequestParameter("user_id", String
				.valueOf(BFGameConfig.USER_ID)));
		parameter.add(new RequestParameter("gateway_name", gatewayName));
		parameter.add(new RequestParameter("type", "card"));
		parameter.add(new RequestParameter("amount", String.valueOf(money)));
		parameter.add(new RequestParameter("card_price", String
				.valueOf(card_price)));
		parameter.add(new RequestParameter("gateway", gateway));
		parameter.add(new RequestParameter("sdk_flg", "1"));
		parameter.add(new RequestParameter("card_number", cardNum));
		parameter.add(new RequestParameter("card_pwd", cardPwd));
		parameter.add(new RequestParameter("cp_bill_no",
				BFGameConfig.PAYMENT_PARAM.getCpBillNo()));
		parameter.add(new RequestParameter("notify_url",
				BFGameConfig.PAYMENT_PARAM.getNotifyUrl()));
		parameter.add(new RequestParameter("extra", BFGameConfig.PAYMENT_PARAM
				.getExtra()));
		startHttpRequst(BFGameConfig.HTTP_GET,
				BFGameConfig.BX_SERVICE_URL_PAY_BY_ALIPAY, parameter, true, "",
				true, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, PAY_BY_GAME_CARD_KEY);
	}

	/**
	 * 银联充值-获取订单信息
	 */
	private void payByUP(double money) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("service", "pay.unionpay"));
		parameter.add(new RequestParameter("cp_id", String
				.valueOf(BFGameConfig.GAMEPARAM.getCpId())));
		parameter.add(new RequestParameter("user_id", String
				.valueOf(BFGameConfig.USER_ID)));
		parameter.add(new RequestParameter("game_id", String
				.valueOf(BFGameConfig.GAMEPARAM.getGameId())));
		parameter.add(new RequestParameter("server_id", String
				.valueOf(BFGameConfig.GAMEPARAM.getServerId())));
		parameter.add(new RequestParameter("gateway_name", "银联支付"));
		parameter.add(new RequestParameter("type", "yinlian"));
		parameter.add(new RequestParameter("amount", String.valueOf(money)));
		parameter.add(new RequestParameter("gateway", "yinlian_ccb"));
		parameter.add(new RequestParameter("cp_bill_no",
				BFGameConfig.PAYMENT_PARAM.getCpBillNo()));
		parameter.add(new RequestParameter("notify_url",
				BFGameConfig.PAYMENT_PARAM.getNotifyUrl()));
		parameter.add(new RequestParameter("extra", BFGameConfig.PAYMENT_PARAM
				.getExtra()));
		startHttpRequst(BFGameConfig.HTTP_GET,
				BFGameConfig.BX_SERVICE_URL_PAY_BY_ALIPAY, parameter, true, "",
				true, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, PAY_BY_UP_KEY);
	}

	/**
	 * 支付宝充值-获取订单信息
	 */
	private void payAlipay(double money) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("service", "pay.alipay"));
		parameter.add(new RequestParameter("cp_id", String
				.valueOf(BFGameConfig.GAMEPARAM.getCpId())));
		parameter.add(new RequestParameter("user_id", String
				.valueOf(BFGameConfig.USER_ID)));
		parameter.add(new RequestParameter("game_id", String
				.valueOf(BFGameConfig.GAMEPARAM.getGameId())));
		parameter.add(new RequestParameter("server_id", String
				.valueOf(BFGameConfig.GAMEPARAM.getServerId())));
		parameter.add(new RequestParameter("gateway_name", "支付宝支付"));
		parameter.add(new RequestParameter("type", "ap"));
		parameter.add(new RequestParameter("amount", String.valueOf(money)));
		parameter.add(new RequestParameter("gateway", "ap_spt"));
		parameter.add(new RequestParameter("cp_bill_no",
				BFGameConfig.PAYMENT_PARAM.getCpBillNo()));
		parameter.add(new RequestParameter("notify_url",
				BFGameConfig.PAYMENT_PARAM.getNotifyUrl()));
		parameter.add(new RequestParameter("extra", BFGameConfig.PAYMENT_PARAM
				.getExtra()));

		startHttpRequst(BFGameConfig.HTTP_GET,
				BFGameConfig.BX_SERVICE_URL_PAY_BY_ALIPAY, parameter, true, "",
				true, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, PAY_BY_ALIPAY_KEY);
	}

	@Override
	public void onCallBackFromThread(String resultJson, int resultCode) {
		// TODO Auto-generated method stub
		super.onCallBackFromThread(resultJson, resultCode);
		try {
			LogUtil.d("result", resultJson);
			if (StringUtil.isNotEmpty(resultJson)) {
				switch (resultCode) {
				case PAY_BY_PHONE_KEY:
					result = ParseUtil.getPayResult(resultJson);
					if (result != null) {
						if (result.getCode() == 1) {
							getPayEndView(PAY_RESULT_SUCCESS);
							payResult(OperateType.PAY, BFSDKStatusCode.SUCCESS);
						} else {
							getPayEndView(result.getCode());
						}
					} else {
						getPayEndView(PAY_RESULT_ERROR);
					}
					break;
				case PAY_BY_GAME_CARD_KEY:
					result = ParseUtil.getPayResult(resultJson);
					if (result != null && result.getCode() == 1) {
						orderInquiry(result.getOrderId());
					} else {
						getPayEndView(PAY_RESULT_ERROR);
					}
					break;
				case PAY_BY_UP_KEY:
					result = ParseUtil.getPayResult(resultJson);
					if (result != null
							&& StringUtil.isNotEmpty(result.getOrderId())) {
						String serverMode = "00";
						UPPayAssistEx.startPayByJAR(context, PayActivity.class, null, null, result.getTn(), serverMode);
						
//						int ret = UPPayAssistEx.startPay(context, "", "",
//								result.getTn(), serverMode);
//						if (ret == UPPayAssistEx.PLUGIN_NOT_FOUND) {
//							// 安装Asset中提供的UPPayPlugin.apk
//							// 此处可根据实际情况，添加相应的处理逻辑
//							isInstall = UPPayAssistEx
//									.installUPPayPlugin(context);
//						}
					} else {
						showToast("支付失败");
					}
					break;
				case PAY_BY_RESULT_QUERY:
					result = ParseUtil.getPayResultQuery(resultJson);
					if (result != null) {
						beginQuery(result.getCode());

					} else {
						getPayEndView(PAY_RESULT_ERROR);
					}
					break;
				case PAY_BY_SMS_KEY:
					BFGameConfig.isSMSCallBack = true;
					result = ParseUtil.getPayResultQuery(resultJson);
					if (result != null && result.getCode() == 1) {
						if (BFGameConfig.isSMSOK) {
							payResult(OperateType.PAY, BFSDKStatusCode.SUCCESS);
							BFGameConfig.isSMSOK = false;
						}
					} else {

					}
					break;
				case PAY_BY_ALIPAY_KEY:
					result = ParseUtil.getPayResult(resultJson);
					if (result != null && result.getCode() == SUCCESS) {
						AlixManager aliMgr = new AlixManager(context,
								BFGameConfig.PAYMENT_PARAM.getSubject(),
								BFGameConfig.PAYMENT_PARAM.getCpBillMoney(),
								result.getOrderId(), result.getNotifyUrl(),
								new OnPayListener() {
									public void payCallback(
											final boolean success,
											String statusCode) {
										String content = "";
										if (success) {
											content = "支付成功";
											payResult(OperateType.PAY,
													BFSDKStatusCode.SUCCESS);
										} else {
											content = "支付失败";
										}
										BXDialogUtil.showDialog(context, "提示",
												content, "确定", null,
												new OnAlertSelectId() {
													public void onClick(
															int whichButton,
															Object o) {
														if (success) {
															finish();
														}
													}
												}).show();
									}
								});
						aliMgr.init();
					} else {
						showToast("支付失败");
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {

		if (BFGameConfig.SCREEN_ORIENTATION == BFOrientation.HORIZONTAL) {
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (BFGameConfig.SCREEN_ORIENTATION == BFOrientation.VERTICAL) {
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		super.onResume();

//		if (isInstall) {
//			isInstall = false;
//			if (result != null && StringUtil.isNotEmpty(result.getTn())) {
//				String serverMode = "00";
//				int ret = UPPayAssistEx.startPay(context, "", "",
//						result.getTn(), serverMode);
//				if (ret == UPPayAssistEx.PLUGIN_NOT_FOUND) {
//					// 安装Asset中提供的UPPayPlugin.apk
//					// 此处可根据实际情况，添加相应的处理逻辑
//					isInstall = UPPayAssistEx.installUPPayPlugin(context);
//				}
//			}
//		}
	}

	public void payResult(int retType, int retCode) {
		Message msg = Message.obtain();

		Bundle data = new Bundle();
		data.putInt("retType", retType);
		data.putInt("retCode", retCode);

		msg.setData(data);

		mHandler.sendMessage(msg);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*************************************************
		 * 
		 * 步骤3：处理银联手机支付控件返回的支付结果
		 * 
		 ************************************************/
		if (data == null) {
			return;
		}

		String msg = "";
		/*
		 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
		String str = data.getExtras().getString("pay_result");
		if (str.equalsIgnoreCase("success")) {
			msg = "支付成功！";
			getPayEndView(PAY_RESULT_SUCCESS);
			payResult(OperateType.PAY, BFSDKStatusCode.SUCCESS);
		} else if (str.equalsIgnoreCase("fail")) {
			msg = "支付失败！";
			getPayEndView(PAY_RESULT_ERROR);
		} else if (str.equalsIgnoreCase("cancel")) {
			msg = "用户取消了支付";
			getPayEndView(PAY_RESULT_ERROR);
		}

	}

	// /**
	// * 短信支付
	// */
	// private void paySMS(String status, double amount) {
	// List<RequestParameter> parameter = new ArrayList<RequestParameter>();
	//
	// parameter.add(new RequestParameter("input_cpid", String
	// .valueOf(BFGameConfig.GAMEPARAM.getCpId())));
	// parameter.add(new RequestParameter("input_game", String
	// .valueOf(BFGameConfig.GAMEPARAM.getGameId())));
	// parameter.add(new RequestParameter("input_area", String
	// .valueOf(BFGameConfig.GAMEPARAM.getServerId())));
	// parameter.add(new RequestParameter("input_username",
	// BFGameConfig.USER_NAME));
	//
	// parameter.add(new RequestParameter("input_amount", amount + ""));
	// parameter.add(new RequestParameter("input_cp_bill_no",
	// BFGameConfig.PAYMENT_PARAM.getCpBillNo()));
	// parameter.add(new RequestParameter("status", status));
	// parameter.add(new RequestParameter("input_extra",
	// BFGameConfig.PAYMENT_PARAM
	// .getExtra()));
	//
	// startHttpRequst(BFGameConfig.HTTP_GET,
	// BFGameConfig.BX_SERVICE_URL_PAY_BY_SMS,
	// parameter, true, "", true, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
	// BFGameConfig.READ_MIDDLE_TIMEOUT, PAY_BY_SMS_KEY);
	// }

	/**
	 * 设置骏网提示列表
	 */
	public void setJunWangTipList() {
		meonyList.clear();
		meonyList.add("5元");
		meonyList.add("6元");
		meonyList.add("10元");
		meonyList.add("15元");
		meonyList.add("20元");
		meonyList.add("30元");
		meonyList.add("50元");
		meonyList.add("120元");
		meonyList.add("200元");
		meonyList.add("300元");
		meonyList.add("500元");
		gameCardPayMoenyAct.setAdapter(null);
		gameCardPayMoenyAct.setText(meonyList.get(0));
		gameCardPayMoenyAct.setAdapter(new BFSearchAdapter(context,
				gameCardPayMoenyAct, meonyList, new OnAlertSelectId() {
					public void onClick(int whichButton, Object o) {

					}
				}));
	}

	/**
	 * 设置Q币提示列表
	 */
	public void setQBiTipList() {
		meonyList.clear();
		meonyList.add("5元");
		meonyList.add("10元");
		meonyList.add("15元");
		meonyList.add("30元");
		meonyList.add("60元");
		meonyList.add("100元");
		meonyList.add("200元");
		gameCardPayMoenyAct.setAdapter(null);
		gameCardPayMoenyAct.setText(meonyList.get(0));
		gameCardPayMoenyAct.setAdapter(new BFSearchAdapter(context,
				gameCardPayMoenyAct, meonyList, new OnAlertSelectId() {
					public void onClick(int whichButton, Object o) {

					}
				}));
	}

	/**
	 * 设置盛大提示列表
	 */
	public void setShengDaTipList() {
		meonyList.clear();
		meonyList.add("1元");
		meonyList.add("2元");
		meonyList.add("3元");
		meonyList.add("5元");
		meonyList.add("9元");
		meonyList.add("10元");
		meonyList.add("15元");
		meonyList.add("25元");
		meonyList.add("30元");
		meonyList.add("35元");
		meonyList.add("40元");
		meonyList.add("50元");
		meonyList.add("100元");
		meonyList.add("300元");
		meonyList.add("350元");
		meonyList.add("1000元");
		gameCardPayMoenyAct.setAdapter(null);
		gameCardPayMoenyAct.setText(meonyList.get(0));
		gameCardPayMoenyAct.setAdapter(new BFSearchAdapter(context,
				gameCardPayMoenyAct, meonyList, new OnAlertSelectId() {
					public void onClick(int whichButton, Object o) {

					}
				}));
	}

	/**
	 * 设置完美提示列表
	 */
	public void setWanMeiTipList() {
		meonyList.clear();
		meonyList.add("10元");
		meonyList.add("30元");
		meonyList.add("50元");
		meonyList.add("100元");
		gameCardPayMoenyAct.setAdapter(null);
		gameCardPayMoenyAct.setText(meonyList.get(0));
		gameCardPayMoenyAct.setAdapter(new BFSearchAdapter(context,
				gameCardPayMoenyAct, meonyList, new OnAlertSelectId() {
					public void onClick(int whichButton, Object o) {

					}
				}));
	}

	/**
	 * 设置网易提示列表
	 */
	public void setWangYiTipList() {
		meonyList.clear();
		meonyList.add("5元");
		meonyList.add("10元");
		meonyList.add("15元");
		meonyList.add("20元");
		meonyList.add("30元");
		meonyList.add("50元");
		gameCardPayMoenyAct.setAdapter(null);
		gameCardPayMoenyAct.setText(meonyList.get(0));
		gameCardPayMoenyAct.setAdapter(new BFSearchAdapter(context,
				gameCardPayMoenyAct, meonyList, new OnAlertSelectId() {
					public void onClick(int whichButton, Object o) {

					}
				}));
	}

	/**
	 * 设置联通提示列表
	 */
	public void setLTTipList() {
		meonyList.clear();
		meonyList.add("20元");
		meonyList.add("30元");
		meonyList.add("100元");
		meonyList.add("300元");
		meonyList.add("500元");
		prepaidCardPayMoenyAct.setAdapter(null);
		prepaidCardPayMoenyAct.setText(meonyList.get(0));
		prepaidCardPayMoenyAct.setAdapter(new BFSearchAdapter(context,
				prepaidCardPayMoenyAct, meonyList, new OnAlertSelectId() {
					public void onClick(int whichButton, Object o) {

					}
				}));
	}

	/**
	 * 设置移动提示列表
	 */
	public void setYDTipList() {
		meonyList.clear();
		meonyList.add("10元");
		meonyList.add("20元");
		meonyList.add("30元");
		meonyList.add("50元");
		meonyList.add("100元");
		meonyList.add("300元");
		meonyList.add("500元");
		prepaidCardPayMoenyAct.setAdapter(null);
		prepaidCardPayMoenyAct.setText(meonyList.get(0));
		prepaidCardPayMoenyAct.setAdapter(new BFSearchAdapter(context,
				prepaidCardPayMoenyAct, meonyList, new OnAlertSelectId() {
					public void onClick(int whichButton, Object o) {

					}
				}));
	}

	/**
	 * 设置电信提示列表
	 */
	public void setDXTipList() {
		meonyList.clear();
		meonyList.add("10元");
		meonyList.add("20元");
		meonyList.add("30元");
		meonyList.add("50元");
		meonyList.add("100元");
		meonyList.add("200元");
		meonyList.add("300元");
		meonyList.add("500元");
		prepaidCardPayMoenyAct.setAdapter(null);
		prepaidCardPayMoenyAct.setText(meonyList.get(0));
		prepaidCardPayMoenyAct.setAdapter(new BFSearchAdapter(context,
				prepaidCardPayMoenyAct, meonyList, new OnAlertSelectId() {
					public void onClick(int whichButton, Object o) {

					}
				}));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_BACK) {
			payResult(OperateType.PAY, BFSDKStatusCode.CHARGE_USER_EXIT);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
