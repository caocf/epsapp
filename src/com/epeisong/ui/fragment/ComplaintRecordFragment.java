package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import lib.pulltorefresh.PullToRefreshListView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetComplaintCount;
import com.epeisong.data.net.NetComplaintsByPhone;
import com.epeisong.data.net.NetComplaintsList;
import com.epeisong.data.net.NetLogisticsInfo;
import com.epeisong.data.net.parser.ComplaintParser;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq.Builder;
import com.epeisong.model.Complaint;
import com.epeisong.model.User;
import com.epeisong.ui.activity.ComplaintRecordDetailActivity;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 投诉记录
 * 
 * @author gnn
 * 
 */
public class ComplaintRecordFragment extends Fragment implements
		OnClickListener, OnItemClickListener, OnRefreshListener2<ListView> {

	private EditText etSearch;
	private Button btn_search;
	private PullToRefreshListView mPullToRefreshListView;
	private ListView mListView;
	private MyAdapter mAdapter;
	
	private TextView mTextViewEmpty;
    private boolean mLoaded;
    private ReceiveBroadCast receiveBroadCast;
    private int complaintCount;
    private boolean flag = false;
    private List<Complaint> copyList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		copyList = new ArrayList<Complaint>();
		View root = SystemUtils.inflate(R.layout.fragment_members);
		etSearch = (EditText) root.findViewById(R.id.et_search);
		btn_search = (Button) root.findViewById(R.id.btn_search);
		btn_search.setOnClickListener(this);
		mPullToRefreshListView = (PullToRefreshListView) root
				.findViewById(R.id.elv);
		mPullToRefreshListView.setOnRefreshListener(this);
		mListView = mPullToRefreshListView.getRefreshableView();
		// addHeadView();
		mPullToRefreshListView.setAdapter(mAdapter = new MyAdapter());
		mPullToRefreshListView.setMode(Mode.BOTH);
		mListView.setOnItemClickListener(this);
		setEmptyView();
		
		//刷新已处理列表
		receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.epeisong.ui.activity.updateComplaint"); // 只有持有相同的action的接受者才能接收此广播
        getActivity().getApplicationContext().registerReceiver(receiveBroadCast, filter);
		
		return root;
	}
	
	@Override
	public void onAttach(Activity activity) {
		/** 注册广播 */
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        getActivity().getApplicationContext().registerReceiver(receiveBroadCast, filter);
		super.onAttach(activity);
	}
		
    class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Complaint complaint = (Complaint) intent.getSerializableExtra("complaintResult");
        	if(complaint != null){
//        		List<Complaint> c = mAdapter.getAllItem();
//        		for(Complaint result : c){
//        			if(result.equals(complaint)){
//        				return;
//        			}else{
//        				mAdapter.addItem(complaint);
//        			}
//        		}
//        		mAdapter.addItem(complaint);
        		onPullDownToRefresh(mPullToRefreshListView);
        	}
        	
        }
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			if(TextUtils.isEmpty(etSearch.getText().toString())){
				ToastUtils.showToast("请输入搜索内容");
				flag = false;
				return;
			}
			requestSearchData(10, null, etSearch.getText().toString(), null);
			break;
		}
	}
	
	private void requestSearchData(final int limit , final String edgeId , final String number , final String type){
		AsyncTask<Void, Void, List<Complaint>> task = new AsyncTask<Void, Void, List<Complaint>>(){

			@Override
			protected List<Complaint> doInBackground(Void... params) {
				NetComplaintsByPhone net = new NetComplaintsByPhone() {
					
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setLimitCount(limit);
						if (edgeId != null) {
							req.setId(String.valueOf(edgeId));
						}
						req.setMobile(number);
						req.setGetComplainantOrRespondent(Properties.GET_RESPONDENT_ONLY);
						req.setStatus(Properties.COMPLAIN_STATUS_PROCESSED);
						return true;
					}
				};
				try {
					CommonLogisticsResp.Builder resp = net.request();
					if (net.isSuccess(resp)) {
						return ComplaintParser.parseList(resp);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(List<Complaint> result) {
				mPullToRefreshListView.onRefreshComplete();
				List<Complaint> newList = new ArrayList<Complaint>();
				List<String> newName = new ArrayList<String>() ;
				flag = true;
				if (result != null) {
					if (result.size() > 0) {
						if(type != null){
//							newList = copyList;
							newName.clear();
							if(copyList.size() > 0){
								for(int k = 0; k < copyList.size() ; k++){
									newName.add(copyList.get(k).getByNameId());
								}
							}
						}
						for(int i = 0 ; i < result.size() ; i++){
							Complaint c = result.get(i);
							if(type != null){
								if(newList.size() > 0){
									for(int j = 0; j < newList.size() ; j++){
										if(!newName.contains(c.getByNameId())){
											newList.add(c);
											newName.add(c.getByNameId());
										}
									}
								}
							}else{
								if(i == 0){
									newList.add(result.get(0));
									newName.add(result.get(0).getByNameId());
								}
								if(newList.size() > 0){
									for(int j = 0; j < newList.size() ; j++){
										if(!newName.contains(c.getByNameId())){
											newList.add(c);
											newName.add(c.getByNameId());
										}
									}
								}
							}
						}
						copyList.addAll(newList);
//						ToastUtils.showToast(copyList.size()+"");
						if(type != null){
							mAdapter.addAll(newList);
						}else{
							mAdapter.replaceAll(newList);
						}
					} else {
						ToastUtils.showToast("没有搜索到更多");
					}
				}else{
					ToastUtils.showToast("数据异常，请稍后");
				}
			}
			
		};
		task.execute();
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if(!TextUtils.isEmpty(etSearch.getText().toString()) && flag){
			requestSearchData(10, null, etSearch.getText().toString(), null);
		}else{
			requestData(10, null);
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		String edgeId = null;
		if (!mAdapter.isEmpty()) {
			edgeId = mAdapter.getItem(mAdapter.getCount() - 1).getId();
		}
		if(!TextUtils.isEmpty(etSearch.getText().toString()) && flag){
			requestSearchData(10, edgeId, etSearch.getText().toString(),"up");
		}else{
			requestUpData(10, edgeId);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		position -= mPullToRefreshListView.getRefreshableView().getHeaderViewsCount();
		Complaint complaint = mAdapter.getItem(position);
		Intent intent = new Intent(getActivity(), ComplaintRecordDetailActivity.class);
		intent.putExtra(ComplaintRecordDetailActivity.EXTRA_COMPLAINT, complaint);
		startActivity(intent);
	}
	
	private void requestData(final int limit , final String edgeId){
		AsyncTask<Void, Void, List<Complaint>> task = new AsyncTask<Void, Void, List<Complaint>>(){

			@Override
			protected List<Complaint> doInBackground(Void... params) {
				NetComplaintsList net = new NetComplaintsList() {
					
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setLimitCount(limit);
						if (edgeId != null) {
							req.setId(String.valueOf(edgeId));
						}
						req.setStatus(Properties.COMPLAIN_STATUS_PROCESSED);
						return true;
					}
				};
				try {
					CommonLogisticsResp.Builder resp = net.request();
					if (net.isSuccess(resp)) {
						return ComplaintParser.parseList(resp);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(List<Complaint> result) {
				mPullToRefreshListView.onRefreshComplete();
				List<Complaint> newList = new ArrayList<Complaint>();
				if (result != null) {
					if (result.size() > 0) {
						for(int i = 0 ; i<result.size();i++){
							if(i == 0){
								newList.add(result.get(0));
							}else{
								for(Complaint c : newList){
									if(!c.getByNameId().equals(result.get(i).getByNameId())){
										newList.add(result.get(i));
									}
								}
							}
						}
						mAdapter.replaceAll(newList);
					} else {
						ToastUtils.showToast("没有更多投诉内容");
					}
				}else{
					ToastUtils.showToast("数据有问题，请稍后");
				}
			}
			
		};
		task.execute();
	}
	
	private void requestUpData(final int limit , final String edgeId){
		AsyncTask<Void, Void, List<Complaint>> task = new AsyncTask<Void, Void, List<Complaint>>(){

			@Override
			protected List<Complaint> doInBackground(Void... params) {
				NetComplaintsList net = new NetComplaintsList() {
					
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setLimitCount(limit);
						if (edgeId != null) {
							req.setId(String.valueOf(edgeId));
						}
						req.setStatus(Properties.COMPLAIN_STATUS_PROCESSED);
						return true;
					}
				};
				try {
					CommonLogisticsResp.Builder resp = net.request();
					if (net.isSuccess(resp)) {
						return ComplaintParser.parseList(resp);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(List<Complaint> result) {
				mPullToRefreshListView.onRefreshComplete();
				List<Complaint> newList = new ArrayList<Complaint>();
				if (result != null) {
					if (result.size() > 0) {
						for(int i = 0 ; i<result.size();i++){
							if(i == 0){
								newList.add(result.get(0));
							}else{
								for(Complaint c : newList){
									if(!c.getByNameId().equals(result.get(i).getByNameId())){
										newList.add(result.get(i));
									}
								}
							}
						}
						mAdapter.addAll(newList);
					} else {
						ToastUtils.showToast("没有更多投诉内容");
					}
				}else{
					ToastUtils.showToast("数据有问题，请稍后");
				}
			}
			
		};
		task.execute();
	}
	
	private User getUserInfo(final String id) {
        NetLogisticsInfo netInfo = new NetLogisticsInfo() {
            @Override
            protected boolean onSetRequest(Builder req) {
                req.setLogisticsId(Integer.parseInt(id));
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
	
	private int getComplaintCount(final String id){
		NetComplaintCount net = new NetComplaintCount() {
			
			@Override
			protected boolean onSetRequest(Builder req) {
				req.setLogisticsId(Integer.parseInt(id));
                return true;
			}
		};
		try {
            CommonLogisticsResp.Builder resp = net.request();
            if (resp != null) {
                return resp.getCountOfHasBeenComplained();
            }
        } catch (NetGetException e) {
            e.printStackTrace();
        }
        return -1;
	}

	private class MyAdapter extends HoldDataBaseAdapter<Complaint> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils
						.inflate(R.layout.activity_complaint_record_item);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Complaint c = getItem(position);
			holder.fillData(c);
			return convertView;
		}
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
        mTextViewEmpty.setText("没有投诉记录");
        mTextViewEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mTextViewEmpty.setTextColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
        mTextViewEmpty.setGravity(Gravity.CENTER);
        emptyLayout.addView(mTextViewEmpty);
        mPullToRefreshListView.setEmptyView(emptyLayout);
    }
	
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isAdded() && !mLoaded) {
            mPullToRefreshListView.setRefreshing();
            mLoaded = true;
        }
    }

	private class ViewHolder {
		private TextView tv_name;
		private TextView tv_phone;
		private TextView tv_num;

		private void fillData(Complaint c) {
			User user = getUserInfo(c.getByNameId());
			complaintCount = getComplaintCount(c.getByNameId());
			tv_name.setText(c.getByName());
			tv_phone.setText(user.getPhone());
			tv_num.setText(complaintCount + "次");
		}

		private void findView(View v) {
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_phone = (TextView) v.findViewById(R.id.tv_phone);
			tv_num = (TextView) v.findViewById(R.id.tv_num);
		}
	}

}
