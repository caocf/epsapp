package com.epeisong.ui.activity;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.bdmap.LocManager;
import com.bdmap.LocManager.LocObserver;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.dialog.ChooseLineActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.data.dao.RegionDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.utils.PromptUtils;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Dictionary;
import com.epeisong.model.Region;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.model.UserRole;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.ui.activity.temp.ChooseRoleActivity.Role;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.java.JavaUtils;

/**
 * 管理添加用户
 * @author Jack Zhu
 *
 */
public class ManageAddUserActivity extends BaseActivity implements OnClickListener ,LocObserver{
	private static final int REQUEST_CODE_CHOOSE_REGION = 100;
	private static final int REQUEST_CODE_CHOOSE_LINE = 102;
	
    private EditText et_useraccount, et_username, et_paypassword, et_logpassword;
    private Button bt_cityname;
    private TextView et_typename, tv_payhint;
    private int serveRegionCode;
    private int logisticType;
	
	private int areaRegionCode;
	private String areaRegionName;
	private BDLocation bdLocation;
	private ImageView iv_area_info;
	private TextView tv_area_info;
	private double longitude;
	private double latitude;
	private String address;
	private String generalName;
	private RegionResult mUserRegionResult;
	private int transportTypeCode=1;
	private int goodsTypeCode;
	private TextView mRegionTv;
    private EditText mAddressEt;
    private RegionResult mRegionResult;
    private RegionResult start;
    private RegionResult end; 
	
	private EditText et_contact;
	private EditText et_contact_tel;
	private EditText et_contact_mobile;
	private EditText et_intro_content;
	private TextView tv_area; // 地区
	private TextView tv_line;
	private TextView tv_goods_type;
	private TextView tv_transport_type;
	
	private LinearLayout ll_contact;
	private LinearLayout ll_contact_tel;
	private LinearLayout ll_contact_mobile;
	private RelativeLayout ll_intro_content;
	private LinearLayout ll_area;
	private LinearLayout ll_line;
	private LinearLayout ll_goods_type;
	private LinearLayout ll_transport_type;
	
	private Button btn_obtain, btn_clear;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_manage_add_user);
        et_useraccount = (EditText) findViewById(R.id.et_useraccount);
        et_logpassword = (EditText) findViewById(R.id.et_logpassword);
        et_typename = (TextView) findViewById(R.id.et_typename);
        et_username = (EditText) findViewById(R.id.et_username);
        et_paypassword = (EditText) findViewById(R.id.et_paypassword);
        tv_payhint = (TextView) findViewById(R.id.tv_payhint);
        findViewById(R.id.ll_button).setOnClickListener(this);
        findViewById(R.id.bt_finish).setOnClickListener(this);
        bt_cityname = (Button) findViewById(R.id.bt_cityname);
        bt_cityname.setOnClickListener(this);
    	
        et_contact = (EditText) findViewById(R.id.et_contact);
		et_contact_tel = (EditText) findViewById(R.id.et_contact_tel);
		et_contact_mobile = (EditText) findViewById(R.id.et_contact_mobile);
		et_intro_content = (EditText) findViewById(R.id.et_intro_content);

		tv_area = (TextView) findViewById(R.id.tv_area);
		tv_line = (TextView) findViewById(R.id.tv_line);
		tv_goods_type = (TextView) findViewById(R.id.tv_goods_type);
		tv_transport_type = (TextView) findViewById(R.id.tv_transport_type);
		
		
		ll_contact = (LinearLayout) findViewById(R.id.ll_contact);
		ll_contact_tel = (LinearLayout) findViewById(R.id.ll_contact_tel);
		ll_contact_mobile = (LinearLayout) findViewById(R.id.ll_contact_mobile);
		ll_intro_content = (RelativeLayout) findViewById(R.id.ll_intro_content);
		
		ll_area = (LinearLayout) findViewById(R.id.ll_area);
		ll_area.setOnClickListener(this);
		ll_line = (LinearLayout) findViewById(R.id.ll_line);
		ll_line.setOnClickListener(this);
		ll_goods_type = (LinearLayout) findViewById(R.id.ll_goods_type);
		ll_goods_type.setOnClickListener(this);
		ll_transport_type = (LinearLayout) findViewById(R.id.ll_transport_type);
		ll_transport_type.setOnClickListener(this);
		
		tv_transport_type.setText("公路运输");
    	transportTypeCode = 1;
    	
    	et_useraccount.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				et_contact_mobile.setText(s.toString());
			}
		});
