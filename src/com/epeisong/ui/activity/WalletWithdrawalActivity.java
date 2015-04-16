package com.epeisong.ui.activity;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.MoneyEditText;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.model.User;
import com.epeisong.model.Wallet;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.BankCard;
import com.epeisong.net.ws.utils.BankCardResp;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.net.ws.utils.WalletResp;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;
/**
 * 银行卡提现
 * 
 * @author Jack
 * 
 */

public class WalletWithdrawalActivity extends BaseActivity implements OnClickListener {
	private MoneyEditText et_card;
	private EditText et_password;
	private TextView et_num;
	private Button bt_selectbank;
	private Wallet mWallet;
    private BankCard mBankCard;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mWallet = (Wallet) getIntent().getSerializableExtra("wallet");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet_withdrawal);
		et_card = (MoneyEditText) findViewById(R.id.et_card);
		et_password = (EditText) findViewById(R.id.et_password);
		et_num = (TextView) findViewById(R.id.et_num);
		bt_selectbank = (Button) findViewById(R.id.bt_selectbank);
		findViewById(R.id.bt_next).setOnClickListener(this);
		
		//((TextView) findViewById(R.id.et_name)).setText("可用余额：");
		((TextView) findViewById(R.id.tv_point)).setText("　　金额");
		if(mWallet==null || mWallet.getAmount()==null)
			et_num.setText("0");//+" 元");
		else
			et_num.setText(String.valueOf(mWallet.getAmount()/100));//+" 元");
		((TextView) findViewById(R.id.et_payname)).setText("支付密码");
		
		bt_selectbank.setOnClickListener(this);

	}
	
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()) {
		case R.id.bt_selectbank:
			getBankCard();
        	break;
		case R.id.bt_next:
			String accountString = et_card.getText().toString();
			if(TextUtils.isEmpty(accountString)) {
				ToastUtils.showToast("请输入提取金额！");
				return;
			}
			
			if(!TextUtils.isEmpty(accountString)) {
				double accountnum = Double.valueOf(accountString);
				if(accountnum==0.0) {
					ToastUtils.showToast("提取金额不能为0");
					return;
				}
				accountString = String.valueOf((long)(accountnum*100.0));
			}
			
			String passString = et_password.getText().toString();
			if(TextUtils.isEmpty(passString)) {
				ToastUtils.showToast("请输入支付密码！");
				return;
			}
			depositWallet(Long.valueOf(accountString), passString, 0);
				
			break;
		}
		
	}
	
	public void getBankCard() {
		AsyncTask<Void, Void, BankCardResp> task = new AsyncTask<Void, Void, BankCardResp>() {
			@Override
			protected BankCardResp doInBackground(Void... params) {
				ApiExecutor api = new ApiExecutor();

				try {
					User user=UserDao.getInstance().getUser();

					return api.listBankCard(user.getAccount_name(), 
							SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null));
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(BankCardResp result) {

				if (null != result) {
					if(result.getResult()==BankCardResp.SUCC && result.getBankCardList().size()!=0) {
						//mBankCard = result.getBankCardList().get(0);
						//depositWallet(amount, passstring, 0);
						ShowSelectCard(result.getBankCardList());
						//myAdapter.replaceAll(result.List());
					} else {
						ToastUtils.showToast(result.getDesc());
					}
				} else {
					ToastUtils.showToast("请添加银行卡");
				}
			}

		};

		task.execute();
	}
	
	void ShowSelectCard(final List<BankCard> lbankList) {
		//lbankList.add(lbankList.get(0));
		int count = lbankList.size();
		final String[] arrayFruit = new String[lbankList.size()] ;
		for(int i=0;i<count;i++) {
			BankCard bankCard = lbankList.get(i);
			String banknString = bankCard.getBankName();
			if(bankCard.getCardType()==1)
				;//储蓄卡;
			arrayFruit[i]=banknString+"  尾号"+//"("+
			bankCard.getCardNumber().substring(bankCard.getCardNumber().length()-4);//+")";
		}
		
		final Dialog dialog = new AlertDialog.Builder(this).setTitle("选择银行卡")
//			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//					
//				@Override
//				public void onClick(DialogInterface arg0, int arg1) {
//						
//				}
//			})
			.setItems(arrayFruit, new DialogInterface.OnClickListener() {
					
			public void onClick(DialogInterface dialog, int which) {   
				mBankCard = lbankList.get(which);
				bt_selectbank.setTextColor(Color.BLACK);
				bt_selectbank.setText(arrayFruit[which]);
				} 
			}).create();  

		dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 
	
		dialog.show();
//		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();    
//		params.width = 800;
//		if(count>6)
//			params.height = 800+(5)*60;
//		else
//			params.height = 800+(count-1)*60;    
//		dialog.getWindow().setAttributes(params); 
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == WalletActivity.WITHDRAWAL_BANK) {
            	final Serializable serializable = data.getSerializableExtra("wallet");
            	if(serializable!=null) {
                    Intent intent = new Intent();
                    intent.putExtra("wallet", mWallet);
                    setResult(RESULT_OK, intent);
                    finish();
            	}
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    public void depositWallet(final long amount, final String paymentPwd, final int index ) {
        
        AsyncTask<Void, Void, WalletResp> task = new AsyncTask<Void, Void, WalletResp>() {
            @Override
            protected WalletResp doInBackground(Void... params) {
            	ApiExecutor api = new ApiExecutor();
                try {
                	User user=UserDao.getInstance().getUser();
                	
                	return api.withdrawToBankCard(user.getAccount_name(),
                    		SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), amount, paymentPwd,
                    		mBankCard.getCardType(), user.getShow_name(), user.getUser_type_code(), mBankCard.getId());

                		//return api.withdraw(user.getAccount_name(),
                        //		SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), amount, paymentPwd,
                        //		mBankCard.getCardType(), mBankCard.getOpenBankName(), mBankCard.getBankCode(), mBankCard.getBankName(),
                        //		mBankCard.getRegionCode(), mBankCard.getRegionName(), mBankCard.getRealName(), mBankCard.getCardNumber());
                        		//payeeType, openBankName, bankCode, bankName, bankRegionCode, bankRegionName, payeeName, payeeAccount)
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                return null;
            }

            @Override
            protected void onPostExecute(WalletResp result) {
                dismissPendingDialog();
                if (result==null) {
                	ToastUtils.showToast("操作失败");
                }else {
                	
        			if (result.getResult() == Resp.SUCC) {
        				ToastUtils.showToast("操作成功");
                    	mWallet = result.getWallet();
                    	
                        Intent intent1 = new Intent(getApplicationContext(), WalletWithdrawalfActivity.class);
                        intent1.putExtra("wallet", mWallet);
            			String banknString = mBankCard.getBankName();
            			banknString=banknString+"  尾号"+
            					mBankCard.getCardNumber().substring(mBankCard.getCardNumber().length()-4);
                        intent1.putExtra("bankinfor", banknString);
                        intent1.putExtra("drawalnum", amount);
                        startActivityForResult(intent1, WalletActivity.WITHDRAWAL_BANK);

        			} else {
        				ToastUtils.showToast(result.getDesc());
        			}
                }
            }
        };
        task.execute();
    }
    
	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "提现到银行卡", null).setShowLogo(false);
	}

}
