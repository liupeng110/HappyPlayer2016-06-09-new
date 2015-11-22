package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;

public class TitleRelativeLayout extends RelativeLayout implements Observer {
	private SkinInfo skinInfo;

	public TitleRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TitleRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TitleRelativeLayout(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	private void init(Context context) {
		ObserverManage.getObserver().addObserver(this);
		setBackground();
	}

	private void setBackground() {
		skinInfo = Constants.skinInfo;
		setBackgroundColor(skinInfo.getTitleBackgroundColor());
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			setBackground();
		}
	}
}
