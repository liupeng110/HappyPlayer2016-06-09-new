package com.happy.ui;

import name.teze.layout.lib.SwipeBackActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.happy.manage.ActivityManage;

public class ScanMusicActivity extends SwipeBackActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanmusic);
		initComponent();
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
	}

	public void back(View v) {
		finish();
	}

	public void scanMusic(View v) {
		Intent intent = new Intent(this, ScaningMusicActivity.class);
		startActivity(intent);
		overridePendingTransition(0, 0);
		finish();
	}

}
