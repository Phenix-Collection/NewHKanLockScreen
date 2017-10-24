package com.haokan.hklockscreen.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.home.ActivityHomePage;
import com.haokan.hklockscreen.lockscreen.ServiceLockScreen;
import com.haokan.hklockscreen.lockscreeninitset.ActivityLockScreenInitSet;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.util.CommonUtil;
import com.haokan.pubic.util.StatusBarUtil;
import com.haokan.pubic.util.Values;


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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean first = preferences.getBoolean(Values.PreferenceKey.KEY_SP_FIRSTINSTALL, true);
        if (first) {
            preferences.edit().putBoolean(Values.PreferenceKey.KEY_SP_FIRSTINSTALL, false).apply();
            Intent intent = new Intent(this, ActivityLockScreenInitSet.class);
            startActivity(intent);
        }

        finish();
        overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
    }
}
