package com.epeisong.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.AdjustHeightListView;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.model.Dictionary;
import com.epeisong.model.User;
import com.epeisong.model.Wallet;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 选择充值方式
 * 
 * @author Jack
 * 
 */

public class PaymentSelectActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	private ImageView oldimImageView=null;
	private AdjustHeightListView lv;
	private TextView view_empty;
	private TextView tv_remaindmoney;
	private TextView tv_numnum, tv_walletnum;
	private ImageView iv_walletsel;
	private RelativeLayout rl_walletmoney;
	private LinearLayout ll_remaindall;
	private Button bt_payment;
	MyAdapter mAdapter = new MyAdapter();
	private User mUser;
	private Wallet mWallet;
	double paymentMoney, walletmoney;
	private boolean walletselected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mWallet = (Wallet) getIntent().getSerializableExtra("wallet");
		paymentMoney = ((double) getIntent().getIntExtra("paymentmoney", 0))/100.0;
		walletmoney = mWallet.getAmount()/100.0;
		walletmoney=600.0;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_select);
		
		mUser = UserDao.getInstance().getUser();
		((TextView) findViewById(R.id.tv_username)).setText(mUser.getShow_name());
		tv_numnum = (TextView) findViewById(R.id.tv_numnum);
		tv_numnum.setText(String.valueOf(paymentMoney));
		tv_walletnum = (TextView) findViewById(R.id.tv_walletnum);
		tv_walletnum.setText(String.valueOf(walletmoney));
		iv_walletsel = (ImageView) findViewById(R.id.iv_walletsel);
		walletselected = false;
		rl_walletmoney = (RelativeLayout) findViewById(R.id.rl_walletmoney);
		rl_walletmoney.setOnClickListener(this);
		
		ll_remaindall = (LinearLayout) findViewById(R.id.ll_remaindall);
		tv_remaindmoney = (TextView) findViewById(R.id.tv_remaindmoney);
		tv_remaindmoney.setText(String.valueOf(paymentMoney));
			
		lv = (AdjustHeightListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(this);
		lv.setAdapter(mAdapter = new MyAdapter());
		
		view_empty = (TextView) findViewById(R.id.view_empty);
		
		bt_payment = (Button) findViewById(R.id.bt_payment);
		bt_payment.setOnClickListener(this);
		
		//temp 
		for(int i=0;i<8;i++) {
			mAdapter.addItem(new Dictionary());
		}
		view_empty.setVisibility(View.GONE);
	}
	
	private class MyAdapter extends HoldDataBaseAdapter<Dictionary> {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.activity_recharge_list);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Dictionary f = getItem(position);
			holder.fillData(f, position);

			convertView.setBackgroundColor(Color.WHITE);
			return convertView;
		}
	}

	private class ViewHolder {
		RelativeLayout rl_list;
		ImageView iv_rechargetype;
		ImageView iv_radio;
	
		public void fillData(Dictionary f, int position) {

			iv_rechargetype.setImageResource(WalletRechargeActivity.getImageDrawable(position));
			rl_list.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(oldimImageView!=null)
						oldimImageView.setImageResource(R.drawable.radio_no);
					iv_radio.setImageResource(R.drawable.radio_select);
					oldimImageView = iv_radio;

				}
			});
		}
	
		public void findView(View v) {
			rl_list = (RelativeLayout) v.findViewById(R.id.rl_list);
			iv_rechargetype = (ImageView) v.findViewById(R.id.iv_rechargetype);
			iv_radio = (ImageView) v.findViewById(R.id.iv_radio);
		}
	}
	
	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "选择支付方式", null).setShowLogo(false);
	}

	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.rl_walletmoney:
			if(walletselected) {
				walletselected = false;
				ll_remaindall.setVisibility(View.VISIBLE);
				iv_walletsel.setImageResource(R.drawable.radio_no);
				tv_remaindmoney.setText(String.valueOf(paymentMoney));
			} else {
				walletselected = true;
				iv_walletsel.setImageResource(R.drawable.radio_select);
				if(paymentMoney>walletmoney) {
					ll_remaindall.setVisibility(View.VISIBLE);
					tv_remaindmoney.setText(String.valueOf(paymentMoney-walletmoney));
				}
				else {
					ll_remaindall.setVisibility(View.GONE);
				}
			}
			break;
		case R.id.bt_payment:
			String accountString = tv_remaindmoney.getText().toString();
			if(TextUtils.isEmpty(accountString)) {
				ToastUtils.showToast("请输入支付金额！");
				return;
			}
			
			if(!TextUtils.isEmpty(accountString)) {
				double accountnum = Double.valueOf(accountString);
				if(accountnum==0.0) {
					ToastUtils.showToast("支付金额不能为0");
					return;
				}
				accountString = String.valueOf((long)(accountnum*100.0));
			}
			break;
		default:
			break;
		}
	}

}
