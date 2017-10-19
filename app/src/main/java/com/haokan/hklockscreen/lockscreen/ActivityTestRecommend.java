package com.haokan.hklockscreen.lockscreen;

import android.os.Bundle;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.recommendpage.CV_RecommendPage;
import com.haokan.pubic.base.ActivityBase;

/**
 * Created by wangzixu on 2017/3/2.
 */
public class ActivityTestRecommend extends ActivityBase {
    private CV_RecommendPage mRecommendPage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_test);
    }
}