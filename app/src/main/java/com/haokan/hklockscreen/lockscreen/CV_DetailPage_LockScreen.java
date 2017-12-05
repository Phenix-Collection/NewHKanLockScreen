package com.haokan.hklockscreen.lockscreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.haokanAd.BeanAdRes;
import com.haokan.hklockscreen.haokanAd.ModelHaoKanAd;
import com.haokan.hklockscreen.haokanAd.onAdResListener;
import com.haokan.hklockscreen.haokanAd.request.BannerReq;
import com.haokan.hklockscreen.haokanAd.request.BidRequest;
import com.haokan.hklockscreen.lockscreenautoupdateimage.AlarmUtil;
import com.haokan.hklockscreen.recommendpageland.ActivityLandPageRecommend;
import com.haokan.hklockscreen.recommendpagelist.BeanRecommendItem;
import com.haokan.hklockscreen.setting.ActivityLockSetting;
import com.haokan.pubic.App;
import com.haokan.pubic.bean.BigImageBean;
import com.haokan.pubic.detailpage.CV_DetailPageView_Base;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.maidian.UmengMaiDianManager;
import com.haokan.pubic.util.MyDialogUtil;
import com.haokan.pubic.util.ToastManager;
import com.haokan.pubic.util.Values;
import com.haokan.pubic.webview.ActivityWebview;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangzixu on 2017/10/17.
 * 锁屏用的详情页, 类似一个fragment, 控制锁屏相关view的各种逻辑, 这样就和activity解耦开, 每次开启锁屏activity都可以用同一个锁屏view
 */
public class CV_DetailPage_LockScreen extends CV_DetailPageView_Base implements CV_UnLockImageView.onUnLockListener {
    protected volatile boolean mIsDestory;
    protected boolean mIsLocked = true; //当前是否是锁屏状态
    protected int mLockPosition; //当前锁屏的位置
    private View mLayoutTime;
    private ImageView mIvSwitch;
    private TextView mTvLockTime;
    private TextView mTvLockData;
    private TextView mTvLockTitle;
    private TextView mTvLockLink;
    public static boolean sIsSwitching = false; //是否在换一换
    protected ArrayList<BigImageBean> mLocalImgData = new ArrayList<>(); //锁屏的数据分成两部分, 一部分是本地添加的照片, 一部分是网络更新的数据
    protected ArrayList<BigImageBean> mSwitchImgData = new ArrayList<>();
    protected ArrayList<BigImageBean> mTempData = new ArrayList<>();
    private int mLocalLockIndex = 0; //本地图片循环时用的
    private int mNoLocalLockIndex = 0; //没有本地图片循环时用的
    private boolean mIsFrist = true;
    private TextView mTvSwitch;
    private BroadcastReceiver mReceiver;
    protected int mInitIndex; //初始在第几页
    private View mTimeTitleLayout;

    private BigImageBean mAdData5; //第5个位置和第11个位置的广告
    private BigImageBean mAdData11;
    //为动态插入第5帧广告而用的集合, 始终保持有5条数据, 广告数据向后接续
    //需要把date分成2份, [lockindex, lockindex+5]和[lockindex+6~~因为要往11位置~~lockindex]
    private ArrayList<BigImageBean> mImgDataForAd5 = new ArrayList<>();
    private View mLayoutSwitch;

    public CV_DetailPage_LockScreen(@NonNull Context context) {
        this(context, null);
    }

