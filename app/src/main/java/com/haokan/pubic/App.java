package com.haokan.pubic;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreen.CV_DetailPage_LockScreen;
import com.haokan.hklockscreen.lockscreen.ReceiverLockScreen;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.UrlsUtil;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.maidian.MaidianManager;
import com.haokan.pubic.util.BuildProperties;
import com.haokan.pubic.util.CommonUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class App extends Application {
    public static String APP_VERSION_NAME = "";
    public static int APP_VERSION_CODE;
    public static final int  sAPPid = 10017;
    public static String sH5_EID = "138005"; //默认值 ,H5产品分给我们的值, Exit

    public static String sDID = "default"; //默认值
    public static String sPID = "206"; //默认值
    public static String sEID = "138005"; //默认值
    public static String sPhoneModel = "defaultPhone";
    public static String sImgSize_Big = "1080x1920";
//    public static String sImgSize_Big = "720x1080";
//    public static String sImgSize_Small = "360x640";
    public static String sImgSize_Small = "540x960";
    public static String sLanguageCode = "zh";
    public static String sCountryCode = "CN";

    public static final Handler sMainHanlder = new Handler(Looper.getMainLooper());
    public static String sReview = "0"; //1表示review, 0表示没有

    private ReceiverLockScreen mReceiver;
    public static CV_DetailPage_LockScreen sHaokanLockView;

    /**
     * 是否是已经适配了的手机, 0代表未适配<br/>
     * 1:colorOs-3.0.0i-Android6.0 <br/>
     * 2, 小米v9
     */
    public static int sIsAdapterPhone = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        init(this);

        //友盟分享初始化begin
        UMShareAPI.get(this);
        MobclickAgent.setDebugMode(false);
        //微信 appid appsecret
        PlatformConfig.setWeixin("wx9d116eb352937363", "7c8e12f912049757a143ab874346bfe2");
        //新浪微博 appkey appsecret
        PlatformConfig.setSinaWeibo("2156364876","e3350a8d04bebf03da9e457f50682c0f","https://api.weibo.com/oauth2/default.html");
        // QQ和Qzone appid appkey
        PlatformConfig.setQQZone("1104604449", "DYIcUy0pqatvbvWj");
        //友盟分享初始化end

        //阿里云反馈begin
        FeedbackAPI.init(this, "24649885", "5761371ea5beb9c2d15ea43c1b453c32");
        FeedbackAPI.setTranslucent(false);
        FeedbackAPI.setBackIcon(R.drawable.icon_back_hei);
        //阿里云反馈end

        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.setPriority(Integer.MAX_VALUE);
        mReceiver = new ReceiverLockScreen();
        registerReceiver(mReceiver, filter);
        sHaokanLockView = new CV_DetailPage_LockScreen(getApplicationContext());

//        if (LogHelper.DEBUG) {
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                // This process is dedicated to LeakCanary for heap analysis.
//                // You should not init your app in this process.
//                return;
//            }
//            LeakCanary.install(this);
//        }

        StringBuilder builder = new StringBuilder();
        builder.append(App.sDID).append(",")
                .append(UrlsUtil.COMPANYID).append(",")
                .append(App.sEID).append(",")
                .append(App.sPID).append(",")
                .append(App.APP_VERSION_CODE).append(",")
                .append(HttpStatusManager.getIPAddress(this)).append(",")
                .append(App.sPhoneModel).append(",")
                .append(HttpStatusManager.getNetworkType(this)).append(",")
                .append(System.currentTimeMillis());
        MaidianManager.initUser(builder.toString());

        Schedulers.io().createWorker().schedulePeriodically(new Action0() {
            @Override
            public void call() {
                MaidianManager.actionUpdate();
            }
        }, 0, 20, TimeUnit.SECONDS);

        //控制接听电话的状态, 需要2步, 先注册这个, 有的rom注册了这个会一直生效, 有的rom会只生效一次,
        //需要ACTION_NEW_OUTGOING_CALL和android.intent.action.PHONE_STATE广播, 靠广告中获取状态
        TelephonyManager tm = (TelephonyManager)getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(new PhoneStateListener(){
                      @Override
                      public void onCallStateChanged(int state, String incomingNumber) {
                          //注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
                          super.onCallStateChanged(state, incomingNumber);
                          switch(state){
                              case TelephonyManager.CALL_STATE_IDLE:
                                  LogHelper.d("wangzixu", "phonestate TelephonyManager 挂断");
                                  ReceiverLockScreen.sIsCallIng = false;
                                  break;
                              case TelephonyManager.CALL_STATE_OFFHOOK:
                                  LogHelper.d("wangzixu", "phonestate TelephonyManager 接听");
                                  ReceiverLockScreen.sIsCallIng = true;
                                  break;
                              case TelephonyManager.CALL_STATE_RINGING:
                                  LogHelper.d("wangzixu", "phonestate TelephonyManager 响铃:来电号码"+incomingNumber);
                                  ReceiverLockScreen.sIsCallIng = true;
                                  //输出来电号码
                                  break;
                          }
                      }}, PhoneStateListener.LISTEN_CALL_STATE);
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
//        LogHelper.d("wangzixu", "app init = " + CommonUtil.getDevice());
//        String string = BuildProperties.getSystemProperty("ro.build.display.id");
//        LogHelper.d("wangzixu", "app init = " + string);
        BuildProperties.newInstance();
    }
}
