package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.EditText;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;

public class SearchEditText extends EditText implements Observer {
	private SkinInfo skinInfo;
	private boolean isLoadColor = false;

	public SearchEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SearchEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SearchEditText(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		ObserverManage.getObserver().addObserver(this);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {

		if (!isLoadColor) {
			setBackgroundColor(Color.TRANSPARENT);
			skinInfo = Constants.skinInfo;
			setTextColor(skinInfo.getIndicatorLineBackgroundColor());
			setHintTextColor(skinInfo.getIndicatorLineBackgroundColor());

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
