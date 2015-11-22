package com.happy.iface;

import android.view.View;

public interface PageAction {
	public void addPage(View view, String title);

	public View getPage();

	public void finish();
}
