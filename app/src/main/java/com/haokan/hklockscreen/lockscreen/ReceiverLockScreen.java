package com.haokan.hklockscreen.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.Values;

/**
 * Created by wangzixu on 2017/10/28.
 */
public class ReceiverLockScreen extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean aBoolean = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Values.PreferenceKey.KEY_SP_OPENLOCKSCREEN, true);
        if (!aBoolean) {
            return;
        }
        String action = intent.getAction();
        LogHelper.d("wangzixu", "ServiceLockScreen action----" + action);
        switch (action) {
            case Intent.ACTION_SCREEN_OFF:


                Intent intent1 = new Intent(context, ActivityLockScreen.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                break;
            case Intent.ACTION_SCREEN_ON: {
                break;
            }
        }
    }
}
