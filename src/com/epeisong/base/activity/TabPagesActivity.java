package com.epeisong.base.activity;

import java.util.List;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.epeisong.R;
import com.epeisong.base.view.TabViewsText;

/**
 * 分页显示的Activity：Viewpager
 * 
 * @author poet
 * 
 */
public abstract class TabPagesActivity extends BaseActivity {

    protected TabViewsText mTabViewsText;
    protected ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_tab_text_viewpager);
        mTabViewsText = (TabViewsText) findViewById(R.id.tab);
        float textSize = onGetTabTextSizeInsp();
        if (textSize > 0) {
            mTabViewsText.setTextSizeInSp(textSize);
        }
        int textColorResId = onGetTabTextColorResId();
        if (textColorResId > 0) {
            mTabViewsText.setTextColorResId(textColorResId);
        }
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mTabViewsText.setViewPager(mViewPager);
        mTabViewsText.setDividerColor(onGetTabDividerColor());
        mTabViewsText.setTabTexts(getTabTitles());

        mViewPager.setAdapter(getAdapter());

        mTabViewsText.setTab(0);

    }

    protected int onGetTabDividerColor() {
        return 0;
    }

    protected float onGetTabTextSizeInsp() {
        return 0;
    }

    protected int onGetTabTextColorResId() {
        return 0;
    }

    protected abstract List<String> getTabTitles();

    protected abstract PagerAdapter getAdapter();

    public TabViewsText getTabViewsText() {
        return mTabViewsText;
    }
}
