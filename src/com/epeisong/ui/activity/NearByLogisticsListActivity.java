package com.epeisong.ui.activity;

import java.io.Serializable;
import java.text.DecimalFormat;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import lib.pulltorefresh.PullToRefreshListView;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.bdmap.impl.NearByLogisticsMapActivity;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.model.NearByLogisticsDataHolder;
import com.epeisong.model.NearByLogisticsDataHolder.ChooseTabViewHolder;
import com.epeisong.model.NearByLogisticsDataHolder.PoiTitleViewHolder;
import com.epeisong.model.User;
import com.epeisong.ui.activity.VerticalFilterActivity.ViewHolder;
import com.epeisong.ui.view.ChooseLineSmallLayout;
import com.epeisong.ui.view.ChooseLogisticsTypeLayout;
import com.epeisong.ui.view.ChooseLogisticsTypeLayout.LogisticsType;
import com.epeisong.ui.view.ChooseLogisticsTypeLayout.OnChooseLogisticsTypeListener;
import com.epeisong.ui.view.ChooseTabLayout;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

public class NearByLogisticsListActivity extends BaseActivity implements OnRefreshListener2<ListView>,
        OnItemClickListener, OnChooseLogisticsTypeListener {

    final int OP_REFRESH = 1;
    final int OP_LOAD_MORE = 2;

    final int SIZE_REFRESH = 10;
    final int SIZE_LOAD_MORE = 10;

    EditText mPoiTitleEt;
    ListView mPoiTitleLv;

    ChooseTabLayout mChooseTabLayout;
    ChooseLogisticsTypeLayout mChooseLogisticsTypeLayout;
    ChooseLineSmallLayout mChooseLineSmallLayout;

    PullToRefreshListView mPullToRefreshListView;
    ListView mListView;
    VerticalFilterActivity.MyAdapter mAdapter;

    NearByLogisticsDataHolder mDataHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDataHolder = (NearByLogisticsDataHolder) getIntent().getSerializableExtra(
                NearByLogisticsMapActivity.EXTRA_DATA_HOLDER);
        if (mDataHolder == null) {
            mDataHolder = new NearByLogisticsDataHolder();
        }
        super.onCreate(savedInstanceState);
        ChooseTabViewHolder viewHolder = NearByLogisticsDataHolder.viewCreateChooseTab(this, mDataHolder);
        mChooseTabLayout = viewHolder.chooseTabLayout;
        mChooseLogisticsTypeLayout = viewHolder.chooseLogisticsTypeLayout;
        mChooseLineSmallLayout = viewHolder.chooseLineSmallLayout;

        setContentView(mChooseTabLayout, new ViewGroup.LayoutParams(-1, DimensionUtls.getPixelFromDpInt(45)));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -1);
        params.topMargin = DimensionUtls.getPixelFromDpInt(90) + 1;
        addContentViewSuper(mChooseLogisticsTypeLayout, params);
        addContentViewSuper(mChooseLineSmallLayout, params);

        mPullToRefreshListView = new PullToRefreshListView(this);
        setContentView(mPullToRefreshListView);

        mPullToRefreshListView.setOnRefreshListener(this);
        mPullToRefreshListView.setMode(Mode.DISABLED);
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.setAdapter(mAdapter = new VerticalFilterActivity.MyAdapter() {
            @Override
            void customFillData(User user, ViewHolder holder) {
                holder.tv_distance.setVisibility(View.VISIBLE);
                if (mDataHolder.distanceAnchorMap != null && mDataHolder.distanceAnchorMap.containsKey(user)) {
                    double distance = mDataHolder.distanceAnchorMap.get(user);
                    String text;
                    if (distance < 1000) {
                        text = (int) distance + "m";
                    } else {
                        DecimalFormat df = new java.text.DecimalFormat("#.0");
                        text = df.format(distance / 1000) + "km";
                    }
                    holder.tv_distance.setText(text);
                } else {
                    holder.tv_distance.setText("未知");
                }
            }
        });
        mListView.setOnItemClickListener(this);

        if (mDataHolder.searchLoc == null) {
            NearByLogisticsDataHolder.dataRequestData(this, mDataHolder);
        } else if (mDataHolder.userList != null) {
            mAdapter.replaceAll(mDataHolder.userList);
        }

        PoiTitleViewHolder poiTitle = NearByLogisticsDataHolder.viewCustomPoiTitle(this, mDataHolder);
        mPoiTitleEt = poiTitle.et;
        mPoiTitleLv = poiTitle.lv;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (NearByLogisticsDataHolder.viewDispatchTouchEvent(this, ev, mPoiTitleEt, mPoiTitleLv)) {
                return super.dispatchTouchEvent(ev);
            }
            if (cancelPoiTitle()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (mChooseLogisticsTypeLayout.getVisibility() == View.VISIBLE) {
            mChooseLogisticsTypeLayout.setVisibility(View.GONE);
            mChooseTabLayout.cancelAll();
            return;
        }
        if (mChooseLineSmallLayout.getVisibility() == View.VISIBLE) {
            mChooseLineSmallLayout.setVisibility(View.GONE);
            mChooseTabLayout.cancelAll();
            return;
        }
        if (cancelPoiTitle()) {
            return;
        }
        gotoMapAndFinish();
        // super.onBackPressed();
    }

    boolean cancelPoiTitle() {
        if (mPoiTitleEt != null && mPoiTitleEt.getVisibility() == View.VISIBLE) {
            SystemUtils.hideInputMethod(mPoiTitleEt);
            mPoiTitleEt.setVisibility(View.GONE);
            getTitleContainer().getChildAt(0).setVisibility(View.VISIBLE);
            if (mPoiTitleLv != null && mPoiTitleLv.getVisibility() == View.VISIBLE) {
                mPoiTitleLv.setVisibility(View.GONE);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onChoosedLogisticsType(LogisticsType type) {
        mChooseTabLayout.cancelAll();
        mChooseLogisticsTypeLayout.setVisibility(View.GONE);
        if (type != null) {
            mChooseTabLayout.setTabText(0, type.name);
            mDataHolder.logisticsType = type;
            NearByLogisticsDataHolder.dataRequestData(this, mDataHolder);
        }
    }

    void setTitleByCity() {
        setTitleText(NearByLogisticsDataHolder.dataTitleText(mDataHolder));
    }

    @Override
    protected TitleParams getTitleParams() {
        Action action = new ActionImpl() {
            @Override
            public View getView() {
                ImageView iv = new ImageView(getApplicationContext());
                iv.setImageResource(R.drawable.icon_nearby_map);
                return iv;
            }

            @Override
            public void doAction(View v) {
                gotoMapAndFinish();
            }
        };
        String title = "";
        if (mDataHolder != null && mDataHolder.anchorLoc != null) {
            title = mDataHolder.anchorLoc.getAddressName();
        }
        return new TitleParams(getDefaultHomeAction(), title).setAction(action);
    }

    void gotoMapAndFinish() {
        Intent intent = new Intent(getApplicationContext(), NearByLogisticsMapActivity.class);
        intent.putExtra(NearByLogisticsMapActivity.EXTRA_DATA_HOLDER, mDataHolder);
        startActivity(intent);
        finish();
    }

    private void requestData() {
        if (mDataHolder.searchLoc == null) {
            return;
        }
        NearByLogisticsDataHolder.dataRequestData(this, mDataHolder);
    }

    @Override
    public void onPostData(Serializable seri) {
        if (seri != null && seri instanceof Integer) {
            int type = (Integer) seri;
            switch (type) {
            case NearByLogisticsDataHolder.POST_DATA_TYPE_LOCATION:
                mDataHolder.searchLoc = mDataHolder.anchorLoc;
                if (mDataHolder.anchorLoc == null) {
                    ToastUtils.showToast("定位失败");
                } else {
                    setTitleText(mDataHolder.anchorLoc.getCityName() + mDataHolder.anchorLoc.getAddressName());
                    NearByLogisticsDataHolder.dataRequestData(this, mDataHolder);
                }
                break;
            case NearByLogisticsDataHolder.POST_DATA_TYPE_USER_LIST:
                if (mDataHolder.userList == null) {
                    ToastUtils.showToast("请求失败");
                } else {
                    mAdapter.replaceAll(mDataHolder.userList);
                }
                break;
            case NearByLogisticsDataHolder.POST_DATA_POI_SUCCESS:
                cancelPoiTitle();
                setTitleByCity();
                requestData();
                break;
            case NearByLogisticsDataHolder.POST_DATA_CHOOSED_LOGISTICS_TYPE:
            case NearByLogisticsDataHolder.POST_DATA_CHOOSED_LINE:
                requestData();
                break;
            }
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= mListView.getHeaderViewsCount();
        User u = mAdapter.getItem(position);
        Intent intent = new Intent(this, ContactsDetailActivity.class);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER, u);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, u.getId());
        intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, u.getUser_type_code());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mChooseLineSmallLayout.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }
}
