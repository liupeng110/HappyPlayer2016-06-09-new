package com.happy.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

import com.eva.views.RoundedImageView;
import com.happy.adapter.SearchSongAdapter;
import com.happy.common.Constants;
import com.happy.fragment.TabMyFragment;
import com.happy.fragment.TabRecommendFragment;
import com.happy.iface.PageAction;
import com.happy.logger.LoggerManage;
import com.happy.manage.ActivityManage;
import com.happy.manage.MediaManage;
import com.happy.model.app.DownloadTask;
import com.happy.model.app.HttpResult;
import com.happy.model.app.MessageIntent;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.model.pc.AppInfo;
import com.happy.observable.ObserverManage;
import com.happy.receiver.PhoneReceiver;
import com.happy.service.FloatLrcService;
import com.happy.service.MediaPlayerService;
import com.happy.util.AlbumUtil;
import com.happy.util.DataUtil;
import com.happy.util.DateUtil;
import com.happy.util.DownloadManage;
import com.happy.util.DownloadThreadManage;
import com.happy.util.DownloadThreadPool;
import com.happy.util.DownloadThreadPool.IDownloadTaskEventCallBack;
import com.happy.util.HttpUtil;
import com.happy.util.ImageUtil;
import com.happy.util.ImageUtil.ImageLoadCallBack;
import com.happy.util.ToastUtil;
import com.happy.widget.AlartOneButtonDialog;
import com.happy.widget.AlartOneButtonDialog.ButtonDialogListener;
import com.happy.widget.BaseSeekBar;
import com.happy.widget.ButtonPressRelativeLayout;
import com.happy.widget.LoadRelativeLayout;
import com.happy.widget.MainTextView;
import com.happy.widget.SearchDelImageView;
import com.happy.widget.SearchEditText;
import com.happy.widget.TabPageIndicatorTextView;
import com.happy.widget.TitleTextView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements Observer {
	public static boolean SCREEN_OFF = false;

	/**
	 * 侧滑菜单
	 */
	private SlidingMenu slidingMenu;

	private MenuAction action;

	private LoggerManage logger;

	private SkinInfo skinInfo;

	/**
	 * -----------------------------------------------------底部播放界面开始------------
	 * ---------------------------
	 **/

	/**
	 * 底部bar背景
	 */
	private RelativeLayout playBarRelativeLayout;

	/**
	 * 底部bar歌手默认图标
	 */
	private RoundedImageView playBarDefArtist;

	private long mExitTime;
	/**
	 * 弹出menu菜单
	 */
	private RelativeLayout popMenuParent;
	/**
	 * 弹出菜单
	 */
	private LinearLayout popLayout;
	/**
	 * 菜单显示
	 */
	private boolean menuShow = false;
	/**
	 * 是否退出
	 */
	private boolean isExit = false;
	/**
	 * 动画进入
	 */
	private boolean isAniEnter = false;
	/**
	 * 动画退出
	 */
	private boolean isAniEXit = false;
	/**
	 * 菜单
	 */
	private ButtonPressRelativeLayout playBarSidebarParent;
	/**
	 * -----------------------------------------------------底部播放界面结束------------
	 * ---------------------------
	 **/

	/**
	 * -----------------------------------------------------sliding_menu界面开始
	 * ---------------------------------------
	 **/
	/**
	 * viewpager的背景图片
	 */
	private ImageView viewpagerBackground;
	/**
	 * 标题图标
	 */
	private ImageView titleIconImageView;
	/**
	 * 标题搜索按钮
	 */
	private ImageView searchImageView;

	private ButtonPressRelativeLayout titleSearchParent;

	/**
	 * 指示器背景
	 */
	private RelativeLayout tabPageIndicator;
	/**
	 * 指示器底部线
	 */
	private ImageView vline;
	/**
	 * 导航item
	 */
	private RelativeLayout[] indicatorItem;
	/**
	 * 导航标题提示文字
	 */
	private TabPageIndicatorTextView[] indicatorTip;
	/**
	 * 标签索引
	 */
	private int tabIndex = 0;

	/**
	 * 指示器偏移宽度
	 */
	private int offsetWidth = 0;

	/**
	 * 屏幕宽度
	 */
	private int screenWith = 0;

	/**
	 * 页面列表
	 */
	private ArrayList<Fragment> menuFragmentList;

	private ViewPager menuViewPager;
	private MenuTabFragmentPagerAdapter menuTabFragmentPagerAdapter;

	/**
	 * -----------------------------------------------------sliding_menu界面结束
	 * ---------------------------------------
	 **/

	/**
	 * -----------------------------------------------------sliding_content界面开始
	 * ---------------------------------------
	 **/
	/**
	 * 内容视图
	 */
	private LinearLayout contentRelativeLayout;
	/**
	 * 标题
	 */
	private TitleTextView titleTextView;
	/**
	 * 标题返回按钮
	 */
	private ButtonPressRelativeLayout backRelativeLayout;

	/**
	 * 背景图片
	 */
	private LinearLayout backgroundImage;
	/**
	 * 背景颜色
	 */
	private LinearLayout backgroundColor;

	/**
	 * -----------------------------------------------------sliding_content界面结束
	 * ---------------------------------------
	 **/

	/**
	 * 播放模式
	 */
	private int playModel = Constants.playModel;
	/**
	 * 随机播放
	 */
	private RelativeLayout menuShuffleParent;
	/**
	 * 顺序播放
	 */
	private RelativeLayout menuSequenceParent;
	/**
	 * 单曲循环
	 */
	private RelativeLayout menuRepeatoneParent;
	/**
	 * 循环播放
	 */
	private RelativeLayout menuRepeatParent;

	/**
	 * 设置显示播放模式的按钮
	 */
	private Handler playModelHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放
			switch (playModel) {
			case 0:
				menuSequenceParent.setVisibility(View.VISIBLE);
				menuShuffleParent.setVisibility(View.INVISIBLE);
				menuRepeatParent.setVisibility(View.INVISIBLE);
				menuRepeatoneParent.setVisibility(View.INVISIBLE);
				break;
			case 1:
				menuSequenceParent.setVisibility(View.INVISIBLE);
				menuShuffleParent.setVisibility(View.VISIBLE);
				menuRepeatParent.setVisibility(View.INVISIBLE);
				menuRepeatoneParent.setVisibility(View.INVISIBLE);
				break;
			case 2:
				menuSequenceParent.setVisibility(View.INVISIBLE);
				menuShuffleParent.setVisibility(View.INVISIBLE);
				menuRepeatParent.setVisibility(View.VISIBLE);
				menuRepeatoneParent.setVisibility(View.INVISIBLE);
				break;
			case 3:
				menuSequenceParent.setVisibility(View.INVISIBLE);
				menuShuffleParent.setVisibility(View.INVISIBLE);
				menuRepeatParent.setVisibility(View.INVISIBLE);
				menuRepeatoneParent.setVisibility(View.VISIBLE);
				break;
			}
			Constants.playModel = playModel;
			DataUtil.saveValue(MainActivity.this, Constants.playModel_KEY,
					Constants.playModel);
		}
	};
	/**
	 * -----------------------------------------------------通知栏
	 * ---------------------------------------
	 **/
	/**
	 * -----------------------------------------------------通知栏广播事件
	 * 
	 * ---------------------------------------
	 **/

	BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			doSomeThing(intent);
		}

	};

	/**
	 * -----------------------------------------------------通知栏
	 * ---------------------------------------
	 **/
	private NotificationManager mNotificationManager;

	/**
	 * ----底部播放歌曲操作面板------
	 */
	/**
	 * 专辑图片
	 */
	private RoundedImageView albumRoundedImageView;
	/**
	 * 进度条
	 */
	private BaseSeekBar seekBar;
	/**
	 * 歌曲名称
	 */
	private MainTextView songName;
	/**
	 * 歌手名称
	 */
	private MainTextView artistName;
	/**
	 * 暂停按钮
	 */
	private ButtonPressRelativeLayout pauseBarPlayParent;
	/**
	 * 播放按钮
	 */
	private ButtonPressRelativeLayout playBarPlayParent;
	/**
	 * 下一首按钮
	 */
	private ButtonPressRelativeLayout playBarNextParent;
	/**
	 * 专辑图片加载提示圈
	 */
	private ImageView artistLoadingImageView;

	/**
	 * 旋转动画
	 */
	private static Animation rotateAnimation;

	/**
	 * 歌曲处理
	 */
	private Handler songInfoHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			SongMessage songMessageTemp = (SongMessage) msg.obj;
			SongInfo songInfo = songMessageTemp.getSongInfo();
			if (songInfo == null
					|| songMessageTemp.getType() == SongMessage.ERRORMUSIC
					|| songMessageTemp.getType() == SongMessage.SERVICEERRORMUSIC) {

				seekBar.setProgress(0);
				seekBar.setSecondaryProgress(0);
				seekBar.setMax(0);

				songName.setText("乐乐音乐");
				artistName.setText("传播好音乐");

				playBarPlayParent.setVisibility(View.VISIBLE);
				pauseBarPlayParent.setVisibility(View.INVISIBLE);

				playBarDefArtist.setVisibility(View.VISIBLE);
				artistLoadingImageView.setVisibility(View.INVISIBLE);
				albumRoundedImageView.setVisibility(View.INVISIBLE);

				AlbumUtil.mSinger = "";

				if (artistLoadingImageView.getVisibility() == View.VISIBLE) {
					artistLoadingImageView.clearAnimation();
					artistLoadingImageView.setVisibility(View.INVISIBLE);
				}

				return;
			}
			if (songMessageTemp.getType() == SongMessage.INITMUSIC) {

				seekBar.setProgress(0);
				seekBar.setSecondaryProgress(0);
				seekBar.setMax((int) songInfo.getDuration());

				songName.setText(songInfo.getTitle());
				artistName.setText(songInfo.getSinger());

				playBarPlayParent.setVisibility(View.VISIBLE);
				pauseBarPlayParent.setVisibility(View.INVISIBLE);

				Bitmap bitmap = ImageUtil.loadImageFormFile(skinInfo
						.getPlayBarDefArtistIcon().getNormal(),
						MainActivity.this);
				if (bitmap != null) {
					playBarDefArtist
							.setImageDrawable(new BitmapDrawable(bitmap));
				}
				AlbumUtil.loadAlbumImage(MainActivity.this, playBarDefArtist,
						artistLoadingImageView, rotateAnimation,
						albumRoundedImageView, songInfo.getSid(),
						songInfo.getAlbumUrl(), songInfo.getSinger());

			} else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYMUSIC) {
				playBarPlayParent.setVisibility(View.INVISIBLE);
				pauseBarPlayParent.setVisibility(View.VISIBLE);

				if (artistLoadingImageView.getVisibility() == View.VISIBLE) {
					artistLoadingImageView.clearAnimation();
					artistLoadingImageView.setVisibility(View.INVISIBLE);
				}

			} else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYINGMUSIC) {

				seekBar.setProgress((int) songInfo.getPlayProgress());
				// seekBar.setSecondaryProgress(0);
			} else if (songMessageTemp.getType() == SongMessage.SERVICEPAUSEEDMUSIC) {
				playBarPlayParent.setVisibility(View.VISIBLE);
				pauseBarPlayParent.setVisibility(View.INVISIBLE);

				seekBar.setProgress((int) songInfo.getPlayProgress());

				if (artistLoadingImageView.getVisibility() == View.VISIBLE) {
					artistLoadingImageView.clearAnimation();
					artistLoadingImageView.setVisibility(View.INVISIBLE);
				}

			} else if (songMessageTemp.getType() == SongMessage.UPDATEMUSIC) {
				long max = songInfo.getDuration();
				float downloadProgress = songInfo.getDownloadProgress();
				long fileSize = songInfo.getSize();
				if (fileSize <= downloadProgress) {
					seekBar.setSecondaryProgress(0);
				} else
					seekBar.setSecondaryProgress((int) (downloadProgress
							/ fileSize * max));
			} else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYWAITING) {
				if (artistLoadingImageView.getVisibility() != View.VISIBLE) {
					artistLoadingImageView.setVisibility(View.VISIBLE);
					artistLoadingImageView.startAnimation(rotateAnimation);
				}
			} else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYWAITINGEND) {
				if (artistLoadingImageView.getVisibility() == View.VISIBLE) {
					artistLoadingImageView.clearAnimation();
					artistLoadingImageView.setVisibility(View.INVISIBLE);
				}
			} else if (songMessageTemp.getType() == SongMessage.SERVICEDOWNLOADFINISHED) {
				seekBar.setSecondaryProgress(0);
			}
		}

	};

	/**
	 * 状态栏播放器点击事件
	 */
	private BroadcastReceiver onClickNotifiReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.NOTIFIATION_APP_PLAYMUSIC)) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PLAYMUSIC);
				ObserverManage.getObserver().setMessage(songMessage);
			} else if (intent.getAction().equals(
					Constants.NOTIFIATION_APP_PAUSEMUSIC)) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PAUSEMUSIC);
				ObserverManage.getObserver().setMessage(songMessage);
			} else if (intent.getAction().equals(
					Constants.NOTIFIATION_APP_NEXTMUSIC)) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.NEXTMUSIC);
				ObserverManage.getObserver().setMessage(songMessage);
			} else if (intent.getAction().equals(
					Constants.NOTIFIATION_APP_PREMUSIC)) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PREMUSIC);
				ObserverManage.getObserver().setMessage(songMessage);
			} else if (intent.getAction().equals(
					Constants.NOTIFIATION_APP_CLOSE)) {
				close();
			} else if (intent.getAction().equals(
					Constants.NOTIFIATION_DESLRC_SHOW)) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.DESLRCSHOWORHIDE);
				ObserverManage.getObserver().setMessage(songMessage);
			} else if (intent.getAction().equals(
					Constants.NOTIFIATION_DESLRC_HIDE)) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.DESLRCSHOWORHIDE);
				ObserverManage.getObserver().setMessage(songMessage);
			} else if (intent.getAction().equals(
					Constants.NOTIFIATION_DESLRC_UNLOCK)) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.DESLRCLOCKORUNLOCK);
				ObserverManage.getObserver().setMessage(songMessage);
			}
		}

	};
	/**
	 * 状态栏播放器视图
	 */
	private RemoteViews notifyPlayBarRemoteViews;
	/**
	 * 
	 */
	private Notification mPlayBarNotification;

	/**
	 * 状态栏播放器
	 */
	private Handler notifyPlayBarHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 自定义界面
			if (notifyPlayBarRemoteViews == null) {
				notifyPlayBarRemoteViews = new RemoteViews(getPackageName(),
						R.layout.notify_playbarview);
			}

			Intent buttoncloseIntent = new Intent(
					Constants.NOTIFIATION_APP_CLOSE);
			PendingIntent pendcloseButtonIntent = PendingIntent.getBroadcast(
					MainActivity.this, 0, buttoncloseIntent, 0);

			notifyPlayBarRemoteViews.setOnClickPendingIntent(R.id.close,
					pendcloseButtonIntent);

			Intent buttonplayIntent = new Intent(
					Constants.NOTIFIATION_APP_PLAYMUSIC);
			PendingIntent pendplayButtonIntent = PendingIntent.getBroadcast(
					MainActivity.this, 0, buttonplayIntent, 0);

			notifyPlayBarRemoteViews.setOnClickPendingIntent(R.id.play,
					pendplayButtonIntent);

			Intent buttonpauseIntent = new Intent(
					Constants.NOTIFIATION_APP_PAUSEMUSIC);
			PendingIntent pendpauseButtonIntent = PendingIntent.getBroadcast(
					MainActivity.this, 0, buttonpauseIntent, 0);

			Intent buttonnextIntent = new Intent(
					Constants.NOTIFIATION_APP_NEXTMUSIC);
			PendingIntent pendnextButtonIntent = PendingIntent.getBroadcast(
					MainActivity.this, 0, buttonnextIntent, 0);

			notifyPlayBarRemoteViews.setOnClickPendingIntent(R.id.next,
					pendnextButtonIntent);

			Intent buttonprewtIntent = new Intent(
					Constants.NOTIFIATION_APP_PREMUSIC);
			PendingIntent pendprewButtonIntent = PendingIntent.getBroadcast(
					MainActivity.this, 0, buttonprewtIntent, 0);

			notifyPlayBarRemoteViews.setOnClickPendingIntent(R.id.prew,
					pendprewButtonIntent);

			SongMessage songMessage = (SongMessage) msg.obj;
			final SongInfo songInfo = songMessage.getSongInfo();
			if (songInfo != null) {

				if (songMessage.getType() == SongMessage.INITMUSIC) {

					notifyPlayBarRemoteViews.setTextViewText(R.id.songName,
							songInfo.getDisplayName());

					// notifyPlayBarRemoteViews.setImageViewResource(R.id.play,
					// R.drawable.statusbar_btn_play);
					// notifyPlayBarRemoteViews.setOnClickPendingIntent(R.id.play,
					// pendplayButtonIntent);

					notifyPlayBarRemoteViews.setViewVisibility(R.id.play,
							View.VISIBLE);
					notifyPlayBarRemoteViews.setViewVisibility(R.id.pause,
							View.INVISIBLE);

					Bitmap bm = AlbumUtil.getNotifiIcon(MainActivity.this,
							songInfo.getSid(), songInfo.getAlbumUrl(),
							songInfo.getSinger());
					if (bm != null) {
						notifyPlayBarRemoteViews.setImageViewBitmap(
								R.id.icon_pic, bm);// 显示专辑封面图片
					} else {
						notifyPlayBarRemoteViews.setImageViewResource(
								R.id.icon_pic, R.drawable.ic_launcher);// 显示专辑封面图片
					}

				} else if (songMessage.getType() == SongMessage.SERVICEPLAYMUSIC) {

					// notifyPlayBarRemoteViews.setImageViewResource(R.id.play,
					// R.drawable.statusbar_btn_pause);

					notifyPlayBarRemoteViews.setViewVisibility(R.id.play,
							View.INVISIBLE);
					notifyPlayBarRemoteViews.setViewVisibility(R.id.pause,
							View.VISIBLE);

					notifyPlayBarRemoteViews.setOnClickPendingIntent(
							R.id.pause, pendpauseButtonIntent);
				} else if (songMessage.getType() == SongMessage.SERVICEPAUSEEDMUSIC) {

					// notifyPlayBarRemoteViews.setImageViewResource(R.id.play,
					// R.drawable.statusbar_btn_play);

					notifyPlayBarRemoteViews.setViewVisibility(R.id.play,
							View.VISIBLE);
					notifyPlayBarRemoteViews.setViewVisibility(R.id.pause,
							View.INVISIBLE);

					notifyPlayBarRemoteViews.setOnClickPendingIntent(R.id.play,
							pendplayButtonIntent);
				}

				else if (songMessage.getType() == SongMessage.ERRORMUSIC) {
					notifyPlayBarRemoteViews.setTextViewText(R.id.songName,
							"乐乐音乐-传播好音乐");
					// notifyPlayBarRemoteViews.setImageViewResource(R.id.play,
					// R.drawable.statusbar_btn_play);

					notifyPlayBarRemoteViews.setViewVisibility(R.id.play,
							View.VISIBLE);
					notifyPlayBarRemoteViews.setViewVisibility(R.id.pause,
							View.INVISIBLE);

					notifyPlayBarRemoteViews.setOnClickPendingIntent(R.id.play,
							pendplayButtonIntent);
				} else if (songMessage.getType() == SongMessage.ALUBMPHOTOLOADED) {
					Bitmap bm = AlbumUtil.getNotifiIcon(MainActivity.this,
							songInfo.getSid(), songInfo.getAlbumUrl(),
							songInfo.getSinger());
					if (bm != null) {
						notifyPlayBarRemoteViews.setImageViewBitmap(
								R.id.icon_pic, bm);// 显示专辑封面图片
					} else {
						notifyPlayBarRemoteViews.setImageViewResource(
								R.id.icon_pic, R.drawable.ic_launcher);// 显示专辑封面图片
					}
				}
			} else {
				notifyPlayBarRemoteViews.setImageViewResource(R.id.icon_pic,
						R.drawable.ic_launcher);// 显示专辑封面图片

				notifyPlayBarRemoteViews.setTextViewText(R.id.songName,
						"乐乐音乐-传播好音乐");
				// notifyPlayBarRemoteViews.setImageViewResource(R.id.play,
				// R.drawable.statusbar_btn_play);

				notifyPlayBarRemoteViews.setViewVisibility(R.id.play,
						View.VISIBLE);
				notifyPlayBarRemoteViews.setViewVisibility(R.id.pause,
						View.INVISIBLE);
				notifyPlayBarRemoteViews.setOnClickPendingIntent(R.id.play,
						pendplayButtonIntent);
			}

			// 设置歌词显示状态和解锁歌词

			Intent buttonDesLrcUnlockIntent = new Intent(
					Constants.NOTIFIATION_DESLRC_UNLOCK);
			PendingIntent pendDesLrcUnlockIntent = PendingIntent.getBroadcast(
					MainActivity.this, 0, buttonDesLrcUnlockIntent, 0);

			notifyPlayBarRemoteViews.setOnClickPendingIntent(R.id.deslrcUnlock,
					pendDesLrcUnlockIntent);

			Intent buttonDesLrcHideIntent = new Intent(
					Constants.NOTIFIATION_DESLRC_HIDE);
			PendingIntent pendDesLrcHideIntent = PendingIntent.getBroadcast(
					MainActivity.this, 0, buttonDesLrcHideIntent, 0);

			notifyPlayBarRemoteViews.setOnClickPendingIntent(R.id.showdeslrc,
					pendDesLrcHideIntent);

			Intent buttonDesLrcShowIntent = new Intent(
					Constants.NOTIFIATION_DESLRC_SHOW);
			PendingIntent pendDesLrcShowIntent = PendingIntent.getBroadcast(
					MainActivity.this, 0, buttonDesLrcShowIntent, 0);

			notifyPlayBarRemoteViews.setOnClickPendingIntent(R.id.hidedeslrc,
					pendDesLrcShowIntent);

			if (Constants.showDesktopLyrics) {
				if (Constants.desktopLyricsIsMove) {
					notifyPlayBarRemoteViews.setViewVisibility(R.id.showdeslrc,
							View.VISIBLE);
					notifyPlayBarRemoteViews.setViewVisibility(R.id.hidedeslrc,
							View.INVISIBLE);
					notifyPlayBarRemoteViews.setViewVisibility(
							R.id.deslrcUnlock, View.INVISIBLE);
				} else {
					notifyPlayBarRemoteViews.setViewVisibility(
							R.id.deslrcUnlock, View.VISIBLE);
					notifyPlayBarRemoteViews.setViewVisibility(R.id.hidedeslrc,
							View.INVISIBLE);
					notifyPlayBarRemoteViews.setViewVisibility(R.id.showdeslrc,
							View.INVISIBLE);
				}
			} else {
				notifyPlayBarRemoteViews.setViewVisibility(R.id.hidedeslrc,
						View.VISIBLE);
				notifyPlayBarRemoteViews.setViewVisibility(R.id.showdeslrc,
						View.INVISIBLE);
				notifyPlayBarRemoteViews.setViewVisibility(R.id.deslrcUnlock,
						View.INVISIBLE);
			}

			mPlayBarNotification.contentView = notifyPlayBarRemoteViews;

			// mRemoteViews.setOnClickPendingIntent(R.id.play,
			// playPendingIntent());

			mNotificationManager.notify(notificationPlayBarId,
					mPlayBarNotification);

		}
	};

	private AudioManager mAudioManager;
	private ComponentName mRemoteControlResponder;

	/**
	 * ----底部播放歌曲操作面板------
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mRemoteControlResponder = new ComponentName(getPackageName(),
				PhoneReceiver.class.getName());

		init();
		initPlayBar();
		initSlidingMenu();
		initSlidingContent();
		initPlayModel();
		initSkin();
		initReceiver();
		initPlayBarNotifi();
		ActivityManage.getInstance().addActivity(this);
		ObserverManage.getObserver().addObserver(this);
		initPlayInfo();
		initStatus();

		initService();

		/**
		 * android系统提供内置的Equalizer支持，我们可以直接声明并且使用。 但必须注意，当我们在代码中使用Equalizer的时候，
		 * 其实就是调整音量(EQ均衡器是改变音频使得声音发生变化， 像是洪亮或者低沉)。所以，我们需要在我们的代码中声明这么一句
		 */
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	/**
	 * 初始化服务
	 */
	private void initService() {
		if (Constants.showDesktopLyrics) {
			Intent floatLrcServiceIntent = new Intent(this,
					FloatLrcService.class);
			startService(floatLrcServiceIntent);
		}

		// if (Constants.showLockScreen) {
		// Intent lockServiceIntent = new Intent(MainActivity.this,
		// LockService.class);
		// startService(lockServiceIntent);
		// }
	}

	/**
	 * 初始化状态栏播放器
	 */
	private void initPlayBarNotifi() {
		// 更新通知栏
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "乐乐音乐，传播好的音乐";
		long when = System.currentTimeMillis();
		mPlayBarNotification = new Notification(icon, tickerText, when);
		// FLAG_AUTO_CANCEL 该通知能被状态栏的清除按钮给清除掉
		// FLAG_NO_CLEAR 该通知不能被状态栏的清除按钮给清除掉
		// FLAG_ONGOING_EVENT 通知放置在正在运行
		// FLAG_INSISTENT 是否一直进行，比如音乐一直播放，知道用户响应
		mPlayBarNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		// mNotification.flags |= Notification.FLAG_NO_CLEAR;

		// DEFAULT_ALL 使用所有默认值，比如声音，震动，闪屏等等
		// DEFAULT_LIGHTS 使用默认闪光提示
		// DEFAULT_SOUND 使用默认提示声音
		// DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission
		// android:name="android.permission.VIBRATE" />权限
		// mNotification.defaults = Notification.DEFAULT_SOUND;

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(MainActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		PendingIntent pendingIntent = PendingIntent
				.getActivity(MainActivity.this, 0, intent,
						PendingIntent.FLAG_CANCEL_CURRENT);
		mPlayBarNotification.contentIntent = pendingIntent;

		SongMessage songMessage = new SongMessage();
		songMessage.setSongInfo(null);
		Message msg = new Message();
		msg.what = 0;
		msg.obj = songMessage;
		notifyPlayBarHandler.sendMessage(msg);

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

			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

			window.setStatusBarColor(Color.TRANSPARENT);
			window.setNavigationBarColor(Color.TRANSPARENT);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT,
					getStatusBarHeight(this));
			RelativeLayout statusMenuView = (RelativeLayout) findViewById(R.id.statusMenuView);
			statusMenuView.setLayoutParams(lp);
			statusMenuView.setVisibility(View.VISIBLE);

			RelativeLayout statusContentView = (RelativeLayout) findViewById(R.id.statusContentView);
			statusContentView.setLayoutParams(lp);
			statusContentView.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 获取状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	private int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	private void init() {
		logger = LoggerManage.getZhangLogger(this);

		slidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
		// 设置是左滑还是右滑，还是左右都可以滑
		slidingMenu.setMode(SlidingMenu.LEFT);
		// 设置要使菜单滑动，触碰屏幕的范围
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setMenu(R.layout.sliding_menu);
		slidingMenu.setContent(R.layout.sliding_content);
		// 设置滑动时拖拽效果
		slidingMenu.setBehindScrollScale(0);
		// 设置滑动时菜单的是否淡入淡出
		slidingMenu.setFadeEnabled(true);
		// 设置淡入淡出的比例
		slidingMenu.setFadeDegree(1f);
		slidingMenu.showMenu();

		slidingMenu.setOnOpenedListener(new OnOpenedListener() {

			@Override
			public void onOpened() {
				if (searchView != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(
							searchView.getWindowToken(), 0);

					if (searchSongAdapter != null) {
						searchSongAdapter.finish();
					}
				}
			}
		});

		action = new MenuAction();
	}

	/**
	 * 初始化底部操作面板
	 */
	private void initPlayBar() {
		playBarRelativeLayout = (RelativeLayout) findViewById(R.id.playBar);

		playBarRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, LrcActivity.class));
				// overridePendingTransition(R.anim.rotate, 0);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

		popMenuParent = (RelativeLayout) findViewById(R.id.popMenuParent);

		popLayout = (LinearLayout) findViewById(R.id.pop_layout);
		popMenuParent.setVisibility(View.INVISIBLE);

		playBarSidebarParent = (ButtonPressRelativeLayout) findViewById(R.id.playBarSidebarParent);

		playBarSidebarParent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initMenuPopupWindow();
			}
		});

		seekBar = (BaseSeekBar) findViewById(R.id.seekBar);
		playBarDefArtist = (RoundedImageView) findViewById(R.id.play_bar_def_artist);
		songName = (MainTextView) findViewById(R.id.songName);
		artistName = (MainTextView) findViewById(R.id.artistName);

		pauseBarPlayParent = (ButtonPressRelativeLayout) findViewById(R.id.pauseBarPlayParent);
		pauseBarPlayParent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PAUSEMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});

		playBarPlayParent = (ButtonPressRelativeLayout) findViewById(R.id.playBarPlayParent);
		playBarPlayParent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PLAYMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});

		playBarNextParent = (ButtonPressRelativeLayout) findViewById(R.id.playBarNextParent);
		playBarNextParent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.NEXTMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});
		albumRoundedImageView = (RoundedImageView) findViewById(R.id.album);
		artistLoadingImageView = (ImageView) findViewById(R.id.artistLoadingImageView);
		rotateAnimation = AnimationUtils
				.loadAnimation(this, R.anim.anim_rotate);
		rotateAnimation.setInterpolator(new LinearInterpolator());// 匀速
		artistLoadingImageView.setVisibility(View.INVISIBLE);
	}

	private void initSlidingMenu() {
		viewpagerBackground = (ImageView) findViewById(R.id.viewpager_background);

		titleIconImageView = (ImageView) findViewById(R.id.title_icon);
		searchImageView = (ImageView) findViewById(R.id.search);

		titleSearchParent = (ButtonPressRelativeLayout) findViewById(R.id.title_searchParent);

		// 指示器背景
		tabPageIndicator = (RelativeLayout) findViewById(R.id.tabPageIndicator);

		menuFragmentList = new ArrayList<Fragment>();

		menuFragmentList.add(new TabMyFragment(action));
		menuFragmentList.add(new TabRecommendFragment());

		menuViewPager = (ViewPager) findViewById(R.id.menu_viewpager);
		menuTabFragmentPagerAdapter = new MenuTabFragmentPagerAdapter(
				getSupportFragmentManager());
		menuViewPager.setAdapter(menuTabFragmentPagerAdapter);
		menuViewPager.setOnPageChangeListener(pageChangedListener);
		menuViewPager.setOffscreenPageLimit(menuFragmentList.size());

		// 指示器底部线
		vline = (ImageView) findViewById(R.id.line);
		screenWith = getWindow().getWindowManager().getDefaultDisplay()
				.getWidth();
		// 设置底部线的长度和偏移量
		LayoutParams lp = vline.getLayoutParams();
		offsetWidth = lp.width = screenWith / menuFragmentList.size();
		vline.setLayoutParams(lp);

		// 设置indicatorItem
		indicatorItem = new RelativeLayout[menuFragmentList.size()];
		int i = 0;
		indicatorItem[i] = (RelativeLayout) findViewById(R.id.myItem);
		indicatorItem[i++].setOnClickListener(new ItemOnClick());

		indicatorItem[i] = (RelativeLayout) findViewById(R.id.recommendItem);
		indicatorItem[i++].setOnClickListener(new ItemOnClick());

		//
		i = 0;
		indicatorTip = new TabPageIndicatorTextView[menuFragmentList.size()];
		indicatorTip[i++] = (TabPageIndicatorTextView) findViewById(R.id.my);
		indicatorTip[i++] = (TabPageIndicatorTextView) findViewById(R.id.recommend);

		titleSearchParent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				initSearchView();
			}
		});
	}

	private void initSlidingContent() {
		contentRelativeLayout = (LinearLayout) findViewById(R.id.content_linearLayout);
		titleTextView = (TitleTextView) findViewById(R.id.title);
		backRelativeLayout = (ButtonPressRelativeLayout) findViewById(R.id.title_backParent);
		backRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				action.finish();
			}
		});
		backgroundImage = (LinearLayout) findViewById(R.id.backgroundImage);
		backgroundColor = (LinearLayout) findViewById(R.id.backgroundColor);
	}

	private void initPlayModel() {
		/**
		 * 随机播放
		 */
		menuShuffleParent = (RelativeLayout) findViewById(R.id.menuShuffleParent);
		/**
		 * 顺序播放
		 */
		menuSequenceParent = (RelativeLayout) findViewById(R.id.menuSequenceParent);
		/**
		 * 单曲循环
		 */
		menuRepeatoneParent = (RelativeLayout) findViewById(R.id.menuRepeatoneParent);
		/**
		 * 循环播放
		 */
		menuRepeatParent = (RelativeLayout) findViewById(R.id.menuRepeatParent);

		playModelHandler.sendEmptyMessage(0);
	}

	/**
	 * 初始化皮肤
	 */
	private void initSkin() {
		skinInfo = Constants.skinInfo;

		// if (CrashApplication.bitmap != null) {
		// viewpagerBackground.setBackground(new BitmapDrawable(
		// CrashApplication.bitmap));
		// } else {
		// slidingmenu
		ImageUtil.loadImageFormFile(viewpagerBackground,
				skinInfo.getBackgroundPath(), this, null);
		// }

		// ImageLoadUtil.loadImageFormFile(skinInfo.getBackgroundPath(),
		// viewpagerBackground, 0, true);

		playBarRelativeLayout.setBackgroundColor(skinInfo
				.getPlayBarBackgroundColor());

		popLayout.setBackgroundColor(skinInfo.getMenuBackgroundColor());

		Bitmap bitmap = ImageUtil.loadImageFormFile(skinInfo
				.getPlayBarDefArtistIcon().getNormal(), this);
		if (bitmap != null) {
			playBarDefArtist.setImageDrawable(new BitmapDrawable(bitmap));
		}

		ImageUtil.loadImageFormFile(titleIconImageView, skinInfo.getTitleIcon()
				.getNormal(), this, null);
		ImageUtil.loadImageFormFile(searchImageView, skinInfo
				.getTitleSearchIcon().getNormal(), this, null);
		tabPageIndicator.setBackgroundColor(skinInfo
				.getIndicatorBackgroundColor());
		vline.setBackgroundColor(skinInfo.getIndicatorLineBackgroundColor());

		resetTipColor();
		indicatorTip[tabIndex].setSelect(true);

		// slidingcontent
		ImageUtil.loadImageFormFile(backgroundImage,
				skinInfo.getBackgroundPath(), this, imcallBack);

		if (searchView != null) {
			initSearchSkin();
		}
	}

	/**
	 * 下载完成图片后，要执行的方法
	 */
	private ImageLoadCallBack imcallBack = new ImageLoadCallBack() {

		@Override
		public void callback() {
			backgroundColor.setBackgroundColor(skinInfo.getBackgroundColor());
		}
	};

	/**
	 * 重置tab标题文字的颜色
	 */
	private void resetTipColor() {
		for (int j = 0; j < indicatorTip.length; j++) {
			indicatorTip[j].setSelect(false);
		}
	}

	/**
	 * 
	 * @Title: initMenuPopupWindow
	 * @Description: (初始化菜单窗口)
	 * @param:
	 * @return: void
	 * @throws
	 */
	private void initMenuPopupWindow() {
		if (isAniEnter || isAniEXit) {

		} else {
			if (!menuShow) {
				anHandler.sendEmptyMessage(0);
			} else {
				anHandler.sendEmptyMessage(1);
			}
		}

	}

	/**
	 * 
	 * @Title: enterAnimation
	 * @Description: (进入动画)
	 * @param:
	 * @return: void
	 * @throws
	 */
	private void enterAnimation() {
		if (enterAnimation == null) {
			enterAnimation = AnimationUtils.loadAnimation(this,
					R.anim.dialog_enter);
			enterAnimation.setFillAfter(true);
			enterAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					isAniEnter = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					isAniEnter = false;
					popMenuParent.setBackgroundColor(Color.argb(110, 0, 0, 0));
					menuShow = true;

					popMenuParent.setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							// int bottomHeight = mMenu.getTop();
							int topHeight = popLayout.getTop();
							int y = (int) event.getY();
							if (event.getAction() == MotionEvent.ACTION_UP) {
								// y > bottomHeight ||
								if (topHeight > y) {
									initMenuPopupWindow();
								}
							}
							return true;
						}
					});
				}
			});
		}
		popLayout.startAnimation(enterAnimation);

	}

	/**
	 * 
	 * @Title: exitAnimation
	 * @Description: (退出动画)
	 * @param:
	 * @return: void
	 * @throws
	 */
	private void exitAnimation() {
		if (exitAnimation == null) {
			exitAnimation = AnimationUtils.loadAnimation(this,
					R.anim.dialog_exit);
			exitAnimation.setFillAfter(true);
			exitAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					isAniEXit = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					popMenuParent.setVisibility(View.INVISIBLE);
					isAniEXit = false;
					menuShow = false;
					popMenuParent.setOnTouchListener(null);
					if (isExit) {
						close();
					}
				}
			});
		}
		popLayout.startAnimation(exitAnimation);

	}

	@Override
	protected void onDestroy() {
		close();
	}

	/**
	 * 关闭应用
	 */
	protected void close() {

		if (FloatLrcService.isServiceRunning) {
			Intent floatLrcServiceIntent = new Intent(MainActivity.this,
					FloatLrcService.class);
			stopService(floatLrcServiceIntent);
		}

		unregisterReceiver(onClickNotifiReceiver);
		unregisterReceiver(receiver);

		if (Constants.isWire) {

			mAudioManager
					.unregisterMediaButtonEventReceiver(mRemoteControlResponder);
		}

		mNotificationManager.cancelAll();
		// 如果服务正在运行，则是正在播放
		if (MediaPlayerService.isServiceRunning) {
			stopService(new Intent(MainActivity.this, MediaPlayerService.class));
		}
		unregisterReceiver(mTimeReceiver);

		ActivityManage.getInstance().exit();

	}

	private Animation exitAnimation;
	private Animation enterAnimation;
	private Handler anHandler = new Handler() {

		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 显示弹出窗口
			case 0:
				popMenuParent.setVisibility(View.VISIBLE);
				enterAnimation();
				break;
			// 隐藏弹出窗口
			case 1:
				popMenuParent.setBackground(new BitmapDrawable());
				exitAnimation();
				break;
			default:
				break;
			}
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 如果弹出窗口显示了，点击返回按钮，则将其隐藏
			if (menuShow) {
				initMenuPopupWindow();
				return true;
			}
			// 如果slidingMenu界面显示了，点击返回按钮，则将其隐藏
			if (!slidingMenu.isMenuShowing()) {
				slidingMenu.showMenu();
				return true;
			}
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				ToastUtil.showTextToast(this, getString(R.string.back_tip));
				mExitTime = System.currentTimeMillis();
			} else {
				// 跳转到主界面
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU
				&& event.getRepeatCount() == 0) {
			initMenuPopupWindow();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 菜单点击事件
	 * 
	 * @Title: menuOnClick
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param: @param v
	 * @return: void
	 * @throws
	 */
	private long pressTime = 0;

	public void menuOnClick(View v) {
		// 播放模式不用判断点击的时间间隔
		if ((System.currentTimeMillis() - pressTime) < 500
				&& v.getId() != R.id.menuPlayMode) {
			pressTime = System.currentTimeMillis();
			// ToastUtil.showTextToast(getApplicationContext(),
			// "按钮不要按得太频繁....");
			return;
		}
		// 播放模式不用记录点击的时间
		if (v.getId() != R.id.menuPlayMode) {
			pressTime = System.currentTimeMillis();
		}
		switch (v.getId()) {
		// 退出
		case R.id.menuExit:
			isExit = true;
			// 再次执行该方法隐藏窗口
			initMenuPopupWindow();
			break;
		// 主题背景
		case R.id.menuSkin:
			startActivity(new Intent(MainActivity.this, SkinActivity.class));
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			// 再次执行该方法隐藏窗口
			initMenuPopupWindow();
			break;
		case R.id.menuSetting:
			startActivity(new Intent(MainActivity.this, SettingActivity.class));
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			// 再次执行该方法隐藏窗口
			initMenuPopupWindow();
			break;
		case R.id.menuAbout:
			startActivity(new Intent(MainActivity.this, AboutActivity.class));
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			// 再次执行该方法隐藏窗口
			initMenuPopupWindow();
			break;
		case R.id.menuScan:
			startActivity(new Intent(MainActivity.this, ScanMusicActivity.class));
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			// 再次执行该方法隐藏窗口
			initMenuPopupWindow();
			break;

		case R.id.menuPlayMode:
			switch (playModel) {
			case 0:
				playModel = 1;
				break;
			case 1:
				playModel = 2;
				break;
			case 2:
				playModel = 3;
				break;
			case 3:
				playModel = 0;
				break;
			}
			playModelHandler.sendEmptyMessage(0);
			break;
		}
	}

	/**
	 * 
	 * @ClassName: MenuTabFragmentPagerAdapter
	 * @Description:(slidingmenu菜单)
	 * @author: Android_Robot
	 * @date: 2015-6-16 下午6:50:16
	 * 
	 */
	public class MenuTabFragmentPagerAdapter extends FragmentPagerAdapter {

		public MenuTabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return menuFragmentList.get(arg0);
		}

		@Override
		public int getCount() {
			return menuFragmentList.size();
		}
	}

	/**
	 * 
	 * @ClassName: ItemOnClick
	 * @Description:(这里用一句话描述这个类的作用)
	 * @author: Android_Robot
	 * @date: 2015-6-16 下午10:48:51
	 * 
	 */
	class ItemOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.myItem:
				menuViewPager.setCurrentItem(0, true);
				break;
			case R.id.recommendItem:
				menuViewPager.setCurrentItem(1, true);
				break;
			}
		}
	}

	/**
	 * slidingmenu的viewpager滑动事件
	 */
	private OnPageChangeListener pageChangedListener = new OnPageChangeListener() {

		private boolean isAnim = false;

		@Override
		public void onPageSelected(int position) {
			tabIndex = position;
			resetTipColor();
			indicatorTip[tabIndex].setSelect(true);
		}

		@SuppressLint("NewApi")
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// 下划线跟着页面滑动
			if (arg0 == 0) {
				vline.setX(arg2 / menuFragmentList.size());
			} else if (isAnim) {
				vline.setX(offsetWidth * arg0 + arg2 / menuFragmentList.size());
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (arg0 != 0) {
				isAnim = true;
			} else {
				isAnim = false;
			}
		}
	};

	class MenuAction implements PageAction {

		View view;

		@Override
		public void addPage(View view, String title) {
			this.view = view;
			contentRelativeLayout.removeAllViews();
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			contentRelativeLayout.addView(view, p);
			contentRelativeLayout.invalidate();
			titleTextView.setText(title);
			slidingMenu.showContent();
		}

		@Override
		public View getPage() {
			return view;
		}

		@Override
		public void finish() {
			slidingMenu.showMenu();
		}

	}

	/**
	 * 通知栏为app的操作
	 */
	private final int NOTIFICATION_APP = 0;
	/**
	 * app下载通知栏的id号
	 */
	private int notificationAPPId = 19910117;
	/**
	 * 状态栏播放器id
	 */
	private int notificationPlayBarId = 19900420;

	/**
	 * App下载任务
	 */
	public static DownloadTask task = null;
	/**
	 * app的相关信息
	 */
	private AppInfo appInfo = null;
	// private static ExecutorService SINGLE_TASK_EXECUTOR;
	// static {
	// SINGLE_TASK_EXECUTOR = (ExecutorService) Executors
	// .newSingleThreadExecutor();
	// };

	private int downloadSize = 0;
	private Handler mNotificationHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NOTIFICATION_APP:
				DownloadTask task = (DownloadTask) msg.obj;
				if (task == null) {
				} else {
					if (downloadSize < task.getDownloadedSize()) {
						downloadSize = (int) task.getDownloadedSize();
						createAPPDownloadNotification(task);
						if (task.getStatus() == DownloadTask.DOWNLOAD_FINISH) {
							install();
						}
					}
				}
				break;

			default:
				break;
			}
		}

	};
	/**
	 * //初始化媒体(耳机)广播对象.
	 */
	private PhoneReceiver phoneReceiver = new PhoneReceiver();

	private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {

				MessageIntent messageIntent = new MessageIntent();
				messageIntent.setAction(MessageIntent.SYSTEMTIME);
				ObserverManage.getObserver().setMessage(messageIntent);
			}
		}
	};

	/**
	 * 初始化通知栏管理器
	 */
	private void initReceiver() {

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.NOTIFIATION_APP_DOWNLOAD);
		filter.addAction(Constants.NOTIFIATION_APP_DOWNLOADFINISH);

		// 屏幕
		filter.addAction("android.intent.action.SCREEN_ON");
		filter.addAction("android.intent.action.SCREEN_OFF");

		// 耳机
		filter.addAction("android.media.AUDIO_BECOMING_NOISY");
		// 短信
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(receiver, filter);

		IntentFilter nofifilter = new IntentFilter();
		nofifilter.addAction(Constants.NOTIFIATION_APP_PLAYMUSIC);
		nofifilter.addAction(Constants.NOTIFIATION_APP_PAUSEMUSIC);
		nofifilter.addAction(Constants.NOTIFIATION_APP_NEXTMUSIC);
		nofifilter.addAction(Constants.NOTIFIATION_APP_PREMUSIC);
		nofifilter.addAction(Constants.NOTIFIATION_APP_CLOSE);

		nofifilter.addAction(Constants.NOTIFIATION_DESLRC_SHOW);
		nofifilter.addAction(Constants.NOTIFIATION_DESLRC_HIDE);
		nofifilter.addAction(Constants.NOTIFIATION_DESLRC_UNLOCK);
		registerReceiver(onClickNotifiReceiver, nofifilter);

		// 添加来电监听事件
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); // 获取系统服务
		telManager.listen(new MobliePhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);

		// 注册线控
		if (Constants.isWire) {
			mAudioManager
					.registerMediaButtonEventReceiver(mRemoteControlResponder);
		}

		IntentFilter mTimeFilter = new IntentFilter();
		mTimeFilter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(mTimeReceiver, mTimeFilter);
	}

	/**
	 * 
	 * @author wwj 电话监听器类
	 */
	private class MobliePhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // 挂机状态
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // 通话状态
			case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
				// 接收到来电
				if (MediaManage.getMediaManage(getApplicationContext())
						.getPlayStatus() == MediaManage.PLAYING) {
					SongMessage songMessage = new SongMessage();
					songMessage.setType(SongMessage.PAUSEMUSIC);
					ObserverManage.getObserver().setMessage(songMessage);
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 安装应用
	 */
	protected void install() {
		if (task != null) {
			Uri uri = Uri.fromFile(new File(task.getFilePath())); // 获取文件的Uri
			Intent installIntent = new Intent(Intent.ACTION_VIEW);
			installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			installIntent.setDataAndType(uri,
					"application/vnd.android.package-archive");// 设置intent的数据类型
			startActivity(installIntent);
		}
	}

	/**
	 * 创建通知栏
	 * 
	 * @param task
	 */
	protected void createAPPDownloadNotification(DownloadTask task) {

		// 定义Notification的各种属性
		int icon = R.drawable.ic_launcher; // 通知图标
		CharSequence tickerText = getString(R.string.app_name); // 状态栏显示的通知文本提示
		long when = System.currentTimeMillis(); // 通知产生的时间，会在通知信息里显示
		// 用上面的属性初始化 Nofification
		Notification notification = new Notification(icon, tickerText, when);
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notification_downloadapp);

		contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
		contentView.setTextViewText(R.id.title, "乐乐音乐新版本");

		// if (task.getFileSize() != 0) {
		if (task.getStatus() == DownloadTask.DOWNLOAD_FINISH) {
			contentView.setTextViewText(R.id.text, "下载完成，点击安装");

			Intent finishIntent = new Intent(
					Constants.NOTIFIATION_APP_DOWNLOADFINISH);
			PendingIntent pendFinishIntent = PendingIntent.getBroadcast(this,
					0, finishIntent, 0);
			contentView.setOnClickPendingIntent(R.id.bg, pendFinishIntent);

		} else if (task.getStatus() == DownloadTask.DOWNLOING) {
			contentView.setTextViewText(
					R.id.text,
					(int) ((float) task.getDownloadedSize()
							/ task.getFileSize() * 100)
							+ "%");
		} else if (task.getStatus() == DownloadTask.DOWNLOAD_ERROR_NONET
				|| task.getStatus() == DownloadTask.DOWNLOAD_ERROR_NOTWIFI
				|| task.getStatus() == DownloadTask.DOWNLOAD_ERROR_OTHER) {
			Intent downloadIntent = new Intent(
					Constants.NOTIFIATION_APP_DOWNLOAD);
			PendingIntent pendDownloadIntent = PendingIntent.getBroadcast(this,
					0, downloadIntent, 0);
			contentView.setOnClickPendingIntent(R.id.bg, pendDownloadIntent);

			contentView.setTextViewText(R.id.text, "下载失败，点击重新下载");
		}
		// }
		notification.contentView = contentView;
		// 把Notification传递给NotificationManager
		mNotificationManager.notify(notificationAPPId, notification);
	}

	/**
	 * 根据通知栏广播来执行不同的操作
	 * 
	 * @param intent
	 */
	protected void doSomeThing(Intent intent) {
		if (intent.getAction().equals(Constants.NOTIFIATION_APP_DOWNLOAD)) {
			if (task != null) {
				task.setStatus(DownloadTask.INT);
				downloadAPP();
			} else {
				if (appInfo != null)
					downloadApk(appInfo);
			}
		} else if (intent.getAction().equals(
				Constants.NOTIFIATION_APP_DOWNLOADFINISH)) {
			install();
		} else if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {

			SCREEN_OFF = false;
			if (!FloatLrcService.isServiceRunning) {
				startService(new Intent(MainActivity.this,
						FloatLrcService.class));
			}

			// int status = MediaManage.getMediaManage(MainActivity.this)
			// .getPlayStatus();

			if (!LockActivity.active
			// && status == MediaManage.PLAYING
					&& Constants.showLockScreen) {
				Intent lockIntent = new Intent(MainActivity.this,
						LockActivity.class);
				lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(lockIntent);
			}

		} else if (intent.getAction()
				.equals("android.intent.action.SCREEN_OFF")) {

			SCREEN_OFF = true;
			if (FloatLrcService.isServiceRunning) {
				stopService(new Intent(MainActivity.this, FloatLrcService.class));
			}

			// int status = MediaManage.getMediaManage(MainActivity.this)
			// .getPlayStatus();
			if (!LockActivity.active
			// && status == MediaManage.PLAYING
					&& Constants.showLockScreen) {
				Intent lockIntent = new Intent(MainActivity.this,
						LockActivity.class);
				lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(lockIntent);
			}

		} else if (intent.getAction().equals(
				"android.media.AUDIO_BECOMING_NOISY")) {
			// 耳机拔出
			/**
			 * 从硬件层面来看，直接监听耳机拔出事件不难，耳机的拔出和插入，会引起手机电平的变化，然后触发什么什么中断，
			 * 
			 * 最终在stack overflow找到答案，监听Android的系统广播AudioManager.
			 * ACTION_AUDIO_BECOMING_NOISY，
			 * 但是这个广播只是针对有线耳机，或者无线耳机的手机断开连接的事件，监听不到有线耳机和蓝牙耳机的接入
			 * ，但对于我的需求来说足够了，监听这个广播就没有延迟了，UI可以立即响应
			 */
			if (MediaManage.getMediaManage(getApplicationContext())
					.getPlayStatus() == MediaManage.PLAYING) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PAUSEMUSIC);
				ObserverManage.getObserver().setMessage(songMessage);
			}
		} else if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
			// 接收到短信
			if (MediaManage.getMediaManage(getApplicationContext())
					.getPlayStatus() == MediaManage.PLAYING) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PAUSEMUSIC);
				ObserverManage.getObserver().setMessage(songMessage);
			}
		}
	}

	/**
	 * 下载apk文件
	 * 
	 * @param aid
	 */
	protected void downloadApk(AppInfo appInfo) {
		if (task == null) {
			String url = HttpUtil.getAppInfoDataByID(appInfo.getAid());
			task = new DownloadTask();
			task.setTid(appInfo.getAid());
			task.setStatus(DownloadTask.INT);
			task.setDownloadUrl(url);
			task.setFilePath(Constants.PATH_APK + File.separator
					+ appInfo.getAid() + "." + appInfo.getType());
			task.setFileSize(appInfo.getSize());
			task.setDownloadedSize(0);
			task.setAddTime(DateUtil.dateToOtherString(new Date()));
			task.setFinishTime("");
			task.setType(DownloadTask.APK);

			downloadAPP();
		} else {
			task.setStatus(DownloadTask.INT);
			downloadAPP();
		}
	}

	private IDownloadTaskEventCallBack eventCallBack = new IDownloadTaskEventCallBack() {

		@Override
		public void waiting(DownloadTask task) {
			task.setStatus(DownloadTask.WAITING);
			ObserverManage.getObserver().setMessage(task);
		}

		@Override
		public void downloading(DownloadTask task) {
			task.setStatus(DownloadTask.DOWNLOING);
			ObserverManage.getObserver().setMessage(task);
		}

		@Override
		public void threadDownloading(DownloadTask task) {
		}

		@Override
		public void pauseed(DownloadTask task) {
			task.setStatus(DownloadTask.DOWNLOAD_PAUSE);
			ObserverManage.getObserver().setMessage(task);
		}

		@Override
		public void canceled(DownloadTask task) {
			task.setStatus(DownloadTask.DOWNLOAD_CANCEL);
			ObserverManage.getObserver().setMessage(task);
		}

		@Override
		public void finished(DownloadTask task) {
			task.setStatus(DownloadTask.DOWNLOAD_FINISH);
			ObserverManage.getObserver().setMessage(task);
		}

		@Override
		public void error(DownloadTask task) {
			ObserverManage.getObserver().setMessage(task);
		}

	};

	/**
	 * 下载app
	 */
	private void downloadAPP() {
		downloadSize = 0;
		ToastUtil.showTextToast(this, "开始后台下载，通知栏可查看进度!");
		//
		// new AsyncTask<String, Integer, String>() {
		//
		// @Override
		// protected String doInBackground(String... params) {
		//
		// FileDownloadThread thread = task.getThread();
		// if (thread.isFinish() || thread.isCancel() || thread.isError()
		// || thread.isPause()) {
		// thread = null;
		// } else {
		// thread.start(getApplicationContext());
		// }
		//
		// return null;
		// }
		//
		// @SuppressLint("NewApi")
		// @Override
		// protected void onPostExecute(String result) {
		//
		// }
		// }.executeOnExecutor(SINGLE_TASK_EXECUTOR, "");

		DownloadThreadManage dtm = new DownloadThreadManage(task, 10, 1000);
		task.setDownloadThreadManage(dtm);
		// System.out.println(System.currentTimeMillis());
		DownloadThreadPool dp = DownloadManage
				.getAPKTM(getApplicationContext());
		dp.setEvent(eventCallBack);
		dp.addDownloadTask(task);

	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			initSkin();
		} else if (data instanceof DownloadTask) {
			DownloadTask task = (DownloadTask) data;
			if (task.getType() == DownloadTask.APK) {
				Message msg = new Message();
				msg.what = NOTIFICATION_APP;
				msg.obj = task;
				// this.task = task;
				mNotificationHandler.sendMessage(msg);
			}
		} else if (data instanceof AppInfo) {
			AppInfo appInfo = (AppInfo) data;
			this.appInfo = appInfo;
			downloadApk(appInfo);
		} else if (data instanceof MessageIntent) {
			MessageIntent messageIntent = (MessageIntent) data;
			if (messageIntent.getAction().equals(MessageIntent.CHANGEMODE)) {
				playModel = Constants.playModel;
				playModelHandler.sendEmptyMessage(0);
			} else if (messageIntent.getAction().equals(
					MessageIntent.OPENORCLOSEWIRE)) {
				if (Constants.isWire) {

					mAudioManager
							.registerMediaButtonEventReceiver(mRemoteControlResponder);

				} else {

					mAudioManager
							.unregisterMediaButtonEventReceiver(mRemoteControlResponder);
				}
			}
		} else if (data instanceof SongMessage) {
			SongMessage songMessageTemp = (SongMessage) data;
			if (songMessageTemp.getType() == SongMessage.INITMUSIC
					|| songMessageTemp.getType() == SongMessage.SERVICEPLAYINGMUSIC
					|| songMessageTemp.getType() == SongMessage.SERVICEPAUSEEDMUSIC
					|| songMessageTemp.getType() == SongMessage.ERRORMUSIC
					|| songMessageTemp.getType() == SongMessage.SERVICEPLAYMUSIC
					|| songMessageTemp.getType() == SongMessage.SERVICEERRORMUSIC) {
				Message msg = new Message();
				msg.obj = songMessageTemp;
				songInfoHandler.sendMessage(msg);

				if (songMessageTemp.getType() == SongMessage.INITMUSIC
						|| songMessageTemp.getType() == SongMessage.SERVICEPLAYMUSIC
						|| songMessageTemp.getType() == SongMessage.SERVICEPAUSEEDMUSIC
						|| songMessageTemp.getType() == SongMessage.ERRORMUSIC
						|| songMessageTemp.getType() == SongMessage.SERVICEERRORMUSIC) {

					Message msg2 = new Message();
					msg2.obj = songMessageTemp;
					notifyPlayBarHandler.sendMessage(msg2);
				}

			} else if (songMessageTemp.getType() == SongMessage.UPDATEMUSIC
					|| songMessageTemp.getType() == SongMessage.SERVICEPLAYWAITING
					|| songMessageTemp.getType() == SongMessage.SERVICEPLAYWAITINGEND
					|| songMessageTemp.getType() == SongMessage.SERVICEDOWNLOADFINISHED) {
				Message msg = new Message();
				msg.obj = songMessageTemp;
				songInfoHandler.sendMessage(msg);
			} else if (songMessageTemp.getType() == SongMessage.ALUBMPHOTOLOADED) {
				SongInfo songInfo = MediaManage.getMediaManage(
						MainActivity.this).getSongInfo();
				if (!songInfo.getSid().equals(songMessageTemp.getSid())) {
				} else {
					songMessageTemp.setSongInfo(songInfo);
					Message msg = new Message();
					msg.obj = songMessageTemp;
					notifyPlayBarHandler.sendMessage(msg);
				}
			} else if (songMessageTemp.getType() == SongMessage.DESLRCLOCKORUNLOCK) {
				if (Constants.desktopLyricsIsMove) {
					// 解锁
					Constants.desktopLyricsIsMove = false;
				} else {
					Constants.desktopLyricsIsMove = true;
				}

				DataUtil.saveValue(MainActivity.this,
						Constants.desktopLyricsIsMove_KEY,
						Constants.desktopLyricsIsMove);

				SongMessage songMessageT = new SongMessage();
				songMessageT.setType(SongMessage.DESLRCLOCKORUNLOCKED);
				ObserverManage.getObserver().setMessage(songMessageT);

				Message msg = new Message();
				songMessageTemp.setSongInfo(new SongInfo());
				msg.obj = songMessageTemp;
				notifyPlayBarHandler.sendMessage(msg);

			} else if (songMessageTemp.getType() == SongMessage.DESLRCSHOWORHIDE) {

				if (Constants.showDesktopLyrics) {
					// 显示桌面歌词
					Constants.showDesktopLyrics = false;
				} else {
					Constants.showDesktopLyrics = true;
					Constants.desktopLyricsIsMove = true;

					DataUtil.saveValue(MainActivity.this,
							Constants.desktopLyricsIsMove_KEY,
							Constants.desktopLyricsIsMove);
				}

				DataUtil.saveValue(MainActivity.this,
						Constants.showDesktopLyrics_KEY,
						Constants.showDesktopLyrics);

				if (Constants.showDesktopLyrics) {

					if (!FloatLrcService.isServiceRunning) {
						Intent floatLrcServiceIntent = new Intent(
								MainActivity.this, FloatLrcService.class);
						startService(floatLrcServiceIntent);
					}

					if (Constants.isFristSettingDesLrc) {

						Constants.isFristSettingDesLrc = false;

						new Thread() {

							@Override
							public void run() {
								DataUtil.saveValue(getApplicationContext(),
										Constants.isFristSettingDesLrc_KEY,
										Constants.isFristSettingDesLrc);
							}

						}.start();

						try {
							// 开启悬浮窗设置界面
							Intent localIntent = new Intent(
									"miui.intent.action.APP_PERM_EDITOR");
							localIntent
									.setClassName("com.miui.securitycenter",
											"com.miui.permcenter.permissions.AppPermissionsEditorActivity");
							localIntent.putExtra("extra_pkgname",
									getPackageName());
							startActivity(localIntent);
						} catch (ActivityNotFoundException localActivityNotFoundException) {
							// 设置页面
							Intent intent = new Intent(
									Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							Uri uri = Uri.fromParts("package",
									getPackageName(), null);
							intent.setData(uri);
							startActivity(intent);
						}
					}
				} else {
					if (FloatLrcService.isServiceRunning) {
						Intent floatLrcServiceIntent = new Intent(
								MainActivity.this, FloatLrcService.class);
						stopService(floatLrcServiceIntent);
					}
				}

				SongMessage songMessageT = new SongMessage();
				songMessageT.setType(SongMessage.DESLRCSHOWORHIDEED);
				ObserverManage.getObserver().setMessage(songMessageT);

				Message msg = new Message();
				songMessageTemp.setSongInfo(new SongInfo());
				msg.obj = songMessageTemp;
				notifyPlayBarHandler.sendMessage(msg);
			}
		}
	}

	@Override
	public void finish() {
		ObserverManage.getObserver().deleteObserver(this);
		super.finish();
	}

	private View searchView;
	private RelativeLayout searchParent;
	private View lineViewTop;
	private View lineViewBottom;

	private SearchEditText searchEditText;

	private SearchDelImageView searchDelImageView;

	private RecyclerView searchPlayListview;

	private SearchSongAdapter searchSongAdapter;

	private LoadRelativeLayout searchSongLoadRelativeLayout;

	private ButtonPressRelativeLayout buttonPressRelativeLayout;

	/**
	 * 一个按钮弹出窗口
	 */
	private AlartOneButtonDialog alartOneButtonDialog = null;

	private Handler searchSongHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				searchSongLoadRelativeLayout.showLoadingView();
				break;
			case 1:
				searchSongLoadRelativeLayout.showSuccessView();
				break;
			case 2:
				searchSongLoadRelativeLayout.showNoResultView();
				break;
			}
		}

	};

	/**
	 * 初始化搜索的界面
	 */
	protected void initSearchView() {
		LayoutInflater inflater = getLayoutInflater();
		searchView = inflater.inflate(R.layout.fragment_search, null, false);
		searchParent = (RelativeLayout) searchView
				.findViewById(R.id.searchParent);

		lineViewTop = searchView.findViewById(R.id.searchlineTop);
		lineViewBottom = searchView.findViewById(R.id.searchlineBottom);

		searchEditText = (SearchEditText) searchView
				.findViewById(R.id.etSearch);

		searchDelImageView = (SearchDelImageView) searchView
				.findViewById(R.id.ivDeleteText);

		searchDelImageView.setVisibility(View.INVISIBLE);

		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					if (searchEditText.getText() == null
							|| searchEditText.getText().toString().equals("")) {
						searchDelImageView.setVisibility(View.INVISIBLE);
					} else {
						searchDelImageView.setVisibility(View.VISIBLE);
					}
				} else {
					searchDelImageView.setVisibility(View.INVISIBLE);
				}
			}
		});

		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (searchEditText.getText() == null
						|| searchEditText.getText().toString().equals("")) {
					searchDelImageView.setVisibility(View.INVISIBLE);
				} else {
					searchDelImageView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		searchDelImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				searchEditText.setText("");
				searchDelImageView.setVisibility(View.INVISIBLE);
			}
		});

		buttonPressRelativeLayout = (ButtonPressRelativeLayout) searchView
				.findViewById(R.id.btnSearch);

		searchPlayListview = (RecyclerView) searchView
				.findViewById(R.id.searchPlayListview);
		searchPlayListview.setHasFixedSize(true);
		searchPlayListview.setLayoutManager(new LinearLayoutManager(this));

		searchSongLoadRelativeLayout = (LoadRelativeLayout) searchView
				.findViewById(R.id.searchsongloadRelativeLayout);
		searchSongLoadRelativeLayout.init(this);

		buttonPressRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				loadSearchSongInfo();
			}
		});

		initSearchSkin();

		action.addPage(searchView, "搜索");
	}

	/**
	 * 弹出警告窗口
	 */
	protected void showOneAlert(String text) {
		if (alartOneButtonDialog == null) {
			alartOneButtonDialog = new AlartOneButtonDialog(this,
					R.style.dialog, new ButtonDialogListener() {

						@Override
						public void ButtonClick() {

						}
					});
		}
		alartOneButtonDialog.showDialog(text, "确定");
	}

	protected void loadSearchSongInfo() {

		String key = searchEditText.getText().toString();
		if (key == null || key.equals("")) {
			showOneAlert("关键字不能为空!!");
			return;
		}

		new AsyncTask<String, Integer, HttpResult<SongInfo>>() {

			@Override
			protected HttpResult<SongInfo> doInBackground(String... params) {
				searchSongHandler.sendEmptyMessage(0);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return HttpUtil.getSongInfoByKey(MainActivity.this, params[0]);
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(HttpResult<SongInfo> httpResult) {
				int status = httpResult.getStatus();
				if (status == HttpUtil.NORESULT) {
					searchSongHandler.sendEmptyMessage(2);
					ToastUtil.showTextToast(MainActivity.this, "没有找到相关数据!!");
				} else if (status == HttpUtil.SUCCESS) {
					List<SongInfo> result = httpResult.getModels();
					ToastUtil.showTextToast(MainActivity.this,
							"为您加载" + result.size() + "条数据");

					searchSongAdapter = new SearchSongAdapter(
							MainActivity.this, result);
					searchPlayListview.setAdapter(searchSongAdapter);

					searchSongHandler.sendEmptyMessage(1);
				}
			}

		}.execute(key);
	}

	/**
	 * 初始化搜索界面的样式
	 */
	private void initSearchSkin() {
		searchParent.setBackgroundColor(skinInfo.getIndicatorBackgroundColor());
		lineViewTop
				.setBackgroundColor(skinInfo.getItemDividerBackgroundColor());
		lineViewBottom.setBackgroundColor(skinInfo
				.getItemDividerBackgroundColor());
	}

	/**
	 * 初始化歌曲数据
	 */
	private void initPlayInfo() {
		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				int playListType = Constants.playListType;
				MediaManage.getMediaManage(MainActivity.this).initSongInfoData(
						playListType);
				return null;
			}

		}.execute("");
	}
}
