package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import lib.pulltorefresh.PullToRefreshExpandableListView;
import lib.pulltorefresh.extras.listfragment.PullToRefreshExpandableListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.AdjustHeightListView;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetSearchFreightList;
import com.epeisong.data.net.NetSearchUserList;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.Freight;
import com.epeisong.model.MarketMember;
import com.epeisong.model.User;
import com.epeisong.ui.activity.FreightDetailActivity;
import com.epeisong.ui.activity.FreightMarketDetailActivity;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 配货站列表，用于配货市场左边TAB
 * @see {FreightMarketDetailActivity }
 * @author poet
 *
 */
public class FreightStationListFragment extends PullToRefreshExpandableListFragment implements OnGroupClickListener,
        OnRefreshListener2<ExpandableListView>, OnScrollListener {

    private PullToRefreshExpandableListView mPullToRefreshExpandableListView;
    private ExpandableListView mExpandableListView;
    private MyAdapter mAdapter;

    private ImageView mTopView, mMergeView;

    private User mMarket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mMarket = (User) args.getSerializable(FreightMarketDetailActivity.EXTRA_MARKET);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPullToRefreshExpandableListView = getPullToRefreshListView();
        mPullToRefreshExpandableListView.setMode(Mode.BOTH);
        mPullToRefreshExpandableListView.setLoadingTextColor(Color.argb(0xff, 0xa, 0xa, 0xa));
        mPullToRefreshExpandableListView.setOnRefreshListener(this);
        mExpandableListView = mPullToRefreshExpandableListView.getRefreshableView();
        View bottom = new View(getActivity());
        int h = DimensionUtls.getPixelFromDpInt(50);
        bottom.setLayoutParams(new AbsListView.LayoutParams(-1, h));
        mExpandableListView.addFooterView(bottom, null, false);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setAdapter(mAdapter = new MyAdapter());
        mExpandableListView.setOnGroupClickListener(this);
        mExpandableListView.setOnGroupExpandListener(mAdapter);
        mExpandableListView.setBackgroundResource(R.color.page_bg);
        mExpandableListView.setOnScrollListener(this);

        setListShown(true);

        requestData(10, null);

        if (view instanceof FrameLayout) {
            mMergeView = new ImageView(getActivity());
            mMergeView.setImageResource(R.drawable.common_icon_merge_list);
            // mMergeView.setBackgroundColor(Color.argb(0x44, 0x00, 0x0, 0x0));
            mMergeView.setVisibility(View.GONE);
            mMergeView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.merge();
                    mMergeView.setVisibility(View.GONE);
                    View child = mExpandableListView.getChildAt(0);
                    if (child != null && child.getTop() <= 0) {
                        mTopView.setVisibility(View.GONE);
                    }
                }
            });

            mTopView = new ImageView(getActivity());
            mTopView.setImageResource(R.drawable.common_icon_go_top);
            // mTopView.setBackgroundColor(Color.argb(0x44, 0x00, 0x0, 0x0));
            mTopView.setVisibility(View.GONE);
            mTopView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExpandableListView.setSelection(0);
                    mTopView.setVisibility(View.GONE);
                }
            });

            LinearLayout ll = new LinearLayout(getActivity());
            ll.setOrientation(LinearLayout.VERTICAL);
            int w = DimensionUtls.getPixelFromDpInt(30);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, w);
            lp.leftMargin = lp.rightMargin = lp.bottomMargin = w / 3;
            ll.addView(mMergeView, lp);
            ll.addView(mTopView, lp);

            FrameLayout fl = (FrameLayout) view;
            int b = DimensionUtls.getPixelFromDpInt(10);
            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.RIGHT);
            p.rightMargin = b / 2;
            p.bottomMargin = b;
            fl.addView(ll, p);
        }
    }

    private void requestData(final int size, final String edgeId) {
        if (mMarket == null) {
            ToastUtils.showToast("Fragment未传入参数");
            return;
        }
        AsyncTask<Void, Void, List<MarketMember>> task = new AsyncTask<Void, Void, List<MarketMember>>() {
            @Override
            protected List<MarketMember> doInBackground(Void... params) {
                NetSearchUserList net = new NetSearchUserList() {
                    @Override
                    protected int getCommandCode() {
                        return CommandConstants.GET_MEMBERS_REQ;
                    }

                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setMarketId(Integer.parseInt(mMarket.getId()));
                        req.setLimitCount(size);
                        if (edgeId != null) {
                            req.setId(Integer.parseInt(edgeId));
                        }
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        return UserParser.parseMember(resp);
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<MarketMember> result) {
                mPullToRefreshExpandableListView.onRefreshComplete();
                if (result != null) {
                    if (edgeId == null) {
                        mAdapter.replaceAll(result);
                    } else {
                        mAdapter.addAll(result);
                    }
                }
            }
        };
        task.execute();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
        requestData(10, null);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
        String edgeId = null;
        if (!mAdapter.isEmpty()) {
            edgeId = mAdapter.getGroup(mAdapter.getGroupCount() - 1).getId();
        }
        requestData(10, edgeId);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        mAdapter.expandGroup(groupPosition);
        return true;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            View child = mExpandableListView.getChildAt(0);
            if (child != null && child.getTop() < -100) {
                mTopView.setVisibility(View.VISIBLE);
            } else {
                mTopView.setVisibility(View.GONE);
            }
        }
    }

    private class MyAdapter extends BaseExpandableListAdapter implements OnGroupExpandListener, OnLoadMoreListener,
            OnItemClickListener {

        private List<MarketMember> stationList;
        private int curGroup = -1;
        private boolean mIsLoading;
        private TextView mLoadingTv;

        private ListView mListView;
        private EndlessAdapter mEndlessAdapter;
        private ChildrenListAdapter mChildrenListAdapter;

        public MyAdapter() {
            stationList = new ArrayList<MarketMember>();

            mLoadingTv = new TextView(getActivity());
            mLoadingTv.setGravity(Gravity.CENTER);
            mLoadingTv.setText("加载中...");
            mLoadingTv.setTextColor(Color.argb(0xff, 0xa, 0xa, 0xa));
            mLoadingTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            int p = DimensionUtls.getPixelFromDpInt(10);
            mLoadingTv.setPadding(p, p, p, p);

            mListView = new AdjustHeightListView(getActivity());
            mEndlessAdapter = new EndlessAdapter(getActivity(), mChildrenListAdapter = new ChildrenListAdapter());
            mEndlessAdapter.setIsAutoLoad(false);
            mEndlessAdapter.setOnLoadMoreListener(this);
            mListView.setAdapter(mEndlessAdapter);
            mListView.setOnItemClickListener(this);
        }

        public void replaceAll(List<MarketMember> users) {
            stationList.clear();
            if (users != null && !users.isEmpty()) {
                stationList.addAll(users);
            }
            notifyDataSetChanged();
        }

        public void addAll(List<MarketMember> users) {
            if (users != null && !users.isEmpty()) {
                stationList.addAll(users);
                notifyDataSetChanged();
            }
        }

        public void merge() {
            if (curGroup >= 0) {
                mIsLoading = false;
                mExpandableListView.collapseGroup(curGroup);
                mChildrenListAdapter.clear();
                curGroup = -1;
                mAdapter.notifyDataSetChanged();
            }
        }

        public void expandGroup(int groupPos) {
            boolean ex = true;
            if (curGroup >= 0) {
                mExpandableListView.collapseGroup(curGroup);
                mChildrenListAdapter.clear();
            }
            if (curGroup != groupPos) {
                mExpandableListView.expandGroup(groupPos);
                curGroup = groupPos;
            } else {
                curGroup = -1;
                ex = false;
            }
            if (ex) {
                mMergeView.setVisibility(View.VISIBLE);
            } else {
                mMergeView.setVisibility(View.GONE);
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onGroupExpand(int groupPosition) {
            mIsLoading = false;
            loadChilren(groupPosition, getGroup(groupPosition).getUser().getId(), 10, null);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Freight f = mChildrenListAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), FreightDetailActivity.class);
            intent.putExtra(FreightDetailActivity.EXTRA_FREIGHT, f);
            BusinessChatModel model = BusinessChatModel.getFromFreight(f);
            intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
            intent.putExtra(FreightDetailActivity.EXTRA_CAN_DELETE, false);
            startActivity(intent);
        }

        @Override
        public void onStartLoadMore(EndlessAdapter adapter) {
            String edgeId = null;
            if (!mChildrenListAdapter.isEmpty()) {
                edgeId = mChildrenListAdapter.getItem(mChildrenListAdapter.getCount() - 1).getId();
            }
            loadChilren(curGroup, getGroup(curGroup).getId(), 10, edgeId);
        }

        private void loadChilren(final int groupPos, final String userId, final int size, final String edgeId) {
            if (edgeId == null) {
                // ((XBaseActivity) getActivity()).showPendingDialog(null);
                mIsLoading = true;
                notifyDataSetChanged();
            }
            AsyncTask<Void, Void, List<Freight>> task = new AsyncTask<Void, Void, List<Freight>>() {
                @Override
                protected List<Freight> doInBackground(Void... params) {
                    NetSearchFreightList net = new NetSearchFreightList() {
                        @Override
                        protected boolean onSetRequest(FreightReq.Builder req) {
                            req.setLogisticId(Integer.parseInt(userId));
                            req.setLimitCount(size);
                            req.setFreightType(Properties.FREIGHT_TYPE_ALL);
                            if (edgeId != null) {
                                req.setId(Integer.parseInt(edgeId));
                            }
                            return true;
                        }
                    };
                    try {
                        FreightResp.Builder resp = net.request();
                        if (net.isSuccess(resp)) {
                            return FreightParser.parse(resp);
                        }
                    } catch (NetGetException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(List<Freight> result) {
                    if (edgeId == null) {
                        // ((XBaseActivity)
                        // getActivity()).dismissPendingDialog();
                        mIsLoading = false;
                        notifyDataSetChanged();
                    }
                    if (curGroup == groupPos) {
                        if (result != null) {
                            mEndlessAdapter.endLoad(true);
                            mEndlessAdapter.setHasMore(result.size() >= size);
                            mChildrenListAdapter.addAll(result);
                            if (result.isEmpty()) {
                                ToastUtils.showToast("没有数据");
                            }
                        } else {
                            mEndlessAdapter.endLoad(false);
                        }
                    }
                }
            };
            task.execute();
        }

        @Override
        public int getGroupCount() {
            return stationList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public MarketMember getGroup(int groupPosition) {
            return stationList.get(groupPosition);
        }

        @Override
        public Freight getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder = null;
            if (convertView == null) {
                holder = new GroupViewHolder();
                convertView = holder.createView();
                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }
            holder.fillData(getGroup(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {
            if (mIsLoading) {
                return mLoadingTv;
            }
            return mListView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    class GroupViewHolder {
        TextView tv_name;
        TextView tv_region;

        public View createView() {
            int p = DimensionUtls.getPixelFromDpInt(10);
            LinearLayout ll = new LinearLayout(getActivity());
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setBackgroundColor(Color.WHITE);
            ll.setPadding(p * 2, p, p, p);
            tv_name = new TextView(getActivity());
            tv_name.setTextColor(Color.BLACK);
            tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            ll.addView(tv_name);
            tv_region = new TextView(getActivity());
            tv_region.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            tv_region.setTextColor(Color.GRAY);
            ll.addView(tv_region);
            return ll;
        }

        public void fillData(MarketMember memeber) {
            tv_name.setText(memeber.getUser().getShow_name());
            String region = memeber.getUser().getRegion() + memeber.getUser().getAddress();
            tv_region.setText(region);
        }
    }

    class ChildrenListAdapter extends HoldDataBaseAdapter<Freight> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListItemViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.activity_freight_of_contacts_item);
                holder = new ListItemViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
                int p = DimensionUtls.getPixelFromDpInt(10);
                convertView.setPadding(p, 0, 0, 0);
            } else {
                holder = (ListItemViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position));
            return convertView;
        }
    }

    class ListItemViewHolder {
        LinearLayout ll_parent;
        ImageView iv_freight_type;
        TextView tv_start_region;
        TextView tv_end_region;
        TextView tv_name;
        TextView tv_time;
        TextView tv_desc;
        TextView iv_arrow;

        public void findView(View v) {
            ll_parent = (LinearLayout) v.findViewById(R.id.ll_parent);
            iv_freight_type = (ImageView) v.findViewById(R.id.iv_freight_type);
            tv_start_region = (TextView) v.findViewById(R.id.tv_start_region);
            tv_end_region = (TextView) v.findViewById(R.id.tv_end_region);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_time = (TextView) v.findViewById(R.id.tv_time);
            tv_desc = (TextView) v.findViewById(R.id.tv_freight_desc);
            iv_arrow = (TextView) v.findViewById(R.id.iv_arrow);
            v.findViewById(R.id.line).setVisibility(View.GONE);
            int b = DimensionUtls.getPixelFromDpInt(10);
            v.setPadding(0, 0, 0, b);
        }

        public void fillData(Freight f) {
            ll_parent.setBackgroundResource(0);
            tv_start_region.setTextColor(getResources().getColor(R.color.black));
            tv_end_region.setTextColor(getResources().getColor(R.color.black));
            tv_name.setTextColor(getResources().getColor(R.color.black));
            tv_time.setTextColor(getResources().getColor(R.color.content_gray));
            tv_desc.setTextColor(getResources().getColor(R.color.content_gray));
            iv_arrow.setTextColor(getResources().getColor(R.color.black));
            int status = f.getStatus();
            if (status == Properties.FREIGHT_STATUS_NO_PROCESSED) {
                if (f.getType() == Freight.TYPE_GOODS) {
                    iv_freight_type.setImageResource(R.drawable.black_board_goods);
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                    iv_freight_type.setImageResource(R.drawable.black_board_truck);
                }
            } else if (status == Properties.FREIGHT_STATUS_BOOK) {
                if (f.getType() == Freight.TYPE_GOODS) {
                    iv_freight_type.setImageResource(R.drawable.bload_booked_goods);
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                    iv_freight_type.setImageResource(R.drawable.bload_booked_truck);
                }
            } else if (status == Properties.FREIGHT_STATUS_COMPLETED) {
                ll_parent.setBackgroundResource(R.color.qian_gray);
                tv_start_region.setTextColor(getResources().getColor(R.color.text_gray2));
                tv_end_region.setTextColor(getResources().getColor(R.color.text_gray2));
                tv_name.setTextColor(getResources().getColor(R.color.text_gray2));
                tv_time.setTextColor(getResources().getColor(R.color.text_gray2));
                tv_desc.setTextColor(getResources().getColor(R.color.text_gray2));
                iv_arrow.setTextColor(getResources().getColor(R.color.text_gray2));
                if (f.getType() == Freight.TYPE_GOODS) {
                    // 货源已过期
                    iv_freight_type.setImageResource(R.drawable.black_board_goods2);
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                    // 车源已过期
                    iv_freight_type.setImageResource(R.drawable.black_board_truck2);
                }
            }
            tv_start_region.setText(f.getStart_region());
            tv_end_region.setText(f.getEnd_region());
            tv_name.setText(f.getOwner_name());
            tv_time.setText(DateUtil.long2vague(f.getUpdate_time()));
            tv_desc.setText(f.getDesc());
            // iv_advisory.setTag(f);
        }
    }
}
