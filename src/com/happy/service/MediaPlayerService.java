package com.happy.service;

import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.audiofx.PresetReverb;
import android.os.IBinder;

import com.happy.common.Constants;
import com.happy.logger.LoggerManage;
import com.happy.manage.MediaManage;
import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
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
				playInfoMusic(songInfo);
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
	 */
	private void playInfoMusic(SongInfo songInfo) {
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
				playInfoMusic(songMessage.getSongInfo());
			} else if (songMessage.getType() == SongMessage.INITMUSIC
					|| songMessage.getType() == SongMessage.SERVICEPAUSEMUSIC) {
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
