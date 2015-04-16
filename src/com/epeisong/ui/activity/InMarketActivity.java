package com.epeisong.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.User;

/**
 * 入园申请
 * 
 * @author Jack
 * 
 */
public class InMarketActivity extends BaseActivity {
	
	public static final int REQUEST_BACK_CONSULT = 108;
	private User mUser;
	//private String mUserId;
	private int Logistic_type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        //mUserId = getIntent().getStringExtra(ContactsDetailActivity.EXTRA_USER_ID);
        mUser = (User) getIntent().getSerializableExtra(ContactsDetailActivity.EXTRA_USER);
		Logistic_type = getIntent().getIntExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, 0);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addapply);
//		TextView applytv = (TextView)findViewById(R.id.tv_apply);
		switch(Logistic_type)
		{
		case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
			((TextView)findViewById(R.id.tv_apply)).setText("如何成为物流园专线？");
			((TextView)findViewById(R.id.tv_text1)).setText(
			"﻿﻿﻿﻿﻿　　成为物流园内的专线，您将出现在物流园的专线列表中，您会优先、及时地获得物流园向您发送的物流信息。");
			
			((TextView)findViewById(R.id.tv_text2)).setText(
			"　　具体申请细节，您可以在线咨询该物流园。物流园会根据您的情况核查您的身份信息，协商一致后，物流园会主动添加您为其专线。");
			//applytv.setText("入园须知");
			break;
		case Properties.LOGISTIC_TYPE_MARKET:
			((TextView)findViewById(R.id.tv_apply)).setText("如何成为配货市场会员？");
			((TextView)findViewById(R.id.tv_text1)).setText(
			"﻿﻿﻿﻿﻿　　成为配货市场会员，您将出现在配货市场的配载信息部列表中；在您发布车源货源信息时，将有权限将信息发送到配货市场的信息电子屏中，也将会有更多人看到您发布的信息。");
			
			((TextView)findViewById(R.id.tv_text2)).setText(
			"　　具体申请细节，您可以在线咨询该配货市场。配货市场会根据您的情况核查您的身份信息，协商一致后，配货市场会主动添加您为其会员。");
			//applytv.setText("加入声明");
			break;
		}
		
		findViewById(R.id.bt_consult).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
                Intent data = new Intent();
                setResult(Activity.RESULT_OK, data);
				finish();
			}
		});
	}

	@Override
	protected TitleParams getTitleParams() {
		switch(Logistic_type)
		{
		case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
			return new TitleParams(getDefaultHomeAction(), mUser.getShow_name(), null)
			.setShowLogo(false);
		case Properties.LOGISTIC_TYPE_MARKET:
			return new TitleParams(getDefaultHomeAction(), mUser.getShow_name(), null)
			.setShowLogo(false);
		}
		return new TitleParams(getDefaultHomeAction(), "入园申请", null)
		.setShowLogo(false);
	}
	
}