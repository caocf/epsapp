package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.TabPages2Activity;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.adapter.impl.IconTextAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.base.fragment.PendingFragment;
import com.epeisong.base.fragment.PendingFragment.OnPendingAgainListener;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.PhoneContactsDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetLogisticsInfo;
import com.epeisong.data.net.parser.ContactsParser;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq;
import com.epeisong.model.Contacts;
import com.epeisong.model.User;
import com.epeisong.net.request.NetAddContacts;
import com.epeisong.net.request.NetContactsUpdateStatus;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.ui.fragment.CarGoodsSourceFragment;
import com.epeisong.ui.fragment.ChatRoomFragment;
import com.epeisong.ui.fragment.ContactsInfoFragment;
import com.epeisong.ui.fragment.ContactsOrderFragment;
import com.epeisong.ui.fragment.StowageInformationFragment;
import com.epeisong.utils.ContactsUtils;
import com.epeisong.utils.ContactsUtils.OnContactsUtilsListener;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 联系人详情
 * 
 * @author Jack
 * 
 */
public class ContactsDetailActivity extends TabPages2Activity {

	// public static final String EXTRA_CONTACTS_ID = "contacts_id";
	public static final String EXTRA_USER = "user";

	public static final String EXTRA_USER_ID = "user_id";
	public static final String EXTRA_USER_TYPEID = "user_typeid";
	public static final String EXTRA_TAGNODIS_STRING = "tagdisplay";
	public static final String EXTRA_SHOW_PAGE_COUNT_OTHER = "show_page_count_other";

	private View mTitleRight;
	private int Logistic_type;
	private String mUserId;
	private User mUser;
	private int tagdis;
	private int mContactstype;

	private int mShowPageCount = 3;
	// private ImageView iv;
	private List<Fragment> mFragments;
	private ChatRoomFragment mChatRoomFragment;
	private Fragment fg;

	private PopupWindow mPopupWindowMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int Product_type;
		mShowPageCount = getIntent()
				.getIntExtra(EXTRA_SHOW_PAGE_COUNT_OTHER, 3);
		mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
		mUser = (User) getIntent().getSerializableExtra(EXTRA_USER);
		tagdis = getIntent().getIntExtra(EXTRA_TAGNODIS_STRING, 0);
		Product_type = getIntent().getIntExtra(
				String.valueOf(R.string.producttypenum), 0);

		super.onCreate(savedInstanceState);
		// if (mUser == null) {
		// ToastUtils.showToast("参数错误");
		// finish();
		// return;
		// }
		if (TextUtils.isEmpty(mUserId)) {
			throw new RuntimeException("contact is can not be empty!");
		}
		Bundle args = new Bundle();
		args.putString(EXTRA_USER_ID, mUserId);
		args.putString(ChatRoomFragment.ARGS_REMOTE_ID, mUserId);
		args.putInt(EXTRA_TAGNODIS_STRING, tagdis);
		args.putInt(EXTRA_USER_TYPEID, Logistic_type);
		args.putInt(String.valueOf(R.string.producttypenum), Product_type);
		mFragments = new ArrayList<Fragment>();
		// TODO
		ContactsInfoFragment contactsInfoFragment = new ContactsInfoFragment();
		contactsInfoFragment.setArguments(args);
		mFragments.add(contactsInfoFragment);

		if (mShowPageCount >= 2) {
			switch (Logistic_type) {
			case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
				// titles.add("小黑板");
				CarGoodsSourceFragment cargoodsFragment = new CarGoodsSourceFragment();
				((Fragment) cargoodsFragment).setArguments(args);
				args.putSerializable(EXTRA_USER, mUser);
				args.putString(EXTRA_USER_ID, mUserId);
				mFragments.add(cargoodsFragment);
				break;
			case Properties.LOGISTIC_TYPE_MARKET:
			case Properties.LOGISTIC_TYPE_EXPRESS:
			case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
			case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
				// titles.add("配载信息部");// 会员");
				StowageInformationFragment stowageFragment = new StowageInformationFragment();

				args.putSerializable(EXTRA_USER, mUser);
				args.putString(EXTRA_USER_ID, mUserId);
				args.putInt(EXTRA_USER_TYPEID, Logistic_type);
				((Fragment) stowageFragment).setArguments(args);
				mFragments.add(stowageFragment);
				break;

			default:// 报价 // 下单
				ContactsOrderFragment orderFragment1 = new ContactsOrderFragment();

				args.putSerializable(EXTRA_USER, mUser);
				args.putString(EXTRA_USER_ID, mUserId);
				args.putInt(EXTRA_USER_TYPEID, Logistic_type);

				orderFragment1.setArguments(args);
				mFragments.add(orderFragment1);
				break;
			}
			fg = mFragments.get(1);
		}
		if (mShowPageCount >= 3) {
			mChatRoomFragment = new ChatRoomFragment();
			mChatRoomFragment.setArguments(args);
			mFragments.add(mChatRoomFragment);
		}

