package com.epeisong.ui.fragment;

import java.io.Serializable;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshListView;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.universal_image_loader.ImageLoaderUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.R.id;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder;
import com.epeisong.model.MarketMember;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.model.UserRole;
import com.epeisong.net.request.NetSearchMarkets;
import com.epeisong.ui.activity.ChooseRegionActivity;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.ui.activity.InMarketActivity;
import com.epeisong.ui.activity.InfoScreenActivity;
import com.epeisong.ui.activity.MarketOfFreightActivity;
import com.epeisong.ui.view.ChooseLineLayout;
import com.epeisong.ui.view.ChooseLineLayout.OnChooseLineListener;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 联系人 - 配载信息部会员 
 * @author Jack
 *
 */
public class StowageInformationFragment extends Fragment implements OnChooseLineListener,
OnClickListener, OnItemClickListener, OnLoadMoreListener {
	//private static final int SIZE_LOAD_MORE = 10;
	private static final int REQUEST_CODE_CHOOSE_REGION = 101;
	// private static final int REQUEST_CODE_CHOOSE_LINE = 102;
	private static final int SIZE_LOAD_FIRST = 10;
	private static View mChooseContainer;
	private static TextView mChoosableTv02;
	private int mStartRegionCode;
	private int mEndRegionCode;
	//private TextView view_empty;
	//private ListView lv;

	private static ChooseLineLayout mChooseLineLayout;
	protected MyAdapter mAdapter;
	private int Logistic_type;
	User mUser;
	private String mUserId;

	protected EndlessAdapter mEndlessAdapter;
	
    private PullToRefreshListView lv_black;
    //private ListView mListView;
    private TextView mTextViewEmpty;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Bundle args = getArguments();
		mUser = (User) args.getSerializable(ContactsDetailActivity.EXTRA_USER);
		// if(mUser==null)
		// {
		// ToastUtils.showToast("参数错误");
		// return null;
		// }
		// else
		Logistic_type = args.getInt(ContactsDetailActivity.EXTRA_USER_TYPEID);
		// Logistic_type =
		// args.getInt(ContactsDetailActivity.EXTRA_MARKET_TYPEID);
		mUserId = args.getString(ContactsDetailActivity.EXTRA_USER_ID);

		View root = inflater.inflate(R.layout.activity_stowage_info, null);
		// root.setBackgroundColor(Color.WHITE);

		TextView textbottom = (TextView) root.findViewById(R.id.tv_quick_order);
		TextView textbottom1 = (TextView) root.findViewById(R.id.tv_information_screen);
		TextView twobtn20 = (TextView) root.findViewById(R.id.two_btn20);
		switch (Logistic_type) {
		case Properties.LOGISTIC_TYPE_EXPRESS://
		case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:// 选择地区
		{
			textbottom1.setText("快速下单");
			textbottom.setText("申请加入网点");
		}
		break;
		case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
			textbottom.setText("快速下单");
			textbottom1.setText("更多收货点");
			break;
		case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
			//textbottom1.setText("快速下单");//"申请入园");
			textbottom1.setVisibility(View.GONE);
			//textbottom1.setOnClickListener(this);
			textbottom.setVisibility(View.GONE);
			twobtn20.setVisibility(View.GONE);
			break;
		case Properties.LOGISTIC_TYPE_MARKET:// LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
			// textbottom1.setText("申请加入会员");//加入到基本信息中去
			textbottom.setVisibility(View.GONE);
			twobtn20.setVisibility(View.GONE);
			textbottom1.setText("信息电子屏");
			textbottom1.setOnClickListener(this);
			break;
		default:
			textbottom.setText("快速下单");
			textbottom1.setVisibility(View.GONE);
			twobtn20.setVisibility(View.GONE);
			break;
		}

		View view02 = root.findViewById(R.id.fl_choosable_002);
		mChoosableTv02 = (TextView) view02.findViewById(R.id.tv_choosable0);

		mChooseLineLayout = (ChooseLineLayout) root.findViewById(R.id.choose_line_layout0);
		//        mChooseLineLayout.setFilter(ChooseRegionActivity.FILTER_0_2);
		switch (Logistic_type) {
		case Properties.LOGISTIC_TYPE_EXPRESS://
		case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:// 选择地区
			mChoosableTv02.setText("地区不限");
			// view02.setVisibility(View.GONE);
			// mChoosableTv02.setVisibility(View.GONE);
			// mChooseLineLayout.setVisibility(View.GONE);
			break;
		default:
			mChoosableTv02.setText("线路不限");
			break;
		}
		view02.setOnClickListener(this);
		mChooseContainer = root.findViewById(R.id.fl_choose_container0);
		mChooseContainer.setOnClickListener(this);


		// mChooseServeRegionLayout = (ChooseServeRegionLayout)
		mChooseLineLayout.setFragment(this);
		mChooseLineLayout.setOnChooseLineListener(this);

//		lv = (EndlessEmptyListView) root.findViewById(R.id.lv);
//		mAdapter = new MyAdapter();
//		lv.setOnItemClickListener(this);
//
//		if (mAdapter != null) {
//			mEndlessAdapter = new EndlessAdapter(getActivity(), mAdapter);
//			mEndlessAdapter.setIsAutoLoad(true);
//			mEndlessAdapter.setOnLoadMoreListener(this);
//			lv.setAdapter(mEndlessAdapter);
//		}
//
//		view_empty = (TextView) root.findViewById(R.id.view_empty);
//		//view_empty.setText(null);
//		lv.setEmptyView(view_empty);

		
		lv_black = (PullToRefreshListView) root.findViewById(R.id.lv_borad_list);
		
		
        //mListView = lv_black.getRefreshableView();
        lv_black.setAdapter(mAdapter = new MyAdapter());
        lv_black.setMode(Mode.BOTH);
        lv_black.setOnItemClickListener(this);
        lv_black.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            	requestData(0, 10);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mAdapter.isEmpty()) {
                    HandlerUtils.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        	lv_black.onRefreshComplete();
                        }
                    }, 100);
                    return;
                }
                //String edge_id = mAdapter.getItem(mAdapter.getCount() - 1).getId();
                int lastSyncIndex = Integer.valueOf(mAdapter.getItem(mAdapter.getCount() - 1).getId());
                requestData(lastSyncIndex, 10);
            }
        });
		
        setEmptyView();
        requestData(0, 10);
		//loadData(SIZE_LOAD_FIRST, "0", 0, true);
		return root;
	}
	
    private void setEmptyView() {
        LinearLayout emptyLayout = new LinearLayout(getActivity());
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyLayout.setPadding(0, DimensionUtls.getPixelFromDpInt(100), 0, 0);
        mTextViewEmpty = new TextView(getActivity());
        mTextViewEmpty.setText("没有数据");
        mTextViewEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mTextViewEmpty.setTextColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
        mTextViewEmpty.setGravity(Gravity.CENTER);
        emptyLayout.addView(mTextViewEmpty);
        lv_black.setEmptyView(emptyLayout);
    }
    
    private void requestData(final int edge_id, final int size) {
    	final int marketId = Integer.parseInt(mUserId);
        AsyncTask<Void, Void, List<MarketMember>> task = new AsyncTask<Void, Void, List<MarketMember>>() {
        	@Override
        	protected List<MarketMember> doInBackground(Void... params) {
				
				NetSearchMarkets net = new NetSearchMarkets() {
					@Override
					protected int getCommandCode() {
						return CommandConstants.GET_MEMBERS_REQ;
					}

					@Override
					protected boolean onSetRequest(Builder req) {

						int id = 0;
						try {
							if (edge_id != 0) {
								id = edge_id;
							}
						} catch (Exception e) {
							id = 0;
						}
						req.setId(id);

						req.setRouteCodeA(mStartRegionCode);
						req.setRouteCodeB(mEndRegionCode);
						req.setMarketId(marketId);
						req.setLimitCount(size);
						//req.setId(marketId);
						req.setLogisticTypeCode(-1);
						return true;
					}
				};

				try {
					CommonLogisticsResp.Builder resp = net.request();
					if (resp == null) {
						return null;
					}
					return UserParser.parseMember(resp);
				} catch (NetGetException e) {
					e.printStackTrace();
				}
				return null;
			}

            @Override
            protected void onPostExecute(List<MarketMember> result) {

            	//result=null;
            	lv_black.onRefreshComplete();
//            	if(result==null || result.isEmpty()) {
//            		if (edge_id > 0) 
//            			ToastUtils.showToast("没有更多数据");
////            		else
////            			ToastUtils.showToast("没有数据");
//            	} else {
			
	                if (edge_id > 0) {
	                    mAdapter.addAll(result);
	                } else {
	                    mAdapter.replaceAll(result);
	                }
            //	}
            }
        };
        task.execute();
    }
    
	public static boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mChooseLineLayout.getVisibility() == View.VISIBLE) {
				hideChooseLine();
				return false;
			}
		}
		return true;
	}

	@Override
	public void onChoosedLine(RegionResult start, RegionResult end) {
		if (start != null && end != null) {
			mChoosableTv02.setText(start.getShortNameFromDistrict() + "-" + end.getShortNameFromDistrict());
			mStartRegionCode = start.getCode();
			mEndRegionCode = end.getCode();
		} else {
			mStartRegionCode = 0;
			mEndRegionCode = 0;
			mChoosableTv02.setText("线路不限");
		}
		requestData(0, SIZE_LOAD_FIRST);
		//loadData(SIZE_LOAD_FIRST, "0", 0, true);
		hideChooseLine();
	}

	private static void hideChooseLine() {
		mChooseContainer.setVisibility(View.GONE);
		mChooseLineLayout.setVisibility(View.GONE);
		mChoosableTv02.setSelected(false);
	}

	private void showChooseLine() {
		mChooseContainer.setVisibility(View.VISIBLE);
		mChooseLineLayout.setVisibility(View.VISIBLE);
		mChoosableTv02.setSelected(true);
	}

	@Override
	public void onClick(View v) {
		String flag = "manage_flag";
		switch (v.getId()) {
		case R.id.tv_information_screen:
			switch (Logistic_type) {
			case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
				Intent intent = new Intent();

				intent.setClass(this.getActivity(), InMarketActivity.class);
				intent.putExtra(ContactsDetailActivity.EXTRA_USER, mUser);
				intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, mUserId);
				intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, Logistic_type);
				startActivity(intent);
				break;
			case Properties.LOGISTIC_TYPE_MARKET:
				InfoScreenActivity.launch(getActivity(), mUser);
				//                Intent intent1 = new Intent();
				//
				//                intent1.setClass(this.getActivity(), InfoScreenActivity.class);
				//                intent1.putExtra(ContactsDetailActivity.EXTRA_USER, mUser);
				//                intent1.putExtra(ContactsDetailActivity.EXTRA_USER_ID, mUserId);
				//                intent1.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, Logistic_type);
				//                startActivity(intent1);
				break;
			}
			break;

		case R.id.tv_quick_order:
			Intent i = new Intent();
			i.setClass(this.getActivity(), MarketOfFreightActivity.class);
			i.putExtra(MarketOfFreightActivity.EXTRA_FLAG, flag);
			i.putExtra(MarketOfFreightActivity.EXTRA_MARKET, mUser);
			startActivity(i);
			// Intent intentm = new Intent(this.getActivity(),
			// ManageInfoActivity.class);
			// this.getActivity().startActivity(intentm);
			break;
		case R.id.fl_choose_container0:
			mChooseContainer.setVisibility(View.GONE);
			hideChooseLine();
			break;
		case R.id.fl_choosable_002:
			switch (Logistic_type) {
			case Properties.LOGISTIC_TYPE_EXPRESS://
			case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:// 选择地区
				// ChooseRegionActivity.launch(this,
				// ChooseRegionActivity.FILTER_0_2, REQUEST_CODE_CHOOSE_REGION);

				Intent intent = new Intent(this.getActivity(), ChooseRegionActivity.class);
				intent.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER, ChooseRegionActivity.FILTER_0_2);
				intent.putExtra(ChooseRegionActivity.EXTRA_IS_SHOW_NO_LIMIT, true);
				this.startActivityForResult(intent, REQUEST_CODE_CHOOSE_REGION);
				break;
				// case Properties.LOGISTIC_TYPE_MARKET:
				// case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
				// ChooseLineActivity.launch(this, REQUEST_CODE_CHOOSE_LINE);
				// break;
			default:
				if (mChooseLineLayout.getVisibility() == View.GONE) {
					showChooseLine();
				} else {
					hideChooseLine();
				}
				break;
			}
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (Logistic_type) {
		case Properties.LOGISTIC_TYPE_EXPRESS://
		case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:// 选择地区
			if (resultCode == Activity.RESULT_OK) {
				if (REQUEST_CODE_CHOOSE_REGION == requestCode) {
					Serializable extra = data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
					if (extra == null || !(extra instanceof RegionResult)) {
						return;
					}
					final RegionResult regionResult = (RegionResult) extra;
					UserRole role = new UserRole();
					role.setRegionCode(regionResult.getFullCode());
					role.setRegionName(regionResult.getGeneralName());
					if (regionResult != null) {
						mChoosableTv02.setText(regionResult.getShortNameFromDistrict());
						mStartRegionCode = regionResult.getCode();
//					} else {
//						mStartRegionCode = 0;
//						mEndRegionCode = 0;
//						mChoosableTv02.setText("地区不限");
					}
					requestData(0, 10);
					//loadData(10, "0", 0, true);
				}
			}
			break;
			// case Properties.LOGISTIC_TYPE_PARKING_LOT:
			// case Properties.LOGISTIC_TYPE_MARKET:
			// if (resultCode == Activity.RESULT_OK) {
			// if (REQUEST_CODE_CHOOSE_LINE == requestCode) {
			// Serializable extra1 =
			// data.getSerializableExtra(ChooseLineActivity.EXTRA_START_REGION);
			// Serializable extra2 =
			// data.getSerializableExtra(ChooseLineActivity.EXTRA_END_REGION);
			// if (extra1 != null && extra2 != null) {
			// RegionResult start = (RegionResult) extra1;
			// RegionResult end = (RegionResult) extra2;
			// mChoosableTv02.setText(start.getFullName() + "-" +
			// end.getFullName());
			// mStartRegionCode = start.getCode();
			// mEndRegionCode = end.getCode();
			//
			// loadData(10, "0", 0, true);
			// }
			// }
			// }
			// break;
		default:
			if (mChooseLineLayout.onActivityResult(requestCode, resultCode, data)) {
				return;
			}
			break;
		}
	}

	private void loadData(final int size, final String edge_id, final double weight, final boolean bFirst) {
		final int marketId = Integer.parseInt(mUserId);
		//String id = mAdapter.getItem(mAdapter.getCount() - 1).getId();

		AsyncTask<Void, Void, List<MarketMember>> task = new AsyncTask<Void, Void, List<MarketMember>>() {
			@Override
			protected List<MarketMember> doInBackground(Void... params) {
				NetSearchMarkets net = new NetSearchMarkets() {
					@Override
					protected int getCommandCode() {
						return CommandConstants.GET_MEMBERS_REQ;
					}

					@Override
					protected boolean onSetRequest(Builder req) {

						int id = 0;
						try {
							if (edge_id != null) {
								id = Integer.parseInt(edge_id);
							}
						} catch (Exception e) {
							id = 0;
						}
						req.setId(id);

						req.setRouteCodeA(mStartRegionCode);
						req.setRouteCodeB(mEndRegionCode);
						req.setMarketId(marketId);
						req.setLimitCount(size);
						//req.setId(marketId);
						req.setLogisticTypeCode(-1);
						return true;
					}
				};

				try {
					CommonLogisticsResp.Builder resp = net.request();
					if (resp == null) {
						return null;
					}
					return UserParser.parseMember(resp);
				} catch (NetGetException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<MarketMember> result) {
				//dismissPendingDialog();
				if (result != null) {
					if (result.isEmpty()) {
						mEndlessAdapter.setHasMore(false);
						if (bFirst) {
							ToastUtils.showToast("没有数据");
							mAdapter.clear();
						} else {
							mEndlessAdapter.endLoad(true);
						}
					} else {
						mEndlessAdapter.setHasMore(result.size() >= size);
						if (bFirst) {
							mAdapter.replaceAll(result);
						} else {
							mAdapter.addAll(result);
							mEndlessAdapter.endLoad(true);
						}
					}
				} else {
					if (!bFirst) {
						mEndlessAdapter.endLoad(false);
					} else
						mAdapter.clear();
				}
			}
		};
		task.execute();
		if (bFirst) {
			//showPendingDialog(null);
		}

	}

	@Override
	public void onStartLoadMore(EndlessAdapter adapter) {
		String edge_id = mAdapter.getItem(mAdapter.getCount() - 1).getId();
		//weightScore = mAdapter.getItem(mAdapter.getCount() - 1).getUserRole().getWeight();
		loadData(10, edge_id, 0, false);// remember to add
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// Dint iit = lv.getHeaderViewsCount();
		MarketMember marketMember = mAdapter.getItem(arg2-1);
		User user = marketMember.getUser();
		Intent intent = new Intent();
		intent.setClass(getActivity(), ContactsDetailActivity.class);

		intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, user.getId());
		intent.putExtra(ContactsDetailActivity.EXTRA_USER, user);
		intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, user.getUser_type_code());
		startActivity(intent);
	}

	public static void IV_setImageResource(ImageView iv_market_logo, int User_type_code) {
		switch (User_type_code) {// Logistic_type) {
		case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
			iv_market_logo.setImageResource(R.drawable.home_inv_goods);
			break;
		case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
			// 整车运输
			// iv_market_logo.setBackgroundResource(R.drawable.home_ftl);
			iv_market_logo.setImageResource(R.drawable.home_ftl);
			break;
		case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
			// 零担专线
			iv_market_logo.setImageResource(R.drawable.home_lcl);
			break;
		case Properties.LOGISTIC_TYPE_EXPRESS:
			// 快递
			iv_market_logo.setImageResource(R.drawable.home_fast_mail);
			break;
		case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
			iv_market_logo.setImageResource(R.drawable.more_citydistribution);///////////同城配送
			break;
		case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS:
			iv_market_logo.setImageResource(R.drawable.home_third_part);///////////第三方物流
			break;
		case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
			iv_market_logo.setImageResource(R.drawable.icon_logistics_park);///////////物流园
			break;
		case Properties.LOGISTIC_TYPE_STORAGE:
			iv_market_logo.setImageResource(R.drawable.more_storage);///////////仓储
			break;
		case Properties.LOGISTIC_TYPE_PACKAGING:
			iv_market_logo.setImageResource(R.drawable.more_packaging);///////////包装
			break;
		case Properties.LOGISTIC_TYPE_MOVE_HOUSE:
			iv_market_logo.setImageResource(R.drawable.more_move_house);///////////搬家
			break;
		case Properties.LOGISTIC_TYPE_INSURANCE:
			// 保险
			iv_market_logo.setImageResource(R.drawable.home_insurance);
			break;
		case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
			// 配载信息部
			iv_market_logo.setImageResource(R.drawable.home_information);
			break;
		case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
			// 设备租赁
			iv_market_logo.setImageResource(R.drawable.home_device_lease);
			break;
		}
	}
	
	protected class MyAdapter extends HoldDataBaseAdapter<MarketMember> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.item_search_market);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.fillData(getItem(position), position);
			return convertView;
		}
	}

	private class ViewHolder {
		public ImageView iv_market_logo;
		public TextView tv_market_name;
		public TextView tv_market_intro;
		public TextView tv_market_addr;
		private RatingBar ratingBar;

		public void fillData(MarketMember marketMemberu, int pos) {
			// 参考 HomeFragment
			if (marketMemberu == null) {
				return;
			}
			User u=marketMemberu.getUser();
			if (u == null) {
				return;
			}
			int temptype = u.getUser_type_code();
			
            if (!TextUtils.isEmpty(u.getLogo_url())) {
                ImageLoader.getInstance().displayImage(u.getLogo_url(), iv_market_logo,
                        ImageLoaderUtils.getListOptionsForUserLogo());
            } else {
                iv_market_logo.setImageResource(User.getDefaultIcon(u.getUser_type_code(), true));
            	IV_setImageResource(iv_market_logo, temptype);
            }
            
			//iv_market_logo.setImageResource(R.drawable.contacts_logo_default);
//			switch (temptype) {// Logistic_type) {
//			case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					iv_market_logo.setImageResource(R.drawable.home_inv_goods);
//				}
//				break;
//			case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
//				// 整车运输
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					// iv_market_logo.setBackgroundResource(R.drawable.home_ftl);
//					iv_market_logo.setImageResource(R.drawable.home_ftl);
//				}
//				break;
//			case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
//				// 零担专线
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					iv_market_logo.setImageResource(R.drawable.home_lcl);
//				}
//				break;
//			case Properties.LOGISTIC_TYPE_EXPRESS:
//				// 快递
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					iv_market_logo.setImageResource(R.drawable.home_fast_mail);
//				}
//				break;
//			case Properties.LOGISTIC_TYPE_INSURANCE:
//				// 保险
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					iv_market_logo.setImageResource(R.drawable.home_insurance);
//				}
//				break;
//			case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
//				// 配载信息部
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					iv_market_logo.setImageResource(R.drawable.home_information);
//				}
//				break;
//			case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
//				// 设备租赁
//				if (TextUtils.isEmpty(u.getLogo_url())) {
//					iv_market_logo.setImageResource(R.drawable.home_device_lease);
//				}
//				break;
//			}

			tv_market_name.setText(u.getShow_name());
			tv_market_intro.setText(u.getuserintroducation());
			// tv_market_intro.setText(u.getSelf_intro());
			tv_market_addr.setText(u.getAddress());
			ratingBar.setProgress(u.getStar_level());
		}

		public void findView(View v) {
			iv_market_logo = (ImageView) SystemUtils.find(v, R.id.iv_user_logo);
			tv_market_name = (TextView) SystemUtils.find(v, R.id.tv_market_name);
			tv_market_intro = (TextView) SystemUtils.find(v, R.id.tv_user_intro_content);
			tv_market_addr = (TextView) SystemUtils.find(v, R.id.tv_market_addr);
			ratingBar = (RatingBar) SystemUtils.find(v, R.id.ratingBar);
		}
	}

}
