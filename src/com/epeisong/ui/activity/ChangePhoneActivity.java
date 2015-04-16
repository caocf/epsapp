package com.epeisong.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.SmsCodeActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.base.view.viewinject.ViewInject;
import com.epeisong.base.view.viewinject.ViewInjecter;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeResp.Builder;
import com.epeisong.model.User;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.net.ws.utils.WalletResp;
import com.epeisong.service.thread.KeepConnectionThread;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.epeisong.utils.java.JavaUtils;

/**
 * 更换手机号码
 * @author poet
 *
 */
public class ChangePhoneActivity extends SmsCodeActivity implements OnClickListener {

    public static final String EXTRA_OLD_PHONE = "old_phone";
    public static final String EXTRA_IS_WALLET_OPENED = "is_wallet_opened";

    @ViewInject(id = R.id.tv_old_phone)
    private TextView mOldPhoneTv;
    @ViewInject(id = R.id.et_login_pwd)
    private EditText mLoginPwdEt;
    @ViewInject(id = R.id.tv_warn_login_pwd)
    private TextView mWarnLoginPwdTv;
    @ViewInject(id = R.id.et_pay_pwd)
    private EditText mPayPwdEt;
    @ViewInject(id = R.id.tv_warn_pay_pwd)
    private TextView mWarnPayPwdTv;
    @ViewInject(id = R.id.et_new_phone)
    private EditText mNewPhoneEt;
    @ViewInject(id = R.id.tv_warn_new_phone)
    private TextView mWarnNewPhoneTv;
    @ViewInject(id = R.id.et_code)
    private EditText mCodeEt;
    @ViewInject(id = R.id.tv_warn_code)
    private TextView mWarnCodeTv;
    @ViewInject(id = R.id.btn_get_code)
    private Button mGetCodeBtn;

