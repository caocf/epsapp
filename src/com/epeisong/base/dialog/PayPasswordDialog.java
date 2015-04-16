package com.epeisong.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.payment.net.NetWallet;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 支付密码对话框
 * 
 * @author Jack
 * 
 */

public class PayPasswordDialog extends Dialog {

	private final static int PAY_PASSWORD_ERROR_TIMES = 5;
	
	private static int mErrorTimes;
	private static long mLastErroStartTime;
	
	public PayPasswordDialog(Context context, int theme) {
		super(context, theme);
	}

	public PayPasswordDialog(Context context) {
		super(context);
	}
	
	public static void Initerrortimes() {
		mErrorTimes = 0;
		SetStartTimes(0);
	}
	
	public static void SetStartTimes(int times) {
		mLastErroStartTime = times;
	}

	/**
	 * Helper class for creating a pay dialog
	 */
	public static abstract class Builder {

		private Context context;
		private String title;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private EditText et_password;
		private PayPasswordDialog dialog;

		private DialogInterface.OnClickListener positiveButtonClickListener, 
		negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Create the pay dialog
		 */
		public PayPasswordDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the pay Theme
			dialog = new PayPasswordDialog(context, R.style.payDialog);
			View layout = inflater.inflate(R.layout.dialog_paypassword, null);
			// set the dialog title
			((TextView) layout.findViewById(R.id.tv_passwordhint)).setText(title);
			et_password = (EditText) layout.findViewById(R.id.et_password);
			Button bt_ok = (Button) layout.findViewById(R.id.bt_ok);
			Button bt_cancel = (Button) layout.findViewById(R.id.bt_cancel);
			// set the confirm button
			if (positiveButtonText != null) {
				bt_ok.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					bt_ok.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							positiveButtonClickListener.onClick(
									dialog, DialogInterface.BUTTON_POSITIVE);
						}
					});
				}
			} else {
				bt_ok.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						//dialog.dismiss();
						//VerifyPassword(et_password.getText().toString());
					}
				});
				// if no confirm button just set the visibility to GONE
				//layout.findViewById(R.id.bt_ok).setVisibility(
				//        View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				bt_cancel.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					bt_cancel.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							positiveButtonClickListener.onClick(
									dialog, DialogInterface.BUTTON_NEGATIVE);
						}
					});
				}
			} else {
				bt_cancel.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				// if no confirm button just set the visibility to GONE
				//layout.findViewById(R.id.bt_cancel).setVisibility(
				//       View.GONE);
			}

			if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body

			}

			dialog.setContentView(layout);
			dialog.setCanceledOnTouchOutside(true);
			return dialog;
		}

		/**
		 * Set the Dialog title from resource
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public String getPassword() {
			return et_password.getText().toString();
		}

		/**
		 * Set a pay content view for the Dialog.
		 * If a message is set, the contentView is not
		 * added to the Dialog...
		 * @param v
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public void VerifyPassword(final String pasString) {
			
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    Boolean result = false;
                    try {
                        NetWallet netWallet = new NetWallet();
                        
                        result =  netWallet.chkPaymentPwd(12, pasString);
                    } catch (Exception e) {
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if (!result) {
                        //ToastUtils.showToast("支付密码错误");
                        
                        if(mLastErroStartTime<System.currentTimeMillis()-30*60*1000)
        					Initerrortimes();
        					
        				mLastErroStartTime = System.currentTimeMillis();
        				if(mErrorTimes>=PAY_PASSWORD_ERROR_TIMES-1) {
        					ToastUtils.showToast("账号已冻结，请在30分钟后使用");
        				} else {
        					mErrorTimes++;
        					ToastUtils.showToast("支付密码输入不正确，你还有"+String.valueOf(PAY_PASSWORD_ERROR_TIMES-mErrorTimes)+"次输入机会");
        				}
                        return;
	    			} else  {
	    				Initerrortimes();
	    				dialog.dismiss();
	    				onOkClick();
	    			}
                    
                }
            };
            
            task.execute();
		}
		protected abstract void onOkClick() ;
	}

	
	
}


