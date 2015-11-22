package com.happy.ui;

import java.util.Observable;
import java.util.Observer;

import name.teze.layout.lib.SwipeBackActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.happy.common.Constants;
import com.happy.manage.ActivityManage;
import com.happy.model.app.MessageIntent;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.util.DataUtil;
import com.happy.widget.SetupBGButton;
import com.happy.widget.SetupColorBGButton;
import com.happy.widget.SetupDesktoplyricsButton;
import com.happy.widget.SetupLockScreenButton;
import com.happy.widget.SetupWifiButton;

public class SettingActivity extends SwipeBackActivity implements Observer {

	/**
	 * 仅wifi按钮
	 */
	private SetupWifiButton WifiButton;
	/**
	 * 桌面歌词
	 */
	private SetupDesktoplyricsButton desktoplyricsButton;
	/**
	 * 锁屏歌词
	 */
	private SetupLockScreenButton lockScreenButton;
	/**
	 * 线控按钮
	 */
	private SetupBGButton wireBGButton;
	/**
	 * 是否开启辅助操控功能
	 */
	private SetupBGButton easyTouchBGButton;
	/**
	 * 是否开启问候音
	 */
	private SetupBGButton sayHelloBGButton;

	/**
	 * 音质按钮
	 */
	private SetupBGButton[] soundBGButton;

	/**
	 * 音质索引
	 */
	private int soundIndex = Constants.soundIndex;
	/**
	 * 标题颜色
	 */
	private SetupColorBGButton[] colorBGButton;
	/**
	 * 标题颜色集合
	 */
	private String[] colorBGColorStr = Constants.colorBGColorStr;

	private int colorIndex = Constants.colorIndex;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			SongMessage songMessageTemp = (SongMessage) msg.obj;

			if (songMessageTemp.getType() == SongMessage.DESLRCSHOWORHIDEED) {
				desktoplyricsButton.setSelect(Constants.showDesktopLyrics);

			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initComponent();
		ActivityManage.getInstance().addActivity(this);
		initStatus();

		ObserverManage.getObserver().addObserver(this);
	}

