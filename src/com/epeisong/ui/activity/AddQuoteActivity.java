package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Contacts;
import com.epeisong.model.Dictionary;
import com.epeisong.model.RegionResult;
import com.epeisong.utils.ToastUtils;

/**
 * 添加报价
 * 
 * @author jack
 * 
 */
public class AddQuoteActivity extends BaseActivity implements OnClickListener{
	private static final int REQUEST_CODE_CHOOSE_REGIONS = 100;
	private static final int REQUEST_CODE_CHOOSE_REGIONE = 101;
	private static final int REQUEST_CODE_SERVER_REGION = 102;
	private static final int REQUEST_CHOOSE_CONTACTS_NOTICE = 110;
	
	private String nameactivity;
	private int long_distance;
	private int logitics_type;
	
	private Button bt_startname, bt_endname;
	private int startRegionCode, endRegionCode;
	private EditText et_distance;
	
	private Button bt_serverzone;
	private int serverzoneCode;
	private EditText et_transhiprange;
	private Button bt_vechilelen;
	private int vechilelenCode;
	private Button bt_forgoods;
	private int forgoodsType;
	private EditText et_quotecost;
	private Button bt_quotecost;
	private int quotecostType=0;
	private EditText et_minprice;
	private EditText et_minamount, et_largerprice;
	private Button bt_minamount;//, bt_largerprice;
	private int minamountType=0;//, largerpriceType; 
	private TextView tv_largerprice;
	private Button bt_quoteunit;
	private int quoteunitType=0;
	private EditText et_quotemin, et_quotemax, et_quoteoneunit, et_quotetwo,
		et_quotetwounit, et_pickupexpense, et_deliverexpense;
	private EditText et_onequote;
	private TextView tv_oneyuandun;
	private Button bt_quoteusetype;
	private int quoteusetypecode = 0;
	private TextView tv_dun, tv_yuandun, tv_dunyishang, tv_yuandun2;

	private EditText et_otherexpense;
	private Button bt_payway;
	private int paywayType;
	
	private Button bt_permissionset;
	private int permissionsetType;
	
	private Button bt_vechiletype;
	private int vechiletypeCode;
	private EditText et_goodsinfor, et_dispathcrange, 
		et_weight, et_volume, et_zonelimit;
	
	private String[] areas;
	private boolean[] areaState;
	private ListView areaCheckListView;

