package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import lib.pulltorefresh.PullToRefreshListView;
import lib.universal_image_loader.ImageLoaderUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.dao.UserDao.UserObserver;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetAddMembers;
import com.epeisong.data.net.NetDeleteMembers;
import com.epeisong.data.net.NetSearchUserList;
import com.epeisong.data.net.NetUserUpdateStatus;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder;
import com.epeisong.model.MarketMember;
import com.epeisong.model.User;
import com.epeisong.model.UserRole;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.ui.activity.SearchUserDetailActivity;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zxing.CaptureActivity;

/**
 * 会员管理页面(左侧)
 * 
 * @author gnn
 * 
 */
public class MembersManagerFragment extends Fragment implements OnClickListener, OnItemClickListener, UserObserver,
        OnItemLongClickListener, OnRefreshListener2<ListView> {

    private EditText etSearch;
    private View line;
    private User mUser;
    private Button btn_search;
    private List<MarketMember> userList;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private MyAdapter mAdapter;
    private TextView mTextViewEmpty;

    private String mResult;
    private String addResult;
    private ReceiveBroadCast receiveBroadCast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userList = new ArrayList<MarketMember>();
        View root = SystemUtils.inflate(R.layout.fragment_members);
        etSearch = (EditText) root.findViewById(R.id.et_search);
        root.findViewById(R.id.tv_add_phone).setOnClickListener(this);
        root.findViewById(R.id.tv_add_code).setOnClickListener(this);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(final Editable edit) {
                if (TextUtils.isEmpty(edit.toString())) {
                    mAdapter.removeAllItem(userList);
                    userList.clear();
                    requestData(10, null , true);
                }
            }
        });
        btn_search = (Button) root.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        line = root.findViewById(R.id.view_line);
        line.setVisibility(View.VISIBLE);
        mPullToRefreshListView = (PullToRefreshListView) root.findViewById(R.id.elv);
        mPullToRefreshListView.setOnRefreshListener(this);
        mListView = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setAdapter(mAdapter = new MyAdapter());
        mPullToRefreshListView.setMode(Mode.BOTH);
        mPullToRefreshListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mUser = UserDao.getInstance().getUser();
        setEmptyView();
        
      //刷新已处理列表
  	  receiveBroadCast = new ReceiveBroadCast();
      IntentFilter filter = new IntentFilter();
      filter.addAction("com.epeisong.ui.activity.refreshMember"); // 只有持有相同的action的接受者才能接收此广播
      this.getActivity().registerReceiver(receiveBroadCast, filter);

        UserDao.getInstance().addObserver(this);
        return root;
    }
    
    @Override
   	public void onAttach(Activity activity) {
    	super.onAttach(activity);
   		/** 注册广播 */
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        this.getActivity().registerReceiver(receiveBroadCast, filter);
   		
   	}
   		
       class ReceiveBroadCast extends BroadcastReceiver {
           @Override
           public void onReceive(Context context, Intent intent) {
        	User user = (User) intent.getSerializableExtra("refreshMember");
           	if(user != null){
//           		mAdapter.addItem(user);
           		onPullUpToRefresh(mPullToRefreshListView);
           		ToastUtils.showToast("更新成功");
           	}
           }
       }

   	@Override
   	public void onDestroyView() {
   		super.onDestroyView();
   		if (receiveBroadCast != null) {
               this.getActivity().unregisterReceiver(receiveBroadCast);
           }
   	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestData(10, null , true);
    }

    private void setEmptyView() {
        LinearLayout emptyLayout = new LinearLayout(getActivity());
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyLayout.setPadding(0, DimensionUtls.getPixelFromDpInt(100), 0, 0);
        ImageView iv = new ImageView(getActivity());
        iv.setImageResource(R.drawable.nopeihuo);
        emptyLayout.addView(iv);
        mTextViewEmpty = new TextView(getActivity());
        mTextViewEmpty.setText("没有会员");
        mTextViewEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mTextViewEmpty.setTextColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
        mTextViewEmpty.setGravity(Gravity.CENTER);
        emptyLayout.addView(mTextViewEmpty);
        mPullToRefreshListView.setEmptyView(emptyLayout);
    }

    public void refresh() {
        mPullToRefreshListView.setRefreshing();
    }

    @Override
    public void onClick(View v) {
        List<MarketMember> slist;
        switch (v.getId()) {
        case R.id.btn_search:
            if (TextUtils.isEmpty(etSearch.getText().toString())) {
                ToastUtils.showToast("请输入搜索姓名");
                return;
            }
            slist = new ArrayList<MarketMember>();
            if (userList != null) {
                for (MarketMember member : userList) {
                    if (member.getUser().getShow_name().contains(etSearch.getText().toString())) {
                        slist.add(member);
                    }
                }
                SystemUtils.hideInputMethod(v);
                if (slist.size() > 0) {
                    mAdapter.replaceAll(slist);
                } else {
                    ToastUtils.showToast("没有您要搜索的会员");
                }
            }
            break;
        default:
            // Object tag = v.getTag();
            // if (tag != null && tag instanceof Contacts) {
            // Contacts c = (Contacts) tag;
            // if (!TextUtils.isEmpty(c.getLogo_url())) {
            // ArrayList<String> urls = new ArrayList<String>();
            // urls.add(c.getLogo_url());
            // ShowImagesActivity.launch(getActivity(), urls, 0);
            // }
            // }
            break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        position -= mPullToRefreshListView.getRefreshableView().getHeaderViewsCount();
        TextView tv_name;
        TextView tv_delete;
        TextView tv_sign;
        final MarketMember m = mAdapter.getItem(position);

        if (m != null) {
            final AlertDialog builder = new AlertDialog.Builder(getActivity()).create();
            builder.show();
            Window window = builder.getWindow();
            window.setContentView(R.layout.members_dialog);
            tv_name = (TextView) window.findViewById(R.id.tv_name);
            tv_delete = (TextView) window.findViewById(R.id.tv_delete);
            tv_sign = (TextView) window.findViewById(R.id.tv_sign);
            builder.setCanceledOnTouchOutside(true);
            tv_name.setText(m.getUser().getShow_name());
            if (m.getIsBanned() == User.STATUS_SHIELDING) {
                tv_sign.setText("解除屏蔽");
            } else {
                tv_sign.setText("屏蔽会员");
            }
            tv_delete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    deleteMembers(m);
                    builder.dismiss();
                }
            });
            tv_sign.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    changeState(m);
                    builder.dismiss();
                }
            });
            // dialog.show();
            return true;
        }

        return false;
    }

    private void changeState(final MarketMember member) {
        final int curStatus = member.getIsBanned();
        ((XBaseActivity) getActivity()).showPendingDialog(null);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                NetUserUpdateStatus net = new NetUserUpdateStatus() {

                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setLogisticId(Integer.parseInt(member.getUser().getId()));
                        if (curStatus == User.STATUS_SHIELDING) {
                            req.setNewStatus(User.STATUS_SHIELDING);
                            req.setIsBanned(User.STATUS_NOSHIELDING);
                        } else {
                            req.setNewStatus(User.STATUS_SHIELDING);
                            req.setIsBanned(User.STATUS_SHIELDING);
                        }
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (resp != null && "SUCC".equals(resp.getResult())) {
                        return true;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                ((XBaseActivity) getActivity()).dismissPendingDialog();
                if (result) {
                    if (curStatus == User.STATUS_SHIELDING) {
                        member.setIsBanned(User.STATUS_NOSHIELDING);
                    } else {
                        member.setIsBanned(User.STATUS_SHIELDING);
                    }
                    Intent intent = new Intent("com.epeisong.ui.activity.changeLish");
                    intent.putExtra("changeList", member);
                    getActivity().sendBroadcast(intent); // 发送广播
                    mAdapter.notifyDataSetChanged();
                }
            }
        };
        task.execute();
    }

    @Override
    public void onUserChange(User user) {
        // TODO Auto-generated method stub
        // requestData(10, null);
    }

    private void requestData(final int size, final String edgeId , final boolean refresh) {
        mUser = UserDao.getInstance().getUser();
        AsyncTask<Void, Void, List<MarketMember>> task = new AsyncTask<Void, Void, List<MarketMember>>() {
            @Override
            protected List<MarketMember> doInBackground(Void... params) {
                NetSearchUserList net = new NetSearchUserList() {
                    @Override
                    protected int getCommandCode() {
                        return CommandConstants.MANAGE_MEMBERS_REQ;
                    }

                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setMarketId(Integer.parseInt(mUser.getId()));
                        req.setLimitCount(size);
                        // req.setLogisticId(Integer.parseInt(mMarket.getId()));
                        // req.setIsManageMembers(true);
                        if (edgeId != null) {
                            req.setId(Integer.parseInt(edgeId));
                        }
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        // List<LogisticAndRMarketMember> logi =
                        // resp.getLogisticAndRMarketMemberList();
                        return UserParser.parseMember(resp);
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<MarketMember> result) {
                mPullToRefreshListView.onRefreshComplete();
                // setListShown(true);

                if (result != null) {
                    if (result.size() > 0) {
                        if(refresh){
                        	userList.addAll(result);
                            mAdapter.addAll(result);
                        }else{
                        	userList.removeAll(result);
                            mAdapter.replaceAll(result);
                        }
                    } else {
                        ToastUtils.showToast("没有更多会员");
                    }
                }
            }
        };
        task.execute();
    }
    
    private class MyAdapter extends HoldDataBaseAdapter<MarketMember> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.activity_choose_members_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            MarketMember m = getItem(position);
            if (m.getIsBanned() == User.STATUS_SHIELDING) {
                convertView.setBackgroundResource(R.color.market_gray);
            } else {
                convertView.setBackgroundResource(0);
            }
            holder.fillData(m);

            return convertView;
        }
    }

    private class ViewHolder {
        RelativeLayout rl_parent;
        ImageView iv_head;
        TextView tv_user_name;
        TextView tv_address;
        TextView tv_members_sign;
        View view;

        public void fillData(MarketMember member) {
            if (!TextUtils.isEmpty(member.getUser().getLogo_url())) {
                ImageLoader.getInstance().displayImage(member.getUser().getLogo_url(), iv_head,
                        ImageLoaderUtils.getListOptionsForUserLogo());
            } else {
                int defaultIcon = User.getDefaultIcon(member.getUser().getUser_type_code(), true);
                iv_head.setImageResource(defaultIcon);
            }
            iv_head.setTag(member);
            tv_user_name.setText(member.getUser().getShow_name());
            UserRole userRole = member.getUser().getUserRole();
            if (userRole != null) {
                tv_address.setText(userRole.getRegionName());// .getAddress());
            }

        }

        public void findView(View v) {
            rl_parent = (RelativeLayout) v.findViewById(R.id.rl_parent);
            iv_head = (ImageView) v.findViewById(R.id.iv_contacts_logo);
            tv_user_name = (TextView) v.findViewById(R.id.tv_contacts_name);
            tv_address = (TextView) v.findViewById(R.id.tv_address);
            tv_members_sign = (TextView) v.findViewById(R.id.tv_members_sign);
            view = v.findViewById(R.id.child_view);
            iv_head.setOnClickListener(MembersManagerFragment.this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        position -= mPullToRefreshListView.getRefreshableView().getHeaderViewsCount();
        MarketMember m = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), ContactsDetailActivity.class);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, m.getUser().getId());
        // User u = contacts.convertToUser();
        intent.putExtra(ContactsDetailActivity.EXTRA_SHOW_PAGE_COUNT_OTHER, 3);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER, m.getUser());
        intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, m.getUser().getUser_type_code());
        startActivity(intent);

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
    	requestData(10, null , false);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        String edgeId = null;
        if (!mAdapter.isEmpty()) {
            edgeId = mAdapter.getItem(mAdapter.getCount() - 1).getId();
        }
        requestData(10, edgeId , true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
            User user = (User) data.getSerializableExtra(SearchUserDetailActivity.EXTRA_USER);
            data.putExtra("mUser", user);
//             mAdapter.addItem(user);
            onPullUpToRefresh(mPullToRefreshListView);
            Intent intent = new Intent("com.epeisong.ui.activity.refreshMember");
            intent.putExtra("refreshMember", user);
            getActivity().sendBroadcast(intent); // 发送广播
        }
        if (requestCode == 22 && resultCode == Activity.RESULT_OK) {
            mResult = data.getStringExtra(CaptureActivity.EXTRA_OUT_RESULT);
            if (TextUtils.isEmpty(mResult)) {
                ToastUtils.showToast("无结果");
            } else if (mResult.startsWith("http://www.epeisong.com/addcontact")) {
                addMembers(mResult);
            } else {
                ToastUtils.showToast("扫描失败，请重新扫描");
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteMembers(final MarketMember m) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                NetDeleteMembers net = new NetDeleteMembers() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setLogisticId(Integer.parseInt(m.getUser().getId()));
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (resp != null && "SUCC".equals(resp.getResult())) {
                        return true;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    ToastUtils.showToast("删除成功");
                    Intent intent = new Intent("com.epeisong.ui.activity.romeveLish");
                    intent.putExtra("romeveLish", m);
                    getActivity().sendBroadcast(intent); // 发送广播
                    mAdapter.removeItem(m);
                }
            }
        };
        task.execute();
    }

    private void addMembers(final String qrUrl) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetAddMembers net = new NetAddMembers() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setQrCodeAddContactURL(mResult);
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        onPullUpToRefresh(mPullToRefreshListView);
                        return true;
                    } else {
                    	addResult = resp.getDesc();
                        LogUtils.e("", resp.getDesc());
                    }
                    ToastUtils.showToastInThread(resp.getDesc());
                } catch (NetGetException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showToastInThread("解析失败");
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    ToastUtils.showToast("添加会员成功");
                }else{
                	ToastUtils.showToast(addResult);
                }
            }
        };
        task.execute();
    }
}
