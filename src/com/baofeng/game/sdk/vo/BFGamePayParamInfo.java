package com.baofeng.game.sdk.vo;

public class BFGamePayParamInfo {
	
	/**CP订单号 **/
	private String cp_bill_no;
	
	
	
	/**扩展参数 支付成功服务器通知接口将原样返回 **/
	private String extra;
	
	/**设置订单金额 可选项 **/
	private double cp_bill_money;
	
	/**服务器ID **/
	private int server_id;
	
	/**本单据支付成功后的通知返回地址 用于多服务器不同通知地址
	 * 如果不设置 请提供同一通知地址给我们 ，通知将统一发送到所提供的地址
	 *
	 */
	private String notify_url;
	
	
	
	/**
	 * 商品名称
	 */
	private String subject;
	
	private String order_id;
	
	public String getCpBillNo() {
		return cp_bill_no;
	}

	public void setCpBillNo(String cp_bill_no) {
		this.cp_bill_no = cp_bill_no;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public double getCpBillMoney() {
		return cp_bill_money;
	}

	public void setCpBillMoney(double cp_bill_money) {
		this.cp_bill_money = cp_bill_money;
	}

	public int getServerId() {
		return server_id;
	}

	public void setServerId(int server_id) {
		this.server_id = server_id;
	}

	public String getNotifyUrl() {
		return notify_url;
	}

	public void setNotifyUrl(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

}
