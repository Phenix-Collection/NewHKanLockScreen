package com.haokan.hklockscreen.lockscreensetting;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.util.StatusBarUtil;

/**
 * Created by wangzixu on 2017/10/20.
 */
public class ActivityLockSetting extends ActivityBase {
    private ScrollView mScrollview;
    private FrameLayout mBannerlayout;
    private RelativeLayout mLayoutlockscreen;
    private ImageView mIvLockscreen;
    private RelativeLayout mLayoutautoupdate;
    private ImageView mIvAutoupdate;
    private RelativeLayout mLayoutInitset;
    private RelativeLayout mLayoutFadeback;
    private RelativeLayout mLayoutCheckupdate;
    private RelativeLayout mLayoutAboutus;
    private ImageView mIvImage1;
    private ImageView mIvImage2;
    private ImageView mIvImage3;
    private FrameLayout mHeader;
    private ImageView mBack;
    private TextView mSettingStore;
    private FrameLayout mHeader1;
    private ImageView mBack1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locksetting);
        StatusBarUtil.setStatusBarTransparnet(this);
        initView();
    }

    private void initView() {
        mScrollview = (ScrollView) findViewById(R.id.scrollview);
        mBannerlayout = (FrameLayout) findViewById(R.id.bannerlayout);
        mLayoutlockscreen = (RelativeLayout) findViewById(R.id.layoutlockscreen);
        mIvLockscreen = (ImageView) findViewById(R.id.iv_lockscreen);
        mLayoutautoupdate = (RelativeLayout) findViewById(R.id.layoutautoupdate);
        mIvAutoupdate = (ImageView) findViewById(R.id.iv_autoupdate);
        mLayoutInitset = (RelativeLayout) findViewById(R.id.layout_initset);
        mLayoutFadeback = (RelativeLayout) findViewById(R.id.layout_fadeback);
        mLayoutCheckupdate = (RelativeLayout) findViewById(R.id.layout_checkupdate);
        mLayoutAboutus = (RelativeLayout) findViewById(R.id.layout_aboutus);
        mIvImage1 = (ImageView) findViewById(R.id.iv_image1);
        mIvImage2 = (ImageView) findViewById(R.id.iv_image2);
        mIvImage3 = (ImageView) findViewById(R.id.iv_image3);
        mHeader = (FrameLayout) findViewById(R.id.header);
        mBack = (ImageView) findViewById(R.id.back);
        mSettingStore = (TextView) findViewById(R.id.setting_collect);
        mHeader1 = (FrameLayout) findViewById(R.id.header1);
        mBack1 = (ImageView) findViewById(R.id.back1);
    }
}
