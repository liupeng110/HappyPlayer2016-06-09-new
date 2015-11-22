package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;

/**
 * 
 * @ClassName: MenuItemPressRelativeLayout
 * @Description:TODO(menu的item点击后，切换颜色)
 * @author: Android_Robot
 * @date: 2015-6-11 下午5:42:12
 * 
 */
public class MenuItemPressRelativeLayout extends RelativeLayout implements
		Observer {
	private int pressColor;
	private boolean isPressed = false;
	private boolean isLoadColor = false;
	private SkinInfo skinInfo;

	public MenuItemPressRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MenuItemPressRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MenuItemPressRelativeLayout(Context context) {
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
			pressColor = skinInfo.getMenuItemPressBgColor();
			if (isPressed) {
				setBackgroundColor(pressColor);
			} else {
				setBackgroundColor(Color.TRANSPARENT);
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

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			isLoadColor = false;
			invalidate();
		}
	}

}
