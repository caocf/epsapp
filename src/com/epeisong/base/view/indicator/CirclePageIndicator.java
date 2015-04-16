package com.epeisong.base.view.indicator;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

import com.epeisong.base.adapter.CyclePagerAdapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 
 * @author poet
 */
public class CirclePageIndicator extends View implements PageIndicator {

	private ViewPager.OnPageChangeListener mListener;
	private ViewPager mViewPager;
	private int mScrollState;
	private int mPageCount;
	private int mCurrentPage;
	private int mSnapPage;
	private float mPageOffset;

	private final Paint mPaintPage = new Paint(ANTI_ALIAS_FLAG);// 普通
	private final Paint mPaintStroke = new Paint(ANTI_ALIAS_FLAG); // 描边
	private final Paint mPaintAlived = new Paint(ANTI_ALIAS_FLAG); // 选中上层

	private int mOrientation = LinearLayout.HORIZONTAL;
	private float mRadius;
	private boolean mCentered = true;
	private boolean mSnap; // true 猛拽，不使用平滑效果

	public CirclePageIndicator(Context context) {
		this(context, null);
	}

	public CirclePageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode())
			return;
		mPaintPage.setStyle(Style.FILL);
		mPaintStroke.setStyle(Style.STROKE);
		mPaintAlived.setStyle(Style.FILL);

		// 默认值
		mPaintPage.setColor(Color.GRAY);
		mPaintStroke.setColor(Color.WHITE);
		mPaintAlived.setColor(Color.BLACK);
		mRadius = 5;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mOrientation == HORIZONTAL) {
			setMeasuredDimension(measureLong(widthMeasureSpec),
					measureShort(heightMeasureSpec));
		} else {
			setMeasuredDimension(measureShort(widthMeasureSpec),
					measureLong(heightMeasureSpec));
		}
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureLong(int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)) {
			// We were told how big to be
			result = specSize;
		} else {
			// Calculate the width according the views count
			result = (int) (getPaddingLeft() + getPaddingRight()
					+ (mPageCount * 2 * mRadius) + (mPageCount - 1) * mRadius + 1);
			// Respect AT_MOST value if that was what is called for by
			// measureSpec
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureShort(int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the height
			result = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
			// Respect AT_MOST value if that was what is called for by
			// measureSpec
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mViewPager == null || mPageCount == 0) {
			return;
		}
		if (mCurrentPage >= mPageCount) {
			mCurrentPage = mCurrentPage % mPageCount;
			setCurrentItem(mCurrentPage);
			return;
		}

		int longSize;
		int longPaddingBefore;
		int longPaddingAfter;
		int shortPaddingBefore;
		if (mOrientation == HORIZONTAL) {
			longSize = getWidth();
			longPaddingBefore = getPaddingLeft();
			longPaddingAfter = getPaddingRight();
			shortPaddingBefore = getPaddingTop();
		} else {
			longSize = getHeight();
			longPaddingBefore = getPaddingTop();
			longPaddingAfter = getPaddingBottom();
			shortPaddingBefore = getPaddingLeft();
		}

		final float threeRadius = mRadius * 3;
		float shortOffset = shortPaddingBefore + mRadius;
		float longOffset = longPaddingBefore + mRadius;
		if (mCentered) {
			longOffset += ((longSize - longPaddingBefore - longPaddingAfter) / 2.0f)
					- ((mPageCount * threeRadius) / 2.0f);
		}

		float dX;
		float dY;

		float pageRadius = mRadius;
		if (mPaintStroke.getStrokeWidth() > 0) {
			pageRadius -= mPaintStroke.getStrokeWidth() / 2.0f;
		}

		// Draw stroked circles
		for (int iLoop = 0; iLoop < mPageCount; iLoop++) {
			float drawLong = longOffset + (iLoop * threeRadius);
			if (mOrientation == HORIZONTAL) {
				dX = drawLong;
				dY = shortOffset;
			} else {
				dX = shortOffset;
				dY = drawLong;
			}
			// Only paint fill if not completely transparent
			if (mPaintPage.getAlpha() > 0) {
				canvas.drawCircle(dX, dY, pageRadius, mPaintPage);
			}

			// Only paint stroke if a stroke width was non-zero
			if (pageRadius != mRadius) {
				canvas.drawCircle(dX, dY, mRadius, mPaintStroke);
			}
		}

		// Draw the filled circle according to the current scroll
		float cx = (mSnap ? mSnapPage : mCurrentPage) * threeRadius;
		if (!mSnap) {
			cx += mPageOffset * threeRadius;
		}
		if (mOrientation == HORIZONTAL) {
			dX = longOffset + cx;
			dY = shortOffset;
		} else {
			dX = shortOffset;
			dY = longOffset + cx;
		}
		canvas.drawCircle(dX, dY, mRadius, mPaintAlived);
	}

	@Override
	public void setViewPager(ViewPager view) {
		if (mViewPager == view) {
			return;
		}
		if (mViewPager != null) {
			mViewPager.setOnPageChangeListener(null);
		}
		if (view.getAdapter() == null) {
			throw new IllegalStateException(
					"ViewPager does not have adapter instance.");
		}
		mViewPager = view;
		mViewPager.setOnPageChangeListener(this);
		if (mViewPager.getAdapter() instanceof CyclePagerAdapter) {
			mPageCount = ((CyclePagerAdapter) mViewPager.getAdapter())
					.getRealCount();
		} else {
			mPageCount = mViewPager.getAdapter().getCount();
		}
		invalidate();
	}

	@Override
	public void setViewPager(ViewPager view, int initialPosition) {
		setViewPager(view);
		setCurrentItem(initialPosition);
	}

	@Override
	public void setCurrentItem(int item) {
		if (mViewPager == null) {
			throw new IllegalStateException("ViewPager has not been bound.");
		}
		if (item >= mCurrentPage) {
			item = item % mCurrentPage;
		}
		mViewPager.setCurrentItem(item);
		mCurrentPage = item;
		invalidate();
	}

	@Override
	public void notifyDataSetChanged() {
		if (mViewPager.getAdapter() instanceof CyclePagerAdapter) {
			mPageCount = ((CyclePagerAdapter) mViewPager.getAdapter())
					.getRealCount();
		} else {
			mPageCount = mViewPager.getAdapter().getCount();
		}
//		invalidate();
		requestLayout();
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		mScrollState = state;
		if (mListener != null) {
			mListener.onPageScrollStateChanged(state);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (position >= mPageCount) {
			position = position % mPageCount;
		}
		mCurrentPage = position;
		mPageOffset = positionOffset;
		invalidate();

		if (mListener != null) {
			mListener.onPageScrolled(position, positionOffset,
					positionOffsetPixels);
		}
	}

	@Override
	public void onPageSelected(int position) {
		if (position >= mPageCount) {
			position = position % mPageCount;
		}
		if (mSnap || mScrollState == ViewPager.SCROLL_STATE_IDLE) {
			mCurrentPage = position;
			mSnapPage = position;
			invalidate();
		}

		if (mListener != null) {
			mListener.onPageSelected(position);
		}
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		mCurrentPage = savedState.currentPage;
		mSnapPage = savedState.currentPage;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPage = mCurrentPage;
		return savedState;
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mListener = listener;
	}

	public void setRadius(float radius) {
		mRadius = radius;
		invalidate();
	}

	public void setPageColor(int pageColor) {
		mPaintPage.setColor(pageColor);
		invalidate();
	}

	public void setStrokeColor(int strokeColor) {
		mPaintStroke.setColor(strokeColor);
		invalidate();
	}

	public void setStrokeWidth(float strokeWidth) {
		mPaintStroke.setStrokeWidth(strokeWidth);
		invalidate();
	}

	public void setPageAlivedColor(int fillColor) {
		mPaintAlived.setColor(fillColor);
		invalidate();
	}

	public void setCentered(boolean centered) {
		mCentered = centered;
		invalidate();
	}

	public void setSnap(boolean snap) {
		mSnap = snap;
		invalidate();
	}

	public void setOrientation(int orientation) {
		switch (orientation) {
		case HORIZONTAL:
		case VERTICAL:
			mOrientation = orientation;
			requestLayout();
			break;

		default:
			throw new IllegalArgumentException(
					"Orientation must be either HORIZONTAL or VERTICAL.");
		}
	}

	static class SavedState extends BaseSavedState {
		int currentPage;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPage = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPage);
		}

		// @SuppressWarnings("UnusedDeclaration")
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