//    	et_username.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//			}
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//			}
//			
//			@Override
//			public void afterTextChanged(Editable s) {
//				et_contact.setText(s.toString());
//			}
//		});
	}

	@Override
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "添加用户", null).setShowLogo(false);
	}
	
    public String getTextViewString(TextView view) {
    	String tString = view.getText().toString();
    	if(!TextUtils.isEmpty(tString))
    		return tString;
    	else
    		return "%22%22";//'"'+'"';
    }
    
    public String getEditTextString(EditText view) {
    	String tString = view.getText().toString();
    	if(!TextUtils.isEmpty(tString))
    		return tString;
    	else
    		return "%22%22";
    }
    
    public String getStringString(String string) {
    	if(!TextUtils.isEmpty(string))
    		return string;
    	else {
			return "%22%22";
		}
    }
    
    public void AddUser(final String accountString, final String logString, final String typeString, final String namesString, 
    	final String cityString, final String passwordString, final Boolean showtoast) {
        
        AsyncTask<Void, Void, Resp> task = new AsyncTask<Void, Void, Resp>() {
            @Override
            protected Resp doInBackground(Void... params) {
            	ApiExecutor api = new ApiExecutor();
                try {
                	User user=UserDao.getInstance().getUser();
//                    return api.createAccountAndLogisticByPlatformManager(accountString, logisticType, typeString,
//                    		namesString, serveRegionCode, cityString, user.getAccount_name(),
//                    		SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), passwordString
//                    		, Properties.OPERATION_TYPE_CREATE_ACCOUNT_AND_LOGISTIC_BY_PLATFORM_MANAGER, Properties.APP_CLIENT_BIG_TYPE_PHONE, logString);
//                  
                	int startcode=-1, endcode=-1;
                	String startString="", endString="";
                	if(start!=null) {
                		startcode = start.getFullCode();
                		startString = start.getShortNameFromDistrict();
                	}
                	if(end!=null) {
                		endcode = end.getFullCode();
                		endString = end.getShortNameFromDistrict();
                	}
                	String contactmoString = et_contact_mobile.getText().toString();
                	if(TextUtils.isEmpty(contactmoString))
                		contactmoString = accountString;
                    return api.createAccountAndLogisticByPlatformManager(accountString, logisticType, typeString,
                    		namesString, serveRegionCode, cityString, user.getAccount_name(),
                    		SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), passwordString
                    		, Properties.OPERATION_TYPE_CREATE_ACCOUNT_AND_LOGISTIC_BY_PLATFORM_MANAGER, Properties.APP_CLIENT_BIG_TYPE_PHONE, logString,
				            
                    		getEditTextString(et_contact), getEditTextString(et_contact_tel), contactmoString, getEditTextString(et_intro_content),
                    		areaRegionCode, getStringString(areaRegionName), getTextViewString(tv_area), longitude, latitude,
				    		 
                    		startcode, getStringString(startString), endcode, getStringString(endString), 
                    		goodsTypeCode, getTextViewString(tv_goods_type), transportTypeCode, getTextViewString(tv_transport_type)
				    		);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                return null;
            }

            @Override
            protected void onPostExecute(Resp result) {
                dismissPendingDialog();
                if(!showtoast)
                	return;
                if (result==null) {
                	ToastUtils.showToast("添加失败");
                }else {
        			if (result.getResult() == Resp.SUCC) {
        				ToastUtils.showToast("添加成功");
        				finish();
        				
        			} else {
        				if(result.getResult()!=-1)
        					ToastUtils.showToast(PromptUtils.getPrompt(result.getResult(), true));
        				else {
        					ToastUtils.showToast(result.getDesc());
						}
        			}
                }
            }
        };
        task.execute();
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.id.ll_button:
        	final String[] arrayFruit1 = new String[] {"快递", "同城配送", "配货市场", "物流园", "交易平台", "保险", "平台客服", "钱包管理", "钱包提现"};
        	final int[] arraytype1 = new int[] {Properties.LOGISTIC_TYPE_EXPRESS, Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION, 
        		Properties.LOGISTIC_TYPE_MARKET, Properties.LOGISTIC_TYPE_LOGISTICS_PARK, Properties.LOGISTIC_TYPE_GUARANTEE,
        		Properties.LOGISTIC_TYPE_INSURANCE, Properties.LOGISTIC_TYPE_PLATFORM_CUSTOMER_SERVICE,
        		Properties.LOGISTIC_TYPE_WALLET_MANAGE, Properties.LOGISTIC_TYPE_WALLET_CASH};
        	Dialog dialog1 = new AlertDialog.Builder(this)
        		.setItems(arrayFruit1, new DialogInterface.OnClickListener() {
        				
        		public void onClick(DialogInterface dialog, int which) {     
        			et_typename.setText(arrayFruit1[which]);
        			logisticType = arraytype1[which];
        			showOrHide(logisticType);
        		} 
        	}).create();  
        	
        	dialog1.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 

        	dialog1.show();
        	WindowManager.LayoutParams params = dialog1.getWindow().getAttributes();    
        	params.width = 600;    
        	params.height = 1500;    
        	dialog1.getWindow().setAttributes(params); 
        	break;
        case R.id.bt_cityname:
        	ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_0_3, REQUEST_CODE_CHOOSE_REGION);
        	break;
        case R.id.bt_finish:
        	String accountString = et_useraccount.getText().toString();
            if (TextUtils.isEmpty(accountString)) {
                ToastUtils.showToast("账户不能为空");
                return;
            }
            
            String logString = et_logpassword.getText().toString();
            if (!TextUtils.isEmpty(logString)) {
	            if (!JavaUtils.isPwdValid(logString)) {
	                ToastUtils.showToast("无效密码，请重新输入");
	                return;
	            }
            } else {
            	logString = "%22%22";//'"'+'"';
			}
            
            String typeString = et_typename.getText().toString();
            if (TextUtils.isEmpty(typeString)) {
                ToastUtils.showToast("请选择角色类别");
                return;
            }

            String namesString = et_username.getText().toString();
            if (TextUtils.isEmpty(namesString)) {
                ToastUtils.showToast("请输入用户名称");
                return;
            }
            
            String cityString = bt_cityname.getText().toString();
            if (TextUtils.isEmpty(cityString)) {
                ToastUtils.showToast("请选择地区");
                return;
            }
            
            String passwordString = et_paypassword.getText().toString();
            if(TextUtils.isEmpty(passwordString)) {
            	tv_payhint.setVisibility(View.VISIBLE);
                ToastUtils.showToast("请输入支付密码");
                return;
            } else {
            	tv_payhint.setVisibility(View.GONE);
            }
            if (!JavaUtils.isPwdValid(passwordString)) {
                ToastUtils.showToast("支付密码不正确");
                return;
            }
            AddUser(accountString, logString, typeString, namesString, cityString, passwordString, true);
            //InputPayPassword(0, null);
            break;
		case R.id.ll_line:
			Bundle extrasForChooseRegion = new Bundle();
            extrasForChooseRegion.putInt(ChooseRegionActivity.EXTRA_IN_FILTER, ChooseRegionActivity.FILTER_0_3);
