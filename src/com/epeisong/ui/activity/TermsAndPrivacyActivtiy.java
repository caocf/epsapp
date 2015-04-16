package com.epeisong.ui.activity;

import android.os.Bundle;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
/**
 * 
 * @author 孙灵洁
 * 使用条款与隐私政策
 *
 */
public class TermsAndPrivacyActivtiy extends BaseActivity {

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "使用条款与隐私政策", null)
				.setShowLogo(false);
	}

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terms_and_privacy);

	}

}
