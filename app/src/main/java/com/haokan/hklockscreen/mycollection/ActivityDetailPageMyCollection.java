package com.haokan.hklockscreen.mycollection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.bean.BeanConvertUtil;
import com.haokan.pubic.bean.BigImageBean;
import com.haokan.pubic.database.BeanCollection;
import com.haokan.pubic.util.StatusBarUtil;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/10/26.
 */
public class ActivityDetailPageMyCollection extends ActivityBase {
    private CV_DetailPage_MyCollection mCvDetailPageMyCollection;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarTransparnet(this);
        setContentView(R.layout.activity_mycollection_detailpage);
        initView();

        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(mReceiver, filter);
    }

    private void initView() {
        mCvDetailPageMyCollection = (CV_DetailPage_MyCollection) findViewById(R.id.cv_detailpage_mycollection);
        mCvDetailPageMyCollection.setActivity(this);

        ArrayList<BeanCollection> list = getIntent().getParcelableArrayListExtra("initdata");
        if (list == null) {
            return;
        }
        ArrayList<BigImageBean> mainList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            BigImageBean mainImageBean = BeanConvertUtil.collectionBean2BigImageBean(list.get(i));
            mainList.add(mainImageBean);
        }

        int i = getIntent().getIntExtra("initpos", 0);
        mCvDetailPageMyCollection.initData(mainList, i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivityAnim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCvDetailPageMyCollection != null) {
            mCvDetailPageMyCollection.onDestory();
        }

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
