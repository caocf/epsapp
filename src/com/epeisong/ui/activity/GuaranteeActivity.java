package com.epeisong.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;

/**
 * 担保账户管理
 * @author Jack
 *
 */
public class GuaranteeActivity extends BaseActivity implements OnClickListener {
	private LinearLayout ll_account;
	private RelativeLayout ll_childnum, ll_usenum, ll_frenum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guarantee);
		ll_account = (LinearLayout) findViewById(R.id.ll_account);
		ll_account.setOnClickListener(this);
		ll_childnum = (RelativeLayout) findViewById(R.id.ll_childnum);
		ll_childnum.setOnClickListener(this);
		ll_usenum = (RelativeLayout) findViewById(R.id.ll_usenum);
		ll_usenum.setOnClickListener(this);
		ll_frenum = (RelativeLayout) findViewById(R.id.ll_frenum);
		ll_frenum.setOnClickListener(this);
	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "担保账户管理", null).setShowLogo(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_childnum:
		case R.id.ll_usenum:
		case R.id.ll_frenum:
			break;
		case R.id.ll_account:
			//Intent intent = new Intent(getApplication(), WithdrawalDetailActivity.class);
			//startActivity(intent);
			break;

		default:
			break;
		}
		
	}

}
