package com.happy.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.happy.adapter.RecommendSongAdapter;
import com.happy.common.Constants;
import com.happy.db.SongDB;
import com.happy.logger.LoggerManage;
import com.happy.model.app.HttpResult;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.model.app.SongInfo;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;
import com.happy.util.HttpUtil;
import com.happy.util.ToastUtil;
import com.happy.widget.LoadSwipeRefreshLayout;

public class TabRecommendFragment extends Fragment implements Observer {

	public TabRecommendFragment() {

	}

	private View mMainView;

	private SkinInfo skinInfo;

	private LinearLayoutManager mLayoutManager;
	private RecyclerView recyclerView;
	private List<SongInfo> datas;
	private RecommendSongAdapter adapter;

	private SwipeRefreshLayout swipeLayout;

	private LoggerManage logger;

	private int lastVisibleItem = -1;
	private boolean hasData = true;
	private boolean isloadDataing = false;
	private boolean downScrolled = false;

	private LoadSwipeRefreshLayout loadSwipeRefreshLayout;

	private boolean isFirst = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initComponent();
		initSkin();
		ObserverManage.getObserver().addObserver(this);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		if (isVisibleToUser && isFirst) {
			isFirst = false;
			initData();
		}
	}

	private void initComponent() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		mMainView = inflater.inflate(R.layout.fragment_tab_recommend, null,
				false);

		recyclerView = (RecyclerView) mMainView.findViewById(R.id.listview);
		// 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
		recyclerView.setHasFixedSize(true);
		swipeLayout = (SwipeRefreshLayout) mMainView
				.findViewById(R.id.swipe_refresh_widget);

		mLayoutManager = new LinearLayoutManager(getActivity());

		recyclerView.setLayoutManager(mLayoutManager);

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
				lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
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

		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				loadNewData();
			}
		});
		swipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
				android.R.color.holo_blue_light,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light);
		//
		loadSwipeRefreshLayout = (LoadSwipeRefreshLayout) mMainView
				.findViewById(R.id.loadSwipeRefreshLayout);
		loadSwipeRefreshLayout.init(this.getActivity());
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) mMainView.getParent();
		if (viewGroup != null) {
			viewGroup.removeAllViewsInLayout();
		}
		return mMainView;
	}

	private void initSkin() {
		skinInfo = Constants.skinInfo;
		mMainView.setBackgroundColor(skinInfo.getBackgroundColor());
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		datas = new ArrayList<SongInfo>();

		adapter = new RecommendSongAdapter(this.getActivity(), datas);
		recyclerView.setAdapter(adapter);

		// 先从数据库里面获取
		// 再根据数据库的数据，获取应用最后更新的时间
		// 根据最后更新的时间，向服务器获取最新的数据
		new AsyncTask<String, Integer, List<SongInfo>>() {

			@Override
			protected List<SongInfo> doInBackground(String... arg0) {
				return SongDB.getSongInfoDB(getActivity())
						.getAllRecommendSong();
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(List<SongInfo> result) {
				if (result == null || result.size() == 0) {

				} else {
					for (int i = result.size() - 1; i >= 0; i--) {
						datas.add(0, result.get(i));
						adapter.notifyItemInserted(0);
					}
					recyclerView.scrollToPosition(0);
					// adapter.notifyDataSetChanged();
				}
				loadNewData();
			}

		}.execute("");
	}

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
		new AsyncTask<String, Integer, HttpResult<SongInfo>>() {

			@Override
			protected HttpResult<SongInfo> doInBackground(String... params) {

				isloadDataing = true;

				// swipeBackLayout.setEnableGesture(false);
				return HttpUtil.loadMoreSongInfoByCreateTime(
						TabRecommendFragment.this.getActivity(), params[0]);
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(HttpResult<SongInfo> httpResult) {
				int status = httpResult.getStatus();
				if (status == HttpUtil.NORESULT) {
					hasData = false;
				} else if (status == HttpUtil.SUCCESS) {
					List<SongInfo> result = httpResult.getModels();
					ToastUtil.showTextToast(
							TabRecommendFragment.this.getActivity(), "为您加载"
									+ result.size() + "条数据");
					if (result.size() < 10) {
						hasData = false;
					}
					for (int i = 0; i < result.size(); i++) {
						SongInfo SongInfo = result.get(i);

						if (!SongDB.getSongInfoDB(getActivity())
								.songIsExistsBySID(SongInfo.getSid())) {
							SongDB.getSongInfoDB(getActivity()).add(SongInfo);
						}

						datas.add(SongInfo);
						adapter.notifyItemInserted(datas.size() - 1);
					}
					// recyclerView.scrollToPosition(datas.size() - 1);
					// adapter.notifyDataSetChanged();
				}
				// swipeBackLayout.setEnableGesture(true);

				isloadDataing = false;
			}

		}.execute(createTime);
	}

	protected void loadNewData() {

		String createTime = "";
		if (datas != null && datas.size() != 0) {
			createTime = datas.get(0).getCreateTime();
		}
		new AsyncTask<String, Integer, HttpResult<SongInfo>>() {

			@Override
			protected HttpResult<SongInfo> doInBackground(String... params) {
				swipeLayout.setRefreshing(true);
				// swipeBackLayout.setEnableGesture(false);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return HttpUtil.loadNewSongInfoByCreateTime(
						TabRecommendFragment.this.getActivity(), params[0]);
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(HttpResult<SongInfo> httpResult) {
				swipeLayout.setRefreshing(false);
				int status = httpResult.getStatus();
				if (status == HttpUtil.SUCCESS) {
					List<SongInfo> result = httpResult.getModels();
					ToastUtil.showTextToast(
							TabRecommendFragment.this.getActivity(), "为您更新"
									+ result.size() + "条数据");
					for (int i = result.size() - 1; i >= 0; i--) {
						SongInfo SongInfo = result.get(i);

						if (!SongDB.getSongInfoDB(getActivity())
								.songIsExistsBySID(SongInfo.getSid())) {
							SongDB.getSongInfoDB(getActivity()).add(SongInfo);
						}
						datas.add(0, SongInfo);
						adapter.notifyItemInserted(0);
					}
					recyclerView.scrollToPosition(0);
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

	class ItemOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// case R.id.setting_system_style:
			// goTheme();
			// break;
			}
		}
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			initSkin();
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onDestroy() {
		ObserverManage.getObserver().deleteObserver(this);
		if (adapter != null)
			adapter.finish();
		super.onDestroy();
	}
}
