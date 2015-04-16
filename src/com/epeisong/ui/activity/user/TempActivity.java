package com.epeisong.ui.activity.user;

import android.os.Bundle;

import com.epeisong.base.activity.BaseActivity;

/**
 * 所有和用户注册登录相关的Activity基类，进入主界面后全部清除
 * @author poet
 *
 */
public abstract class TempActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TempActivityManager.getInstance().add(this);
	}

	@Override
	protected void onDestroy() {
		TempActivityManager.getInstance().remove(this);
		super.onDestroy();
	}
}
