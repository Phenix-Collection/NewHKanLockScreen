package com.haokan.hklockscreen.lockscreeninitset.manualsetitems;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreeninitset.activityprompt.ActivityPrompt_AutoStart;
import com.haokan.hklockscreen.lockscreeninitset.CV_LockInitSetView;
import com.haokan.hklockscreen.lockscreeninitset.SystemLockAdapterUtil;
import com.haokan.pubic.App;
import com.haokan.pubic.maidian.UmengMaiDianManager;

/**
 * Created by wangzixu on 2017/11/16.
 */
public class CV_LockInit_ManualSetItems_Xiaomi extends CV_LockInit_ManualSetItemsBase implements View.OnClickListener {
    private int mManusetBit = 0x00000000;
    private final int MANUSET_BIT_AUTOSTART = 0x00000001;
    private final int MANUSET_BIT_ALLSET = 0x00000001;

    public CV_LockInit_ManualSetItems_Xiaomi(Context context) {
        this(context, null);
    }

    public CV_LockInit_ManualSetItems_Xiaomi(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_LockInit_ManualSetItems_Xiaomi(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.cv_lockinit_manualsetitems_xiaomi, this, true);

        findViewById(R.id.tv_manualset_autostart).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_manualset_autostart:
                try{
                    Intent intent = SystemLockAdapterUtil.getAutoStartIntent();
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
                }catch (Exception e){
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
