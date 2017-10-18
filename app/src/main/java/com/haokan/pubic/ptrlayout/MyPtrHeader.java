package com.haokan.pubic.ptrlayout;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.hklockscreen.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;


/**
 * Created by wangzixu on 2017/9/1.
 */
public class MyPtrHeader extends RelativeLayout implements PtrUIHandler {
    private View mProcressBar;
    private ImageView mIvArrow;
    private TextView mTvTitle;
    protected RotateAnimation mFlipAnimation;
    protected RotateAnimation mReverseFlipAnimation;
    private int mRotateAniTime = 150;

    public MyPtrHeader(@NonNull Context context) {
        this(context, null);
    }

    public MyPtrHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyPtrHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        View header = LayoutInflater.from(getContext()).inflate(R.layout.ptrlayout_myheader, this);
        buildAnimation();
        mTvTitle = (TextView) header.findViewById(R.id.tv_title);
        mIvArrow = (ImageView) header.findViewById(R.id.arrow);
        mProcressBar = header.findViewById(R.id.progress_bar);
    }

    protected void buildAnimation() {
        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(mRotateAniTime);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(mRotateAniTime);
        mReverseFlipAnimation.setFillAfter(true);
    }


    @Override
    public void onUIReset(PtrFrameLayout frame) {
        Log.d("myptr", "onUIReset");
        mProcressBar.setVisibility(INVISIBLE);
        mIvArrow.clearAnimation();
        mIvArrow.setVisibility(VISIBLE);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        Log.d("myptr", "onUIRefreshPrepare");
        mTvTitle.setText("下拉刷新");
        mProcressBar.setVisibility(INVISIBLE);
        mIvArrow.clearAnimation();
        mIvArrow.setVisibility(VISIBLE);
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        Log.d("myptr", "onUIRefreshBegin");
        mProcressBar.setVisibility(VISIBLE);
        mIvArrow.clearAnimation();
        mIvArrow.setVisibility(INVISIBLE);
        mTvTitle.setText("加载中...");
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame, boolean isHeader) {
        Log.d("myptr", "onUIRefreshComplete");
        mTvTitle.setText("加载完成");
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        final int mOffsetToRefresh = ptrIndicator.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();


        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                mTvTitle.setText("下拉刷新");
                if (mIvArrow != null) {
                    mIvArrow.clearAnimation();
                    mIvArrow.startAnimation(mReverseFlipAnimation);
                }
            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                mTvTitle.setText("释放刷新");
                if (mIvArrow != null) {
                    mIvArrow.clearAnimation();
                    mIvArrow.startAnimation(mFlipAnimation);
                }
            }
        }
    }
}
