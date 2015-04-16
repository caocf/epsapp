package com.epeisong.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 纵向TextView
 * @author poet
 *
 */
public class VerticalTextView extends TextView {
	
	private TextPaint mTextPaint;
	private CharSequence mText;

	public VerticalTextView(Context context) {
		this(context, null);
	}

	public VerticalTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTextPaint = getPaint();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth() + 5);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		float x = getWidth()/2 - mTextPaint.getTextSize()/2;
		float y = mTextPaint.getTextSize();
		mText = getText();
		for(char c : mText.toString().toCharArray()) {
			canvas.drawText(String.valueOf(c), x, y, mTextPaint);
			y += mTextPaint.getTextSize();
		}
	}

}