    public CV_DetailPage_LockScreen(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_DetailPage_LockScreen(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(mContext).inflate(R.layout.cv_detailpage_lockscreen, this, true);
//        addView(view);
        initViews(view);
        loadData(false);
    }

    private void initViews(View rootView) {
        View layoutTop = rootView.findViewById(R.id.lockscreen_layouttop); //换一换
        mLayoutMainTop.setVisibility(GONE);
        mLayoutMainTop = layoutTop;//把base中的顶部view替换掉, 切面编程思想, 这样利用baseview中关于顶部view的

        layoutTop.findViewById(R.id.backlockscreen).setOnClickListener(mLockClickListener);

        mLayoutSwitch = layoutTop.findViewById(R.id.ll_switch);
        mIvSwitch = (ImageView) mLayoutSwitch.findViewById(R.id.iv_switch);
        mTvSwitch = (TextView) mLayoutSwitch.findViewById(R.id.tv_switch);
//        mLayoutSwitch.setOnClickListener(this);


        mLayoutTime = rootView.findViewById(R.id.layout_time); //底部时间区域
        mTvLockTime = (TextView) mLayoutTime.findViewById(R.id.tv_time);
        mTvLockData = (TextView) mLayoutTime.findViewById(R.id.tv_data);
        mTimeTitleLayout = mLayoutTime.findViewById(R.id.time_title_layout);
        mTvLockTitle = (TextView) mLayoutTime.findViewById(R.id.tv_title);
        mTvLockLink = (TextView) mLayoutTime.findViewById(R.id.tv_link);
        mTvLockLink.setOnClickListener(mLockClickListener);

        setTime();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_TIME_TICK.equals(action)) {
                    setTime();
                } else if ("com.haokan.receiver.autoupdateimage".equals(action)) { //自动更新了图片
                    LogHelper.d("wangzixu", "autoupdate 收到了更新广播");
                    LogHelper.writeLog(mContext, "autoupdate 收到了更新广播");
                    loadSwitchOfflineData(true);
                } else if ("com.haokan.receiver.localimagechange".equals(action)) { //本地相册变化了
                    LogHelper.d("wangzixu", "localimagechange 本地相册变化");
                    loadData(true);
                } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    if (mIsLocked && mCurrentImgBean != null) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("action", "亮屏");
                        if (mCurrentImgBean.mBeanAdRes != null) {
                            map.put("from", "广告图片");
                        } else if (mCurrentImgBean.myType == 3){
                            map.put("from", "我的相册图片");
                        } else {
                            map.put("from", "离线图片");
                        }
                        UmengMaiDianManager.onEvent(mContext, "event_074", map);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction("com.haokan.receiver.autoupdateimage");
        filter.addAction("com.haokan.receiver.localimagechange");
        mContext.registerReceiver(mReceiver, filter);

        AlarmUtil.setOfflineAlarm(mContext);
    }

