package com.epeisong.ui.activity;

import android.os.Bundle;
import android.view.Display;
import android.widget.LinearLayout;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
/**
 * 保证金账户介绍	订车配货保证金介绍
 * 
 * @author Jack
 * 
 */
public class GuaranteeIntroActivity extends BaseActivity {
	
	private LinearLayout ll_parent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guarantee_intro);
		ll_parent = (LinearLayout) findViewById(R.id.ll_parent);
		Display curr = getWindowManager().getDefaultDisplay();
		int height = curr.getHeight();
		ll_parent.setMinimumHeight(height);
//		WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
//		int height = wm.getDefaultDisplay().getHeight();
	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "保证金账户介绍", null)
		.setShowLogo(false);
	}

}
