package com.epeisong.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.net.NetComplain;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq.Builder;
import com.epeisong.model.User;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;

public class CustomerComplaintActivity extends BaseActivity implements OnClickListener {
	private TextView tv_name;
	private EditText et_content;
	private Button btn_send;
	private User mUser;
	String phone = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PHONE, null);
	private User loginUser;
	private String resultContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mUser = (User) getIntent().getSerializableExtra("user");
		loginUser = UserDao.getInstance().getUser();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complaint);
		tv_name = (TextView) findViewById(R.id.tv_name);
		et_content = (EditText) findViewById(R.id.et_content);
		btn_send = (Button) findViewById(R.id.btn_send);
		tv_name.setText(mUser.getShow_name());
		btn_send.setOnClickListener(this);
	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "投诉", null)
				.setShowLogo(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			
			if(TextUtils.isEmpty(et_content.getText().toString())){
				ToastUtils.showToast("请输入投诉原因");
				return;
			}
			showPendingDialog(null);
			AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>(){

				@Override
				protected Boolean doInBackground(Void... params) {
					NetComplain net = new NetComplain() {
						
						@Override
						protected boolean onSetRequest(Builder req) {
							req.setComplainContent(et_content.getText().toString());
							req.setComplainantName(loginUser.getShow_name());
							req.setComplainantId(Integer.parseInt(loginUser.getId()));
							if(mUser.getUser_type_code() == 33){
								//投诉对象是客服
								req.setComplainType(Properties.COMPLAIN_TYPE_CUSTOMER_SERVICE);
							}else{
								//投诉对象是普通用户
								req.setComplainType(Properties.COMPLAIN_TYPE_USER);
							}
							req.setRespondentId(Integer.parseInt(mUser.getId()));
							req.setRespondentName(mUser.getShow_name());
							return true;
						}
					};
					try {
						CommonLogisticsResp.Builder resp = net.request();
						if (resp != null && "SUCC".equals(resp.getResult())){
							return true;
						}else{
							resultContent = resp.getResult();
							return false;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}
				@Override
				protected void onPostExecute(Boolean result) {
					dismissPendingDialog();
					if(result){
						ToastUtils.showToast("投诉成功");
						finish();
					}else{
						ToastUtils.showToast(resultContent);
					}
					super.onPostExecute(result);
				}
			};
			task.execute();
			break;

		default:
			break;
		}
		
	}

}