    private OnClickListener mLockClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_link:
                    onClickLink();
                    break;
                case R.id.backlockscreen:
                    intoLockScreenState(false);
                    UmengMaiDianManager.onEvent(mContext, "event_069");
                    break;
                case R.id.ll_switch:
                    pullToSwitch();
                    break;
                default:
                    break;
            }
        }
    };

    public void pullToSwitch() {
        if (sIsSwitching) {
            return;
        }
        if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
            ToastManager.showNetErrorToast(mContext);
            setIvSwitching(false);
            return;
        }

        boolean wifi = HttpStatusManager.isWifi(mContext);
        //如果是wifi, 或者允许在非wifi下换一换
        if (wifi || PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(Values.PreferenceKey.KEY_SP_SWITCH_NOWIFI, false)) {
            loadSwitchData();
        } else {
            if (mActivity == null) {
                ToastManager.showCenter(mContext, "未设置Activity, 无法弹窗");
                return;
            }
            MyDialogUtil.showMyDialog(mActivity, "提示", "当前在非wifi环境下, 将会耗费您的移动流量更新图片", null, null
                    , true, new MyDialogUtil.myDialogOnClickListener() {
                        @Override
                        public void onClickCancel() {
                            //nothing
                        }

                        @Override
                        public void onClickConfirm(boolean checked) {
                            if (checked) {//勾选存储
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putBoolean(Values.PreferenceKey.KEY_SP_SWITCH_NOWIFI, true).apply();
                            }
                            loadSwitchData();
                        }
                    });

//            View cv = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_nowifi_switch, null);
//            final CheckBox checkBox = (CheckBox) cv.findViewById(R.id.checkbox);
//            checkBox.setChecked(true);
//
//            final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
//                    .setTitle("提示")
//                    .setView(cv)
//                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    }).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (checkBox.isChecked()) {//勾选存储
//                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
//                                SharedPreferences.Editor edit = preferences.edit();
//                                edit.putBoolean(Values.PreferenceKey.KEY_SP_SWITCH_NOWIFI, true).apply();
//                            }
//                            loadSwitchData();
//                        }
//                    });
//            AlertDialog alertDialog = builder.create();
//            alertDialog.setCancelable(false);
//            alertDialog.show();
        }
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    public CV_UnLockImageView getUnLockView() {
        ImageView imageView = mAdapterVpMain.getCurrentImageView(mCurrentPosition);
        if (imageView != null && imageView instanceof CV_UnLockImageView) {
            return (CV_UnLockImageView) imageView;
        }
        return null;
    }

    private void setTime() {
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间

        ContentResolver cv = mContext.getContentResolver();
//        String strTimeFormat = Settings.System.getString(cv, Settings.System.TIME_12_24);
//        String strF = "hh:mm";
//        if ("24".equals(strTimeFormat)) {
//        }
        String strF = "HH:mm";
        SimpleDateFormat fTime = new SimpleDateFormat(strF);
        String time = fTime.format(curDate);
        mTvLockTime.setText("" + time);

        SimpleDateFormat fData = new SimpleDateFormat("E  MM月dd日");
        String data = fData.format(curDate);
        mTvLockData.setText(data);
    }

    @Override
    public boolean onLongClick(View v) {
        if (mIsLocked) {
            //nothing
        } else {
            super.onLongClick(v);
        }
        return true;
    }

    @Override
    protected void onClickBigImage() {
        if (mIsLocked) {//点击解锁
            intoDetialPageState();
        } else {
            if (mCurrentImgBean != null && mCurrentImgBean.mBeanAdRes != null) {
                Intent intent = new Intent(mContext, ActivityWebview.class);
//                Intent intent = new Intent(mContext, ActivityWebviewForLockPage.class);
                intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mCurrentImgBean.mBeanAdRes.landPageUrl);
                if (mActivity != null) {
                    mActivity.startActivity(intent);
                    mActivity.startActivityAnim();
                } else {
                    mContext.startActivity(intent);
                }
            } else {
                super.onClickBigImage();

                if (mCurrentImgBean != null) {
                    if (mIsCaptionShow) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("type", mCurrentImgBean.typeName);
                        UmengMaiDianManager.onEvent(mContext, "event_072", map);
                    }
                }
            }
        }
    }

    @Override
    protected void onClickBack() {
        if (!mIsLocked) {
            intoLockScreenState(false);
            UmengMaiDianManager.onEvent(mContext, "event_068");
        }
    }

    @Override
    public void setVpAdapter() {
        mAdapterVpMain = new Adapter_DetailPage_LockScreen(mContext, mData, this, this);
        mVpMain.setAdapter(mAdapterVpMain);

        if (mData.size() > 0) {
            if (mInitIndex == 0) {
                onPageSelected(0);
            } else {
                mVpMain.setCurrentItem(mInitIndex, false);
            }
        }
    }

    @Override
    protected void onClickLink() {
        if (mCurrentImgBean == null) {
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("type", mCurrentImgBean.typeName);
        UmengMaiDianManager.onEvent(mContext, "event_064", map);

        if (TextUtils.isEmpty(mCurrentImgBean.linkUrl)) {
//            Intent intent = new Intent(mContext, ActivityLandPageForLockPage.class);
            Intent intent = new Intent(mContext, ActivityLandPageRecommend.class);
            BeanRecommendItem beanRecommendItem = new BeanRecommendItem();
            beanRecommendItem.GroupId = mCurrentImgBean.jump_id;
            if (TextUtils.isEmpty(beanRecommendItem.GroupId)) {
                beanRecommendItem.GroupId = mCurrentImgBean.imgId;
            }
            beanRecommendItem.cover = mCurrentImgBean.imgSmallUrl;
            beanRecommendItem.urlClick = mCurrentImgBean.shareUrl;
            beanRecommendItem.imgTitle = mCurrentImgBean.imgTitle;
            beanRecommendItem.imgDesc = mCurrentImgBean.imgDesc;
            intent.putExtra(ActivityLandPageRecommend.KEY_INTENT_RECOMMENDBEAN, beanRecommendItem);
            if (mActivity != null) {
                mActivity.startActivity(intent);
                mActivity.startActivityAnim();
            } else {
                mContext.startActivity(intent);
            }
        } else {
//            Intent intent = new Intent(mContext, ActivityWebviewForLockPage.class);
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
    }

    protected void onClickCollect(final View view) {
        super.onClickCollect(view);
        if (mCurrentImgBean != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("type", mCurrentImgBean.typeName);
            UmengMaiDianManager.onEvent(mContext, "event_070", map);
        }
    }

    @Override
    public void downloadImage(@NonNull BigImageBean bean) {
        super.downloadImage(bean);
        if (mCurrentImgBean != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("type", mCurrentImgBean.typeName);
            UmengMaiDianManager.onEvent(mContext, "event_071", map);
        }
    }

    /**
     * 进入详情页状态
     */
    public void intoDetialPageState() {
        if (mCurrentImgBean != null && mCurrentImgBean.mBeanAdRes != null) { //是广告
            //显示顶部, 不显示底部
            mIsCaptionShow = true;
            mLayoutMainTop.setVisibility(View.VISIBLE);

            int flags = 0;
            flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags = flags | View.SYSTEM_UI_FLAG_VISIBLE;
            setSystemUiVisibility(flags);
        } else {
            showCaption();
        }
        hideTimeLayout();
        ((Adapter_DetailPage_LockScreen)mAdapterVpMain).setCanUnLock(false);
        mIsLocked = false;
        if (mOnLockScreenStateChangeListener != null) {
            mOnLockScreenStateChangeListener.onLockScreenStateChange(mIsLocked);
        }
    }

    /**
     * 进入锁屏状态, 是否自动滚动向下一张
     */
    public void intoLockScreenState(boolean scrollNext) {
        if (mData.size() == 0) {
            loadData(false);
            return;
        }

        LogHelper.d("wangzixu", "lockscreenview intoLockScreenState scrollNext = " + scrollNext);
//        Thread.dumpStack();

        //隐藏分享界面
        if (mShareLayout.getVisibility() == VISIBLE) {
            mShareLayout.setVisibility(View.GONE);
            mShareBlurBgView.setImageDrawable(null);
        }

        //隐藏下载界面
        if (mDownloadLayout.getVisibility() == VISIBLE) {
            mDownloadLayout.setVisibility(GONE);
        }

        //显示锁屏时间界面
        showTimeLayout();
        hideCaption();

        mIsLocked = true;

        //自动换下一张的逻辑
        if (scrollNext) {
            if (mLocalImgData.size() > 0) {
                mLocalLockIndex = (mLocalLockIndex+1)%mLocalImgData.size();
                mInitIndex = mTempData.size()*9+mLocalLockIndex;
                mCurrentPosition = mInitIndex;
            } else {
                mNoLocalLockIndex = (mNoLocalLockIndex+1)%mTempData.size();
                mInitIndex = mTempData.size()*9+mNoLocalLockIndex;
                mCurrentPosition = mInitIndex;

//                int indexOf = mCurrentPosition + 1;
//                if (indexOf >= mData.size()) {
//                    indexOf = 0;
//                }
//                mInitIndex = mData.size()*10 + indexOf;
//                mCurrentPosition = indexOf;
            }

            mLockPosition = mCurrentPosition;
            mCurrentImgBean = mData.get(mCurrentPosition);

            App.sMainHanlder.removeCallbacks(mPageSelectedDelayRunnable);
            if (mAdData5 != null) {
                mData.remove(mAdData5);
            }
            if (mAdData11 != null) {
                mData.remove(mAdData11);
            }
            mAdapterVpMain.notifyDataSetChanged();
            mVpMain.setCurrentItem(mInitIndex, false);
            mHasLoadAd5 = false;
            mHasLoadAd11 = false;
        }

        //处理时间界面上的一些标题等信息
        if (mCurrentImgBean != null) {
            if (mCurrentImgBean.imgTitle == null) {
                mCurrentImgBean.imgTitle = "";
            }
            mTvLockTitle.setText(mCurrentImgBean.imgTitle);

            if (mCurrentImgBean.myType == 3) {
                mTvLockLink.setVisibility(GONE);
            } else {
                mTvLockLink.setBackground(mTvLinkBg);
                mTvLockLink.setVisibility(VISIBLE);
                mTvLockLink.setText("查看更多");
            }
        }

        ((Adapter_DetailPage_LockScreen)mAdapterVpMain).setCanUnLock(true);
        mLockPosition = mCurrentPosition; //记录下锁屏的位置, 滑动解锁使用到, 来判断是否是滑动解锁
        if (mOnLockScreenStateChangeListener != null) {
            mOnLockScreenStateChangeListener.onLockScreenStateChange(mIsLocked);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;

        if (mIsLocked && mLockPosition != mCurrentPosition) {
            App.sMainHanlder.removeCallbacks(mPageSelectedDelayRunnable);
            mCurrentImgBean = mData.get(mCurrentPosition);
            mPageSelectedDelayRunnable.run();

            //解锁
            intoDetialPageState();
            LogHelper.d("wangzixu", "mLockPosition = " + mLockPosition + ", position = " + position);
        } else {
            super.onPageSelected(position);
        }

        if (mCurrentImgBean.mBeanAdRes != null) {
            if (mLayoutMainBottom.getVisibility() == VISIBLE) {
                mLayoutMainBottom.setVisibility(INVISIBLE);
            }
            //广告展示上报
            ModelHaoKanAd.adShowUpLoad(mCurrentImgBean.mBeanAdRes.showUpUrl);
        } else if (mCurrentImgBean.myType == 3) {
            mLocalLockIndex = position;
        } else {
            mNoLocalLockIndex = position;
        }

        if (!mHasLoadAd5 && position == mLockPosition + 1) {
            loadHaoKanAdDate5(position);
        }

        if (!mHasLoadAd11 && position == mLockPosition + 5) {
            loadHaoKanAdDate11(position);
        }

        if (mCurrentImgBean != null && !mIsLocked) {
            HashMap<String, String> map = new HashMap<>();
            map.put("action", "滑动");
            if (mCurrentImgBean.mBeanAdRes != null) {
                map.put("from", "广告图片");
            } else if (mCurrentImgBean.myType == 3){
                map.put("from", "我的相册图片");
            } else {
                map.put("from", "离线图片");
            }
            UmengMaiDianManager.onEvent(mContext, "event_074", map);
        }
    }

    public void onResume() {
        if (mCurrentImgBean != null && mCurrentImgBean.mBeanAdRes != null) {
            //广告展示上报
            ModelHaoKanAd.adShowUpLoad(mCurrentImgBean.mBeanAdRes.showUpUrl);
        }
    }

    private boolean mHasLoadAd5;
    private boolean mHasLoadAd11;
    protected void loadHaoKanAdDate5(final int position) {
        //第5个位置的广告
        mHasLoadAd5 = true;

        BannerReq bannerReq = new BannerReq();
        bannerReq.w = 1080;
        bannerReq.h = 1920;
        BidRequest request = ModelHaoKanAd.getBidRequest("28-53-206", 5, null, bannerReq);

        ModelHaoKanAd.getAd(mContext, request, new onAdResListener<BeanAdRes>() {
            @Override
            public void onAdResSuccess(BeanAdRes adRes) {
                if (mCurrentPosition >= position+4) {
                    return;
                }
                LogHelper.d("wangzixu", "ModelHaoKanAd loadAdData 28-53-206 onADSuccess");
                BigImageBean imageBean = new BigImageBean();
                imageBean.mBeanAdRes = adRes;
                imageBean.imgBigUrl = adRes.imgUrl;
                imageBean.imgSmallUrl = adRes.imgUrl;
                imageBean.shareUrl = adRes.landPageUrl;
                mAdData5 = imageBean;
                mData.add(position+4, imageBean);
                mAdapterVpMain.notifyDataSetChanged();
            }

            @Override
            public void onAdResFail(String errmsg) {
                LogHelper.d("wangzixu", "HaokanADManager loadAdData 28-53-206 onADError errmsg = " + errmsg);
            }
        });
    }

    protected void loadHaoKanAdDate11(final int position) {
        //第11个位置的广告
        mHasLoadAd11 = true;
        BannerReq bannerReq = new BannerReq();
        bannerReq.w = 1080;
        bannerReq.h = 1920;
        BidRequest request = ModelHaoKanAd.getBidRequest("28-53-207", 5, null, bannerReq);

        ModelHaoKanAd.getAd(mContext, request, new onAdResListener<BeanAdRes>() {
            @Override
            public void onAdResSuccess(BeanAdRes adRes) {
                if (mCurrentPosition >= position+5) {
                    return;
                }

                LogHelper.d("wangzixu", "ModelHaoKanAd loadAdData 28-53-206 onADSuccess");
                BigImageBean imageBean = new BigImageBean();
                imageBean.mBeanAdRes = adRes;
                imageBean.imgBigUrl = adRes.imgUrl;
                imageBean.imgSmallUrl = adRes.imgUrl;
                imageBean.shareUrl = adRes.landPageUrl;
                mAdData11 = imageBean;
                mData.add(position+5, imageBean);
                mAdapterVpMain.notifyDataSetChanged();
            }

            @Override
            public void onAdResFail(String errmsg) {
                LogHelper.d("wangzixu", "HaokanADManager loadAdData 28-53-206 onADError errmsg = " + errmsg);
            }
        });
    }

    public void setIvSwitching(boolean isUpdating) {
        if (isUpdating) {
            sIsSwitching = true;
            mLayoutSwitch.setVisibility(VISIBLE);
            mTvSwitch.setText("更新中...");
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.switch_updating_anim);
            animation.setInterpolator(new LinearInterpolator());
            mIvSwitch.startAnimation(animation);
        } else {
            sIsSwitching = false;
            mLayoutSwitch.setVisibility(GONE);
            mTvSwitch.setText("换一换");
            mIvSwitch.clearAnimation();
        }
    }

    private int mSwitchDataPage = 1;
    protected void loadSwitchData() {
        ModelLockScreen.getSwitchData(mContext, mSwitchDataPage, new onDataResponseListener<List<BigImageBean>>() {
            @Override
            public void onStart() {
                setIvSwitching(true);
            }

            @Override
            public void onDataSucess(List<BigImageBean> mainImageBeen) {
                if (mIsDestory) {
                    return;
                }

                LogHelper.d("wangzixu", "loadSwitchData onDataSucess");

                mSwitchImgData.clear();
                mSwitchImgData.addAll(mainImageBeen);

                refreshData(true);

                setIvSwitching(false);
                mSwitchDataPage++;
//                modelLockScreen.saveSwitchData(mContext, mSwitchImgData);
            }

            @Override
            public void onDataEmpty() {
                setIvSwitching(false);
                LogHelper.d("wangzixu", "loadSwitchData onDataEmpty");
            }

            @Override
            public void onDataFailed(String errmsg) {
                setIvSwitching(false);
                LogHelper.d("wangzixu", "loadSwitchData errmsg = " + errmsg);
            }

            @Override
            public void onNetError() {
                setIvSwitching(false);
                LogHelper.d("wangzixu", "loadSwitchData loadData onDataFailed onNetError");
            }
        });
    }

    /**
     * 刷新完后是显示本地的数据, 还是显示网络图片
     * @param showOfflineImage
     */
    public void refreshData(boolean showOfflineImage) {
        mData.clear();
        mTempData.clear();
        mTempData.addAll(mLocalImgData);
        mTempData.addAll(mSwitchImgData);
        for (int i = 0; i < 30; i++) { //为了实现伪无限循环
            mData.addAll(mTempData);
        }
        setVpAdapter();

        if (showOfflineImage) {
            mInitIndex = mTempData.size() * 10 + mLocalImgData.size();
        } else {
            mInitIndex = mTempData.size() * 10;
        }

        if (mIsFrist || mIsLocked) {
            mIsFrist = false;
            mCurrentPosition = mInitIndex;
            mLockPosition = mCurrentPosition;
            mCurrentImgBean = mData.get(mCurrentPosition);
            intoLockScreenState(false);
        }

        if (mInitIndex == 0) {
            onPageSelected(0);
        } else {
            mVpMain.setCurrentItem(mInitIndex, false);
        }

        if (showOfflineImage) {
            LogHelper.d("wangzixu", "autoupdate 自动更新完成");
            LogHelper.writeLog(mContext, "autoupdate 自动更新完成");
        }
    }

    public void loadSwitchOfflineData(final boolean showOfflineImage) {
        ModelLockScreen.getOffineSwitchData(mContext, new onDataResponseListener<List<BigImageBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(List<BigImageBean> mainImageBeen) {
                mSwitchImgData.clear();
                mSwitchImgData.addAll(mainImageBeen);

                refreshData(showOfflineImage);
            }

            @Override
            public void onDataEmpty() {
                mSwitchImgData.clear();

                refreshData(showOfflineImage);
            }

            @Override
            public void onDataFailed(String errmsg) {
                LogHelper.d("wangzixu", "loadSwitchOfflineData onDataFailed errmsg = " + errmsg);
            }

            @Override
            public void onNetError() {
                LogHelper.d("wangzixu", "loadSwitchOfflineData onNetError");
            }
        });
    }

    /**
     * @param onlyLocalImage  是否只加载本地图片
     */
    public void loadData(final boolean onlyLocalImage) {
        ModelLockScreen.getLocalImg(mContext, new onDataResponseListener<List<BigImageBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(List<BigImageBean> mainImageBeen) {
                mLocalImgData.clear();
                mLocalImgData.addAll(mainImageBeen);
                LogHelper.d("wangzixu", "localimagechange 本地相册变化 mainImageBeen size = " + mainImageBeen.size());
                if (onlyLocalImage) {
                    refreshData(false);
                } else {
                    loadSwitchOfflineData(false);
                }
            }

            @Override
            public void onDataEmpty() {
                mLocalImgData.clear();
                if (onlyLocalImage) {
                    refreshData(false);
                } else {
                    loadSwitchOfflineData(false);
                }
            }

            @Override
            public void onDataFailed(String errmsg) {
                LogHelper.d("wangzixu", "getLocalImg onDataFailed errmsg = " + errmsg);
                if (onlyLocalImage) {
                    //nothing
                } else {
                    loadSwitchOfflineData(false);
                }
            }

            @Override
            public void onNetError() {
                LogHelper.d("wangzixu", "getLocalImg onNetError");
                if (onlyLocalImage) {
                    //nothing
                } else {
                    loadSwitchOfflineData(false);
                }
            }
        });
    }

    @Override
    protected void onClickSetting() {
//        Intent i = new Intent(mContext, ActivitySettingForLockPage.class);
        Intent i = new Intent(mContext, ActivityLockSetting.class);
        if (mActivity != null) {
            mActivity.startActivityForResult(i, 201); //锁屏上打开设置页
            mActivity.startActivityAnim();
        } else {
            mContext.startActivity(i);
        }
        UmengMaiDianManager.onEvent(mContext, "event_066");
    }

    @Override
    protected void shareTo(SHARE_MEDIA platfrom) {
        super.shareTo(platfrom);

        if (mCurrentImgBean != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("type", mCurrentImgBean.typeName);
            UmengMaiDianManager.onEvent(mContext, "event_067", map);
        }
    }

    @Override
    public void onUnLockSuccess() {
        LogHelper.d("wangzixu", "ActivityLockScreen onUnLockSuccess");
        if (mActivity != null) {
            mActivity.finish();
        }
        CV_DetailPage_LockScreen.this.setVisibility(INVISIBLE);
        mIsLocked = false;
        App.sMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                CV_DetailPage_LockScreen.this.setVisibility(VISIBLE);
                mLayoutTime.setAlpha(1.0f);
                mLayoutMainBottom.setAlpha(1.0f);
            }
        }, 500);

        UmengMaiDianManager.onEvent(mContext, "event_065");
    }

    @Override
    public void onUnLockFailed() {
        mLayoutTime.setAlpha(1.0f);
        mLayoutMainBottom.setAlpha(1.0f);
    }

    @Override
    public void onUnLocking(float f) {
        float ff = 3.3f * f - 2.3f;
        mLayoutTime.setAlpha(ff);
        mLayoutMainBottom.setAlpha(ff);
    }

    /**
     * 显示图说
     */
    public void showTimeLayout() {
        mLayoutTime.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.setDuration(sAinmDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                mLayoutTime.setAlpha(f);
            }
        });
        valueAnimator.start();
    }

    public boolean isShowLongClickLayout() {
        return mDownloadLayout.getVisibility() == VISIBLE;
    }


    /**
     * 隐藏图说
     */
    public void hideTimeLayout() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.setDuration(sAinmDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                mLayoutTime.setAlpha(1.0f-f);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLayoutTime.setAlpha(1.0f);
                mLayoutTime.setVisibility(View.GONE);
            }
        });
        valueAnimator.start();
    }

    private OnLockScreenStateChangeListener mOnLockScreenStateChangeListener;
    public interface OnLockScreenStateChangeListener{
        void onLockScreenStateChange(boolean isLock);
    }
    public void setOnLockScreenStateListener(OnLockScreenStateChangeListener listener) {
        mOnLockScreenStateChangeListener = listener;
    }

    public void onDestory() {
        super.onDestory();
        mIsDestory = true;
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }
}
