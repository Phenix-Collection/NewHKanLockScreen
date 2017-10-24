package com.haokan.hklockscreen.lockscreeninitset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.util.StatusBarUtil;


public class ActivityLockScreenInitSet extends ActivityBase {
    private View mBtnGoSet;
    private CV_ScanRadarView mRadarView;
    private WindowManager mWindowManager;
    private View mView;
    private CV_LockInitSetView mCvLockScreenInitSetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initlockscreen);
        StatusBarUtil.setStatusBarTransparnet(this);
        initView();
    }

    private void initView() {
        mCvLockScreenInitSetView = (CV_LockInitSetView) findViewById(R.id.cv_lockinitview);
        mCvLockScreenInitSetView.setActivityBase(this);
    }

//    @Override
//    public void onClick(View v) {
//        if (CommonUtil.isQuickClick()) {
//            return;
//        }
//        switch (v.getId()) {
//            case R.id.gotest:
//                goAutoSetActivity();
//                break;
//            case R.id.goset:
////                try {
//////                    i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
////                    Intent i = new Intent(this, ActivityLockSetting.class);
////                    startActivityForResult(i, 101);
////                    overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
////
//////                    App.sMainHanlder.post(new Runnable() {
//////                        @Override
//////                        public void run() {
//////                            Intent i2 = new Intent(ActivityAutoSetLockScreen.this, ActivityPrompt_AutoStart.class);
//////                            startActivity(i2);
//////                        }
//////                    });
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//
//                if (mView != null) {
//                    mWindowManager.removeView(mView);
//                    mView = null;
//                    return;
//                }
//
//                mWindowManager = (WindowManager)getApplication().getSystemService(Context.WINDOW_SERVICE);
//
//                WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
//                // 设置window type
//                mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
////                mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
//
//
//                mParams.format = PixelFormat.RGBA_8888;
//                mParams.flags = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
//                mParams.flags = mParams.flags | WindowManager.LayoutParams.FLAG_FULLSCREEN;
////                mParams.flags = mParams.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
//                mParams.flags = mParams.flags | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//                mParams.flags = mParams.flags | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
//                mParams.flags = mParams.flags | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//
//                // 调整悬浮窗显示的停靠位置为左侧置顶
//                mParams.gravity = Gravity.LEFT | Gravity.TOP;
//                // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
//                mParams.x = 0;
//                mParams.y = 0;
//
//                // 屏幕方向
//                mParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                // 设置悬浮窗口长宽数据
//                mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
////                mParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//                mParams.height = 1500;
//
////                Class<? extends WindowManager.LayoutParams> mParamsClass = mParams.getClass();
////                try {
////                    Field field = mParamsClass.getDeclaredField("hideTimeoutMilliseconds");
////                    field.setAccessible(true);
////                    field.set(mParams, 60000l);
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//
//                // 获取浮动窗口视图所在布局
//                mView = LayoutInflater.from(this).inflate(R.layout.activity_initlockscreen_view, null, false);
//                mWindowManager.addView(mView, mParams);
//
//                mView.findViewById(R.id.gotest).setOnClickListener(ActivityLockScreenInitSet.this);
////                App.sMainHanlder.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        goAutoSetActivity();
////                    }
////                }, 3000);
//
//                break;
//            default:
//                break;
//        }
//    }

    //权限相关begin*****


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCvLockScreenInitSetView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        //
    }
}
