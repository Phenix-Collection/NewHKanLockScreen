package com.haokan.pubic.detailpage;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.mycollection.BeanCollection;
import com.haokan.hklockscreen.mycollection.EventCollectionChange;
import com.haokan.hklockscreen.mycollection.ModelCollection;
import com.haokan.hklockscreen.setting.ActivityLockSetting;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.bean.BeanConvertUtil;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.customview.ViewPagerTransformer;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.maidian.MaidianManager;
import com.haokan.pubic.util.BlurUtil;
import com.haokan.pubic.util.CommonUtil;
import com.haokan.pubic.util.DisplayUtil;
import com.haokan.pubic.util.ToastManager;
import com.haokan.pubic.webview.ActivityWebview;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/10/16.
 */
public class CV_DetailPageView_Base extends FrameLayout implements ViewPager.OnPageChangeListener, View.OnClickListener, View.OnLongClickListener {
    public Context mContext;
    public ActivityBase mActivity;
    protected ViewPager mVpMain;
    protected View mLayoutMainTop;
    protected View mLayoutCaption;
    protected TextView mTvDescSimple;
    protected View mLayoutMainBottom;
    protected TextView mTvTitlle;
    protected TextView mTvLink;
    //外链需要随机的背景色, 所以new个背景, 随机设置颜色
    protected GradientDrawable mTvLinkBg;
    protected View mBottomBar;
    protected View mTvBottomDownloadParent;
    protected TextView mTvBottomDownload;
    protected View mDownloadLayout;
    protected View mDownloadLayoutContent;
    protected View mDownloadLayoutBgView;
    protected TextView mTvSetWallpager;
    protected TextView mTvSaveImg;
    protected View mShareLayout;
    protected int mShareLayoutH;
    protected View mShareLayoutContent;
    protected CV_ShareBgImageView mShareBlurBgView;
    protected ArrayList<MainImageBean> mData = new ArrayList<>();
    protected Adapter_DetailPage_Base mAdapterVpMain;
    protected int mCurrentPosition;
    protected MainImageBean mCurrentImgBean;
    protected View mTvBottomCollect;
    protected TextView mTvBottomCollectTitle;
    protected View mLayoutTitleLink;
    protected boolean mIsCaptionShow; //当前是否正在显示图说
    protected boolean mIsAnimnating; //正在执行一些动画, 如显示隐藏图说等
    protected static final long sAinmDuration = 150; //一些动画的时长, 如显示隐藏图说等
    protected View mBottomSettingView;
    protected TextView mTvCount;

    public CV_DetailPageView_Base(@NonNull Context context) {
        this(context, null);
    }

