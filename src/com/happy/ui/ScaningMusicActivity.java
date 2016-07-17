package com.happy.ui;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.happy.db.SongDB;
import com.happy.logger.LoggerManage;
import com.happy.manage.ActivityManage;
import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.model.app.StorageInfo;
import com.happy.observable.ObserverManage;
import com.happy.util.AniUtil;
import com.happy.util.AudioFilter;
import com.happy.util.MediaUtils;
import com.happy.util.StorageListUtil;
import com.happy.widget.CycleViewPager;

public class ScaningMusicActivity extends FragmentActivity implements
		OnPageChangeListener {

	private int currentPage = 1;// 当前展示的页码

	private int[] pics = { R.drawable.img_scan_navigation001,
			R.drawable.img_scan_navigation002,
			R.drawable.img_scan_navigation003,
			R.drawable.img_scan_navigation004,
			R.drawable.img_scan_navigation005,
			R.drawable.img_scan_navigation006 };

	private ImageView[] tips;// 提示性点点数组

	private CycleViewPager pager;

	private static final int MSG_CHANGE_PHOTO = 1;

	private LoggerManage logger;

	/** 图片自动切换时间 */
	private static final int PHOTO_CHANGE_TIME = 2000;
	private Handler mHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case MSG_CHANGE_PHOTO:
				int index = pager.getCurrentItem();
				if (index == pics.length + 1) {
					pager.setCurrentItem(2);
				} else {
					pager.setCurrentItem(index + 1);
				}
				mHandler.sendEmptyMessageDelayed(MSG_CHANGE_PHOTO,
						PHOTO_CHANGE_TIME);
				break;
			}
			super.dispatchMessage(msg);
		}
	};

	private Handler scanHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// 开始扫描
				scaningPICImageView.setVisibility(View.VISIBLE);
				scanedPICImageView.setVisibility(View.INVISIBLE);
				scanedOKImageView.setVisibility(View.INVISIBLE);
				finishButton.setVisibility(View.INVISIBLE);
				pathTextView.setVisibility(View.VISIBLE);
				AniUtil.startAnimation(aniLoading);
				break;
			case 1:
				// 扫描完成
				scaningPICImageView.setVisibility(View.INVISIBLE);
				scanedPICImageView.setVisibility(View.VISIBLE);
				scanedOKImageView.setVisibility(View.VISIBLE);
				finishButton.setVisibility(View.VISIBLE);
				pathTextView.setVisibility(View.INVISIBLE);
				AniUtil.stopAnimation(aniLoading);
				break;
			case 2:
				// 扫描中
				String path = (String) msg.obj;
				pathTextView.setText(path);
				break;
			default:
				break;
			}
			scaningTipTextView.setText("已添加歌曲" + songSize + "首");
		}

	};

	/**
	 * 扫描图片
	 */
	private ImageView scaningPICImageView;
	private AnimationDrawable aniLoading;

	/**
	 * 扫描完成图片
	 */
	private ImageView scanedPICImageView;
	/**
	 * 扫描完成ok
	 */
	private ImageView scanedOKImageView;
	/**
	 * 扫描结果
	 */
	private TextView scaningTipTextView;
	/**
	 * 扫描路径
	 */
	private TextView pathTextView;

	/**
	 * 完成按钮
	 */
	private Button finishButton;

	/**
	 * 是否完成
	 */
	private boolean isFinish = false;
	/**
	 * 歌曲首数
	 */
	private int songSize = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scaningmusic);
		initComponent();
		loadData();
		ActivityManage.getInstance().addActivity(this);
		mHandler.sendEmptyMessageDelayed(MSG_CHANGE_PHOTO, PHOTO_CHANGE_TIME);
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
		}
	}

	private void initComponent() {
		logger = LoggerManage.getZhangLogger(this);
		pager = (CycleViewPager) findViewById(R.id.viewpager);

		// 存放点点的容器
		LinearLayout tipsBox = (LinearLayout) findViewById(R.id.tipsBox);
		// 初始化 提示点点
		tips = new ImageView[pics.length];
		for (int i = 0; i < tips.length; i++) {
			ImageView img = new ImageView(this);
			img.setLayoutParams(new LayoutParams(10, 10));
			tips[i] = img;
			if (i == 0) {
				img.setBackgroundResource(R.drawable.img_imageview_navigate_dot_focused);
			} else {
				img.setBackgroundResource(R.drawable.img_imageview_navigate_dot_normal);
			}
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			params.leftMargin = 5;
			params.rightMargin = 5;
			tipsBox.addView(img, params);
		}

		pager.setOnPageChangeListener(this);
		pager.setAdapter(adapter);

		scaningPICImageView = (ImageView) findViewById(R.id.scaning_pic);
		aniLoading = (AnimationDrawable) scaningPICImageView.getBackground();

		scanedPICImageView = (ImageView) findViewById(R.id.scaned_pic);
		scanedOKImageView = (ImageView) findViewById(R.id.scaned_ok);
		scaningTipTextView = (TextView) findViewById(R.id.scaningTip);
		pathTextView = (TextView) findViewById(R.id.scaningPathTip);
		finishButton = (Button) findViewById(R.id.scanFinishButton);

		finishButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, 0);
			}
		});
	}

	private void loadData() {
		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				scanStart();
				scaning();
				// try {
				// Thread.sleep(3000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				scaned();
			}

		}.execute("");
	}

	/**
	 * 扫描开始
	 */
	private void scanStart() {
		scanHandler.sendEmptyMessage(0);
		isFinish = false;
	}

	/**
	 * 扫描中
	 */
	private void scaning() {
		scannerMusic();
	}

	/**
	 * 扫描歌曲，从手机文件夹里面进行递归扫描
	 */
	private void scannerMusic() {
		songSize = 0;
		List<StorageInfo> list = StorageListUtil
				.listAvaliableStorage(getApplicationContext());
		for (int i = 0; i < list.size(); i++) {
			StorageInfo storageInfo = list.get(i);
			scannerLocalMP3File(storageInfo.path, true);
		}
	}

	/**
	 * 
	 * @param Path
	 *            搜索目录
	 * @param Extension
	 *            扩展名
	 * @param IsIterative
	 *            是否进入子文件夹
	 */
	public void scannerLocalMP3File(String Path, boolean IsIterative) {
		File[] files = new File(Path).listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File f = files[i];

				Message msg = new Message();
				msg.what = 2;
				msg.obj = f.getPath();
				scanHandler.sendMessage(msg);

				if (f.isFile() && AudioFilter.acceptFilter(f)) {
					String Extension = MediaUtils.getFileExt(f.getPath());
					if (f.getPath().endsWith(Extension)) // 判断扩展名
					{
						if (!f.exists()) {
							continue;
						}
						// 文件名
						String displayName = f.getName();
						if (displayName.endsWith(Extension)) {
							String[] displayNameArr = displayName
									.split(Extension);
							displayName = displayNameArr[0].trim();
						}

						boolean isExists = SongDB.getSongInfoDB(this)
								.songIsExists(displayName);
						if (isExists) {
							continue;
						}
						// 将扫描到的数据保存到播放列表
						SongInfo songInfo = MediaUtils.getSongInfoByFile(f
								.getPath());
						if (songInfo != null) {
							SongDB.getSongInfoDB(this).add(songInfo);
							songSize++;
						} else {
							continue;
						}

					}
					if (!IsIterative)
						break;
				} else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) // 忽略点文件（隐藏文件/文件夹）
				{
					scannerLocalMP3File(f.getPath(), IsIterative);
				}
			}
		}
	}

	/**
	 * 扫描完成
	 */
	private void scaned() {
		scanHandler.sendEmptyMessage(1);
		isFinish = true;

		SongMessage songMessage = new SongMessage();
		songMessage.setType(SongMessage.SCANEDMUSIC);
		// 通知
		ObserverManage.getObserver().setMessage(songMessage);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (isFinish) {
				finish();
				overridePendingTransition(0, 0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int i) {
		tips[currentPage]
				.setBackgroundResource(R.drawable.img_imageview_navigate_dot_normal);
		if (i == 1 || i == pics.length + 1) {
			currentPage = 0;
		} else if (i == 0) {
			currentPage = tips.length - 1;
		} else {
			currentPage = i - 1;
		}
		tips[currentPage]
				.setBackgroundResource(R.drawable.img_imageview_navigate_dot_focused);
	}

	private PagerAdapter adapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return pics.length;
		}

		public Object instantiateItem(android.view.ViewGroup container,
				int position) {
			View itemView = LayoutInflater.from(ScaningMusicActivity.this)
					.inflate(R.layout.viewpager_item_pic, null);
			ImageView item = (ImageView) itemView.findViewById(R.id.pic);
			item.setBackgroundResource(pics[position]);
			container.addView(itemView);
			return itemView;
		}

		public void destroyItem(android.view.ViewGroup container, int position,
				Object object) {
			container.removeView((View) object);
		}
	};
}