	ArrayList<Contacts> mContactsSelectedList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		nameactivity = getIntent().getStringExtra("quotename");
		logitics_type = getIntent().getIntExtra("logiticstype", -1);
		long_distance = getIntent().getIntExtra("longdis", 1);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_quote);
		List<Dictionary> goodsType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_GOODS_TYPE);
		int size = goodsType.size();
		areas = new String[size];
		areaState = new boolean[size];
		for(int i=0; i<size;i++) {
			areas[i] = goodsType.get(i).getName();
			areaState[i] = false; 
		}
		
		bt_startname = (Button) findViewById(R.id.bt_startname);
		bt_startname.setOnClickListener(this);
		bt_endname = (Button) findViewById(R.id.bt_endname);
		bt_endname.setOnClickListener(this);
		et_distance = (EditText) findViewById(R.id.et_distance);
		switch(logitics_type) {
		case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
		case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
			break;
		case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
		case Properties.LOGISTIC_TYPE_EXPRESS:
			if(long_distance!=1) {
				findViewById(R.id.tv_line).setVisibility(View.GONE);
				findViewById(R.id.ll_line).setVisibility(View.GONE);
			} else {
				findViewById(R.id.ll_distance).setVisibility(View.GONE);
			}
			break;
		case Properties.LOGISTIC_TYPE_LOAD_UNLOAD:
			findViewById(R.id.tv_line).setVisibility(View.GONE);
			findViewById(R.id.ll_line).setVisibility(View.GONE);
			break;
		}
		
		bt_forgoods = (Button) findViewById(R.id.bt_forgoods);
		bt_forgoods.setOnClickListener(this);
		//bt_forgoods.setOnClickListener(new CheckBoxClickListener());
		switch(logitics_type) {
		case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
		case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
		case Properties.LOGISTIC_TYPE_LOAD_UNLOAD:
			if(long_distance!=1) {
				findViewById(R.id.ll_serverzone).setVisibility(View.VISIBLE);
				bt_serverzone = (Button) findViewById(R.id.bt_serverzone);
				bt_serverzone.setOnClickListener(this);
				findViewById(R.id.ll_transhiprange).setVisibility(View.VISIBLE);
				et_transhiprange = (EditText) findViewById(R.id.et_transhiprange);
			}
			findViewById(R.id.ll_vechilelen).setVisibility(View.VISIBLE);
			bt_vechilelen = (Button) findViewById(R.id.bt_vechilelen);
			bt_vechilelen.setOnClickListener(this);
			findViewById(R.id.ll_quotecost).setVisibility(View.VISIBLE);
			et_quotecost = (EditText) findViewById(R.id.et_quotecost);
			if(Properties.LOGISTIC_TYPE_TRANSHIP_GOODS == logitics_type) {
				findViewById(R.id.bt_quotecost).setVisibility(View.GONE);
			} else {
				findViewById(R.id.tv_quotecost).setVisibility(View.GONE);
				bt_quotecost = (Button) findViewById(R.id.bt_quotecost);
				bt_quotecost.setOnClickListener(this);
			}
			break;
		case Properties.LOGISTIC_TYPE_EXPRESS:
			if(long_distance!=1) {
				findViewById(R.id.ll_serverzone).setVisibility(View.VISIBLE);
				bt_serverzone = (Button) findViewById(R.id.bt_serverzone);
				bt_serverzone.setOnClickListener(this);
			}
			findViewById(R.id.ll_minprice).setVisibility(View.VISIBLE);
			et_minprice = (EditText) findViewById(R.id.et_minprice);
			findViewById(R.id.ll_minamount).setVisibility(View.VISIBLE);
			et_minamount = (EditText) findViewById(R.id.et_minamount);
			bt_minamount = (Button) findViewById(R.id.bt_minamount);
			bt_minamount.setOnClickListener(this);
			findViewById(R.id.ll_largerprice).setVisibility(View.VISIBLE);
			et_largerprice = (EditText) findViewById(R.id.et_largerprice);
			tv_largerprice = (TextView) findViewById(R.id.tv_largerprice);
			//bt_largerprice.setOnClickListener(this);
			break;
		case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
			findViewById(R.id.ll_minprice).setVisibility(View.VISIBLE);
			et_minprice = (EditText) findViewById(R.id.et_minprice);
			
			findViewById(R.id.ll_quoteunit).setVisibility(View.VISIBLE);
			bt_quoteunit = (Button) findViewById(R.id.bt_quoteunit);
			bt_quoteunit.setOnClickListener(this);
			findViewById(R.id.ll_quoteone).setVisibility(View.VISIBLE);
			et_quotemin = (EditText) findViewById(R.id.et_quotemin);
			et_quotemax = (EditText) findViewById(R.id.et_quotemax);
			findViewById(R.id.ll_quoteoneunit).setVisibility(View.VISIBLE);
			et_quoteoneunit = (EditText) findViewById(R.id.et_quoteoneunit);
			findViewById(R.id.ll_quotetwo).setVisibility(View.VISIBLE);
			et_quotetwo = (EditText) findViewById(R.id.et_quotetwo);
			et_quotetwounit = (EditText) findViewById(R.id.et_quotetwounit);
			tv_dun = (TextView) findViewById(R.id.tv_dun);
			tv_yuandun = (TextView) findViewById(R.id.tv_yuandun);
			tv_dunyishang = (TextView) findViewById(R.id.tv_dunyishang);
			tv_yuandun2 = (TextView) findViewById(R.id.tv_yuandun2);
			findViewById(R.id.ll_quoteusetype).setVisibility(View.VISIBLE);
			bt_quoteusetype = (Button) findViewById(R.id.bt_quoteusetype);
			bt_quoteusetype.setOnClickListener(this);
			et_onequote = (EditText) findViewById(R.id.et_onequote);
			tv_oneyuandun = (TextView) findViewById(R.id.tv_oneyuandun);
			
			findViewById(R.id.ll_pickupexpense).setVisibility(View.VISIBLE);
			et_pickupexpense = (EditText) findViewById(R.id.et_pickupexpense);
			findViewById(R.id.ll_deliverexpense).setVisibility(View.VISIBLE);
			et_deliverexpense = (EditText) findViewById(R.id.et_deliverexpense);
			break;
		}
		et_otherexpense = (EditText) findViewById(R.id.et_otherexpense);
		bt_payway = (Button) findViewById(R.id.bt_payway);
		bt_payway.setOnClickListener(this);
		
		bt_permissionset = (Button) findViewById(R.id.bt_permissionset);
		bt_permissionset.setOnClickListener(this);
		
		et_goodsinfor = (EditText) findViewById(R.id.et_goodsinfor);
		et_zonelimit = (EditText) findViewById(R.id.et_zonelimit);
		switch(logitics_type) {
		case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
		case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
			findViewById(R.id.ll_vechiletype).setVisibility(View.VISIBLE);
			bt_vechiletype = (Button) findViewById(R.id.bt_vechiletype);
			bt_vechiletype.setOnClickListener(this);
			findViewById(R.id.ll_weight).setVisibility(View.VISIBLE);
			et_weight = (EditText) findViewById(R.id.et_weight);
			findViewById(R.id.ll_volume).setVisibility(View.VISIBLE);
			et_volume = (EditText) findViewById(R.id.et_volume);
			break;
		case Properties.LOGISTIC_TYPE_EXPRESS:
			//findViewById(R.id.ll_dispatchrange).setVisibility(View.VISIBLE);
			//et_dispathcrange = (EditText) findViewById(R.id.et_dispatchrange);
			break;
		case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
		case Properties.LOGISTIC_TYPE_LOAD_UNLOAD:
			//findViewById(R.id.ll_goodsinfor).setVisibility(View.GONE);
			findViewById(R.id.ll_zonelimit).setVisibility(View.GONE);
			findViewById(R.id.ll_other).setVisibility(View.VISIBLE);
			break;
		}
		
		findViewById(R.id.bt_add).setOnClickListener(this);

	}
	
	public boolean ViewEmptyInput(Button view, String toast) {
		if (TextUtils.isEmpty(view.getText().toString())) {
            ToastUtils.showToast(toast);
            return false;
        }
		return true;
	}
	
	public boolean ViewEmptyInput(EditText view, String toast) {
		if (TextUtils.isEmpty(view.getText().toString())) {
            ToastUtils.showToast(toast);
            return false;
        }
		return true;
	}
	
	public boolean ValidInput() {

		switch(logitics_type) {
		case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
		case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
			if (TextUtils.isEmpty(bt_startname.getText().toString()) ||
					TextUtils.isEmpty(bt_endname.getText().toString())) {
	            ToastUtils.showToast("请选择地址");
	            return false;
	        }
			if (TextUtils.isEmpty(et_distance.getText().toString())) {
	            ToastUtils.showToast("请输入公里数");
	            return false;
	        }
			break;
		case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
		case Properties.LOGISTIC_TYPE_EXPRESS:
			if(long_distance==1) {
				if (TextUtils.isEmpty(bt_startname.getText().toString()) ||
						TextUtils.isEmpty(bt_endname.getText().toString())) {
		            ToastUtils.showToast("请选择地址");
		            return false;
		        }
			}
			break;
		}
		
		switch(logitics_type) {
		case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
		case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
		case Properties.LOGISTIC_TYPE_LOAD_UNLOAD:
			if(long_distance!=1) {
				//findViewById(R.id.ll_serverzone).setVisibility(View.VISIBLE);
				//bt_serverzone = (Button) findViewById(R.id.bt_serverzone);
				if (TextUtils.isEmpty(bt_serverzone.getText().toString())) {
		            ToastUtils.showToast("请选择地址");
		            return false;
		        }
			}
			//findViewById(R.id.ll_vechilelen).setVisibility(View.VISIBLE);
			//bt_vechilelen = (Button) findViewById(R.id.bt_vechilelen);
			if (ViewEmptyInput(bt_vechilelen, "请选择车长")==false) {
	            return false;
	        }
			//findViewById(R.id.ll_quotecost).setVisibility(View.VISIBLE);
			//et_quotecost = (EditText) findViewById(R.id.et_quotecost);
			if (ViewEmptyInput(et_quotecost, "请选择报价")==false) {
	            return false;
	        }
			if(Properties.LOGISTIC_TYPE_TRANSHIP_GOODS == logitics_type) {
				//findViewById(R.id.bt_quotecost).setVisibility(View.GONE);
			} else {
				//findViewById(R.id.tv_quotecost).setVisibility(View.GONE);
				//bt_quotecost = (Button) findViewById(R.id.bt_quotecost);
				if (ViewEmptyInput(bt_quotecost, "请选择报价单位")==false)
		            return false;
			}
			break;
		case Properties.LOGISTIC_TYPE_EXPRESS:
			if(long_distance!=1) {
				//findViewById(R.id.ll_serverzone).setVisibility(View.VISIBLE);
				//bt_serverzone = (Button) findViewById(R.id.bt_serverzone);
				if (ViewEmptyInput(bt_serverzone, "请选择服务地区")==false)
					return false;
			}
			//findViewById(R.id.ll_minprice).setVisibility(View.VISIBLE);
			//et_minprice = (EditText) findViewById(R.id.et_minprice);
			if (ViewEmptyInput(et_minprice, "请输入起步价")==false)
	            return false;
			//findViewById(R.id.ll_minamount).setVisibility(View.VISIBLE);
			//et_minamount = (EditText) findViewById(R.id.et_minamount);
			if (ViewEmptyInput(et_minamount, "请输入起步量")==false)
	            return false;
			//bt_minamount = (Button) findViewById(R.id.bt_minamount);
			if (ViewEmptyInput(et_minamount, "请选择起步量单位")==false)
	            return false;
			//findViewById(R.id.ll_largerprice).setVisibility(View.VISIBLE);
			//et_largerprice = (EditText) findViewById(R.id.et_largerprice);
			if (ViewEmptyInput(et_largerprice, "请输入超出首重报价")==false)
	            return false;
			//bt_largerprice = (Button) findViewById(R.id.bt_largerprice);
//			if (ViewEmptyInput(bt_largerprice, "请选择超出首重报价单位")==false)
//	            return false;
			break;
		case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
			//findViewById(R.id.ll_minprice).setVisibility(View.VISIBLE);
			//et_minprice = (EditText) findViewById(R.id.et_minprice);
			if (ViewEmptyInput(et_minprice, "请输入起步价")==false)
	            return false;
			
			//findViewById(R.id.ll_quoteunit).setVisibility(View.VISIBLE);
			//bt_quoteunit = (Button) findViewById(R.id.bt_quoteunit);
			if (ViewEmptyInput(bt_quoteunit, "请选择报价单位")==false)
	            return false;
//			findViewById(R.id.ll_quoteone).setVisibility(View.VISIBLE);
//			et_quotemin = (EditText) findViewById(R.id.et_quotemin);
//			et_quotemax = (EditText) findViewById(R.id.et_quotemax);
//			findViewById(R.id.ll_quoteoneunit).setVisibility(View.VISIBLE);
//			et_quoteoneunit = (EditText) findViewById(R.id.et_quoteoneunit);
//			findViewById(R.id.ll_quotetwo).setVisibility(View.VISIBLE);
//			et_quotetwo = (EditText) findViewById(R.id.et_quotetwo);
//			et_quotetwounit = (EditText) findViewById(R.id.et_quotetwounit);
//			
//			findViewById(R.id.ll_pickupexpense).setVisibility(View.VISIBLE);
//			et_pickupexpense = (EditText) findViewById(R.id.et_pickupexpense);
//			findViewById(R.id.ll_deliverexpense).setVisibility(View.VISIBLE);
//			et_deliverexpense = (EditText) findViewById(R.id.et_deliverexpense);
			break;
		}
		//et_otherexpense = (EditText) findViewById(R.id.et_otherexpense);
		//bt_payway = (Button) findViewById(R.id.bt_payway);
		if (ViewEmptyInput(bt_payway, "请选择付款方式")==false) {
            return false;
        }
		
		if (ViewEmptyInput(bt_permissionset, "请选择公开对象")==false) {
            return false;
        }
		
//		bt_forgoods = (Button) findViewById(R.id.bt_forgoods);
//		bt_forgoods.setOnClickListener(this);
//		et_vechileinfor = (EditText) findViewById(R.id.et_vechileinfor);
//		et_zonelimit = (EditText) findViewById(R.id.et_zonelimit);
//		switch(logitics_type) {
//		case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
//			findViewById(R.id.ll_transhiprange).setVisibility(View.VISIBLE);
//			et_transhiprange = (EditText) findViewById(R.id.et_transhiprange);
//		case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
//			findViewById(R.id.ll_weight).setVisibility(View.VISIBLE);
//			et_weight = (EditText) findViewById(R.id.et_weight);
//			findViewById(R.id.ll_volume).setVisibility(View.VISIBLE);
//			et_volume = (EditText) findViewById(R.id.et_volume);
//			break;
//		case Properties.LOGISTIC_TYPE_EXPRESS:
//			findViewById(R.id.ll_dispatchrange).setVisibility(View.VISIBLE);
//			et_dispathcrange = (EditText) findViewById(R.id.et_dispatchrange);
//			break;
//		case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
//		case Properties.LOGISTIC_TYPE_LOAD_UNLOAD:
//			findViewById(R.id.ll_vechileinfor).setVisibility(View.GONE);
//			findViewById(R.id.ll_zonelimit).setVisibility(View.GONE);
//			findViewById(R.id.ll_other).setVisibility(View.VISIBLE);
//			break;
//		}

		return true;
	}
	
	@Override
	public void onClick(View arg0) {
		Dialog dialog;
		final String[] dialogsStrings;
		switch (arg0.getId()) {
		case R.id.bt_startname:
			ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_0_3, REQUEST_CODE_CHOOSE_REGIONS);
			break;
		case R.id.bt_endname:
			ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_0_3, REQUEST_CODE_CHOOSE_REGIONE);
			break;
		case R.id.bt_vechilelen:
			List<Dictionary> vechilelenType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRUCK_LENGTH);
			showDictionaryListDialog("选择车长", vechilelenType, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                	bt_vechilelen.setText(item.getName());
                	vechilelenCode = item.getId();
                }
            });
			break;
		case R.id.bt_quotecost:
			dialogsStrings = new String[] {"元/车", "元/吨", "元/方"};
        	dialog = new AlertDialog.Builder(this)
        		.setItems(dialogsStrings, new DialogInterface.OnClickListener() {
        				
        		public void onClick(DialogInterface dialog, int which) {     
        			bt_quotecost.setText(dialogsStrings[which]);
        			quotecostType = which;
        		} 
        	}).create();  
        	
        	dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 
        	dialog.show();
			break;
		case R.id.bt_serverzone:
			ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_0_3, REQUEST_CODE_SERVER_REGION);
			break;
		case R.id.bt_minamount:
			dialogsStrings = new String[] {"公斤", "件", "方"};
        	dialog = new AlertDialog.Builder(this)
        		.setItems(dialogsStrings, new DialogInterface.OnClickListener() {
        				
        		public void onClick(DialogInterface dialog, int which) {     
        			bt_minamount.setText(dialogsStrings[which]);
        			minamountType = which;
        			tv_largerprice.setText("元/"+dialogsStrings[which]);
        		} 
        	}).create();  
        	
        	dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 
        	dialog.show();
			break;
