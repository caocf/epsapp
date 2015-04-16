package com.epeisong.ui.fragment;

import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.universal_image_loader.ImageLoaderUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.fragment.PullToRefreshListViewFragment;
import com.epeisong.data.dao.BulletinDao;
import com.epeisong.data.dao.BulletinDao.BulletinObserver;
import com.epeisong.data.dao.ChatMsgDao;
import com.epeisong.data.dao.ChatRoomDao;
import com.epeisong.data.dao.ChatRoomDao.ChatRoomObserver;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.FreightDao;
import com.epeisong.data.dao.FreightDao.FreightObserver;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.data.layer01.CommonMsgProvider;
import com.epeisong.data.layer01.CommonMsgProvider.ProviderHelper;
import com.epeisong.data.model.BaseListModel.DataType;
import com.epeisong.data.model.CommonMsg;
import com.epeisong.model.Bulletin;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.ChatRoom;
import com.epeisong.model.Contacts;
import com.epeisong.model.Freight;
import com.epeisong.model.User;
import com.epeisong.ui.activity.BulletinDetailActivity;
import com.epeisong.ui.activity.ChatRoomActivity;
import com.epeisong.ui.activity.ComplaintDealDetailActivity;
import com.epeisong.ui.activity.FreightDetailActivity;
import com.epeisong.ui.activity.InfoFeeDetailActivity;
import com.epeisong.ui.activity.TransferWithdrawalDetailActivity;
import com.epeisong.ui.view.RoundImageView;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 消息
 * 
 * @author poet
 * 
 */
