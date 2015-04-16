package com.epeisong.ui.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshListView;
import lib.pulltorefresh.PullToRefreshBase.Mode;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.User;
import com.epeisong.model.Wallet;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.WalletDetail;
import com.epeisong.net.ws.utils.WalletResp;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 钱包账户明细
 * @author Jack
 *
 */
public class WalletDetailActivity extends BaseActivity implements OnDateSetListener, OnClickListener, OnItemClickListener {
	private final static int ACCOUNT_DETAIL_ALL = 0;
	private final static int ACCOUNT_DETAIL_DAY = 1;
	private final static int ACCOUNT_DETAIL_MONTH = 2;
	private final static int ACCOUNT_DETAIL_YEAR = 3;
	private final static int BOND_WALLET_DETAIL_OPERATION_TYPE_WITHDRAW = 2;
	private final static int BOND_WALLET_DETAIL_OPERATION_TYPE_DEPOSIT = 1;
	private final static int WALLET_DETAIL_OPERATION_TYPE_WITHDRAW_PAYER_DESC = 5;

    public TextView tv_select_time;
    private LinearLayout rl_time;
    
    private ImageView iv_search;

    private int mDetailYear=-1, mDetailMonth=-1, mDetailDay=-1;
    private int mAccount_Detail_type;
    String initDateTime;
    
	protected MyAdapter mAdapter;
    private PullToRefreshListView lv_black;
    private TextView mTextViewEmpty;
    
    private Wallet mWallet;
    private int mWalletId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	mWallet = (Wallet) getIntent().getSerializableExtra("wallet");
    	if(mWallet!=null) {
    		mWalletId=mWallet.getId();
    	}
        super.onCreate(savedInstanceState);
        
        mAccount_Detail_type = ACCOUNT_DETAIL_ALL;
        setContentView(R.layout.activity_wallet_guaratee_manager);
        tv_select_time = (TextView) findViewById(R.id.tv_select_time);
        rl_time = (LinearLayout) findViewById(R.id.rl_time);
        rl_time.setOnClickListener(this);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setOnClickListener(this);
        
		lv_black = (PullToRefreshListView) findViewById(R.id.lv_wallet_manager);
		
