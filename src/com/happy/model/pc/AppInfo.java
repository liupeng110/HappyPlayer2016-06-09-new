package com.happy.model.pc;

public class AppInfo {
	private String aid;// 编号id
	private String name; // 应用名称
	private String title;// 应用简介
	private String versionName; // 版本
	private String versionCode;// 数字版本
	private long size; // 大小
	private String sizeStr; // 大小字符串
	private byte[] data;// apk数据
	private String createTime;// 添加时间
	private String updateTime;// 更新时间
	private String type;// 文件类型

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getSizeStr() {
		return sizeStr;
	}

	public void setSizeStr(String sizeStr) {
		this.sizeStr = sizeStr;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
