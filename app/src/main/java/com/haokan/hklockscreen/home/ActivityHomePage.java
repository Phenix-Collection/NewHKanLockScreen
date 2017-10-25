package com.haokan.hklockscreen.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreen.ServiceLockScreen;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.util.StatusBarUtil;
import com.haokan.pubic.util.ToastManager;

/**
 * Created by wangzixu on 2017/10/20.
 */
public class ActivityHomePage extends ActivityBase {
    private CV_RecommendPage_HomePage mCvHomePage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarTransparnet(this);
        setContentView(R.layout.activity_homepage);

        initView();
        mCvHomePage.setTypeName("娱乐");
        mCvHomePage.loadData(true);

        Intent i = new Intent(this, ServiceLockScreen.class);
        startService(i);
    }

    private void initView() {
        mCvHomePage = (CV_RecommendPage_HomePage) findViewById(R.id.cv_homepage);
        mCvHomePage.setActivityBase(this);
    }


    protected long mExitTime;
    @Override
    public void onBackPressed() {
        if ((SystemClock.uptimeMillis() - mExitTime) >= 1500) {
            mExitTime = SystemClock.uptimeMillis();
            ToastManager.showShort(this, "再按一次退出");
        } else {
            super.onBackPressed();
//            Process.killProcess(Process.myPid());
//            System.exit(0);
        }
    }
}
