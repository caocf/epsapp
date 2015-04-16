package com.epeisong.ui.fragment;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.EndlessEmptyListView;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetLogisticsSearch;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.model.User;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.ui.activity.SetGetGoodsActivity;
import com.epeisong.ui.view.Choosable;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 网点信息
 * 
 * @author Jack
 * 
 */

public class NetInforFragment extends VerticalFilterFragment implements OnClickListener, OnItemClickListener {
    private static final int SIZE_LOAD_FIRST = 10;
    private TextView view_empty;
    private EndlessEmptyListView lv;

    protected MyAdapter mAdapter;
    private int Logistic_type;

    private String mUserId;
    private User mUser;
    protected EndlessAdapter mEndlessAdapter;

    private int mGoodstype;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        mUser = (User) args.getSerializable(ContactsDetailActivity.EXTRA_USER);
        // if(mUser==null)
        // {
        // ToastUtils.showToast("参数错误");
        // return null;
        // }
        // else
        // Logistic_type = mUser.getUser_type_code();
        Logistic_type = args.getInt(ContactsDetailActivity.EXTRA_USER_TYPEID);
        mUserId = args.getString(ContactsDetailActivity.EXTRA_USER_ID);
        mGoodstype = Integer.valueOf(args.getString(SetGetGoodsActivity.EXTRA_GOODS_TYPE));

        View root = inflater.inflate(R.layout.activity_net_info, null);
        // root.setBackgroundColor(Color.WHITE);

        lv = (EndlessEmptyListView) root.findViewById(R.id.lv_net);
        lv.setAdapter(mAdapter = new MyAdapter());
        lv.setOnItemClickListener(this);

        if (mAdapter != null) {
            mEndlessAdapter = new EndlessAdapter(getActivity(), mAdapter);
            mEndlessAdapter.setIsAutoLoad(true);//false);
            mEndlessAdapter.setOnLoadMoreListener(this);
            lv.setAdapter(mEndlessAdapter);
        }

        view_empty = (TextView) root.findViewById(R.id.view_netempty);
        view_empty.setText(null);
        lv.setEmptyView(view_empty);

        loadData(SIZE_LOAD_FIRST, "0", 0, true);
        return root;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        default:
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void loadData(final int size, final String edge_id, final double weight, final boolean bFirst) {
        final int marketId = Integer.parseInt(mUserId);

        AsyncTask<Void, Void, List<User>> task = new AsyncTask<Void, Void, List<User>>() {
        	@Override
        	protected List<User> doInBackground(Void... params) {
        		NetLogisticsSearch net = new NetLogisticsSearch() {
        			@Override
        			protected int getCommandCode() {
        				// TODO Auto-generated method stub
        				return CommandConstants.GET_MEMBERS_REQ;
        			}
        			@Override
        			protected boolean onSetRequest(com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder req) {
        				int id = 0;
        				try {
        					if (edge_id != null) {
        						id = Integer.parseInt(edge_id);
        					}
        				} catch (Exception e) {
        					id = 0;
        				}
        				req.setId(id);
        				req.setMarketId(marketId);
        				req.setLimitCount(size);
        				req.setLogisticTypeCode(-1);

        				return true;
        			}
        		};

        		try {
        			CommonLogisticsResp.Builder resp = net.request();
        			if (resp == null) {
        				return null;
        			}
        			return UserParser.parse(resp);
        		} catch (NetGetException e) {
        			e.printStackTrace();
        		}
        		return null;                	

        	}

        	protected void onPostExecute(List<User> result) {
        		if (bFirst) {
        			mAdapter.addAll(result);
        		} else {
        			mAdapter.replaceAll(result);
        		}
        		mEndlessAdapter.setHasMore(result.size() >= size);
        	}
        };
        task.execute();
                
//        NetSearchNetInfor net = new NetSearchNetInfor((XBaseActivity) getActivity(), marketId, size,
//                Integer.parseInt(edge_id), -1, mUser.getRegion_code(), mUser.getServe_type_a(),
//                mUser.getServe_type_b(), -1, -1, weight);// Logistic_type);//user
                                                         // type code
//        net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
//
//            public void onSuccess(CommonLogisticsResp.Builder response) {
//                List<User> result = UserParser.parse(response);
//                if (edge_id == null) {
//                    mAdapter.addAll(result);
//                } else {
//                    mAdapter.replaceAll(result);
//                }
//                mEndlessAdapter.setHasMore(result.size() >= size);
//            }
//        });
    }

