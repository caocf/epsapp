package com.epeisong.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * 自适应高度的ExpandableListView
 * @author poet
 *
 */
public class AdjustHeightExpandableListView extends ExpandableListView {

    public AdjustHeightExpandableListView(Context context) {
        super(context);
    }

    public AdjustHeightExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AdjustHeightExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
