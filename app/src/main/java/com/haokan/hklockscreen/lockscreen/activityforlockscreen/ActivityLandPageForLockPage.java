package com.haokan.hklockscreen.lockscreen.activityforlockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.recommendpagedetail.ActivityDetailPageRecommend;
import com.haokan.hklockscreen.recommendpageland.ActivityLandPageRecommend;
import com.haokan.hklockscreen.recommendpageland.BeanRecommendPageLand;
import com.haokan.pubic.webview.ActivityWebview;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/11/11.
 * 锁屏界面跳转的设置页面, 和应用内跳转的是一个页面, 但是有一些特殊的操作, 所以独立出来一个
 */
public class ActivityLandPageForLockPage extends ActivityLandPageRecommend {
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
                overridePendingTransition(0,0);
            }
        };
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void startDetailPage(ArrayList<BeanRecommendPageLand> data, int pos) {
        if (data == null) {
            return;
        }
        Intent intent = new Intent(this, ActivityLandDetailPageForLockPage.class);
        intent.putParcelableArrayListExtra(ActivityDetailPageRecommend.KEY_INTENT_GROUDDATE, data);
        intent.putExtra(ActivityDetailPageRecommend.KEY_INTENT_POSITION, Math.max(pos, 0));
        startActivity(intent);
        overridePendingTransition(R.anim.activity_fade_bigger_in, R.anim.activity_retain);
    }

    @Override
    public void startAdDetailPage(BeanRecommendPageLand mBean) {
        if (mBean.mBeanAdRes == null) {
            return;
        }
        Intent intent = new Intent(this, ActivityWebviewForLockPage.class);
        intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBean.mBeanAdRes.landPageUrl);
        startActivity(intent);
        startActivityAnim();
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }
}
