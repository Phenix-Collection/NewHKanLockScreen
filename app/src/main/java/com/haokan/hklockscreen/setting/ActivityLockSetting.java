package com.haokan.hklockscreen.setting;

import android.Manifest;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.bumptech.glide.Glide;
import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.haokanAd.BeanAdRes;
import com.haokan.hklockscreen.haokanAd.ModelHaoKanAd;
import com.haokan.hklockscreen.haokanAd.onAdResListener;
import com.haokan.hklockscreen.haokanAd.request.BannerReq;
import com.haokan.hklockscreen.haokanAd.request.BidRequest;
import com.haokan.hklockscreen.localDICM.ModelLocalImage;
import com.haokan.hklockscreen.lockscreen.CV_ScrollView;
import com.haokan.hklockscreen.lockscreeninitset.ActivityLockScreenInitSet;
import com.haokan.hklockscreen.lockscreeninitset.phone3.ActivityPrompt_CloseSysPswd_3;
import com.haokan.hklockscreen.lockscreeninitset.phone4.ActivityPrompt_CloseSysPswd_4;
import com.haokan.hklockscreen.lockscreeninitset.phone5.ActivityPrompt_CloseSysPswd_5;
import com.haokan.hklockscreen.mycollection.ActivityMyCollection;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.checkupdate.UpdateManager;
import com.haokan.pubic.clipimage.ActivityClipImage;
import com.haokan.pubic.clipimage.ClipImgManager;
import com.haokan.pubic.database.BeanLocalImage;
import com.haokan.pubic.database.MyDatabaseHelper;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.maidian.UmengMaiDianManager;
import com.haokan.pubic.util.CommonUtil;
import com.haokan.pubic.util.DisplayUtil;
import com.haokan.pubic.util.FileUtil;
import com.haokan.pubic.util.MyDialogUtil;
import com.haokan.pubic.util.StatusBarUtil;
import com.haokan.pubic.util.ToastManager;
import com.haokan.pubic.util.Values;
import com.haokan.pubic.webview.ActivityWebview;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/10/20.
 */
