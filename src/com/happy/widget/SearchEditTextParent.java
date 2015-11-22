package com.happy.widget;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.happy.common.Constants;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;

public class SearchEditTextParent extends RelativeLayout implements Observer {
	private Paint paint;
	private SkinInfo skinInfo;
	private boolean isLoadColor = false;

	public SearchEditTextParent(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SearchEditTextParent(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SearchEditTextParent(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		paint = new Paint();
		paint.setDither(true);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(3);
		ObserverManage.getObserver().addObserver(this);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {

		if (!isLoadColor) {
			skinInfo = Constants.skinInfo;
			paint.setColor(skinInfo.getIndicatorLineBackgroundColor());
			isLoadColor = true;
		}

		int paintPadding = 10;
		canvas.drawLine(paintPadding, getHeight() / 4 * 3, paintPadding,
				getHeight() - paint.getStrokeWidth(), paint);
		canvas.drawLine(paintPadding, getHeight() - paint.getStrokeWidth(),
				getWidth() - paintPadding,
				getHeight() - paint.getStrokeWidth(), paint);
		canvas.drawLine(getWidth() - paintPadding, getHeight() / 4 * 3,
				getWidth() - paintPadding,
				getHeight() - paint.getStrokeWidth(), paint);

		super.dispatchDraw(canvas);
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			isLoadColor = false;
			invalidate();
		}
	}

}
