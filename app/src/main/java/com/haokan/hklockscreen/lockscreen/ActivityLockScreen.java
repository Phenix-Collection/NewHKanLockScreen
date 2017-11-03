package com.haokan.hklockscreen.lockscreen;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.DisplayUtil;
import com.haokan.pubic.util.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by wangzixu on 2017/3/2.
 */
public class ActivityLockScreen extends ActivityBase implements View.OnClickListener, View.OnSystemUiVisibilityChangeListener, CV_DetailPage_LockScreen.OnLockScreenStateChangeListener {
    private CV_RecommendPage_LockScreen mLockRecommendPage;
    private CV_ScrollView mScrollView;
    private View mRootView;
    private FrameLayout mLockScreenLayout;
    private int mScreenH;
    private int mScreenW;
    private boolean mIsRecommendPage;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;
    private float mLastX;
    private float mLastY;
    private boolean mScrollViewMove; //是够控制scrollview移动过了, 如果移动过了, up事件应该直接返回, 否则会相应点击事件(解决bug:上划显示推荐页后, 再下滑到底, 相应点击事件)


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableKeyGuard();
        StatusBarUtil.setStatusBarTransparnet(this);
        setContentView(R.layout.activity_lockscreen);
        LogHelper.d("wangzixu", "ActivityLockScreen onCreate");

        disableKeyGuard();
//        mScreenH = getResources().getDisplayMetrics().heightPixels;

        Point point = DisplayUtil.getRealScreenPoint(this);
        mScreenW = point.x;
        mScreenH = point.y;

        initView();
        initLockScreenView();
        initRecommendPageView();

        checkStoragePermission();

