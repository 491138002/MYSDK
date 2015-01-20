package com.baofeng.game.sdk.vo;

public class BFUser {
	private int user_id;// 冰雪游戏 账号 用户唯一编号
	private int adult;// 用户成年标识 1=成年 0=未成年
	private String ticket;// 用户登录成功的票据 用于服务器端验证用户合法性
	private String nick_name;// 用户昵称
	private int quick_game;//
	private String username = "";//
	private int isSMS;
	
	
	
	public int getUser_id() {    
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getAdult() {
		return adult;
	}
	public void setAdult(int adult) {
		this.adult = adult;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public int getQuick_game() {
		return quick_game;
	}
	public void setQuick_game(int quick_game) {
		this.quick_game = quick_game;
	}
	public int getSMS() {
		return isSMS;
	}
	public void setSMS(int isSMS) {
		this.isSMS = isSMS;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}