//		case R.id.bt_largerprice:
//			dialogsStrings = new String[] {"元/公斤", "元/件", "元/方"};
//        	dialog = new AlertDialog.Builder(this)
//        		.setItems(dialogsStrings, new DialogInterface.OnClickListener() {
//        				
//        		public void onClick(DialogInterface dialog, int which) {     
//        			bt_largerprice.setText(dialogsStrings[which]);
//        			largerpriceType = which;
//        		} 
//        	}).create();  
//        	
//        	dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 
//        	dialog.show();
//			break;
		case R.id.bt_quoteunit:
			dialogsStrings = new String[] {"吨", "方", "件"};
        	dialog = new AlertDialog.Builder(this)
        		.setItems(dialogsStrings, new DialogInterface.OnClickListener() {
        				
        		public void onClick(DialogInterface dialog, int which) {    
        			tv_dun.setText(dialogsStrings[which]);
        			tv_yuandun.setText("元/"+dialogsStrings[which]);
        			tv_dunyishang.setText(dialogsStrings[which]+"以上");
        			tv_yuandun2.setText("元/"+dialogsStrings[which]);
        			bt_quoteunit.setText(dialogsStrings[which]);
        			quoteunitType = which;
        			tv_oneyuandun.setText("元/"+dialogsStrings[which]);
        		} 
        	}).create();  
        	
        	dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 
        	dialog.show();
        	break;
		case R.id.bt_quoteusetype:
			if(quoteusetypecode==0) {
				quoteusetypecode = 1;
				findViewById(R.id.ll_quoteone).setVisibility(View.GONE);
				findViewById(R.id.ll_quoteoneunit).setVisibility(View.GONE);
				findViewById(R.id.ll_quotetwo).setVisibility(View.GONE);
				findViewById(R.id.ll_onequote).setVisibility(View.VISIBLE);
				bt_quoteusetype.setText("使用阶梯报价");
			} else {
				quoteusetypecode = 0;
				findViewById(R.id.ll_quoteone).setVisibility(View.VISIBLE);
				findViewById(R.id.ll_quoteoneunit).setVisibility(View.VISIBLE);
				findViewById(R.id.ll_quotetwo).setVisibility(View.VISIBLE);
				findViewById(R.id.ll_onequote).setVisibility(View.GONE);
				bt_quoteusetype.setText("使用单一报价");
			}
			break;
		case R.id.bt_payway:
        	final String[] payway = new String[] {"预付", "到付", "月结"};
        	dialog = new AlertDialog.Builder(this)
        		.setItems(payway, new DialogInterface.OnClickListener() {
        				
        		public void onClick(DialogInterface dialog, int which) {     
        			bt_payway.setText(payway[which]);
        			paywayType = which;
        		} 
        	}).create();  
        	
        	dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 
        	dialog.show();
        	break;
		case R.id.bt_permissionset:
        	//final String[] permissionset = new String[] {"所有人", "所有联系人", "某位联系人", "具有某类标签的联系人", "不公开"};
        	final String[] permissionset = new String[] {"所有人", "向特定对象公开", "不公开"};
        	dialog = new AlertDialog.Builder(this)
        		.setItems(permissionset, new DialogInterface.OnClickListener() {
        				
        		public void onClick(DialogInterface dialog, int which) {   
        			if(which==1)
        			{
        				Intent intent1 = new Intent(AddQuoteActivity.this,
        						ChooseSpecificContactsActivity.class);
        				intent1.putExtra("originalUserId", UserDao.getInstance().getUser().getId());
        				if(mContactsSelectedList!=null && mContactsSelectedList.size()>0)
        					intent1.putExtra(ChooseSpecificContactsActivity.EXTRA_ORIGINAL_CONTACTS_LIST, mContactsSelectedList);
        				startActivityForResult(intent1, REQUEST_CHOOSE_CONTACTS_NOTICE);
        			}
        			bt_permissionset.setText(permissionset[which]);
        			permissionsetType = which;
        		} 
        	}).create();  
        	
        	dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 
        	dialog.show();
        	break;
		case R.id.bt_forgoods:
			List<Dictionary> goodsType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_GOODS_TYPE);
			showDictionaryListDialog("选择货物类型", goodsType, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                	bt_forgoods.setText(item.getName());
                	forgoodsType = item.getId();
                }
            });
			break;
		case R.id.bt_vechiletype:
			List<Dictionary> vechileType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRUCK_TYPE);
			showDictionaryListDialog("选择车型", vechileType, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                	bt_vechiletype.setText(item.getName());
                	vechiletypeCode = item.getId();
                }
            });
			break;
		case R.id.bt_add:
			//if(!ValidInput())
			//	break;
			AddQuotetoData();
			break;
		default:
			break;
		}
	}
	
	class AlertClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			new AlertDialog.Builder(AddQuoteActivity.this).setTitle("选择货物类型").setItems(areas,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					//Toast.makeText(AddQuoteActivity.this, "您已经选择了: " + which + ":" + areas[which],Toast.LENGTH_LONG).show();
					dialog.dismiss();
				}
			}).show();
		}
	}

	class CheckBoxClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			AlertDialog ad = new AlertDialog.Builder(AddQuoteActivity.this)
			.setTitle("选择货物类型")
			.setMultiChoiceItems(areas,areaState,new DialogInterface.OnMultiChoiceClickListener(){
				public void onClick(DialogInterface dialog,int whichButton, boolean isChecked){
					//点击某个区域
				}
			}).setPositiveButton("确定",new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog,int whichButton){
					String s = "";
					forgoodsType = 0;
					for (int i = 0; i < areas.length; i++){
						if (areaCheckListView.getCheckedItemPositions().get(i)){
							s += areaCheckListView.getAdapter().getItem(i)+ " ";
							forgoodsType += 1<<i;
							
						}else{
							areaCheckListView.getCheckedItemPositions().get(i,false);
						}
					}
					s=s.trim();

					if (areaCheckListView.getCheckedItemPositions().size() > 0){
						bt_forgoods.setText(s);
						//Toast.makeText(AddQuoteActivity.this, s, Toast.LENGTH_LONG).show();
					}else{
						//没有选择
					}
					dialog.dismiss();
				}
			}).setNegativeButton("取消", null).create();
			areaCheckListView = ad.getListView();
			ad.setCanceledOnTouchOutside(true);
			ad.show();
		}
	}

	public void AddQuotetoData() {
		ToastUtils.showToast("调用添加流程");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE_REGIONS) {
                RegionResult result = (RegionResult) data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
                startRegionCode = result.getFullCode();
            	bt_startname.setText(result.getShortNameFromDistrict());
            } else if (requestCode == REQUEST_CODE_CHOOSE_REGIONE) {
            	RegionResult result = (RegionResult) data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
            	endRegionCode = result.getFullCode();
            	bt_endname.setText(result.getShortNameFromDistrict());
            }
            else if (requestCode == REQUEST_CODE_SERVER_REGION) {
            	RegionResult result = (RegionResult) data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
            	serverzoneCode = result.getFullCode();
            	bt_serverzone.setText(result.getShortNameFromDistrict());
            } else if(requestCode == REQUEST_CHOOSE_CONTACTS_NOTICE) {
            	String userId = data.getStringExtra("originalUserId");
                mContactsSelectedList = (ArrayList<Contacts>) data.getSerializableExtra(ChooseSpecificContactsActivity.EXTRA_SELECTED_CONTACTS_LIST);
            }
    	
        }
	}

	@Override
	protected TitleParams getTitleParams() {
		nameactivity = getIntent().getStringExtra("quotename");
		logitics_type = getIntent().getIntExtra("logiticstype", 1);
		
		switch (logitics_type) {
		case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
		case Properties.LOGISTIC_TYPE_EXPRESS:
			return new TitleParams(getDefaultHomeAction(), nameactivity, null).setShowLogo(false);
		default:
			return new TitleParams(getDefaultHomeAction(), "添加报价", null).setShowLogo(false);
		}
	}

}
