package com.epeisong.ui.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.AdjustHeightGridView;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetFreightDetail;
import com.epeisong.data.net.NetGuarantee;
import com.epeisong.data.net.NetUpdateFreightWhetherCanPostToMarketScreenStatus;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.data.net.parser.GuaranteeParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightReq.Builder;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.model.Freight;
import com.epeisong.model.Guarantee;
import com.epeisong.model.User;
import com.epeisong.net.request.NetUpdateFreightStatus;
import com.epeisong.net.request.OnNetRequestListener;
import com.epeisong.ui.activity.BlackBoardActivity;
import com.epeisong.ui.activity.ChooseNewContactsActivity;
import com.epeisong.ui.activity.NoticeOrRelayActivity;
import com.epeisong.ui.view.SwitchButton;
import com.epeisong.ui.view.SwitchButton.OnSwitchListener;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.android.AsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyCarSourceFragment extends Fragment implements OnSwitchListener,
		OnClickListener {

	public static final String EXTRA_FREIGHT = "mFreight";
	private static final int REQUEST_CHOOSE_CONTACTS_NOTICE = 100;
	public static final String EXTRA_USER_ID = "user_id";
	public static final String EXTRA_FREIGHT_ID = "freight_id";
	public static final String EXTRA_IS_HIDE_DELETE_BTN = "is_hide_delete_btn";

//	private ImageView iv_zt;
	private TextView tv_state;
	private ImageView iv_goods;
	private TextView tv_time;
	private TextView tv_region;
	private TextView tv_describe;
	// private TextView tv_money;
	private LinearLayout bt_forwarding;
	// private LinearLayout bt_immediately_consult;
	private TextView tv_user_show_name;
	private TextView tv_contact_name;
	private TextView tv_phone_number;
	private TextView tv_telephone_number;
	private Button bt_delete;
	private SwitchButton mSwitchButton;

	private Freight mFreight;
	private String user_id;
	private int mFreight_id;
	private User user;
	private String flag;

	private AdjustHeightGridView mGridView;
	private MyAdapter mAdapter;
	/*
	 * @Override protected TitleParams getTitleParams() { // TODO Auto-generated
	 * method stub return new TitleParams(getDefaultHomeAction(), "车源详情", null)
	 * .setShowLogo(false); }
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((resultCode == Activity.RESULT_OK || resultCode == 12) && requestCode == REQUEST_CHOOSE_CONTACTS_NOTICE) {
			Intent intent = new Intent(getActivity(),NoticeOrRelayActivity.class);
			intent.putExtra(NoticeOrRelayActivity.EXTRA_ACTION_TYPE,NoticeOrRelayActivity.ACTION_NOTICE);
			intent.putExtra(NoticeOrRelayActivity.EXTRA_DISPATCH, mFreight);
			intent.putExtra(NoticeOrRelayActivity.EXTRA_CONTACTS_LIST,data.getSerializableExtra(ChooseNewContactsActivity.EXTRA_SELECTED_CONTACTS_LIST));
			startActivity(intent);
		}
	}

	private void updateOrderStatus() {
		if (mFreight == null) {
			return;
		}
		switch (mFreight.getStatus()) {
		case Properties.FREIGHT_STATUS_NO_PROCESSED:
			iv_goods.setImageResource(R.drawable.black_board_goods);
			tv_state.setText("该车待配货");
			break;
		case Properties.FREIGHT_STATUS_BOOK:
			iv_goods.setImageResource(R.drawable.black_board_goods);
			bt_forwarding.setEnabled(true);
			tv_state.setText("该车被预订");
			break;

		default:
			iv_goods.setImageResource(R.drawable.black_board_truck);
			bt_forwarding.setBackgroundColor(Color.argb(255, 192, 192, 192));
			bt_forwarding.setEnabled(false);
			tv_state.setText("该信息已成交");
			break;
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		mFreight = (Freight) args.getSerializable(EXTRA_FREIGHT);
		user_id = args.getString(EXTRA_USER_ID);
		mFreight_id = Integer.parseInt(args.getString(EXTRA_FREIGHT_ID));
		flag = args.getString("flag");

		super.onCreate(savedInstanceState);
		View root = inflater.inflate(R.layout.activity_my_car_source, null);
		tv_time = (TextView) root.findViewById(R.id.tv_time);
		tv_region = (TextView) root.findViewById(R.id.tv_region);
		tv_describe = (TextView) root.findViewById(R.id.tv_describe);
		// tv_money=(TextView)root.findViewById(R.id.tv_money);
		bt_forwarding = (LinearLayout) root.findViewById(R.id.bt_forwarding);
		// bt_immediately_consult = (LinearLayout) root
		// .findViewById(R.id.bt_immediately_consult);
		tv_user_show_name = (TextView) root
				.findViewById(R.id.tv_user_show_name);
		tv_contact_name = (TextView) root.findViewById(R.id.tv_contact_name);
		tv_phone_number = (TextView) root.findViewById(R.id.tv_phone_number);
		tv_telephone_number = (TextView) root
				.findViewById(R.id.tv_telephone_number);
		bt_delete = (Button) root.findViewById(R.id.bt_delete);
//		iv_zt = (ImageView) root.findViewById(R.id.iv_zt);
		tv_state = (TextView) root.findViewById(R.id.tv_state_content);
		iv_goods = (ImageView) root.findViewById(R.id.iv_goods);
		mSwitchButton = (SwitchButton) root.findViewById(R.id.switch_button);
		mSwitchButton.setOnSwitchListener(this);
		mSwitchButton.setSwitchText("是", "否", true);
		
		mGridView = (AdjustHeightGridView) root.findViewById(R.id.gv_img);
		mGridView.setNumColumns(4);
		int p = DimensionUtls.getPixelFromDpInt(10);
		mGridView.setPadding(0, p, 0, 0);
		mGridView.setSelector(R.color.transparent);
		mGridView.setBackgroundColor(Color.WHITE);
		mGridView.setAdapter(mAdapter = new MyAdapter());
		
		User user = UserDao.getInstance().getUser();
		if (user != null) {
			tv_contact_name.setText(user.getContacts_name());
			tv_phone_number.setText(user.getContacts_phone());
			tv_telephone_number.setText(user.getContacts_telephone());
		}

		bt_forwarding.setOnClickListener(this);
		// bt_immediately_consult.setOnClickListener(this);
		bt_delete.setOnClickListener(this);
		// 搜索车源货源如果是自己的，删除按钮隐藏
		if (args.getBoolean(EXTRA_IS_HIDE_DELETE_BTN, false)) {
			bt_delete.setVisibility(View.GONE);
			// 是否发送到配货市场按钮
			// switch_button.setVisibility(View.GONE);
			// findViewById(R.id.rl_hide).setVisibility(View.GONE);
			// findViewById(R.id.view_hide).setVisibility(View.GONE);
		}

		AsyncTask<Void, Void, Freight> task = new AsyncTask<Void, Void, Freight>() {
			@Override
			protected Freight doInBackground(Void... params) {

				NetFreightDetail net = new NetFreightDetail() {

					@Override
					protected boolean onSetRequest(
							com.epeisong.logistics.proto.Eps.FreightReq.Builder req) {
						// TODO Auto-generated method stub
						req.setFreightId(mFreight_id);
						return true;
					}
				};
				try {
					FreightResp.Builder resp = net.request();
					if (resp.getResult().equals("SUCC")) {
						return FreightParser.parseSingle(resp);
					}
				} catch (NetGetException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Freight result) {

				if (result != null) {

					mFreight = result;
					updateOrderStatus();
					if (mFreight == null) {
						return;
					}

					tv_time.setText(DateUtil.long2YMDHM(mFreight
							.getCreate_time()));
					// 起始地址，终止地址
					tv_region.setText(mFreight.getStart_region() + " — "
							+ mFreight.getEnd_region());
					tv_describe.setText(mFreight.getDesc());
					/************ 我的车源详情信息费 ****************/
					// tv_money.setText(String.valueOf(mFreight.getInfo_cost()));
					tv_user_show_name.setText(mFreight.getOwner_name());
					if (mFreight.getDistribution_market() == Properties.FREIGHT_NOT_POST_TO_MARKET_SCREEN) {
						mSwitchButton.setSwitch(false);
					} else {
						mSwitchButton.setSwitch(true);
					}
//					if (mFreight.getOrder_status() == Freight.ORDER_STATUS_UN_ORDER) {
//						iv_goods.setImageResource(R.drawable.selector_board_truck);
//					} else if (mFreight.getOrder_status() == Freight.ORDER_STATUS_ORDERED) {
//						iv_goods.setImageResource(R.drawable.selector_booked_truck);
//					}
					iv_goods.setImageResource(R.drawable.black_board_truck);
				}

			}
		};
		task.execute();
		loadData(-1, "0", 0, true);
		return root;

	}

	private void loadData(final int size, final String edge_id, final double weight, final boolean bFirst)
	{
		
		AsyncTask<Void, Void, List<Guarantee>> task = new AsyncTask<Void, Void, List<Guarantee>>() {

			@Override
			protected List<Guarantee> doInBackground(Void... params) {

				// TODO Auto-generated method stub
				NetGuarantee netGuarantee = new NetGuarantee() {

					@Override
					protected int getCommandCode() {
						// TODO Auto-generated method stub
						return CommandConstants.LIST_GUARANTEE_PRODUCT_CUSTOMER_CONTACT_REQ;//LIST_GUARANTEE_PRODUCT_REQ;
					}

					@Override
					protected boolean onSetRequest(com.epeisong.logistics.proto.Eps.GuaranteeReq.Builder req) {
						// TODO Auto-generated method stub
						req.setLimitCount(size);
						int id = 0;
						try {
							if (edge_id != null) {
								id = Integer.parseInt(edge_id);
							}
						} catch (Exception e) {
							id = 0;
						}
						User mUser = UserDao.getInstance().getUser();
						req.setCustomerId(Integer.valueOf(mUser.getId()));
						return true;
					}

				};
				try {
					com.epeisong.logistics.proto.Eps.GuaranteeResp.Builder resp = netGuarantee.request();
					return GuaranteeParser.parse(resp);
				} catch (NetGetException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<Guarantee> result) {
				// TODO Auto-generated method stub

				if (result != null && !result.isEmpty()) {
					mAdapter.addAll(result);
				}
			} 

		};
		task.execute();
	}
	
	@Override
	public synchronized void onSwitch(SwitchButton btn, final boolean on) {
		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... params) {
				NetUpdateFreightWhetherCanPostToMarketScreenStatus net = new NetUpdateFreightWhetherCanPostToMarketScreenStatus() {
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setFreightId(mFreight_id);
						int value = on ? Properties.FREIGHT_POST_TO_MARKET_SCREEN
								: Properties.FREIGHT_NOT_POST_TO_MARKET_SCREEN;
						req.setNewStatus(value);
						return true;
					}
				};
				try {
					FreightResp.Builder resp = net.request();
					if(net.isSuccess(resp)){
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
					mSwitchButton.setSwitch(on);
				}
			}
			
		};
		task.execute();
		
