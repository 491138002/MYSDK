package com.baofeng.game.sdk.user.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.activity.BFMainActivity;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.net.RequestParameter;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.type.OperateType;
import com.baofeng.game.sdk.ui.BFLoadingDialog;
import com.baofeng.game.sdk.user.BFBasePanel;
import com.baofeng.game.sdk.user.HomePanel;
import com.baofeng.game.sdk.util.DeviceUtil;
import com.baofeng.game.sdk.util.LogUtil;
import com.baofeng.game.sdk.util.ParseUtil;
import com.baofeng.game.sdk.util.StringUtil;
import com.baofeng.game.sdk.vo.BFUser;

@SuppressLint("UseSparseArrays")
public class BFHomeWebView extends BFBasePanel {
	private HomePanel mHomePanel;
	private ProgressDialog customLoadingDialog;
	
	private static BFHomeWebView mHomeWebView;
	private View mBaseView;
	private Button mWebViewBack;
	private WebView mWebView;
//	private TextView mWebTitle;
	
	private final int REQUEST_LOGIN_KEY = 10002;
	
	public static final int BF_WEB_URL_LOGIN = 1;
	public static final int BF_WEB_URL_BIND_PHONE = 2;
	public static final int BF_WEB_URL_CHANGE_PASSWORD = 3;
	public static final int BF_WEB_URL_REGISTER = 3;
	
	private static Map<Integer, String> mBfUrlList = new HashMap<Integer, String>();
	static {
		mBfUrlList.put(BF_WEB_URL_LOGIN, "http://sso.baofeng.net/api/mlogin/default");
		mBfUrlList.put(BF_WEB_URL_BIND_PHONE, "http://sso.baofeng.net/api/mlogin/bindmobile");
		mBfUrlList.put(BF_WEB_URL_CHANGE_PASSWORD, "");
		mBfUrlList.put(BF_WEB_URL_REGISTER, "http://sso.baofeng.net/api/mreg/default");
    }
	
	private static Map<Integer, String> mBfTitleList = new HashMap<Integer, String>();
	static {
		mBfTitleList.put(BF_WEB_URL_LOGIN, "暴风用户登录");
		mBfTitleList.put(BF_WEB_URL_BIND_PHONE, "绑定手机");
		mBfTitleList.put(BF_WEB_URL_CHANGE_PASSWORD, "修改密码");
		mBfTitleList.put(BF_WEB_URL_REGISTER, "暴风用户注册");
    }
	
	public synchronized static BFHomeWebView getInstance(Context context){
		if(mHomeWebView == null){
			mHomeWebView = new BFHomeWebView(context);
		}
		return mHomeWebView;
	}
	
	public BFHomeWebView(Context context) {
		super(context);
		
		init();
		initView();
		initListener();
		initValue();
	}

	public void init() {
		mBaseView = mInflater.inflate(BFResources.layout.bx_webview, null);
		customLoadingDialog = new ProgressDialog(mContext);
	}

	public void initView() {
		mWebViewBack = (Button) mBaseView.findViewById(BFResources.id.bx_back);
		mWebView = (WebView) mBaseView.findViewById(BFResources.id.bx_webview);
//		mWebTitle = (TextView) mBaseView.findViewById(BFResources.id.bx_title_tv);
	}

