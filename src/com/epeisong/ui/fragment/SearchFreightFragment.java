package com.epeisong.ui.fragment;

import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshListView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetSearchFreightFromMarket;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.Freight;
import com.epeisong.model.RegionResult;
import com.epeisong.ui.activity.FreightDetailActivity;
import com.epeisong.ui.adapter.FreightListAdapter;
import com.epeisong.ui.view.ChooseFreightTypeLayout;
import com.epeisong.ui.view.ChooseFreightTypeLayout.OnChooseFreightTypeListener;
import com.epeisong.ui.view.ChooseLineLayout;
import com.epeisong.ui.view.ChooseLineLayout.OnChooseLineListener;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

public class SearchFreightFragment extends Fragment implements OnClickListener, OnChooseLineListener,
        OnChooseFreightTypeListener, OnItemClickListener {

    public static final String EXTRA_REGION_CODE = "region_code";

    private final int OP_REFRESH = 0; // 第一次加载，刷新
    private final int OP_LOAD_MORE = 1; // 加载更多
    private final int OP_LOAD_NEWER = 2;// 定时取最新数据

    private final int SIZE_REFRESH = 10;
    private final int SIZE_LOAD_MORE = 10;
    private final int SIZE_LOAD_NEWER = 10;

    private static final long DURTION = 1000 * 15;

    private TextView mFreightTypeTv;
    private TextView mLineTv;

    private ChooseFreightTypeLayout mChooseFreightTypeLayout;
    private ChooseLineLayout mChooseLineLayout;
    private View mChooseContainer;

    private RegionResult mStartRegion;
    private RegionResult mEndRegion;
    private int mStartCode = 0;
    private int mEndCode = 0;
    private int mFreightType = Properties.FREIGHT_TYPE_ALL;

    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private FreightListAdapter mAdapter;

    private boolean mIsTop = true;
    private boolean mIsScreenOn = true;

    private MyThread mThread;

    private int mRegioncodeDefault;
    private int mRegionCode;
    private int mMarketId; // 每次刷新数据，id改为0；自动线程，判断没有id，执行初始操作（确定当前没有其他操作）

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        mRegioncodeDefault = mRegionCode = args.getInt(EXTRA_REGION_CODE);

        View view = SystemUtils.inflate(R.layout.activity_info_screen);
        mFreightTypeTv = (TextView) view.findViewById(R.id.tv_choosable_all);
        mLineTv = (TextView) view.findViewById(R.id.tv_choosable_line);
        mFreightTypeTv.setOnClickListener(this);
        mLineTv.setOnClickListener(this);
        mChooseContainer = view.findViewById(R.id.fl_choose_container0);
        mChooseContainer.setOnClickListener(this);
        mChooseFreightTypeLayout = (ChooseFreightTypeLayout) view.findViewById(R.id.choose_freight_type_layout0);
        mChooseFreightTypeLayout.setOnChooseFreightTypeListener(this);
        mChooseLineLayout = (ChooseLineLayout) view.findViewById(R.id.choose_line_layout0);
        mChooseLineLayout.setFragment(this);
        mChooseLineLayout.setOnChooseLineListener(this);

        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv);
        mListView = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setAdapter(mAdapter = new FreightListAdapter());
        mPullToRefreshListView.setMode(Mode.BOTH);
        mPullToRefreshListView.setOnItemClickListener(this);
        // 视图滚动监听
        mPullToRefreshListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 视图已经停止滑动
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    if (mListView.getFirstVisiblePosition() == 0) {
                        mIsTop = true;
                    } else {
                        mIsTop = false;
                    }
                }
            }
        });
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                requestData(OP_REFRESH);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mAdapter.isEmpty()) {
                    return;
                } else {
                    requestData(OP_LOAD_MORE);
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPullToRefreshListView.setRefreshing();

        mThread = new MyThread();
        mThread.start();
    }

    private synchronized void requestData(final int op) {
        AsyncTask<Void, Void, CommonLogisticsResp.Builder> task = new AsyncTask<Void, Void, CommonLogisticsResp.Builder>() {
            @Override
            protected CommonLogisticsResp.Builder doInBackground(Void... params) {
                NetSearchFreightFromMarket net = new NetSearchFreightFromMarket() {

                    @Override
                    protected int getCommandCode() {
                        if (op == OP_LOAD_MORE) {
                            return CommandConstants.LIST_OLDER_FREIGHTS_ADJOIN_LOCAL_FROM_MARKET_SCREEN_REQ;
                        } else {
                            return CommandConstants.LIST_LATEST_FREIGHTS_ON_MARKET_SCREEN_BY_EPS_REQ;
                        }
                    }

                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setCurrentRegionCode(mRegionCode);
                        if (mMarketId > 0) {
                            req.setMarketId(mMarketId);
                        }
                        int size = 10;
                        long createTime = 0;
                        int idOnScreen = 0;
                        if (op == OP_REFRESH) {
                            size = SIZE_REFRESH;
                        } else if (op == OP_LOAD_MORE) {
                            if (mAdapter.isEmpty()) {
                                return false;
                            }
                            size = SIZE_LOAD_MORE;
                            Freight last = mAdapter.getItem(mAdapter.getCount() - 1);
                            createTime = last.getCreate_time();
                            idOnScreen = last.getMarket_screen_freight_id();
                        } else if (op == OP_LOAD_NEWER) {
                            size = SIZE_LOAD_NEWER;
                            if (!mAdapter.isEmpty()) {
                                Freight first = mAdapter.getItem(0);
                                createTime = first.getCreate_time();
                                idOnScreen = first.getMarket_screen_freight_id();
                            }
                        }
                        if (createTime > 0 && idOnScreen > 0) {
                            req.setCreateDate(createTime);
                            req.setId(idOnScreen);
                        }
                        req.setLimitCount(size);
                        req.setFreightType(mFreightType);
                        if (mStartCode > 0 && mEndCode > 0) {
                            req.setRouteCodeA(mStartCode);
                            req.setRouteCodeB(mEndCode);
                        }
                        return true;
                    }
                };
                try {
                    return net.request();
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonLogisticsResp.Builder resp) {
                mPullToRefreshListView.onRefreshComplete();
                if (resp == null || !(Constants.SUCC.equals(resp.getResult()))) {
                    return;
                }
                List<Freight> list = FreightParser.parse(resp);
                switch (op) {
                case OP_LOAD_NEWER:
                    mAdapter.addAll(0, list);
                    break;
                case OP_REFRESH:
                    mAdapter.replaceAll(list);
                    if (mAdapter.getCount() > 0) {
                        mListView.setSelection(0);
                        mMarketId = resp.getMarketScreenList().get(0).getMarketId();
                    }
                    break;
                case OP_LOAD_MORE:
                    if (list.isEmpty()) {
                        ToastUtils.showToast("没有更多~");
                    } else {
                        mAdapter.addAll(list);
                    }
                    break;
                }
            }
        };
        task.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_choosable_all:
            if (mChooseFreightTypeLayout.getVisibility() == View.GONE) {
                showChooseFreightType();
            } else {
                hideChooseFreightType();
            }
            break;

        case R.id.tv_choosable_line:
            if (mChooseLineLayout.getVisibility() == View.GONE) {
                showChooseLine();
            } else {
                hideChooseLine();
            }
            break;

        case R.id.fl_choose_container0:
            mChooseContainer.setVisibility(View.GONE);
            hideChooseFreightType();
            hideChooseLine();
            break;
        }

    }

    public boolean onBackPressed() {
        if (mChooseContainer.getVisibility() == View.VISIBLE) {
            mChooseContainer.setVisibility(View.GONE);
            hideChooseFreightType();
            hideChooseLine();
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= mPullToRefreshListView.getRefreshableView().getHeaderViewsCount();
        Freight f = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), FreightDetailActivity.class);
        intent.putExtra(FreightDetailActivity.EXTRA_FREIGHT, f);
        BusinessChatModel model = BusinessChatModel.getFromFreight(f);
        intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
        intent.putExtra(FreightDetailActivity.EXTRA_CAN_DELETE, false);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        if (mThread != null) {
            mThread.shutDown();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mChooseLineLayout.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    public void onChoosedFreightType(String name, int type, boolean change) {
        if (type != -1) {
            mFreightTypeTv.setText(name);
            if (type == OnChooseFreightTypeListener.FREIGHT_TYPE_ALL) {
                mFreightType = Properties.FREIGHT_TYPE_ALL;
            } else if (type == OnChooseFreightTypeListener.FREIGHT_TYPE_GOODS) {
                mFreightType = Properties.FREIGHT_TYPE_GOODS;
            } else if (type == OnChooseFreightTypeListener.FREIGHT_TYPE_TRUCK) {
                mFreightType = Properties.FREIGHT_TYPE_VEHICLE;
            }
        }
        requestData(OP_REFRESH);
        hideChooseFreightType();
    }

    @Override
    public void onChoosedLine(RegionResult start, RegionResult end) {
        if (start != null && end != null) {
            mLineTv.setText(start.getShortNameFromDistrict() + "-" + end.getShortNameFromDistrict());
            mStartRegion = start;
            mEndRegion = end;
            mStartCode = mStartRegion.getCode();
            mEndCode = mEndRegion.getCode();

            mRegionCode = mStartCode;
        } else {
            mStartRegion = null;
            mEndRegion = null;
            mStartCode = 0;
            mEndCode = 0;
            mLineTv.setText("线路不限");

            mRegionCode = mRegioncodeDefault;
        }
        mAdapter.clear();
        requestData(OP_REFRESH);
        hideChooseLine();
    }

    private void hideChooseFreightType() {
        mChooseContainer.setVisibility(View.GONE);
        mChooseFreightTypeLayout.setVisibility(View.GONE);
        mFreightTypeTv.setSelected(false);
    }

    private void hideChooseLine() {
        mChooseContainer.setVisibility(View.GONE);
        mChooseLineLayout.setVisibility(View.GONE);
        mLineTv.setSelected(false);
    }

    private void showChooseFreightType() {
        hideChooseLine();
        mChooseContainer.setVisibility(View.VISIBLE);
        mChooseFreightTypeLayout.setVisibility(View.VISIBLE);
        mFreightTypeTv.setSelected(true);
    }

    private void showChooseLine() {
        hideChooseFreightType();
        mChooseContainer.setVisibility(View.VISIBLE);
        mChooseLineLayout.setVisibility(View.VISIBLE);
        mLineTv.setSelected(true);
    }

    // private class MyAdapter extends HoldDataBaseAdapter<Freight> {
    // @Override
    // public View getView(int position, View convertView, ViewGroup parent) {
    // ViewHolder holder = null;
    // if (convertView == null || !(convertView instanceof LinearLayout)) {
    // convertView =
    // SystemUtils.inflate(R.layout.activity_freight_of_contacts_item);
    // holder = new ViewHolder();
    // holder.findView(convertView);
    // convertView.setTag(holder);
    // } else {
    // holder = (ViewHolder) convertView.getTag();
    // }
    // holder.fillData(getItem(position));
    // return convertView;
    // }
    // }
    //
    // private class ViewHolder {
    // LinearLayout ll_parent;
    // ImageView iv_freight_type;
    // TextView tv_start_region;
    // TextView tv_end_region;
    // TextView tv_name;
    // TextView tv_time;
    // TextView tv_desc;
    // TextView iv_arrow;
    //
    // public void fillData(Freight f) {
    // ll_parent.setBackgroundResource(0);
    // tv_start_region.setTextColor(getResources().getColor(R.color.black));
    // tv_end_region.setTextColor(getResources().getColor(R.color.black));
    // tv_name.setTextColor(getResources().getColor(R.color.black));
    // tv_time.setTextColor(getResources().getColor(R.color.content_gray));
    // tv_desc.setTextColor(getResources().getColor(R.color.content_gray));
    // iv_arrow.setTextColor(getResources().getColor(R.color.black));
    // if (f.getStatus() == Freight.STATUS_VALID) {
    // if (f.getType() == Freight.TYPE_GOODS && f.getOrder_status() ==
    // Freight.ORDER_STATUS_UN_ORDER) {
    // iv_freight_type.setImageResource(R.drawable.black_board_goods);
    // } else if (f.getType() == Freight.TYPE_TRUCK && f.getOrder_status() ==
    // Freight.ORDER_STATUS_UN_ORDER) {
    // iv_freight_type.setImageResource(R.drawable.black_board_truck);
    // } else if (f.getType() == Freight.TYPE_GOODS && f.getOrder_status() ==
    // Freight.ORDER_STATUS_ORDERED) {
    // iv_freight_type.setImageResource(R.drawable.bload_booked_goods);
    // } else if (f.getType() == Freight.TYPE_TRUCK && f.getOrder_status() ==
    // Freight.ORDER_STATUS_ORDERED) {
    // iv_freight_type.setImageResource(R.drawable.bload_booked_truck);
    // }
    // } else {
    // ll_parent.setBackgroundResource(R.color.qian_gray);
    // tv_start_region.setTextColor(getResources().getColor(R.color.text_gray2));
    // tv_end_region.setTextColor(getResources().getColor(R.color.text_gray2));
    // tv_name.setTextColor(getResources().getColor(R.color.text_gray2));
    // tv_time.setTextColor(getResources().getColor(R.color.text_gray2));
    // tv_desc.setTextColor(getResources().getColor(R.color.text_gray2));
    // iv_arrow.setTextColor(getResources().getColor(R.color.text_gray2));
    // if (f.getType() == Freight.TYPE_GOODS) {
    // // 货源已过期
    // iv_freight_type.setImageResource(R.drawable.black_board_goods2);
    // } else if (f.getType() == Freight.TYPE_TRUCK) {
    // // 车源已过期
    // iv_freight_type.setImageResource(R.drawable.black_board_truck2);
    // }
    // }
    // tv_start_region.setText(f.getStart_region());
    // tv_end_region.setText(f.getEnd_region());
    // tv_name.setText(f.getOwner_name());
    // tv_time.setText(DateUtil.long2vague(f.getUpdate_time()));
    // tv_desc.setText(f.getDesc());
    // // iv_advisory.setTag(f);
    // }
    //
    // public void findView(View v) {
    // ll_parent = (LinearLayout) v.findViewById(R.id.ll_parent);
    // iv_freight_type = (ImageView) v.findViewById(R.id.iv_freight_type);
    // tv_start_region = (TextView) v.findViewById(R.id.tv_start_region);
    // tv_end_region = (TextView) v.findViewById(R.id.tv_end_region);
    // tv_name = (TextView) v.findViewById(R.id.tv_name);
    // tv_time = (TextView) v.findViewById(R.id.tv_time);
    // tv_desc = (TextView) v.findViewById(R.id.tv_freight_desc);
    // iv_arrow = (TextView) v.findViewById(R.id.iv_arrow);
    // // iv_state = (ImageView) v.findViewById(R.id.iv_state);
    // // iv_advisory = (ImageView) v.findViewById(R.id.iv_advisory);
    // // iv_advisory.setOnClickListener(FreightOfContactsActivity.this);
    // }
    // }

    private class MyThread extends Thread {
        @Override
        public void run() {
            while (mIsScreenOn) {
                try {
                    sleep(DURTION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                if (mIsTop) {
                    requestData(OP_LOAD_NEWER);
                } else {
                    LogUtils.d("SearchFreightFragment", "不在最上面，不自动刷新");
                }
            }
        }

        public void shutDown() {
            if (mIsScreenOn) {
                mIsScreenOn = false;
                this.interrupt();
            }
        }
    }
}
