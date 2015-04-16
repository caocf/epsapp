package com.epeisong.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.utils.DimensionUtls;

public class SwitchButton extends LinearLayout implements OnClickListener {

    private TextView mOnBtn;
    private TextView mOffBtn;
    private boolean mSwitchOn = true;

    private Rect mRect;
    private int mWidth;
    private int mHeight;

    private OnSwitchListener mListener;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setBackgroundResource(R.drawable.common_switch_button_bg_on);
        LayoutParams params = new LayoutParams(0, -1, 1);
        mOnBtn = new TextView(context);
        mOnBtn.setBackgroundColor(Color.TRANSPARENT);
        mOnBtn.setText(" 开 ");
        mOnBtn.setTextColor(Color.WHITE);
        mOnBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mOnBtn.setGravity(Gravity.CENTER);
        mOffBtn = new TextView(context);
        mOffBtn.setBackgroundColor(Color.TRANSPARENT);
        mOffBtn.setText(" 关 ");
        mOffBtn.setTextColor(Color.WHITE);
        mOffBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mOffBtn.setGravity(Gravity.CENTER);
        this.addView(mOnBtn, params);
        this.addView(mOffBtn, params);
        mOnBtn.setOnClickListener(this);
        mOffBtn.setOnClickListener(this);

        mRect = new Rect();
    }

    private void refreshSize() {
        String onText = mOnBtn.getText().toString();
        String offText = mOffBtn.getText().toString();
        mOnBtn.getPaint().getTextBounds(onText, 0, onText.length() - 1, mRect);
        int onTextWidth = mRect.width();
        mOffBtn.getPaint().getTextBounds(offText, 0, mOffBtn.length() - 1, mRect);
        int offTextWidth = mRect.width();
        int textHeight = mRect.height();
        mWidth = (int) (Math.max(onTextWidth, offTextWidth) * 2 + DimensionUtls.getPixelFromDp(15) * 4);
        mHeight = (int) (textHeight + DimensionUtls.getPixelFromDp(6) * 2);
        // LogUtils.d("SwitchButton", "w:" + mWidth + ",h:" + mHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidth == 0 || mHeight == 0) {
            refreshSize();
        }
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        if (mOnBtn == v) {
            changeSwitch(true);
        } else if (mOffBtn == v) {
            changeSwitch(false);
        }
    }

    private void changeSwitch(boolean on) {
        if (mSwitchOn != on) {
            if (mListener != null) {
                mListener.onSwitch(this, on);
            } else {
                if (on) {
                    this.setBackgroundResource(R.drawable.common_switch_button_bg_on);
                } else {
                    this.setBackgroundResource(R.drawable.common_switch_button_off);
                }
                mSwitchOn = on;
            }
        }
    }

    public boolean isOpen() {
        return mSwitchOn;
    }

    public boolean setSwitch(boolean on) {
        if (mSwitchOn != on) {
            if (on) {
                this.setBackgroundResource(R.drawable.common_switch_button_bg_on);
            } else {
                this.setBackgroundResource(R.drawable.common_switch_button_off);
            }
            mSwitchOn = on;
            return true;
        }
        return false;
    }

    public void setSwitchText(String onText, String offTxt, boolean defOn) {
        if (onText.length() == 1 && offTxt.length() == 1) {
            onText = " " + onText + " ";
            offTxt = " " + offTxt + " ";
        }
        mOnBtn.setText(onText);
        mOffBtn.setText(offTxt);
        refreshSize();
        setSwitch(defOn);
    }

    public void setSwitchTextSize(int sp) {
        if (sp > 0) {
            mOnBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
            mOffBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        }
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        mListener = listener;
    }

    public interface OnSwitchListener {
        void onSwitch(SwitchButton btn, boolean on);
    }
}
