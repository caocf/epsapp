package com.epeisong.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.epeisong.EpsApplication;
import com.epeisong.model.Freight;

/**
 * 推送消息收到Freight变化
 * @author poet
 *
 */
public abstract class FreightReceiver extends BroadcastReceiver {

    public static final String EXTRA_FREIGHT = "freight";

    private static final String ACTION = "com.epeisong.receiver.freight";

    public static void register(Context context, FreightReceiver receiver) {
        IntentFilter filter = new IntentFilter(ACTION);
        context.registerReceiver(receiver, filter);
    }

    public static void unRegister(Context context, FreightReceiver receiver) {
        context.unregisterReceiver(receiver);
    }

    public static void send(Freight f) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_FREIGHT, f);
        EpsApplication.getInstance().sendBroadcast(intent);
    }

}
