package com.epeisong.ui.activity;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.AdjustHeightExpandableListView;
import com.epeisong.base.view.SlipButton;
import com.epeisong.base.view.SlipButton.Attr;
import com.epeisong.base.view.SlipButton.SlipButtonChangeListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.model.PayResult;
import com.epeisong.data.net.NetGuarantee;
import com.epeisong.data.net.parser.GuaranteeParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.GuaranteeReq;
import com.epeisong.logistics.proto.Eps.GuaranteeResp;
import com.epeisong.logistics.proto.Eps.GuaranteeReq.Builder;
import com.epeisong.model.Guarantee;
import com.epeisong.model.User;
import com.epeisong.model.Wallet;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.BondWallet;
import com.epeisong.net.ws.utils.BondWalletResp;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.ui.dialog.PayChooseDialog;
import com.epeisong.utils.AliPayUtils;
import com.epeisong.utils.DoubleUtil;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 信息费担保服务
 * 
 * @author Jack
 * 
 */
public class WalletGuaranteeActivity extends BaseActivity implements OnItemClickListener, 
		OnClickListener , SlipButtonChangeListener {
	public static final int GUARANTEE_MONEY_DEPOSIT = 1;
	public static final int GUARANTEE_MONEY_WITHDRAW = 2;
	
	int position = -1;
    private MyAdapter1 mAdapter1;
    private AdjustHeightExpandableListView lv;
	MyAdapter mAdapter = new MyAdapter();
	private User mUser;
	private Wallet wallet;
	//private long walletmoney=0;
	private BondWallet mbondWallet;
	private Boolean inputpass=false;

	private LinearLayout ll_wallet_in;
	private LinearLayout ll_wallet_out;
	private RelativeLayout rl_account_detail;
	private RelativeLayout rl_account_intro;
	
	TextView tv_remaining, tv_freeze;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//wallet = (Wallet) getIntent().getSerializableExtra("wallet");
		//walletmoney = getIntent().getLongExtra("walletmoney", 0);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet_new_guarantee);
		tv_remaining = (TextView) findViewById(R.id.tv_remaining);
		tv_freeze = (TextView) findViewById(R.id.tv_freeze);
		ll_wallet_in = (LinearLayout) findViewById(R.id.ll_wallet_in);
		ll_wallet_out = (LinearLayout) findViewById(R.id.ll_wallet_out);
		rl_account_detail = (RelativeLayout) findViewById(R.id.rl_account_detail);
		rl_account_detail.setOnClickListener(this);
		rl_account_intro = (RelativeLayout) findViewById(R.id.rl_account_intro);
		rl_account_intro.setOnClickListener(this);
		ll_wallet_in.setOnClickListener(this);
		ll_wallet_out.setOnClickListener(this);
		
		mUser = UserDao.getInstance().getUser();
		
		lv = (AdjustHeightExpandableListView) findViewById(R.id.lv_wallet_balance);
		List<String> titles = new ArrayList<String>();
        List<List<String>> lists = new ArrayList<List<String>>();
//        titles.add("车货源信息费担保");
////        titles.add("2");
//        lists.add(titles);

        //lv.setAdapter(mAdapter1 = new MyAdapter1());
        lv.setAdapter(mAdapter1 = new MyAdapter1(titles, lists));
        
        lv.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (lv.isGroupExpanded(groupPosition)) {
                	lv.collapseGroup(groupPosition);
                    position = -1;

                } else {
                    if (position > -1) {
                        lv.collapseGroup(position);
                    }
                    lv.expandGroup(groupPosition);
                    position = groupPosition;
                    lv.setSelectedGroup(position);
                }
                // mAdapter.notifyDataSetChanged();
                return true;
            }
        });
		
        GetBondWallet();
        loadData1(-1, "0", 0, true);
		
	}

    public void GetBondWallet() {
        
        AsyncTask<Void, Void, BondWalletResp> task = new AsyncTask<Void, Void, BondWalletResp>() {
            @Override
            protected BondWalletResp doInBackground(Void... params) {
            	ApiExecutor api = new ApiExecutor();
                try {
                	User user=UserDao.getInstance().getUser();
                    return api.getBondWallet(user.getAccount_name(),
                    		SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                return null;
            }

            @Override
            protected void onPostExecute(BondWalletResp result) {
                dismissPendingDialog();
                if (result==null) {
                	//ToastUtils.showToast("获取失败");
                }else {
        			if (result.getResult() == Resp.SUCC) {
        				//ToastUtils.showToast("获取成功");
                    	mbondWallet = result.getBondWallet();
                    	if(mbondWallet!=null) {
                    		if(mbondWallet.getNormalAmount()!=null)
                    			tv_remaining.setText(String.valueOf(mbondWallet.getNormalAmount()/100.0));
                    		else
                    			tv_remaining.setText("0");
                    		if(mbondWallet.getFrozenAmount()!=null) 
                    			tv_freeze.setText(String.valueOf(mbondWallet.getFrozenAmount()/100.0));
                    		else
                    			tv_freeze.setText("0");
                    	}
        			} else {
        				//ToastUtils.showToast(result.getDesc());
        			}
                }
            }
        };
        task.execute();
    }
    
    public void depositBondWallet(final long amount, final String paymentPwd, final int index ) {
        
        AsyncTask<Void, Void, BondWalletResp> task = new AsyncTask<Void, Void, BondWalletResp>() {
            @Override
            protected BondWalletResp doInBackground(Void... params) {
            	ApiExecutor api = new ApiExecutor();
                try {
                	User user=UserDao.getInstance().getUser();
                	if(index == GUARANTEE_MONEY_DEPOSIT)
                		return api.deposit2BondWalletFromWallet(user.getAccount_name(), 
                    		SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), amount, paymentPwd);//
                	else if(index == GUARANTEE_MONEY_WITHDRAW) {
                		return api.withdraw2WalletFromBondWallet(user.getAccount_name(), 
                        		SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), amount, paymentPwd);//
					}
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                return null;
            }

            @Override
            protected void onPostExecute(BondWalletResp result) {
                dismissPendingDialog();
                if (result==null) {
                	if(index == GUARANTEE_MONEY_DEPOSIT)
                		ToastUtils.showToast("存入失败");
                	else
                		ToastUtils.showToast("取出失败");
                }else {
        			if (result.getResult() == Resp.SUCC) {
        				if(index == GUARANTEE_MONEY_DEPOSIT)
        					ToastUtils.showToast("存入成功");
        				else
        					ToastUtils.showToast("取出成功");
                    	mbondWallet = result.getBondWallet();
                    	//if(wallet!=null)
                    	//	wallet = result.getWallet();
                    	if(mbondWallet!=null) {
                    		if(mbondWallet.getNormalAmount()!=null)
                    			tv_remaining.setText(String.valueOf(mbondWallet.getNormalAmount()/100.0));
                    		else
                    			tv_remaining.setText("0");
                    		if(mbondWallet.getFrozenAmount()!=null)
                    			tv_freeze.setText(String.valueOf(mbondWallet.getFrozenAmount()/100.0));
                    		else
                    			tv_freeze.setText("0");
                    	}
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
//		return new TitleParams(getDefaultHomeAction(), "订车配货保证金", null).setShowLogo(false);
		return new TitleParams(getDefaultHomeAction(), "保证金账户", null).setShowLogo(false);
	}
	
	
	   // 其它支付

    private void otherPayDialog() {

        // 初始化一个自定义的Dialog
    
    }
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_wallet_in:
//		    Dialog dialog = new PayChooseDialog(WalletGuaranteeActivity.this);
//	        dialog.show();
//			if(mbondWallet!=null)
 				getWallet();
//			else {
//				  aliPay("300");
//			}
			break;
		case R.id.ll_wallet_out:
			if(mbondWallet!=null)
				GuaranteeMoney(GUARANTEE_MONEY_WITHDRAW);
			break;
		case R.id.rl_account_detail:
			Intent i = new Intent(getApplicationContext(), WalletGuarateeDetailActivity.class);
			i.putExtra("bondwallet", mbondWallet);
			startActivity(i);
			break;
		case R.id.rl_account_intro:
			Intent intent = new Intent(getApplicationContext(), GuaranteeIntroActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
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
                    int operationStatus = resp.getResult();
                    switch (operationStatus) {
                    case Properties.GET_WALLET_STATUS_NOMRAL:
                        if (wallet.getStatus() == 1) {
                            ToastUtils.showToast("请先激活钱包");
                            break;
                        } else if (wallet.getStatus() == 4) {
                            ToastUtils.showToast("钱包已停用，请联系客服");
                            break;
                        } else if (wallet.getStatus() != 2) {
                            ToastUtils.showToast("钱包已冻结，请先解冻");//请联系客服");
                            break;
                        } else {
                        	if(wallet!=null)
                        		GuaranteeMoney(GUARANTEE_MONEY_DEPOSIT);
						}
                        break;
                    case Properties.GET_WALLET_STATUS_NO_WALLET:
                         ToastUtils.showToast("请联系客服绑定钱包");
                     
                        break;
                    default:
                        ToastUtils.showToast("无法获取钱包信息");
                        break;
                    }
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
    
    // 阿里支付
    private void aliPay(String amount) {
    	StringBuilder bodySb = new StringBuilder();
    	bodySb.append("logisticsId=").append(UserDao.getInstance().getUser().getId()).append("#");
        String trade_no = AliPayUtils.getOutTradeNo();
 
        // 充值
        String orderInfo = AliPayUtils.getBondInfo( trade_no, 
        		"易配送保证金充值", bodySb.toString(),amount);

        String sign = "";
        try {
            // 对订单做RSA 签名
            sign = AliPayUtils.sign(orderInfo);
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showToast("支付宝暂时未开通");
            return;
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + AliPayUtils.getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(WalletGuaranteeActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                aliHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
    private static final int SDK_PAY_FLAG = 1; // 支付

    private static final int SDK_CHECK_FLAG = 2;// 检测
    private Handler aliHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SDK_PAY_FLAG: {
                PayResult payResult = new PayResult((String) msg.obj);
 
                String resultInfo = payResult.getResult();
                String resultStatus = payResult.getResultStatus();
                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    Toast.makeText(WalletGuaranteeActivity.this, "充值成功", 
                    		Toast.LENGTH_SHORT).show();
                } else {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        Toast.makeText(WalletGuaranteeActivity.this, 
                        		"充值结果确认中", Toast.LENGTH_SHORT).show();
                    } else {
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        Toast.makeText(WalletGuaranteeActivity.this,
                        		"充值失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case SDK_CHECK_FLAG: {
                Toast.makeText(WalletGuaranteeActivity.this,
                		"检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                break;
            }
        };
    };
    
//	@Override
//	public void onBackPressed() {
//        Intent intent = new Intent();
//        intent.putExtra("wallet", wallet);
//        intent.putExtra("walletmoney", walletmoney);
//        setResult(RESULT_OK, intent);
//		super.onBackPressed();
//	}
	
	void GuaranteeMoney( final int index ) {
		final AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
		LayoutInflater factory = LayoutInflater.from(this);  
		View view = factory.inflate(R.layout.dialog_guarantee, null);  
		// builder.setIcon(R.drawable.icon);  
		//builder.setTitle("自定义输入框");  
		final EditText et_card = (EditText) view.findViewById(R.id.et_card);
		final EditText et_password = (EditText) view.findViewById(R.id.et_password);

		builder.setView(view);  
		if(index==GUARANTEE_MONEY_DEPOSIT) {
			if(inputpass==false) {
				view.findViewById(R.id.ll_inputpass).setVisibility(View.GONE);
				et_password.setText("111111");
			}
			((TextView) view.findViewById(R.id.et_name)).setText("\u3000"+"钱包余额：");
			((TextView) view.findViewById(R.id.tv_point)).setText("存入保证金：");
			((TextView) view.findViewById(R.id.et_payname)).setText("\u3000"+"支付密码：");
			((TextView) view.findViewById(R.id.et_num)).setText(DoubleUtil.moneyFormatNoE(wallet.getAmount()/100.0)+" 元");
			
		} else {
			view.findViewById(R.id.ll_inputpass).setVisibility(View.GONE);
			((TextView) view.findViewById(R.id.et_name)).setText("\u3000"+"可用余额：");
			((TextView) view.findViewById(R.id.tv_point)).setText("取出保证金：");
			if(mbondWallet==null || mbondWallet.getNormalAmount()==null)
				((TextView) view.findViewById(R.id.et_num)).setText("0"+" 元");
			else
				((TextView) view.findViewById(R.id.et_num)).setText(mbondWallet.getNormalAmount()/100.0+" 元");
		}
		
		et_card.addTextChangedListener(new TextWatcher() 
		{
			public void afterTextChanged(Editable s) 
			{
				String temp = s.toString();
				int d = temp.indexOf(".");
				if (d < 0) return;
				if (temp.length() - d - 1 > 2){
					s.delete(d + 3, d + 4);
				}else if (d==0) {
					s.delete(d, d+1);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}
		});
		
		
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
				String passwords = et_password.getText().toString();
				
				if(!TextUtils.isEmpty(accountString)) {
					double accountnum = Double.valueOf(accountString);
					if(accountnum==0.0) {
						if(index == GUARANTEE_MONEY_DEPOSIT) {
							ToastUtils.showToast("存入金额不能为0");
						} else {
							ToastUtils.showToast("取出金额不能为0");
						}
						return;
					}
					accountString = String.valueOf((long)(accountnum*100.0));
				}
				
				if (!TextUtils.isEmpty(accountString) && index == GUARANTEE_MONEY_DEPOSIT && !TextUtils.isEmpty(passwords)) {
					depositBondWallet(Long.valueOf(accountString), passwords, GUARANTEE_MONEY_DEPOSIT);
					dialog.dismiss();
				}
				else if (!TextUtils.isEmpty(accountString) && index == GUARANTEE_MONEY_WITHDRAW) {
					depositBondWallet(Long.valueOf(accountString), "111111", GUARANTEE_MONEY_WITHDRAW);
					dialog.dismiss();
				} else {
					
					if(index == GUARANTEE_MONEY_DEPOSIT) {
						ToastUtils.showToast("请输入存入金额");
					} else {
						ToastUtils.showToast("请输入取出金额");
					}
				}
			}
		});
	}
	
	private class MyAdapter extends HoldDataBaseAdapter<Guarantee> {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.activity_guarantee_list);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Guarantee f = getItem(position);
			holder.fillData(f);

			if (f.getStatus() == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL) {
				convertView.setEnabled(true);
			} else {
				convertView.setEnabled(false);
			}
			holder.enable(f.getStatus() == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL);
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView iv_guarantee;
		TextView tv_name;
		TextView tv_type, tv_money;
		public SlipButton cb_switch;
		//ImageView iv_state;
	
        public void enable(boolean enabled) {
        	tv_name.setEnabled(enabled);
        	iv_guarantee.setEnabled(enabled);
        }
        
		public void fillData(Guarantee f) {
	        if (!TextUtils.isEmpty(f.getMark_url1())) {
	            ImageLoader.getInstance().displayImage(f.getMark_url1(), iv_guarantee);
	        }
			//iv_guarantee.setImageResource(R.drawable.productmoney);
			tv_name.setText(f.getName());
			if(f.getType()==0) {
				tv_type.setText("车源货源保证金");//f.getTypeName());
			}
			tv_money.setText(String.valueOf((long)(f.getAccount()/100.0))+"元");
			if (f.getStatus() == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL) {
				cb_switch.setDefaultOpen(true);
			} else {
				cb_switch.setDefaultOpen(false);
			}
			
			cb_switch.setTag(f);
		}
	
		public void findView(View v) {
			iv_guarantee = (ImageView) v.findViewById(R.id.iv_guarantee);
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_type = (TextView) v.findViewById(R.id.tv_type);
			tv_type.setVisibility(View.GONE);
			tv_money = (TextView) v.findViewById(R.id.tv_money);
			//iv_state = (ImageView) v.findViewById(R.id.iv_state);
			cb_switch = (SlipButton) v.findViewById(R.id.iv_switch);
			cb_switch.SetOnChangedListener(WalletGuaranteeActivity.this);
		}
	}
	
	private void loadData1(final int size, final String edge_id, final double weight, final boolean bFirst) {
		AsyncTask<Void, Void, List<Guarantee>> task = new AsyncTask<Void, Void, List<Guarantee>>() {
			@Override
			protected List<Guarantee> doInBackground(Void... params) {

				NetGuarantee netGuarantee = new NetGuarantee() {

					@Override
					protected int getCommandCode() {
						return CommandConstants.LIST_GUARANTEE_PRODUCT_CUSTOMER_REQ;//LIST_GUARANTEE_PRODUCT_REQ;
					}

					@Override
					protected boolean onSetRequest(Builder req) {
						req.setLimitCount(size);
                        //req.setStatus(0);//all
                        //req.setId(id);
                        req.setCustomerId(Integer.valueOf(mUser.getId()));
						req.setGuaranteeId(Integer.valueOf(300001));
						//req.setProductType(0);
						return true;
					}
				};
				try {
					com.epeisong.logistics.proto.Eps.GuaranteeResp.Builder resp = netGuarantee.request();
					return GuaranteeParser.parse(resp);
				} catch (NetGetException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<Guarantee> result) {
				
				if (result != null) {
					if (result.isEmpty()) {
						if (bFirst) {
						} else {
						}
					} else {
						if (bFirst) {
							int i;
							//result.add(result.get(0));
							int count = result.size();
							
							List<String> titles = new ArrayList<String>();
					        List<List<String>> lists = new ArrayList<List<String>>();
					        for(i=0;i<count;i++) {
						        titles.add("车货源信息费担保");
						        lists.add(titles);
					        }
					        
							mAdapter1.replace(titles, lists, result);
								//mAdapter1.replaceAll(result);
							mAdapter1.notifyDataSetChanged();
							//lv.notifyAll();
						} else {
							//mAdapter1.addAll(result);
						}
					}
				}
			}
		};
		task.execute();
	}
	
	@Override
	public void OnChanged(boolean CheckState, SlipButton btn) {
		Object tag = btn.getTag();
		if (tag != null && tag instanceof Guarantee) {
			changeState((Guarantee) tag, btn);
		}
	}

    private void changeState(final Guarantee f, final SlipButton btn) {
        final int curStatus = f.getStatus();

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
        	@Override
        	protected Boolean doInBackground(Void... params) {
        		NetGuarantee net = new NetGuarantee() {
        			@Override
        			protected int getCommandCode() {
        				return CommandConstants.CREATE_GUARANTEE_PRODUCT_ORDER_REQ;
        			}
        			@Override
        			protected boolean onSetRequest(GuaranteeReq.Builder req) {
                        req.setProductId(Integer.parseInt(f.getId()));
                        req.setCustomerId(Integer.valueOf(mUser.getId()));
        				return true;
        			}
        		};

        		try {
        			GuaranteeResp.Builder resp = net.request();
        			if (resp == null) {
        				return null;
        			}
        			return true;
        		} catch (NetGetException e) {
        			e.printStackTrace();
        		}
        		return null;                	
        	}

        	protected void onPostExecute(Boolean result) {
        		if (result) {
                    if (curStatus == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL) {
                        f.setStatus(Properties.GUARANTEE_PRODUCT_STATUS_INVALID);
                    } else {
                        f.setStatus(Properties.GUARANTEE_PRODUCT_STATUS_NORMAL);
                    }
                    mAdapter.notifyDataSetChanged();
        		}
        		else {
        			btn.toggle();
				}
        	}
        };
        task.execute();
    }
    
//    private void toggleSlipButton(final SlipButton btn) {
//        HandlerUtils.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                btn.toggle();
//            }
//        }, 200);
//    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}

    private class MyAdapter1 extends BaseExpandableListAdapter {

        private List<Guarantee> glists;//titles;
        private List<String> titles;
        private List<List<String>> lists;

        //@SuppressWarnings("unused")
        public MyAdapter1(List<String> titles, List<List<String>> lists) {
            this.titles = titles;
            this.lists = lists;
        }

        public void replace(List<String> titles, List<List<String>> lists, List<Guarantee> glists) {
            this.titles = titles;
            this.lists = lists;
            this.glists = glists;
            this.notifyDataSetChanged();
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return lists.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;//lists.get(groupPosition).size();
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {
        	Guarantee mguarantee=null;
        	if(glists!=null)
        		mguarantee = glists.get(groupPosition);
            TextView tv_content;

            View view = null;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.item_consulting_to_help_child, null);
                view.setBackgroundColor(Color.parseColor("#f4f8fa"));
            } else {
                view = convertView;
            }
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            if(mguarantee!=null)
            	tv_content.setText(mguarantee.getIntroduce());
            tv_content.setTextSize(16);
            tv_content.setTextColor(Color.parseColor("#7d7e80"));
            return view;
        }

        @Override
        public String getGroup(int groupPosition) {
            return titles.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return titles.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        	Guarantee s=null;
        	if(glists!=null)
        		s = glists.get(groupPosition);
            TextView tv_info_guaratee;
            TextView tv_info_money;
            SlipButton cb_guaratee;
    		ImageView iv_guarantee;

            View view = null;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.activity_wallet_guaratee_item, null);
                view.setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                view = convertView;
            }
            iv_guarantee = (ImageView) view.findViewById(R.id.iv_guarantee);
	        
            cb_guaratee = (SlipButton) view.findViewById(R.id.cb_guaratee);
            tv_info_guaratee = (TextView) view.findViewById(R.id.tv_info_guaratee);
            tv_info_money = (TextView) view.findViewById(R.id.tv_info_money);
            if(s!=null)
            {
            	tv_info_guaratee.setText(s.getName());
            	tv_info_money.setText(String.valueOf((long)(s.getAccount()/100.0))+"元/票");
            }
            cb_guaratee.setAttr(new Attr().setOpenBgResId(R.drawable.wallet_open_bg).setCloseBgResId(R.drawable.wallet_close_bg));
            cb_guaratee.SetOnChangedListener(WalletGuaranteeActivity.this);
            if(s!=null)
            {
    	        if (!TextUtils.isEmpty(s.getMark_url2())) {
    	            ImageLoader.getInstance().displayImage(s.getMark_url2(), iv_guarantee);
    	        }
    	        
				if (s.getStatus() == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL) {
					cb_guaratee.setDefaultOpen(true);
				} else {
					//cb_switch.setEnabled(false);
					cb_guaratee.setDefaultOpen(false);
				}
			
				cb_guaratee.setTag(s);
            }
            
            if (groupPosition == position) {
                lv.expandGroup(groupPosition);
            } else {
                lv.collapseGroup(groupPosition);
            }

            return view;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
