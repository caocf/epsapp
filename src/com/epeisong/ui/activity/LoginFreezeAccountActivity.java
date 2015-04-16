package com.epeisong.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.model.Dictionary;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

public class LoginFreezeAccountActivity extends BaseActivity implements OnClickListener {
	private LinearLayout ll_account;
	private LinearLayout ll_type;
	private TextView tv_top;
//	private TextView tv_name;
	private EditText et_account;
	private EditText et_name;
	private EditText et_phone;
	private EditText et_content;
	private TextView tv_typename;
	private Button btn_send;
	private int typeCode;
	
	private String account;
	private String name;
	private String phone;
	private String content;
	private String typeName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_other_problem);
		ll_account = (LinearLayout) findViewById(R.id.ll_account);
		ll_account.setVisibility(View.VISIBLE);
		ll_type = (LinearLayout) findViewById(R.id.ll_type);
		ll_type.setOnClickListener(this);
		tv_typename = (TextView) findViewById(R.id.tv_typename);
		tv_top = (TextView) findViewById(R.id.tv_top);
//		tv_name = (TextView) findViewById(R.id.tv_name);
		et_account = (EditText) findViewById(R.id.et_account);
		et_name = (EditText) findViewById(R.id.et_name);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_content = (EditText) findViewById(R.id.et_content);
		btn_send = (Button) findViewById(R.id.btn_send);
		btn_send.setOnClickListener(this);
		tv_top.setText("如果您有账号安全或业务问题，发送您的问题给客服，客服会尽快为你解决");
		
	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "其他问题", null).setShowLogo(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			if(Validation() < 0){
				return;
			}
			createProblemHttp(account, name, phone, typeCode, content);
			break;
			
		case R.id.ll_type:
			List<Dictionary> dataLength = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_QUESTIONS);
			showDictionaryListDialog("选择问题类型", dataLength, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                	tv_typename.setText(item.getName());
                    typeCode = item.getId();
                }
            });
			break;

		default:
			break;
		}
	}
	
	private void createProblemHttp(final String userAccount, final String userName, final String contactTel , final int type ,final String detail) {
        showPendingDialog(null);
        AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                ApiExecutor net = new ApiExecutor();
                try {
                    return net.createTask(userAccount, userName, contactTel, type, detail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer result) {
                dismissPendingDialog();
                if (result == null) {
                    ToastUtils.showToast("解析失败");
                } else {
                	if(result == 1){
	                	ToastUtils.showToastInThread("问题提交成功");
//	                    Intent intent = new Intent(LoginFreezeAccountActivity.this, LoginActivity.class);
//	                    startActivity(intent);
	                    finish();
                	}else{
                		ToastUtils.showToast("问题提交失败，请重新提交");
                	}
                }
            }
        };
        task.execute();

    }
	
	private int Validation(){
		account = et_account.getText().toString();
		name = et_name.getText().toString();
		phone = et_phone.getText().toString();
		content = et_content.getText().toString();
		typeName = tv_typename.getText().toString();
		
		if(TextUtils.isEmpty(account)){
			ToastUtils.showToast("请输入您的账号");
			return -1;
		}
		
		if(TextUtils.isEmpty(name)){
			ToastUtils.showToast("请输入您的名称");
			return -1;
		}
		
		if(TextUtils.isEmpty(phone)){
			ToastUtils.showToast("请输入您的联系电话");
			return -1;
		}
		
		if(TextUtils.isEmpty(typeName)){
			ToastUtils.showToast("请选择您的问题类型");
			return -1;
		}
		
		if(TextUtils.isEmpty(content)){
			ToastUtils.showToast("请输入您的问题");
			return -1;
		}
		
		return 1;
	}

}
