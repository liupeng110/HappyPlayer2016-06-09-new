package com.happy.model.pc;

/**
 * 
 * 歌手写真图片
 * 
 */
public class SingerPhoto {
	/**
	 * id
	 */
	private String sid;

	/**
	 * 歌手名
	 */
	private String singer;

	/**
	 * 添加时间
	 */
	private String createTime;

	/**
	 * 更新时间
	 */
	private String updateTime;

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
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

}
