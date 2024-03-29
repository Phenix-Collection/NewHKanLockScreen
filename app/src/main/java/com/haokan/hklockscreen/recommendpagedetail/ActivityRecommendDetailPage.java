package com.haokan.hklockscreen.recommendpagedetail;

import android.content.Intent;
import android.os.Bundle;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.recommendpageland.BeanRecommendLandPage;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.bean.BeanConvertUtil;
import com.haokan.pubic.bean.BigImageBean;
import com.haokan.pubic.util.StatusBarUtil;
import com.haokan.pubic.util.ToastManager;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/5/25.
 * 带高斯模糊, 可放大缩小的详情页
 */
public class ActivityRecommendDetailPage extends ActivityBase{
    public static final String KEY_INTENT_GROUDDATE = "groupdate";
    public static final String KEY_INTENT_POSITION = "initpos";
    protected ArrayList<BeanRecommendLandPage> mData = new ArrayList<>();
    private CV_DetailPage_Recommend mCv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendpagedetail);
        StatusBarUtil.setStatusBarTransparnet(this);
        initView();

        Intent intent = getIntent();
        mData = intent.getParcelableArrayListExtra(KEY_INTENT_GROUDDATE);
        if (mData == null || mData.size() == 0) {
            ToastManager.showShort(this, "无法打开详情页, 数据为空");
            finish();
            return;
        }

        initView();
    }

    private void initView() {
        mCv = (CV_DetailPage_Recommend) findViewById(R.id.cv_detailpage_recommend);
        mCv.setActivity(this);

//        if (ActivityRecommendPageLand.mBlurDrawable != null) {
//            ImageView ivBg = (ImageView) findViewById(R.id.iv_image);
//            ivBg.setImageDrawable(ActivityRecommendPageLand.mBlurDrawable);
//        }

        ArrayList<BigImageBean> mainList = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            BeanRecommendLandPage beanRecommendPageLand = mData.get(i);
            if (beanRecommendPageLand.myType == 0) {
                BigImageBean mainImageBean = BeanConvertUtil.recommendLandBean2BigImageBean(beanRecommendPageLand);
                mainList.add(mainImageBean);
            }
        }

        int i = getIntent().getIntExtra(KEY_INTENT_POSITION, 0);
        mCv.initData(mainList, i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCv != null) {
            mCv.onDestory();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_retain, R.anim.activity_fade_smaller_out);
    }
}
