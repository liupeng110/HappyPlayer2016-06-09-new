package com.happy.util;

import java.util.ArrayList;

import android.content.Context;

import com.happy.model.app.DownloadTask;

/**
 * 任务下载线程
 * 
 * @author zhangliangming
 * 
 */
public class DownloadThreadPool {
	/**
	 * 任务列表
	 */
	private ArrayList<DownloadTask> tasks = new ArrayList<DownloadTask>();
	/**
	 * 下载线程
	 */
	private Thread downloadThread;
	/**
	 * 是否是线程等待
	 */
	private boolean isWaiting = false;
	/**
	 * 任务接口事件
	 */
	private IDownloadTaskEventCallBack event = null;

	/**
	 * 任务完成接口事件
	 */
	private ITaskFinishCallBack finishEvent = new ITaskFinishCallBack() {

		public void updateList() {
			if (downloadThread != null && tasks.size() > 0 && isWaiting) {
				// 唤醒任务下载队列
				synchronized (runnable) {
					runnable.notify();
				}
			}
		}
	};

	private Context context;

	public DownloadThreadPool(Context context) {
		this.context = context;
	}

	/**
	 * 获取任务
	 * 
	 * @param tid
	 * @return
	 */
	public DownloadTask getDownloadTask(String tid) {
		for (int i = 0; i < tasks.size(); i++) {
			DownloadTask temp = tasks.get(i);
			if (temp.getTid().equals(tid)) {
				return temp;
			}
		}
		return null;
	}

	/**
	 * 取消等待
	 */
	public void cancelWaiting(String tid) {
		for (int i = 0; i < tasks.size(); i++) {
			DownloadTask temp = tasks.get(i);
			if (temp.getTid().equals(tid)) {
				tasks.remove(i);
				if (event != null) {
					event.cancelWaiting(temp);
				}
				break;
			}
		}
	}

	/**
	 * 添加在线任务
	 * 
	 * @param task
	 */
	public void addNetDownloadTask(DownloadTask task) {
		while (tasks.size() > 0) {
			DownloadTask temp = tasks.get(0);
			DownloadThreadManage dtm = temp.getDownloadThreadManage();
			if (dtm.isFinish() || dtm.isCancel() || dtm.isError()
					|| dtm.isPause()) {
				tasks.remove(0);
			} else {
				dtm.cancel();
				tasks.remove(0);
			}
		}
		tasks.add(task);
		if (downloadThread == null) {
			downloadThread = new Thread(runnable);
			downloadThread.start();
		} else {
			// 唤醒任务下载队列
			synchronized (runnable) {
				runnable.notify();
			}
		}
	}

	/**
	 * 添加下载歌曲任务
	 * 
	 * @param task
	 */
	public void addDownloadSongTask(DownloadTask task) {
		boolean flag = false;
		if (tasks.size() == 0) {
			flag = true;
		}
		if (!tasks.contains(task)) {
			// tasks.add(task);
			// 通过tid对任务进行排序，tid在服务器端添加时，要自动递增
			int taskIndex = -1;
			int i = 0;
			for (; i < tasks.size(); i++) {
				DownloadTask temp = tasks.get(i);
				// System.out.println(task.getTid() + "   " + temp.getTid());
				if (task.getAddTime().compareTo(temp.getAddTime()) > 0) {
					taskIndex = i;
					break;
				}
			}
			if (taskIndex == -1) {
				tasks.add(task);
			} else {
				if (tasks.size() >= taskIndex) {
					tasks.add(taskIndex, task);
				} else if (taskIndex - 1 < 0) {
					tasks.add(0, task);
				} else {
					tasks.add(taskIndex - 1, task);
				}
			}
			if (event != null) {
				event.waiting(task);
			}
			if (downloadThread != null && flag) {
				// 唤醒任务下载队列
				synchronized (runnable) {
					runnable.notify();
				}
			}
		} else {
			// 再次点击下载时，如果任务已经在列表中，则更新该下载任务的ui
			for (int i = 0; i < tasks.size(); i++) {
				DownloadTask temp = tasks.get(i);
				if (task.getTid().equals(temp.getTid())) {
					tasks.remove(i);
					tasks.add(i, task);
					if (event != null) {
						event.waiting(task);
					}
					break;
				}
			}
		}
		if (downloadThread == null) {
			downloadThread = new Thread(runnable);
			downloadThread.start();
		}
	}

