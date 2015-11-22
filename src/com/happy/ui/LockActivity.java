package com.happy.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import name.teze.layout.lib.SwipeBackActivity;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happy.common.Constants;
import com.happy.manage.ActivityManage;
import com.happy.manage.KscLyricsManage;
import com.happy.manage.MediaManage;
import com.happy.model.app.KscLyricsLineInfo;
import com.happy.model.app.MessageIntent;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.util.AniUtil;
import com.happy.util.ImageLoadUtil;
import com.happy.util.KscLyricsParserUtil;
import com.happy.util.KscUtil;
import com.happy.util.SingerPhotoUtil;
import com.happy.widget.lock.LockButtonRelativeLayout;
import com.happy.widget.lock.LockPalyOrPauseButtonRelativeLayout;
import com.happy.widget.lrc.KscManyLineLyricsView;
import com.happy.widget.lrc.KscManyLineLyricsViewParent;

/**
 * 屏锁歌词界面
 * 
 * @author zhangliangming
 * 
 */
public class LockActivity extends SwipeBackActivity implements Observer {
	public static boolean active = false;

	private Handler handler = new Handler();
	private Runnable myRunnable = new Runnable() {
		public void run() {
			// if (active) {
			KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
			// 屏锁
			if (km.inKeyguardRestrictedInputMode()) {
				handler.postDelayed(this, 100);
			} else {
				finish();
			}
			// }
		}
	};

	// /**
	// * 背景图片
	// */
	private RelativeLayout backgroundImage;

	private SkinInfo skinInfo;

	/**
	 * 滑动提示图标
	 */
	private ImageView lockImageView;
	private AnimationDrawable aniLoading;

	/**
	 * 歌名
	 */
	private TextView songNameTextView;
	/**
	 * 歌手
	 */
	private TextView songerTextView;
	/**
	 * 时间
	 */
	private TextView timeTextView;
	/**
	 * 日期
	 */
	private TextView dateTextView;
	/**
	 * 星期几
	 */
	private TextView dayTextView;

	private LockButtonRelativeLayout prewButton;
	private LockButtonRelativeLayout nextButton;

	private LockPalyOrPauseButtonRelativeLayout playOrPauseButton;

	private ImageView playImageView;

	private ImageView pauseImageView;

	/**
	 * 当前播放歌曲
	 */
	private SongInfo mSongInfo;

	/**
	 * 歌手写真图片
	 */
	private ImageView singerBackgroundImage;
	/**
	 * 歌手写真图片线程
	 */
	private Thread singerPhotoThread = null;
	/**
	 * 歌手写真图片
	 */
	private Drawable[] singerPhotoDrawable = null;

	/**
	 * 歌词解析
	 */
	private KscLyricsParserUtil kscLyricsParser;

	/**
	 * 歌词列表
	 */
	private TreeMap<Integer, KscLyricsLineInfo> lyricsLineTreeMap;

	/**
	 * 歌词视图
	 */
	private KscManyLineLyricsView kscManyLineLyricsView;

