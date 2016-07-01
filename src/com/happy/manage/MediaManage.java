package com.happy.manage;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import android.content.Context;
import android.content.Intent;

import com.happy.common.Constants;
import com.happy.db.SongDB;
import com.happy.logger.LoggerManage;
import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.service.MediaPlayerService;
import com.happy.util.DataUtil;

/**
 * 播放器管理
 * 
 * @author Administrator
 * 
 */
public class MediaManage implements Observer {
	/***
	 * 本地歌曲列表
	 */
	public static int PLAYLISTTYPE_LOCALLIST = 0;

	/**
	 * 网络歌曲列表
	 */
	public static int PLAYLISTTYPE_NETLIST = 1;
	/**
	 * 下载歌曲列表
	 */
	public static int PLAYLISTTYPE_DOWNLOADLIST = 2;
	/**
	 * 搜索歌曲列表
	 */
	public static int PLAYLISTTYPE_NETSEARCHLIST = 3;
	/**
	 * 我的最爱歌曲列表
	 */
	public static int PLAYLISTTYPE_LOCALLIKELIST = 4;

	private static MediaManage _mediaManage;
	private Context context;
	/**
	 * 播放歌曲类型
	 */
	private int playListType = Constants.playListType;
	/**
	 * 播放列表-本地歌曲列表、网络歌曲列表、下载歌曲列表、搜索歌曲列表、我的最爱歌曲列表
	 */
	private List<SongInfo> playlist;
	/**
	 * 当前播放歌曲
	 */
	private SongInfo songInfo;

	/**
	 * 正在播放
	 */
	public static final int PLAYING = 0;
	/**
	 * 暂停
	 */
	public static final int PAUSE = 1;
	/**
	 * 播放歌曲状态
	 */
	private int playStatus = PAUSE;

	private LoggerManage logger;

	public MediaManage(Context context) {
		this.context = context;
		init(context);
	}

	public static MediaManage getMediaManage(Context context) {
		if (_mediaManage == null) {
			_mediaManage = new MediaManage(context);
		}
		return _mediaManage;
	}

	private void init(Context context) {
		logger = LoggerManage.getZhangLogger(context);
		ObserverManage.getObserver().addObserver(MediaManage.this);
	}

	/***
	 * 初始化播放歌曲数据
	 */
	public void initSongInfoData(int mPlayListType) {
		initPlayListData(mPlayListType);

		String sid = Constants.playInfoID;

		if (sid == null || sid.equals("")) {

			SongMessage msg = new SongMessage();
			msg.setSongInfo(null);
			msg.setType(SongMessage.INITMUSIC);
			ObserverManage.getObserver().setMessage(msg);

			return;
		}

		switch (mPlayListType) {
		case 0:
			// 本地歌曲列表

			songInfo = SongDB.getSongInfoDB(context).getSongInfo(sid);
			break;
		case 1:
			// 网络歌曲列表
			songInfo = SongDB.getSongInfoDB(context).getSongInfo(sid);
			break;
		case 2:
			// 下载歌曲列表
			songInfo = SongDB.getSongInfoDB(context).getSongInfo(sid,
					SongInfo.DOWNLOADSONG);
			break;
		case 3:
			// 搜索歌曲列表
			break;
		case 4:
			// 我的最爱歌曲列表
			songInfo = SongDB.getSongInfoDB(context).getSongInfo(sid);
			break;
		default:
			break;
		}

		SongMessage msg = new SongMessage();
		msg.setSongInfo(songInfo);
		msg.setType(SongMessage.INITMUSIC);
		ObserverManage.getObserver().setMessage(msg);
	}

