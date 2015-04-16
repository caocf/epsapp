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

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.R;
import com.epeisong.base.activity.TabPages2Activity;
import com.epeisong.base.adapter.impl.IconTextAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.base.fragment.EmptyFragment;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.Contacts;
import com.epeisong.ui.fragment.ChatRoomExpandFragment;
import com.epeisong.ui.fragment.ChatRoomExpandFragment.ChatRoomListAndItemChangable;
import com.epeisong.ui.fragment.ChatRoomExpandFragment.OnContactsOptionListener;
import com.epeisong.ui.fragment.ChatRoomFragment;
import com.epeisong.ui.fragment.ComplainFragment;
import com.epeisong.ui.fragment.GuaCompDetailFragment;
import com.epeisong.ui.fragment.InfoFeeAdvisoryListFragment;
import com.epeisong.ui.fragment.InfoFeeFragment;
import com.epeisong.utils.ContactsUtils;
import com.epeisong.utils.ContactsUtils.OnContactsUtilsListener;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ToastUtils;

/**
 * 担保申诉详情
 * @author Jack
 *
 */
public class GuaCompDetailActivity extends TabPages2Activity implements ChatRoomListAndItemChangable,
        OnContactsOptionListener, OnItemClickListener, OnContactsUtilsListener {

    public static final String EXTRA_BUSINESS_CHAT_MODEL = "business_chat_model";
    public static final String EXTRA_GUA_COMPLAIN_TASK = "gua_complain_task";
    public static final String EXTRA_INFO_FEE_ID = "info_fee_id";
    
    public static final String EXTRA_SHOW_CHAT_FIRST = "show_chat_first";
    public static final String EXTRA_REMOTE_ID = "remote_id";

    public static final String EXTRA_FINISH_TO_MAIN = "finish_to_main";

    private List<Fragment> mFragments;
    private EmptyFragment mEmptyFragment;
    private InfoFeeAdvisoryListFragment mInfoFeeAdvisoryListFragment;
    private ChatRoomExpandFragment mChatRoomExpandFragment;

    private BusinessChatModel mBusinessChatModel;
    private boolean mShowChatFirst;
    private String mRemoteId;

    private String mOptionContactsId;
    private ImageView mContactsOptionIv;
    private PopupWindow mPopupWindow;
    private IconTextAdapter mPopupAdapter;

    boolean mFinishToMain;
    private int mComplaintState = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBusinessChatModel = (BusinessChatModel) getIntent().getSerializableExtra(EXTRA_BUSINESS_CHAT_MODEL);
        mShowChatFirst = getIntent().getBooleanExtra(EXTRA_SHOW_CHAT_FIRST, false);
        mRemoteId = getIntent().getStringExtra(EXTRA_REMOTE_ID);
        mComplaintState = getIntent().getIntExtra(ComplainFragment.ARGS_COMPLAINT_TYPE, -1);
        mFinishToMain = getIntent().getBooleanExtra(EXTRA_FINISH_TO_MAIN, false);
        if (mBusinessChatModel == null || (mShowChatFirst && TextUtils.isEmpty(mRemoteId))) {
            ToastUtils.showToast("参数错误");
            finish();
            return;
        }

        mFragments = new ArrayList<Fragment>();
        GuaCompDetailFragment guaCompDetailFragment = new GuaCompDetailFragment();
        Bundle args = new Bundle();
        args.putString(InfoFeeFragment.EXTRA_INFO_FEE_ID, mBusinessChatModel.getBusiness_id());
        args.putSerializable(EXTRA_GUA_COMPLAIN_TASK, getIntent().getSerializableExtra(EXTRA_GUA_COMPLAIN_TASK));
        args.putInt(ComplainFragment.ARGS_COMPLAINT_TYPE, mComplaintState);
        guaCompDetailFragment.setArguments(args);
        mFragments.add(guaCompDetailFragment);
        mEmptyFragment = new EmptyFragment();
        mFragments.add(mEmptyFragment);

        if (mShowChatFirst) {
            mChatRoomExpandFragment = new ChatRoomExpandFragment();
            Bundle argsChat = new Bundle();
            argsChat.putSerializable(ChatRoomExpandFragment.ARGS_BUSINESS_CHAT_MODEL, mBusinessChatModel);
            argsChat.putString(ChatRoomExpandFragment.ARGS_REMOTE_ID, mRemoteId);
            argsChat.putBoolean(ChatRoomExpandFragment.ARGS_CAN_BACK_TO_LIST, true);
            argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_ME, 1);
            argsChat.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_OTHER, 1);
            mChatRoomExpandFragment.setArguments(argsChat);
            mEmptyFragment.replace(mChatRoomExpandFragment);
            mViewPager.setCurrentItem(1, false);
        } else {
            mInfoFeeAdvisoryListFragment = new InfoFeeAdvisoryListFragment();
            Bundle argsList = new Bundle();
            argsList.putString(InfoFeeAdvisoryListFragment.ARGS_INFO_FEE_ID, mBusinessChatModel.getBusiness_id());
            mInfoFeeAdvisoryListFragment.setArguments(argsList);
            mEmptyFragment.replace(mInfoFeeAdvisoryListFragment);
        }

        mTabRoot.setBackgroundColor(Color.argb(0xff, 0xdf, 0xdf, 0xdf));
        int top = DimensionUtls.getPixelFromDpInt(10);
        int bottom = DimensionUtls.getPixelFromDpInt(15);
        mTabRoot.setPadding(bottom, top, bottom, bottom);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "任务详情", null).setShowLogo(false);
    }

    @Override
    public void finish() {
        super.finish();
        if (mFinishToMain) {
       	 MainActivity.setTabPos(1,true);
         MainActivity.launchAndClear(this, 1);
       }
    }
    

    @Override
    protected void onSetTabTitle(List<String> titles) {
        titles.add("详情");
        titles.add("会话");
    }

    @Override
    protected TabStyle getTabStyle() {
        return new TabStyle() {
            @Override
            public int getTabBg() {
                return R.drawable.shape_content_trans_frame_green;
            }

            public int[] getTabItemBgs() {
                return new int[] { R.drawable.shape_tab2_bg_green_left, R.drawable.shape_tab2_bg_green_right };
            }

            public int getTextColorSelectorId() {
                return R.color.selector_tab_text_5;
            }

            @Override
            public int getDividerColor() {
                return Color.TRANSPARENT;
            }
        };
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

    public void changeToChatRoomList(String business_id) {
        if (TextUtils.isEmpty(business_id)) {
            ToastUtils.showToast("business_id is emtpy!");
            return;
        }
        if (mInfoFeeAdvisoryListFragment == null) {
            mInfoFeeAdvisoryListFragment = new InfoFeeAdvisoryListFragment();
        }
        Bundle argsList = new Bundle();
        argsList.putString(InfoFeeAdvisoryListFragment.ARGS_INFO_FEE_ID, business_id);
        mInfoFeeAdvisoryListFragment.setArguments(argsList);
        mEmptyFragment.replace(mInfoFeeAdvisoryListFragment);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPopupWindow.dismiss();
        IconTextItem item = mPopupAdapter.getItem(position);
        if (item.getName().equals("投诉")) {
            ToastUtils.showToast("投诉");
        } else {
            ContactsUtils.onContactsOption(item.getName(), mOptionContactsId, this);
            showPendingDialog(null);
        }
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
