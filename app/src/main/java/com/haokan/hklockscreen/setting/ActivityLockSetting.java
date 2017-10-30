package com.haokan.hklockscreen.setting;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreen.CV_ScrollView;
import com.haokan.hklockscreen.lockscreeninitset.ActivityLockScreenInitSet;
import com.haokan.hklockscreen.mycollection.ActivityMyCollection;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.checkupdate.UpdateManager;
import com.haokan.pubic.util.DisplayUtil;
import com.haokan.pubic.util.StatusBarUtil;
import com.haokan.pubic.util.Values;

/**
 * Created by wangzixu on 2017/10/20.
 */
public class ActivityLockSetting extends ActivityBase implements View.OnClickListener {
    private CV_ScrollView mScrollview;
    private FrameLayout mBannerlayout;
    private RelativeLayout mLayoutlockscreen;
    private ImageView mIvLockscreen;
    private RelativeLayout mLayoutAutoUpdateImage;
    private ImageView mIvAutoupdateImage;
    private RelativeLayout mLayoutInitset;
    private RelativeLayout mLayoutFadeback;
    private RelativeLayout mLayoutCheckupdate;
    private RelativeLayout mLayoutAboutus;
    private ImageView mIvImage1;
    private ImageView mIvImage2;
    private ImageView mIvImage3;
    private FrameLayout mHeader;
    private ImageView mBack;
    private TextView mSettingCollect;
    private FrameLayout mHeader1;
    private ImageView mBack1;
    private int mHeaderChangeHeigh;
    private TextView mSettingCollect1;
    private TextView mTvLocalImageEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locksetting);
        StatusBarUtil.setStatusBarTransparnet(this);
        initView();
    }

    private void initView() {
        mScrollview = (CV_ScrollView) findViewById(R.id.scrollview);
        mBannerlayout = (FrameLayout) findViewById(R.id.bannerlayout);
        mLayoutlockscreen = (RelativeLayout) findViewById(R.id.layoutlockscreen);
        mIvLockscreen = (ImageView) findViewById(R.id.iv_lockscreen);
        mLayoutAutoUpdateImage = (RelativeLayout) findViewById(R.id.layoutautoupdateimg);
        mIvAutoupdateImage = (ImageView) findViewById(R.id.iv_autoupdate);
        mLayoutInitset = (RelativeLayout) findViewById(R.id.layout_initset);
        mLayoutFadeback = (RelativeLayout) findViewById(R.id.layout_fadeback);
        mLayoutCheckupdate = (RelativeLayout) findViewById(R.id.layout_checkupdate);
        mLayoutAboutus = (RelativeLayout) findViewById(R.id.layout_aboutus);
        mIvImage1 = (ImageView) findViewById(R.id.iv_image1);
        mIvImage2 = (ImageView) findViewById(R.id.iv_image2);
        mIvImage3 = (ImageView) findViewById(R.id.iv_image3);
        mHeader = (FrameLayout) findViewById(R.id.header);
        mBack = (ImageView) findViewById(R.id.back);
        mSettingCollect = (TextView) findViewById(R.id.setting_collect);
        mSettingCollect1 = (TextView) findViewById(R.id.setting_collect1);
        mHeader1 = (FrameLayout) findViewById(R.id.header1);
        mBack1 = (ImageView) findViewById(R.id.back1);
        mTvLocalImageEdit = (TextView) findViewById(R.id.edit);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //设置是否打开了锁屏
        boolean open = preferences.getBoolean(Values.PreferenceKey.KEY_SP_OPENLOCKSCREEN, true);
        if (open) {
            mIvLockscreen.setSelected(true);
        } else {
            mIvLockscreen.setSelected(false);
        }

        //设置是否自动更新锁屏图片
        boolean auto = preferences.getBoolean(Values.PreferenceKey.KEY_SP_AUTOUPDATEIMAGE, true);
        if (auto) {
            mIvAutoupdateImage.setSelected(true);
        } else {
            mIvAutoupdateImage.setSelected(false);
        }

        mBack.setOnClickListener(this);
        mBack1.setOnClickListener(this);
        mSettingCollect.setOnClickListener(this);
        mSettingCollect1.setOnClickListener(this);
        mLayoutlockscreen.setOnClickListener(this);
        mLayoutCheckupdate.setOnClickListener(this);
        mLayoutAboutus.setOnClickListener(this);
        mLayoutAutoUpdateImage.setOnClickListener(this);
        mLayoutInitset.setOnClickListener(this);
        mTvLocalImageEdit.setOnClickListener(this);
        mLayoutFadeback.setOnClickListener(this);

        //顶部banner高-mHeader1高
        mHeaderChangeHeigh = DisplayUtil.dip2px(this, 220-65);
        mScrollview.setMyOnScrollChangeListener(new CV_ScrollView.MyOnScrollChangeListener() {
            @Override
            public void onScrollChange(int scrollX, int scrollY, int oldX, int oldY) {
//                LogHelper.d("wangzixu", "locksetting height = " + height + ", height1 = " + height1);
                if (scrollY >= mHeaderChangeHeigh) {
                    if (mHeader1.getVisibility() != View.VISIBLE) {
                        mHeader1.setVisibility(View.VISIBLE);
                        mHeader.setVisibility(View.INVISIBLE);
//                        StatusBarUtil.setStatusBarTextColor(ActivityLockSetting.this, true);
                    }
                } else {
                    if (mHeader.getVisibility() != View.VISIBLE) {
                        mHeader1.setVisibility(View.INVISIBLE);
                        mHeader.setVisibility(View.VISIBLE);
//                        StatusBarUtil.setStatusBarTextColor(ActivityLockSetting.this, false);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
            case R.id.back1:
                onBackPressed();
                break;
            case R.id.layoutlockscreen:
                {
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    if (mIvLockscreen.isSelected()) {
                        mIvLockscreen.setSelected(false);
                        edit.putBoolean(Values.PreferenceKey.KEY_SP_OPENLOCKSCREEN, false).apply();
                    } else {
                        mIvLockscreen.setSelected(true);
                        edit.putBoolean(Values.PreferenceKey.KEY_SP_OPENLOCKSCREEN, true).apply();
                    }
                }
                break;
            case R.id.layoutautoupdateimg://自动更新锁屏图片
                {
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    if (mIvAutoupdateImage.isSelected()) {
                        mIvAutoupdateImage.setSelected(false);
                        edit.putBoolean(Values.PreferenceKey.KEY_SP_AUTOUPDATEIMAGE, false).apply();
                    } else {
                        mIvAutoupdateImage.setSelected(true);
                        edit.putBoolean(Values.PreferenceKey.KEY_SP_AUTOUPDATEIMAGE, true).apply();
                    }
                }
                break;
            case R.id.setting_collect:
            case R.id.setting_collect1:
                {
                    Intent intent = new Intent(this, ActivityMyCollection.class);
                    startActivity(intent);
                    startActivityAnim();
                }
                break;
            case R.id.layout_fadeback:
            {
//                Intent intent = new Intent(this, ActivityMyCollection.class);
//                startActivity(intent);
//                startActivityAnim();

                FeedbackAPI.openFeedbackActivity();
            }
            break;
            case R.id.layout_aboutus:
                {
                    Intent intent = new Intent(this, ActivityAboutUs.class);
                    startActivity(intent);
                    startActivityAnim();
                }
                break;
            case R.id.layout_checkupdate:
                checkStoragePermission();
                break;
            case R.id.layout_initset:
            {
                Intent intent = new Intent(this, ActivityLockScreenInitSet.class);
                startActivity(intent);
                startActivityAnim();
            }
                break;
            default:
                break;
        }
    }

    //权限相关begin*****
    /**
     * 检查权限
     */
    public void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //需要用权限的地方之前，检查是否有某个权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) { //没有这个权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
                return;
            } else {
                UpdateManager.checkUpdate(this, false);
            }
        } else {
            UpdateManager.checkUpdate(this, false);;
        }
    }

    //检查权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 201:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //同意
                        UpdateManager.checkUpdate(this, false);
                    } else {
                        // 不同意
//                        ToastManager.showCenter(this, "没有授予存储权限, 无法更新, 请去设置打开");
                        App.sMainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                askToOpenPermissions();
                            }
                        });
                    }
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 提示用户去设置界面开启权限
     */
    private void askToOpenPermissions() {
        View cv = LayoutInflater.from(this).inflate(R.layout.dialog_layout_asksdpermission, null);
        TextView desc = (TextView) cv.findViewById(R.id.tv_desc);
        desc.setText("没有授予存储权限, 无法更新, 请去设置授予存储权限后, 再执行检查更新");

        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("重要提示")
                .setView(cv)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    //权限相关end*****

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivityAnim();
    }
}
