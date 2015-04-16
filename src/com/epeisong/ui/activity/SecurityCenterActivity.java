package com.epeisong.ui.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.net.NetGetWallet;
import com.epeisong.data.net.parser.WalletParser;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Wallet.WalletResp;
import com.epeisong.logistics.proto.Wallet.WalletResp.Builder;
import com.epeisong.model.User;
import com.epeisong.model.Wallet;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 
 * @author gnn 2014/12/10
 *
 * @param <SwitchBt>
 */
public class SecurityCenterActivity<SwitchBt> extends BaseActivity implements OnClickListener {

    // private SwitchButton switch_button;

    private RelativeLayout llt_xiugaimima;
    private RelativeLayout llt_wangjimima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_center);
        llt_xiugaimima = (RelativeLayout) findViewById(R.id.llt_xiugaimima);
        llt_wangjimima = (RelativeLayout) findViewById(R.id.llt_wangjimima);
        llt_xiugaimima.setOnClickListener(this);
        llt_wangjimima.setOnClickListener(this);
        findViewById(R.id.rl_change_phone).setOnClickListener(this);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "安全中心", null).setShowLogo(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.llt_xiugaimima:
            Intent i1 = new Intent(getApplicationContext(), ChangeLoginPwdActivity.class);
            startActivityForResult(i1, 1);
            break;

        case R.id.llt_wangjimima:
            Intent i2 = new Intent(getApplicationContext(), ForgetLoginPwdActivity.class);
            i2.putExtra(ForgetLoginPwdActivity.EXTRA_PHONE, UserDao.getInstance().getUser().getPhone());
            startActivity(i2);
            break;

        case R.id.rl_change_phone:
            checkWalletHttp();
            break;

        }
    }

    private void checkWalletHttp() {
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
                    boolean isWalletOpened = false;
                    Wallet wallet = resp.getWallet();
                    if (wallet != null && wallet.getStatus() != 1) {
                        isWalletOpened = true;
                    }
                    Intent intent = new Intent(SecurityCenterActivity.this, ChangePhoneActivity.class);
                    intent.putExtra(ChangePhoneActivity.EXTRA_OLD_PHONE, UserDao.getInstance().getUser().getPhone());
                    intent.putExtra(ChangePhoneActivity.EXTRA_IS_WALLET_OPENED, isWalletOpened);
                    startActivity(intent);
                } else {
                    ToastUtils.showToast("数据获取失败");
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

    private void checkWallet() {
        final AsyncTask<Void, Void, Builder> task = new AsyncTask<Void, Void, Builder>() {
            @Override
            protected Builder doInBackground(Void... params) {
                NetGetWallet net = new NetGetWallet() {

                    @Override
                    protected boolean onSetRequest(com.epeisong.logistics.proto.Wallet.WalletReq.Builder req) {
                        req.setLogisticsId(1);
                        return true;
                    }
                };
                try {
                    WalletResp.Builder response = net.request();
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Builder response) {
                dismissPendingDialog();
                if (response != null && "SUCC".equals(response.getResult())) {
                    boolean isWalletOpened = false;
                    switch (response.getStatus()) {
                    case Properties.GET_WALLET_STATUS_NOMRAL:
                        Wallet wallet = WalletParser.parser(response.getWallet());
                        if (wallet.getStatus() != 1) {
                            isWalletOpened = true;
                        }
                        break;
                    }
                    Intent intent = new Intent(SecurityCenterActivity.this, ChangePhoneActivity.class);
                    intent.putExtra(ChangePhoneActivity.EXTRA_OLD_PHONE, UserDao.getInstance().getUser().getPhone());
                    intent.putExtra(ChangePhoneActivity.EXTRA_IS_WALLET_OPENED, isWalletOpened);
                    startActivity(intent);
                } else {
                    ToastUtils.showToast("数据获取失败");
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
}
