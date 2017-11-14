package com.haokan.hklockscreen.lockscreen;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.recommendpageland.ActivityRecommendPageLand;
import com.haokan.hklockscreen.recommendpagelist.BeanRecommendItem;
import com.haokan.hklockscreen.recommendpagelist.CV_RecommendPage;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by wangzixu on 2017/10/20.
 */
public class CV_RecommendPage_LockScreen extends CV_RecommendPage implements View.OnClickListener {
    public CV_RecommendPage_LockScreen(@NonNull Context context) {
        this(context, null);
    }

    public CV_RecommendPage_LockScreen(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_RecommendPage_LockScreen(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cv_recommendpage_lockscreen, this, true);

        view.findViewById(R.id.back).setOnClickListener(this);
        view.findViewById(R.id.backlockscreen).setOnClickListener(this);
        mHeaderView = findViewById(R.id.header);
        mHeaderViewCopy = findViewById(R.id.headercopy);
    }

    public void loadDataForLockScreen(String typeName) {
        if (typeName.equals(mFirstTypeName)) {
            mRecyclerView.scrollToPosition(0);
        } else {
            mTypeName = typeName;
            mFirstTypeName = typeName;
            loadData(true);
        }
    }

    public void onShow() {
        showHeader();
    }

    public void onHide() {
        hideHeader();
    }

    protected void showHeader() {
        mHeaderView.setVisibility(VISIBLE);
        mHeaderViewCopy.setVisibility(GONE);
//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
//        valueAnimator.setDuration(200);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float f = (float) animation.getAnimatedValue();
//                mHeaderView.setAlpha(f);
//                mHeaderViewCopy.setAlpha(1.0f - f);
//            }
//        });
//        valueAnimator.start();
        mRecyclerView.scrollToPosition(0);
    }

    protected void hideHeader() {
        mHeaderView.setVisibility(GONE);
        mHeaderViewCopy.setVisibility(VISIBLE);

//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
//        valueAnimator.setDuration(200);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float f = (float) animation.getAnimatedValue();
//                mHeaderView.setAlpha(1.0f - f);
//                mHeaderViewCopy.setAlpha(f);
//            }
//        });
//        valueAnimator.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back: //回到上面的详情页页
                if (mActivityBase != null && mActivityBase instanceof ActivityLockScreen) {
                    ((ActivityLockScreen)mActivityBase).backToDetailPage();
                    MobclickAgent.onEvent(mContext, "recommend_back");
                }
                break;
            case R.id.backlockscreen: //回到上面的详情页页, 并进入锁屏状态
                if (mActivityBase != null && mActivityBase instanceof ActivityLockScreen) {
                    ((ActivityLockScreen)mActivityBase).backToLockScreenPage();
                    MobclickAgent.onEvent(mContext, "recommend_backlock");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void startDetailPage(BeanRecommendItem beanRecommendItem) {
        if (beanRecommendItem == null) {
            return;
        }

        if (beanRecommendItem.mBeanAdRes == null) {
            Intent intent = new Intent(mContext, ActivityRecommendPageLand.class);
            intent.putExtra(ActivityRecommendPageLand.KEY_INTENT_RECOMMENDBEAN, beanRecommendItem);;
            if (mActivityBase != null) {
                mActivityBase.startActivityForResult(intent, 101);
                mActivityBase.startActivityAnim();
            } else {
                mContext.startActivity(intent);
            }

//            Intent intent = new Intent(mContext, ActivityWebview.class);
//            intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, beanRecommendItem.urlClick);
//            if (mActivityBase != null) {
//                mActivityBase.startActivity(intent);
//                mActivityBase.startActivityAnim();
//            } else {
//                mContext.startActivity(intent);
//            }
        } else {
            //跳转webview
            Intent intent = new Intent(mContext, ActivityWebviewForLockPage.class);
            intent.putExtra(ActivityWebviewForLockPage.KEY_INTENT_WEB_URL, beanRecommendItem.mBeanAdRes.landPageUrl);
            if (mActivityBase != null) {
                mActivityBase.startActivity(intent);
                mActivityBase.startActivityAnim();
            } else {
                mContext.startActivity(intent);
            }
        }
    }
}
