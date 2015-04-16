package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshListView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.net.NetLatestFreightsOnMarket;
import com.epeisong.data.net.NetOlderFreightsAdjoinMarket;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEMarketScreen;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.Freight;
import com.epeisong.model.User;
import com.epeisong.ui.adapter.FreightListAdapter;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 逛逛配货市场电子屏
 * 
 * @author gnn
 * 
 */
public class MarketOfFreightActivity extends BaseActivity implements OnItemClickListener {

//    private class MyAdapter extends HoldDataBaseAdapter<Freight> {
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder = null;
//            if (convertView == null || !(convertView instanceof LinearLayout)) {
//                convertView = SystemUtils.inflate(R.layout.activity_search_market_item);
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
//        ImageView iv_freight_type;
//        TextView tv_start_region;
//        TextView tv_end_region;
//        TextView tv_name;
//        TextView tv_time;
//        TextView tv_desc;
//
//        public void fillData(Freight f) {
//            if (f.getType() == Freight.TYPE_GOODS && f.getOrder_status() == Freight.ORDER_STATUS_UN_ORDER) {
//                // 货源未被订
//                iv_freight_type.setImageResource(R.drawable.selector_borad_goods);
//            } else if (f.getType() == Freight.TYPE_TRUCK && f.getOrder_status() == Freight.ORDER_STATUS_UN_ORDER) {
//                // 车源未被订
//                iv_freight_type.setImageResource(R.drawable.selector_board_truck);
//            }else if (f.getType() == Freight.TYPE_GOODS && f.getOrder_status() == Freight.ORDER_STATUS_ORDERED) {
//                // 货源已被订
//                iv_freight_type.setImageResource(R.drawable.selector_booked_goods);
//            } else if (f.getType() == Freight.TYPE_TRUCK && f.getOrder_status() == Freight.ORDER_STATUS_ORDERED) {
//                // 车源已被订
//                iv_freight_type.setImageResource(R.drawable.selector_booked_truck);
//            }
//            tv_start_region.setText(f.getStart_region());
//            tv_end_region.setText(f.getEnd_region());
//            tv_name.setText(f.getOwner_name());
//            tv_time.setText(DateUtil.long2vague(f.getUpdate_time()));
//            tv_desc.setText(f.getDesc());
//        }
//
//        public void findView(View v) {
//            iv_freight_type = (ImageView) v.findViewById(R.id.iv_freight_type);
//            tv_start_region = (TextView) v.findViewById(R.id.tv_start_region);
//            tv_end_region = (TextView) v.findViewById(R.id.tv_end_region);
//            tv_name = (TextView) v.findViewById(R.id.tv_name);
//            tv_time = (TextView) v.findViewById(R.id.tv_time);
//            tv_desc = (TextView) v.findViewById(R.id.tv_freight_desc);
//        }
//    }

    public static final String EXTRA_MARKET = "market";

    public static final String EXTRA_FLAG = "flag";
    private static final long DURTION = 1000 * 30;

    private User mMarket;
    private String flag;

    private TextView mMarketTime;

    private PullToRefreshListView mPullToRefreshListView;
    private ImageView mIvSearch;

    private ListView mListView;
    private FreightListAdapter mAdapter;
    private Thread mThread;