//		NetUpdateFreightWhetherCanPostToMarketScreenStatus net = new NetUpdateFreightWhetherCanPostToMarketScreenStatus(
//				(XBaseActivity) getActivity()) {
//
//			@Override
//			protected boolean onSetRequest(
//					com.epeisong.logistics.proto.Eps.FreightReq.Builder req) {
//				req.setFreightId(mFreight_id);
//				int value = on ? Properties.FREIGHT_POST_TO_MARKET_SCREEN
//						: Properties.FREIGHT_NOT_POST_TO_MARKET_SCREEN;
//				req.setNewStatus(value);
//				return true;
//			}
//		};
//		net.request(new OnNetRequestListenerImpl<FreightResp.Builder>() {
//			@Override
//			public void onSuccess(FreightResp.Builder response) {
//				mSwitchButton.setSwitch(on);
//			}
//		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_forwarding:
			// Intent intent1 = new Intent(getActivity(),
			// ChooseContactsActivity.class);
			Intent intent1 = new Intent(getActivity(),
					ChooseNewContactsActivity.class);
			startActivityForResult(intent1, REQUEST_CHOOSE_CONTACTS_NOTICE);
			break;
		/*
		 * case R.id.bt_immediately_consult: Intent intent2 = new
		 * Intent(getActivity(), ChatRoomActivity.class);
		 * intent2.putExtra(ChatRoomActivity.EXTRA_REMOTE_ID, user_id);
		 * intent2.putExtra(ChatRoomActivity.EXTRA_BUSINESS_TYPE,
		 * ChatMsg.business_type_freight);
		 * intent2.putExtra(ChatRoomActivity.EXTRA_BUSINESS_ID, mFreight_id);
		 * startActivity(intent2); break;
		 */
		case R.id.bt_delete:
			NetUpdateFreightStatus update = new NetUpdateFreightStatus(
					(XBaseActivity) getActivity()) {
				@Override
				protected boolean onSetRequest(FreightReq.Builder req) {
					req.setFreightId(Integer.parseInt(mFreight.getId()));
					req.setNewStatus(Properties.FREIGHT_STATUS_DELETED);
					return true;
				}
			};
			update.request(new OnNetRequestListener<Eps.FreightResp.Builder>() {

				@Override
				public void onError() {
					// TODO Auto-generated method stub
				}

				@Override
				public void onFail(String msg) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onSuccess(
						com.epeisong.logistics.proto.Eps.FreightResp.Builder response) {
					Intent data = new Intent();
					data.putExtra("todayCount", response.getFreightCountOfTodatyOnBlackBoard());
                    data.putExtra("totalCount", response.getFreightCountOnBlackBoard());
					data.putExtra(BlackBoardActivity.EXTRA_FREIGHT, mFreight);
					getActivity().setResult(
							BlackBoardActivity.RESULT_CODE_DELETE_DETAIL, data);
					getActivity().finish();

				}
			});
			break;
		}
	}
	private class MyAdapter extends HoldDataBaseAdapter<Guarantee> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.fragment_guarantee_gridview_item);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.fillData(getItem(position));
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView iv;

		public void findView(View v) {
			iv = (ImageView) v.findViewById(R.id.iv);
		}

		public void fillData(final Guarantee item) {
			if(!UserDao.getInstance().getUser().getId().equals(user_id)) {
				if (!TextUtils.isEmpty(item.getMark_url2())) {
					ImageLoader.getInstance().displayImage(item.getMark_url2(), iv);
				}
				else {
					return;
				}
			}else {
				if (!TextUtils.isEmpty(item.getMark_url1())) {
					ImageLoader.getInstance().displayImage(item.getMark_url1(), iv);
				}
				else {
					return;
				}
			}
				iv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if(item!=null) {
							//ToastUtils.showToast("对方已开启"+item.getName()+"服务，若对方在交易中违约，对方将赔付"+String.valueOf(item.getAccount())+"元");
							if(UserDao.getInstance().getUser().getId().equals(user_id))
								Toast.makeText(getActivity(), "您已开启"+item.getName()+"服务，若您在交易中违约，您将赔付"+String.valueOf(item.getAccount()/100)+"元", Toast.LENGTH_LONG).show();
							else
								Toast.makeText(getActivity(), "对方已开启"+item.getName()+"服务，若对方在交易中违约，对方将赔付"+String.valueOf(item.getAccount()/100)+"元", Toast.LENGTH_LONG).show();
//							Intent intentcerIntent = new Intent(getActivity(), ProductDetailActivity.class);
//							intentcerIntent.putExtra(ProductDetailActivity.EXTRA_GUARANTEE, item);
//							intentcerIntent.putExtra(ProductDetailActivity.EXTRA_GUARDIS, "disable");
//							startActivity(intentcerIntent);
						}
					}
				});

		}
	}
}
