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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.User;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.WithdrawTask;
import com.epeisong.net.ws.utils.WithdrawTaskResp;
import com.epeisong.ui.activity.TransferWithdrawalDetailActivity;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

public class TransferWithdrawalProcessedListFragment extends Fragment
implements OnRefreshListener2<ListView>, OnItemClickListener{
	public static final String ARGS_TRANSFER_TYPE = "transfer_type";
	private MyAdapter mAdapter;
	private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private TextView mTextViewEmpty;
    private boolean mLoaded;
    private int withdrawalState;
    private List<AsyncTask<?, ?, ?>> mTasks;
    private ReceiveBroadCast receiveBroadCast;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	Bundle args = getArguments();
		if (args != null) {
			withdrawalState = args.getInt(ARGS_TRANSFER_TYPE);
		}
		if (withdrawalState == -1) {
			ToastUtils.showToast("参数传递错误");
		}
         mTasks = new ArrayList<AsyncTask<?, ?, ?>>();
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
        filter.addAction("com.epeisong.ui.activity.refreshWithdraw"); // 只有持有相同的action的接受者才能接收此广播
        this.getActivity().registerReceiver(receiveBroadCast, filter);
    	
    	return ll;
    }
    
    @Override
	public void onAttach(Activity activity) {
		/** 注册广播 */
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        this.getActivity().registerReceiver(receiveBroadCast, filter);
		super.onAttach(activity);
	}
	
    class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	WithdrawTask wt = (WithdrawTask) intent.getSerializableExtra("withdrawlDetail");
        	if(wt != null){
//        		mAdapter.addItem(wt);
//        		onPullDownToRefresh(mPullToRefreshListView);
        		requestData(10, 0, Properties.PULL_DOWN_GET_LIST_TYPE_NEWEST);
        		ToastUtils.showToast("处理成功");
        	}
