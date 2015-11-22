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
import com.happy.model.widget.Text;
import com.happy.observable.ObserverManage;

public class MainTextView extends TextView implements Observer {

	private int defColor;
	private int selectedColor;
	private int pressColor;

	private boolean isPressed = false;
	private boolean isSelected = false;
	private boolean isLoadColor = false;

	private SkinInfo skinInfo;

	public MainTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MainTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MainTextView(Context context) {
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
			Text commonTitleText = skinInfo.getCommonTitleText();

			defColor = commonTitleText.getNormal();
			selectedColor = commonTitleText.getSelected();
			pressColor = commonTitleText.getSelected();
			if (isPressed) {
				setTextColor(pressColor);
			} else {
				if (isSelected) {
					setTextColor(selectedColor);
				} else {
					setTextColor(defColor);
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
