package com.haokan.hklockscreen.lockscreeninitset;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreen.ActivityLockScreen;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.logsys.LogHelper;
import com.umeng.analytics.MobclickAgent;

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
    private View mSetAccessLayout; //设置辅助功能界面
    private View mSetSuccessLayout; //自动设置成功该界面
    private View mManulSetLayout; //手动设置界面
    private View mManulSetTvStartLock;
    private int mManusetBit = 0x00000000;
    private static final int MANUSET_BIT_AUTOSTART = 0x00000001;
    private static final int MANUSET_BIT_REMOVESYSPSWD = 0x00000010;
    private static final int MANUSET_BIT_REMOVESYSMAGAZINE = 0x00000100;
    private static final int MANUSET_BIT_ALLSET = 0x00000111;

    public CV_LockInitSetView(@NonNull Context context) {
        this(context, null);
    }

    public CV_LockInitSetView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_LockInitSetView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.cv_lockinit_view, this, true);

        initScanLayout();
        initSetAccessLayout();
        initSetSuccessLayout();
        initManualSetLayout();
    }

    private void initScanLayout() {
        mScanLayout = findViewById(R.id.scanlayout);
        mScanLayoutRadarView = (CV_ScanRadarView) mScanLayout.findViewById(R.id.radarview);
    }

    private void initSetAccessLayout() {
        mSetAccessLayout = findViewById(R.id.setaccesslayout);
        mSetAccessLayout.findViewById(R.id.tvsetaccess).setOnClickListener(this);
        mSetAccessLayout.findViewById(R.id.tv_skip).setOnClickListener(this);
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
        mManulSetTvStartLock = mManulSetLayout.findViewById(R.id.tvstartlock);

        mManulSetLayout.findViewById(R.id.tv_skip).setOnClickListener(this);
        mManulSetLayout.findViewById(R.id.tv_manualset_autostart).setOnClickListener(this);
        mManulSetLayout.findViewById(R.id.tv_manualset_removesyspswd).setOnClickListener(this);
        mManulSetLayout.findViewById(R.id.tv_manualset_removesysmagazine).setOnClickListener(this);
    }

    private void showScanLayout() {
        mSetAccessLayout.setVisibility(GONE);
        mScanLayout.setVisibility(VISIBLE);
        mScanLayoutRadarView.start();
        mSetSuccessLayout.setVisibility(GONE);
        mManulSetLayout.setVisibility(GONE);
    }

    private void showSetAccessLayout() {
        mSetAccessLayout.setVisibility(VISIBLE);
        mScanLayout.setVisibility(GONE);
        mScanLayoutRadarView.stop();
        mSetSuccessLayout.setVisibility(GONE);
        mManulSetLayout.setVisibility(GONE);
    }

    private void showSetSuccessLayout() {
        mSetAccessLayout.setVisibility(GONE);
        mScanLayout.setVisibility(GONE);
        mScanLayoutRadarView.stop();
        mSetSuccessLayout.setVisibility(VISIBLE);
        mManulSetLayout.setVisibility(GONE);
    }

    private void showManualSetLayout() {
        mSetAccessLayout.setVisibility(GONE);
        mScanLayout.setVisibility(GONE);
        mScanLayoutRadarView.stop();
        mSetSuccessLayout.setVisibility(GONE);
        mManulSetLayout.setVisibility(VISIBLE);
    }

    public void setActivityBase(ActivityBase activityBase) {
        mActivityBase = activityBase;
    }

    public void startScanAnim() {
        App.sMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mScanLayoutRadarView.getWidth() == 0) {
                    startScanAnim();
                } else {
                    mScanLayoutRadarView.start();
                    App.sMainHanlder.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkAccessibility();
                        }
                    }, 1500);
                }
            }
        }, 50);
    }

    /**
     * 是否是已经适配的手机
     * @return
     */
    public boolean isAdaptedPhone() {
        String manufacturer = Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase("xiaomi") || manufacturer.equalsIgnoreCase("oppo")) {
            return true;
        }
        return false;
    }

    private void checkAccessibility() {
        if (isAdaptedPhone()) {
            if (isAccessibilitySettingsOn(mContext)) {
                sIsAutoSetting = true;

                //开始自动设置, 跳去设置开机启动的界面
                Intent intent = SystemIntentUtil.getAutoStartIntent();
                mActivityBase.startActivityForResult(intent, 102);
                mActivityBase.startActivityAnim();
                MobclickAgent.onEvent(mContext, "initset_setting");
            } else {
                showSetAccessLayout();
                MobclickAgent.onEvent(mContext, "initset_checkfailed");
            }
        } else {
            showSetSuccessLayout();
            MobclickAgent.onEvent(mContext, "initset_checkfailed");
        }
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
                }
                break;
            case R.id.tv_manualset_autostart:
                try{
                    Intent intent = SystemIntentUtil.getAutoStartIntent();
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivityBase.startActivityForResult(intent, 103);
                    mActivityBase.startActivityAnim();
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent i2 = new Intent(mContext, ActivityPrompt_AutoStart.class);
                            mActivityBase.startActivity(i2);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_manualset_removesyspswd:
                try{
                    Intent intent = SystemIntentUtil.getRemoveSysPswdIntent();
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivityBase.startActivityForResult(intent, 104);
                    mActivityBase.startActivityAnim();
//                    App.sMainHanlder.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent i2 = new Intent(mContext, ActivityPrompt_AutoStart.class);
//                            mActivityBase.startActivity(i2);
//                        }
//                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_manualset_removesysmagazine:
                try{
                    Intent intent = SystemIntentUtil.getRemoveSysMagazineIntent();
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivityBase.startActivityForResult(intent, 105);
                    mActivityBase.startActivityAnim();
//                    App.sMainHanlder.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent i2 = new Intent(mContext, ActivityPrompt_AutoStart.class);
//                            mActivityBase.startActivity(i2);
//                        }
//                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tvstartlock:
                MobclickAgent.onEvent(mContext, "initset_successgolock");

                Intent i = new Intent(mContext, ActivityLockScreen.class);
                mActivityBase.startActivity(i);
                mActivityBase.finish();
                mActivityBase.overridePendingTransition(0,0);
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

    /**
     * 打开自启动界面, 并提示用户去开启自启动
     */
    public void goAutoSetActivity() {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) { //辅助功能界面回来
            if (isAccessibilitySettingsOn(mContext)) {
                sIsAutoSetting = true;

                showScanLayout();
                App.sMainHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = SystemIntentUtil.getAutoStartIntent();
                        mActivityBase.startActivityForResult(intent, 102);
                        mActivityBase.startActivityAnim();

                        MobclickAgent.onEvent(mContext, "initset_setting");
                    }
                },300);
            } else {
                MobclickAgent.onEvent(mContext, "initset_checkfailed");

                mManulSetLayout.setVisibility(VISIBLE);
                mScanLayoutRadarView.stop();
                mScanLayout.setVisibility(GONE);
            }
        } else if (requestCode == 102) { //自动设置自启动回来
            sIsAutoSetting = false;

            if (sAutoSetSuccess) { //自动设置成功回来
                MobclickAgent.onEvent(mContext, "initset_success");
                showSetSuccessLayout();
            } else { //自动设置失败回来
                MobclickAgent.onEvent(mContext, "initset_failmanual");
                showManualSetLayout();
            }
        } else if (requestCode == 103) { //手动自启动回来
            sIsAutoSetting = false;

            mManusetBit |= MANUSET_BIT_AUTOSTART;
            if (mManusetBit == MANUSET_BIT_ALLSET) {
                mManulSetTvStartLock.setBackgroundResource(R.drawable.selector_lockinit_btnbg2);
                mManulSetTvStartLock.setOnClickListener(this);
                mManulSetIvCryLaugh.setImageResource(R.drawable.icon_lockinit_laugh);
                mManulSetTvTitle.setText("锁屏设置已完成");
            }
        } else if (requestCode == 104) { //手动去密码回来
            sIsAutoSetting = false;

            mManusetBit |= MANUSET_BIT_REMOVESYSPSWD;
            if (mManusetBit == MANUSET_BIT_ALLSET) {
                mManulSetTvStartLock.setBackgroundResource(R.drawable.selector_lockinit_btnbg2);
                mManulSetTvStartLock.setOnClickListener(this);
                mManulSetIvCryLaugh.setImageResource(R.drawable.icon_lockinit_laugh);
                mManulSetTvTitle.setText("锁屏设置已完成");
            }
        } else if (requestCode == 105) { //oppo手机手动去杂志锁屏回来
            sIsAutoSetting = false;

            mManusetBit |= MANUSET_BIT_REMOVESYSMAGAZINE;
            if (mManusetBit == MANUSET_BIT_ALLSET) {
                mManulSetTvStartLock.setBackgroundResource(R.drawable.selector_lockinit_btnbg2);
                mManulSetTvStartLock.setOnClickListener(this);
                mManulSetIvCryLaugh.setImageResource(R.drawable.icon_lockinit_laugh);
                mManulSetTvTitle.setText("锁屏设置已完成");
            }
        }
    }
}
