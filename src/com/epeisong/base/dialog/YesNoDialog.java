package com.epeisong.base.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.epeisong.R;

/**
 * Yes-or-No对话框
 * 
 * @author poet
 * 
 */
public class YesNoDialog extends ProgressDialog implements
		android.view.View.OnClickListener {

	public static final int BTN_NO = 0;
	public static final int BTN_YES = 1;

	private TextView mTitleTv;
	private TextView mMessageTv;
	private Button mNoBtn, mYesBtn;

	private OnYesNoDialogClickListener mOnYesNoDialogClickListener;

	public YesNoDialog(Activity activity) {
		this(activity, 0);
	}

	public YesNoDialog(Activity activity, int theme) {
		super(activity, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_yes_no);
		mTitleTv = (TextView) findViewById(R.id.tv_title);
		mMessageTv = (TextView) findViewById(R.id.tv_message);
		mNoBtn = (Button) findViewById(R.id.btn_no);
		mYesBtn = (Button) findViewById(R.id.btn_yes);
		mNoBtn.setOnClickListener(this);
		mYesBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_no) {
			dismiss();
			if (mOnYesNoDialogClickListener != null) {
				mOnYesNoDialogClickListener.onYesNoDialogClick(BTN_NO);
			}
		} else if (id == R.id.btn_yes) {
			dismiss();
			if (mOnYesNoDialogClickListener != null) {
				mOnYesNoDialogClickListener.onYesNoDialogClick(BTN_YES);
			}
		}
	}

	public void setTitle(String title) {
		mTitleTv.setText(title);
	}

	public void setMessage(String message) {
		mMessageTv.setText(message);
	}

	public void setButtonTxt(String noText, String yesText) {
		if (!TextUtils.isEmpty(noText)) {
			mNoBtn.setText(noText);
		}
		if (!TextUtils.isEmpty(yesText)) {
			mYesBtn.setText(yesText);
		}
	}

	public void setOnYesNoDialogClickListener(
			OnYesNoDialogClickListener listener) {
		mOnYesNoDialogClickListener = listener;
	}

	public static interface OnYesNoDialogClickListener {
		void onYesNoDialogClick(int yesOrNo);
	}
}
