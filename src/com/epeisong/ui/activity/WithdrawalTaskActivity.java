package com.epeisong.ui.activity;

import java.util.List;

import android.os.Bundle;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;

/**
 * 提现任务(转入任务)页面
 * @author Administrator
 * activity_withdrawal_task_item list列表页面
 */
public class WithdrawalTaskActivity extends BaseActivity {
	private List lv;
	private String titleName = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		titleName = getIntent().getStringExtra("titleName");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_withdrawal_task);
	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), titleName, null).setShowLogo(false);
	}
	
	

}
