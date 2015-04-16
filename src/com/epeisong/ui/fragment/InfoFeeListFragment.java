package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import lib.pulltorefresh.PullToRefreshListView;
import lib.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.layer02.InfoFeeManager;
import com.epeisong.data.layer02.InfoFeeManager.InfoFeeObserver;
import com.epeisong.data.utils.infofee.InfoFeeStatusUtils;
import com.epeisong.data.utils.infofee.InfoFeeStatusUtils.InfoFeeStatusParams;
import com.epeisong.data.utils.infofee.InfoFeeStatusUtils.InfoFeeStatusResult;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.InfoFee;
import com.epeisong.model.User;
import com.epeisong.ui.activity.InfoFeeDetailActivity;
import com.epeisong.ui.activity.InfoFeeListActivity;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 信息费订单列表
 * @author poet
 *
 */
public class InfoFeeListFragment extends PullToRefreshListFragment implements OnRefreshListener2<ListView>,
        OnItemClickListener, InfoFeeObserver {

    public static final String ARGS_ORDER_TYPE = "order_type";

    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private MyAdapter mAdapter;
    private User mUserSelf;

    private TextView mTextViewEmpty;

    private int mInfoFeeState = -1;

    private boolean mLoaded;

    private List<AsyncTask<?, ?, ?>> mTasks;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mInfoFeeState = args.getInt(ARGS_ORDER_TYPE);
        }
        if (mInfoFeeState == -1) {
            ToastUtils.showToast("参数传递错误");
        }
        mTasks = new ArrayList<AsyncTask<?, ?, ?>>();
        mUserSelf = UserDao.getInstance().getUser();
        mPullToRefreshListView = getPullToRefreshListView();
        mPullToRefreshListView.setMode(Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(this);
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.setAdapter(mAdapter = new MyAdapter());
        mListView.setOnItemClickListener(this);

        setEmptyView();

        InfoFeeManager.addObserver(this);
    }

    @Override
    public void onInfoFeeChange(InfoFee infoFee) {
        boolean statusEqual = infoFee.getStatus() == mInfoFeeState;
        if (!statusEqual && mInfoFeeState == Properties.INFO_FEE_STATUS_EXECUTE) {
            ((InfoFeeListActivity) getActivity()).onUiChange(infoFee.getStatus());
        }

        List<InfoFee> list = mAdapter.getAllItem();
        if (list.isEmpty()) {
            if (infoFee.getStatus() == mInfoFeeState) {
                mAdapter.addItem(infoFee);
            }
        } else {
            if (list.contains(infoFee)) {
                if (statusEqual) {
                    int index = list.indexOf(infoFee);
                    mAdapter.removeItem(infoFee);
                    mAdapter.addItem(index, infoFee);
                } else {
                    mAdapter.removeItem(infoFee);
                }
            } else {
                if (statusEqual && infoFee.getCreateDate() > mAdapter.getItem(0).getCreateDate()) {
                    mAdapter.addItem(0, infoFee);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        for (AsyncTask<?, ?, ?> task : mTasks) {
            task.cancel(true);
        }
        InfoFeeManager.removeObserver(this);
        super.onDestroyView();
    }

    private void setEmptyView() {
        LinearLayout emptyLayout = new LinearLayout(getActivity());
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyLayout.setPadding(0, DimensionUtls.getPixelFromDpInt(100), 0, 0);
        ImageView iv = new ImageView(getActivity());
        iv.setImageResource(R.drawable.nopeihuo);
        emptyLayout.addView(iv);
        mTextViewEmpty = new TextView(getActivity());
        mTextViewEmpty.setText("没有订单");
        mTextViewEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mTextViewEmpty.setTextColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
        mTextViewEmpty.setGravity(Gravity.CENTER);
        emptyLayout.addView(mTextViewEmpty);
        mPullToRefreshListView.setEmptyView(emptyLayout);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isAdded() && !mLoaded) {
            mPullToRefreshListView.setRefreshing();
            mLoaded = true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= mListView.getHeaderViewsCount();
        InfoFee infoFee = mAdapter.getItem(position);
        Intent i = new Intent(getActivity(), InfoFeeDetailActivity.class);
        BusinessChatModel model = BusinessChatModel.getFromInfoFee(infoFee);
        i.putExtra(InfoFeeDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
        i.putExtra(InfoFeeDetailActivity.EXTRA_INFO_FEE, infoFee);
        startActivity(i);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        requestData(0, 10);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        int lastSyncIndex = 0;
        if (!mAdapter.isEmpty()) {
            lastSyncIndex = mAdapter.getItem(mAdapter.getCount() - 1).getSyncIndex();
        }
        if (lastSyncIndex <= 1) {
            HandlerUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPullToRefreshListView.onRefreshComplete();
                }
            }, 100);
            return;
        }
        requestData(lastSyncIndex, 10);
    }

    private void requestData(final int lastSyncIndex, final int size) {
        AsyncTask<Void, Void, List<InfoFee>> task = new AsyncTask<Void, Void, List<InfoFee>>() {
            @Override
            protected List<InfoFee> doInBackground(Void... params) {
                mTasks.add(this);
                // InfoFeeProvider_old infoFeeProvider = new
                // InfoFeeProvider_old();
                // if (lastSyncIndex > 0) {
                // return
                // infoFeeProvider.getInfoFeeList(Integer.parseInt(mUserSelf.getId()),
                // lastSyncIndex, size,
                // mInfoFeeState);
                // }
                // int lastSyncIndex = 0;
                // InfoFee infoFee =
                // InfoFeeDao.getInstance().queryNewestBySyncIndex(mInfoFeeState);
                // if (infoFee != null) {
                // lastSyncIndex = infoFee.getSyncIndex();
                // }
                // return
                // infoFeeProvider.getNewestInfoFeeList(Integer.parseInt(mUserSelf.getId()),
                // lastSyncIndex, size,
                // mInfoFeeState);
                if (lastSyncIndex > 0) {
                    return new InfoFeeManager().queryOlder(size, mInfoFeeState, lastSyncIndex);
                } else {
                    return new InfoFeeManager().queryFirst(size, mInfoFeeState);
                }
            }

            @Override
            protected void onPostExecute(List<InfoFee> result) {
                mTasks.remove(this);
                mPullToRefreshListView.onRefreshComplete();
                setListShown(true);
                if (result != null && result.size() > 0) {
                    if (lastSyncIndex > 0) {
                        mAdapter.addAll(result);
                    } else {
                        mAdapter.replaceAll(result);
                    }
                }
            }
        };
        task.execute();
    }

    private class MyAdapter extends HoldDataBaseAdapter<InfoFee> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.fragment_infofeelist_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            InfoFee infoFee = getItem(position);
            holder.fillData(infoFee);
            return convertView;
        }
    }

    private class ViewHolder {

        ImageView iv_icon;
        TextView tv_region;
        TextView tv_time;
        TextView tv_desc;
        TextView tv_source;
        TextView tv_status;

        public void findView(View v) {
            iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
            tv_region = (TextView) v.findViewById(R.id.tv_region);
            tv_time = (TextView) v.findViewById(R.id.tv_time);
            tv_desc = (TextView) v.findViewById(R.id.tv_desc);
            tv_source = (TextView) v.findViewById(R.id.tv_source);
            tv_status = (TextView) v.findViewById(R.id.tv_status);
        }

        public void fillData(InfoFee infoFee) {
            if (infoFee.getType() == Properties.INFO_FEE_TYPE_GOODS) {
                iv_icon.setImageResource(R.drawable.black_board_goods);
            } else if (infoFee.getType() == Properties.INFO_FEE_TYPE_VEHICLE) {
                iv_icon.setImageResource(R.drawable.black_board_truck);
            }
            tv_region.setText(infoFee.getFreightAddr());
            tv_time.setText(DateUtil.long2vaguehourMinute(infoFee.getCreateDate()));
            tv_desc.setText(infoFee.getFreightInfo());

            InfoFeeStatusResult result = InfoFeeStatusUtils.getResult(new InfoFeeStatusParams(mUserSelf.getId(),
                    infoFee));
            tv_source.setText(result.source + "：" + result.sourceName);
            tv_status.setText(result.getStatus());
            if (infoFee.getLocalStatus() == InfoFee.UNREAD) {
                Drawable start = getResources().getDrawable(R.drawable.main_red_point_17);
                int b = DimensionUtls.getPixelFromDpInt(18);
                start.setBounds(0, 0, b, b);
                tv_status.setCompoundDrawables(start, null, null, null);
            } else {
                tv_status.setCompoundDrawables(null, null, null, null);
            }
        }
    }

    public interface changeUiByStatus { // 根据状态变化，修改菜单红点
        void onUiChange(int orderStatus);
    }
}
