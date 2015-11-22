package com.happy.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.happy.ui.R;

public class AlartOneButtonDialog extends Dialog {

	private ButtonDialogListener listener;
	/**
	 * 提示
	 */
	private TextView tipTextView;
	/**
	 * 按钮文字提示
	 */
	private TextView centerTextView;

	private AlartDialogCenterButton alartDialogCenterButton;

	public AlartOneButtonDialog(Context context, int theme,
			ButtonDialogListener listener) {
		super(context, theme);
		this.listener = listener;
	}

	public AlartOneButtonDialog(Context context, int theme) {
		super(context, theme);
	}

	protected AlartOneButtonDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public AlartOneButtonDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog_onebutton);

		WindowManager windowManager = (WindowManager) getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = (int) (display.getWidth()); // 设置宽度
		getWindow().setAttributes(lp);

		setCanceledOnTouchOutside(false);

		tipTextView = (TextView) findViewById(R.id.tipCom);
		centerTextView = (TextView) findViewById(R.id.centerTip);

		alartDialogCenterButton = (AlartDialogCenterButton) findViewById(R.id.alartDialogCenterButton);
		alartDialogCenterButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (listener != null) {
					dismiss();
					listener.ButtonClick();
				}
			}
		});
	}

	private Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			show();
			String[] text = (String[]) msg.obj;
			tipTextView.setText(text[0]);
			centerTextView.setText(text[1]);
		}

	};

	/**
	 * 提示
	 * 
	 * @param text
	 */
	public void showDialog(String tipText, String centerText) {
		String[] text = { tipText, centerText };
		Message msg = new Message();
		msg.obj = text;
		mhandler.sendMessage(msg);
	}

	public interface ButtonDialogListener {
		public void ButtonClick();

	}

}
