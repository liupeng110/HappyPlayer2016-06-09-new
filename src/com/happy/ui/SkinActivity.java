package com.happy.ui;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import name.teze.layout.lib.SwipeBackActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.happy.common.Constants;
import com.happy.fragment.SkinMyFragment;
import com.happy.fragment.SkinRecommendFragment;
import com.happy.logger.LoggerManage;
import com.happy.manage.ActivityManage;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;
import com.happy.util.ImageUtil;
import com.happy.util.ImageUtil.ImageLoadCallBack;
import com.happy.widget.NoScrollViewPager;
import com.happy.widget.TabLeftRelativeLayout;
import com.happy.widget.TabRightRelativeLayout;

@SuppressLint("NewApi")
public class SkinActivity extends SwipeBackActivity implements Observer {

	/**
	 * 背景图片
	 */
	private LinearLayout backgroundImage;
	/**
	 * 背景颜色
	 */
	private LinearLayout backgroundColor;

	private LoggerManage logger;

	private SkinInfo skinInfo;

	private TabLeftRelativeLayout leftRelativeLayout;

	private TabRightRelativeLayout rightRelativeLayout;

	//
	private NoScrollViewPager viewPager;
	/**
	 * 页面列表
	 */
	private ArrayList<Fragment> fragmentList;
	private TabFragmentPagerAdapter tabFragmentPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_skin);
		initComponent();
		initSkin();
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

	private void initComponent() {

		backgroundImage = (LinearLayout) findViewById(R.id.backgroundImage);

		backgroundColor = (LinearLayout) findViewById(R.id.backgroundColor);

		logger = LoggerManage.getZhangLogger(this);

		leftRelativeLayout = (TabLeftRelativeLayout) findViewById(R.id.left);
		leftRelativeLayout.setSelect(true);
		leftRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (leftRelativeLayout.isSelect()) {
					return;
				} else {
					leftRelativeLayout.setSelect(true);
					if (rightRelativeLayout.isSelect()) {
						rightRelativeLayout.setSelect(false);
					}
					viewPager.setCurrentItem(0, false);
				}
			}
		});

		rightRelativeLayout = (TabRightRelativeLayout) findViewById(R.id.right);
		rightRelativeLayout.setSelect(false);
		rightRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (rightRelativeLayout.isSelect()) {
					return;
				} else {
					rightRelativeLayout.setSelect(true);
					if (leftRelativeLayout.isSelect()) {
						leftRelativeLayout.setSelect(false);
					}
					viewPager.setCurrentItem(1, false);
				}
			}
		});

		viewPager = (NoScrollViewPager) findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();

		fragmentList.add(new SkinRecommendFragment());
		fragmentList.add(new SkinMyFragment());

		//
		tabFragmentPagerAdapter = new TabFragmentPagerAdapter(
				getSupportFragmentManager());
		viewPager.setAdapter(tabFragmentPagerAdapter);

		viewPager.setOffscreenPageLimit(fragmentList.size());
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
		// backgroundColor.setBackgroundColor(skinInfo.getBackgroundColor());
		// } else {
		ImageUtil.loadImageFormFile(backgroundImage,
				skinInfo.getBackgroundPath(), this, imcallBack);
		// }
	}

	public void back(View v) {
		finish();
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			initSkin();
		}
	}

	/**
	 * 
	 * @author Administrator Fragment滑动事件
	 */
	public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragmentList.get(arg0);
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}
	}

	@Override
	public void finish() {
		ObserverManage.getObserver().deleteObserver(this);
		super.finish();
	}
}
