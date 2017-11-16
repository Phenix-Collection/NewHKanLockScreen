package com.haokan.hklockscreen.lockscreeninitset.manualsetitems;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreeninitset.ActivityPrompt_AutoStart;
import com.haokan.hklockscreen.lockscreeninitset.CV_LockInitSetView;
import com.haokan.hklockscreen.lockscreeninitset.SystemIntentUtil;
import com.haokan.pubic.App;
import com.haokan.pubic.maidian.UmengMaiDianManager;

import java.util.HashMap;

/**
 * Created by wangzixu on 2017/11/16.
 */
public class CV_LockInit_ManualSetItems_Oppo extends CV_LockInit_ManualSetItemsBase implements View.OnClickListener {
    private int mManusetBit = 0x00000000;
    private final int MANUSET_BIT_AUTOSTART = 0x00000001;
    private final int MANUSET_BIT_REMOVESYSPSWD = 0x00000010;
    private final int MANUSET_BIT_REMOVESYSMAGAZINE = 0x00000100;
    private final int MANUSET_BIT_ALLSET = 0x00000111;

    public CV_LockInit_ManualSetItems_Oppo(Context context) {
        this(context, null);
    }

    public CV_LockInit_ManualSetItems_Oppo(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_LockInit_ManualSetItems_Oppo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.cv_lockinit_manualsetitems_oppo, this, true);

        findViewById(R.id.tv_manualset_autostart).setOnClickListener(this);
        findViewById(R.id.tv_manualset_removesyspswd).setOnClickListener(this);
        findViewById(R.id.tv_manualset_removesysmagazine).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_manualset_autostart:
                try{
                    Intent intent = SystemIntentUtil.getAutoStartIntent();
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
            case R.id.tv_manualset_removesyspswd:
                try{
                    Intent intent = SystemIntentUtil.getRemoveSysPswdIntent();
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivityBase.startActivityForResult(intent, 202);
                    mActivityBase.startActivityAnim();
//                    App.sMainHanlder.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent i2 = new Intent(mContext, ActivityPrompt_AutoStart.class);
//                            mActivityBase.startActivity(i2);
//                        }
//                    });
                    HashMap<String, String> map = new HashMap<>();
                    map.put("type", "关闭系统密码解锁");
                    if (CV_LockInitSetView.sInitCheckStatus == 1) {
                        UmengMaiDianManager.onEvent(mContext, "event_052", map);
                    } else {
                        UmengMaiDianManager.onEvent(mContext, "event_059", map);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_manualset_removesysmagazine:
                try{
                    Intent intent = SystemIntentUtil.getRemoveSysMagazineIntent();
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivityBase.startActivityForResult(intent, 203);
                    mActivityBase.startActivityAnim();
//                    App.sMainHanlder.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent i2 = new Intent(mContext, ActivityPrompt_AutoStart.class);
//                            mActivityBase.startActivity(i2);
//                        }
//                    });

                    HashMap<String, String> map = new HashMap<>();
                    map.put("type", "关闭锁屏杂志");
                    if (CV_LockInitSetView.sInitCheckStatus == 1) {
                        UmengMaiDianManager.onEvent(mContext, "event_052", map);
                    } else {
                        UmengMaiDianManager.onEvent(mContext, "event_059", map);
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
//                mManulSetTvStartLock.setBackgroundResource(R.drawable.selector_lockinit_btnbg2);
//                mManulSetTvStartLock.setOnClickListener(this);
//                mManulSetIvCryLaugh.setImageResource(R.drawable.icon_lockinit_laugh);
//                mManulSetTvTitle.setText("锁屏设置已完成");

                if (mOnAllItemSetListener != null) {
                    mOnAllItemSetListener.onAllItemSet();
                }
            }
        } else if (requestCode == 202) { //手动去密码回来
            mManusetBit |= MANUSET_BIT_REMOVESYSPSWD;
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
