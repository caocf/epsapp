package com.epeisong.base.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * ListView快速索引
 * 
 * @author poet
 * 
 */
public class IndexBar extends View {

    private Paint mPaint;

    private List<String> mIndexValues;
    private float mTextSize;
    private int mBgColor = 0x55000000;
    private int mTextColor = Color.BLACK;

    private boolean mTouching;
    private int mChoosedIndex = -1;

    private OnChooseIndexListener mOnChooseIndexListener;

    public IndexBar(Context context) {
        this(context, null);
    }

    public IndexBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIndexValues == null || mIndexValues.isEmpty()) {
            return;
        }
        if (mTouching) {
            canvas.drawColor(mBgColor);
        }
        int itemHeight = getMeasuredHeight() / mIndexValues.size();
        for (int i = 0; i < mIndexValues.size(); i++) {
            float posX = getMeasuredWidth() / 2 - mPaint.measureText(mIndexValues.get(i)) / 2;
            float posY = (i + 1) * itemHeight;
            canvas.drawText(mIndexValues.get(i), posX, posY, mPaint);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final float y = event.getY();
        if (y < 0) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        // 算出点击的字母的索引
        final int index = (int) (y / getHeight() * mIndexValues.size());
        // 保存上次点击的字母的索引到oldChoose
        final int oldChoose = mChoosedIndex;
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mTouching = true;
            if (oldChoose != index && mOnChooseIndexListener != null && index >= 0 && index < mIndexValues.size()) {
                mChoosedIndex = index;
                mOnChooseIndexListener.onChoosedIndex(mIndexValues.get(index));
                invalidate();
            }
            break;

        case MotionEvent.ACTION_MOVE:
            if (!mTouching) {
                return true;
            }
            if (oldChoose != index && mOnChooseIndexListener != null && index >= 0 && index < mIndexValues.size()) {
                mChoosedIndex = index;
                mOnChooseIndexListener.onChoosedIndex(mIndexValues.get(index));
                // invalidate();
            }
            break;
        case MotionEvent.ACTION_UP:
        default:
            mTouching = false;
            mChoosedIndex = -1;
            invalidate();
            if(mOnChooseIndexListener != null) {
                mOnChooseIndexListener.onChoosedIndex(null);
            }
            break;
        }
        return true;
    }

    public void setIndexValues(List<String> values) {
        mIndexValues = values;
    }

    public void setOnChooseIndexListener(OnChooseIndexListener listener) {
        mOnChooseIndexListener = listener;
    }

    public interface OnChooseIndexListener {
        void onChoosedIndex(String indexValue);
    }

    public static List<String> getA_Z() {
        List<String> list = new ArrayList<String>();
        char c = 'A';
        while (c <= 'Z') {
            list.add(String.valueOf(c));
            c += 1;
        }
        return list;
    }
}
