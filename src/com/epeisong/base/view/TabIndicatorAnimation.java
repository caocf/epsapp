package com.epeisong.base.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * TAB指示器动画工具
 * 
 * @author poet
 * 
 */
public class TabIndicatorAnimation {

	private View mViewIndicator;
	private int mTabWidth;
	private int mCurrentTab;

	public TabIndicatorAnimation(View viewIndicator, int tabWidth) {
		mViewIndicator = viewIndicator;
		mTabWidth = tabWidth;
	}

	public void onTabChanged(int tab) {
//		if (android.os.Build.VERSION.SDK_INT >= 11) {
//			ObjectAnimator
//					.ofFloat(mViewIndicator, "translationX",
//							mCurrentTab * mTabWidth, tab * mTabWidth)
//					.setDuration(200).start();
//		}
		Animation ani = new TranslateAnimation(mCurrentTab * mTabWidth,
				tab * mTabWidth, 0, 0);
		ani.setFillAfter(true);
		ani.setDuration(100);
		mViewIndicator.startAnimation(ani);
		mCurrentTab = tab;
	}
}