    @Override
    public void onStartLoadMore(EndlessAdapter adapter) {
        String edge_id = mAdapter.getItem(mAdapter.getCount() - 1).getId();
        loadData(10, edge_id, 0, false);// remember to add
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

        User user = mAdapter.getItem(arg2);
        Intent intent = new Intent();
        intent.setClass(getActivity(), ContactsDetailActivity.class);

        intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, user.getId());
        intent.putExtra(ContactsDetailActivity.EXTRA_USER, user);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, user.getUser_type_code());
        // startActivity(intent);

    }

    protected class MyAdapter extends HoldDataBaseAdapter<User> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.fragment_netinfor_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position), position);
            return convertView;
        }
    }

    private class ViewHolder {
        public TextView tv_netname;
        public TextView tv_net_name;
        public TextView tv_net_telephone;
        public TextView tv_net_phone;
        public TextView tv_net_address;
        public TextView tv_region_name;

        public ImageView iv_netaction_phone;
        public ImageView iv_netaction_telephone;

        // TODO
        public void fillData(final User u, int pos) {
            // 参考 HomeFragment
            if (u == null) {
                return;
            }
            if (!TextUtils.isEmpty(u.getUser_type_name())) {
                tv_netname.setText(u.getUser_type_name());
            }
            if (!TextUtils.isEmpty(u.getContacts_name())) {
                tv_net_name.setText(u.getContacts_name());
            }
            if (!TextUtils.isEmpty(u.getRegion())) {
                tv_region_name.setText(u.getRegion());
            }
            if (!TextUtils.isEmpty(u.getContacts_phone())) {
                tv_net_phone.setText(u.getContacts_phone());
            }

            if (!TextUtils.isEmpty(u.getContacts_telephone())) {
                tv_net_telephone.setText(u.getContacts_telephone());
            }

            if (!TextUtils.isEmpty(u.getAddress())) {
                tv_net_address.setText(u.getRegion() + u.getAddress());
            }
            iv_netaction_phone.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    // ToastUtils.showToast("phone");
                    if (!TextUtils.isEmpty(u.getContacts_phone())) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + u.getContacts_phone()));
                        getActivity().startActivity(intent);
                    }
                }
            });
            iv_netaction_telephone.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    // ToastUtils.showToast("telephone");
                    if (!TextUtils.isEmpty(u.getContacts_telephone())) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + u.getContacts_telephone()));
                        getActivity().startActivity(intent);
                    }
                }
            });
        }

        public void findView(View v) {
            tv_netname = (TextView) v.findViewById(R.id.tv_netname);
            tv_net_name = (TextView) v.findViewById(R.id.tv_net_name);
            tv_region_name = (TextView) v.findViewById(R.id.tv_region_name);
            tv_net_telephone = (TextView) v.findViewById(R.id.tv_net_telephone);
            tv_net_phone = (TextView) v.findViewById(R.id.tv_net_phone);
            tv_net_address = (TextView) v.findViewById(R.id.tv_net_address);
            iv_netaction_phone = (ImageView) v.findViewById(R.id.iv_netaction_phone);
            iv_netaction_telephone = (ImageView) v.findViewById(R.id.iv_netaction_telephone);

        }
    }

    @Override
    protected void onClickSearchBtn() {
        // TODO Auto-generated method stub

    }

    @Override
    protected Map<String, Choosable> onCreateFilterView() {
        // TODO Auto-generated method stub
        return null;
    }

}
