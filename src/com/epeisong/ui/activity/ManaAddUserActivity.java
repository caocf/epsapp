package com.epeisong.ui.activity;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.RegionResult;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.epeisong.utils.java.JavaUtils;
import com.test.request.RatioView;
import com.test.request.RequestTest;
import com.test.request.RequestTestActivity.RequestResult;

/**
 * 添加用户(平台角色--任务管理)
 * 
 * @author Jack
 * 
 */
public class ManaAddUserActivity extends BaseActivity implements OnClickListener {
	public static boolean ADDUSERDEBUGGING = true;
	private static final int thread_count = 1;
	private static final int request_count = 10;
	
	private static final int REQUEST_CODE_CHOOSE_REGION = 100;
	
    private EditText et_useraccount, et_username, et_paypassword, et_logpassword;
    private Button bt_cityname;
    private TextView et_typename;
    private int serveRegionCode;
    private int logisticType;
    
    List<RequestThread> mRequestThreads = new ArrayList<ManaAddUserActivity.RequestThread>();
    String mLogFileName;
    MyAdapter mAdapter;
    ListView mListView;
    TextView mFileNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mana_add_user);

        et_useraccount = (EditText) findViewById(R.id.et_useraccount);
        et_logpassword = (EditText) findViewById(R.id.et_logpassword);
        et_typename = (TextView) findViewById(R.id.et_typename);
        et_username = (EditText) findViewById(R.id.et_username);
        et_paypassword = (EditText) findViewById(R.id.et_paypassword);
        findViewById(R.id.ll_button).setOnClickListener(this);
        findViewById(R.id.bt_finish).setOnClickListener(this);
        findViewById(R.id.bt_finishm).setOnClickListener(this);
        findViewById(R.id.bt_finishm).setVisibility(View.GONE);
        bt_cityname = (Button) findViewById(R.id.bt_cityname);
        bt_cityname.setOnClickListener(this);
        
        mListView = (ListView) findViewById(R.id.lv);
        mFileNameTv = (TextView) findViewById(R.id.tv_file_name);
        if(ADDUSERDEBUGGING)
        	mListView.setAdapter(mAdapter = new MyAdapter());
        else {
			mListView.setVisibility(View.GONE);
		}
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ll_button:
        	final String[] arrayFruit = new String[] {"配货市场", "物流园", "担保", "保险", "平台客服", "钱包管理", "钱包提现"};
        	final int[] arraytype = new int[] {Properties.LOGISTIC_TYPE_MARKET, Properties.LOGISTIC_TYPE_LOGISTICS_PARK, 
        			Properties.LOGISTIC_TYPE_GUARANTEE, Properties.LOGISTIC_TYPE_INSURANCE, Properties.LOGISTIC_TYPE_PLATFORM_CUSTOMER_SERVICE,
        			Properties.LOGISTIC_TYPE_WALLET_MANAGE, Properties.LOGISTIC_TYPE_WALLET_CASH};
        	//OnClickListener listener = null;  
        	Dialog dialog = new AlertDialog.Builder(this)
        			.setItems(arrayFruit, new DialogInterface.OnClickListener() {
        				
        			public void onClick(DialogInterface dialog, int which) {     
        				et_typename.setText(arrayFruit[which]);
        				logisticType = arraytype[which];
        				//Toast.makeText(ManaAddUserActivity.this, arrayFruit[which], Toast.LENGTH_SHORT).show();    
        				} 
        			}).create();  
        	
