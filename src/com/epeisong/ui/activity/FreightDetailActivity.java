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
import com.epeisong.MainActivity;
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
import com.epeisong.model.Contacts;
import com.epeisong.model.Freight;
import com.epeisong.model.User;
import com.epeisong.ui.fragment.ChatRoomExpandFragment;
import com.epeisong.ui.fragment.ChatRoomExpandFragment.ChatRoomListAndItemChangable;
import com.epeisong.ui.fragment.ChatRoomExpandFragment.OnContactsOptionListener;
import com.epeisong.ui.fragment.ChatRoomFragment;
import com.epeisong.ui.fragment.FreightAdvisoryListFragment;
import com.epeisong.ui.fragment.MyCarSourceFragment;
import com.epeisong.ui.fragment.MySupplyDetailsFragment;
import com.epeisong.ui.fragment.OtherCarSourceFragment;
import com.epeisong.ui.fragment.OtherSupplyDetailsFragment;
import com.epeisong.utils.ContactsUtils;
import com.epeisong.utils.ContactsUtils.OnContactsUtilsListener;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.ToastUtils;


/**
 * 车源货源详情
 * 
 * @author poet
 * 
 */
public class FreightDetailActivity extends TabPages2Activity implements ChatRoomListAndItemChangable,
        OnContactsOptionListener, OnItemClickListener, OnContactsUtilsListener {

    public static final String EXTRA_FREIGHT = "freight";
    public static final String EXTRA_BUSINESS_CHAT_MODEL = "business_chat_model";
    public static final String EXTRA_SHOW_ADVISORY_FIRST = "show_advisory_first";
    public static final String EXTRA_CAN_DELETE = "can_delete";
    public static final String EXTRA_ADVISORYER_ID = "advisoryer_id";

    public static final String EXTRA_FINISH_TO_MAIN = "finish_to_main";

    private List<Fragment> mFragments;
    private ChatRoomExpandFragment mChatRoomExpandFragment;
    private FreightAdvisoryListFragment mAdvisoryListFragment;
    private EmptyFragment mEmptyFragment;

    private Freight mFreight;
    private BusinessChatModel mBusinessChatModel;
    private boolean mShowAdvisoryFirst;
    private boolean mCanDelete;
    private String mAdvisoryerId;
    private String flag; // 判断是否是从朋友的车源货源进入的

    private String mUserId;
    private User mUser;
    private Contacts mContacts;
    private String mUserIdforUser;
    private PopupWindow mPopupWindow;
    private IconTextAdapter mPopupAdapter;
    private ImageView mContactsOptionIv;
    private boolean mShowBackToListView;

    boolean mFinishToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        flag = getIntent().getStringExtra("flag");
        mFreight = (Freight) getIntent().getSerializableExtra(EXTRA_FREIGHT);
        mBusinessChatModel = (BusinessChatModel) getIntent().getSerializableExtra(EXTRA_BUSINESS_CHAT_MODEL);
        mShowAdvisoryFirst = getIntent().getBooleanExtra(EXTRA_SHOW_ADVISORY_FIRST, false);
        mUserIdforUser = getIntent().getStringExtra(ContactsDetailActivity.EXTRA_USER_ID);
        mCanDelete = getIntent().getBooleanExtra(EXTRA_CAN_DELETE, false);

        mFinishToMain = getIntent().getBooleanExtra(EXTRA_FINISH_TO_MAIN, false);
        super.onCreate(savedInstanceState);
        if (mBusinessChatModel == null || TextUtils.isEmpty(mBusinessChatModel.getBusiness_extra())) {
            ToastUtils.showToast("BusinessChatModel or extra is empty!");
            finish();
            return;
        }
        try {
            initUi();
        } catch (NumberFormatException e) {
            ToastUtils.showToast("freight type not int value!");
            finish();
        }
    }

    private void initUi() {
        mFragments = new ArrayList<Fragment>();
        mEmptyFragment = new EmptyFragment();
        Fragment freightFragment = null;
        String mine_id = UserDao.getInstance().getUser().getId();
        String owner_id = mBusinessChatModel.getBusiness_owner_id();
        int freightType = mBusinessChatModel.getBusiness_extra_int();
        // 判断车源货源详情页
        if (mine_id.equals(owner_id)) {
            if (freightType == Freight.TYPE_GOODS) {
                freightFragment = new MySupplyDetailsFragment();
            } else {
                freightFragment = new MyCarSourceFragment();
            }
        } else {
            if (freightType == Freight.TYPE_GOODS) {
                freightFragment = new OtherSupplyDetailsFragment();
            } else {
                freightFragment = new OtherCarSourceFragment();
            }
        }
        Bundle args = new Bundle();
        args.putSerializable(MySupplyDetailsFragment.EXTRA_FREIGHT, mFreight);
        args.putString(MySupplyDetailsFragment.EXTRA_FREIGHT_ID, mBusinessChatModel.getBusiness_id());
        args.putString(MySupplyDetailsFragment.EXTRA_USER_ID, owner_id);
        args.putString("flag", flag);
        if (!TextUtils.isEmpty(mUserIdforUser))// &&
                                               // !mUserIdforUser.equals(mine_id))//联系人隐藏删除
                                               // button 14.11.26 Zhu
            args.putBoolean(MySupplyDetailsFragment.EXTRA_IS_HIDE_DELETE_BTN, true);
        else
            args.putBoolean(MySupplyDetailsFragment.EXTRA_IS_HIDE_DELETE_BTN, !mCanDelete);
        freightFragment.setArguments(args);
        mFragments.add(freightFragment);

        // 判断该车源货源是不是我自己的
        if (owner_id.equals(mine_id)) {
            mShowBackToListView = true;
            // 是自己的，根据是否要先显示聊天，加载对应fragment，如果先显示聊天，需要指定当前的咨询者id
            if (mShowAdvisoryFirst) {
                mAdvisoryerId = getIntent().getStringExtra(EXTRA_ADVISORYER_ID);
                if (TextUtils.isEmpty(mAdvisoryerId)) {
                    ToastUtils.showToast("当前咨询者的id为空！");
                    finish();
                    return;
                }
                mChatRoomExpandFragment = new ChatRoomExpandFragment();
                Bundle argsChat = new Bundle();
                argsChat.putSerializable(ChatRoomExpandFragment.ARGS_BUSINESS_CHAT_MODEL, mBusinessChatModel);
                argsChat.putString(ChatRoomExpandFragment.ARGS_REMOTE_ID, mAdvisoryerId);
                argsChat.putBoolean(ChatRoomExpandFragment.ARGS_CAN_BACK_TO_LIST, true);
                argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_ME, 1);
                argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_OTHER, 1);
                mChatRoomExpandFragment.setArguments(argsChat);
                mEmptyFragment.replace(mChatRoomExpandFragment);
            } else {
                mAdvisoryListFragment = new FreightAdvisoryListFragment();
                Bundle argsList = new Bundle();
                argsList.putString(FreightAdvisoryListFragment.ARGS_FREIGHT_ID, mBusinessChatModel.getBusiness_id());
                mAdvisoryListFragment.setArguments(argsList);
                mEmptyFragment.replace(mAdvisoryListFragment);
            }
        } else {
            mChatRoomExpandFragment = new ChatRoomExpandFragment();
            Bundle argsChat = new Bundle();
            argsChat.putSerializable(ChatRoomFragment.ARGS_BUSINESS_CHAT_MODEL, mBusinessChatModel);
            argsChat.putString(ChatRoomFragment.ARGS_REMOTE_ID, mBusinessChatModel.getBusiness_owner_id());
            argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_ME, 1);
            argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_OTHER, 1);
            mChatRoomExpandFragment.setArguments(argsChat);
            mEmptyFragment.replace(mChatRoomExpandFragment);
        }
        mFragments.add(mEmptyFragment);

        if (mShowAdvisoryFirst) {
            HandlerUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mViewPager.setCurrentItem(1, false);
                }
            }, 100);
        }
    }

 

    @Override
    protected TitleParams getTitleParams() {
        String title = "";
        if (mBusinessChatModel != null) {
            if (mBusinessChatModel.getBusiness_extra_int() == Freight.TYPE_GOODS) {
                title = "货源";
            } else {
                title = "车源";
            }
        }
        return new TitleParams(getDefaultHomeAction(), title, null).setShowLogo(false);

    }

    @Override
    protected void onSetTabTitle(List<String> titles) {
        titles.add("详情");
        titles.add("咨询");
    }

    @Override
    protected PagerAdapter onGetAdapter() {
        return new FragmentPagerAdapter(getSupportFragmentManager()) {
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
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 1) {
            if (mChatRoomExpandFragment != null && mChatRoomExpandFragment.hideMoreView()) {
                return;
            }

        }

        Intent result = new Intent();
        result.putExtra("String", true);
        setResult(RESULT_OK, result);
        finish();

        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mChatRoomExpandFragment != null && mChatRoomExpandFragment.getChatRoomFragment() != null
                && mChatRoomExpandFragment.getChatRoomFragment().handleTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
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
        args.putSerializable(ChatRoomExpandFragment.ARGS_BUSINESS_CHAT_MODEL, model);
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
            mAdvisoryListFragment = new FreightAdvisoryListFragment();
        }
        Bundle argsList = new Bundle();
        argsList.putString(FreightAdvisoryListFragment.ARGS_FREIGHT_ID, business_id);
        mAdvisoryListFragment.setArguments(argsList);
        mEmptyFragment.replace(mAdvisoryListFragment);
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
        int x = 0;
        if (mShowBackToListView) {
            x = DimensionUtls.getPixelFromDpInt(60);
        } else {
            x = DimensionUtls.getPixelFromDpInt(30);
        }
        mPopupWindow.showAtLocation(optionView, Gravity.TOP | Gravity.RIGHT, x,
                loc[1] + DimensionUtls.getPixelFromDpInt(35));
    }
    
    @Override
    public void finish() {
        super.finish();
        if (mFinishToMain) {
       	 MainActivity.setTabPos(1,true);
         MainActivity.launchAndClear(this, 1);
       }
    }

 
}
