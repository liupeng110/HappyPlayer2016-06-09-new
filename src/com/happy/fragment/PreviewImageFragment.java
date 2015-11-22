package com.happy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.happy.ui.R;
import com.happy.util.HttpUtil;
import com.happy.util.ImageLoadUtil;

public class PreviewImageFragment extends Fragment {
	private View mMainView;

	private String sid;

	private ImageView imavPicImageView;

	public PreviewImageFragment() {

	}

	public PreviewImageFragment(String sid) {
		this.sid = sid;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initComponent();
		loadData();
	}

	private void initComponent() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		mMainView = inflater.inflate(R.layout.fragment_item_preview_image,
				null, false);

		imavPicImageView = (ImageView) mMainView.findViewById(R.id.imavPic);
	}

	private void loadData() {
		String imageUrl = HttpUtil.getSplashImageByID(sid);
		ImageLoadUtil.loadImageFormUrl(imageUrl, imavPicImageView,
				R.drawable.picture_manager_default, true);
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

}
