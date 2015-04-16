package com.epeisong.ui.activity;

import java.io.Serializable;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.proto.Eps;
import com.epeisong.logistics.proto.Eps.AccountReq;
import com.epeisong.net.request.NetForgetPassword;
import com.epeisong.net.request.OnNetRequestListener;
import com.epeisong.ui.activity.temp.LoginActivity;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.java.JavaUtils;

public class SetPassWordActivity extends BaseActivity {

    public static final String EXTRA_OBJECT_ID_STRING = "object_id_string";

    private EditText et_xinpassword;
    private EditText et_querenpassword;
    private TextView tv_telephonenumber;
    private Button bt_queding;
    private String VerificationCode;

    @Override
    protected TitleParams getTitleParams() {
        // TODO Auto-generated method stub
        return new TitleParams(getDefaultHomeAction(), "设置新密码", null).setShowLogo(false);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        VerificationCode = getIntent().getStringExtra("VerificationCode");
        et_xinpassword = (EditText) findViewById(R.id.et_xinpassword);
        et_querenpassword = (EditText) findViewById(R.id.et_querenpassword);
        tv_telephonenumber = (TextView) findViewById(R.id.tv_telephonenumber);
        final String phone = UserDao.getInstance().getUser().getPhone();
        tv_telephonenumber.setText(phone);
        bt_queding = (Button) findViewById(R.id.bt_queding);

        bt_queding.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String pwd = et_xinpassword.getText().toString();
                String pwd2 = et_querenpassword.getText().toString();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd2)) {
                    ToastUtils.showToast("请输入密码");
                    return;
                }
                
                if(!JavaUtils.isPwdValid(pwd)) {
                    ToastUtils.showToast("密码格式不合法");
                    return;
                }

                if (pwd.equals(pwd2)) {
                    NetForgetPassword net = new NetForgetPassword(SetPassWordActivity.this) {
                        @Override
                        protected boolean onSetRequest(AccountReq.Builder req) {
                            req.setNewPassword(et_xinpassword.getText().toString());
                            req.setMobile(phone);
                            req.setVerificationCode(VerificationCode);
                            return true;
                        }

                    };

                    net.request(new OnNetRequestListener<Eps.AccountResp.Builder>() {

                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onFail(String msg) {
                            ToastUtils.showToast(msg);
                        }

                        @Override
                        public void onSuccess(com.epeisong.logistics.proto.Eps.AccountResp.Builder response) {
                            ToastUtils.showToast("更改成功");
                            EpsApplication.exit(SetPassWordActivity.this, LoginActivity.class, (Serializable) null);
                        }
                    });

                } else {
                    ToastUtils.showToast("两次密码输入不一致，请重新输入");
                }

            }
        });

    }
}
