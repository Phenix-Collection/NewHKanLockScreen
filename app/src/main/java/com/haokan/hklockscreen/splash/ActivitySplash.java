package com.haokan.hklockscreen.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.home.ActivityHomePage;
import com.haokan.hklockscreen.lockscreen.ServiceLockScreen;
import com.haokan.hklockscreen.lockscreeninitset.ActivityLockScreenInitSet;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.UrlsUtil;
import com.haokan.pubic.maidian.MaidianManager;
import com.haokan.pubic.util.CommonUtil;
import com.haokan.pubic.util.StatusBarUtil;
import com.haokan.pubic.util.Values;

import java.util.concurrent.TimeUnit;

import rx.functions.Action0;
import rx.schedulers.Schedulers;


public class ActivitySplash extends ActivityBase implements View.OnClickListener {
    private long mStayTime = 1000; //倒计时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarUtil.setStatusBarTransparnet(this);

        initView();

        Intent i = new Intent(ActivitySplash.this, ServiceLockScreen.class);
        startService(i);
        App.sMainHanlder.postDelayed(mLaunchHomeRunnable, mStayTime);

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

    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
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

//        Intent i = new Intent(ActivitySplash.this, ActivityLockScreen.class);
//        Intent i = new Intent(ActivitySplash.this, ActivityAutoSetLockScreen.class);
        Intent i = new Intent(ActivitySplash.this, ActivityHomePage.class);
        startActivity(i);

        //是否是第一次安装
        String manufacturer = Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase("xiaomi")
                || manufacturer.equalsIgnoreCase("oppo")) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean first = preferences.getBoolean(Values.PreferenceKey.KEY_SP_FIRSTINSTALL, true);
            if (first) {
                preferences.edit().putBoolean(Values.PreferenceKey.KEY_SP_FIRSTINSTALL, false).apply();
                Intent intent = new Intent(this, ActivityLockScreenInitSet.class);
                startActivity(intent);
            }
        }

        finish();
        overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
    }
}
