package com.epeisong.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.epeisong.service.CoreService;
import com.epeisong.service.notify.NotifyService;
import com.epeisong.utils.SpUtils;

/**
 * 开启启动广播监听
 * @author poet
 *
 */
public class SysReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_CURR_USER_LOGINED, false)) {
                CoreService.startService();
                NotifyService.startService();
            }
        }
    }
}