//        	onPullUpToRefresh(mPullToRefreshListView);
//			mAdapter.notifyDataSetChanged();
        }
    }
    
    @Override
    public void onDestroyView() {
    	super.onDestroyView();
    	if (receiveBroadCast != null) {
            this.getActivity().unregisterReceiver(receiveBroadCast);
        }
        for (AsyncTask<?, ?, ?> task : mTasks) {
            task.cancel(true);
        }
    }
	 
	private void requestData(final int size, final int edgeId , final int type){
		AsyncTask<Void, Void, WithdrawTaskResp> task = new AsyncTask<Void, Void, WithdrawTaskResp>(){

			@Override
			protected WithdrawTaskResp doInBackground(Void... params) {
				mTasks.add(this);
				ApiExecutor api = new ApiExecutor();

				try {
					User user=UserDao.getInstance().getUser();
					String pwd = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null);

					return api.listWithdrawTask(user.getAccount_name(), pwd, edgeId, withdrawalState, type, size);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}
        	
			@Override
			protected void onPostExecute(WithdrawTaskResp result) {
				mTasks.remove(this);
				mPullToRefreshListView.onRefreshComplete();
				//setListShown(true);
				if (null != result && result.getWithdrawTaskList() != null) {
					if(result.getWithdrawTaskList().size() != 0 ){
						mAdapter.replaceAll(result.getWithdrawTaskList());
					}else{
						ToastUtils.showToast("没有更多提现信息");
					}
				}else{
					ToastUtils.showToast("没有更多提现信息");
				}
			}
        };
        task.execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		position -= mListView.getHeaderViewsCount();
		WithdrawTask wt = mAdapter.getItem(position);
		Intent intent = new Intent(getActivity(), TransferWithdrawalDetailActivity.class);
		BusinessChatModel model = BusinessChatModel.getFromWithdraw(wt);
		intent.putExtra(TransferWithdrawalDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
        intent.putExtra(TransferWithdrawalDetailActivity.EXTRA_WITHDRAW, wt);
		startActivity(intent);
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
        mTextViewEmpty.setText("没有提现列表");
        mTextViewEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mTextViewEmpty.setTextColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
        mTextViewEmpty.setGravity(Gravity.CENTER);
        emptyLayout.addView(mTextViewEmpty);
        mPullToRefreshListView.setEmptyView(emptyLayout);
    }
	
    private class MyAdapter extends HoldDataBaseAdapter<WithdrawTask> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.activity_withdrawal_task_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            WithdrawTask tw = getItem(position);
            holder.fillData(tw);
            return convertView;
        }
    }

    private class ViewHolder {
    	LinearLayout ll_parent;
    	TextView tv_name;
    	TextView tv_phone;
    	TextView tv_bank_name;
    	TextView tv_bank_area;
    	TextView tv_money;
    	TextView tv_bank_card;
    	TextView tv_note_content;
    	TextView tv_serial_number;
    	LinearLayout ll_note;
    	

        public void findView(View v) {
        	ll_parent = (LinearLayout) v.findViewById(R.id.ll_parent);
        	tv_name = (TextView) v.findViewById(R.id.tv_name);
        	tv_phone = (TextView) v.findViewById(R.id.tv_phone);
        	tv_bank_name = (TextView) v.findViewById(R.id.tv_bank_name);
        	tv_bank_area = (TextView) v.findViewById(R.id.tv_bank_area);
            tv_money = (TextView) v.findViewById(R.id.tv_money);
            tv_bank_card = (TextView) v.findViewById(R.id.tv_bank_card);
            tv_serial_number = (TextView) v.findViewById(R.id.tv_serial_number);
            tv_note_content = (TextView) v.findViewById(R.id.tv_note_content);
            ll_note = (LinearLayout) v.findViewById(R.id.ll_note);
            ll_note.setVisibility(View.VISIBLE);
        }

        public void fillData(WithdrawTask tw) {
        	String payeeAccount;
        	String accountFormat = "";
        	ll_parent.setBackgroundResource(0);
        	if(tw.getStatus() == Properties.WITHDRAW_TASK_STATUS_PROCESSED){
	        	if(tw.getSubStatus() != null){
					if (tw.getSubStatus() == Properties.WITHDRAW_TASK_SUB_STATUS_PROCESSED_WARNING) {
						ll_parent.setBackgroundResource(R.color.page_red);
					}
				}
        	}
        	payeeAccount = tw.getPayeeAccount();
    		int index = 4;
    		int count = payeeAccount.length()/4;
    		for(int i = 0; i < count ; i++){
    			accountFormat = accountFormat + payeeAccount.substring(0, index)+" "; // 前四个数字之后加空格
    			payeeAccount = payeeAccount.substring(index); //获取前四个之后的数组
    		}
    		accountFormat = accountFormat + payeeAccount;
    		
        	tv_name.setText(tw.getPayeeName());
            tv_bank_name.setText(tw.getBankName());
            tv_bank_area.setText(tw.getBankRegionName());
            tv_money.setText(String.valueOf(tw.getAmount()/100.0)+"元");
            tv_bank_card.setText(accountFormat);
            tv_serial_number.setText(tw.getSerialNumber());
            if(!TextUtils.isEmpty(tw.getNote())){
            	ll_note.setVisibility(View.VISIBLE);
            	tv_note_content.setText(tw.getNote());
            }else{
            	ll_note.setVisibility(View.GONE);
            }
        }
    }
    
    @Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isAdded() && !mLoaded) {
			mPullToRefreshListView.setRefreshing();
			mLoaded = true;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		int edgeId = 0;
		if (!mAdapter.isEmpty()) {
			edgeId = mAdapter.getItem(0).getId();
		}
		requestData(10, edgeId, Properties.PULL_DOWN_GET_LIST_TYPE_NEWEST);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		int edgeId = 0;
		if (!mAdapter.isEmpty()) {
			edgeId = mAdapter.getItem(mAdapter.getCount() - 1).getId();
		}
		pullUp(10, edgeId, Properties.PULL_GET_LIST_TYPE_OLD);
		
	}
	
	private void pullUp(final int size, final int edgeId , final int type){
		AsyncTask<Void, Void, WithdrawTaskResp> task = new AsyncTask<Void, Void, WithdrawTaskResp>(){

			@Override
			protected WithdrawTaskResp doInBackground(Void... params) {
				mTasks.add(this);
				ApiExecutor api = new ApiExecutor();

				try {
					User user=UserDao.getInstance().getUser();
					String pwd = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null);

					return api.listWithdrawTask(user.getAccount_name(), pwd, edgeId, withdrawalState, type, size);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}
        	
			@Override
			protected void onPostExecute(WithdrawTaskResp result) {
				mTasks.remove(this);
				mPullToRefreshListView.onRefreshComplete();
				if (null != result && result.getWithdrawTaskList() != null) {
					if(result.getWithdrawTaskList().size() != 0 ){
						mAdapter.addAll(result.getWithdrawTaskList());
					}else{
						ToastUtils.showToast("没有更多提现信息");
					}
				}else{
					ToastUtils.showToast("没有更多提现信息");
				}
			}
        };
        task.execute();
	}

}
