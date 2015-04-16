package com.epeisong.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.utils.PromptUtils;
import com.epeisong.model.GuaComplainTask;
import com.epeisong.model.InfoFee;
import com.epeisong.model.User;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.ComplainTaskResp;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.java.JavaUtils;

public class GuaCompResultActivity extends BaseActivity implements OnClickListener {
	
	private TextView et_infoname;
	private TextView tv_selectinfo;
	private TextView et_payergua;
	private TextView tv_selectpayer;
	private TextView et_payeegua;
	private TextView tv_selectpayee;
	
	private EditText et_resultnote, et_paypassword;
	
    private User mUserSelf;
    private String mInfoFeeId;
    private InfoFee mInfoFee;
    private GuaComplainTask guaComplainTask;
    private int NoneNum;
    private int ResultType[] = new int[3] , ResultId[] = new int[3];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mInfoFeeId = getIntent().getStringExtra(GuaCompDetailActivity.EXTRA_INFO_FEE_ID);
        guaComplainTask = (GuaComplainTask) getIntent().getSerializableExtra(GuaCompDetailActivity.EXTRA_GUA_COMPLAIN_TASK);
        mInfoFee = guaComplainTask.getInfoFee();
        if (TextUtils.isEmpty(mInfoFeeId)) {
            ToastUtils.showToast("InfoFeeFragment 参数错误");
            finish();
            return;
        }
        mUserSelf = UserDao.getInstance().getUser();
        
        NoneNum=0;
		setContentView(R.layout.activity_guacomp_result);
		if(mInfoFee.getInfoAmount()==null || mInfoFee.getInfoAmount()<=0.0) {
			findViewById(R.id.ll_info).setVisibility(View.GONE);
			findViewById(R.id.ll_feebutton).setVisibility(View.GONE);
			NoneNum++;
		} else {
			et_infoname = (TextView) findViewById(R.id.et_infoname);
			et_infoname.setText(mInfoFee.getPayerName()+"支付交易费:"+mInfoFee.getInfoAmount()/100.0+"元");
			tv_selectinfo = (TextView) findViewById(R.id.tv_selectinfo);
			findViewById(R.id.ll_button).setOnClickListener(this);
		}
		
		if(mInfoFee.getPayeeGuaranteeAmount()==null || mInfoFee.getPayeeGuaranteeAmount()<=0.0) {
			findViewById(R.id.ll_infopayee).setVisibility(View.GONE);
			findViewById(R.id.ll_infobuttonpayee).setVisibility(View.GONE);
			NoneNum +=2;
		} else {
			et_payeegua = (TextView) findViewById(R.id.et_payeegua);
			et_payeegua.setText(mInfoFee.getPayeeName()+"担保费:"+mInfoFee.getPayeeGuaranteeAmount()/100.0+"元");
			tv_selectpayee = (TextView) findViewById(R.id.tv_selectpayee);
			findViewById(R.id.ll_buttonpayee).setOnClickListener(this);
		}
		
		if(mInfoFee.getPayerGuaranteeAmount()==null || mInfoFee.getPayerGuaranteeAmount()<=0.0) {
			findViewById(R.id.ll_infopayer).setVisibility(View.GONE);
			findViewById(R.id.ll_infobuttonpayer).setVisibility(View.GONE);
			NoneNum +=4;
		} else {
			et_payergua = (TextView) findViewById(R.id.et_payergua);
			et_payergua.setText(mInfoFee.getPayerName()+"担保费:"+mInfoFee.getPayerGuaranteeAmount()/100.0+"元");
			tv_selectpayer = (TextView) findViewById(R.id.tv_selectpayer);
			findViewById(R.id.ll_buttonpayer).setOnClickListener(this);
		}
		findViewById(R.id.bt_finish).setOnClickListener(this);

