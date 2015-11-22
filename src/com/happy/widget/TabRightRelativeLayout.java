package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;

public class TabRightRelativeLayout extends RelativeLayout implements Observer {

	private SkinInfo skinInfo;

	private boolean isSelect = true;
	private boolean isLoadColor = false;

	public TabRightRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TabRightRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TabRightRelativeLayout(Context context) {
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
			int strokeWidth = 3; // 3dp 边框宽度
			float[] roundRadius = { 0, 0, 10, 10, 10, 10, 0, 0 }; // 圆角半径
			int strokeColor = skinInfo.getTitleColor();// 边框颜色
			int fillColor = skinInfo.getTitleBackgroundColor();
			if (isSelect) {
				strokeWidth = 0;
				fillColor = skinInfo.getTitleColor();// 内部填充颜色
				invalidateChild(skinInfo.getTitleBackgroundColor());
			} else {
				invalidateChild(skinInfo.getTitleColor());
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

	private void invalidateChild(int textColor) {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View v = getChildAt(i);
			if (v instanceof TextView) {
				TextView temp = (TextView) v;
				temp.setTextColor(textColor);
			}
		}
	}

	/**
	 * 设置标签被选中
	 * 
	 * @param selected
	 */
	public void setSelect(boolean selected) {
		isLoadColor = false;
		isSelect = selected;
		invalidate();
	}

	public boolean isSelect() {
		return isSelect;
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			isLoadColor = false;
			invalidate();
		}
	}

}
