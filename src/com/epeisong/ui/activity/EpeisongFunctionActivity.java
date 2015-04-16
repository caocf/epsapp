package com.epeisong.ui.activity;

import android.os.Bundle;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;

public class EpeisongFunctionActivity extends BaseActivity {

	  @Override
	    protected TitleParams getTitleParams() {
	        return new TitleParams(getDefaultHomeAction(), "热点问题", null).setShowLogo(false);
	    }

	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_epeisong_function);
	        
	    }
}
