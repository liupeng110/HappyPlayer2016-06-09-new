package com.happy.ui;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import name.teze.layout.lib.SwipeBackActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.happy.adapter.PopupLrcPlayListAdapter;
import com.happy.common.Constants;
import com.happy.db.SongDB;
import com.happy.logger.LoggerManage;
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
import com.happy.util.DataUtil;
import com.happy.util.ImageLoadUtil;
import com.happy.util.KscLyricsParserUtil;
import com.happy.util.KscUtil;
import com.happy.util.SingerPhotoUtil;
import com.happy.util.ToastUtil;
import com.happy.widget.ButtonPressRelativeLayout;
import com.happy.widget.LrcSeekBar;
import com.happy.widget.lrc.KscManyLineLyricsViewParent;
import com.happy.widget.lrc.LrcKscManyLineLyricsView;

@SuppressLint("NewApi")
public class LrcActivity extends SwipeBackActivity implements Observer {
	// /**
	// * 背景图片
	// */
	private RelativeLayout backgroundImage;

	private LoggerManage logger;

	private SkinInfo skinInfo;

	private ButtonPressRelativeLayout buttonPressRelativeLayout;
	/**
	 * 随机播放
	 */
	private ImageView mode_random_button;
	/**
	 * 顺序播放
	 */
	private ImageView mode_all1_button;
	/**
	 * 单曲循环
	 */
	private ImageView mode_single_button;

	/**
	 * 循环播放
	 */
	private ImageView mode_all_button;

	/**
	 * 播放模式
	 */
	private int playModel = Constants.playModel;
	/**
	 * 当前播放歌曲
	 */
	private SongInfo mSongInfo;

	/**
	 * 播放进度条
	 */
	private LrcSeekBar playerSeekBar;

	/**
	 * 判断其是否是正在拖动
	 */
	private boolean isStartTrackingTouch = false;

	/**
	 * 标题
	 */
	private TextView titleTextView;
	/**
	 * 播放进度
	 */
	private TextView songProgressTextView;
	/**
	 * 歌曲进度
	 */
	private TextView songSizeTextView;
	/**
	 * 喜欢面板
	 */
	private RelativeLayout likeParentRelativeLayout;
	/**
	 * 喜欢
	 */
	private ImageView likeImageView;
	/**
	 * 不喜欢
	 */
	private ImageView unlikeImageView;

	/**
	 * 下载面板
	 */
	private RelativeLayout downloadParentRelativeLayout;
	/**
	 * 播放按钮
	 */
	private ImageView playImageView;
	/**
	 * 暂停按钮
	 */
	private ImageView pauseImageView;
	/**
	 * 播放列表按钮
	 */
	private ImageView playlistImageView;
	/**
	 * 上一首
	 */
	private ImageView preImageView;
	/**
	 * 下一首
	 */
	private ImageView nextImageView;
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
	 * 播放列表弹出窗口
	 */
	private PopupWindow playlistPopupWindow;
	/**
	 * 弹出窗口播放列表
	 */
	private ListView popPlayListView;

	private TextView popPlaysumTextTextView;

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
	private LrcKscManyLineLyricsView kscManyLineLyricsView;

	/**
	 * 颜色面板
	 */
	private ImageView imageviews[];
	/**
	 * 颜色面板选择状态
	 */
	private ImageView flagimageviews[];
	/**
	 * 歌词菜单弹出窗口
	 */
	private PopupWindow lrcMenuPopupWindowDialog;
	/**
	 * 窗口隐藏时间
	 */
	private int hideTime = -1;

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
				playerSeekBar.setEnabled(false);
				playerSeekBar.setProgress(0);
				playerSeekBar.setSecondaryProgress(0);
				playerSeekBar.setMax(0);
				titleTextView.setText("乐乐音乐-传播好音乐");
				songProgressTextView.setText("00:00");
				songSizeTextView.setText("00:00");
				likeParentRelativeLayout.setVisibility(View.INVISIBLE);
				downloadParentRelativeLayout.setVisibility(View.INVISIBLE);
				playImageView.setVisibility(View.VISIBLE);
				pauseImageView.setVisibility(View.INVISIBLE);

				kscManyLineLyricsView.setHasKsc(false);

