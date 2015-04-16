package com.epeisong.ui.activity;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.alipay.sdk.app.PayTask;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.AdjustHeightListView;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.model.PayResult;
import com.epeisong.logistics.common.NumberGenerator;
import com.epeisong.model.Dictionary;
import com.epeisong.model.User;
import com.epeisong.model.Wallet;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.net.ws.utils.WalletResp;
import com.epeisong.utils.AliPayUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 选择充值方式
 * 
 * @author Jack
 * 
 */

public class WalletRechargeActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	private ImageView oldimImageView=null;
	private AdjustHeightListView lv;
	private TextView view_empty;
	private EditText tv_withdrawmoney;
	private Button bt_recharge;
	MyAdapter mAdapter = new MyAdapter();
	private User mUser;
	private Wallet mWallet;
	private static int defaltIndex=0;//默认选择的付款方式
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mWallet = (Wallet) getIntent().getSerializableExtra("wallet");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet_recharge);
		
		mUser = UserDao.getInstance().getUser();
		((TextView) findViewById(R.id.tv_username)).setText(mUser.getShow_name());
		tv_withdrawmoney = (EditText) findViewById(R.id.tv_withdrawmoney);
		lv = (AdjustHeightListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(this);
		lv.setAdapter(mAdapter = new MyAdapter());
		
		view_empty = (TextView) findViewById(R.id.view_empty);
		
		bt_recharge = (Button) findViewById(R.id.bt_recharge);
		bt_recharge.setOnClickListener(this);
		
		findViewById(R.id.traceroute_rootview).setOnClickListener(this);  
		
		//temp 
		for(int i=0;i<1;i++) {
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

	public static int getImageDrawable(int type) {
		switch (type) {
		case 0:
			return R.drawable.recharge_zfb;
		case 1:
			return R.drawable.recharge_weixin;
		case 2:
			return R.drawable.recharge_abc;
		case 3:
			return R.drawable.recharge_icbc;
		case 4:
			return R.drawable.recharge_ccb;
		case 5:
			return R.drawable.recharge_bcm;
		case 6:
			return R.drawable.recharge_boc;
		case 7:
			return R.drawable.recharge_psbc;
		default:
			return R.drawable.recharge_psbc;
			//break;
		}
		//return type;
	}
	
	private class ViewHolder {
		RelativeLayout rl_list;
		ImageView iv_rechargetype;
		ImageView iv_radio;
	
		public void fillData(Dictionary f, final int position) {

			iv_rechargetype.setImageResource(getImageDrawable(position));
			rl_list.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(oldimImageView!=null)
						oldimImageView.setImageResource(R.drawable.radio_no);
					iv_radio.setImageResource(R.drawable.radio_select);
					oldimImageView = iv_radio;
					defaltIndex = position;
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
		return new TitleParams(getDefaultHomeAction(), "选择充值方式", null).setShowLogo(false);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		 case R.id.traceroute_rootview:  
	         InputMethodManager imm = (InputMethodManager)  
	         getSystemService(Context.INPUT_METHOD_SERVICE);  
	         imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0);  
	        break; 
		case R.id.bt_recharge:
			String accountString = tv_withdrawmoney.getText().toString();
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
				if(defaltIndex!=0) {
					ToastUtils.showToast("对不起目前仅支持支付宝充值");
					return;
				}
				aliPay(accountString);
				//accountString = String.valueOf((long)(accountnum*100.0));
			}
			//RechargeWallet(Long.valueOf(accountString), 0);
			break;
		default:
			break;
		}
	}
 
	 // 阿里支付
    private void aliPay(String amount) {
    	StringBuilder bodySb = new StringBuilder();
    	bodySb.append("logisticsId=").append(UserDao.getInstance().getUser().getId()).append("#");
        String trade_no = AliPayUtils.getOutTradeNo();
 
        // 充值
        String orderInfo = AliPayUtils.getWalletInfo( trade_no, 
        		"易配送钱包充值", bodySb.toString(),amount);

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
                PayTask alipay = new PayTask(WalletRechargeActivity.this);
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
                    Toast.makeText(WalletRechargeActivity.this, "充值成功", 
                    		Toast.LENGTH_SHORT).show();
                    Intent aintent = new Intent( );
                    /* 将数据打包到aintent Bundle 的过程略 */
                    setResult(WalletActivity.RECHARGE_BANK,aintent );  
                    finish();
                } else {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        Toast.makeText(WalletRechargeActivity.this, 
                        		"充值结果确认中", Toast.LENGTH_SHORT).show();
                    } else {
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        Toast.makeText(WalletRechargeActivity.this,
                        		"充值失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case SDK_CHECK_FLAG: {
                Toast.makeText(WalletRechargeActivity.this,
                		"检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                break;
            }
        };
    };
    
 
	

    public void RechargeWallet(final long amount, int type) {
        
        AsyncTask<Void, Void, WalletResp> task = new AsyncTask<Void, Void, WalletResp>() {
            @Override
            protected WalletResp doInBackground(Void... params) {
            	ApiExecutor api = new ApiExecutor();
                try {
                	User user=UserDao.getInstance().getUser();
                	
                	return null;//api.withdrawToBankCard(user.getAccount_name(),
                    		//SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), amount, paymentPwd,
                    		//mBankCard.getCardType(), user.getShow_name(), user.getUser_type_code(), mBankCard.getId());

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

        			} else {
        				//ToastUtils.showToast(result.getDesc());
        			}
                }
            }
        };
        task.execute();
    }

}
