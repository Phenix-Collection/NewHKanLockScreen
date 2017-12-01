package com.haokan.hklockscreen.lockscreenautoupdateimage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.haokan.pubic.App;
import com.haokan.pubic.logsys.LogHelper;

/**
 * Created by wangzixu on 2017/12/1.
 */
public class WiFiChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        LogHelper.d("wangzixu", "WiFiChangeReceiver autoupdate onReceive action = " + action);
        if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){//wifi连接上与否
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.State state = info.getState();
            LogHelper.d("wangzixu", "WiFiChangeReceiver autoupdate onReceive state = " + state.toString());
            if(NetworkInfo.State.CONNECTED.equals(state)){
                App.startAutoUpdate();
            } else if(NetworkInfo.State.DISCONNECTED.equals(state)){
            }
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) { //初次安装时, 会直接发送这个广播
            //很多手机在息屏一段时间后, 会自动切断网络, 亮屏时再重新连接, 都会发出这个广播
            App.startAutoUpdate();
        }
    }
}
