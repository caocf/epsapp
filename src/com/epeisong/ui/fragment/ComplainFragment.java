package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import lib.pulltorefresh.PullToRefreshListView;
import lib.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.parser.ComplainTaskParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.ComplainTask.ComplainTaskReq.Builder;
import com.epeisong.logistics.proto.ComplainTask.ComplainTaskResp;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.GuaComplainTask;
import com.epeisong.model.InfoFee;
import com.epeisong.net.request.NetComplainTask;
import com.epeisong.ui.activity.GuaCompDetailActivity;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.ReleaseLog;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 申诉订单列表
 * @author Jack
 *
 */
public class ComplainFragment extends PullToRefreshListFragment implements OnRefreshListener2<ListView>,
        OnItemClickListener {

    public static final String ARGS_COMPLAINT_TYPE = "complaint_type";

    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private MyAdapter mAdapter;
    //private User mUserSelf;

    private TextView mTextViewEmpty;

    private int mComplaintState = -1;

    private boolean mLoaded;
    private ReceiveBroadCast receiveBroadCast;
    private List<AsyncTask<?, ?, ?>> mTasks;
    private GuaComplainTask mGuaComplainTask;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
        	mComplaintState = args.getInt(ARGS_COMPLAINT_TYPE);
        }
        if (mComplaintState == -1) {
            ToastUtils.showToast("参数传递错误");
        }
        mTasks = new ArrayList<AsyncTask<?, ?, ?>>();
        //mUserSelf = UserDao.getInstance().getUser();
        mPullToRefreshListView = getPullToRefreshListView();
        mPullToRefreshListView.setMode(Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(this);
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.setAdapter(mAdapter = new MyAdapter());
        mListView.setOnItemClickListener(this);

        setEmptyView();

//        View v = getView().findViewById(0x00ff0002);
//        if (v != null && v instanceof ViewGroup) {
//            View child = ((ViewGroup) v).getChildAt(0);
//            if (child != null && child instanceof ProgressBar) {
//                ((ProgressBar) child).setProgress(android.R.attr.progressBarStyleSmall);
//            }
//        }
        
        
		//刷新已处理列表
		receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.epeisong.ui.activity.updateGuaComplain"); // 只有持有相同的action的接受者才能接收此广播
        getActivity().getApplicationContext().registerReceiver(receiveBroadCast, filter);

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
        	GuaComplainTask guaComplainTask = (GuaComplainTask) intent.getSerializableExtra("guaComplaintResult");
        	if(guaComplainTask != null){
//        		mAdapter.removeItem(mGuaComplainTask);
        		onPullDownToRefresh(mPullToRefreshListView);
        		//ToastUtils.showToast("处理成功");
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
    
    @Override
    public void onDestroyView() {
        for (AsyncTask<?, ?, ?> task : mTasks) {
            task.cancel(true);
        }
        super.onDestroyView();
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
        mTextViewEmpty.setText("没有申诉");
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= mListView.getHeaderViewsCount();
        mGuaComplainTask = mAdapter.getItem(position);
        //GuaComplainTask guaComplainTask = mAdapter.getItem(position);
        Intent i = new Intent(getActivity(), GuaCompDetailActivity.class);
       // i.putExtra(InfoFeeDetailActivity.EXTRA_INFO_FEE, infoFee);
        BusinessChatModel model = BusinessChatModel.getFromInfoFee(mGuaComplainTask.getInfoFee());
        i.putExtra(GuaCompDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
        i.putExtra(GuaCompDetailActivity.EXTRA_GUA_COMPLAIN_TASK, mGuaComplainTask);
        i.putExtra(ARGS_COMPLAINT_TYPE, mComplaintState);
        startActivity(i);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        requestData(0, 10);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        int lastSyncIndex = 0;
        if (!mAdapter.isEmpty()) {
            lastSyncIndex = mAdapter.getItem(mAdapter.getCount() - 1).getComplainTask().getSyncIndex();
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
        requestData(lastSyncIndex, 10);
    }

    private void requestData(final int lastSyncIndex, final int size) {
        AsyncTask<Void, Void, List<GuaComplainTask>> task = new AsyncTask<Void, Void, List<GuaComplainTask>>() {
            @Override
            protected List<GuaComplainTask> doInBackground(Void... params) {
            	NetComplainTask net = new NetComplainTask() {

					@Override
					protected int getCommandCode() {
						return CommandConstants.LIST_COMPLAIN_TASK_REQ;
					}

					@Override
					protected void setRequest(Builder req) {
						//req.setTaskId(Integer.valueOf(mUserSelf.getId()));
		                req.setCount(10);//size);
		                req.setStatus(mComplaintState);
		                req.setThanSyncIndex(0);//lastSyncIndex);
		                req.setLessSyncIndex(lastSyncIndex);
					}

            	};
            	
        		try {
        			ComplainTaskResp.Builder resp = net.request();
        			if (resp == null) {
        				return null;
        			}
        			List<GuaComplainTask> result = ComplainTaskParser.parseList(resp);

        			return result;
        		} catch (NetGetException e) {
        			e.printStackTrace();
        			ReleaseLog.log("LIST_COMPLAIN_TASK_REQ", e);
        			ToastUtils.showToastInThread("网路异常");
        		}
        		return null;        
            }
            	
            @Override
            protected void onPostExecute(List<GuaComplainTask> result) {
                mTasks.remove(this);
                mPullToRefreshListView.onRefreshComplete();
                setListShown(true);
                if (lastSyncIndex > 0) {
                    mAdapter.addAll(result);
                } else {
                    mAdapter.replaceAll(result);
                }
            }
        };
        task.execute();
    }

    private class MyAdapter extends HoldDataBaseAdapter<GuaComplainTask> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.fragment_complaintlist_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            GuaComplainTask infoFee = getItem(position);
            holder.fillData(infoFee);
            return convertView;
        }
    }

    private class ViewHolder {

        ImageView iv_icon;
        TextView tv_region;
        TextView tv_time;
        TextView tv_desc;
        //TextView tv_source;
        //TextView tv_status;

        public void findView(View v) {
            iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
            tv_region = (TextView) v.findViewById(R.id.tv_region);
            tv_time = (TextView) v.findViewById(R.id.tv_time);
            tv_desc = (TextView) v.findViewById(R.id.tv_desc);
            //tv_source = (TextView) v.findViewById(R.id.tv_source);
            //tv_status = (TextView) v.findViewById(R.id.tv_status);
        }

        public void fillData(GuaComplainTask guaComplainTask) {
        	InfoFee infoFee = guaComplainTask.getInfoFee();
            if (infoFee.getType() == Properties.INFO_FEE_TYPE_GOODS) {
                iv_icon.setImageResource(R.drawable.black_board_goods);
            } else if (infoFee.getType() == Properties.INFO_FEE_TYPE_VEHICLE) {
                iv_icon.setImageResource(R.drawable.black_board_truck);
            }
            tv_region.setText(infoFee.getFreightAddr());
            tv_time.setText(DateUtil.long2vaguehourMinute(infoFee.getCreateDate()));
            tv_desc.setText(infoFee.getFreightInfo());

            //String source = complainTask.getSourceStr(mUserSelf.getId());
            //tv_source.setText(source);
            //infoFee.setViews(mUserSelf.getId(), null, tv_status);
        }
    }
}
