package com.epeisong.ui.activity.temp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.EpsNetConfig;
import com.epeisong.R;
import com.epeisong.base.activity.SmsCodeActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.logistics.proto.Eps.AccountResp;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeResp.Builder;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.ui.activity.temp.LoginActivity.Loginer;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ReleaseLog;
import com.epeisong.utils.ShapeUtils;
import com.epeisong.utils.ShapeUtils.ShapeParams;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.epeisong.utils.java.JavaUtils;

/**
 * 注册-开始注册
 * 
 * @author poet
 * 
 */
public class RegisterStartActivity extends SmsCodeActivity implements OnClickListener, OnTouchListener {

    private EditText mPhoneEt;
    private EditText mPwdEt;
    private EditText mPwd2Et;
    private EditText mCodeEt;
    private Button mGetCodeBtn;
    private TextView mWarnCodeTv;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_start);
        mPhoneEt = (EditText) findViewById(R.id.et_phone);
        mPwdEt = (EditText) findViewById(R.id.et_pwd);
        mPwd2Et = (EditText) findViewById(R.id.et_pwd2);
        mCodeEt = (EditText) findViewById(R.id.et_code);
        mWarnCodeTv = (TextView) findViewById(R.id.tv_warn_code);
        View pwdView = findViewById(R.id.ll_pwd);
        View pwd2View = findViewById(R.id.ll_pwd2);
        ShapeParams params = new ShapeParams().setCorner(DimensionUtls.getPixelFromDp(3)).setStrokeColor(Color.GRAY);
        mPhoneEt.setBackgroundDrawable(ShapeUtils.getShape(params));
        // mPwdEt.setBackgroundDrawable(ShapeUtils.getShape(params));
        pwdView.setBackgroundDrawable(ShapeUtils.getShape(params));
        pwd2View.setBackgroundDrawable(ShapeUtils.getShape(params));
        mCodeEt.setBackgroundDrawable(ShapeUtils.getShape(params));
        mGetCodeBtn = (Button) findViewById(R.id.btn_get_code);
        mGetCodeBtn.setOnClickListener(this);
        findViewById(R.id.iv_eye).setOnTouchListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        handleProcotolTv();
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "开始注册", null).setShowLogo(false);
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
            if (desc.contains("验证码已发送，有效时间还有")) {
                mWarnCodeTv.setVisibility(View.VISIBLE);
                mWarnCodeTv.setText(desc);
                mWarnCodeTv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWarnCodeTv.setVisibility(View.GONE);
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
            final String phone = mPhoneEt.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                // TODO 手机号码校验
                ToastUtils.showToast("手机号码不可为空！");
                return;
            }
            getSmsCode(mGetCodeBtn, phone, Properties.VERIFICATION_CODE_PURPOSE_CREATE_ACCOUNT);
            break;
        case R.id.btn_register:
            String mPhone = mPhoneEt.getText().toString();
            if (TextUtils.isEmpty(mPhone)) {
                ToastUtils.showToast("请输入手机号");
                return;
            }
            String pwd = mPwdEt.getText().toString();
            if (TextUtils.isEmpty(pwd)) {
                ToastUtils.showToast("请输入密码");
                return;
            }
            if (!JavaUtils.isPwdValid(pwd)) {
                ToastUtils.showToast("请检查密码格式！");
                return;
            }
            String pwd2 = mPwd2Et.getText().toString();
            if (TextUtils.isEmpty(pwd2)) {
                ToastUtils.showToast("请输入确认密码");
                return;
            }
            if (!pwd.equals(pwd2)) {
                ToastUtils.showToast("两个密码输入不一致");
                return;
            }
            String code = mCodeEt.getText().toString();
            if (TextUtils.isEmpty(code)) {
                ToastUtils.showToast("请输入验证码！");
                return;
            }
            createAccountHttp(mPhone, pwd, code);
            break;
        }
    }

    private void createAccountHttp(final String phone, final String pwd, final String code) {
        showPendingDialog(null);
        AsyncTask<Void, Void, Resp> task = new AsyncTask<Void, Void, Resp>() {
            @Override
            protected Resp doInBackground(Void... params) {
                ApiExecutor net = new ApiExecutor();
                try {
                    return net.createAccount(phone, pwd, code);
                } catch (Exception e) {
                    e.printStackTrace();
                    ReleaseLog.log("NetUserAccount.createAccount", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Resp result) {
                dismissPendingDialog();
                if (result == null) {
                    ToastUtils.showToast("注册失败！");
                } else {
                    if (result.getResult() == Resp.SUCC) {
                        Loginer.saveLoginer(phone);
                        UserDao.saveAccount(phone, pwd);
                        ToastUtils.showToastInThread("注册成功！");
                        Intent intent = new Intent(RegisterStartActivity.this, ChooseRoleActivityNew.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtils.showToastInThread(result.getDesc());
                    }
                }
            }
        };
        task.execute();

    }

    private void createAccount(final String phone, final String pwd, final String code, final String objectIdString) {
        showPendingDialog(null);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    AccountResp.Builder respAccount = NetServiceFactory.getInstance().createAccount(
                            EpsNetConfig.getHost(), EpsNetConfig.PORT, phone, pwd, code, objectIdString, 9000);
                    if (respAccount == null || !"SUCC".equals(respAccount.getResult())) {
                        if (respAccount == null) {
                            ToastUtils.showToastInThread("注册失败！");
                        } else {
                            ToastUtils.showToastInThread(respAccount.getDesc());
                        }
                        return false;
                    } else {
                        Loginer.saveLoginer(phone);
                        ToastUtils.showToastInThread("注册成功！");
                        UserDao.saveAccount(phone, pwd);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showToastInThread("注册失败！");
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissPendingDialog();
                if (result) {
                    // 创建钱包账号
                    // String id = UserDao.getInstance().getUser().getId();
                    // createWalletAccount(id, mPhone, pwd);

                    Intent intent = new Intent(RegisterStartActivity.this, ChooseRoleActivityNew.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        task.execute();
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
                        Intent intent = new Intent(RegisterStartActivity.this, RegisterAgreenActivity.class);
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
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mPwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            mPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            break;
        }
        mPwdEt.setSelection(mPwdEt.getText().length());
        return true;
    }
}
