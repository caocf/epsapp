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
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetComplaintById;
import com.epeisong.data.net.parser.ComplaintParser;
import com.epeisong.data.utils.ChatUtils;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.ChatRoom;
import com.epeisong.model.Complaint;
import com.epeisong.ui.fragment.ChatRoomExpandFragment.ChatRoomListAndItemChangable;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

public class ComplaintAdvisoryListFragment extends PullToRefreshListViewFragment implements ChatRoomObserver {

    public final static String ARGS_COMPLAINT_ID = "complaint_id";
    public static final String ARGS_COMPLIANT = "complaint_type";
    public static final String ARGS_BUSINESS_OWNER_ID = "business_owner_id";
    private MyAdapter mAdapter;
    private String mComplaintId;
    private Complaint complaint;
    
    String mBusinessOwnerIdForFirst;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPullToRefreshListView.setMode(Mode.DISABLED);
        Bundle bundle = getArguments();
        if (bundle != null) {
        	mComplaintId = bundle.getString(ARGS_COMPLAINT_ID);
        	complaint = getComplaintById(mComplaintId);
        	mBusinessOwnerIdForFirst = bundle.getString(ARGS_BUSINESS_OWNER_ID);
        }
        if (TextUtils.isEmpty(mComplaintId)) {
            ToastUtils.showToast("complaint_id is empty!");
            return;
        } else {
            AsyncTask<Void, Void, List<ChatRoom>> task = new AsyncTask<Void, Void, List<ChatRoom>>() {
                @Override
                protected List<ChatRoom> doInBackground(Void... params) {
                    String mineId = UserDao.getInstance().getUser().getId();
                    String byRemoteId = "";
                    String byRemoteName = "";
                    byRemoteId = String.valueOf(complaint.getByNameId());
                    byRemoteName = "被投诉方："+complaint.getByName();
                    
                    String remoteId = "";
                    String remoteName = "";
                    remoteId = String.valueOf(complaint.getNameId());
                    remoteName = "投诉方："+complaint.getName();
                    
                    List<ChatRoom> list = ChatRoomDao.getInstance().queryComplaintAdvisoryList(mComplaintId);
                    boolean byRemoteHasChat = false;
                    boolean remoteHasChat = false;
                    for (ChatRoom room : list) {
                        if (room.getRemote_id().equals(byRemoteId)) {
                        	byRemoteHasChat = true;
                        }
                        if (room.getRemote_id().equals(remoteId)) {
                        	remoteHasChat = true;
                        }
                    }
                    if (!byRemoteHasChat) {
                        ChatRoom room = new ChatRoom();
                        room.setBusiness_type(ChatMsg.business_type_complaint);
                        room.setBusiness_id(mComplaintId);
                        room.setBusiness_owner_id(mBusinessOwnerIdForFirst);
                        room.setId(ChatUtils.getChatMsgTableNameForComplaint(mineId, byRemoteId,
                                String.valueOf(mComplaintId)));
                        room.setRemote_id(byRemoteId);
                        room.setRemote_name(byRemoteName);
                        list.add(room);
                    }
                    if (!remoteHasChat) {
                        ChatRoom room = new ChatRoom();
                        room.setBusiness_type(ChatMsg.business_type_complaint);
                        room.setBusiness_id(mComplaintId);
                        room.setBusiness_owner_id(mBusinessOwnerIdForFirst);
                        room.setId(ChatUtils.getChatMsgTableNameForComplaint(mineId, remoteId,
                                String.valueOf(mComplaintId)));
                        room.setRemote_id(remoteId);
                        room.setRemote_name(remoteName);
                        list.add(room);
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
    
    private Complaint getComplaintById(final String id) {
        NetComplaintById net = new NetComplaintById() {
            @Override
            protected boolean onSetRequest(LogisticsReq.Builder req) {
                req.setId(id);
                return true;
            }
        };
        try {
            CommonLogisticsResp.Builder resp = net.request();
            if (resp != null) {
                return ComplaintParser.parseSingleComplaint(resp);
            }
        } catch (NetGetException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ChatRoomDao.getInstance().removeObserver(this);
    }

    @Override
    public void onChatRoomChange(ChatRoom room, CRUD curd) {
        if (!ChatUtils.isComplaintAdvisoryChatRoom(room.getId(), mComplaintId)) {
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
        if(room.getRemote_id().equals(UserDao.getInstance().getUser().getId())){
        	ToastUtils.showToast("你不能和自己聊天");
        }else{
	        model.setBusiness_type(ChatMsg.business_type_complaint);
	        model.setBusiness_id(room.getBusiness_id());
	        model.setBusiness_owner_id(room.getBusiness_owner_id());
	        if (getActivity() instanceof ChatRoomListAndItemChangable) {
	            ((ChatRoomListAndItemChangable) getActivity()).changeToChatRoomItem(model, room.getRemote_id());
	        }
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
