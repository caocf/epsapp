package com.epeisong.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.User;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.net.ws.utils.WithdrawTask;
import com.epeisong.net.ws.utils.WithdrawTaskResp;
import com.epeisong.ui.activity.TransferWithdrawalDetailActivity;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

public class TransferWithdrawalDetailFragment extends Fragment implements OnClickListener {
	private TextView tv_name;
	private TextView tv_bank_name;
	private TextView tv_bank_area;
	private TextView tv_money;
	private TextView tv_bank_card;
	private TextView tv_note_content;
	private TextView tv_serial_number;
	private EditText et_serial_number;
	private EditText et_note;
	private LinearLayout ll_transfer_number;
	private LinearLayout ll_note;
	private LinearLayout ll_transfer;
	private LinearLayout ll_note_content;
	private Button bt_error;
	private Button bt_cancel;
	private Button bt_sure;
	
	private WithdrawTask withdraw;
	private String flag;
	private String payeeAccount;
	private String accountFormat = "";
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		withdraw = (WithdrawTask) args.getSerializable(TransferWithdrawalDetailActivity.EXTRA_WITHDRAW);
		flag = args.getString(TransferWithdrawalDetailActivity.EXTRA_FLAG);
		
		View view =SystemUtils.inflate(R.layout.activity_withdrawal_detail);
		tv_name = (TextView) view.findViewById(R.id.tv_name);
		tv_bank_name = (TextView) view.findViewById(R.id.tv_bank_name);
		tv_bank_area = (TextView) view.findViewById(R.id.tv_bank_area);
		tv_money = (TextView) view.findViewById(R.id.tv_money);
		tv_bank_card = (TextView) view.findViewById(R.id.tv_bank_card);
		tv_note_content = (TextView) view.findViewById(R.id.tv_note_content);
		tv_serial_number = (TextView) view.findViewById(R.id.tv_serial_number);
		et_serial_number = (EditText) view.findViewById(R.id.et_serial_number);
		et_note = (EditText) view.findViewById(R.id.et_note);
		ll_transfer_number = (LinearLayout) view.findViewById(R.id.ll_transfer_number);
		ll_note = (LinearLayout) view.findViewById(R.id.ll_note);
		ll_transfer = (LinearLayout) view.findViewById(R.id.ll_transfer);
		ll_note_content = (LinearLayout) view.findViewById(R.id.ll_note_content);
		bt_error = (Button) view.findViewById(R.id.bt_error);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_sure = (Button) view.findViewById(R.id.bt_sure);
		bt_error.setOnClickListener(this);
		bt_cancel.setOnClickListener(this);
		bt_sure.setOnClickListener(this);
		
		if(withdraw != null){
			tv_name.setText(withdraw.getPayeeName());
			tv_bank_name.setText(withdraw.getBankName());
			tv_bank_area.setText(withdraw.getBankRegionName());
			tv_money.setText(String.valueOf(withdraw.getAmount()/100.0) + "元");
			payeeAccount = withdraw.getPayeeAccount();
	//		payeeAccount = payeeAccount.replace(/(\d{4})(?=\d)/g,"$1"+"-");
			int index = 4;
			int count = payeeAccount.length()/4;
			for(int i = 0; i < count ; i++){
				accountFormat = accountFormat + payeeAccount.substring(0, index)+" "; // 前四个数字之后加空格
				payeeAccount = payeeAccount.substring(index); //获取前四个之后的数组
			}
			accountFormat = accountFormat + payeeAccount;
			
			tv_bank_card.setText(accountFormat);
			if (!TextUtils.isEmpty(withdraw.getNote())) {
				et_note.setText(withdraw.getNote());
			}else{
				ll_note_content.setVisibility(View.GONE);
			}
			tv_note_content.setText(withdraw.getNote());
			tv_serial_number.setText(withdraw.getSerialNumber());
			
			if(!TextUtils.isEmpty(flag)){
				bt_error.setVisibility(View.GONE);
				bt_cancel.setVisibility(View.GONE);
				bt_sure.setVisibility(View.GONE);
				et_serial_number.setVisibility(View.GONE);
				et_note.setVisibility(View.GONE);
				ll_transfer_number.setVisibility(View.GONE);
				ll_note.setVisibility(View.GONE);
				if (!TextUtils.isEmpty(withdraw.getNote())) {
					ll_note_content.setVisibility(View.VISIBLE);
				}else{
					ll_note_content.setVisibility(View.GONE);
				}
				if (!TextUtils.isEmpty(withdraw.getSerialNumber())) {
					ll_transfer.setVisibility(View.VISIBLE);
				}else{
					ll_transfer.setVisibility(View.GONE);
				}
			}
	
			if(withdraw.getStatus() == Properties.WITHDRAW_TASK_STATUS_NOT_PROCESSED){
				ll_transfer.setVisibility(View.GONE);
				ll_note_content.setVisibility(View.GONE);
				if(withdraw.getSubStatus() != null){
					bt_error.setVisibility(View.GONE);
				}
			}else if(withdraw.getStatus() == Properties.WITHDRAW_TASK_STATUS_PROCESSED){
				ll_transfer_number.setVisibility(View.GONE);
				ll_note.setVisibility(View.GONE);
				bt_error.setVisibility(View.GONE);
				bt_cancel.setVisibility(View.GONE);
				bt_sure.setVisibility(View.GONE);
			}else if(withdraw.getStatus() == Properties.WITHDRAW_TASK_STATUS_CANCEL){
				ll_transfer.setVisibility(View.GONE);
				ll_transfer_number.setVisibility(View.GONE);
				ll_note.setVisibility(View.GONE);
				bt_error.setVisibility(View.GONE);
				bt_cancel.setVisibility(View.GONE);
				bt_sure.setVisibility(View.GONE);
			}
		}
		return view;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_error:
			AsyncTask<Void, Void, WithdrawTaskResp> taskError = new AsyncTask<Void, Void, WithdrawTaskResp>() {

				@Override
				protected WithdrawTaskResp doInBackground(Void... params) {
					ApiExecutor api = new ApiExecutor();

					try {
						User user = UserDao.getInstance().getUser();
						String pwd = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED,null);
						String serial = "null";
						if (!TextUtils.isEmpty(et_serial_number.getText().toString())) {
							serial = et_serial_number.getText().toString();
						}
						String note = "null";
						if (!TextUtils.isEmpty(et_note.getText().toString())) {
							note = et_note.getText().toString();
						}
						return api.execWithdrawTask( user.getAccount_name(), pwd, withdraw.getId(), Properties.WITHDRAW_TASK_STATUS_NOT_PROCESSED,
								Properties.WITHDRAW_TASK_SUB_STATUS_NOT_PROCESSED_WARNING,
								serial, note);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(WithdrawTaskResp result) {
					if (result == null) {
						ToastUtils.showToast("操作失败");
					} else {
						if (result.getResult() == Resp.SUCC) {
							getActivity().finish();
							Intent intent = new Intent("com.epeisong.ui.activity.errorWithdraw");
							intent.putExtra("errorFlag", "flag");
							getActivity().sendBroadcast(intent); // 发送广播
						} else {
							ToastUtils.showToast(result.getDesc());
						}
					}
				}
			};
			taskError.execute();
			break;
		case R.id.bt_cancel:
			final EditText et_password;
			Button btn_sure;
			Button btn_cancel;
			final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog);
			View view1 = SystemUtils.inflate(R.layout.activity_transfer_dialog);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			btn_sure = (Button) view1.findViewById(R.id.bt_sure);
			btn_cancel = (Button) view1.findViewById(R.id.bt_cancel);
			et_password = (EditText) view1.findViewById(R.id.et_password);
			dialog.setContentView(view1);
			dialog.setCanceledOnTouchOutside(true);
			
