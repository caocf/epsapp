package com.epeisong.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.model.Wallet;
/**
 * 银行卡提现详情
 * 
 * @author Jack
 * 
 */

public class WalletWithdrawalfActivity extends BaseActivity implements OnClickListener {
	private TextView et_cardinfor, et_account;
	private Wallet mWallet;
	private double amount;
	private String bankString;
    //private BankCard mBankCard;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mWallet = (Wallet) getIntent().getSerializableExtra("wallet");
		amount = getIntent().getLongExtra("drawalnum", 0)/100.0;
		bankString = getIntent().getStringExtra("bankinfor");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet_withdrawalf);
		et_cardinfor = (TextView) findViewById(R.id.et_cardinfor);
		et_account = (TextView) findViewById(R.id.et_account);
		et_cardinfor.setText(bankString);
		et_account.setText(String.valueOf(amount));
		findViewById(R.id.bt_ok).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()) {
		case R.id.bt_ok:
			onBackPressed();
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("wallet", mWallet);
        setResult(RESULT_OK, intent);
		super.onBackPressed();
	}
	
	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "提现详情", null).setShowLogo(false);
	}

}
