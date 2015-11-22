package com.happy.fragment;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.happy.adapter.SkinThemeAdapter;
import com.happy.db.SkinThemeDB;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;
import com.happy.widget.LoadRelativeLayout;

public class SkinMyFragment extends Fragment implements Observer {
	private View mMainView;

	private LoadRelativeLayout loadRelativeLayout;

	private RecyclerView gridView;

	private List<SkinThemeApp> skinThemes;

	private SkinThemeAdapter adapter;

	private boolean isLoadData = false;

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

	public SkinMyFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initComponent();
		handler.sendEmptyMessage(0);
		ObserverManage.getObserver().addObserver(this);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// 可见
		if (isVisibleToUser && !isLoadData) {
			isLoadData = true;
			loadData();
		}
	}

	private void initComponent() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		mMainView = inflater.inflate(R.layout.fragment_skin_my, null, false);

		gridView = (RecyclerView) mMainView.findViewById(R.id.grid);
		// 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
		gridView.setHasFixedSize(true);
		// 网格布局管理器
		GridLayoutManager gridLayoutManager = new GridLayoutManager(
				getActivity(), 3);
		gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

		// 设置布局管理器
		gridView.setLayoutManager(gridLayoutManager);

		loadRelativeLayout = (LoadRelativeLayout) mMainView
				.findViewById(R.id.loadRelativeLayout);
		loadRelativeLayout.init(getActivity());
	}

	private void loadData() {
		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... arg0) {

				// try {
				// Thread.sleep(100);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				loadSkinThemeData();
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				adapter = new SkinThemeAdapter(
						SkinMyFragment.this.getActivity(), skinThemes);
				gridView.setAdapter(adapter);

				handler.sendEmptyMessage(1);
			}

		}.execute("");
	}

	/**
	 * 加载皮肤数据
	 */
	protected void loadSkinThemeData() {
		skinThemes = SkinThemeDB.getSkinThemeDB(getActivity())
				.getAllSkinTheme();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) mMainView.getParent();
		if (viewGroup != null) {
			viewGroup.removeAllViewsInLayout();
		}
		return mMainView;
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
		if (data instanceof Message) {
			Message msg = (Message) data;
			if (msg.obj instanceof SkinThemeApp) {
				SkinThemeApp skinThemeApp = (SkinThemeApp) msg.obj;
				if (skinThemes != null && !contains(skinThemeApp)) {
					skinThemes.add(skinThemeApp);
					if (adapter != null) {
						adapter.notifyItemInserted(skinThemes.size() - 1);
					}
				}
			}
		}
	}

	/**
	 * 判断列表是否已经含有该对象
	 * 
	 * @param skinThemeApp
	 * @return
	 */
	private boolean contains(SkinThemeApp skinThemeApp) {
		for (int i = 0; i < skinThemes.size(); i++) {
			SkinThemeApp temp = skinThemes.get(i);
			if (temp.getID().equals(skinThemeApp.getID())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onDestroy() {
		ObserverManage.getObserver().deleteObserver(this);
		if (adapter != null)
			adapter.finish();
		super.onDestroy();
	}

}
