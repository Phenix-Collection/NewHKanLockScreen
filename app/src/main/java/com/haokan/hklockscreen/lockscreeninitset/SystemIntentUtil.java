package com.haokan.hklockscreen.lockscreeninitset;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

/**
 * Created by wangzixu on 2017/10/11.
 */
public class SystemIntentUtil {
    public static Intent getAutoStartIntent() {
        Intent intent = new Intent();
        String pkg = "com.miui.securitycenter";
        String cls = "com.miui.permcenter.autostart.AutoStartManagementActivity";

        String manufacturer = Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase("xiaomi")) {
            pkg = "com.miui.securitycenter";
            cls = "com.miui.permcenter.autostart.AutoStartManagementActivity";
//            cls = "com.miui.permcenter.autostart.AutoStartDetailManagementActivity";

        } else if (android.os.Build.MANUFACTURER.equals("huawei")) {
            pkg = "com.huawei.systemmanager";
            cls = "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity";

        } else if (android.os.Build.MANUFACTURER.equals("oppo")) {
            pkg = "com.coloros.oppoguardelf";
            cls = "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity";

        } else if (android.os.Build.MANUFACTURER.equals("vivo")) {
            pkg = "com.iqoo.secure";
            cls = ".ui.phoneoptimize.AddWhiteListActivity";
        }

        ComponentName componentName = new ComponentName(pkg, cls);
        intent.setComponent(componentName);
        return intent;
    }
}
