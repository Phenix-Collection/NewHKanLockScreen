package com.haokan.hklockscreen.lockscreeninitset;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

import com.haokan.pubic.logsys.LogHelper;

/**
 * Created by wangzixu on 2017/10/11.
 */
public class SystemIntentUtil {
    public static boolean isOppo() {
        String manufacturer = Build.MANUFACTURER;
        return manufacturer.equalsIgnoreCase("oppo");
    }

    public static boolean isXiaomi() {
        String manufacturer = Build.MANUFACTURER;
        return manufacturer.equalsIgnoreCase("xiaomi");
    }


    public static Intent getAutoStartIntent() {
        Intent intent = new Intent();
        String pkg = "com.miui.securitycenter";
        String cls = "com.miui.permcenter.autostart.AutoStartManagementActivity";

        String manufacturer = Build.MANUFACTURER;
        LogHelper.d("wangzixu", "SystemIntentUtil manufacturer = " + manufacturer);

        if (manufacturer.equalsIgnoreCase("xiaomi")) {
            pkg = "com.miui.securitycenter";
            cls = "com.miui.permcenter.autostart.AutoStartManagementActivity";
        } else if (manufacturer.equalsIgnoreCase("huawei")) {
            pkg = "com.huawei.systemmanager";
            cls = "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity";

        } else if (manufacturer.equalsIgnoreCase("oppo")) {
            pkg = "com.coloros.safecenter";
            cls = "com.coloros.safecenter.startupapp.StartupAppListActivity";

        } else if (manufacturer.equalsIgnoreCase("vivo")) {
            pkg = "com.iqoo.secure";
            cls = ".ui.phoneoptimize.AddWhiteListActivity";
        }

        ComponentName componentName = new ComponentName(pkg, cls);
        intent.setComponent(componentName);
        return intent;
    }

    public static Intent getRemoveSysPswdIntent() {
        Intent intent = new Intent();
        String pkg = "com.coloros.fingerprint";
        String cls = "com.coloros.fingerprint.FingerLockActivity";

        String manufacturer = Build.MANUFACTURER;
        LogHelper.d("wangzixu", "SystemIntentUtil manufacturer = " + manufacturer);

        if (manufacturer.equalsIgnoreCase("xiaomi")) {
            pkg = "com.miui.securitycenter";
            cls = "com.miui.permcenter.autostart.AutoStartManagementActivity";
        } else if (manufacturer.equalsIgnoreCase("huawei")) {
            pkg = "com.huawei.systemmanager";
            cls = "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity";

        } else if (manufacturer.equalsIgnoreCase("oppo")) {
            pkg = "com.coloros.fingerprint";
            cls = "com.coloros.fingerprint.FingerLockActivity";
        } else if (manufacturer.equalsIgnoreCase("vivo")) {
            pkg = "com.iqoo.secure";
            cls = ".ui.phoneoptimize.AddWhiteListActivity";
        }

        ComponentName componentName = new ComponentName(pkg, cls);
        intent.setComponent(componentName);
        return intent;
    }

    public static Intent getRemoveSysMagazineIntent() {
        Intent intent = new Intent();
        String pkg = "com.oppo.screenlock.pictorial";
        String cls = "com.oppo.screenlock.pictorial.MainActivity";

        String manufacturer = Build.MANUFACTURER;
        LogHelper.d("wangzixu", "SystemIntentUtil manufacturer = " + manufacturer);

        if (manufacturer.equalsIgnoreCase("xiaomi")) {
            pkg = "com.miui.securitycenter";
            cls = "com.miui.permcenter.autostart.AutoStartManagementActivity";
        } else if (manufacturer.equalsIgnoreCase("huawei")) {
            pkg = "com.huawei.systemmanager";
            cls = "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity";

        } else if (manufacturer.equalsIgnoreCase("oppo")) {
            pkg = "com.oppo.screenlock.pictorial";
            cls = "com.oppo.screenlock.pictorial.MainActivity";
        } else if (manufacturer.equalsIgnoreCase("vivo")) {
            pkg = "com.iqoo.secure";
            cls = ".ui.phoneoptimize.AddWhiteListActivity";
        }

        ComponentName componentName = new ComponentName(pkg, cls);
        intent.setComponent(componentName);
        return intent;
    }
}
