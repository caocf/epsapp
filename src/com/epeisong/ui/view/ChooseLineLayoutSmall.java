package com.epeisong.ui.view;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.model.RegionResult;
import com.epeisong.ui.activity.ChooseRegionActivity;
import com.epeisong.ui.view.ChooseLineLayout.OnChooseLineListener;

/**
 * 选择线路，用于Tab点击时下拉显示
 * @author poet
 *
 */
public class ChooseLineLayoutSmall extends FrameLayout implements OnClickListener {

    TextView startTv, endTv;

    Activity activity;

    int filter = ChooseRegionActivity.FILTER_0_3;

    OnChooseLineListener listener;

    public ChooseLineLayoutSmall(Activity a, int width) {
        super(a);
        this.setBackgroundColor(Color.argb(0x88, 0x00, 0x00, 0x00));
        this.setOnClickListener(this);
        View view = LayoutInflater.from(a).inflate(R.layout.layout_choose_line_small, null);
        this.addView(view, new FrameLayout.LayoutParams(width, -2));

        findViewById(R.id.tv_line_no_limit).setOnClickListener(this);
        startTv = (TextView) findViewById(R.id.tv_start_region);
        endTv = (TextView) findViewById(R.id.tv_end_region);
        startTv.setOnClickListener(this);
        endTv.setOnClickListener(this);
        findViewById(R.id.btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_line_no_limit:

            break;
        case R.id.tv_start_region:

            break;
        case R.id.tv_end_region:

            break;
        case R.id.btn:

            break;
        }
    }

    public void setListener(OnChooseLineListener l) {
        listener = l;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public static interface OnChooseLineSmallListener {
        void onChoosedLine();
    }
    
    public static class ChooseLineResult {
//        RegionResult
    }
}
