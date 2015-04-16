package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
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
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.SystemUtils;


public class VechileInforActivity extends BaseActivity implements
OnClickListener, OnItemClickListener, OnChooseLineListener,
OnChooseFreightTypeListener, OnLoadMoreListener {

//	private class MyAdapter extends HoldDataBaseAdapter<Freight> {
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//
//			ViewHolder holder = null;
//			if (convertView == null) {
//				convertView = SystemUtils
//						.inflate(R.layout.item_search_the_source_supply_of_cars);
//				holder = new ViewHolder();
//				holder.findView(convertView);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//			Freight f = getItem(position);
//			holder.fillData(f);
//			return convertView;
//		}
//	}
//
//	private class ViewHolder {
//
//		ImageView iv_type;
//		TextView tv_start;
//		TextView tv_end;
//		TextView tv_gongsi;
//		TextView tv_time;
//		TextView tv_xiangqing;
//
//		public void fillData(Freight f) {
//
//			if (f.getType() == Freight.TYPE_GOODS
//					&& f.getOrder_status() == Freight.ORDER_STATUS_UN_ORDER) {
//				// 货源未被订
//				iv_type.setImageResource(R.drawable.selector_borad_goods);
//			} else if (f.getType() == Freight.TYPE_TRUCK
//					&& f.getOrder_status() == Freight.ORDER_STATUS_UN_ORDER) {
//				// 车源未被订
//				iv_type.setImageResource(R.drawable.selector_board_truck);
//			} else if (f.getType() == Freight.TYPE_GOODS
//					&& f.getOrder_status() == Freight.ORDER_STATUS_ORDERED) {
//				// 货源已被订
//				iv_type.setImageResource(R.drawable.selector_booked_goods);
//			} else if (f.getType() == Freight.TYPE_TRUCK
//					&& f.getOrder_status() == Freight.ORDER_STATUS_ORDERED) {
//				// 车源已被订
//				iv_type.setImageResource(R.drawable.selector_booked_truck);
//			} else {
//				iv_type.setImageResource(R.drawable.ic_launcher);
//			}
//
//			tv_start.setText(f.getStart_region());
//			tv_end.setText(f.getEnd_region());
//			tv_gongsi.setText(f.getOwner_name());
//			tv_time.setText((DateUtil.long2vague(f.getCreate_time())));
//			tv_xiangqing.setText(f.getDesc());
//			// 删除或耳机根据此方法判断
//			/*
//			 * if (f.getUser_id() == UserDao.getInstance().getUser().getId()) {
//			 * }
//			 */
//		}
//
//		public void findView(View v) {
//			iv_type = (ImageView) v.findViewById(R.id.iv_type);
//			tv_start = (TextView) v.findViewById(R.id.tv_start);
//			tv_end = (TextView) v.findViewById(R.id.tv_end);
//			tv_gongsi = (TextView) v.findViewById(R.id.tv_gongsi);
//			tv_time = (TextView) v.findViewById(R.id.tv_time);
//			tv_xiangqing = (TextView) v.findViewById(R.id.tv_xiangqing);
//		}
//	}

	public static final String EXTRA_FLAG = "flag";

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
	private String flag;

	FreightListAdapter mAdapter = new FreightListAdapter();

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
			}
		}
		hideChooseFreightType();
	}

	@Override
	public void onChoosedLine(RegionResult start, RegionResult end) {
		if (start != null && end != null) {
			mChoosableTv02.setText(start.getFullName() + "-"
					+ end.getFullName());
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
		onSearchBtn();
	}

	@Override
	public void onClick(View v) {
		Object tag = v.getTag();
		if (tag != null && tag instanceof Freight) {
			Freight f = (Freight) tag;
			String user_id = f.getUser_id();
			String freight_id = f.getId();
			Intent intent = new Intent(VechileInforActivity.this,
					ChatRoomActivity.class);
			intent.putExtra(ChatRoomActivity.EXTRA_REMOTE_ID, user_id);
			intent.putExtra(ChatRoomActivity.EXTRA_BUSINESS_TYPE,
					ChatMsg.business_type_freight);
			intent.putExtra(ChatRoomActivity.EXTRA_BUSINESS_ID, freight_id);
			startActivity(intent);
			return;
		}

		switch (v.getId()) {
		case R.id.fl_choose_container0:
			mChooseContainer.setVisibility(View.GONE);
			//hideChooseFreightType();
			hideChooseLine();
			break;
		case R.id.fl_choosable_001:
			if (mChooseFreightTypeLayout.getVisibility() == View.GONE) {
				showChooseFreightType();
			} else {
				//hideChooseFreightType();
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
			//onSearchBtn();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		if (mMarketId == null || "-1".equals(mMarketId)) {

		} else {

		}
		Freight f = mAdapter.getItem(position);
		if (true) {
			Intent intent = new Intent(getApplicationContext(),
					FreightDetailActivity.class);
			intent.putExtra(FreightDetailActivity.EXTRA_FREIGHT, f);
			BusinessChatModel model = new BusinessChatModel();
			model.setBusiness_type(ChatMsg.business_type_freight);
			model.setBusiness_id(f.getId());
			model.setBusiness_desc(f.getStart_region() + "-"
					+ f.getEnd_region());
			model.setBusiness_extra(String.valueOf(f.getType()));
			model.setBusiness_owner_id(f.getUser_id());
			intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL,
					model);
			intent.putExtra(FreightDetailActivity.EXTRA_CAN_DELETE, false);
			startActivity(intent);
			return;
		}
	}

	private void onSearchBtn()
	{
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
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
			
//			NetSearchFreight net = new NetSearchFreight(this,
//					mStartRegionCode, mEndRegionCode, mFreightType,
//					SIZE_LOAD_FIRST, 0, 0);
//			net.request(new OnNetRequestListenerImpl<Eps.FreightResp.Builder>() {
//				@Override
//				public void onSuccess(FreightResp.Builder response) {
//					List<Freight> result = FreightParser.parse(response);
//					if (result == null || result.isEmpty()) {
//						mAdapter.clear();
//						view_empty.setText("没有数据");
//						return;
//					}
//					mEndlessAdapter.setHasMore(result.size() >= SIZE_LOAD_FIRST);
//					mAdapter.replaceAll(result);
//				}
//			});
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
    		        req.setRouteCodeA(0);
    		        req.setRouteCodeB(0);
    				return true;
				}
    		};
    		try {
    			CommonLogisticsResp.Builder response = net.request();
    			if(net.isSuccess(response)){
    				List<ProtoEMarketScreen> freightList = response
							.getMarketScreenList();
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
    			  }
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
//			NetLatestFreightsOnMarket net = new NetLatestFreightsOnMarket(
//					this, marketId, SIZE_LOAD_FIRST, 0, mFreightType ,0 ,0);
//			net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
//				@Override
//				public void onSuccess(CommonLogisticsResp.Builder response) {
//					List<ProtoEMarketScreen> freightList = response
//							.getMarketScreenList();
//					if (freightList == null || freightList.isEmpty()) {
//						// showMessageDialog(null, "该配货市场没有发布配货信息");
//						mAdapter.clear();
//						view_empty.setText("没有数据");
//						return;
//					}
//					List<Freight> result = new ArrayList<Freight>();
//					for (ProtoEMarketScreen item : freightList) {
//						result.add(FreightParser.parse(item));
//					}
//					mEndlessAdapter.setHasMore(result.size() >= SIZE_LOAD_FIRST);
//					mAdapter.replaceAll(result);
//				}
//
//			});
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
			
//			NetSearchFreight net = new NetSearchFreight(this, mStartRegionCode,
//					mEndRegionCode, mFreightType, SIZE_LOAD_MORE,
//					Integer.parseInt(id), 0);
//			net.request(new OnNetRequestListener<Eps.FreightResp.Builder>() {
//
//				@Override
//				public void onError() {
//					mEndlessAdapter.endLoad(false);
//				}
//
//				@Override
//				public void onFail(String msg) {
//					mEndlessAdapter.endLoad(false);
//				}
//
//				@Override
//				public void onSuccess(Builder response) {
//					mEndlessAdapter.endLoad(true);
//					List<ProtoEFreight> freightList = response.getFreightList();
//					if (freightList == null || freightList.isEmpty()) {
//						mEndlessAdapter.setHasMore(false);
//						return;
//					}
//					List<Freight> freight = new ArrayList<Freight>();
//					for (ProtoEFreight freights : freightList) {
//						freight.add(FreightParser.parse(freights));
//					}
//					mAdapter.addAll(freight);
//				}
//			});
		} else {
			// TODO 针对配货市场的搜索
			if (mAdapter.isEmpty()) {
				return;
			}
			final long time = mAdapter.getItem(mAdapter.getCount() - 1)
					.getCreate_time();
			final int market_freight_index = mAdapter
					.getItem(mAdapter.getCount() - 1)
					.getMarket_screen_freight_id();
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
//			NetOlderFreightsAdjoinMarket net = new NetOlderFreightsAdjoinMarket(
//					this, Integer.parseInt(mMarketId), time,
//					market_freight_index, 10, mFreightType ,mStartRegionCode, mEndRegionCode);
//			net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
//
//				@Override
//				public void onSuccess(CommonLogisticsResp.Builder response) {
//					mEndlessAdapter.endLoad(true);
//					List<ProtoEMarketScreen> freightList = response
//							.getMarketScreenList();
//					if (freightList == null || freightList.isEmpty()) {
//						mEndlessAdapter.setHasMore(false);
//						return;
//					}
//					List<Freight> result = new ArrayList<Freight>();
//					for (ProtoEMarketScreen item : freightList) {
//						result.add(FreightParser.parse(item));
//					}
//					mAdapter.addAll(result);
//				}
//
//				@Override
//				public void onFail(String msg) {
//					mEndlessAdapter.endLoad(false);
//				}
//
//				@Override
//				public void onError() {
//					mEndlessAdapter.endLoad(false);
//				}
//			});
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
		//hideChooseFreightType();
		mChooseContainer.setVisibility(View.VISIBLE);
		mChooseLineLayout.setVisibility(View.VISIBLE);
		mChoosableTv02.setSelected(true);
		mUnderLine02.setSelected(true);
	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "车辆信息", null)
		.setShowLogo(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mChooseLineLayout.onActivityResult(requestCode, resultCode, data)) {
			return;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO
		mMarketId = getIntent().getStringExtra(ContactsDetailActivity.EXTRA_USER_ID);
		mMarket = (User) getIntent().getSerializableExtra(ContactsDetailActivity.EXTRA_USER);
		flag = getIntent().getStringExtra(EXTRA_FLAG);
		if (mMarketId == null || "-1".equals(mMarketId)) {
			mPlatformName = "车辆信息";
		} else {
			mPlatformName = mMarket.getShow_name();
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_the_source_supply_of_cars);
		View view01 = findViewById(R.id.fl_choosable_001);
		view01.setVisibility(View.GONE);
//		mChoosableTv01 = (TextView) view01.findViewById(R.id.tv_choosable0);
//		mChoosableTv01.setText("全部车源货源");
//		mUnderLine01 = view01.findViewById(R.id.view_under_line0);
//		view01.setOnClickListener(this);
		
		View view02 = findViewById(R.id.fl_choosable_002);
		mChoosableTv02 = (TextView) view02.findViewById(R.id.tv_choosable0);
		mChoosableTv02.setText("线路不限");
		mUnderLine02 = view02.findViewById(R.id.view_under_line0);
		view02.setOnClickListener(this);

		mChooseContainer = findViewById(R.id.fl_choose_container0);
		mChooseContainer.setOnClickListener(this);
		//mChooseFreightTypeLayout = (ChooseFreightTypeLayout) findViewById(R.id.choose_freight_type_layout0);
		//mChooseFreightTypeLayout.setOnChooseFreightTypeListener(this);
		mChooseLineLayout = (ChooseLineLayout) findViewById(R.id.choose_line_layout0);
		mChooseLineLayout.setActivity(this);
		mChooseLineLayout.setOnChooseLineListener(this);

		lv = (ListView) findViewById(R.id.lv);
		mEndlessAdapter = new EndlessAdapter(getApplicationContext(), mAdapter);
		mEndlessAdapter.setOnLoadMoreListener(this);
		mEndlessAdapter.setIsAutoLoad(true);
		mEndlessAdapter.setHasMore(true);
		lv.setAdapter(mEndlessAdapter);
		lv.setOnItemClickListener(this);

		view_empty = (TextView) findViewById(R.id.view_empty);
		view_empty.setText(null);
		lv.setEmptyView(view_empty);
		bt_search = (Button) findViewById(R.id.bt_search);
		bt_search.setOnClickListener(this);
		bt_search.setVisibility(View.GONE);
		
		mFreightType = Freight.TYPE_TRUCK;
		onSearchBtn();
	}

}
