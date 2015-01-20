/*
 * Copyright 2011 meiyitian
 * Blog  :http://www.cnblogs.com/meiyitian
 * Email :haoqqemail@qq.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baofeng.game.sdk.net;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import android.os.Handler;
import android.os.Message;
import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.ui.BFLoadingDialog;
import com.baofeng.game.sdk.util.ErrorUtil;
import com.baofeng.game.sdk.util.LogUtil;
import com.baofeng.game.sdk.util.MD5Util;
import com.baofeng.game.sdk.util.Util;

/**
 * 
 * 异步HTTPPOST请求
 * 
 * 线程的终止工作交给线程池，当activity停止的时候，设置回调函数为false ，就不会执行回调方法。
 * 
 * @author sailor
 * 
 */
public class AsyncHttpPost extends BaseRequest {
	private static final long serialVersionUID = 2L;
	DefaultHttpClient httpClient;
	List<RequestParameter> parameter = null;
	BFLoadingDialog customLoadingDialog;
	private int resultCode = -1;

	Handler resultHandler = new Handler() {
		public void handleMessage(Message msg) {
			String resultData = (String) msg.obj;
			if (!resultData.contains("ERROR.HTTP.008")) {
				ThreadCallBack callBack = (ThreadCallBack) msg.getData()
						.getSerializable("callback");
				if (resultCode == -1)
					callBack.onCallbackFromThread(resultData);
				LogUtil.d("BXSDK", "result : " + resultData);
				callBack.onCallBackFromThread(resultData, resultCode);
			}

		}
	};
	ThreadCallBack callBack;

	public AsyncHttpPost(ThreadCallBack callBack, String url,
			List<RequestParameter> parameter, boolean isShowLoadingDialog,
			String loadingCode, boolean isHideCloseBtn, int resultCode) {
		this.callBack = callBack;
		this.resultCode = resultCode;
		if (isShowLoadingDialog) {
			customLoadingDialog = new BFLoadingDialog(callBack.getContext(),
					"加载中…请稍后…", isHideCloseBtn);
			if (customLoadingDialog != null && !customLoadingDialog.isShowing()) {
				customLoadingDialog.show();
			}
		}
		this.url = url;
		this.parameter = parameter;
		if (httpClient == null)
			httpClient = new DefaultHttpClient();
	}

	public AsyncHttpPost(ThreadCallBack callBack, String url,
			List<RequestParameter> parameter, boolean isShowLoadingDialog,
			int connectTimeout, int readTimeout) {
		this(callBack, url, parameter, isShowLoadingDialog, "", false, -1);
		if (connectTimeout > 0) {
			this.connectTimeout = connectTimeout;
		}
		if (readTimeout > 0) {
			this.readTimeout = readTimeout;
		}
	}

	public AsyncHttpPost(ThreadCallBack callBack, String url,
			List<RequestParameter> parameter, boolean isShowLoadingDialog,
			String loadingDialogContent, boolean isHideCloseBtn,
			int connectTimeout, int readTimeout, int resultCode) {
		this(callBack, url, parameter, isShowLoadingDialog,
				loadingDialogContent, isHideCloseBtn, resultCode);
		if (connectTimeout > 0) {
			this.connectTimeout = connectTimeout;
		}
		if (readTimeout > 0) {
			this.readTimeout = readTimeout;
		}
	}

	@Override
	public void run() {
		String ret = "";
		try {
			for (int i = 0; i < BFGameConfig.CONNECTION_COUNT; i++) {
				try {
					request = new HttpPost(url);

					request.addHeader("Accept-Encoding", "default");

					if (parameter != null && parameter.size() > 0) {
						List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();

						
						for (RequestParameter p : parameter) {

							list.add(new BasicNameValuePair(Util.encode(p
									.getName()), Util.encode(p.getValue())));
							LogUtil.d("AsyncHttpPost Param ", p.getName()
									+ " , " + p.getValue());

						}
						StringBuffer sb = new StringBuffer();
						for (int j = 0; j < list.size(); j++) {
							sb.append(list.get(j));
							if (!(j == list.size() - 1)) {
								sb.append("&");
							}
						}

						BasicNameValuePair bn = new BasicNameValuePair("sign",
								MD5Util.MD5(sb + "1234"
										+ BFGameConfig.SERVERKEY));
//						System.out.println("@@@" +  sb + "1234"
//								+ BFGameConfig.SERVERKEY);
						list.add(bn);
						((HttpPost) request)
								.setEntity(new UrlEncodedFormEntity(list,
										HTTP.UTF_8));

					}
					LogUtil.d("AsyncHttpPost : ", url);

					httpClient.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT,
							connectTimeout);
					httpClient.getParams().setParameter(
							CoreConnectionPNames.SO_TIMEOUT, readTimeout);
					HttpResponse response = httpClient.execute(request);
					int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode == HttpStatus.SC_OK) {
						// HttpManager.saveCookies(response);
						InputStream is = response.getEntity().getContent();
						BufferedInputStream bis = new BufferedInputStream(is);
						bis.mark(2);
						// 取前两个字节
						byte[] header = new byte[2];
						int result = bis.read(header);
						// reset输入流到开始位置
						bis.reset();
						// 判断是否是GZIP格式
						int headerData = getShort(header);
						// Gzip 流 的前两个字节是 0x1f8b
						if (result != -1 && headerData == 0x1f8b) {
							is = new GZIPInputStream(bis);
						} else {
							is = bis;
						}
						InputStreamReader reader = new InputStreamReader(is,
								"utf-8");
						char[] data = new char[100];
						int readSize;
						StringBuffer sb = new StringBuffer();
						while ((readSize = reader.read(data)) > 0) {
							sb.append(data, 0, readSize);
						}
						ret = sb.toString();
						bis.close();
						reader.close();

					} else {
						RequestException exception = new RequestException(
								RequestException.IO_EXCEPTION, "响应码异常,响应码："
										+ statusCode);
						ret = ErrorUtil.errorJson("-1", exception.getMessage());
					}

					break;
				} catch (Exception e) {
					if (i == BFGameConfig.CONNECTION_COUNT - 1) {
						RequestException exception = new RequestException(
								RequestException.IO_EXCEPTION, "网络连接超时");
						ret = ErrorUtil.errorJson("-1", exception.getMessage());
					} else {
						LogUtil.d("connection url", "连接超时" + i);
						continue;
					}
				}
			}
		} catch (java.lang.IllegalArgumentException e) {
			RequestException exception = new RequestException(
					RequestException.IO_EXCEPTION, BFGameConfig.ERROR_MESSAGE);
			ret = ErrorUtil.errorJson("-2", exception.getMessage());
		} finally {
			if (!BFGameConfig.IS_STOP_REQUEST) {
				Message msg = new Message();
				msg.obj = ret;
				msg.getData().putSerializable("callback", callBack);
				resultHandler.sendMessage(msg);
			}
			if (customLoadingDialog != null && customLoadingDialog.isShowing()) {
				customLoadingDialog.dismiss();
				customLoadingDialog = null;
			}
		}
		super.run();
	}

	private int getShort(byte[] data) {
		return (int) ((data[0] << 8) | data[1] & 0xFF);
	}
}
