package com.epeisong.ui.fragment;

import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.fragment.PullToRefreshListViewFragment;
import com.epeisong.data.dao.ChatRoomDao;
import com.epeisong.data.dao.ChatRoomDao.ChatRoomObserver;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.dao.helper.InfoFeeDbHelper;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.data.utils.ChatUtils;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.ChatRoom;
import com.epeisong.model.InfoFee;
import com.epeisong.ui.fragment.ChatRoomExpandFragment.ChatRoomListAndItemChangable;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

public class InfoFeeAdvisoryListFragment extends PullToRefreshListViewFragment implements ChatRoomObserver {

    public static final String ARGS_INFO_FEE_ID = "info_fee_id";

    private MyAdapter mAdapter;

    private String mInfoFeeId;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPullToRefreshListView.setMode(Mode.DISABLED);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mInfoFeeId = bundle.getString(ARGS_INFO_FEE_ID);
        }
        if (TextUtils.isEmpty(mInfoFeeId)) {
            ToastUtils.showToast("info_fee_id is empty!");
            return;
        } else {
            AsyncTask<Void, Void, List<ChatRoom>> task = new AsyncTask<Void, Void, List<ChatRoom>>() {
                @Override
                protected List<ChatRoom> doInBackground(Void... params) {
                    InfoFee infoFee = InfoFeeDbHelper.getInstance().queryById(InfoFee.class, mInfoFeeId);
                    if (infoFee == null) {
                        return null;
                    }
                    String mineId = UserDao.getInstance().getUser().getId();
                    String remoteId = "";
                    String remoteName = "";
                    String remoteId2 = "";
                    String remoteName2 = "";
                    if (mineId.equals(String.valueOf(infoFee.getPayerId()))) {
                        remoteId = String.valueOf(infoFee.getPayeeId());
                        remoteName = infoFee.getPayeeName();
                    } else if (mineId.equals(String.valueOf(infoFee.getPayeeId()))) {
                        remoteId = String.valueOf(infoFee.getPayerId());
                        remoteName = infoFee.getPayerName();
                    } else {
                        // ToastUtils.showToastInThread("数据异常");
                        // return null;
                        remoteId = String.valueOf(infoFee.getPayeeId());
                        remoteName = infoFee.getPayeeName();
                        remoteId2 = String.valueOf(infoFee.getPayerId());
                        remoteName2 = infoFee.getPayerName();
                    }
                    List<ChatRoom> list = ChatRoomDao.getInstance().queryInfoFeeAdvisoryList(mInfoFeeId);
                    boolean remoteHasChat = false;
                    for (ChatRoom room : list) {
                        if (room.getRemote_id().equals(remoteId)) {
                            remoteHasChat = true;
                            break;
                        }
                    }
                    if (!remoteHasChat) {
                        ChatRoom room = new ChatRoom();
                        room.setBusiness_type(ChatMsg.business_type_info_fee);
                        room.setBusiness_desc(infoFee.getFreightAddr());
                        room.setBusiness_id(String.valueOf(infoFee.getId()));
                        room.setId(ChatUtils.getChatMsgTableNameForInfoFee(mineId, remoteId,
                                String.valueOf(infoFee.getId())));
                        room.setRemote_id(remoteId);
                        room.setRemote_name(remoteName);
                        list.add(room);

                        if (!TextUtils.isEmpty(remoteId2)) {
                            ChatRoom room2 = new ChatRoom();
                            room2.setBusiness_type(ChatMsg.business_type_info_fee);
                            room2.setBusiness_desc(infoFee.getFreightAddr());
                            room2.setBusiness_id(String.valueOf(infoFee.getId()));
                            room2.setId(ChatUtils.getChatMsgTableNameForInfoFee(mineId, remoteId2,
                                    String.valueOf(infoFee.getId())));
                            room2.setRemote_id(remoteId2);
                            room2.setRemote_name(remoteName2);
                            list.add(room2);
                        }
                    }
                    return list;
                }

                @Override
                protected void onPostExecute(List<ChatRoom> result) {
                    onRequestComplete(true);
                    mAdapter.replaceAll(result);
                }
            };
            task.execute();
        }

        ChatRoomDao.getInstance().addObserver(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ChatRoomDao.getInstance().removeObserver(this);
    }

    @Override
    public void onChatRoomChange(ChatRoom room, CRUD curd) {
        if (!ChatUtils.isInfoFeeAdvisoryChatRoom(room.getId(), mInfoFeeId)) {
            return;
        }
        switch (curd) {
        case CREATE:
            mAdapter.addItem(0, room);
            break;
        case DELETE:
            mAdapter.removeItem(room);
            break;
        case UPDATE:
            mAdapter.removeItem(room);
            mAdapter.addItem(0, room);
            break;
        default:
            break;
        }
    }

    @Override
    protected ListAdapter onCreateAdapter() {
        return mAdapter = new MyAdapter();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
    }

    @Override
    public void onStartLoadMore(EndlessAdapter adapter) {

    }

    @Override
    protected void onFailViewClick() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        position -= mListView.getHeaderViewsCount();
        ChatRoom room = mAdapter.getItem(position);
        BusinessChatModel model = new BusinessChatModel();
        model.setBusiness_type(ChatMsg.business_type_info_fee);
        model.setBusiness_id(room.getBusiness_id());
        model.setBusiness_owner_id(room.getBusiness_owner_id());
        model.setBusiness_desc(room.getBusiness_desc());
        model.setBusiness_extra(room.getBusiness_extra());
        if (getActivity() instanceof ChatRoomListAndItemChangable) {
            ((ChatRoomListAndItemChangable) getActivity()).changeToChatRoomItem(model, room.getRemote_id());
        }
    }

    private class MyAdapter extends HoldDataBaseAdapter<ChatRoom> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.fragment_freight_advisory_list_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position));
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView iv_logo;
        TextView tv_name;
        TextView tv_last_msg;
        TextView tv_time;

        public void findView(View v) {
            iv_logo = (ImageView) v.findViewById(R.id.iv_logo);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_last_msg = (TextView) v.findViewById(R.id.tv_last_msg);
            tv_time = (TextView) v.findViewById(R.id.tv_time);
        }

        public void fillData(ChatRoom room) {
            // TODO logo
            tv_name.setText(room.getRemote_name());
            tv_last_msg.setText(room.getLast_msg());
            tv_time.setText(DateUtil.long2vague(room.getUpdate_time(), true));
        }
    }
}
