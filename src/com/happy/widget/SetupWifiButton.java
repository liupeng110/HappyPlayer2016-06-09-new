package com.happy.widget;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.happy.ui.R;

public class SetupWifiButton extends ImageView {
	private Bitmap normalIconBitmap;
	private Bitmap pressedIconBitmap;
	private boolean isPressed = false;
	private boolean isLoadImage = false;
	private boolean isSelect = false;
	private Context context;
	private Map<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();

	public SetupWifiButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SetupWifiButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SetupWifiButton(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
	}

	@SuppressLint("NewApi")
	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (!isLoadImage) {
			if (isPressed || isSelect) {
				pressedIconBitmap = bitmaps.get("isTouchIsTrue");
				if (pressedIconBitmap == null) {
					pressedIconBitmap = BitmapFactory.decodeResource(
							getResources(), R.drawable.bt_setup_wifi_hl);

					bitmaps.put("isTouchIsTrue", pressedIconBitmap);
				}
				setBackground(new BitmapDrawable(pressedIconBitmap));
			} else {
				normalIconBitmap = bitmaps.get("isTouchIsFalse");
				if (normalIconBitmap == null) {
					normalIconBitmap = BitmapFactory.decodeResource(
							getResources(), R.drawable.bt_setup_wifi_nor);

					bitmaps.put("isTouchIsFalse", normalIconBitmap);
				}
				setBackground(new BitmapDrawable(normalIconBitmap));
			}
			isLoadImage = true;
		}
		super.dispatchDraw(canvas);
	}

	public void setPressed(boolean pressed) {
		isLoadImage = false;
		isPressed = pressed;
		invalidate();
		super.setPressed(pressed);
	}

	public void setSelect(boolean select) {
		isLoadImage = false;
		isSelect = select;
		invalidate();
	}

	public boolean isSelect() {
		return isSelect;
	}
	
}
