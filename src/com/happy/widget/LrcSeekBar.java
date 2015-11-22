package com.happy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.happy.ui.R;
import com.happy.util.MediaUtils;

public class LrcSeekBar extends SeekBar {

	private Context context;
	private Paint backgroundPaint;
	private Paint progressPaint;
	private Paint secondProgressPaint;

	private Paint thumbPaint;

	private boolean isLoadColor = false;

	/**
	 * 弹出提示信息窗口
	 */
	private PopupWindow mPopupWindow;

	/**
	 * 弹出窗口显示文本
	 */
	private TextView tipTextView = null;

	private class SeekBarMessage {
		String timeTip;
		String timeLrc;
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			SeekBarMessage sm = (SeekBarMessage) msg.obj;
			tipTextView.setText(sm.timeTip + sm.timeLrc);
		}
	};

	public LrcSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public LrcSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LrcSeekBar(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化
	 */
	private void init(Context context) {
		this.context = context;
		initPaint();
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

		thumbPaint = new Paint();
		thumbPaint.setDither(true);
		thumbPaint.setAntiAlias(true);

	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		if (!isLoadColor) {
			backgroundPaint.setColor(parserColor("#ffffff,100"));
			progressPaint.setColor(parserColor("#ffffff,255"));
			secondProgressPaint.setColor(parserColor("#ffffff,150"));
			thumbPaint.setColor(parserColor("#9eff3f,255"));
			isLoadColor = true;
		}

		int height = 2;

		Rect backgroundRect = new Rect(0, getHeight() / 2 - height, getWidth(),
				getHeight() / 2 + height);
		canvas.drawRect(backgroundRect, backgroundPaint);
		if (getMax() != 0) {
			Rect secondProgressRect = new Rect(0, getHeight() / 2 - height,
					getSecondaryProgress() * getWidth() / getMax(), getHeight()
							/ 2 + height);
			canvas.drawRect(secondProgressRect, secondProgressPaint);

			Rect progressRect = new Rect(0, getHeight() / 2 - height,
					getProgress() * getWidth() / getMax(), getHeight() / 2
							+ height);
			canvas.drawRect(progressRect, progressPaint);

			int thumbHeight = 6;
			int thumbWidth = 10;
			Rect thumbRect = new Rect(getProgress() * getWidth() / getMax()
					- thumbWidth, getHeight() / 2 - thumbHeight, getProgress()
					* getWidth() / getMax() + thumbWidth, getHeight() / 2
					+ thumbHeight);
			canvas.drawRect(thumbRect, thumbPaint);
		}
	}

	/**
	 * 创建PopupWindow
	 */
	private void initPopuptWindow(String timeStr, View v, String lrc) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View popupWindow = layoutInflater.inflate(
				R.layout.seekbar_progress_dialog, null);
		tipTextView = (TextView) popupWindow.findViewById(R.id.tip);

		tipTextView.setText(timeStr + lrc);

		int padding = 25;

		mPopupWindow = new PopupWindow(popupWindow, screenWidth - padding * 2,
				80, true);
		// mPopupWindow = new PopupWindow(popupWindow, LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT, true);
		// int[] location = new int[2];
		// this.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
		// this.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
		// mPopupWindow.showAsDropDown(v, 0, v.getHeight() - 80);

		int[] location = new int[2];
		v.getLocationOnScreen(location);

		mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, padding, location[1]
				- mPopupWindow.getHeight());
	}

	/**
	 * 获取PopupWindow实例
	 * 
	 * @param lrc
	 */
	public void popupWindowShow(int timeLongStr, View v, String lrc) {
		String timeStr = MediaUtils.formatTime(timeLongStr);
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			Message msg = new Message();
			SeekBarMessage sm = new SeekBarMessage();
			sm.timeTip = timeStr;
			sm.timeLrc = lrc;
			msg.obj = sm;
			handler.sendMessage(msg);
		} else {
			initPopuptWindow(timeStr, v, lrc);
		}
	}

	/**
	 * 关闭窗口
	 */
	public void popupWindowDismiss() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
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
