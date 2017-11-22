package com.haokan.hklockscreen.setting;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.util.CommonUtil;

/**
 * 关于我们页面
 */
public class ActivityAboutUs extends ActivityBase implements View.OnClickListener, View.OnLongClickListener {
    private TextView mTvDesc;
    private int mLongClickCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        TextView tv_copy_right = (TextView) findViewById(R.id.tv_copy_right);
//        tv_copy_right.setText(getString(R.string.app_copyright, Calendar.getInstance().get(Calendar.YEAR)));
        TextView version = (TextView) findViewById(R.id.tv_about_us_version);
        version.setText("v" + App.APP_VERSION_NAME);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.iv_about_us_img).setOnLongClickListener(this);
        mTvDesc = (TextView) findViewById(R.id.tv_desc);
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public boolean onLongClick(View v) {
        if (mLongClickCount < 1) {
            mLongClickCount ++;
            return true;
        }
        if (mLongClickCount > 1) {
            mLongClickCount = 0;
            hideInfoText();
            return true;
        }
        mLongClickCount ++;
        String info = getAboutUsInfo();
        showInfoText(info);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //singinstance的,去掉动画
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    public void showInfoText(String info) {
        mTvDesc.setText(info);
        mTvDesc.setVisibility(View.VISIBLE);
    }

    public void hideInfoText() {
        mTvDesc.setVisibility(View.GONE);
    }

    public String getAboutUsInfo() {
        String pid = "";
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            pid = info.metaData.getInt("UMENG_CHANNEL") + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        String packageName = getPackageName();
        int code = CommonUtil.getLocalVersionCode(this);
        StringBuilder builder = new StringBuilder("packageName:" + packageName + "\npid:" + App.sPID + "\npid(real):" + pid
                + "\nversioncode : " + code);
        return builder.toString();
    }
}
