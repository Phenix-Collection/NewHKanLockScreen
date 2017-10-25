package com.haokan.hklockscreen.lockscreeninitset;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.provider.Settings;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreen.ActivityLockScreen;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.util.LogHelper;
import com.haokan.pubic.util.ToastManager;

/**
 * Created by wangzixu on 2017/10/23.
 */
public class CV_LockInitSetView extends FrameLayout implements View.OnClickListener {
    private Context mContext;
    private ActivityBase mActivityBase;
    private CV_ScanRadarView mRadarView;
    private View mCryTvLayout;
    private View mCheckTvLayout;
    private ImageView mIvCryLaugh;
    private View mManulSetLayout;
    private View mTvAutoStartSet;
    private View mTvManualSet;
    private View mLoadingLayout;
    private View mAutoCompleteLayout;
    private boolean mHasSetAutoStart;
    private TextView mTvSkip;
    public static boolean sIsAutoSet = false; //是否正在用辅助功能自动设置
    public static boolean sAutoSuccess = false; //是否自动设置成功了
    private ImageView mIvGear;
    private TextView mTvPercent;

    public CV_LockInitSetView(@NonNull Context context) {
        this(context, null);
    }

    public CV_LockInitSetView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_LockInitSetView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.cv_lockinit_view, this, true);

        mRadarView = (CV_ScanRadarView) findViewById(R.id.radarview);
        startScanAnim();

        mLoadingLayout = findViewById(R.id.layout_loading);
        mIvGear = (ImageView) mLoadingLayout.findViewById(R.id.iv_gear);
        mTvPercent = (TextView) findViewById(R.id.tv_percent);

        mCryTvLayout = findViewById(R.id.crytvlayout);
        mCheckTvLayout = findViewById(R.id.checktitlelayout);
        mIvCryLaugh = (ImageView) findViewById(R.id.iv_cry_laugh);
        mTvSkip = (TextView) findViewById(R.id.tv_skip);

        mAutoCompleteLayout = findViewById(R.id.autosetcompletelayout);
        mAutoCompleteLayout.findViewById(R.id.startlock).setOnClickListener(this);

        mTvSkip.setOnClickListener(this);
        findViewById(R.id.tvaccessset).setOnClickListener(this);
        App.sMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAccessibilitySettingsOn(mContext)) {
                    sIsAutoSet = true;

                    mLoadingLayout.setVisibility(VISIBLE);
                    AnimationDrawable animationDrawable = (AnimationDrawable) mIvGear.getDrawable();
                    animationDrawable.start();

                    mIvCryLaugh.setVisibility(GONE);
                    mCryTvLayout.setVisibility(GONE);
                    mRadarView.setRadar(false);
                    mRadarView.setVisibility(VISIBLE);
                    mRadarView.start();

                    App.sMainHanlder.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = SystemIntentUtil.getAutoStartIntent();
                            mActivityBase.startActivityForResult(intent, 103);
                            mActivityBase.startActivityAnim();
                        }
                    }, 500);
                } else {
                    mRadarView.stop();
                    mRadarView.setVisibility(GONE);
                    mCheckTvLayout.setVisibility(GONE);

                    mIvCryLaugh.setVisibility(VISIBLE);
                    mIvCryLaugh.setImageResource(R.drawable.icon_lockinit_cry);
                    mCryTvLayout.setVisibility(VISIBLE);
                }
            }
        }, 1500);
    }

    private void startScanAnim() {
        App.sMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRadarView.getWidth() == 0) {
                    startScanAnim();
                } else {
                    mRadarView.start();
                }
            }
        }, 30);
    }

    //检查辅助功能是否开启了
    private boolean isAccessibilitySettingsOn(Context mContext) {
        final String service = mContext.getPackageName() + "/" + ServiceMyAccessibility.class.getCanonicalName();
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

    public void setActivityBase(ActivityBase activityBase) {
        mActivityBase = activityBase;
    }

    @Override
    public void onClick(View v) {
        if (sIsAutoSet) {
            return;
        }
        switch (v.getId()) {
            case R.id.tvaccessset: //打开辅助功能按钮
                goAccessablityActivity();
                break;
            case R.id.autostartset:
                goAutoSetActivity();
                break;
            case R.id.tvmanualset:
                if (mHasSetAutoStart) {
                    Intent i = new Intent(mContext, ActivityLockScreen.class);
                    mActivityBase.startActivity(i);
                    mActivityBase.finish();
                    mActivityBase.startActivityAnim();
                }
                break;
            case R.id.startlock:
                if (mHasSetAutoStart) {
                    Intent i = new Intent(mContext, ActivityLockScreen.class);
                    mActivityBase.startActivity(i);
                    mActivityBase.finish();
                    mActivityBase.startActivityAnim();
                }
                break;
            case R.id.tv_skip:
                {
                    mActivityBase.finish();
                    mActivityBase.closeActivityAnim();
                }
                break;
            default:
                break;
        }
    }

    //进入辅助功能呢界面, 提示用户开启辅助功能
    public void goAccessablityActivity() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivityBase.startActivityForResult(intent, 101);
        mActivityBase.startActivityAnim();
        App.sMainHanlder.post(new Runnable() {
            @Override
            public void run() {
                Intent i2 = new Intent(mContext, ActivityPrompt_Accessablity.class);
                mActivityBase.startActivity(i2);
            }
        });
    }

    public void goAutoSetActivity() {
        Intent intent = SystemIntentUtil.getAutoStartIntent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
            mActivityBase.startActivityForResult(intent, 102);
            mActivityBase.startActivityAnim();
            App.sMainHanlder.post(new Runnable() {
                @Override
                public void run() {
                    Intent i2 = new Intent(mContext, ActivityPrompt_AutoStart.class);
                    mActivityBase.startActivity(i2);
                }
            });
        }catch (Exception e){//抛出异常就直接打开设置页面
            e.printStackTrace();
            ToastManager.showShort(mActivityBase, "没有找到自启动界面");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) { //辅助功能界面回来
            if (isAccessibilitySettingsOn(mContext)) {
                sIsAutoSet = true;

                mLoadingLayout.setVisibility(VISIBLE);
                AnimationDrawable animationDrawable = (AnimationDrawable) mIvGear.getDrawable();
                animationDrawable.start();

                mIvCryLaugh.setVisibility(GONE);
                mCryTvLayout.setVisibility(GONE);
                mRadarView.setRadar(false);
                mRadarView.setVisibility(VISIBLE);
                mRadarView.start();

                App.sMainHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = SystemIntentUtil.getAutoStartIntent();
                        mActivityBase.startActivityForResult(intent, 103);
                        mActivityBase.startActivityAnim();
                    }
                }, 500);
            } else {
                if (mManulSetLayout == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.manualsetlayout);
                    mManulSetLayout = viewStub.inflate();
                    mTvAutoStartSet = mManulSetLayout.findViewById(R.id.autostartset);
                    mTvManualSet = mManulSetLayout.findViewById(R.id.tvmanualset);
                    mTvAutoStartSet.setOnClickListener(this);
                    mTvManualSet.setOnClickListener(this);
                }
                mManulSetLayout.setVisibility(VISIBLE);
                mIvCryLaugh.setVisibility(GONE);
                mCryTvLayout.setVisibility(GONE);
            }
        } else if (requestCode == 102) { //手动开机启动
            sIsAutoSet = false;
            mHasSetAutoStart = true;
            mTvManualSet.setBackgroundResource(R.drawable.selector_lockinit_btnbg2);
        } else if (requestCode == 103) { //自动设置自启动回来
            sIsAutoSet = false;

            if (sAutoSuccess) { //自动设置成功回来
                mHasSetAutoStart = true;
                mTvPercent.setText("100%");
                mRadarView.stop();
                mRadarView.setVisibility(GONE);
                mLoadingLayout.setVisibility(GONE);
                AnimationDrawable animationDrawable = (AnimationDrawable) mIvGear.getDrawable();
                animationDrawable.stop();
                mIvCryLaugh.setImageResource(R.drawable.icon_lockinit_laugh);
                mIvCryLaugh.setVisibility(VISIBLE);
                mAutoCompleteLayout.setVisibility(VISIBLE);
            } else { //自动设置失败回来
                if (mManulSetLayout == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.manualsetlayout);
                    mManulSetLayout = viewStub.inflate();
                    mTvAutoStartSet = mManulSetLayout.findViewById(R.id.autostartset);
                    mTvManualSet = mManulSetLayout.findViewById(R.id.tvmanualset);
                    mTvAutoStartSet.setOnClickListener(this);
                    mTvManualSet.setOnClickListener(this);
                }
                mHasSetAutoStart = false;
                mManulSetLayout.setVisibility(VISIBLE);
                mRadarView.stop();
                mRadarView.setVisibility(GONE);
                mLoadingLayout.setVisibility(GONE);
                AnimationDrawable animationDrawable = (AnimationDrawable) mIvGear.getDrawable();
                animationDrawable.stop();
            }
        }
    }
}
