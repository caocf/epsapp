package com.epeisong.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * 消息处理工具类
 * 
 * @author poet
 * 
 */
public class HandlerUtils {
    private static Handler sMainHandler = new Handler(Looper.getMainLooper());

    public static void post(Runnable runnable) {
        sMainHandler.post(runnable);
    }

    public static void postDelayed(Runnable r, long delayMillis) {
        sMainHandler.postDelayed(r, delayMillis);
    }

    public static void remove(Runnable r) {
        sMainHandler.removeCallbacks(r);
    }
}
