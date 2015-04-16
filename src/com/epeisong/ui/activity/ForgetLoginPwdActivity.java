package com.epeisong.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.SmsCodeActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.base.view.viewinject.ViewInject;
import com.epeisong.base.view.viewinject.ViewInjecter;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeResp.Builder;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.epeisong.utils.java.JavaUtils;

/**
 * 忘记登录密码
 * @author poet
 *
 */
public class ForgetLoginPwdActivity extends SmsCodeActivity implements OnClickListener {

    public static final String EXTRA_PHONE = "phone";

    @ViewInject(id = R.id.et_phone)
    private EditText mPhoneEt;

    @ViewInject(id = R.id.et_new_pwd)
    private EditText mNewPwdEt;

    @ViewInject(id = R.id.et_new_pwd2)
    private EditText mNewPwd2Et;

    @ViewInject(id = R.id.et_code)
    private EditText mCodeEt;

    @ViewInject(id = R.id.tv_phone_warn)
    private TextView mPhoneWarnTv;

    @ViewInject(id = R.id.tv_new_pwd_warn)
    private View mNewPwdWarnTv;

    @ViewInject(id = R.id.tv_new_pwd2_warn)
    private View mNewPwd2WarnTv;

    @ViewInject(id = R.id.tv_code_warn)
    private TextView mCodeWarnTv;

    @ViewInject(id = R.id.btn_get_code)
    private Button mGetCodeBtn;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_login_pwd);
        findViewById(R.id.btn_get_code).setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.iv_eye).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mNewPwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mNewPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    break;
                }
                mNewPwdEt.setSelection(mNewPwdEt.getText().length());
                return true;
            }
        });
        ViewInjecter.inject(this);
        String phone = getIntent().getStringExtra(EXTRA_PHONE);
        if (!TextUtils.isEmpty(phone)) {
            mPhoneEt.setText(phone);
            mPhoneEt.setBackgroundDrawable(null);
            mPhoneEt.setEnabled(false);
        } else {
            mPhoneEt.requestFocus();
        }
        mPhoneWarnTv.setVisibility(View.GONE);
        mNewPwdWarnTv.setVisibility(View.INVISIBLE);
        mNewPwd2WarnTv.setVisibility(View.INVISIBLE);
        mCodeWarnTv.setVisibility(View.INVISIBLE);

        addTextWatcher(mPhoneEt, mPhoneWarnTv);
        addTextWatcher(mNewPwdEt, mNewPwdWarnTv, View.INVISIBLE);
        addTextWatcher(mNewPwd2Et, mNewPwd2WarnTv, View.INVISIBLE);
        addTextWatcher(mCodeEt, mCodeWarnTv, View.INVISIBLE);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "忘记登录密码");
    }

    @Override
    protected void onReceiveSmsCode(String code) {
        mCodeEt.setText(code);
    }

    @Override
    protected void onSendRequest(Builder resp) {
        if (resp == null) {
            ToastUtils.showToast("发送失败");
        } else if (resp.hasDesc()) {
            String desc = resp.getDesc();
            if ("没有找到账号".equals(desc)) {
                mPhoneWarnTv.setVisibility(View.VISIBLE);
                mPhoneWarnTv.setText("该手机号未注册");
            } else if (desc.contains("验证码已发送，有效时间还有")) {
                mCodeWarnTv.setVisibility(View.VISIBLE);
                mCodeWarnTv.setText(desc);
                mCodeWarnTv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCodeWarnTv.setVisibility(View.INVISIBLE);
                    }
                }, 3000);
            } else {
                ToastUtils.showToast(desc);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_get_code:
            String phone = mPhoneEt.getText().toString();
            if (TextUtils.isEmpty(phone) || !phone.startsWith("1") || phone.length() != 11) {
                mPhoneWarnTv.setText("该手机号是空号");
                mPhoneWarnTv.setVisibility(View.VISIBLE);
                return;
            }
            getSmsCode(mGetCodeBtn, phone, Properties.VERIFICATION_CODE_PURPOSE_FORGET_LOGIN_PASSWORD);
            break;
        case R.id.btn_ok:
            String nPhone = mPhoneEt.getText().toString();
            if (TextUtils.isEmpty(nPhone)) {
                mPhoneWarnTv.setVisibility(View.VISIBLE);
                return;
            }
            String pwd = mNewPwdEt.getText().toString();
            if (!JavaUtils.isPwdValid(pwd)) {
                mNewPwdWarnTv.setVisibility(View.VISIBLE);
                return;
            }
            String pwd2 = mNewPwd2Et.getText().toString();
            if (!pwd.equals(pwd2)) {
                mNewPwd2WarnTv.setVisibility(View.VISIBLE);
                return;
            }
            String code = mCodeEt.getText().toString();
            if (TextUtils.isEmpty(code)) {
                mCodeWarnTv.setVisibility(View.VISIBLE);
                return;
            }
            forgetLoginPwd(nPhone, code, pwd);
            break;
        }
    }

    private void forgetLoginPwd(final String phone, final String code, final String newPwd) {
        showPendingDialog(null);
        AsyncTask<Void, Void, Resp> task = new AsyncTask<Void, Void, Resp>() {
            @Override
            protected Resp doInBackground(Void... params) {
                ApiExecutor net = new ApiExecutor();
                try {
                    return net.forgetPassword(phone, code, newPwd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Resp result) {
                dismissPendingDialog();
                if (result == null) {
                    ToastUtils.showToast("修改失败");
                } else {
                    if (result.getResult() == Resp.SUCC) {
                        ToastUtils.showToast("登陆密码已重置");
                        finish();
                    } else {
                        String desc = result.getDesc();
                        if ("请输入正确的验证码".equals(desc)) {
                            mCodeWarnTv.setVisibility(View.VISIBLE);
                            mCodeWarnTv.setText("验证码输入错误");
                        } else if ("验证码已过期".equals(desc)) {
                            mCodeWarnTv.setVisibility(View.VISIBLE);
                            mCodeWarnTv.setText("验证码已失效，请重新获取");
                        } else {
                            ToastUtils.showToast(desc);
                        }
                    }
                }
            };
        };
        task.execute();
    }

    class TextChangeListener implements TextWatcher {

        EditText target;

        public TextChangeListener(EditText target) {
            this.target = target;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (target == mPhoneEt) {
                mPhoneWarnTv.setVisibility(View.GONE);
            } else if (target == mNewPwdEt) {
                mNewPwdWarnTv.setVisibility(View.INVISIBLE);
            } else if (target == mNewPwd2Et) {
                mNewPwd2WarnTv.setVisibility(View.INVISIBLE);
            } else if (target == mCodeEt) {
                mCodeWarnTv.setVisibility(View.INVISIBLE);
            }
        }

    }
}
