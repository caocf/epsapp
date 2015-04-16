package com.epeisong.ui.activity;

import android.os.Bundle;
import android.view.Display;
import android.widget.RelativeLayout;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;

public class LoginOtherProblemActivity extends BaseActivity {
	private RelativeLayout rl_parent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_other_problem);
//		rl_parent = (RelativeLayout) findViewById(R.id.rl_parent);
//		Display curr = getWindowManager().getDefaultDisplay();
//		int height = curr.getHeight()-100;
//		rl_parent.setMinimumHeight(height);
	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "其他问题", null).setShowLogo(false);
	}

}
