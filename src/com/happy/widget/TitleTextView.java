package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;

public class TitleTextView extends TextView implements Observer {

	private int defColor;

	private boolean isLoadColor = false;
	private SkinInfo skinInfo;

	public TitleTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TitleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TitleTextView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		ObserverManage.getObserver().addObserver(this);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (!isLoadColor) {
			skinInfo = Constants.skinInfo;
			defColor = skinInfo.getTitleColor();
			setTextColor(defColor);
			isLoadColor = true;
		}
		super.dispatchDraw(canvas);
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			isLoadColor = false;
			invalidate();
		}
	}

}
