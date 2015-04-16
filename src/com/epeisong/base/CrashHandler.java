package com.epeisong.base;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;

import com.epeisong.EpsApplication;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.ReleaseLog;
import com.epeisong.utils.ToastUtils;
import com.test.log.CrashLogDao;
import com.test.log.CrashLogListActivity.CrashLog;

/**
 * 未处理异常处理器
 * 
 * @author poet
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

    private static CrashHandler instance;

    private Context mContext;

    private CrashHandler(Context context) {
        mContext = context;
    }

    public static synchronized CrashHandler getInstance(Context context) {
        if (instance == null) {
            instance = new CrashHandler(context);
        }
        return instance;
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(instance);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();// 记得关闭
        String time = DateUtil.long2YMDHM(System.currentTimeMillis());
        String result = time + "\n" + writer.toString();
        result = result.replaceAll("\n", "\r\n");

        if (EpsApplication.DEBUGGING) {
            CrashLog log = new CrashLog();
            log.setTime(System.currentTimeMillis());
            log.setContent(result);
            CrashLogDao.getInstance().insert(log);
            LogUtils.e("CrashHandler", result);
        } else {
            ReleaseLog.log("CrashHandler", result);
        }
        ToastUtils.showToast("异常退出");
        System.exit(0);
    }
}
