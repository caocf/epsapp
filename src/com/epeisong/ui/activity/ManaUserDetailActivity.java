package com.epeisong.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;

import com.epeisong.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.dialog.PayPasswordDialog;
import com.epeisong.base.view.AdjustHeightGridView;
import com.epeisong.base.view.SlipButton;
import com.epeisong.base.view.SlipButton.SlipButtonChangeListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.base.view.SlipButton.Attr;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Guarantee;
import com.epeisong.model.User;
import com.epeisong.ui.fragment.HomeFragment.Item;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 管理用户详情
 * 
 * @author Jack
 * 
 */
public class ManaUserDetailActivity extends BaseActivity implements OnClickListener, SlipButtonChangeListener {
	private static final int INDEX_USERNAME = 1;
	private static final int INDEX_REALNAME = 3;
	private static final int INDEX_CERTIFICATE = 4;
	private static final int INDEX_USERACCOUNT = 5;
	//private static final int INDEX_USEPASS = 6;
	//private static final int INDEX_PAYPASS = 7;
	
    private TextView et_username, et_typename, et_realname, et_certificate, et_useraccount;
    //private TextView et_usepass, et_paypass;
    private User mUser;
    private Dialog typedialog;
    private Dialog inputdialog;
    private int currentTypeCode;
    SlipButton cb_freeze;
    
    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "管理用户", null).setShowLogo(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

    	mUser = (User) getIntent().getSerializableExtra(ContactsDetailActivity.EXTRA_USER);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_user_detail);

        et_username = (TextView) findViewById(R.id.et_username);
        et_typename = (TextView) findViewById(R.id.et_typename);
        et_realname = (TextView) findViewById(R.id.et_realname);
        et_certificate = (TextView) findViewById(R.id.et_certificate);
        et_useraccount = (TextView) findViewById(R.id.et_useraccount);
        //et_usepass = (TextView) findViewById(R.id.et_usepass);
        //et_paypass = (TextView) findViewById(R.id.et_paypass);
        
        et_username.setText(mUser.getShow_name());
        et_realname.setText(mUser.getContacts_name());
        et_typename.setText(mUser.getUser_type_name());
        et_certificate.setText(mUser.getAddress());
        et_useraccount.setText(mUser.getAccount_name());
        //et_usepass.setText(mUser.)
        et_username.setOnClickListener(this);
        et_realname.setOnClickListener(this);
        et_certificate.setOnClickListener(this);
        et_useraccount.setOnClickListener(this);
        //et_usepass.setOnClickListener(this);
        //et_paypass.setOnClickListener(this);
        findViewById(R.id.ll_button).setOnClickListener(this);
        Button btn_del = (Button) findViewById(R.id.btn_del);
        btn_del.setVisibility(View.GONE);
        btn_del.setOnClickListener(this);
        
        cb_freeze = (SlipButton) findViewById(R.id.cb_freeze);
        
        cb_freeze.setAttr(new Attr().setOpenBgResId(R.drawable.wallet_open_bg).setCloseBgResId(R.drawable.wallet_close_bg));
        //cb_freeze.SetOnChangedListener(this);
        
        //Guarantee s = null;
//		if (s.getStatus() == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL) {
//			cb_freeze.setDefaultOpen(true);
//		} else {
			cb_freeze.setDefaultOpen(false);
		//}
	
		//cb_freeze.setTag(s);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.et_username:
        	UpdateUserIndexDialogue(INDEX_USERNAME, et_username.getText().toString());
        	break;
        case R.id.et_realname:
        	UpdateUserIndexDialogue(INDEX_REALNAME, et_realname.getText().toString());
        	break;
        case R.id.et_certificate:
        	UpdateUserIndexDialogue(INDEX_CERTIFICATE, et_certificate.getText().toString());
        	break;
        case R.id.et_useraccount:
        	UpdateUserIndexDialogue(INDEX_USERACCOUNT, et_useraccount.getText().toString());
        	break;
