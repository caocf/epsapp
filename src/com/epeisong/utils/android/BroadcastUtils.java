package com.epeisong.utils.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.epeisong.EpsApplication;

/**
 * 广播工具类
 * @author poet
 *
 */
public class BroadcastUtils {

    public static final String ACTION_FIND_NEW_VERSION = "com.epeisong.action_find_new_version";

    private static Context sContext = EpsApplication.getInstance();

    public static void send(String action, Bundle bundle) {
        Intent intent = new Intent(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        sContext.sendBroadcast(intent);
    }

    public static void register(String action, BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter(action);
        sContext.registerReceiver(receiver, filter);
    }

    public static void unRegister(BroadcastReceiver receiver) {
        sContext.unregisterReceiver(receiver);
    }
}
