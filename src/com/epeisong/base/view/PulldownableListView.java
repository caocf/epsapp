package com.epeisong.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * 可下拉的ListView
 * 
 * @author poet
 * 
 */
public abstract class PulldownableListView extends ListView {

	protected View mTopView;
	protected int mTopViewPaddingTop;
	protected int mTopViewPaddingTopMax;
	protected int mTopViewPaddingTopMin;

	protected Scroller mScroller;
	protected boolean mIsVerticalScrollBarEnabled;
	protected boolean mIsRunning;
	protected boolean mIsTouching;
	protected boolean mCanHandlePulldown;
	protected boolean mIsHandledPulldown;
	protected boolean mCanRunTop = true;

	protected float mLastTouchY;
	protected float mTouchYDistance;

	protected OnPullListener mOnPullListener;

	protected Runnable mTopRunnable = new Runnable() {
		@Override
		public void run() {
			if (mScroller.computeScrollOffset()) {
				setTopViewPaddingTop(mScroller.getCurrX());
				post(this);
			}
		}
	};

	public PulldownableListView(Context context) {
		this(context, null);
	}

	public PulldownableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTopView();
		mScroller = new Scroller(getContext());
	}

	private void initTopView() {
		mTopView = onCreateTopView();
		addHeaderView(mTopView);
		sMeasureView(mTopView);
		int nTopViewHeight = mTopView.getMeasuredHeight();
		mTopViewPaddingTop = mTopView.getPaddingTop();
		mTopViewPaddingTopMax = mTopViewPaddingTop + getPulldownBeyondHeight();
		mTopViewPaddingTopMin = mTopViewPaddingTop - nTopViewHeight;
		setTopViewPaddingTop(mTopViewPaddingTopMin);
		setHeaderDividersEnabled(false);
	}

	protected abstract View onCreateTopView();

	protected abstract int getPulldownBeyondHeight();

	protected abstract void onStartRun();

	protected abstract void onEndRun();

	protected void onTopViewPaddingTopChange(int newPadding, int oldPadding) {
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!mScroller.isFinished()) {
			return super.onInterceptTouchEvent(ev);
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN://
			if (!mIsRunning && getFirstVisiblePosition() == 0
					&& !mCanHandlePulldown) {
				mCanHandlePulldown = true;
				mLastTouchY = ev.getY();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_OUTSIDE:
			mIsTouching = false;
			mCanHandlePulldown = false;
			mTouchYDistance = 0;
			if (mIsHandledPulldown) {
				mIsHandledPulldown = false;
				setVerticalScrollBarEnabled(mIsVerticalScrollBarEnabled);
				if (ev.getAction() == MotionEvent.ACTION_UP) {
					if (mCanRunTop) {
						// if (mTopView.getPaddingTop() <= mTopViewPaddingTop) {
						if (mTopView.getPaddingTop() < mTopViewPaddingTopMax) {
							hideTopView();
						} else {
							startRunTop();
						}
					} else {
						hideTopView();
					}
				} else {
					setTopViewPaddingTop(mTopViewPaddingTopMin);
				}
				return true;
			}
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!mScroller.isFinished()) {
			return super.onTouchEvent(ev);
		}
		final float nTouchY = ev.getY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			mIsTouching = true;
			if (mCanHandlePulldown && !mIsHandledPulldown) {
				float nTouchDistance = (nTouchY - mLastTouchY) / 2.0f;
				mLastTouchY = nTouchY;
				if (nTouchDistance > 0) {
					mTouchYDistance += nTouchDistance;
					if (mTouchYDistance > 0) {
						mIsVerticalScrollBarEnabled = isVerticalScrollBarEnabled();
						setVerticalScrollBarEnabled(false);
						ViewParent parent = getParent();
						if (parent != null) {
							parent.requestDisallowInterceptTouchEvent(true);
						}
						ev.setAction(MotionEvent.ACTION_CANCEL);
						super.onTouchEvent(ev);
						mIsHandledPulldown = true;
						return true;
					} else {
						return true;
					}
				} else if (nTouchDistance < 0) {
					mTouchYDistance = 0;
					mCanHandlePulldown = false;
				}
			}
			if (mIsHandledPulldown) {
				float nTouchDistance = (nTouchY - mLastTouchY) / 2.0f;
				mLastTouchY = nTouchY;

				int nTopViewPaddingTopLast = mTopView.getPaddingTop();
				int nTopViewPaddingTopNew = (int) (nTopViewPaddingTopLast + nTouchDistance);
				if (nTouchDistance > 0) {
					if (nTopViewPaddingTopLast < mTopViewPaddingTopMax) {
						setTopViewPaddingTop(nTopViewPaddingTopNew);
					}
				} else if (nTouchDistance < 0) {
					if (nTopViewPaddingTopLast > mTopViewPaddingTopMin) {
						setTopViewPaddingTop(nTopViewPaddingTopNew);
					}
				}
				return true;
			}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_OUTSIDE:
			mIsTouching = false;
			mCanHandlePulldown = false;
			mTouchYDistance = 0;
			if (mIsHandledPulldown) {
				mIsHandledPulldown = false;
				setVerticalScrollBarEnabled(mIsVerticalScrollBarEnabled);
				if (ev.getAction() == MotionEvent.ACTION_UP) {
					if (mCanRunTop) {
						if (mTopView.getPaddingTop() < mTopViewPaddingTopMax) {
							hideTopView();
						} else {
							startRunTop();
						}
					} else {
						hideTopView();
					}
				} else {
					setTopViewPaddingTop(mTopViewPaddingTopMin);
				}
				return true;
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	protected void hideTopView() {
		startChangeTopViewPaddingTop(mTopViewPaddingTopMin);
	}

	protected void startChangeTopViewPaddingTop(int paddingTopDest) {
		int nPaddingTopCur = mTopView.getPaddingTop();
		int duration = 250;
		mScroller.startScroll(nPaddingTopCur, 0, paddingTopDest
				- nPaddingTopCur, 0, duration);
		post(mTopRunnable);
	}

	protected void setTopViewPaddingTop(int padding) {
		if (padding > mTopViewPaddingTopMax) {
			padding = mTopViewPaddingTopMax;
		} else if (padding < mTopViewPaddingTopMin) {
			padding = mTopViewPaddingTopMin;
		}
		int nPaddingTopCur = mTopView.getPaddingTop();
		if (padding != nPaddingTopCur) {
			mTopView.setPadding(mTopView.getPaddingLeft(), padding,
					mTopView.getPaddingRight(), mTopView.getPaddingBottom());
			if (!mIsTouching) {
				if (mTopView.getPaddingTop() == mTopViewPaddingTop) {
					mIsRunning = true;
					onStartRun();
					if (mOnPullListener != null) {
						mOnPullListener.onStartRunTop(this);
					}
				}
			}
			onTopViewPaddingTopChange(padding, nPaddingTopCur);
		}
	}

	public static void sMeasureView(View v) {
		ViewGroup.LayoutParams p = v.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int nWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int nHeightSpec;
		int nHeight = p.height;
		if (nHeight > 0) {
			nHeightSpec = MeasureSpec.makeMeasureSpec(nHeight,
					MeasureSpec.EXACTLY);
		} else {
			nHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		v.measure(nWidthSpec, nHeightSpec);
	}

	public void startRunTop() {
		if (!mIsRunning) {
			startChangeTopViewPaddingTop(mTopViewPaddingTop);
		}
	}

	public void endRunTop() {
		if (mIsRunning) {
			mIsRunning = false;
			hideTopView();
			onEndRun();
		}
	}

	public void setCanRunTop(boolean canRun) {
		mCanRunTop = canRun;
		if (mCanRunTop) {
			mTopView.setVisibility(View.VISIBLE);
		} else {
			mTopView.setVisibility(View.GONE);
		}
	}

	public void setOnPullListener(OnPullListener listener) {
		mOnPullListener = listener;
	}

	public interface OnPullListener {
		void onStartRunTop(PulldownableListView view);
	}
}
