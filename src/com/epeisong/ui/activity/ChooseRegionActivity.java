package com.epeisong.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.bdmap.epsloc.EpsLocation;
import com.bdmap.epsloc.EpsLocationRequestor;
import com.bdmap.epsloc.EpsLocationRequestor.OnEpsLocationListener;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.IndexBar;
import com.epeisong.base.view.IndexBar.OnChooseIndexListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.CommonRegionDao;
import com.epeisong.data.dao.RegionDao;
import com.epeisong.model.CommonRegion;
import com.epeisong.model.Region;
import com.epeisong.model.RegionResult;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 选择地区
 * 
 * @author poet
 * 
 */
public class ChooseRegionActivity extends BaseActivity implements OnClickListener, OnChildClickListener {

    private class MyAdapter extends BaseExpandableListAdapter {

        private List<String> titles;
        private List<List<Region>> lists;

        public MyAdapter(List<String> titles, List<List<Region>> lists) {
            this.titles = titles;
            this.lists = lists;
        }

        public int getTitleIndex(String title) {
            return titles.indexOf(title);
        }

        @Override
        public Region getChild(int groupPosition, int childPosition) {
            return lists.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return lists.get(groupPosition).size();
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {
            ChildViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.activity_choose_region_child_item);
                holder = new ChildViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }
            holder.fillData(getChild(groupPosition, childPosition));
            return convertView;
        }

