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
import com.epeisong.logistics.common.Properties;
import com.epeisong.ui.fragment.ComplainFragment;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;

/**
 * 申诉处理
 * 
 * @author Jack
 * 
 */
public class ComplaintActivity extends TabPages2Activity {

    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragments = new ArrayList<Fragment>();
        ComplainFragment noneFragment = new ComplainFragment();
        Bundle args = new Bundle();
        args.putInt(ComplainFragment.ARGS_COMPLAINT_TYPE, Properties.COMPLAIN_TASK_STATUS_NOT_PROCESSED);
        noneFragment.setArguments(args);
        mFragments.add(noneFragment);
        
        ComplainFragment readyFragment = new ComplainFragment();
        Bundle args1 = new Bundle();
        args1.putInt(ComplainFragment.ARGS_COMPLAINT_TYPE, Properties.COMPLAIN_TASK_STATUS_PROCESSED);
        readyFragment.setArguments(args1);
        mFragments.add(readyFragment);
        
        ComplainFragment warningFragment = new ComplainFragment();
        Bundle args2 = new Bundle();
        args2.putInt(ComplainFragment.ARGS_COMPLAINT_TYPE, Properties.COMPLAIN_TASK_STATUS_WARNING);
        warningFragment.setArguments(args2);
        mFragments.add(warningFragment);
        
        
        mTabRoot.setBackgroundColor(Color.argb(0xff, 0x00, 0x9c, 0xff));
        int top = DimensionUtls.getPixelFromDpInt(10);
        int bottom = DimensionUtls.getPixelFromDpInt(15);
        mTabRoot.setPadding(bottom, top, bottom, bottom);
        
		mViewPager.setOffscreenPageLimit(3);
		HandlerUtils.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mFragments.get(0).isAdded()) {
					mFragments.get(0).setUserVisibleHint(true);
				} else {
					HandlerUtils.postDelayed(this, 100);
				}
			}
		}, 200);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "申诉处理", null).setShowLogo(false);
    }

    @Override
    protected void onSetTabTitle(List<String> titles) {
        titles.add("未处理");
        titles.add("已处理");
        titles.add("警报");
    }

    @Override
    protected TabStyle getTabStyle() {
        return new TabStyle() {
            @Override
            public int getTabBg() {
                return R.drawable.shape_content_trans_frame_white;
            }

            public int[] getTabItemBgs() {
                return new int[] { R.drawable.shape_tab2_bg_white_left, R.drawable.shape_tab2_bg_white_middle, R.drawable.shape_tab2_bg_white_right };
            }

            public int getTextColorSelectorId() {
                return R.color.selector_tab_text_3;
            }

            @Override
            public int getDividerColor() {
                return Color.WHITE;
            }
        };
    }

    @Override
    protected PagerAdapter onGetAdapter() {
        return new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }
        };
    }

}
