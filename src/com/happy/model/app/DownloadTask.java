package com.happy.model.app;

import java.io.Serializable;

import com.happy.util.DownloadThreadManage;

/**
 * 下载任务
 * 
 * @author Administrator
 * 
 */
public class DownloadTask implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int SKIN = 1;// 皮肤
	public static final int APK = 2;// 应用
	public static final int SONG = 3;// 歌曲
	public static final int SONG_NET = 4;// 网络歌曲
	public static final int SONG_NET_DOWNLOAD = 5;// 下载歌曲

	private static int i = 1000;
	public static final int INT = i++;// 初始化状态
	public static final int WAITING = i++;// 等待下载
	public static final int DOWNLOING = i++;// 下载中
	public static final int DOWNLOAD_PAUSE = i++;// 下载暂停
	public static final int DOWNLOAD_CANCEL = i++;// 下载取消
	public static final int DOWNLOAD_CANCELWAITING = i++;// 取消下载暂停
	public static final int DOWNLOAD_ERROR_NONET = i++; // 下载失败-无网络
	public static final int DOWNLOAD_ERROR_NOTWIFI = i++; // 下载失败-不是wifi
	public static final int DOWNLOAD_ERROR_OTHER = i++; // 下载失败-其它原因
	public static final int DOWNLOAD_FINISH = i++;// 下载完成

	private String tid;
	/**
	 * 任务名称
	 */
	private String tName;

	/**
	 * 初始化状态
	 */
	private int status = INT;
	/**
	 * 下载地址
	 */
	private String downloadUrl;
	/**
	 * 文件路径
	 */
	private String filePath;
	/**
	 * 文件大小
	 */
	private long fileSize;
	/**
	 * 文件大小
	 */
	private String fileSizeStr;
	/**
	 * 已经下载的进度
	 */
	private long downloadedSize;
	/**
	 * 下载任务的线程管理器
	 */
	private DownloadThreadManage downloadThreadManage;
	/**
	 * 下载任务类型
	 */
	private int type = APK;
	/**
	 * 添加时间
	 */
	private String addTime;
	/**
	 * 完成时间
	 */
	private String finishTime;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getDownloadedSize() {
		return downloadedSize;
	}

	public void setDownloadedSize(long downloadedSize) {
		this.downloadedSize = downloadedSize;
	}

	public DownloadThreadManage getDownloadThreadManage() {
		return downloadThreadManage;
	}

	public void setDownloadThreadManage(
			DownloadThreadManage downloadThreadManage) {
		this.downloadThreadManage = downloadThreadManage;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public String gettName() {
		return tName;
	}

	public void settName(String tName) {
		this.tName = tName;
	}

	public String getFileSizeStr() {
		return fileSizeStr;
	}

	public void setFileSizeStr(String fileSizeStr) {
		this.fileSizeStr = fileSizeStr;
	}

}
