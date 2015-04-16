package com.epeisong.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

import com.epeisong.EpsApplication;

/**
 * Log工具类
 * 
 * @author poet
 * 
 */
public class LogUtils {
    public static interface LogListener {
        public static final int verbose = 0;
        public static final int debug = 1;
        public static final int info = 2;
        public static final int warn = 3;
        public static final int error = 4;

        void onLog(com.test.log.LogcatActivity.Log log);
    }

    public static void addLogListener(LogListener listener) {
        if (sLogListeners == null) {
            sLogListeners = new ArrayList<LogUtils.LogListener>();
        }
        sLogListeners.add(listener);
    }

    public static void d(Object obj, String msg) {
        d(obj.getClass().getSimpleName(), msg);
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
            notifyListener(LogListener.debug, tag, msg);
            saveLog(tag, msg);
        }
    }

    // private static List<String> sLogs = new ArrayList<String>();

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
            notifyListener(LogListener.error, tag, msg);
            saveLog(tag, msg);
        }
    }

    public static String e(String tag, Throwable ex) {
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
        String result = writer.toString();
        result = result.replaceAll("\n", "\r\n");
        e(tag, result);
        return result;
    }

    public static void et(String msg) {
        if (ERROR && TEMP) {
            e("log_temp", msg);
            saveLog("log_temp", msg);
        }
    }

    public static void noRepeat(String msg) {
        if (last == null || !last.equals(msg)) {
            last = msg;
            d("log_temp", msg);
        }
    }

    public static void removeLogListener(LogListener l) {
        if (l != null && sLogListeners != null) {
            sLogListeners.remove(l);
        }
    }

    public static void saveLog(String tag, String log) {
        if (!EpsApplication.DEBUGGING) {
            return;
        }
        // 写入文件
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String content = DateUtil.long2YMDHMSS(System.currentTimeMillis()) + lineSeparator + tag + lineSeparator
                    + log + lineSeparator + lineSeparator;

            writeLogToFile(content);
        }
    }

    public static void t(String msg) {
        if (TEMP) {
            d("log_temp", msg);
        }
    }

    private synchronized static void notifyListener(final int level, final String tag, final String log) {
        if (sLogListeners != null) {
            HandlerUtils.post(new Runnable() {

                @Override
                public void run() {
                    long time = System.currentTimeMillis();
                    for (LogListener l : sLogListeners) {
                        l.onLog(new com.test.log.LogcatActivity.Log(level, time, tag, log));
                    }
                }
            });

        }
    }

    private synchronized static void writeLogToFile(String content) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash_eps");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String fileName = DateUtil.long2YMD(System.currentTimeMillis()) + ".txt";

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

    private final static String lineSeparator = System.getProperty("line.separator");

    private static boolean TEMP = EpsApplication.DEBUGGING;

    private static boolean DEBUG = EpsApplication.DEBUGGING;

    private static boolean ERROR = EpsApplication.DEBUGGING;

    public static String last;

    private static List<LogListener> sLogListeners;
}
