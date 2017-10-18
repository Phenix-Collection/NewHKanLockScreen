package com.haokan.hklockscreen.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.haokan.pubic.util.LogHelper;


/**
 * 用来启动锁屏服务的广播, 如果开机启动
 */
public class ReceiverLockScreenBoot extends BroadcastReceiver {
    /**
     * 网络状态改变
     * 网络链接改变的广播，用来在开机的时候调起服务，大部分手机开机都会发这个广播，并且不用开机启动的权限
     */
    public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogHelper.d("wangzixu","ReceiverLockScreenBoot action----" + action);
        if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(ACTION_CONNECTIVITY_CHANGE)) {
            Intent i = new Intent();
            i.setClass(context, ServiceLockScreen.class);
            context.startService(i);
        }
    }
}
