/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.ui.dialog.PaymentDialog.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月30日下午6:06:14
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.ui.dialog;

import com.epeisong.R;
import com.epeisong.model.Wallet;
import com.epeisong.payment.net.NetWallet;
import com.epeisong.utils.ToastUtils;

import android.app.Dialog;
import android.content.Context;
import com.epeisong.utils.android.AsyncTask;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PaymentDialog extends Dialog implements OnClickListener {
    
    private Wallet wallet;
    private OnListener listener;
    
    private TextView tv_account_name;
    private TextView tv_balance;
    private TextView tv_pay_amount;
    private EditText et_pay_pwd;
    private Button bt_pay;
    private Button bt_cancel;

    
    private Long payAmount;
    
    public PaymentDialog(Context context) {
        super(context);
    }
    
    public PaymentDialog(Context context,OnListener listener,Long payAmount) {
        super(context);
        this.listener = listener;
        this.payAmount = payAmount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_payment);
        this.setCanceledOnTouchOutside(false);
        
        tv_account_name = (TextView) findViewById(R.id.tv_account_name);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
        tv_pay_amount = (TextView) findViewById(R.id.tv_pay_amount);
        et_pay_pwd = (EditText) findViewById(R.id.et_pay_pwd);
        
        bt_pay = (Button) findViewById(R.id.bt_pay);
        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        
        bt_pay.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        
        tv_pay_amount.setText(String.valueOf(payAmount));

        AsyncTask<Void, Void, Wallet> task = new AsyncTask<Void, Void, Wallet>() {
            @Override
            protected Wallet doInBackground(Void... params) {
                try {
                    NetWallet netWallet = new NetWallet();
                    
                    wallet =  netWallet.getWallet(12);
                } catch (Exception e) {
                }
                return wallet;
            }

            @Override
            protected void onPostExecute(Wallet wallet) {
                tv_account_name.setText(wallet.getWalletName());
                String str = tv_balance.getText().toString();
                tv_balance.setText(str + wallet.getAmount());;
            }
        };
        
        task.execute();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
        case R.id.bt_pay:
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    Boolean result = false;
                    try {
                        NetWallet netWallet = new NetWallet();
                        
                        result =  netWallet.chkPaymentPwd(12, et_pay_pwd.getText().toString());
                    } catch (Exception e) {
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if (!result) {
                        ToastUtils.showToast("支付密码错误");
                        return;
                    }
                    
                    listener.onClick(v,wallet.getId());
                }
            };
            
            task.execute();
            break;

        case R.id.bt_cancel:
            this.dismiss();
            break;
        }
    }
    
    public interface OnListener{   
        public void onClick(View view,Integer walletId);   
    }
    
}

