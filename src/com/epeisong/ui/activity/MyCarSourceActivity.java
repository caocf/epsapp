/*package com.epeisong.ui.activity;

import android.app.Activity;
import android.content.Intent;
import com.epeisong.utils.android.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetFreightDetail;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.logistics.proto.Eps;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.Freight;
import com.epeisong.model.User;
import com.epeisong.net.request.NetUpdateFreightStatus;
import com.epeisong.net.request.NetUpdateFreightWhetherCanPostToMarketScreenStatus;
import com.epeisong.net.request.OnNetRequestListener;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.ui.view.SwitchButton;
import com.epeisong.ui.view.SwitchButton.OnSwitchListener;
import com.epeisong.utils.DateUtil;

*//**
 * 
 * @author 孙灵洁 我的车源详情（搜索，发布小黑板）
 * 
 *//*
public class MyCarSourceActivity extends BaseActivity implements
		OnSwitchListener, OnClickListener {
	public static final String EXTRA_FREIGHT = "mFreight";
	private static final int REQUEST_CHOOSE_CONTACTS_NOTICE = 100;
	public static final String EXTRA_USER_ID = "user_id";
	public static final String EXTRA_FREIGHT_ID = "mFreight_id";
	public static final String EXTRA_IS_HIDE_DELETE_BTN = "is_hide_delete_btn";

	private ImageView iv_zt;
	private TextView tv_time;
	private TextView tv_region;
	private TextView tv_describe;
	// private TextView tv_money;
	private LinearLayout bt_forwarding;
//	private LinearLayout bt_immediately_consult;
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

	@Override
	protected TitleParams getTitleParams() {
		// TODO Auto-generated method stub
		return new TitleParams(getDefaultHomeAction(), "车源详情", null)
				.setShowLogo(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK
				&& requestCode == REQUEST_CHOOSE_CONTACTS_NOTICE) {
			Intent intent = new Intent(getApplicationContext(),
					NoticeOrRelayActivity.class);
			intent.putExtra(NoticeOrRelayActivity.EXTRA_ACTION_TYPE,
					NoticeOrRelayActivity.ACTION_NOTICE);
			intent.putExtra(NoticeOrRelayActivity.EXTRA_DISPATCH, mFreight);
			intent.putExtra(
					NoticeOrRelayActivity.EXTRA_CONTACTS_LIST,
					data.getSerializableExtra(ChooseContactsActivity.EXTRA_SELECTED_CONTACTS_LIST));
			startActivity(intent);
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
			} else {
				// 没被定
				iv_zt.setImageResource(R.drawable.search_youxiao);
			}
		} else {
			// TODO　无效
			iv_zt.setImageResource(R.drawable.search_yiguoqi);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mFreight = (Freight) getIntent().getSerializableExtra(EXTRA_FREIGHT);
		user_id = getIntent().getStringExtra(EXTRA_USER_ID);
		mFreight_id = Integer.parseInt(getIntent().getStringExtra(
				EXTRA_FREIGHT_ID));

		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_my_car_source);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_region = (TextView) findViewById(R.id.tv_region);
		tv_describe = (TextView) findViewById(R.id.tv_describe);
		// tv_money = (TextView) findViewById(R.id.tv_money);
		bt_forwarding = (LinearLayout) findViewById(R.id.bt_forwarding);
//		bt_immediately_consult = (LinearLayout) findViewById(R.id.bt_immediately_consult);
		tv_user_show_name = (TextView) findViewById(R.id.tv_user_show_name);
		tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
		tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
		tv_telephone_number = (TextView) findViewById(R.id.tv_telephone_number);
		bt_delete = (Button) findViewById(R.id.bt_delete);
		iv_zt = (ImageView) findViewById(R.id.iv_zt);
		mSwitchButton = (SwitchButton) findViewById(R.id.switch_button);
		mSwitchButton.setOnSwitchListener(this);
		mSwitchButton.setSwitchText("是", "否", true);
		User user = UserDao.getInstance().getUser();
		if(user!=null){
		tv_contact_name.setText(user.getContacts_name());
		tv_phone_number.setText(user.getContacts_phone());
		tv_telephone_number.setText(user.getContacts_telephone());}

		// 搜索车源货源如果是自己的，删除按钮隐藏
		if (getIntent().getBooleanExtra(EXTRA_IS_HIDE_DELETE_BTN, false)) {
			bt_delete.setVisibility(View.GONE);
			// 是否发送到配货市场按钮
			// switch_button.setVisibility(View.GONE);
			// findViewById(R.id.rl_hide).setVisibility(View.GONE);
			// findViewById(R.id.view_hide).setVisibility(View.GONE);
		}
		bt_forwarding.setOnClickListener(this);
//		bt_immediately_consult.setOnClickListener(this);
		bt_delete.setOnClickListener(this);
		// 搜索车源货源如果是自己的，删除按钮隐藏
		if (getIntent().getBooleanExtra(EXTRA_IS_HIDE_DELETE_BTN, false)) {
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
					protected boolean onSetRequest(FreightReq.Builder req) {
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
				}

				tv_time.setText(DateUtil.long2YMDHM(mFreight.getCreate_time()));
				// 起始地址，终止地址
				tv_region.setText(mFreight.getStart_region() + "-"
						+ mFreight.getEnd_region());
				tv_describe.setText(mFreight.getDesc());
				*//**************** 我的车源详情信息费 ***********************//*
				// tv_money.setText(String.valueOf(mFreight.getInfo_cost()));
				tv_user_show_name.setText(mFreight.getOwner_name());
				if (mFreight.getDistribution_market() == Properties.FREIGHT_NOT_POST_TO_MARKET_SCREEN) {
					mSwitchButton.setSwitch(false);
				} else {
					mSwitchButton.setSwitch(true);
				}

			}
		};
		task.execute();

	}

	@Override
	public synchronized void onSwitch(SwitchButton btn, final boolean on) {
		// TODO Auto-generated method stub
		NetUpdateFreightWhetherCanPostToMarketScreenStatus net = new NetUpdateFreightWhetherCanPostToMarketScreenStatus(
				this) {
			@Override
			protected boolean onSetRequest(
					com.epeisong.logistics.proto.Eps.FreightReq.Builder req) {
				req.setFreightId(mFreight_id);
				int value = on ? Properties.FREIGHT_POST_TO_MARKET_SCREEN
						: Properties.FREIGHT_NOT_POST_TO_MARKET_SCREEN;
				req.setNewStatus(value);
				return true;
			}
		};
		net.request(new OnNetRequestListenerImpl<FreightResp.Builder>() {
			@Override
			public void onSuccess(FreightResp.Builder response) {
				mSwitchButton.setSwitch(on);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_forwarding:
			Intent intent1 = new Intent(getApplicationContext(),
					ChooseContactsActivity.class);
			startActivityForResult(intent1, REQUEST_CHOOSE_CONTACTS_NOTICE);
			break;
		case R.id.bt_immediately_consult:
			Intent intent2 = new Intent(MyCarSourceActivity.this,
					ChatRoomActivity.class);
			intent2.putExtra(ChatRoomActivity.EXTRA_REMOTE_ID, user_id);
			intent2.putExtra(ChatRoomActivity.EXTRA_BUSINESS_TYPE,
					ChatMsg.business_type_freight);
			intent2.putExtra(ChatRoomActivity.EXTRA_BUSINESS_ID, mFreight_id);
			startActivity(intent2);
			break;
		case R.id.bt_delete:
			NetUpdateFreightStatus update = new NetUpdateFreightStatus(
					MyCarSourceActivity.this) {
				@Override
				protected boolean onSetRequest(FreightReq.Builder req) {
					req.setFreightId(Integer.parseInt(mFreight.getId()));
					req.setNewStatus(Freight.STATUS_DELETED);
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
					data.putExtra(BlackBoardActivity.EXTRA_FREIGHT, mFreight);
					setResult(BlackBoardActivity.RESULT_CODE_DELETE_DETAIL,
							data);
					finish();
				}
			});
			break;
		}
	}
}
*/