        hideNavigation();
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);


    }

    public void showGestureGudie() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsRecommendPage) {
            MobclickAgent.onEvent(this, "recommend_show"); //推荐页show
        } else {
            MobclickAgent.onEvent(this, "lockscreen_show"); //锁屏页show
        }

        //为了处理一个bug --- 锁屏view不触发layout, 第一帧不绘制
        App.sMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(1, 1);
                mScrollView.scrollTo(0, 0);
            }
        }, 200);
    }

    private void initView() {
        mScrollView = (CV_ScrollView) findViewById(R.id.scrollview);
        mRootView = findViewById(R.id.rootview);

        final ViewConfiguration configuration = ViewConfiguration.get(this);
//        mTouchSlop = configuration.getScaledTouchSlop();
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    private void initLockScreenView() {
        mLockScreenLayout = (FrameLayout) findViewById(R.id.lockscreen_content);

        if (App.sHaokanLockView == null) {
            App.sHaokanLockView = new CV_DetailPage_LockScreen(this.getApplicationContext());
        }
        Intent i = new Intent(this, ServiceLockScreen.class);
        startService(i);

        App.sHaokanLockView.setActivity(this);
        App.sHaokanLockView.setOnLockScreenStateListener(this);
        ViewParent parent = App.sHaokanLockView.getParent();
        if (parent != null) {
            ((ViewGroup)parent).removeView(App.sHaokanLockView);
        }
        mLockScreenLayout.addView(App.sHaokanLockView);

        App.sHaokanLockView.intoLockScreenState(true);

        ViewGroup.LayoutParams params = App.sHaokanLockView.getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenH);
        }
        params.height = mScreenH;
        App.sHaokanLockView.setLayoutParams(params);
    }

    private void initRecommendPageView() {
        mLockRecommendPage = (CV_RecommendPage_LockScreen) findViewById(R.id.cv_recommendpage);
        mLockRecommendPage.setActivityBase(this);
        mLockRecommendPage.onHide();
        mIsRecommendPage = false;
//        mLockRecommendPage.setTypeName("美女");

        ViewGroup.LayoutParams params = mLockRecommendPage.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenH);
        }
        params.height = mScreenH;
        mLockRecommendPage.setLayoutParams(params);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogHelper.d("wangzixu", "ActivityLockScreen onNewIntent");
        App.sHaokanLockView.intoLockScreenState(true);
        mScrollView.scrollTo(0,0);
        mLockRecommendPage.onHide();
        mIsRecommendPage = false;

        hideNavigation();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mIsAnimingPrompt) {
            return true;
        }

        if (App.sHaokanLockView.isLocked()) { //锁屏界面, 直接解锁
            return mLockScreenLayout.dispatchTouchEvent(event);
        }

        int action = event.getActionMasked();
        int scrollY = mScrollView.getScrollY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();

                mScrollViewMove = false;

                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.clear();
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (App.sHaokanLockView.isShowLongClickLayout()) { //如果当前显示出了长按的对话框, 不响应事件
                    return true;
                }

                float x = event.getX();
                float y = event.getY();
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(event);
                }

                if (scrollY < mScreenH) {
                    if (scrollY > 0 || mScrollViewMove) { //2在推荐和锁屏页之间, 拦截掉事件, 自己处理
                        mScrollViewMove = true;
                        mScrollView.scrollBy(0, (int) (mLastY - y));
                        if (mScrollView.getScrollY() < 0) {
                            mScrollView.scrollTo(0,0);
                        } else if (mScrollView.getScrollY() > mScreenH) {
                            mScrollView.scrollTo(0, mScreenH);
                        }
                        mLastY = y;
                        return true;
                    }

                    //此时在锁屏界面, 且不知道用户意图
                    float absX = Math.abs(mLastX - x);
    //                    float absY = Math.abs(mLastY - y);
                    float absY = mLastY - y; //只有向上划, 才相应滑动scrollview的事件, 其他的事件都交由viewpager去处理
                    if (absY > mTouchSlop && absY > absX) {
                        mScrollView.scrollBy(0, 1);
                        mScrollViewMove = true;
                        mLastY = y;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(event);
                }

                //如果当前在推荐和锁屏页中间, 拦截掉事件, 自己处理. 其他情况都交由系统处理
                if (scrollY > 0 && scrollY < mScreenH) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = (int) mVelocityTracker.getYVelocity(); //计算抬手时速度, 向上滑小于0, 向下滑大于0
                    LogHelper.d("wangzixu", "ActivityLockScreen recyclerView initialVelocity = " + initialVelocity);

                    if (mIsRecommendPage) {
                        if (initialVelocity > 1000
//                                || scrollY < mScreenH*3/4
                                ) { //下滑fling, 或者下滑过1/4
//                            mScrollView.myScrollTo(0, 0, 350);
                            mScrollView.smoothScrollTo(0, 0);
                            mIsRecommendPage = false;
                            mLockRecommendPage.onHide();

                            MobclickAgent.onEvent(this, "lockscreen_show"); //锁屏页show
                        } else {
//                            mScrollView.myScrollTo(0, mScreenH, 350);
                            mScrollView.smoothScrollTo(0, mScreenH);
                            mIsRecommendPage = true;
                        }
                    } else {
                        if (initialVelocity < -1000
//                                || scrollY > mScreenH / 4
                                ) {//上滑fling, 或者上滑过1/4
//                            mScrollView.myScrollTo(0, mScreenH, 350);
                            mScrollView.smoothScrollTo(0, mScreenH);
                            mIsRecommendPage = true;
                            mLockRecommendPage.onShow();

                            MobclickAgent.onEvent(this, "lockscreen_recommend"); //锁屏页进入推荐
                            MobclickAgent.onEvent(this, "recommend_show"); //推荐页show

                            MainImageBean bean = App.sHaokanLockView.getCurrentImageBean();
                            if (bean != null) {
                                mLockRecommendPage.refreshIfChangeType(bean.typeName);
                            }
                        } else {
//                            mScrollView.myScrollTo(0, 0, 350);
                            mScrollView.smoothScrollTo(0, 0);
                            mIsRecommendPage = false;
                        }
                    }
                }

                if (mScrollViewMove) {
                    mScrollViewMove = false;
                    event.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
            default:
                break;
        }
        if (scrollY < mScreenH) {
            return mLockScreenLayout.dispatchTouchEvent(event);
        } else {
            return mScrollView.dispatchTouchEvent(event);
        }
    }
    //控制向上滑动的逻辑end

    CV_UnLockImageView mUnLockImageView;
    @Override
    public void onLockScreenStateChange(boolean isLock) {
        if (isLock) {
            mUnLockImageView = App.sHaokanLockView.getUnLockView();
        } else {
            App.sMainHanlder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    promptRecommenAnim();
                }
            }, 250);
        }
    }

    public void backToDetailPage() {
        mScrollView.smoothScrollTo(0, 0);
        mIsRecommendPage = false;
        mLockRecommendPage.onHide();
    }

    public void backToLockScreenPage() {
        App.sHaokanLockView.intoLockScreenState(false);
        backToDetailPage();
    }

    private boolean mIsAnimingPrompt;
    public void promptRecommenAnim() {
        final ValueAnimator animator = ValueAnimator.ofInt(0, DisplayUtil.dip2px(this, 40));
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

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimingPrompt = false;
            }
        });

        App.sMainHanlder.post(new Runnable() {
            @Override
            public void run() {
                mIsAnimingPrompt = true;
                animator.start();
            }
        });
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

    /**
     * 屏蔽掉返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK || keyCode==KeyEvent.KEYCODE_HOME){
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
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
//                UpdateManager.checkUpdate(this, false);
            }
        } else {
//            UpdateManager.checkUpdate(this, false);;
        }
    }

    //检查权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 201:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //同意
//                        UpdateManager.checkUpdate(this, false);
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


    //隐藏导航栏相关
    /**
     * 设置状态栏和导航栏
     * <p>
     * 参考Android ApiDemos中的View - System UI Visibility - System UI Modes
     * 源码地址：https://github.com/android/platform_development/tree/master/samples/ApiDemos 具体类：SystemUIModes
     * 源码项目运营不了，我是用的模拟器自带的API Demos，对照着源码处理写的
     */
    private void hideNavigation() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int visibility = decorView.getSystemUiVisibility();
        visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;//隐藏导航栏
//        visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;//view获取焦点后导航栏不显示. 边缘向内化导航栏一直显示, 出发listenrer
        visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;//view获取焦点后导航栏不显示. 边缘向内化导航栏暂时显示, 不触发listener
        visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;//view全屏
        decorView.setSystemUiVisibility(visibility);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        hideNavigation();
    }

    @Override
    protected void onDestroy() {
        LogHelper.d("wangzixu", "ActivityLockScreen onDestroy");
        if (App.sHaokanLockView != null) {
            App.sHaokanLockView.setActivity(null);
            App.sHaokanLockView.setOnLockScreenStateListener(null);
            ViewParent parent = App.sHaokanLockView.getParent();
            if (parent != null) {
                ((ViewGroup)parent).removeView(App.sHaokanLockView);
            }
        }
        super.onDestroy();
    }
}