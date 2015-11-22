package com.happy.ui;

import java.util.Observable;
import java.util.Observer;

import name.teze.layout.lib.SwipeBackActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happy.manage.ActivityManage;
import com.happy.model.app.DownloadTask;
import com.happy.model.app.HttpResult;
import com.happy.model.pc.AppInfo;
import com.happy.observable.ObserverManage;
import com.happy.util.ApkUtil;
import com.happy.util.HttpUtil;
import com.happy.util.ToastUtil;
import com.happy.widget.AlartOneButtonDialog;
import com.happy.widget.AlartOneButtonDialog.ButtonDialogListener;
import com.happy.widget.AlartTwoButtonDialog.TwoButtonDialogListener;
import com.happy.widget.AlartTwoButtonDialogTitle;
import com.happy.widget.LoadingDialog;
import com.happy.widget.LoadingDialog.DialogListener;

public class AboutActivity extends SwipeBackActivity implements Observer {

	private static Context context;

	/**
	 * 版本号
	 */
	private TextView vtextSecondTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initComponent();
		loadData();
		context = getApplicationContext();
		ActivityManage.getInstance().addActivity(this);
		ObserverManage.getObserver().addObserver(this);
		initStatus();
	}

	/**
	 * 初始化状态栏
	 */
	@SuppressLint("NewApi")
	private void initStatus() {
		if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
					| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
			window.setNavigationBarColor(Color.TRANSPARENT);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT,
					getStatusBarHeight(this));
			RelativeLayout statusView = (RelativeLayout) findViewById(R.id.statusView);
			statusView.setLayoutParams(lp);
			statusView.setVisibility(View.VISIBLE);
		}
	}

	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	private void initComponent() {
		vtextSecondTextView = (TextView) findViewById(R.id.vtext);
	}

	private void loadData() {
		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				return ApkUtil.getVersionName(AboutActivity.this);
			}

			@Override
			protected void onPostExecute(String result) {
				vtextSecondTextView.setText("版本信息: " + result);
			}

		}.execute("");
	}

	public void back(View v) {
		finish();
	}

	/**
	 * 加载窗口
	 */
	private LoadingDialog dialog = null;

	/**
	 * 两个按钮弹出窗口
	 */
	private AlartTwoButtonDialogTitle alartTwoButtonDialog = null;

	/**
	 * 一个按钮弹出窗口
	 */
	private AlartOneButtonDialog alartOneButtonDialog = null;

	public void itemOnClick(View v) {
		switch (v.getId()) {

		// 乐乐印象
		case R.id.leleyinxiang:
			startActivity(new Intent(AboutActivity.this,
					LEImpressionActivity.class));
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.introduction:
			startActivity(new Intent(AboutActivity.this,
					IntroductionActivity.class));
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.update:

			if (MainActivity.task != null) {
				if (MainActivity.task.getStatus() == DownloadTask.DOWNLOING) {
					showOneAlert("新版本正在后台下载中，请稍等。");
					return;
				}
			}

			if (dialog == null)
				dialog = new LoadingDialog(this, R.style.dialog,
						new DialogListener() {

							@Override
							public void onShowed() {
								onShow();
							}

							@Override
							public void onDismissed() {

							}
						});
			dialog.showDialog("正在检查更新......");
			break;
		}
	}

	/**
	 * 检查更新
	 */
	protected void onShow() {

		new AsyncTask<String, Integer, HttpResult<AppInfo>>() {

			@Override
			protected HttpResult<AppInfo> doInBackground(String... arg0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				String versionName = ApkUtil.getVersionName(AboutActivity.this);
				int versionCode = ApkUtil.getVersionCode(AboutActivity.this);
				return HttpUtil.getAppInfoMessage(AboutActivity.this,
						versionName, versionCode);
			}

			@Override
			protected void onPostExecute(HttpResult<AppInfo> httpResult) {
				int status = httpResult.getStatus();
				if (status == HttpUtil.SUCCESS) {
					AppInfo appInfo = httpResult.getModel();
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
						// ToastUtil.showTextToast(AboutActivity.this, "有新版本!");
						showTwoAlert(appInfo);
					}
				} else if (status == HttpUtil.NORESULT) {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
						ToastUtil.showTextToast(AboutActivity.this,
								"当前已经是最新版本!");
					}
				} else {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
						// showOneAlert();
					}
				}
			}

		}.execute("");
	}

	/**
	 * 弹出警告窗口
	 */
	protected void showOneAlert(String text) {
		if (alartOneButtonDialog == null) {
			alartOneButtonDialog = new AlartOneButtonDialog(this,
					R.style.dialog, new ButtonDialogListener() {

						@Override
						public void ButtonClick() {

						}
					});
		}
		alartOneButtonDialog.showDialog(text, "确定");
	}

	/**
	 * 弹出警告窗口
	 * 
	 * @param aid
	 */
	protected void showTwoAlert(final AppInfo appInfo) {

		if (alartTwoButtonDialog == null) {
			alartTwoButtonDialog = new AlartTwoButtonDialogTitle(this,
					R.style.dialog, new TwoButtonDialogListener() {

						@Override
						public void twoButtonClick() {
							ObserverManage.getObserver().setMessage(appInfo);
						}

						@Override
						public void oneButtonClick() {
						}
					});
		}
		alartTwoButtonDialog
				.showDialog("发现新版本", appInfo.getTitle(), "取消", "下载");
	}

	@Override
	public void update(Observable observable, Object data) {

	}

	@Override
	public void finish() {
		ObserverManage.getObserver().deleteObserver(this);
		super.finish();
	}

}
