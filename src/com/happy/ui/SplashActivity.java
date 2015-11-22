package com.happy.ui;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.happy.common.Constants;
import com.happy.model.app.HttpResult;
import com.happy.model.pc.Splash;
import com.happy.util.DataUtil;
import com.happy.util.HttpUtil;
import com.happy.util.ImageLoadUtil;
import com.happy.util.ImageUtil;

public class SplashActivity extends Activity {
	/**
	 * 跳转到主页面
	 */
	private final int GOHOME = 0;
	/**
	 * 页面停留时间 5s
	 */
	private final int SLEEPTIME = 4000;
	/**
	 * splash ImageView
	 */
	private ImageView splashImageView = null;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GOHOME:
				goHome();
				break;
			default:
				break;
			}
		}

	};

	@SuppressLint({ "InlinedApi", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		}

		setContentView(R.layout.activity_splash);
		init();
		loadSplash();
	}

	private void init() {
		splashImageView = (ImageView) findViewById(R.id.splash);
		// 加载splash里面的图片文件
		ImageUtil.loadImageFormFile(splashImageView, Constants.PATH_SPLASH_JPG,
				this, R.drawable.splash);
		// splashImageView.setBackgroundResource(R.drawable.splash);
	}

	private void loadSplash() {

		new AsyncTask<String, Integer, HttpResult<Splash>>() {

			@Override
			protected HttpResult<Splash> doInBackground(String... arg0) {
				return HttpUtil.getSplashMessageByDate(SplashActivity.this);
			}

			@Override
			protected void onPostExecute(HttpResult<Splash> httpResult) {
				int status = httpResult.getStatus();
				if (status == HttpUtil.SUCCESS) {
					Splash splash = httpResult.getModel();
					String sid = splash.getSid();
					String url = HttpUtil.getSplashImageByID(sid);
					loadImageFormUrl(url);
				}
				loadData();
			}

		}.execute("");

	}

	/**
	 * 加载网络图片
	 * 
	 * @param sid
	 * @param url
	 */
	protected void loadImageFormUrl(String url) {

		ImageLoadUtil.loadImageFormUrl(url, splashImageView, R.drawable.splash,
				true);
	}

	private void loadData() {
		new AsyncTask<String, Integer, Integer>() {

			@Override
			protected Integer doInBackground(String... params) {

				// 配置数据初始化
				DataUtil.init(SplashActivity.this);
				loadSplashMusic();
				return GOHOME;
			}

			@Override
			protected void onPostExecute(Integer result) {

				// 根据返回的result 跳转到不同的页面
				mHandler.sendEmptyMessageDelayed(result, SLEEPTIME);
				// mHandler.sendEmptyMessage(result);
			}

		}.execute("");

	}

	/**
	 * 加载启动页面的问候语
	 */
	protected void loadSplashMusic() {
		boolean isSayHello = (Boolean) DataUtil.getValue(this,
				Constants.isSayHello_KEY, Constants.isSayHello);
		if (isSayHello) {
			AssetManager assetManager = getAssets();
			AssetFileDescriptor fileDescriptor;
			try {
				fileDescriptor = assetManager.openFd("hellolele.mp3");
				MediaPlayer mediaPlayer = new MediaPlayer();
				mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 跳转到主界面
	 */
	protected void goHome() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		// 添加界面切换效果，注意只有Android的2.0(SdkVersion版本号为5)以后的版本才支持
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version >= 5) {
			overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
			// overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
		}
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
