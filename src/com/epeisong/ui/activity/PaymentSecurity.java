package com.epeisong.ui.activity;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.activity.SmsCodeActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetGetCode;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeResp;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeResp.Builder;
import com.epeisong.model.Wallet;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.WalletResp;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 支付安全
 * @author gnn
 *
 */
public class PaymentSecurity extends SmsCodeActivity implements OnClickListener {
	
	private RelativeLayout rl_modify;
	private RelativeLayout rl_find;
	private LinearLayout ll_modify;
	private LinearLayout ll_find;
	private ImageView iv_modify;
	private ImageView iv_find;
	private View view_top;
	private boolean isModifyClick = true;
	private boolean isFindClick = true;
	private String loginPwd; // 用户登录密码
	private String loginPhone; // 用户登录帐号
	private String phone;
	
	private EditText et_original_pw;
	private TextView tv_original_pwd;
	private EditText et_new_pwd;
	private TextView tv_new_pwd;
	private EditText et_confirm_pwd;
	private TextView tv_confirm_pwd;
	private LinearLayout ll_modify_code;
	private TextView tv_modify_code_text;
	private TextView tv_modify_code_number;
	private EditText et_modify_code;
	private TextView tv_modify_code;
	private Button btn_modify;
	
	private TextView tv_user_phone;
	private EditText et_code;
	private TextView tv_code;
	private EditText et_find_pwd;
	private TextView tv_find_pwd;
	private EditText et_find_new_pwd;
	private TextView tv_find_new_pwd;
	private EditText et_send_code;
	private TextView tv_send_code;
	private Button btn_get_code;
	private Button btn_find;
	
