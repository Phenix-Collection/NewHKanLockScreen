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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.App;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.detailpage.CV_DetailPageView_Base;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.util.LogHelper;
import com.haokan.pubic.util.ToastManager;
import com.haokan.pubic.util.Values;
import com.haokan.pubic.webview.ActivityWebview;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2017/10/17.
 * 锁屏用的详情页, 类似一个fragment, 控制锁屏相关view的各种逻辑, 这样就和activity解耦开, 每次开启锁屏activity都可以用同一个锁屏view
 */
public class CV_DetailPage_LockScreen extends CV_DetailPageView_Base implements CV_UnLockImageView.onUnLockListener {
    protected volatile boolean mIsDestory;
    protected boolean mIsLocked; //当前是否是锁屏状态
    protected int mLockPosition; //当前锁屏的位置
    private View mLayoutTime;
    private ImageView mIvSwitch;
    private TextView mTvLockTime;
    private TextView mTvLockData;
    private TextView mTvLockTitle;
    private TextView mTvLockLink;
    public static boolean sIsSwitching = false; //是否在换一换
    protected ArrayList<MainImageBean> mLocalImgData = new ArrayList<>(); //锁屏的数据分成两部分, 一部分是本地添加的照片, 一部分是网络更新的数据
    protected ArrayList<MainImageBean> mSwitchImgData = new ArrayList<>();
    private int mLocalLockIndex = 0;
    private boolean mIsFrist = true;
    private TextView mTvSwitch;
    private BroadcastReceiver mTimeTickReceiver;

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
        mTvSwitch = (TextView) layoutSwitch.findViewById(R.id.tv_switch);
        layoutSwitch.setOnClickListener(this);

        mLayoutTime = rootView.findViewById(R.id.layout_time); //底部时间区域
        mTvLockTime = (TextView) mLayoutTime.findViewById(R.id.tv_time);
        mTvLockData = (TextView) mLayoutTime.findViewById(R.id.tv_data);
        mTvLockTitle = (TextView) mLayoutTime.findViewById(R.id.tv_title);
        mTvLockLink = (TextView) mLayoutTime.findViewById(R.id.tv_link);
        mTvLockLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivityWebview.class);
                intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mCurrentImgBean.linkUrl);
                mContext.startActivity(intent);
