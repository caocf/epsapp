package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.impl.IconTextAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.utils.ChatUtils;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.Contacts;
import com.epeisong.model.User;
import com.epeisong.ui.fragment.ChatRoomFragment;
import com.epeisong.ui.fragment.ChatRoomFragment.OnChatRoomInfoListener;
import com.epeisong.utils.ContactsUtils;
import com.epeisong.utils.ContactsUtils.OnContactsUtilsListener;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 聊天室（一对一）
 * 
 * @author poet
 * 
 */
public class ChatRoomActivity extends BaseActivity implements OnChatRoomInfoListener, OnContactsUtilsListener {

    public static void launch(Activity a, String remote_id, int business_type, String business_id) {
        Intent i = new Intent(a, ChatRoomActivity.class);
        i.putExtra(EXTRA_REMOTE_ID, remote_id);
        i.putExtra(EXTRA_BUSINESS_TYPE, business_type);
        i.putExtra(EXTRA_BUSINESS_ID, business_id);
        a.startActivity(i);
    }

    public static final String EXTRA_REMOTE_ID = "remote_id";
    public static final String EXTRA_BUSINESS_TYPE = "business_type";
    public static final String EXTRA_BUSINESS_ID = "business_id";

    public static final String EXTRA_FINISH_TO_MAIN = "finish_to_main";

    private static final String STR_COMPLAIN = "投诉";

    private PopupWindow mPopupWindowMenu;
    private IconTextAdapter mIconTextAdapter;
    private ImageView mTitleRightIv;

    private int mBusinessType;
    private String mBusinessId;
    private User mRemote;
    private Contacts mContacts;
    private String mRemoteId;
    private String mChatRoomId;
    private ChatRoomFragment mChatRoomFragment;

    boolean mFinishToMain;

