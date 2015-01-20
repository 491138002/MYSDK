package com.baofeng.game.sdk.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baofeng.game.sdk.type.BFSDKStatusCode;
import com.baofeng.game.sdk.vo.BFGame;
import com.baofeng.game.sdk.vo.BFGameCommon;
import com.baofeng.game.sdk.vo.BFPayResult;
import com.baofeng.game.sdk.vo.BFUser;

public class ParseUtil {

	public static HashMap<String, Object> getLoginUserData(String jsonData) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			JSONObject obj = new JSONObject(jsonData);
			if (!obj.isNull("timestamp")) {
				resultMap.put("timestamp", obj.getString("timestamp"));
			}
			if (!obj.isNull("code")) {
				resultMap.put("code", obj.getInt("code"));
			}
			if (!obj.isNull("msg")) {
				resultMap.put("msg", obj.getString("msg"));
			}
			
			if(!obj.isNull("code")){
				int result = obj.getInt("code");
				if(result == BFSDKStatusCode.SUCCESS){
					if(!obj.isNull("data")){
						BFUser user = new BFUser();
						JSONObject userObject = obj.getJSONObject("data");
						if (userObject != null) {
							if (!userObject.isNull("user_id")) {
								user.setUser_id(userObject.getInt("user_id"));
							}
							if (!userObject.isNull("nick_name")) {
								user.setNick_name(userObject.getString("nick_name"));
							}
							if (!userObject.isNull("adult")) {
								user.setAdult(userObject.getInt("adult"));
							}
							if (!userObject.isNull("ticket")) {
								user.setTicket(userObject.getString("ticket"));
							}
							if (!userObject.isNull("quick_game")) {
								user.setQuick_game(userObject.getInt("quick_game"));
							}
							if(!userObject.isNull("mobilestate")){
								user.setSMS(userObject.getInt("mobilestate"));
							}
							if(!userObject.isNull("username")){
								user.setUsername(userObject.getString("username"));
							}
						}
						resultMap.put("data", user);
					}
				}
			}
			return resultMap;
		} catch (JSONException e) {
			e.printStackTrace();
			return resultMap;
		}
//		return null;
	}
	
	public static HashMap<String, Object> getGameVersionData(String jsonData) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			JSONObject obj = new JSONObject(jsonData);
			if (!obj.isNull("timestamp")) {
				resultMap.put("timestamp", obj.getString("timestamp"));
			}
			if (!obj.isNull("code")) {
				resultMap.put("code", obj.getInt("code"));
			}
			if (!obj.isNull("msg")) {
				resultMap.put("msg", obj.getString("msg"));
			}
			
			if(!obj.isNull("code")){
				int result = obj.getInt("code");
				if(result == BFSDKStatusCode.SUCCESS){
					if (!obj.isNull("downloadurl")) {
						resultMap.put("downloadurl", obj.getString("downloadurl"));
					}
					
					if (!obj.isNull("version")) {
						resultMap.put("version", obj.getString("version"));
					}
				}
			}
			return resultMap;
		} catch (JSONException e) {
			e.printStackTrace();
			return resultMap;
		}
