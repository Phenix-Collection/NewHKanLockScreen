package com.haokan.hklockscreen.splash;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.haokanAd.BeanAdRes;
import com.haokan.hklockscreen.haokanAd.ModelHaoKanAd;
import com.haokan.hklockscreen.haokanAd.onAdResListener;
import com.haokan.hklockscreen.haokanAd.request.BannerReq;
import com.haokan.hklockscreen.haokanAd.request.BidRequest;
import com.haokan.hklockscreen.home.ActivityHomePage;
import com.haokan.hklockscreen.lockscreen.ServiceLockScreen;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.BuildProperties;
import com.haokan.pubic.util.CommonUtil;
import com.haokan.pubic.util.StatusBarUtil;
import com.haokan.pubic.webview.ActivityWebview;

import java.util.Map;
import java.util.Set;

import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


public class ActivitySplash extends ActivityBase implements View.OnClickListener {
    private long mStayTime = 3000; //倒计时
    private ImageView mIvAdView;
    private TextView mTvJumpAd;
    private BeanAdRes mBeanAdRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarUtil.setStatusBarTransparnet(this);

        App.sMainHanlder.postDelayed(mLaunchHomeRunnable, mStayTime);
        initView();

        Intent i = new Intent(ActivitySplash.this, ServiceLockScreen.class);
        startService(i);

        checkIsAdapter();

