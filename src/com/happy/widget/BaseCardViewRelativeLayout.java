package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;

public class BaseCardViewRelativeLayout extends RelativeLayout implements
		Observer {

	private SkinInfo skinInfo;

	private boolean isLoadColor = false;

	private boolean isPressed = false;

	public BaseCardViewRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public BaseCardViewRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BaseCardViewRelativeLayout(Context context) {
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
			int strokeColor = skinInfo.getMenuTitleColor();// 边框颜色
			int fillColor = Color.TRANSPARENT;

			if (isPressed) {
				fillColor = parserColor("#000000,100");
			}

			GradientDrawable gd = new GradientDrawable();// 创建drawable
			gd.setColor(fillColor);
			gd.setCornerRadii(roundRadius);
			gd.setStroke(strokeWidth, strokeColor);
			setBackgroundDrawable(gd);

			isLoadColor = true;
		}
		super.dispatchDraw(canvas);
	}

	public void setPressed(boolean pressed) {
		isLoadColor = false;
		isPressed = pressed;
		invalidate();
		super.setPressed(pressed);
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			isLoadColor = false;
			invalidate();
		}
	}

	/**
	 * 解析颜色字符串
	 * 
	 * @param value
	 *            颜色字符串 #edf8fc,255
	 * @return
	 */
	private int parserColor(String value) {
		String regularExpression = ",";
		if (value.contains(regularExpression)) {
			String[] temp = value.split(regularExpression);

			int color = Color.parseColor(temp[0]);
			int alpha = Integer.valueOf(temp[1]);
			int red = (color & 0xff0000) >> 16;
			int green = (color & 0x00ff00) >> 8;
			int blue = (color & 0x0000ff);

			return Color.argb(alpha, red, green, blue);
		}
		return Color.parseColor(value);
	}

}
