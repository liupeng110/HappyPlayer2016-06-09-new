package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.model.widget.BackgroundColor;
import com.happy.observable.ObserverManage;

public class PopdownItemRelativeLayout extends RelativeLayout implements
		Observer {

	private int defColor;
	private int selectedColor;
	private int pressColor;

	private boolean isPressed = false;
	private boolean isSelected = false;
	private boolean isLoadColor = false;

	private SkinInfo skinInfo;

	public PopdownItemRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public PopdownItemRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PopdownItemRelativeLayout(Context context) {
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
			BackgroundColor itemBackgroundColor = skinInfo
					.getPopdownItemBackgroundColor();
			defColor = itemBackgroundColor.getNormal();
			selectedColor = itemBackgroundColor.getSelected();
			pressColor = itemBackgroundColor.getSelected();
			if (isPressed) {
				setBackgroundColor(pressColor);
			} else {
				if (isSelected) {
					setBackgroundColor(selectedColor);
				} else {
					setBackgroundColor(defColor);
				}
			}
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

	/**
	 * 设置标签被选中
	 * 
	 * @param selected
	 */
	public void setSelect(boolean selected) {
		isLoadColor = false;
		isSelected = selected;
		invalidate();
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			isLoadColor = false;
			invalidate();
		}
	}

}