	private Handler songHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			SongMessage songMessageTemp = (SongMessage) msg.obj;
			SongInfo songInfo = songMessageTemp.getSongInfo();
			if (songInfo == null
					|| songMessageTemp.getType() == SongMessage.ERRORMUSIC
					|| songMessageTemp.getType() == SongMessage.SERVICEERRORMUSIC) {

				singerPhotoDrawable = null;
				if (singerPhotoThread != null) {
					singerPhotoThread = null;
					singerBackgroundImage.setVisibility(View.INVISIBLE);
				}

				kscManyLineLyricsView.setHasKsc(false);

				songNameTextView.setText("乐乐音乐");
				songerTextView.setText("传播好音乐");

				playImageView.setVisibility(View.VISIBLE);
				pauseImageView.setVisibility(View.INVISIBLE);

				playOrPauseButton.setPlayingProgress(0);
				playOrPauseButton.setMaxProgress(0);
				playOrPauseButton.invalidate();

				return;
			} else {
				if (songMessageTemp.getType() == SongMessage.INITMUSIC) {
					mSongInfo = songInfo;

					singerPhotoDrawable = null;
					if (singerPhotoThread != null) {
						singerPhotoThread = null;
						singerBackgroundImage.setVisibility(View.INVISIBLE);
					}

					SingerPhotoUtil.loadSingerPhotoImage(LockActivity.this,
							singerBackgroundImage, songInfo.getSid(),
							songInfo.getSingerPIC(), songInfo.getSinger());

					KscUtil.loadKsc(LockActivity.this, songInfo.getSid(),
							songInfo.getTitle(), songInfo.getSinger(),
							songInfo.getDisplayName(), songInfo.getKscUrl(),
							SongMessage.KSCTYPELOCK);

					kscManyLineLyricsView.setHasKsc(false);

					songNameTextView.setText(songInfo.getTitle());
					songerTextView.setText(songInfo.getSinger());

					if (MediaManage.PLAYING == MediaManage.getMediaManage(
							LockActivity.this).getPlayStatus()) {
						playImageView.setVisibility(View.INVISIBLE);
						pauseImageView.setVisibility(View.VISIBLE);
					} else {
						playImageView.setVisibility(View.VISIBLE);
						pauseImageView.setVisibility(View.INVISIBLE);
					}

					playOrPauseButton.setMaxProgress((int) songInfo
							.getDuration());
					playOrPauseButton.setPlayingProgress((int) songInfo
							.getPlayProgress());
					playOrPauseButton.invalidate();

				} else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYMUSIC) {

					if (pauseImageView.getVisibility() != View.VISIBLE) {
						pauseImageView.setVisibility(View.VISIBLE);
					}
					if (playImageView.getVisibility() != View.INVISIBLE) {
						playImageView.setVisibility(View.INVISIBLE);
					}

				} else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYINGMUSIC) {

					reshLrcView((int) songInfo.getPlayProgress());

					if (kscManyLineLyricsView.getHasKsc()) {

						kscManyLineLyricsView.showLrc((int) songInfo
								.getPlayProgress());
					}

				} else if (songMessageTemp.getType() == SongMessage.SERVICEPAUSEEDMUSIC) {

					if (kscManyLineLyricsView.getHasKsc()) {

						kscManyLineLyricsView.showLrc((int) songInfo
								.getPlayProgress());
					}

					reshLrcView((int) songInfo.getPlayProgress());

					pauseImageView.setVisibility(View.INVISIBLE);
					playImageView.setVisibility(View.VISIBLE);

				} else if (songMessageTemp.getType() == SongMessage.ERRORMUSIC) {

				} else if (songMessageTemp.getType() == SongMessage.SERVICEERRORMUSIC) {

				}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_lock);
		initComponent();
		setDate();
		initSkin();
		initData();
		ActivityManage.getInstance().addActivity(this);
		ObserverManage.getObserver().addObserver(this);
		initStatus();

		active = true;
	}

	/**
	 * 
	 * @param playProgress
	 *            根据当前歌曲播放进度，刷新歌词
	 */
	private void reshLrcView(int playProgress) {
		playOrPauseButton.setPlayingProgress(playProgress);
		playOrPauseButton.invalidate();
	}

	/**
	 * 设置时间
	 */
	private void setTime() {
		String str = "";
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
		Calendar lastDate = Calendar.getInstance();
		str = sdfTime.format(lastDate.getTime());
		timeTextView.setText(str);
	}

	/**
	 * 设置日期
	 */
	private void setDate() {

		String str = "";
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

		Calendar lastDate = Calendar.getInstance();
		str = sdfDate.format(lastDate.getTime());
		dateTextView.setText(str);
		str = sdfTime.format(lastDate.getTime());
		timeTextView.setText(str);

		String mWay = String.valueOf(lastDate.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "日";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}
		dayTextView.setText("星期" + mWay);

	}

	private void initComponent() {
		backgroundImage = (RelativeLayout) findViewById(R.id.backgroundImage);

		lockImageView = (ImageView) findViewById(R.id.tip_image);
		aniLoading = (AnimationDrawable) lockImageView.getBackground();

		songNameTextView = (TextView) findViewById(R.id.songName);
		songerTextView = (TextView) findViewById(R.id.songer);

		timeTextView = (TextView) findViewById(R.id.time);
		dateTextView = (TextView) findViewById(R.id.date);
		dayTextView = (TextView) findViewById(R.id.day);

		prewButton = (LockButtonRelativeLayout) findViewById(R.id.prev_button);

		prewButton.setOnClickListener(new ItemOnClick());

		nextButton = (LockButtonRelativeLayout) findViewById(R.id.next_button);
		nextButton.setOnClickListener(new ItemOnClick());

		playOrPauseButton = (LockPalyOrPauseButtonRelativeLayout) findViewById(R.id.play_pause_button);
		playOrPauseButton.setOnClickListener(new ItemOnClick());

		playImageView = (ImageView) findViewById(R.id.play);
		pauseImageView = (ImageView) findViewById(R.id.pause);

		singerBackgroundImage = (ImageView) findViewById(R.id.lockSingerBackgroundImage);
		KscManyLineLyricsViewParent kscManyLineLyricsViewParent = (KscManyLineLyricsViewParent) findViewById(R.id.kscManyLineLyricsViewParent);
		kscManyLineLyricsView = (KscManyLineLyricsView) findViewById(R.id.lockKscManyLineLyricsView);

		kscManyLineLyricsViewParent
				.setVerticalScrollChildView(kscManyLineLyricsView);
	}

	class ItemOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.prev_button:
				prev();
				break;
			case R.id.next_button:
				next();
				break;
			case R.id.play_pause_button:
				playOrPause();
				break;
			}
		}

		private void playOrPause() {
			if (MediaManage.getMediaManage(getApplicationContext())
					.getPlayStatus() == MediaManage.PLAYING) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PAUSEMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			} else {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PLAYMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		}

		private void next() {
			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.NEXTMUSIC);
			// 通知
			ObserverManage.getObserver().setMessage(songMessage);
		}

		private void prev() {
			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.PREMUSIC);
			// 通知
			ObserverManage.getObserver().setMessage(songMessage);
		}
	}

	private void initData() {
		AniUtil.startAnimation(aniLoading);

		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... arg0) {

				SongInfo songInfo = MediaManage.getMediaManage(
						LockActivity.this).getSongInfo();
				SongMessage songMessage = new SongMessage();
				songMessage.setSongInfo(songInfo);
				songMessage.setType(SongMessage.INITMUSIC);
				Message msg = new Message();
				msg.obj = songMessage;
				songHandler.sendMessage(msg);

				return null;
			}
		}.execute("");

	}

	private void initSkin() {
		skinInfo = Constants.skinInfo;
		// if (CrashApplication.bitmap != null) {
		// backgroundImage.setBackground(new BitmapDrawable(
		// CrashApplication.bitmap));
		// } else {
		// ImageUtil.loadImageFormFile(backgroundImage,
		// skinInfo.getBackgroundPath(), this, null);
		// }

		ImageLoadUtil.loadImageFormFile(skinInfo.getBackgroundPath(),
				backgroundImage, 0, true);
	}

	@SuppressLint("NewApi")
	private void initStatus() {
		Window window = getWindow();
		if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
					| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
					| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

			window.getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

			window.setStatusBarColor(Color.TRANSPARENT);
			window.setNavigationBarColor(Color.TRANSPARENT);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT,
					getStatusBarHeight(this));
			RelativeLayout statusView = (RelativeLayout) findViewById(R.id.statusView);
			statusView.setLayoutParams(lp);
			statusView.setVisibility(View.VISIBLE);
		} else {

			window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
					| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		}
	}

	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	@Override
	protected void onStart() {
		active = true;
		super.onStart();
	}

	@Override
	protected void onStop() {
		active = false;
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		active = false;
		handler.removeCallbacks(myRunnable);
		super.onDestroy();
	}

	@Override
	public void finish() {
		active = false;
		ObserverManage.getObserver().deleteObserver(this);
		AniUtil.stopAnimation(aniLoading);
		super.finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) { // 屏蔽按键
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 歌手写真图片加载完成
	 */
	private void singerPhotoLoaded() {
		if (singerPhotoThread == null) {
			singerPhotoThread = new Thread(new SingerPhotoRunable());
			singerPhotoThread.start();
		}
	}

	/**
	 * 歌手写真ui线程处理
	 */
	private Handler singerPhotoHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (singerPhotoThread != null) {
				if (singerPhotoDrawable == null) {
					return;
				}
				//
				// TransitionDrawable mTransitionDrawable = new
				// TransitionDrawable(
				// singerPhotoDrawable);
				// mTransitionDrawable.setCrossFadeEnabled(true);
				// mTransitionDrawable.startTransition(1000 * 2);
				// singerBackgroundImage.setBackgroundDrawable(mTransitionDrawable);
				singerBackgroundImage.setVisibility(View.VISIBLE);
				singerBackgroundImage
						.setBackgroundDrawable(singerPhotoDrawable[SingerPhotoUtil.index]);
			}
		}

	};

	/**
	 * 歌手写真图片切换线程
	 * 
	 * @author zhangliangming
	 * 
	 */
	private class SingerPhotoRunable implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					singerPhotoDrawable = SingerPhotoUtil
							.getSingerPhotoDrawable();
					if (singerPhotoDrawable == null
							|| singerPhotoDrawable.length == 0) {
						return;
					}
					if (SingerPhotoUtil.index >= singerPhotoDrawable.length)
						SingerPhotoUtil.index = 0;
					// Thread.sleep(100);
					if (singerPhotoDrawable == null) {
						return;
					}
					singerPhotoHandler.sendEmptyMessage(0);
					Thread.sleep(1000 * 30);
					if (singerPhotoDrawable == null) {
						return;
					}
					SingerPhotoUtil.index++;

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 初始化歌词
	 * 
	 * @param kscFilePath
	 * @param duration
	 * @param kscFilePath2
	 */
	private void initKscLrc(final String sid, final String kscFilePath,
			final long duration, final boolean isFile) {
		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... arg0) {

				if (isFile)
					kscLyricsParser = KscLyricsManage.getKscLyricsParser(sid,
							kscFilePath);
				else

					kscLyricsParser = KscLyricsManage
							.getKscLyricsParserByKscInputStream(sid);
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				lyricsLineTreeMap = kscLyricsParser.getLyricsLineTreeMap();
				if (lyricsLineTreeMap != null && lyricsLineTreeMap.size() != 0) {
					kscManyLineLyricsView.init((int) duration, kscLyricsParser);
					kscManyLineLyricsView.setHasKsc(true);

					if (mSongInfo != null) {
						kscManyLineLyricsView.showLrc((int) mSongInfo
								.getPlayProgress());
					}
				}

				// for (int i = 0; i < lyricsLineTreeMap.size(); i++) {
				// KscLyricsLineInfo kscLyricsLineInfo = lyricsLineTreeMap
				// .get(i);
				// logger.d(kscLyricsLineInfo.getLineLyrics());
				// }
			}

		}.execute("");
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			initSkin();
		} else if (data instanceof SongMessage) {
			SongMessage songMessageTemp = (SongMessage) data;
			if (songMessageTemp.getType() == SongMessage.INITMUSIC
					|| songMessageTemp.getType() == SongMessage.SERVICEPLAYMUSIC
					|| songMessageTemp.getType() == SongMessage.SERVICEPLAYINGMUSIC
					|| songMessageTemp.getType() == SongMessage.SERVICEPAUSEEDMUSIC
					|| songMessageTemp.getType() == SongMessage.ERRORMUSIC
					|| songMessageTemp.getType() == SongMessage.SERVICEERRORMUSIC) {
				Message msg = new Message();
				msg.obj = songMessageTemp;
				songHandler.sendMessage(msg);
			} else if (songMessageTemp.getType() == SongMessage.SINGERPHOTOLOADED) {
				singerPhotoLoaded();
			} else if (songMessageTemp.getType() == SongMessage.LOCKKSCLOADED) {
				if (mSongInfo == null)
					return;
				if (!mSongInfo.getSid().equals(songMessageTemp.getSid())) {
					return;
				}
				String kscFilePath = songMessageTemp.getKscFilePath();
				String sid = songMessageTemp.getSid();

				initKscLrc(sid, kscFilePath, mSongInfo.getDuration(), true);
			} else if (songMessageTemp.getType() == SongMessage.LOCKKSCDOWNLOADED) {
				if (mSongInfo == null)
					return;
				if (!mSongInfo.getSid().equals(songMessageTemp.getSid())) {
					return;
				}
				String sid = songMessageTemp.getSid();

				initKscLrc(sid, null, mSongInfo.getDuration(), false);

			}
		} else if (data instanceof MessageIntent) {
			MessageIntent messageIntent = (MessageIntent) data;
			if (messageIntent.getAction().equals(MessageIntent.SYSTEMTIME)) {
				setTime();
			}
		}
	}

}
