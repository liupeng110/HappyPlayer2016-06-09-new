package com.happy.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class BaseStatusBarRelativeLayout extends RelativeLayout {

	public BaseStatusBarRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public BaseStatusBarRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BaseStatusBarRelativeLayout(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	private void init(Context context) {
		setBackground();
	}

	private void setBackground() {
		setBackgroundColor(Color.TRANSPARENT);
	}
}