//        case R.id.et_usepass:
//        	UpdateUserIndexDialogue(INDEX_USEPASS, et_usepass.getText().toString());
//        	break;
//        case R.id.et_paypass:
//        	UpdateUserIndexDialogue(INDEX_PAYPASS, et_paypass.getText().toString());
//        	break;
        case R.id.ll_button:
        	final String[] arrayTypeName = new String[] {
        			"整车运输", "零担专线", "配货市场", "配载信息部", "快递", "快递员",
        			"分拣中转", "收派网店", "同城配送", "第三方物流", "物流园", "企业物流部",
        			"装卸", "包装", "驳货", "专线接货", "仓储", "停车场",
        			"设备租赁", "搬家", "保险", "担保", "个人消费者", "代售点",
        			"证照年检", "加油站", "汽车维修", "寄存柜", "旅馆", "平台服务",
        			"钱包管理", "钱包提现", "其它"};
           	final int[] arrayTypeCode = new int[] {
        			Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE, Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE,
        			Properties.LOGISTIC_TYPE_MARKET, Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT,
        			Properties.LOGISTIC_TYPE_EXPRESS, Properties.LOGISTIC_TYPE_COURIER,
        			Properties.LOGISTIC_TYPE_SORTING_TRANSFER, Properties.LOGISTIC_TYPE_COLLECTING_POINT,
        			Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION, Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS,
        			Properties.LOGISTIC_TYPE_LOGISTICS_PARK, Properties.LOGISTIC_TYPE_ENTERPRISE_LOGISTICS_DEPARTMENT,
        			Properties.LOGISTIC_TYPE_LOAD_UNLOAD, Properties.LOGISTIC_TYPE_PACKAGING,
        			Properties.LOGISTIC_TYPE_TRANSHIP_GOODS, Properties.LOGISTIC_TYPE_LINE_RECEIVE_GOODS,
        			Properties.LOGISTIC_TYPE_STORAGE, Properties.LOGISTIC_TYPE_PARKING_LOT,
        			Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING, Properties.LOGISTIC_TYPE_MOVE_HOUSE,
        			Properties.LOGISTIC_TYPE_INSURANCE, Properties.LOGISTIC_TYPE_GUARANTEE,
        			Properties.LOGISTIC_TYPE_PERSONAL_CONSUMER, Properties.LOGISTIC_TYPE_PICK_UP_POINT,
        			Properties.LOGISTIC_TYPE_ANNUAL_INSPECTION, Properties.LOGISTIC_TYPE_GAS_STATION,
        			Properties.LOGISTIC_TYPE_VEHICLE_REPAIR, Properties.LOGISTIC_TYPE_CONSIGN_CABINET,
        			Properties.LOGISTIC_TYPE_HOTEL, Properties.LOGISTIC_TYPE_PLATFORM_CUSTOMER_SERVICE,
        			Properties.LOGISTIC_TYPE_WALLET_MANAGE, Properties.LOGISTIC_TYPE_WALLET_CASH,
        			Properties.LOGISTIC_TYPE_UNKNOW
        	};
    		LayoutInflater factory = LayoutInflater.from(this);  
    		View view = factory.inflate(R.layout.dialog_usertype, null);  
    		AdjustHeightGridView mGridView;
    		MyAdapter mAdapter;
    		 
    		mGridView = (AdjustHeightGridView) view.findViewById(R.id.gv_img);
    		mGridView.setNumColumns(2);
//    		int p = DimensionUtls.getPixelFromDpInt(10);
//    		mGridView.setPadding(0, p, 0, 0);
    		mGridView.setSelector(R.color.transparent);