	/**
	 * 初始化播放列表数据
	 */
	public void initPlayListData(int mPlayListType) {

		playListType = mPlayListType;
		switch (playListType) {
		case 0:
			// 本地歌曲列表
			playlist = SongDB.getSongInfoDB(context).getAllLocalSong();
			break;
		case 1:
			// 网络歌曲列表
			playlist = SongDB.getSongInfoDB(context).getAllRecommendSong(false);
			break;
		case 2:
			// 下载歌曲列表
			playlist = SongDB.getSongInfoDB(context).getDownloadSong(
					SongInfo.DOWNLOADED);
			break;
		case 3:
			// 搜索歌曲列表
			break;
		case 4:
			// 我的最爱歌曲列表
			playlist = SongDB.getSongInfoDB(context).getAllLikeSong();
			break;
		default:
			break;
		}

		DataUtil.saveValue(context, Constants.playListType_KEY,
				Constants.playListType);
	}

	/**
	 * 上一首
	 * 
	 * @param playModel
	 */
	private void preMusic(int playModel) {

		// 先判断当前的播放模式，再根据播放模式来获取上一首歌曲的索引

		boolean isInit = true;

		int playIndex = getPlayIndex();
		if (playIndex == -1) {
			songInfo = null;
			return;
		}
		if (songInfo.getType() == SongInfo.NETSONG
				|| songInfo.getType() == SongInfo.DOWNLOADSONG) {
			playModel = 0;
		}
		switch (playModel) {
		case 0:
			// 顺序播放
			playIndex--;
			if (playIndex < 0) {
				stopToPlay();
				return;
			}
			if (playlist.size() != 0) {
				songInfo = playlist.get(playIndex);
			} else {
				stopToPlay();
				return;
			}

			break;
		case 1:
			// 随机播放

			playIndex = new Random().nextInt(playlist.size());

			if (playlist.size() != 0) {
				songInfo = playlist.get(playIndex);
			} else {
				stopToPlay();
				return;
			}
		case 2:
			// 循环播放

			playIndex--;
			if (playIndex < 0) {
				playIndex = 0;
			}

			if (playlist.size() != 0) {
				songInfo = playlist.get(playIndex);
			} else {
				stopToPlay();
				return;
			}
		case 3:
			// 单曲播放

			break;
		}

		// playIndex == -1说明在单曲播放状态下歌曲被移除了
		if (songInfo == null || songInfo.getSid() == null || playIndex == -1) {
			stopToPlay();
			return;
		}

		// 保存歌曲索引

		Constants.playInfoID = songInfo.getSid();

		new Thread() {

			@Override
			public void run() {
				DataUtil.saveValue(context, Constants.playInfoID_KEY,
						Constants.playInfoID);
			}

		}.start();

		playInfoMusic(songInfo, isInit);
	}

	/**
	 * 随机播放歌曲
	 */
	private void randomMusic() {
		if (songInfo == null) {
			// 从本地获取歌曲
			if (playListType != PLAYLISTTYPE_LOCALLIST) {
				// 初始化播放列表
				initPlayListData(PLAYLISTTYPE_LOCALLIST);
			}
		}
		if (songInfo != null) {
			if (songInfo.getType() == SongInfo.LOCALSONG) {
				nextMusic(1);
			} else {
				nextMusic(0);
			}
		}
	}

