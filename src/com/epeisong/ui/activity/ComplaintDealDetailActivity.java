package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.epeisong.base.adapter.impl.IconTextAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.base.fragment.EmptyFragment;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetLogisticsInfo;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq.Builder;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.Complaint;
import com.epeisong.model.Contacts;
import com.epeisong.model.User;
import com.epeisong.ui.fragment.ChatRoomExpandFragment;
import com.epeisong.ui.fragment.ChatRoomExpandFragment.ChatRoomListAndItemChangable;
import com.epeisong.ui.fragment.ChatRoomExpandFragment.OnContactsOptionListener;
import com.epeisong.ui.fragment.ChatRoomFragment;
import com.epeisong.ui.fragment.ComplaintAdvisoryListFragment;
import com.epeisong.ui.fragment.ComplaintResultDetailFragment;
import com.epeisong.utils.ContactsUtils;
import com.epeisong.utils.ContactsUtils.OnContactsUtilsListener;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ToastUtils;

/**
 * 投诉详情页面
 * 
 * @author gnn
 * 
 */
public class ComplaintDealDetailActivity extends TabPages2Activity implements
		ChatRoomListAndItemChangable, OnContactsOptionListener,
		OnItemClickListener, OnContactsUtilsListener {
	public static final String EXTRA_BUSINESS_CHAT_MODEL = "business_chat_model";
	public static final String EXTRA_COMPLAINT = "trans_complaint";
	public static final String EXTRA_REMOTE_ID = "remote_id";
	public static final String EXTRA_SHOW_ADVISORY_FIRST = "show_advisory_first";
	public static final String EXTRA_SHOW_CHAT_FIRST = "show_chat_first";
	public static final String EXTRA_ADVISORYER_ID = "advisoryer_id";
	public static final String EXTRA_FINISH_TO_MAIN = "finish_to_main";
	public static final String EXTRA_WHETHER_CUSTOMER = "whether_customer"; //是否是客服登录
	private List<Fragment> mFragments;
	private BusinessChatModel mBusinessChatModel;
	private String mRemoteId;
	private EmptyFragment mEmptyFragment;
	private ChatRoomExpandFragment mChatRoomExpandFragment;
	private ComplaintAdvisoryListFragment mAdvisoryListFragment;

	private Complaint complaint;
	// private Complaint mComplaint;
	private boolean mShowAdvisoryFirst;
	 private String mWhetherCustomer;
	boolean mFinishToMain;
	private User mUser;

	private String mOptionContactsId;
	private ImageView mContactsOptionIv;
	private PopupWindow mPopupWindow;
	private IconTextAdapter mPopupAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBusinessChatModel = (BusinessChatModel) getIntent().getSerializableExtra(EXTRA_BUSINESS_CHAT_MODEL);
		complaint = (Complaint) getIntent().getSerializableExtra(EXTRA_COMPLAINT);
		mShowAdvisoryFirst = getIntent().getBooleanExtra(EXTRA_SHOW_ADVISORY_FIRST, false);
		mFinishToMain = getIntent().getBooleanExtra(EXTRA_FINISH_TO_MAIN, false);
		mWhetherCustomer = getIntent().getStringExtra(EXTRA_WHETHER_CUSTOMER);
		User user = UserDao.getInstance().getUser();
		mRemoteId = getIntent().getStringExtra(EXTRA_REMOTE_ID);
		// mRemoteId = complaint.getByNameId();
		if (mRemoteId == null && complaint != null) {
			mRemoteId = complaint.getByNameId();
		}
		//
		// if(complaint == null){
		// complaint = getComplaintById(mBusinessChatModel.getBusiness_id());
		// }

		mFragments = new ArrayList<Fragment>();
		ComplaintResultDetailFragment cFragment = new ComplaintResultDetailFragment();
		Bundle args = new Bundle();
		args.putSerializable("complaint", complaint);
		args.putSerializable(ComplaintResultDetailFragment.ARG_BUSINESS_ID, mBusinessChatModel.getBusiness_id());
		if(mWhetherCustomer != null){
			args.putString(ComplaintResultDetailFragment.EXTRA_WHETHER_CUSTOMER, mWhetherCustomer);
		}
		cFragment.setArguments(args);
		mFragments.add(cFragment);

		mEmptyFragment = new EmptyFragment();

		if (mBusinessChatModel.getBusiness_owner_id().equals(user.getId())) {
			if (mShowAdvisoryFirst) {
				mChatRoomExpandFragment = new ChatRoomExpandFragment();
				Bundle argsChat = new Bundle();
				argsChat.putSerializable(ChatRoomExpandFragment.ARGS_BUSINESS_CHAT_MODEL,mBusinessChatModel);
				argsChat.putString(ChatRoomExpandFragment.ARGS_REMOTE_ID,mRemoteId);
				argsChat.putBoolean(ChatRoomExpandFragment.ARGS_CAN_BACK_TO_LIST, true); // 是否以列表形式显示
				argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_ME,1);
				argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_OTHER, 1);
				mChatRoomExpandFragment.setArguments(argsChat);
				mEmptyFragment.replace(mChatRoomExpandFragment);
				mViewPager.setCurrentItem(1, false);
			} else {
				mAdvisoryListFragment = new ComplaintAdvisoryListFragment();
				Bundle argsList = new Bundle();
				argsList.putString(
						ComplaintAdvisoryListFragment.ARGS_COMPLAINT_ID,
						mBusinessChatModel.getBusiness_id());
				argsList.putSerializable(
						ComplaintAdvisoryListFragment.ARGS_COMPLIANT, complaint);
				if (complaint != null) {
					argsList.putString(
							ComplaintAdvisoryListFragment.ARGS_BUSINESS_OWNER_ID,
							complaint.getOwner_id());
				}
				mAdvisoryListFragment.setArguments(argsList);
				mEmptyFragment.replace(mAdvisoryListFragment);
			}
		}else{
			mChatRoomExpandFragment = new ChatRoomExpandFragment();
            Bundle argsChat = new Bundle();
            argsChat.putSerializable(ChatRoomFragment.ARGS_BUSINESS_CHAT_MODEL, mBusinessChatModel);
            argsChat.putString(ChatRoomFragment.ARGS_REMOTE_ID, mRemoteId);
            argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_ME, 1);
            argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_OTHER, 1);
            mChatRoomExpandFragment.setArguments(argsChat);
            mEmptyFragment.replace(mChatRoomExpandFragment);
            mViewPager.setCurrentItem(1, false);
		}

		// if(mRemoteId != null){
		// if (mShowAdvisoryFirst) {
		// if(!mRemoteId.equals(user.getId())){
		// mChatRoomExpandFragment = new ChatRoomExpandFragment();
		// Bundle argsChat = new Bundle();
		// argsChat.putSerializable(ChatRoomExpandFragment.ARGS_BUSINESS_CHAT_MODEL,
		// mBusinessChatModel);
		// argsChat.putString(ChatRoomExpandFragment.ARGS_REMOTE_ID, mRemoteId);
		// argsChat.putBoolean(ChatRoomExpandFragment.ARGS_CAN_BACK_TO_LIST,
		// true); //是否以列表形式显示
		// argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_ME, 1);
		// argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_OTHER, 1);
		// mChatRoomExpandFragment.setArguments(argsChat);
		// mEmptyFragment.replace(mChatRoomExpandFragment);
		// mViewPager.setCurrentItem(1, false);
		// }else{
		// Bundle argsEmpty = new Bundle();
		// argsEmpty.putString("flag", "flag");
		// mEmptyFragment.setArguments(argsEmpty);
		// }
		// } else {
		// if(!mRemoteId.equals(user.getId())){
		// mAdvisoryListFragment = new ComplaintAdvisoryListFragment();
		// Bundle argsList = new Bundle();
		// argsList.putString(ComplaintAdvisoryListFragment.ARGS_COMPLAINT_ID,
		// mBusinessChatModel.getBusiness_id());
		// argsList.putSerializable(ComplaintAdvisoryListFragment.ARGS_COMPLIANT,
		// complaint);
		// mAdvisoryListFragment.setArguments(argsList);
		// mEmptyFragment.replace(mAdvisoryListFragment);
		// }else{
		// Bundle argsEmpty = new Bundle();
		// argsEmpty.putString("flag", "flag");
		// mEmptyFragment.setArguments(argsEmpty);
		// }
		// }
		// }
		mFragments.add(mEmptyFragment);
	}

	@Override
	protected void onSetTabTitle(List<String> titles) {
		titles.add("详情");
		titles.add("会话");
	}

	private void getUserInfo() {
		showPendingDialog(null);
		AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>() {
			@Override
			protected User doInBackground(Void... params) {
				NetLogisticsInfo netInfo = new NetLogisticsInfo() {
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setLogisticsId(Integer.parseInt(mOptionContactsId));
						return true;
					}

				};
				try {
					CommonLogisticsResp.Builder resp = netInfo.request();
					if (resp != null) {
						return UserParser.parseSingleUser(resp);
					}
				} catch (NetGetException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(User result) {
				dismissPendingDialog();
				super.onPostExecute(result);
				if (result != null) {
					mUser = result;
				}
			}
		};
		task.execute();

	}

	// private Complaint getComplaintById(final String id) {
	// AsyncTask<Void, Void, Complaint> task = new AsyncTask<Void, Void,
	// Complaint>(){
	// @Override
	// protected Complaint doInBackground(Void... params) {
	// NetComplaintById net = new NetComplaintById() {
	// @Override
	// protected boolean onSetRequest(LogisticsReq.Builder req) {
	// req.setId(id);
	// return true;
	// }
	// };
	// try {
	// CommonLogisticsResp.Builder resp = net.request();
	// if (resp != null) {
	// return ComplaintParser.parseSingleComplaint(resp);
	// }
	// } catch (NetGetException e) {
	// e.printStackTrace();
	// return null;
	// }
	// return null;
	// }
	// @Override
	// protected void onPostExecute(Complaint result) {
	// super.onPostExecute(result);
	// if(result != null){
	// mComplaint = result;
	// }
	// }
	// };
	// task.execute();
	// return mComplaint;
	// }

	@Override
	protected PagerAdapter onGetAdapter() {
		return new FragmentStatePagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return 2;
			}

			@Override
			public Fragment getItem(int position) {
				return mFragments.get(position);
			}
		};
	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "投诉详情", null)
				.setShowLogo(false);
	}

	@Override
	public void changeToChatRoomItem(BusinessChatModel model, String remote_id) {
		if (TextUtils.isEmpty(remote_id)) {
			ToastUtils.showToast("remote_id is empty!");
			return;
		}
		if (mChatRoomExpandFragment == null) {
			mChatRoomExpandFragment = new ChatRoomExpandFragment();
		}
		Bundle args = new Bundle();
		args.putSerializable(ChatRoomExpandFragment.ARGS_BUSINESS_CHAT_MODEL,
				model);
		args.putString(ChatRoomExpandFragment.ARGS_REMOTE_ID, remote_id);
		args.putBoolean(ChatRoomExpandFragment.ARGS_CAN_BACK_TO_LIST, true);
		args.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_ME, 1);
		args.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_OTHER, 1);
		mChatRoomExpandFragment.setArguments(args);
		mEmptyFragment.replace(mChatRoomExpandFragment);

	}

	@Override
	public void changeToChatRoomList(String business_id) {
		if (TextUtils.isEmpty(business_id)) {
			ToastUtils.showToast("business_id is emtpy!");
			return;
		}
		if (mAdvisoryListFragment == null) {
			mAdvisoryListFragment = new ComplaintAdvisoryListFragment();
		}
		Bundle argsList = new Bundle();
		argsList.putString(ComplaintAdvisoryListFragment.ARGS_COMPLAINT_ID,
				business_id);
		mAdvisoryListFragment.setArguments(argsList);
		mEmptyFragment.replace(mAdvisoryListFragment);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mPopupWindow.dismiss();
		IconTextItem item = mPopupAdapter.getItem(position);
		if (item.getName().equals("投诉")) {
			getUserInfo();
			if (mUser != null) {
				Intent complaint = new Intent(getApplication(),
						CustomerComplaintActivity.class);
				complaint.putExtra("user", mUser);
				startActivity(complaint);
			} else {
				Toast.makeText(getApplicationContext(), "请重新点击投诉",
						Toast.LENGTH_SHORT).show();// 投诉
			}
		} else {
			ContactsUtils.onContactsOption(item.getName(), mOptionContactsId,
					this);
			showPendingDialog(null);
		}
	}

	@Override
	public void onContactsUtilsComplete(int option, boolean success) {
		dismissPendingDialog();
		if (success) {
			switch (option) {
			case OnContactsUtilsListener.option_add:
				mChatRoomExpandFragment
						.onContactsStatusChange(R.drawable.chatroom_contacts_option_is_contacts);
				ToastUtils.showToast("添加成功");
				break;
			case OnContactsUtilsListener.option_delete:
				mChatRoomExpandFragment
						.onContactsStatusChange(R.drawable.chatroom_contacts_option_not_contacts);
				ToastUtils.showToast("删除成功");
				break;
			case OnContactsUtilsListener.option_black:
				mChatRoomExpandFragment
						.onContactsStatusChange(R.drawable.chatroom_contacts_option_is_contacts);
				ToastUtils.showToast("已加入黑名单");
				break;
			}
		}
	}

	@Override
	public void onContactsOption(Contacts contacts, View optionView) {

		if (contacts == null) {
			return;
		}
		if (mContactsOptionIv == null) {
			mContactsOptionIv = (ImageView) optionView;
		}
		mOptionContactsId = contacts.getId();
		if (mPopupWindow == null) {
			mPopupWindow = new PopupWindow(this);
			mPopupAdapter = new IconTextAdapter(this, 40);
			ListView lv = new ListView(getApplicationContext());
			lv.setAdapter(mPopupAdapter);
			lv.setBackgroundColor(Color.argb(0xFF, 0x2D, 0x30, 0x37));
			mPopupWindow.setContentView(lv);
			mPopupWindow.setWidth(EpsApplication.getScreenWidth() / 2);
			mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(getResources()
					.getColor(R.color.transparent)));
			mPopupWindow.setFocusable(true);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setAnimationStyle(R.style.popup_window_menu);
			lv.setOnItemClickListener(this);
			lv.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_MENU
							&& event.getAction() == KeyEvent.ACTION_UP
							&& mPopupWindow.isShowing()) {
						mPopupWindow.dismiss();
						return true;
					}
					return false;
				}
			});
		}
		List<IconTextItem> list = new ArrayList<IconTextItem>();
		Contacts c = ContactsDao.getInstance().queryById(mOptionContactsId);
		if (c == null) {
			list.add(new IconTextItem(0, ContactsUtils.STR_ADD_CONTACTS, null));
			list.add(new IconTextItem(0, ContactsUtils.STR_ADD_BLACK, null));
		} else if (c.getStatus() == Contacts.STATUS_NORNAL) {
			list.add(new IconTextItem(0, ContactsUtils.STR_RM_CONTACTS, null));
			list.add(new IconTextItem(0, ContactsUtils.STR_ADD_BLACK, null));
		} else if (c.getStatus() == Contacts.STATUS_BLACKLIST) {
			list.add(new IconTextItem(0, ContactsUtils.STR_ADD_CONTACTS, null));
			list.add(new IconTextItem(0, ContactsUtils.STR_RM_BLACK, null));
		}
		list.add(new IconTextItem(0, "投诉", null));
		mPopupAdapter.replaceAll(list);
		int[] loc = new int[2];
		optionView.getLocationInWindow(loc);
		int x = DimensionUtls.getPixelFromDpInt(60);
		mPopupWindow.showAtLocation(optionView, Gravity.TOP | Gravity.RIGHT, x,
				loc[1] + DimensionUtls.getPixelFromDpInt(35));

	}

}
