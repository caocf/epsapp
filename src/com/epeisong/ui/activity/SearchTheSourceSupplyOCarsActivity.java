package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.net.NetLatestFreightsOnMarket;
import com.epeisong.data.net.NetOlderFreightsAdjoinMarket;
import com.epeisong.data.net.NetSearchFreight;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEFreight;
import com.epeisong.logistics.proto.Base.ProtoEMarketScreen;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.Freight;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.ui.adapter.FreightListAdapter;
import com.epeisong.ui.view.ChooseFreightTypeLayout;
import com.epeisong.ui.view.ChooseFreightTypeLayout.OnChooseFreightTypeListener;
import com.epeisong.ui.view.ChooseLineLayout;
import com.epeisong.ui.view.ChooseLineLayout.OnChooseLineListener;

/**
 * 
 * @author 孙灵洁 搜索车源货源
 * 
 */
public class SearchTheSourceSupplyOCarsActivity extends BaseActivity implements OnClickListener, OnChooseLineListener,
        OnItemClickListener, OnChooseFreightTypeListener, OnLoadMoreListener {

//    private class MyAdapter extends HoldDataBaseAdapter<Freight> {
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            ViewHolder holder = null;
//            if (convertView == null) {
//                convertView = SystemUtils.inflate(R.layout.item_search_the_source_supply_of_cars);
//                holder = new ViewHolder();
//                holder.findView(convertView);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//            Freight f = getItem(position);
//            holder.fillData(f);
//            return convertView;
//        }
//    }
//
//    private class ViewHolder {
//        LinearLayout ll_parent;
//        ImageView iv_type;
//        TextView tv_start;
//        TextView tv_end;
//        TextView tv_gongsi;
//        TextView tv_time;
//        TextView tv_xiangqing;
//        TextView iv_arrow;
//
//        // ImageView iv_state;
//
//        public void fillData(Freight f) {
//            // iv_state.setBackgroundResource(0);
//            ll_parent.setBackgroundResource(0);
//            tv_start.setTextColor(getResources().getColor(R.color.black));
//            tv_end.setTextColor(getResources().getColor(R.color.black));
//            tv_gongsi.setTextColor(getResources().getColor(R.color.black));
//            tv_time.setTextColor(getResources().getColor(R.color.text_gray2));
//            tv_xiangqing.setTextColor(getResources().getColor(R.color.text_gray2));
//            iv_arrow.setTextColor(getResources().getColor(R.color.black));
//            if (f.getStatus() == Properties.FREIGHT_STATUS_COMPLETED) {
//                ll_parent.setBackgroundResource(R.color.qian_gray);
//                tv_start.setTextColor(getResources().getColor(R.color.text_gray2));
//                tv_end.setTextColor(getResources().getColor(R.color.text_gray2));
//                tv_gongsi.setTextColor(getResources().getColor(R.color.text_gray2));
//                tv_time.setTextColor(getResources().getColor(R.color.text_gray2));
//                tv_xiangqing.setTextColor(getResources().getColor(R.color.text_gray2));
//                iv_arrow.setTextColor(getResources().getColor(R.color.text_gray2));
//                if (f.getType() == Freight.TYPE_GOODS) {
//                    // 货源已被订
//                    iv_type.setImageResource(R.drawable.black_board_goods2);
//                } else if (f.getType() == Freight.TYPE_TRUCK) {
//                    // 车源已被订
//                    iv_type.setImageResource(R.drawable.black_board_truck2);
//                }
//            }else if(f.getStatus() == Properties.FREIGHT_STATUS_NO_PROCESSED){
//            	if (f.getType() == Freight.TYPE_GOODS) {
//            		iv_type.setImageResource(R.drawable.black_board_goods);
//                } else if (f.getType() == Freight.TYPE_TRUCK) {
//                	iv_type.setImageResource(R.drawable.black_board_truck);
//                }
//            } else if(f.getStatus() == Properties.FREIGHT_STATUS_BOOK) {
//            	if(f.getType() == Freight.TYPE_GOODS){
//                	iv_type.setImageResource(R.drawable.bload_booked_goods);
//                }else if(f.getType() == Freight.TYPE_TRUCK){
//                	iv_type.setImageResource(R.drawable.bload_booked_truck);
//                }
//            }
//            tv_start.setText(f.getStart_region());
//            tv_end.setText(f.getEnd_region());
//            tv_gongsi.setText(f.getOwner_name());
//            tv_time.setText((DateUtil.long2vague(f.getCreate_time())));
//            tv_xiangqing.setText(f.getDesc());
//        }
//
//        public void findView(View v) {
//            // iv_state = (ImageView) v.findViewById(R.id.iv_state);
//            ll_parent = (LinearLayout) v.findViewById(R.id.ll_parent);
//            iv_type = (ImageView) v.findViewById(R.id.iv_type);
//            tv_start = (TextView) v.findViewById(R.id.tv_start);
//            tv_end = (TextView) v.findViewById(R.id.tv_end);
//            tv_gongsi = (TextView) v.findViewById(R.id.tv_gongsi);
//            tv_time = (TextView) v.findViewById(R.id.tv_time);
//            tv_xiangqing = (TextView) v.findViewById(R.id.tv_xiangqing);
//            iv_arrow = (TextView) v.findViewById(R.id.iv_arrow);
//        }
//    }

    public static final String EXTRA_PLATFORM_NAME = "platform_name";
    public static final String EXTRA_MARKET_ID = "market_id";
    public static final String EXTRA_FLAG = "flag";
    public static final String EXTRA_MARKET = "market";

    private static final int SIZE_LOAD_FIRST = 10;
    private static final int SIZE_LOAD_MORE = 10;

    private Button bt_search;
    private ListView lv;
    private TextView mChoosableTv01;
    private View mUnderLine01;
    private TextView mChoosableTv02;
    private View mUnderLine02;
    private View mChooseContainer;
    private int mStartRegionCode;
    private int mEndRegionCode;

    private ChooseFreightTypeLayout mChooseFreightTypeLayout;

    private ChooseLineLayout mChooseLineLayout;
    private int mFreightType = Properties.FREIGHT_TYPE_ALL;
    private RegionResult mStartRegion;
    private RegionResult mEndRegion;

    private EndlessAdapter mEndlessAdapter;

    private User mMarket;
    private String mPlatformName;
    private String mMarketId;
    private TextView view_empty;
    // private String flag;
    private int positonindex;
    private Freight refreshFreight;

    FreightListAdapter mAdapter = new FreightListAdapter();

    private ReceiveBroadCast receiveBroadCast;

    @Override
    public void onAttachFragment(Fragment fragment) {
        /** 注册广播 */
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        // filter.addAction("com.gasFragment"); // 只有持有相同的action的接受者才能接收此广播
        this.registerReceiver(receiveBroadCast, filter);
        super.onAttachFragment(fragment);
    }

    class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshFreight = (Freight) intent.getSerializableExtra("freshList");
            // searchContacts(); // 从后台数据库获取较慢，采用下面方式
            List<Freight> fList = mAdapter.getAllItem();
            for (Freight f : fList) {
                if (f.getId().equals(refreshFreight.getId())) {
                    f.setStatus(Properties.FREIGHT_STATUS_BOOK);
                }
            }
            mAdapter.notifyDataSetChanged();
            // ToastUtils.showToast(refreshFreight.getOrder_status()+"");
        }
    }

    /**
     * 注销广播
     * */
    @Override
    protected void onDestroy() {
        if (receiveBroadCast != null) {
            this.unregisterReceiver(receiveBroadCast);
        }
        super.onDestroy();
    }

    @Override
    public void onChoosedFreightType(String name, int type, boolean change) {
        if (type != -1) {
            mChoosableTv01.setText(name);
            if (type == OnChooseFreightTypeListener.FREIGHT_TYPE_GOODS) {
                mFreightType = Freight.TYPE_GOODS;
            } else if (type == OnChooseFreightTypeListener.FREIGHT_TYPE_TRUCK) {
                mFreightType = Freight.TYPE_TRUCK;
            } else {
                mFreightType = Properties.FREIGHT_TYPE_ALL;
                // mFreightType = Freight.TYPE_ALL;
            }
        }
        hideChooseFreightType();
    }

    @Override
    public void onChoosedLine(RegionResult start, RegionResult end) {
        if (start != null && end != null) {
            mChoosableTv02.setText(start.getShortNameFromDistrict() + "-" + end.getShortNameFromDistrict());
            mStartRegion = start;
            mEndRegion = end;
            mStartRegionCode = start.getCode();
            mEndRegionCode = end.getCode();

        } else {
            mStartRegion = null;
            mEndRegion = null;
            mStartRegionCode = 0;
            mEndRegionCode = 0;
            mChoosableTv02.setText("线路不限");
        }
        hideChooseLine();
    }

    @Override
    public void onBackPressed() {
        if (mChooseContainer.getVisibility() == View.VISIBLE) {
            mChooseContainer.setVisibility(View.GONE);
            hideChooseFreightType();
            hideChooseLine();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Freight) {
            Freight f = (Freight) tag;
            String user_id = f.getUser_id();
            String freight_id = f.getId();
            Intent intent = new Intent(SearchTheSourceSupplyOCarsActivity.this, ChatRoomActivity.class);
            intent.putExtra(ChatRoomActivity.EXTRA_REMOTE_ID, user_id);
            intent.putExtra(ChatRoomActivity.EXTRA_BUSINESS_TYPE, ChatMsg.business_type_freight);
            intent.putExtra(ChatRoomActivity.EXTRA_BUSINESS_ID, freight_id);
            startActivity(intent);
            return;
        }

        switch (v.getId()) {
        case R.id.fl_choose_container0:
            mChooseContainer.setVisibility(View.GONE);
            hideChooseFreightType();
            hideChooseLine();
            break;
        case R.id.fl_choosable_001:
            if (mChooseFreightTypeLayout.getVisibility() == View.GONE) {
                showChooseFreightType();
            } else {
                hideChooseFreightType();
            }
            break;
        case R.id.fl_choosable_002:
            if (mChooseLineLayout.getVisibility() == View.GONE) {
                showChooseLine();
            } else {
                hideChooseLine();
            }
            break;
        case R.id.bt_search:
            searchContacts();
            break;

        default:
            break;
        }
    }

    private void searchContacts() {
        if (mMarketId == null || "-1".equals(mMarketId)) {
        	NetSearchFreight net = new NetSearchFreight() {
    			@Override
    			protected boolean onSetRequest(
    					com.epeisong.logistics.proto.Eps.FreightReq.Builder req) {
    				req.setStartPointCode(mStartRegionCode);
    				req.setDestinationCode(mEndRegionCode);
    				req.setLimitCount(SIZE_LOAD_FIRST);
    				req.setId(0);
    				req.setFreightType(mFreightType);
    				req.setLogisticId(0);
    				return true;
    			}
    		};
    		try {
    			FreightResp.Builder response = net.request();
    			if(net.isSuccess(response)){
    				List<Freight> result = FreightParser.parse(response);
                    if (result == null || result.isEmpty()) {
                        mAdapter.clear();
                        view_empty.setText("没有数据");
                        return;
                    }
                    mEndlessAdapter.setHasMore(result.size() >= SIZE_LOAD_FIRST);
                    mAdapter.replaceAll(result);
                    lv.setSelection(0);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
//            NetSearchFreight net = new NetSearchFreight(this, mStartRegionCode, mEndRegionCode, mFreightType,
//                    SIZE_LOAD_FIRST, 0, 0);
//            net.request(new OnNetRequestListenerImpl<Eps.FreightResp.Builder>() {
//                @Override
//                public void onSuccess(FreightResp.Builder response) {
//                    List<Freight> result = FreightParser.parse(response);
//                    if (result == null || result.isEmpty()) {
//                        mAdapter.clear();
//                        view_empty.setText("没有数据");
//                        return;
//                    }
//                    mEndlessAdapter.setHasMore(result.size() >= SIZE_LOAD_FIRST);
//                    mAdapter.replaceAll(result);
//                    lv.setSelection(0);
//                }
//            });
        } else {
            // TODO 针对配货市场的搜索
            final int marketId = Integer.parseInt(mMarketId);
            NetLatestFreightsOnMarket net = new NetLatestFreightsOnMarket() {
    			
				@Override
				protected boolean onSetRequest(
						com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder req) {
    		        req.setMarketId(marketId);
    		        req.setCurrentTime(System.currentTimeMillis());
    		        req.setLimitCount(SIZE_LOAD_FIRST);
    		        req.setFreightType(mFreightType);
    		        req.setRouteCodeA(mStartRegionCode);
    		        req.setRouteCodeB(mEndRegionCode);
    				return true;
				}
    		};
    		try {
    			CommonLogisticsResp.Builder response = net.request();
    			if(net.isSuccess(response)){
    				List<ProtoEMarketScreen> freightList = response.getMarketScreenList();
                    if (freightList == null || freightList.isEmpty()) {
                        // showMessageDialog(null, "该配货市场没有发布配货信息");
                        mAdapter.clear();
                        view_empty.setText("没有数据");
                        return;
                    }
                    List<Freight> result = new ArrayList<Freight>();
                    for (ProtoEMarketScreen item : freightList) {
                        result.add(FreightParser.parse(item));
                    }
                    mEndlessAdapter.setHasMore(result.size() >= SIZE_LOAD_FIRST);
                    mAdapter.replaceAll(result);
                    lv.setSelection(0);
    			  }
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
//            NetLatestFreightsOnMarket net = new NetLatestFreightsOnMarket(this, marketId, SIZE_LOAD_FIRST, 0,
//                    mFreightType, mStartRegionCode, mEndRegionCode);
//            net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
//                @Override
//                public void onSuccess(CommonLogisticsResp.Builder response) {
//                    List<ProtoEMarketScreen> freightList = response.getMarketScreenList();
//                    if (freightList == null || freightList.isEmpty()) {
//                        // showMessageDialog(null, "该配货市场没有发布配货信息");
//                        mAdapter.clear();
//                        view_empty.setText("没有数据");
//                        return;
//                    }
//                    List<Freight> result = new ArrayList<Freight>();
//                    for (ProtoEMarketScreen item : freightList) {
//                        result.add(FreightParser.parse(item));
//                    }
//                    mEndlessAdapter.setHasMore(result.size() >= SIZE_LOAD_FIRST);
//                    mAdapter.replaceAll(result);
//                    lv.setSelection(0);
//                }
//
//            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        if (mMarketId == null || "-1".equals(mMarketId)) {

        } else {

        }
        positonindex = position;
        Freight f = mAdapter.getItem(position);
        if (true) {
            Intent intent = new Intent(getApplicationContext(), FreightDetailActivity.class);
            intent.putExtra(FreightDetailActivity.EXTRA_FREIGHT, f);
            BusinessChatModel model = new BusinessChatModel();
            model.setBusiness_type(ChatMsg.business_type_freight);
            model.setBusiness_id(f.getId());
            model.setBusiness_desc(f.getStart_region() + "-" + f.getEnd_region());
            model.setBusiness_extra(String.valueOf(f.getType()));
            model.setBusiness_owner_id(f.getUser_id());
            intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
            intent.putExtra(FreightDetailActivity.EXTRA_CAN_DELETE, false);
            startActivity(intent);
            return;
        }

    }

    @Override
    public void onStartLoadMore(EndlessAdapter adapter) {
        if (mMarketId == null || "-1".equals(mMarketId)) {
            final String id = mAdapter.getItem(mAdapter.getCount() - 1).getId();
            NetSearchFreight net = new NetSearchFreight() {
    			@Override
    			protected boolean onSetRequest(
    					com.epeisong.logistics.proto.Eps.FreightReq.Builder req) {
    				req.setStartPointCode(mStartRegionCode);
    				req.setDestinationCode(mEndRegionCode);
    				req.setLimitCount(SIZE_LOAD_MORE);
    				req.setId(Integer.parseInt(id));
    				req.setFreightType(mFreightType);
    				req.setLogisticId(0);
    				return true;
    			}
    		};
    		try {
    			FreightResp.Builder response = net.request();
    			if(net.isSuccess(response)){
    				mEndlessAdapter.endLoad(true);
                    List<ProtoEFreight> freightList = response.getFreightList();
                    if (freightList == null || freightList.isEmpty()) {
                        mEndlessAdapter.setHasMore(false);
                        return;
                    }
                    List<Freight> freight = new ArrayList<Freight>();
                    for (ProtoEFreight freights : freightList) {
                        freight.add(FreightParser.parse(freights));
                    }
                    mAdapter.addAll(freight);
    			}
    		} catch (Exception e) {
    			mEndlessAdapter.endLoad(false);
    			e.printStackTrace();
    		}
//            NetSearchFreight net = new NetSearchFreight(this, mStartRegionCode, mEndRegionCode, mFreightType,
//                    SIZE_LOAD_MORE, Integer.parseInt(id), 0);
//            net.request(new OnNetRequestListener<Eps.FreightResp.Builder>() {
//
//                @Override
//                public void onError() {
//                    mEndlessAdapter.endLoad(false);
//                }
//
//                @Override
//                public void onFail(String msg) {
//                    mEndlessAdapter.endLoad(false);
//                }
//
//                @Override
//                public void onSuccess(Builder response) {
//                    mEndlessAdapter.endLoad(true);
//                    List<ProtoEFreight> freightList = response.getFreightList();
//                    if (freightList == null || freightList.isEmpty()) {
//                        mEndlessAdapter.setHasMore(false);
//                        return;
//                    }
//                    List<Freight> freight = new ArrayList<Freight>();
//                    for (ProtoEFreight freights : freightList) {
//                        freight.add(FreightParser.parse(freights));
//                    }
//                    mAdapter.addAll(freight);
//                }
//            });
        } else {
            // TODO 针对配货市场的搜索
            if (mAdapter.isEmpty()) {
                return;
            }
            final long time = mAdapter.getItem(mAdapter.getCount() - 1).getCreate_time();
            final int market_freight_index = mAdapter.getItem(mAdapter.getCount() - 1).getMarket_screen_freight_id();
            NetOlderFreightsAdjoinMarket net = new NetOlderFreightsAdjoinMarket() {

				@Override
				protected boolean onSetRequest(
						com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder req) {
					req.setMarketId(Integer.parseInt(mMarket.getId()));
    		        req.setLimitCount(10);
    		        req.setCreateDate(time);
    		        req.setFreightType(Properties.FREIGHT_TYPE_ALL);
    		        req.setId(market_freight_index);
    		        req.setRouteCodeA(mStartRegionCode);
    		        req.setRouteCodeB(mEndRegionCode);
    				return true;
				}
    		};
    		try {
    			CommonLogisticsResp.Builder response = net.request();
    			if(net.isSuccess(response)){
    				mEndlessAdapter.endLoad(true);
                    List<ProtoEMarketScreen> freightList = response.getMarketScreenList();
                    if (freightList == null || freightList.isEmpty()) {
                        mEndlessAdapter.setHasMore(false);
                        return;
                    }
                    List<Freight> result = new ArrayList<Freight>();
                    for (ProtoEMarketScreen item : freightList) {
                        result.add(FreightParser.parse(item));
                    }
                    mAdapter.addAll(result);
    			}
    		} catch (Exception e) {
    			mEndlessAdapter.endLoad(false);
    			e.printStackTrace();
    		}
//            NetOlderFreightsAdjoinMarket net = new NetOlderFreightsAdjoinMarket(this,
//                    Integer.parseInt(mMarket.getId()), time, market_freight_index, 10, mFreightType, mStartRegionCode,
//                    mEndRegionCode);
//            net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
//
//                @Override
//                public void onSuccess(CommonLogisticsResp.Builder response) {
//                    mEndlessAdapter.endLoad(true);
//                    List<ProtoEMarketScreen> freightList = response.getMarketScreenList();
//                    if (freightList == null || freightList.isEmpty()) {
//                        mEndlessAdapter.setHasMore(false);
//                        return;
//                    }
//                    List<Freight> result = new ArrayList<Freight>();
//                    for (ProtoEMarketScreen item : freightList) {
//                        result.add(FreightParser.parse(item));
//                    }
//                    mAdapter.addAll(result);
//                }
//
//                @Override
//                public void onFail(String msg) {
//                    mEndlessAdapter.endLoad(false);
//                }
//
//                @Override
//                public void onError() {
//                    mEndlessAdapter.endLoad(false);
//                }
//            });
        }

    }

    private void hideChooseFreightType() {
        mChooseContainer.setVisibility(View.GONE);
        mChooseFreightTypeLayout.setVisibility(View.GONE);
        mChoosableTv01.setSelected(false);
        mUnderLine01.setSelected(false);
    }

    private void hideChooseLine() {
        mChooseContainer.setVisibility(View.GONE);
        mChooseLineLayout.setVisibility(View.GONE);
        mChoosableTv02.setSelected(false);
        mUnderLine02.setSelected(false);
    }

    private void showChooseFreightType() {
        hideChooseLine();
        mChooseContainer.setVisibility(View.VISIBLE);
        mChooseFreightTypeLayout.setVisibility(View.VISIBLE);
        mChoosableTv01.setSelected(true);
        mUnderLine01.setSelected(true);
    }

    private void showChooseLine() {
        hideChooseFreightType();
        mChooseContainer.setVisibility(View.VISIBLE);
        mChooseLineLayout.setVisibility(View.VISIBLE);
        mChoosableTv02.setSelected(true);
        mUnderLine02.setSelected(true);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), mPlatformName, null).setShowLogo(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO
        mMarketId = getIntent().getStringExtra(EXTRA_MARKET_ID);
        mMarket = (User) getIntent().getSerializableExtra(EXTRA_MARKET);
        // flag = getIntent().getStringExtra(EXTRA_FLAG);
        if (mMarketId == null || "-1".equals(mMarketId)) {
            mPlatformName = "易配送";
        } else {
            mPlatformName = mMarket.getShow_name();
        }

        super.onCreate(savedInstanceState);
        int sign = 1;
        if (sign == 1) {
            sign = 2;
        } else {
            searchContacts();
            sign = 1;
        }
        setContentView(R.layout.activity_search_the_source_supply_of_cars);
        View view01 = findViewById(R.id.fl_choosable_001);
        mChoosableTv01 = (TextView) view01.findViewById(R.id.tv_choosable0);
        mChoosableTv01.setText("全部车源货源");
        mUnderLine01 = view01.findViewById(R.id.view_under_line0);
        view01.setOnClickListener(this);
        View view02 = findViewById(R.id.fl_choosable_002);
        mChoosableTv02 = (TextView) view02.findViewById(R.id.tv_choosable0);
        mChoosableTv02.setText("线路不限");
        mUnderLine02 = view02.findViewById(R.id.view_under_line0);
        view02.setOnClickListener(this);

        mChooseContainer = findViewById(R.id.fl_choose_container0);
        mChooseContainer.setOnClickListener(this);
        mChooseFreightTypeLayout = (ChooseFreightTypeLayout) findViewById(R.id.choose_freight_type_layout0);
        mChooseFreightTypeLayout.setOnChooseFreightTypeListener(this);
        mChooseLineLayout = (ChooseLineLayout) findViewById(R.id.choose_line_layout0);
        mChooseLineLayout.setActivity(this);
//        mChooseLineLayout.setFilter(ChooseRegionActivity.FILTER_0_2);
        mChooseLineLayout.setOnChooseLineListener(this);

        lv = (ListView) findViewById(R.id.lv);
        mEndlessAdapter = new EndlessAdapter(getApplicationContext(), mAdapter);
        mEndlessAdapter.setOnLoadMoreListener(this);
        mEndlessAdapter.setIsAutoLoad(false);
        mEndlessAdapter.setHasMore(true);
        lv.setAdapter(mEndlessAdapter);
        lv.setOnItemClickListener(this);

        view_empty = (TextView) findViewById(R.id.view_empty);
        view_empty.setText(null);
        lv.setEmptyView(view_empty);
        bt_search = (Button) findViewById(R.id.bt_search);
        bt_search.setOnClickListener(this);

        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.epeisong.ui.activity.refreshLish"); // 只有持有相同的action的接受者才能接收此广播
        this.registerReceiver(receiveBroadCast, filter);
    }

}
