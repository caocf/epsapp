package com.epeisong.base.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.epeisong.R;

/**
 * 显示提醒消息的对话框：标题 + 内容 + 确定按钮
 * @author poet
 *
 */
public class MessageDialog extends ProgressDialog implements
		android.view.View.OnClickListener {

	private TextView mTitleTv;
	private TextView mMessageTv;

	public MessageDialog(Activity activity) {
		super(activity);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_message);
		mTitleTv = (TextView) findViewById(R.id.tv_title);
		mMessageTv = (TextView) findViewById(R.id.tv_message);
		findViewById(R.id.btn_ok).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_ok) {
			dismiss();
		}
	}

	public void setTitle(String title) {
		mTitleTv.setText(title);
	}

	public void setMessage(String message) {
		mMessageTv.setText(message);
	}
}
