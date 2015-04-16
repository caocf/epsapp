package com.epeisong.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 自适应高度的GridView
 * 
 * @author poet
 * 
 */
public class AdjustHeightGridView extends GridView {

	private int mMinHeight;

	private OnBlankTouchListener mOnBlankTouchListener;

	public AdjustHeightGridView(Context context) {
		super(context);
	}

	public AdjustHeightGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			return false;
		}

		if (mOnBlankTouchListener != null) {
			if (ev.getAction() == MotionEvent.ACTION_UP) {
				int item = pointToPosition((int) ev.getX(), (int) ev.getY());
				if (item == INVALID_POSITION) {
					return mOnBlankTouchListener.onBlackTouch(this);
				}
			}
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if ((getAdapter() == null || getAdapter().getCount() == 0)) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		}
	}

	public void setUseAdjustHeight(int minHeight) {
		mMinHeight = minHeight;
	}

	public void setOnBlankTouchListener(OnBlankTouchListener listener) {
		this.mOnBlankTouchListener = listener;
	}

	public interface OnBlankTouchListener {
		boolean onBlackTouch(GridView gridView);
	}
}