//                    if (mActivity != null) {
//                        mActivity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
//                    }
            }
        });

        setTime();
        mTimeTickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_TIME_TICK.equals(action)) {
                    setTime();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        mContext.registerReceiver(mTimeTickReceiver, filter);
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    public CV_UnLockImageView getUnLockView() {
        if (mAdapterVpMain instanceof Adapter_DetailPage_LockScreen) {
            return ((Adapter_DetailPage_LockScreen)mAdapterVpMain).getCurrentImageView(mCurrentPosition);
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
            super.onClickBigImage();
        }
    }

    @Override
    protected void onClickBack() {
        intoLockScreenState(false);
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
                if (sIsSwitching) {
                    return;
                }
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
                break;
            default:
                break;
        }
    }

    /**
     * 进入详情页状态
     */
    public void intoDetialPageState() {
        showCaption();
        hideTimeLayout();
        ((Adapter_DetailPage_LockScreen)mAdapterVpMain).setCanUnLock(false);
        mIsLocked = false;
        if (mOnLockScreenStateChangeListener != null) {
            mOnLockScreenStateChangeListener.onLockScreenStateChange(mIsLocked);
        }
    }

    /**
     * 进入锁屏状态
     */
    public void intoLockScreenState(boolean scrollNext) {
        if (mData.size() == 0) {
            loadData();
            return;
        }

        LogHelper.d("wangzixu", "lockscreenview intoLockScreenState scrollNext = " + scrollNext);

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
//        mLayoutTime.setVisibility(VISIBLE);
//        mLayoutMainTop.setVisibility(GONE);
//        mLayoutMainBottom.setVisibility(GONE);
//        mIsCaptionShow = false;


        //图说恢复高度
        mTvDescSimple.setVisibility(View.VISIBLE);
        mTvDescAll.setVisibility(View.GONE);

        //自动换下一张的逻辑
        if (scrollNext) {
            if (mLocalImgData.size() > 0) {
                mInitIndex = mData.size()*10 + mLocalLockIndex;
                mLocalLockIndex = (mLocalLockIndex+1)%mLocalImgData.size();
                mCurrentPosition = mInitIndex;
            } else {
                int indexOf = mData.indexOf(mCurrentImgBean);
                indexOf = indexOf + 1;
                if (indexOf >= mData.size()) {
                    indexOf = 0;
                }
                mInitIndex = mData.size()*10 + indexOf;
                mCurrentPosition = indexOf;
            }

            mLockPosition = mCurrentPosition;
            mCurrentImgBean = mData.get(mCurrentPosition);

            App.sMainHanlder.removeCallbacks(mPageSelectedDelayRunnable);
            mVpMain.setCurrentItem(mInitIndex, false);
        }

        //处理时间界面上的一些标题等信息
        if (mCurrentImgBean != null) {
            String linkTitle = mCurrentImgBean.linkTitle;
            if (TextUtils.isEmpty(mCurrentImgBean.linkUrl)) {
                linkTitle = "";
            } else {
                if (TextUtils.isEmpty(linkTitle)) {
                    linkTitle = "查看更多";
                }
            }


            if (TextUtils.isEmpty(linkTitle)) {
                mTvLockLink.setVisibility(GONE);
            } else {
                mTvLockLink.setBackground(mTvLinkBg);
                mTvLockLink.setVisibility(VISIBLE);
                mTvLockLink.setText(linkTitle);
            }
            mTvLockTitle.setText(mCurrentImgBean.imgTitle);
        }

        mIsLocked = true;
        ((Adapter_DetailPage_LockScreen)mAdapterVpMain).setCanUnLock(true);
        mLockPosition = mCurrentPosition; //记录下锁屏的位置, 滑动解锁使用到, 来判断是否是滑动解锁
        if (mOnLockScreenStateChangeListener != null) {
            mOnLockScreenStateChangeListener.onLockScreenStateChange(mIsLocked);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position%mData.size();

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
    }

    public void setIvSwitching(boolean isUpdating) {
        if (isUpdating) {
            sIsSwitching = true;
            mTvSwitch.setText("更新中...");
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.switch_updating_anim);
            animation.setInterpolator(new LinearInterpolator());
            mIvSwitch.startAnimation(animation);
        } else {
            sIsSwitching = false;
            mTvSwitch.setText("换一换");
            mIvSwitch.clearAnimation();
        }
    }

    private int mSwitchDataPage = 1;
    protected void loadSwitchData() {
        final ModelLockScreen modelLockScreen = new ModelLockScreen();
        modelLockScreen.getSwitchData(mContext, mSwitchDataPage, new onDataResponseListener<List<MainImageBean>>() {
            @Override
            public void onStart() {
                setIvSwitching(true);
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

                if (mInitIndex == 0) {
                    onPageSelected(0);
                } else {
                    mVpMain.setCurrentItem(mInitIndex, false);
                }

                setIvSwitching(false);
                modelLockScreen.saveSwitchData(mContext, mSwitchImgData);
                mSwitchDataPage++;
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

    public void loadSwitchOfflineData() {
        new ModelLockScreen().getOffineSwitchData(mContext, new onDataResponseListener<List<MainImageBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(List<MainImageBean> mainImageBeen) {
                mSwitchImgData.clear();
                mSwitchImgData.addAll(mainImageBeen);

                mData.clear();
                mData.addAll(mLocalImgData);
                mData.addAll(mSwitchImgData);
                mInitIndex = mData.size() * 10;
                setVpAdapter();

                if (mInitIndex == 0) {
                    onPageSelected(0);
                } else {
                    mVpMain.setCurrentItem(mInitIndex, false);
                }

                if (mIsFrist) {
                    mIsFrist = false;
                    intoLockScreenState(false);
                }
            }

            @Override
            public void onDataEmpty() {
                mSwitchImgData.clear();

                mData.clear();
                mData.addAll(mLocalImgData);
                mData.addAll(mSwitchImgData);
                mInitIndex = mData.size() * 10;
                setVpAdapter();

                if (mInitIndex == 0) {
                    onPageSelected(0);
                } else {
                    mVpMain.setCurrentItem(mInitIndex, false);
                }

                if (mIsFrist) {
                    mIsFrist = false;
                    intoLockScreenState(false);
                }
            }

            @Override
            public void onDataFailed(String errmsg) {
                mSwitchImgData.clear();

                mData.clear();
                mData.addAll(mLocalImgData);
                mData.addAll(mSwitchImgData);
                mInitIndex = mData.size() * 10;
                setVpAdapter();

                if (mInitIndex == 0) {
                    onPageSelected(0);
                } else {
                    mVpMain.setCurrentItem(mInitIndex, false);
                }

                if (mIsFrist) {
                    mIsFrist = false;
                    intoLockScreenState(false);
                }
            }

            @Override
            public void onNetError() {
            }
        });
    }

    public void loadData() {
        new ModelLockScreen().getLocalImg(mContext, new onDataResponseListener<List<MainImageBean>>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDataSucess(List<MainImageBean> mainImageBeen) {
                mLocalImgData.clear();
                mLocalImgData.addAll(mainImageBeen);
                loadSwitchOfflineData();
            }

            @Override
            public void onDataEmpty() {
                mLocalImgData.clear();
                loadSwitchOfflineData();
            }

            @Override
            public void onDataFailed(String errmsg) {
                mLocalImgData.clear();
                loadSwitchOfflineData();
            }

            @Override
            public void onNetError() {
                mLocalImgData.clear();
                loadSwitchOfflineData();
            }
        });
    }


    public void onDestory() {
        mIsDestory = true;
        if (mTimeTickReceiver != null) {
            mContext.unregisterReceiver(mTimeTickReceiver);
        }
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
}
