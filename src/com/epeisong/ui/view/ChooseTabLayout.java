package com.epeisong.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.utils.DimensionUtls;

/**
 * 
 * @author poet
 *
 */
public class ChooseTabLayout extends RelativeLayout {

    Context context;

    LinearLayout mTabContainerLayout;
    List<View> mTabContainerList;
    List<TextView> mTabViewList;

    public ChooseTabLayout(Context context) {
        super(context);
        this.context = context;
        mTabContainerList = new ArrayList<View>();
        mTabViewList = new ArrayList<TextView>();

        mTabContainerLayout = new LinearLayout(context);
        mTabContainerLayout.setOrientation(LinearLayout.HORIZONTAL);
        this.addView(mTabContainerLayout, new ViewGroup.LayoutParams(-1, -1));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, DimensionUtls.getPixelFromDpInt(1));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        View line = new View(context);
        line.setBackgroundColor(Color.GRAY);
        this.addView(line, params);
    }

    public void addTab(String title, View.OnClickListener l) {
        if (mTabContainerLayout.getChildCount() > 0) {
            View line = new View(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DimensionUtls.getPixelFromDpInt(1), -1);
            params.topMargin = params.bottomMargin = DimensionUtls.getPixelFromDpInt(5);
            line.setLayoutParams(params);
            line.setBackgroundColor(Color.argb(0xff, 0x80, 0x80, 0x80));
            mTabContainerLayout.addView(line);
        }
        LinearLayout ll = new LinearLayout(context);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1, 1);
        mTabContainerLayout.addView(ll, params);
        this.mTabContainerList.add(ll);
        ll.setOnClickListener(l);

        TextView tv = new TextView(context);
        tv.setTextColor(new ColorStateList(new int[][] { { android.R.attr.state_enabled },
                { -android.R.attr.state_enabled } }, new int[] { Color.BLACK, Color.GRAY }));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        tv.setText(title);
        Drawable right = context.getResources().getDrawable(R.drawable.selector_common_arrow_gray_top_and_bottom2);
        right.setBounds(0, 0, 32, 18);
        tv.setCompoundDrawables(null, null, right, null);
        tv.setCompoundDrawablePadding(DimensionUtls.getPixelFromDpInt(5));
        ll.addView(tv);
        this.mTabViewList.add(tv);
    }

    public View getTabView(int pos) {
        return mTabContainerList.get(pos);
    }

    public void setTabEnable(int pos, boolean enabled) {
        mTabViewList.get(pos).setEnabled(enabled);
        mTabContainerList.get(pos).setEnabled(enabled);
    }

    public void setSelected(int pos) {
        for (int i = 0; i < mTabViewList.size(); i++) {
            mTabViewList.get(i).setSelected(i == pos);
        }
    }

    public void setTabText(int pos, String text) {
        mTabViewList.get(pos).setText(text);
    }

    public void cancelAll() {
        for (TextView tv : mTabViewList) {
            tv.setSelected(false);
        }
    }
}
