package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshListView;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import lib.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.User;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.Task;
import com.epeisong.net.ws.utils.TaskResp;
import com.epeisong.ui.activity.PaymentActivity;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 转账支付列表
 * 
 * @author Jack
 * 
 */
public class TransferWithPayListFragment extends PullToRefreshListFragment implements OnRefreshListener2<ListView>,
OnItemClickListener {
	public static final String PAYMENT_ORDER_TYPE = "order_type";

	private List<AsyncTask<?, ?, ?>> mTasks;
	private PullToRefreshListView mPullToRefreshListView;

	private int mPaymentState = -1;
	ListView mListView = null;  
	MyAdapter mAdapter;
	private boolean mLoaded;
	private TextView mTextViewEmpty;
	private int mPayment_Gua_Fee;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Bundle args = getArguments();
		if (args != null) {
			mPayment_Gua_Fee = args.getInt(PaymentActivity.PAYMENT_TYPE_STRING);
			mPaymentState = args.getInt(PAYMENT_ORDER_TYPE);
		}
		mTasks = new ArrayList<AsyncTask<?, ?, ?>>();
		mPullToRefreshListView = getPullToRefreshListView();
		mPullToRefreshListView.setMode(Mode.BOTH);
		mPullToRefreshListView.setOnRefreshListener(this);
		mListView = mPullToRefreshListView.getRefreshableView();
		mListView.setAdapter(mAdapter = new MyAdapter());
		mListView.setOnItemClickListener(this);

		setEmptyView();

	}

	private void setEmptyView() {
		LinearLayout emptyLayout = new LinearLayout(getActivity());
		emptyLayout.setOrientation(LinearLayout.VERTICAL);
		emptyLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		emptyLayout.setPadding(0, DimensionUtls.getPixelFromDpInt(100), 0, 0);
		mTextViewEmpty = new TextView(getActivity());
		mTextViewEmpty.setText("没有数据");
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

	@Override
	public void onDestroy() {
		for (AsyncTask<?, ?, ?> task : mTasks) {
			task.cancel(true);
		}

		super.onDestroy();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestData(0, 10, Properties.PULL_DOWN_GET_LIST_TYPE_NEWEST);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (mAdapter.isEmpty()) {
			HandlerUtils.postDelayed(new Runnable() {
				@Override
				public void run() {
					mPullToRefreshListView.onRefreshComplete();
				}
			}, 100);
			return;
		}
		int lastSyncIndex = mAdapter.getItem(mAdapter.getCount() - 1).getId();
		requestData(lastSyncIndex, 10, Properties.PULL_GET_LIST_TYPE_OLD);//GET_LIST_WALLET_AUTO_TASK_TYPE_OLD);
	}

	private void requestData(final int lastSyncIndex, final int size, final int type) {
		AsyncTask<Void, Void, TaskResp> task = new AsyncTask<Void, Void, TaskResp>() {
			@Override
			protected TaskResp doInBackground(Void... params) {
				mTasks.add(this);
				ApiExecutor api = new ApiExecutor();
				try {
					User user=UserDao.getInstance().getUser();
					if(mPayment_Gua_Fee==PaymentActivity.PAYMENT_TYPE_GUARANTEE)
						return api.listGuaranteeTask(user.getAccount_name(), 
							SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), mPaymentState, lastSyncIndex, size, type, mPayment_Gua_Fee);
					else 
						return null;
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(TaskResp result) {
				mTasks.remove(this);
				mPullToRefreshListView.onRefreshComplete();
				setListShown(true);
					if (lastSyncIndex > 0) {
						if(result!=null)
							mAdapter.addAll(result.getTaskList());
					} else {
						if(result==null) {
							mAdapter.clear();
						} else {
							mAdapter.replaceAll(result.getTaskList());
						}
					}
			}
		};
		task.execute();
	}

	public void fragmentRefreshList(int index, int status) {
		//if(status==Properties.WALLET_AUTO_TASK_START)
	}

	private class MyAdapter extends HoldDataBaseAdapter<Task> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.activity_payment_task_item);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Task tw = getItem(position);
			holder.fillData(tw);
			return convertView;
		}
	}

	private class ViewHolder {

		TextView tv_name;
		TextView tv_money;
		TextView tv_paystate, tv_time;

		public void findView(View v) {
			tv_paystate = (TextView) v.findViewById(R.id.tv_paystate);
			tv_time = (TextView) v.findViewById(R.id.tv_time);
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_money = (TextView) v.findViewById(R.id.tv_money);

		}

		public void fillData(Task gtask) {
			tv_time.setText(DateUtil.long2YMDHM(gtask.getCreateDate()));
			tv_money.setText(String.valueOf(String.valueOf(gtask.getAmount())));
			tv_name.setText(gtask.getPayerDesc());
			tv_paystate.setText(gtask.getPayeeDesc());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

}
