package com.epeisong.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.model.User;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.net.ws.utils.WalletResp;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
/**
 * 冻结/解冻	钱包
 * 
 * @author Jack
 * 
 */
public class FreezeWalletActivity extends BaseActivity implements OnClickListener {
	private EditText et_name, et_inputiden;
	private TextView tv_name, tv_code;
	private int walletState;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		walletState = getIntent().getIntExtra("walletstate", 0);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_wallet_freeze);
		
		et_name = (EditText) findViewById(R.id.et_name);
		et_inputiden = (EditText) findViewById(R.id.et_inputiden);
		findViewById(R.id.bt_freeze).setOnClickListener(this);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_code = (TextView) findViewById(R.id.tv_code);
		if(walletState==1) {
			TextView tv_note = (TextView) findViewById(R.id.tv_note);
			tv_note.setText("请输入钱包预设姓名和身份证号以解冻钱包");
			((Button) findViewById(R.id.bt_freeze)).setText("解冻钱包");
		} 
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bt_freeze:
			boolean inputerror=false;
			final String namesString = et_name.getText().toString();
			if (TextUtils.isEmpty(namesString)) {
				inputerror = true;
				tv_name.setVisibility(View.VISIBLE);
				//ToastUtils.showToast("请输入持卡人姓名");//"持卡人姓名应与钱包预设姓名相同");
				//return;
			} else {
				tv_name.setVisibility(View.INVISIBLE);
			}

			final String idenName = et_inputiden.getText().toString();
			if (idenName.length()!=18 && idenName.length()!=15) {
				//ToastUtils.showToast("请输入身份证号码");
				inputerror = true;
				tv_code.setVisibility(View.VISIBLE);
				//return;
			} else {
				tv_code.setVisibility(View.INVISIBLE);
			}
			
			if(inputerror)
				return;
			AsyncTask<Void, Void, WalletResp> task = new AsyncTask<Void, Void, WalletResp>() {
				@Override
				protected WalletResp doInBackground(Void... params) {
					ApiExecutor api = new ApiExecutor();
					try {
						User user=UserDao.getInstance().getUser();
						if(walletState==1) {
							return api.thawWallet(user.getAccount_name(), SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null),
									namesString, idenName);
						} else
							return api.frozenWallet(user.getAccount_name(), SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null),
								namesString, idenName);
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
					        Intent intent = new Intent();
					        intent.putExtra("wallet", result.getWallet());
					        setResult(RESULT_OK, intent);
					        finish();
					        
						} else {
							ToastUtils.showToast(result.getDesc());
						}
					}
				}
			};
			task.execute();
			break;
		default:
			break;
		}
			
	}
	
	@Override
	protected TitleParams getTitleParams() {
		if(walletState==1)
			return new TitleParams(getDefaultHomeAction(), "解冻钱包", null).setShowLogo(false);
		else
			return new TitleParams(getDefaultHomeAction(), "冻结钱包", null).setShowLogo(false);
	}

}
