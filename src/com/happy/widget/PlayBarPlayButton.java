package com.happy.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.model.widget.ButtonIcon;
import com.happy.observable.ObserverManage;
import com.happy.util.ImageUtil;

public class PlayBarPlayButton extends ImageView implements Observer {
	private Bitmap normalIconBitmap;
	private Bitmap pressedIconBitmap;
	private boolean isPressed = false;
	private boolean isLoadImage = false;
	private SkinInfo skinInfo;
	private Context context;
	private Map<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();

	public PlayBarPlayButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public PlayBarPlayButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PlayBarPlayButton(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		ObserverManage.getObserver().addObserver(this);
	}

	@SuppressLint("NewApi")
	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (!isLoadImage) {
			skinInfo = Constants.skinInfo;
			ButtonIcon buttonIcon = skinInfo.getPlayBarPlayButtonIcon();
			if (isPressed) {
				pressedIconBitmap = bitmaps.get("isTouchIsTrue");
				if (pressedIconBitmap == null) {
					pressedIconBitmap = ImageUtil.loadImageFormFile(
							buttonIcon.getPressedIcon(), context);

					bitmaps.put("isTouchIsTrue", pressedIconBitmap);
				}
				setBackground(new BitmapDrawable(pressedIconBitmap));
			} else {
				normalIconBitmap = bitmaps.get("isTouchIsFalse");
				if (normalIconBitmap == null) {
					normalIconBitmap = ImageUtil.loadImageFormFile(
							buttonIcon.getNormalIcon(), context);

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

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			isLoadImage = false;
			bitmaps = new HashMap<String, Bitmap>();
			invalidate();
		}
	}
}
