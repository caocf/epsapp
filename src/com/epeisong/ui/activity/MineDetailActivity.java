package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.epeisong.R;
import com.epeisong.base.activity.TabPages2Activity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.ui.fragment.MineAccountFragment;
import com.epeisong.ui.fragment.MineBusinessFragment;
import com.epeisong.utils.DimensionUtls;

/**
 * 我的基本信息（账号和业务)
 * 
 * @author poet
 * 
 */
public class MineDetailActivity extends TabPages2Activity {

    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragments = new ArrayList<Fragment>();
        mFragments.add(new MineAccountFragment());
        mFragments.add(new MineBusinessFragment());
        mTabRoot.setBackgroundColor(Color.argb(0xff, 0x00, 0x9c, 0xff));
        int top = DimensionUtls.getPixelFromDpInt(10);
        int bottom = DimensionUtls.getPixelFromDpInt(15);
        mTabRoot.setPadding(bottom, top, bottom, bottom);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "我的基本信息", null).setShowLogo(false);
    }

    @Override
    protected void onSetTabTitle(List<String> titles) {
        titles.add("账号");
        titles.add("业务");
    }

    @Override
    protected TabStyle getTabStyle() {
        return new TabStyle() {
            @Override
            public int getTabBg() {
                return R.drawable.shape_content_trans_frame_white;
            }

            public int[] getTabItemBgs() {
                return new int[] { R.drawable.shape_tab2_bg_white_left, R.drawable.shape_tab2_bg_white_right };
            }

            public int getTextColorSelectorId() {
                return R.color.selector_tab_text_3;
            }

            @Override
            public int getDividerColor() {
                return Color.TRANSPARENT;
            }
        };
    }

    @Override
    protected PagerAdapter onGetAdapter() {
        return new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }
        };
    }

}
