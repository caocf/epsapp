package com.epeisong.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.User;
import com.epeisong.model.Wallet;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.BankCard;
import com.epeisong.net.ws.utils.BankCardResp;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.net.ws.utils.WalletResp;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.DoubleUtil;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
/**
 * 我的钱包
 * 
 * @author Jack
 * 
 */
public class WalletActivity extends BaseActivity implements OnItemClickListener, OnClickListener {
    public static final int GUARANTEE_RESULTUPDATE = 101;
    public static final int RECHARGE_BANK = 102;
    public static final int WITHDRAWAL_BANK = 103;
    public static final int FREEZE_WALLET = 105;
    public static final int THAW_WALLET = 106;
    private ListView mListView;
    private MyAdapter mAdapter;
    private TextView tv_balacne;
    private TextView tv_yuan;
    private TextView tv_name;
    private TextView tv_text;
    private TextView tv_refresh, btn_thaw;
    private LinearLayout ll_wallet_in;
    private LinearLayout ll_wallet_out;

    private BankCard mBankCard;
    private Wallet wallet;
    private int operationStatus = -1;

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "我的钱包", null).setShowLogo(false);
    }

    @Override
    public void onClick(View v) {
    	Intent intent;
        switch (v.getId()) {
        case R.id.ll_wallet_in:
        	if (checkWalletStatus() < 0)
        		return;
            intent = new Intent(getApplicationContext(), WalletRechargeActivity.class);
            intent.putExtra("wallet", wallet);
            startActivityForResult(intent, RECHARGE_BANK);
            
//            intent = new Intent(getApplicationContext(), PaymentSelectActivity.class);
//            intent.putExtra("wallet", wallet);
//            intent.putExtra("paymentmoney", 50000);
//            startActivityForResult(intent, RECHARGE_BANK);
            break;
        case R.id.ll_wallet_out:
        	if (checkWalletStatus() < 0)
        		return;
        	intent = new Intent(getApplicationContext(), WalletWithdrawalActivity.class);
        	intent.putExtra("wallet", wallet);
            startActivityForResult(intent, WITHDRAWAL_BANK);
        	//WalletOutMoney();
            break;
        case R.id.tv_name:
            intent = new Intent(getApplicationContext(), WalletPersonalInfoActivity.class);
            intent.putExtra("wallet", wallet);
            startActivity(intent);
            break;
        case R.id.tv_balacne:
        	intent = new Intent(getApplicationContext(), OpenWalletActivity.class);
        	intent.putExtra("wallet", wallet);
            startActivityForResult(intent, 0);
            // startActivity(i);
            break;
        case R.id.btn_refresh:
        	getWallet();
        	break;
        case R.id.btn_thaw:
        	intent = new Intent(WalletActivity.this, FreezeWalletActivity.class);
        	intent.putExtra("walletstate", 1);
            startActivityForResult(intent, WalletActivity.THAW_WALLET);
        	break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
        	if (requestCode == WalletActivity.FREEZE_WALLET) {
            	final Serializable serializable = data.getSerializableExtra("wallet");
            	if(serializable!=null) {
            		wallet=(Wallet)serializable;
            		operationStatus = Properties.GET_WALLET_STATUS_NOMRAL;
            		SetWalletFreeze();
            	} else 
            		getWallet();
        		return;
        	} else if(requestCode == WalletActivity.THAW_WALLET) {
            	final Serializable serializable = data.getSerializableExtra("wallet");
            	if(serializable!=null) {
            		wallet=(Wallet)serializable;
            		operationStatus = Properties.GET_WALLET_STATUS_NOMRAL;
            		SetWalletNormal();
            	} else 
            		getWallet();
        		return;
        	}
        	
            if (requestCode == WITHDRAWAL_BANK) {
            	final Serializable serializable = data.getSerializableExtra("wallet");
            	if(serializable!=null) {
            		wallet=(Wallet)serializable;
            		tv_balacne.setText(DoubleUtil.moneyFormatNoE(wallet.getAmount()/100.0));
            	}
                return;
            }
            
            if (requestCode == GUARANTEE_RESULTUPDATE) {
            	final Serializable serializable = data.getSerializableExtra("wallet");
            	if(serializable!=null) {
            		wallet=(Wallet)serializable;
            		tv_balacne.setText(DoubleUtil.moneyFormatNoE(wallet.getAmount()/100.0));
            	}
            	//getWallet();
                //long walletmoney = (long) data.getLongExtra("walletmoney", 0);
                //walletmoney += 1;
                return;
            }

            wallet = (Wallet) data.getSerializableExtra("wallet");
            if (wallet != null) {
                tv_name.setVisibility(View.VISIBLE);
                tv_yuan.setVisibility(View.VISIBLE);
                tv_text.setVisibility(View.GONE);
                tv_balacne.setVisibility(View.VISIBLE);
                tv_balacne.setText(DoubleUtil.moneyFormatNoE(wallet.getAmount()/100.0));
                tv_name.setText(wallet.getRealName());
                tv_balacne.setTextSize(40);
                tv_balacne.setBackgroundResource(0);
                tv_name.setOnClickListener(WalletActivity.this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallet);
        mListView = (ListView) findViewById(R.id.lv_wallet_balance);
        mListView.setOnItemClickListener(this);
        ll_wallet_in = (LinearLayout) findViewById(R.id.ll_wallet_in);
        ll_wallet_out = (LinearLayout) findViewById(R.id.ll_wallet_out);
        tv_balacne = (TextView) findViewById(R.id.tv_balacne);
        tv_yuan = (TextView) findViewById(R.id.tv_yuan);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_text = (TextView) findViewById(R.id.tv_text);
        tv_refresh = (TextView) findViewById(R.id.btn_refresh);
        btn_thaw = (TextView) findViewById(R.id.btn_thaw);
        tv_name.setVisibility(View.GONE);
        tv_yuan.setVisibility(View.GONE);
        tv_text.setVisibility(View.GONE);
        tv_refresh.setVisibility(View.GONE);
        btn_thaw.setOnClickListener(this);
        ll_wallet_in.setOnClickListener(this);
        ll_wallet_out.setOnClickListener(this);

        List<Item> data = new ArrayList<WalletActivity.Item>();

        data.add(new Item(R.drawable.mine_wallet_payment, "收支明细", new Runnable() {
            @Override
            public void run() {
                if (checkWalletStatus() < 0)
                    return;
                Intent intent = new Intent(WalletActivity.this,
                		WalletDetailActivity.class);
                intent.putExtra("wallet", wallet);
                startActivity(intent);
                
            }
        }));
        data.add(new Item(R.drawable.mine_wallet_card, "我的银行卡", new Runnable() {
            @Override
            public void run() {
                if (checkWalletStatus() < 0)
                    return;
                 //ToastUtils.showToast("即将上线，敬请期待");
                 Intent intent = new Intent(WalletActivity.this,
                 WalletBankActivity.class);
                 intent.putExtra(WalletBankActivity.EXTRA_WALLET, wallet);
                 startActivity(intent);
            }
        }));
        data.add(new Item(R.drawable.mine_wallet_safe, "支付安全", new Runnable() {
            @Override
            public void run() {
                if (checkWalletStatus() < 0)
                    return;
                Intent intent = new Intent(getApplicationContext(), PaymentSecurity.class);
                startActivityForResult(intent, FREEZE_WALLET);
                //startActivity(intent);
            }
        }));
        //data.add(new Item());
//        data.add(new Item(R.drawable.mine_wallet_guarantee, "订车配货保证金", new Runnable() {
//            @Override
//            public void run() {
//                if (checkWalletStatus() < 0)
//                    return;
//                Intent intent = new Intent(WalletActivity.this, WalletGuaranteeActivity.class);
//                // startActivity(intent);
//                intent.putExtra("wallet", wallet);
//               	intent.putExtra("walletmoney", wallet.getAmount()/100.0);
//                startActivityForResult(intent, GUARANTEE_RESULTUPDATE);
//            }
//        }));
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mAdapter.replaceAll(data);

        getWallet();
    }

    private void getWallet() {

        final AsyncTask<Void, Void, com.epeisong.net.ws.utils.WalletResp> task = new AsyncTask<Void, Void, com.epeisong.net.ws.utils.WalletResp>() {
            @Override
            protected com.epeisong.net.ws.utils.WalletResp doInBackground(Void... params) {
                User user = UserDao.getInstance().getUser();
                String pwd = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null);
                try {
                    return new ApiExecutor().getWallet(user.getPhone(), pwd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(com.epeisong.net.ws.utils.WalletResp resp) {
            	dismissPendingDialog();
                if (resp != null) {
                	wallet = resp.getWallet();
                    operationStatus = resp.getResult();
                    switch (operationStatus) {
                    case Properties.GET_WALLET_STATUS_NOMRAL:
//                        wallet = resp.getWallet();
                        // 待开通
                        if (wallet.getStatus() == 1) {
                            tv_balacne.setVisibility(View.VISIBLE);
                            tv_balacne.setText("激活钱包");
                            tv_balacne.setTextSize(24);
                            tv_balacne.setOnClickListener(WalletActivity.this);
                            tv_name.setVisibility(View.GONE);
                            tv_yuan.setVisibility(View.GONE);
                            tv_text.setVisibility(View.GONE);
                            tv_refresh.setVisibility(View.GONE);
                        } else if (wallet.getStatus() == 2) { // 正常
                        	SetWalletNormal();
                        } else if (wallet.getStatus() == 4) { // 正常
                        	tv_balacne.setText("您的钱包已停用，请联系客服");
                            tv_balacne.setVisibility(View.VISIBLE);
                            tv_balacne.setBackgroundResource(0);
                            tv_balacne.setTextSize(24);
                            tv_name.setVisibility(View.GONE);
                            tv_yuan.setVisibility(View.GONE);
                            tv_text.setVisibility(View.GONE);
                            tv_refresh.setVisibility(View.GONE);
                            btn_thaw.setVisibility(View.GONE);
                        } else {
                        	
                        	SetWalletFreeze();
                        }
                        break;
                    case Properties.GET_WALLET_STATUS_NO_WALLET:
                        tv_balacne.setVisibility(View.VISIBLE);
                        tv_balacne.setText("请联系客服绑定钱包");
                        tv_balacne.setBackgroundResource(0);
                        tv_balacne.setTextSize(24);
                        tv_name.setVisibility(View.GONE);
                        tv_yuan.setVisibility(View.GONE);
                        tv_text.setVisibility(View.GONE);
                        tv_refresh.setVisibility(View.GONE);
                        break;

                    default:
                        tv_balacne.setVisibility(View.VISIBLE);
                        tv_balacne.setText("无法获取钱包信息");
                        tv_balacne.setBackgroundResource(0);
                        tv_balacne.setTextSize(24);
                        tv_name.setVisibility(View.GONE);
                        tv_yuan.setVisibility(View.GONE);
                        tv_text.setVisibility(View.GONE);
                        tv_refresh.setVisibility(View.GONE);
                        break;
                    }

                } else {
                    tv_balacne.setVisibility(View.VISIBLE);
                    tv_balacne.setText("无法获取钱包信息");
                    tv_balacne.setTextSize(24);
                    tv_name.setVisibility(View.GONE);
                    tv_yuan.setVisibility(View.GONE);
                    tv_text.setVisibility(View.GONE);
                    tv_refresh.setVisibility(View.GONE);
                    tv_balacne.setBackgroundResource(0);
                }
            }

        };
        showPendingDialog(null, new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                task.cancel(true);
            }
        });
        task.execute();

    }

    public void SetWalletFreeze() {
        tv_balacne.setVisibility(View.VISIBLE);
        tv_balacne.setText("钱包已冻结");//，请联系客服");
        tv_balacne.setBackgroundResource(0);
        tv_balacne.setTextSize(24);
        tv_name.setVisibility(View.GONE);
        tv_yuan.setVisibility(View.GONE);
        tv_text.setVisibility(View.GONE);
        tv_refresh.setVisibility(View.GONE);
        btn_thaw.setVisibility(View.VISIBLE);
    }
    
    public void SetWalletNormal() {
	    tv_balacne.setVisibility(View.VISIBLE);
	    tv_name.setVisibility(View.VISIBLE);
	    tv_yuan.setVisibility(View.VISIBLE);
	    tv_refresh.setVisibility(View.VISIBLE);
	    tv_text.setVisibility(View.GONE);
	    if(wallet.getAmount() == null){
	    	tv_balacne.setText(String.valueOf(0));
	    }else{
	    	//tv_balacne.setText(String.valueOf(wallet.getAmount()/100.0));
	    	tv_balacne.setText(DoubleUtil.moneyFormatNoE(wallet.getAmount()/100.0));
	    }
	    tv_name.setText(wallet.getRealName());
	    tv_balacne.setTextSize(40);
	    tv_balacne.setBackgroundResource(0);
	    btn_thaw.setVisibility(View.GONE);
	    tv_name.setOnClickListener(WalletActivity.this);
	    tv_refresh.setOnClickListener(WalletActivity.this);
    }
    
	void WalletOutMoney()
	{
		final AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
		LayoutInflater factory = LayoutInflater.from(this);  
		View view = factory.inflate(R.layout.dialog_guarantee, null);  
		final EditText et_card = (EditText) view.findViewById(R.id.et_card);
		final EditText et_password = (EditText) view.findViewById(R.id.et_password);
		
		builder.setView(view);  
			//view.findViewById(R.id.ll_inputpass).setVisibility(View.GONE);
			((TextView) view.findViewById(R.id.et_name)).setText("可用余额：");
			((TextView) view.findViewById(R.id.tv_point)).setText("提取现金：");
			if(wallet==null)
				((TextView) view.findViewById(R.id.et_num)).setText("0"+" 元");
			else
				((TextView) view.findViewById(R.id.et_num)).setText(DoubleUtil.moneyFormatNoE(wallet.getAmount()/100.0)+" 元");
			((TextView) view.findViewById(R.id.et_payname)).setText("支付密码：");

		
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
				
					getBankCard(Long.valueOf(accountString), passString);
					//depositWallet(Long.valueOf(accountString), "", 0);
					dialog.dismiss();
			}
		});
	}
	
	public void getBankCard(final long amount, final String passstring) {
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
						mBankCard = result.getBankCardList().get(0);
						depositWallet(amount, passstring, 0);
						
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
	
    public void depositWallet(final long amount, final String paymentPwd, final int index ) {
        
        AsyncTask<Void, Void, WalletResp> task = new AsyncTask<Void, Void, WalletResp>() {
            @Override
            protected WalletResp doInBackground(Void... params) {
            	ApiExecutor api = new ApiExecutor();
                try {
                	User user=UserDao.getInstance().getUser();
                	
                		return api.withdraw(user.getAccount_name(),
                        		SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), amount, paymentPwd,
                        		mBankCard.getCardType(), mBankCard.getOpenBankName(), mBankCard.getBankCode(), mBankCard.getBankName(),
                        		mBankCard.getRegionCode(), mBankCard.getRegionName(), mBankCard.getRealName(), mBankCard.getCardNumber());
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
                    	wallet = result.getWallet();
                    	
                        if(wallet.getAmount() == null){
                        	tv_balacne.setText(String.valueOf(0));
                        }else{
                        	tv_balacne.setText(DoubleUtil.moneyFormatNoE(wallet.getAmount()/100.0));
                        }

        			} else {
        				ToastUtils.showToast(result.getDesc());
        			}
                }
            }
        };
        task.execute();
    }
    
    private int checkWalletStatus() {
        switch (operationStatus) {
        case Properties.GET_WALLET_STATUS_NOMRAL:
            if (wallet.getStatus() == 1) {
                ToastUtils.showToast("请先激活钱包");
                return -1;
            } else if (wallet.getStatus() == 4) {
                ToastUtils.showToast("钱包已停用，请联系客服");
                return -1;
            } else if (wallet.getStatus() != 2) {
                ToastUtils.showToast("钱包已冻结，请先解冻");//请联系客服");
                return -1;
            }
            break;
        case Properties.GET_WALLET_STATUS_NO_WALLET:
            ToastUtils.showToast("请联系客服绑定钱包");
            return -1;
        default:
            ToastUtils.showToast("无法获取钱包信息");
            return -1;
        }
        return 1;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= mListView.getHeaderViewsCount();

        if (position >= 0) {
            Item item = mAdapter.getItem(position);
            if (item.getRunnable() != null) {
                view.post(item.getRunnable());
            }
        }
    }

    private class MyAdapter extends HoldDataBaseAdapter<Item> {
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getType();
        }

        @Override
        public boolean isEnabled(int position) {
            return getItem(position).getType() != Item.type_invalid;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Item last = null;
            if (position > 0) {
                last = getItem(position - 1);
            }
            Item item = getItem(position);
            if (item.getType() == Item.type_invalid) {
                View v = new View(getApplicationContext());
                int h = (int) DimensionUtls.getPixelFromDp(15);
                v.setLayoutParams(new AbsListView.LayoutParams(-1, h));
                v.setBackgroundColor(Color.argb(0xED, 0xED, 0xED, 0xED));
                return v;
            }
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.fragment_mine_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            boolean hideLine = last != null && last.getType() == Item.type_invalid;
            holder.fillData(!hideLine, item);
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
        ImageView iv_new;
        View line;

        public void findView(View v) {
            iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_desc = (TextView) v.findViewById(R.id.tv_desc);
            iv_new = (ImageView) v.findViewById(R.id.iv_point);
            line = v.findViewById(R.id.line);

        }

        public void fillData(boolean showLine, Item item) {
            tv_desc.setVisibility(View.INVISIBLE);
            iv_icon.setImageResource(item.getResId());
            tv_name.setText(item.getName());
            if (item.hasNewMsg) {
                iv_new.setVisibility(View.VISIBLE);
            } else {
                iv_new.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(item.getDesc())) {
                tv_desc.setVisibility(View.VISIBLE);
                tv_desc.setText(item.getDesc());
            }
            if (showLine) {
                line.setVisibility(View.VISIBLE);
            } else {
                line.setVisibility(View.GONE);
            }
        }
    }

    public static class Item {

        public static final int type_invalid = -1;
        public static final int type_normal = 0;

        private int type;
        private int resId;
        private String name;
        private String desc;
        private Runnable runnable;
        private boolean hasNewMsg;

        public Item() {
            this.type = type_invalid;
        }

        public Item(int resId, String name, Runnable runnable) {
            super();
            this.type = type_normal;
            this.resId = resId;
            this.name = name;
            this.runnable = runnable;
        }

        public int getType() {
            return type;
        }

        public Item setType(int type) {
            this.type = type;
            return this;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public Item setDesc(String desc) {
            this.desc = desc;
            return this;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public Item setRunnable(Runnable runnable) {
            this.runnable = runnable;
            return this;
        }

        public boolean isHasNewMsg() {
            return hasNewMsg;
        }

        public Item setHasNewMsg(boolean hasNewMsg) {
            this.hasNewMsg = hasNewMsg;
            return this;
        }
    }

}
