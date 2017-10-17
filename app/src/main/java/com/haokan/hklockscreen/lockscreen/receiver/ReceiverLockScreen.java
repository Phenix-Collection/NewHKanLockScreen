package com.haokan.hklockscreen.lockscreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.haokan.hklockscreen.lockscreen.activity.ActivityLockScreen;
import com.haokan.hklockscreen.lockscreen.service.ServiceLockScreen;
import com.haokan.pubic.util.LogHelper;


/**
 * 锁屏的广播, 会接受一些系统的广播, 处理一些锁屏的应有的逻辑, 如亮屏, 灭屏
 */
public class ReceiverLockScreen extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogHelper.d("wangzixu", "ReceiverLockScreen action----" + action);
        switch (action) {
            case Intent.ACTION_SCREEN_OFF:
                if (ServiceLockScreen.sLockEnable) {
                    Intent intent1 = new Intent(context, ActivityLockScreen.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                }
                break;
            case Intent.ACTION_SCREEN_ON: { //亮屏，初始化一些锁屏上的东西
                if (ServiceLockScreen.sHaokanLockView != null) {
//                    ServiceLockScreen.sHaokanLockView.onScreenOn();
                }
                break;
            }
        }
     }
}
