package com.haokan.hklockscreen.lockscreen;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreenautoupdateimage.ServiceAutoUpdateImage;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.bean.BigImageBean;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.maidian.MaidianManager;
import com.haokan.pubic.maidian.UmengMaiDianManager;
import com.haokan.pubic.util.DisplayUtil;
import com.haokan.pubic.util.MyDateTimeUtil;
import com.haokan.pubic.util.StatusBarUtil;

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
    private View mGustureView;
    private static boolean sLockguideDown = true;
    private static boolean sLockguideUp = true;
    private static boolean sLockguideRL = true;
    private BroadcastReceiver mReceiver;

    //下拉刷新相关
    private boolean mPullToRefresh;
    private TextView mPullTvSwitch;
    private View mPullLayout;
    private ImageView mPullIvArraw;
    private int mPullRefreshDistence;
    private float mDownY;
    private double mOldPowY;
    private int mPullInitTranY; //下拉刷新布局初始的偏移量

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarTransparnet(this);
        hideNavigation();
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);
        setContentView(R.layout.activity_lockscreen);

        disableKeyGuard();
//        mScreenH = getResources().getDisplayMetrics().heightPixels;

        Point point = DisplayUtil.getRealScreenPoint(this);
        mScreenW = point.x;
        mScreenH = point.y;

        initView();
        initRecommendPageView();
        initLockScreenView();

        //监听home键, 关闭锁屏
        final IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String reason = intent.getStringExtra("reason");
                LogHelper.d("wangzixu", "homehome onReceive intent = " + intent.getAction()
                        + ", reason = " + reason);
                //recentapps最近任务, homekey  home键
                if (!TextUtils.isEmpty(reason) && reason.equals("homekey")) {
                    App.sMainHanlder.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            if (App.sHaokanLockView != null) {
                                App.sHaokanLockView.mIsLocked = false;
                            }
                            UmengMaiDianManager.onEvent(ActivityLockScreen.this, "event_065");
                        }
                    }, 0);
                }
            }
        };
        registerReceiver(mReceiver, filter);

        LogHelper.d("wangzixu", "ActivityLockScreen onCreate " + this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogHelper.d("wangzixu", "ActivityLockScreen onNewIntent " + this);
        App.sHaokanLockView.intoLockScreenState(true);
        mScrollView.scrollTo(0,0);

        if (mIsRecommendPage == true) {
            mLockRecommendPage.onHide();
            mIsRecommendPage = false;

            UmengMaiDianManager.onEvent(ActivityLockScreen.this, "event_080");
        } else {
            UmengMaiDianManager.onEvent(ActivityLockScreen.this, "event_075");
        }

        App.sHaokanLockView.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideNavigation();
            }
        }, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.sMainHanlder.postDelayed(mEventRun, 1500);
        if (mIsRecommendPage) {
            if (mLockRecommendPage != null) {
                mLockRecommendPage.onResume();
            }
        } else {
            App.sHaokanLockView.onResume();

            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String time = preferences.getString(ServiceAutoUpdateImage.KEY_AUTOUPDATA_TIME, "----");
            String curTime = MyDateTimeUtil.getCurrentSimpleData();
            if (time.equals(curTime)) {
                App.sHaokanLockView.setUpdateSign(0);
            } else {
                App.sHaokanLockView.setUpdateSign(1);
            }

            if (sLockguideRL) {
                sLockguideRL = preferences.getBoolean("lockguiderl", true);
                if (sLockguideRL) {
                    mGustureView = findViewById(R.id.gusture_rl);
                    mGustureView.setVisibility(View.GONE);
                    mGustureView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mGustureView.setOnClickListener(null);
                            mGustureView.setVisibility(View.GONE);
                            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ActivityLockScreen.this).edit();
                            edit.putBoolean("lockguiderl", false).apply();
                            sLockguideRL = false;
                            mGustureView = null;
                        }
                    });
                    App.sMainHanlder.postDelayed(mShowGestureRun, 700);
                }
            }
        }
    }

    Runnable mShowGestureRun = new Runnable() {
        @Override
        public void run() {
            showGestureGudieRl();
        }
    };

    Runnable mEventRun = new Runnable() {
        @Override
        public void run() {
            if (mIsRecommendPage) {
                UmengMaiDianManager.onEvent(ActivityLockScreen.this, "event_076");
            } else {
                UmengMaiDianManager.onEvent(ActivityLockScreen.this, "event_063");
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LogHelper.d("wangzixu", "ActivityLockScreen onPause " + this);
        App.sMainHanlder.removeCallbacks(mShowGestureRun);
        App.sMainHanlder.removeCallbacks(mEventRun);
        mGustureView = null;
    }

    private void initView() {
        mScrollView = (CV_ScrollView) findViewById(R.id.scrollview);
        mRootView = findViewById(R.id.rootview);
//        mScrollView.setMyOnScrollChangeListener(new CV_ScrollView.MyOnScrollChangeListener() {
//            @Override
//            public void onScrollChange(int scrollX, int scrollY, int oldX, int oldY) {
//                LogHelper.d("wangzixu", "ActivityLockScreen myOnScrollChange oldY = " + oldY + ", scrollY = " + scrollY);
//            }
//        });

        final ViewConfiguration configuration = ViewConfiguration.get(this);
//        mTouchSlop = configuration.getScaledTouchSlop();
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        //下拉刷新相关begin
        mPullLayout = findViewById(R.id.pullrefreshlayout);
        mPullTvSwitch = (TextView) findViewById(R.id.tv_switch);
        mPullIvArraw = (ImageView) findViewById(R.id.arrow);
        mPullInitTranY = -DisplayUtil.dip2px(this, 25);
        mPullLayout.setTranslationY(mPullInitTranY);

        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(150);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(150);
        mReverseFlipAnimation.setFillAfter(true);

        mPullRefreshDistence = DisplayUtil.dip2px(this, 70);
        //下拉刷新相关end
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

        if (params.height != mScreenH) {
            params.height = mScreenH;
            App.sHaokanLockView.setLayoutParams(params);
        }
        App.sHaokanLockView.requestFocus();
        mScrollView.scrollTo(0,0);
    }

    private void initRecommendPageView() {
        mLockRecommendPage = (CV_RecommendPage_LockScreen) findViewById(R.id.cv_recommendpage);
        mLockRecommendPage.setActivityBase(this);
        mIsRecommendPage = false;
        mLockRecommendPage.onHide();
//        mLockRecommendPage.setTypeName("美女");

        ViewGroup.LayoutParams params = mLockRecommendPage.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenH);
        }
        params.height = mScreenH;
        mLockRecommendPage.setLayoutParams(params);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            return myHandDispatchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean myHandDispatchEvent(MotionEvent event) {
        if (mGustureView != null) {
            if (mGustureView.getVisibility() != View.VISIBLE) {
                return true;
            } else {
                return super.dispatchTouchEvent(event);
            }
        }
        if (mIsAniming || mScrollView.isScrolling()) {
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
                mPullToRefresh = false;
                mDownY = mLastY;

                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (App.sHaokanLockView.isShowLongClickLayout()) { //如果当前显示出了长按的对话框, 不响应事件
                    return true;
                }

                float x = event.getX();
                float y = event.getY();
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(event);

                if (scrollY < mScreenH) {
                    if (scrollY > 0 || mScrollViewMove) { //2在推荐和锁屏页之间, 拦截掉事件, 自己处理
                        mScrollViewMove = true;
                        mScrollView.scrollBy(0, (int) (mLastY - y));

                        if (mScrollView.getScrollY() < 0) {
                            mScrollView.scrollTo(0, 0);
//                            mScrollViewMove = false;
//                            mPullToRefresh = true;
                        } else if (mScrollView.getScrollY() > mScreenH) {
                            mScrollView.scrollTo(0, mScreenH);
                        }

                        mLastY = y;
                        return true;
                    }

                    if (mPullToRefresh) { //下拉刷新
                        float deltaY = y - mDownY;

                        if (deltaY > 0) {
                            double pow = Math.pow(deltaY, 0.85d);
//                            LogHelper.d("wangzixu", "pullrefresh mTransLateY = " + mTransLateY + ", pow = " + pow);

                            if (pow > mPullRefreshDistence && mOldPowY <= mPullRefreshDistence) {
                                onRefreshState3();
                            } else if (pow <= mPullRefreshDistence && mOldPowY > mPullRefreshDistence) {
                                onRefreshState2();
                            }
//                            mScrollView.setTranslationY((float) pow);
                            mPullLayout.setTranslationY((float) (mPullInitTranY + pow));
                            mOldPowY = pow;
                        }

                        mLastY = y;
                        return true;
                    }

                    //此时在锁屏界面, 且不知道用户意图
                    float absX = Math.abs(mLastX - x);
                    float absY = Math.abs(mLastY - y);
                    float deltaY = mLastY - y; //只有向上划, 才相应滑动scrollview的事件, 其他的事件都交由viewpager去处理
                    if (absY > 2*absX && absY > mTouchSlop) {
                        if (deltaY > 0) {
                            mScrollView.scrollBy(0, 1);
                            mScrollViewMove = true;
                            mLastY = y;
                            return true;
                        } else {
                            mPullToRefresh = true;
                            mLastY = y;
                            onRefreshState1();
                            return true;
                        }
                    }
                } else {
                    mLastY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(event);

                //如果当前在推荐和锁屏页中间, 拦截掉事件, 自己处理. 其他情况都交由系统处理
                if (scrollY > 0 && scrollY < mScreenH) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = (int) mVelocityTracker.getYVelocity(); //计算抬手时速度, 向上滑小于0, 向下滑大于0
                    LogHelper.d("wangzixu", "ActivityLockScreen recyclerView initialVelocity = " + initialVelocity);

                    if (mIsRecommendPage) {
                        if (initialVelocity > 1000
//                                || scrollY < mScreenH*3/4
                                ) { //下滑fling, 或者下滑过1/4
                            mScrollView.myScrollTo(0, 0, 400);
//                            mScrollView.smoothScrollTo(0, 0);
                            mIsRecommendPage = false;
                            mLockRecommendPage.onHide();

                            UmengMaiDianManager.onEvent(ActivityLockScreen.this, "event_077");
                            UmengMaiDianManager.onEvent(ActivityLockScreen.this, "event_063");
                        } else {
                            mScrollView.myScrollTo(0, mScreenH, 400);
//                            mScrollView.smoothScrollTo(0, mScreenH);
                            mIsRecommendPage = true;
                        }
                    } else {
                        if (initialVelocity < -1000
//                                || scrollY > mScreenH / 4
                                ) {//上滑fling, 或者上滑过1/4
                            mScrollView.myScrollTo(0, mScreenH, 400);
//                            mScrollView.smoothScrollTo(0, mScreenH);
                            mIsRecommendPage = true;
                            mLockRecommendPage.onShow();

                            UmengMaiDianManager.onEvent(ActivityLockScreen.this, "event_076");
                            UmengMaiDianManager.onEvent(ActivityLockScreen.this, "event_073");

                            BigImageBean bean = App.sHaokanLockView.getCurrentImageBean();
                            if (bean != null) {
                                if (TextUtils.isEmpty(bean.typeName)) {
                                    bean.typeName = "娱乐"; //默认分类
                                }

                                mLockRecommendPage.loadDataForLockScreen(bean.typeName);
                            }
                        } else {
                            mScrollView.myScrollTo(0, 0, 400);
//                            mScrollView.smoothScrollTo(0, 0);
                            mIsRecommendPage = false;
                        }
                    }
                }

                final float translationY = mPullLayout.getTranslationY();
                if (translationY != mPullInitTranY) { //下拉刷新时抬手
                    final float v = translationY - mPullInitTranY;
                    mIsAniming = true;

                    ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);
                    animator.setDuration(300);
                    animator.setInterpolator(new DecelerateInterpolator());
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float f = (float) animation.getAnimatedValue();
                            mPullLayout.setAlpha(1.0f - f);
                            mPullLayout.setTranslationY(translationY-v*f);
                        }
                    });

                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (translationY-mPullInitTranY > mPullRefreshDistence) {
                                onRefresh();
                            }
                            mPullLayout.setAlpha(1.0f);
                            mPullIvArraw.clearAnimation();
                            mPullTvSwitch.setText("下拉换一换");
                            mIsAniming = false;
                        }
                    });
                    animator.start();
                } else {
//                    mPullIvArraw.clearAnimation();
//                    mPullTvSwitch.setText("下拉换一换");
//                    mPullLayout.setAlpha(1.0f);
                }

                if (mScrollViewMove || mPullToRefresh) {
                    mScrollViewMove = false;
                    mPullToRefresh = false;
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
    protected RotateAnimation mFlipAnimation;
    protected RotateAnimation mReverseFlipAnimation;

    //开始下拉
    public void onRefreshState1() {
        mPullLayout.setVisibility(View.VISIBLE);
        mPullTvSwitch.setText("下拉换一换");
        mPullIvArraw.clearAnimation();
    }

    //由松手换一换到下拉换一换
    public void onRefreshState2() {
        mPullTvSwitch.setText("下拉换一换");
        mPullIvArraw.clearAnimation();
        mPullIvArraw.startAnimation(mReverseFlipAnimation);
    }

    //下拉换一换到松手换一换
    public void onRefreshState3() {
        mPullTvSwitch.setText("松手换一波新图");
        mPullIvArraw.clearAnimation();
        mPullIvArraw.startAnimation(mFlipAnimation);
    }

    //刷新
    public void onRefresh() {
        App.sHaokanLockView.pullToSwitch(false);

        UmengMaiDianManager.onEvent(this, "event_102");
        MaidianManager.setAction(this, "0", 15, "2");
    }

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

    public void showGustureGuideDown() {
        if (sLockguideDown) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ActivityLockScreen.this);
            sLockguideDown = preferences.getBoolean("lockguidedown", true);
//            sLockguideDown = true;
            if (sLockguideDown) {
                App.sMainHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mGustureView = findViewById(R.id.gusture_down);
                        mGustureView.setVisibility(View.VISIBLE);
                        mGustureView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mGustureView.setVisibility(View.GONE);
                                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ActivityLockScreen.this).edit();
                                edit.putBoolean("lockguidedown", false).apply();
                                sLockguideDown = false;
                                mGustureView = null;
                            }
                        });
                    }
                }, 200);
            }
        }
    }

    public void showGustureGuideUP() {
        mGustureView = findViewById(R.id.gusture_up);
        mGustureView.setVisibility(View.VISIBLE);
        mGustureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGustureView.setVisibility(View.GONE);
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ActivityLockScreen.this).edit();
                edit.putBoolean("lockguideup", false).apply();
                sLockguideUp = false;
                mGustureView = null;
            }
        });
    }

    public void showGestureGudieRl() {
        if (mGustureView == null) {
            mGustureView = findViewById(R.id.gusture_rl);
            mGustureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGustureView.setOnClickListener(null);
                    mGustureView.setVisibility(View.GONE);
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ActivityLockScreen.this).edit();
                    edit.putBoolean("lockguiderl", false).apply();
                    sLockguideRL = false;
                    mGustureView = null;
                }
            });
        }
        mGustureView.setVisibility(View.VISIBLE);
    }

    public void backToDetailPage() {
        LogHelper.d("wangzixu", "backToDetailPage call");
        mScrollView.smoothScrollTo(0, 0);
        mIsRecommendPage = false;
        mLockRecommendPage.onHide();
    }

    public void backToLockScreenPage() {
        App.sHaokanLockView.intoLockScreenState(false);
        backToDetailPage();
    }

    private boolean mIsAniming;
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
                mIsAniming = false;

                if (sLockguideUp) {
                    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ActivityLockScreen.this);
                    sLockguideUp = preferences.getBoolean("lockguideup", true);
                    if (sLockguideUp) {
                        App.sMainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                showGustureGuideUP();
                            }
                        });
                    }
                }
            }
        });

        App.sMainHanlder.post(new Runnable() {
            @Override
            public void run() {
                mIsAniming = true;
                animator.start();
            }
        });
    }

    private void  disableKeyGuard(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

//        KeyguardManager km = (KeyguardManager) this.getApplication().getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock mKeyguardLock = km.newKeyguardLock("keyguard");
//        mKeyguardLock.disableKeyguard();
        //| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED); //在系统锁屏之上显示
    }


    @Override
    public void onClick(View v) {
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (mIsRecommendPage) {
            mLockRecommendPage.clickBack();
        } else {
            App.sHaokanLockView.onClickBack();
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
    public void hideNavigation() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int visibility = decorView.getSystemUiVisibility();
        visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;//隐藏导航栏
//        visibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;//隐藏导航栏
        visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;//view获取焦点后导航栏不显示. 边缘向内化导航栏一直显示, 出发listenrer
//        visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;//view获取焦点后导航栏不显示. 边缘向内化导航栏暂时显示, 不触发listener
        visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;//view全屏
        decorView.setSystemUiVisibility(visibility);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        hideNavigation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogHelper.d("wangzixu", "ActivityLockScreen onActivityResult requestCode "  + requestCode + ", resultCode = " + resultCode);
        if (requestCode == 201 && resultCode == RESULT_OK) { //锁屏上打开设置页, 并且手动点击了返回
            finish();
        }
    }

    @Override
    public void finish() {
        try {
            super.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        LogHelper.d("wangzixu", "ActivityLockScreen onDestroy "  + this);
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (mLockRecommendPage != null) {
            mLockRecommendPage.onDestory();
        }
        if (App.sHaokanLockView != null && App.sHaokanLockView.getParent() != null && App.sHaokanLockView.getParent() == mLockScreenLayout) {
//            App.sHaokanLockView.setActivity(null);
//            App.sHaokanLockView.setOnLockScreenStateListener(null);
            ViewParent parent = App.sHaokanLockView.getParent();
            ((ViewGroup)parent).removeView(App.sHaokanLockView);
        }
        super.onDestroy();
    }
}