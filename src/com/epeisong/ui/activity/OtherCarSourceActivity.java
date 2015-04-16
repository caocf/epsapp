/*package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import com.epeisong.utils.android.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.impl.IconTextAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.FreightOfContactsDao;
import com.epeisong.data.dao.PhoneContactsDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.layer02.SupplyDetailsProvider;
import com.epeisong.data.layer02.SupplyDetailsProvider.ProvideResult;
import com.epeisong.data.net.parser.ContactsParser;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Base.ProtoEInfoFee;
import com.epeisong.logistics.proto.Eps;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.InfoFeeResp;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.Contacts;
import com.epeisong.model.Freight;
import com.epeisong.model.User;
import com.epeisong.net.request.NetAddBanned;
import com.epeisong.net.request.NetAddContacts;
import com.epeisong.net.request.NetInfoFee;
import com.epeisong.net.request.NetRepasteFreight;
import com.epeisong.net.request.NetUpdateFreightDeliveryReceiverStatus;
import com.epeisong.net.request.NetUpdateIsAllowToShow;
import com.epeisong.net.request.OnNetRequestListener;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

*//**
 * 
 * @author 孙灵洁 搜索车源货源，朋友车源详情
 * 
 *//*
public class OtherCarSourceActivity extends BaseActivity implements
		OnClickListener {

	public static final String EXTRA_FREIGHT = "mFreight";
	public static final String EXTRA_MARKET = "market";
	public static final String EXTRA_FLAG = "flag";

	public static final String EXTRA_USER_ID = "user_id";
	public static final String EXTRA_FREIGHT_ID = "freight_id";
	private static final int REQUEST_CHOOSE_CONTACTS_NOTICE = 100;
	public static final String EXTRA_IS_HIDE_DELETE_BTN = "is_hide_delete_btn";
	public static final String SEND_BLACKBOARD_BTN = "is_send_btn";

	private ImageView iv_zt;
	private TextView tv_time;
	private TextView tv_region;
	private TextView tv_describe;
	// private TextView tv_money;
	private LinearLayout bt_forwarding;
	private LinearLayout bt_chalkboard;
//	private LinearLayout bt_immediately_consult;
	private TextView tv_chalkboard;
	private TextView tv_user_show_name;
	private RatingBar ratingBar;
	private TextView tv_contact_name;
	private ImageView iv_add_contacts;
	private TextView tv_phone_number;
	private ImageView iv_phone;
	private TextView tv_telephone_number;
	private ImageView iv_telephone;
	private Button bt_delete;
	private Button bt_distribution;
	private RelativeLayout rllt3;
	private RelativeLayout rllt4;
	private RelativeLayout rllt5;

	private User user;
	private Freight mFreight;
	private String user_id;
	private String mFreight_id;
	private PopupWindow mPopupWindowMenu;
	private User mMarket;
	private String flag;

	private Action createAction() {
		return new ActionImpl() {
			@Override
			public void doAction(View v) {
				showMenuPopupWindow();
			}

			@Override
			public int getDrawable() {
				return R.drawable.selector_user_logo_chat;
			}
		};
	}

	private void initPopupWindowMenu() {
		final List<IconTextItem> items = new ArrayList<IconTextItem>();
		items.add(new IconTextItem(R.drawable.selector_common_checkable,
				"屏蔽该用户", null));
		items.add(new IconTextItem(R.drawable.selector_common_checkable,
				"屏蔽该信息", null));
		mPopupWindowMenu = new PopupWindow(getApplicationContext());
		IconTextAdapter adapter = new IconTextAdapter(getApplicationContext(),
				40);
		adapter.replaceAll(items);
		ListView lv = new ListView(getApplicationContext());
		lv.setAdapter(adapter);
		lv.setBackgroundColor(Color.argb(0xFF, 0x2D, 0x30, 0x37));
		mPopupWindowMenu.setContentView(lv);
		mPopupWindowMenu.setWidth(EpsApplication.getScreenWidth() / 2);
		mPopupWindowMenu.setHeight(LayoutParams.WRAP_CONTENT);
		mPopupWindowMenu.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.transparent)));
		mPopupWindowMenu.setFocusable(true);
		mPopupWindowMenu.setOutsideTouchable(true);
		mPopupWindowMenu.setAnimationStyle(R.style.popup_window_menu);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPopupWindowMenu.dismiss();

				if (position == 0) {
					NetAddBanned net = new NetAddBanned(
							OtherCarSourceActivity.this, Integer
									.parseInt(mFreight.getUser_id()));
					net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {

						@Override
						public void onSuccess(CommonLogisticsResp.Builder response) {
							items.add(new IconTextItem(
									R.drawable.selector_common_checkable,
									"该用户已屏蔽", null));
							items.add(new IconTextItem(
									R.drawable.selector_common_checkable,
									"屏蔽该信息", null));
						}

					});
				} else if (position == 1) {
					NetUpdateIsAllowToShow netShow = new NetUpdateIsAllowToShow(
							OtherCarSourceActivity.this,
							Properties.MARKET_SCREEN_NOT_ALLOW_TO_SHOW, Integer
									.parseInt(mFreight.getId()));
					netShow.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
						@Override
						public void onSuccess(
								com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder response) {
							items.add(new IconTextItem(
									R.drawable.selector_common_checkable,
									"屏蔽该用户", null));
							items.add(new IconTextItem(
									R.drawable.selector_common_checkable,
									"该信息已被屏蔽", null));
						}
					});
				}

				Object obj = parent.getItemAtPosition(position);
				if (obj != null && obj instanceof IconTextItem) {
					IconTextItem item = (IconTextItem) obj;
					ToastUtils.showToast(item.getName());
				}
			}
		});
		lv.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU
						&& event.getAction() == KeyEvent.ACTION_UP
						&& mPopupWindowMenu.isShowing()) {
					mPopupWindowMenu.dismiss();
					return true;
				}
				return false;
			}
		});
	}

	private void showMenuPopupWindow() {
		if (mPopupWindowMenu == null) {
			initPopupWindowMenu();
		}
		int statusBar = SystemUtils.getStatusBarHeight(this);
		int y = getResources().getDimensionPixelSize(
				R.dimen.custom_title_height)
				+ statusBar + 1;
		mPopupWindowMenu.showAtLocation(mCustomTitle, Gravity.TOP
				| Gravity.RIGHT, (int) DimensionUtls.getPixelFromDp(10), y);
	}

	@Override
	protected TitleParams getTitleParams() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(createAction());
		if (flag == null) {
			return new TitleParams(getDefaultHomeAction(), "车源详情", null)
					.setShowLogo(false);
		} else if (flag.equals("manage_flag") && flag != null) {
			return new TitleParams(getDefaultHomeAction(), "车源详情", actions)
					.setShowLogo(false);
		} else {
			return null;
		}
	}

	private void updateOrderStatus() {
		if (mFreight == null) {
			return;
		}
		if (mFreight.getStatus() == Freight.STATUS_VALID) {
			if (mFreight.getOrder_status() == Freight.ORDER_STATUS_ORDERED) {
				// 已被订
				iv_zt.setImageResource(R.drawable.search_yibeiding);
				// bt_distribution.setEnabled(false);
				// bt_distribution.setText("已被订");
				bt_distribution.setVisibility(View.GONE);
			} else {
				// 没被定
				iv_zt.setImageResource(R.drawable.search_youxiao);
			}
		} else {
			// TODO　无效
			iv_zt.setImageResource(R.drawable.search_yiguoqi);
			// bt_distribution.setEnabled(false);
			// bt_distribution.setText("已过期");
			bt_distribution.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		mFreight = (Freight) getIntent().getSerializableExtra(EXTRA_FREIGHT);
		mMarket = (User) getIntent().getSerializableExtra(EXTRA_MARKET);
		flag = getIntent().getStringExtra(EXTRA_FLAG);
		user_id = getIntent().getStringExtra(EXTRA_USER_ID);
		mFreight_id = getIntent().getStringExtra(EXTRA_FREIGHT_ID);

		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_other_car_source);

		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_region = (TextView) findViewById(R.id.tv_region);
		tv_describe = (TextView) findViewById(R.id.tv_describe);
		// tv_money = (TextView) findViewById(R.id.tv_money);
		bt_forwarding = (LinearLayout) findViewById(R.id.bt_forwarding);
		bt_chalkboard = (LinearLayout) findViewById(R.id.bt_chalkboard);
//		bt_immediately_consult = (LinearLayout) findViewById(R.id.bt_immediately_consult);
		tv_user_show_name = (TextView) findViewById(R.id.tv_user_show_name);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
		iv_add_contacts = (ImageView) findViewById(R.id.iv_add_contacts);
		tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
		iv_phone = (ImageView) findViewById(R.id.iv_phone);
		tv_telephone_number = (TextView) findViewById(R.id.tv_telephone_number);
		iv_telephone = (ImageView) findViewById(R.id.iv_telephone);
		bt_delete = (Button) findViewById(R.id.bt_delete);
		bt_distribution = (Button) findViewById(R.id.bt_distribution);
		rllt3 = (RelativeLayout) findViewById(R.id.rllt3);
		rllt4 = (RelativeLayout) findViewById(R.id.rllt4);
		rllt5 = (RelativeLayout) findViewById(R.id.rllt5);
		tv_chalkboard = (TextView) findViewById(R.id.tv_chalkboard);
		iv_zt = (ImageView) findViewById(R.id.iv_zt);

		iv_add_contacts.setOnClickListener(this);
		iv_telephone.setOnClickListener(this);
		iv_phone.setOnClickListener(this);
		bt_forwarding.setOnClickListener(this);
//		bt_immediately_consult.setOnClickListener(this);
		bt_chalkboard.setOnClickListener(this);
		bt_delete.setOnClickListener(this);
		bt_distribution.setOnClickListener(this);

		if (getIntent().getBooleanExtra(EXTRA_IS_HIDE_DELETE_BTN, false)) {
			bt_delete.setVisibility(View.GONE);

		}
		*//****************** 朋友的车源详情发到小黑板隐藏 **********************//*
		
		 * if (getIntent().getBooleanExtra(SEND_BLACKBOARD_BTN, false)) {
		 * bt_chalkboard.setVisibility(View.GONE); }
		 

		if (ContactsDao.getInstance().queryById(user_id) == null) {
			iv_add_contacts
					.setImageResource(R.drawable.loading_distribution_lianxiren);

		} else {
			iv_add_contacts
					.setImageResource(R.drawable.loading_distribution_lianxirenyitianjia1);
			iv_add_contacts.setEnabled(false);
		}

		AsyncTask<Void, Void, ProvideResult> task = new AsyncTask<Void, Void, ProvideResult>() {
			@Override
			protected ProvideResult doInBackground(Void... params) {
				SupplyDetailsProvider provider = new SupplyDetailsProvider();
				return provider.provide(user_id, mFreight_id);
			}

			@Override
			protected void onPostExecute(ProvideResult result) {

				if (result != null) {

					Freight f = result.getmFreight();
					if (f != null) {
						mFreight = f;
						updateOrderStatus();
					}
					if (mFreight == null) {
						return;
					}

					user = result.getUser();

					if (mFreight.getForward_to_blacklist() == Freight.FORWARD_TO_BLACKBOARD_NOT) {
						tv_chalkboard.setText("发到小黑板");
					} else {
						tv_chalkboard.setText("已发小黑板");
						tv_chalkboard.setEnabled(false);
					}
					*//******************* 朋友的车源详情信息费 ************************//*
					// tv_money.setText(String.valueOf(mFreight.getInfo_cost()));

					tv_time.setText(DateUtil.long2YMDHM(mFreight
							.getCreate_time()));
					// 起始地址，终止地址
					tv_region.setText(mFreight.getStart_region() + "-"
							+ mFreight.getEnd_region());
					tv_describe.setText(mFreight.getDesc());
					tv_user_show_name.setText(mFreight.getOwner_name());
					Contacts contacts = ContactsDao.getInstance().queryById(
							user_id);
					if (user != null) {
						tv_contact_name.setText(user.getContacts_name());
						tv_phone_number.setText(user.getContacts_phone());
						tv_telephone_number.setText(user
								.getContacts_telephone());
						ratingBar.setProgress(user.getStar_level());
						if (contacts != null) {
							ratingBar.setProgress(contacts.getStar_level());
						}
					}
				}
			}
		};
		task.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_add_contacts:
			NetAddContacts net = new NetAddContacts(
					OtherCarSourceActivity.this, user_id);
			net.request(new OnNetRequestListenerImpl<CommonLogisticsResp.Builder>() {
				@Override
				public void onSuccess(CommonLogisticsResp.Builder response) {
					ProtoEBizLogistics logistics = response.getBizLogistics(0);

					if (logistics != null) {
						Contacts c = ContactsParser.parse(logistics);
						c.setStatus(Contacts.STATUS_NORNAL);
						ContactsDao.getInstance().insert(c);
						PhoneContactsDao.getInstance()
								.updateAdded(c.getPhone());
						ToastUtils.showToast("添加成功");
						iv_add_contacts
								.setImageResource(R.drawable.loading_distribution_lianxirenyitianjia1);
						iv_add_contacts.setEnabled(false);
					}
				}
			});
			break;
		case R.id.iv_telephone:

			if (user != null) {
				if (TextUtils.isEmpty(user.getContacts_telephone())) {
					iv_telephone.setEnabled(false);
				} else {

					Intent intent1 = new Intent(Intent.ACTION_DIAL,
							Uri.parse("tel:" + user.getContacts_telephone()));
					startActivity(intent1);
				}
			}

			break;

		case R.id.iv_phone:
			if (user != null) {
				if (TextUtils.isEmpty(user.getContacts_phone())) {
					iv_phone.setEnabled(false);
				} else {

					Intent intent2 = new Intent(Intent.ACTION_DIAL,
							Uri.parse("tel:" + user.getContacts_phone()));
					startActivity(intent2);
				}

			}
			break;

		case R.id.bt_forwarding:
			Intent intent3 = new Intent(getApplicationContext(),
					ChooseContactsActivity.class);
			startActivityForResult(intent3, REQUEST_CHOOSE_CONTACTS_NOTICE);
			break;

			*//******************咨询按钮*********************//*
		case R.id.bt_immediately_consult:
			Intent intent4 = new Intent(OtherCarSourceActivity.this,
					ChatRoomActivity.class);
			intent4.putExtra(ChatRoomActivity.EXTRA_REMOTE_ID, user_id);
			intent4.putExtra(ChatRoomActivity.EXTRA_BUSINESS_TYPE,
					ChatMsg.business_type_freight);
			intent4.putExtra(ChatRoomActivity.EXTRA_BUSINESS_ID, mFreight_id);
			startActivity(intent4);
			break;

		case R.id.bt_chalkboard:
			if (mFreight.getForward_to_blacklist() == Freight.FORWARD_TO_BLACKBOARD_ALREADY) {
				return;
			}
			NetRepasteFreight repaste = new NetRepasteFreight(
					OtherCarSourceActivity.this) {
				@Override
				protected boolean onSetRequest(FreightReq.Builder req) {
					req.setFreightId(Integer.parseInt(mFreight.getId()));
					return true;
				}
			};
			repaste.request(new OnNetRequestListener<Eps.FreightResp.Builder>() {
				@Override
				public void onError() {

				}

				@Override
				public void onFail(String msg) {

				}

				@Override
				public void onSuccess(
						com.epeisong.logistics.proto.Eps.FreightResp.Builder response) {
					mFreight.setForward_to_blacklist(Freight.FORWARD_TO_BLACKBOARD_ALREADY);
					FreightOfContactsDao.getInstance().update(mFreight);
					tv_chalkboard.setText("已发小黑板");
					tv_chalkboard.setClickable(false);
					tv_chalkboard.setOnClickListener(null);
				}
			});
			break;

		case R.id.bt_delete:
			NetUpdateFreightDeliveryReceiverStatus update = new NetUpdateFreightDeliveryReceiverStatus(
					OtherCarSourceActivity.this) {
				@Override
				protected boolean onSetRequest(FreightReq.Builder req) {
					req.setFreightDeliveryId(Integer.parseInt(mFreight.getId()));
					req.setNewReceiverStatus(Freight.STATUS_DELETED);
					return true;
				}
			};
			update.request(new OnNetRequestListener<Eps.FreightResp.Builder>() {

				@Override
				public void onError() {

				}

				@Override
				public void onFail(String msg) {

				}

				@Override
				public void onSuccess(
						com.epeisong.logistics.proto.Eps.FreightResp.Builder response) {
					FreightOfContactsDao.getInstance().delete(mFreight);
				}
			});
			break;
		case R.id.bt_distribution:
			if (mFreight.getOrder_status() == Properties.FREIGHT_DEAL_STATUS_NOT_ORDERED) {
				AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

					@Override
					protected Boolean doInBackground(Void... params) {
						NetInfoFee net = new NetInfoFee() {
							@Override
							protected int getCommandCode() {
								return CommandConstants.CREATE_INFO_FEE_REQ;
							}

							@Override
							protected void setRequest(
									com.epeisong.logistics.proto.Eps.InfoFeeReq.Builder req) {
								ProtoEInfoFee.Builder infofeebuBuilder = ProtoEInfoFee
										.newBuilder();
								infofeebuBuilder
										.setType(Properties.INFO_FEE_TYPE_DISTRIBUTION_GOODS);
								infofeebuBuilder.setFreightId(Integer
										.parseInt(mFreight.getId()));
								infofeebuBuilder.setFreightAddr(mFreight
										.getStart_region()
										+ " —— "
										+ mFreight.getEnd_region());
								infofeebuBuilder.setFreightInfo(mFreight
										.getDesc());
								infofeebuBuilder.setInfoAmount(mFreight
										.getInfo_cost());
								// 担保方目前测试写死的 start
								infofeebuBuilder.setGuaranteeId(6);
								infofeebuBuilder.setGuaranteeName("刘林");
								infofeebuBuilder.setGuaranteeAmount(Float
										.parseFloat("200.00"));
								infofeebuBuilder
										.setGuaranteeStatus(Properties.INFO_FEE_GUARANTEE_STATUS_NEED);
								// 担保方目前测试写死的 end
								User user = UserDao.getInstance().getUser();
								infofeebuBuilder.setDistributionId(Integer
										.parseInt(user.getId()));
								infofeebuBuilder.setDistributionName(user
										.getShow_name());

								infofeebuBuilder.setPublisherId(Integer
										.parseInt(mFreight.getUser_id()));
								infofeebuBuilder.setPublisherName(mFreight
										.getOwner_name());

								infofeebuBuilder
										.setGFlowStatus(Properties.INFO_FEE_GOODS_FLOW_STATUS_WAITING_FOR_PAYMENT);
								infofeebuBuilder
										.setVFlowStatus(Properties.INFO_FEE_VEHICLE_FLOW_STATUS_WAITING_FOR_PAYMENT);

								req.setInfoFee(infofeebuBuilder);
							}
						};

						InfoFeeResp.Builder resp = null;
						try {
							resp = net.request();

						} catch (NetGetException e) {
							e.printStackTrace();
						}

						if (null != resp) {
							return true;
						}
						return false;
					}

					@Override
					protected void onPostExecute(Boolean result) {
						if (result) {
							mFreight.setOrder_status(Properties.FREIGHT_DEAL_STATUS_ORDERED);
							ToastUtils.showToast("配货成功");
							bt_distribution.setEnabled(false);
						} else {
							ToastUtils.showToast("配货失败");
						}
					}
				};

				task.execute();

			}

			break;
		}
	}

}
*/