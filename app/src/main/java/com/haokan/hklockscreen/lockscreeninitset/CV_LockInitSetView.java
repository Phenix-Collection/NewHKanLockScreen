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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreen.ActivityLockScreen;
import com.haokan.hklockscreen.lockscreeninitset.phone1.ManualSetItems_1;
import com.haokan.hklockscreen.lockscreeninitset.phone2.ManualSetItems_2;
import com.haokan.hklockscreen.lockscreeninitset.phone3.ManualSetItems_3;
import com.haokan.hklockscreen.lockscreeninitset.phone4.ManualSetItems_4;
import com.haokan.hklockscreen.lockscreeninitset.phone5.ManualSetItems_5;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.maidian.UmengMaiDianManager;

/**
 * Created by wangzixu on 2017/10/23.
 */
public class CV_LockInitSetView extends FrameLayout implements View.OnClickListener {
    private Context mContext;
    private ActivityBase mActivityBase;
    private CV_ScanRadarView mScanLayoutRadarView;
    public static boolean sIsAutoSetting = false; //是否正在用辅助功能自动设置
    public static boolean sAutoSetSuccess = false; //是否自动设置成功了
    private ImageView mManulSetIvCryLaugh;
    private TextView mManulSetTvTitle;
    private View mScanLayout; //扫描界面
    private View mSetProgressLayout; //过程进度界面
    private View mSetAccessLayout; //设置辅助功能界面
    private View mSetSuccessLayout; //自动设置成功该界面
    private View mManulSetLayout; //手动设置界面
    private TextView mTvSetProgress;
    private View mManulSetTvStartLock;
    private CV_LockInit_ManualSetItemsBase mManualSetItemsLayout;

    /**
     * For 熊守义的需求, 初检失败还是成功.还是未适配
     * 这个变量用来记录刚开始检测到的是开启还是没有开启辅助功能,或者未适配
     * 没有开启时产品叫做"初检失败"<br/>
     * 1初检失败<br/>
     * 2初检成功<br/>
     * 3未适配自动设置
     */
    public static int sInitCheckStatus = 1;

    public CV_LockInitSetView(@NonNull Context context) {
        this(context, null);
    }

