package com.happy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.happy.ui.R;

public class LoadRelativeLayout extends RelativeLayout {

	/**
	 * 加载完成后显示的页面
	 */
	private View contentView;
	/**
	 * 正在加载页面
	 */
	private View loadingView;
	/**
	 * 旋转动画
	 */
	private Animation rotateAnimation;
	private LoadingImageView loadingImageView;

	private View noResultView;
	private MainTextView noResultTextView;

	public LoadRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LoadRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoadRelativeLayout(Context context) {
		super(context);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		if (getChildCount() == 0) {
			return;
		}
		contentView = getChildAt(0);
		contentView.setVisibility(View.INVISIBLE);

		LayoutInflater inflater = LayoutInflater.from(context);
		LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		loadingView = inflater.inflate(R.layout.view_loading, null, false);
		loadingImageView = (LoadingImageView) loadingView
				.findViewById(R.id.loadingImageView);
		loadingView.setVisibility(View.INVISIBLE);
		loadingView.setLayoutParams(params);
		rotateAnimation = AnimationUtils.loadAnimation(context,
				R.anim.anim_rotate);
		rotateAnimation.setInterpolator(new LinearInterpolator());// 匀速

		noResultView = inflater.inflate(R.layout.view_noresult, null, false);
		noResultView.setVisibility(View.INVISIBLE);
		noResultView.setLayoutParams(params);

		noResultTextView = (MainTextView) noResultView
				.findViewById(R.id.noResultTip);
		noResultTextView.setText("没有找到相关数据");

		addView(loadingView);
		addView(noResultView);
	}

	/**
	 * 显示正在加载页面
	 */
	public void showLoadingView() {
		if (contentView == null)
			return;
		loadingImageView.clearAnimation();
		loadingImageView.startAnimation(rotateAnimation);
		contentView.setVisibility(View.INVISIBLE);
		loadingView.setVisibility(View.VISIBLE);
		noResultView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 显示加载成功页面
	 */
	public void showSuccessView() {
		if (contentView == null)
			return;
		// 停止动画
		loadingImageView.clearAnimation();
		contentView.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.INVISIBLE);
		noResultView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 显示没有结果
	 */
	public void showNoResultView() {
		contentView.setVisibility(View.INVISIBLE);
		noResultView.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.INVISIBLE);
	}
}
