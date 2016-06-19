package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;

public class BaseSeekBar extends SeekBar implements Observer {

	private Context context;
	private SkinInfo skinInfo;

	private Paint backgroundPaint;
	private Paint progressPaint;
	private Paint secondProgressPaint;

	private boolean isLoadColor = false;

	public BaseSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public BaseSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BaseSeekBar(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化
	 */
	private void init(Context context) {
		this.context = context;
		initPaint();
		ObserverManage.getObserver().addObserver(this);
	}

	private void initPaint() {

		backgroundPaint = new Paint();
		backgroundPaint.setDither(true);
		backgroundPaint.setAntiAlias(true);

		progressPaint = new Paint();
		progressPaint.setDither(true);
		progressPaint.setAntiAlias(true);

		secondProgressPaint = new Paint();
		secondProgressPaint.setDither(true);
		secondProgressPaint.setAntiAlias(true);

	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		if (!isLoadColor) {
			skinInfo = Constants.skinInfo;
			com.happy.model.widget.SeekBar seekBar = skinInfo.getPlayBarSlide();
			backgroundPaint.setColor(seekBar.getBackgroundColor());
			progressPaint.setColor(seekBar.getProgressColor());
			secondProgressPaint.setColor(seekBar.getSecondProgressColor());
			isLoadColor = true;
		}

		Rect backgroundRect = new Rect(0, 0, getWidth(), getHeight());
		canvas.drawRect(backgroundRect, backgroundPaint);
		if (getMax() != 0) {
			if(getSecondaryProgress()!=0){
				Rect secondProgressRect = new Rect(0, 0, getSecondaryProgress()
						* getWidth() / getMax(), getHeight());
				canvas.drawRect(secondProgressRect, secondProgressPaint);
			}
			Rect progressRect = new Rect(0, 0, getProgress() * getWidth()
					/ getMax(), getHeight());
			canvas.drawRect(progressRect, progressPaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			isLoadColor = false;
			invalidate();
		}
	}

}
