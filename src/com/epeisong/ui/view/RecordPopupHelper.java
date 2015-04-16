package com.epeisong.ui.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.ui.view.RecordChatView.OnRecordChangeListener;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.MediaRecorderManager.OnRecordListener;

/**
 * 语音聊天View的帮助类，用于显示PopupWindow
 * @author poet
 *
 */
public class RecordPopupHelper implements OnRecordChangeListener, OnRecordListener {

	private PopupWindow mPopupWindowPrepare;
	private PopupWindow mPopupWindowRecord;
	private View mAnyView;

	private View mDecibelContainer;
	private ImageView mDecibelIv;
	private ImageView mCancelIv;
	private TextView mPromptTv;

	public RecordPopupHelper(Context context, View anyView) {
		mAnyView = anyView;

		int prepareW = (int) DimensionUtls.getPixelFromDp(130);
		View prepareView = LayoutInflater.from(context).inflate(
				R.layout.one_record_preparing, null);
		mPopupWindowPrepare = new PopupWindow(prepareView, prepareW, prepareW);

		int w = (int) DimensionUtls.getPixelFromDp(170);
		View view = LayoutInflater.from(context).inflate(
				R.layout.one_record_prompt, null);
		mPopupWindowRecord = new PopupWindow(view, w, w);

		mDecibelContainer = view.findViewById(R.id.ll_decibel);
		mDecibelIv = (ImageView) view.findViewById(R.id.iv_decibel);
		mCancelIv = (ImageView) view.findViewById(R.id.iv_cancel);
		mPromptTv = (TextView) view.findViewById(R.id.tv_prompt);
	}

	private void resetPrompt() {
		mDecibelContainer.setVisibility(View.VISIBLE);
		mCancelIv.setVisibility(View.GONE);
		mPromptTv.setText("手指上滑，取消发送");
		mPromptTv.setBackgroundResource(android.R.color.transparent);
	}
	
	@Override
	public void onRecordEvent(int event, Object... extra) {
	    // TODO Auto-generated method stub
	    
	}

	@Override
	public void onStateChange(int state) {
		switch (state) {
		case OnRecordChangeListener.STATE_PREPARE:
			mPopupWindowPrepare.showAtLocation(mAnyView, Gravity.CENTER, 0, 0);
			break;
		case OnRecordChangeListener.STATE_START:
			mPopupWindowPrepare.dismiss();
			mPopupWindowRecord.showAtLocation(mAnyView, Gravity.CENTER, 0, 0);
			break;
		case OnRecordChangeListener.STATE_NORMAL:
			mCancelIv.setVisibility(View.GONE);
			mDecibelContainer.setVisibility(View.VISIBLE);
			mPromptTv.setText("手指上滑，取消发送");
			mPromptTv.setBackgroundResource(android.R.color.transparent);
			break;
		case OnRecordChangeListener.STATE_CANCELABLE:
			mCancelIv.setVisibility(View.VISIBLE);
			mDecibelContainer.setVisibility(View.GONE);
			mPromptTv.setText("松开手指，取消发送");
			mPromptTv.setBackgroundResource(R.drawable.record_cancel_bg);
			break;
		case OnRecordChangeListener.STATE_STOP:
			mPopupWindowRecord.dismiss();
			resetPrompt();
			break;
		}
	}

	@Override
	public void onAudioChange(float audio) {
		int index = (int) (audio * 7) + 1;
		int id = mAnyView
				.getContext()
				.getResources()
				.getIdentifier("amp" + index, "drawable",
						mAnyView.getContext().getPackageName());
		mDecibelIv.setImageResource(id);
	}
}
