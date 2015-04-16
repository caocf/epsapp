package com.epeisong.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;

/**
 * 提现账户管理页面
 * @author Administrator
 *
 */
public class WithdrawalActivity extends BaseActivity implements OnClickListener {
	private LinearLayout ll_account;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_withdrawal);
		ll_account = (LinearLayout) findViewById(R.id.ll_account);
		ll_account.setOnClickListener(this);
	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "提现账户管理", null).setShowLogo(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_account:
			Intent intent = new Intent(getApplication(), WithdrawalDetailActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		
	}

}
