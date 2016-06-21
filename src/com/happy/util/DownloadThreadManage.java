package com.happy.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.content.Context;

import com.happy.common.Constants;
import com.happy.model.app.DownloadTask;
import com.happy.util.DownloadThread.DTEventCallBack;
import com.happy.util.DownloadThreadPool.IDownloadTaskEventCallBack;
import com.happy.util.DownloadThreadPool.ITaskFinishCallBack;

/**
 * 任务下载线程管理器
 * 
 * @author zhangliangming
 * 
 */
public class DownloadThreadManage {
	/**
	 * 任务
	 */
	private DownloadTask task;
	/**
	 * 线程数
	 */
	private int threadCount = 1;
	/**
	 * 睡眠线程时间
	 */
	private int sleepTime = 100;
	/**
	 * 下载任务线程
	 */
	private DownloadThread[] downloadThreads;
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
	 * 时间线程
	 */
	private Thread timeThread = null;

	/**
	 * 下载任务回调
	 */
	private IDownloadTaskEventCallBack event;
	/**
	 * 任务完成回调
	 */
	private ITaskFinishCallBack finishEvent;
	/**
	 * 
	 */
	private IDownloadCallBack callBack = new IDownloadCallBack() {

		public void downloading() {
			if (timeThread == null) {
				timeThread = new Thread(new TimeRunable());
				timeThread.start();
			}
		}

		public void pauseed() {
			int downloadSize = 0;
			for (int i = 0; i < downloadThreads.length; i++) {
				DownloadThread downloadThread = downloadThreads[i];
				if (downloadThread != null) {
					if (!downloadThread.isPause()) {
						return;
					}
					downloadSize += downloadThread.getDownloadSize();
				}
			}
			if (timeThread != null) {
				timeThread = null;
			}
			if (event != null && task != null) {
				task.setDownloadedSize(downloadSize);
				event.pauseed(task);
			}
		}

		public void finished() {
			int downloadSize = 0;
			for (int i = 0; i < downloadThreads.length; i++) {
				DownloadThread downloadThread = downloadThreads[i];
				if (downloadThread != null)
					downloadSize += downloadThread.getDownloadSize();
			}
			if (downloadSize == task.getFileSize() && !isFinish) {

				if (timeThread != null) {
					timeThread = null;
				}

				isFinish = true;
				System.out.println("完成下载:" + downloadSize);
				// System.out.println(System.currentTimeMillis());
				if (finishEvent != null)
					finishEvent.updateList();
				if (event != null && task != null) {
					task.setDownloadedSize(downloadSize);
					event.finished(task);
				}
			}
		}

		@Override
		public void canceled() {
			for (int i = 0; i < downloadThreads.length; i++) {
				DownloadThread downloadThread = downloadThreads[i];
				if (downloadThread != null) {
					if (!downloadThread.isPause()) {
						return;
					}
				}
			}
			if (timeThread != null) {
				timeThread = null;
			}
			if (event != null && task != null) {
				event.canceled(task);
			}
		}

		public void error() {
			isError = true;
			for (int i = 0; i < downloadThreads.length; i++) {
				DownloadThread downloadThread = downloadThreads[i];
				if (downloadThread != null)
					downloadThread.cancelTask();
			}
			if (timeThread != null) {
				timeThread = null;
			}
			if (event != null) {
				event.error(task);
			}
		}

		public void threadDownloading(DownloadTask task) {
			if (event != null && task != null) {
				event.threadDownloading(task);
			}
		}

	};

	public DownloadThreadManage(DownloadTask task, int threadCount,
			int sleepTime) {
		this.sleepTime = sleepTime;
		this.task = task;
		this.threadCount = threadCount;
	}

	public DownloadThreadManage(ITaskFinishCallBack finishEvent,
			IDownloadTaskEventCallBack event, DownloadTask task,
			int threadCount, int sleepTime) {
		this.sleepTime = sleepTime;
		this.event = event;
		this.task = task;
		this.finishEvent = finishEvent;
		this.threadCount = threadCount;
	}