	/**
	 * 添加任务
	 * 
	 * @param task
	 */
	public void addDownloadTask(DownloadTask task) {
		boolean flag = false;
		if (tasks.size() == 0) {
			flag = true;
		}
		if (!tasks.contains(task)) {
			// tasks.add(task);
			// 通过tid对任务进行排序，tid在服务器端添加时，要自动递增
			int taskIndex = -1;
			int i = 0;
			for (; i < tasks.size(); i++) {
				DownloadTask temp = tasks.get(i);
				// System.out.println(task.getTid() + "   " + temp.getTid());
				if (task.getTid().compareTo(temp.getTid()) > 0) {
					taskIndex = i;
					break;
				}
			}
			if (taskIndex == -1) {
				tasks.add(task);
			} else {
				if (tasks.size() >= taskIndex) {
					tasks.add(taskIndex, task);
				} else if (taskIndex - 1 < 0) {
					tasks.add(0, task);
				} else {
					tasks.add(taskIndex - 1, task);
				}
			}
			if (event != null) {
				event.waiting(task);
			}
			if (downloadThread != null && flag) {
				// 唤醒任务下载队列
				synchronized (runnable) {
					runnable.notify();
				}
			}
		} else {
			// 再次点击下载时，如果任务已经在列表中，则更新该下载任务的ui
			for (int i = 0; i < tasks.size(); i++) {
				DownloadTask temp = tasks.get(i);
				if (task.getTid().equals(temp.getTid())) {
					tasks.remove(i);
					tasks.add(i, task);
					if (event != null) {
						event.waiting(task);
					}
					break;
				}
			}
		}
		if (downloadThread == null) {
			downloadThread = new Thread(runnable);
			downloadThread.start();
		}
	}

	/**
	 * 任务线程
	 */
	private Runnable runnable = new Runnable() {

		public void run() {
			while (tasks.size() > 0) {
				DownloadTask task = tasks.get(0);
				DownloadThreadManage dtm = task.getDownloadThreadManage();
				dtm.setFinishEvent(finishEvent);
				if (event != null) {
					dtm.setEvent(event);
				}
				if (dtm.isFinish() || dtm.isCancel() || dtm.isError()
						|| dtm.isPause()) {
					tasks.remove(0);
					// 如果没有下载的任务，这里wait等待，方便后期添加任务
					if (tasks.size() == 0) {
						// 如果队列为空,则令线程等待
						synchronized (this) {
							try {
								isWaiting = true;
								this.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					isWaiting = false;
					if (task.getType() == DownloadTask.SONG_NET) {
						dtm.startNetTask(context);
					} else {
						dtm.start(context);
					}
					// 更新下载数据
					if (tasks.size() > 0) {
						DownloadTask taskTemp = dtm.getUpdateDownloadTask();
						tasks.remove(0);
						tasks.add(taskTemp);
					}
					// 如果队列为空,则令线程等待
					synchronized (this) {
						try {
							isWaiting = true;
							this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			// // 如果队列为空,则令线程等待
			// synchronized (this) {
			// try {
			// this.wait();
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// }
		}

	};

	public void setEvent(IDownloadTaskEventCallBack event) {
		this.event = event;
	}

	/**
	 * 任务完成事件回调
	 * 
	 * @author zhangliangming
	 * 
	 */
	public interface ITaskFinishCallBack {
		public void updateList();
	}

	/**
	 * 下载任务回调事件
	 * 
	 * @author zhangliangming
	 * 
	 */
	public interface IDownloadTaskEventCallBack {
		/**
		 * 等待暂停
		 * 
		 * @param task
		 */
		public void cancelWaiting(DownloadTask task);

		/**
		 * 等待中
		 * 
		 * @param task
		 */
		public void waiting(DownloadTask task);

		/**
		 * 下载中
		 * 
		 * @param task
		 */
		public void downloading(DownloadTask task, int downloadSize);

		/**
		 * 子线程下载进度
		 * 
		 * @param task
		 */
		public void threadDownloading(DownloadTask task, int downloadSize,
				int threadIndex, int threadNum, int startIndex, int endIndex);

		/**
		 * 暂停
		 * 
		 * @param task
		 */
		public void pauseed(DownloadTask task, int downloadSize);

		/**
		 * 取消任务
		 * 
		 * @param task
		 */
		public void canceled(DownloadTask task);

		/**
		 * 下载完成
		 * 
		 * @param task
		 */
		public void finished(DownloadTask task);

		/**
		 * 下载错误
		 * 
		 * @param task
		 */
		public void error(DownloadTask task);
	}
}
