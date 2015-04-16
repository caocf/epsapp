package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import lib.universal_image_loader.ImageLoaderUtils;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.AdjustHeightGridView;
import com.epeisong.base.view.AdjustHeightGridView.OnBlankTouchListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.model.Contacts;
import com.epeisong.model.Freight;
import com.epeisong.model.User;
import com.epeisong.net.request.NetNoticeOrRelayDispatch;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 转发、通知
 * 
 * @author poet
 * 
 */
public class NoticeOrRelayActivity extends BaseActivity implements
		OnClickListener, OnBlankTouchListener {
	private class MyAdapter extends HoldDataBaseAdapter<Contacts> {

		@Override
		public int getCount() {
			return super.getCount() + 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == getCount() - 1) {
				return 2;
			} else if (position == getCount() - 2) {
				return 1;
			} else {
				return 0;
			}
		}

		public int getRealCount() {
			return super.getCount();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position < getCount() - 2) {
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = SystemUtils
							.inflate(R.layout.item_notice_or_relay_contacts);
					holder = new ViewHolder();
					holder.findView(convertView);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.fillData(getItem(position));
				if (mIsEditStatus) {
					holder.iv_delete.setVisibility(View.VISIBLE);
				} else {
					holder.iv_delete.setVisibility(View.GONE);
				}
				return convertView;
			}

			ImageView iv = new ImageView(getApplicationContext());
			if (position == getCount() - 2) {
				iv.setImageResource(R.drawable.btn_add_contacts);
				iv.setTag("add");
			} else if (position == getCount() - 1) {
				iv.setImageResource(R.drawable.btn_remove_contacts);
				iv.setTag("remove");
			}
			iv.setOnClickListener(NoticeOrRelayActivity.this);
			if (mIsEditStatus) {
				iv.setVisibility(View.GONE);
			} else {
				if (position == getCount() - 2) {
					iv.setVisibility(View.VISIBLE);
				} else {
					if (getCount() > 2) {
						iv.setVisibility(View.VISIBLE);
					} else {
						iv.setVisibility(View.GONE);
					}
				}
			}
			iv.setLayoutParams(new AbsListView.LayoutParams(mItemIvWidth,
					mItemIvWidth));
			if (position == getCount() - 2) {
				LinearLayout ll = new LinearLayout(getApplicationContext());
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.addView(iv);
				TextView tv = new TextView(getApplicationContext());
				ll.addView(tv);
				return ll;
			}
			return iv;
		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}
	}

	private class ViewHolder {
		ImageView iv_head;
		ImageView iv_delete;
		TextView tv_name;

		public void fillData(Contacts contacts) {
			// iv_head
			if (!TextUtils.isEmpty(contacts.getLogo_url())) {
				ImageLoader.getInstance().displayImage(contacts.getLogo_url(),
						iv_head, ImageLoaderUtils.getListOptionsForUserLogo());
			} else {
				int defaultIcon = User.getDefaultIcon(
						contacts.getLogistic_type_code(), true);
				iv_head.setImageResource(defaultIcon);
			}
			tv_name.setText(contacts.getShow_name());
			iv_delete.setTag(contacts);
		}

		public void findView(View v) {
			iv_head = (ImageView) v.findViewById(R.id.iv_head);
			iv_delete = (ImageView) v.findViewById(R.id.iv_delete);
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			iv_delete.setOnClickListener(NoticeOrRelayActivity.this);

			iv_head.setLayoutParams(new RelativeLayout.LayoutParams(
					mItemIvWidth, mItemIvWidth));
		}
	}

	private static final int REQUEST_CODE_ADD_CONTACTS = 100;
	public static final String EXTRA_CONTACTS_LIST = "contacts_list";
	public static final String EXTRA_DISPATCH = "dispatch";
	public static final String EXTRA_ACTION_TYPE = "action_type";

	public static final int ACTION_NOTICE = 1;
	public static final int ACTION_RELAY = 2;
	private int mActionType;
	private ArrayList<Contacts> mContactss;
	private String userId;

	private Freight mDispatch;
	private String mBaseTitleText;
	private AdjustHeightGridView mGridView;
	private MyAdapter mAdapter;

	private int mItemIvWidth;
	private boolean mIsEditStatus;

	//
	private RadioButton mOriginalRb; // 原始联系信息选择按钮

	private RadioButton mMineRb; // 我的联系信息选择按钮

	private DataSetObserver mDataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			String title = String.format(mBaseTitleText,
					mAdapter.getRealCount());
			setTitleText(title);
		};
	};

	@Override
	public boolean onBlackTouch(GridView gridView) {
		if (mIsEditStatus) {
			mIsEditStatus = false;
			mAdapter.notifyDataSetChanged();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_notice) {
			// TODO
			if (mActionType == ACTION_NOTICE || mActionType == ACTION_RELAY) {
				noticeOrRelay();
			} else {
				if (mOriginalRb.isChecked()) {
					ToastUtils.showToast("original");
				} else {
					ToastUtils.showToast("my");
				}
			}
			return;
		}
		Object tag = v.getTag();
		if (tag != null) {
			if (tag instanceof String) {
				if ("add".equals(tag)) {
					Intent intent = new Intent(getApplicationContext(),
							ChooseNewContactsActivity.class);
					intent.putExtra(
							ChooseNewContactsActivity.EXTRA_ORIGINAL_CONTACTS_LIST,
							(ArrayList<Contacts>) mAdapter.getAllItem());
					intent.putExtra("originalUserId", userId);
					startActivityForResult(intent, REQUEST_CODE_ADD_CONTACTS);
				} else if ("remove".equals(tag)) {
					mIsEditStatus = true;
					mAdapter.notifyDataSetChanged();
				}
			} else if (tag instanceof Contacts) {
				Contacts c = (Contacts) tag;
				mAdapter.removeItem(c);
			}
		}
	}

	private void initViewAndFillData() {
		TextView tv_region = (TextView) findViewById(R.id.tv_region);
		tv_region.setText(mDispatch.getRegion());
		TextView tv_desc = (TextView) findViewById(R.id.tv_describe);
		tv_desc.setText(mDispatch.getDesc());

		Button btn = (Button) findViewById(R.id.btn_notice);
		btn.setOnClickListener(this);
		User user = UserDao.getInstance().getUser();
		if (mActionType == ACTION_NOTICE) {
			TextView tv_time = (TextView) findViewById(R.id.tv_time);
			ImageView tv_type = (ImageView) findViewById(R.id.iv_goods);
			TextView tv_show_name = (TextView) findViewById(R.id.tv_user_show_name);
			TextView tv_contacts_name = (TextView) findViewById(R.id.tv_contact_name);
			TextView tv_contacts_phone = (TextView) findViewById(R.id.tv_phone_number);
			TextView tv_contacts_tel = (TextView) findViewById(R.id.tv_telephone_number);
			tv_time.setText(DateUtil.long2YMDHM(mDispatch.getCreate_time()));
			tv_show_name.setText(mDispatch.getOwner_name());
			tv_contacts_name.setText(user.getUser_type_name());
			tv_contacts_phone.setText(user.getContacts_phone());
			tv_contacts_tel.setText(user.getContacts_telephone());
			if (mDispatch.getType() == Freight.TYPE_GOODS) {
				tv_type.setBackgroundResource(R.drawable.black_board_goods);
			} else if (mDispatch.getType() == Freight.TYPE_TRUCK) {
				tv_type.setBackgroundResource(R.drawable.black_board_truck);
			}
			btn.setText("群发");
		} else {
			TextView tv_time = (TextView) findViewById(R.id.tv_time);
			ImageView tv_type = (ImageView) findViewById(R.id.iv_goods);
			TextView tv_show_name = (TextView) findViewById(R.id.tv_user_show_name);
			TextView tv_contacts_name = (TextView) findViewById(R.id.tv_contact_name);
			TextView tv_contacts_phone = (TextView) findViewById(R.id.tv_phone_number);
			TextView tv_contacts_tel = (TextView) findViewById(R.id.tv_telephone_number);
			tv_time.setText(DateUtil.long2YMDHM(mDispatch.getCreate_time()));
			tv_show_name.setText(mDispatch.getOwner_name());
			tv_contacts_name.setText(user.getUser_type_name());
			tv_contacts_phone.setText(user.getContacts_phone());
			tv_contacts_tel.setText(user.getContacts_telephone());
			if (mDispatch.getType() == Freight.TYPE_GOODS) {
				tv_type.setBackgroundResource(R.drawable.black_board_goods);
			} else if (mDispatch.getType() == Freight.TYPE_TRUCK) {
				tv_type.setBackgroundResource(R.drawable.black_board_truck);
			}
			// btn.setText("通知");
			// mOriginalRb = (RadioButton) findViewById(R.id.rb_original);
			// mMineRb = (RadioButton) findViewById(R.id.rb_my);
			// findViewById(R.id.rl_contacts_relay_item_original)
			// .setOnClickListener(this);
			// findViewById(R.id.rl_contacts_relay_item_my).setOnClickListener(
			// this);
			btn.setText("转发");
		}
	}

	private void noticeOrRelay() {
		List<Contacts> list = mAdapter.getAllItem();
		if (list == null || list.isEmpty()) {
			return;
		}
		List<Integer> ids = new ArrayList<Integer>();
		for (Contacts c : list) {
			ids.add(Integer.parseInt(c.getId()));
		}
		NetNoticeOrRelayDispatch net = new NetNoticeOrRelayDispatch(this,
				mDispatch.getId(), ids);
		net.request(new OnNetRequestListenerImpl<FreightResp.Builder>() {
			@Override
			public void onSuccess(FreightResp.Builder response) {
				ToastUtils.showToast("操作成功");
				finish();
			}
		});
	}

	@Override
	protected TitleParams getTitleParams() {
		String title = String.format(mBaseTitleText, mContactss.size());
		return new TitleParams(getDefaultHomeAction(), title, null)
				.setShowLogo(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_ADD_CONTACTS && data != null) {
			ArrayList<Contacts> list = (ArrayList<Contacts>) data
					.getSerializableExtra(ChooseNewContactsActivity.EXTRA_SELECTED_CONTACTS_LIST);
			userId = data.getStringExtra("originalUserId");
			mAdapter.addAll(list);
			// mAdapter.replaceAll(list);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mActionType = getIntent().getIntExtra(EXTRA_ACTION_TYPE, 0);
		if (mActionType == ACTION_NOTICE) {
			mBaseTitleText = getString(R.string.notice_base_title);
		} else if (mActionType == ACTION_RELAY) {
			mBaseTitleText = getString(R.string.relay_base_title);
		} else {
			throw new RuntimeException("EXTRA_ACTION_TYPE 数据错误，只能处理通知或转发");
		}
		mContactss = (ArrayList<Contacts>) getIntent().getSerializableExtra(
				EXTRA_CONTACTS_LIST);
		mDispatch = (Freight) getIntent().getSerializableExtra(EXTRA_DISPATCH);
		userId = getIntent().getStringExtra("originalUserId");
		super.onCreate(savedInstanceState);
		int padding = (int) (DimensionUtls.getPixelFromDp(10) * 4);
		int spacing = (int) (DimensionUtls.getPixelFromDp(8) * 5);
		mItemIvWidth = (EpsApplication.getScreenWidth() - padding - spacing) / 6;
		setContentView(R.layout.activity_notice);
		mAdapter = new MyAdapter();
		mAdapter.replaceAll(mContactss);
		mAdapter.registerDataSetObserver(mDataSetObserver);
		mGridView = (AdjustHeightGridView) findViewById(R.id.gv_contactss);
		mGridView.setMinimumHeight(DimensionUtls.getPixelFromDpInt(100));

		mGridView.setAdapter(mAdapter);
		mGridView.setOnBlankTouchListener(this);
		
		initViewAndFillData();
	}

	@Override
	protected void onDestroy() {
		mAdapter.unregisterDataSetObserver(mDataSetObserver);
		super.onDestroy();
	}
}
