package com.baofeng.game.sdk.user.home;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.net.RequestParameter;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.user.BFBasePanel;
import com.baofeng.game.sdk.user.HomePanel;
import com.baofeng.game.sdk.util.StringUtil;

public class BFChangeBindPhoneAgainView extends BFBasePanel{
	private static BFChangeBindPhoneAgainView mBXChangeBindPhoneAgain;
	
	private HomePanel mHomePanel;
	private View mBaseView;
	
	private TextView mAccountPhoneNumberTv;
	private EditText mAccountPasswordEt;
	private Button mNextBtn;
//	private Button mBackBtn;
	
	private final int REQUEST_CHECK_PASSWORD_CODE = 10001;
	
	public synchronized static BFChangeBindPhoneAgainView getInstance(Context context){
		if(mBXChangeBindPhoneAgain == null){
			mBXChangeBindPhoneAgain = new BFChangeBindPhoneAgainView(context);
		}
		return mBXChangeBindPhoneAgain;
	}
	
	private BFChangeBindPhoneAgainView(Context context) {
		super(context);
		
		init();
		initView();
		initListener();
		initValue();
	}
	
	public void init() {
		mBaseView = mInflater.inflate(BFResources.layout.bx_change_bind_phone, null);
	}

	public void initView() {
		mAccountPhoneNumberTv = (TextView) mBaseView.findViewById(BFResources.id.bx_phone_number);
		mAccountPasswordEt = (EditText) mBaseView.findViewById(BFResources.id.bx_change_bind_phone_password);
		mNextBtn = (Button) mBaseView.findViewById(BFResources.id.bx_send_commit);
//		mBackBtn = (Button) mBaseView.findViewById(BFResources.id.bx_back);
	}

	public void initListener() {
		mNextBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(StringUtil.isEmpty(mAccountPasswordEt.getText().toString())){
					showToast("密码不能为空");
				}else if(mAccountPasswordEt.getText().toString().length() < 6){
					showToast("请输入6位以上数字与字母密码");
				}else{
					checkAccountInfo(
							BFGameConfig.GAMEPARAM.getCpId(),
							BFGameConfig.ACCOUNT, 
							mAccountPasswordEt.getText().toString(),
							BFGameConfig.GAMEPARAM.getGameId(),
							BFGameConfig.GAMEPARAM.getServerId());
				}
			}
		});
		
//		mBackBtn.setOnClickListener(new OnClickListener() {
//			public void onClick(View arg0) {
//				mHomePanel.showViewType(HomePanel.SHOW_ACCOUNT_INFO);
//			}
//		});
	}

	public void initValue() {
		if(mHomePanel != null)
			mAccountPhoneNumberTv.setText(mHomePanel.phone_Number);
	}

	public View getView(){
		return mBaseView;
	}
	
	public void setHomePanel(HomePanel homePanel){
		this.mHomePanel = homePanel;
	}
	
	public static void releaseViews(){
		mBXChangeBindPhoneAgain = null;
	}
	
	/**
	 * 验证用户
	 * 
	 * @param cp_id 合作者唯一编号,由冰雪游戏 网游戏生成并提供
	 * @param account 冰雪游戏 网账号
	 * @param password 冰雪游戏 网密码
	 * @param game_id 联运游戏编号
	 * @param server_id 联运服务器编号:用户验证后需要跳转的服务器编号没有为0
	 */
	private void checkAccountInfo( int cp_id, String account,
			String password, int game_id, int server_id) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("account", account));
		parameter.add(new RequestParameter("password", password));
		parameter.add(new RequestParameter("cp_id", String.valueOf(cp_id)));
		parameter.add(new RequestParameter("game_id", String.valueOf(game_id)));
		parameter.add(new RequestParameter("server_id", String.valueOf(server_id)));

		startHttpRequst(BFGameConfig.HTTP_GET,
				BFGameConfig.BX_SERVICE_URL_ACCOUNT_INFO, parameter, true, "",
				false, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, REQUEST_CHECK_PASSWORD_CODE);
	}
	
	@Override
	public void onCallBackFromThread(String resultJson, int resultCode) {
		// TODO Auto-generated method stub
		super.onCallBackFromThread(resultJson, resultCode);
		try{
			switch(resultCode){
			case REQUEST_CHECK_PASSWORD_CODE:
				int code = 0;
				JSONObject resultObject = new JSONObject(resultJson);
				if (!resultObject.isNull("code")) {
					code = resultObject.getInt("code");
				}
				if (code == BFSDKStatusCode.SUCCESS) {
					mHomePanel.showViewType(HomePanel.SHOW_BIND_PHONE);
				} else {
					showToast("密码错误");
				}
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
