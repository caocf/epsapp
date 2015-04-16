package com.epeisong;

import java.io.Serializable;

import lib.universal_image_loader.CustomImageDownloader;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.epeisong.base.CrashHandler;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.service.CoreService;
import com.epeisong.service.notify.AppManager;
import com.epeisong.service.notify.NotifyService;
import com.epeisong.service.receiver.AlarmBroadcastReceiver;
import com.epeisong.ui.activity.temp.LoginActivity;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SysUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * 自定义Application
 * 
 * @author poet
 * 
 */
public class EpsApplication extends Application {

    public static String getDbName(String sub) {
        String userIdMd5 = EpsApplication.getUserIdMd5();
        if (userIdMd5 == null) {
            // TODO 退出应用，重新登录
            exit();
            throw new RuntimeException("当前没有用户登录，确保登录后保存登录状态");
        }
        return userIdMd5 + "_" + sub + ".db";
    }

    public static EpsApplication getInstance() {
        return singleton;
    }

    public static int getScreenHeight() {
        return sScreenHeight;
    }

    public static int getScreenWidth() {
        return sScreenWidth;
    }

    public static String getUserIdMd5() {
        if (TextUtils.isEmpty(sUserIdMd5)) {
            sUserIdMd5 = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_ID_MD5, null);
        }
        return sUserIdMd5;
    }

    public static String getVersionName() {
        return versionName;
    }

    public static void setUserIdMd5(String md5) {
        sUserIdMd5 = md5;
    }

    private static EpsApplication singleton = null;

    public static boolean DEBUGGING = ReleaseConfig.DEBUGGING;

    private static int sScreenWidth;

    private static int sScreenHeight;

    private static String versionName;

    private static String sUserIdMd5;

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;

        String processName = SysUtils.getProcessName();
        if (processName != null && processName.endsWith("remote")) {
            return;
        }

        super.onCreate();
        LogUtils.et("application onCreate!!!");

        CrashHandler.getInstance(this).init();

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        sScreenWidth = dm.widthPixels;
        sScreenHeight = dm.heightPixels;

        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        SDKInitializer.initialize(this);

        initImageloader();
    }

    public static void registAlarmTask() {
        Intent intent = new Intent(getInstance(), AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getInstance(), 0, intent,
                Intent.FLAG_ACTIVITY_NEW_TASK);
        AlarmManager mgr = (AlarmManager) getInstance().getSystemService(Context.ALARM_SERVICE);
        mgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 1000 * 2, 1000 * 60 * 16, pendingIntent);
    }

    private static void unRegistAlarmTask() {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getInstance(), 0, new Intent(getInstance(),
                AlarmBroadcastReceiver.class), Intent.FLAG_ACTIVITY_NEW_TASK);
        AlarmManager mgr = (AlarmManager) getInstance().getSystemService(Context.ALARM_SERVICE);
        mgr.cancel(pendingIntent);
    }

    private void initImageloader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache()).diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs()
                .imageDownloader(new CustomImageDownloader(this)).build();
        ImageLoader.getInstance().init(config);
    }

    public static void exit() {
        exit(null);
    }

    public static void exit(Activity a) {
        exit(a, null);
    }

    public static void exit(final Activity from, final Class<? extends Activity> targetClass,
            final Serializable... params) {
        if (Thread.currentThread() != getInstance().getMainLooper().getThread()) {
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    exit(from, targetClass, params);
                }
            });
        }
        LogUtils.e("EpsApplication", "EpsApplication.exit");
        try {
            LocationClient c = new LocationClient(getInstance());
            c.stop();
            CoreService.stopService();
            NotifyService.stopService();
            unRegistAlarmTask();
            BaseActivity.clearActivities();
            AppManager.getAppManager().finishAllActivity();
        } catch (Exception e) {
            LogUtils.e("EpsApplicaton", e);
        } finally {
            SpUtils.remove(SpUtils.KEYS_SYS.BOOL_CURR_USER_LOGINED);
            SpUtils.remove(SpUtils.KEYS_SYS.STRING_CURR_USER_ID_MD5);
            SpUtils.remove(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED);
            SpUtils.remove(SpUtils.KEYS_SYS.STRING_CURR_CONN_IP);
            SpUtils.remove(SpUtils.KEYS_SYS.INT_CURR_CONN_PORT);
            if (targetClass != null) {
                String extra = "";
                if (params != null && params.length > 0) {
                    extra = (String) params[0];
                }
                Intent intent = new Intent(getInstance(), targetClass);
                intent.putExtra("0", extra);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getInstance().startActivity(intent);
            } else {
                System.exit(0);
            }
        }
    }

    private static void detailStart(String extra) {
        Intent intent = new Intent(getInstance(), LoginActivity.class);
        intent.putExtra("0", extra);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(getInstance(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        AlarmManager mgr = (AlarmManager) getInstance().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent);
        System.exit(0);
    }
}