	/**
	 * 播放下一首歌曲
	 * 
	 * @param playModel
	 *            播放模式
	 */
	private void nextMusic(int playModel) {

		// 先判断当前的播放模式，再根据播放模式来获取下一首歌曲的索引

		boolean isInit = true;

		// 顺序播放
		int playIndex = getPlayIndex();
		if (playIndex == -1) {

			songInfo = null;

			return;
		}
		if (songInfo.getType() == SongInfo.NETSONG
				|| songInfo.getType() == SongInfo.DOWNLOADSONG) {
			playModel = 0;
		}
		switch (playModel) {
		case 0:

			playIndex++;
			if (playIndex >= playlist.size()) {
				stopToPlay();
				return;
			}
			if (playlist.size() != 0) {
				songInfo = playlist.get(playIndex);
			} else {
				stopToPlay();
				return;
			}

			break;
		case 1:
			// 随机播放

			playIndex = new Random().nextInt(playlist.size());
			if (playlist.size() != 0) {
				songInfo = playlist.get(playIndex);
			} else {
				stopToPlay();
				return;
			}
			break;
		case 2:
			// 循环播放

			playIndex++;
			if (playIndex >= playlist.size()) {
				playIndex = 0;
			}

			if (playlist.size() != 0) {
				songInfo = playlist.get(playIndex);
			} else {
				stopToPlay();
				return;
			}

			break;
		case 3:
			// 单曲播放

			break;
		}
		// playIndex == -1说明在单曲播放状态下歌曲被移除了
		if (songInfo == null || songInfo.getSid() == null || playIndex == -1) {
			stopToPlay();
			return;
		}

		// 保存歌曲索引

		Constants.playInfoID = songInfo.getSid();

		new Thread() {

			@Override
			public void run() {
				DataUtil.saveValue(context, Constants.playInfoID_KEY,
						Constants.playInfoID);
			}

		}.start();

		playInfoMusic(songInfo, isInit);
	}

	/**
	 * 停止去播放歌曲
	 */
	private void stopToPlay() {
		// 结束播放保存歌曲索引

		Constants.playInfoID = "";

		new Thread() {

			@Override
			public void run() {
				DataUtil.saveValue(context, Constants.playInfoID_KEY,
						Constants.playInfoID);
			}

		}.start();

		songInfo = null;

		// 结束播放

		SongMessage msg = new SongMessage();
		msg.setSongInfo(null);
		msg.setType(SongMessage.INITMUSIC);
		ObserverManage.getObserver().setMessage(msg);

	}

	/**
	 * 播放歌曲
	 */
	private void playMusic() {
		if (songInfo != null) {

			playInfoMusic(songInfo, false);

		} else {
			SongMessage msg = new SongMessage();
			msg.setType(SongMessage.ERRORMUSIC);
			msg.setErrorMessage(SongMessage.ERRORMESSAGESONGNULL);
			ObserverManage.getObserver().setMessage(msg);
		}
	}

