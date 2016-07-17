package com.happy.model.app;

import java.io.Serializable;

/**
 * 
 * @author zhangliangming
 * 
 */
public class SongInfo  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 本地歌曲
	 */
	public static int LOCALSONG = 0;

	/**
	 * 网络歌曲
	 */
	public static int NETSONG = 1;
	/**
	 * 下载歌曲
	 */
	public static int DOWNLOADSONG = 2;
	/**
	 * 搜索歌曲
	 */
	public static int NETSEARCHSONG = 3;

	/**
	 * 不喜欢
	 */
	public static final int UNLIKE = 0;
	/**
	 * 喜欢
	 */
	public static final int LIKE = 1;
	/**
	 * 下载中
	 */
	public static final int DOWNLOADING = 0;
	/**
	 * 下载完成
	 */
	public static final int DOWNLOADED = 1;

	private String sid = ""; // 歌曲id
	private String displayName = "";// 歌曲显示名称
	private String title = ""; // 歌曲名称
	private String singer = ""; // 歌手名称
	private long duration; // 歌曲时长
	private String durationStr = "";// 歌曲时长
	private long size; // 文件大小
	private String sizeStr = ""; // 文件大小字符串
	private String filePath = "";// 文件路径
	private int type; // 歌曲类型 0是本地 1是网络
	private int islike; // 是否是喜欢歌曲
	private String category = "";// 分类
	private String childCategory = "";// 子分类
	private String createTime = "";// 创建时间
	private String albumUrl = ""; // 专辑图片下载路径
	private String singerPIC = "";// 歌手写真图片下载路径
	private String kscUrl = "";// 歌词文件下载路径
	private String fileExt = ""; //歌曲类型
	// private String singerPIC1 = ""; // 歌手写真图片1
	// private String singerPIC2 = ""; // 歌手写真图片2
	// private String singerPIC3 = ""; // 歌手写真图片3

	private long playProgress;// 播放的进度

	// /////////////华丽分隔线，用来区分网络歌曲和本地歌曲////////////////////////////

	private String downloadUrl = "";// 下载路径
	private long downloadProgress;// 下载进度
	private int downloadStatus; // 下载状态

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getDurationStr() {
		return durationStr;
	}

	public void setDurationStr(String durationStr) {
		this.durationStr = durationStr;
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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIslike() {
		return islike;
	}

	public void setIslike(int islike) {
		this.islike = islike;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getChildCategory() {
		return childCategory;
	}

	public void setChildCategory(String childCategory) {
		this.childCategory = childCategory;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getAlbumUrl() {
		return albumUrl;
	}

	public void setAlbumUrl(String albumUrl) {
		this.albumUrl = albumUrl;
	}

	//
	// public String getSingerPIC1() {
	// return singerPIC1;
	// }
	//
	// public void setSingerPIC1(String singerPIC1) {
	// this.singerPIC1 = singerPIC1;
	// }
	//
	// public String getSingerPIC2() {
	// return singerPIC2;
	// }
	//
	// public void setSingerPIC2(String singerPIC2) {
	// this.singerPIC2 = singerPIC2;
	// }
	//
	// public String getSingerPIC3() {
	// return singerPIC3;
	// }
	//
	// public void setSingerPIC3(String singerPIC3) {
	// this.singerPIC3 = singerPIC3;
	// }

	public String getSingerPIC() {
		return singerPIC;
	}

	public void setSingerPIC(String singerPIC) {
		this.singerPIC = singerPIC;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public long getDownloadProgress() {
		return downloadProgress;
	}

	public void setDownloadProgress(long downloadProgress) {
		this.downloadProgress = downloadProgress;
	}

	public String getKscUrl() {
		return kscUrl;
	}

	public void setKscUrl(String kscUrl) {
		this.kscUrl = kscUrl;
	}

	public long getPlayProgress() {
		return playProgress;
	}

	public void setPlayProgress(long playProgress) {
		this.playProgress = playProgress;
	}

	public int getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(int downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

}