//        	Window win = dialog.getWindow();  
//        	LayoutParams params = new LayoutParams();  
//        	params.x = -80;//设置x坐标  
//        	params.y = -60;//设置y坐标  
//        	win.setAttributes(params);  
        	dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 

        	dialog.show();
        	//dialog.getWindow().setLayout(200, -1);
        	WindowManager.LayoutParams params = dialog.getWindow().getAttributes();    
        	params.width = 600;    
        	params.height = 1200;    
        	dialog.getWindow().setAttributes(params); 
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
            }
            else {
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
            if (!JavaUtils.isPwdValid(passwordString)) {
                ToastUtils.showToast("支付密码不正确");
                return;
            }
            //CleanInputInfor();
            AddUser(accountString, logString, typeString, namesString, cityString, passwordString, true);
            //InputPayPassword(0, null);
            break;
            
        case R.id.bt_finishm:
        	if(ADDUSERDEBUGGING)
        		;
        	else {
        	final String accountString1 = et_useraccount.getText().toString();
            if (TextUtils.isEmpty(accountString1)) {
                ToastUtils.showToast("账户不能为空");
                return;
            }
            final String typeString1 = et_typename.getText().toString();
            if (TextUtils.isEmpty(typeString1)) {
                ToastUtils.showToast("请选择角色类别");
                return;
            }

            final String namesString1 = et_username.getText().toString();
            if (TextUtils.isEmpty(namesString1)) {
                ToastUtils.showToast("请输入用户名称");
                return;
            }
            
            final String cityString1 = bt_cityname.getText().toString();
            if (TextUtils.isEmpty(cityString1)) {
                ToastUtils.showToast("请选择地区");
                return;
            }
            
            final String passwordString1 = et_paypassword.getText().toString();
            if (!JavaUtils.isPwdValid(passwordString1)) {
                ToastUtils.showToast("支付密码不正确");
                return;
            }
            
        	}
            if(ADDUSERDEBUGGING)
            	start(thread_count, request_count);
            break;
        }

    }
  
    public void AddUser(final String accountString, final String logString, final String typeString, final String namesString, 
    		final String cityString, final String passwordString, final Boolean showtoast) {
        
        AsyncTask<Void, Void, Resp> task = new AsyncTask<Void, Void, Resp>() {
            @Override
            protected Resp doInBackground(Void... params) {
            	//ApiExecutor api = new ApiExecutor();
                try {
                	//User user=UserDao.getInstance().getUser();
                    return null;
                    //api.createAccountAndLogisticByPlatformManager(accountString, logisticType, typeString,
                    //		namesString, serveRegionCode, cityString, user.getAccount_name(),
                    //		SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), passwordString
                    //		, Properties.OPERATION_TYPE_CREATE_ACCOUNT_AND_LOGISTIC_BY_PLATFORM_MANAGER, Properties.APP_CLIENT_BIG_TYPE_PHONE, logString);
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
        				
        				//CleanInputInfor();

//        			} else if (result.getResult() == -1) {
//        				ToastUtils.showToast("用户账号已经存在");
        			} else {
        				ToastUtils.showToast(result.getDesc());
        			}
                }
            }
        };
        task.execute();

