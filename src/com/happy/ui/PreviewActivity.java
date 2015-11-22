package com.happy.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happy.fragment.PreviewImageFragment;
import com.happy.manage.ActivityManage;
import com.happy.model.pc.Splash;

public class PreviewActivity extends FragmentActivity {

	private ViewPager viewPager;

	private ArrayList<Fragment> imageFragmentList;

	private List<Splash> splashs;

	private TextView titleTextView;

	private int position = 0;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (splashs == null || splashs.size() == 0)
				return;
			Splash splash = splashs.get(position);
			switch (msg.what) {
			case 1:
				titleTextView.setText(splash.getTitle() + "(" + (position + 1)
						+ "/" + splashs.size() + ")");
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);
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

	@SuppressWarnings("unchecked")
	private void initComponent() {
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		splashs = (List<Splash>) getIntent().getSerializableExtra("splashs");
		if (splashs == null || splashs.size() == 0) {
			return;
		}
		int position = getIntent().getIntExtra("position", 1);
		imageFragmentList = new ArrayList<Fragment>();
		for (int i = 0; i < splashs.size(); i++) {
			Splash splash = splashs.get(i);
			PreviewImageFragment pif = new PreviewImageFragment(splash.getSid());
			imageFragmentList.add(pif);
		}
		ImageFragmentPagerAdapter adapter = new ImageFragmentPagerAdapter(
				getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(pageChangedListener);
		viewPager.setOffscreenPageLimit(imageFragmentList.size());
		viewPager.setCurrentItem(position);

		titleTextView = (TextView) findViewById(R.id.title);

		Message msg = new Message();
		msg.what = 1;
		handler.sendMessage(msg);
	}

	public void back(View v) {
		finish();
		overridePendingTransition(0, 0);
	}

	/**
	 * slidingmenu的viewpager滑动事件
	 */
	private OnPageChangeListener pageChangedListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int positionT) {
			Message msg = new Message();
			msg.what = 1;
			position = positionT;
			handler.sendMessage(msg);
		}

		@SuppressLint("NewApi")
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	/**
	 * 
	 * @ClassName: ImageFragmentPagerAdapter
	 * @Description:(图片界面)
	 * @author: Android_Robot
	 * @date: 2015-6-16 下午6:50:16
	 * 
	 */
	public class ImageFragmentPagerAdapter extends FragmentPagerAdapter {

		public ImageFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return imageFragmentList.get(arg0);
		}

		@Override
		public int getCount() {
			return imageFragmentList.size();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(0, 0);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	//
	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// // 清除内存缓存
	// ImageLoader.getInstance().clearMemoryCache();
	// // 清除本地缓存
	// // ImageLoader.getInstance().clearDiskCache();
	// }
}
