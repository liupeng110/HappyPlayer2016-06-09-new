package com.happy.model.pc;

/**
 * 
 *歌词
 *
 */
public class KscInfo {
	// 歌词id
	private String kid;
	// 歌手名称
	private String artist;
	// 歌曲歌名
	private String songName;
	private long size; // 大小
	private String sizeStr; // 大小字符串
	private String createTime;// 添加时间
	private String updateTime;// 更新时间
	private String type;// 文件类型

	public String getKid() {
		return kid;
	}

	public void setKid(String kid) {
		this.kid = kid;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
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
