package com.epeisong.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.Contacts;
import com.epeisong.model.User;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.ui.fragment.ChatRoomFragment.OnChatRoomInfoListener;

/**
 * 聊天界面的扩展，所需参数和ChatRoomFragment相同
 * @author poet
 *
 */
public class ChatRoomExpandFragment extends Fragment implements OnClickListener, OnChatRoomInfoListener {

    public static final String ARGS_REMOTE_ID = "remote_id";
    public static final String ARGS_BUSINESS_CHAT_MODEL = "business_chat_model";
    public static final String ARGS_CAN_BACK_TO_LIST = "can_back_to_list";

    private TextView mRemoteNameTv;
    private ImageView mContactsOptionIv;
    private ChatRoomFragment mChatRoomFragment;

    private String mRemoteId;
    private User mRemote;
    private BusinessChatModel mBusinessChatModel;
    private boolean mCanBackToList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mRemoteId = args.getString(ARGS_REMOTE_ID);
            mBusinessChatModel = (BusinessChatModel) args.getSerializable(ARGS_BUSINESS_CHAT_MODEL);
            mCanBackToList = args.getBoolean(ARGS_CAN_BACK_TO_LIST);
        }
        if (mRemoteId == null || mBusinessChatModel == null) {
            TextView tv = new TextView(getActivity());
            tv.setText("参数错误");
            tv.setGravity(Gravity.CENTER);
            return tv;
        }
        View root = inflater.inflate(R.layout.fragment_chat_room_expand, null);
        mRemoteNameTv = (TextView) root.findViewById(R.id.tv_remote_name);
        mRemoteNameTv.setOnClickListener(this);
        mContactsOptionIv = (ImageView) root.findViewById(R.id.iv_contacts_option);
        mContactsOptionIv.setOnClickListener(this);
        if (ContactsDao.getInstance().queryById(mRemoteId) != null) {
            mContactsOptionIv.setImageResource(R.drawable.chatroom_contacts_option_is_contacts);
        } else {
            mContactsOptionIv.setImageResource(R.drawable.chatroom_contacts_option_not_contacts);
        }
        if (mCanBackToList) {
            View backToListView = root.findViewById(R.id.iv_back_to_list);
            backToListView.setVisibility(View.VISIBLE);
            backToListView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof ChatRoomListAndItemChangable) {
                        ((ChatRoomListAndItemChangable) getActivity()).changeToChatRoomList(mBusinessChatModel
                                .getBusiness_id());
                    }
                }
            });
        }
        mChatRoomFragment = new ChatRoomFragment();
        mChatRoomFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_chatroom_container, mChatRoomFragment).commit();
        mChatRoomFragment.setOnChatRoomInfoListener(this);
        return root;
    }

    @Override
    public void onChatRoomInfo(User remote) {
        mRemoteNameTv.setText(remote.getShow_name());
        mRemote = remote;
    }

    public void onContactsStatusChange(int resId) {
        mContactsOptionIv.setImageResource(resId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_remote_name:
            Intent intent = new Intent(getActivity(), ContactsDetailActivity.class);
            intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, mRemoteId);
            intent.putExtra(ContactsDetailActivity.EXTRA_SHOW_PAGE_COUNT_OTHER, 1);
            intent.putExtra(ContactsDetailActivity.EXTRA_USER, mRemote);
            startActivity(intent);
            break;
        case R.id.iv_contacts_option:
            if (mRemote == null) {
                return;
            }
            if (getActivity() instanceof OnContactsOptionListener) {
                ((OnContactsOptionListener) getActivity()).onContactsOption(Contacts.convertFromUser(mRemote), v);
            }
            break;
        }
    }

    public boolean hideMoreView() {
        return mChatRoomFragment.hideMoreView();
    }

    public ChatRoomFragment getChatRoomFragment() {
        return mChatRoomFragment;
    }

    public interface OnContactsOptionListener {
        void onContactsOption(Contacts c, View optionView);
    }
    
    public interface ChatRoomListAndItemChangable {
        void changeToChatRoomItem(BusinessChatModel model, String remote_id);

        void changeToChatRoomList(String business_id);
    }
}