		// mViewPager.setCurrentItem(2);
		mViewPager.setOffscreenPageLimit(2);

		switch (Logistic_type) {
		case Properties.LOGISTIC_TYPE_MARKET:
		case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
		case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
			mViewPager.setCurrentItem(1, false);
			break;
		}

		requestData();
		InitContactsStatus();
	}

	@Override
	public boolean isFinishing() {
		// TODO Auto-generated method stub
		// ToastUtils.showToast("联系人finishing");
		return super.isFinishing();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// ToastUtils.showToast("联系人退出");
		super.onDestroy();
	}

	public void setFragmenttoChat() {
		mViewPager.setCurrentItem(2);
	}

	@Override
	public void onBackPressed() {
		int pos = mViewPager.getCurrentItem();
		if (pos == 2) {
			if (mChatRoomFragment.hideMoreView()) {
				return;
			}
		}

		super.onBackPressed();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int pos = mViewPager.getCurrentItem();
		if (fg == null) {
			return super.onKeyDown(keyCode, event);
		}

		if (pos == 1)// 处理中间keydown
		{
			// TODO Auto-generated method stub
			// Log.d("ActionBar", "OnKey事件");
			if (fg instanceof StowageInformationFragment) {
				if (StowageInformationFragment.onKeyDown(keyCode, event) == false)
					return false;
			} else if (fg instanceof CarGoodsSourceFragment) {
				if (CarGoodsSourceFragment.onKeyDown(keyCode, event) == false)
					return false;
			} else if (fg instanceof ContactsOrderFragment) {
				if (ContactsOrderFragment.onKeyDown(keyCode, event) == false)
					return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected PagerAdapter onGetAdapter() {
		return new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return mShowPageCount;
			}

			@Override
			public Fragment getItem(int position) {

				return mFragments.get(position);
			}
		};
	}

	@Override
	protected void onSetTabTitle(List<String> titles) {
		titles.add("基本信息");
		if (mShowPageCount == 1) {
			return;
		}
		Logistic_type = getIntent().getIntExtra(EXTRA_USER_TYPEID,
				Logistic_type);
		switch (Logistic_type) {
		case Properties.LOGISTIC_TYPE_MARKET:// 一类， 类型不变
			titles.add("市场摊位");// 会员");//配载信息部
			// mUser.setUser_type_code(Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT);
			break;
		case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
			titles.add("小黑板");
			break;
		case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:// 物流园//一类， 类型不变
			// mUser.setUser_type_code(Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE);
			titles.add("园区内专线");
			break;
		case Properties.LOGISTIC_TYPE_EXPRESS:// 快递
		case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
			titles.add("收发网点");
			break;
		default:
			titles.add("报价");// "下单");
			break;
		}
		if (mShowPageCount == 2) {
			return;
		}
		titles.add("咨询");
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mChatRoomFragment != null && mChatRoomFragment.handleTouchEvent(ev)) {
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	private Action createAction() {
		return new ActionImpl() {

			@Override
			public void doAction(View v) {
				showMenuPopupWindow();
			}

			@Override
			public View getView() {
				mTitleRight = new ImageView(getApplicationContext());
				// ((ImageView)
				// mTitleRight)//chatroom_contacts_option_is_contacts
				// chatroom_contacts_option_not_contacts
				// .setImageResource(R.drawable.selector_contacts_info_add);
				// mTitleRight = iv;
				// mTitleRight.setVisibility(View.GONE);
				return mTitleRight;
			}
		};
	}

	private void initPopupWindowMenu() {
		List<IconTextItem> items = new ArrayList<IconTextItem>();
		final Contacts c = ContactsDao.getInstance().queryById(mUserId);

		if (mContactstype == Contacts.STATUS_DELETED)// 陌生人
		{
			items.add(new IconTextItem(0, "加入联系人", null));
			items.add(new IconTextItem(0, "加入黑名单", null));
			items.add(new IconTextItem(0, "投诉", null));
		} else if (mContactstype == Contacts.STATUS_NORNAL)// 联系人
		{
			items.add(new IconTextItem(0, "移出联系人", null));
			items.add(new IconTextItem(0, "加入黑名单", null));
			items.add(new IconTextItem(0, "投诉", null));
		} else// 黑名单
		{
			items.add(new IconTextItem(0, "移出黑名单", null));
			items.add(new IconTextItem(0, "投诉", null));
		}
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
					final int position, long id) {
				mPopupWindowMenu.dismiss();
				mPopupWindowMenu = null;
				switch (mContactstype) {
				case Contacts.STATUS_DELETED:// 陌生人
					if (position == 0) {
						AddContacts(c);// 加为联系人
					} else if (position == 1)// 不是联系人无法加入黑名单
						addBlackList();// 加入黑名单
					else {
						// Toast.makeText(getApplicationContext(), "投诉",
						// Toast.LENGTH_SHORT).show();// 投诉
						if(mUser != null){
							Intent complaint = new Intent(getApplication(),CustomerComplaintActivity.class);
							complaint.putExtra("user", mUser);
							startActivity(complaint);
						}else{
							 Toast.makeText(getApplicationContext(), "投诉",
							 Toast.LENGTH_SHORT).show();// 投诉
						}
					}
					break;
				case Contacts.STATUS_NORNAL:// 联系人
					if (position == 0)
						deleteContacts(c);// 移出联系人
					else if (position == 1)
						addBlackList();// 加入黑名单
					else {
						// Toast.makeText(getApplicationContext(), "投诉",
						// Toast.LENGTH_SHORT).show();// 投诉
						if(mUser != null){
							Intent complaint = new Intent(getApplication(),
									CustomerComplaintActivity.class);
							complaint.putExtra("user", mUser);
							startActivity(complaint);
						}else{
							 Toast.makeText(getApplicationContext(), "投诉",
							 Toast.LENGTH_SHORT).show();// 投诉
						}
					}
					break;
				case Contacts.STATUS_BLACKLIST://
					if (position == 0)
						deleteContacts(c);// 移出黑名单
					else {
						// Toast.makeText(getApplicationContext(), "投诉",
						// Toast.LENGTH_SHORT).show();// 投诉
						if(mUser != null){
							Intent complaint = new Intent(getApplication(),
									CustomerComplaintActivity.class);
							complaint.putExtra("user", mUser);
							startActivity(complaint);
						}else{
							 Toast.makeText(getApplicationContext(), "投诉",
							 Toast.LENGTH_SHORT).show();// 投诉
						}
					}

					break;
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

	private void deleteContacts(Contacts c) {
		showPendingDialog(null);
		ContactsUtils.delete(c.getId(), new OnContactsUtilsListener() {
			@Override
			public void onContactsUtilsComplete(int option, boolean success) {
				dismissPendingDialog();
				if (success) {
					mContactstype = Contacts.STATUS_DELETED;
					ContactsStatusImage(mContactstype);
				}
			}
		});
	}

	private void AddContacts(Contacts c) {
		NetAddContacts net = new NetAddContacts(ContactsDetailActivity.this,
				mUserId);
		net.request(new OnNetRequestListenerImpl<CommonLogisticsResp.Builder>() {
			@Override
			public void onSuccess(CommonLogisticsResp.Builder resp) {
				ProtoEBizLogistics logistics = resp.getBizLogistics(0);
				if (logistics != null) {
					Contacts c = ContactsParser.parse(logistics);

					c.setStatus(Contacts.STATUS_NORNAL);
					mContactstype = Contacts.STATUS_NORNAL;
					ContactsStatusImage(mContactstype);
					ContactsDao.getInstance().insert(c);
					PhoneContactsDao.getInstance().updateAdded(c.getPhone());
					ToastUtils.showToast("添加成功");
				}
			}
		});
	}

	// 加入黑名单
	private void addBlackList() {

		XBaseActivity a = null;
		if (this instanceof TabPages2Activity) {
			a = (XBaseActivity) this;
		}
		NetContactsUpdateStatus blackList = new NetContactsUpdateStatus(a) {
			@Override
			protected boolean onSetRequest(ContactReq.Builder req) {
				req.setContactId(Integer.parseInt(mUserId));// c.getId()));
				req.setNewStatus(Contacts.STATUS_BLACKLIST);
				return true;
			}
		};
		blackList
				.request(new OnNetRequestListenerImpl<CommonLogisticsResp.Builder>() {

					@Override
					public void onSuccess(CommonLogisticsResp.Builder response) {
						// TODO Auto-generated method stub
						if (mUser != null) {
							Contacts c = Contacts.convertFromUser(mUser);
							c.setStatus(Contacts.STATUS_BLACKLIST);
							if (mContactstype == Contacts.STATUS_DELETED) {
								ContactsDao.getInstance().insert(c);
							} else {
								ContactsDao.getInstance().update(c);
							}
							mContactstype = Contacts.STATUS_BLACKLIST;
							ContactsStatusImage(mContactstype);
						}

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

	private synchronized void requestData() {
		AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>() {
			@Override
			protected User doInBackground(Void... params) {
				NetLogisticsInfo net = new NetLogisticsInfo() {
					@Override
					protected boolean onSetRequest(
							com.epeisong.logistics.proto.Eps.LogisticsReq.Builder req) {
						req.setLogisticsId(Integer.parseInt(mUserId));
						return true;
					}
				};
				try {
					CommonLogisticsResp.Builder resp = net.request();
					if (net.isSuccess(resp)) {
						return UserParser.parseSingleUser(resp);
					}
				} catch (NetGetException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(User result) {
				mUser = result;
				if (result != null) {
					if (UserDao.getInstance().getUser().getId()
							.equals(result.getId())) {
						setTitleText("我");
					} else {
						setTitleText(result.getShow_name());
					}

					if (UserDao.getInstance().getUser().getId()
							.equals(result.getId()))
						mTitleRight.setVisibility(View.GONE);
					else if (ContactsDao.getInstance()
							.queryById(result.getId()) != null) {
						mTitleRight.setVisibility(View.VISIBLE);
					} else {
						mTitleRight.setVisibility(View.VISIBLE);
					}
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable("bundle", result);
				for (Fragment f : mFragments) {
					if (f instanceof PendingFragment) {
						PendingFragment pending = (PendingFragment) f;
						pending.endPending(result != null, bundle,
								new OnPendingAgainListener() {
									@Override
									public void onPendingAgain() {
										requestData();
									}
								});
					}
				}
			}
		};
		task.execute();
	}

	void ContactsStatusImage(int mcontectstype) {
		switch (mcontectstype) {
		case -1:
			mTitleRight.setVisibility(View.GONE);
			break;
		case Contacts.STATUS_DELETED:
			((ImageView) mTitleRight)// chatroom_contacts_option_is_contacts
					.setImageResource(R.drawable.chatroom_contacts_option_not_contacts);
			break;
		default:
			((ImageView) mTitleRight)// chatroom_contacts_option_is_contacts
					.setImageResource(R.drawable.chatroom_contacts_option_is_contacts);
			break;
		}

	}

	void InitContactsStatus() {
		Contacts dcContacts = ContactsDao.getInstance().queryById(mUserId);

		if (dcContacts != null) {
			if (UserDao.getInstance().getUser().getId().equals(mUserId)) {
				setTitleText("我");
				mContactstype = -1;
			} else {
				setTitleText(dcContacts.getShow_name());
			}

			if (UserDao.getInstance().getUser().getId().equals(mUserId))
				mTitleRight.setVisibility(View.GONE);
			else if (ContactsDao.getInstance().queryById(mUserId) != null) {
				mTitleRight.setVisibility(View.VISIBLE);
				if (dcContacts.getStatus() == Contacts.STATUS_BLACKLIST)
					mContactstype = Contacts.STATUS_BLACKLIST;
				else {
					mContactstype = Contacts.STATUS_NORNAL;
				}
			} else {
				mTitleRight.setVisibility(View.VISIBLE);
				mContactstype = Contacts.STATUS_DELETED;
			}
		} else {
			mContactstype = Contacts.STATUS_DELETED;
		}
		ContactsStatusImage(mContactstype);
	}

	@Override
	protected TitleParams getTitleParams() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(createAction());
		return new TitleParams(getDefaultHomeAction(), "", actions)
				.setShowLogo(false);
	}

}
