package com.epeisong.base.dialog;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.model.RegionResult;
import com.epeisong.ui.activity.ChooseRegionActivity;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 选择路线的Activity
 * @author poet
 *
 */
public class ChooseLineActivity extends Activity implements OnClickListener {

    public static final String EXTRA_START_REGION = "start_region";
    public static final String EXTRA_END_REGION = "end_region";

    public static final String EXTRA_HIDE_NO_LIMIT = "hide_no_limit";

    private ImageView mHookIv01;
    private ImageView mHookIv02;
    private TextView mStartRegionTv;
    private TextView mEndRegionTv;

    private RegionResult mStartRegion;
    private RegionResult mEndRegion;

    private boolean mHideNoLimit;
    private Bundle mExtrasForChooseRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHideNoLimit = getIntent().getBooleanExtra(EXTRA_HIDE_NO_LIMIT, false);
        mExtrasForChooseRegion = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        LinearLayout ll = new LinearLayout(this);
        ll.setPadding(1, 1, 1, 1);
        ll.setBackgroundResource(R.drawable.shape_corner_frame_transparent);
        ll.addView(SystemUtils.inflate(R.layout.dialog_choose_line));
        setContentView(ll);
        if (mHideNoLimit) {
            findViewById(R.id.rl_line_no_limit).setVisibility(View.GONE);
        } else {
            findViewById(R.id.rl_line_no_limit).setOnClickListener(this);
        }

        findViewById(R.id.rl_line).setOnClickListener(this);
        mHookIv01 = (ImageView) findViewById(R.id.iv_hook_01);
        mHookIv02 = (ImageView) findViewById(R.id.iv_hook_02);
        mHookIv01.setSelected(true);
        mStartRegionTv = (TextView) findViewById(R.id.tv_start_region);
        mEndRegionTv = (TextView) findViewById(R.id.tv_end_region);
        mStartRegionTv.setOnClickListener(this);
        mEndRegionTv.setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rl_line_no_limit:
            if (!mHookIv01.isSelected()) {
                mHookIv01.setSelected(true);
                mHookIv02.setSelected(false);
            }
            break;
        case R.id.rl_line:
            if (!mHookIv02.isSelected()) {
                mHookIv02.setSelected(true);
                mHookIv01.setSelected(false);
            }
            break;
        case R.id.tv_start_region:
            if (!mHookIv02.isSelected()) {
                mHookIv02.setSelected(true);
                mHookIv01.setSelected(false);
            }
            ChooseRegionActivity.launch(this, 100, mExtrasForChooseRegion);
            // Intent start = new Intent(EpsApplication.getInstance(),
            // ChooseRegionActivity.class);
            // start.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER,
            // ChooseRegionActivity.FILTER_0_3);
            // startActivityForResult(start, 100);
            break;
        case R.id.tv_end_region:
            if (!mHookIv02.isSelected()) {
                mHookIv02.setSelected(true);
                mHookIv01.setSelected(false);
            }
            ChooseRegionActivity.launch(this, 101, mExtrasForChooseRegion);
            // Intent end = new Intent(EpsApplication.getInstance(),
            // ChooseRegionActivity.class);
            // end.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER,
            // ChooseRegionActivity.FILTER_0_3);
            // startActivityForResult(end, 101);
            break;
        case R.id.btn_ok:
            if (mHookIv02.isSelected()) {
                if (mStartRegion == null || mEndRegion == null) {
                    ToastUtils.showToast("请选择地址！");
                    return;
                }
            }
            Intent intent = new Intent();
            intent.putExtra(EXTRA_START_REGION, mStartRegion);
            intent.putExtra(EXTRA_END_REGION, mEndRegion);
            setResult(Activity.RESULT_OK, intent);
            finish();
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Serializable serializable = data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
            if (serializable != null && serializable instanceof RegionResult) {
                RegionResult result = (RegionResult) serializable;
                if (requestCode == 100) {
                    mStartRegion = result;
                    mStartRegionTv.setText(result.getShortNameFromDistrict());
                } else if (requestCode == 101) {
                    mEndRegion = result;
                    mEndRegionTv.setText(result.getShortNameFromDistrict());
                }
            }
        }
    }

    public static void launch(Activity a, int requestCode) {
        if (a == null) {
            return;
        }
        Intent intent = new Intent(a, ChooseLineActivity.class);
        a.startActivityForResult(intent, requestCode);
    }

    public static void launch(Fragment f, int requestCode) {
        if (f == null || f.getActivity() == null) {
            return;
        }
        Intent intent = new Intent(f.getActivity(), ChooseLineActivity.class);
        f.startActivityForResult(intent, requestCode);
    }

    public static void launch(Fragment f, int requestCode, boolean hideNoLimit, Bundle extrasForChooseRegion) {
        if (f == null || f.getActivity() == null) {
            return;
        }
        Intent intent = new Intent(f.getActivity(), ChooseLineActivity.class);
        intent.putExtra(EXTRA_HIDE_NO_LIMIT, hideNoLimit);
        if (extrasForChooseRegion != null) {
            intent.putExtras(extrasForChooseRegion);
        }
        f.startActivityForResult(intent, requestCode);
    }
    
    public static void launch(Activity a, int requestCode, boolean hideNoLimit, Bundle extrasForChooseRegion) {
    	if (a == null) {
            return;
        }
        Intent intent = new Intent(a, ChooseLineActivity.class);
        intent.putExtra(EXTRA_HIDE_NO_LIMIT, hideNoLimit);
        if (extrasForChooseRegion != null) {
            intent.putExtras(extrasForChooseRegion);
        }
        a.startActivityForResult(intent, requestCode);
    }
}
