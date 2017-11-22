package com.haokan.hklockscreen.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.Values;

/**
 * Created by wangzixu on 2017/10/28.
 */
public class ReceiverLockScreen extends BroadcastReceiver {
    /**
     * 是否正在打电话状态
     */
    public static boolean sIsCallIng = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean aBoolean = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Values.PreferenceKey.KEY_SP_OPENLOCKSCREEN, true);
        if (!aBoolean) {
            return;
        }
        String action = intent.getAction();
        LogHelper.d("wangzixu", "ActivityLockScreen onReceive action----" + action);
        switch (action) {
            case Intent.ACTION_SCREEN_OFF:
                if (sIsCallIng) {
                    return;
                }
                try {
//                    Intent intent1 = new Intent(context, ActivityLockScreen.class);
//                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent1);
//                    LogHelper.d("wangzixu", "ActivityLockScreen onReceive ServiceLockScreen startActivity");

                    Intent intent1 = new Intent(context, ServiceLockScreen.class);
                    intent1.putExtra("type", 1);
                    context.startService(intent1);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogHelper.d("wangzixu", "ActivityLockScreen onReceive Exception");
                }
                break;
            case Intent.ACTION_SCREEN_ON:
                break;
            case "android.intent.action.PHONE_STATE":
                String extra = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                LogHelper.d("wangzixu", "phonestate onReceive PHONE_STATE----" + extra);
                if (TelephonyManager.EXTRA_STATE_IDLE.equals(extra)) {
                    sIsCallIng = false;
                } else {
                    sIsCallIng = true;
                }
                break;
        }
    }
}
