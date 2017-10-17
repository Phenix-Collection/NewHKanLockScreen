package com.haokan.hklockscreen.lockscreen.detailpage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreen.model.ModelLockScreen;
import com.haokan.pubic.App;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.detailpage.CV_DetailPageView_Base;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.util.LogHelper;
import com.haokan.pubic.util.ToastManager;
import com.haokan.pubic.util.Values;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2017/10/17.
 * 锁屏用的详情页, 类似一个fragment, 控制锁屏相关view的各种逻辑, 这样就和activity解耦开, 每次开启锁屏activity都可以用同一个锁屏view
 */
public class CV_DetailPage_LockScreen extends CV_DetailPageView_Base {
    protected volatile boolean mIsDestory;
    protected boolean mIsLocked; //当前是否是锁屏状态
    private View mLayoutTime;
    private ImageView mIvSwitch;
    private TextView mTvLockTime;
    private TextView mTvLockData;
    private TextView mTvLockTitle;
    private TextView mTvLockLink;
    public static boolean mIsSwitching = false; //是否在换一换
    protected ArrayList<MainImageBean> mPhotoData = new ArrayList<>(); //锁屏的数据分成两部分, 一部分是本地添加的照片, 一部分是网络更新的数据
    protected ArrayList<MainImageBean> mNetData = new ArrayList<>();

    public CV_DetailPage_LockScreen(@NonNull Context context) {
        this(context, null);
    }

