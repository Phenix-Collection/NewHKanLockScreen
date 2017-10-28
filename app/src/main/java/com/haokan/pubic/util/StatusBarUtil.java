package com.haokan.pubic.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.haokan.hklockscreen.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by xiefeng on 16/7/29.
 */
public class StatusBarUtil {

    /**
     * 修改状态栏为全透明
     * @param activity
     */
    public static void transparencyBar(Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window =activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏透明
     */
    public static void setStatusBarTransparnet(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE //让应用的主体内容占用系统状态栏和导航栏的空间
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); //内容填充进statusbar下面（android:fitsSystemWindows="false"）
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window =activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏，白底黑字
     * @param activity
     */
    public static void setStatusBarWhiteBg_BlackText(Activity activity) {
        Window window = activity.getWindow();
        String os = Build.BRAND;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if ("xiaomi".equalsIgnoreCase(os)) {
                setStatusBarBgColor(activity, R.color.bai);
                setStatusBarLightModeMIUI(window, true);
            } else if ("meizu".equalsIgnoreCase(os)){
                setStatusBarBgColor(activity, R.color.bai);
                setStatusBarLightModeFlyme(window, true);
            } else if ("kubi".equalsIgnoreCase(os)){
                setStatusBarBgColor(activity, R.color.bai);
                setStatusBarLightModeKUBI(window, true);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ("google".equalsIgnoreCase(os)
                        || "Samsung".equalsIgnoreCase(os)
                        || "HUAWEI".equalsIgnoreCase(os)){
                    setStatusBarBgColor(activity, R.color.bai);
                    setStatusBarLightModeDefault(window, true);
                } else {
                    //其他未适配厂商
                }
            }
        }
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本,如果要设置成透明请直接调用设置透明的方法
     * @param activity
     * @param colorResId
     */
    public static void setStatusBarBgColor(Activity activity,int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorResId));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTint库使4.4版本状态栏变色，需要先将状态栏设置为透明
            Window window =activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(colorResId);
        }
    }

    /**
     * 设置状态栏上文字的白/黑色
     * 目前已知怎么改的有4.4以上的小米的MIUI、魅族的Flyme，kubi手机，Android6.0
     * 注意，很多厂商都对这个地方做了定制，所以Android6.0的修改方法在许多国内系统不适用，
     * 已知通过6.0可以修改的有huawei，google，sunsung。
     */
    public static void setStatusBarTextColor(Activity activity, boolean isDark) {
        Window window = activity.getWindow();
        String os = Build.BRAND;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if ("xiaomi".equalsIgnoreCase(os)) {
                StatusBarUtil.setStatusBarLightModeMIUI(window, isDark);
            } else if ("meizu".equalsIgnoreCase(os)){
                StatusBarUtil.setStatusBarLightModeFlyme(window, isDark);
            } else if ("kubi".equalsIgnoreCase(os)){
                StatusBarUtil.setStatusBarLightModeKUBI(window, isDark);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ("google".equalsIgnoreCase(os)
                        || "Samsung".equalsIgnoreCase(os)
                        || "HUAWEI".equalsIgnoreCase(os)){
                    setStatusBarLightModeDefault(window, isDark);
                } else {
                    //其他未适配厂商
                }
            }
        }
    }

    /**
     * 设置状态栏图标为黑色/白色, google默认的实现，许多厂商都做了修改，需要调用厂商具体的方法
     * 可以用来判断是否为Flyme用户...注意只支持android6.0以上
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     */
    public static boolean setStatusBarLightModeDefault(Window window, boolean dark) {
        if (window != null) {
            if (dark) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            return true;
        }
        return false;
    }

    /**
     * 设置状态栏图标为黑色/白色，和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     */
    public static boolean setStatusBarLightModeFlyme(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为黑色/白色，需要MIUIV6以上
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     */
    public static boolean setStatusBarLightModeMIUI(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if(dark){
                    extraFlagField.invoke(window,darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
                }else{
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result=true;
            }catch (Exception e){

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为黑色/白色
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     */
    public static boolean setStatusBarLightModeKUBI(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Class layoutParams = Class.forName("android.app.StatusBarManager");
                String inverse = "";
                if (dark) {
                    inverse = "STATUS_BAR_INVERSE_GRAY";
                } else {
                    inverse = "STATUS_BAR_INVERSE_WHITE";
                }
                Field darkFlag = layoutParams.getDeclaredField(inverse);
                Field kubiFlags = WindowManager.LayoutParams.class.getDeclaredField("statusBarInverse");
                darkFlag.setAccessible(true);
                kubiFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                kubiFlags.setInt(lp, bit);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }
}