	/**
	 * 执行任务
	 */
	public void startNetTask(final Context context) {
		if (!NetUtil.isNetworkAvailable(context)) {
			// 无网络
			isError = true;

			task.setStatus(DownloadTask.DOWNLOAD_ERROR_NONET);
			if (event != null && task != null) {
				event.error(task);
			}

			ToastUtil.showText("无网络状态");
			return;
		}

		if (Constants.isWifi) {
			if (!NetUtil.isWifi(context)) {
				// 不是wifi
				isError = true;
				task.setStatus(DownloadTask.DOWNLOAD_ERROR_NOTWIFI);
				if (event != null && task != null) {
					event.error(task);
				}

				ToastUtil.showText("非wifi状态");
				return;
			}
		}
		try {
			// 获取该网络资源文件的长度
			// long length = getFileLength(new URL(task.getDownloadUrl()));
			final int length = (int) task.getFileSize();
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
			downloadThreads = new DownloadThread[threadCount];
			// 平均每一个线程下载的文件大小.
			final int blockSize = length / threadCount;
			// 整个下载资源整除后剩下的余数取模
			final int left = length % threadCount;
			int threadId = 1;
			if (threadId <= threadCount) {
				int startIndex = (threadId - 1) * blockSize;
				int endIndex = threadId * blockSize;
				if (threadId == threadCount) {// 最后一个线程下载的长度要稍微长一点
					// 最后一个线程下载指定endIndex+left个字节
					endIndex = endIndex + left;
				}
				System.out.println("线程：" + threadId + "下载:---" + startIndex
						+ "--->" + endIndex);

				final DownloadThread dt = new DownloadThread(context, task,
						threadId, startIndex, endIndex, callBack);
				downloadThreads[threadId - 1] = dt;
				downloadThreads[threadId - 1].start();
				downloadThreads[threadId - 1]
						.setDteCallBack(new DTEventCallBack() {

							@Override
							public void notifyOtherThread() {
								notifyOtherThreadTask(context, blockSize,
										length, left);
							}
						});
			}

		} catch (Exception e) {
			isError = true;
			e.printStackTrace();
		}
	}

	/**
	 * 其它线程下载任务
	 * 
	 * @param context
	 * @param blockSize
	 * @param length
	 */
	protected void notifyOtherThreadTask(Context context, int blockSize,
			int length, int left) {
		for (int threadId = 2; threadId <= threadCount; threadId++) {
			// 第一个线程下载的开始位置
			int startIndex = (threadId - 1) * blockSize;
			int endIndex = threadId * blockSize;
			if (threadId == threadCount) {// 最后一个线程下载的长度要稍微长一点
				// 最后一个线程下载指定endIndex+left个字节
				endIndex = endIndex + left;
			}
			System.out.println("线程：" + threadId + "下载:---" + startIndex
					+ "--->" + endIndex);
			DownloadThread dt = new DownloadThread(context, task, threadId,
					startIndex, endIndex, callBack);
			if (dt.isFinish()) {
				continue;
			}
			downloadThreads[threadId - 1] = dt;
			downloadThreads[threadId - 1].start();
		}

	}

