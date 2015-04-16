package com.epeisong.ui.view;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.model.RegionResult;
import com.epeisong.ui.activity.ChooseRegionActivity;
import com.epeisong.utils.ToastUtils;

/**
 * 选择线路，用于Tab点击时下拉显示
 * @author poet
 *
 */
public class ChooseLineSmallLayout extends FrameLayout implements OnClickListener {

    TextView startTv, endTv;

    Activity activity;

    int filter = ChooseRegionActivity.FILTER_0_3;

    OnChooseLineSmallListener listener;

    ChooseLineResult lineResult = new ChooseLineResult();

    public ChooseLineSmallLayout(Activity a, int width) {
        super(a);
        this.activity = a;
        this.setBackgroundColor(Color.argb(0x88, 0x00, 0x00, 0x00));
        this.setOnClickListener(this);
        View view = LayoutInflater.from(a).inflate(R.layout.layout_choose_line_small, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, -2);
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        this.addView(view, params);

        findViewById(R.id.tv_line_no_limit).setOnClickListener(this);
        startTv = (TextView) findViewById(R.id.tv_start_region);
        endTv = (TextView) findViewById(R.id.tv_end_region);
        startTv.setOnClickListener(this);
        endTv.setOnClickListener(this);
        findViewById(R.id.btn).setOnClickListener(this);
    }

    public ChooseLineSmallLayout setChoosedLine(ChooseLineResult result) {
        if (result != null) {
            this.lineResult = result;
            if (result.start != null && result.end != null) {
                startTv.setText(result.start.getShortNameFromDistrict());
                endTv.setText(result.end.getShortNameFromDistrict());
            }
        }
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v == this) {
            callListener(null);
            return;
        }
        switch (v.getId()) {
        case R.id.tv_line_no_limit:
            lineResult.start = null;
            lineResult.end = null;
            startTv.setText(null);
            endTv.setText(null);
            callListener(lineResult);
            break;
        case R.id.tv_start_region:
            Intent start = new Intent(activity, ChooseRegionActivity.class);
            start.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER, filter);
            start.putExtra(ChooseRegionActivity.EXTRA_IS_SHOW_COUNTRY, false);
            activity.startActivityForResult(start, 100);
            break;
        case R.id.tv_end_region:
            Intent end = new Intent(activity, ChooseRegionActivity.class);
            end.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER, filter);
            activity.startActivityForResult(end, 200);
            break;
        case R.id.btn:
            if (lineResult.start == null || lineResult.end == null) {
                ToastUtils.showToast("选择地址");
                return;
            }
            callListener(lineResult);
            break;
        }
    }

    void callListener(ChooseLineResult result) {
        if (listener != null) {
            listener.onChoosedLine(result);
        }
    }

    public void setListener(OnChooseLineSmallListener l) {
        listener = l;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Serializable serializable = data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
            if (serializable != null && serializable instanceof RegionResult) {
                RegionResult result = (RegionResult) serializable;
                if (requestCode == 100) {
                    lineResult.start = result;
                    startTv.setText(result.getShortNameFromDistrict());
                    return true;
                } else if (requestCode == 200) {
                    lineResult.end = result;
                    endTv.setText(result.getShortNameFromDistrict());
                    return true;
                }
            }
        }
        return false;
    }

    public static interface OnChooseLineSmallListener {
        void onChoosedLine(ChooseLineResult result);
    }

    public static class ChooseLineResult implements Serializable {
        private static final long serialVersionUID = -4809143386628318035L;

        public RegionResult start;
        public RegionResult end;
    }
}