    public CV_DetailPageView_Base(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_DetailPageView_Base(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setActivity(ActivityBase activity) {
        mActivity = activity;
    }

    private void init(Context context) {
        EventBus.getDefault().register(this);

        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.cv_detailpage_base, this, true);

        //页面上部的导航条背景，如果没有背景，会看不清状态栏上的文字
        mLayoutMainTop = findViewById(R.id.layout_top);

        //页面的底部区域, 包括各种图说和各种功能区
        mLayoutMainBottom = findViewById(R.id.layout_bottom);
        mIsCaptionShow = true;

        mTvCount = (TextView) findViewById(R.id.tv_count);

        //单图区域
        mLayoutCaption = mLayoutMainBottom.findViewById(R.id.layout_caption);
        mLayoutCaption.setOnClickListener(this);
        mTvDescSimple = (TextView) mLayoutMainBottom.findViewById(R.id.tv_desc_simple);

        mLayoutTitleLink = mLayoutMainBottom.findViewById(R.id.layout_title);
        mTvTitlle = (TextView) mLayoutTitleLink.findViewById(R.id.tv_title);
        mTvLink = (TextView) mLayoutTitleLink.findViewById(R.id.tv_link);
        mTvLinkBg = new GradientDrawable();
        mTvLinkBg.setCornerRadius(DisplayUtil.dip2px(mContext, 2));
        mTvLinkBg.setColor(getLinkBgColor());
        mTvLink.setBackground(mTvLinkBg);

        //底部功能按钮条, 返回, 分享...等
        mBottomBar = findViewById(R.id.bottom_bar);
        mLayoutMainBottom.findViewById(R.id.bottom_back).setOnClickListener(this);//返回
        mBottomSettingView = mLayoutMainBottom.findViewById(R.id.setting);
        mBottomSettingView.setOnClickListener(this);//设置

        mTvBottomDownloadParent = mLayoutMainBottom.findViewById(R.id.bottom_download);//赞
        mTvBottomDownload = (TextView) mLayoutMainBottom.findViewById(R.id.bottom_download_title);
        mTvBottomDownloadParent.setOnClickListener(this);
        mTvBottomCollect = mLayoutMainBottom.findViewById(R.id.bottom_collect);
        mTvBottomCollectTitle = (TextView) mLayoutMainBottom.findViewById(R.id.bottom_collect_title);
        mTvBottomCollect.setOnClickListener(this);
        mLayoutMainBottom.findViewById(R.id.bottom_share).setOnClickListener(this);//分享

        //************底部下载layout相关 begin *****************
        mDownloadLayout = findViewById(R.id.download_img_layout);
        mDownloadLayoutContent = mDownloadLayout.findViewById(R.id.content);
        mDownloadLayoutBgView = mDownloadLayout.findViewById(R.id.bgview);
        mDownloadLayoutBgView.setOnClickListener(this);
        mDownloadLayoutContent.findViewById(R.id.cancel).setOnClickListener(this);
        mDownloadLayoutContent.findViewById(R.id.set_wallpaper).setOnClickListener(this);
        mDownloadLayoutContent.findViewById(R.id.save_img).setOnClickListener(this);

        mTvSetWallpager = (TextView) mDownloadLayoutContent.findViewById(R.id.set_wallpaper);
        mTvSaveImg = (TextView) mDownloadLayoutContent.findViewById(R.id.save_img);
        mDownloadLayoutContent.findViewById(R.id.cancel).setOnClickListener(this);
        //************底部下载layout相关 end *****************

        //*****底部分享区域begin*********
        mShareLayout = findViewById(R.id.bottomshare_layout);
        mShareLayout.setOnClickListener(this);
        mShareLayoutContent =  mShareLayout.findViewById(R.id.content);
        mShareLayoutContent.findViewById(R.id.share_weixin_circle).setOnClickListener(this);
        mShareLayoutContent.findViewById(R.id.share_weixin).setOnClickListener(this);
        mShareLayoutContent.findViewById(R.id.share_qq).setOnClickListener(this);
        mShareLayoutContent.findViewById(R.id.share_sina).setOnClickListener(this);
        mShareLayoutContent.findViewById(R.id.share_qqzone).setOnClickListener(this);
        mShareLayout.findViewById(R.id.cancel).setOnClickListener(this);
        mShareBlurBgView = (CV_ShareBgImageView) mShareLayout.findViewById(R.id.blurbgview);
        mShareLayoutH = DisplayUtil.dip2px(context, 190);
        //如果分享区域的高度不固定, 则需要提前测量, 以确定背景图片的高度
//        CommonUtil.haokanMeasure(mShareLayoutContent);
//        mShareLayoutH = mShareLayoutContent.getMeasuredHeight();
//        ViewGroup.LayoutParams params = mShareBlurBgView.getLayoutParams();
//        if (params == null) {
//            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mShareLayoutH);
//        } else {
//            params.height = mShareLayoutH;
//        }
//        mShareBlurBgView.setLayoutParams(params);
        //*****底部分享区域end*********

        //viewpager区域
        mVpMain = (ViewPager) findViewById(R.id.vp_main);
        mVpMain.setOffscreenPageLimit(1);
        mVpMain.setPageTransformer(true, new ViewPagerTransformer.ParallaxTransformer(R.id.iv_image));
        mVpMain.addOnPageChangeListener(this);

        setVpAdapter();
    }

    public void setVpAdapter() {
        //为主vp设置监听器
        mAdapterVpMain = new Adapter_DetailPage_Base(mContext, mData, this, this);
        mVpMain.setAdapter(mAdapterVpMain);
    }

    public MainImageBean getCurrentImageBean() {
        return mCurrentImgBean;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    protected Runnable mPageSelectedDelayRunnable = new Runnable() {
        @Override
        public void run() {
            refreshBottomLayout();
        }
    };

    protected long mPreImageShowTime;
    @Override
    public void onPageSelected(int position) {
        long currentTimeMillis = System.currentTimeMillis();
        if (mCurrentImgBean != null) {
            //好看埋点
            MaidianManager.setAction(mCurrentImgBean.imgId, App.sDID, 9, String.valueOf(currentTimeMillis-mPreImageShowTime), currentTimeMillis);
        }
        mPreImageShowTime = currentTimeMillis;

        App.sMainHanlder.removeCallbacks(mPageSelectedDelayRunnable);
        mCurrentPosition = position;
        mCurrentImgBean = mData.get(mCurrentPosition);
        if (mCurrentImgBean == null) {
            return;
        }
        App.sMainHanlder.postDelayed(mPageSelectedDelayRunnable, 300);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_image:
                onClickBigImage();
                break;
            case R.id.tv_link:
            case R.id.layout_caption:
                onClickLink();
                break;
            case R.id.bottom_back:
                onClickBack();
                break;
            case R.id.bottom_download:
                if (Build.VERSION.SDK_INT >= 23 && mActivity != null) {
                    //需要用权限的地方之前，检查是否有某个权限
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) { //没有这个权限
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
                        return;
                    } else {
                        downloadImage(mCurrentImgBean);
                    }
                } else {
                    downloadImage(mCurrentImgBean);
                }
                break;
            case R.id.bottom_collect:
                onClickCollect(v);
                break;
            case R.id.setting:
                onClickSetting();
                break;
            case R.id.bottom_share:
                showShareLayout();
                break;
            case R.id.save_img:
                downloadImage(mCurrentImgBean);
                hideDownloadLayout();
                break;
            case R.id.bottomshare_layout:
            case R.id.bgview:
            case R.id.cancel:
                if (mShareLayout.getVisibility() == View.VISIBLE) {
                    hideShareLayout();
                }
                if (mDownloadLayout.getVisibility() == View.VISIBLE) {
                    hideDownloadLayout();
                }
                break;
            case R.id.share_weixin_circle:
//                if (UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.WEIXIN)) {
//                } else {
//                    ToastManager.showShort(this, getString(R.string.no_install_weixin));
//                }
                shareTo(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.share_weixin:
//                if (UMShareAPI.get(mContext).isInstall(this, SHARE_MEDIA.WEIXIN)) {
//                } else {
//                    ToastManager.showShort(this, getString(R.string.no_install_weixin));
//                }
                shareTo(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.share_sina:
                shareTo(SHARE_MEDIA.SINA);
                break;
            case R.id.share_qq:
                shareTo(SHARE_MEDIA.QQ);
//                if (UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ)) {
//                } else {
//                    ToastManager.showShort(this, getString(R.string.no_install_qq));
//                }
                break;
            case R.id.share_qqzone:
                shareTo(SHARE_MEDIA.QZONE);
//                if (UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ)) {
//                } else {
//                    ToastManager.showShort(this, getString(R.string.no_install_qq));
//                }
                break;
            default:
                break;
        }
    }

    protected void onClickLink(){
        if (mCurrentImgBean == null || TextUtils.isEmpty(mCurrentImgBean.linkUrl)) {
            return;
        }
        Intent intent = new Intent(mContext, ActivityWebview.class);
        intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mCurrentImgBean.linkUrl);
        intent.putExtra(ActivityWebview.KEY_INTENT_WEB_TITLE, mCurrentImgBean.imgTitle);
        if (mActivity != null) {
            mActivity.startActivity(intent);
            mActivity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
        } else {
            mContext.startActivity(intent);
        }
    }

    protected void shareTo(final SHARE_MEDIA platfrom) {
        if (mCurrentImgBean == null) {
            LogHelper.d("wangzixu", "shareTo mCurrentImgBean = null");
            return;
        }

        if (mActivity == null) {
            LogHelper.d("wangzixu", "shareTo mActivity = null");
            return;
        }

        //好看埋点
        MaidianManager.setAction(mCurrentImgBean.imgId, App.sDID, 4, "", System.currentTimeMillis());

        if (TextUtils.isEmpty(mCurrentImgBean.shareUrl)) {
//            LogHelper.d("wangzixu", "shareTo mActivity  mCurrentImgBean.imgBigUrl = " + mCurrentImgBean.imgBigUrl);
            Glide.with(mContext).load(mCurrentImgBean.imgBigUrl)
                    .asBitmap()
                    .dontAnimate()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                            UMImage shareMedia = new UMImage(mContext, resource);//
                            new ShareAction(mActivity)
                                    .withMedia(shareMedia)
                                    .setCallback(mUMShareListener)
                                    .setPlatform(platfrom)
                                    .share();
                        }
                    });
        } else {
            UMWeb web = new UMWeb(mCurrentImgBean.shareUrl);
            web.setTitle(mCurrentImgBean.imgTitle);//标题
            web.setDescription(mCurrentImgBean.imgDesc);
            web.setThumb(new UMImage(mContext, mCurrentImgBean.imgSmallUrl));  //缩略图

            new ShareAction(mActivity)
                    .setPlatform(platfrom)
                    .withMedia(web)
                    .setCallback(mUMShareListener)
                    .share();
        }

    }

    private UMShareListener mUMShareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            ToastManager.showShort(mContext, "已分享");
            hideShareLayout();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastManager.showShort(mContext, "分享失败");
            LogHelper.d("share","分享失败:"+t);
            t.printStackTrace();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastManager.showShort(mContext, "分享取消");
        }
    };

    protected void onClickCollect(final View view) {
        if (mCurrentImgBean == null) {
            return;
        }

        //好看埋点
        MaidianManager.setAction(mCurrentImgBean.imgId, App.sDID, 3, mCurrentImgBean.isCollect != 0 ? "0" : "1", System.currentTimeMillis());

        if (mCurrentImgBean.isCollect != 0) {
            new ModelCollection().delCollection(mContext, mCurrentImgBean, new onDataResponseListener<Integer>() {
                @Override
                public void onStart() {
                    if (mActivity != null) {
                        mActivity.showLoadingDialog();
                    }
                }

                @Override
                public void onDataSucess(Integer integer) {
                    if (mActivity != null) {
                        mActivity.dismissLoadingDialog();
                    }
                    view.setSelected(false);
                    mCurrentImgBean.isCollect = 0;

                    EventCollectionChange change = new EventCollectionChange();
                    change.mIsAdd = false;
                    change.mFrom = CV_DetailPageView_Base.this;
                    change.imgIds = mCurrentImgBean.imgId;
                    EventBus.getDefault().post(change);
                }

                @Override
                public void onDataEmpty() {
                    if (mActivity != null) {
                        mActivity.dismissLoadingDialog();
                    }
                }

                @Override
                public void onDataFailed(String errmsg) {
                    if (mActivity != null) {
                        mActivity.dismissLoadingDialog();
                    }
                    ToastManager.showShort(mContext, "取消收藏失败: " + errmsg);
                }

                @Override
                public void onNetError() {
                    if (mActivity != null) {
                        mActivity.dismissLoadingDialog();
                    }
                    ToastManager.showNetErrorToast(mContext);
                }
            });
        } else {
            BeanCollection bean = BeanConvertUtil.mainImageBean2CollectionBean(mCurrentImgBean);
            new ModelCollection().addCollection(mContext, bean, new onDataResponseListener<BeanCollection>() {
                @Override
                public void onStart() {
                    if (mActivity != null) {
                        mActivity.showLoadingDialog();
                    }
                }

                @Override
                public void onDataSucess(BeanCollection collectionBean) {
                    if (mActivity != null) {
                        mActivity.dismissLoadingDialog();
                    }
                    view.setSelected(true);
                    mCurrentImgBean.isCollect = 1;

                    EventCollectionChange change = new EventCollectionChange();
                    change.mIsAdd = true;
                    change.mFrom = CV_DetailPageView_Base.this;
                    change.imgIds = mCurrentImgBean.imgId;
                    change.mBean = collectionBean;
                    EventBus.getDefault().post(change);
                }

                @Override
                public void onDataEmpty() {
                    if (mActivity != null) {
                        mActivity.dismissLoadingDialog();
                    }
                }

                @Override
                public void onDataFailed(String errmsg) {
                    if (mActivity != null) {
                        mActivity.dismissLoadingDialog();
                    }
                    ToastManager.showShort(mContext, "收藏失败: " + errmsg);
                }

                @Override
                public void onNetError() {
                    if (mActivity != null) {
                        mActivity.dismissLoadingDialog();
                    }
                    ToastManager.showNetErrorToast(mContext);
                }
            });
        }
    }

    protected void onClickBack() {
        if (mActivity != null) {
            mActivity.onBackPressed();
        }
    }

    protected void onClickSetting() {
        Intent i = new Intent(mContext, ActivityLockSetting.class);
        if (mActivity != null) {
            mActivity.startActivity(i);
            mActivity.startActivityAnim();
        } else {
            mContext.startActivity(i);
        }
    }

    protected void onClickBigImage() {
        if (mCurrentImgBean != null) {
            MaidianManager.setAction(mCurrentImgBean.imgId, App.sDID, 7, mIsCaptionShow ? "0" : "1", System.currentTimeMillis());
        }

        if (mIsCaptionShow) {
            hideCaption();
        } else {
            showCaption();
        }
    }

    protected void refreshBottomLayout() {
        if (mData.size() == 0) {
            return;
        }

        //处理点赞和收藏了
        refreshCollectNum(mCurrentImgBean);

        String desc = mCurrentImgBean.imgDesc;
        String cp_name = mCurrentImgBean.cpName;
        if (desc == null) {
            desc = "";
        }
        if (!TextUtils.isEmpty(cp_name) && !TextUtils.isEmpty(desc)) {
            String aa = new StringBuilder(desc).append(" @").append(cp_name).toString();
            mTvDescSimple.setText(aa);

        } else {
            mTvDescSimple.setText(desc);
        }

        mTvTitlle.setText(mCurrentImgBean.imgTitle);

        if (mTvCount.getVisibility() == VISIBLE) {
            mTvCount.setText(mCurrentPosition + "/" + (mData.size()));
        }

        if (TextUtils.isEmpty(mCurrentImgBean.linkUrl)) {
            mLayoutCaption.setOnClickListener(null);
            mTvLink.setVisibility(View.GONE);
        } else {
    //        mTvLink.setText(TextUtils.isEmpty(mCurrentImgBean.linkTitle) ? "查看更多" : mCurrentImgBean.linkTitle);
    //        mTvTitlle.setMaxWidth(mLayoutTitleLink.getWidth() - mTvLink.getMeasuredWidth());
            mLayoutCaption.setOnClickListener(this);
            mTvLinkBg.setColor(getLinkBgColor());
            mTvLink.setBackground(mTvLinkBg);
            mTvLink.setText("查看更多");
            mTvLink.setVisibility(View.VISIBLE);
        }


        // 设置标题、图说
        if (mIsCaptionShow
                && mLayoutMainBottom.getVisibility() != VISIBLE
//                && mCurrentImgBean.mBeanAdRes == null
                ) {
            showCaption();
        }
    }

    /**
     * 显示图说
     */
    public void showCaption() {
        if (mIsAnimnating) {
            return;
        }

        mLayoutMainBottom.setVisibility(View.VISIBLE);
        mLayoutMainTop.setVisibility(View.VISIBLE);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.setDuration(sAinmDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                mLayoutMainBottom.setAlpha(f);
                mLayoutMainTop.setAlpha(f);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimnating = false;
                mIsCaptionShow = true;
            }
        });

        int flags = 0;
        flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        flags = flags | View.SYSTEM_UI_FLAG_VISIBLE;
        setSystemUiVisibility(flags);
        mIsAnimnating = true;
        valueAnimator.start();
    }


    /**
     * 隐藏图说
     */
    public void hideCaption() {
        if (mIsAnimnating) {
            return;
        }

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.setDuration(sAinmDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                mLayoutMainBottom.setAlpha(1.0f-f);
                mLayoutMainTop.setAlpha(1.0f-f);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimnating = false;
                mIsCaptionShow = false;
                mLayoutMainBottom.setAlpha(1.0f);
                mLayoutMainTop.setAlpha(1.0f);
                mLayoutMainBottom.setVisibility(View.GONE);
                mLayoutMainTop.setVisibility(View.GONE);
            }
        });

        int flags = 0;
        flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        flags = flags | View.SYSTEM_UI_FLAG_FULLSCREEN;
        flags = flags | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        flags = flags | View.SYSTEM_UI_FLAG_IMMERSIVE;
        flags = flags | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        setSystemUiVisibility(flags);

        valueAnimator.start();
        mIsAnimnating = true;
    }

    public void showDownloadLayout() {//长按的选择框
        if (mCurrentImgBean == null || mDownloadLayout.getVisibility() == View.VISIBLE) {
            return;
        }

        mDownloadLayout.setVisibility(View.VISIBLE);

        Animation aBottom = AnimationUtils.loadAnimation(mContext, R.anim.view_bottom_in);
        mDownloadLayoutContent.startAnimation(aBottom);

        Animation aFadein = AnimationUtils.loadAnimation(mContext, R.anim.view_fade_in);
        mDownloadLayoutBgView.startAnimation(aFadein);
    }

    public void hideDownloadLayout() {
        if (mDownloadLayout.getVisibility() != View.VISIBLE) {
            return;
        }

        Animation aFadein = AnimationUtils.loadAnimation(mContext, R.anim.view_fade_out);
        mDownloadLayoutBgView.startAnimation(aFadein);

        Animation aBottom = AnimationUtils.loadAnimation(mContext, R.anim.view_bottom_out);
        aBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDownloadLayout.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mDownloadLayoutContent.startAnimation(aBottom);
    }

    public void showShareLayout() {
        if (mIsAnimnating || mCurrentImgBean == null || mShareLayout.getVisibility() == View.VISIBLE) {
            return;
        }

        Bitmap srcBitmap = mAdapterVpMain.getCurrentBitmap(mCurrentPosition);
        if (srcBitmap != null) {
            int srcH = srcBitmap.getHeight();
            int SrcW = srcBitmap.getWidth();
            int screenH = getResources().getDisplayMetrics().heightPixels;
            int cutH = (srcH * mShareLayoutH) / screenH;
            Bitmap sentBitmap = Bitmap.createBitmap(srcBitmap, 0, srcH - cutH, SrcW, cutH);
            Bitmap blurBitmap = BlurUtil.blurBitmap2(sentBitmap, 5, 4);
            BitmapDrawable drawable = new BitmapDrawable(getResources(), blurBitmap);
            drawable.setColorFilter(0xFF777777, PorterDuff.Mode.MULTIPLY);
            mShareBlurBgView.setImageDrawable(drawable);
        } else {
            mShareBlurBgView.setBackgroundColor(0xff777777);
        }

        mShareLayout.setVisibility(VISIBLE);
        mShareLayoutContent.setTranslationY(mShareLayoutH);
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0);
        animator.setDuration(sAinmDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                float top = f * mShareLayoutH;
                mShareLayoutContent.setTranslationY(top);
                mShareBlurBgView.setTopEdge((int) top);
                mLayoutMainBottom.setAlpha(f);
            }
        });

        mIsAnimnating = true;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimnating = false;
            }
        });
        animator.start();
    }

    public void hideShareLayout() {
        if (mIsAnimnating || mShareLayout.getVisibility() != View.VISIBLE) {
            return;
        }

        mShareLayoutContent.setTranslationY(0);
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);
        animator.setDuration(sAinmDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                float top = f * mShareLayoutH;
                mShareLayoutContent.setTranslationY(top);
                mShareBlurBgView.setTopEdge((int) top);
                mLayoutMainBottom.setAlpha(f);
            }
        });

        mIsAnimnating = true;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mShareLayout.setVisibility(View.GONE);
                mShareBlurBgView.setImageDrawable(null);
                mIsAnimnating = false;
            }
        });
        animator.start();
    }

    public void refreshCollectNum(MainImageBean bean) {
        if (bean == null) {
            return;
        }
        mTvBottomCollect.setSelected(bean.isCollect != 0);
    }


    public void downloadImage(@NonNull MainImageBean bean) {
        if (mCurrentImgBean == null) {
            return;
        }

        //好看埋点
        MaidianManager.setAction(mCurrentImgBean.imgId, App.sDID, 6, "", System.currentTimeMillis());

        ModelDownLoadImage.downLoadImg(mContext, bean, new onDataResponseListener<File>() {
            @Override
            public void onStart() {
                if (mActivity != null) {
                    mActivity.showLoadingDialog();
                }
            }

            @Override
            public void onDataSucess(File file) {
                LogHelper.d("wangzixu", "downloadImage file = " + file.getAbsolutePath() + ", filelength = " + file.length());
                App.sMainHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mActivity != null) {
                            mActivity.dismissLoadingDialog();
                        }
                        ToastManager.showShort(mContext, "下载成功");
                    }
                }, 1000);
            }

            @Override
            public void onDataEmpty() {
                if (mActivity != null) {
                    mActivity.dismissLoadingDialog();
                }
            }

            @Override
            public void onDataFailed(String errmsg) {
                if (mActivity != null) {
                    mActivity.dismissLoadingDialog();
                }
                ToastManager.showShort(mContext, "下载失败 : " + errmsg);
            }

            @Override
            public void onNetError() {
                if (mActivity != null) {
                    mActivity.dismissLoadingDialog();
                }
                ToastManager.showNetErrorToast(mContext);
            }
        });
    }

    //******设置为桌面 begin *******
    public void setWallPaper(@NonNull final MainImageBean bean) {
        if (bean == null) {
            return;
        }
        new ModelSetWallpaper().setWallPaper(mContext, bean.imgBigUrl, new onDataResponseListener() {
            @Override
            public void onStart() {
                if (mActivity != null) {
                    mActivity.showLoadingDialog();
                }
            }

            @Override
            public void onDataSucess(Object o) {
                if (mActivity != null) {
                    mActivity.dismissLoadingDialog();
                }
                ToastManager.showShort(mContext, "设置成功");
            }

            @Override
            public void onDataEmpty() {
                if (mActivity != null) {
                    mActivity.dismissLoadingDialog();
                }
                ToastManager.showShort(mContext, "设置失败");
            }

            @Override
            public void onDataFailed(String errmsg) {
                if (mActivity != null) {
                    mActivity.dismissLoadingDialog();
                }
                ToastManager.showShort(mContext, "设置失败");
            }

            @Override
            public void onNetError() {
                if (mActivity != null) {
                    mActivity.dismissLoadingDialog();
                }
                ToastManager.showShort(mContext, "设置失败");
            }
        });
    }

    private int[] mLinkBgColors={0xccF8546B,0xccF6A623,0xcc7ED321,0xcc417505,0xcc50E3C2,0xcc0986CD,0xccBD0FE1};
    private int mLingBgColorIndex = 0;
    public int getLinkBgColor(){
        int i = mLingBgColorIndex % mLinkBgColors.length;
        mLingBgColorIndex++;
        return mLinkBgColors[i];
    }

    @Subscribe
    public void onEvent(EventCollectionChange event) {
        if (this != event.mFrom) {
            if (event.mIsAdd) {
                BeanCollection bean = event.mBean;
                for (int i = 0; i < mData.size(); i++) {
                    MainImageBean mainImageBean = mData.get(i);
                    if (bean.imgId != null && bean.imgId.equals(mainImageBean.imgId)) {
                        mainImageBean.isCollect = 1;
                        if (mainImageBean == mCurrentImgBean) {
                            refreshCollectNum(mCurrentImgBean);
                        }
                    }
                }
            } else {
                String[] split = event.imgIds.split(",");
                for (int i = 0; i < mData.size(); i++) {
                    MainImageBean bean = mData.get(i);
                    for (int j = 0; j < split.length; j++) {
                        if (bean.imgId != null && bean.imgId.equals(split[j])) {
                            bean.isCollect = 0;
                            if (bean == mCurrentImgBean) {
                                refreshCollectNum(mCurrentImgBean);
                            }
                        }
                    }
                }
            }
        }
    }

    public void onDestory() {
        EventBus.getDefault().unregister(this);
    }
}
