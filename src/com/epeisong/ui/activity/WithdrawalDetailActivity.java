package com.epeisong.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.DateTimePickDialogUtil;
import com.epeisong.base.view.TitleParams;

/**
 * 提现账户明细
 * @author Administrator
 *
 */
public class WithdrawalDetailActivity extends BaseActivity implements OnClickListener{
	private LinearLayout ll_account;
	private LinearLayout ll_account2;
	private LinearLayout rl_time;
	private TextView tv_select_time;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_account_manager);
		ll_account = (LinearLayout) findViewById(R.id.ll_account);
		ll_account2 = (LinearLayout) findViewById(R.id.ll_account2);
		ll_account.setOnClickListener(this);
		ll_account2.setOnClickListener(this);
		tv_select_time = (TextView) findViewById(R.id.tv_select_time);
		rl_time = (LinearLayout) findViewById(R.id.rl_time);
        rl_time.setOnClickListener(this);
	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "提现账户明细", null).setShowLogo(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_account:
			//转入
			Intent transfer = new Intent(getApplication(), WithdrawalTaskActivity.class);
			transfer.putExtra("titleName", "转入任务");
			startActivity(transfer);
			
			break;
		case R.id.ll_account2:
			//提现
			Intent withdrawal = new Intent(getApplication(), WithdrawalTaskActivity.class);
			withdrawal.putExtra("titleName", "提现任务");
			startActivity(withdrawal);
			break;
			
		case R.id.rl_time:
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
					WithdrawalDetailActivity.this);
			dateTimePicKDialog.dateTimePicKDialog(tv_select_time);
            break;

		default:
			break;
		}
	}

}
