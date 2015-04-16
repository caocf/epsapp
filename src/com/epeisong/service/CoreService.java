package com.epeisong.service;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.net.Handler;
import com.epeisong.logistics.net.NetService;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.model.User;
import com.epeisong.service.notify.NotifyService;
import com.epeisong.service.thread.BackgroundHandler;
import com.epeisong.service.thread.BuilderPackage;
import com.epeisong.service.thread.HandlerProcessThread;
import com.epeisong.service.thread.RefreshEpsLocationThread;
import com.epeisong.speech.tts.TTSServiceFactory;
import com.epeisong.ui.activity.temp.LoginActivity;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

public class CoreService extends Service {
    public static void startService() {
        sDoStop = false;
        Context context = EpsApplication.getInstance();
        Intent intent = new Intent(context, CoreService.class);
        context.startService(intent);
    }

    public static void stopService() {
        sDoStop = true;
        Context context = EpsApplication.getInstance();
        Intent intent = new Intent(context, CoreService.class);
        context.stopService(intent);
    }

    private static boolean sDoStop;

    private HandlerProcessThread handlerProcessThread = null;
    private NetService netService = NetServiceFactory.getInstance();

    private RefreshEpsLocationThread refreshEpsLocationThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.et("CoreService onCreate!!!");
        LogUtils.saveLog("CoreService.onCreate", "entry");

        LinkedBlockingQueue<BuilderPackage> msgQueue = new LinkedBlockingQueue<BuilderPackage>();
        handlerProcessThread = new HandlerProcessThread(msgQueue);
        handlerProcessThread.start();

        refreshEpsLocationThread = new RefreshEpsLocationThread();
        refreshEpsLocationThread.start();

        Handler handler = new BackgroundHandler(msgQueue);
        String ip = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_CONN_IP, null);
        int port = SpUtils.getInt(SpUtils.KEYS_SYS.INT_CURR_CONN_PORT, 0);
        if (ip == null || port == 0) {
            EpsApplication.exit(MainActivity.sMainActivity, LoginActivity.class);
            return;
        }
        String phone = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PHONE, null);
        String pwd = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null);
        netService.setDstName(ip);
        netService.setDstPort(port);
        netService.setHandler(handler);
        netService.setContext(this.getApplicationContext());
        netService.setMobile(phone);
        netService.setPassword(pwd);
        netService.startService();

        User user = UserDao.getInstance().getUser();
        if (user == null) {
            EpsApplication.exit(null, LoginActivity.class, (Serializable) null);
            return;
        }

        try {
            TTSServiceFactory.getInstance().init(this);
        } catch (Exception e) {
            ToastUtils.showToast("TTS服务初始化失败");
            LogUtils.e("TTSService.init", e);
        }
        
        if (!SystemUtils.isServiceRunning(NotifyService.class.getName())) {
            NotifyService.startService();
        }

        LogUtils.saveLog("CoreService.onCreate", "exit");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LogUtils.saveLog("CoreService.onDestroy", "entry");
        LogUtils.et("CoreService onDestory!!!");
        if (handlerProcessThread != null) {
            handlerProcessThread.shutdown();
            handlerProcessThread = null;
        }
        if (refreshEpsLocationThread != null) {
            refreshEpsLocationThread.shutdown();
            refreshEpsLocationThread = null;
        }
        if (netService != null) {
            netService.stopService();
        }

        if (!sDoStop) {
            startService();
        }

        LogUtils.saveLog("CoreService.onDestroy", "exit");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
}
