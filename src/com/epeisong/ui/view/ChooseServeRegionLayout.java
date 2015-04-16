package com.epeisong.ui.view;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity.OnChooseDictionaryListener;
import com.epeisong.model.RegionResult;
import com.epeisong.ui.activity.ChooseRegionActivity;
import com.epeisong.utils.ToastUtils;

/**
 * 服务区
 * 
 * @author poet
 * 
 */
public class ChooseServeRegionLayout extends FrameLayout implements OnClickListener, Choosable {

    private static final int REQUEST_CODE_REGION = 109;

    private FragmentActivity mActivity;
    private Fragment mFragment;

    private ImageView mHookIv01;
    private ImageView mHookIv02;

    private TextView mRegionTv;

    private RegionResult mRegionResult;

    private Choosion mChoosion;

    private OnChooseServeRegionListener mListener;

    private int mFileter = ChooseRegionActivity.FILTER_0_3;

    public ChooseServeRegionLayout(Context context) {
        this(context, null);
    }

    public ChooseServeRegionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_choose_serve_region, this);
        findViewById(R.id.rl_line_no_limit).setOnClickListener(this);
        findViewById(R.id.rl_line).setOnClickListener(this);
        mHookIv01 = (ImageView) findViewById(R.id.iv_hook_01);
        mHookIv02 = (ImageView) findViewById(R.id.iv_hook_02);
        mHookIv01.setSelected(true);
        mRegionTv = (TextView) findViewById(R.id.tv_region);
        mRegionTv.setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    @Override
    public Choosion getDefaultChoosion() {
        if (mChoosion == null) {
            mChoosion = new Choosion(-1, "地区不限");
        }
        return mChoosion;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public int getChooseDictionaryType() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getChooseTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OnChooseDictionaryListener getOnChooseDictionaryListener() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            Intent start = new Intent(EpsApplication.getInstance(), ChooseRegionActivity.class);
            start.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER, mFileter);
            start.putExtra(ChooseRegionActivity.EXTRA_IS_SHOW_NO_LIMIT, true);
            if (mActivity != null) {
                mActivity.startActivityForResult(start, REQUEST_CODE_REGION);
            } else {
                mFragment.startActivityForResult(start, REQUEST_CODE_REGION);
            }
        } else {
            super.setVisibility(visibility);
        }
    }

    @Override
    public void onClick(View v) {
        if (mActivity == null && mFragment == null) {
            ToastUtils
                    .showToast("Please invoke setActivity(FragmentActivity a) first，and invoke handleActivityResult method!");
            return;
        }
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
        case R.id.tv_region:
            if (!mHookIv02.isSelected()) {
                mHookIv02.setSelected(true);
                mHookIv01.setSelected(false);
            }
            Intent start = new Intent(EpsApplication.getInstance(), ChooseRegionActivity.class);
            start.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER, ChooseRegionActivity.FILTER_0_3);
            if (mActivity != null) {
                mActivity.startActivityForResult(start, REQUEST_CODE_REGION);
            } else {
                mFragment.startActivityForResult(start, REQUEST_CODE_REGION);
            }
            // mActivity.startActivityForResult(start, REQUEST_CODE_REGION);
            break;
        case R.id.btn_ok:
            if (mListener != null) {
                if (mHookIv01.isSelected()) {
                    mListener.onChoosedServeRegionDefault(mChoosion);
                } else if (mHookIv02.isSelected()) {
                    if (mRegionResult == null) {
                        ToastUtils.showToast("请选择服务区域");
                        return;
                    }
                    mListener.onChoosedServeRegion(mRegionResult);
                }
            }
            break;
        }
    }

    public void setActivity(FragmentActivity a) {
        mActivity = a;
    }

    public void setFragment(Fragment a) {
        mFragment = a;
    }

    public ChooseServeRegionLayout setFilter(int filter) {
        mFileter = filter;
        return this;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Serializable serializable = data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
            if (serializable != null && serializable instanceof RegionResult) {
                RegionResult result = (RegionResult) serializable;
                if (requestCode == REQUEST_CODE_REGION) {
                    mRegionResult = result;
                    mRegionTv.setText(result.getFullName());
                    if (mListener != null) {
                        mListener.onChoosedServeRegion(mRegionResult);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void setOnChooseServeRegionListener(OnChooseServeRegionListener listener) {
        mListener = listener;
    }

    public interface OnChooseServeRegionListener {
        void onChoosedServeRegion(RegionResult result);

        void onChoosedServeRegionDefault(Choosion choosion);
    }
}
