package com.epeisong.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.SmsCodeActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeResp.Builder;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.WalletResp;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

public class OpenWalletActivity extends SmsCodeActivity implements OnClickListener {
    private EditText et_name;
    private TextView tv_name;
    private EditText et_card;
    private TextView tv_card;
    private EditText et_password;
    private TextView tv_judge_pw;
    private EditText et_confirm_pw;
    private TextView tv_confirm_pw;
    private EditText et_code;
    private Button btn_complete;
    private Button btn_code;
    private TextView tv_phone;
    private TextView tv_send_code;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // wallet = (Wallet) getIntent().getSerializableExtra("wallet");
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_wallet);
        handleProcotolTv();
        et_name = (EditText) findViewById(R.id.et_name);
        tv_name = (TextView) findViewById(R.id.tv_real_name);
        et_card = (EditText) findViewById(R.id.et_card);
        tv_card = (TextView) findViewById(R.id.tv_id_card);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_judge_pw = (TextView) findViewById(R.id.tv_judge_pw);
        et_confirm_pw = (EditText) findViewById(R.id.et_confirm_pw);
        tv_confirm_pw = (TextView) findViewById(R.id.tv_confirm_pw);
        et_code = (EditText) findViewById(R.id.et_code);
        btn_complete = (Button) findViewById(R.id.btn_complete);
        btn_code = (Button) findViewById(R.id.btn_get_code);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_phone.setText(UserDao.getInstance().getUser().getPhone());
        tv_send_code = (TextView) findViewById(R.id.tv_send_code);

        btn_complete.setOnClickListener(this);
        btn_code.setOnClickListener(this);
    }

    private int Validation() {
        String pwd = et_password.getText().toString();
        String confirm_pw = et_confirm_pw.getText().toString();
        String id_card = et_card.getText().toString();
        tv_confirm_pw.setVisibility(View.INVISIBLE);
        tv_judge_pw.setVisibility(View.INVISIBLE);
        tv_name.setVisibility(View.INVISIBLE);
        tv_card.setVisibility(View.INVISIBLE);
        tv_send_code.setVisibility(View.INVISIBLE);
        if (TextUtils.isEmpty(et_name.getText().toString())) {
            // ToastUtils.showToast("真实姓名不能为空");
            tv_name.setVisibility(View.VISIBLE);
            // return -1;
        }
        // if(TextUtils.isEmpty(id_card)){
        // ToastUtils.showToast("身份证号不能为空");
        // tv_card.setVisibility(View.VISIBLE);
        // return -1;
        // }
        if (id_card.trim().length() != 18 && id_card.trim().length() != 15) {
            // ToastUtils.showToast("身份证号长度应为15或者18位");
            tv_card.setVisibility(View.VISIBLE);
            // return -1;
        }
        // if(TextUtils.isEmpty(pwd)){
        // ToastUtils.showToast("支付密码不能为空");
        // return -1;
        // }
        if (pwd.trim().length() < 6 || pwd.trim().length() > 20) {
            tv_judge_pw.setVisibility(View.VISIBLE);
            // ToastUtils.showToast("密码应为6-20位数字或字母");
            // return -1;
        } else {
            if (!pwd.equals(confirm_pw)) {
                tv_confirm_pw.setVisibility(View.VISIBLE);
            }
        }
        // if(!pwd.equals(confirm_pw)){
        // tv_confirm_pw.setVisibility(View.VISIBLE);
        // ToastUtils.showToast("两次输入的密码不一致");
        // return -1;
        // }
        if (TextUtils.isEmpty(et_code.getText().toString())) {
            tv_send_code.setVisibility(View.VISIBLE);
            tv_send_code.setText("验证码不能为空");
            // ToastUtils.showToast("验证码不能为空");
            // return -1;
        }

        if (TextUtils.isEmpty(et_name.getText().toString())
                || (id_card.trim().length() != 18 && id_card.trim().length() != 15)
                || (pwd.trim().length() < 6 || pwd.trim().length() > 20) || !pwd.equals(confirm_pw)
                || TextUtils.isEmpty(et_code.getText().toString())) {
            return -1;
        }

        return 1;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_complete:
            if (Validation() < 0)
                return;
            showPendingDialog(null);
            AsyncTask<Void, Void, WalletResp> task = new AsyncTask<Void, Void, WalletResp>() {
                @Override
                protected WalletResp doInBackground(Void... params) {
                    ApiExecutor net = new ApiExecutor();
                    String pwd = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null);
                    String phone = UserDao.getInstance().getUser().getPhone();
                    String paymentPwd = et_password.getText().toString();
                    String realName = et_name.getText().toString();
                    String identityNumber = et_card.getText().toString();
                    String verifyCode = et_code.getText().toString();
                    int idType = 1;
                    try {
                        return net.openWallet(phone, pwd, phone, verifyCode,
                                Properties.VERIFICATION_CODE_PURPOSE_OPEN_WALLET, realName, idType, identityNumber,
                                paymentPwd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                protected void onPostExecute(WalletResp result) {
                    dismissPendingDialog();
                    if (result == null) {
                        ToastUtils.showToast("激活钱包失败");
                    } else {
                        if (result.getResult() == WalletResp.SUCC) {
                            ToastUtils.showToast("激活钱包成功");
                            Intent intent = new Intent();
                            intent.putExtra("wallet", result.getWallet());
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            if ("请输入正确的验证码".equals(result.getDesc())) {
                                tv_send_code.setVisibility(View.VISIBLE);
                                tv_send_code.setText(result.getDesc());
                            }
                        }
                    }
                };
            };
            task.execute();
            break;

        case R.id.btn_get_code:
            phone = UserDao.getInstance().getUser().getPhone();
            getSmsCode(btn_code, phone, Properties.VERIFICATION_CODE_PURPOSE_OPEN_WALLET);

            break;

        default:
            break;
        }

    }


    private void handleProcotolTv() {
        TextView tv_procotol = (TextView) findViewById(R.id.tv_procotol);
        tv_procotol.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence charSequence = tv_procotol.getText();
        if (charSequence != null && charSequence instanceof Spannable) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(charSequence);
            ssb.clearSpans();

            int end = charSequence.length();
            Spannable spannable = (Spannable) charSequence;
            URLSpan[] spans = spannable.getSpans(0, end, URLSpan.class);
            for (URLSpan span : spans) {
                int start = spannable.getSpanStart(span);
                end = spannable.getSpanEnd(span);
                spannable.removeSpan(span);
                span = new URLSpanNoUnderline(span.getURL());
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        // ToastUtils.showToast(span.getURL());
                        Intent intent = new Intent(OpenWalletActivity.this, WalletAgreenActivity.class);
                        startActivity(intent);
                    }
                }, start, end, 0);
            }
            tv_procotol.setText(ssb);
        }
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "激活钱包", null).setShowLogo(false);
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

}
