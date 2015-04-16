//package com.epeisong.ui.activity;
//
//import android.content.Intent;
//import android.net.Uri;
//import com.epeisong.utils.android.AsyncTask;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.epeisong.R;
//import com.epeisong.base.activity.BaseActivity;
//import com.epeisong.base.view.TitleParams;
//import com.epeisong.data.dao.ContactsDao;
//import com.epeisong.data.dao.FreightForwardDao;
//import com.epeisong.data.dao.PhoneContactsDao;
//import com.epeisong.data.layer02.SupplyDetailsProvider;
//import com.epeisong.data.layer02.SupplyDetailsProvider.ProvideResult;
//import com.epeisong.data.net.parser.ContactsParser;
//import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
//import com.epeisong.logistics.proto.Eps;
//import com.epeisong.logistics.proto.Eps.AddContactResp;
//import com.epeisong.logistics.proto.Eps.RepasteFreightReq;
//import com.epeisong.logistics.proto.Eps.UpdateFreightDeliveryReceiverStatusReq;
//import com.epeisong.model.ChatMsg;
//import com.epeisong.model.Contacts;
//import com.epeisong.model.FreightForward;
//import com.epeisong.model.User;
//import com.epeisong.net.request.NetAddContacts;
//import com.epeisong.net.request.NetRepasteFreight;
//import com.epeisong.net.request.NetUpdateFreightDeliveryReceiverStatus;
//import com.epeisong.net.request.OnNetRequestListener;
//import com.epeisong.net.request.OnNetRequestListenerImpl;
//import com.epeisong.utils.DateUtil;
//import com.epeisong.utils.ToastUtils;
//
///**
// * 
// * @author 孙灵洁 朋友车源详情
// * 
// */
//@Deprecated
//public class CarSourceActivity extends BaseActivity {
//
//	public static final String EXTRA_USER_ID = "user_id";
//	public static final String EXTRA_FREIGHT_FORWARD_ID = "freight_forward";
//	private static final int REQUEST_CHOOSE_CONTACTS_NOTICE = 100;
//
//	// private TextView tv_release_time;
//	private TextView tv_time;
//	// private ImageView iv_goods;
//	private TextView tv_region;
//	private TextView tv_describe;
//	private LinearLayout bt_forwarding;
//	private LinearLayout bt_chalkboard;
//	private LinearLayout bt_immediately_consult;
//	// private TextView tv_contact;
//	private TextView tv_user_show_name;
//	// private ImageView iv_support;
//	private TextView tv_support;
//	// private ImageView iv_oppose;
//	private TextView tv_oppose;
//	// private TextView tv_the_contact;
//	private TextView tv_contact_name;
//	private ImageView iv_add_contacts;
//	// private TextView tv_phone;
//	private TextView tv_phone_number;
//	private ImageView iv_phone;
//	// private TextView tv_telephone;
//	private TextView tv_telephone_number;
//	private ImageView iv_telephone;
//	private Button bt_delete;
//	private Button bt_Distribution;
//	private TextView tv_chalkboard;
//	private RelativeLayout rllt3;
//
//	private RelativeLayout rllt4;
//	private RelativeLayout rllt5;
//	private User user;
//	private FreightForward freightForward;
//
//	private String user_id;
//	private String freightForward_id;
//
//	@Override
//	protected TitleParams getTitleParams() {
//		// TODO Auto-generated method stub
//		return null;
//
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//
//		user_id = getIntent().getStringExtra(EXTRA_USER_ID);
//		freightForward_id = getIntent()
//				.getStringExtra(EXTRA_FREIGHT_FORWARD_ID);
//
//		super.onCreate(savedInstanceState);
//		super.setContentView(R.layout.activity_car_source);
//
//		tv_time = (TextView) findViewById(R.id.tv_time);
//		tv_region = (TextView) findViewById(R.id.tv_region);
//		tv_describe = (TextView) findViewById(R.id.tv_describe);
//		bt_forwarding = (LinearLayout) findViewById(R.id.bt_forwarding);
//		bt_chalkboard = (LinearLayout) findViewById(R.id.bt_chalkboard);
//		bt_immediately_consult = (LinearLayout) findViewById(R.id.bt_immediately_consult);
//		tv_user_show_name = (TextView) findViewById(R.id.tv_user_show_name);
//		tv_support = (TextView) findViewById(R.id.tv_support);
//		tv_oppose = (TextView) findViewById(R.id.tv_oppose);
//		tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
//		iv_add_contacts = (ImageView) findViewById(R.id.iv_add_contacts);
//		tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
//		iv_phone = (ImageView) findViewById(R.id.iv_phone);
//		tv_telephone_number = (TextView) findViewById(R.id.tv_telephone_number);
//		iv_telephone = (ImageView) findViewById(R.id.iv_telephone);
//		bt_delete = (Button) findViewById(R.id.bt_delete);
//		bt_Distribution = (Button) findViewById(R.id.bt_Distribution);
//
//		tv_chalkboard = (TextView) findViewById(R.id.tv_chalkboard);
//
//		rllt3 = (RelativeLayout) findViewById(R.id.rllt3);
//		rllt4 = (RelativeLayout) findViewById(R.id.rllt4);
//		rllt5 = (RelativeLayout) findViewById(R.id.rllt5);
//
//		if (ContactsDao.getInstance().queryById(user_id) == null) {
//			iv_add_contacts
//					.setImageResource(R.drawable.loading_distribution_lianxiren);
//
//		} else {
//			iv_add_contacts
//					.setImageResource(R.drawable.loading_distribution_lianxirenyitianjia1);
//			iv_add_contacts.setEnabled(false);
//		}
//
//		iv_add_contacts.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				NetAddContacts net = new NetAddContacts(CarSourceActivity.this,
//						user_id);
//				net.request(new OnNetRequestListenerImpl<AddContactResp.Builder>() {
//
//					@Override
//					public void onSuccess(AddContactResp.Builder response) {
//						// TODO Auto-generated method stub
//						ProtoEBizLogistics logistics = response
//								.getBizLogistics();
//
//						if (logistics != null) {
//							Contacts c = ContactsParser.parse(logistics);
//							c.setStatus(Contacts.STATUS_NORNAL);
//
//							ContactsDao.getInstance().insert(c);
//							PhoneContactsDao.getInstance().updateAdded(
//									c.getPhone());
//							ToastUtils.showToast("添加成功");
//							iv_add_contacts
//									.setImageResource(R.drawable.loading_distribution_lianxirenyitianjia1);
//							iv_add_contacts.setEnabled(false);
//						}
//
//					}
//
//				});
//
//			}
//		});
//
//		iv_telephone.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
//						+ user.getContacts_telephone()));
//				startActivity(intent);
//			}
//		});
//
//		bt_delete.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				NetUpdateFreightDeliveryReceiverStatus update = new NetUpdateFreightDeliveryReceiverStatus(
//						CarSourceActivity.this) {
//
//					@Override
//					protected boolean onSetRequest(
//							UpdateFreightDeliveryReceiverStatusReq.Builder req) {
//						// TODO Auto-generated method stub
//						req.setFreightDeliveryId(Integer
//								.parseInt(freightForward.getId()));
//						req.setNewReceiverStatus(FreightForward.STATUS_DELETED);
//						return true;
//					}
//
//				};
//				update.request(new OnNetRequestListener<Eps.UpdateFreightDeliveryReceiverStatusResp.Builder>() {
//
//					@Override
//					public void onError() {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onFail(String msg) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onSuccess(
//							com.epeisong.logistics.proto.Eps.UpdateFreightDeliveryReceiverStatusResp.Builder response) {
//						FreightForwardDao.getInstance().delete(freightForward);
//					}
//				});
//			}
//		});
//
//		iv_phone.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
//						+ user.getContacts_phone()));
//				startActivity(intent);
//
//			}
//		});
//
//		bt_forwarding.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				// ToastUtils.showToast("转发信息");
//				Intent intent = new Intent(getApplicationContext(),
//						ChooseContactsActivity.class);
//				startActivityForResult(intent, REQUEST_CHOOSE_CONTACTS_NOTICE);
//			}
//		});
//
//		bt_chalkboard.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//
////				if (freightForward.getForward_to_blackboard() == FreightForward.FORWARD_TO_BLACKBOARD_ALREADY) {
////					return;
////				}
//
//				NetRepasteFreight repaste = new NetRepasteFreight(
//						CarSourceActivity.this) {
//
//					@Override
//					protected boolean onSetRequest(RepasteFreightReq.Builder req) {
//						// TODO Auto-generated method stub
//						req.setFreightId(Integer.parseInt(freightForward
//								.getId()));
//
//						return true;
//					}
//				};
//
//				repaste.request(new OnNetRequestListener<Eps.RepasteFreightResp.Builder>() {
//
//					@Override
//					public void onError() {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onFail(String msg) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onSuccess(
//							com.epeisong.logistics.proto.Eps.RepasteFreightResp.Builder response) {
//						// TODO Auto-generated method stub
//
////						freightForward
////								.setForward_to_blackboard(FreightForward.FORWARD_TO_BLACKBOARD_ALREADY);
////						FreightForwardDao.getInstance().update(freightForward);
////						tv_chalkboard.setText("  已发小黑板");
////						tv_chalkboard.setClickable(false);
////						tv_chalkboard.setOnClickListener(null);
//					}
//				});
//			}
//		});
//
//		bt_immediately_consult.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				// ToastUtils.showToast("转发信息");
//				Intent intent = new Intent(CarSourceActivity.this,
//						ChatRoomActivity.class);
//				intent.putExtra(ChatRoomActivity.EXTRA_REMOTE_ID, user_id);
//				intent.putExtra(ChatRoomActivity.EXTRA_BUSINESS_TYPE,
//						ChatMsg.business_type_freight);
//				intent.putExtra(ChatRoomActivity.EXTRA_BUSINESS_ID,
//						freightForward_id);
//				startActivity(intent);
//			}
//		});
//
//		AsyncTask<Void, Void, ProvideResult> task = new AsyncTask<Void, Void, ProvideResult>() {
//			@Override
//			protected ProvideResult doInBackground(Void... params) {
//
//				// 货源详情的ID
//				// freightForward=FreightForwardDao.getInstance.queryById();
//				SupplyDetailsProvider provider = new SupplyDetailsProvider();
//				return provider.provide(user_id, freightForward_id);
//			}
//
//			@Override
//			protected void onPostExecute(ProvideResult result) {
//
//				if (result != null) {
//
//					user = result.getUser();
//
////					if (freightForward.getForward_to_blackboard() == FreightForward.FORWARD_TO_BLACKBOARD_NOT) {
////						tv_chalkboard.setText("发到我的小黑板");
////					} else {
////
////						tv_chalkboard.setText("  已发小黑板");
////						bt_chalkboard.setEnabled(false);
////
////					}
//
//					tv_time.setText(DateUtil.long2YMDHM(freightForward
//							.getForward_create_time()));
//					// 起始地址，终止地址
//					tv_region.setText(freightForward.getFreight()
//							.getStart_region()
//							+ "-"
//							+ freightForward.getFreight().getEnd_region());
//					tv_describe.setText(freightForward.getFreight().getDesc());
//					tv_user_show_name.setText(freightForward.getFreight()
//							.getOwner_name());
//
//					if (user != null) {
//						tv_contact_name.setText(user.getContacts_name());
//						tv_phone_number.setText(user.getContacts_phone());
//						tv_telephone_number.setText(user
//								.getContacts_telephone());
//					}
//
//					/*
//					 * if (user != null) { String contacts_name =
//					 * user.getContacts_name();
//					 * 
//					 * if (TextUtils.isEmpty(contacts_name)) {
//					 * rllt3.setVisibility(View.GONE); } else {
//					 * tv_contact_name.setText(contacts_name); } String
//					 * contacts_phone = user.getContacts_phone(); if
//					 * (TextUtils.isEmpty(contacts_phone)) {
//					 * 
//					 * rllt4.setVisibility(View.GONE); } else {
//					 * 
//					 * tv_phone_number.setText(contacts_phone); }
//					 * 
//					 * String contacts_telephone = user
//					 * .getContacts_telephone(); if
//					 * (TextUtils.isEmpty(contacts_telephone)) {
//					 * rllt5.setVisibility(View.GONE);
//					 * 
//					 * } else { tv_telephone_number.setText(contacts_telephone);
//					 * } }
//					 */
//
//				}
//			}
//		};
//		task.execute();
//
//	}
//
//}
