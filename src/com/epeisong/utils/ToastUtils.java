package com.epeisong.utils;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.epeisong.EpsApplication;

/**
 * Toast工具类
 * 
 * @author poet
 * 
 */
public class ToastUtils {

    private static Toast mToast;

    public static void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(EpsApplication.getInstance(), msg, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    public static void showToastInThread(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                showToast(msg);
            }
        });
    }
}
