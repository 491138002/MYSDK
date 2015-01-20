package com.baofeng.game.sdk.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class AccountPersistenceUtil {

	private static final String ENCRYPT_KEY = "0123456789ABCDEF";

	/**
	 * 将账户集存入文件中 存入文件的时候对数据进行加密
	 * 
	 * @param accounts
	 */
	public static void writerAccountToFile(final String fileName,
			final ArrayList<HashMap<String, String>> accounts) {
		if (accounts == null || accounts.size() == 0)
			return;
		new Thread(new Runnable() {

			@Override
			public void run() {
				String cleartext = "";// 明文
				String encrypted = "";// 密文
				FileWriter writer = null;
				BufferedWriter bufferWriter = null;

				JSONArray array = new JSONArray();
				int lenght = accounts.size();
				for(int i=lenght-1;i>=0;i--){
					HashMap<String, String> account = accounts.get(i);
					JSONObject object = new JSONObject(account);
					array.put(object);
				}
//				for (HashMap<String, String> account : accounts) {
//					JSONObject object = new JSONObject(account);
//					array.put(object);
//				}
				cleartext = array.toString();
				try {
					File file = new File(fileName);
					if (file.exists())
						file.delete();
					file.createNewFile();
					encrypted = EncryptUtil.encrypt(ENCRYPT_KEY, cleartext);
					writer = new FileWriter(file);
					bufferWriter = new BufferedWriter(writer);
					bufferWriter.write(encrypted);
					bufferWriter.flush();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (bufferWriter != null) {
							bufferWriter.close();
						}
						if (writer != null) {
							writer.close();
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		}).start();

	}

	/**
	 * 异步读取账户文件
	 * @param fileName
	 * @param listener
	 */
	public static void readAccountByFile(final String fileName,
			final OnAccountFileReadComplete listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<HashMap<String, String>> accounts = readAccountByFile(fileName);
				if (listener != null)
					listener.onFileReadCompleteListener(accounts);
			}
		}).start();
	}

	public interface OnAccountFileReadComplete {
		public void onFileReadCompleteListener(
				ArrayList<HashMap<String, String>> accounts);
	};

	/**
	 * 从文件中读取账户信息 读取的时候要先对读取到的字符进行解密
	 * 
	 * @return
	 */
	private static ArrayList<HashMap<String, String>> readAccountByFile(
			String fileName) {
		String cleartext = "";// 明文
		String encrypted = "";// 密文
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		ArrayList<HashMap<String, String>> accounts = new ArrayList<HashMap<String, String>>();
		StringBuffer encryptBuffer = new StringBuffer();
		try {
			fileReader = new FileReader(new File(fileName));
			bufferedReader = new BufferedReader(fileReader);
			String line = bufferedReader.readLine();
			while (line != null) {
				encryptBuffer.append(line);
				line = bufferedReader.readLine();
			}
			encrypted = encryptBuffer.toString();
			if (!TextUtils.isEmpty(encrypted)) {
				cleartext = EncryptUtil.decrypt(ENCRYPT_KEY, encrypted);
				try {
					JSONArray array = new JSONArray(cleartext);
					if (array != null) {
						for (int i = array.length() - 1; i >= 0; i--) {
							JSONObject object = array.getJSONObject(i);
							HashMap<String, String> account = new HashMap<String, String>();
							account.put("name", object.getString("name"));
							account.put("password", object.getString("password"));
							if(!object.isNull("userType"))
								account.put("userType", object.getString("userType"));
							if(!object.isNull("token"))
								account.put("token", object.getString("token"));
							if(!object.isNull("user_id"))
								account.put("user_id", object.getString("user_id"));
							accounts.add(account);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return accounts;
	}
}
