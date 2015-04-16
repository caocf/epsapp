package com.epeisong.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.net.NetCustomServiceTask;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.CustomServiceTask.CustomServiceTaskResp;
import com.epeisong.logistics.proto.CustomServiceTask.CustomServiceTaskReq.Builder;
import com.epeisong.net.ws.utils.CustomServiceTask;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

public class CustomerProblemDetailActivity extends BaseActivity implements
		OnClickListener {
	public static final String EXTRA_CUSTOMER_PROBLEM = "extra_customer";
	private TextView tv_type;
	private TextView tv_name;
	private TextView tv_account;
	private TextView tv_detail;
	private TextView tv_phone;
	private LinearLayout ll_result;
	private TextView tv_result;
	private RelativeLayout rl_result;
	private EditText et_result;
	private LinearLayout ll_btn;
	private Button btn_cancel;
	private Button btn_complete;
	private TextView tv_create_time;
	private TextView tv_result_time;

	private CustomServiceTask mCustom;
	private String resultDesc;
	private String flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mCustom = (CustomServiceTask) getIntent().getSerializableExtra(EXTRA_CUSTOMER_PROBLEM);
		flag = getIntent().getStringExtra("flag");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_problem_detail);
		tv_create_time = (TextView) findViewById(R.id.tv_create_time);
		tv_result_time = (TextView) findViewById(R.id.tv_result_time);
		tv_type = (TextView) findViewById(R.id.tv_type);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_account = (TextView) findViewById(R.id.tv_account);
		tv_detail = (TextView) findViewById(R.id.tv_detail);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		ll_result = (LinearLayout) findViewById(R.id.ll_result);
		tv_result = (TextView) findViewById(R.id.tv_result);
		rl_result = (RelativeLayout) findViewById(R.id.rl_result);
		et_result = (EditText) findViewById(R.id.et_result);
		ll_btn = (LinearLayout) findViewById(R.id.ll_btn);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_complete = (Button) findViewById(R.id.btn_complete);

		if (mCustom != null) {
			if (mCustom.getType() == 1) {
				tv_type.setText("账号安全");
			}else if(mCustom.getType() == 2){
				tv_type.setText("业务问题");
			}
			tv_name.setText(mCustom.getUserName());
			tv_account.setText("(" + mCustom.getUserAccount() + ")：");
			tv_detail.setText(mCustom.getDetail());
			tv_phone.setText(mCustom.getContactTel());
			tv_result.setText(mCustom.getResult());
			tv_result_time.setText(DateUtil.date2YMDHMSS(mCustom.getUpdateDate()));
			tv_create_time.setText(DateUtil.date2YMDHMSS(mCustom.getCreateDate()));
		}

		if (!TextUtils.isEmpty(flag)) {
			ll_result.setVisibility(View.VISIBLE);
			rl_result.setVisibility(View.GONE);
			ll_btn.setVisibility(View.GONE);
		}

		btn_cancel.setOnClickListener(this);
		btn_complete.setOnClickListener(this);

	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "用户问题详情", null)
				.setShowLogo(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			if (TextUtils.isEmpty(et_result.getText().toString())) {
				ToastUtils.showToast("请输入处理结果");
				return;
			}
			AsyncTask<Void, Void, Boolean> taskCancel = new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(Void... params) {
					NetCustomServiceTask net = new NetCustomServiceTask() {
						@Override
						protected int getCommandCode() {
							// TODO Auto-generated method stub
							return CommandConstants.UPDATE_CUSTOM_SERVICE_TASK_REQ;
						}

						@Override
						protected boolean onSetRequest(Builder req) {
							req.setTaskId(mCustom.getId());
							req.setStatus(Properties.CUSTOM_SERVICE_TASK_STATUS_CANCEL);
							req.setResult(et_result.getText().toString());
							return true;
						}
					};
					try {
						CustomServiceTaskResp.Builder resp = net.request();
						if (net.isSuccess(resp)) {
							return true;
						} else {
							resultDesc = resp.getDesc();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}

				@Override
				protected void onPostExecute(Boolean result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					if (result) {
						Intent intent = new Intent("com.epeisong.ui.activity.CustomServiceCancel");
						intent.putExtra("CustomServiceCancel", mCustom);
						sendBroadcast(intent); // 发送广播
						finish();
					} else {
						if (!TextUtils.isEmpty(resultDesc)) {
							ToastUtils.showToast(resultDesc);
						} else {
							ToastUtils.showToast("取消问题失败");
						}
					}
				}
			};
			taskCancel.execute();
			break;

		case R.id.btn_complete:
			if (TextUtils.isEmpty(et_result.getText().toString())) {
				ToastUtils.showToast("请输入处理结果");
				return;
			}
			AsyncTask<Void, Void, Boolean> taskComplete = new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(Void... params) {
					NetCustomServiceTask net = new NetCustomServiceTask() {
						@Override
						protected int getCommandCode() {
							// TODO Auto-generated method stub
							return CommandConstants.UPDATE_CUSTOM_SERVICE_TASK_REQ;
						}

						@Override
						protected boolean onSetRequest(Builder req) {
							req.setTaskId(mCustom.getId());
							req.setStatus(Properties.CUSTOM_SERVICE_TASK_STATUS_PROCESSED);
							req.setResult(et_result.getText().toString());
							return true;
						}
					};
					try {
						CustomServiceTaskResp.Builder resp = net.request();
						if (net.isSuccess(resp)) {
							return true;
						} else {
							resultDesc = resp.getDesc();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}

				@Override
				protected void onPostExecute(Boolean result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					if (result) {
						Intent intent = new Intent("com.epeisong.ui.activity.CustomServiceComplete");
						intent.putExtra("CustomServiceComplete", mCustom);
						sendBroadcast(intent); // 发送广播
						finish();
					} else {
						if (!TextUtils.isEmpty(resultDesc)) {
							ToastUtils.showToast(resultDesc);
						} else {
							ToastUtils.showToast("处理完成失败");
						}
					}
				}
			};
			taskComplete.execute();
			break;

		default:
			break;
		}
	}

}
