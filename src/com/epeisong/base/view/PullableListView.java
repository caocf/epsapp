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
 * 可拉的ListvView
 * 	上拉未实现
 * @author poet
 * 
 */
@Deprecated
public abstract class PullableListView extends ListView {

	protected Scroller mScroller;
	protected boolean mIsVerticalScrollBarEnabled;
	protected boolean mIsRunning;
	protected boolean mIsTouching;

	protected View mTopView;
	protected int mTopViewPaddingTop;
	protected int mTopViewPaddingTopMax;
	protected int mTopViewPaddingTopMin;

	protected boolean mCanHandlePulldown;
	protected boolean mIsHandledPulldown;
	protected boolean mCanRunTop = true;

	protected View mBottomView;
	protected int mBottomViewPaddingBottom;
	protected int mBottomViewPaddingBottomMax;
	protected int mBottomViewPaddingBottomMin;

	protected boolean mCanHandlePullup;
	protected boolean mIsHandledPullup;
	protected boolean mCanRunBottom = true;

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

	protected Runnable mBottomRunnable = new Runnable() {
		@Override
		public void run() {
			if (mScroller.computeScrollOffset()) {
				setBottomViewPaddingBottom(mScroller.getCurrX());
				post(this);
			}
		}
	};

	public PullableListView(Context context) {
		this(context, null);
	}

	public PullableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(getContext());
		initTopView();
		initBottomView();
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

	private void initBottomView() {
		mBottomView = onCreateBottomView();
		addFooterView(mBottomView);
		sMeasureView(mBottomView);
		int nBottomViewH = mBottomView.getMeasuredHeight();
		mBottomViewPaddingBottom = mBottomView.getPaddingBottom();
		mBottomViewPaddingBottomMax = mBottomViewPaddingBottom
				+ getPullupBeyondHeight();
		mBottomViewPaddingBottomMin = mBottomViewPaddingBottom - nBottomViewH;
		setBottomViewPaddingBottom(mBottomViewPaddingBottomMin);
		setFooterDividersEnabled(false);
	}

	protected abstract View onCreateTopView();

	protected abstract View onCreateBottomView();

	protected abstract int getPulldownBeyondHeight();

	protected abstract int getPullupBeyondHeight();

	protected abstract void onStartRunTop();

	protected abstract void onStartRunBottom();

	protected abstract void onEndRunTop();

	protected abstract void onEndRunBottom();

	protected void onTopViewPaddingTopChange(int newPadding, int oldPadding) {
	}

