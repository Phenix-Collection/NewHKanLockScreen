package com.haokan.hklockscreen.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreen.ServiceLockScreen;
import com.haokan.hklockscreen.lockscreenautoupdateimage.ServiceAutoUpdateImage;
import com.haokan.hklockscreen.lockscreeninitset.ActivityLockScreenInitSet;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.checkupdate.UpdateManager;
import com.haokan.pubic.maidian.UmengMaiDianManager;
import com.haokan.pubic.util.MyDateTimeUtil;
import com.haokan.pubic.util.StatusBarUtil;
import com.haokan.pubic.util.ToastManager;
import com.haokan.pubic.util.Values;

/**
 * Created by wangzixu on 2017/10/20.
 */
public class ActivityHomePage extends ActivityBase {
    private CV_RecommendPage_HomePage mCvHomePage;
    private boolean mFirst;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarTransparnet(this);
        setContentView(R.layout.activity_homepage);

        initView();
//        mCvHomePage.setTypeName("娱乐");
//        Random random = new Random();
//        int anInt = random.nextInt(CV_RecommendPage.sTypes.length);
//        mCvHomePage.setTypeName(CV_RecommendPage.sTypes[anInt]);
//        mCvHomePage.setTypeName("艺术");
        mCvHomePage.loadData(true);

        Intent i = new Intent(this, ServiceLockScreen.class);
        startService(i);

        //是否是第一次安装
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean mFirst = preferences.getBoolean(Values.PreferenceKey.KEY_SP_FIRSTINSTALL, true);
        if (mFirst) {
            preferences.edit().putBoolean(Values.PreferenceKey.KEY_SP_FIRSTINSTALL, false).apply();
            Intent intent = new Intent(this, ActivityLockScreenInitSet.class);
            intent.putExtra(ActivityLockScreenInitSet.KEY_INTENT_FROMHOME, true);
            startActivity(intent);

        } else {
            App.sMainHanlder.postDelayed(mPermissionRun, 600);
        }

        boolean mFirstUpdateFunction = preferences.getBoolean(Values.PreferenceKey.KEY_SP_FIRSTUPDATEFUNCTION, true);
        if (mFirstUpdateFunction) {
            preferences.edit().putBoolean(Values.PreferenceKey.KEY_SP_FIRSTUPDATEFUNCTION, false).apply();

            String curTime = MyDateTimeUtil.getCurrentSimpleData();
            preferences.edit().putString(ServiceAutoUpdateImage.KEY_AUTOUPDATA_TIME, curTime).apply();
            if (App.sHaokanLockView != null) {
                App.sHaokanLockView.setUpdateSign(0);
            }
        }
    }

    private void initView() {
        mCvHomePage = (CV_RecommendPage_HomePage) findViewById(R.id.cv_homepage);
        mCvHomePage.setActivityBase(this);
    }

    Runnable mPermissionRun = new Runnable() {
        @Override
        public void run() {
            checkStoragePermission();
        }
    };

    //熊守义友盟埋点, 统计这个页的曝光, 类似统计广告的方式来统计的页面
    Runnable mUmMaidianRun = new Runnable() {
        @Override
        public void run() {
            UmengMaiDianManager.onEvent(ActivityHomePage.this, "event_081");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (mCvHomePage != null) {
            mCvHomePage.onResume();
        }

        App.sMainHanlder.postDelayed(mUmMaidianRun, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.sMainHanlder.removeCallbacks(mPermissionRun);
        App.sMainHanlder.removeCallbacks(mUmMaidianRun);
    }

    //权限相关begin*****
    /**
     * 检查权限
     */
    public void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //需要用权限的地方之前，检查是否有某个权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) { //没有这个权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
                return;
            } else {
                UpdateManager.checkUpdate(this, true);
            }
        } else {
            UpdateManager.checkUpdate(this, true);
        }
    }

    //检查权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 201:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //同意
                        UpdateManager.checkUpdate(this, true);
                    } else {
                        // 不同意
//                        ToastManager.showCenter(this, "没有授予存储权限, 无法更新, 请去设置打开");
                        App.sMainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                askToOpenPermissions();
                            }
                        });
                    }
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 提示用户去设置界面开启权限
     */
    private void askToOpenPermissions() {
        View cv = LayoutInflater.from(this).inflate(R.layout.dialog_layout_asksdpermission, null);
        TextView desc = (TextView) cv.findViewById(R.id.tv_desc);
        desc.setText("没有授予存储权限, 无法下载和自动更新, 请去设置授予存储权限");

        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("重要提示")
                .setView(cv)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    //权限相关end*****

    protected long mExitTime;
    @Override
    public void onBackPressed() {
        if ((SystemClock.uptimeMillis() - mExitTime) >= 1500) {
            mExitTime = SystemClock.uptimeMillis();
            ToastManager.showShort(this, "再按一次退出");
        } else {
            UmengMaiDianManager.onEvent(this, "event_084");
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (mCvHomePage != null) {
            mCvHomePage.onDestory();
        }
        super.onDestroy();
    }
}
