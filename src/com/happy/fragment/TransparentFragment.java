package com.happy.fragment;

import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;

public class TransparentFragment extends Fragment implements Observer {
	private View mMainView;

	public TransparentFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initComponent();
		initSkin();
		ObserverManage.getObserver().addObserver(this);
	}

	private void initComponent() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		mMainView = inflater
				.inflate(R.layout.fragment_transparent, null, false);
		// mMainView.setBackgroundColor(color.translucent);
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

	private void initSkin() {

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
		}
	}

	@Override
	public void onDestroy() {
		ObserverManage.getObserver().deleteObserver(this);
		super.onDestroy();
	}
}
