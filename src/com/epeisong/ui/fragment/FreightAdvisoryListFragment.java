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
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.data.utils.ChatUtils;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.ChatRoom;
import com.epeisong.ui.fragment.ChatRoomExpandFragment.ChatRoomListAndItemChangable;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 我的车源货源的咨询列表
 * 
 * @author poet
 * 
 */
public class FreightAdvisoryListFragment extends PullToRefreshListViewFragment implements ChatRoomObserver {

    public static final String ARGS_FREIGHT_ID = "freight_id";

    private MyAdapter mAdapter;

    private String mFreightId;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPullToRefreshListView.setMode(Mode.DISABLED);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFreightId = bundle.getString(ARGS_FREIGHT_ID);
        }
        if (TextUtils.isEmpty(mFreightId)) {
            ToastUtils.showToast("freight_id is empty!");
            return;
        } else {
            AsyncTask<Void, Void, List<ChatRoom>> task = new AsyncTask<Void, Void, List<ChatRoom>>() {
                @Override
                protected List<ChatRoom> doInBackground(Void... params) {
                    return ChatRoomDao.getInstance().queryFreightAdvisoryList(mFreightId);
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
    protected View onCreateEmptyView() {
        return createEmptyViewDefault("暂无人咨询");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ChatRoomDao.getInstance().removeObserver(this);
    }

    @Override
    public void onChatRoomChange(ChatRoom room, CRUD curd) {
        if (!ChatUtils.isFreightAdvisoryChatRoom(room.getId(), mFreightId)) {
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
        model.setBusiness_type(ChatMsg.business_type_freight);
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
