package com.happy.util;

import android.content.Context;

/**
 * 线程管理
 * 
 * @author zhangliangming
 * 
 */
public class DownloadManage {

	/**
	 * 皮肤线程
	 */
	private static DownloadThreadPool skinTM;
	/**
	 * 应用线程
	 */
	private static DownloadThreadPool apkTM;
	/**
	 * 歌曲线程
	 */
	private static DownloadThreadPool songNetTM;
	/**
	 * 歌曲下载线程
	 */
	private static DownloadThreadPool downloadSongTM;

	/**
	 * 获取下载歌曲
	 * 
	 * @param context
	 * @return
	 */
	public static DownloadThreadPool getDownloadSongTM(Context context) {
		if (downloadSongTM == null) {
			downloadSongTM = new DownloadThreadPool(context);
		}
		return downloadSongTM;
	}

	/**
	 * 获取皮肤线程管理
	 * 
	 * @return
	 */
	public static DownloadThreadPool getSkinTM(Context context) {
		if (skinTM == null) {
			skinTM = new DownloadThreadPool(context);
		}
		return skinTM;
	}

	/**
	 * 获取应用线程管理
	 * 
	 * @return
	 */
	public static DownloadThreadPool getAPKTM(Context context) {
		if (apkTM == null) {
			apkTM = new DownloadThreadPool(context);
		}
		return apkTM;
	}

	/**
	 * 获取在线歌曲线程管理
	 * 
	 * @param context
	 * @return
	 */
	public static DownloadThreadPool getSongNetTM(Context context) {
		if (songNetTM == null) {
			songNetTM = new DownloadThreadPool(context);
		}
		return songNetTM;
	}
}