	/**
	 * 执行任务
	 */
	public void start(Context context) {
		if (!NetUtil.isNetworkAvailable(context)) {
			// 无网络
			isError = true;

			task.setStatus(DownloadTask.DOWNLOAD_ERROR_NONET);
			if (event != null && task != null) {
				event.error(task);
			}

			ToastUtil.showText("无网络状态");
			return;
		}

		if (Constants.isWifi) {
			if (!NetUtil.isWifi(context)) {
				// 不是wifi
				isError = true;
				task.setStatus(DownloadTask.DOWNLOAD_ERROR_NOTWIFI);
				if (event != null && task != null) {
					event.error(task);
				}

				ToastUtil.showText("非wifi状态");
				return;
			}
		}
		try {
			// 获取该网络资源文件的长度
			// long length = getFileLength(new URL(task.getDownloadUrl()));
			int length = (int) task.getFileSize();
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
			downloadThreads = new DownloadThread[threadCount];
			// 平均每一个线程下载的文件大小.
			int blockSize = length / threadCount;
			int left = length % threadCount;
			for (int threadId = 1; threadId <= threadCount; threadId++) {
				// 第一个线程下载的开始位置
				int startIndex = (threadId - 1) * blockSize;
				int endIndex = threadId * blockSize;
				if (threadId == threadCount) {// 最后一个线程下载的长度要稍微长一点
					// 最后一个线程下载指定endIndex+left个字节
					endIndex = endIndex + left;
				}
				// System.out.println("线程：" + threadId + "下载:---" + startIndex
				// + "--->" + endIndex);
				DownloadThread dt = new DownloadThread(context, task, threadId,
						startIndex, endIndex, callBack);
				if (dt.isFinish()) {
					continue;
				}
				downloadThreads[threadId - 1] = dt;
				downloadThreads[threadId - 1].start();
			}

		} catch (Exception e) {
			isError = true;
			e.printStackTrace();
		}
	}

	private class TimeRunable implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {

					if (callBack != null && !isCancel && !isError && !isPause
							&& !isFinish) {
						updateDownloadUI();
					}
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 下载进度
	 */
	private synchronized void updateDownloadUI() {
		int downloadSize = 0;
		for (int i = 0; i < downloadThreads.length; i++) {
			DownloadThread downloadThread = downloadThreads[i];
			if (downloadThread != null)
				downloadSize += downloadThread.getDownloadSize();
		}
		// System.out.println("当前下载进度:" + downloadSize);
		if (event != null && task != null) {
			task.setDownloadedSize(downloadSize);
			event.downloading(task);
		}

	}

	/**
	 * 暂停
	 */
	public void pause() {
		for (int i = 0; i < downloadThreads.length; i++) {
			DownloadThread downloadThread = downloadThreads[i];
			if (downloadThread != null)
				downloadThread.pauseTask();
		}
		// if (event != null) {
		// event.pauseed(task);
		// }
	}

	/**
	 * 取消
	 */
	public void cancel() {
		for (int i = 0; i < downloadThreads.length; i++) {
			DownloadThread downloadThread = downloadThreads[i];
			if (downloadThread != null)
				downloadThread.cancelTask();
		}
		// if (event != null) {
		// event.canceled(task);
		// }
	}

	/**
	 * 根据URL获取该URL所指向的资源文件的长度
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	// private long getFileLength(URL url) throws IOException {
	// long length = 0;
	// URLConnection urlConnection = url.openConnection();
	// long size = urlConnection.getContentLength();
	// length = size;
	// return length;
	// }

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

	public void setFinishEvent(ITaskFinishCallBack finishEvent) {
		this.finishEvent = finishEvent;
	}

	public void setEvent(IDownloadTaskEventCallBack event) {
		this.event = event;
	}

	/**
	 * 获取更新后的任务类
	 * 
	 * @return
	 */
	public DownloadTask getUpdateDownloadTask() {
		task.setDownloadThreadManage(this);
		return task;
	}

	/**
	 * 下载任务线程回调
	 * 
	 * @author zhangliangming
	 * 
	 */
	public interface IDownloadCallBack {
		/**
		 * 每个子线程的下载进度
		 * 
		 * @param task
		 */
		public void threadDownloading(DownloadTask task);

		/**
		 * 下载中回调接口
		 */
		public void downloading();

		/**
		 * 暂停回调接口
		 */
		public void pauseed();

		/**
		 * 下载完成回调接口
		 */
		public void finished();

		/**
		 * 错误回调接口
		 */
		public void error();

		/**
		 * 
		 */
		public void canceled();
	}

}