    public CV_LockInitSetView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_LockInitSetView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.cv_lockinit, this, true);

        initScanLayout();
        initProgressLayout();
        initSetAccessLayout();
        initSetSuccessLayout();
        initManualSetLayout();
    }

    private void initScanLayout() {
        mScanLayout = findViewById(R.id.scanlayout);
        mScanLayoutRadarView = (CV_ScanRadarView) mScanLayout.findViewById(R.id.radarview);
    }

    private void initProgressLayout() {
        mSetProgressLayout = findViewById(R.id.setprogresslayout);
        mTvSetProgress = (TextView) mSetProgressLayout.findViewById(R.id.tv_setprogress);

//        CV_ScanRadarView scanRadarView = (CV_ScanRadarView) mSetProgressLayout.findViewById(R.id.radarview);
//        scanRadarView.setRadar(false);
//        scanRadarView.start();
//
//        ImageView imageView = (ImageView) mSetProgressLayout.findViewById(R.id.iv_gear);
//        AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
//        drawable.start();
    }

    private void initSetAccessLayout() {
        mSetAccessLayout = findViewById(R.id.setaccesslayout);
        mSetAccessLayout.findViewById(R.id.tvsetaccess).setOnClickListener(this);
        mSetAccessLayout.findViewById(R.id.tv_skip).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengMaiDianManager.onEvent(mContext, "event_046", null);
                if (mSkipToLock) {
                    Intent intent1 = new Intent(mActivityBase, ActivityLockScreen.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mActivityBase.startActivity(intent1);
                    mActivityBase.finish();
                    mActivityBase.startActivityAnim();
                } else {
                    mActivityBase.finish();
                    mActivityBase.closeActivityAnim();
                }
            }
        });
    }

    private void initSetSuccessLayout() {
        mSetSuccessLayout = findViewById(R.id.setsuccesslayout);
        mSetSuccessLayout.findViewById(R.id.tvstartlock).setOnClickListener(this);
        mSetSuccessLayout.findViewById(R.id.tv_skip).setOnClickListener(this);
    }

    private void initManualSetLayout() {
        mManulSetLayout = findViewById(R.id.manualsetlayout);
        mManulSetIvCryLaugh = (ImageView) mManulSetLayout.findViewById(R.id.iv_cry_laugh);
        mManulSetTvTitle = (TextView) mManulSetLayout.findViewById(R.id.tv_title);
        mManulSetTvStartLock = mManulSetLayout.findViewById(R.id.tvstartlock_manual);

        mManulSetLayout.findViewById(R.id.tv_skip).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sInitCheckStatus == 1) {
                    UmengMaiDianManager.onEvent(mContext, "event_054", null);
                } else if (sInitCheckStatus == 2) {
                    UmengMaiDianManager.onEvent(mContext, "event_061", null);
                }
                if (mSkipToLock) {
                    Intent intent1 = new Intent(mActivityBase, ActivityLockScreen.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mActivityBase.startActivity(intent1);
                    mActivityBase.finish();
                    mActivityBase.startActivityAnim();
                } else {
                    mActivityBase.finish();
                    mActivityBase.closeActivityAnim();
                }
            }
        });

        FrameLayout frameLayout = (FrameLayout) mManulSetLayout.findViewById(R.id.manualsetitemslayout);
        //根据适配的机型, 添加不同的条目
        if (App.sIsAdapterPhone == 1) {
            mManualSetItemsLayout = new ManualSetItems_1(mContext);

        } else if (App.sIsAdapterPhone == 2) {
            mManualSetItemsLayout = new ManualSetItems_2(mContext);

        } else if (App.sIsAdapterPhone == 3) {
            mManualSetItemsLayout = new ManualSetItems_3(mContext);

        } else  if (App.sIsAdapterPhone == 4) {
            mManualSetItemsLayout = new ManualSetItems_4(mContext);

        } else  if (App.sIsAdapterPhone == 5) {
            mManualSetItemsLayout = new ManualSetItems_5(mContext);

        }  else {
            mManualSetItemsLayout = new ManualSetItems_4(mContext);
        }

        mManualSetItemsLayout.setActivityBase(mActivityBase);
        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLayout.addView(mManualSetItemsLayout, lp);

        mManualSetItemsLayout.setOnAllItemSetListener(new CV_LockInit_ManualSetItemsBase.onAllItemSetListener() {
            @Override
            public void onAllItemSet() {
                mManulSetTvStartLock.setBackgroundResource(R.drawable.selector_lockinit_btnbg2);
                mManulSetTvStartLock.setOnClickListener(CV_LockInitSetView.this);
                mManulSetIvCryLaugh.setImageResource(R.drawable.icon_lockinit_laugh);
                mManulSetTvTitle.setText("锁屏设置已完成");
            }
        });
    }

    private void showScanLayout() {
        mScanLayout.setVisibility(VISIBLE);
        mSetProgressLayout.setVisibility(GONE);
        mSetAccessLayout.setVisibility(GONE);
        mScanLayoutRadarView.start();
        mSetSuccessLayout.setVisibility(GONE);
        mManulSetLayout.setVisibility(GONE);
    }

    private void showSetProgressLayout() {
        mSetProgressLayout.setVisibility(VISIBLE);

        final CV_ScanRadarView scanRadarView = (CV_ScanRadarView) mSetProgressLayout.findViewById(R.id.radarview);
        if (scanRadarView.getWidth() == 0) {
            App.sMainHanlder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSetProgressLayout();
                }
            }, 30);
            return;
        }

        scanRadarView.setRadar(false);
        scanRadarView.start();

        ImageView imageView = (ImageView) mSetProgressLayout.findViewById(R.id.iv_gear);
        AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
        drawable.start();

        //---------------------------
        if (sInitCheckStatus == 1) {
            UmengMaiDianManager.onEvent(mContext, "event_047");
        } else if (sInitCheckStatus == 2) {
            UmengMaiDianManager.onEvent(mContext, "event_042");
        }

        if (App.sIsAdapterPhone == 1) {

        } else if (App.sIsAdapterPhone == 2) {
//                    sIsAutoSetting = true;
//                    //开始自动设置, 跳去设置开机启动的界面
//                    Intent intent = SystemLockAdapterUtil.getAutoStartIntent();
//                    mActivityBase.startActivityForResult(intent, 102);
//                    mActivityBase.startActivityAnim();
        }

        App.sMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                sAutoSetSuccess = false;
                onActivityResult(102, 0, null);
            }
        }, 2000);

        mScanLayout.setVisibility(GONE);
        mSetAccessLayout.setVisibility(GONE);
        mScanLayoutRadarView.start();
        mSetSuccessLayout.setVisibility(GONE);
        mManulSetLayout.setVisibility(GONE);
    }

    private void showSetAccessLayout() {
        mScanLayout.setVisibility(GONE);
        mSetProgressLayout.setVisibility(GONE);
        mSetAccessLayout.setVisibility(VISIBLE);
        mScanLayoutRadarView.stop();
        mSetSuccessLayout.setVisibility(GONE);
        mManulSetLayout.setVisibility(GONE);
    }

    private void showSetSuccessLayout() {
        mScanLayout.setVisibility(GONE);
        mSetProgressLayout.setVisibility(GONE);
        mSetAccessLayout.setVisibility(GONE);
        mScanLayoutRadarView.stop();
        mSetSuccessLayout.setVisibility(VISIBLE);
        mManulSetLayout.setVisibility(GONE);
    }

    private void showManualSetLayout() {
        mScanLayout.setVisibility(GONE);
        mSetProgressLayout.setVisibility(GONE);
        mSetAccessLayout.setVisibility(GONE);
        mScanLayoutRadarView.stop();
        mSetSuccessLayout.setVisibility(GONE);
        mManulSetLayout.setVisibility(VISIBLE);
    }

    public void setActivityBase(ActivityBase activityBase) {
        mActivityBase = activityBase;
        if (mManualSetItemsLayout != null) {
            mManualSetItemsLayout.setActivityBase(mActivityBase);
        }
    }

    boolean mSkipToLock = false;
    public void setSkipToLock(boolean skipToLock) {
        mSkipToLock = skipToLock;
    }

    public void startScanAnim() {
        App.sMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mScanLayoutRadarView.getWidth() == 0) {
                    startScanAnim();
                } else {
                    mScanLayoutRadarView.start();

                    UmengMaiDianManager.onEvent(mContext, "event_040", null);
                    App.sMainHanlder.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkAccessibility();
                        }
                    }, 1500);
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

    @Override
    public void onClick(View v) {
        if (sIsAutoSetting) {
            return;
        }
        switch (v.getId()) {
            case R.id.tvsetaccess:
                {
                    //进入辅助功能呢界面, 提示用户开启辅助功能
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivityBase.startActivityForResult(intent, 101);
                    mActivityBase.startActivityAnim();

                    //提示界面
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent i2 = new Intent(mContext, ActivityPrompt_Accessablity.class);
                            mActivityBase.startActivity(i2);
                        }
                    });

                    UmengMaiDianManager.onEvent(mContext, "event_045");
                }
                break;
            case R.id.tvstartlock:
                {
                    if (sInitCheckStatus == 1) {
                        UmengMaiDianManager.onEvent(mContext, "event_049");
                    } else if (sInitCheckStatus == 2) {
                        UmengMaiDianManager.onEvent(mContext, "event_056");
                    } else if (sInitCheckStatus == 3) {
                        UmengMaiDianManager.onEvent(mContext, "event_062");
                    }

                    Intent i = new Intent(mContext, ActivityLockScreen.class);
                    mActivityBase.startActivity(i);
                    mActivityBase.finish();
                    mActivityBase.startActivityAnim();
                }
                break;
            case R.id.tvstartlock_manual:
                {
                    if (sInitCheckStatus == 1) {
                        UmengMaiDianManager.onEvent(mContext, "event_053");
                    } else if (sInitCheckStatus == 2) {
                        UmengMaiDianManager.onEvent(mContext, "event_060");
                    }

                    Intent i = new Intent(mContext, ActivityLockScreen.class);
                    mActivityBase.startActivity(i);
                    mActivityBase.finish();
                    mActivityBase.startActivityAnim();
                }
                break;
            case R.id.tv_skip: {
//                    LogHelper.d("wangzixu", "tv_skip mSkipToLock = " + mSkipToLock);
                    if (mSkipToLock) {
                        Intent intent1 = new Intent(mActivityBase, ActivityLockScreen.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mActivityBase.startActivity(intent1);
                        mActivityBase.finish();
                        mActivityBase.startActivityAnim();
                    } else {
                        mActivityBase.finish();
                        mActivityBase.closeActivityAnim();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void checkAccessibility() {
        if (App.sIsAdapterPhone > 0) { //适配了的手机
            if (isAccessibilitySettingsOn(mContext)) {
                sInitCheckStatus = 2;
                beginAutoSetup();
            } else {
                showSetAccessLayout();

                sInitCheckStatus = 1;
                UmengMaiDianManager.onEvent(mContext, "event_041");
            }
        } else {
            sInitCheckStatus = 3;
            UmengMaiDianManager.onEvent(mContext, "event_043");

            showSetSuccessLayout();
        }
    }

    private void beginAutoSetup() {
        showSetProgressLayout();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) { //辅助功能界面回来
            if (isAccessibilitySettingsOn(mContext)) {
                beginAutoSetup();
            } else {
                showManualSetLayout();
            }
        } else if (requestCode == 102) { //自动设置自启动回来
            sIsAutoSetting = false;

            if (sAutoSetSuccess) { //自动设置成功回来, 开启进入锁屏界面
                showSetSuccessLayout();

                if (sInitCheckStatus == 1) {
                    UmengMaiDianManager.onEvent(mContext, "event_048");
                } else if (sInitCheckStatus == 2){
                    UmengMaiDianManager.onEvent(mContext, "event_055");
                }
            } else { //自动设置失败回来, 开启手动设置界面
                showManualSetLayout();

                if (sInitCheckStatus == 1) {
                    UmengMaiDianManager.onEvent(mContext, "event_050");
                } else if (sInitCheckStatus == 2) {
                    UmengMaiDianManager.onEvent(mContext, "event_057");
                }
            }
        } else {
            if (mManualSetItemsLayout != null) {
                mManualSetItemsLayout.onActivityResult(requestCode, resultCode,data);
            }
        }
    }
}
