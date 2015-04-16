package com.epeisong.ui.view;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity.OnChooseDictionaryListener;
import com.epeisong.model.RegionResult;
import com.epeisong.ui.activity.ChooseRegionActivity;
import com.epeisong.utils.ToastUtils;

/**
 * 选择所在地
 * @author gnn
 *
 */
public class ChooseCurrentPlaceLayout extends FrameLayout implements OnClickListener , Choosable {

	public static final int REQUEST_CODE_START_REGION = 100;
	
	private FragmentActivity mActivity;

	private RegionResult mStartRegion;
	
	private ImageView mHookIv01;
	private ImageView mHookIv02;

	private TextView mStartRegionTv;

	private OnChooseCurrentPlaceListener mOnChooseCurrentPlaceListener;

	public ChooseCurrentPlaceLayout(Context context) {
		this(context, null);
	}

	public ChooseCurrentPlaceLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.layout_choose_current_palce, this);
		findViewById(R.id.rl_line_no_limit).setOnClickListener(this);
		findViewById(R.id.rl_line).setOnClickListener(this);
		mHookIv01 = (ImageView) findViewById(R.id.iv_hook_01);
		mHookIv02 = (ImageView) findViewById(R.id.iv_hook_02);
		mHookIv01.setSelected(true);
		mStartRegionTv = (TextView) findViewById(R.id.tv_start_region);
		mStartRegionTv.setOnClickListener(this);
		findViewById(R.id.btn_ok).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (mActivity == null) {
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
		case R.id.tv_start_region:
			if (!mHookIv02.isSelected()) {
				mHookIv02.setSelected(true);
				mHookIv01.setSelected(false);
			}
			Intent start = new Intent(mActivity, ChooseRegionActivity.class);
			start.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER,
					ChooseRegionActivity.FILTER_0_3);
			mActivity.startActivityForResult(start, REQUEST_CODE_START_REGION);
			break;
		
		case R.id.btn_ok:
			if (mOnChooseCurrentPlaceListener != null) {
				if (mHookIv01.isSelected()) {
//					mOnChooseLineListener.onChoosedLine(null, null);
				} else if (mHookIv02.isSelected()) {
					if (mStartRegion == null) {
						ToastUtils.showToast("请选择地址！");
					} else {
						mOnChooseCurrentPlaceListener.onChoosedCurrentPlace(mStartRegion);
					}
				}
			}
			break;
		}
	}
	
	@Override
	public int getChooseDictionaryType() {
		// TODO Auto-generated method stub
		return 0;
	}@Override
	public String getChooseTitle() {
		// TODO Auto-generated method stub
		return null;
	}@Override
	public OnChooseDictionaryListener getOnChooseDictionaryListener() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Serializable serializable = data
					.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
			if (serializable != null && serializable instanceof RegionResult) {
				RegionResult result = (RegionResult) serializable;
				if (requestCode == REQUEST_CODE_START_REGION) {
					mStartRegion = result;
					mStartRegionTv.setText(result.getFullName());
				}
			}
		}
		return false;
	}

	public void setActivity(FragmentActivity a) {
		mActivity = a;
	}

	public void setOnChooseCurrentPlaceListener(OnChooseCurrentPlaceListener listener) {
		mOnChooseCurrentPlaceListener = listener;
	}

	public interface OnChooseCurrentPlaceListener {
		void onChoosedCurrentPlace(RegionResult start);
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Choosion getDefaultChoosion() {
		Choosion choosion = new Choosion();
        choosion.setName("所在地");
        choosion.setCode(-1);
        return choosion;
	}
}