	/**
	 * 快进歌曲
	 * 
	 * @param progress
	 */
	private void seekTo(int progress) {
		if (songInfo == null) {
			return;
		}
		if (playStatus == PLAYING) {
			//
			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.SERVICESEEKTOMUSIC);
			songMessage.setProgress(progress);
			ObserverManage.getObserver().setMessage(songMessage);
		} else {
			songInfo.setPlayProgress(progress);
			// playInfoMusic(songInfo, false);
			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.SERVICEPAUSEEDMUSIC);
			songMessage.setSongInfo(songInfo);
			ObserverManage.getObserver().setMessage(songMessage);
		}
	}

	/**
	 * 暂停歌曲
	 */
	private void pauseMusic() {
		if (songInfo != null) {

			playStatus = PAUSE;

			SongMessage msg = new SongMessage();
			msg.setSongInfo(songInfo);
			msg.setType(SongMessage.SERVICEPAUSEMUSIC);
			ObserverManage.getObserver().setMessage(msg);

			if (MediaPlayerService.isServiceRunning) {
				context.stopService(new Intent(context,
						MediaPlayerService.class));
			}
			SongMessage msgTemp = new SongMessage();
			msgTemp.setSongInfo(songInfo);
			msgTemp.setType(SongMessage.SERVICEPAUSEEDMUSIC);
			ObserverManage.getObserver().setMessage(msgTemp);

		} else {
			SongMessage msg = new SongMessage();
			msg.setType(SongMessage.ERRORMUSIC);
			msg.setErrorMessage(SongMessage.ERRORMESSAGESONGNULL);
			ObserverManage.getObserver().setMessage(msg);
		}
	}

	/**
	 * 播放歌曲
	 * 
	 * @param songInfo
	 */
	private void playInfoMusic(final SongInfo mSongInfo, boolean isInit) {
		if (!MediaPlayerService.isServiceRunning) {
			context.startService(new Intent(context, MediaPlayerService.class));
		} else {
			SongMessage msg = new SongMessage();
			msg.setType(SongMessage.SERVICEPLAYINIT);
			ObserverManage.getObserver().setMessage(msg);
		}

		this.songInfo = mSongInfo;

		if (isInit) {

			songInfo.setPlayProgress(0);
			SongMessage msg = new SongMessage();
			msg.setSongInfo(songInfo);
			msg.setType(SongMessage.INITMUSIC);
			ObserverManage.getObserver().setMessage(msg);
		}

		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				playStatus = PLAYING;

				SongMessage msg2 = new SongMessage();
				msg2.setType(SongMessage.SERVICEPLAYMUSIC);
				msg2.setSongInfo(songInfo);
				ObserverManage.getObserver().setMessage(msg2);
			}

		}.start();

	}

	/**
	 * 获取当前播放歌曲的索引
	 * 
	 * @return
	 */
	public int getPlayIndex() {
		int index = -1;
		for (int i = 0; i < playlist.size(); i++) {
			SongInfo tempSongInfo = playlist.get(i);
			if (tempSongInfo.getSid().equals(Constants.playInfoID)) {
				return i;
			}
		}
		return index;
	}

	public int getPlayListType() {
		return playListType;
	}

	public SongInfo getSongInfo() {
		return songInfo;
	}

	public List<SongInfo> getPlaylist() {
		return playlist;
	}

	public void setPlayStatus(int playStatus) {
		this.playStatus = playStatus;
	}

	public int getPlayStatus() {
		return playStatus;
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SongMessage) {
			SongMessage songMessage = (SongMessage) data;
			if (songMessage.getType() == SongMessage.ADDMUSIC
					|| songMessage.getType() == SongMessage.LOCALDELMUSIC
					|| songMessage.getType() == SongMessage.LOCALADDLIKEMUSIC
					|| songMessage.getType() == SongMessage.LOCALUNLIKEMUSIC
					|| songMessage.getType() == SongMessage.LIKEDELMUSIC) {
				if (playListType == Constants.playListType) {
					// 列表的数据发生变化，更新播放列表
					// playListType = Constants.playListType;
					initPlayListData(playListType);
				}
			} else if (songMessage.getType() == SongMessage.PLAYMUSIC) {
				playMusic();
			} else if (songMessage.getType() == SongMessage.SEEKTOMUSIC) {
				int progress = songMessage.getProgress();
				seekTo(progress);
			} else if (songMessage.getType() == SongMessage.PLAYINFOMUSIC) {
				playInfoMusic(songMessage.getSongInfo(), true);
			} else if (songMessage.getType() == SongMessage.SERVICEPLAYINGMUSIC
					|| songMessage.getType() == SongMessage.SERVICEPAUSEEDMUSIC) {
				this.songInfo = songMessage.getSongInfo();
			} else if (songMessage.getType() == SongMessage.PAUSEMUSIC) {
				pauseMusic();
			} else if (songMessage.getType() == SongMessage.NEXTMUSIC) {
				if (songInfo == null) {
					return;
				}
				nextMusic(Constants.playModel);
			} else if (songMessage.getType() == SongMessage.RANDOMMUSIC) {
				randomMusic();
			} else if (songMessage.getType() == SongMessage.PREMUSIC) {
				if (songInfo == null) {
					return;
				}
				preMusic(Constants.playModel);
			} else if (songMessage.getType() == SongMessage.LOCALADDLIKEMUSIC
					|| songMessage.getType() == SongMessage.LOCALUNLIKEMUSIC) {
				if (songInfo == null || songMessage.getSongInfo() == null)
					return;
				if (songMessage.getSongInfo().getSid()
						.equals(songInfo.getSid())) {
					songInfo.setIslike(songMessage.getSongInfo().getIslike());
				}
			}
		}
	}

}
