package com.epeisong.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import com.epeisong.utils.android.AsyncTask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.AdjustHeightListView;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.model.User;
import com.epeisong.model.Wallet;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.BankCard;
import com.epeisong.net.ws.utils.BankCardResp;
//import com.epeisong.payment.net.utils.EnumBankCardType;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 添加银行卡
 * 
 * @author Jack
 * 
 */

public class WalletBankActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

	public static final String EXTRA_WALLET = "wallet";
	public static final String EXTRA_BANK_CARD = "bankcard";
	public static final int REQUEST_CODE_BY_ADD = 1001;

	private AdjustHeightListView lv;
	private MyAdapter myAdapter;
	private RelativeLayout rl_addcard;
	private Button bt_cancel;

	private Wallet wallet;

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "我的银行卡", null).setShowLogo(false);
	}

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		wallet = (Wallet) getIntent().getSerializableExtra(EXTRA_WALLET);

		super.onCreate(savedInstanceState);
		if (wallet == null) {
			ToastUtils.showToast("参数错误");
			return;
		}

		setContentView(R.layout.activity_wallet_bank);

		lv = (AdjustHeightListView) findViewById(R.id.lv_wallet_bank);
		lv.setAdapter(myAdapter = new MyAdapter());
		lv.setOnItemClickListener(this);

		bt_cancel = (Button) findViewById(R.id.bt_cancel);
		rl_addcard = (RelativeLayout) findViewById(R.id.rl_addcard);
		rl_addcard.setOnClickListener(this);
		bt_cancel.setOnClickListener(this);

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
						//bt_cancel.setVisibility(View.VISIBLE);
						myAdapter.replaceAll(result.getBankCardList());
					} else {
						//bt_cancel.setVisibility(View.GONE);
					}
				} else {

					//bt_cancel.setVisibility(View.GONE);
				}
				rl_addcard.setVisibility(View.VISIBLE);
			}

		};

		task.execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		InputPayPassword(2, position);
		/*
		 * position -= lv.getHeaderViewsCount(); BankCard bankCard =
		 * myAdapter.getItem(position);
		 * 
		 * Intent i = new Intent(this, DealbillDetailActivity.class);
		 * i.putExtra(DealbillDetailActivity.EXTRA_INFO_FEE, bankCard);
		 */
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_addcard:
			Intent i = new Intent(this, WalletAddBankActivity.class);
			i.putExtra(WalletAddBankActivity.EXTRA_WALLET, wallet);
			//i.putExtra("paypassword", passwords);
			startActivityForResult(i, REQUEST_CODE_BY_ADD);
			//InputPayPassword(1);
			break;
		case R.id.bt_cancel:
			//InputPayPassword(2);
			break;
		default:
			break;
		}

	}

	void InputPayPassword(final int index,  final int position) {

		final AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
		LayoutInflater factory = LayoutInflater.from(this);  
		View view = factory.inflate(R.layout.dialog_paypassword, null);  
		// builder.setIcon(R.drawable.icon);  
		//builder.setTitle("自定义输入框");  
		final EditText et_password = (EditText) view.findViewById(R.id.et_password);
		final Intent i = new Intent(this, WalletAddBankActivity.class);

		builder.setView(view);  
		if(index==1) {
			((TextView) view.findViewById(R.id.tv_passwordhint)).setText("请输入支付密码,以添加银行卡");
		} else {
			((TextView) view.findViewById(R.id.tv_passwordhint)).setText("请输入支付密码,以解除绑定的银行卡");
		}

		dialog = builder.show();
		dialog.setCanceledOnTouchOutside(true);
		view.findViewById(R.id.bt_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//ToastUtils.showToast("cancel");
				dialog.dismiss();

			}
		});
		view.findViewById(R.id.bt_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String passwords = et_password.getText().toString();
				if (passwords.length() >= 6) {

					if(VerifyPayPassword(passwords, wallet.getId() )) {

						dialog.dismiss();
						if( index == 1 ) {
							i.putExtra(WalletAddBankActivity.EXTRA_WALLET, wallet);
							i.putExtra("paypassword", passwords);
							startActivityForResult(i, REQUEST_CODE_BY_ADD);
						}
						else {
							//int position=0;
							//position -= lv.getHeaderViewsCount(); 
							BankCard bankCard = myAdapter.getItem(position);
							DelCardFromWallet(bankCard, passwords);
						}
					}
					else {
						ToastUtils.showToast("密码不正确！");
					}

				} else {
					ToastUtils.showToast("密码不正确！");
					return;
				}
			}
		});

	}

	Boolean VerifyPayPassword(String passwords, Integer id) {
		return true;
	}

	void DelCardFromWallet(final BankCard bankCard, final String passwords)
	{
		AsyncTask<Void, Void, BankCardResp> task = new AsyncTask<Void, Void, BankCardResp>() {
			@Override
			protected BankCardResp doInBackground(Void... params) {
				ApiExecutor api = new ApiExecutor();

				try {
					User user=UserDao.getInstance().getUser();
					//boolean isExist = netWalletBank.checkBankCardIsExist(bankCard.getWalletId(),
					//		bankCard.getCardNumber());
					//if (isExist) {
					//ret = netWalletBank.deleteBankCard(bankCard.getId(),bankCard.getWalletId());
					return api.deleteBankCard(user.getAccount_name(), SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null),
							passwords, String.valueOf(bankCard.getId()));
					//} else {
					//	ret = -1;
					//}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(BankCardResp result) {
				if (result.getResult() > 0) {
					if(result.getResult()==BankCardResp.SUCC) {
						ToastUtils.showToast("银行卡解除绑定成功");
						myAdapter.removeItem(bankCard);
						//myAdapter.clear();
						//bt_cancel.setVisibility(View.GONE);
						//rl_addcard.setVisibility(View.VISIBLE);
					}
					else {
						ToastUtils.showToast(result.getDesc());
					}

				} else if (result.getResult() == -1) {
					ToastUtils.showToast("银行卡解除绑定失败");
				}
			}
		};

		task.execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_BY_ADD) {

			//rl_addcard.setVisibility(View.GONE);
			//bt_cancel.setVisibility(View.VISIBLE);
			BankCard bankCard = (BankCard) data.getSerializableExtra(EXTRA_BANK_CARD);
			myAdapter.addItem(bankCard);
		}
	}

	private class ViewHolder {
		public ImageView ll_bankcard;
		public TextView tv_bank_name;
		public TextView tv_bank_type;
		public TextView tv_bank_number;

		public void fillData(BankCard bankCard) {
			int bktype= Integer.valueOf(bankCard.getBankCode());
			switch (bktype) {
			case 1:
				ll_bankcard.setBackgroundResource(R.drawable.mybank_abc);
				break;
			case 2:
				ll_bankcard.setBackgroundResource(R.drawable.mybank_icbc);
				break;
			case 3:
				ll_bankcard.setBackgroundResource(R.drawable.mybank_ccb);
				break;
			case 4:
				ll_bankcard.setBackgroundResource(R.drawable.mybank_bcm);
				break;
			case 5:
				ll_bankcard.setBackgroundResource(R.drawable.mybank_boc);
				break;
			case 6:
				ll_bankcard.setBackgroundResource(R.drawable.mybank_psbc);
				break;

			default:
				break;
			}
			
			String cardNumber = bankCard.getCardNumber();
			int len = cardNumber.length();

			tv_bank_name.setText(bankCard.getBankName());
			if(len<8)
				tv_bank_number.setText("****" + cardNumber.substring(len - 4));
			else if(len>10) {
				String cardsString=cardNumber.substring(0, 6);
				for(int i=0;i<len-10;i++)
					cardsString += '*';
				cardsString += cardNumber.substring(len - 4);
				tv_bank_number.setText(cardsString);
			}
			else
				tv_bank_number.setText(cardNumber.substring(0, len-8)+"****" + cardNumber.substring(len - 4));

			//            if (EnumBankCardType.BANK_CARD.getValue() == bankCard.getCardType()) {
			tv_bank_type.setText("银行卡");
			//            } else if (EnumBankCardType.CREDIT.getValue() == bankCard.getCardType()) {

			//  tv_bank_type.setText("信用卡");
			//            }

		}

		public void findView(View v) {
			ll_bankcard = (ImageView) v.findViewById(R.id.iv_bankcard);
			tv_bank_name = (TextView) v.findViewById(R.id.tv_bank_name);
			tv_bank_type = (TextView) v.findViewById(R.id.tv_bank_type);
			tv_bank_number = (TextView) v.findViewById(R.id.tv_bank_number);
		}
	}

	private class MyAdapter extends HoldDataBaseAdapter<BankCard> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.item_wallet_bank);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			BankCard bankCard = getItem(position);
			holder.fillData(bankCard);

			return convertView;
		}
	}
}
