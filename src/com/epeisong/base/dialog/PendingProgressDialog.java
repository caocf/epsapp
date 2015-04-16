package com.epeisong.base.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.epeisong.R;

/**
 * 等待对话框：ProgressBar + 显示文本
 * @author poet
 *
 */
public class PendingProgressDialog extends ProgressDialog {

    public static final int DIALOG_STYLE_DEFAULT = 0;
    public static final int DIALOG_STYLE_RUNNING_CAR = 1;

    private TextView mMsgTv;
    private View mCarView;

    private int mDialogStyle;

    private AnimationDrawable mAnimationDrawable;

    public PendingProgressDialog(Context context) {
        super(context);
    }

    public PendingProgressDialog(Context context, int dialogStyle) {
        super(context);
        mDialogStyle = dialogStyle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (mDialogStyle) {
        case DIALOG_STYLE_RUNNING_CAR:
            setContentView(R.layout.dialog_pending_running_car);
            mCarView = findViewById(R.id.iv);
            Drawable d = mCarView.getBackground();
            if (d != null && d instanceof AnimationDrawable) {
                mAnimationDrawable = (AnimationDrawable) d;
                mAnimationDrawable.start();
            }
            break;
        case DIALOG_STYLE_DEFAULT:
        default:
            setContentView(R.layout.dialog_pending_default);
            mMsgTv = (TextView) findViewById(R.id.tv_pend_msg);
            break;
        }

        if (mCarView != null) {

        }
    }

    @Override
    public void show() {
        if (mAnimationDrawable != null) {
            mAnimationDrawable.start();
        }
        super.show();
    }

    @Override
    public void dismiss() {
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
        super.dismiss();
    }

    public void beforeDismiss() {

    }

    public void setPendingMsg(String msg) {
        if (mMsgTv != null) {
            mMsgTv.setText(msg);
        }
    }
}