	private String codeDesc;
	private String modifyCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_security);
		loginPwd = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null);
		loginPhone = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PHONE, null);
		phone = UserDao.getInstance().getUser().getPhone();
		
		rl_modify = (RelativeLayout) findViewById(R.id.rl_modify);
		rl_find = (RelativeLayout) findViewById(R.id.rl_find);
		ll_modify = (LinearLayout) findViewById(R.id.ll_modify);
		ll_find = (LinearLayout) findViewById(R.id.ll_find);
		iv_modify = (ImageView) findViewById(R.id.iv_modify);
		iv_find = (ImageView) findViewById(R.id.iv_find);
		view_top = findViewById(R.id.view_top);
		
		rl_modify.setOnClickListener(this);
		rl_find.setOnClickListener(this);
		//修改支付密码
		et_original_pw = (EditText) findViewById(R.id.et_original_pw);
		tv_original_pwd = (TextView) findViewById(R.id.tv_original_pwd);
		et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
		tv_new_pwd = (TextView) findViewById(R.id.tv_new_pwd);
		et_confirm_pwd = (EditText) findViewById(R.id.et_confirm_pwd);
		tv_confirm_pwd = (TextView) findViewById(R.id.tv_confirm_pwd);
		ll_modify_code = (LinearLayout) findViewById(R.id.ll_modify_code);
		tv_modify_code_text = (TextView) findViewById(R.id.tv_modify_code_text);
		tv_modify_code_number = (TextView) findViewById(R.id.tv_modify_code_number);
		et_modify_code = (EditText) findViewById(R.id.et_modify_code);
		tv_modify_code = (TextView) findViewById(R.id.tv_modify_code);
		btn_modify = (Button) findViewById(R.id.btn_modify);
		btn_modify.setOnClickListener(this);
		ll_modify_code.setOnClickListener(this);
		tv_modify_code_text.setText("获取验证码");
		//找回支付密码
		tv_user_phone = (TextView) findViewById(R.id.tv_user_phone);
		et_code = (EditText) findViewById(R.id.et_code);
		tv_code = (TextView) findViewById(R.id.tv_code);
		et_find_pwd = (EditText) findViewById(R.id.et_find_pwd);
		tv_find_pwd = (TextView) findViewById(R.id.tv_find_pwd);
		et_find_new_pwd = (EditText) findViewById(R.id.et_find_new_pwd);
		tv_find_new_pwd = (TextView) findViewById(R.id.tv_find_new_pwd);
		btn_get_code = (Button) findViewById(R.id.btn_get_code);
		et_send_code = (EditText) findViewById(R.id.et_send_code);
		tv_send_code = (TextView) findViewById(R.id.tv_send_code);
		btn_find = (Button) findViewById(R.id.btn_find);
		btn_find.setOnClickListener(this);
		btn_get_code.setOnClickListener(this);
		findViewById(R.id.rl_freeze).setOnClickListener(this);
		tv_user_phone.setText(UserDao.getInstance().getUser().getPhone());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_modify:
			if(isModifyClick){
				view_top.setVisibility(View.VISIBLE);
				iv_modify.setBackgroundResource(R.drawable.common_arrow_blue_top);
				ll_modify.setVisibility(View.VISIBLE);
				iv_find.setBackgroundResource(R.drawable.common_arrow_blue_bottom);
				ll_find.setVisibility(View.GONE);
				isFindClick = true;
				isModifyClick = false;
			}else{
				view_top.setVisibility(View.GONE);
				iv_modify.setBackgroundResource(R.drawable.common_arrow_blue_bottom);
				ll_modify.setVisibility(View.GONE);
				isModifyClick = true;
			}
			break;
		case R.id.rl_find:
			if(isFindClick){
				iv_find.setBackgroundResource(R.drawable.common_arrow_blue_top);
				ll_find.setVisibility(View.VISIBLE);
				iv_modify.setBackgroundResource(R.drawable.common_arrow_blue_bottom);
				ll_modify.setVisibility(View.GONE);
				isModifyClick = true;
				isFindClick = false;
			}else{
				iv_find.setBackgroundResource(R.drawable.common_arrow_blue_bottom);
				ll_find.setVisibility(View.GONE);
				isFindClick = true;
			}
			break;
		
		case R.id.btn_get_code:
			phone = UserDao.getInstance().getUser().getPhone();
            getSmsCode(btn_get_code, phone, Properties.VERIFICATION_CODE_PURPOSE_FORGET_PAY_PASSWORD);
			break;
		case R.id.btn_modify:
			if(Validation1() < 0){
				return;
			}
			if(!TextUtils.isEmpty(modifyCode)){
				tv_modify_code.setVisibility(View.VISIBLE);
				tv_modify_code.setText(modifyCode);
			}
			showPendingDialog(null);
			AsyncTask<Void, Void, WalletResp> taskChange = new AsyncTask<Void, Void, WalletResp>(){
                @Override
                protected WalletResp doInBackground(Void... arg0) {
                    ApiExecutor net = new ApiExecutor();
                    String orig_pwd = et_original_pw.getText().toString();
                    String new_pwd = et_new_pwd.getText().toString();
                    // String code = et_modify_code.getText().toString();
                    // String confirm_pwd = et_confirm_pwd.getText().toString();
                    try {
                        return net.changePaymentPwd(loginPhone, loginPwd, "1", "11", -1, new_pwd, orig_pwd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(WalletResp result) {
                    dismissPendingDialog();
                    if (result == null) {
                        ToastUtils.showToast("修改失败");
                    } else {
                        if (result.getResult() == WalletResp.SUCC) {
                            ToastUtils.showToast("修改成功");
                            finish();
                        } else {
                            tv_original_pwd.setVisibility(View.VISIBLE);
                            tv_original_pwd.setText("原密码输入错误");
                        }
                    }
                }
            };
            taskChange.execute();
            break;
        case R.id.btn_find:
            if (Validation2() < 0) {
                return;
            }
            if (!TextUtils.isEmpty(codeDesc)) {
                tv_send_code.setText("");
                tv_send_code.setVisibility(View.VISIBLE);
                tv_send_code.setText(codeDesc);
            }
            showPendingDialog(null);
            AsyncTask<Void, Void, WalletResp> taskFind = new AsyncTask<Void, Void, WalletResp>() {
            	@Override
                protected WalletResp doInBackground(Void... arg0) {
                    ApiExecutor net = new ApiExecutor();
                    String phone = UserDao.getInstance().getUser().getPhone();
                    String cardId = et_code.getText().toString();
                    String find_pwd = et_find_pwd.getText().toString();
                    String code = et_send_code.getText().toString();
                    try {
                        return net.forgetPaymentPwd(loginPhone, loginPwd, phone, code,
                                Properties.VERIFICATION_CODE_PURPOSE_FORGET_PAY_PASSWORD, cardId, find_pwd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(WalletResp result) {
                    dismissPendingDialog();
                    if (result == null) {
                        ToastUtils.showToast("找回支付密码失败");
                    } else {
                        if (result.getResult() == WalletResp.SUCC) {
                            ToastUtils.showToast("找回成功");
                            finish();
                        } else {
                        	if(result.getDesc().contains("验证码")){
                        		tv_send_code.setVisibility(View.VISIBLE);
                                tv_send_code.setText(result.getDesc());
                        	}else{
                            	ToastUtils.showToast(result.getDesc());
                            }
//                            if ("请输入正确的验证码".equals(result.getDesc())) {
//                                // ToastUtils.showToast(result.getDesc());
//                                tv_send_code.setVisibility(View.VISIBLE);
//                                tv_send_code.setText(result.getDesc());
//                            }else{
//                            	ToastUtils.showToast(result.getDesc());
//                            }
                        }
                    }
                }
            };
            taskFind.execute();
            break;
        case R.id.rl_freeze:///冻结
            Intent intent = new Intent(PaymentSecurity.this, FreezeWalletActivity.class);
            startActivityForResult(intent, WalletActivity.FREEZE_WALLET);
        	break;
        default:
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == WalletActivity.FREEZE_WALLET) {
            	Intent intent = new Intent();
            	final Serializable serializable = data.getSerializableExtra("wallet");
            	Wallet wallet;
            	if(serializable!=null) {
            		wallet=(Wallet)serializable;
            		intent.putExtra("wallet", wallet);
            	}
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
	@Override
	protected void onSendRequest(Builder resp) {
		if (resp == null) {
            ToastUtils.showToast("发送失败");
        } else if (resp.hasDesc()) {
            String desc = resp.getDesc();
            if (desc.contains("验证码已发送，有效时间还有")) {
            	tv_send_code.setVisibility(View.VISIBLE);
            	tv_send_code.setText(desc);
            	tv_send_code.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	tv_send_code.setVisibility(View.INVISIBLE);
                    }
                }, 3000);
            } else {
                ToastUtils.showToast(desc);
            }
        }
	}

	@Override
	protected void onReceiveSmsCode(String code) {
		et_code.setText(code);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

    private int Validation1() {
        tv_original_pwd.setVisibility(View.INVISIBLE);
        tv_new_pwd.setVisibility(View.INVISIBLE);
        tv_confirm_pwd.setVisibility(View.INVISIBLE);
        String et_pwd = et_original_pw.getText().toString().trim();
        String new_pwd = et_new_pwd.getText().toString().trim();
        String confirm_pwd = et_confirm_pwd.getText().toString().trim();
        // if(TextUtils.isEmpty(et_pwd)){
        // tv_original_pwd.setVisibility(View.VISIBLE);
        // tv_original_pwd.setText("原密码不能为空");
        // // ToastUtils.showToast("原密码不能为空");
        // // return -1;
        // }
        // if(!et_pwd.equals(pwd)){
        // tv_original_pwd.setVisibility(View.VISIBLE);
        // tv_original_pwd.setText("原密码输入错误");
        // // ToastUtils.showToast("原密码输入错误");
        // // return -1;
        // }

        if (TextUtils.isEmpty(et_pwd)) {
            tv_original_pwd.setVisibility(View.VISIBLE);
            tv_original_pwd.setText("请输入原密码");
            // ToastUtils.showToast("原密码不能为空");
            // return -1;
        } else {
            // if(!et_pwd.equals(pwd)){
            // tv_original_pwd.setVisibility(View.VISIBLE);
            // tv_original_pwd.setText("原密码输入错误");
            // // ToastUtils.showToast("原密码输入错误");
            // // return -1;
            // }
        }

        if (new_pwd.length() < 6 || new_pwd.length() > 20) {
            tv_new_pwd.setVisibility(View.VISIBLE);
            // ToastUtils.showToast("密码应在6-20位");
            // return -1;
        } else {
            if (!new_pwd.equals(confirm_pwd)) {
                tv_confirm_pwd.setVisibility(View.VISIBLE);
                // ToastUtils.showToast("两次输入的密码不一致");
                // return -1;
            }
        }
        // if(!new_pwd.equals(confirm_pwd)){
        // tv_confirm_pwd.setVisibility(View.VISIBLE);
        // // ToastUtils.showToast("两次输入的密码不一致");
        // // return -1;
        // }
        // if(TextUtils.isEmpty(et_pwd) || !et_pwd.equals(pwd) ||
        // (new_pwd.length() < 6 || new_pwd.length() > 20) ||
        // !new_pwd.equals(confirm_pwd)){
        // return -1;
        // }
        if (TextUtils.isEmpty(et_pwd) || (new_pwd.length() < 6 || new_pwd.length() > 20)
                || !new_pwd.equals(confirm_pwd)) {
            return -1;
        }
        return 1;
    }

    private int Validation2() {
        tv_find_pwd.setVisibility(View.INVISIBLE);
        tv_find_new_pwd.setVisibility(View.INVISIBLE);
        tv_send_code.setVisibility(View.INVISIBLE);
        tv_code.setVisibility(View.INVISIBLE);
        String id_card = et_code.getText().toString().trim();
        String find_pwd = et_find_pwd.getText().toString().trim();
        String find_confirm_pwe = et_find_new_pwd.getText().toString().trim();
        String send_code = et_send_code.getText().toString().trim();
        // if(TextUtils.isEmpty(id_card)){
        // ToastUtils.showToast("身份证号不能为空");
        // return -1;
        // }
        if (id_card.length() != 18 && id_card.length() != 15) {
            tv_code.setVisibility(View.VISIBLE);
            // ToastUtils.showToast("身份证号长度应为15或者18位");
            // return -1;
        }
        // if(TextUtils.isEmpty(find_pwd)){
        // tv_find_pwd.setVisibility(View.VISIBLE);
        // ToastUtils.showToast("密码应为6-20位数字或字母");
        // return -1;
        // }
        if (find_pwd.length() < 6 || find_pwd.length() > 20) {
            tv_find_pwd.setVisibility(View.VISIBLE);
            // ToastUtils.showToast("密码应在6-20位");
            // return -1;
        } else {
            if (!find_pwd.equals(find_confirm_pwe)) {
                tv_find_new_pwd.setVisibility(View.VISIBLE);
                // ToastUtils.showToast("两次输入的密码不一致");
                // return -1;
            }
        }
        // if(!find_pwd.equals(find_confirm_pwe)){
        // tv_find_new_pwd.setVisibility(View.VISIBLE);
        // // ToastUtils.showToast("两次输入的密码不一致");
        // // return -1;
        // }
        if (TextUtils.isEmpty(send_code)) {
            tv_send_code.setVisibility(View.VISIBLE);
            tv_send_code.setText("请输入验证码");
            // return -1;
        }
        if ((id_card.length() != 18 && id_card.length() != 15) || (find_pwd.length() < 6 || find_pwd.length() > 20)
                || !find_pwd.equals(find_confirm_pwe) || TextUtils.isEmpty(send_code)) {
            return -1;
        }
        return 1;
    }



	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "支付安全", null).setShowLogo(false);
	}

}
