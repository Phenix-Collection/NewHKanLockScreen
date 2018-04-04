package com.haokan.pubic.util;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.haokan.pubic.logsys.LogHelper;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommonUtil {
    private static final String TAG = "CommonUtil";
    private static long sLastClickTime;

    /**
     * 方法名称:transMapToString
     * 传入参数:map
     * 返回值:String 形如 username'chenziwen^password'1234
     */
    public static String transMapToString(Map map) {
        Map.Entry entry;
        StringBuffer sb = new StringBuffer("{");
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            entry = (Map.Entry) iterator.next();
            sb.append(entry.getKey().toString()).append(":")
                    .append(null == entry.getValue() ? "null" : entry.getValue().toString())
                    .append(iterator.hasNext() ? " , " : "");
        }
        sb.append("}");
        return sb.toString();
    }

    public static boolean isPkgInstalled(Context context, String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (Exception e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * 测量view的尺寸，实际上view的最终尺寸会由于父布局传递来的MeasureSpec和view本身的LayoutParams共同决定
     * 这里预先测量，由自己给出的MeasureSpec计算尺寸
     * @param view
     */
    public static void haokanMeasure(View view) {
        int sizeWidth, sizeHeight, modeWidth, modeHeight;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            sizeWidth = 0;
            modeWidth = View.MeasureSpec.UNSPECIFIED;
        } else {
            sizeWidth = layoutParams.width;
            modeWidth = View.MeasureSpec.EXACTLY;
        }
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            sizeHeight = 0;
            modeHeight = View.MeasureSpec.UNSPECIFIED;
        } else {
            sizeHeight = layoutParams.height;
            modeHeight = View.MeasureSpec.EXACTLY;
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(sizeWidth, modeWidth),
                View.MeasureSpec.makeMeasureSpec(sizeHeight, modeHeight)
        );
    }

    /**
     * 获取did
     * v3.2之前采用IMEI或MAC或AndroidID的方式进行MD5
     * v3.3之后采用IMEI+AndroidID+MAC的方式进行MD5
     *
     * @param context
     * @return
     */
    public static String getDid(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String did = sharedPreferences.getString(Values.PreferenceKey.KEY_SP_DID, "");
        if (TextUtils.isEmpty(did)) {
            try {
                did = getIMEI(context) + getAndroid_ID(context) + getMAC(context);
                did = did.replace("null", "");
                did = Md5Util.md5(did);

                if (!TextUtils.isEmpty(did)) {
                    sharedPreferences.edit().putString(Values.PreferenceKey.KEY_SP_DID, did).apply();
                }
            } catch (Exception e) {
                LogHelper.e("CommonUtil", "getDid exception = " + e.getMessage());
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(did)) {
            did = "unknow" + System.currentTimeMillis();
        }
        return did;
    }

    /**
     * 是否快速点击
     * @return
     */
    public static synchronized boolean isQuickClick() {
        long time = SystemClock.uptimeMillis();
        if (time - sLastClickTime < 300) {
            return true;
        }
        sLastClickTime = time;
        return false;
    }

    /**
     * 获取应用程序版本的名称，清单文件中的versionName属性
     */
    public static String getLocalVersionName(Context c) {
        try {
            PackageManager manager = c.getPackageManager();
            PackageInfo info = manager.getPackageInfo(c.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    /**
     * 获取应用程序版本的名称，清单文件中的versionCode属性
     */
    public static int getLocalVersionCode(Context c) {
        try {
            PackageManager manager = c.getPackageManager();
            PackageInfo info = manager.getPackageInfo(c.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 获取手机型号
     * @return
     */
    public static String getPhoneModel(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String phone = sharedPreferences.getString(Values.PreferenceKey.KEY_SP_PHONE_MODEL, "");
        if (TextUtils.isEmpty(phone)) {
            try {
                phone = Build.MODEL;
                if (!TextUtils.isEmpty(phone)) {
                    sharedPreferences.edit().putString(Values.PreferenceKey.KEY_SP_PHONE_MODEL, phone).apply();
                }
            } catch (Exception e) {
                LogHelper.e("CommonUtil", "getPhoneModel exception = " + e.getMessage());
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(phone)) {
            phone = "defaultPhone";
        }
        return phone;
    }

    /**
     * 获取渠道id
     */
    public static String getPid(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        String pid = sharedPreferences.getString(Values.PreferenceKey.KEY_SP_PID, "");
        if (TextUtils.isEmpty(pid)) {
            ApplicationInfo appInfo = null;
            try {
                appInfo = c.getPackageManager().getApplicationInfo(c.getPackageName(), PackageManager.GET_META_DATA);
                pid = String.valueOf(appInfo.metaData.getInt("UMENG_CHANNEL"));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(pid)) {
                pid = "239";
            } else {
                sharedPreferences.edit().putString(Values.PreferenceKey.KEY_SP_PID, pid).commit();
            }
        }
        return pid;
    }

    /**
     * 获取MAC地址android.os.Build.VERSION.SDK_INT
     * 需权限android.Manifest.permission.ACCESS_WIFI_STATE
     * @return
     */
    public static String getMAC(Context context) {
//        if (checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
//            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            String macAddress = info.getMacAddress();
//            if (macAddress == null) {
//                return "";
//            } else {
//                return macAddress;
//            }
//        } else {
//            return "";
//        }
        //以上方法在6.0以上不能用了
        String macString = "02:00:00:00:00:xx";
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    continue;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b)); //转换成16进制, 宽度为2位, 不够的补零
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return macString;
    }

    /**
     * 获取Android_Id
     *
     * @param context
     * @return
     */
    public static String getAndroid_ID(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (android_id == null) {
            android_id = "";
        }
        return android_id;
    }

    /**
     * 获取IMEI号
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (imei != null) {
                return imei;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static boolean checkPermission(Context context, String permissionStr) {
        PackageManager pm = context.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permissionStr, context.getPackageName()));
        return permission;
    }

    public static String getDevice() {
        HashMap map = new HashMap();

        map.put("Build.MANUFACTURER", Build.MANUFACTURER);
        map.put("Build.MODEL", Build.MODEL);
        map.put("Build.VERSION.SDK_INT", Build.VERSION.SDK_INT);
        map.put("Build.BOARD", Build.BOARD);
        map.put("Build.BOOTLOADER", Build.BOOTLOADER);
        map.put("Build.PRODUCT", Build.PRODUCT);
        map.put("Build.DISPLAY", Build.DISPLAY);
        map.put("Build.FINGERPRINT", Build.FINGERPRINT);
        map.put("Build.getRadioVersion", Build.getRadioVersion());
        map.put("Build.SERIAL", Build.SERIAL);
        map.put("Build.ID", Build.ID);
        map.put("Build.VERSION.INCREMENTAL", Build.VERSION.INCREMENTAL);
        map.put("Build..VERSION.BASE_OS", Build.VERSION.BASE_OS);
        map.put("Build..VERSION.CODENAME", Build.VERSION.CODENAME);
        map.put("Build..VERSION.RELEASE", Build.VERSION.RELEASE);
        map.put("Build..VERSION.SECURITY_PATCH", Build.VERSION.SECURITY_PATCH);
        return transMapToString(map);
    }

    /**
     * 返回手机运营商名称，在调用支付前调用作判断
     * @return 0, 未授权或者没有 1 移动 2 联通 3 电信
     */
    public static int getProvidersName(Context context) {
        int ProvidersName = 0;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }
        String IMSI = telephonyManager.getSubscriberId();
        if( IMSI == null){
            return 0;
        }

        if(IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")){
            ProvidersName = 1;
        }else if(IMSI.startsWith("46001")){
            ProvidersName = 2;
        }else if (IMSI.startsWith("46003")) {
            ProvidersName = 3;
        }
        return ProvidersName;
    }
}