        lv_black.setAdapter(mAdapter = new MyAdapter());
        lv_black.setMode(Mode.BOTH);
        lv_black.setOnItemClickListener(this);
        lv_black.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            	requestData(0, 10, Properties.PULL_DOWN_GET_LIST_TYPE_NEWEST);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mAdapter.isEmpty()) {
                    HandlerUtils.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        	lv_black.onRefreshComplete();
                        }
                    }, 100);
                    return;
                }
                int lastSyncIndex;
                if(mAdapter.getItem(mAdapter.getCount() - 1).getId()!=null) {
                	lastSyncIndex = Integer.valueOf(mAdapter.getItem(mAdapter.getCount() - 1).getId());
                }
                else {
                	lastSyncIndex = 0;
				}
                requestData(lastSyncIndex, 10, Properties.PULL_GET_LIST_TYPE_OLD);
            }
        });
		
        setEmptyView();
        requestData(0, 10, Properties.PULL_DOWN_GET_LIST_TYPE_NEWEST);
        
    }
    
	private void requestData(final int lastSyncIndex, final int size, final int type) {
		AsyncTask<Void, Void, WalletResp> task = new AsyncTask<Void, Void, WalletResp>() {
			@Override
			protected WalletResp doInBackground(Void... params) {
				ApiExecutor api = new ApiExecutor();
				try {
					User user=UserDao.getInstance().getUser();
					//String uname, String upwd, int detailId, int type, int count, int year,
		    		//int month, int date
						return api.listDetail(user.getAccount_name(), 
							SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), lastSyncIndex, type, 10, mDetailYear, mDetailMonth, mDetailDay);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(WalletResp result) {

				lv_black.onRefreshComplete();
				if (lastSyncIndex > 0) {
					if(result!=null)
						mAdapter.addAll(result.getWalletDetailList());
				} else {
					if(result==null) {
						mAdapter.clear();
					} else {

						mAdapter.replaceAll(result.getWalletDetailList());
					}
				}
			}
		};
		task.execute();
	}
    
    private void setEmptyView() {
        LinearLayout emptyLayout = new LinearLayout(this);
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyLayout.setPadding(0, DimensionUtls.getPixelFromDpInt(100), 0, 0);
        mTextViewEmpty = new TextView(this);
        mTextViewEmpty.setText("没有数据");
        mTextViewEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mTextViewEmpty.setTextColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
        mTextViewEmpty.setGravity(Gravity.CENTER);
        emptyLayout.addView(mTextViewEmpty);
        lv_black.setEmptyView(emptyLayout);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "收支明细", null).setShowLogo(false);
    }
    
	protected class MyAdapter extends HoldDataBaseAdapter<WalletDetail> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.item_search_guadetail);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if(position==0)
				holder.fillData(getItem(position), null, position);
			else 
				holder.fillData(getItem(position), getItem(position-1), position);
			return convertView;
		}
	}

	private class ViewHolder {
		TextView tv_yearmonth;
		TextView tv_time, tv_amount, tv_feenum, tv_guatype;
		ImageView iv_guarantee;
		
		public void fillData(WalletDetail nTask, WalletDetail nLastTask, int pos) {
			User u = UserDao.getInstance().getUser();
			int id = Integer.valueOf(u.getId());
            
			if(nTask.getId()==184)
				id=id+1-1;
			Calendar ncal = Calendar.getInstance();
			ncal.setTimeInMillis(nTask.getCreateDate());
			if(mAccount_Detail_type==ACCOUNT_DETAIL_ALL || mAccount_Detail_type==ACCOUNT_DETAIL_YEAR) {
		        
				//String nstrString = DateUtil.long2YMD(nTask.getCreateDate());
		        Calendar lcal = Calendar.getInstance();
				if(nLastTask!=null) {
			        lcal.setTimeInMillis(nLastTask.getCreateDate());
				}
				Calendar tcal = Calendar.getInstance();
				if(nLastTask!=null && lcal.get(Calendar.YEAR) == ncal.get(Calendar.YEAR) && lcal.get(Calendar.MONTH) == ncal.get(Calendar.MONTH))
				{
					tv_yearmonth.setVisibility(View.GONE);
				}
				else {
					tv_yearmonth.setVisibility(View.VISIBLE);
					if(tcal.get(Calendar.YEAR) == ncal.get(Calendar.YEAR) && tcal.get(Calendar.MONTH) == ncal.get(Calendar.MONTH))
						tv_yearmonth.setText("本月");
					else
						tv_yearmonth.setText(ncal.get(Calendar.YEAR)+"年"+(ncal.get(Calendar.MONTH)+1)+"月");
				}
			} else {
				tv_yearmonth.setVisibility(View.GONE);
			}
			
			if(nTask.getOperationType()!=null)
				iv_guarantee.setImageResource(getInforFee_pic(nTask.getOperationType()));
			if(nTask.getPayeeWalletId()!=null && nTask.getPayeeWalletId() == mWalletId ) {
				if(nTask.getPayeeDesc()!=null)
					tv_guatype.setText(String.valueOf(nTask.getPayeeDesc()));
				//iv_guarantee.setImageResource(getInforFee_pic(2));
			} else if(nTask.getPayerWalletId()!=null && nTask.getPayerWalletId() == mWalletId ){
				if(nTask.getPayerDesc()!=null)
					tv_guatype.setText(String.valueOf(nTask.getPayerDesc()));
				//iv_guarantee.setImageResource(getInforFee_pic(3));
			}
			else {
				tv_guatype.setText("");
			}
			
			if(nTask.getDealBillId()!=null)
				tv_feenum.setText(getInforFee_String(Properties.DEAL_BILL_TYPE_INFO_FEE)+String.valueOf(nTask.getDealBillId()));
			else {
				tv_feenum.setText("");
			}
			if(nTask.getPayeeWalletId()!=null && nTask.getPayeeWalletId() == mWalletId ) {
				tv_amount.setText(String.valueOf(nTask.getAmount()/100.0));
			} else if(nTask.getPayerWalletId()!=null && nTask.getPayerWalletId() == mWalletId ){
				tv_amount.setText("-"+String.valueOf(nTask.getAmount()/100.0));
			}
			else {
				tv_amount.setText("");
			}
//			String timeString = "";
//			if(ncal.get(Calendar.MONTH)<9)
//				timeString += "0";
//			timeString += (ncal.get(Calendar.MONTH)+1);
//			timeString += "-";
//			if(ncal.get(Calendar.DATE)<10)
//				timeString += "0";
//			timeString += ncal.get(Calendar.DATE);
			tv_time.setText(DateUtil.long2vaguehourMinute(nTask.getCreateDate()));//timeString);
			
		}

		public void findView(View v) {
			iv_guarantee = (ImageView) v.findViewById(R.id.iv_guarantee);
			tv_yearmonth = (TextView) v.findViewById(R.id.tv_yearmonth);
			tv_guatype = (TextView) v.findViewById(R.id.tv_guatype);
			tv_feenum = (TextView) v.findViewById(R.id.tv_feenum);
			tv_amount = (TextView) v.findViewById(R.id.tv_amount);
			tv_time = (TextView) v.findViewById(R.id.tv_time);
				
		}
	}
	
	public int getInforFee_pic(int type) {
		switch(type) {
		case BOND_WALLET_DETAIL_OPERATION_TYPE_WITHDRAW:
		case BOND_WALLET_DETAIL_OPERATION_TYPE_DEPOSIT:
			return R.drawable.fee_gua;
		case WALLET_DETAIL_OPERATION_TYPE_WITHDRAW_PAYER_DESC:
			return R.drawable.fee_output;
		//case 4:
		//	return R.drawable.fee_input;
		default:
			return R.drawable.fee_info;
		}
	}
	
	public String getInforFee_String(int type) {
		
		return "订单号:";//车源货源
	}

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
        case R.id.rl_time:
        	final String[] arrayFruit = new String[] {"全部", "按日", "按月", "按年",};
        	Dialog dialog = new AlertDialog.Builder(this)
        	.setItems(arrayFruit, new DialogInterface.OnClickListener() {

        		public void onClick(DialogInterface dialog, int which) { 
        			
        			if(which==ACCOUNT_DETAIL_ALL && mAccount_Detail_type!=ACCOUNT_DETAIL_ALL) {
        				tv_select_time.setText(arrayFruit[which]);
        				mAccount_Detail_type = which;
        				mDetailYear=-1;
        				mDetailMonth=-1;
        				mDetailDay=-1;
        				requestData(0, 10, Properties.PULL_DOWN_GET_LIST_TYPE_NEWEST);
        			} else if(which==ACCOUNT_DETAIL_DAY){
        				GetYMDDialogue();
        			}
        			else if(which==ACCOUNT_DETAIL_MONTH){
        				GetYMDialogue();
        			}
        			else if(which==ACCOUNT_DETAIL_YEAR){
        				GetYearDialogue();
        			}
        		} 
        	}).create();  
        	
        	dialog.setCanceledOnTouchOutside(true); 

        	dialog.show();
        	WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        	dialog.getWindow().setAttributes(params); 
        	
        	break;
        case R.id.iv_search:
            break;

        default:
            break;
        }

    }
    
	/**
	 * 重写datePicker 1.只显示 年-月 2.title 只显示 年-月
	 */
	public class MonPickerDialog extends DatePickerDialog {
		public MonPickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
			super(context, callBack, year, monthOfYear, dayOfMonth);
			this.setTitle(year + "年" + (monthOfYear+1) + "月");
			
			((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
		}

		@Override
		public void onDateChanged(DatePicker view, int year, int month, int day) {
			super.onDateChanged(view, year, month, day);

			this.setTitle(year + "年" + (month+1) + "月");
		}

	}
	
	/**
	 * 重写datePicker 1.只显示 年 2.title 只显示 年
	 */
	public class YearPickerDialog extends DatePickerDialog {
		public YearPickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
			super(context, callBack, year, monthOfYear, dayOfMonth);
			this.setTitle((year) + "年");
			
			((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
			((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
		}

		@Override
		public void onDateChanged(DatePicker view, int year, int month, int day) {
			super.onDateChanged(view, year, month, day);

			this.setTitle((year) + "年");
		}
		
	}
	
    public void GetYMDDialogue() {
    	
		final Calendar localCalendar = Calendar.getInstance();
		if(mAccount_Detail_type == ACCOUNT_DETAIL_DAY)
			localCalendar.set(mDetailYear, mDetailMonth-1, mDetailDay);
		DatePickerDialog dlg=new DatePickerDialog(this,this,localCalendar.get(Calendar.YEAR), 
				localCalendar.get(Calendar.MONTH),localCalendar.get(Calendar.DAY_OF_MONTH));
		 //新建一个DatePickerDialog 构造方法中         
		 //     （设备上下文，OnDateSetListener时间设置监听器，默认年，默认月，默认日）
		dlg.show();
		 //让DatePickerDialog显示出来
		
//	    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//	    //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		
//	    //showMenuPopupWindow();
//	    DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
//	    		WalletGuarateeDetailActivity.this);
//		dateTimePicKDialog.dateTimePicKDialog(tv_select_time);
    }
	
	@Override
	public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
		tv_select_time.setText(arg1+"."+(arg2+1)+"."+arg3);
		mDetailYear=arg1;
		mDetailMonth=arg2+1;
		mDetailDay=arg3;
		mAccount_Detail_type=ACCOUNT_DETAIL_DAY;
		requestData(0, 10, Properties.PULL_DOWN_GET_LIST_TYPE_NEWEST);
	}

	public void GetYearDialogue() {

		final Calendar localCalendar = Calendar.getInstance();
		if(mAccount_Detail_type == ACCOUNT_DETAIL_YEAR)
			localCalendar.set(mDetailYear+1, -1, -1);
		//localCalendar.setTime(DateUtils.strToDate("yyyy-MM", tv_select_time.getText().toString()));
		new YearPickerDialog(this,new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
				Calendar lCalendar = Calendar.getInstance();
				lCalendar.set(1, year);
				//localCalendar.set(year, monthOfYear);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
				mDetailYear=year;
				mDetailMonth=-1;
				mDetailDay=-1;
				mAccount_Detail_type=ACCOUNT_DETAIL_YEAR;
				tv_select_time.setText(sdf.format(lCalendar.getTime()));//DateUtils..clanderTodatetime(localCalendar, "yyyy-MM"));
				requestData(0, 10, Properties.PULL_DOWN_GET_LIST_TYPE_NEWEST);
			}
		}, localCalendar.get(1), localCalendar.get(2),localCalendar.get(5)).show();
	}

	public void GetYMDialogue() {

		final Calendar localCalendar = Calendar.getInstance();
		if(mAccount_Detail_type == ACCOUNT_DETAIL_MONTH)
			localCalendar.set(mDetailYear, mDetailMonth, -1);
		//localCalendar.setTime(DateUtils.strToDate("yyyy-MM", tv_select_time.getText().toString()));
		new MonPickerDialog(this,new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
				Calendar lCalendar = Calendar.getInstance();
				lCalendar.set(1, year);
				lCalendar.set(2, monthOfYear);
				//localCalendar.set(year, monthOfYear);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
				mDetailYear=year;
				mDetailMonth=monthOfYear+1;
				mDetailDay=-1;
				mAccount_Detail_type=ACCOUNT_DETAIL_MONTH;
				tv_select_time.setText(sdf.format(lCalendar.getTime()));//DateUtils..clanderTodatetime(localCalendar, "yyyy-MM"));
				requestData(0, 10, Properties.PULL_DOWN_GET_LIST_TYPE_NEWEST);
			}
		}, localCalendar.get(1), localCalendar.get(2),localCalendar.get(5)).show();
	}
    
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
	
}