package com.haokan.hklockscreen.lockscreeninitset.phone1;

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
import com.haokan.hklockscreen.lockscreeninitset.SystemLockAdapterUtil;
import com.haokan.hklockscreen.lockscreeninitset.ActivityPrompt_AutoStart;
import com.haokan.hklockscreen.lockscreeninitset.CV_LockInit_ManualSetItemsBase;
import com.haokan.pubic.App;
import com.haokan.pubic.maidian.UmengMaiDianManager;

import java.util.HashMap;

/**
 * Created by wangzixu on 2017/11/16.
 */
public class ManualSetItems_1 extends CV_LockInit_ManualSetItemsBase implements View.OnClickListener {
    private int mManusetBit = 0x00000000;
    private final int MANUSET_BIT_AUTOSTART = 0x00000001;
    private final int MANUSET_BIT_CLOSESYSPSWD = 0x00000010;
    private final int MANUSET_BIT_REMOVESYSMAGAZINE = 0x00000100;
    private final int MANUSET_BIT_ALLSET = 0x00000111;
    private View mAutoStartLayout;
    private TextView mTvAutoStart;

    private View mCloseSysPswdLayout;
    private TextView mTvClosePswd;

    private View mCloseSysMagazineLayout;
    private TextView mTvCloseMagazine;

    public ManualSetItems_1(Context context) {
        this(context, null);
    }

    public ManualSetItems_1(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ManualSetItems_1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.cv_lockinit_manualsetitems_1, this, true);

        mAutoStartLayout = findViewById(R.id.autostartlayout);
        mTvAutoStart = (TextView) findViewById(R.id.tv_manualset_autostart);
        mTvAutoStart.setOnClickListener(this);

        mCloseSysPswdLayout = findViewById(R.id.closesyspswdlayout);
        mTvClosePswd = (TextView) findViewById(R.id.tv_manualset_closesyspswd);
        mTvClosePswd.setOnClickListener(this);

        mCloseSysMagazineLayout = findViewById(R.id.closesysmagezinelayout);
        mTvCloseMagazine = (TextView) findViewById(R.id.tv_manualset_closesysmagazine);
        mTvCloseMagazine.setOnClickListener(this);
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
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.coloros.fingerprint"
                            , "com.coloros.fingerprint.FingerLockActivity");
                    intent.setComponent(componentName);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivityBase.startActivityForResult(intent, 202);
                    mActivityBase.startActivityAnim();
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent i2 = new Intent(mContext, ActivityPrompt_CloseSysPswd_1.class);
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
                    onActivityResult(202, 0, null);
                    e.printStackTrace();
                }
                break;
            case R.id.tv_manualset_closesysmagazine:
                try{
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.oppo.screenlock.pictorial"
                            , "com.oppo.screenlock.pictorial.MainActivity");
                    intent.setComponent(componentName);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivityBase.startActivityForResult(intent, 203);
                    mActivityBase.startActivityAnim();
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent i2 = new Intent(mContext, ActivityPrompt_CloseSysMagazine_1.class);
                            mActivityBase.startActivity(i2);
                        }
                    });

                    HashMap<String, String> map = new HashMap<>();
                    map.put("type", "关闭锁屏杂志");
                    if (CV_LockInitSetView.sInitCheckStatus == 1) {
                        UmengMaiDianManager.onEvent(mContext, "event_052", map);
                    } else {
                        UmengMaiDianManager.onEvent(mContext, "event_059", map);
                    }

                    //显示已经设置过的状态
                    App.sMainHanlder.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTvCloseMagazine.setSelected(true);
                            mTvCloseMagazine.setText("已设置");
                            mCloseSysMagazineLayout.setBackgroundColor(0xfff4f4f4);
                        }
                    }, 500);
                }catch (Exception e){
                    mTvCloseMagazine.setSelected(true);
                    mTvCloseMagazine.setText("已设置");
                    mCloseSysMagazineLayout.setBackgroundColor(0xfff4f4f4);
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
//                mManulSetTvStartLock.setBackgroundResource(R.drawable.selector_lockinit_btnbg2);
//                mManulSetTvStartLock.setOnClickListener(this);
//                mManulSetIvCryLaugh.setImageResource(R.drawable.icon_lockinit_laugh);
//                mManulSetTvTitle.setText("锁屏设置已完成");

                if (mOnAllItemSetListener != null) {
                    mOnAllItemSetListener.onAllItemSet();
                }
            }
        } else if (requestCode == 202) { //手动去密码回来
            mManusetBit |= MANUSET_BIT_CLOSESYSPSWD;
            if (mManusetBit == MANUSET_BIT_ALLSET) {
                if (mOnAllItemSetListener != null) {
                    mOnAllItemSetListener.onAllItemSet();
                }
            }
        } else if (requestCode == 203) { //oppo手机手动去杂志锁屏回来
            mManusetBit |= MANUSET_BIT_REMOVESYSMAGAZINE;
            if (mManusetBit == MANUSET_BIT_ALLSET) {
                if (mOnAllItemSetListener != null) {
                    mOnAllItemSetListener.onAllItemSet();
                }
            }
        }
    }
}
