package com.happy.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;

public class NoResultImageView extends ImageView implements Observer {
	private Canvas pCanvas;
	private Bitmap defBitmap;
	private Paint paint;
	private Bitmap baseBitmap;
	private boolean isLoadImage = false;
	private SkinInfo skinInfo;
	private Map<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();

	public NoResultImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public NoResultImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public NoResultImageView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		paint = new Paint();
		paint.setDither(true);
		paint.setAntiAlias(true);
		ObserverManage.getObserver().addObserver(this);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (!isLoadImage) {
			baseBitmap = bitmaps.get("isBase");
			if (baseBitmap == null) {
				baseBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.img_empty);
				bitmaps.put("isBase", defBitmap);
			}

			defBitmap = Bitmap.createBitmap(baseBitmap.getWidth(),
					baseBitmap.getHeight(), baseBitmap.getConfig());
			pCanvas = new Canvas(defBitmap);
			skinInfo = Constants.skinInfo;
			int color = skinInfo.getMenuTitleColor();

			float progressR = Color.red(color) / 255f;
			float progressG = Color.green(color) / 255f;
			float progressB = Color.blue(color) / 255f;
			float progressA = Color.alpha(color) / 255f;

			// 根据SeekBar定义RGBA的矩阵
			float[] src = new float[] { progressR, 0, 0, 0, 0, 0, progressG, 0,
					0, 0, 0, 0, progressB, 0, 0, 0, 0, 0, progressA, 0 };
			// 定义ColorMatrix，并指定RGBA矩阵
			ColorMatrix colorMatrix = new ColorMatrix();
			colorMatrix.set(src);
			// 设置Paint的颜色
			paint.setColorFilter(new ColorMatrixColorFilter(src));
			// 通过指定了RGBA矩阵的Paint把原图画到空白图片上
			pCanvas.drawBitmap(baseBitmap, new Matrix(), paint);

			setBackgroundDrawable(new BitmapDrawable(defBitmap));
			isLoadImage = true;
		}
		super.dispatchDraw(canvas);
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