public class ActivityLockSetting extends ActivityBase implements View.OnClickListener {
    private CV_ScrollView mScrollview;
    private FrameLayout mBannerlayout;
    private RelativeLayout mLayoutlockscreen;
    private ImageView mIvLockscreen;
    private RelativeLayout mLayoutAutoUpdateImage;
    private ImageView mIvAutoupdateImage;
    private RelativeLayout mLayoutInitset;
    private RelativeLayout mLayoutFadeback;
    private RelativeLayout mLayoutCheckupdate;
    private RelativeLayout mLayoutAboutus;
    private RelativeLayout mLayoutCloseSysLock;
    private RelativeLayout mLayoutCollect;
    private ImageView mIvImage1;
    private ImageView mIvImage2;
    private ImageView mIvImage3;
    private FrameLayout mHeader;
    private ImageView mBack;
    private FrameLayout mHeader1;
    private ImageView mBack1;
    private int mHeaderChangeHeigh;
    private TextView mTvLocalImageEdit;
    private ImageView mCurrentImage;
    private ClipImgManager mClipImgManager;
    private ImageView mIvAdView;
    private View mIvDelte1;
    private View mIvDelte2;
    private View mIvDelte3;
    private BeanLocalImage mLocalImage1;
    private BeanLocalImage mLocalImage2;
    private BeanLocalImage mLocalImage3;
    private ImageView mIvBigImage;
    private View mAdSignView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locksetting);
        StatusBarUtil.setStatusBarTransparnet(this);
        initView();
        loadLocalImages();
        loadHaoKanAd();

        UmengMaiDianManager.onEvent(this, "event_085");
    }

    private void initView() {
        mScrollview = (CV_ScrollView) findViewById(R.id.scrollview);
        mBannerlayout = (FrameLayout) findViewById(R.id.bannerlayout);
        mIvAdView = (ImageView) findViewById(R.id.adview);
        mAdSignView = findViewById(R.id.adsgin);
        mLayoutlockscreen = (RelativeLayout) findViewById(R.id.layoutlockscreen);
        mIvLockscreen = (ImageView) findViewById(R.id.iv_lockscreen);
        mLayoutAutoUpdateImage = (RelativeLayout) findViewById(R.id.layoutautoupdateimg);
        mIvAutoupdateImage = (ImageView) findViewById(R.id.iv_autoupdate);
        mLayoutInitset = (RelativeLayout) findViewById(R.id.layout_initset);
        mLayoutFadeback = (RelativeLayout) findViewById(R.id.layout_fadeback);
        mLayoutCheckupdate = (RelativeLayout) findViewById(R.id.layout_checkupdate);
        mLayoutAboutus = (RelativeLayout) findViewById(R.id.layout_aboutus);
        mLayoutCloseSysLock = (RelativeLayout) findViewById(R.id.closesyslock);
        mLayoutCollect = (RelativeLayout) findViewById(R.id.setting_collect);

        mIvImage1 = (ImageView) findViewById(R.id.iv_image1);
        mIvImage2 = (ImageView) findViewById(R.id.iv_image2);
        mIvImage3 = (ImageView) findViewById(R.id.iv_image3);
        mIvDelte1 = findViewById(R.id.iv_close1);
        mIvDelte2 = findViewById(R.id.iv_close2);
        mIvDelte3 = findViewById(R.id.iv_close3);
        mHeader = (FrameLayout) findViewById(R.id.header);
        mBack = (ImageView) findViewById(R.id.back);
        mHeader1 = (FrameLayout) findViewById(R.id.header1);
        mBack1 = (ImageView) findViewById(R.id.back1);
        mTvLocalImageEdit = (TextView) findViewById(R.id.edit);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //设置是否打开了锁屏
        boolean open = preferences.getBoolean(Values.PreferenceKey.KEY_SP_OPENLOCKSCREEN, true);
        if (open) {
            mIvLockscreen.setSelected(true);
        } else {
            mIvLockscreen.setSelected(false);
        }

        //设置是否自动更新锁屏图片
        boolean auto = preferences.getBoolean(Values.PreferenceKey.KEY_SP_AUTOUPDATEIMAGE, true);
        if (auto) {
            mIvAutoupdateImage.setSelected(true);
        } else {
            mIvAutoupdateImage.setSelected(false);
        }

        mBack.setOnClickListener(this);
        mBack1.setOnClickListener(this);
        mLayoutCollect.setOnClickListener(this);
        mLayoutlockscreen.setOnClickListener(this);
        mLayoutCheckupdate.setOnClickListener(this);
        mLayoutAboutus.setOnClickListener(this);
        mLayoutAutoUpdateImage.setOnClickListener(this);
        mLayoutInitset.setOnClickListener(this);
        mTvLocalImageEdit.setOnClickListener(this);
        mLayoutFadeback.setOnClickListener(this);
        mIvImage1.setOnClickListener(this);
        mIvImage2.setOnClickListener(this);
        mIvImage3.setOnClickListener(this);
        mIvDelte1.setOnClickListener(this);
        mIvDelte2.setOnClickListener(this);
        mIvDelte3.setOnClickListener(this);
        mIvAdView.setOnClickListener(this);
        mLayoutCloseSysLock.setOnClickListener(this);

        //顶部banner高-mHeader1高
        mHeaderChangeHeigh = DisplayUtil.dip2px(this, 220-65);
        mScrollview.setMyOnScrollChangeListener(new CV_ScrollView.MyOnScrollChangeListener() {
            @Override
            public void onScrollChange(int scrollX, int scrollY, int oldX, int oldY) {
                //LogHelper.d("wangzixu", "locksetting height = " + height + ", height1 = " + height1);
                if (scrollY >= mHeaderChangeHeigh) {
                    if (mHeader1.getVisibility() != View.VISIBLE) {
                        mHeader1.setVisibility(View.VISIBLE);
                        mHeader.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (mHeader.getVisibility() != View.VISIBLE) {
                        mHeader1.setVisibility(View.INVISIBLE);
                        mHeader.setVisibility(View.VISIBLE);
                        if (mBeanAdRes != null) {
                            //广告展示上报
                            ModelHaoKanAd.onAdShow(mBeanAdRes.onShowUrls);
                        }
                    }
                }
            }
        });

        mIvBigImage = new ImageView(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mIvBigImage.setLayoutParams(lp);
        mIvBigImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewParent parent = mIvBigImage.getParent();
                if (parent != null) {
                    ((ViewGroup)parent).removeView(mIvBigImage);
                    mIvBigImage.setImageBitmap(null);
                    mIvBigImage.setVisibility(View.GONE);
                }
            }
        });

        //根据是否适配, 显示一些条目
        if (App.sIsAdapterPhone == 1
                || App.sIsAdapterPhone == 3
                || App.sIsAdapterPhone == 4
                || App.sIsAdapterPhone == 5
                ) {
            mLayoutCloseSysLock.setVisibility(View.VISIBLE);
        } else {
            mLayoutCloseSysLock.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mBeanAdRes != null && mHeader.getVisibility() == View.VISIBLE) {
//            //广告展示上报
//            ModelHaoKanAd.onAdShow(mBeanAdRes);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closesyslock:
                if (App.sIsAdapterPhone == 1) {
                    Intent intent = new Intent(ActivityLockSetting.this, ActivityLockSetting_CloseSysLock_1.class);
                    startActivityForResult(intent, 303);
                    startActivityAnim();
                } else if (App.sIsAdapterPhone == 3) {
                    try{
                        Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                        startActivity(intent);
                        startActivityAnim();
                        App.sMainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent i2 = new Intent(ActivityLockSetting.this, ActivityPrompt_CloseSysPswd_3.class);
                                ActivityLockSetting.this.startActivity(i2);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (App.sIsAdapterPhone == 4) {
                    try{
                        Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                        startActivity(intent);
                        startActivityAnim();
                        App.sMainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent i2 = new Intent(ActivityLockSetting.this, ActivityPrompt_CloseSysPswd_4.class);
                                ActivityLockSetting.this.startActivity(i2);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (App.sIsAdapterPhone == 5) {
                    try{
                        Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                        startActivity(intent);
                        startActivityAnim();
                        App.sMainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent i2 = new Intent(ActivityLockSetting.this, ActivityPrompt_CloseSysPswd_5.class);
                                ActivityLockSetting.this.startActivity(i2);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.adview:
                if (mBeanAdRes != null) {
//                    Intent intent = new Intent(ActivityLockSetting.this, ActivityWebview.class);
//                    intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBeanAdRes.landPageUrl);

                    Intent intent = new Intent(ActivityLockSetting.this, ActivityWebview.class);
//                Intent intent = new Intent(ActivityLockSetting.this, ActivityWebviewForLockPage.class);
                    //查询是否有deeplink的app
                    if (!TextUtils.isEmpty(mBeanAdRes.deeplink)) {
                        Intent qi = new Intent(Intent.ACTION_VIEW, Uri.parse(mBeanAdRes.deeplink));
                        if (CommonUtil.deviceCanHandleIntent(ActivityLockSetting.this, qi)) { //是否可以支持deeplink
                            intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBeanAdRes.deeplink);
                        } else {
                            intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBeanAdRes.landPageUrl);
                        }
                    }else {
                        intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBeanAdRes.landPageUrl);
                    }

                    startActivityForResult(intent, 306);
                    startActivityAnim();

                    //广告点击上报
                    if (mBeanAdRes.onClickUrls != null && mBeanAdRes.onClickUrls.size() > 0) {
                        ModelHaoKanAd.onAdClick(mBeanAdRes.onClickUrls);
                    }
                }
                break;
            case R.id.back:
            case R.id.back1:
                onBackPressed();
                break;
            case R.id.iv_close1:
                if (mLocalImage1 != null) {
                    deleteLocalImage(mLocalImage1);
                    notifyLocalImageChange();
                    mIvImage1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    mIvImage1.setBackgroundResource(R.drawable.shape_setting_addbg);
                    mIvImage1.setImageResource(R.drawable.icon_addto);
                    mLocalImage1 = null;
                    v.setVisibility(View.GONE);

                    UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_097");
                }
                break;
            case R.id.iv_close2:
                if (mLocalImage2 != null) {
                    deleteLocalImage(mLocalImage2);
                    notifyLocalImageChange();
                    mIvImage2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    mIvImage2.setBackgroundResource(R.drawable.shape_setting_addbg);
                    mIvImage2.setImageResource(R.drawable.icon_addto);
                    mLocalImage2 = null;
                    v.setVisibility(View.GONE);

                    UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_097");
                }
                break;
            case R.id.iv_close3:
                if (mLocalImage3 != null) {
                    deleteLocalImage(mLocalImage3);
                    notifyLocalImageChange();
                    mIvImage3.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    mIvImage3.setBackgroundResource(R.drawable.shape_setting_addbg);
                    mIvImage3.setImageResource(R.drawable.icon_addto);
                    mLocalImage3 = null;
                    v.setVisibility(View.GONE);

                    UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_097");
                }
                break;
            case R.id.edit:
                if (v.isSelected()) {
                    mTvLocalImageEdit.setText("编辑");
                    mTvLocalImageEdit.setSelected(false);
                    mIvDelte1.setVisibility(View.GONE);
                    mIvDelte2.setVisibility(View.GONE);
                    mIvDelte3.setVisibility(View.GONE);
                } else {
                    if (mLocalImage1 == null && mLocalImage2 == null && mLocalImage3 == null) {
                        return;
                    }
                    mTvLocalImageEdit.setText("取消");
                    mTvLocalImageEdit.setSelected(true);
                    if (mLocalImage1 != null) {
                        mIvDelte1.setVisibility(View.VISIBLE);
                    }
                    if (mLocalImage2 != null) {
                        mIvDelte2.setVisibility(View.VISIBLE);
                    }
                    if (mLocalImage3 != null) {
                        mIvDelte3.setVisibility(View.VISIBLE);
                    }

                    UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_093");
                }
                break;
            case R.id.layoutlockscreen:
                {
                    final SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    if (mIvLockscreen.isSelected()) {
                        MyDialogUtil.showMyDialog(this, "提示", "确定要关闭锁屏功能吗?", null, null, false
                                , new MyDialogUtil.myDialogOnClickListener() {
                                    @Override
                                    public void onClickCancel() {
                                        //nothing
                                    }

                                    @Override
                                    public void onClickConfirm(boolean checked) {
                                        mIvLockscreen.setSelected(false);
                                        edit.putBoolean(Values.PreferenceKey.KEY_SP_OPENLOCKSCREEN, false).apply();

                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("开/关", "关");
                                        UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_087", map);
                                    }
                                });
                    } else {
                        mIvLockscreen.setSelected(true);
                        edit.putBoolean(Values.PreferenceKey.KEY_SP_OPENLOCKSCREEN, true).apply();

                        HashMap<String, String> map = new HashMap<>();
                        map.put("开/关", "开");
                        UmengMaiDianManager.onEvent(this, "event_087", map);
                    }
                }
                break;
            case R.id.layoutautoupdateimg://自动更新锁屏图片
                {
                    final SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    if (mIvAutoupdateImage.isSelected()) {
                        MyDialogUtil.showMyDialog(this, "提示", "关闭后每天将不会自动更新锁屏图片, 确定要关闭吗?",  null, null, false
                                , new MyDialogUtil.myDialogOnClickListener() {
                                    @Override
                                    public void onClickCancel() {
                                        //nothing
                                    }

                                    @Override
                                    public void onClickConfirm(boolean checked) {
                                        mIvAutoupdateImage.setSelected(false);
                                        edit.putBoolean(Values.PreferenceKey.KEY_SP_AUTOUPDATEIMAGE, false).apply();

                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("开/关", "关");
                                        UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_088", map);
                                    }
                                });
                    } else {
                        mIvAutoupdateImage.setSelected(true);
                        edit.putBoolean(Values.PreferenceKey.KEY_SP_AUTOUPDATEIMAGE, true).apply();

                        HashMap<String, String> map = new HashMap<>();
                        map.put("开/关", "开");
                        UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_088", map);
                    }
                }
                break;
            case R.id.setting_collect:
                {
                    Intent intent = new Intent(this, ActivityMyCollection.class);
                    startActivityForResult(intent, 304);
                    startActivityAnim();

                    UmengMaiDianManager.onEvent(this, "event_086");
                }
                break;
            case R.id.layout_fadeback:
                if (HttpStatusManager.checkNetWorkConnect(this)) {
                    FeedbackAPI.openFeedbackActivity();
                    UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_090");
                } else {
                    ToastManager.showNetErrorToast(this);
                }
                break;
            case R.id.layout_aboutus:
                {
                    Intent intent = new Intent(this, ActivityAboutUs.class);
                    startActivityForResult(intent, 300);
//                    startActivity(intent);
                    startActivityAnim();

                    UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_092");
                }
                break;
            case R.id.layout_checkupdate:
                checkStoragePermission();

                UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_091");
                break;
            case R.id.layout_initset:
                {
                    Intent intent = new Intent(this, ActivityLockScreenInitSet.class);
                    startActivityForResult(intent, 301);
                    startActivityAnim();

                    UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_089");
                }
                break;
            case R.id.iv_image1:
                if (mTvLocalImageEdit.isSelected() || mLocalImage1 == null) {
                    mCurrentImage = mIvImage1;
                    if (mClipImgManager == null) {
                        mClipImgManager = new ClipImgManager();
                    }
                    mClipImgManager.startChose(this, 101);

                    if (mLocalImage1 == null) { //加新图
                        if (mTvLocalImageEdit.isSelected()) {
                            //event_098,8-设置页-我的故事-加图,0
                            UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_098");
                        } else {
                            //event_094,9-设置页-点击我的故事-加图,0
                            UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_094");
                        }
                    } else { //换图
                        UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_096");
                    }
                } else {
                    ViewParent parent = mIvBigImage.getParent();
                    if (parent != null) {
                        ((ViewGroup)parent).removeView(mIvBigImage);
                        mIvBigImage.setImageBitmap(null);
                    }

                    mIvBigImage.setVisibility(View.VISIBLE);
                    final ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                decorView.addView(mIvBigImage);
                                Glide.with(ActivityLockSetting.this).load(mLocalImage1.imgUrl).into(mIvBigImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_095");
                }
                break;
            case R.id.iv_image2:
                if (mTvLocalImageEdit.isSelected()|| mLocalImage2 == null){
                    mCurrentImage = mIvImage2;
                    if (mClipImgManager == null) {
                        mClipImgManager = new ClipImgManager();
                    }
                    mClipImgManager.startChose(this, 101);

                    if (mLocalImage2 == null) { //加新图
                        if (mTvLocalImageEdit.isSelected()) {
                            //event_098,8-设置页-我的故事-加图,0
                            UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_098");
                        } else {
                            //event_094,9-设置页-点击我的故事-加图,0
                            UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_094");
                        }
                    } else { //换图
                        UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_096");
                    }
                } else {
                    ViewParent parent = mIvBigImage.getParent();
                    if (parent != null) {
                        ((ViewGroup)parent).removeView(mIvBigImage);
                        mIvBigImage.setImageBitmap(null);
                    }

                    mIvBigImage.setVisibility(View.VISIBLE);
                    final ViewGroup decorView = (ViewGroup) getWindow().getDecorView();

                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                decorView.addView(mIvBigImage);
                                Glide.with(ActivityLockSetting.this).load(mLocalImage2.imgUrl).into(mIvBigImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    LogHelper.d("wangzixu", "clipimg clickimage2 mLocalImage2.imgUrl = " + mLocalImage2.imgUrl);
                    UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_095");
                }
                break;
            case R.id.iv_image3:
                if (mTvLocalImageEdit.isSelected()|| mLocalImage3 == null) {
                    mCurrentImage = mIvImage3;
                    if (mClipImgManager == null) {
                        mClipImgManager = new ClipImgManager();
                    }
                    mClipImgManager.startChose(this, 101);

                    if (mLocalImage3 == null) { //加新图
                        if (mTvLocalImageEdit.isSelected()) {
                            //event_098,8-设置页-我的故事-加图,0
                            UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_098");
                        } else {
                            //event_094,9-设置页-点击我的故事-加图,0
                            UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_094");
                        }
                    } else { //换图
                        UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_096");
                    }
                } else {
                    ViewParent parent = mIvBigImage.getParent();
                    if (parent != null) {
                        ((ViewGroup)parent).removeView(mIvBigImage);
                        mIvBigImage.setImageBitmap(null);
                    }

                    mIvBigImage.setVisibility(View.VISIBLE);
                    final ViewGroup decorView = (ViewGroup) getWindow().getDecorView();

                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                decorView.addView(mIvBigImage);
                                Glide.with(ActivityLockSetting.this).load(mLocalImage3.imgUrl).into(mIvBigImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    UmengMaiDianManager.onEvent(ActivityLockSetting.this, "event_095");
                }
                break;
            default:
                break;
        }
    }

    BeanAdRes mBeanAdRes;
    private void loadHaoKanAd() {
        BannerReq bannerReq = new BannerReq();
        bannerReq.w = 1080;
        bannerReq.h = 630;
        BidRequest request = ModelHaoKanAd.getBidRequest(this, "28-53-208", 5, null, bannerReq);

        ModelHaoKanAd.getAd(this, request, new onAdResListener<BeanAdRes>() {
            @Override
            public void onAdResSuccess(final BeanAdRes adRes) {
                mBeanAdRes = adRes;

                LogHelper.d("wangzixu", "ModelHaoKanAd onAdResSuccess adRes = " + adRes.landPageUrl);
                Glide.with(ActivityLockSetting.this).load(adRes.imgUrl).into(mIvAdView);
                mAdSignView.setVisibility(View.VISIBLE);

                mIvAdView.setOnClickListener(ActivityLockSetting.this);

                //广告展示上报
                ModelHaoKanAd.onAdShow(adRes.onShowUrls);
            }

            @Override
            public void onAdResFail(String errmsg) {
                LogHelper.d("wangzixu", "ModelHaoKanAd onAdResFail errmsg = " + errmsg);
            }
        });
    }

    private void loadLocalImages() {
        ModelLocalImage.getLocalImgList(this, new onDataResponseListener<List<BeanLocalImage>>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDataSucess(List<BeanLocalImage> list) {
                for (int i = 0; i < list.size(); i++) {
                    BeanLocalImage imageBean = list.get(i);
                    if (imageBean.index == 1) {
                        mIvImage1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        mIvImage1.setBackground(null);
                        Glide.with(ActivityLockSetting.this).load(imageBean.imgUrl).into(mIvImage1);
                        mLocalImage1 = imageBean;
                    } else
                    if (imageBean.index == 2) {
                        mIvImage2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        mIvImage2.setBackground(null);
                        Glide.with(ActivityLockSetting.this).load(imageBean.imgUrl).into(mIvImage2);
                        mLocalImage2 = imageBean;
                    } else
                    if (imageBean.index == 3) {
                        mIvImage3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        mIvImage3.setBackground(null);
                        Glide.with(ActivityLockSetting.this).load(imageBean.imgUrl).into(mIvImage3);
                        mLocalImage3 = imageBean;
                    }
                }
            }

            @Override
            public void onDataEmpty() {

            }

            @Override
            public void onDataFailed(String errmsg) {

            }

            @Override
            public void onNetError() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                final String path = mClipImgManager.onResult(this, data);
                if (!TextUtils.isEmpty(path)) {
                    Intent intent = new Intent(this, ActivityClipImage.class);
                    intent.putExtra(ActivityClipImage.KEY_INTENT_CLIPIMG_SRC_PATH, path);
                    startActivityForResult(intent, 102);
                    startActivityAnim();
                }
            } else if (requestCode == 102) {
                final String path = data.getStringExtra(ActivityClipImage.KEY_INTENT_CLIPIMG_DOWN_PATH);
                LogHelper.d("wangzixu", "clipimg onActivityResult 102 path = " + path);
                if (!TextUtils.isEmpty(path)) {
                    mCurrentImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    mCurrentImage.setBackground(null);
                    Glide.with(this).load(path).into(mCurrentImage);

                    final Scheduler.Worker worker = Schedulers.io().createWorker();
                    worker.schedule(new Action0() {
                        @Override
                        public void call() {
                            try {
                                BeanLocalImage beanOld = null;
                                BeanLocalImage beanNew = new BeanLocalImage();
                                beanNew.imgId = ModelLocalImage.sLocalImgIdPreffix + System.currentTimeMillis();
                                beanNew.imgUrl = path;
                                beanNew.index = 1;
                                beanNew.create_time = System.currentTimeMillis();
                                if (mCurrentImage == mIvImage1) {
                                    beanOld = mLocalImage1;
                                    beanNew.index = 1;
                                    mLocalImage1 = beanNew;
                                } else if (mCurrentImage == mIvImage2) {
                                    beanOld = mLocalImage2;
                                    beanNew.index = 2;
                                    mLocalImage2 = beanNew;
                                } else if (mCurrentImage == mIvImage3) {
                                    beanOld = mLocalImage3;
                                    beanNew.index = 3;
                                    mLocalImage3 = beanNew;
                                }

                                if (beanOld == null && mTvLocalImageEdit.isSelected()) {
                                    if (beanNew.index == 1) {
                                        App.sMainHanlder.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mIvDelte1.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    } else if (beanNew.index == 2) {
                                        App.sMainHanlder.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mIvDelte2.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    } else if (beanNew.index == 3) {
                                        App.sMainHanlder.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mIvDelte3.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    }
                                }

                                Dao daoLocalImg = MyDatabaseHelper.getInstance(ActivityLockSetting.this).getDaoQuickly(BeanLocalImage.class);
                                if (beanOld != null) {
                                    daoLocalImg.delete(beanOld);
                                    File file = new File(beanOld.imgUrl);
                                    if (file.exists()) {
                                        FileUtil.deleteFile(file);
                                    }
                                }
                                daoLocalImg.create(beanNew);
                                notifyLocalImageChange();
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogHelper.d("wangzixu", "clipimg onActivityResult Exception = " + e.getMessage());
                            }

                            worker.unsubscribe();
                        }
                    });
                }
            }
        }
    }

    protected void deleteLocalImage(BeanLocalImage beanOld) {
        if (beanOld != null) {
            Dao daoLocalImg;
            try {
                daoLocalImg = MyDatabaseHelper.getInstance(ActivityLockSetting.this).getDaoQuickly(BeanLocalImage.class);
                daoLocalImg.delete(beanOld);

                File file = new File(beanOld.imgUrl);
                if (file.exists()) {
                    FileUtil.deleteFile(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void notifyLocalImageChange() {
        //通知锁屏更新图片
//        Intent intent = new Intent("com.haokan.receiver.localimagechange");
//        sendBroadcast(intent);

        if (App.sHaokanLockView != null) {
            App.sHaokanLockView.loadLocalImgDate(true);
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
                UpdateManager.checkUpdate(this, false);
            }
        } else {
            UpdateManager.checkUpdate(this, false);
        }
    }

    //检查权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 201:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //同意
                        UpdateManager.checkUpdate(this, false);
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
        desc.setText("没有授予存储权限, 无法更新, 请去设置授予存储权限后, 再执行检查更新");

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
                            startActivityForResult(intent, 305);
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

    @Override
    public void onBackPressed() {
        if (mIvBigImage != null && mIvBigImage.getParent() != null) {
            ViewParent parent = mIvBigImage.getParent();
            ((ViewGroup)parent).removeView(mIvBigImage);
            mIvBigImage.setImageBitmap(null);
            mIvBigImage.setVisibility(View.GONE);
        } else {
            setResult(RESULT_OK);
            super.onBackPressed();
            closeActivityAnim();
        }
    }
}
