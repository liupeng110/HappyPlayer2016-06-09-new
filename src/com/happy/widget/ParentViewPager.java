package com.happy.widget;

/////////////////////这个是重点类////////////////////////////
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class ParentViewPager extends ViewPager {
	public ParentViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ParentViewPager(Context context) {
		super(context);
	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		return true;
	}
}