//		return null;
	}

	public static HashMap<String, Object> getGameListData(String jsonData) {
		try {
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			JSONObject obj = new JSONObject(jsonData);
			if (!obj.isNull("timestamp")) {
				resultMap.put("timestamp", obj.getString("timestamp"));
			}
			if (!obj.isNull("code")) {
				resultMap.put("code", obj.getInt("code"));
			}
			if (!obj.isNull("msg")) {
				resultMap.put("msg", obj.getString("msg"));
			}
			if (!obj.isNull("bbs_url")) {
				resultMap.put("bbs_url", obj.getString("bbs_url"));
			}
			if (!obj.isNull("main_url")) {
				resultMap.put("main_url", obj.getString("main_url"));
			}
			if (!obj.isNull("data")) {
				ArrayList<BFGame> gameList = new ArrayList<BFGame>();
				JSONArray resultObj = obj.getJSONArray("data");
				
				for (int i = 0; i < resultObj.length(); i++) {
					JSONObject gameObj = resultObj.getJSONObject(i);
					BFGame game = getGameByJsonObject(gameObj);
					if (game != null)
						gameList.add(game);
				}
				
				resultMap.put("data", gameList);
			}

			return resultMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	private static BFGame getGameByJsonObject(JSONObject object) {
		if (object == null)
			return null;
		try {
			BFGame game = new BFGame();
			if (!object.isNull("game_id")) {
				game.setId(object.getInt("game_id"));
			}
			if (!object.isNull("game_name")) {
				game.setName(new String(object.getString("game_name")
						.getBytes("utf-8")));
			}
			if (!object.isNull("apk_download_url")) {
				game.setApkDownloadUrl(object.getString("apk_download_url"));
			}
			if (!object.isNull("image_download_url")) {
				game.setImageDownloadUrl(object.getString("image_download_url"));
			}
			return game;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public static BFGameCommon getCommData(String jsonData) {
		try {

			JSONObject object = new JSONObject(jsonData);
			
			BFGameCommon result = new BFGameCommon();
			
			if (!object.isNull("GLOBAL")) {
				JSONObject data = object.getJSONObject("GLOBAL");
				if (!data.isNull("version")) {
					result.setVersion(data.getString("version"));
				}
				
			}
			if (!object.isNull("SERVICE")) {
				JSONObject data = object.getJSONObject("SERVICE");
				if (!data.isNull("qq")) {
					result.setQq(data.getString("qq"));
				}
				if (!data.isNull("phone")) {
					result.setPhone(data.getString("phone"));
				}
				if (!data.isNull("qqgroup")) {
					result.setQqGroup(data.getString("qqgroup"));
				}
			}
			if (!object.isNull("MODULE")) {
				JSONObject data = object.getJSONObject("MODULE");
				if (!data.isNull("game_card_open")) {
					result.setGame_card_open(data.getString("game_card_open"));
				}
				
			}
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
	public static HashMap<String, Object> getData(String jsonData) {
		try {
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			JSONObject obj = new JSONObject(jsonData);
			if (!obj.isNull("timestamp")) {
				resultMap.put("timestamp", obj.getInt("timestamp"));
			}
			if (!obj.isNull("code")) {
				resultMap.put("code", obj.getInt("code"));
			}
			if (!obj.isNull("msg")) {
				resultMap.put("msg", obj.getString("msg"));
			}
			
			if (!obj.isNull("data")) {
				resultMap.put("data", obj.getInt("data"));
			}
			return resultMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * 解析支付返回结果
	 * @param object
	 * @return
	 */
	public static BFPayResult getPayResult(String jsonData) {
		try {
			JSONObject object = new JSONObject(jsonData);
			
			BFPayResult result = new BFPayResult();
			if (!object.isNull("timestamp")) {
				result.setTimestamp(object.getLong("timestamp"));
			}
			if (!object.isNull("code")) {
				result.setCode(object.getInt("code"));
			}
			if (!object.isNull("msg")) {
				result.setMsg(object.getString("msg"));
			}
			if(!object.isNull("data")){
				JSONObject data = object.getJSONObject("data");
				if (!data.isNull("order_id")) {
					result.setOrderId(data.getString("order_id"));
				}
				if (!data.isNull("tn")) {
					result.setTn(data.getString("tn"));
				}
				if (!data.isNull("notify_url")) {
					result.setNotifyUrl(data.getString("notify_url"));
				}
			}
			
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 解析支付查询接口结果
	 * @param object
	 * @return
	 */
	public static BFPayResult getPayResultQuery(String jsonData) {
		try {
			JSONObject object = new JSONObject(jsonData);
			
			BFPayResult result = new BFPayResult();
			if (!object.isNull("timestamp")) {
				result.setTimestamp(object.getLong("timestamp"));
			}
			if (!object.isNull("code")) {
				result.setCode(object.getInt("code"));
			}
			if (!object.isNull("msg")) {
				result.setMsg(object.getString("msg"));
			}
			if (!object.isNull("orderId")) {
				result.setOrderId(object.getString("orderId"));
			}
			
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static HashMap<String, Object> getVersionData(String jsonData) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			JSONObject obj = new JSONObject(jsonData);
			if (!obj.isNull("code")) {
				resultMap.put("status", obj.getString("code"));
			}

			if (!obj.isNull("version")) {
				resultMap.put("version", obj.getInt("version"));
			}
			
			if (!obj.isNull("downloadurl")) {
				resultMap.put("downloadurl", obj.getString("downloadurl"));
			}
			
			if (!obj.isNull("forcedown")) {
				resultMap.put("forcedown", obj.getInt("forcedown"));
			}
			return resultMap;
		} catch (JSONException e) {
			e.printStackTrace();
			return resultMap;
		}
	}
	
}
