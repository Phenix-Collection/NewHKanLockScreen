package com.haokan.hklockscreen.lockscreeninitset.phone2;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreeninitset.ActivityPrompt_AutoStart;
import com.haokan.hklockscreen.lockscreeninitset.CV_LockInitSetView;
import com.haokan.hklockscreen.lockscreeninitset.CV_LockInit_ManualSetItemsBase;
import com.haokan.hklockscreen.lockscreeninitset.SystemLockAdapterUtil;
import com.haokan.hklockscreen.lockscreeninitset.phone3.ActivityPrompt_CloseSysPswd_3;
import com.haokan.pubic.App;
import com.haokan.pubic.maidian.UmengMaiDianManager;

import java.util.HashMap;

/**
 * Created by wangzixu on 2017/11/16.
 */
public class ManualSetItems_2 extends CV_LockInit_ManualSetItemsBase implements View.OnClickListener {
    private int mManusetBit = 0x00000000;
    private final int MANUSET_BIT_AUTOSTART = 0x00000001;
    private final int MANUSET_BIT_CLOSESYSPSWD = 0x00000010;
    private final int MANUSET_BIT_ALLSET = 0x00000001;
    private View mAutoStartLayout;
    private TextView mTvAutoStart;

    private View mCloseSysPswdLayout;
    private TextView mTvClosePswd;

    public ManualSetItems_2(Context context) {
        this(context, null);
    }

    public ManualSetItems_2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ManualSetItems_2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.cv_lockinit_manualsetitems_2, this, true);
        mAutoStartLayout = findViewById(R.id.autostartlayout);
        mTvAutoStart = (TextView) findViewById(R.id.tv_manualset_autostart);
        mTvAutoStart.setOnClickListener(this);

        mCloseSysPswdLayout = findViewById(R.id.closesyspswdlayout);
        mTvClosePswd = (TextView) findViewById(R.id.tv_manualset_closesyspswd);
        mTvClosePswd.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
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
            case R.id.tv_manualset_closesyspswd:
                try{
                    Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                    mActivityBase.startActivityForResult(intent, 202);
                    mActivityBase.startActivityAnim();
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent i2 = new Intent(mContext, ActivityPrompt_CloseSysPswd_3.class);
                            mActivityBase.startActivity(i2);
                        }
                    });

                    HashMap<String, String> map = new HashMap<>();
                    map.put("type", "关闭系统密码解锁");
                    if (CV_LockInitSetView.sInitCheckStatus == 1) {
                        UmengMaiDianManager.onEvent(mContext, "event_052", map);
                    } else {
                        UmengMaiDianManager.onEvent(mContext, "event_059", map);
                    }

                    //显示已经设置过的状态
                    App.sMainHanlder.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTvClosePswd.setSelected(true);
                            mTvClosePswd.setText("已设置");
                            mCloseSysPswdLayout.setBackgroundColor(0xfff4f4f4);
                        }
                    }, 500);
                }catch (Exception e){
                    mTvClosePswd.setSelected(true);
                    mTvClosePswd.setText("已设置");
                    mCloseSysPswdLayout.setBackgroundColor(0xfff4f4f4);
                    onActivityResult(203, 0, null);
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
        } else if (requestCode == 202) { //白名单
            mManusetBit |= MANUSET_BIT_CLOSESYSPSWD;
            if (mManusetBit == MANUSET_BIT_ALLSET) {
                if (mOnAllItemSetListener != null) {
                    mOnAllItemSetListener.onAllItemSet();
                }
            }
        }
    }
}
