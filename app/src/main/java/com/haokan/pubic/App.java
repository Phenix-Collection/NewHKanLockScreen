package com.haokan.pubic;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import com.haokan.hklockscreen.lockscreen.ReceiverLockScreen;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.CommonUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.Locale;

public class App extends Application {
    public static String APP_VERSION_NAME = "";
    public static int APP_VERSION_CODE;
    public static String sDID = "default"; //默认值
    public static String sPID = "138003"; //默认值
    public static String sEID = "0"; //默认值
    public static String sPhoneModel = "defaultPhone";
    public static String sImgSize_Big = "1080x1920";
//    public static String sImgSize_Small = "360x640";
//    public static String sImgSize_Small = "720x1080";
    public static String sImgSize_Small = "540x960";
    public static String sLanguageCode = "zh";
    public static String sCountryCode = "CN";

    public static final Handler sMainHanlder = new Handler(Looper.getMainLooper());
    public static String sReview = "0"; //1表示review, 0表示没有
    private ReceiverLockScreen mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        init(this);

        UMShareAPI.get(this);
        MobclickAgent.setDebugMode(true);

        //微信 appid appsecret
        PlatformConfig.setWeixin("wx9d116eb352937363", "7c8e12f912049757a143ab874346bfe2");
        //新浪微博 appkey appsecret
        PlatformConfig.setSinaWeibo("2156364876","e3350a8d04bebf03da9e457f50682c0f","https://api.weibo.com/oauth2/default.html");
        // QQ和Qzone appid appkey
        PlatformConfig.setQQZone("1104604449", "DYIcUy0pqatvbvWj");

        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.setPriority(Integer.MAX_VALUE);
        mReceiver = new ReceiverLockScreen();
        registerReceiver(mReceiver, filter);
    }

    public static void init(final Context context) {
        APP_VERSION_NAME = CommonUtil.getLocalVersionName(context);
        APP_VERSION_CODE = CommonUtil.getLocalVersionCode(context);
        sDID = CommonUtil.getDid(context);
        sPID = CommonUtil.getPid(context);
        sPhoneModel = CommonUtil.getPhoneModel(context);

        Locale locale = Locale.getDefault();
        sLanguageCode = locale.getLanguage();
        sCountryCode = locale.getCountry();
        LogHelper.d("wangzixu", "app init sLanguageCode = " + sLanguageCode + ", sCountryCode = " + sCountryCode);
    }
}
