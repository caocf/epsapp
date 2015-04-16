package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
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
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq.Builder;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.Contacts;
import com.epeisong.model.User;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.WithdrawTask;
import com.epeisong.net.ws.utils.WithdrawTaskResp;
import com.epeisong.ui.fragment.ChatRoomExpandFragment;
import com.epeisong.ui.fragment.ChatRoomExpandFragment.OnContactsOptionListener;
import com.epeisong.ui.fragment.ChatRoomFragment;
import com.epeisong.ui.fragment.TransferWithdrawalDetailFragment;
import com.epeisong.utils.ContactsUtils;
import com.epeisong.utils.ContactsUtils.OnContactsUtilsListener;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;

public class TransferWithdrawalDetailActivity extends TabPages2Activity implements
OnContactsOptionListener, OnItemClickListener, OnContactsUtilsListener {
	public static final String EXTRA_BUSINESS_CHAT_MODEL = "business_chat_model";
	public static final String EXTRA_WITHDRAW = "trans_withdraw";
	public static final String EXTRA_REMOTE_ID = "remote_id";
	public static final String EXTRA_SHOW_CHAT_FIRST = "show_chat_first";
	public static final String EXTRA_FLAG = "ower_or_other";
	private List<Fragment> mFragments;
	private EmptyFragment mEmptyFragment;
	private ChatRoomExpandFragment mChatRoomExpandFragment;
	private WithdrawTask withdraw;
	private BusinessChatModel mBusinessChatModel;
	private boolean mShowChatFirst;
	private String mRemoteId = "";
	private PopupWindow mPopupWindow;
    private IconTextAdapter mPopupAdapter;
    private ImageView mContactsOptionIv;
    private User mUser;
    private Contacts mContacts;
    private String mUserId;
//    private WithdrawTask mRemote;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBusinessChatModel = (BusinessChatModel) getIntent().getSerializableExtra(EXTRA_BUSINESS_CHAT_MODEL);
		withdraw = (WithdrawTask) getIntent().getSerializableExtra(EXTRA_WITHDRAW);
		mShowChatFirst = getIntent().getBooleanExtra(EXTRA_SHOW_CHAT_FIRST, false);
		User user = UserDao.getInstance().getUser();
		mRemoteId = getIntent().getStringExtra(EXTRA_REMOTE_ID);
		if (mRemoteId == null && withdraw != null) {
            mRemoteId = String.valueOf(withdraw.getPayerLogisticsId());
        }
		
		if(withdraw == null){
			if(requestData(mBusinessChatModel.getBusiness_id()) != null){
				withdraw = requestData(mBusinessChatModel.getBusiness_id()).getWithdrawTask();
			}
		}
        
		mFragments = new ArrayList<Fragment>();
		TransferWithdrawalDetailFragment dFragment = new TransferWithdrawalDetailFragment();
		Bundle args = new Bundle();
		args.putSerializable(TransferWithdrawalDetailActivity.EXTRA_WITHDRAW, withdraw);
		if(String.valueOf(withdraw.getPayerLogisticsId()).equals(user.getId())){
			args.putString(TransferWithdrawalDetailActivity.EXTRA_FLAG, "flag");
		}
		dFragment.setArguments(args);
		mFragments.add(dFragment);
		
		mEmptyFragment = new EmptyFragment();
		if(mShowChatFirst){
			if(!mRemoteId.equals(user.getId())){
		        mChatRoomExpandFragment = new ChatRoomExpandFragment();
		        Bundle argsChat = new Bundle();
		        argsChat.putSerializable(ChatRoomExpandFragment.ARGS_BUSINESS_CHAT_MODEL, mBusinessChatModel);
		        argsChat.putString(ChatRoomExpandFragment.ARGS_REMOTE_ID, mRemoteId);
		//        argsChat.putBoolean(ChatRoomExpandFragment.ARGS_CAN_BACK_TO_LIST, true); //是否以列表形式显示
		        argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_ME, 1);
		        argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_OTHER, 1);
		        mChatRoomExpandFragment.setArguments(argsChat);
		        mEmptyFragment.replace(mChatRoomExpandFragment);
		        mViewPager.setCurrentItem(1, false);
			}else{
				Bundle argsEmpty = new Bundle();
				argsEmpty.putString("flag", "flag");
				mEmptyFragment.setArguments(argsEmpty);
			}
		}else{
			if(!mRemoteId.equals(user.getId())){
		        mChatRoomExpandFragment = new ChatRoomExpandFragment();
		        Bundle argsChat = new Bundle();
		        argsChat.putSerializable(ChatRoomExpandFragment.ARGS_BUSINESS_CHAT_MODEL, mBusinessChatModel);
		        argsChat.putString(ChatRoomExpandFragment.ARGS_REMOTE_ID, mRemoteId);
		//        argsChat.putBoolean(ChatRoomExpandFragment.ARGS_CAN_BACK_TO_LIST, true); //是否以列表形式显示
		        argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_ME, 1);
		        argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_OTHER, 1);
		        mChatRoomExpandFragment.setArguments(argsChat);
		        mEmptyFragment.replace(mChatRoomExpandFragment);
			}else{
				Bundle argsEmpty = new Bundle();
				argsEmpty.putString("flag", "flag");
				mEmptyFragment.setArguments(argsEmpty);
			}
		}
		mFragments.add(mEmptyFragment);
	}
	
	private WithdrawTaskResp requestData(final String taskId) {
		ApiExecutor api = new ApiExecutor();
		try {
			User user = UserDao.getInstance().getUser();
			String pwd = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED,null);
			
			return api.getWithdrawTask(user.getAccount_name(), pwd, taskId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	@Override
	protected void onSetTabTitle(List<String> titles) {
		titles.add("详情");
		titles.add("会话");
	}

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
		if(withdraw != null){
			switch (withdraw.getStatus()) {
			case Properties.WITHDRAW_TASK_STATUS_NOT_PROCESSED:
				if(withdraw.getSubStatus() != null){
					return new TitleParams(getDefaultHomeAction(), "未处理-报警状态", null).setShowLogo(false);
				}else{
					return new TitleParams(getDefaultHomeAction(), "未处理-正常状态", null).setShowLogo(false);
				}
			case Properties.WITHDRAW_TASK_STATUS_PROCESSED:
				return new TitleParams(getDefaultHomeAction(), "已处理状态", null).setShowLogo(false);
			case Properties.WITHDRAW_TASK_STATUS_CANCEL:
				return new TitleParams(getDefaultHomeAction(), "已取消状态", null).setShowLogo(false);
			default:
				break;
			}
		}
		return new TitleParams(getDefaultHomeAction(), "详情", null).setShowLogo(false);
	}
	
	@Override
    public void onContactsUtilsComplete(int option, boolean success) {
        dismissPendingDialog();
        if (success) {
            switch (option) {
            case OnContactsUtilsListener.option_add:
                mChatRoomExpandFragment.onContactsStatusChange(R.drawable.chatroom_contacts_option_is_contacts);
                ToastUtils.showToast("添加成功");
                break;
            case OnContactsUtilsListener.option_delete:
                mChatRoomExpandFragment.onContactsStatusChange(R.drawable.chatroom_contacts_option_not_contacts);
                ToastUtils.showToast("删除成功");
                break;
            case OnContactsUtilsListener.option_black:
                mChatRoomExpandFragment.onContactsStatusChange(R.drawable.chatroom_contacts_option_is_contacts);
                ToastUtils.showToast("已加入黑名单");
                break;
            }
        }
    }
	
	private User getUserInfo() {
        NetLogisticsInfo netInfo = new NetLogisticsInfo() {
            @Override
            protected boolean onSetRequest(Builder req) {
                req.setLogisticsId(Integer.parseInt(mUserId));
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPopupWindow.dismiss();
        IconTextItem item = mPopupAdapter.getItem(position);
        if (item.getName().equals("投诉")) {
            // ToastUtils.showToast("投诉");
            // mUser = UserDao.getInstance().queryById(mUserId);
            mUser = getUserInfo();

            if (mUser != null) {
                Intent complaint = new Intent(getApplication(), CustomerComplaintActivity.class);
                complaint.putExtra("user", mUser);
                startActivity(complaint);
            } else {
                Toast.makeText(getApplicationContext(), "投诉", Toast.LENGTH_SHORT).show();// 投诉
            }
        } else {
            ContactsUtils.onContactsOption(item.getName(), mUserId, this);
            showPendingDialog(null);
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
        mUserId = contacts.getId();
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(this);
            mPopupAdapter = new IconTextAdapter(this, 40);
            ListView lv = new ListView(getApplicationContext());
            lv.setAdapter(mPopupAdapter);
            lv.setBackgroundColor(Color.argb(0xFF, 0x2D, 0x30, 0x37));
            mPopupWindow.setContentView(lv);
            mPopupWindow.setWidth(EpsApplication.getScreenWidth() / 2);
            mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setAnimationStyle(R.style.popup_window_menu);
            lv.setOnItemClickListener(this);
            lv.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP
                            && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
        List<IconTextItem> list = new ArrayList<IconTextItem>();
        Contacts c = ContactsDao.getInstance().queryById(mUserId);
        if (mContacts == null) {
            mContacts = contacts;
        }
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
        int x = DimensionUtls.getPixelFromDpInt(30);
        mPopupWindow.showAtLocation(optionView, Gravity.TOP | Gravity.RIGHT, x,
                loc[1] + DimensionUtls.getPixelFromDpInt(35));
    }

}