        try {
            WebView webView = new WebView(this);
            App.sUserAgent = webView.getSettings().getUserAgentString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mTvJumpAd = (TextView) findViewById(R.id.jumpad);
        mTvJumpAd.setOnClickListener(this);

        mIvAdView = (ImageView) findViewById(R.id.adview);
        mIvAdView.setOnClickListener(this);

        BannerReq bannerReq = new BannerReq();
        bannerReq.w = 1080;
        bannerReq.h = 1560;
        BidRequest request = ModelHaoKanAd.getBidRequest(this, "28-53-209", 5, null, bannerReq);

        ModelHaoKanAd.getAd(this.getApplicationContext(), request, new onAdResListener<BeanAdRes>() {
            @Override
            public void onAdResSuccess(final BeanAdRes adRes) {
                LogHelper.d("wangzixu", "ModelHaoKanAd splash onAdResSuccess");
                mBeanAdRes = adRes;
                Glide.with(ActivitySplash.this).load(adRes.imgUrl).into(mIvAdView);
                App.sMainHanlder.removeCallbacks(mLaunchHomeRunnable);
                App.sMainHanlder.postDelayed(mLaunchHomeRunnable, 3000);
                mTvJumpAd.setVisibility(View.VISIBLE);
                mTvJumpAd.setText("广告3");

                //广告展示上报
                ModelHaoKanAd.onAdShow(adRes.onShowUrls);

                App.sMainHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTvJumpAd.setText("广告2");
                    }
                }, 1000);
                App.sMainHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTvJumpAd.setText("广告1");
                    }
                }, 2000);

                if (!TextUtils.isEmpty(adRes.landPageUrl)) {
                    mIvAdView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            App.sMainHanlder.removeCallbacks(mLaunchHomeRunnable);

                            //先跳首页, 再跳落地页
                            Intent i = new Intent(ActivitySplash.this, ActivityHomePage.class);
                            startActivity(i);

//                            Intent intent = new Intent(ActivitySplash.this, ActivityWebview.class);
//                            intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, adRes.landPageUrl);

                            Intent intent = new Intent(ActivitySplash.this, ActivityWebview.class);
                            //查询是否有deeplink的app
                            if (!TextUtils.isEmpty(mBeanAdRes.deeplink)) {
                                Intent qi = new Intent(Intent.ACTION_VIEW, Uri.parse(mBeanAdRes.deeplink));
                                if (CommonUtil.deviceCanHandleIntent(ActivitySplash.this, qi)) { //是否可以支持deeplink
                                    intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBeanAdRes.deeplink);
                                } else {
                                    intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBeanAdRes.landPageUrl);
                                }
                            }else {
                                intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBeanAdRes.landPageUrl);
                            }
                            startActivity(intent);

                            finish();
                            startActivityAnim();
                        }
                    });
                }
            }

            @Override
            public void onAdResFail(String errmsg) {
                LogHelper.d("wangzixu", "ModelHaoKanAd splash onAdResFail errmsg = " + errmsg);
            }
        });
    }

    public void checkIsAdapter() {
        final Scheduler.Worker worker = Schedulers.io().createWorker();
        worker.schedule(new Action0() {
            @Override
            public void call() {
                App.sIsAdapterPhone = 0;

                boolean beginAdapter = true;

                BuildProperties properties = BuildProperties.newInstance();
                int sdkInt = Build.VERSION.SDK_INT;

                Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();//返回的属性键值对实体
                for (Map.Entry<Object, Object> entry : entrySet) {
                    LogHelper.d("wangzixu", "app init : " + entry.getKey() + " = " + entry.getValue());
//                    LogHelper.writeLog(ActivitySplash.this, "app init : " + entry.getKey() + " = " + entry.getValue());
                }

                //适配机型
                if (beginAdapter) {
                    String property = properties.getProperty("ro.build.version.opporom");
                    if (!TextUtils.isEmpty(property) && property.contains("V3.0") && sdkInt == 23) {
                        beginAdapter = false;
                        App.sIsAdapterPhone = 1; //第一类型, oppo的 colorOs-3.0.0i-Android6.0
                    }
                }

                if (beginAdapter) {
                    if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
                        beginAdapter = false;
                        App.sIsAdapterPhone = 2; //第二类型, 小米v9, 自启动, 无法适配其去除系统锁屏
                    }
                }

                if (beginAdapter) {
                    String property = properties.getProperty("ro.build.version.emui");
                    if (!TextUtils.isEmpty(property) && property.contains("EmotionUI_4.0") && sdkInt == 23) {
                        beginAdapter = false;
                        App.sIsAdapterPhone = 3; //第3类型, 华为emui4.0.1- Android6.0
                    }
                }

                if (beginAdapter) {
                    String property = properties.getProperty("ro.build.display.id");
                    if (!TextUtils.isEmpty(property) && property.contains("amigo3.5") && sdkInt == 23) {
                        beginAdapter = false;
                        App.sIsAdapterPhone = 4; //第4类型, 金立GN8002S, amigo3.5.11- Android6.0
                    }
                }

                if (beginAdapter) {
                    String property = properties.getProperty("ro.security.vpnpp.release");
                    if (!TextUtils.isEmpty(property) && property.startsWith("8.1") && sdkInt == 24) {
                        beginAdapter = false;
                        App.sIsAdapterPhone = 5; //第5类型, 三星se 8.1 android7.0
                    }
                }


                worker.unsubscribe();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.jumpad:
                App.sMainHanlder.removeCallbacks(mLaunchHomeRunnable);
                App.sMainHanlder.post(mLaunchHomeRunnable);
                break;
            case R.id.adview:
                if (mBeanAdRes != null) {
                    App.sMainHanlder.removeCallbacks(mLaunchHomeRunnable);

                    Intent i = new Intent(ActivitySplash.this, ActivityHomePage.class);
                    startActivity(i);

                    Intent intent = new Intent(ActivitySplash.this, ActivityWebview.class);
                    //查询是否有deeplink的app
                    if (!TextUtils.isEmpty(mBeanAdRes.deeplink)) {
                        Intent qi = new Intent(Intent.ACTION_VIEW, Uri.parse(mBeanAdRes.deeplink));
                        if (CommonUtil.deviceCanHandleIntent(ActivitySplash.this, qi)) { //是否可以支持deeplink
                            intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBeanAdRes.deeplink);
                        } else {
                            intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBeanAdRes.landPageUrl);
                        }
                    }else {
                        intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBeanAdRes.landPageUrl);
                    }
                    startActivity(intent);
                    //广告点击上报
                    if (mBeanAdRes.onClickUrls != null && mBeanAdRes.onClickUrls.size() > 0) {
                        ModelHaoKanAd.onAdClick(mBeanAdRes.onClickUrls);
                    }
                    startActivityAnim();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private Runnable mLaunchHomeRunnable = new Runnable() {
        @Override
        public void run() {
            launcherHome();
        }
    };

    public void launcherHome() {
        if (mIsDestory) {
            return;
        }
        mIsDestory = true;

        Intent i = new Intent(ActivitySplash.this, ActivityHomePage.class);
        startActivity(i);
        finish();
        startActivityAnim();
    }
}
