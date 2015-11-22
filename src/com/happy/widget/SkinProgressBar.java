//package com.happy.widget;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Paint.Style;
//import android.graphics.PaintFlagsDrawFilter;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuff.Mode;
//import android.graphics.PorterDuffXfermode;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.util.AttributeSet;
//import android.widget.ProgressBar;
//
//public class SkinProgressBar extends ProgressBar {
//	private Paint backgroundPaint;
//	// private Paint maxPaint;
//	private Paint progressPaint;
//
//	public SkinProgressBar(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		init(context);
//	}
//
//	public SkinProgressBar(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		init(context);
//	}
//
//	public SkinProgressBar(Context context) {
//		super(context);
//		init(context);
//	}
//
//	private PorterDuffXfermode xfermode = new PorterDuffXfermode(
//			PorterDuff.Mode.SRC_OUT);
//
//	/**
//	 * 初始化
//	 */
//	private void init(Context context) {
//
//		backgroundPaint = new Paint();
//		backgroundPaint.setDither(true);
//		backgroundPaint.setAntiAlias(true);
//		backgroundPaint.setStyle(Style.FILL);
//		backgroundPaint.setColor(parserColor("#000000,180"));
//
//		// maxPaint = new Paint();
//		// maxPaint.setDither(true);
//		// maxPaint.setAntiAlias(true);
//		// maxPaint.setColor(parserColor("#000000,200"));
//		// maxPaint.setStyle(Style.FILL);// 空心
//		// maxPaint.setStrokeWidth(10);
//
//		progressPaint = new Paint();
//		progressPaint.setDither(true);
//		progressPaint.setAntiAlias(true);
//		progressPaint.setColor(parserColor("#000000,180"));
//		progressPaint.setStyle(Style.FILL);// 实心
//	}
//
//	@Override
//	protected synchronized void onDraw(Canvas canvas) {
//
//		// save (must save layer before!!!!!!!)
//		int save = canvas.saveLayer(0, 0, getWidth(), getHeight(), null,
//				Canvas.ALL_SAVE_FLAG);
//		Rect backgroundRect = new Rect(0, 0, getWidth(), getHeight());
//		canvas.drawRect(backgroundRect, backgroundPaint);
//
//		backgroundPaint.setXfermode(xfermode);
//
//		int cx = getWidth() / 2;
//		int cy = getHeight() / 2;
//		int r = getWidth() / 3;
//		canvas.drawCircle(cx, cy, r, backgroundPaint);
//		backgroundPaint.setXfermode(null);
//
//		canvas.restoreToCount(save);
//
//		// canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
//		// Paint.ANTI_ALIAS_FLAG
//		// | Paint.FILTER_BITMAP_FLAG));
//
//		// canvas.drawRect(backgroundRect, backgroundPaint);
//
//		// maxPaint.setStrokeWidth(5);
//		// int cx = getWidth() / 2;
//		// int cy = getHeight() / 2;
//		//
//		// r = getWidth() / 3 - 10;
//		// canvas.drawCircle(cx, cy, r, maxPaint);
//
//		if (getMax() != 0) {
//			// int save2 = canvas.saveLayer(0, 0, getWidth(), getHeight(), null,
//			// Canvas.ALL_SAVE_FLAG);
//			// Rect progressRect = new Rect(10, 10, getProgress() * getWidth()
//			// / getMax() - 10, getHeight() - 10);
//			// canvas.drawRect(progressRect, progressPaint);
//
//			r = getWidth() / 3 - 10;
//			RectF oval = new RectF();
//			oval.left = (getWidth() / 2 - r);
//			oval.top = (getHeight() / 2 - r);
//			oval.right = getWidth() / 2 + r;
//			oval.bottom = getHeight() / 2 + r;
//
//			// progressPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
//			canvas.drawArc(oval, -90,
//					((float) getProgress() / getMax()) * 360 - 360, true,
//					progressPaint);
//
//			// canvas.restoreToCount(save2);
//		}
//	}
//
//	/**
//	 * 解析颜色字符串
//	 * 
//	 * @param value
//	 *            颜色字符串 #edf8fc,255
//	 * @return
//	 */
//	private int parserColor(String value) {
//		String regularExpression = ",";
//		if (value.contains(regularExpression)) {
//			String[] temp = value.split(regularExpression);
//
//			int color = Color.parseColor(temp[0]);
//			int alpha = Integer.valueOf(temp[1]);
//			int red = (color & 0xff0000) >> 16;
//			int green = (color & 0x00ff00) >> 8;
//			int blue = (color & 0x0000ff);
//
//			return Color.argb(alpha, red, green, blue);
//		}
//		return Color.parseColor(value);
//	}
//}

package com.happy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class SkinProgressBar extends ProgressBar {
	private Paint backgroundPaint;
	private Paint progressPaint;

	public SkinProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SkinProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SkinProgressBar(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化
	 */
	private void init(Context context) {
		backgroundPaint = new Paint();
		backgroundPaint.setDither(true);
		backgroundPaint.setAntiAlias(true);
		backgroundPaint.setColor(Color.WHITE);
		backgroundPaint.setStyle(Style.STROKE);// 空心
		backgroundPaint.setStrokeWidth(10);

		progressPaint = new Paint();
		progressPaint.setDither(true);
		progressPaint.setAntiAlias(true);
		progressPaint.setColor(Color.WHITE);
		progressPaint.setStyle(Style.FILL);// 实心
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {

		// Rect backgroundRect = new Rect(0, 0, getWidth(), getHeight());
		// canvas.drawRect(backgroundRect, backgroundPaint);

		backgroundPaint.setStrokeWidth(5);
		int cx = getWidth() / 2;
		int cy = getHeight() / 2;
		int r = getWidth() / 3;
		canvas.drawCircle(cx, cy, r, backgroundPaint);

		if (getMax() != 0) {
			// Rect progressRect = new Rect(10, 10, getProgress() * getWidth()
			// / getMax() - 10, getHeight() - 10);
			// canvas.drawRect(progressRect, progressPaint);

			r = getWidth() / 3 - 10;
			RectF oval = new RectF();
			oval.left = (getWidth() / 2 - r);
			oval.top = (getHeight() / 2 - r);
			oval.right = getWidth() / 2 + r;
			oval.bottom = getHeight() / 2 + r;
			canvas.drawArc(oval, -90,
					((float) getProgress() / getMax()) * 360 - 360, true,
					progressPaint);
		}
	}
}
