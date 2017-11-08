package com.haokan.hklockscreen.splash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.UrlsUtil;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.maidian.MaidianManager;
import com.haokan.pubic.util.CommonUtil;
import com.haokan.pubic.util.StatusBarUtil;
import com.haokan.pubic.webview.ActivityWebview;

import java.util.concurrent.TimeUnit;

import rx.functions.Action0;
import rx.schedulers.Schedulers;


public class ActivitySplash extends ActivityBase implements View.OnClickListener {
    private long mStayTime = 2000; //倒计时
    private ImageView mIvAdView;
    private TextView mTvJumpAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarUtil.setStatusBarTransparnet(this);

        App.sMainHanlder.postDelayed(mLaunchHomeRunnable, mStayTime);
        initView();

        Intent i = new Intent(ActivitySplash.this, ServiceLockScreen.class);
        startService(i);

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
    }

    private void initView() {
        mTvJumpAd = (TextView) findViewById(R.id.jumpad);
        mTvJumpAd.setOnClickListener(this);

        mIvAdView = (ImageView) findViewById(R.id.adview);

        BannerReq bannerReq = new BannerReq();
        bannerReq.w = 1080;
        bannerReq.h = 1560;
        BidRequest request = ModelHaoKanAd.getBidRequest("28-53-209", 5, null, bannerReq);

        ModelHaoKanAd.getAd(this, request, new onAdResListener<BeanAdRes>() {
            @Override
            public void onAdResSuccess(final BeanAdRes adRes) {
                LogHelper.d("wangzixu", "ModelHaoKanAd splash onAdResSuccess");
                Glide.with(ActivitySplash.this).load(adRes.imgUrl).into(mIvAdView);
                App.sMainHanlder.removeCallbacks(mLaunchHomeRunnable);
                App.sMainHanlder.postDelayed(mLaunchHomeRunnable, 3000);
                mTvJumpAd.setVisibility(View.VISIBLE);
                mTvJumpAd.setText("广告3");
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

                            Intent intent = new Intent(ActivitySplash.this, ActivityWebview.class);
                            intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, adRes.landPageUrl);
                            startActivity(intent);

                            finish();
                            startActivityAnim();
                        }
                    });
                }

                //广告展示上报
                ModelHaoKanAd.adShowUpLoad(adRes.showUpUrl);
            }

            @Override
            public void onAdResFail(String errmsg) {
                LogHelper.d("wangzixu", "ModelHaoKanAd splash onAdResFail errmsg = " + errmsg);
            }
        });

//        mAdMediaView.setAdJumpWebview(true);
//        mAdMediaView.setAdClickListener(new AdClickListener() {
//            @Override
//            public void onAdClick() {
//                App.sMainHanlder.removeCallbacks(mLaunchHomeRunnable);
//            }
//        });
//        Intent i = new Intent(ActivitySplash.this, ActivityHomePage.class);
//        mAdMediaView.setAdJumpWebViewCloseIntent(i.toUri(0));
//
//        HaokanADManager.getInstance().loadAdData(getApplication(), AdTypeCommonUtil.REQUEST_SPLASH_TYPE, "28-53-209", 1080, 1560, new HaokanADInterface() {
//            @Override
//            public void onADSuccess(AdData adData) {
//                mAdMediaView.setNativeAd(adData, new EffectiveAdListener() {
//                    @Override
//                    public void onAdInvalid() {
//                        LogHelper.d("wangzixu", "HaokanADManager  28-53-209 setNativeAd onAdInvalid");
//                    }
//
//                    @Override
//                    public void onLoadSuccess() {
//                        LogHelper.d("wangzixu", "HaokanADManager 28-53-209 setNativeAd onLoadSuccess");
//                        App.sMainHanlder.removeCallbacks(mLaunchHomeRunnable);
//                        App.sMainHanlder.postDelayed(mLaunchHomeRunnable, 3000);
//                        mTvJumpAd.setVisibility(View.VISIBLE);
//                        mTvJumpAd.setText("3");
//                        App.sMainHanlder.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                mTvJumpAd.setText("2");
//                            }
//                        }, 1000);
//                        App.sMainHanlder.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                mTvJumpAd.setText("1");
//                            }
//                        }, 2000);
//                    }
//
//                    @Override
//                    public void onLoadFailure() {
//                        LogHelper.d("wangzixu", "HaokanADManager 28-53-209 setNativeAd onLoadFailure");
//                    }
//                });
//            }
//
//            @Override
//            public void onADError(String s) {
//                LogHelper.d("wangzixu", "HaokanADManager loadAdData 28-53-209 onADError s = " + s);
//            }
//        });
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
