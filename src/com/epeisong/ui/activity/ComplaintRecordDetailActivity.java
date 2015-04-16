package com.epeisong.ui.activity;

import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import lib.pulltorefresh.PullToRefreshListView;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetComplaintCount;
import com.epeisong.data.net.NetComplaintsListPeopleThat;
import com.epeisong.data.net.NetLogisticsInfo;
import com.epeisong.data.net.parser.ComplaintParser;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq.Builder;
import com.epeisong.model.Complaint;
import com.epeisong.model.User;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 投诉记录详情
 * @author gnn
 *
 */
public class ComplaintRecordDetailActivity extends BaseActivity implements OnRefreshListener2<ListView> , OnClickListener {
	public static final String EXTRA_COMPLAINT = "complaint";
	public static final String EXTRA_COUNT = "count";
	private Complaint mComplaint;
	private RelativeLayout rl_top;
	private TextView tv_name;
	private TextView tv_phone;
	private TextView tv_num;
	private PullToRefreshListView mPullToRefreshListView;
//	private ListView mListView;
	private MyAdapter mAdapter;
	private TextView mTextViewEmpty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mComplaint = (Complaint) getIntent().getSerializableExtra(EXTRA_COMPLAINT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complaint_detail);
		rl_top = (RelativeLayout) findViewById(R.id.rl_top);
		rl_top.setOnClickListener(this);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_num = (TextView) findViewById(R.id.tv_num);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.elv);
		mPullToRefreshListView.setOnRefreshListener(this);
//		mListView = mPullToRefreshListView.getRefreshableView();
		mPullToRefreshListView.setAdapter(mAdapter = new MyAdapter());
		setEmptyView();
		requestData(10, null);
		tv_name.setText(mComplaint.getByName());
		tv_phone.setText(getUserInfo(mComplaint.getByNameId()).getPhone());
		tv_num.setText(getComplaintCount(mComplaint.getByNameId()) + "次");
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

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "记录详情", null).setShowLogo(false);
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
	
	private void requestData(final int limit , final String edgeId){
		AsyncTask<Void, Void, List<Complaint>> task = new AsyncTask<Void, Void, List<Complaint>>(){

			@Override
			protected List<Complaint> doInBackground(Void... params) {
				NetComplaintsListPeopleThat net = new NetComplaintsListPeopleThat() {
					
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setLimitCount(limit);
						if (edgeId != null) {
							req.setId(String.valueOf(edgeId));
						}
						req.setIsManageOthers(true);
						req.setLogisticsId(Integer.parseInt(mComplaint.getByNameId()));
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
						ToastUtils.showToast("没有更多投诉者");
					}
				}else{
					ToastUtils.showToast("没有更多投诉者");
				}
			}
			
		};
		task.execute();
	}
	
	private void requestUpData(final int limit , final String edgeId){
		AsyncTask<Void, Void, List<Complaint>> task = new AsyncTask<Void, Void, List<Complaint>>(){

			@Override
			protected List<Complaint> doInBackground(Void... params) {
				NetComplaintsListPeopleThat net = new NetComplaintsListPeopleThat() {
					
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setLimitCount(limit);
						if (edgeId != null) {
							req.setId(String.valueOf(edgeId));
						}
						req.setIsManageOthers(true);
						req.setLogisticsId(Integer.parseInt(mComplaint.getByNameId()));
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
						ToastUtils.showToast("没有更多投诉者");
					}
				}else{
					ToastUtils.showToast("没有更多投诉者");
				}
			}
			
		};
		task.execute();
	}
	
	private void setEmptyView() {
        LinearLayout emptyLayout = new LinearLayout(this);
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyLayout.setPadding(0, DimensionUtls.getPixelFromDpInt(100), 0, 0);
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.nopeihuo);
        emptyLayout.addView(iv);
        mTextViewEmpty = new TextView(this);
        mTextViewEmpty.setText("没有未处理投诉信息");
        mTextViewEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mTextViewEmpty.setTextColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
        mTextViewEmpty.setGravity(Gravity.CENTER);
        emptyLayout.addView(mTextViewEmpty);
        mPullToRefreshListView.setEmptyView(emptyLayout);
    }
	
	private class MyAdapter extends HoldDataBaseAdapter<Complaint> {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils
						.inflate(R.layout.activity_complaint_record_deal_item);
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
		TextView tv_result;

		public void fillData(Complaint complaint) {
			User user = getUserInfo(complaint.getNameId());
			if(TextUtils.isEmpty(user.getContacts_phone())){
				tv_name.setText(complaint.getName() + "：" + complaint.getContent());
			}else{
				tv_name.setText(complaint.getName() + "(" + user.getContacts_phone() + ")：" + complaint.getContent());
			}
			tv_result.setText("处理结果：" + complaint.getResult());
		}

		public void findView(View v) {
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_result = (TextView) v.findViewById(R.id.tv_result);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_top:
			User user = getUserInfo(mComplaint.getByNameId());
			Intent intent = new Intent(this, ContactsDetailActivity.class);
            intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, user.getId());
            intent.putExtra(ContactsDetailActivity.EXTRA_SHOW_PAGE_COUNT_OTHER, 1);
            intent.putExtra(ContactsDetailActivity.EXTRA_USER, user);
            startActivity(intent);
			break;

		default:
			break;
		}
	}

}