    private String mOldPhone;
    private boolean mIsWalletOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mOldPhone = getIntent().getStringExtra(EXTRA_OLD_PHONE);
        mIsWalletOpened = getIntent().getBooleanExtra(EXTRA_IS_WALLET_OPENED, false);
        super.onCreate(savedInstanceState);
        if (TextUtils.isEmpty(mOldPhone)) {
            finish();
            return;
        }
        setContentView(R.layout.activity_change_phone);
        ViewInjecter.inject(this);
        mOldPhoneTv.setText(mOldPhone);
        mGetCodeBtn.setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);

        if (!mIsWalletOpened) {
            findViewById(R.id.ll_pay_pwd).setVisibility(View.GONE);
        }

        addTextWatcher(mLoginPwdEt, mWarnLoginPwdTv);
        addTextWatcher(mPayPwdEt, mWarnPayPwdTv);
        addTextWatcher(mNewPhoneEt, mWarnNewPhoneTv);
        addTextWatcher(mCodeEt, mWarnCodeTv);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "更换手机号");
    }

    @Override
    protected void onSendRequest(Builder resp) {
        mWarnNewPhoneTv.setVisibility(View.GONE);
        if (resp == null) {
            ToastUtils.showToast("发送失败");
        } else if (resp.hasDesc()) {
            String desc = resp.getDesc();
            if ("此手机号已注册".equals(desc)) {
                mWarnNewPhoneTv.setVisibility(View.VISIBLE);
                mWarnNewPhoneTv.setText(desc);
            } else if (desc.contains("验证码已发送，有效时间还有")) {
                mWarnCodeTv.setVisibility(View.VISIBLE);
                mWarnCodeTv.setText(desc);
                mWarnCodeTv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWarnCodeTv.setVisibility(View.INVISIBLE);
                    }
                }, 3000);
            } else {
                ToastUtils.showToast(desc);
            }
        }
    }

    @Override
    protected void onReceiveSmsCode(String code) {
        mCodeEt.setText(code);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_get_code:
            String newPhone = mNewPhoneEt.getText().toString();
            if (JavaUtils.isPhoneNumValid(newPhone)) {
                getSmsCode(mGetCodeBtn, newPhone, Properties.VERIFICATION_CODE_PURPOSE_CHANGE_MOBILE);
            } else {
                mWarnNewPhoneTv.setVisibility(View.VISIBLE);
                mWarnNewPhoneTv.setText("新手机号输入错误");
            }
            break;
        case R.id.btn_ok:
            checkInput();
            break;
        }
    }

    private void checkInput() {
        String loginPwd = mLoginPwdEt.getText().toString();
        if (TextUtils.isEmpty(loginPwd)) {
            mWarnLoginPwdTv.setVisibility(View.VISIBLE);
            mWarnLoginPwdTv.setText("请输入登录密码");
            return;
        }
        if (!JavaUtils.isPwdValid(loginPwd)) {
            mWarnLoginPwdTv.setVisibility(View.VISIBLE);
            mWarnLoginPwdTv.setText("登录密码输入错误");
            return;
        }
        String payPwd = "";
        if (mIsWalletOpened) {
            payPwd = mPayPwdEt.getText().toString();
            if (TextUtils.isEmpty(payPwd)) {
                mWarnPayPwdTv.setVisibility(View.VISIBLE);
                mWarnPayPwdTv.setText("支付密码输入错误");
                return;
            }
        }
        String newPhone = mNewPhoneEt.getText().toString();
        if (TextUtils.isEmpty(newPhone)) {
            mWarnNewPhoneTv.setVisibility(View.VISIBLE);
            mWarnNewPhoneTv.setText("请输入新手机号");
            return;
        }
        if (!JavaUtils.isPhoneNumValid(newPhone)) {
            mWarnNewPhoneTv.setVisibility(View.VISIBLE);
            mWarnNewPhoneTv.setText("新手机号输入错误");
            return;
        }
        String code = mCodeEt.getText().toString();
        if (TextUtils.isEmpty(code)) {
            mWarnCodeTv.setVisibility(View.VISIBLE);
            mWarnCodeTv.setText("请输入验证码");
            return;
        }
        changePhone(loginPwd, payPwd, newPhone, code);
    }

    private void changePhone(final String loginPwd, final String payPwd, final String newMobile, final String code) {
        showPendingDialog(null);
        AsyncTask<Void, Void, WalletResp> task = new AsyncTask<Void, Void, WalletResp>() {
            @Override
            protected WalletResp doInBackground(Void... params) {
                ApiExecutor api = new ApiExecutor();
                try {
                    return api.changeMobile(mOldPhone, loginPwd, code,
                            Properties.VERIFICATION_CODE_PURPOSE_CHANGE_MOBILE, payPwd, mOldPhone, newMobile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(WalletResp result) {
                dismissPendingDialog();
                if (result == null) {
                    ToastUtils.showToast("请求失败");
                } else {
                    if (result.getResult() == Resp.SUCC) {
                        User user = UserDao.getInstance().getUser();
                        user.setPhone(newMobile);
                        user.setAccount_name(newMobile);
                        UserDao.getInstance().replace(user);
                        SpUtils.put(SpUtils.KEYS_SYS.STRING_CURR_USER_PHONE, newMobile);
                        KeepConnectionThread.changePhone(newMobile);
                        ToastUtils.showToast("手机号已更换成功");
                        finish();
                    } else {
                        String desc = result.getDesc();
                        if ("用户名密码不正确".equals(desc)) {
                            mWarnLoginPwdTv.setVisibility(View.VISIBLE);
                        } else if ("请输入正确的验证码".equals(desc)) {
                            mWarnCodeTv.setVisibility(View.VISIBLE);
                            mWarnCodeTv.setText("验证码输入错误");
                        } else {
                            ToastUtils.showToast(result.getDesc());
                        }
                    }
                }
            }
        };
        task.execute();
    }
}
