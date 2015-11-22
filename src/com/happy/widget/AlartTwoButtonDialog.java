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

public class AlartTwoButtonDialog extends Dialog {

	private TwoButtonDialogListener listener;
	/**
	 * 提示
	 */
	private TextView tipTextView;
	/**
	 * 左按钮文字提示
	 */
	private TextView leftTextView;
	/**
	 * 右按钮文字提示
	 */
	private TextView rightTextView;

	private AlartDialogLeftButton alartDialogLeftButton;

	private AlartDialogRightButton alartDialogRightButton;

	public AlartTwoButtonDialog(Context context, int theme,
			TwoButtonDialogListener listener) {
		super(context, theme);
		this.listener = listener;
	}

	public AlartTwoButtonDialog(Context context, int theme) {
		super(context, theme);
	}

	protected AlartTwoButtonDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public AlartTwoButtonDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog_twobutton);

		WindowManager windowManager = (WindowManager) getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = (int) (display.getWidth()); // 设置宽度
		getWindow().setAttributes(lp);

		setCanceledOnTouchOutside(false);

		tipTextView = (TextView) findViewById(R.id.tipCom);
		leftTextView = (TextView) findViewById(R.id.leftTip);
		rightTextView = (TextView) findViewById(R.id.rightTip);

		alartDialogLeftButton = (AlartDialogLeftButton) findViewById(R.id.alartDialogLeftButton);
		alartDialogLeftButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (listener != null) {
					dismiss();
					listener.oneButtonClick();
				}
			}
		});

		alartDialogRightButton = (AlartDialogRightButton) findViewById(R.id.alartDialogRightButton);
		alartDialogRightButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (listener != null) {
					dismiss();
					listener.twoButtonClick();
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
			leftTextView.setText(text[1]);
			rightTextView.setText(text[2]);
		}

	};

	/**
	 * 提示
	 * 
	 * @param text
	 */
	public void showDialog(String tipComText, String leftText, String rightText) {
		String[] text = { tipComText, leftText, rightText };
		Message msg = new Message();
		msg.obj = text;
		mhandler.sendMessage(msg);
	}

	public interface TwoButtonDialogListener {
		public void oneButtonClick();

		public void twoButtonClick();
	}

}
