package com.haokan.hklockscreen.lockscreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
import com.haokan.pubic.App;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.detailpage.CV_DetailPageView_Base;
import com.haokan.pubic.detailpage.CV_UnLockImageView;
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
public class CV_DetailPage_LockScreen extends CV_DetailPageView_Base implements CV_UnLockImageView.onUnLockListener {
    protected volatile boolean mIsDestory;
    protected boolean mIsLocked; //当前是否是锁屏状态
    private View mLayoutTime;
    private ImageView mIvSwitch;
    private TextView mTvLockTime;
    private TextView mTvLockData;
    private TextView mTvLockTitle;
    private TextView mTvLockLink;
    public static boolean mIsSwitching = false; //是否在换一换
    protected ArrayList<MainImageBean> mLocalImgData = new ArrayList<>(); //锁屏的数据分成两部分, 一部分是本地添加的照片, 一部分是网络更新的数据
    protected ArrayList<MainImageBean> mSwitchImgData = new ArrayList<>();
    private int mLocalLockIndex = 0;
    private boolean mIsFrist = true;

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
            showCaption();
            hideTimeLayout();
            ((Adapter_DetailPage_LockScreen)mAdapterVpMain).setCanUnLock(false);
            mIsLocked = false;
        } else {
            super.onClickBigImage();
        }
    }

    @Override
    protected void onClickBack() {
        if (!mIsLocked) {
            hideCaption();
            showTimeLayout();
            ((Adapter_DetailPage_LockScreen)mAdapterVpMain).setCanUnLock(true);
            mIsLocked = true;
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
     * 显示锁屏界面, 是否自动轮播到下一张
     */
    public void showLockScreenLayout(boolean scrollNext) {
        LogHelper.d("wangzixu", "lockscreenview screenOff");
        mIsLocked = true;

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
        mIsCaptionShow = false;

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

        //自动换下一张的逻辑
        if (scrollNext) {
            if (mData.size() <= 0) {
                return;
            }
            if (mLocalImgData.size() > 0) {
                mInitIndex = mData.size()*10 + mLocalLockIndex;
                mLocalLockIndex = (mLocalLockIndex+1)%mLocalImgData.size();
            } else {
                int indexOf = mData.indexOf(mCurrentImgBean);
                indexOf = indexOf + 1;
                if (indexOf >= mData.size()) {
                    indexOf = 0;
                }
                mInitIndex = mData.size()*10 + indexOf;
            }
            App.sMainHanlder.removeCallbacks(mPageSelectedDelayRunnable);
            mVpMain.setCurrentItem(mInitIndex, false);
        }
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
    }

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

                mSwitchImgData.clear();
                mSwitchImgData.addAll(mainImageBeen);

                mData.clear();
                mData.addAll(mLocalImgData);
                mData.addAll(mSwitchImgData);
                mInitIndex = mData.size() * 10 + mLocalImgData.size();
                setVpAdapter();
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

    public void loadLoclImgData() {
        new ModelLockScreen().getLocalImg(mContext, new onDataResponseListener<List<MainImageBean>>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDataSucess(List<MainImageBean> mainImageBeen) {
                mLocalImgData.clear();
                mLocalImgData.addAll(mainImageBeen);

                mData.clear();
                mData.addAll(mLocalImgData);
                mData.addAll(mSwitchImgData);
                mInitIndex = mData.size() * 10;
                setVpAdapter();

            }

            @Override
            public void onDataEmpty() {
                mLocalImgData.clear();

                mData.clear();
                mData.addAll(mSwitchImgData);
                mInitIndex = mData.size() * 10;
                setVpAdapter();

                if (mIsFrist) {
                    mIsFrist = false;
                    showLockScreenLayout(false);
                    LogHelper.d("wangzixu", "loadLoclImgData mData = " + mData.size());
                }
            }

            @Override
            public void onDataFailed(String errmsg) {

            }

            @Override
            public void onNetError() {

            }
        });
    }

    public void loadData() {
        new ModelLockScreen().getOffineLineSwitchData(mContext, new onDataResponseListener<List<MainImageBean>>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDataSucess(List<MainImageBean> mainImageBeen) {
                mSwitchImgData.clear();
                mSwitchImgData.addAll(mainImageBeen);
                LogHelper.d("wangzixu", "loadData mData = " + mSwitchImgData.size());
                loadLoclImgData();
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


    public void onDestory() {
        mIsDestory = true;
    }

    @Override
    public void onUnLockSuccess() {
        if (mActivity != null) {
            mActivity.finish();
        }
        CV_DetailPage_LockScreen.this.setVisibility(INVISIBLE);
        App.sMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                CV_DetailPage_LockScreen.this.setVisibility(VISIBLE);
                mLayoutTime.setAlpha(1.0f);
            }
        }, 500);
    }

    @Override
    public void onUnLockFailed() {
        mLayoutTime.setAlpha(1.0f);
    }

    @Override
    public void onUnLocking(float f) {
        float ff = 3.3f * f - 2.3f;
        mLayoutTime.setAlpha(ff);
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
                mLayoutTime.setVisibility(View.GONE);
            }
        });
        valueAnimator.start();
    }
}
