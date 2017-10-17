package com.haokan.pubic.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpStatusManager {
    /**检查网络连接状态
     * */
    public static boolean checkNetWorkConnect(Context context) {
        boolean result;
        if (context!=null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netinfo = cm.getActiveNetworkInfo();
            result = netinfo != null && netinfo.isConnected();
            return result;
        }else{
            return false;
        }
    }

    /**
     * 当前是不是wifi链接
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getState() == NetworkInfo.State.CONNECTED && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 获取网络类型
     *
     * @param mContext
     * @return 0没网 1Wi-Fi 2mobile
     */
    public static int getNetworkType(Context mContext) {
        int type = 0;
        try {
            ConnectivityManager connectMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
                type = 1;
            } else if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
                type = 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }
}
