package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;

public class DividerView extends View implements Observer {
	private boolean isLoadColor = false;
	private SkinInfo skinInfo;

	public DividerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public DividerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DividerView(Context context) {
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
			setBackgroundColor(skinInfo.getPlayBarDividerBackgroundColor());
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