	public void initListener() {
		mWebViewBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mHomePanel.showViewType(HomePanel.SHOW_HOME_PANEL);
			}
		});
	}

	public void initValue() {
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSavePassword(true);
		mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		
		mWebView.setWebChromeClient(new WebChromeClient(){
        	public void onProgressChanged(WebView view,int progress){//载入进度改变而触发 
                super.onProgressChanged(view, progress);  
                if (progress == 100) {
					if(customLoadingDialog != null && customLoadingDialog.isShowing()){
						customLoadingDialog.dismiss();
					}
				}else{
					if (customLoadingDialog != null && !customLoadingDialog.isShowing()) {
						customLoadingDialog.show();
					}
				}
            }   
        });
		
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				
				
				LogUtil.d("url", url);
				if(url.indexOf("token") != -1 && url.indexOf("http://sso.baofeng.net/api/mlogin/appok") != -1){
					BFGameConfig.TOKEN = url.substring(url.indexOf("token=")+6, url.indexOf("&", url.indexOf("token=")+6));
					LogUtil.d("Token", BFGameConfig.TOKEN);
					// 是否初始化
					
					if (BFGameConfig.GAMEPARAM != null) {
						accountLoginByToken(
								BFGameConfig.GAMEPARAM.getCpId(),
								BFGameConfig.GAMEPARAM.getGameId(),
								BFGameConfig.GAMEPARAM.getServerId(),
								BFGameConfig.TOKEN);
					} else {

						((BFMainActivity) mContext).loginResult(
								OperateType.NO_INIT, OperateType.NO_INIT);

					}
				}else{
					view.loadUrl(url);
				}
				return true;
			}
		});
	}

	public void setPage(int type){
		if(StringUtil.isNotEmpty(mBfUrlList.get(type))){
			String url = mBfUrlList.get(type) + getRequestParams();
			LogUtil.d("url", url);
			mWebView.loadUrl(url);
//			mWebTitle.setText(mBfTitleList.get(type));
		}else{
			showToast("没有当前暴风网页地址");
		}
	}
	
	public String getRequestParams(){
		return "?from=gamecenter&version=" + BFGameConfig.SDK_VERSION_CODE + "&did=" + DeviceUtil.getIMEI(mContext) + "&btncolor=blue";//&next_action=" + BFGameConfig.BF_LOGIN_NOTIFY_URL;
	}
	
	public View getView(){
		return mBaseView;
	}
	
	public void setHomePanel(HomePanel homePanel){
		this.mHomePanel = homePanel;
	}
	
	public void releaseViews(){
		mHomeWebView = null;
		mWebView.stopLoading();
		mWebView.clearView();
		mWebView = null;
		//关闭dialog
		if(customLoadingDialog != null){
			customLoadingDialog.dismiss();
			customLoadingDialog.cancel();
			customLoadingDialog = null;
		}
	}
	
	/**
	 * 用户登录(Token)
	 * 
	 * @param token url返回标识
	 * @param cp_id 合作者唯一编号,由冰雪游戏 网游戏生成并提供
	 * @param game_id 联运游戏编号
	 * @param server_id 联运服务器编号:用户验证后需要跳转的服务器编号没有为0
	 */
	private void accountLoginByToken(int cp_id, int game_id, int server_id, 
		 String token) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("cp_id", String.valueOf(cp_id)));
		parameter.add(new RequestParameter("game_id", String.valueOf(game_id)));
		parameter.add(new RequestParameter("server_id", String.valueOf(server_id)));
		parameter.add(new RequestParameter("token", token));

		startHttpRequst(BFGameConfig.HTTP_GET,
				BFGameConfig.BX_SERVICE_URL_ACCOUNT_LOGIN_BYTOKEN, parameter, false, "",
				false, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, REQUEST_LOGIN_KEY);
	}
	
	@Override
	public void onCallBackFromThread(String resultJson, int resultCode) {
		// TODO Auto-generated method stub
		super.onCallBackFromThread(resultJson, resultCode);
		try{
			switch (resultCode) {
			case REQUEST_LOGIN_KEY: 
				HashMap<String, Object> loginResult = ParseUtil
						.getLoginUserData(resultJson);
				if (loginResult != null) {
					int code = (Integer) loginResult.get("code");
					
					if (code==3) {
						Toast.makeText((BFMainActivity) mContext, "用户不存在", Toast.LENGTH_LONG).show();
					}
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
									BFGameConfig.ACCOUNT = user.getUsername();
									BFGameConfig.USER_NAME = user.getUsername();
									BFGameConfig.PASSWORD = "";
									mHomePanel.rememberAccount();
									((BFMainActivity) mContext).loginResult(OperateType.LOGIN, code);
								}
							} else {
								BFGameConfig.TICKET = user.getTicket();
								BFGameConfig.USER_ID = user.getUser_id();
								BFGameConfig.USER_NAME = BFGameConfig.ACCOUNT;
								((BFMainActivity) mContext).loginResult(OperateType.LOGIN,
										code);
							}

						}
					} else {
						((BFMainActivity) mContext).loginResult(OperateType.LOGIN, code);
					}

				} else {
					((BFMainActivity) mContext).loginResult(OperateType.LOGIN,
							BFSDKStatusCode.SYSTEM_ERROR);
				}
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
