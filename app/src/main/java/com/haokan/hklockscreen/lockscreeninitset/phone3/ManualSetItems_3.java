package com.haokan.hklockscreen.lockscreeninitset.phone3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreeninitset.CV_LockInitSetView;
import com.haokan.hklockscreen.lockscreeninitset.ActivityPrompt_AutoStart;
import com.haokan.hklockscreen.lockscreeninitset.CV_LockInit_ManualSetItemsBase;
import com.haokan.pubic.App;
import com.haokan.pubic.maidian.UmengMaiDianManager;

/**
 * Created by wangzixu on 2017/11/16.
 * //第3类型, 华为emui4.0.x- Android6.0
 */
public class ManualSetItems_3 extends CV_LockInit_ManualSetItemsBase implements View.OnClickListener {
    private int mManusetBit = 0x00000000;
    private final int MANUSET_BIT_AUTOSTART = 0x00000001;
    private final int MANUSET_BIT_ALLSET = 0x00000001;
    private View mAutoStartLayout;
    private TextView mTvAutoStart;

    public ManualSetItems_3(Context context) {
        this(context, null);
    }

    public ManualSetItems_3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ManualSetItems_3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.cv_lockinit_manualsetitems_2, this, true);
        mAutoStartLayout = findViewById(R.id.autostartlayout);
        mTvAutoStart = (TextView) findViewById(R.id.tv_manualset_autostart);
        mTvAutoStart.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tv_manualset_autostart:
                try{
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.huawei.systemmanager"
                            , "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
                    intent.setComponent(componentName);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivityBase.startActivityForResult(intent, 201);
                    mActivityBase.startActivityAnim();
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent i2 = new Intent(mContext, ActivityPrompt_AutoStart.class);
                            mActivityBase.startActivity(i2);
                        }
                    });

                    if (CV_LockInitSetView.sInitCheckStatus == 1) {
                        UmengMaiDianManager.onEvent(mContext, "event_051");
                    } else {
                        UmengMaiDianManager.onEvent(mContext, "event_058");
                    }

                    //显示已经设置过的状态
                    App.sMainHanlder.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTvAutoStart.setSelected(true);
                            mTvAutoStart.setText("已设置");
                            mAutoStartLayout.setBackgroundColor(0xfff4f4f4);
                        }
                    }, 500);
                }catch (Exception e){
                    mTvAutoStart.setSelected(true);
                    mTvAutoStart.setText("已设置");
                    mAutoStartLayout.setBackgroundColor(0xfff4f4f4);
                    onActivityResult(201, 0, null);
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 201) { //手动自启动回来
            mManusetBit |= MANUSET_BIT_AUTOSTART;
            if (mManusetBit == MANUSET_BIT_ALLSET) {
                if (mOnAllItemSetListener != null) {
                    mOnAllItemSetListener.onAllItemSet();
                }
            }
        }
    }
}