//    		mGridView.setBackgroundColor(Color.WHITE);
    		mGridView.setAdapter(mAdapter = new MyAdapter());
    		for(int i=0; i<arrayTypeName.length;i++)
    			mAdapter.addItem(new Item(arrayTypeName[i], 0).setUserTypeCode(arrayTypeCode[i]));
        	//OnClickListener listener = null;  
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	// = new AlertDialog.Builder(this).show();
//        			.setItems(arrayName, new DialogInterface.OnClickListener() {
//        				
//        			public void onClick(DialogInterface dialog, int which) {     
//        				et_typename.setText(arrayTypeName[which]);
//        				//Toast.makeText(ManaAddUserActivity.this, arrayFruit[which], Toast.LENGTH_SHORT).show();    
//        				} 
//        			}).create();  
        	builder.setView(view); 
        	typedialog = builder.show();
        	typedialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 
        	
        	//dialog.getWindow().setLayout(200, -1);
        	WindowManager.LayoutParams params = typedialog.getWindow().getAttributes();    
        	params.width = 700;    
        	params.height = 1600;    
        	typedialog.getWindow().setAttributes(params); 
        	
        	break;
        case R.id.btn_del:
        	break;

        }
    }
    
    void UpdateUserIndexDialogue(final int index, String name) {
    	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
		LayoutInflater factory = LayoutInflater.from(this);  
		View view = factory.inflate(R.layout.dialog_paypassword, null);  
		final EditText et_password = (EditText) view.findViewById(R.id.et_password);
		
		builder.setView(view);  
		if(index==INDEX_USERNAME) {
			et_password.setInputType(EditorInfo.TYPE_CLASS_TEXT);
			((TextView) view.findViewById(R.id.tv_passwordhint)).setText("输入新用户名称");
		} else if (index==INDEX_REALNAME) {
			et_password.setInputType(EditorInfo.TYPE_CLASS_TEXT);
			((TextView) view.findViewById(R.id.tv_passwordhint)).setText("输入新真实姓名");
		} else if (index==INDEX_CERTIFICATE) {
			//et_password.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
			((TextView) view.findViewById(R.id.tv_passwordhint)).setText("输入新身份证号");
		} else if (index==INDEX_USERACCOUNT) {
			et_password.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
			((TextView) view.findViewById(R.id.tv_passwordhint)).setText("输入新用户账号");
//		} else if (index==INDEX_USEPASS) {
//			((TextView) view.findViewById(R.id.tv_passwordhint)).setText("输入新用户密码");
//		} else if (index==INDEX_PAYPASS) {
//			((TextView) view.findViewById(R.id.tv_passwordhint)).setText("输入新支付密码");
		} else {
		}
		et_password.setText(name);

		inputdialog = builder.show();
		inputdialog.setCanceledOnTouchOutside(true);
		view.findViewById(R.id.bt_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//ToastUtils.showToast("cancel");
				inputdialog.dismiss();
			}
		});
		view.findViewById(R.id.bt_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final String passwords = et_password.getText().toString();
				if (passwords.length() >= 1) {
					Dialog dialog = null;
					PayPasswordDialog.Builder builder = new PayPasswordDialog.Builder(ManaUserDetailActivity.this) {

						@Override
						protected void onOkClick() {
							inputdialog.dismiss();
							UpdateUserIndexName(index, passwords);
						}
						
					};
					builder.setTitle("修改用户信息前，请输入支付密码");
//					new PayPasswordDialog.Builder(ManaUserDetailActivity.this) {
//						
//						@Override
//						protected void onOkClick() {
//							
//						}
//					}.create().show();
					dialog = builder.create();
					dialog.show();
					
					//InputPayPassword(index, passwords);

				} else {
					ToastUtils.showToast("内容为空！");
					return;
				}
			}
		});
		
    }
    
    void InputPayPassword(final int index, final String name) {
    	
		final AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
		LayoutInflater factory = LayoutInflater.from(this);  
		View view = factory.inflate(R.layout.dialog_paypassword, null);  
		// builder.setIcon(R.drawable.icon);  
		//builder.setTitle("自定义输入框");  
		final EditText et_password = (EditText) view.findViewById(R.id.et_password);
		
		builder.setView(view);  
		((TextView) view.findViewById(R.id.tv_passwordhint)).setText("修改用户信息前，请输入支付密码");

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
				String passwords = et_password.getText().toString();
				if (passwords.length() >= 6) {
						dialog.dismiss();
						inputdialog.dismiss();
						UpdateUserIndexName(index, name);
					
				} else {
					ToastUtils.showToast("密码不正确！");
					return;
				}
			}
		});
		
    }
    
	private class MyAdapter extends HoldDataBaseAdapter<Item> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.fragment_type_gridview_item);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.fillData(getItem(position));
			return convertView;
		}
	}

	private class ViewHolder {
		TextView tv_tv;
		int typecode;
 
		public void findView(View v) {
			tv_tv = (TextView) v.findViewById(R.id.tv_lstrght);
			tv_tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					typedialog.dismiss();
					currentTypeCode = typecode;
					et_typename.setText(tv_tv.getText());
				}
			});
		}

		public void fillData(final Item item) {
			tv_tv.setText(item.name);
			typecode = item.userTypeCode;
		}
	}
    
    
    void UpdateUserIndexName(int index, String updatestr) {
    	
		if(index==INDEX_USERNAME) {
			et_username.setText(updatestr);
		} else if (index==INDEX_REALNAME) {
			et_realname.setText(updatestr);
		} else if (index==INDEX_CERTIFICATE) {
			et_certificate.setText(updatestr);
		} else if (index==INDEX_USERACCOUNT) {
			et_useraccount.setText(updatestr);
//		} else if (index==INDEX_USEPASS) {
//			et_usepass.setText(updatestr);
//		} else if (index==INDEX_PAYPASS) {
//			et_paypass.setText(updatestr);
		} else {
		}
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

        if (curStatus == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL) {
            f.setStatus(Properties.GUARANTEE_PRODUCT_STATUS_INVALID);
        } else {
            f.setStatus(Properties.GUARANTEE_PRODUCT_STATUS_NORMAL);
        }
    }
    
//    private void toggleSlipButton(final SlipButton btn) {
//        HandlerUtils.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                btn.toggle();
//            }
//        }, 200);
//    }

}
