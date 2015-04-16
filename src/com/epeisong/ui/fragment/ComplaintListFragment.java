package com.epeisong.ui.fragment;

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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.data.net.NetComplaintsList;
import com.epeisong.data.net.parser.ComplaintParser;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq.Builder;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.Complaint;
import com.epeisong.ui.activity.ComplaintDealDetailActivity;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
/**
 * 投诉处理列表（未处理）
 * @author gnn
 *
 */
public class ComplaintListFragment extends Fragment
implements OnRefreshListener2<ListView>, OnItemLongClickListener , OnItemClickListener{
	private MyAdapter mAdapter;
	private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private TextView mTextViewEmpty;
    private boolean mLoaded;
    private ReceiveBroadCast receiveBroadCast;
    private Complaint c;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout ll = new LinearLayout(getActivity());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		mPullToRefreshListView = new PullToRefreshListView(getActivity());
		mPullToRefreshListView.setLayoutParams(lp);
		ll.addView(mPullToRefreshListView);

		mPullToRefreshListView.setOnRefreshListener(this);
		mListView = mPullToRefreshListView.getRefreshableView();
		// addHeadView();
		mPullToRefreshListView.setAdapter(mAdapter = new MyAdapter());
		mPullToRefreshListView.setMode(Mode.BOTH);
		mPullToRefreshListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		setEmptyView();
		
		//刷新已处理列表
		receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.epeisong.ui.activity.updateComplaint"); // 只有持有相同的action的接受者才能接收此广播
        getActivity().getApplicationContext().registerReceiver(receiveBroadCast, filter);
        
		return ll;
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
        		mAdapter.removeItem(c);
//        		onPullDownToRefresh(mPullToRefreshListView);
        		ToastUtils.showToast("处理成功");
        	}
        	
        }
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if (receiveBroadCast != null) {
            getActivity().getApplicationContext().unregisterReceiver(receiveBroadCast);
        }
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
						req.setStatus(Properties.COMPLAIN_STATUS_UN_PROCESSED);
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
				if (result != null) {
					if (result.size() > 0) {
						mAdapter.replaceAll(result);
					} else {
						ToastUtils.showToast("没有更多投诉内容");
					}
				}else{
					ToastUtils.showToast("没有更多投诉内容");
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
						req.setStatus(Properties.COMPLAIN_STATUS_UN_PROCESSED);
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
				if (result != null) {
					if (result.size() > 0) {
						mAdapter.addAll(result);
					} else {
						ToastUtils.showToast("没有更多投诉内容");
					}
				}else{
					ToastUtils.showToast("没有更多投诉内容");
				}
			}
			
		};
		task.execute();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
//		position -= mPullToRefreshListView.getRefreshableView()
//				.getHeaderViewsCount();
//		TextView tv_name;
//		TextView tv_delete;
//		TextView tv_sign;
//		final Complaint c = mAdapter.getItem(position);
//		if (c != null) {
//			final AlertDialog builder = new AlertDialog.Builder(getActivity())
//					.create();
//			builder.show();
//			Window window = builder.getWindow();
//			window.setContentView(R.layout.members_dialog);
//			tv_name = (TextView) window.findViewById(R.id.tv_name);
//			tv_delete = (TextView) window.findViewById(R.id.tv_delete);
//			tv_sign = (TextView) window.findViewById(R.id.tv_sign);
//			builder.setCanceledOnTouchOutside(true);
//			tv_name.setText(c.getByName());
//			tv_delete.setText("删除");
//			tv_sign.setText("处理完成");
//			tv_delete.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					builder.dismiss();
//				}
//			});
//			tv_sign.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// EditTagActivity.launch(getActivity(), u);
//				}
//			});
//			// dialog.show();
//			return true;
//		}
//
		return false;
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
        mTextViewEmpty.setText("没有未处理投诉信息");
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

	private class MyAdapter extends HoldDataBaseAdapter<Complaint> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils
						.inflate(R.layout.complaint_list_item);
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

	private class ViewHolder {

		TextView tv_name;
		TextView tv_phone;
		TextView tv_complaint_name;
		TextView tv_complaint_phone;

		public void findView(View v) {
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_phone = (TextView) v.findViewById(R.id.tv_phone);
			tv_phone.setVisibility(View.GONE);
			tv_complaint_name = (TextView) v.findViewById(R.id.tv_complaint_name);
			tv_complaint_phone = (TextView) v.findViewById(R.id.tv_complaint_phone);
			tv_complaint_phone.setVisibility(View.GONE);
		}

		public void fillData(Complaint c) {
			tv_name.setText(c.getByName());
//			tv_phone.setText("("+c.getByPhone()+")");
			tv_complaint_name.setText(c.getName());
//			tv_complaint_phone.setText("("+c.getPhone()+")");
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestData(10, null);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		String edgeId = null;
		if (!mAdapter.isEmpty()) {
			edgeId = mAdapter.getItem(mAdapter.getCount() - 1).getId();
		}
		requestUpData(10, edgeId);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		position -= mPullToRefreshListView.getRefreshableView().getHeaderViewsCount();
		c = mAdapter.getItem(position);
		Intent intent = new Intent(getActivity(), ComplaintDealDetailActivity.class);
		intent.putExtra(ComplaintDealDetailActivity.EXTRA_COMPLAINT, c);
		BusinessChatModel model = BusinessChatModel.getFromComplaint(c);
        intent.putExtra(ComplaintDealDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
		startActivity(intent);
	}

}