    @Override
    public void onTitleLeftClick(TextView titleLeftTv) {
        Intent intent = new Intent(this, ContactsDetailActivity.class);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, mRemoteId);
        intent.putExtra(ContactsDetailActivity.EXTRA_SHOW_PAGE_COUNT_OTHER, 2);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER, mRemote);
        startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mChatRoomFragment.handleTouchEvent(ev)) {
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
                mTitleRightIv = new ImageView(ChatRoomActivity.this);
                if (ContactsDao.getInstance().queryById(mRemoteId) == null) {
                    mTitleRightIv.setImageResource(R.drawable.chatroom_contacts_option_not_contacts);
                } else {
                    mTitleRightIv.setImageResource(R.drawable.chatroom_contacts_option_is_contacts);
                }
                return mTitleRightIv;
            }
        };
    }

    @Override
    public void onContactsUtilsComplete(int option, boolean success) {
        dismissPendingDialog();
        if (success) {
            switch (option) {
            case OnContactsUtilsListener.option_add:
                mTitleRightIv.setImageResource(R.drawable.chatroom_contacts_option_is_contacts);
                ToastUtils.showToast("添加成功");
                break;
            case OnContactsUtilsListener.option_delete:
                mTitleRightIv.setImageResource(R.drawable.chatroom_contacts_option_not_contacts);
                ToastUtils.showToast("删除成功");
                break;
            case OnContactsUtilsListener.option_black:
                mTitleRightIv.setImageResource(R.drawable.chatroom_contacts_option_is_contacts);
                ToastUtils.showToast("已加入黑名单");
                break;
            }
        }
    }

    private void initPopupWindowMenu() {
        mPopupWindowMenu = new PopupWindow(getApplicationContext());
        mIconTextAdapter = new IconTextAdapter(getApplicationContext(), 40);
        ListView lv = new ListView(getApplicationContext());
        lv.setAdapter(mIconTextAdapter);
        lv.setBackgroundColor(Color.argb(0xFF, 0x2D, 0x30, 0x37));
        mPopupWindowMenu.setContentView(lv);
        mPopupWindowMenu.setWidth(EpsApplication.getScreenWidth() / 2);
        mPopupWindowMenu.setHeight(LayoutParams.WRAP_CONTENT);
        mPopupWindowMenu.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        mPopupWindowMenu.setFocusable(true);
        mPopupWindowMenu.setOutsideTouchable(true);
        mPopupWindowMenu.setAnimationStyle(R.style.popup_window_menu);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopupWindowMenu.dismiss();
                IconTextItem item = mIconTextAdapter.getItem(position);
                if (item.getName().equals(STR_COMPLAIN)) {
                    // ToastUtils.showToast(STR_COMPLAIN);
                    if (mRemote != null) {
                        Intent complaint = new Intent(getApplication(), CustomerComplaintActivity.class);
                        complaint.putExtra("user", mRemote);
                        startActivity(complaint);
                    } else {
                        Toast.makeText(getApplicationContext(), "投诉", Toast.LENGTH_SHORT).show();// 投诉
                    }

                } else {
                    ContactsUtils.onContactsOption(item.getName(), mRemoteId, ChatRoomActivity.this);
                    showPendingDialog(null);
                }
            }
        });
        lv.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP
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
        List<IconTextItem> list = new ArrayList<IconTextItem>();
        Contacts c = ContactsDao.getInstance().queryById(mRemoteId);
        if (mContacts == null) {
            mContacts = c;
        }
        if (mContacts == null && mRemote != null) {
            mContacts = Contacts.convertFromUser(mRemote);
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
        list.add(new IconTextItem(0, STR_COMPLAIN, null));
        mIconTextAdapter.replaceAll(list);

        int statusBar = SystemUtils.getStatusBarHeight(this);
        int y = getResources().getDimensionPixelSize(R.dimen.custom_title_height) + statusBar + 1;
        mPopupWindowMenu.showAtLocation(mCustomTitle, Gravity.TOP | Gravity.RIGHT,
                (int) DimensionUtls.getPixelFromDp(10), y);
    }

    @Override
    protected TitleParams getTitleParams() {
        String title = mRemote == null ? "" : mRemote.getShow_name();
        List<Action> actions = new ArrayList<Action>();
        if (!UserDao.getInstance().getUser().getId().equals(mRemoteId)) {
            actions.add(createAction());
        }
        return new TitleParams(getDefaultHomeAction(), title, actions).setShowLogo(false).setShowLeftTitle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRemoteId = getIntent().getStringExtra(EXTRA_REMOTE_ID);

        mBusinessType = getIntent().getIntExtra(EXTRA_BUSINESS_TYPE, ChatMsg.business_type_normal);
        mBusinessId = getIntent().getStringExtra(EXTRA_BUSINESS_ID);

        mFinishToMain = getIntent().getBooleanExtra(EXTRA_FINISH_TO_MAIN, false);

        switch (mBusinessType) {
        case ChatMsg.business_type_normal:
            mChatRoomId = ChatUtils.getChatMsgTableName(UserDao.getInstance().getUser().getId(), mRemoteId);
            break;
        case ChatMsg.business_type_freight:
            mChatRoomId = ChatUtils.getChatMsgTableNameForFreight(UserDao.getInstance().getUser().getId(), mRemoteId,
                    mBusinessId);
            break;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment);
        Bundle args = new Bundle();
        args.putString(ChatRoomFragment.ARGS_REMOTE_ID, mRemoteId);
        args.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_ME, 1);
        args.putInt(ChatRoomFragment.ARGS_LOGO_SHOW_PAGE_COUNT_OTHER, 2);

        mChatRoomFragment = new ChatRoomFragment();
        mChatRoomFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.frame, mChatRoomFragment).commit();

        mChatRoomFragment.setOnChatRoomInfoListener(this);
    }

    @Override
    public void onChatRoomInfo(User remote) {
        setTitleText(remote.getShow_name());
        mRemote = remote;
    }

    @Override
    public void onBackPressed() {
        if (mChatRoomFragment.hideMoreView()) {
            return;
        }
        super.onBackPressed();
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
