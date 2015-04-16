package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;

public abstract class HorizontalFilterActivity extends BaseActivity implements OnClickListener {

    private LinearLayout mFilterTitleLayout;
    private FrameLayout mContentContainer;
    private FrameLayout mChoosableContainer;

    private List<TextView> mTitleTvs;
    private List<View> mTitleUnderLines;
    private List<View> mChoosableViews;

    private TextView view_empty;
    @Override
    public final void onBackPressed() {
        if (!hideAll()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.fl_filter_container:
            hideAll();
            return;
        }
        Object tag = v.getTag();
        if (tag != null && tag instanceof Integer) {
            int index = (Integer) tag;
            boolean show = !mTitleTvs.get(index).isSelected();
            hideAll();
            if (show) {
                showItem(index);
            }
        }
    }

    public TextView getEmptyText()
    {
    	return view_empty;
    }
    private boolean hideAll() {
        boolean showing = mChoosableContainer.getVisibility() == View.VISIBLE;
        if (showing) {
            mChoosableContainer.setVisibility(View.GONE);
            for (View v : mChoosableViews) {
                v.setVisibility(View.GONE);
            }
            for (View v : mTitleTvs) {
                v.setSelected(false);
            }
            for (View v : mTitleUnderLines) {
                v.setSelected(false);
            }
        }
        return showing;
    }

    private void showItem(int pos) {
        mChoosableContainer.setVisibility(View.VISIBLE);
        mChoosableViews.get(pos).setVisibility(View.VISIBLE);
        mTitleTvs.get(pos).setSelected(true);
        mTitleUnderLines.get(pos).setSelected(true);
    }

    protected void hideFilter(int pos) {
        if (pos >= 0 && pos < mTitleTvs.size()) {
            mChoosableContainer.setVisibility(View.GONE);
            mChoosableViews.get(pos).setVisibility(View.GONE);
            mTitleTvs.get(pos).setSelected(false);
            mTitleUnderLines.get(pos).setSelected(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_filter_layout);
        mFilterTitleLayout = (LinearLayout) findViewById(R.id.ll_filter_title);
        mContentContainer = (FrameLayout) findViewById(R.id.fl_content_container);
        mChoosableContainer = (FrameLayout) findViewById(R.id.fl_filter_container);
        mChoosableContainer.setOnClickListener(this);

        view_empty = (TextView) findViewById(R.id.view_empty);
        view_empty.setText(null);
        //lv.setEmptyView(view_empty);
        
        Map<String, ? extends View> map = onCreateFilterTitle();
        if (map != null && !map.isEmpty()) {
            mTitleTvs = new ArrayList<TextView>();
            mTitleUnderLines = new ArrayList<View>();
            mChoosableViews = new ArrayList<View>();

            LinearLayout.LayoutParams params = new LayoutParams(0, -1);
            params.weight = 1;
            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(-1, -2);
            int index = 0;
            for (Map.Entry<String, ? extends View> entry : map.entrySet()) {
                View title = SystemUtils.inflate(R.layout.layout_choosable);
                title.setTag(index++);
                title.setOnClickListener(this);
                mFilterTitleLayout.addView(title, params);
                if (index > 0 && index < map.size()) {
                    View v = new View(getApplicationContext());
                    int w = (int) DimensionUtls.getPixelFromDp(1);
                    v.setLayoutParams(new LayoutParams(w, -1));
//                    v.setBackgroundResource(R.color.choosable_under_line_normal);
                    v.setBackgroundResource(R.color.white);
                    mFilterTitleLayout.addView(v);
                }
                TextView tv = (TextView) title.findViewById(R.id.tv_choosable);
                tv.setText(entry.getKey());
                tv.setTextSize(18);
                mTitleTvs.add(tv);
                View underline = title.findViewById(R.id.view_under_line);
                mTitleUnderLines.add(underline);

                View filter = entry.getValue();
                filter.setVisibility(View.GONE);
                mChoosableContainer.addView(filter, params2);
                mChoosableViews.add(filter);
            }
        }

        View content = onCreateContentView();
        if (content != null) {
            mContentContainer.addView(content);
        }
    }

    protected abstract View onCreateContentView();

    protected abstract Map<String, ? extends View> onCreateFilterTitle();

    protected void setFilterTitile(int pos, String title) {
        if (pos >= 0 && pos < mTitleTvs.size()) {
            mTitleTvs.get(pos).setText(title);
        }
    }
}