public class CommonMsgFragment extends PullToRefreshListViewFragment implements ChatRoomObserver, BulletinObserver,
        FreightObserver {

    private static final int SIZE_BUFFER = 30;
    private static final int SIZE_LOAD_FIRST = 10;
    private static final int SIZE_LOAD_NEWER = 10;
    private static final int SIZE_LOAD_OLDER = 10;

    private MyAdapter mAdapter;

    private ProviderHelper mProviderHelper;

    private boolean mHasMoreNewer;
    private boolean mIsLoadingNewer;

    private int mAutoLoadMoreCount;

    private boolean mIsEditable;
    private View mEditBackView;

    @Override
    protected View onCreateEmptyView() {
        return createEmptyViewDefault("没有消息");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoadingView();
        loadDataFirst(SIZE_LOAD_FIRST);

        mEndlessAdapter.setIsAutoLoad(true);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsEditable && event.getAction() == MotionEvent.ACTION_UP) {
                    int item = mListView.pointToPosition((int) event.getX(), (int) event.getY());
                    if (item == ListView.INVALID_POSITION) {
                        mEditBackView.setVisibility(View.GONE);
                        mIsEditable = false;
                        mAdapter.notifyDataSetChanged();
                        return true;
                    }
                }
                return false;
            }
        });

        BulletinDao.getInstance().addObserver(this);
        ChatRoomDao.getInstance().addObserver(this);
        FreightDao.getInstance().addObserver(this);
    }

    @Override
    public void onDestroyView() {
        BulletinDao.getInstance().removeObserver(this);
        ChatRoomDao.getInstance().removeObserver(this);
        FreightDao.getInstance().removeObserver(this);
        super.onDestroyView();
    }

    @Override
    protected ListAdapter onCreateAdapter() {
        mAdapter = new MyAdapter();
        mAdapter.setMaxSize(SIZE_BUFFER);
        return mAdapter;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mIsEditable) {
            return true;
        }
        position -= mListView.getHeaderViewsCount();
        final CommonMsg msg = mAdapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] items;
        items = new String[2];
        items[0] = "删除";
        items[1] = "批量删除";
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    removeMsg(msg);
                } else if (which == 1) {
                    mIsEditable = true;
                    mAdapter.notifyDataSetChanged();
                    if (mEditBackView == null) {
                        RelativeLayout rl = new RelativeLayout(getActivity());
                        rl.setBackgroundColor(Color.WHITE);
                        Button btn = new Button(getActivity());
                        btn.setText("返回");
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mEditBackView.setVisibility(View.GONE);
                                mIsEditable = false;
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        rl.addView(btn, params);
                        mEditBackView = rl;
                        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(-1, DimensionUtls
                                .getPixelFromDpInt(60));
                        p.gravity = Gravity.BOTTOM;
                        getActivity().addContentView(mEditBackView, p);
                    } else {
                        mEditBackView.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
        AlertDialog d = builder.create();
        d.setCanceledOnTouchOutside(true);
        d.show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= mListView.getHeaderViewsCount();
        CommonMsg msg = mAdapter.getItem(position);
        if (mIsEditable) {
            removeMsg(msg);
            return;
        }
        switch (msg.getDataType()) {
        case BULLETIN:
            Intent bulletinIntent = new Intent(getActivity(), BulletinDetailActivity.class);
            bulletinIntent.putExtra(BulletinDetailActivity.EXTRA_BULLETIN_ID, msg.getId());
            startActivity(bulletinIntent);
            break;
        case CHAT:
            BusinessChatModel model = msg.getBusinessChatModel();
            if (model == null) {
                Intent chatIntent = new Intent(getActivity(), ChatRoomActivity.class);
                chatIntent.putExtra(ChatRoomActivity.EXTRA_REMOTE_ID, msg.getSender_id());
                startActivity(chatIntent);
                return;
            }
            if (model.getBusiness_type() == ChatMsg.business_type_freight) {
                Intent intent = new Intent(getActivity(), FreightDetailActivity.class);
                intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
                intent.putExtra(FreightDetailActivity.EXTRA_SHOW_ADVISORY_FIRST, true);
                if (model.getBusiness_owner_id().equals(UserDao.getInstance().getUser().getId())) {
                    intent.putExtra(FreightDetailActivity.EXTRA_ADVISORYER_ID, msg.getSender_id());
                }
                startActivity(intent);
                return;
			} else if (model.getBusiness_type() == ChatMsg.business_type_complaint) {
				Intent intent = new Intent(getActivity(), ComplaintDealDetailActivity.class);
				intent.putExtra(ComplaintDealDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
                intent.putExtra(ComplaintDealDetailActivity.EXTRA_REMOTE_ID, msg.getSender_id());
                intent.putExtra(ComplaintDealDetailActivity.EXTRA_SHOW_ADVISORY_FIRST, true);
                intent.putExtra(ComplaintDealDetailActivity.EXTRA_SHOW_CHAT_FIRST, true);
				if(!model.getBusiness_owner_id().equals(UserDao.getInstance().getUser().getId())){
					intent.putExtra(ComplaintDealDetailActivity.EXTRA_WHETHER_CUSTOMER, "NoCustomer");
				}
                startActivity(intent);
                return;
            } else if (model.getBusiness_type() == ChatMsg.business_type_info_fee) {
                Intent intent = new Intent(getActivity(), InfoFeeDetailActivity.class);
                intent.putExtra(InfoFeeDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
                intent.putExtra(InfoFeeDetailActivity.EXTRA_SHOW_CHAT_FIRST, true);
                intent.putExtra(InfoFeeDetailActivity.EXTRA_REMOTE_ID, msg.getSender_id());
                startActivity(intent);
                return;
            } else if (model.getBusiness_type() == ChatMsg.business_type_withdraw) {
                Intent intent = new Intent(getActivity(), TransferWithdrawalDetailActivity.class);
                intent.putExtra(TransferWithdrawalDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
                intent.putExtra(TransferWithdrawalDetailActivity.EXTRA_REMOTE_ID, msg.getSender_id());
                intent.putExtra(TransferWithdrawalDetailActivity.EXTRA_SHOW_CHAT_FIRST, true);
//                if (model.getBusiness_owner_id().equals(UserDao.getInstance().getUser().getId())) {
//                    intent.putExtra(TransferWithdrawalDetailActivity.EXTRA_ADVISORYER_ID, msg.getSender_id());
//                }
                startActivity(intent);
                return;
            }
            break;
        case FREIGHT:
            Intent i = new Intent(getActivity(), FreightDetailActivity.class);
            i.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, msg.getBusinessChatModel());
            startActivity(i);
            break;
        default:
            break;
        }
    }

    @Override
    public void onBulletinChange(Bulletin b, CRUD crud) {
        CommonMsg msg = CommonMsgProvider.convert(b);
        onDataChange(msg, crud);
    }

    @Override
    public void onChatRoomChange(final ChatRoom room, final CRUD crud) {
        CommonMsg msg = CommonMsgProvider.convert(room);
        onDataChange(msg, crud);
    }

    @Override
    public void onFreightChange(Freight f, CRUD crud) {
        CommonMsg msg = CommonMsgProvider.convert(f);
        onDataChange(msg, crud);
    }

    private void removeMsg(CommonMsg msg) {
        switch (msg.getDataType()) {
        case BULLETIN:
            Bulletin b = new Bulletin();
            b.setId(msg.getId());
            BulletinDao.getInstance().delete(b);
            break;
        case CHAT:
            ChatRoom room = new ChatRoom();
            room.setId(msg.getId());
            ChatRoomDao.getInstance().delete(room);
            ChatMsgDao.getInstance().deleteTable(msg.getId());
            break;
        case FREIGHT:
            Freight f = new Freight();
            f.setId(msg.getId());
            FreightDao.getInstance().delete(f);
            break;
        default:
            break;
        }
    }

    private void onDataChange(CommonMsg msg, CRUD crud) {
        switch (crud) {
        case CREATE:
        case REPLACE:
            if (mAdapter.getAllItem().contains(msg)) {
                mAdapter.removeItem(msg);
            }
            if (!mHasMoreNewer) {
                mAdapter.addItem(0, msg);
            }
            break;
        case DELETE:
            if (mAdapter.getAllItem().contains(msg)) {
                mAdapter.removeItem(msg);
            }
            break;
        case UPDATE:
            if (mAdapter.getAllItem().contains(msg)) {
                mAdapter.removeItem(msg);
            }
            if (!mHasMoreNewer) {
                mAdapter.addItem(0, msg);
            }
            break;
        case READ:
            if (mAdapter.getAllItem().contains(msg)) {
                int index = mAdapter.indexOf(msg);
                mAdapter.removeItem(msg);
                mAdapter.addItem(index, msg);
            }
        default:
            break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        loadDataFirst(SIZE_LOAD_FIRST);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        if (firstVisibleItem == 0) {
            if (mHasMoreNewer && !mIsLoadingNewer) {
                AsyncTask<Void, Void, List<CommonMsg>> task = new AsyncTask<Void, Void, List<CommonMsg>>() {
                    @Override
                    protected List<CommonMsg> doInBackground(Void... params) {
                        CommonMsgProvider p = new CommonMsgProvider();
                        long last_time = mAdapter.getItem(0).getSend_time();
                        return p.provideNewer(last_time, mProviderHelper, SIZE_LOAD_NEWER);
                    }

                    @Override
                    protected void onPostExecute(List<CommonMsg> data) {
                        if (data == null) {
                            ToastUtils.showToast("加载失败");
                            mIsLoadingNewer = false;
                            return;
                        }
                        boolean bFull = data.size() >= SIZE_LOAD_NEWER;
                        if (bFull) {
                            mEndlessAdapter.setHasMore(true);
                        }
                        if (data == null || data.isEmpty() || data.size() < SIZE_LOAD_NEWER) {
                            mHasMoreNewer = false;
                            mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
                        }
                        mAdapter.addAll(0, data);
                        refreshProviderHelper();
                        mListView.setSelection(data.size());
                        mIsLoadingNewer = false;
                    }
                };
                task.execute();
                mIsLoadingNewer = true;
            }
        }
    }

    @Override
    public void onStartLoadMore(EndlessAdapter adapter) {
        AsyncTask<Void, Void, List<CommonMsg>> task = new AsyncTask<Void, Void, List<CommonMsg>>() {

            @Override
            protected List<CommonMsg> doInBackground(Void... params) {
                CommonMsgProvider p = new CommonMsgProvider();
                long last_time = mAdapter.getItem(mAdapter.getCount() - 1).getSend_time();
                return p.provideOlder(last_time, mProviderHelper, SIZE_LOAD_OLDER);
            }

            @Override
            protected void onPostExecute(List<CommonMsg> data) {
                if (data == null) {
                    mEndlessAdapter.endLoad(false);
                    return;
                }
                mEndlessAdapter.endLoad(true);

                boolean bFull = mAdapter.getCount() >= SIZE_BUFFER;
                if (data.size() >= SIZE_LOAD_OLDER) {
                    mEndlessAdapter.setHasMore(true);
                } else {
                    mEndlessAdapter.setHasMore(false);
                }
                mAdapter.addAll(data);
                refreshProviderHelper();
                if (bFull) {
                    mHasMoreNewer = true;
                    mPullToRefreshListView.setMode(Mode.DISABLED);
                    int showCount = mListView.getLastVisiblePosition() - mListView.getFirstVisiblePosition();
                    mListView.setSelection(mAdapter.getCount() - data.size() - showCount + 1);
                }
                if (++mAutoLoadMoreCount > 3) {
                    mEndlessAdapter.setIsAutoLoad(false);
                }
            }
        };
        task.execute();
    }

    private void refreshProviderHelper() {
        if (mProviderHelper == null) {
            mProviderHelper = new ProviderHelper();
        }
        List<CommonMsg> msgs = mAdapter.getAllItem();

        String freightIdOlder = null;
        String freightIdNewer = null;
        for (CommonMsg msg : msgs) {
            if (msg.getDataType() == DataType.FREIGHT) {
                if (freightIdNewer == null) {
                    freightIdNewer = msg.getId();
                }
                freightIdOlder = msg.getId();
            }
        }
        mProviderHelper.setFreight_id_newer(freightIdNewer);
        mProviderHelper.setFreight_id_older(freightIdOlder);
    }

    @Override
    protected void onFailViewClick() {
        loadDataFirst(SIZE_LOAD_FIRST);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser && mIsEditable) {
            mEditBackView.setVisibility(View.GONE);
            mIsEditable = false;
            mAdapter.notifyDataSetChanged();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void loadDataFirst(int size) {
        AsyncTask<Void, Void, List<CommonMsg>> task = new AsyncTask<Void, Void, List<CommonMsg>>() {

            @Override
            protected List<CommonMsg> doInBackground(Void... params) {
                CommonMsgProvider p = new CommonMsgProvider();
                return p.provideFirst(SIZE_LOAD_FIRST);
            }

            @Override
            protected void onPostExecute(List<CommonMsg> data) {
                if (data == null) {
                    onRequestComplete(false);
                    return;
                }
                mAdapter.replaceAll(data);
                refreshProviderHelper();
                onRequestComplete(true);
                if (data.size() >= SIZE_LOAD_FIRST) {
                    mEndlessAdapter.setHasMore(true);
                    mEndlessAdapter.mUseEndView = true;
                }
            }
        };
        task.execute();
    }

    private class MyAdapter extends HoldDataBaseAdapter<CommonMsg> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = SystemUtils.inflate(R.layout.fragment_common_msg_item);
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
        RoundImageView iv_icon;
        TextView tv_new_msg_count;
        TextView tv_top_left;
        TextView tv_top_right;
        TextView tv_bottom_left;
        TextView tv_bottom_right;
        TextView tv_delete;

        public void findView(View v) {
            iv_icon = (RoundImageView) v.findViewById(R.id.iv_icon);
            tv_new_msg_count = (TextView) v.findViewById(R.id.tv_new_msg_count);
            tv_top_left = (TextView) v.findViewById(R.id.tv_top_left);
            tv_top_right = (TextView) v.findViewById(R.id.tv_top_right);
            tv_bottom_left = (TextView) v.findViewById(R.id.tv_bottom_left);
            tv_bottom_right = (TextView) v.findViewById(R.id.tv_bottom_right);
            tv_delete = (TextView) v.findViewById(R.id.tv_delete);
        }

        public void fillData(CommonMsg msg) {
            tv_new_msg_count.setVisibility(View.GONE);
            tv_top_right.setVisibility(View.INVISIBLE);
            Contacts contacts = ContactsDao.getInstance().queryById(msg.getSender_id());
            switch (msg.getDataType()) {
            case BULLETIN:
                tv_top_right.setVisibility(View.VISIBLE);
                iv_icon.setImageResource(R.drawable.message_bulletin);
                tv_top_left.setText("公告");
                if (contacts != null) {
                    tv_top_right.setText(contacts.getShow_name());
                } else {
                    tv_top_right.setText(msg.getSender_name());
                }
                break;
            case CHAT:
                String extra_01 = msg.getExtra_01();
                if (!TextUtils.isEmpty(extra_01)) {
                    tv_new_msg_count.setVisibility(View.VISIBLE);
                    tv_new_msg_count.setText(extra_01);
                }
                if (msg.getBusinessChatModel() == null) {
                    if (contacts != null) {
                        tv_top_left.setText(contacts.getShow_name());
                    } else {
                        tv_top_left.setText(msg.getSender_name());
                    }
                    String url = null;
                    Contacts c = ContactsDao.getInstance().queryById(msg.getSender_id());
                    if (c != null) {
                        url = c.getLogo_url();
                    }
                    int logoId = User.getDefaultIcon(msg.getSender_logistic_type(), contacts != null);
                    if (TextUtils.isEmpty(url)) {
                        iv_icon.setImageResource(logoId);
                    } else {
                        ImageLoader.getInstance().displayImage(url, iv_icon,
                                ImageLoaderUtils.getListOptionsForUserLogo());
                    }
                } else {
                    tv_top_right.setVisibility(View.VISIBLE);
                    BusinessChatModel model = msg.getBusinessChatModel();
                    if (model.getBusiness_type() == ChatMsg.business_type_freight) {
                        int type = 0;
                        try {
                            type = Integer.parseInt(model.getBusiness_extra());
                        } catch (Exception e) {

                        }
                        if (type == Freight.TYPE_GOODS) {
                            iv_icon.setImageResource(R.drawable.icon_freight_goods);
                        } else {
                            iv_icon.setImageResource(R.drawable.icon_freight_truck);
                        }
                        tv_top_left.setText(model.getBusiness_desc());
                        if ((UserDao.getInstance().getUser().getId()).equals(model.getBusiness_owner_id())) {
                            tv_top_right.setText("我的");
                        } else {
                            tv_top_right.setText(msg.getSender_name());
                        }
                    } else if (model.getBusiness_type() == ChatMsg.business_type_info_fee) {
                        iv_icon.setImageResource(R.drawable.icon_common_msg_info_fee);
                        tv_top_left.setText(model.getBusiness_desc());
                        tv_top_right.setText(msg.getSender_name());
                    } else if(model.getBusiness_type() == ChatMsg.business_type_withdraw){
                    	iv_icon.setImageResource(R.drawable.icon_withdrawal_task);
                        tv_top_right.setVisibility(View.GONE);
                        tv_top_left.setText(msg.getSender_name());
                    }else if(model.getBusiness_type() == ChatMsg.business_type_complaint){
                    	iv_icon.setImageResource(R.drawable.complaint_msg_task);
                        tv_top_right.setVisibility(View.GONE);
                        tv_top_left.setText(msg.getSender_name());
//                        tv_top_left.setText(msg.getBusinessChatModel().getBusiness_id());
                    }
                }
                break;
            case FREIGHT:
                tv_top_right.setVisibility(View.VISIBLE);
                String type = msg.getExtra_01();
                if (String.valueOf(Freight.TYPE_GOODS).equals(type)) {
                    iv_icon.setImageResource(R.drawable.icon_freight_goods);
                } else if (String.valueOf(Freight.TYPE_TRUCK).equals(type)) {
                    iv_icon.setImageResource(R.drawable.icon_freight_truck);
                } else {
                    iv_icon.setImageResource(R.drawable.ic_launcher);
                }
                tv_top_right.setText(msg.getSender_name());
                tv_top_left.setText(msg.getExtra_02() + "-" + msg.getExtra_03());
                break;
            default:
                break;
            }
            tv_bottom_left.setText(msg.getContent());
            tv_bottom_right.setText(DateUtil.long2vague(msg.getSend_time()));
            if (mIsEditable) {
                tv_delete.setVisibility(View.VISIBLE);
            } else {
                tv_delete.setVisibility(View.GONE);
            }
        }
    }

}