    private boolean mAutoLoad = true;
    private boolean mScreen = true;

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        position -= mPullToRefreshListView.getRefreshableView().getHeaderViewsCount();
        Freight f = mAdapter.getItem(position);
        if (true) {
            Intent intent = new Intent(this, FreightDetailActivity.class);
            intent.putExtra(FreightDetailActivity.EXTRA_FREIGHT, f);
            BusinessChatModel model = BusinessChatModel.getFromFreight(f);
            intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
            intent.putExtra(FreightDetailActivity.EXTRA_CAN_DELETE, false);
            startActivity(intent);
            return;
        }
        
    }

    // 数据自动加载
    private void autoLoading() {
    	NetLatestFreightsOnMarket net = new NetLatestFreightsOnMarket() {
			
			@Override
			protected boolean onSetRequest(Builder req) {
		        req.setMarketId(Integer.parseInt(mMarket.getId()));
		        req.setCurrentTime(System.currentTimeMillis());
		        req.setLimitCount(10);
		        req.setFreightType(Properties.FREIGHT_TYPE_ALL);
		        req.setRouteCodeA(0);
		        req.setRouteCodeB(0);
				return true;
			}
		};
		try {
			CommonLogisticsResp.Builder response = net.request();
			if(net.isSuccess(response)){
				List<ProtoEMarketScreen> freightList = response.getMarketScreenList();
                if (freightList == null || freightList.isEmpty()) {
                    mPullToRefreshListView.onRefreshComplete();
                    return;
                }
                List<Freight> result = new ArrayList<Freight>();
                for (ProtoEMarketScreen item : freightList) {
              	  result.add(FreightParser.parse(item));
                }
                mAdapter.replaceAll(result);
                mPullToRefreshListView.onRefreshComplete();
			}
		} catch (Exception e) {
			mPullToRefreshListView.onRefreshComplete();
			e.printStackTrace();
		}
//        NetLatestFreightsOnMarket net = new NetLatestFreightsOnMarket(this, Integer.parseInt(mMarket.getId()), 10, 0,
//                Properties.FREIGHT_TYPE_ALL ,0 , 0);
//        net.setShowDialog(false);
//        net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
//
//            @Override
//            public void onError() {
//                super.onError();
//                mPullToRefreshListView.onRefreshComplete();
//            }
//
//            @Override
//            public void onFail(String msg) {
//                super.onFail(msg);
//                mPullToRefreshListView.onRefreshComplete();
//            }
//
//            @Override
//            public void onSuccess(CommonLogisticsResp.Builder response) {
//                List<ProtoEMarketScreen> freightList = response.getMarketScreenList();
//                if (freightList == null || freightList.isEmpty()) {
//                    mPullToRefreshListView.onRefreshComplete();
//                    return;
//                }
//                List<Freight> result = new ArrayList<Freight>();
//                for (ProtoEMarketScreen item : freightList) {
//                	result.add(FreightParser.parse(item));
//                }
//                mAdapter.replaceAll(result);
//                mPullToRefreshListView.onRefreshComplete();
//            }
//        });
    }

    private void clickSearch() {
    	NetLatestFreightsOnMarket net = new NetLatestFreightsOnMarket() {
			
			@Override
			protected boolean onSetRequest(Builder req) {
		        req.setMarketId(Integer.parseInt(mMarket.getId()));
		        req.setCurrentTime(System.currentTimeMillis());
		        req.setLimitCount(10);
		        req.setFreightType(Properties.FREIGHT_TYPE_ALL);
		        req.setRouteCodeA(0);
		        req.setRouteCodeB(0);
				return true;
			}
		};
		try {
			CommonLogisticsResp.Builder response = net.request();
			if(net.isSuccess(response)){
				List<ProtoEMarketScreen> freightList = response.getMarketScreenList();
                if (freightList == null || freightList.isEmpty()) {
                    ToastUtils.showToast("该配货市场没有发布配货信息");
                }
                List<Freight> result = new ArrayList<Freight>();
                for (ProtoEMarketScreen item : freightList) {
                    result.add(FreightParser.parse(item));
                }
                mAdapter.addAll(0, result);
			  }
		} catch (Exception e) {
			e.printStackTrace();
		}
//        NetLatestFreightsOnMarket net = new NetLatestFreightsOnMarket(this, Integer.parseInt(mMarket.getId()), 10, 0,
//                Properties.FREIGHT_TYPE_ALL ,0 ,0);
//        net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
//
//            @Override
//            public void onSuccess(CommonLogisticsResp.Builder response) {
//            	List<ProtoEMarketScreen> freightList = response.getMarketScreenList();
//                if (freightList == null || freightList.isEmpty()) {
//                    ToastUtils.showToast("该配货市场没有发布配货信息");
//                }
//                List<Freight> result = new ArrayList<Freight>();
//                for (ProtoEMarketScreen item : freightList) {
//                    result.add(FreightParser.parse(item));
//                }
//                mAdapter.addAll(0, result);
//            }
//        });
    }

    private void initView() {
        mAutoLoad = true;

        // btn_search_condition = (Button) findViewById(R.id.btn_market);
        // btn_search_condition.setOnClickListener(this);
        mMarketTime = (TextView) findViewById(R.id.tv_market_time);
        mMarketTime.setText(DateUtil.long2YMDW(System.currentTimeMillis()));

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv);
        mListView = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setAdapter(mAdapter = new FreightListAdapter());
        mAdapter.setMaxSize(10);
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
                        mAutoLoad = true;
                    } else {
                        mAutoLoad = false;
                    }
                }
            }
        });
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                slideDownEvent();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                slideUpEvent();
            }
        });
    }

    // 往下滑动加载
    private void slideDownEvent() {
        mAutoLoad = true;
        autoLoading();
    }

    // 往上滑动加载
    private void slideUpEvent() {
        if (mAdapter.isEmpty()) {
            return;
        }

        final long time = mAdapter.getItem(mAdapter.getCount() - 1).getCreate_time();
        final int market_freight_index = mAdapter.getItem(mAdapter.getCount() - 1).getMarket_screen_freight_id();
        NetOlderFreightsAdjoinMarket net = new NetOlderFreightsAdjoinMarket() {
			
			@Override
			protected boolean onSetRequest(Builder req) {
				req.setMarketId(Integer.parseInt(mMarket.getId()));
		        req.setLimitCount(10);
		        req.setCreateDate(time);
		        req.setFreightType(Properties.FREIGHT_TYPE_ALL);
		        req.setId(market_freight_index);
		        req.setRouteCodeA(0);
		        req.setRouteCodeB(0);
				return true;
			}
		};
		try {
			CommonLogisticsResp.Builder response = net.request();
			if(net.isSuccess(response)){
				List<ProtoEMarketScreen> freightList = response.getMarketScreenList();
                if (freightList == null || freightList.isEmpty()) {
                    ToastUtils.showToast("上滑没有更多车源货源信息");
                    mPullToRefreshListView.onRefreshComplete();
                    return;
                }
                List<Freight> result = new ArrayList<Freight>();
                for (ProtoEMarketScreen item : freightList) {
                    result.add(FreightParser.parse(item));
                }
                mAdapter.addAll(result);
                mPullToRefreshListView.onRefreshComplete();
			}
		} catch (Exception e) {
			mPullToRefreshListView.onRefreshComplete();
			e.printStackTrace();
		}
//        NetOlderFreightsAdjoinMarket net = new NetOlderFreightsAdjoinMarket(this, Integer.parseInt(mMarket.getId()),
//                time, market_freight_index, 10, Properties.FREIGHT_TYPE_ALL ,0,0 );
//        net.setShowDialog(false);
//        net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
//
//            @Override
//            public void onError() {
//                super.onError();
//                mPullToRefreshListView.onRefreshComplete();
//            }
//
//            @Override
//            public void onFail(String msg) {
//                super.onFail(msg);
//                mPullToRefreshListView.onRefreshComplete();
//            }
//
//            @Override
//            public void onSuccess(CommonLogisticsResp.Builder response) {
//                List<ProtoEMarketScreen> freightList = response.getMarketScreenList();
//                if (freightList == null || freightList.isEmpty()) {
//                    ToastUtils.showToast("上滑没有更多车源货源信息");
//                    mPullToRefreshListView.onRefreshComplete();
//                    return;
//                }
//                List<Freight> result = new ArrayList<Freight>();
//                for (ProtoEMarketScreen item : freightList) {
//                    result.add(FreightParser.parse(item));
//                }
//                mAdapter.addAll(result);
//                mPullToRefreshListView.onRefreshComplete();
//            }
//        });

    }

    private Action createAction() {
        return new ActionImpl() {

            @Override
            public void doAction(View v) {
                butSearch();
            }

            @Override
            public View getView() {
                // TODO
                mIvSearch = new ImageView(getApplicationContext());
                mIvSearch.setBackgroundResource(R.drawable.search_market_icon);
                int padding = (int) DimensionUtls.getPixelFromDp(5);
                mIvSearch.setPadding(padding, padding, padding, padding);
                return mIvSearch;
            }
        };
    }

    private void butSearch() {
        Intent i = new Intent(this, SearchTheSourceSupplyOCarsActivity.class);
        i.putExtra(SearchTheSourceSupplyOCarsActivity.EXTRA_MARKET, mMarket);
        i.putExtra(SearchTheSourceSupplyOCarsActivity.EXTRA_MARKET_ID, mMarket.getId());
        i.putExtra(SearchTheSourceSupplyOCarsActivity.EXTRA_FLAG, flag);
        startActivity(i);
    }

    @Override
    protected TitleParams getTitleParams() {
        List<Action> actions = new ArrayList<Action>();
        actions.add(createAction());
        return new TitleParams(getDefaultHomeAction(), mMarket.getShow_name(), actions).setShowLogo(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO
        mMarket = (User) getIntent().getSerializableExtra(EXTRA_MARKET);
        flag = getIntent().getStringExtra(EXTRA_FLAG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_market);
        initView();
        clickSearch();

        mThread = new Thread() {
            @Override
            public void run() {
                while (mScreen) {
                    try {
                        Thread.sleep(DURTION);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        this.interrupt();
                        return;
                    }

                    if (!mAutoLoad) {
                        try {
                            LogUtils.d("", "当前不在最上，休息1秒！");
                            Thread.sleep(1000);
                            continue;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            this.interrupt();
                            return;
                        }
                    }

                    mPullToRefreshListView.post(new Runnable() {
                        @Override
                        public void run() {
                            autoLoading();
                        }
                    });
                }
            };
        };
        mThread.start();
    }

    @Override
    protected void onDestroy() {
        mThread.interrupt();
        mScreen = false;
        super.onDestroy();
    }

}