//            if (mUser.getUser_type_code() == Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE) {
//                extrasForChooseRegion.putBoolean(ChooseRegionActivity.EXTRA_IS_SHOW_COUNTRY, false);
//            }
            ChooseLineActivity.launch(this, REQUEST_CODE_CHOOSE_LINE, true, extrasForChooseRegion);
//            ChooseLineActivity.launch(this, REQUEST_CODE_CHOOSE_LINE);
            break;
		case R.id.ll_transport_type:
			List<Dictionary> transportType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRANSPORT_MODE);
			showDictionaryListDialog("选择运输方式", transportType, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                	tv_transport_type.setText(item.getName());
                	transportTypeCode = item.getId();
                }
            });
			break;
		case R.id.ll_goods_type:
			List<Dictionary> goodsType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_GOODS_TYPE);
			showDictionaryListDialog("选择货物类型", goodsType, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                	tv_goods_type.setText(item.getName());
                	goodsTypeCode = item.getId();
                }
            });
			break;
		case R.id.ll_area:
//			final Dialog area = new AlertDialog.Builder(this).create();
			Button mbtn;
			
			final Dialog area = new Dialog(this);
			area.setTitle("选择地址");
			area.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 
			area.show();
        	Window windowArea = area.getWindow();
        	windowArea.setContentView(R.layout.activity_add_edit_address);
        	mAddressEt = (EditText)windowArea.findViewById(R.id.et_address);
        	mRegionTv = (TextView)windowArea.findViewById(R.id.tv_region);
        	iv_area_info = (ImageView) windowArea.findViewById(R.id.iv_area_info);
        	tv_area_info = (TextView) windowArea.findViewById(R.id.tv_area_info);
            mRegionTv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ChooseRegionActivity.launch(ManageAddUserActivity.this, ChooseRegionActivity.FILTER_0_3, 105);
				}
			});
            btn_obtain = (Button)windowArea.findViewById(R.id.btn_obtain);
    		btn_obtain.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					bdLocation = LocManager.getInstance().getBdLocation(ManageAddUserActivity.this);
					if (bdLocation != null) {
		                onBdLocChange(bdLocation);
		            }else{
		            	showPendingDialog("定位中...");
		            }
				}
			});
    		btn_clear = (Button)windowArea.findViewById(R.id.btn_clear);
    		btn_clear.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					longitude = 0;
					latitude = 0;
					iv_area_info.setVisibility(View.GONE);
				}
			});
           
            mbtn =  (Button) windowArea.findViewById(R.id.btn_ok);
            mbtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(longitude != 0 && latitude != 0 && !TextUtils.isEmpty(generalName)){
						tv_area.setText(generalName+ address);
						area.dismiss();
					}else{
						if(TextUtils.isEmpty(mRegionTv.getText().toString()) || TextUtils.isEmpty(mAddressEt.getText().toString())){
							ToastUtils.showToast("地址不能为空，请手动填写");
						}else{
							tv_area.setText(mRegionTv.getText().toString()+ mAddressEt.getText().toString());
							area.dismiss();
						}
					}