	protected void onBottomViewPaddingBottomChange(int newPadding,
			int oldPadding) {
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!mScroller.isFinished()) {
			return super.onInterceptTouchEvent(ev);
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!mIsRunning) {
				if (getFirstVisiblePosition() == 0 && !mCanHandlePulldown) {
					mCanHandlePulldown = true;
					mLastTouchY = ev.getY();
				} else if (getLastVisiblePosition() == getCount()
						- getHeaderViewsCount()
						&& !mCanHandlePullup) {
					mCanHandlePullup = true;
					mLastTouchY = ev.getY();
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_OUTSIDE:
			mIsTouching = false;
			mCanHandlePulldown = false;
			mCanHandlePullup = false;
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
			} else if (mIsHandledPullup) {
				mIsHandledPullup = false;
				setVerticalScrollBarEnabled(mIsVerticalScrollBarEnabled);
				if (ev.getAction() == MotionEvent.ACTION_UP) {
					if (mCanRunBottom) {
						if (mBottomView.getPaddingBottom() < mBottomViewPaddingBottomMax) {
							hideBottomView();
						} else {
							startRunBottom();
						}
					} else {
						hideBottomView();
					}
				} else {
					setBottomViewPaddingBottom(mBottomViewPaddingBottomMin);
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
			} else if (mCanHandlePullup && !mIsHandledPullup) {
				float nTouchDistance = (nTouchY - mLastTouchY) / 2.0f;
				mLastTouchY = nTouchY;
				if (nTouchDistance < 0) {
					mTouchYDistance -= nTouchDistance;
					if (mTouchYDistance > 0) {
						mIsVerticalScrollBarEnabled = isVerticalScrollBarEnabled();
						setVerticalScrollBarEnabled(false);
						ViewParent parent = getParent();
						if (parent != null) {
							parent.requestDisallowInterceptTouchEvent(true);
						}
						ev.setAction(MotionEvent.ACTION_CANCEL);
						super.onTouchEvent(ev);
						mIsHandledPullup = true;
						return true;
					} else {
						return true;
					}
				} else if (nTouchDistance < 0) {
					mTouchYDistance = 0;
					mCanHandlePullup = false;
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
			} else if (mIsHandledPullup) {
				float nTouchDistance = (nTouchY - mLastTouchY) / 2.0f;
				mLastTouchY = nTouchY;

				int nBottomViewPaddingBottomLast = mBottomView
						.getPaddingBottom();
				int nBottomViewPaddingBottomNew = (int) (nBottomViewPaddingBottomLast - nTouchDistance);
				if (nTouchDistance < 0) {
					if (nBottomViewPaddingBottomLast < mTopViewPaddingTopMax) {
						setBottomViewPaddingBottom(nBottomViewPaddingBottomNew);
					}
				} else if (nTouchDistance > 0) {
					if (nBottomViewPaddingBottomLast > mTopViewPaddingTopMin) {
						setBottomViewPaddingBottom(nBottomViewPaddingBottomNew);
					}
				}
				return true;
			}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_OUTSIDE:
			mIsTouching = false;
			mCanHandlePulldown = false;
			mCanHandlePullup = false;
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
			} else if (mIsHandledPullup) {
				mIsHandledPullup = false;
				setVerticalScrollBarEnabled(mIsVerticalScrollBarEnabled);
				if (ev.getAction() == MotionEvent.ACTION_UP) {
					if (mCanRunBottom) {
						if (mBottomView.getPaddingBottom() < mBottomViewPaddingBottomMax) {
							hideBottomView();
						} else {
							startRunBottom();
						}
					} else {
						hideBottomView();
					}
				} else {
					setBottomViewPaddingBottom(mBottomViewPaddingBottomMin);
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

	protected void hideBottomView() {
		startChangeBottomViewPaddingBottom(mBottomViewPaddingBottomMin);
	}

	protected void startChangeTopViewPaddingTop(int paddingTopDest) {
		int nPaddingTopCur = mTopView.getPaddingTop();
		int duration = 250;
		mScroller.startScroll(nPaddingTopCur, 0, paddingTopDest
				- nPaddingTopCur, 0, duration);
		post(mTopRunnable);
	}

	protected void startChangeBottomViewPaddingBottom(int paddingBottomDest) {
		int nPaddingBottomCur = mBottomView.getPaddingBottom();
		int duration = 250;
		mScroller.startScroll(nPaddingBottomCur, 0, paddingBottomDest
				- nPaddingBottomCur, 0, duration);
		post(mBottomRunnable);
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
			// TODO
			if (!mIsTouching) {
				if (mTopView.getPaddingTop() == mTopViewPaddingTop) {
					mIsRunning = true;
					onStartRunTop();
					if (mOnPullListener != null) {
						mOnPullListener.onStartRunTop(this);
					}
				}
			}
			onTopViewPaddingTopChange(padding, nPaddingTopCur);
		}
	}

	protected void setBottomViewPaddingBottom(int padding) {
		if (padding > mBottomViewPaddingBottomMax) {
			padding = mBottomViewPaddingBottomMax;
		} else if (padding < mBottomViewPaddingBottomMin) {
			padding = mBottomViewPaddingBottomMin;
		}
		int nPaddingBottomCur = mBottomView.getPaddingBottom();
		if (padding != nPaddingBottomCur) {
			mBottomView.setPadding(mBottomView.getPaddingLeft(),
					mBottomView.getPaddingTop(), mBottomView.getPaddingRight(),
					padding);
			// TODO
			if (!mIsTouching) {
				if (mBottomView.getPaddingBottom() == mBottomViewPaddingBottom) {
					mIsRunning = true;
					onStartRunBottom();
					if (mOnPullListener != null) {
						mOnPullListener.onStartRunBottom(this);
					}
				}
			}
			onBottomViewPaddingBottomChange(padding, nPaddingBottomCur);
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
			onEndRunTop();
		}
	}

	public void startRunBottom() {
		if (!mIsRunning) {
			startChangeBottomViewPaddingBottom(mBottomViewPaddingBottom);
		}
	}

	public void endRunBottom() {
		if (mIsRunning) {
			mIsRunning = false;
			hideBottomView();
			onEndRunBottom();
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

	public void setCanRunBottom(boolean canRun) {
		mCanRunBottom = canRun;
		if (mCanRunBottom) {
			mBottomView.setVisibility(View.VISIBLE);
		} else {
			mBottomView.setVisibility(View.GONE);
		}
	}

	public void setOnPullListener(OnPullListener listener) {
		mOnPullListener = listener;
	}

	public interface OnPullListener {
		void onStartRunTop(PullableListView view);

		void onStartRunBottom(PullableListView view);
	}
}