    public CV_DetailPage_LockScreen(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_DetailPage_LockScreen(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(mContext).inflate(R.layout.cv_detailpage_lockscreen, this, false);
        addView(view);
        initViews(view);

        loadSwitchData();
    }

    private void initViews(View rootView) {
        View layoutSwitch = rootView.findViewById(R.id.layout_switch); //换一换
        mLayoutMainTop.setVisibility(GONE);
        mLayoutMainTop = layoutSwitch;//把base中的顶部view替换掉, 切面编程思想, 这样利用baseview中关于顶部view的

        layoutSwitch.findViewById(R.id.ll_switch).setOnClickListener(this);
        mIvSwitch = (ImageView) layoutSwitch.findViewById(R.id.iv_switch);
        layoutSwitch.setOnClickListener(this);

        mLayoutTime = rootView.findViewById(R.id.layout_time); //底部时间区域
        mTvLockTime = (TextView) mLayoutTime.findViewById(R.id.tv_time);
        mTvLockData = (TextView) mLayoutTime.findViewById(R.id.tv_data);
        mTvLockTitle = (TextView) mLayoutTime.findViewById(R.id.tv_title);
        mTvLockLink = (TextView) mLayoutTime.findViewById(R.id.tv_link);
        mTvLockLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_switch:
                if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
                    ToastManager.showNetErrorToast(mContext);
                    mIvSwitch.clearAnimation();
                    return;
                }

                boolean wifi = HttpStatusManager.isWifi(mContext);
                //如果是wifi, 或者允许在非wifi下换一换
                if (wifi || PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(Values.PreferenceKey.KEY_SP_SWITCH_WIFI, false)) {
                    loadSwitchData();
                } else {
                    final View switchDialog = findViewById(R.id.nowifi_switch_dialog);
                    View cancel = switchDialog.findViewById(R.id.cancel);
                    View confirm = switchDialog.findViewById(R.id.confirm);
                    final CheckBox checkBox = (CheckBox) switchDialog.findViewById(R.id.checkbox);
                    checkBox.setChecked(true);

                    OnClickListener listener = new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switchDialog.setVisibility(GONE);
                            if (v.getId() == R.id.confirm) {
                                if (checkBox.isChecked()) {//勾选存储
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                                    SharedPreferences.Editor edit = preferences.edit();
                                    edit.putBoolean(Values.PreferenceKey.KEY_SP_SWITCH_WIFI, true).apply();
                                }
                                loadSwitchData();
                            }
                        }
                    };
                    switchDialog.setOnClickListener(listener);
                    confirm.setOnClickListener(listener);
                    cancel.setOnClickListener(listener);
                    switchDialog.setVisibility(VISIBLE);
                }

                loadSwitchData();
                break;
            default:
                break;
        }
    }

    /**
     * 显示锁屏界面
     */
    public void showLockScreenLayout() {
        LogHelper.d("wangzixu", "lockscreenview screenOff");
        mIsLocked = true;
        App.sMainHanlder.removeCallbacks(mPageSelectedDelayRunnable);

        //隐藏分享界面
        if (mShareLayout.getVisibility() == VISIBLE) {
            mShareLayout.setVisibility(View.GONE);
            mShareBlurBgView.setImageDrawable(null);
        }

        //隐藏下载界面
        if (mDownloadLayout.getVisibility() == VISIBLE) {
            mDownloadLayout.setVisibility(GONE);
        }

        mLayoutTime.setVisibility(VISIBLE);

        //图说恢复高度
        mTvDescSimple.setVisibility(View.VISIBLE);
        mTvDescAll.setVisibility(View.GONE);

        //隐藏引导
//        if (mGestViewSwitch != null && mGestViewSwitch.getVisibility() == View.VISIBLE) {
//            mGestViewSwitch.setVisibility(View.GONE);
//        }
//        if (mGestViewSlide != null && mGestViewSlide.getVisibility() == View.VISIBLE) {
//            mGestViewSlide.setVisibility(View.GONE);
//        }

        mLayoutMainTop.setVisibility(GONE);
        mLayoutMainBottom.setVisibility(GONE);
        mIsAnimnating = false;
        mIsCaptionShow = true;

        if (mCurrentImgBean != null) {
            String linkTitle = mCurrentImgBean.linkTitle;
            if (TextUtils.isEmpty(mCurrentImgBean.linkUrl)) {
                linkTitle = "";
            } else {
                if (TextUtils.isEmpty(linkTitle)) {
                    linkTitle = "查看更过";
                }
            }

            mTvLockTitle.setText(mCurrentImgBean.imgTitle);

            if (TextUtils.isEmpty(linkTitle)) {
                mTvLockLink.setVisibility(GONE);
            } else {
                mTvLockLink.setVisibility(VISIBLE);
                mTvLockLink.setText(linkTitle);
            }
        }

        App.sMainHanlder.postDelayed(mSwitchImageRunnable, 300);
    }

    private Runnable mSwitchImageRunnable = new Runnable() {
        @Override
        public void run() {
            //新换图逻辑bein------------------------------------
            if (mLockImageBean == null) {//没锁定时
                LogHelper.e("times", "reset mLockImageBean == null");
                mData.clear();
                mData.addAll(mOfflineData);
                mData.addAll(mLocalData);

                if (mRefreshLocalImage) {
                    mRefreshLocalImage = false;
                    mInitIndex = mData.size() - 1;
                } else {
                    final int indexOf = mData.indexOf(mCurrentImgBean);
                    mInitIndex = indexOf + 1;
                    if (mInitIndex >= mData.size()) {
                        mInitIndex = 0;
                    }
                }

                mAdapterVpMain.notifyDataSetChanged();
                int offset = mData.size() * 10;
                mLockPositon = mInitIndex + offset;
                mVpMain.setCurrentItem(mLockPositon, false);
                mCurrentImgBean = mData.get(mInitIndex);
            } else {
                LogHelper.e("times", "reset pai xu111");
                boolean isLockedLocal = mLockImageBean.type == 3 && mLockImageBean.originalImagurl != null;
                boolean isLockedOffline = mLockImageBean.image_id != null && !mCurrentImgBean.image_id.equals(mLockImageBean.image_id);

                if (isLockedLocal || isLockedOffline) {//判断imgId
                    LogHelper.e("times", "reset pai xu22");
                    mData.clear();
                    mData.addAll(mOfflineData);
                    mData.addAll(mLocalData);

                    if (mRefreshLocalImage) {
                        mRefreshLocalImage = false;
                        mData.add(0, mLockImageBean);
                        mInitIndex = mData.size() - 1;
                    } else {
                        final int indexOf = mData.indexOf(mCurrentImgBean);
                        mInitIndex = indexOf + 1;
                        if (mInitIndex >= mData.size()) {
                            mInitIndex = 0;
                        }
                        mData.add(mInitIndex, mLockImageBean);
                    }

                    mAdapterVpMain = new AdapterVp_DetailMainView(mLocalResContext, mRemoteAppContext, mData, DetailPage_MainView.this, DetailPage_MainView.this, true, mLockImageBean, mAdData);
                    mVpMain.setAdapter(mAdapterVpMain);
//                mAdapterVpMain.notifyDataSetChanged();
                    int offset = mData.size() * 10;
                    mLockPositon = mInitIndex + offset;
                    mCurrentImgBean = mData.get(mInitIndex);

                    if (isLockedLocal && mLockImageBean != null && (!mLockImageBean.image_url.equals(mCurrentImgBean.image_url))) {//解决添加并锁定bug
                        for (int i = 0; i < mData.size(); i++) {
                            if (mData.get(i).image_url.equals(mLockImageBean.image_url)) {
                                mInitIndex = i;
                            }
                        }
                        mCurrentImgBean = mData.get(mInitIndex);
                        mLockPositon = mInitIndex + offset;
                    }

                    mVpMain.setCurrentItem(mLockPositon, false);

                    if (mLockImageBean == mCurrentImgBean) {
                        LogHelper.e("times", "----mLockImageBean == mCurrentImgBean");
                        mUnLockImg.setVisibility(VISIBLE);
                    }
                } else { //当前的图片就是锁定的图片, 所以队列不用变
                    LogHelper.e("times", "no reset mRefreshLocalImage == null");
                    //nothing
                    if (mRefreshLocalImage) {
                        mRefreshLocalImage = false;
                        mData.clear();
                        mData.add(mLockImageBean);
                        mData.addAll(mOfflineData);
                        mData.addAll(mLocalData);
                        mInitIndex = mData.size() - 1;

                        mAdapterVpMain = new AdapterVp_DetailMainView(mLocalResContext, mRemoteAppContext, mData, DetailPage_MainView.this, DetailPage_MainView.this, true, mLockImageBean, mAdData);
                        mVpMain.setAdapter(mAdapterVpMain);
//                mAdapterVpMain.notifyDataSetChanged();
                        int offset = mData.size() * 10;
                        mLockPositon = mInitIndex + offset;
                        mVpMain.setCurrentItem(mLockPositon, false);
                        mCurrentImgBean = mData.get(mInitIndex);
                    } else {
                        mUnLockImg.setVisibility(VISIBLE);
                    }
                }
            }
            String linkTitle = mCurrentImgBean.url_title;
            if (TextUtils.isEmpty(mCurrentImgBean.getUrl_click())) {
                linkTitle = "";
            } else {
                if (TextUtils.isEmpty(linkTitle)) {
                    linkTitle = mLocalResContext.getResources().getString(R.string.look_more);
                }
            }
            if (TextUtils.isEmpty(linkTitle)) {
                mTvTimeClickMore.setVisibility(INVISIBLE);
                mTvTimeTitle.setVisibility(GONE);
            } else {
                mTvTimeClickMore.setVisibility(VISIBLE);
                mTvTimeClickMore.setText(linkTitle);
                mTvTimeTitle.setVisibility(VISIBLE);
                mTvTimeTitle.setText(mCurrentImgBean.getTitle());
            }
//            mISystemUiView.setTitleAndUrl(mCurrentImgBean.getTitle(), linkTitle);
        }
    };

    protected void loadSwitchData() {
        new ModelLockScreen().getSwitchData(mContext, 1, new onDataResponseListener<List<MainImageBean>>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDataSucess(List<MainImageBean> mainImageBeen) {
                if (mIsDestory) {
                    return;
                }
                LogHelper.d("wangzixu", "loadSwitchData onDataSucess");

                refreshData(mainImageBeen);
            }

            @Override
            public void onDataEmpty() {
                LogHelper.d("wangzixu", "loadSwitchData onDataEmpty");
            }

            @Override
            public void onDataFailed(String errmsg) {
                LogHelper.d("wangzixu", "loadSwitchData errmsg = " + errmsg);
            }

            @Override
            public void onNetError() {
                LogHelper.d("wangzixu", "loadSwitchData loadData onDataFailed onNetError");
            }
        });
    }


    public void onDestory() {
        mIsDestory = true;
    }
}
