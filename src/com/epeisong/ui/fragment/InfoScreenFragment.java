package com.epeisong.ui.fragment;

import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshListView;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetInfoScreenList;
import com.epeisong.data.net.NetUpdateIsAllowToShow;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.Freight;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.ui.activity.FreightDetailActivity;
import com.epeisong.ui.adapter.FreightListAdapter;
import com.epeisong.ui.view.ChooseFreightTypeLayout;
import com.epeisong.ui.view.ChooseFreightTypeLayout.OnChooseFreightTypeListener;
import com.epeisong.ui.view.ChooseLineLayout;
import com.epeisong.ui.view.ChooseLineLayout.OnChooseLineListener;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 信息电子屏
 * @author gnn
 *
 */
public class InfoScreenFragment extends Fragment implements OnClickListener, OnChooseLineListener,
        OnChooseFreightTypeListener, OnItemClickListener {

    public static final String EXTRA_MARKET = "market";

    private static final int fGoods = Properties.FREIGHT_TYPE_GOODS;
    private static final int fTruck = Properties.FREIGHT_TYPE_VEHICLE;
    private static final int fAll = Properties.FREIGHT_TYPE_ALL;

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
    private int mFreightType = fAll;

    private User mMarket;
    private String selfMarket;

    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private FreightListAdapter mAdapter;

    private boolean mIsTop = true;
    private boolean mIsScreenOn = true;

    private MyThread mThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mMarket = (User) args.getSerializable(EXTRA_MARKET);
            selfMarket = args.getString("flag"); // 判断是不是从任务页面的配货市场传过来的
        }
        if (mMarket == null) {
            TextView tv = new TextView(getActivity());
            tv.setText("参数错误");
            tv.setGravity(Gravity.CENTER);
            mIsTop = false;
            mIsScreenOn = false;
            return tv;
        }
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
//        mChooseLineLayout.setFilter(ChooseRegionActivity.FILTER_0_2);
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
                requestData(OP_LOAD_MORE);
            }
        });
        if(!TextUtils.isEmpty(selfMarket)){
        	mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					position -= mPullToRefreshListView.getRefreshableView()
							.getHeaderViewsCount();
					TextView tv_name;
					TextView tv_delete;
					TextView tv_sign;
					View line;
					final Freight f = mAdapter.getItem(position);
					
					if (f != null) {
						final AlertDialog builder = new AlertDialog.Builder(getActivity())
								.create();
						builder.show();
						Window window = builder.getWindow();
						window.setContentView(R.layout.members_dialog);
						tv_name = (TextView) window.findViewById(R.id.tv_name);
						tv_delete = (TextView) window.findViewById(R.id.tv_delete);
						tv_sign = (TextView) window.findViewById(R.id.tv_sign);
						line = window.findViewById(R.id.line);
						builder.setCanceledOnTouchOutside(true);
						tv_name.setText(f.getStart_region()+"-"+f.getEnd_region());
						line.setVisibility(View.GONE);
						tv_sign.setVisibility(View.GONE);
						tv_delete.setText("删除该条信息");
						tv_delete.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								deleteFreight(f);
								builder.dismiss();
							}
						});
						return true;
					}

					return false;
				}
			});
        }
        	  //删除会员
//      		  receiveBroadCast = new ReceiveBroadCast();
//              IntentFilter filter = new IntentFilter();
//              filter.addAction("com.epeisong.ui.activity.romeveLish"); // 只有持有相同的action的接受者才能接收此广播
//              this.getActivity().registerReceiver(receiveBroadCast, filter);
//              
//              //增加会员
//              filter.addAction("com.epeisong.ui.activity.refreshMember");
//              this.getActivity().registerReceiver(receiveBroadCast, filter);
//              //屏蔽会员
//              filter.addAction("com.epeisong.ui.activity.changeLish");
//              this.getActivity().registerReceiver(receiveBroadCast, filter);
        return view;
    }
    
    private void deleteFreight(final Freight f){
    	AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... params) {
				NetUpdateIsAllowToShow net = new NetUpdateIsAllowToShow() {
					
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setNewStatus(Properties.MARKET_SCREEN_NOT_ALLOW_TO_SHOW);
				        req.setMarketScreenId(f.getMarket_screen_freight_id());
						return true;
					}
				};
				try {
					CommonLogisticsResp.Builder resp = net.request();
					if (resp != null && "SUCC".equals(resp.getResult())) {
		                return true;
		            }
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if(result){
					ToastUtils.showToast("此消息已删除");
					mAdapter.removeItem(f);
				}
			}
    		
    	};
    	task.execute();
    	
    }
    
