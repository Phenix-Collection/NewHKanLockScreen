package com.haokan.hklockscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.haokan.hklockscreen.App;
import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.util.CommonUtil;
import com.haokan.hklockscreen.util.StatusBarUtil;

public class ActivitySplash extends ActivityBase implements View.OnClickListener {
    private long mStayTime = 1000; //倒计时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarUtil.setStatusBarTransparnet(this);

        initView();

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

        Intent i = new Intent(ActivitySplash.this, ActivitySetLockScreen.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
    }
}
