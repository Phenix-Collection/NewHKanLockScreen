package com.haokan.hklockscreen.home;

import android.os.Bundle;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.util.StatusBarUtil;

/**
 * Created by wangzixu on 2017/10/20.
 */
public class ActivityHomePage extends ActivityBase {
    private CV_RecommendPage_HomePage mCvHomePage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarTransparnet(this);
        setContentView(R.layout.activity_homepage);

        initView();
        mCvHomePage.setTypeName("美女");
        mCvHomePage.loadData(true);
    }

    private void initView() {
        mCvHomePage = (CV_RecommendPage_HomePage) findViewById(R.id.cv_homepage);
    }
}
