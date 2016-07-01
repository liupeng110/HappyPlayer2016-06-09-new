package com.happy.model.app;

import java.io.Serializable;

/**
 * 
 * @ClassName: SkinTheme
 * @Description:(皮肤主题)
 * @author: Android_Robot
 * @date: 2015-6-17 上午9:32:14
 * 
 */
public class SkinThemeApp  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 本地
	 */
	public static final int LOCAL = 0;
	/**
	 * 网络
	 */
	public static final int NET = 1;
	/**
	 * 编号
	 */
	private String sid;

	/**
	 * 皮肤主题id号
	 */
	private String ID;
	/**
	 * 皮肤主题名称
	 */
	private String ThemeName;

	/**
	 * 皮肤类型，是assets还是网络类型
	 */
	private int assetsType;
	/**
	 * 预览图片路径
	 */
	private String previewPath;
	/**
	 * 解压后的文件路径
	 */
	private String unZipPath;

	/** -----------------------------网络类型---------------------------- **/

	/**
	 * 预览图片url
	 */
	private String previewUrl;
	/**
	 * 下载路径
	 */
	private String downloadUrl;
	/**
	 * 保存路径
	 */
	private String downloadPath;

	/**
	 * 主题类型
	 */
	private int themeType;

	/**
	 * 文件大小
	 */
	private long fileSize;
	/**
	 * 文件大小
	 */
	private String fileSizeStr;
	/**
	 * 下载进度
	 */
	private long progerssFileSize;
	/**
	 * 下载状态
	 */
	private int status;
	/**
	 * 添加时间
	 */
	private String addTime;

	/**
	 * 添加时间
	 */
	private String createTime;

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getThemeName() {
		return ThemeName;
	}

	public void setThemeName(String themeName) {
		ThemeName = themeName;
	}

	public int getAssetsType() {
		return assetsType;
	}

	public void setAssetsType(int assetsType) {
		this.assetsType = assetsType;
	}

	public String getPreviewPath() {
		return previewPath;
	}

	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
	}

	public String getUnZipPath() {
		return unZipPath;
	}

	public void setUnZipPath(String unZipPath) {
		this.unZipPath = unZipPath;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public int getThemeType() {
		return themeType;
	}

	public void setThemeType(int themeType) {
		this.themeType = themeType;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileSizeStr() {
		return fileSizeStr;
	}

	public void setFileSizeStr(String fileSizeStr) {
		this.fileSizeStr = fileSizeStr;
	}

	public long getProgerssFileSize() {
		return progerssFileSize;
	}

	public void setProgerssFileSize(long progerssFileSize) {
		this.progerssFileSize = progerssFileSize;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
