package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.model.widget.Text;
import com.happy.observable.ObserverManage;

public class SlideBar extends View implements Observer {
	// 触摸事件
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	// 26个字母
	public static String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "#" };
	private int choose = -1;// 选中
	private Paint paint = new Paint();

	private SkinInfo skinInfo;

	private TextView mTextDialog;

	/**
	 * 为SideBar设置显示字母的TextView
	 * 
	 * @param mTextDialog
	 */
	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}

	public SlideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SlideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlideBar(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		ObserverManage.getObserver().addObserver(this);
	}

	/**
	 * 重写这个方法
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		skinInfo = Constants.skinInfo;

		Text text = skinInfo.getIndicatorTitleText();

		// 获取焦点改变背景颜色.
		int height = getHeight();// 获取对应高度
		int width = getWidth(); // 获取对应宽度
		int singleHeight = height / b.length;// 获取每一个字母的高度

		for (int i = 0; i < b.length; i++) {
			paint.setColor(text.getNormal());
			// paint.setColor(Color.WHITE);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize(20);

			// x坐标等于中间-字符串宽度的一半.
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight;

			// 选中的状态
			if (i == choose) {
				paint.setColor(text.getNormal());
				canvas.drawRect(paint.measureText(b[i]) / 2, yPos
						- singleHeight + paint.measureText(b[i]), getWidth()
						- paint.measureText(b[i]) / 2,
						yPos + paint.measureText(b[i]), paint);
			}
			if (i == choose) {
				// paint.setColor(text.getSelected());
				paint.setColor(skinInfo.getTitleColor());
				paint.setFakeBoldText(true);
			}

			canvas.drawText(b[i], xPos, yPos, paint);
			paint.reset();// 重置画笔
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();// 点击y坐标
		// final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * b.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

		switch (action) {
		case MotionEvent.ACTION_UP:
			choose = -1;//
			invalidate();
			if (mTextDialog != null) {
				// mTextDialog.setVisibility(View.INVISIBLE);
				mTextDialog.postDelayed(new Runnable() {

					@Override
					public void run() {
						mTextDialog.setVisibility(View.INVISIBLE);
					}
				}, 400);
			}
			break;

		default:
			// if (oldChoose != c) {
			if (c >= 0 && c < b.length) {
				if (listener != null) {
					listener.onTouchingLetterChanged(b[c]);
				}
				if (mTextDialog != null) {
					mTextDialog.setText(b[c]);
					mTextDialog.setVisibility(View.VISIBLE);
				}

				choose = c;
				invalidate();
			}
			// }

			break;
		}
		return true;
	}

	/**
	 * 向外公开的方法
	 * 
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	/**
	 * 接口
	 * 
	 * @author coder
	 * 
	 */
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

	public void setChoose(char c) {
		// this.choose = getIndexByChar(c);
		// invalidate();
	}

	/***
	 * 通过char获取索引
	 * 
	 * @param c
	 * @return
	 */
	private int getIndexByChar(char c) {
		for (int i = 0; i < b.length; i++) {
			if (b[i].equals(c + "")) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			invalidate();
		}
	}
}