//    	AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {
//    		@Override
//    		protected Integer doInBackground(Void... params) {
//    			//NetWalletBank netWalletBank = new NetWalletBank();
//
//    			int ret = 0;
//    			try {
//    			} catch (Exception e) {
//    				e.printStackTrace();
//    			}
//
//    			return ret;
//    		}
//
//    		@Override
//    		protected void onPostExecute(Integer result) {
//    			if (result > 0) {
//    				ToastUtils.showToast("添加成功");
//
//    			} else if (result == -1) {
//    				ToastUtils.showToast("用户账号已经存在");
//    			}
//    			else {
//					ToastUtils.showToast("添加错误");
//				}
//    		}
//    	};
//
//    	task.execute();
    }
    
    public void CleanInputInfor() {
        et_useraccount.setText("");
        et_username.setText("");
        et_paypassword.setText("");
        bt_cityname.setText("请选择");
        bt_cityname.setTextColor(Color.parseColor("#cdd4da"));
        et_typename.setText("");
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
            }
        }
    }
    
    void InputPayPassword(final int index, final String name) {
    	
		final AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
		LayoutInflater factory = LayoutInflater.from(this);  
		View view = factory.inflate(R.layout.dialog_paypassword, null);  
		// builder.setIcon(R.drawable.icon);  
		//builder.setTitle("自定义输入框");  
		final EditText et_password = (EditText) view.findViewById(R.id.et_password);
		//final Intent i = new Intent(this, WalletAddBankActivity.class);
		
		builder.setView(view);  
		((TextView) view.findViewById(R.id.tv_passwordhint)).setText("添加用户前，请输入支付密码");
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
						//AddUser();
				} else {
					ToastUtils.showToast("密码不正确！");
					return;
				}
			}
		});
		
    }
    
    private void start(int threadCount, int requestCount) {
        if (!mRequestThreads.isEmpty()) {
            ToastUtils.showToast("当前正在执行，" + mRequestThreads.size());
            return;
        }
        Calendar cal = Calendar.getInstance();
        mLogFileName = DateUtil.long2YMD(System.currentTimeMillis()) + "_" + cal.get(Calendar.HOUR_OF_DAY) + "_"
                + cal.get(Calendar.MINUTE) + "_" + cal.get(Calendar.SECOND) + ".txt";

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "crash_eps_request_log");
        mFileNameTv.setText("日志文件：" + dir.getAbsolutePath() + "/" + mLogFileName);
        for (int i = 0; i < threadCount; i++) {
            RequestThread t = new RequestThread(requestCount) {
                @Override
                protected RequestResult request() {
                    return RequestTest.test(requestCount, 1,1);
                }
            };
            mRequestThreads.add(t);
            t.start();
        }
        mAdapter.replaceAll(mRequestThreads);
        ToastUtils.showToast(threadCount + "个线程启动完毕");
    }

    abstract class RequestThread extends Thread {
        boolean stop;
        int requestCount;
        int successCount;
        int failCount;
        long duration;

        public RequestThread(int requestCount) {
            this.requestCount = requestCount;
        }

        @Override
        public void run() {
            String threadName = getName();
            int count = 0;
            long start = System.currentTimeMillis();
            while (!stop && count++ < requestCount) {
                RequestResult result = request();
                if (result.success) {
                    successCount++;
                } else {
                    failCount++;
                }
                String content = DateUtil.long2YMDHMSS(System.currentTimeMillis()) + "\n" + threadName + "第" + count
                        + "次请求:\n" + result.log + "\n\n";
                writeLog(mLogFileName, content);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
            String content = threadName + requestCount + "次请求，" + successCount + "次成功,成功率：" + 100f * successCount
                    / requestCount + "%\n";
            writeLog(mLogFileName, content);
            stop = true;
            mRequestThreads.remove(this);

            duration = System.currentTimeMillis() - start;
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
            String str = threadName + "耗时：" + DateUtil.long2MS_SSS(duration) + "\n\n";
            writeLog(mLogFileName, str);
        }

        protected abstract RequestResult request();
    }

//    static class RequestResult {
//        boolean success;
//        String log;
//    }

    private void writeLog(String fileName, String content) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "crash_eps_request_log");
        if (!dir.exists()) {
            dir.mkdir();
        }

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(new File(dir, fileName), "rw");
            raf.seek(raf.length());
            raf.write(content.getBytes("GBK"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
            }
        }
    }

    class MyAdapter extends HoldDataBaseAdapter<RequestThread> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RatioView ratioView = null;
            if (convertView == null) {
                ratioView = new RatioView(ManaAddUserActivity.this);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(-1, DimensionUtls.getPixelFromDpInt(20));
                ratioView.setLayoutParams(params);
                convertView = ratioView;
            } else {
                ratioView = (RatioView) convertView;
            }
            RequestThread item = getItem(position);
            ratioView.setTotal(item.requestCount);
            ratioView.setSuccess(item.successCount);
            ratioView.setFail(item.failCount);
            if (item.stop) {
                ratioView.setDuration(DateUtil.long2MS_SSS(item.duration));
            }
            return convertView;
        }

    }
    
    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "添加用户", null).setShowLogo(false);
    }
}
