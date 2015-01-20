package com.baofeng.game.sdk.vo;

public class BFGame {
	private int id;
	private String name;
	private String imageDownloadUrl;
	private String apkDownloadUrl;

	public BFGame() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageDownloadUrl() {
		return imageDownloadUrl;
	}

	public void setImageDownloadUrl(String imageDownloadUrl) {
		this.imageDownloadUrl = imageDownloadUrl;
	}

	public String getApkDownloadUrl() {
		return apkDownloadUrl;
	}

	public void setApkDownloadUrl(String apkDownloadUrl) {
		this.apkDownloadUrl = apkDownloadUrl;
	}
}
