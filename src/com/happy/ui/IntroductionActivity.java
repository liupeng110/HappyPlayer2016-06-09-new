package com.happy.ui;

import name.teze.layout.lib.SwipeBackActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.happy.manage.ActivityManage;
import com.happy.widget.LoadRelativeLayout;

@SuppressLint("SetJavaScriptEnabled")
public class IntroductionActivity extends SwipeBackActivity {
	private LoadRelativeLayout loadRelativeLayout;
	private android.webkit.WebView webView;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				loadRelativeLayout.showLoadingView();
				break;
			case 1:
				loadRelativeLayout.showSuccessView();
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_introduction);
		initComponent();
		handler.sendEmptyMessage(0);
		loadData();
		ActivityManage.getInstance().addActivity(this);
		initStatus();
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

	private void initComponent() {
		webView = (android.webkit.WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportMultipleWindows(true);

		loadRelativeLayout = (LoadRelativeLayout) findViewById(R.id.loadRelativeLayout);
		loadRelativeLayout.init(this);
	}

	private void loadData() {
		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(String result) {
				webView.loadUrl("file:///android_asset/www/introduction/index.html");
				handler.sendEmptyMessage(1);
			}

		}.execute("");
	}

	public void back(View v) {
		finish();
	}
}
