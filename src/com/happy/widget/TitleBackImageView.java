package com.happy.widget;

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
import com.happy.observable.ObserverManage;
import com.happy.util.ImageUtil;

public class TitleBackImageView extends ImageView implements Observer {
	private Bitmap normalIconBitmap;
	private boolean isLoadImage = false;
	private SkinInfo skinInfo;
	private Context context;

	public TitleBackImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TitleBackImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TitleBackImageView(Context context) {
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
			normalIconBitmap = ImageUtil.loadImageFormFile(skinInfo
					.getTitleBackIcon().getNormal(), context);

			setBackground(new BitmapDrawable(normalIconBitmap));
			isLoadImage = true;
		}
		super.dispatchDraw(canvas);
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			isLoadImage = false;
			invalidate();
		}
	}
}
