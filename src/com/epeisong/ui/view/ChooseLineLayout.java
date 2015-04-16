package com.epeisong.ui.view;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity.OnChooseDictionaryListener;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.data.dao.CommonLineDao;
import com.epeisong.data.dao.CommonLineDao.CommonLineObserver;
import com.epeisong.data.dao.RegionDao;
import com.epeisong.model.CommonLine;
import com.epeisong.model.Region;
import com.epeisong.model.RegionResult;
import com.epeisong.ui.activity.ChooseRegionActivity;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 选择线路布局
 * 
 * @author poet
 * 
 */
public class ChooseLineLayout extends FrameLayout implements OnClickListener, Choosable, OnItemClickListener,
        CommonLineObserver {

    public static final int REQUEST_CODE_START_REGION = 110;
    public static final int REQUEST_CODE_END_REGION = 111;

    private FragmentActivity mActivity;
    private Fragment mFragment;

    private RegionResult mStartRegion;
    private RegionResult mEndRegion;

    private ImageView mHookIv01;
    private ImageView mHookIv02;

    private TextView mStartRegionTv;
    private TextView mEndRegionTv;

    private ListView mListView;
    private MyAdapter mAdapter;

    private Choosion mChoosionDefault;

    private OnChooseLineListener mOnChooseLineListener;

    private int mFilter = ChooseRegionActivity.FILTER_0_3;

    public ChooseLineLayout(Context context) {
        this(context, null);
    }

    public ChooseLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_choose_line, this);
        findViewById(R.id.rl_line_no_limit).setOnClickListener(this);
        findViewById(R.id.rl_line).setOnClickListener(this);
        mHookIv01 = (ImageView) findViewById(R.id.iv_hook_01);
        mHookIv02 = (ImageView) findViewById(R.id.iv_hook_02);
        mHookIv01.setSelected(true);
        mStartRegionTv = (TextView) findViewById(R.id.tv_start_region);
        mEndRegionTv = (TextView) findViewById(R.id.tv_end_region);
        mStartRegionTv.setOnClickListener(this);
        mEndRegionTv.setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.lv);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter = new MyAdapter());

        requestCommonLine();

        CommonLineDao.getInstance().addCommonLineObserver(this);
    }

    @Override
    public void onCommonLineChange() {
        requestCommonLine();
    }

    private void requestCommonLine() {
        AsyncTask<Void, Void, List<CommonLine>> task = new AsyncTask<Void, Void, List<CommonLine>>() {
            @Override
            protected List<CommonLine> doInBackground(Void... params) {
                if (!SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_CURR_USER_LOGINED, false)) {
                    return null;
                }
                return CommonLineDao.getInstance().query(CommonLine.SCENE_SEARCH, 10);
            }

            @Override
            protected void onPostExecute(List<CommonLine> result) {
                if (result != null && !result.isEmpty()) {
                    mAdapter.replaceAll(result);
                }
            }
        };
        task.execute();
    }

    private class MyAdapter extends HoldDataBaseAdapter<CommonLine> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CommonLine line = getItem(position);
            TextView tv = new TextView(getContext());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tv.setText(line.getStart_name() + " - " + line.getEnd_name());
            int p = DimensionUtls.getPixelFromDpInt(10);
            tv.setPadding(p, p, p, p);
            return tv;
        }
    }

    @Override
    public Choosion getDefaultChoosion() {
        if (mChoosionDefault == null) {
            mChoosionDefault = new Choosion(-1, "线路不限");
        }
        return mChoosionDefault;
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
    public View getView() {
        return this;
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
        case R.id.tv_start_region:
            if (!mHookIv02.isSelected()) {
                mHookIv02.setSelected(true);
                mHookIv01.setSelected(false);
            }
            // Intent start = new Intent(getContext(), AreaListItem.class);
            // start.putExtra("flag", 1);
            // mActivity.startActivityForResult(start,
            // REQUEST_CODE_START_REGION);
            Intent start = new Intent(EpsApplication.getInstance(), ChooseRegionActivity.class);
            start.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER, mFilter);
            start.putExtra(ChooseRegionActivity.EXTRA_IS_SHOW_COUNTRY, false);
            if (mActivity != null) {
                mActivity.startActivityForResult(start, REQUEST_CODE_START_REGION);
            } else {
                mFragment.startActivityForResult(start, REQUEST_CODE_START_REGION);
            }
            break;
        case R.id.tv_end_region:
            if (!mHookIv02.isSelected()) {
                mHookIv02.setSelected(true);
                mHookIv01.setSelected(false);
            }
            // Intent end = new Intent(getContext(), AreaListItem.class);
            // end.putExtra("flag", 1);
            // mActivity.startActivityForResult(end, REQUEST_CODE_END_REGION);
            Intent end = new Intent(EpsApplication.getInstance(), ChooseRegionActivity.class);
            end.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER, mFilter);
            if (mActivity != null) {
                mActivity.startActivityForResult(end, REQUEST_CODE_END_REGION);
            } else {
                mFragment.startActivityForResult(end, REQUEST_CODE_END_REGION);
            }
            break;
        case R.id.btn_ok:
            if (mOnChooseLineListener != null) {
                if (mHookIv01.isSelected()) {
                    mOnChooseLineListener.onChoosedLine(null, null);
                } else if (mHookIv02.isSelected()) {
                    if (mStartRegion == null || mEndRegion == null) {
                        ToastUtils.showToast("请选择地址！");
                    } else {
                        mOnChooseLineListener.onChoosedLine(mStartRegion, mEndRegion);
                        if (SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_CURR_USER_LOGINED, false)) {
                            CommonLine line = new CommonLine();
                            line.setStart_code(mStartRegion.getCode());
                            line.setStart_name(mStartRegion.getNameByType());
                            line.setStart_type(mStartRegion.getType());
                            line.setEnd_code(mEndRegion.getCode());
                            line.setEnd_name(mEndRegion.getNameByType());
                            line.setEnd_type(mEndRegion.getType());
                            line.setScene(CommonLine.SCENE_SEARCH);
                            line.setUpdate_time(System.currentTimeMillis());
                            CommonLineDao.getInstance().insert(line);
                        }
                    }
                }
            }
            break;
        }
    }

    private void onChooseStart(RegionResult start) {
        mStartRegion = start;
        mStartRegionTv.setText(start.getShortNameFromDistrict());
    }

    private void onChooseEnd(RegionResult end) {
        mEndRegion = end;
        mEndRegionTv.setText(end.getShortNameFromDistrict());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CommonLine line = mAdapter.getItem(position);
        line.setUpdate_time(System.currentTimeMillis());
        CommonLineDao.getInstance().insert(line);
        Region start = new Region();
        start.setCode(line.getStart_code());
        start.setName(line.getStart_name());
        start.setType(line.getStart_type());
        Region end = new Region();
        end.setCode(line.getEnd_code());
        end.setType(line.getEnd_type());
        end.setName(line.getEnd_name());
        RegionResult startResult = RegionDao.convertToResult(start);
        RegionResult endResult = RegionDao.convertToResult(end);
        onChooseStart(startResult);
        onChooseEnd(endResult);
        mHookIv01.setSelected(false);
        mHookIv02.setSelected(true);
        mOnChooseLineListener.onChoosedLine(startResult, endResult);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Serializable serializable = data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
            if (serializable != null && serializable instanceof RegionResult) {
                RegionResult result = (RegionResult) serializable;
                if (requestCode == REQUEST_CODE_START_REGION) {
                    onChooseStart(result);
                    return true;
                } else if (requestCode == REQUEST_CODE_END_REGION) {
                    onChooseEnd(result);
                    return true;
                }
            }
        }
        return false;
    }

    public void setActivity(FragmentActivity a) {
        mActivity = a;
    }

    public void setFragment(Fragment fragment) {
        mFragment = fragment;
    }

    public void setFilter(int filter) {
        mFilter = filter;
    }

    public void setOnChooseLineListener(OnChooseLineListener listener) {
        mOnChooseLineListener = listener;
    }

    public interface OnChooseLineListener {
        /**
         * 选择路线的结果，如果选择不限，start和end都为null
         * 
         * @param start
         * @param end
         */
        void onChoosedLine(RegionResult start, RegionResult end);
    }
}
