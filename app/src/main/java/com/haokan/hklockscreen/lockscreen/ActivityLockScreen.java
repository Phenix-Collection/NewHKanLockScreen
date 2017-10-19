package com.haokan.hklockscreen.lockscreen;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.util.LogHelper;
import com.haokan.pubic.util.StatusBarUtil;

/**
 * Created by wangzixu on 2017/3/2.
 */
public class ActivityLockScreen extends ActivityBase implements View.OnClickListener, View.OnSystemUiVisibilityChangeListener, CV_DetailPage_LockScreen.OnLockScreenStateChangeListener {
    private View mRecommendPage;
    private ScrollView mScrollView;
    private View mRootView;
    private FrameLayout mLockScreenLayout;
    private int mScreenH;
    private boolean mIsRecommendPage;

    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableKeyGuard();
        StatusBarUtil.setStatusBarTransparnet(this);
        setContentView(R.layout.activity_lock);
        hideNavigation();
        disableKeyGuard();
        mScreenH = getResources().getDisplayMetrics().heightPixels;

        initView();
        initLockScreenView();
        initRecommendPageView();
    }

    private void initView() {
        LogHelper.d("wangzixu", "ActivityLockScreen onCreate");
        mScrollView = (ScrollView) findViewById(R.id.scrollview);
        mRootView = findViewById(R.id.rootview);

        final ViewConfiguration configuration = ViewConfiguration.get(this);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }

                if (action == MotionEvent.ACTION_DOWN) {
                    mVelocityTracker.clear();
                    if (mVelocityTracker != null) {
                        mVelocityTracker.addMovement(event);
                    }
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    int scrollY = mScrollView.getScrollY();
                    if (mVelocityTracker != null) {
                        mVelocityTracker.addMovement(event);
                    }

                    if (scrollY > 0 && scrollY < mScreenH) {
                        mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                        int initialVelocity = (int) mVelocityTracker.getYVelocity(); //计算抬手时速度, 向上滑小于0, 向下滑大于0
                        LogHelper.d("wangzixu", "ActivityLockScreen initialVelocity = " + initialVelocity);

                        if (mIsRecommendPage) {
                            if (initialVelocity > 1000 || scrollY < mScreenH*3/4) { //下滑fling, 或者下滑过1/4
                                mScrollView.smoothScrollTo(0, 0);
                                mIsRecommendPage = false;
                            } else {
                                mScrollView.smoothScrollTo(0, mScreenH);
                                mIsRecommendPage = true;
                            }
                        } else {
                            if (initialVelocity < -1000 || scrollY > mScreenH / 4) {//上滑fling, 或者上滑过1/4
                                mScrollView.smoothScrollTo(0, mScreenH);
                                mIsRecommendPage = true;
                            } else {
                                mScrollView.smoothScrollTo(0, 0);
                                mIsRecommendPage = false;
                            }
                        }
                        return true;
                    }
                } else {
                    if (mVelocityTracker != null) {
                        mVelocityTracker.addMovement(event);
                    }
                }
                return false;
            }
        });
    }

    private void initLockScreenView() {
        mLockScreenLayout = (FrameLayout) findViewById(R.id.lockscreen_content);

        if (ServiceLockScreen.sHaokanLockView == null) {
            ServiceLockScreen.sHaokanLockView = new CV_DetailPage_LockScreen(this.getApplicationContext());
        }
        ServiceLockScreen.sHaokanLockView.setActivity(this);
        ServiceLockScreen.sHaokanLockView.setOnLockScreenStateListener(this);
        ViewParent parent = ServiceLockScreen.sHaokanLockView.getParent();
        if (parent != null) {
            ((ViewGroup)parent).removeView(ServiceLockScreen.sHaokanLockView);
        }
        mLockScreenLayout.addView(ServiceLockScreen.sHaokanLockView);
        ServiceLockScreen.sHaokanLockView.intoLockScreenState(true);


        ViewGroup.LayoutParams params = ServiceLockScreen.sHaokanLockView.getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenH);
        }
        params.height = mScreenH;
        ServiceLockScreen.sHaokanLockView.setLayoutParams(params);
    }

    private void initRecommendPageView() {
        mRecommendPage = findViewById(R.id.cv_recommendpage);

        ViewGroup.LayoutParams params = mRecommendPage.getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenH);
        }
        params.height = mScreenH;
        mRecommendPage.setLayoutParams(params);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogHelper.d("wangzixu", "ActivityLockScreen onNewIntent");
        ServiceLockScreen.sHaokanLockView.intoLockScreenState(true);
    }

    //控制向上滑动的逻辑begin
    private float mDownY;
    private boolean mIsScroll;
    private boolean mIntercepe;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ServiceLockScreen.sHaokanLockView.isLocked()) {
            return mLockScreenLayout.dispatchTouchEvent(ev);
        } else {
//            int action = ev.getAction();
//            float y = ev.getY();
//            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    mDownY = y;
//                    mIntercepe = mRootView.dispatchTouchEvent(ev);
//                    return mIntercepe;
//                case MotionEvent.ACTION_MOVE:
//                    float deltaY = mDownY - y;
//                    if (deltaY > 20 || mIsScroll) {
//                        mIsScroll = true;
//                        deltaY = deltaY - 15;
//                        mScrollView.scrollTo(0, (int) deltaY);
//                    }
//                    mIntercepe = mRootView.dispatchTouchEvent(ev);
//                    return mIntercepe;
//                case MotionEvent.ACTION_UP:
//                case MotionEvent.ACTION_CANCEL:
//                    if (mIsScroll) {
//                        mIsScroll = false;
//                        if (mUnLockImageView != null) { //防止当前的imageview响应长按事件
//                            mUnLockImageView.setStartLongClick(false);
//                        }
//
//                        int scrollY = mScrollView.getScrollY();
//                        if (scrollY > 100) {
//                            if (scrollY > mScreenH / 3) {
//                                mScrollView.smoothScrollTo(0, mScreenH);
//                            } else {
//                                mScrollView.smoothScrollTo(0, 0);
//                            }
//                        } else {
//                            mScrollView.scrollTo(0, 0);
//                        }
//                    }
//                    break;
//                default:
//                    break;
//            }
            return mScrollView.dispatchTouchEvent(ev);
        }
    }
    //控制向上滑动的逻辑end

    CV_UnLockImageView mUnLockImageView;
    @Override
    public void onLockScreenStateChange(boolean isLock) {
        if (isLock) {
            mUnLockImageView = ServiceLockScreen.sHaokanLockView.getUnLockView();
        } else {
            promptRecommenAnim();
        }
    }

    public void promptRecommenAnim() {
        final ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(300);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(1);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int i = (int) animation.getAnimatedValue();
                mScrollView.setScrollY(i);
            }
        });

        App.sMainHanlder.post(new Runnable() {
            @Override
            public void run() {
                animator.start();
            }
        });
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
            ServiceLockScreen.sHaokanLockView.setOnLockScreenStateListener(null);
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