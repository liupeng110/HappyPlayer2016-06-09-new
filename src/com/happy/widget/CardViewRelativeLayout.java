package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;

public class CardViewRelativeLayout extends RelativeLayout implements Observer {

	private SkinInfo skinInfo;

	private boolean isLoadColor = false;

	public CardViewRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CardViewRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CardViewRelativeLayout(Context context) {
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
			int strokeWidth = 1; // 3dp 边框宽度
			float[] roundRadius = { 0, 0, 0, 0, 15, 15, 15, 15 }; // 圆角半径
			int strokeColor = skinInfo.getIndicatorLineBackgroundColor();// 边框颜色
			int fillColor = skinInfo.getIndicatorBackgroundColor();

			GradientDrawable gd = new GradientDrawable();// 创建drawable
			gd.setColor(fillColor);
			gd.setCornerRadii(roundRadius);
			gd.setStroke(strokeWidth, strokeColor);
			setBackgroundDrawable(gd);

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
