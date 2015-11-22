package com.happy.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import android.content.Context;

import com.happy.common.Constants;
import com.happy.model.app.DownloadTask;
import com.happy.observable.ObserverManage;

public class FileDownloadThread {
	/**
	 * 任务
	 */
	private DownloadTask task;
	/**
	 * 是否已經完成任务
	 */
	private boolean isFinish = false;
	/**
	 * 取消下载任务
	 */
	private boolean isCancel = false;
	/**
	 * 暂停任务
	 */
	private boolean isPause = false;
	/**
	 * 下载失败
	 */
	private boolean isError = false;

	public FileDownloadThread(DownloadTask task) {
		this.task = task;
	}

	/**
	 * 下载文件
	 */
	@SuppressWarnings("resource")
	public void start(Context context) {

		if (!NetUtil.isNetworkAvailable(context)) {
			// 无网络
			isError = true;

			task.setStatus(DownloadTask.DOWNLOAD_ERROR_NONET);
			ObserverManage.getObserver().setMessage(task);

			ToastUtil.showText("无网络状态");

			return;
		}

		if (Constants.isWifi) {
			if (!NetUtil.isWifi(context)) {
				// 不是wifi
				isError = true;

				task.setStatus(DownloadTask.DOWNLOAD_ERROR_NOTWIFI);
				ObserverManage.getObserver().setMessage(task);

				ToastUtil.showText("非wifi状态");
				return;
			}
		}

		HttpURLConnection connection = null;
		RandomAccessFile randomAccessFile = null;
		InputStream is = null;
		try {
			connection = getHttpURLConnection(task.getDownloadUrl());
			// if (connection.getResponseCode() == 200) {
			File destFile = new File(task.getFilePath());
			File temp = destFile.getParentFile();
			if (!temp.exists()) {
				temp.mkdirs();
			}
			if (!destFile.exists()) {
				// 目标文件不存在 ，则创建目标文件
				task.setDownloadedSize(0);
				destFile.createNewFile();

				RandomAccessFile accessFile = new RandomAccessFile(destFile,
						"rwd");
				accessFile.setLength(task.getFileSize());
				accessFile.close();
			}
			// 设置范围，格式为Range：bytes x-y;
			connection.setRequestProperty(
					"Range",
					"bytes=" + task.getDownloadedSize() + "-"
							+ task.getFileSize());

			randomAccessFile = new RandomAccessFile(destFile, "rwd");
			randomAccessFile.seek(task.getDownloadedSize());

			// 将要下载的文件写到保存在保存路径下的文件中
			is = connection.getInputStream();
			byte[] buffer = new byte[4096];
			int length = -1;
			long progress = -1;
			while (!isCancel && progress != task.getFileSize()
					&& (length = is.read(buffer)) != -1) {

				if (!NetUtil.isNetworkAvailable(context)) {
					// 无网络
					isError = true;

					task.setStatus(DownloadTask.DOWNLOAD_ERROR_NONET);
					ObserverManage.getObserver().setMessage(task);

					ToastUtil.showText("无网络状态");
					return;
				}

				if (Constants.isWifi) {
					if (!NetUtil.isWifi(context)) {
						// 不是wifi
						isError = true;
						task.setStatus(DownloadTask.DOWNLOAD_ERROR_NOTWIFI);
						ObserverManage.getObserver().setMessage(task);

						ToastUtil.showText("非wifi状态");
						return;
					}
				}

				randomAccessFile.write(buffer, 0, length);
				task.setDownloadedSize(task.getDownloadedSize() + length);
				if (task.getDownloadedSize() > progress) {
					progress = task.getDownloadedSize();
					// 正在下载

					task.setStatus(DownloadTask.DOWNLOING);
					ObserverManage.getObserver().setMessage(task);
				}
				if (isPause) {
					// 暂停任务

					task.setStatus(DownloadTask.DOWNLOAD_PAUSE);
					ObserverManage.getObserver().setMessage(task);

					return;
				}
				if (isCancel) {
					// 取消下载任务

					task.setStatus(DownloadTask.DOWNLOAD_CANCEL);
					ObserverManage.getObserver().setMessage(task);
					return;
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!isPause && !isCancel) {
				// 完成任务
				isFinish = true;
				task.setFinishTime(DateUtil.dateToOtherString(new Date()));
				task.setStatus(DownloadTask.DOWNLOAD_FINISH);
				ObserverManage.getObserver().setMessage(task);
			}
			// } else {
			// // 服务器异常
			// isError = true;
			// }
		} catch (IOException e) {
			e.printStackTrace();
			// 下载出错
			isError = true;

			task.setStatus(DownloadTask.DOWNLOAD_ERROR_OTHER);
			ObserverManage.getObserver().setMessage(task);

			ToastUtil.showText("下载出错");

		} finally {
			try {
				if(is != null){
					is.close();
					randomAccessFile.close();
					connection.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 暂停任务
	 */
	public void pauseTask() {
		isCancel = false;
		isPause = true;
		isFinish = false;
	}

	/**
	 * 取消任务
	 */
	public void cancelTask() {
		isCancel = true;
		isPause = false;
		isFinish = false;
	}

	/**
	 * 
	 * 获取相关的下载链接
	 */
	private HttpURLConnection getHttpURLConnection(String downloadUrl)
			throws IOException {
		HttpURLConnection conn = null;
		URL url = new URL(downloadUrl);
		conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		conn.setRequestProperty(
				"Accept",
				"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setConnectTimeout(5 * 1000);
		return conn;
	}

	public boolean isFinish() {
		return isFinish;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	public boolean isPause() {
		return isPause;
	}

	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

}