        @Override
        public String getGroup(int groupPosition) {
            return titles.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return titles.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.activity_choose_region_group_item);
                holder = new GroupViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }
            holder.fillData(getGroup(groupPosition));
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    class ChildViewHolder {
        TextView tv_region;
        TextView tv_region_note;

        public void fillData(Region region) {
            tv_region.setText(region.getName());
            tv_region_note.setText(region.getNote());
        }

        public void findView(View v) {
            tv_region = (TextView) v.findViewById(R.id.tv_region_name);
            tv_region_note = (TextView) v.findViewById(R.id.tv_region_note);
        }
    }

    class GroupViewHolder {
        TextView tv_title;

        public void fillData(String title) {
            tv_title.setText(title);
        }

        public void findView(View v) {
            tv_title = (TextView) v.findViewById(R.id.tv_title);
        }
    }

    public static final String EXTRA_IN_FILTER = "filter";

    public static final String EXTRA_OUT_REGION = "region";

    public static final String EXTRA_IS_SHOW_NO_LIMIT = "is_show_no_limit";

    public static final String EXTRA_IS_SHOW_COUNTRY = "is_show_country";

    public static final int FILTER_0_3 = 1; // 显示大区到小区
    public static final int FILTER_0_2 = 2; // 显示大区省市
    public static final int FILTER_2 = 3; // 只显示市
    public static final int FILTER_1_3 = 4; // 显示省市区

    private static final int REQUEST_CODE_SEARCH = 100;
    private int mFilter;
    private boolean mShowNoLimitRegion;
    private boolean mShowCountry;

    private ExpandableListView mExpandableListView;

    private MyAdapter mAdapter;

    private List<Region> mData;
    private List<Region> mCommonRegions;

    private boolean mUseAnim = false;

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Region region = mAdapter.getChild(groupPosition, childPosition);
        if (region.getCategory() == Region.CATEGORY_LOC_ING) {
            return true;
        } else if (region.getCategory() == Region.CATEGORY_LOC_FAIL) {
            requestLocation();
            region.setName("定位中...");
            region.setCategory(Region.CATEGORY_LOC_ING);
            mAdapter.notifyDataSetChanged();
            return true;
        }
        doResult(region);
        return true;
    }

    void requestLocation() {
        EpsLocationRequestor requestor = new EpsLocationRequestor();
        requestor.requestEpsLocation(new OnEpsLocationListener() {
            @Override
            public void onEpsLocation(EpsLocation epsLocation) {
                if (epsLocation == null || TextUtils.isEmpty(epsLocation.getCityName())) {
                    Region region = new Region();
                    region.setName("定位失败，点击重新定位");
                    region.setCategory(Region.CATEGORY_LOC_FAIL);
                    handleData(region);
                } else {
                    Region region = epsLocation.convertToRegion();
                    region.setCategory(Region.CATEGORY_LOC_SUCCESS);
                    handleData(region);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_search:
            Intent i = new Intent(this, ChooseRegionSearchActivity.class);
            i.putExtra(EXTRA_IN_FILTER, mFilter);
            startActivityForResult(i, REQUEST_CODE_SEARCH);
            mCustomTitle.setVisibility(View.GONE);
            return;
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mUseAnim) {
            overridePendingTransition(0, R.anim.slide_out_to_bottom);
        }
    }

    private void doResult(Region region) {
        if (SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_CURR_USER_LOGINED, false)) {
            if (region.getCode() != -1 && region.getCode() != 9) {
                CommonRegionDao.getInstance().insert(region);
            }
        }
        if (region.getType() > 0) {
            region.setParent(RegionDao.getInstance().queryParent(region.getCode()));
        }
        RegionResult result = new RegionResult();
        result.setType(region.getType());
        if (region.getCode() == -1) {
            result.setCode(region.getCode());
            result.setRegionName(region.getName());
        } else {
            result.setCode(region.getCode());
            while (region != null) {
                if (region.getType() == 3) {
                    result.setDistrictName(region.getName());
                } else if (region.getType() == 2 || region.getType() == 11) {
                    result.setCityName(region.getName());
                } else if (region.getType() == 1 || region.getType() == 11) {
                    result.setProvinceName(region.getName());
                } else if (region.getType() == 0) {
                    result.setRegionName(region.getName());
                }
                region = region.getParent();
            }
        }
        Intent data = new Intent();
        data.putExtra(EXTRA_OUT_REGION, result);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void handleData(Region loc) {
        List<String> titles = new ArrayList<String>();
        List<List<Region>> lists = new ArrayList<List<Region>>();
        RegionDao.groupByPinyin(mData, titles, lists);

        titles.add(0, "定位城市");
        List<Region> locs = new ArrayList<Region>();
        locs.add(loc);
        lists.add(0, locs);

        List<Region> usedRegions = new ArrayList<Region>();
        if (mShowNoLimitRegion) {
            Region noLimit = new Region();
            noLimit.setCode(-1);
            noLimit.setName("地区不限");
            usedRegions.add(noLimit);
        } else {
            if (mShowCountry && mFilter <= FILTER_0_2) {
                Region 全国 = new Region();
                全国.setCode(9);
                全国.setName("全国");
                usedRegions.add(全国);
            }
        }
        if (mCommonRegions != null && !mCommonRegions.isEmpty()) {
            usedRegions.addAll(mCommonRegions);
        }
        if (!usedRegions.isEmpty()) {
            titles.add(1, "常用地区");
            lists.add(1, usedRegions);
        }
        mExpandableListView.setAdapter(mAdapter = new MyAdapter(titles, lists));
        for (int i = 0; i < titles.size(); i++) {
            mExpandableListView.expandGroup(i);
        }
    }

    private void requestData() {
        showPendingDialog(null);
        AsyncTask<Void, Void, List<Region>> task = new AsyncTask<Void, Void, List<Region>>() {
            @Override
            protected List<Region> doInBackground(Void... params) {
                List<Region> regions = RegionDao.getInstance().query(mFilter, null);
                if (!SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_CURR_USER_LOGINED, false)) {
                    return regions;
                }
                List<CommonRegion> crs = CommonRegionDao.getInstance().query(mFilter, 8);
                if (!crs.isEmpty()) {
                    if (mCommonRegions == null) {
                        mCommonRegions = new ArrayList<Region>();
                    }
                    for (CommonRegion cr : crs) {
                        in: for (Region r : regions) {
                            if (r.getCode() == cr.getCode()) {
                                mCommonRegions.add(r);
                                break in;
                            }
                        }
                    }
                }
                return regions;
            }

            @Override
            protected void onPostExecute(List<Region> result) {
                dismissPendingDialog();
                mData = result;
                Region region = new Region();
                region.setName("定位中...");
                handleData(region);
                requestLocation();
            }
        };
        task.execute();
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "地点定位").setShowLogo(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SEARCH) {
                if (data != null) {
                    final Serializable serializable = data.getSerializableExtra(EXTRA_OUT_REGION);
                    if (serializable != null && serializable instanceof Region) {
                        HandlerUtils.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doResult((Region) serializable);
                                finish();
                            }
                        }, 200);
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFilter = getIntent().getIntExtra(EXTRA_IN_FILTER, -1);
        mShowNoLimitRegion = getIntent().getBooleanExtra(EXTRA_IS_SHOW_NO_LIMIT, false);
        mShowCountry = getIntent().getBooleanExtra(EXTRA_IS_SHOW_COUNTRY, true);
        if (mFilter == -1) {
            throw new RuntimeException("必须指定显示地区，传递：EXTRA_IN_FILTER。");
        }
        if (mUseAnim) {
            overridePendingTransition(R.anim.slide_in_from_bottom, 0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_region);
        findViewById(R.id.tv_search).setOnClickListener(this);
        mExpandableListView = (ExpandableListView) findViewById(R.id.elv);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setOnChildClickListener(this);
        mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        requestData();

        String[] values = { "定位", "常用", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
                "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
        IndexBar bar = (IndexBar) findViewById(R.id.slideBar);
        bar.setIndexValues(Arrays.asList(values));
        bar.setOnChooseIndexListener(new OnChooseIndexListener() {

            @Override
            public void onChoosedIndex(String indexValue) {
                showPinyinPopup(indexValue);
                if ("定位".equals(indexValue)) {
                    indexValue = "定位城市";
                } else if ("常用".equals(indexValue)) {
                    indexValue = "常用地区";
                }
                int index = mAdapter.getTitleIndex(indexValue);
                if (index >= 0) {
                    mExpandableListView.setSelectedGroup(index);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        mCustomTitle.setVisibility(View.VISIBLE);
        super.onResume();
    }

    public static void launch(Activity a, int filter, int requestCode) {
        Intent start = new Intent(a, ChooseRegionActivity.class);
        start.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER, filter);
        a.startActivityForResult(start, requestCode);
    }

    public static void launch(Activity a, int requestCode, Bundle extras) {
        Intent intent = new Intent(a, ChooseRegionActivity.class);
        if (extras != null) {
            intent.putExtras(extras);
        }
        a.startActivityForResult(intent, requestCode);
    }

    public static void launch(Fragment f, int filter, int requestCode) {
        if (f == null || f.getActivity() == null) {
            return;
        }
        Intent intent = new Intent(f.getActivity(), ChooseRegionActivity.class);
        intent.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER, filter);
        f.startActivityForResult(intent, requestCode);
    }
}