			btn_sure.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(TextUtils.isEmpty(et_password.getText().toString())){
						ToastUtils.showToast("请输入支付密码");
						return;
					}
					
					AsyncTask<Void, Void, WithdrawTaskResp> taskCancel = new AsyncTask<Void, Void, WithdrawTaskResp>(){

						@Override
						protected WithdrawTaskResp doInBackground(
								Void... params) {
							ApiExecutor api = new ApiExecutor();
							try {
								User user = UserDao.getInstance().getUser();
								String pwd = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED,null);
								String note = "null";
								if (!TextUtils.isEmpty(et_note.getText().toString())) {
									note = et_note.getText().toString();
								}
								return api.cancelWithdrawTask(user.getAccount_name(), pwd, String.valueOf(withdraw.getId()), note, et_password.getText().toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
							return null;
						}
						@Override
						protected void onPostExecute(WithdrawTaskResp result) {
							super.onPostExecute(result);
							if (result == null) {
								ToastUtils.showToast("操作失败");
							} else {
								if (result.getResult() == Resp.SUCC) {
									dialog.dismiss();
									getActivity().finish();
									Intent intent = new Intent("com.epeisong.ui.activity.cancelWithdraw");
									intent.putExtra("withdrawlDetail", withdraw);
									getActivity().sendBroadcast(intent); // 发送广播
								} else {
									ToastUtils.showToast(result.getDesc());
								}
							}
						}
					};
					taskCancel.execute();
				}
			});
			btn_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		case R.id.bt_sure:

			if (TextUtils.isEmpty(et_serial_number.getText().toString())) {
				ToastUtils.showToast("请输入转账流水号");
				return;
			}
			AsyncTask<Void, Void, WithdrawTaskResp> taskSure = new AsyncTask<Void, Void, WithdrawTaskResp>() {

				@Override
				protected WithdrawTaskResp doInBackground(Void... params) {
					ApiExecutor api = new ApiExecutor();

					try {
						User user = UserDao.getInstance().getUser();
						String pwd = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED,null);
						String serial = et_serial_number.getText().toString();
						String note = "null";
						if (!TextUtils.isEmpty(et_note.getText().toString())) {
							note = et_note.getText().toString();
						}
						return api.execWithdrawTask(user.getAccount_name(), pwd, withdraw.getId(), Properties.WITHDRAW_TASK_STATUS_PROCESSED,
								-1, serial, note);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(WithdrawTaskResp result) {
					if (result == null) {
						ToastUtils.showToast("操作失败");
					} else {
						if (result.getResult() == Resp.SUCC) {
							getActivity().finish();
							Intent intent = new Intent("com.epeisong.ui.activity.refreshWithdraw");
							intent.putExtra("withdrawlDetail", withdraw);
							getActivity().sendBroadcast(intent); // 发送广播
						} else {
							ToastUtils.showToast(result.getDesc());
						}
					}
				}
			};
			taskSure.execute();
		
			break;
		default:
			break;
		}
	}

}