	/**
	 * 初始化状态栏
	 */
	@SuppressLint("NewApi")
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

	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	@SuppressLint("NewApi")
	private void initComponent() {

		WifiButton = (SetupWifiButton) findViewById(R.id.item0);
		if (Constants.isWifi) {
			WifiButton.setSelect(true);
		}
		WifiButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (WifiButton.isSelect()) {
					WifiButton.setSelect(false);
				} else {
					WifiButton.setSelect(true);
				}
				Constants.isWifi = WifiButton.isSelect();
				DataUtil.saveValue(SettingActivity.this, Constants.isWifi_KEY,
						Constants.isWifi);
			}
		});

		desktoplyricsButton = (SetupDesktoplyricsButton) findViewById(R.id.item1);
		if (Constants.showDesktopLyrics) {
			desktoplyricsButton.setSelect(true);
		}
		desktoplyricsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (desktoplyricsButton.isSelect()) {
					desktoplyricsButton.setSelect(false);
				} else {
					desktoplyricsButton.setSelect(true);
				}

				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.DESLRCSHOWORHIDE);
				ObserverManage.getObserver().setMessage(songMessage);

				// Constants.showDesktopLyrics = desktoplyricsButton.isSelect();
				// DataUtil.saveValue(SettingActivity.this,
				// Constants.showDesktopLyrics_KEY,
				// Constants.showDesktopLyrics);
				//
				// if (Constants.showDesktopLyrics) {
				//
				// if (!FloatLrcService.isServiceRunning) {
				// Intent floatLrcServiceIntent = new Intent(
				// SettingActivity.this, FloatLrcService.class);
				// startService(floatLrcServiceIntent);
				// }
				//
				// if (!Constants.isFristSettingDesLrc)
				// return;
				//
				// Constants.isFristSettingDesLrc = false;
				//
				// new Thread() {
				//
				// @Override
				// public void run() {
				// DataUtil.saveValue(getApplicationContext(),
				// Constants.isFristSettingDesLrc_KEY,
				// Constants.isFristSettingDesLrc);
				// }
				//
				// }.start();
				//
				// try {
				// // 开启悬浮窗设置界面
				// Intent localIntent = new Intent(
				// "miui.intent.action.APP_PERM_EDITOR");
				// localIntent
				// .setClassName("com.miui.securitycenter",
				// "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
				// localIntent.putExtra("extra_pkgname", getPackageName());
				// startActivity(localIntent);
				// } catch (ActivityNotFoundException
				// localActivityNotFoundException) {
				// // 设置页面
				// Intent intent = new Intent(
				// Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				// Uri uri = Uri.fromParts("package", getPackageName(),
				// null);
				// intent.setData(uri);
				// startActivity(intent);
				// }
				// } else {
				// if (FloatLrcService.isServiceRunning) {
				// Intent floatLrcServiceIntent = new Intent(
				// SettingActivity.this, FloatLrcService.class);
				// stopService(floatLrcServiceIntent);
				// }
				// }
			}
		});

		lockScreenButton = (SetupLockScreenButton) findViewById(R.id.item2);
		if (Constants.showLockScreen) {
			lockScreenButton.setSelect(true);
		}
		lockScreenButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (lockScreenButton.isSelect()) {
					lockScreenButton.setSelect(false);
				} else {
					lockScreenButton.setSelect(true);
				}
				Constants.showLockScreen = lockScreenButton.isSelect();
				DataUtil.saveValue(SettingActivity.this,
						Constants.showLockScreen_KEY, Constants.showLockScreen);
			}
		});

		wireBGButton = (SetupBGButton) findViewById(R.id.wire);
		if (Constants.isWire) {
			wireBGButton.setSelect(true);
		}
		wireBGButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (wireBGButton.isSelect()) {
					wireBGButton.setSelect(false);
				} else {
					wireBGButton.setSelect(true);
				}

				Constants.isWire = wireBGButton.isSelect();

				MessageIntent messageIntent = new MessageIntent();
				messageIntent.setAction(MessageIntent.OPENORCLOSEWIRE);
				ObserverManage.getObserver().setMessage(messageIntent);

				DataUtil.saveValue(SettingActivity.this, Constants.isWire_KEY,
						Constants.isWire);
			}
		});

		easyTouchBGButton = (SetupBGButton) findViewById(R.id.easytouch);
		if (Constants.isEasyTouch) {
			easyTouchBGButton.setSelect(true);
		}
		easyTouchBGButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (easyTouchBGButton.isSelect()) {
					easyTouchBGButton.setSelect(false);
				} else {
					easyTouchBGButton.setSelect(true);
				}
				Constants.isEasyTouch = easyTouchBGButton.isSelect();
				DataUtil.saveValue(SettingActivity.this,
						Constants.isEasyTouch_KEY, Constants.isEasyTouch);
			}
		});

		sayHelloBGButton = (SetupBGButton) findViewById(R.id.sayhello);
		if (Constants.isSayHello) {
			sayHelloBGButton.setSelect(true);
		}
		sayHelloBGButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (sayHelloBGButton.isSelect()) {
					sayHelloBGButton.setSelect(false);
				} else {
					sayHelloBGButton.setSelect(true);
				}
				Constants.isSayHello = sayHelloBGButton.isSelect();
				DataUtil.saveValue(SettingActivity.this,
						Constants.isSayHello_KEY, Constants.isSayHello);
			}
		});

		soundBGButton = new SetupBGButton[6];
		int i = 0;
		soundBGButton[i] = (SetupBGButton) findViewById(R.id.sound0);
		soundBGButton[i++].setOnClickListener(soundOnClickListener);

		soundBGButton[i] = (SetupBGButton) findViewById(R.id.sound1);
		soundBGButton[i++].setOnClickListener(soundOnClickListener);

		soundBGButton[i] = (SetupBGButton) findViewById(R.id.sound2);
		soundBGButton[i++].setOnClickListener(soundOnClickListener);

		soundBGButton[i] = (SetupBGButton) findViewById(R.id.sound3);
		soundBGButton[i++].setOnClickListener(soundOnClickListener);

		soundBGButton[i] = (SetupBGButton) findViewById(R.id.sound4);
		soundBGButton[i++].setOnClickListener(soundOnClickListener);

		soundBGButton[i] = (SetupBGButton) findViewById(R.id.sound5);
		soundBGButton[i++].setOnClickListener(soundOnClickListener);

		soundBGButton[soundIndex].setSelect(true);

		i = 0;
		colorBGButton = new SetupColorBGButton[colorBGColorStr.length];

		colorBGButton[i] = (SetupColorBGButton) findViewById(R.id.color0);
		colorBGButton[i].setDefColorStr(colorBGColorStr[i]);
		colorBGButton[i++].setOnClickListener(colorOnClickListener);

		colorBGButton[i] = (SetupColorBGButton) findViewById(R.id.color1);
		colorBGButton[i].setDefColorStr(colorBGColorStr[i]);
		colorBGButton[i++].setOnClickListener(colorOnClickListener);

		colorBGButton[i] = (SetupColorBGButton) findViewById(R.id.color2);
		colorBGButton[i].setDefColorStr(colorBGColorStr[i]);
		colorBGButton[i++].setOnClickListener(colorOnClickListener);

		colorBGButton[i] = (SetupColorBGButton) findViewById(R.id.color3);
		colorBGButton[i].setDefColorStr(colorBGColorStr[i]);
		colorBGButton[i++].setOnClickListener(colorOnClickListener);

		colorBGButton[i] = (SetupColorBGButton) findViewById(R.id.color4);
		colorBGButton[i].setDefColorStr(colorBGColorStr[i]);
		colorBGButton[i++].setOnClickListener(colorOnClickListener);

		colorBGButton[i] = (SetupColorBGButton) findViewById(R.id.color5);
		colorBGButton[i].setDefColorStr(colorBGColorStr[i]);
		colorBGButton[i++].setOnClickListener(colorOnClickListener);

		colorBGButton[colorIndex].setSelect(true);

	}

	private OnClickListener soundOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int index = 0;
			switch (v.getId()) {
			case R.id.sound0:
				index = 0;
				break;
			case R.id.sound1:
				index = 1;
				break;
			case R.id.sound2:
				index = 2;
				break;
			case R.id.sound3:
				index = 3;
				break;
			case R.id.sound4:
				index = 4;
				break;
			case R.id.sound5:
				index = 5;
				break;
			}
			if (soundIndex == index) {
				return;
			}
			soundBGButton[soundIndex].setSelect(false);
			soundIndex = index;
			soundBGButton[soundIndex].setSelect(true);

			Constants.soundIndex = soundIndex;
			DataUtil.saveValue(SettingActivity.this, Constants.soundIndex_KEY,
					Constants.soundIndex);
		}
	};

	private OnClickListener colorOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int index = 0;
			switch (v.getId()) {
			case R.id.color0:
				index = 0;
				break;
			case R.id.color1:
				index = 1;
				break;
			case R.id.color2:
				index = 2;
				break;
			case R.id.color3:
				index = 3;
				break;
			case R.id.color4:
				index = 4;
				break;
			case R.id.color5:
				index = 5;
				break;
			}
			if (colorIndex == index) {
				return;
			}
			colorBGButton[colorIndex].setSelect(false);
			colorIndex = index;
			colorBGButton[colorIndex].setSelect(true);

			Constants.colorIndex = colorIndex;
			DataUtil.saveValue(SettingActivity.this, Constants.colorIndex_KEY,
					Constants.colorIndex);

			MessageIntent messageIntent = new MessageIntent();
			messageIntent.setAction(MessageIntent.TITLECOLOR);
			ObserverManage.getObserver().setMessage(messageIntent);

		}
	};

	public void back(View v) {
		finish();
	}

	@Override
	public void finish() {
		ObserverManage.getObserver().deleteObserver(this);
		super.finish();
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SongMessage) {
			SongMessage songMessageTemp = (SongMessage) data;

			if (songMessageTemp.getType() == SongMessage.DESLRCSHOWORHIDEED) {
				Message msg = new Message();
				msg.obj = songMessageTemp;
				mHandler.sendMessage(msg);
			}
		}
	}
}
