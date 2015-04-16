package com.epeisong.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.service.thread.KeepConnectionThread;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.epeisong.utils.java.JavaUtils;

/**
 * 修改登录密码
 * @author poet
 *
 */
public class ChangeLoginPwdActivity extends BaseActivity implements OnClickListener {

    private EditText mOldPwdEt, mNewPwdEt, mConfirmPwdEt;
    private TextView mOldWarnTv, mNewWarnTv, mConfirmWarnTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_login_pwd);
        mOldPwdEt = (EditText) findViewById(R.id.et_old_pwd);
        mOldPwdEt.requestFocus();
        mNewPwdEt = (EditText) findViewById(R.id.et_new_pwd);
        mConfirmPwdEt = (EditText) findViewById(R.id.et_confirm_pwd);
        mOldWarnTv = (TextView) findViewById(R.id.tv_old_pwd_warn);
        mNewWarnTv = (TextView) findViewById(R.id.tv_new_pwd_warn);
        mConfirmWarnTv = (TextView) findViewById(R.id.tv_confirm_pwd_warn);
        findViewById(R.id.btn_ok).setOnClickListener(this);

        mOldWarnTv.setVisibility(View.INVISIBLE);
        mNewWarnTv.setVisibility(View.INVISIBLE);
        mConfirmWarnTv.setVisibility(View.INVISIBLE);

        mOldPwdEt.addTextChangedListener(new TextChangeListener(mOldPwdEt));
        mNewPwdEt.addTextChangedListener(new TextChangeListener(mNewPwdEt));
        mConfirmPwdEt.addTextChangedListener(new TextChangeListener(mConfirmPwdEt));
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "修改登录密码");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_ok:
            change();
            break;
        }
    }

    private void change() {
        final String oldPwd = mOldPwdEt.getText().toString();
        final String newPwd = mNewPwdEt.getText().toString();
        String newPwd2 = mConfirmPwdEt.getText().toString();
        if (TextUtils.isEmpty(oldPwd)) {
            mOldWarnTv.setText("请输入原登录密码");
            mOldWarnTv.setVisibility(View.VISIBLE);
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            mNewWarnTv.setVisibility(View.VISIBLE);
            return;
        }
        if (!JavaUtils.isPwdValid(newPwd)) {
            mNewWarnTv.setVisibility(View.VISIBLE);
            return;
        }
        if (!newPwd.equals(newPwd2)) {
            mConfirmWarnTv.setVisibility(View.VISIBLE);
            return;
        }
        showPendingDialog(null);
        AsyncTask<Void, Void, Resp> task = new AsyncTask<Void, Void, Resp>() {
            @Override
            protected Resp doInBackground(Void... params) {
                ApiExecutor net = new ApiExecutor();
                String phone = UserDao.getInstance().getUser().getPhone();
                try {
                    return net.updatePassword(phone, oldPwd, newPwd);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e("", e);
                }
                return null;
            }

            protected void onPostExecute(Resp result) {
                dismissPendingDialog();
                if (result != null) {
                    if (result.getResult() == Resp.SUCC) {
                        ToastUtils.showToast("更改成功");
                        SpUtils.put(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, newPwd);
                        KeepConnectionThread.changePwd(newPwd);
                        finish();
                    } else {
                        String desc = result.getDesc();
                        if ("密码不正确！".equals(desc)) {
                            mOldWarnTv.setText("原登录密码输入错误");
                            mOldWarnTv.setVisibility(View.VISIBLE);
                        } else {
                            ToastUtils.showToast(desc);
                        }
                    }
                } else {
                    ToastUtils.showToast("修改密码失败");
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
            if (target == mOldPwdEt) {
                mOldWarnTv.setVisibility(View.INVISIBLE);
            } else if (target == mNewPwdEt) {
                mNewWarnTv.setVisibility(View.INVISIBLE);
                mConfirmWarnTv.setVisibility(View.INVISIBLE);
            } else if (target == mConfirmPwdEt) {
                mConfirmWarnTv.setVisibility(View.INVISIBLE);
            }
        }

    }
}