//    @Override
//	public void onAttach(Activity activity) {
//		/** 注册广播 */
//        receiveBroadCast = new ReceiveBroadCast();
//        IntentFilter filter = new IntentFilter();
//        this.getActivity().registerReceiver(receiveBroadCast, filter);
//		super.onAttach(activity);
//	}
//	
//    class ReceiveBroadCast extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//        	freshMember = (MarketMember) intent.getSerializableExtra("romeveLish"); // 删除用户
//        	freshUser = (User) intent.getSerializableExtra("refreshMember"); // 添加用户
//        	changeList = (MarketMember) intent.getSerializableExtra("changeList"); // 屏蔽用户
//            if(freshMember != null){
//            	requestData(OP_REFRESH);
//            }
//            if(freshUser != null){
//            	requestData(OP_LOAD_NEWER);
//            }
//            if(changeList != null){
//            	requestData(OP_REFRESH);
//            }
//        }
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mMarket == null) {
            return;
        }
        requestData(OP_REFRESH);
        mThread = new MyThread();
        mThread.start();
    }

    private synchronized void requestData(final int op) {
        AsyncTask<Void, Void, List<Freight>> task = new AsyncTask<Void, Void, List<Freight>>() {
            @Override
            protected List<Freight> doInBackground(Void... params) {
                NetInfoScreenList net = new NetInfoScreenList() {
                    @Override
                    protected int getCommandCode() {
                        switch (op) {
                        case OP_REFRESH:
                        case OP_LOAD_NEWER:
                            return CommandConstants.LIST_LATEST_FREIGHTS_ON_MARKET_SCREEN_REQ;
                        case OP_LOAD_MORE:
                            return CommandConstants.LIST_OLDER_FREIGHTS_ADJOIN_LOCAL_FROM_MARKET_SCREEN_REQ;
                        default:
                            return 0;
                        }
                    }

                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setMarketId(Integer.parseInt(mMarket.getId()));
                        int size = 10;
                        long createTime = 0;
                        int idOnScreen = 0;
                        if (op == OP_LOAD_MORE) {
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
                        } else {
                            size = SIZE_REFRESH;
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
                    CommonLogisticsResp.Builder resp = net.request();
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
                mPullToRefreshListView.onRefreshComplete();
                if (result == null) {
                    return;
                }
                switch (op) {
                case OP_LOAD_NEWER:
                    mAdapter.addAll(0, result);
                    break;
                case OP_REFRESH:
                    mAdapter.replaceAll(result);
                    if (mAdapter.getCount() > 0) {
                        mListView.setSelection(0);
                    }
                    break;
                case OP_LOAD_MORE:
                    mAdapter.addAll(result);
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
//        if (receiveBroadCast != null) {
//            this.getActivity().unregisterReceiver(receiveBroadCast);
//        }
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
                mFreightType = fAll;
            } else if (type == OnChooseFreightTypeListener.FREIGHT_TYPE_GOODS) {
                mFreightType = fGoods;
            } else if (type == OnChooseFreightTypeListener.FREIGHT_TYPE_TRUCK) {
                mFreightType = fTruck;
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
        } else {
            mStartRegion = null;
            mEndRegion = null;
            mStartCode = 0;
            mEndCode = 0;
            mLineTv.setText("线路不限");
        }
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

//    private class MyAdapter extends HoldDataBaseAdapter<Freight> {
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder = null;
//            if (convertView == null || !(convertView instanceof LinearLayout)) {
//                convertView = SystemUtils.inflate(R.layout.activity_freight_of_contacts_item);
//                holder = new ViewHolder();
//                holder.findView(convertView);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//            holder.fillData(getItem(position));
//            return convertView;
//        }
//    }
//
//    private class ViewHolder {
//        LinearLayout ll_parent;
//        ImageView iv_freight_type;
//        TextView tv_start_region;
//        TextView tv_end_region;
//        TextView tv_name;
//        TextView tv_time;
//        TextView tv_desc;
//        TextView iv_arrow;
//
//        public void fillData(Freight f) {
//            ll_parent.setBackgroundResource(0);
//            tv_start_region.setTextColor(getResources().getColor(R.color.black));
//            tv_end_region.setTextColor(getResources().getColor(R.color.black));
//            tv_name.setTextColor(getResources().getColor(R.color.black));
//            tv_time.setTextColor(getResources().getColor(R.color.content_gray));
//            tv_desc.setTextColor(getResources().getColor(R.color.content_gray));
//            iv_arrow.setTextColor(getResources().getColor(R.color.black));
//            if (f.getStatus() == Freight.STATUS_VALID) {
//                if (f.getType() == Freight.TYPE_GOODS && f.getOrder_status() == Freight.ORDER_STATUS_UN_ORDER) {
//                    iv_freight_type.setImageResource(R.drawable.black_board_goods);
//                } else if (f.getType() == Freight.TYPE_TRUCK && f.getOrder_status() == Freight.ORDER_STATUS_UN_ORDER) {
//                    iv_freight_type.setImageResource(R.drawable.black_board_truck);
//                } else if (f.getType() == Freight.TYPE_GOODS && f.getOrder_status() == Freight.ORDER_STATUS_ORDERED) {
//                    iv_freight_type.setImageResource(R.drawable.bload_booked_goods);
//                } else if (f.getType() == Freight.TYPE_TRUCK && f.getOrder_status() == Freight.ORDER_STATUS_ORDERED) {
//                    iv_freight_type.setImageResource(R.drawable.bload_booked_truck);
//                }
//            } else {
//                ll_parent.setBackgroundResource(R.color.qian_gray);
//                tv_start_region.setTextColor(getResources().getColor(R.color.text_gray2));
//                tv_end_region.setTextColor(getResources().getColor(R.color.text_gray2));
//                tv_name.setTextColor(getResources().getColor(R.color.text_gray2));
//                tv_time.setTextColor(getResources().getColor(R.color.text_gray2));
//                tv_desc.setTextColor(getResources().getColor(R.color.text_gray2));
//                iv_arrow.setTextColor(getResources().getColor(R.color.text_gray2));
//                if (f.getType() == Freight.TYPE_GOODS) {
//                    // 货源已过期
//                    iv_freight_type.setImageResource(R.drawable.black_board_goods2);
//                } else if (f.getType() == Freight.TYPE_TRUCK) {
//                    // 车源已过期
//                    iv_freight_type.setImageResource(R.drawable.black_board_truck2);
//                }
//            }
//            tv_start_region.setText(f.getStart_region());
//            tv_end_region.setText(f.getEnd_region());
//            tv_name.setText(f.getOwner_name());
//            tv_time.setText(DateUtil.long2vague(f.getUpdate_time()));
//            tv_desc.setText(f.getDesc());
//            // iv_advisory.setTag(f);
//        }
//
//        public void findView(View v) {
//            ll_parent = (LinearLayout) v.findViewById(R.id.ll_parent);
//            iv_freight_type = (ImageView) v.findViewById(R.id.iv_freight_type);
//            tv_start_region = (TextView) v.findViewById(R.id.tv_start_region);
//            tv_end_region = (TextView) v.findViewById(R.id.tv_end_region);
//            tv_name = (TextView) v.findViewById(R.id.tv_name);
//            tv_time = (TextView) v.findViewById(R.id.tv_time);
//            tv_desc = (TextView) v.findViewById(R.id.tv_freight_desc);
//            iv_arrow = (TextView) v.findViewById(R.id.iv_arrow);
//            // iv_state = (ImageView) v.findViewById(R.id.iv_state);
//            // iv_advisory = (ImageView) v.findViewById(R.id.iv_advisory);
//            // iv_advisory.setOnClickListener(FreightOfContactsActivity.this);
//        }
//    }

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
                    LogUtils.d("InfoScreenFragment", "不在最上面，不自动刷新");
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
