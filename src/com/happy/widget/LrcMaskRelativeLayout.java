package com.happy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class LrcMaskRelativeLayout extends RelativeLayout {
	private Paint paint;

	public LrcMaskRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public LrcMaskRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LrcMaskRelativeLayout(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		paint = new Paint();
		paint.setDither(true);
		paint.setAntiAlias(true);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		paint.setColor(parserColor("#000000,60"));
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		super.dispatchDraw(canvas);
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
