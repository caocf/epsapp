package com.epeisong.base.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.utils.DimensionUtls;

/**
 * 另一种风格的TabPagesActivity
 * 
 * @author poet
 * 
 */
public abstract class TabPages2Activity extends BaseActivity {

    private View mPendingView;
    private LinearLayout mTabContainer;
    private List<TextView> mTextViews;
    private List<View> mTextViewContainers;
    private TabStyle mTabStyle;
    protected LinearLayout mTabRoot;
    protected ViewPager mViewPager;
    protected PagerAdapter mAdapter;
    protected int mCurPos;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isUsePending()) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText("加载中...");
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            int top = DimensionUtls.getPixelFromDpInt(10);
            tv.setPadding(0, top, 0, 0);
            setContentView(tv);
            mPendingView = tv;
        } else {
            initPages(0);
        }
    }

    private void initPages(int pos) {
        setContentView(R.layout.one_tab_text_viewpager_2);
        mTabRoot = (LinearLayout) findViewById(R.id.ll_tab_root);
        mTabContainer = (LinearLayout) findViewById(R.id.ll_tab_container);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mTabStyle = getTabStyle();
        if (mTabStyle == null) {
            mTabStyle = new TabStyle();
        }
        mTabContainer.setBackgroundResource(mTabStyle.getTabBg());

        List<String> titles = new ArrayList<String>();
        onSetTabTitle(titles);
        mTextViews = new ArrayList<TextView>();
        mTextViewContainers = new ArrayList<View>();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1, 1);
        int index = 0;
        for (String title : titles) {
            TextView tv = new TextView(this);
            tv.setText(title);
            tv.setTextColor(getResources().getColorStateList(mTabStyle.getTextColorSelectorId()));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTabStyle.getTextSizeSp());
            LinearLayout ll = new LinearLayout(this);
            ll.setGravity(Gravity.CENTER);
            ll.setLayoutParams(params);
            ll.setOnClickListener(mOnClickListener);
            ll.setTag(index++);
            ll.addView(tv);
            mTabContainer.addView(ll);
            mTextViews.add(tv);
            mTextViewContainers.add(ll);
            if (index < titles.size()) {
                View v = new View(this);
                int w = (int) DimensionUtls.getPixelFromDp(1);
                v.setLayoutParams(new LinearLayout.LayoutParams(w, -1));
                v.setBackgroundColor(mTabStyle.getDividerColor());
                mTabContainer.addView(v);
            }
        }

        mViewPager.setAdapter(mAdapter = onGetAdapter());
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        onPageChange(pos);
    }

    protected boolean isUsePending() {
        return false;
    }

    protected final void endPending(int pos) {
        if (mPendingView != null) {
            mPendingView.setVisibility(View.GONE);
        }
        initPages(pos);
        mViewPager.setCurrentItem(pos);
    }

    protected TabStyle getTabStyle() {
        return new TabStyle();
    }

    protected abstract void onSetTabTitle(List<String> titles);

    protected abstract PagerAdapter onGetAdapter();

    protected void setTabTitle(int pos, String title) {
        if (pos < 0 || pos > mTextViews.size() - 1) {
            return;
        }
        mTextViews.get(pos).setText(title);
    }

    protected TextView getTabTextView(int pos) {
        return mTextViews.get(pos);
    }
    protected void changeUiPoint() {
    	
    }

    private void onPageChange(int index) {
        mCurPos = index;
        for (int i = 0; i < mTextViews.size(); i++) {
            if (i == index) {
                mTextViews.get(i).setSelected(true);
                int id = 0;
                if (mTabStyle.getTabItemBgs().length > 0) {
                    if (i == 0) {
                        id = mTabStyle.getTabItemBgs()[0];
                    } else if (i == mTextViews.size() - 1) {
                        id = mTabStyle.getTabItemBgs()[mTabStyle.getTabItemBgs().length - 1];
                    } else if (mTabStyle.getTabItemBgs().length > 2) {
                        id = mTabStyle.getTabItemBgs()[1];
                    }
                }
                if (id > 0) {
                    mTextViewContainers.get(i).setBackgroundResource(id);
                }
                mTextViews.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, mTabStyle.getTextSizeSpSelected());
            } else {
                mTextViews.get(i).setSelected(false);
                mTextViewContainers.get(i).setBackgroundColor(Color.TRANSPARENT);
                mTextViews.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, mTabStyle.getTextSizeSp());
            }
        }
        changeUiPoint() ;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof Integer) {
                int index = (Integer) tag;
                if (mCurPos != index) {
                    mViewPager.setCurrentItem(index);
                }
                
            }
        }
    };

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            onPageChange(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    protected static class TabStyle {
        private int tab_bg = -1;

        public int getTabBg() {
            if (this.tab_bg == -1)
                return R.drawable.common_tab2_bg;
            else
                return this.tab_bg;

        }

        public void SetTabBg(int tab_bg) {
            this.tab_bg = tab_bg;
        }

        public int[] getTabItemBgs() {
            return new int[] { R.drawable.common_tab2_bg_left, R.drawable.common_tab2_bg_middle,
                    R.drawable.common_tab2_bg_right };
        }

        public int getDividerColor() {
            return Color.argb(0xFF, 0x07, 0xd4, 0x6d);
        }

        public int getTextColorSelectorId() {
            return R.color.selector_tab_text_2;
        }

        int getTextSizeSp() {
            return 18;
        }

        int getTextSizeSpSelected() {
            return 19;
        }
    }
}
