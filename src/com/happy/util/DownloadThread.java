package com.happy.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;

import com.happy.common.Constants;
import com.happy.model.app.DownloadTask;
import com.happy.util.DownloadThreadManage.IDownloadCallBack;

/**
 * 下载
 * 
 * @author zhangliangming
 * 
 */
public class DownloadThread extends Thread {
	/**
	 * 下载任务
	 */
	private DownloadTask task;
	/**
	 * 线程id
	 */
	private int threadId;
	/**
	 * 开始位置
	 */
	private int startIndex;
	/**
	 * 结束位置
	 */
	private int endIndex;
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
	/**
	 * 下载回调
	 */
	private IDownloadCallBack callBack;
	/**
	 * 下载进度
	 */
	private int downloadedSize = 0;
	private Context context;
	/**
	 * 第一个线程回调
	 */
	private DTEventCallBack dteCallBack;
	/**
	 * 是否通知
	 */
	private boolean isNotify = false;

	public DownloadThread(Context context, DownloadTask task, int threadId,
			int startIndex, int endIndex, IDownloadCallBack callBack) {
		this.context = context;
		this.threadId = threadId;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.task = task;
		this.callBack = callBack;
	}

	public void run() {
		HttpURLConnection connection = null;
		InputStream is = null;
		RandomAccessFile randomAccessFile = null;
		try {
			connection = getHttpURLConnection(task.getDownloadUrl());
			// 设置范围，格式为Range：bytes x-y;
			connection.setRequestProperty("Range", "bytes=" + startIndex + "-"
					+ endIndex);
			connection.connect();

			// /**
			// * 代表服务器已经成功处理了部分GET请求
			// */
			// if (connection.getResponseCode() == 206) {
			randomAccessFile = new RandomAccessFile(
					new File(task.getFilePath()), "rwd");
			// 将要下载的文件写到保存在保存路径下的文件中
			is = connection.getInputStream();
			// is.skip(startIndex);
			randomAccessFile.seek(startIndex);
			byte[] buffer = new byte[10 * 1024];
			int length = -1;
			while (!isCancel && !isError && (length = is.read(buffer)) != -1) {
				if (!NetUtil.isNetworkAvailable(context)) {
					// 无网络
					isError = true;

					task.setStatus(DownloadTask.DOWNLOAD_ERROR_NONET);
					if (callBack != null && task != null) {
						callBack.error();
					}

					ToastUtil.showText("无网络状态");
					return;
				}

				if (Constants.isWifi) {
					if (!NetUtil.isWifi(context)) {
						// 不是wifi
						isError = true;
						task.setStatus(DownloadTask.DOWNLOAD_ERROR_NOTWIFI);
						if (callBack != null && task != null) {
							callBack.error();
						}

						ToastUtil.showText("非wifi状态");
						return;
					}
				}

				randomAccessFile.write(buffer, 0, length);
				downloadedSize += length;
				// System.out.println("线程==========================：" +
				// threadId
				// + "下载进度 " + downloadedSize);
				if (downloadedSize < endIndex) {
					// 正在下载
					if (callBack != null) {
						task.setDownloadedSize(downloadedSize);
						callBack.threadDownloading(task);
						callBack.downloading();
					}
					if (endIndex - downloadedSize < endIndex / 2
							&& dteCallBack != null && !isNotify) {
						isNotify = true;
						dteCallBack.notifyOtherThread();
					}
				}
				if (isPause) {
					// 暂停任务
					if (callBack != null) {
						callBack.pauseed();
					}
					return;
				}
				if (isCancel) {
					if (callBack != null) {
						callBack.canceled();
					}
					// 取消下载任务
					return;
				}
				// try {
				// Thread.sleep(100);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
			}
			if (!isPause && !isCancel) {
				// 完成任务
				isFinish = true;
				if (callBack != null) {
					callBack.finished();
				}
			}
			System.out.println("线程：" + threadId + "下载完毕 " + downloadedSize);
			// } else {
			// // 服务器异常
			// isError = true;
			// }
			// }
		} catch (IOException e) {
			e.printStackTrace();

			// 下载出错
			isError = true;
			if (callBack != null) {
				task.setStatus(DownloadTask.DOWNLOAD_ERROR_OTHER);
				if (callBack != null && task != null) {
					callBack.error();
				}
			}

		} finally {
			try {
				if (is != null) {
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
	 * 
	 * 获取相关的下载链接
	 */
	private HttpURLConnection getHttpURLConnection(String downloadUrl)
			throws IOException {
		HttpURLConnection conn = null;
		URL url = new URL(downloadUrl);
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty(
				"Accept",
				"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setConnectTimeout(10 * 1000);
		return conn;
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
	 * 获取当前下载的进度
	 * 
	 * @return
	 */
	public int getDownloadSize() {
		return downloadedSize;
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

	public void setDteCallBack(DTEventCallBack dteCallBack) {
		this.dteCallBack = dteCallBack;
	}

	public interface DTEventCallBack {
		public void notifyOtherThread();
	}
}
