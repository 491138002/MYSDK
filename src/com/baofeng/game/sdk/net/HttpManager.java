package com.baofeng.game.sdk.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.baofeng.game.sdk.util.ErrorUtil;
import com.baofeng.game.sdk.util.Util;

/**
 * 【http请求类】
 * 
 * @ClassName:HttpManager
 * @Description:TODO
 * @author sailor
 * @date 2012-3-8上午10:12:26
 * 
 */
@SuppressWarnings("deprecation")
public class HttpManager {
	private static final int timeoutConnection = 5000; // 连接超时
	private static final int timeoutSocket = 10000; //

	// 静态初始化 httpclient
	// static {
	// if(httpclient == null){
	// HttpParams httpParameters = new BasicHttpParams();
	// HttpConnectionParams.setConnectionTimeout(httpParameters,
	// timeoutConnection);
	// HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	// httpclient = new DefaultHttpClient(httpParameters);
	// }
	// }

	/**
	 * 【httpget请求类】
	 * 
	 * @Title :doGet
	 * @Description :TODO
	 * @params @param reqUrl
	 * @params @return
	 * @return String
	 * @throws
	 * 
	 */
	public static String doGet(String url, List<RequestParameter> parameter,
			Context context) {
		String ret = "";
		try {
			if (parameter != null && parameter.size() > 0) {
				StringBuilder bulider = new StringBuilder();
				for (RequestParameter p : parameter) {
					if (bulider.length() != 0) {
						bulider.append("&");
					}

					bulider.append(Util.encode(p.getName()));
					bulider.append("=");
					bulider.append(Util.encode(p.getValue()));
				}
				url += "?" + bulider.toString();
			}
			HttpGet request = new HttpGet(url);
//			addCookies(request);
//			if (Constants.isGzip) {
//				request.addHeader("Accept-Encoding", "gzip");
//			} else {
				request.addHeader("Accept-Encoding", "default");
//			}

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpResponse response = new DefaultHttpClient(httpParameters)
					.execute(request);
			
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
//				saveCookies(response);//保存cookie
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
				InputStreamReader reader = new InputStreamReader(is, "utf-8");
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
				ret = ErrorUtil.errorJson("ERROR.HTTP.001",
						exception.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 【http post请求】
	 * 
	 * @Title :doPost
	 * @Description :TODO
	 * @params @param reqUrl
	 * @params @param hashMap
	 * @params @return
	 * @return String
	 * @throws
	 * 
	 */
	public static String doPost(String reqUrl, HashMap<String, String> hashMap) {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		String result = null;
		HttpPost httppost = null;
		InputStream content = null;
		try {
			httppost = new HttpPost(reqUrl);
//			addCookies(httppost);
			if (hashMap != null) {
				Set<String> keys = hashMap.keySet();
				for (Iterator<String> i = keys.iterator(); i.hasNext();) {
					String key = (String) i.next();
					String value = (String) hashMap.get(key);
					if (key == null || key.trim().length() == 0
							|| value == null || value.trim().length() == 0) {
						continue;
					}
					pairs.add(new BasicNameValuePair(key, value));
				}
			}

			int responseCode = 0;

			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,
					HTTP.UTF_8);

			/** 将POST数据放入http请求 */
			httppost.setEntity(p_entity);

			/** 发出实际的HTTP POST请求 */
			HttpResponse response = null;
			// LogUtil.printInfo("post 之前:");

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			response = new DefaultHttpClient(httpParameters).execute(httppost);
			// LogUtil.printInfo("post 之后:");
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == HttpStatus.SC_OK) {
//				saveCookies(response);//保存cookie
				HttpEntity entity = response.getEntity();
				content = entity.getContent();
				result = convertStreamToString(content);
			} else {
				return null;
			}

		} catch (Exception uee) {
			uee.printStackTrace();
			return null;
		} finally {
			try {
				if (httppost != null) {
					httppost.abort();
				}
				if (content != null) {
					content.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		// LogUtil.printInfo(result);
		return result;
	}

	/**
	 * 【将流转换成字符串】
	 * 
	 * @Title :convertStreamToString
	 * @Description :TODO
	 * @params @param is
	 * @params @return
	 * @return String
	 * @throws
	 * 
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 检查网络状态
	 * 
	 * @author huangyu
	 * 
	 */
	public static boolean checkNetWork(Context context) {
		// 判断网络是否可用，如果不可用，给出提示
		boolean isAvailable = netWorkIsAvailable(context);
		if (!isAvailable) {// 如果不可用
			openDialog(context);
			return false;
		}
		return true;
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @Title :netWorkIsAvailable
	 * @Description :TODO
	 * @params @param context
	 * @params @return
	 * @return boolean
	 * @throws
	 * 
	 */
	public static boolean netWorkIsAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null) {
			if (activeNetInfo.isAvailable()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 弹出网络不可用提示框
	 * 
	 * @Title :openDialog
	 * @Description :TODO
	 * @params @param context
	 * @return void
	 * @throws
	 * 
	 */
	private static void openDialog(final Context context) {
		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("没有可用的网络");
		builder.setMessage("请开启GPRS或WIFI网络连接");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent mIntent = new Intent("/");
				ComponentName comp = new ComponentName("com.android.settings",
						"com.android.settings.WirelessSettings");
				mIntent.setComponent(comp);
				mIntent.setAction("android.intent.action.VIEW");
				context.startActivity(mIntent);

			}
		}).setNeutralButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		}).create().show();

	}

	private static int getShort(byte[] data) {
		return (int) ((data[0] << 8) | data[1] & 0xFF);
	}
	
//	/**
//	 * 保存Cookie
//	 * 
//	 * @param resp
//	 */
//	public static void saveCookies(HttpResponse httpResponse) {
//		Header[] headers = httpResponse.getHeaders("Set-Cookie");
//		StringBuilder sb = new StringBuilder();
//		if (headers == null || headers.length == 0)
//			return;
//		for (int i = 0; i < headers.length; i++) {
//			String cookie = headers[i].getValue();
//			String[] cookievalues = cookie.split(";");
//			for (int j = 0; j < cookievalues.length; j++) {
//				String[] keyPair = cookievalues[j].split("=");
//				String key = keyPair[0].trim();
//				String value = keyPair.length > 1 ? keyPair[1].trim() : "";
//				
//				sb.append(key);
//				sb.append("=");
//				sb.append(value);
//				sb.append(";");
//			}
//		}
//		BaseActivity.preferencesUtils.putString(Preferences.COOKIE, sb.toString());
//	}
	
//	/**
//	 * 增加Cookie
//	 * 
//	 * @param request
//	 */
//	public static void addCookies(HttpUriRequest request) {
//		request.addHeader(Preferences.COOKIE, BaseActivity.preferencesUtils.getString(Preferences.COOKIE, ""));
//	}
}