//					area.dismiss();
				}
			});
			break;
		case R.id.bt_public_bulletin:

			break;
		
		default:
			break;
		}
	}

	private void showOrHide(int type){
		
		ll_contact.setVisibility(View.GONE);
		ll_contact_tel.setVisibility(View.GONE);
		ll_contact_mobile.setVisibility(View.GONE);
		ll_intro_content.setVisibility(View.GONE);
		ll_area.setVisibility(View.GONE);
		ll_line.setVisibility(View.GONE);
		ll_goods_type.setVisibility(View.GONE);
		ll_transport_type.setVisibility(View.GONE);
		
		switch (type) {
		case Properties.LOGISTIC_TYPE_EXPRESS:
		case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
		case Properties.LOGISTIC_TYPE_MARKET:
		case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
			ll_line.setVisibility(View.VISIBLE);
			ll_goods_type.setVisibility(View.VISIBLE);
			ll_transport_type.setVisibility(View.VISIBLE);
		case Properties.LOGISTIC_TYPE_GUARANTEE:
		case Properties.LOGISTIC_TYPE_INSURANCE:
		case Properties.LOGISTIC_TYPE_PLATFORM_CUSTOMER_SERVICE:
		case Properties.LOGISTIC_TYPE_WALLET_MANAGE:
		case Properties.LOGISTIC_TYPE_WALLET_CASH:
			ll_contact.setVisibility(View.VISIBLE);
			ll_contact_tel.setVisibility(View.VISIBLE);
			ll_contact_mobile.setVisibility(View.VISIBLE);
			ll_intro_content.setVisibility(View.VISIBLE);
			ll_area.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CODE_CHOOSE_REGION) {
                    RegionResult result = (RegionResult) data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
                	bt_cityname.setTextColor(Color.BLACK);
                	serveRegionCode = result.getFullCode();
                	bt_cityname.setText(result.getShortNameFromDistrict());//.getGeneralName());getShortNameFromDistrict
        	
        	
        	
            } else if (REQUEST_CODE_CHOOSE_LINE == requestCode) {
                Serializable extra1 = data.getSerializableExtra(ChooseLineActivity.EXTRA_START_REGION);
                Serializable extra2 = data.getSerializableExtra(ChooseLineActivity.EXTRA_END_REGION);
                if (extra1 != null && extra2 != null) {
                    start = (RegionResult) extra1;
                    end = (RegionResult) extra2;
                    UserRole role = new UserRole();
                    role.setLineStartCode(start.getFullCode());
                    role.setLineStartName(start.getShortNameFromDistrict());
                    role.setLineEndCode(end.getFullCode());
                    role.setLineEndName(end.getShortNameFromDistrict());
                    tv_line.setText(start.getShortNameFromDistrict() + "-" + end.getShortNameFromDistrict());
                }
            }else if (requestCode == 105) {
                Serializable extra = data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
                if (extra != null && extra instanceof RegionResult) {
                    mRegionResult = (RegionResult) extra;
                    areaRegionName = mRegionResult.getGeneralName();
                    mRegionTv.setText(areaRegionName);
                    areaRegionCode = mRegionResult.getFullCode();
                }
            }
        }
    }
	
	class MyAdapter extends HoldDataBaseAdapter<Role> {
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	LinearLayout ll = new LinearLayout(getApplication());
//        	ll.setBackgroundResource(R.color.white);
        	ll.setBackgroundResource(R.drawable.selector_gridview_add_user);
        	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        	TextView tv = new TextView(getApplication());
        	tv.setTextSize(20);
        	tv.setTextColor(getResources().getColor(R.color.black));
        	tv.setLayoutParams(lp);
        	tv.setPadding(15, 15, 0, 15);
        	ll.addView(tv);
            Role role = getItem(position);
            tv.setText(role.getName());
            tv.setTag(role);
            return ll;
        }
    }

	@Override
	public void onBdLocChange(BDLocation loc) {
		dismissPendingDialog();
		address = "";
		if (loc != null) {
			longitude = loc.getLongitude();
			latitude = loc.getLatitude();
            Region city = RegionDao.getInstance().queryByCityName(loc.getCity());
            if (city != null) {
                Region district = RegionDao.getInstance().queryByDistrictNameAndCityCode(loc.getDistrict(),
                        city.getCode());
                if (district != null) {
                    mUserRegionResult = RegionDao.convertToResult(district);
                    
                    if (loc.getStreet() != null) {
                        address += loc.getStreet();
                    }
                    if (loc.getStreetNumber() != null) {
                        address += loc.getStreetNumber();
                    }
                    mRegionTv.setText(mUserRegionResult.getGeneralName());
					mAddressEt.setText(address);
					iv_area_info.setVisibility(View.VISIBLE);
                }
            }else{
            	tv_area_info.setText("定位失败");
            }
        }else{
        	tv_area_info.setText("定位失败");
        }
	}

}
