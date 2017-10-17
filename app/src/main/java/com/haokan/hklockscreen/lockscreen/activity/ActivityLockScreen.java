package com.haokan.hklockscreen.lockscreen.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreen.detailpage.CV_DetailPage_LockScreen;
import com.haokan.hklockscreen.lockscreen.service.ServiceLockScreen;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.detailpage.CV_DetailPageView_Base;
import com.haokan.pubic.util.StatusBarUtil;

/**
 * Created by wangzixu on 2017/3/2.
 */
public class ActivityLockScreen extends ActivityBase implements View.OnClickListener, View.OnSystemUiVisibilityChangeListener {
    private CV_DetailPageView_Base mViewDetailPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableKeyGuard();
        StatusBarUtil.setStatusBarTransparnet(this);
        setContentView(R.layout.activity_lock);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content);
        if (ServiceLockScreen.sHaokanLockView == null) {
            ServiceLockScreen.sHaokanLockView = new CV_DetailPage_LockScreen(this.getApplicationContext());
        }
        ServiceLockScreen.sHaokanLockView.setActivity(this);
        ViewParent parent = ServiceLockScreen.sHaokanLockView.getParent();
        if (parent != null) {
            ((ViewGroup)parent).removeView(ServiceLockScreen.sHaokanLockView);
        }
        frameLayout.addView(ServiceLockScreen.sHaokanLockView);
    }

    /**
     * 设置状态栏和导航栏
     * <p>
     * 参考Android ApiDemos中的View - System UI Visibility - System UI Modes
     * 源码地址：https://github.com/android/platform_development/tree/master/samples/ApiDemos 具体类：SystemUIModes
     * 源码项目运营不了，我是用的模拟器自带的API Demos，对照着源码处理写的
     */
    private void hideNavigation() {
        super.onResume();
        int visibility = 0;
//        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//        visibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;//隐藏导航栏
        visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;//view获取焦点后导航栏别显示
        visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;//view全屏
        getWindow().getDecorView().setSystemUiVisibility(visibility);

        //设置状态栏透明
//        Window win = getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (true) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
    }

    private void  disableKeyGuard(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

//        KeyguardManager.KeyguardLock mKeyguardLock;
//        KeyguardManager km = (KeyguardManager) this.getApplication().getSystemService(Context.KEYGUARD_SERVICE);
//        mKeyguardLock = km.newKeyguardLock("keyguard");
//        mKeyguardLock.disableKeyguard();
    }


    @Override
    public void onClick(View v) {
    }


    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        if (ServiceLockScreen.sHaokanLockView != null) {
            ServiceLockScreen.sHaokanLockView.setActivity(null);
            ViewParent parent = ServiceLockScreen.sHaokanLockView.getParent();
            if (parent != null) {
                ((ViewGroup)parent).removeView(ServiceLockScreen.sHaokanLockView);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        hideNavigation();
    }
}