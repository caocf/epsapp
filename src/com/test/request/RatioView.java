package com.test.request;

import com.epeisong.utils.DimensionUtls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class RatioView extends View {

    int mWidth, mHeight;
    int mTextHeight;

    Paint mPaint;

    int mTotal;
    int mSuccess;
    int mFail;

    String duration;

    public RatioView(Context context) {
        super(context);
        init();
    }

    void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(18);
        Rect bounds = new Rect();
        mPaint.getTextBounds("测试", 0, 2, bounds);
        mTextHeight = bounds.height();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.GRAY);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
        mPaint.setColor(Color.BLUE);
        float successWidth = mWidth * mSuccess / mTotal;
        canvas.drawRect(0, 0, successWidth, mHeight, mPaint);
        mPaint.setColor(Color.RED);
        canvas.drawRect(successWidth, 0, successWidth + mWidth * mFail / mTotal, mHeight, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawText(mSuccess + "/" + mTotal, mWidth / 2 - DimensionUtls.getPixelFromDpInt(30), mHeight / 2
                + mTextHeight / 2, mPaint);
        if (duration != null) {
            canvas.drawText("耗时：" + duration, mWidth / 2 + DimensionUtls.getPixelFromDpInt(30), mHeight / 2
                    + mTextHeight / 2, mPaint);
            duration = null;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    public void setTotal(int total) {
        this.mTotal = total;
    }

    public void setSuccess(int success) {
        this.mSuccess = success;
        invalidate();
    }

    public void setFail(int fail) {
        this.mFail = fail;
        invalidate();
    }

    public void setProgress(boolean success) {
        if (success) {
            mSuccess++;
        } else {
            mFail++;
        }
        invalidate();
    }

    public void setDuration(String duration) {
        this.duration = duration;
        invalidate();
    }
}
