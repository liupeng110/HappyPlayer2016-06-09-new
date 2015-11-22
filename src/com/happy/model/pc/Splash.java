package com.happy.model.pc;

import java.io.Serializable;

public class Splash implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String sid;

	/**
	 * 主题
	 */
	private String title;

	/**
	 * 添加时间
	 */
	private String createTime;

	/**
	 * 开始时间
	 */
	private String startTime;

	/**
	 * 结束时间
	 */
	private String endTime;

	/**
	 * 更新时间
	 */
	private String updateTime;

	/**
	 * 图片数据
	 */
	private byte[] image;

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

}
