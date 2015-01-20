package com.baofeng.game.sdk.vo;

public class BFPayTag {

	private String name;
	private int icon;
	/**
	 * 0支付宝
	 * 1银联
	 * 2手机充值卡(4联通 5移动 6电信)
	 * 3游戏点卡(7.骏网充值卡8.QQ币9.盛大点卡10.完美点卡11网易点卡)
	 */
	private int type;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
