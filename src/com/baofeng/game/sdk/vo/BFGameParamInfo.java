package com.baofeng.game.sdk.vo;

import com.baofeng.game.sdk.config.BFGameConfig;
import com.baofeng.game.sdk.type.BFOrientation;

public class BFGameParamInfo {

	/** 游戏合作商ID **/
	private int cp_id;

	/** 游戏合作商秘钥 **/
	private String cp_key;

	/** 游戏ID **/
	private int game_id;

	/** 游戏服务器（游戏分区）ID **/
	private int server_id;

	/** 渠道编号 **/
	private int channel_id;

	/** 是否显示短信支付 **/
	private boolean is_hasSMS = false;

	public int getCpId() {
		return cp_id;
	}

	public void setCpId(int cp_id) {
		this.cp_id = cp_id;
	}

	public int getGameId() {
		return game_id;
	}

	public void setGameId(int game_id) {
		this.game_id = game_id;
	}

	public int getServerId() {
		return server_id;
	}

	public void setServerId(int server_id) {
		this.server_id = server_id;
	}

	public int getChannelId() {
		return channel_id;
	}

	public void setChannelId(int channel_id) {
		this.channel_id = channel_id;
	}

	public String getCpKey() {
		return cp_key;
	}

	public void setCpKey(String cp_key) {
		this.cp_key = cp_key;
	}

	public boolean getSMS() {
		return is_hasSMS;
	}

	public void setSMS(boolean is_hasSMS) {
		this.is_hasSMS = is_hasSMS;
	}

	/**
	 * 是否是快速注册
	 * 
	 * @param isFastRegister
	 */
	public void setIsFastRegister(boolean isFastRegister) {
		BFGameConfig.ISFASTREGISTER = isFastRegister;
	}

	/**
	 * 是否是竖屏显示
	 * 
	 * @param orientation
	 */
	public void setScreenOrientation(BFOrientation orientation) {
		BFGameConfig.SCREEN_ORIENTATION = orientation;
	}
}
