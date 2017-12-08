package com.haokan.hklockscreen.home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.recommendpagelist.BeanRecommendItem;
import com.haokan.hklockscreen.setting.ActivityLockSetting;
import com.haokan.hklockscreen.recommendpagelist.CV_RecommendListPage;
import com.haokan.pubic.maidian.UmengMaiDianManager;

/**
 * Created by wangzixu on 2017/10/20.
 */
public class CV_RecommendPage_HomePage extends CV_RecommendListPage implements View.OnClickListener {
    public CV_RecommendPage_HomePage(@NonNull Context context) {
        this(context, null);
    }

    public CV_RecommendPage_HomePage(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_RecommendPage_HomePage(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cv_recommendpage_homepage, this, true);
        view.findViewById(R.id.setting).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting:
                Intent i = new Intent(mContext, ActivityLockSetting.class);
                if (mActivityBase != null) {
                    mActivityBase.startActivity(i);
                    mActivityBase.startActivityAnim();
                } else {
                    mContext.startActivity(i);
                }

                UmengMaiDianManager.onEvent(mContext, "event_082");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onLoadDataSuccessForManDian() {
        UmengMaiDianManager.onEvent(mContext, "event_104");
    }

    @Override
    public void startDetailPage(BeanRecommendItem beanRecommendItem) {
        super.startDetailPage(beanRecommendItem);
        UmengMaiDianManager.onEvent(mContext, "event_083");
    }
}
