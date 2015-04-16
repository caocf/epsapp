package com.epeisong.base.view;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.utils.DimensionUtls;

/**
 * tabview基类
 * @author poet
 *
 */
public abstract class TabViews extends RelativeLayout implements
		OnClickListener {

	private OnTabPageChangeListener mOnTabPageChangeListener;
	private TabIndicatorAnimation mTabIndicatorAnimation;

	private LinearLayout mTabLayout;
	private View mIndicator;
	private int mTabCount;
	private int mCurTabIndex = -1;

	private ViewPager mViewPager;
	
	private int mDividerColor;

	public TabViews(Context context) {
		this(context, null);
	}

	public TabViews(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTabLayout = new LinearLayout(context);
		mTabLayout.setOrientation(LinearLayout.HORIZONTAL);
		this.addView(mTabLayout, new LayoutParams(-1, -1));
	}
	
	protected abstract void onTabChange(int tab);

	@Override
	public void onClick(View v) {
		Object tag = v.getTag();
		if (tag != null && tag instanceof Integer) {
			int pos = (Integer) tag;
			if(pos == mCurTabIndex) {
				return;
			}
			mViewPager.setCurrentItem(pos);
		}
	}
	

    public void setDividerColor(int color) {
        mDividerColor = color;
    }

	public void setTabViews(List<View> views) {
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -1, 1);
		for (int i = 0; i < views.size(); i++) {
			View view = views.get(i);
			view.setOnClickListener(this);
			view.setTag(i);
			mTabLayout.addView(view, p);
			if(mDividerColor != 0 && i < views.size() - 1) {
			    View v = new View(getContext());
			    v.setBackgroundColor(mDividerColor);
			    int w = DimensionUtls.getPixelFromDpInt(1);
			    mTabLayout.addView(v, w, -1);
			}
		}
		mTabCount = views.size();
		initIndicator();
	}

	private void initIndicator() {
		FrameLayout bottom = new FrameLayout(getContext());
		RelativeLayout.LayoutParams params = new LayoutParams(-1, -2);
		params.addRule(ALIGN_PARENT_BOTTOM);
		this.addView(bottom, params);

		FrameLayout.LayoutParams lineParams = new FrameLayout.LayoutParams(-1,
				1);
		lineParams.gravity = Gravity.BOTTOM;
		View line = new View(getContext());
		line.setLayoutParams(lineParams);
		line.setBackgroundResource(R.color.gray);
		bottom.addView(line);

		int width = EpsApplication.getScreenWidth() / mTabCount;
		int h = (int) DimensionUtls.getPixelFromDp(5);
		mIndicator = new View(getContext());
		mIndicator.setLayoutParams(new FrameLayout.LayoutParams(width, h));
		mIndicator.setBackgroundResource(R.color.blue);
		bottom.addView(mIndicator);
		mTabIndicatorAnimation = new TabIndicatorAnimation(mIndicator, width);
	}

	private void changeTab(int tab) {
		mTabIndicatorAnimation.onTabChanged(tab);
		onTabChange(tab);
		mCurTabIndex = tab;
	}

	public void setViewPager(ViewPager pager) {
		this.mViewPager = pager;
		mViewPager.setOnPageChangeListener(new MyPageChangeListener());
	}
	
	public void setTab(int pos) {
		mViewPager.setCurrentItem(pos);
	}
	
	public int getCurTabIndex() {
		return mCurTabIndex;
	}

	public void setIndicatorHeightInDp(int height) {
		FrameLayout.LayoutParams p = (android.widget.FrameLayout.LayoutParams) mIndicator
				.getLayoutParams();
		p.height = (int) DimensionUtls.getPixelFromDp(height);
		mIndicator.setLayoutParams(p);
	} 
	
	public void setIndicatorColorResId(int resId) {
		mIndicator.setBackgroundResource(resId);
	}

	private class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			changeTab(position);
			if(mOnTabPageChangeListener != null) {
				mOnTabPageChangeListener.onTabPageChange(position);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}
	
	public void setOnTabPageChangeListener(OnTabPageChangeListener listener) {
		this.mOnTabPageChangeListener = listener;
	}
	
	public interface OnTabPageChangeListener {
		void onTabPageChange(int pos);
	}
}
