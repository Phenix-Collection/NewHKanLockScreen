package com.haokan.hklockscreen.recommendpagedetail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.App;
import com.haokan.pubic.bean.BigImageBean;
import com.haokan.hklockscreen.detailpage.CV_DetailPageView_Base;
import com.haokan.pubic.logsys.LogHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/10/17.
 */
public class CV_DetailPage_Recommend extends CV_DetailPageView_Base{
    private View mCurrentImageView;
    private ZoomImageViewPager_new mAdapterDetailPageRecommend;

    public CV_DetailPage_Recommend(@NonNull Context context) {
        this(context, null);
    }

    public CV_DetailPage_Recommend(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_DetailPage_Recommend(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        findViewById(R.id.divider1).setVisibility(GONE);
//        findViewById(R.id.divider4).setVisibility(GONE);
//        findViewById(R.id.bottom_collect).setVisibility(GONE);
//        findViewById(R.id.setting).setVisibility(GONE);
//        mTvCount.setVisibility(VISIBLE);

        if (mVpMain instanceof ZoomImageViewPager_new) {
            mAdapterDetailPageRecommend = (ZoomImageViewPager_new) mVpMain;
            mAdapterDetailPageRecommend.setCanZoom(true);
            mAdapterDetailPageRecommend.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickBigImage();
                }
            });

            mAdapterDetailPageRecommend.setOnSlideYListener(new ZoomImageViewPager_new.onSlideYListener() {
                @Override
                public void onSlideY(float distance) {
//                    if (mCurrentImageView != null) {
//                        mCurrentImageView.setTranslationY(distance);
//                    }
                }

                @Override
                public void onSlideEnd(float distance) {
//                    if (mCurrentImageView != null) {
//                        mCurrentImageView.setTranslationY(0);
//                    }
                    if (Math.abs(distance) > 20) {
                        if (mActivity != null) {
                            mActivity.finish();
                            if (distance > 0) {
                                mActivity.overridePendingTransition(R.anim.activity_retain, R.anim.activity_out_top2bottom);
                            } else {
                                mActivity.overridePendingTransition(R.anim.activity_retain, R.anim.activity_out_bottom2top);
                            }
                        }
                    }
                }
            });
        }

        mLayoutMainTop.setVisibility(GONE);
        mLayoutMainBottom.setVisibility(GONE);
        mIsCaptionShow = false;

//        initViewPagerRightEdge();
    }

    @Override
    public void setVpAdapter() {
        mAdapterVpMain = new Adapter_DetailPage_Recommend(mContext, mData, this, null);
        mVpMain.setAdapter(mAdapterVpMain);
    }

    @Override
    protected void onClickBigImage() {
        if (mActivity != null) {
            mActivity.onBackPressed();
        }
    }

    public void initData(ArrayList<BigImageBean> mainList, int position) {
        mData.clear();
        mData.addAll(mainList);
        setVpAdapter();
        if (position == 0) {
            App.sMainHanlder.post(new Runnable() {
                @Override
                public void run() {
                    onPageSelected(0);
                }
            });
        } else {
            mVpMain.setCurrentItem(position, false);
        }
    }

    @Override
    protected void refreshBottomLayout() {
        mCurrentImageView = mAdapterVpMain.getCurrentImageView(mCurrentPosition);
        if (mCurrentImageView instanceof Zoomimageview_new) {
            mAdapterDetailPageRecommend.setZoomImageView((Zoomimageview_new) mCurrentImageView);
        } else {
            mAdapterDetailPageRecommend.setZoomImageView(null);
        }
    }

    @Override
    public void showShareLayout() {
        if (mIsAnimnating || mCurrentImgBean == null || mShareLayout.getVisibility() == View.VISIBLE) {
            return;
        }

        mShareBlurBgView.setVisibility(GONE);

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

    protected EdgeEffectCompat mRightEdge;
    protected EdgeEffectCompat mLeftEdge;
    protected void initViewPagerRightEdge() {
        try {
            Field rightEdgeField = mVpMain.getClass().getSuperclass().getDeclaredField("mRightEdge");
            if (rightEdgeField != null) {
                rightEdgeField.setAccessible(true);
                mRightEdge = (EdgeEffectCompat) rightEdgeField.get(mVpMain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initViewPagerLeftEdge() {
        try {
            Field leftEdgeField = mVpMain.getClass().getDeclaredField("mLeftEdge");
            if (leftEdgeField != null) {
                leftEdgeField.setAccessible(true);
                mLeftEdge = (EdgeEffectCompat) leftEdgeField.get(mVpMain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPageScrollStateChanged(int arg0) {
        super.onPageScrollStateChanged(arg0);
        LogHelper.d("wangzixu", "onPageScrollStateChanged arg0 = " + arg0 + ", mRightEdge = " + mRightEdge);
        if(mRightEdge !=null && !mRightEdge.isFinished()){//到了最后一张并且还继续拖动，出现蓝色限制边条了
            try {
//                ToastManager.showCenter(mContext, "已经是最后一张了");
                if (mActivity != null) {
                    mActivity.finish();
                    mActivity.overridePendingTransition(R.anim.activity_retain, R.anim.activity_out_right2left2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
