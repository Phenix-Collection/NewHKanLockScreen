package com.haokan.hklockscreen.autoset;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreensetting.ActivityLockSetting;
import com.haokan.hklockscreen.timeline.ActivityTimelines;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.util.CommonUtil;
import com.haokan.pubic.util.LogHelper;
import com.haokan.pubic.util.StatusBarUtil;
import com.haokan.pubic.util.SystemIntentUtil;
import com.haokan.pubic.util.ToastManager;


public class ActivityAutoSetLockScreen extends ActivityBase implements View.OnClickListener {
    private View mBtnGoSet;
    public static boolean sIsAutoSet = false;
    private CV_ScanRadarView mRadarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlockscreen);
        StatusBarUtil.setStatusBarTransparnet(this);
        initView();

    }

    private void initView() {
        mBtnGoSet = findViewById(R.id.goset);
        mBtnGoSet.setOnClickListener(this);
        findViewById(R.id.gomain).setOnClickListener(this);

        mRadarView = (CV_ScanRadarView) findViewById(R.id.radarview);
//        radarView.setRadarRadius(DisplayUtil.dip2px(this, 170));
        startScanAnim();
    }

    private void startScanAnim() {
        App.sMainHanlder.post(new Runnable() {
            @Override
            public void run() {
                if (mRadarView.getWidth() == 0) {
                    startScanAnim();
                } else {
                    mRadarView.start();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.gomain:
                Intent i = new Intent(this, ActivityTimelines.class);
                startActivity(i);
                break;
            case R.id.goset:
                try {
//                    i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    i = new Intent(this, ActivityLockSetting.class);
                    startActivityForResult(i, 101);
                    overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);

//                    App.sMainHanlder.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent i2 = new Intent(ActivityAutoSetLockScreen.this, ActivityPrompt_AutoStart.class);
//                            startActivity(i2);
//                        }
//                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (isAccessibilitySettingsOn(this)) {
                App.sMainHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = SystemIntentUtil.getAutoStartIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try{
                            sIsAutoSet = true;
                            startActivity(intent);
                        }catch (Exception e){//抛出异常就直接打开设置页面
                            sIsAutoSet = false;
                            e.printStackTrace();
                            ToastManager.showShort(ActivityAutoSetLockScreen.this, "没有找到自启动界面");
                        }
                        overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                    }
                }, 300);
            } else {

            }
        }
    }

    //检查辅助功能是否开启了
    private boolean isAccessibilitySettingsOn(Context mContext) {
        final String service = getPackageName() + "/" + ServiceMyAccessibility.class.getCanonicalName();
        String string = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver()
                , Settings.Secure.ACCESSIBILITY_ENABLED);
        LogHelper.d("wangzixu", "isAccessibilitySettingsOn string = " + string);

        if ("1".equals(string)) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver()
                    , Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
//            LogHelper.d("wangzixu", "isAccessibilitySettingsOn ACCESSIBILITY IS ENABLED settingValue = " + settingValue);
            if (settingValue != null) {
                String[] split = settingValue.split(":");
                for (int i = 0; i < split.length; i++) {
                    if (split[i].equalsIgnoreCase(service)) {
                        LogHelper.d("wangzixu", "isAccessibilitySettingsOn accessibility is switched on!");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
