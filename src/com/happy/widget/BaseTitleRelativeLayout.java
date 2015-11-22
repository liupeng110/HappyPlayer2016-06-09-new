package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.happy.common.Constants;
import com.happy.model.app.MessageIntent;
import com.happy.observable.ObserverManage;

public class BaseTitleRelativeLayout extends RelativeLayout implements
		Observer {

	public BaseTitleRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public BaseTitleRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BaseTitleRelativeLayout(Context context) {
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
		// setBackgroundColor(parserColor("#009958,255"));
		// setBackgroundColor(parserColor("#0a152a,255"));
		setBackgroundColor(parserColor(Constants.colorBGColorStr[Constants.colorIndex]));
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

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof MessageIntent) {
			MessageIntent messageIntent = (MessageIntent) data;
			if (messageIntent.getAction().equals(MessageIntent.TITLECOLOR)) {
				setBackground();
			}
		}

	}
}
