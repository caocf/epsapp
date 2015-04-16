package com.epeisong.ui.activity.user;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.epeisong.EpsApplication;
import com.epeisong.EpsNetConfig;
import com.epeisong.R;
import com.epeisong.ReleaseConfig;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.net.NetService;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.logistics.proto.Base.ProtoSysDictionary;
import com.epeisong.logistics.proto.Eps.AppNewVersionResp;
import com.epeisong.logistics.proto.Eps.AppNewVersionResp.Builder;
import com.epeisong.logistics.proto.Eps.SysDictionaryResp;
import com.epeisong.model.Dictionary;
import com.epeisong.service.notify.ComActDb;
import com.epeisong.service.notify.MenuBean;
import com.epeisong.service.notify.MenuEnum;
import com.epeisong.ui.activity.temp.LoginActivity;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.ReleaseLog;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 加载界面
 * 
 * @author poet
 * 
 */
public class LoadingActivity extends TempActivity {

    private long mPendingTime = 800;
    private long mStartTime;
 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView iv  = new ImageView(getApplicationContext());
        iv.setImageResource(R.drawable.eps_loading);
        iv.setScaleType(ScaleType.FIT_XY);
        setContentView(iv);
        ProgressBar bar  = new ProgressBar(this);
        addContentView(bar, new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER));

        mStartTime = System.currentTimeMillis();

        final NetService netService = NetServiceFactory.getInstance();
        netService.setContext(getApplicationContext());
        netService.setDstName(EpsNetConfig.getHost());
        netService.setDstPort(EpsNetConfig.PORT);

        /******************** 检查新版本 **************************/
        checkDictionory(netService);
 
        ComActDb db = new ComActDb(LoadingActivity.this)  ;
        	SQLiteDatabase sqldb = db.getWritableDatabase();
        	if(!db.isTableExist(MenuBean.class.getSimpleName())) {
        		db.executeSql(db.getCreateSql(MenuBean.class));
        	}
        	 
        	if(sqldb !=null) {
        		sqldb.close();
        		sqldb= null;
        	}

        	
        	
        	iv.getDrawable().setCallback(null);
 
    }

    private void checkDictionory(final NetService frontendService) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    int version = SpUtils.getInt(SpUtils.KEYS_SYS.INT_DICTIONARY_VERSION, 0);
                    if (version > 0) {
                        return true;
                    }
                    SysDictionaryResp.Builder resp = frontendService.checkSysDictionary(EpsNetConfig.getHost(),
                            EpsNetConfig.PORT, version, 9000);
                    if (resp == null) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                    int curVersion = resp.getCurrentVersionCodeOfSysDictionary();
                    List<ProtoSysDictionary.Builder> pDicts = resp.getSysDictionaryBuilderList();
                    List<Dictionary> dicts = new ArrayList<Dictionary>();
                    for (ProtoSysDictionary.Builder item : pDicts) {
                        Dictionary dict = new Dictionary();
                        dict.setId(item.getIndexId());
                        dict.setName(item.getName());
                        dict.setSort_order(item.getSortOrder());
                        dict.setType(item.getDictionaryTypeId());
                        dicts.add(dict);
                    }
                    if (DictionaryDao.getInstance().insertAll(dicts)) {
                        SpUtils.put(SpUtils.KEYS_SYS.INT_DICTIONARY_VERSION, curVersion);
                        SpUtils.put(SpUtils.KEYS_SYS.LONG_LAST_CHECK_DICT_TIME, System.currentTimeMillis());
                    }
                    return true;
                } catch (Throwable e) {
                    e.printStackTrace();
                    String eStr = LogUtils.e("test", e);
                    LogUtils.saveLog("init", eStr);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    long duration = System.currentTimeMillis() - mStartTime;
                    if (duration < mPendingTime) {
                        HandlerUtils.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                checkLogin();
                            }
                        }, mPendingTime - duration);
                    } else {
                        checkLogin();
                    }
                } else {
                	  Toast.makeText(LoadingActivity.this, "数据初始化失败！",  Toast.LENGTH_SHORT).show();
                   // ToastUtils.showToast("数据初始化失败！");
                    checkLogin();
                }
            }
        };
        task.execute();
    }

    private void checkNewVersion(final Runnable runnable) {
        AsyncTask<Void, Void, AppNewVersionResp.Builder> task1 = new AsyncTask<Void, Void, AppNewVersionResp.Builder>() {

            @Override
            protected AppNewVersionResp.Builder doInBackground(Void... arg0) {
                int versionCode = 1;
                try {
                    versionCode = EpsApplication.getInstance().getPackageManager()
                            .getPackageInfo(EpsApplication.getInstance().getPackageName(), 0).versionCode;
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    AppNewVersionResp.Builder resp = NetServiceFactory.getInstance().checkNewVersion(
                            EpsNetConfig.getHost(), EpsNetConfig.PORT, versionCode,
                            Properties.APP_CLIENT_TYPE_ANDROID_PHONE, 9000);
                    return resp;
                } catch (Throwable e) {
                    e.printStackTrace();
                    ReleaseLog.log("checkNewVersion", e);
                    SpUtils.remove(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Builder result) {
                if (result == null || !result.getResult().equals(Constants.SUCC)) {
                    if (false) {
                        showYesNoDialog("提示", "网络连接失败，请检查网络后重试！", "退出", "重试", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_NEGATIVE) {
                                    finish();
                                    System.exit(0);
                                } else if (which == DialogInterface.BUTTON_POSITIVE) {
                                    checkNewVersion(runnable);
                                }
                            }
                        });
                    } else {
                        HandlerUtils.postDelayed(runnable, 100);
                    }
                    return;
                }
                boolean need = result.getIsNeedToUpdate();
                if (need) {
                    boolean mustUpdate = result.getIsMustUpdate();
                    final String url = result.getDownLoadUrl();
                    SpUtils.put(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION, true);
                    SpUtils.put(SpUtils.KEYS_SYS.BOOL_APP_IS_MUST_UPDATE, result.getIsMustUpdate());
                    SpUtils.put(SpUtils.KEYS_SYS.STRING_APP_NEW_VERSION_URL, url);
                    if (!TextUtils.isEmpty(url)) {
                        if (mustUpdate) {
                            showYesNoDialog("提示", "发现新版本，需要更新", "退出", "更新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                                        finish();
                                        System.exit(0);
                                    } else if (which == DialogInterface.BUTTON_POSITIVE) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        startActivity(intent);
                                        finish();
                                        System.exit(0);
                                    }
                                }
                            });
                        } else {
                            showYesNoDialog("提示", "发现新版本，建议更新", "取消", "更新", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                                        if (runnable != null) {
                                            HandlerUtils.postDelayed(runnable, 100);
                                        }
                                    } else if (which == DialogInterface.BUTTON_POSITIVE) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        startActivity(intent);
                                        finish();
                                        System.exit(0);
                                    }
                                }
                            });
                        }
                    }
                } else {
                    SpUtils.remove(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION);
                    SpUtils.remove(SpUtils.KEYS_SYS.BOOL_APP_IS_MUST_UPDATE);
                    SpUtils.remove(SpUtils.KEYS_SYS.STRING_APP_NEW_VERSION_URL);
                    if (runnable != null) {
                        HandlerUtils.postDelayed(runnable, 100);
                    }
                }

            }
        };
        task1.execute();
    }

    private void checkLogin() {
        boolean bLogined = SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_CURR_USER_LOGINED, false);
        final String phone = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PHONE, null);
        final String pwd = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null);
        final String ip = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_CONN_IP, null);
        final int port = SpUtils.getInt(SpUtils.KEYS_SYS.INT_CURR_CONN_PORT, 0);

        if (TextUtils.isEmpty(phone)) {
            checkNewVersion(getLoginRunnable(null));
        } else if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(ip) && port > 0 && bLogined) {
            NetServiceFactory.getInstance().setDstName(ip);
            NetServiceFactory.getInstance().setDstPort(port);
            // TODO 自动登录，直接进入主界面
            HandlerUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoadingActivity.this, UserLoginedInitActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 100);
        } else {
            // TODO 登录界面（显示已存在账号）
            checkNewVersion(getLoginRunnable(phone));
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        return null;
    }

    Runnable getLoginRunnable(final String phone) {
        return new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                if (!TextUtils.isEmpty(phone)) {
                    intent.putExtra(LoginActivity.EXTRA_PHONE, phone);
                }
                startActivity(intent);
                finish();
            }
        };
    }
    
 
}
