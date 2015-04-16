package com.epeisong.ui.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import com.epeisong.utils.android.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.model.Dictionary;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.model.Wallet;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.BankCard;
import com.epeisong.net.ws.utils.BankCardResp;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.payment.net.utils.BankCodeUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 添加银行卡
 * 
 * @author Jack
 * 
 */
public class WalletAddBankActivity extends BaseActivity implements OnClickListener {
	private static final int REQUEST_CODE_CHOOSE_REGION = 100;

	public static final String EXTRA_WALLET = "wallet";
	private Wallet wallet;

	private EditText et_name;
	private EditText et_cardnum1, et_inputiden, et_inputpay;
	private Button bt_cardtype, bt_cityname, bt_finish;

	private int mRegionCode;
	private BankCard mbankCard;

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "添加银行卡", null).setShowLogo(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		wallet = (Wallet) getIntent().getSerializableExtra(EXTRA_WALLET);
		super.onCreate(savedInstanceState);

		if (wallet == null) {
			ToastUtils.showToast("参数错误");
			return;
		}

		setContentView(R.layout.activity_wallet_add_bank);

		mbankCard = new BankCard();
		if (wallet != null) {
			mbankCard.setWalletId(wallet.getId());
			mbankCard.setWalletName(wallet.getWalletName());
		}
		mbankCard.setCardType(1);//EnumBankCardType.BANK_CARD.getValue());
		mbankCard.setBankCode(BankCodeUtils.AGRICULTURAL_BANK);
		mbankCard.setBankName("农业银行");


		et_name = (EditText) findViewById(R.id.et_name);
		et_cardnum1 = (EditText) findViewById(R.id.et_cardnum1);
		et_inputiden = (EditText) findViewById(R.id.et_inputiden);
		et_inputpay = (EditText) findViewById(R.id.et_inputpay);

		bt_cardtype = (Button) findViewById(R.id.bt_cardtype);
		bt_cardtype.setOnClickListener(this);
		bt_cityname = (Button) findViewById(R.id.bt_cityname);
		bt_cityname.setOnClickListener(this);
		bt_finish = (Button) findViewById(R.id.bt_finish);
		bt_finish.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_cardtype:
			List<Dictionary> data = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_BANK_TYPE);
			// Dictionary dict = new Dictionary();
			// dict.setId(-1);
			// dict.setName("货物类型不限");
			// data.add(0, dict);
			showDictionaryListDialog("请选择银行", data, new OnChooseDictionaryListener() {
				@Override
				public void onChoosedDictionary(Dictionary item) {
					// ToastUtils.showToast(item.getName() + " - " +
					// item.getId());
					bt_cardtype.setTextColor(Color.BLACK);
					bt_cardtype.setText(item.getName());
					mbankCard.setBankName(item.getName());
					mbankCard.setBankCode(String.valueOf(item.getId()));
				}
			});

			break;
		case R.id.bt_cityname:
			ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_2, REQUEST_CODE_CHOOSE_REGION);
			break;
		case R.id.bt_finish:
			final String namesString = et_name.getText().toString();
			if (TextUtils.isEmpty(et_name.getText().toString())) {
				ToastUtils.showToast("请输入持卡人姓名");//"持卡人姓名应与钱包预设姓名相同");
				return;
			}

			String idenName = et_inputiden.getText().toString();
			if (idenName.length()==0) {
				ToastUtils.showToast("请输入身份证号码");
				return;
			}
			if (idenName.length()!=18 && idenName.length()!=15) {
				ToastUtils.showToast("与钱包预设身份证号不一致");
				return;
			}

			String bankName = bt_cardtype.getText().toString();
			if (TextUtils.equals("请选择银行",bankName)) {
				ToastUtils.showToast("请选择银行");
				return;
			}

			final String cardNumber = et_cardnum1.getText().toString();
			if (TextUtils.isEmpty(cardNumber)) {// || cardNumber.length() < 4 || cardNumber.substring(0, 4).equals("6226")) {
				ToastUtils.showToast("请输入银行卡号");
				return;
			}

			String cityName = bt_cityname.getText().toString();
			if (TextUtils.equals("请选择",cityName)) {
				ToastUtils.showToast("请选择账户所在城市");
				return;
			}

			final String mPasswords = et_inputpay.getText().toString();
			if (TextUtils.isEmpty(mPasswords)) {
				ToastUtils.showToast("请输入支付密码");
				return;
			}

			mbankCard.setCardType(Integer.valueOf(BankCodeUtils.AGRICULTURAL_BANK));
			mbankCard.setCardNumber(cardNumber);

			mbankCard.setRealName(namesString);
			mbankCard.setRegionCode(mRegionCode);
			mbankCard.setRegionName(cityName);
			mbankCard.setIdentityNumber(idenName);

			AsyncTask<Void, Void, BankCardResp> task = new AsyncTask<Void, Void, BankCardResp>() {
				@Override
				protected BankCardResp doInBackground(Void... params) {
					ApiExecutor api = new ApiExecutor();
					try {
						User user=UserDao.getInstance().getUser();

						return api.createBankCard(user.getAccount_name(), SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), 
								mPasswords, mbankCard.getBankCode(), mbankCard.getBankName(),
								mbankCard.getCardNumber(), mbankCard.getRealName(), String.valueOf(mbankCard.getCardType()), mbankCard.getIdentityNumber(), 
								"铁心桥分行", String.valueOf(mbankCard.getRegionCode()), mbankCard.getRegionName());
						//	ret = netWalletBank.addBankCard(bankCard.getWalletId(), bankCard.getWalletName(),
						//		bankCard.getCardType(), bankCard.getBankCode(), bankCard.getBankName(),
						//		bankCard.getCardNumber(), bankCard.getRealName(), bankCard.getCityName());
					} catch (Exception e) {
						e.printStackTrace();
					}

					return null;
				}

				@Override
				protected void onPostExecute(BankCardResp result) {
					dismissPendingDialog();
					if (result==null) {
						ToastUtils.showToast("操作失败");
					}else {

						if (result.getResult() == Resp.SUCC) {
							ToastUtils.showToast("操作成功");

							mbankCard = result.getBankCard();
							Intent intent = new Intent();
							intent.putExtra(WalletBankActivity.EXTRA_BANK_CARD, mbankCard);
							setResult(Activity.RESULT_OK, intent);
							finish();
						} else {
							ToastUtils.showToast(result.getDesc());
						}
					}
				}
			};
			task.execute();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_CHOOSE_REGION) {
				RegionResult result = (RegionResult) data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
				bt_cityname.setTextColor(Color.BLACK);
				mRegionCode = result.getCode();
				bt_cityname.setText(result.getProvinceName()+result.getCityName());//.getGeneralName());getShortNameFromDistrict
			}
		}
	}
}