		et_resultnote = (EditText) findViewById(R.id.et_resultnote);
		et_paypassword = (EditText) findViewById(R.id.et_paypassword);
	}
	
	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "发布处理结果", null).setShowLogo(false);
	}

	@Override
	public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bt_finish:
        	String infoString=mInfoFee.getPayerName();
        	String payeeString=mInfoFee.getPayerName();
        	String payerString=mInfoFee.getPayerName();
        	int selfid = Integer.valueOf(mUserSelf.getId());
        	if(NoneNum%2==0) {
        		infoString = tv_selectinfo.getText().toString();
	            if (TextUtils.isEmpty(infoString)) {
	                ToastUtils.showToast("请选择赔付对象");
	                return;
	            }
        	} else {
				ResultType[0] = 3;
				ResultId[0] = selfid;
        	}
            
        	if(NoneNum%4/2==0) {
        		payeeString = tv_selectpayee.getText().toString();
	            if (TextUtils.isEmpty(payeeString)) {
	                ToastUtils.showToast("请选择赔付对象");
	                return;
	            }
        	} else {
				ResultType[1] = 3;
				ResultId[1] = selfid;
        	}
            
        	if(NoneNum/4==0) {
        		payerString = tv_selectpayer.getText().toString();
	            if (TextUtils.isEmpty(payerString)) {
	                ToastUtils.showToast("请选择赔付对象");
	                return;
	            }
        	} else {
				ResultType[2] = 3;
				ResultId[2] = selfid;
        	}
        	
        	String noteString="";
        	noteString = et_resultnote.getText().toString();
        	if(NoneNum==7) {
	            if (TextUtils.isEmpty(noteString)) {
	                ToastUtils.showToast("请输入处理解释");
	                return;
	            }
        	} else {
        		if(TextUtils.isEmpty(noteString))
        			noteString = "%22%22";
        	}
            
            String passwordString = et_paypassword.getText().toString();
            if (!JavaUtils.isPwdValid(passwordString)) {
                ToastUtils.showToast("支付密码不正确");
                return;
            }
            NoteFinish(infoString, payeeString, payerString, noteString, passwordString, true);
        	break;
        case R.id.ll_button:
        	SelectName(tv_selectinfo, 0);
        	break;
        case R.id.ll_buttonpayee:
        	SelectName(tv_selectpayee, 1);
        	break;
        case R.id.ll_buttonpayer:
        	SelectName(tv_selectpayer, 2);
        	break;
        }
	}
	
    public void NoteFinish(final String infoString, final String payeeString, final String payerString, 
    	final String noteString, final String passwordString, final Boolean showtoast) {
		
        AsyncTask<Void, Void, ComplainTaskResp> task = new AsyncTask<Void, Void, ComplainTaskResp>() {
            @Override
            protected ComplainTaskResp doInBackground(Void... params) {
            	ApiExecutor api = new ApiExecutor();
                try {
                    return api.execComplainTask(mUserSelf.getAccount_name(), SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null),
                    	guaComplainTask.getComplainTask().getId(), ResultType[2], ResultId[2], 
                    	ResultType[1], ResultId[1], ResultType[0], ResultId[0],
                    	guaComplainTask.getComplainTask().getDealBillType(), guaComplainTask.getComplainTask().getDealBillId(), 
                    	noteString, passwordString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                return null;
            }

            @Override
            protected void onPostExecute(ComplainTaskResp result) {
                dismissPendingDialog();
                if(!showtoast)
                	return;
                if (result==null) {
                	ToastUtils.showToast("处理失败");
                }else {

        			if (result.getResult() == ComplainTaskResp.SUCC) {
                    	ToastUtils.showToast("处理成功");
    					Intent intent = new Intent("com.epeisong.ui.activity.updateGuaComplain");
    					intent.putExtra("guaComplaintResult", guaComplainTask);
    					sendBroadcast(intent); // 发送广播
    					
                        Intent intent1 = new Intent();
                        setResult(RESULT_OK, intent1);
                        finish();
        			} else {
        				ToastUtils.showToast(PromptUtils.getPrompt(result.getResult(), true));
        			}
                }
            }
        };
        task.execute();
    }
    
	public void SelectName(final TextView v, final int index) {
		final String[] arrayFruit;
		final int[] arrayId;
		int selfid = Integer.valueOf(mUserSelf.getId());
		if(index==1) {
			arrayFruit = new String[] {mInfoFee.getPayeeName(), mInfoFee.getPayerName(), mUserSelf.getShow_name() };
			arrayId = new int[] {mInfoFee.getPayeeId(), mInfoFee.getPayerId(), selfid };
			
		} else {
			arrayFruit = new String[] {mInfoFee.getPayerName(), mInfoFee.getPayeeName(), mUserSelf.getShow_name() };
			arrayId = new int[] {mInfoFee.getPayerId(), mInfoFee.getPayeeId(), selfid };
		}
		
		Dialog dialog = new AlertDialog.Builder(this)
			.setItems(arrayFruit, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {  
					ResultType[index] = which+1;
					ResultId[index] = arrayId[which];
					v.setText(arrayFruit[which]);
				} 
			}).create();  
		
		dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 
	
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();    
		dialog.getWindow().setAttributes(params); 
	}
	
}
