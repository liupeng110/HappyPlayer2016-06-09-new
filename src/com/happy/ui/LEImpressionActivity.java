package com.happy.ui;

import java.util.ArrayList;
import java.util.List;

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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.happy.adapter.ImpressionAdapter;
import com.happy.db.SplashDB;
import com.happy.logger.LoggerManage;
import com.happy.manage.ActivityManage;
import com.happy.model.app.HttpResult;
import com.happy.model.pc.Splash;
import com.happy.util.HttpUtil;
import com.happy.util.ToastUtil;
import com.happy.widget.LoadSwipeRefreshLayout;

@SuppressLint("InlinedApi")
public class LEImpressionActivity extends SwipeBackActivity implements
		OnRefreshListener {

	// private SwipeBackLayout swipeBackLayout;

	private RecyclerView recyclerView;
	private GridLayoutManager gridLayoutManager;

	private SwipeRefreshLayout swipeLayout;

	private LoggerManage logger;

	private int lastVisibleItem = -1;
	private boolean hasData = true;
	private boolean isloadDataing = false;
	private boolean downScrolled = false;

	private ImpressionAdapter adapter;

	private List<Splash> datas;

	private LoadSwipeRefreshLayout loadSwipeRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_le_impression);
		initComponent();
		initData();
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

	private void initData() {

		datas = new ArrayList<Splash>();

		adapter = new ImpressionAdapter(LEImpressionActivity.this, datas);
		recyclerView.setAdapter(adapter);

		// 先从数据库里面获取
		// 再根据数据库的数据，获取应用最后更新的时间
		// 根据最后更新的时间，向服务器获取最新的数据
		new AsyncTask<String, Integer, List<Splash>>() {

			@Override
			protected List<Splash> doInBackground(String... arg0) {
				return SplashDB.getSplashDB(LEImpressionActivity.this)
						.getAllSplash();
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(List<Splash> result) {
				if (result == null || result.size() == 0) {

				} else {
					for (int i = result.size() - 1; i >= 0; i--) {
						datas.add(0, result.get(i));
						adapter.notifyItemInserted(0);
					}
					recyclerView.smoothScrollToPosition(0);
					// adapter.notifyDataSetChanged();
				}
				loadNewData();
			}

		}.execute("");

	}

	/**
	 * 加载新的数据
	 */
	protected void loadNewData() {

		// ToastUtil.showTextToast(LEImpressionActivity.this, "正在加载最新的数据,请稍等!");

		String createTime = "";
		if (datas != null && datas.size() != 0) {
			createTime = datas.get(0).getCreateTime();
		}
		new AsyncTask<String, Integer, HttpResult<Splash>>() {

			@Override
			protected HttpResult<Splash> doInBackground(String... params) {
				swipeLayout.setRefreshing(true);
				// swipeBackLayout.setEnableGesture(false);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return HttpUtil.loadNewSplashByCreateTime(
						LEImpressionActivity.this, params[0]);
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(HttpResult<Splash> httpResult) {
				swipeLayout.setRefreshing(false);
				int status = httpResult.getStatus();
				if (status == HttpUtil.SUCCESS) {
					List<Splash> result = httpResult.getModels();
					ToastUtil.showTextToast(LEImpressionActivity.this, "为您更新"
							+ result.size() + "条数据");
					for (int i = result.size() - 1; i >= 0; i--) {
						Splash splash = result.get(i);
						if (!SplashDB.getSplashDB(LEImpressionActivity.this)
								.splashIsExists(splash.getSid())) {
							SplashDB.getSplashDB(LEImpressionActivity.this)
									.add(splash);
						}

						datas.add(0, splash);
						adapter.notifyItemInserted(0);
					}
					recyclerView.smoothScrollToPosition(0);
					handler.sendEmptyMessage(1);
				} else if (status == HttpUtil.HTTPERROR) {
					if (datas == null || datas.size() == 0) {
						handler.sendEmptyMessage(2);
					} else {
						handler.sendEmptyMessage(1);
					}
				} else if (status == HttpUtil.RESULTERROR) {
					if (datas == null || datas.size() == 0) {
						handler.sendEmptyMessage(3);
					} else {
						handler.sendEmptyMessage(1);
					}
				} else if (status == HttpUtil.NONET) {
					if (datas == null || datas.size() == 0) {
						handler.sendEmptyMessage(4);
					} else {
						handler.sendEmptyMessage(1);
					}
				} else if (status == HttpUtil.NOWIFI) {
					if (datas == null || datas.size() == 0) {
						handler.sendEmptyMessage(5);
					} else {
						handler.sendEmptyMessage(1);
					}
				} else if (status == HttpUtil.NORESULT) {
					if (datas == null || datas.size() == 0) {
						handler.sendEmptyMessage(6);
					} else {
						handler.sendEmptyMessage(1);
					}
				} else if (status == HttpUtil.SETRESULTERROR) {
					if (datas == null || datas.size() == 0) {
						handler.sendEmptyMessage(6);
					} else {
						handler.sendEmptyMessage(1);
					}
				}
				// swipeBackLayout.setEnableGesture(true);
			}

		}.execute(createTime);

	}

	private void initComponent() {

		// swipeBackLayout = getSwipeBackLayout();

		logger = LoggerManage.getZhangLogger(this);

		recyclerView = (RecyclerView) findViewById(R.id.listview);
		// 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
		recyclerView.setHasFixedSize(true);
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);

		// 网格布局管理器
		gridLayoutManager = new GridLayoutManager(this, 2);
		gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

		// 设置布局管理器
		recyclerView.setLayoutManager(gridLayoutManager);

		recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					if (hasData) {
						if (lastVisibleItem == -1) {
							if (swipeLayout.isRefreshing()) {
								return;
							}
							loadMoreData();
						} else if (lastVisibleItem + 1 == adapter
								.getItemCount() && downScrolled) {
							if (swipeLayout.isRefreshing()) {
								return;
							}
							loadMoreData();
						}
					}
					// swipeBackLayout.setEnableGesture(true);
				} else {
					// swipeBackLayout.setEnableGesture(false);
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				// swipeBackLayout.setEnableGesture(false);
				lastVisibleItem = gridLayoutManager
						.findLastVisibleItemPosition();
				// dy>0 表示向下滑动
				if (dy > 0) {
					downScrolled = true;
				} else {
					downScrolled = false;
					isloadDataing = true;
				}
				// logger.i("lastVisibleItem = " + lastVisibleItem);
			}

		});

		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
				android.R.color.holo_blue_light,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light);
		//
		loadSwipeRefreshLayout = (LoadSwipeRefreshLayout) findViewById(R.id.loadSwipeRefreshLayout);
		loadSwipeRefreshLayout.init(this);
		loadSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				loadSwipeRefreshLayout.setRefreshing(true);
				// swipeBackLayout.setEnableGesture(false);
				// if (loadSwipeRefreshLayout.isRefreshing()) {
				// return;
				// }
				handler.sendEmptyMessage(0);
				loadNewData();
			}
		});
		handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				loadSwipeRefreshLayout.showLoadingView();
				// loadSwipeRefreshLayout.setEnabled(true);
				// loadSwipeRefreshLayout.setRefreshing(false);
				break;
			case 1:
				loadSwipeRefreshLayout.showSuccessView();
				// loadSwipeRefreshLayout.setEnabled(false);
				// loadSwipeRefreshLayout.setRefreshing(false);
				break;
			case 2:
				loadSwipeRefreshLayout.showErrorView();
				// loadSwipeRefreshLayout.setEnabled(true);
				// loadSwipeRefreshLayout.setRefreshing(false);
				break;
			case 3:
				loadSwipeRefreshLayout.showSErrorView();
				// loadSwipeRefreshLayout.setEnabled(true);
				// loadSwipeRefreshLayout.setRefreshing(false);
				break;
			case 4:
				loadSwipeRefreshLayout.showNoNetView();
				// loadSwipeRefreshLayout.setEnabled(true);
				// loadSwipeRefreshLayout.setRefreshing(false);
				break;
			case 5:
				loadSwipeRefreshLayout.showNoWifiView();
				// loadSwipeRefreshLayout.setEnabled(true);
				// loadSwipeRefreshLayout.setRefreshing(false);
				break;
			case 6:
				loadSwipeRefreshLayout.showNoResultView();
				// loadSwipeRefreshLayout.setEnabled(true);
				// loadSwipeRefreshLayout.setRefreshing(false);
				break;
			}
		}

	};

	protected void loadMoreData() {

		if (isloadDataing)
			return;

		// ToastUtil.showTextToast(LEImpressionActivity.this, "正在加载更多的数据,请稍等!");

		String createTime = "";
		if (datas != null && datas.size() != 0) {
			createTime = datas.get(datas.size() - 1).getCreateTime();
		} else {
			return;
		}
		new AsyncTask<String, Integer, HttpResult<Splash>>() {

			@Override
			protected HttpResult<Splash> doInBackground(String... params) {

				isloadDataing = true;

				// swipeBackLayout.setEnableGesture(false);
				return HttpUtil.loadMoreSplashByCreateTime(
						LEImpressionActivity.this, params[0]);
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(HttpResult<Splash> httpResult) {
				int status = httpResult.getStatus();
				if (status == HttpUtil.NORESULT) {
					hasData = false;
				} else if (status == HttpUtil.SUCCESS) {
					List<Splash> result = httpResult.getModels();
					ToastUtil.showTextToast(LEImpressionActivity.this, "为您加载"
							+ result.size() + "条数据");
					if (result.size() < 10) {
						hasData = false;
					}
					for (int i = 0; i < result.size(); i++) {
						Splash splash = result.get(i);
						if (!SplashDB.getSplashDB(LEImpressionActivity.this)
								.splashIsExists(splash.getSid())) {
							SplashDB.getSplashDB(LEImpressionActivity.this)
									.add(splash);
						}

						datas.add(splash);
						adapter.notifyItemInserted(datas.size() - 1);
					}
					// recyclerView.smoothScrollToPosition(datas.size() - 1);
					// adapter.notifyDataSetChanged();
				}
				// swipeBackLayout.setEnableGesture(true);

				isloadDataing = false;
			}

		}.execute(createTime);
	}

	public void back(View v) {
		finish();
	}

	@Override
	public void onRefresh() {
		loadNewData();
	}
}
