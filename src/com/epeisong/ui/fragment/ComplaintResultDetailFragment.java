package com.epeisong.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetComplaintById;
import com.epeisong.data.net.NetLogisticsInfo;
import com.epeisong.data.net.NetUpdateComplaint;
import com.epeisong.data.net.parser.ComplaintParser;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.logistics.proto.Eps.LogisticsReq.Builder;
import com.epeisong.model.Complaint;
import com.epeisong.model.User;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
/**
 * 投诉处理页面
 * @author gnn
 *
 */
public class ComplaintResultDetailFragment extends Fragment implements OnClickListener {
	public static final String ARG_BUSINESS_ID = "business_id";
	public static final String EXTRA_WHETHER_CUSTOMER = "whether_customer"; //是否是客服登录
	private Complaint complaint;
	private String businessId;
	private String mWhetherCustomer;
	
	private TextView tv_by_name;
	private TextView tv_by_phone;
	private TextView tv_name;
	private TextView tv_phone;
	private TextView tv_content;
	private TextView et_result;
	private Button btn_delete;
	private Button btn_complete;
	private RelativeLayout rl_result;
	
	private User byUser;
	private User user;
	private User mUser;
	
	private String desc;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		if(args != null){
			complaint = (Complaint) args.getSerializable("complaint");
			businessId = args.getString(ARG_BUSINESS_ID);
			mWhetherCustomer = args.getString(EXTRA_WHETHER_CUSTOMER);
		}
		View root = SystemUtils.inflate(R.layout.activity_complaint_deal_detail);
		rl_result = (RelativeLayout) root.findViewById(R.id.rl_result);
		tv_by_name = (TextView) root.findViewById(R.id.tv_by_name);
		tv_by_phone = (TextView) root.findViewById(R.id.tv_by_phone);
		tv_name = (TextView) root.findViewById(R.id.tv_name);
		tv_phone = (TextView) root.findViewById(R.id.tv_phone);
		tv_content = (TextView) root.findViewById(R.id.tv_content);
		et_result = (TextView) root.findViewById(R.id.et_result);
		btn_delete = (Button) root.findViewById(R.id.btn_delete);
		btn_complete = (Button) root.findViewById(R.id.btn_complete);
		btn_delete.setOnClickListener(this);
		btn_complete.setOnClickListener(this);
		if(complaint == null){
			getComplaintById(businessId);
		}else{
			tv_by_name.setText(complaint.getByName());
			tv_name.setText(complaint.getName());
			tv_content.setText(complaint.getContent());
			if(!TextUtils.isEmpty(complaint.getByNameId())){
				getUserInfo(complaint.getByNameId());
				if(mUser != null){
					tv_by_phone.setText(byUser.getPhone());
				}
			}
			if(!TextUtils.isEmpty(complaint.getNameId())){
				getUserInfo(complaint.getNameId());
				if(mUser != null){
					tv_phone.setText(user.getPhone());
				}
			}
		}
		if(mWhetherCustomer != null){
			rl_result.setVisibility(View.GONE);
			btn_complete.setVisibility(View.GONE);
		}
		return root;
	}
	
	private void getComplaintById(final String id) {
		AsyncTask<Void, Void, Complaint> task = new AsyncTask<Void, Void, Complaint>(){
			@Override
			protected Complaint doInBackground(Void... params) {
				NetComplaintById net = new NetComplaintById() {
		            @Override
		            protected boolean onSetRequest(LogisticsReq.Builder req) {
		                req.setId(id);
		                return true;
		            }
		        };
		        try {
		            CommonLogisticsResp.Builder resp = net.request();
		            if (resp != null) {
		                return ComplaintParser.parseSingleComplaint(resp);
		            }
		        } catch (NetGetException e) {
		            e.printStackTrace();
		            return null;
		        }
		        return null;
			}
			@Override
			protected void onPostExecute(Complaint result) {
				super.onPostExecute(result);
				if(result != null){
					complaint = result;
					if(complaint != null){
						tv_by_name.setText(complaint.getByName());
						tv_name.setText(complaint.getName());
						tv_content.setText(complaint.getContent());
						if(!TextUtils.isEmpty(complaint.getByNameId())){
							getUserInfo(complaint.getByNameId());
							if(mUser != null){
								tv_by_phone.setText(byUser.getPhone());
							}
						}
						if(!TextUtils.isEmpty(complaint.getNameId())){
							getUserInfo(complaint.getNameId());
							if(mUser != null){
								tv_phone.setText(user.getPhone());
							}
						}
					}
				}
			}
		};
		task.execute();
    }
	
	private void getUserInfo(final String id) {
		AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>(){
			@Override
			protected User doInBackground(Void... params) {
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
			@Override
			protected void onPostExecute(User result) {
				super.onPostExecute(result);
				if(result != null){
					mUser = result;
				}
			}
		};
		task.execute();
    }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_delete:
			ToastUtils.showToast("删除按钮");
			break;
		case R.id.btn_complete:
			final String resultContent = et_result.getText().toString();
			if(TextUtils.isEmpty(resultContent)){
				ToastUtils.showToast("请输入处理结果");
				return;
			}
			
			((XBaseActivity)getActivity()).showPendingDialog(null);
			AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>(){

				@Override
				protected Boolean doInBackground(Void... params) {
					NetUpdateComplaint net = new NetUpdateComplaint() {
						
						@Override
						protected boolean onSetRequest(Builder req) {
							req.setId(complaint.getId());
							req.setStatus(Properties.COMPLAIN_STATUS_PROCESSED);
							req.setResult(resultContent);
							return true;
						}
					};
					try {
						CommonLogisticsResp.Builder resp = net.request();
			            if (resp != null && "SUCC".equals(resp.getResult())) {
			                return true;
			            }else{
			            	desc = resp.getDesc();
			            	return false;
			            }
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
				
				@Override
				protected void onPostExecute(Boolean result) {
					((XBaseActivity)getActivity()).dismissPendingDialog();
					super.onPostExecute(result);
					if(result != null){
						if(result){
							getActivity().finish();
							Intent intent = new Intent("com.epeisong.ui.activity.updateComplaint");
							intent.putExtra("complaintResult", complaint);
							getActivity().sendBroadcast(intent); // 发送广播
						}else{
							ToastUtils.showToast(desc);
						}
					}else{
						ToastUtils.showToast(desc);
					}
					
				}
			};
			task.execute();
			break;

		default:
			break;
		}
	}

}
