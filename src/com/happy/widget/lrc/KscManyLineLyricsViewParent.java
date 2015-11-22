package com.happy.widget.lrc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 酷狗歌手写真界面
 * 
 * @author Administrator
 * 
 */
public class KscManyLineLyricsViewParent extends RelativeLayout {

	private Context context;

	/**
	 * 可上下滑动的view
	 */
	private View verticalScrollChildView;

	/**
	 * 是否是子view事件
	 */
	private boolean childEvent = false;

	public KscManyLineLyricsViewParent(Context context) {
		super(context);
		init(context);
	}

	public KscManyLineLyricsViewParent(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public KscManyLineLyricsViewParent(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@SuppressLint("NewApi")
	public KscManyLineLyricsViewParent(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
	}

	// @Override
	// public boolean onInterceptTouchEvent(MotionEvent event) {
	// return true;
	// }

	private float x_tmp1 = 0;
	private float y_tmp1 = 0;

	private float x_tmp2 = 0;
	private float y_tmp2 = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// 获取当前坐标
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			x_tmp1 = x;
			y_tmp1 = y;

			// Log.e(tag, mTagTip + "ACTION_DOWN");
			if (verticalScrollChildView != null) {
				verticalScrollChildView.onTouchEvent(event);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// Log.e(tag, mTagTip + "ACTION_MOVE");
			x_tmp2 = x;
			y_tmp2 = y;
			if (x_tmp1 != 0 && y_tmp1 != 0) {
				if (x_tmp1 - x_tmp2 > 8 && !childEvent) {
					// Log.i(tag, mTagTip + "向左滑动");
					// childEvent = false;
					// move(x_tmp1 - x_tmp2);
				} else if (x_tmp2 - x_tmp1 > 8 && !childEvent) {
					// childEvent = false;
					// move(x_tmp2 - x_tmp1);
					// Log.i(tag, mTagTip + "向右滑动");
				} else if (y_tmp1 - y_tmp2 > 8) {
					childEvent = true;
					// Log.i(tag, mTagTip + "向上滑动");
					if (verticalScrollChildView != null) {
						verticalScrollChildView.onTouchEvent(event);
					}
				} else if (y_tmp2 - y_tmp1 > 8) {
					childEvent = true;
					// Log.i(tag, mTagTip + "向下滑动");
					if (verticalScrollChildView != null) {
						verticalScrollChildView.onTouchEvent(event);
					}
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			// Log.e(tag, mTagTip + "ACTION_UP");
			if (verticalScrollChildView != null && childEvent) {
				verticalScrollChildView.onTouchEvent(event);
			}
			childEvent = false;
			break;
		}
		return true;
	}

	public void setVerticalScrollChildView(View verticalScrollChildView) {
		this.verticalScrollChildView = verticalScrollChildView;
	}

}
