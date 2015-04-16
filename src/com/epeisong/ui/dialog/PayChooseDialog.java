package com.epeisong.ui.dialog;

import com.epeisong.R;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
 

public class PayChooseDialog extends Dialog implements OnClickListener {

	private Button bt_pay, bt_cancel;
	private RelativeLayout rl1,rl2;
	private ImageView iv_radio1 ,iv_radio2;
	Context context;

	public PayChooseDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
 
	public PayChooseDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_paychoose);
	    this.setCanceledOnTouchOutside(false);
		 bt_pay = (Button) findViewById(R.id.bt_pay);
	     bt_cancel = (Button) findViewById(R.id.bt_cancel);
	     rl1 =  (RelativeLayout) findViewById(R.id.rl_list1);
	     rl2 =  (RelativeLayout) findViewById(R.id.rl_list2);
	     iv_radio1 = (ImageView) findViewById(R.id.iv_radio1);
	     iv_radio2 = (ImageView) findViewById(R.id.iv_radio2);
	        
	        bt_pay.setOnClickListener(this);
	        bt_cancel.setOnClickListener(this);
	        rl1.setOnClickListener(this);
	        rl2.setOnClickListener(this);
	}
	
	   @Override
	    public void onClick(final View v) {
	        switch (v.getId()) {
	        case R.id.bt_pay:
	        	 this.dismiss();
	            break;

	        case R.id.bt_cancel:
	            this.dismiss();
	            break;
	        case R.id.rl_list1:
	        	iv_radio1.setImageResource(R.drawable.radio_select);
	        	iv_radio2.setImageResource(R.drawable.radio_no);
	        	break;
	        case R.id.rl_list2:
	        	iv_radio2.setImageResource(R.drawable.radio_select);
	        	iv_radio1.setImageResource(R.drawable.radio_no);
	        	break;
	        }
	    }
	    

}
