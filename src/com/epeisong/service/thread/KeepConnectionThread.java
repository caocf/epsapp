/**
 * 
 * 
 */
package com.epeisong.service.thread;

import android.os.SystemClock;

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.net.NetService;
import com.epeisong.logistics.proto.Eps.UserLoginResp.Builder;
import com.epeisong.ui.activity.temp.LoginActivity;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * @author cngaohk
 * @since Sep 26, 2014
 */
public class KeepConnectionThread extends Thread {

    private String host;
    private int port;
    private NetService frontendService;
    private static String sMobile;
    private static String sPassword;
    private boolean stop = false;

    private long lastUnloginTime;

    private final int TRYLOGINMAX = 10;
    private int tryLoginCount = 0;

    private String threadName;

    public KeepConnectionThread(String host, int port, NetService frontendService, String mobile, String password) {
        this.host = host;
        this.port = port;
        this.frontendService = frontendService;
        sMobile = mobile;
        sPassword = password;
        lastUnloginTime = SpUtils.getLong(SpUtils.KEYS_SYS.LONG_LAST_UNLOGIN, 0);
    }

    public static void changePwd(String pwd) {
        sPassword = pwd;
    }

    public static void changePhone(String phone) {
        sMobile = phone;
    }

    private boolean checkTimeCanGoOn() {
        if (lastUnloginTime == 0) {
            SpUtils.put(SpUtils.KEYS_SYS.LONG_LAST_UNLOGIN, System.currentTimeMillis());
            return true;
        } else {
            return System.currentTimeMillis() - lastUnloginTime < 1000 * 60 * 60 * 24;
        }
    }

    private void loginSuccess() {
        SpUtils.remove(SpUtils.KEYS_SYS.LONG_LAST_UNLOGIN);
        lastUnloginTime = 0;
        tryLoginCount = 0;
    }

    @Override
    public void run() {
        threadName = getName();
        stop = false;

        while (!stop) {
            try {
                if (tryLoginCount >= TRYLOGINMAX) {
                    Thread.sleep(55 * 1000);
                } else {
                    Thread.sleep(2 * 1000);
                }

                if (!frontendService.isAvailable()) {
                    if (!SystemUtils.isNetworkAvaliable()) {
                        LogUtils.e("KeepConn -- " + threadName, "network Unavailable,sleep 10 second");
                        Thread.sleep(2 * 1000);
                        if (checkTimeCanGoOn()) {
                            continue;
                        } else {
                            EpsApplication.exit(MainActivity.sMainActivity, LoginActivity.class);
                            break;
                        }
                    }
                    Thread.sleep(200); // 防止收到被T下的瞬间去重连
                    if (stop) {
                        break;
                    }

                    LogUtils.e("KeepConn -- " + threadName,
                            "login.start:" + DateUtil.long2YMDHMSS(System.currentTimeMillis()));
                    // Builder resp = frontendService.login(host, port,
                    // Properties.APP_CLIENT_BIG_TYPE_PHONE, sMobile,
                    // sPassword, 9000, LoginUtils.getLoginParams());
                    Builder resp = frontendService.login(0);
                    LogUtils.e("KeepConn -- " + threadName,
                            "login.end:" + DateUtil.long2YMDHMSS(System.currentTimeMillis()));

                    if (resp != null && Constants.SUCC.equals(resp.getResult())) {
                        if (resp.getIsNeedToCreateLogistic()) {
                            Thread.sleep(100);
                            frontendService.disconnect();
                            LogUtils.d("KeepConn -- " + threadName, "重连成功，返回需要创建角色，断开连接");
                            ToastUtils.showToastInThread("请重新登录");
                            Thread.sleep(1000);
                         } else {
                            loginSuccess();
                        }
                    } else {
                        if (checkTimeCanGoOn() && resp != null) {
                            int status = resp.getLoginStatus();// .getLonginStatus();
                            switch (status) {
                            case -2: // 密码错误
                            case -3: // 账号被锁定
                            case -5: // 你的账号在其他客户端登录
                                EpsApplication.exit(MainActivity.sMainActivity, LoginActivity.class, resp.getDesc());
                                break;
                            }
                        } else {
                            EpsApplication.exit(MainActivity.sMainActivity, LoginActivity.class, "长时间未登录，请重新登录");
                            break;
                        }
                    }
                    LogUtils.e("KeepConn -- " + threadName, "login.result:" + resp.getResult());
                }
            } catch (Exception e) {
                LogUtils.e("KeepConn -- " + threadName, "login.exception:" + e.toString());
                e.printStackTrace();
                if (e instanceof InterruptedException) {
                    break;
                }
                SystemClock.sleep(5 * 1000);
                if (tryLoginCount < TRYLOGINMAX) {
                    tryLoginCount++;
                }
                LogUtils.e("KeepConn -- " + threadName, "login.tryCount-" + tryLoginCount);
            }
        }
    }

    public void onNetworkAvailable() {
        tryLoginCount = 0;
    }

    public void shutdown() {
        LogUtils.saveLog("ConnectionMonitorThrad.shutdown", "entry");

        if (!stop) {
            stop = true;
            interrupt();
        }

        LogUtils.saveLog("ConnectionMonitorThrad.shutdown", "exit");
    }
}
