package com.happy.service;

import java.io.File;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.IBinder;

import com.happy.common.Constants;
import com.happy.db.SongDB;
import com.happy.logger.LoggerManage;
import com.happy.manage.MediaManage;
import com.happy.model.app.DownloadTask;
import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.util.DateUtil;
import com.happy.util.DownloadManage;
import com.happy.util.DownloadThreadManage;
import com.happy.util.DownloadThreadPool;
import com.happy.util.DownloadThreadPool.IDownloadTaskEventCallBack;
import com.happy.util.HttpUtil;
import com.happy.util.ToastUtil;

public class MediaPlayerService extends Service implements Observer {
	/**
	 * 服务是否在进行
	 */
	public static Boolean isServiceRunning = false;
	/**
	 * 是否是第一次运行
	 */
	private Boolean isFirstStart = true;
	private Context context;
	/**
	 * 当前播放歌曲
	 */
	private SongInfo songInfo;
	private MediaPlayer player;

	private Thread playerThread = null;

	private LoggerManage logger;

	private int songDuration = 0;

	private boolean isError = false;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		context = MediaPlayerService.this.getBaseContext();
		logger = LoggerManage.getZhangLogger(context);
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		ObserverManage.getObserver().addObserver(this);
		isServiceRunning = true;
		if (!isFirstStart) {
			isFirstStart = false;
			// 播放歌曲
			if (songInfo != null) {
				playMusic(songInfo);
			}
		}
	}

	@Override
	public void onDestroy() {
		isServiceRunning = false;
		// 结束线程
		playerThread = null;
		ObserverManage.getObserver().deleteObserver(this);
		super.onDestroy();
		// // 如果当前的状态不是暂停，如果播放服务被回收了，要重新启动服务
		if (!Constants.APPCLOSE
				&& MediaManage.PAUSE != MediaManage.getMediaManage(context)
						.getPlayStatus()) {
			// 在此重新启动,使服务常驻内存
			startService(new Intent(this, MediaPlayerService.class));
		}
	}

	/**
	 * 初始化播放器
	 */
	private void initMusic() {
		if (player != null) {
			if (player.isPlaying()) {
				player.stop();

				SongMessage msg = new SongMessage();
				msg.setSongInfo(songInfo);
				msg.setType(SongMessage.SERVICEPAUSEEDMUSIC);
				ObserverManage.getObserver().setMessage(msg);

			}
			player.reset();
			player.release();
			player = null;
		}
		if (playerThread != null) {
			playerThread = null;
		}
	}

	/**
	 * 初始化播放器
	 */
	@SuppressLint("NewApi")
	public void initPlayer() {
		try {
			if (player != null) {
				if (player.isPlaying()) {
					player.stop();
				}
				player.reset();
				player.release();
				player = null;
			}
			if (playerThread != null) {
				playerThread = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 播放歌曲
	 * 
	 * @param songInfo
	 * @param type
	 */
	private void playMusic(SongInfo songInfo) {
		if (songInfo.getType() == SongInfo.LOCALSONG) {
			playLocalMusic(songInfo);
		} else {
			songDuration = (int) songInfo.getPlayProgress();
			playNETMusic(songInfo, false);
		}
	}

	/**
	 * 播放网络歌曲
	 * 
	 * @param songInfo
	 */
	private void playNETMusic(SongInfo songInfoTemp2, boolean isInit) {
		if (isInit) {
			songDuration = 0;
		}

		// 从数据库中获取最新的歌曲数据
		SongInfo songInfoTemp = SongDB.getSongInfoDB(getApplicationContext())
				.getSongInfo(songInfoTemp2.getSid());
		songInfoTemp.setPlayProgress(songDuration);
		this.songInfo = songInfoTemp;

		SongMessage msg = new SongMessage();
		msg.setSongInfo(songInfo);
		msg.setType(SongMessage.SERVICEPLAYWAITING);
		ObserverManage.getObserver().setMessage(msg);

		File songFile = new File(songInfo.getFilePath());
		if (songFile.exists()
				&& songInfo.getDownloadProgress() == songInfo.getSize()) {

			SongMessage msg2 = new SongMessage();
			msg2.setSongInfo(songInfo);
			msg2.setType(SongMessage.SERVICEPLAYWAITINGEND);
			ObserverManage.getObserver().setMessage(msg2);

			playLocalMusic(songInfo);
		} else {
			if (songInfo.getDownloadProgress() == 0 || isError) {
				isError = false;
				// 下载网络歌曲
				downloadNetMusic(songInfo);
			} else {
				playNETMusicByProgress(songInfo);
			}
		}
	}

	private IDownloadTaskEventCallBack eventCallBack = new IDownloadTaskEventCallBack() {

		@Override
		public void waiting(DownloadTask task) {

		}

		@Override
		public void downloading(DownloadTask task) {
			if (songInfo != null && songInfo.getSid().equals(task.getTid())
					&& songInfo.getDownloadProgress() == 0
					&& task.getDownloadedSize() > 1024 * 200) {
				songInfo.setDownloadProgress(task.getDownloadedSize());
				// 播放歌曲
				playNETMusic(songInfo, true);
			}
			SongDB.getSongInfoDB(getApplicationContext())
					.updateSongDownloadProgress(task.getTid(),
							task.getDownloadedSize());
		}

		@Override
		public void threadDownloading(DownloadTask task) {

		}

		@Override
		public void pauseed(DownloadTask task) {

		}

		@Override
		public void canceled(DownloadTask task) {

		}

		@Override
		public void finished(DownloadTask task) {
			if (songInfo != null && songInfo.getSid().equals(task.getTid())) {
				SongMessage songMessage = new SongMessage();
				songMessage.setSongInfo(songInfo);
				songMessage.setType(SongMessage.SERVICEDOWNLOADFINISHED);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);

				SongDB.getSongInfoDB(getApplicationContext())
						.updateSongDownloadProgress(task.getTid(),
								task.getDownloadedSize());
			}

		}

		@Override
		public void error(DownloadTask task) {
			isError = true;
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (songInfo != null && songInfo.getSid().equals(task.getTid())) {

				initPlayer();

				MediaManage.getMediaManage(getApplicationContext())
						.setPlayStatus(MediaManage.PAUSE);
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.INITMUSIC);
				songMessage.setSongInfo(songInfo);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);

			}
		}

	};

	/**
	 * 下载网络歌曲
	 * 
	 * @param songInfo
	 */
	private void downloadNetMusic(SongInfo songInfo) {

		DownloadTask task = new DownloadTask();
		String url = HttpUtil.getSongInfoDataByID(songInfo.getSid());
		task = new DownloadTask();
		task.setTid(songInfo.getSid());
		task.setStatus(DownloadTask.INT);
		task.setDownloadUrl(url);
		task.setFilePath(songInfo.getFilePath());
		task.setFileSize(songInfo.getSize());
		task.setDownloadedSize(0);
		task.setAddTime(DateUtil.dateToOtherString(new Date()));
		task.setFinishTime("");
		task.setType(DownloadTask.SONG_NET);

		DownloadThreadManage dtm = new DownloadThreadManage(task, 10);
		task.setDownloadThreadManage(dtm);
		DownloadThreadPool dp = DownloadManage
				.getSongNetTM(getApplicationContext());
		dp.setEvent(eventCallBack);
		dp.addNetDownloadTask(task);

	}

	/**
	 * 播放网络歌曲
	 * 
	 * @param songInfo
	 */
	private void playNETMusicByProgress(final SongInfo songInfo) {
		this.songInfo = songInfo;
		if (songInfo == null) {

			SongMessage msg = new SongMessage();
			msg.setType(SongMessage.SERVICEERRORMUSIC);
			msg.setErrorMessage(SongMessage.ERRORMESSAGEPLAYSONGNULL);
			ObserverManage.getObserver().setMessage(msg);

			return;
		}
		if (player == null) {
			player = new MediaPlayer();

			player.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					try {
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
					songDuration = mp.getCurrentPosition();
					if (songDuration >= songInfo.getDuration()) {
						// 下一首
						SongMessage songMessage = new SongMessage();
						songMessage.setType(SongMessage.NEXTMUSIC);
						ObserverManage.getObserver().setMessage(songMessage);
						songDuration = 0;
					} else {
						initPlayer();
						// 继续播放
						playNETMusic(songInfo, false);
					}
				}
			});

			player.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int arg1, int arg2) {
					// 播放出错，1秒过后，播放下一首

					ToastUtil.showTextToast(context, "播放歌曲出错!");

					MediaManage.getMediaManage(getApplicationContext())
							.setPlayStatus(MediaManage.PAUSE);
					SongMessage songMessage = new SongMessage();
					songMessage.setType(SongMessage.INITMUSIC);
					songMessage.setSongInfo(songInfo);
					// 通知
					ObserverManage.getObserver().setMessage(songMessage);

					return false;
				}
			});

		}
		if (playerThread == null) {
			playerThread = new Thread(new PlayerRunable());
			playerThread.start();
		}

		try {
			player.reset();
			player.setDataSource(songInfo.getFilePath());
			player.prepare();
			if (songInfo.getPlayProgress() != 0) {
				player.seekTo((int) songInfo.getPlayProgress());
			}
			// songDuration = player.getDuration();
			player.start();

			SongMessage msg = new SongMessage();
			msg.setSongInfo(songInfo);
			msg.setType(SongMessage.SERVICEPLAYWAITINGEND);
			ObserverManage.getObserver().setMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();

			ToastUtil.showText("播放歌曲出错!");
			initPlayer();

			MediaManage.getMediaManage(getApplicationContext()).setPlayStatus(
					MediaManage.PAUSE);
			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.INITMUSIC);
			songMessage.setSongInfo(songInfo);
			// 通知
			ObserverManage.getObserver().setMessage(songMessage);

		}
	}

	/**
	 * 播放本地歌曲
	 * 
	 * @param songInfo
	 */
	private void playLocalMusic(SongInfo songInfo) {
		this.songInfo = songInfo;
		if (songInfo == null) {

			SongMessage msg = new SongMessage();
			msg.setType(SongMessage.SERVICEERRORMUSIC);
			msg.setErrorMessage(SongMessage.ERRORMESSAGEPLAYSONGNULL);
			ObserverManage.getObserver().setMessage(msg);

			return;
		}

		if (player == null) {
			player = new MediaPlayer();

			player.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					// 下一首
					SongMessage songMessage = new SongMessage();
					songMessage.setType(SongMessage.NEXTMUSIC);
					ObserverManage.getObserver().setMessage(songMessage);
				}
			});

			player.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int arg1, int arg2) {
					// 播放出错，1秒过后，播放下一首

					ToastUtil.showTextToast(context, "播放歌曲出错,跳转下一首!");

					// 下一首
					SongMessage songMessage = new SongMessage();
					songMessage.setType(SongMessage.NEXTMUSIC);
					ObserverManage.getObserver().setMessage(songMessage);

					return false;
				}
			});

		}
		if (playerThread == null) {
			playerThread = new Thread(new PlayerRunable());
			playerThread.start();
		}

		try {
			player.reset();
			player.setDataSource(songInfo.getFilePath());
			player.prepare();
			if (songInfo.getPlayProgress() != 0) {
				player.seekTo((int) songInfo.getPlayProgress());
			}
			player.start();
		} catch (Exception e) {
			e.printStackTrace();

			ToastUtil.showTextToast(context, "播放歌曲出错,跳转下一首!");
			// 下一首
			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.NEXTMUSIC);
			ObserverManage.getObserver().setMessage(songMessage);

		}
	}

	/**
	 * 快进
	 * 
	 * @param progress
	 */
	private void seekTo(int progress) {

		File songFile = new File(songInfo.getFilePath());
		if (songFile.exists()
				&& songInfo.getDownloadProgress() == songInfo.getSize()) {
			playSeekToMusic(progress);
		} else {
			if (songInfo.getDownloadProgress() == 0) {
				songDuration = progress;
				initPlayer();
				playNETMusic(songInfo, false);
			} else {
				playSeekToMusic(progress);
			}
		}

	}

	/**
	 * 播放快进歌曲
	 * 
	 * @param progress
	 */
	private void playSeekToMusic(int progress) {
		if (player != null && player.isPlaying()) {
			player.pause();
			player.seekTo(progress);
			player.start();
		}
	}

	private class PlayerRunable implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(100);
					if (player != null && player.isPlaying()) {

						if (songInfo != null) {
							songInfo.setPlayProgress(player
									.getCurrentPosition());
							SongMessage msg = new SongMessage();
							msg.setSongInfo(songInfo);
							msg.setType(SongMessage.SERVICEPLAYINGMUSIC);
							ObserverManage.getObserver().setMessage(msg);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SongMessage) {
			SongMessage songMessage = (SongMessage) data;
			if (songMessage.getType() == SongMessage.SERVICEPLAYMUSIC) {
				playMusic(songMessage.getSongInfo());
			} else if (songMessage.getType() == SongMessage.INITMUSIC) {
				initMusic();
			} else if (songMessage.getType() == SongMessage.SERVICEPAUSEMUSIC) {
				initMusic();
			} else if (songMessage.getType() == SongMessage.SERVICEPLAYINIT) {
				initPlayer();
			} else if (songMessage.getType() == SongMessage.SERVICESEEKTOMUSIC) {
				int progress = songMessage.getProgress();
				seekTo(progress);
			}
		}
	}
}
