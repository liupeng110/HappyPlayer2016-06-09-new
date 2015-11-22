package com.happy.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.happy.ui.R;

@SuppressLint("InlinedApi")
public class LoadSwipeRefreshLayout extends SwipeRefreshLayout {

	/**
	 * 加载完成后显示的页面
	 */
	private RelativeLayout contentView;
	/**
	 * 父页面
	 */
	private RelativeLayout parentView;
	/**
	 * 正在加载页面
	 */
	private View loadingView;
	/**
	 * 旋转动画
	 */
	private Animation rotateAnimation;
	private LoadingImageView loadingImageView;

	private View noNetView;
	private View noWifiView;
	private View noResultView;

	private View errorView;
	private View serrorView;

	public LoadSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoadSwipeRefreshLayout(Context context) {
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
		parentView = (RelativeLayout) getChildAt(0);

		contentView = (RelativeLayout) parentView.getChildAt(0);
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

		noNetView = inflater.inflate(R.layout.view_nonet, null, false);
		noNetView.setVisibility(View.INVISIBLE);
		noNetView.setLayoutParams(params);

		noWifiView = inflater.inflate(R.layout.view_nowifi, null, false);
		noWifiView.setVisibility(View.INVISIBLE);
		noWifiView.setLayoutParams(params);

		noResultView = inflater.inflate(R.layout.view_noresult, null, false);
		noResultView.setVisibility(View.INVISIBLE);
		noResultView.setLayoutParams(params);

		errorView = inflater.inflate(R.layout.view_error, null, false);
		errorView.setVisibility(View.INVISIBLE);
		errorView.setLayoutParams(params);

		serrorView = inflater.inflate(R.layout.view_serror, null, false);
		serrorView.setVisibility(View.INVISIBLE);
		serrorView.setLayoutParams(params);

		setColorSchemeResources(android.R.color.holo_red_light,
				android.R.color.holo_blue_light,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light);

		parentView.addView(loadingView);
		parentView.addView(noNetView);
		parentView.addView(noWifiView);
		parentView.addView(noResultView);
		parentView.addView(errorView);
		parentView.addView(serrorView);
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
		noNetView.setVisibility(View.INVISIBLE);
		noWifiView.setVisibility(View.INVISIBLE);
		noResultView.setVisibility(View.INVISIBLE);
		errorView.setVisibility(View.INVISIBLE);
		serrorView.setVisibility(View.INVISIBLE);
		setEnabled(false);
		setRefreshing(false);
	}

	/**
	 * 没有网络界面
	 */
	public void showNoNetView() {
		contentView.setVisibility(View.INVISIBLE);
		noNetView.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.INVISIBLE);
		noWifiView.setVisibility(View.INVISIBLE);
		noResultView.setVisibility(View.INVISIBLE);
		errorView.setVisibility(View.INVISIBLE);
		serrorView.setVisibility(View.INVISIBLE);

		setEnabled(true);
		setRefreshing(false);
	}

	/**
	 * 没有wifi
	 */
	public void showNoWifiView() {

		contentView.setVisibility(View.INVISIBLE);
		noWifiView.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.INVISIBLE);
		noNetView.setVisibility(View.INVISIBLE);
		noResultView.setVisibility(View.INVISIBLE);
		errorView.setVisibility(View.INVISIBLE);
		serrorView.setVisibility(View.INVISIBLE);

		setEnabled(true);
		setRefreshing(false);
	}

	/**
	 * 显示服务器异常窗口
	 */
	public void showErrorView() {

		contentView.setVisibility(View.INVISIBLE);
		errorView.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.INVISIBLE);
		noNetView.setVisibility(View.INVISIBLE);
		noResultView.setVisibility(View.INVISIBLE);
		noWifiView.setVisibility(View.INVISIBLE);
		serrorView.setVisibility(View.INVISIBLE);

		setEnabled(true);
		setRefreshing(false);
	}

	/**
	 * 显示请求异常窗口
	 */
	public void showSErrorView() {

		contentView.setVisibility(View.INVISIBLE);
		serrorView.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.INVISIBLE);
		noNetView.setVisibility(View.INVISIBLE);
		noResultView.setVisibility(View.INVISIBLE);
		noWifiView.setVisibility(View.INVISIBLE);
		errorView.setVisibility(View.INVISIBLE);

		setEnabled(true);
		setRefreshing(false);
	}

	/**
	 * 显示没有结果
	 */
	public void showNoResultView() {

		contentView.setVisibility(View.INVISIBLE);
		noResultView.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.INVISIBLE);
		noNetView.setVisibility(View.INVISIBLE);
		noWifiView.setVisibility(View.INVISIBLE);
		errorView.setVisibility(View.INVISIBLE);
		serrorView.setVisibility(View.INVISIBLE);

		setEnabled(true);
		setRefreshing(false);
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
		noNetView.setVisibility(View.INVISIBLE);
		loadingView.setVisibility(View.INVISIBLE);
		noWifiView.setVisibility(View.INVISIBLE);
		noResultView.setVisibility(View.INVISIBLE);

		errorView.setVisibility(View.INVISIBLE);
		serrorView.setVisibility(View.INVISIBLE);
		setEnabled(false);
		setRefreshing(false);
	}

}
