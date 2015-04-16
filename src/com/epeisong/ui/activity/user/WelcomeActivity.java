package com.epeisong.ui.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.epeisong.R;
import com.epeisong.base.view.TitleParams;
import com.epeisong.ui.activity.temp.LoginActivity;
import com.epeisong.ui.activity.temp.RegisterStartActivity;

/**
 * 欢迎界面：登录注册入口
 * 
 * @author poet
 * 
 */
public class WelcomeActivity extends TempActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        findViewById(R.id.btn_regist).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    protected TitleParams getTitleParams() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_regist:
            Intent intent = new Intent(getApplicationContext(), RegisterStartActivity.class);
            startActivity(intent);
            finish();
            break;
        case R.id.btn_login:
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            finish();
            break;
        }
    }

}