				return;
			} else {
				if (songMessageTemp.getType() == SongMessage.INITMUSIC) {

					mSongInfo = songInfo;
					singerPhotoDrawable = null;
					if (singerPhotoThread != null) {
						singerPhotoThread = null;
						singerBackgroundImage.setVisibility(View.INVISIBLE);
					}
					// playerSeekBar.setEnabled(false);
					playerSeekBar.setEnabled(true);
					playerSeekBar.setMax((int) songInfo.getDuration());
					playerSeekBar.setSecondaryProgress(0);
					playerSeekBar.setProgress((int) songInfo.getPlayProgress());
					titleTextView.setText(songInfo.getDisplayName());
					songProgressTextView.setText(formatTime((int) songInfo
							.getPlayProgress()));
					songSizeTextView.setText(formatTime((int) songInfo
							.getDuration()));

					if (songInfo.getDownloadStatus() == SongInfo.DOWNLOADED) {
						likeParentRelativeLayout.setVisibility(View.VISIBLE);
						downloadParentRelativeLayout
								.setVisibility(View.INVISIBLE);
						if (songInfo.getIslike() == SongInfo.LIKE) {
							likeImageView.setVisibility(View.VISIBLE);
							unlikeImageView.setVisibility(View.INVISIBLE);
						} else {
							likeImageView.setVisibility(View.INVISIBLE);
							unlikeImageView.setVisibility(View.VISIBLE);
						}

					} else {
						likeParentRelativeLayout.setVisibility(View.INVISIBLE);
						downloadParentRelativeLayout
								.setVisibility(View.VISIBLE);
					}

					if (MediaManage.PLAYING == MediaManage.getMediaManage(
							LrcActivity.this).getPlayStatus()) {
						playImageView.setVisibility(View.INVISIBLE);
						pauseImageView.setVisibility(View.VISIBLE);
					} else {
						playImageView.setVisibility(View.VISIBLE);
						pauseImageView.setVisibility(View.INVISIBLE);
					}

					SingerPhotoUtil.loadSingerPhotoImage(LrcActivity.this,
							singerBackgroundImage, songInfo.getSid(),
							songInfo.getSingerPIC(), songInfo.getSinger());

					KscUtil.loadKsc(LrcActivity.this, songInfo.getSid(),
							songInfo.getTitle(), songInfo.getSinger(),
							songInfo.getDisplayName(), songInfo.getKscUrl(),
							SongMessage.KSCTYPELRC);

					kscManyLineLyricsView.setHasKsc(false);

				} else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYMUSIC) {

					playImageView.setVisibility(View.INVISIBLE);
					pauseImageView.setVisibility(View.VISIBLE);

				} else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYINGMUSIC) {

					songProgressTextView.setText(formatTime((int) songInfo
							.getPlayProgress()));
					if (!isStartTrackingTouch) {
						playerSeekBar.setProgress((int) songInfo
								.getPlayProgress());
						// playerSeekBar.setSecondaryProgress(0);
					}

					if (kscManyLineLyricsView.getHasKsc()) {

						kscManyLineLyricsView.showLrc((int) songInfo
								.getPlayProgress());
					}

				} else if (songMessageTemp.getType() == SongMessage.SERVICEPAUSEEDMUSIC) {
					playImageView.setVisibility(View.VISIBLE);
					pauseImageView.setVisibility(View.INVISIBLE);

					playerSeekBar.setProgress((int) songInfo.getPlayProgress());
					songProgressTextView.setText(formatTime((int) songInfo
							.getPlayProgress()));

					if (kscManyLineLyricsView.getHasKsc()) {

						kscManyLineLyricsView.showLrc((int) songInfo
								.getPlayProgress());
					}

				} else if (songMessageTemp.getType() == SongMessage.ERRORMUSIC) {

				} else if (songMessageTemp.getType() == SongMessage.SERVICEERRORMUSIC) {

				} else if (songMessageTemp.getType() == SongMessage.UPDATEMUSIC) {
					long max = songInfo.getDuration();
					float downloadProgress = songInfo.getDownloadProgress();
					long fileSize = songInfo.getSize();
					if (fileSize <= downloadProgress) {
						playerSeekBar.setSecondaryProgress(0);
					} else
					playerSeekBar.setSecondaryProgress((int) (downloadProgress
							/ fileSize * max));
				} else if (songMessageTemp.getType() == SongMessage.SERVICEDOWNLOADFINISHED) {
					playerSeekBar.setSecondaryProgress(0);
				}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lrc);
		initComponent();
		initSkin();
		initData();
		ActivityManage.getInstance().addActivity(this);
		ObserverManage.getObserver().addObserver(this);
		initStatus();
	}

	/**
	 * 初始化状态栏
	 */
	private void initStatus() {
		if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
					| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
			window.setNavigationBarColor(Color.TRANSPARENT);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT,
					getStatusBarHeight(this));
			RelativeLayout statusView = (RelativeLayout) findViewById(R.id.statusView);
			statusView.setLayoutParams(lp);
			statusView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		SongInfo songInfo = MediaManage.getMediaManage(this).getSongInfo();
		SongMessage songMessage = new SongMessage();
		songMessage.setSongInfo(songInfo);
		songMessage.setType(SongMessage.INITMUSIC);
		Message msg = new Message();
		msg.obj = songMessage;
		songHandler.sendMessage(msg);
	}

	private void initComponent() {

		backgroundImage = (RelativeLayout) findViewById(R.id.backgroundImage);

		logger = LoggerManage.getZhangLogger(this);

		buttonPressRelativeLayout = (ButtonPressRelativeLayout) findViewById(R.id.title_backParent);
		buttonPressRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		playerSeekBar = (LrcSeekBar) findViewById(R.id.playerSeekBar);

		mode_random_button = (ImageView) findViewById(R.id.mode_random_button);
		mode_all1_button = (ImageView) findViewById(R.id.mode_all1_button);
		mode_single_button = (ImageView) findViewById(R.id.mode_single_button);
		mode_all_button = (ImageView) findViewById(R.id.mode_all_button);

		playModelHandler.sendEmptyMessage(0);

		titleTextView = (TextView) findViewById(R.id.title);
		songProgressTextView = (TextView) findViewById(R.id.songProgress);
		songSizeTextView = (TextView) findViewById(R.id.songSize);
		likeParentRelativeLayout = (RelativeLayout) findViewById(R.id.likeParent);
		likeParentRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mSongInfo == null)
					return;
				if (mSongInfo.getIslike() == SongInfo.LIKE) {
					likeImageView.setVisibility(View.INVISIBLE);
					unlikeImageView.setVisibility(View.VISIBLE);
					mSongInfo.setIslike(SongInfo.UNLIKE);

					ToastUtil.showCenterTextToast(LrcActivity.this, "取消喜欢");

					SongDB.getSongInfoDB(LrcActivity.this).updatLikeSong(
							mSongInfo.getSid(), SongInfo.UNLIKE);

					SongMessage songMessage = new SongMessage();
					songMessage.setSongInfo(mSongInfo);
					songMessage.setType(SongMessage.LOCALUNLIKEMUSIC);

					ObserverManage.getObserver().setMessage(songMessage);

				} else {
					likeImageView.setVisibility(View.VISIBLE);
					unlikeImageView.setVisibility(View.INVISIBLE);
					mSongInfo.setIslike(SongInfo.LIKE);

					ToastUtil.showCenterTextToast(LrcActivity.this, "添加喜欢");
					SongDB.getSongInfoDB(LrcActivity.this).updatLikeSong(
							mSongInfo.getSid(), SongInfo.LIKE);
					SongMessage songMessage = new SongMessage();
					songMessage.setSongInfo(mSongInfo);
					songMessage.setType(SongMessage.LOCALADDLIKEMUSIC);

					ObserverManage.getObserver().setMessage(songMessage);
				}

			}
		});
		likeImageView = (ImageView) findViewById(R.id.like);
		unlikeImageView = (ImageView) findViewById(R.id.unlike);
		downloadParentRelativeLayout = (RelativeLayout) findViewById(R.id.downloadParent);

		playImageView = (ImageView) findViewById(R.id.playing_button);
		playImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PLAYMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});
		pauseImageView = (ImageView) findViewById(R.id.pause_button);
		pauseImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PAUSEMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});

		singerBackgroundImage = (ImageView) findViewById(R.id.singerBackgroundImage);
		playlistImageView = (ImageView) findViewById(R.id.playlist_buttom);
		playlistImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				loadPlayListData();
			}
		});

		playerSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// // 拖动条进度改变的时候调用
				if (isStartTrackingTouch) {
					int progress = playerSeekBar.getProgress();
					// 往弹出窗口传输相关的进度
					playerSeekBar.popupWindowShow(progress, playerSeekBar,
							kscManyLineLyricsView.getTimeLrc(progress));

				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				isStartTrackingTouch = true;
				int progress = playerSeekBar.getProgress();
				// 往弹出窗口传输相关的进度
				playerSeekBar.popupWindowShow(progress, playerSeekBar,
						kscManyLineLyricsView.getTimeLrc(progress));
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// 拖动条停止拖动的时候调用
				playerSeekBar.popupWindowDismiss();

				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.SEEKTOMUSIC);
				songMessage.setProgress(playerSeekBar.getProgress());
				ObserverManage.getObserver().setMessage(songMessage);

				new Thread() {

					@Override
					public void run() {
						try {
							// 延迟100ms才更新进度，防止歌曲正在播放会出现进度条闪屏
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						isStartTrackingTouch = false;
					}

				}.start();
			}
		});

		KscManyLineLyricsViewParent kscManyLineLyricsViewParent = (KscManyLineLyricsViewParent) findViewById(R.id.kscManyLineLyricsViewParent);
		kscManyLineLyricsView = (LrcKscManyLineLyricsView) findViewById(R.id.kscManyLineLyricsView);
		kscManyLineLyricsViewParent
				.setVerticalScrollChildView(kscManyLineLyricsView);

		// kscManyLineLyricsView.setOnLrcClickListener(new OnLrcClickListener()
		// {
		//
		// @Override
		// public void onClick() {
		// initLrcMenuPopupWindowInstance();
		// }
		// });

		preImageView = (ImageView) findViewById(R.id.pre_button);
		preImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PREMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});

		nextImageView = (ImageView) findViewById(R.id.next_button);
		nextImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.NEXTMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});
	}

	/**
	 * 显示歌词设置菜单
	 * 
	 * @param v
	 */
	public void showLrcMenuDialog(View v) {
		initLrcMenuPopupWindowInstance();
	}

	/***
	 * 初始化歌词菜单窗口
	 */
	protected void initLrcMenuPopupWindowInstance() {
		if (lrcMenuPopupWindowDialog != null
				&& lrcMenuPopupWindowDialog.isShowing()) {
			lrcMenuPopupWindowDialog.dismiss();
		} else {
			initLrcMenuDialogPopuptWindow();

			if (hideTime < 0) {
				hideTime = 4000;
				songHandler.post(upDateVol);
			} else {
				hideTime = 4000;
			}
		}
	}

	Runnable upDateVol = new Runnable() {

		@Override
		public void run() {
			if (hideTime >= 0) {
				hideTime -= 200;
				songHandler.postDelayed(upDateVol, 200);
			} else {
				if (lrcMenuPopupWindowDialog != null
						&& lrcMenuPopupWindowDialog.isShowing()) {
					lrcMenuPopupWindowDialog.dismiss();
				}
			}

		}
	};

	private void initLrcMenuDialogPopuptWindow() {

		int length = Constants.lrcColorStr.length;
		imageviews = new ImageView[length];
		flagimageviews = new ImageView[length];
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View popupWindow = layoutInflater.inflate(R.layout.lrc_menu, null);

		popupWindow.setFocusableInTouchMode(true);

		int i = 0;
		imageviews[i] = (ImageView) popupWindow.findViewById(R.id.colorpanel0);
		flagimageviews[i] = (ImageView) popupWindow
				.findViewById(R.id.select_flag0);
		flagimageviews[i].setVisibility(View.INVISIBLE);
		imageviews[i].setOnClickListener(new MyImageViewOnClickListener());
		imageviews[i]
				.setBackgroundColor(parserColor(Constants.lrcColorStr[i++]));
		imageviews[i] = (ImageView) popupWindow.findViewById(R.id.colorpanel1);
		flagimageviews[i] = (ImageView) popupWindow
				.findViewById(R.id.select_flag1);
		flagimageviews[i].setVisibility(View.INVISIBLE);
		imageviews[i].setOnClickListener(new MyImageViewOnClickListener());
		imageviews[i]
				.setBackgroundColor(parserColor(Constants.lrcColorStr[i++]));
		imageviews[i] = (ImageView) popupWindow.findViewById(R.id.colorpanel2);
		flagimageviews[i] = (ImageView) popupWindow
				.findViewById(R.id.select_flag2);
		flagimageviews[i].setVisibility(View.INVISIBLE);
		imageviews[i].setOnClickListener(new MyImageViewOnClickListener());
		imageviews[i]
				.setBackgroundColor(parserColor(Constants.lrcColorStr[i++]));
		imageviews[i] = (ImageView) popupWindow.findViewById(R.id.colorpanel3);
		flagimageviews[i] = (ImageView) popupWindow
				.findViewById(R.id.select_flag3);
		flagimageviews[i].setVisibility(View.INVISIBLE);
		imageviews[i].setOnClickListener(new MyImageViewOnClickListener());
		imageviews[i]
				.setBackgroundColor(parserColor(Constants.lrcColorStr[i++]));
		imageviews[i] = (ImageView) popupWindow.findViewById(R.id.colorpanel4);
		flagimageviews[i] = (ImageView) popupWindow
				.findViewById(R.id.select_flag4);
		flagimageviews[i].setVisibility(View.INVISIBLE);
		imageviews[i].setOnClickListener(new MyImageViewOnClickListener());
		imageviews[i]
				.setBackgroundColor(parserColor(Constants.lrcColorStr[i++]));
		imageviews[i] = (ImageView) popupWindow.findViewById(R.id.colorpanel5);
		flagimageviews[i] = (ImageView) popupWindow
				.findViewById(R.id.select_flag5);
		flagimageviews[i].setVisibility(View.INVISIBLE);
		imageviews[i].setOnClickListener(new MyImageViewOnClickListener());
		imageviews[i]
				.setBackgroundColor(parserColor(Constants.lrcColorStr[i++]));

		flagimageviews[Constants.lrcColorIndex].setVisibility(View.VISIBLE);

		final LrcSeekBar fontSizeSeekBar = (LrcSeekBar) popupWindow
				.findViewById(R.id.fontSizeSeekBar);

		fontSizeSeekBar.setMax(Constants.lrcFontMaxSize
				- Constants.lrcFontMinSize);
		fontSizeSeekBar.setSecondaryProgress(0);
		fontSizeSeekBar.setProgress(Constants.lrcFontSize
				- Constants.lrcFontMinSize);

		fontSizeSeekBar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar arg0, int arg1,
							boolean arg2) {
						hideTime = 5000;
						// 过快刷新，导致页面闪屏
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// // 拖动条进度改变的时候调用
						Constants.lrcFontSize = Constants.lrcFontMinSize
								+ fontSizeSeekBar.getProgress();

						// 通知歌词界面去刷新view

						MessageIntent messageIntent = new MessageIntent();
						messageIntent
								.setAction(MessageIntent.KSCMANYLINEFONTSIZE);
						ObserverManage.getObserver().setMessage(messageIntent);

						new Thread() {

							@Override
							public void run() {
								DataUtil.saveValue(LrcActivity.this,
										Constants.lrcFontSize_KEY,
										Constants.lrcFontSize);
							}

						}.start();
					}

					@Override
					public void onStartTrackingTouch(SeekBar arg0) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar arg0) {
					}
				});
		ImageButton lyricDecrease = (ImageButton) popupWindow
				.findViewById(R.id.lyric_decrease);

		lyricDecrease.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideTime = 5000;

				int fontSize = fontSizeSeekBar.getProgress();
				fontSize = fontSize - 10;
				if (fontSize <= 0) {
					fontSize = 0;
				}

				fontSizeSeekBar.setProgress(fontSize);

				// if (Constants.lrcFontSize <= Constants.lrcFontMinSize) {
				// Constants.lrcFontSize = Constants.lrcFontMinSize;
				// } else {
				// Constants.lrcFontSize = Constants.lrcFontSize - 10;
				// }
				//
				// fontSizeSeekBar.setProgress(Constants.lrcFontSize);
				//
				// new Thread() {
				//
				// @Override
				// public void run() {
				// DataUtil.saveValue(LrcActivity.this,
				// Constants.lrcFontSize_KEY,
				// Constants.lrcFontSize);
				// }
				//
				// }.start();
			}
		});

		ImageButton lyricIncrease = (ImageButton) popupWindow
				.findViewById(R.id.lyric_increase);
		lyricIncrease.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideTime = 5000;
				// if (Constants.LRCFONTSIZE >= fontSizeSeekBar.getMax()) {
				// Constants.LRCFONTSIZE = fontSizeSeekBar.getMax();
				// } else {
				// Constants.LRCFONTSIZE = Constants.LRCFONTSIZE + 10;
				// }
				//
				// fontSizeSeekBar.setProgress(Constants.LRCFONTSIZE);
				//
				// new Thread() {
				//
				// @Override
				// public void run() {
				// DataUtil.save(LrcViewActivity.this,
				// Constants.LRCFONTSIZE_KEY,
				// Constants.LRCFONTSIZE);
				// }
				//
				// }.start();

				int fontSize = fontSizeSeekBar.getProgress();
				fontSize = fontSize + 10;
				if (fontSize >= fontSizeSeekBar.getMax()) {
					fontSize = fontSizeSeekBar.getMax();
				}
				fontSizeSeekBar.setProgress(fontSize);

			}
		});

		// 初始化弹出窗口
		// DisplayMetrics dm = new DisplayMetrics();
		// dm = getResources().getDisplayMetrics();
		// int screenWidth = dm.widthPixels;

		lrcMenuPopupWindowDialog = new PopupWindow(popupWindow,
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(parserColor("#000000,240"));
		// 设置SelectPicPopupWindow弹出窗体的背景
		lrcMenuPopupWindowDialog.setBackgroundDrawable(dw);
		// mPopupWindowDialog.setFocusable(true);
		lrcMenuPopupWindowDialog.setOutsideTouchable(true);

		final int[] location = new int[2];
		kscManyLineLyricsView.getLocationOnScreen(location);

		lrcMenuPopupWindowDialog.showAtLocation(kscManyLineLyricsView,
				Gravity.NO_GRAVITY, location[0], location[1]
						+ kscManyLineLyricsView.getHeight());

		// int left = kscTwoLineLyricsView.getLeft();
		// int top = location[1] - kscTwoLineLyricsView.getHeight()
		// - kscTwoLineLyricsView.getHeight() / 4;
		// int width = left + kscTwoLineLyricsView.getWidth();
		// int height = top + kscTwoLineLyricsView.getHeight();
		// kscTwoLineLyricsView.layout(left, top, width, height);
		// kscTwoLineLyricsView.invalidate();
		//
		// RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
		// kscTwoLineLyricsView
		// .getLayoutParams();
		// params.leftMargin = 0;
		// params.topMargin = location[1] - kscTwoLineLyricsView.getHeight()
		// - kscTwoLineLyricsView.getHeight() / 4;
		// kscTwoLineLyricsView.setLayoutParams(params);
		//
		// playSeekbarParent.setVisibility(View.INVISIBLE);
		// footParent.setVisibility(View.INVISIBLE);

		// mPopupWindowDialog.setOnDismissListener(new OnDismissListener() {
		//
		// @Override
		// public void onDismiss() {
		//
		// // int left = kscTwoLineLyricsView.getLeft();
		// // int top = location[1] - kscTwoLineLyricsView.getHeight()
		// // + kscTwoLineLyricsView.getHeight() / 4;
		// // int width = left + kscTwoLineLyricsView.getWidth();
		// // int height = top + kscTwoLineLyricsView.getHeight();
		// // kscTwoLineLyricsView.layout(left, top, width, height);
		// // kscTwoLineLyricsView.invalidate();
		//
		// RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
		// kscTwoLineLyricsView
		// .getLayoutParams();
		// params.leftMargin = 0;
		// params.topMargin = location[1]
		// - kscTwoLineLyricsView.getHeight()
		// + kscTwoLineLyricsView.getHeight() / 4;
		// kscTwoLineLyricsView.setLayoutParams(params);
		//
		// playSeekbarParent.setVisibility(View.VISIBLE);
		// footParent.setVisibility(View.VISIBLE);
		// mPopupWindowDialog = null;
		// }
		// });
	}

	private class MyImageViewOnClickListener implements OnClickListener {

		public void onClick(View arg0) {

			// logger.i("颜色面板点击了");

			hideTime = 5000;
			int index = 0;
			int id = arg0.getId();
			switch (id) {
			case R.id.colorpanel0:
				index = 0;
				break;
			case R.id.colorpanel1:
				index = 1;
				break;
			case R.id.colorpanel2:
				index = 2;
				break;
			case R.id.colorpanel3:
				index = 3;
				break;
			case R.id.colorpanel4:
				index = 4;
				break;
			case R.id.colorpanel5:
				index = 5;
				break;
			default:
				break;
			}
			Constants.lrcColorIndex = index;
			for (int i = 0; i < imageviews.length; i++) {
				if (i == index)
					flagimageviews[i].setVisibility(View.VISIBLE);
				else
					flagimageviews[i].setVisibility(View.INVISIBLE);
			}

			MessageIntent messageIntent = new MessageIntent();
			messageIntent.setAction(MessageIntent.KSCMANYLINELRCCOLOR);
			ObserverManage.getObserver().setMessage(messageIntent);

			// kscTwoLineLyricsView.invalidate();
			// kscManyLineLyricsView.invalidate();

			new Thread() {

				@Override
				public void run() {
					DataUtil.saveValue(LrcActivity.this,
							Constants.lrcColorIndex_KEY,
							Constants.lrcColorIndex);
				}

			}.start();
		}
	}

	/**
	 * 加载歌曲列表数据
	 */
	protected void loadPlayListData() {
		getPlayListPopupWindowInstance();

		int[] location = new int[2];
		playlistImageView.getLocationOnScreen(location);

		playlistPopupWindow.showAtLocation(playlistImageView,
				Gravity.NO_GRAVITY, location[0], location[1]
						- playlistPopupWindow.getHeight());
	}

	/**
	 * 获取PopupWindow实例
	 */
	private void getPlayListPopupWindowInstance() {
		if (null != playlistPopupWindow) {
			playlistPopupWindow.dismiss();
			return;
		} else {
			initPlayListPopuptWindow();

			List<SongInfo> playlist = MediaManage.getMediaManage(
					LrcActivity.this).getPlaylist();

			popPlaysumTextTextView.setText("播放列表(" + playlist.size() + ")");

			PopupLrcPlayListAdapter adapter = new PopupLrcPlayListAdapter(
					LrcActivity.this, playlist, popPlayListView,
					playlistPopupWindow);
			popPlayListView.setAdapter(adapter);

			int playIndex = MediaManage.getMediaManage(LrcActivity.this)
					.getPlayIndex();
			if (playIndex != -1) {
				popPlayListView.setSelection(playIndex);
			}
		}
	}

	/**
	 * 初始化播放列表窗口
	 */
	private void initPlayListPopuptWindow() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		final View popupWindow = layoutInflater.inflate(
				R.layout.popup_lrc_playlist, null);

		playlistPopupWindow = new PopupWindow(popupWindow, getWindowManager()
				.getDefaultDisplay().getWidth() / 4 * 3, getWindowManager()
				.getDefaultDisplay().getHeight() / 3 * 2 - 80, true);

		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0xb0000000);
		playlistPopupWindow.setBackgroundDrawable(new BitmapDrawable());

		// 设置popWindow的显示和消失动画
		// mPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		playlistPopupWindow.setFocusable(true);
		// mPopupWindow.setOutsideTouchable(true);
		popupWindow.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// int bottomHeight = mMenu.getTop();
				int topHeight = popupWindow.findViewById(R.id.pop_layout)
						.getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					// y > bottomHeight ||
					if (topHeight > y) {
						playlistPopupWindow.dismiss();
					}
				}
				return true;
			}
		});

		// popWindow消失监听方法
		playlistPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				playlistPopupWindow = null;
			}
		});

		popPlayListView = (ListView) popupWindow
				.findViewById(R.id.playlistView);

		popPlaysumTextTextView = (TextView) popupWindow
				.findViewById(R.id.playsumText);
	}

	/**
	 * 设置显示播放模式的按钮
	 */
	private Handler playModelHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放
			switch (playModel) {
			case 0:
				mode_random_button.setVisibility(View.INVISIBLE);
				mode_all1_button.setVisibility(View.VISIBLE);
				mode_single_button.setVisibility(View.INVISIBLE);
				mode_all_button.setVisibility(View.INVISIBLE);

				break;
			case 1:
				mode_random_button.setVisibility(View.VISIBLE);
				mode_all1_button.setVisibility(View.INVISIBLE);
				mode_single_button.setVisibility(View.INVISIBLE);
				mode_all_button.setVisibility(View.INVISIBLE);

				break;
			case 2:
				mode_random_button.setVisibility(View.INVISIBLE);
				mode_all1_button.setVisibility(View.INVISIBLE);
				mode_single_button.setVisibility(View.INVISIBLE);
				mode_all_button.setVisibility(View.VISIBLE);

				break;
			case 3:

				mode_random_button.setVisibility(View.INVISIBLE);
				mode_all1_button.setVisibility(View.INVISIBLE);
				mode_single_button.setVisibility(View.VISIBLE);
				mode_all_button.setVisibility(View.INVISIBLE);

				break;
			}
		}
	};

	public void modelOnClick(View v) {
		switch (playModel) {
		case 0:
			playModel = 1;
			ToastUtil.showTextToast(LrcActivity.this, "随机播放");
			break;
		case 1:
			playModel = 2;
			ToastUtil.showTextToast(LrcActivity.this, "循环播放");
			break;
		case 2:
			playModel = 3;
			ToastUtil.showTextToast(LrcActivity.this, "单曲播放");
			break;
		case 3:
			playModel = 0;
			ToastUtil.showTextToast(LrcActivity.this, "顺序播放");
			break;
		}

		Constants.playModel = playModel;
		DataUtil.saveValue(LrcActivity.this, Constants.playModel_KEY,
				Constants.playModel);

		playModelHandler.sendEmptyMessage(0);

		MessageIntent messageIntent = new MessageIntent();
		messageIntent.setAction(MessageIntent.CHANGEMODE);
		ObserverManage.getObserver().setMessage(messageIntent);
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

	@Override
	public void onBackPressed() {
		finish();
	}

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
			} else if (songMessageTemp.getType() == SongMessage.UPDATEMUSIC || songMessageTemp.getType() == SongMessage.SERVICEDOWNLOADFINISHED) {
				Message msg = new Message();
				msg.obj = songMessageTemp;
				songHandler.sendMessage(msg);
			} else if (songMessageTemp.getType() == SongMessage.SINGERPHOTOLOADED) {
				singerPhotoLoaded();
			} else if (songMessageTemp.getType() == SongMessage.LRCKSCLOADED) {
				if (mSongInfo == null)
					return;
				if (!mSongInfo.getSid().equals(songMessageTemp.getSid())) {
					return;
				}
				String kscFilePath = songMessageTemp.getKscFilePath();
				String sid = songMessageTemp.getSid();

				initKscLrc(sid, kscFilePath, mSongInfo.getDuration(), true);
			} else if (songMessageTemp.getType() == SongMessage.LRCKSCDOWNLOADED) {
				if (mSongInfo == null)
					return;
				if (!mSongInfo.getSid().equals(songMessageTemp.getSid())) {
					return;
				}
				String sid = songMessageTemp.getSid();

				initKscLrc(sid, null, mSongInfo.getDuration(), false);

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
	 * 时间格式转换
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTime(int time) {

		time /= 1000;
		int minute = time / 60;
		// int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	@Override
	public void finish() {
		ObserverManage.getObserver().deleteObserver(this);
		singerPhotoDrawable = null;
		if (singerPhotoThread != null) {
			singerPhotoThread = null;
		}
		super.finish();
	}

	/**
	 * 解析颜色字符串
	 * 
	 * @param value
	 *            颜色字符串 #edf8fc,255
	 * @return
	 */
	private int parserColor(String value) {
		String regularExpression = ",";
		if (value.contains(regularExpression)) {
			String[] temp = value.split(regularExpression);

			int color = Color.parseColor(temp[0]);
			int alpha = Integer.valueOf(temp[1]);
			int red = (color & 0xff0000) >> 16;
			int green = (color & 0x00ff00) >> 8;
			int blue = (color & 0x0000ff);

			return Color.argb(alpha, red, green, blue);
		}
		return Color.parseColor(value);
	}
}
