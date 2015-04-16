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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.net.NetCustomServiceTask;
import com.epeisong.data.net.parser.CustomServiceParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.CustomServiceTask.CustomServiceTaskReq.Builder;
import com.epeisong.logistics.proto.CustomServiceTask.CustomServiceTaskResp;
import com.epeisong.model.User;
import com.epeisong.net.ws.utils.CustomServiceTask;
import com.epeisong.ui.activity.CustomerProblemDetailActivity;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

public class CustomerProblemCancelFragment extends Fragment implements OnRefreshListener2<ListView> , OnItemClickListener {
	private MyAdapter mAdapter;
	private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private boolean mLoaded;

    private TextView mTextViewEmpty;
    private User mUser;
    private ReceiveBroadCast receiveBroadCast;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	mUser = UserDao.getInstance().getUser();
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
		setEmptyView();
		
		//刷新已处理列表
		receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.epeisong.ui.activity.CustomServiceCancel"); // 只有持有相同的action的接受者才能接收此广播
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
        	CustomServiceTask custom = (CustomServiceTask) intent.getSerializableExtra("CustomServiceCancel");
        	if(custom != null){
//        		mAdapter.addItem(custom);
        		onPullDownToRefresh(mPullToRefreshListView);
        		ToastUtils.showToast("更新成功");
        	}
        }
    }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (receiveBroadCast != null) {
            getActivity().getApplicationContext().unregisterReceiver(receiveBroadCast);
        }
	}

	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isAdded() && !mLoaded) {
            mPullToRefreshListView.setRefreshing();
            mLoaded = true;
        }
    }
	
	private void requestData(final int lessSyncIndex, final int thanSyncIndex , final int size){
		AsyncTask<Void, Void, List<CustomServiceTask>> task = new AsyncTask<Void, Void, List<CustomServiceTask>>(){

			@Override
			protected List<CustomServiceTask> doInBackground(Void... params) {
				NetCustomServiceTask net = new NetCustomServiceTask() {
					
					@Override
					protected int getCommandCode() {
						return CommandConstants.LIST_CUSTOM_SERVICE_TASK_REQ;
					}
					
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setLogisticsId(Integer.parseInt(mUser.getId()));
						req.setStatus(Properties.CUSTOM_SERVICE_TASK_STATUS_CANCEL);
						req.setCount(size);
						req.setThanSyncIndex(thanSyncIndex);
						req.setLessSyncIndex(lessSyncIndex);
						return true;
					}
				};
				try {
					CustomServiceTaskResp.Builder resp = net.request();
					if(net.isSuccess(resp)){
						return CustomServiceParser.parseList(resp);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(List<CustomServiceTask> result) {
				super.onPostExecute(result);
				mPullToRefreshListView.onRefreshComplete();
				if (result != null) {
					if (result.size() > 0) {
						mAdapter.replaceAll(result);
					} else {
//						ToastUtils.showToast("没有更多已取消问题");
					}
				}else{
					ToastUtils.showToast("数据异常");
				}
			}
		};
		task.execute();
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
        mTextViewEmpty.setText("没有已取消问题");
        mTextViewEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mTextViewEmpty.setTextColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
        mTextViewEmpty.setGravity(Gravity.CENTER);
        emptyLayout.addView(mTextViewEmpty);
        mPullToRefreshListView.setEmptyView(emptyLayout);
    }

	private class MyAdapter extends HoldDataBaseAdapter<CustomServiceTask> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils
						.inflate(R.layout.customer_problem_item);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			CustomServiceTask cp = getItem(position);
			holder.fillData(cp);
			return convertView;
		}
	}

	private class ViewHolder {
		LinearLayout ll_type;
		TextView tv_type;
		TextView tv_name;
		TextView tv_phone;
		TextView tv_content;
		TextView tv_tel;
		TextView tv_time;

		public void findView(View v) {
			tv_time = (TextView) v.findViewById(R.id.tv_time);
			ll_type = (LinearLayout) v.findViewById(R.id.ll_type);
			ll_type.setVisibility(View.VISIBLE);
			tv_type = (TextView) v.findViewById(R.id.tv_type);
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_phone = (TextView) v.findViewById(R.id.tv_phone);
			tv_content = (TextView) v.findViewById(R.id.tv_content);
			tv_tel = (TextView) v.findViewById(R.id.tv_tel);
		}

		public void fillData(CustomServiceTask cp) {
			if(cp.getType() == 1){
				tv_type.setText("账号安全");
			}else if(cp.getType() == 2){
				tv_type.setText("业务问题");
			}
			tv_name.setText(cp.getUserName());
			tv_phone.setText("("+ cp.getUserAccount() +")");
			tv_content.setText(cp.getDetail());
			tv_tel.setText(cp.getContactTel());
			tv_time.setText(DateUtil.date2MDHM(cp.getCreateDate()));
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestData(0, 0, 10);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		int lastSyncIndex = 0;
        if (!mAdapter.isEmpty()) {
            lastSyncIndex = mAdapter.getItem(mAdapter.getCount() - 1).getSyncIndex();
        }
        if (lastSyncIndex <= 1) {
            HandlerUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPullToRefreshListView.onRefreshComplete();
                }
            }, 100);
            return;
        }
        requestData(lastSyncIndex , 0, 10);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		position -= mListView.getHeaderViewsCount();
		CustomServiceTask ct = mAdapter.getItem(position);
		Intent intent = new Intent(getActivity(), CustomerProblemDetailActivity.class);
		intent.putExtra(CustomerProblemDetailActivity.EXTRA_CUSTOMER_PROBLEM, ct);
		intent.putExtra("flag", "flag");
		startActivity(intent);
	}

}
