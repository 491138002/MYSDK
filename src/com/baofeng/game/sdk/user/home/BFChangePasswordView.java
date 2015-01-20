package com.baofeng.game.sdk.user.home;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.baofeng.game.sdk.BFResources;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.net.RequestParameter;
import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.user.BFBasePanel;
import com.baofeng.game.sdk.user.HomePanel;

public class BFChangePasswordView extends BFBasePanel {

	private final int CHANGE_PASSWORD_KEY = 1001;
	
	private static BFChangePasswordView mBXChangePasswordView;

	private HomePanel mHomePanel;
	private View mBaseView;
	
	private EditText mChangeOldPswInput;
	private EditText mChangeNewPswInput;
	private EditText mChangeNewPswConfirmInput;
	private Button mChangeBackBtn;
	private Button mChangeCommitBtn;
	
	private String mOldPassword;
	private String mNewPassword;
	private String mNewPasswordConfirm;
	
	public synchronized static BFChangePasswordView getInstance(Context context){
		if(mBXChangePasswordView == null){
			mBXChangePasswordView = new BFChangePasswordView(context);
		}
		return mBXChangePasswordView;
	}
	
	public BFChangePasswordView(Context context) {
		super(context);
		
		init();
		initView();
		initListener();
		initValue();
	}
	
	public void init() {
		mBaseView = mInflater.inflate(BFResources.layout.bx_change_password, null);
	}

	public void initView() {
		mChangeOldPswInput = (EditText) mBaseView.findViewById(BFResources.id.bx_change_password_old_psw);
		mChangeNewPswInput = (EditText) mBaseView.findViewById(BFResources.id.bx_change_password_new_psw);
		mChangeNewPswConfirmInput = (EditText) mBaseView.findViewById(BFResources.id.bx_change_password_new_confirm);
		mChangeBackBtn = (Button) mBaseView.findViewById(BFResources.id.bx_back);
		mChangeCommitBtn = (Button) mBaseView.findViewById(BFResources.id.bx_change_password_commit);
	}

	public void initListener() {
		mChangeBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mHomePanel.showViewType(HomePanel.SHOW_ACCOUNT_INFO);
				BFAccountInfoView.getInstance(mContext).initValue();
			}
		});
		mChangeCommitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mOldPassword = mChangeOldPswInput.getText().toString();
				mNewPassword = mChangeNewPswInput.getText().toString();
				mNewPasswordConfirm = mChangeNewPswConfirmInput.getText()
						.toString();
				if (mOldPassword == null || mNewPassword == null
						|| "".equals(mOldPassword) || "".equals(mNewPassword)
						|| mNewPasswordConfirm == null
						|| "".equals(mNewPasswordConfirm)
						|| !isAllCharacter(mNewPassword)) {
					showToast("输入密码格式有误，请重新输入！");
					return;
				}

				if (mNewPassword.length() < 6) {
					showToast("请输入不少于6位密码");
					return;
				}

				if (!mNewPasswordConfirm.equals(mNewPassword)) {
					showToast("两次输入密码不一致，请重新输入！");
					return;
				}

				if (mOldPassword.equals(mNewPassword)) {
					showToast("新密码与旧密码相同，请重新输入！");
					return;
				}

				changePassword(BFGameConfig.ACCOUNT, mOldPassword, mNewPassword);

			}
		});
	}

	public void initValue() {
	}

	public View getView(){
		return mBaseView;
	}
	
	public void setHomePanel(HomePanel homePanel){
		this.mHomePanel = homePanel;
	}

	/**
	 * 修改密码 调用的前提是用户已经登录
	 * 
	 * @param user_id
	 *            用户ID
	 * @param oldPassword
	 *            原始密码
	 * @param newPassword
	 *            新密码
	 */
	private void changePassword(String username, String oldPassword,
			String newPassword) {
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("user_id", String.valueOf(BFGameConfig.USER_ID)));
		parameter.add(new RequestParameter("original_pwd", oldPassword));
		parameter.add(new RequestParameter("new_pwd", newPassword));
		startHttpRequst(BFGameConfig.HTTP_POST,
				BFGameConfig.BX_SERVICE_URL_CHANGE_PASSWORD, parameter, true,
				"", true, BFGameConfig.CONNECTION_SHORT_TIMEOUT,
				BFGameConfig.READ_MIDDLE_TIMEOUT, CHANGE_PASSWORD_KEY);
	}
	
	@Override
	public void onCallBackFromThread(String resultJson, int resultCode) {
		// TODO Auto-generated method stub
		super.onCallBackFromThread(resultJson, resultCode);
		try {
			int code = 0;
			String msg = "";
			if (!TextUtils.isEmpty(resultJson)) {
				JSONObject resultObject = new JSONObject(resultJson);
				System.out.println("================resultObject============="
						+ resultObject);
				if (!resultObject.isNull("code")) {
					code = resultObject.getInt("code");
				}
				if (!resultObject.isNull("msg")) {
					msg = resultObject.getString("msg");
				}

				switch (resultCode) {
				case CHANGE_PASSWORD_KEY:
					if (code == BFSDKStatusCode.SUCCESS) {
						showToast("密码修改成功");
						BFGameConfig.PASSWORD = mNewPassword;
						mHomePanel.changeLocationAccountFile();
						mHomePanel.showViewType(HomePanel.SHOW_ACCOUNT_INFO);
						BFAccountInfoView.getInstance(mContext).initValue();
					} else {
						if (code == BFSDKStatusCode.SYSTEM_ERROR){
							showToast("原始密码不正确");
						}else {
							showToast(code);
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void releaseViews(){
		mBXChangePasswordView = null;
	}
}
