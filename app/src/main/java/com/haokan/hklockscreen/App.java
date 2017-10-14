package com.haokan.hklockscreen;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.haokan.hklockscreen.util.CommonUtil;
import com.haokan.hklockscreen.util.LogHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

public class App extends Application {
    public static String APP_VERSION_NAME = "";
    public static int APP_VERSION_CODE;
    public static String sDID = "default"; //默认值
    public static String sPID = "138003"; //默认值
    public static String sPhoneModel = "defaultPhone";
    public static long sStartAppTime; //app开始的时间
    public static final Handler sMainHanlder = new Handler(Looper.getMainLooper());
    public static String sReview = "0"; //1表示review, 0表示没有

    @Override
    public void onCreate() {
        super.onCreate();
        init(this);

        UMShareAPI.get(this);
        MobclickAgent.setDebugMode(false);
        com.umeng.socialize.utils.Log.LOG = false; //友盟分享的log开关

        //微信 appid appsecret
        PlatformConfig.setWeixin("wx9f0b565235da43e1", "759db4319d6c23b09c2d28b9a4fcb4ad");
        //新浪微博 appkey appsecret
        PlatformConfig.setSinaWeibo("357695541", "a4d2df94f7c5c2e48ae93659801e2249","https://api.weibo.com/oauth2/default.html");
        // QQ和Qzone appid appkey
        PlatformConfig.setQQZone("1101819412", "pvH55D7PJ3XTii7j");
    }

    public static void init(final Context context) {
        APP_VERSION_NAME = CommonUtil.getLocalVersionName(context);
        APP_VERSION_CODE = CommonUtil.getLocalVersionCode(context);
        sDID = CommonUtil.getDid(context);
        sPID = CommonUtil.getPid(context);
        sPhoneModel = CommonUtil.getPhoneModel(context);
        LogHelper.d("wangzixu", "app init APP_VERSION_CODE = " + APP_VERSION_CODE);
    }
}
