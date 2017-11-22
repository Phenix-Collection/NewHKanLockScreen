package com.haokan.hklockscreen.lockscreen.activityforlockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.haokan.hklockscreen.setting.ActivityLockSetting;

/**
 * Created by wangzixu on 2017/11/11.
 * 锁屏界面跳转的设置页面, 和应用内跳转的是一个页面, 但是有一些特殊的操作, 所以独立出来一个
 */
public class ActivitySettingForLockPage extends ActivityLockSetting {

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
                overridePendingTransition(0,0);
            }
        };
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }
}
