package com.haokan.hklockscreen.setting;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreeninitset.activityprompt.ActivityPrompt_CloseSysMagazine_1;
import com.haokan.hklockscreen.lockscreeninitset.activityprompt.ActivityPrompt_CloseSysPswd_1;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;

/**
 * Created by wangzixu on 2017/10/20.
 */
public class ActivityLockSetting_CloseSysLock_1 extends ActivityBase implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locksetting_closesyslock_1);

        findViewById(R.id.closesyslock).setOnClickListener(this);
        findViewById(R.id.closesysmagezine).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.closesyslock:
                try {
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.coloros.fingerprint"
                            , "com.coloros.fingerprint.FingerLockActivity");
                    intent.setComponent(componentName);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, 202);
                    startActivityAnim();
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent i2 = new Intent(ActivityLockSetting_CloseSysLock_1.this, ActivityPrompt_CloseSysPswd_1.class);
                            startActivity(i2);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.closesysmagezine:
                try {
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.oppo.screenlock.pictorial"
                            , "com.oppo.screenlock.pictorial.MainActivity");
                    intent.setComponent(componentName);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, 203);
                    startActivityAnim();
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent i2 = new Intent(ActivityLockSetting_CloseSysLock_1.this, ActivityPrompt_CloseSysMagazine_1.class);
                            startActivity(i2);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                try{
//                    Intent intent = new Intent();
//                    ComponentName componentName = new ComponentName("com.oppo.screenlock.pictorial"
//                            , "com.oppo.screenlock.pictorial.MainActivity");
//                    intent.setComponent(componentName);
//                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivityForResult(intent, 203);
//                    startActivityAnim();
//                    App.sMainHanlder.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent i2 = new Intent(ActivityLockSetting_CloseSysLock_1.this, ActivityPrompt_CloseSysMagazine_1.class);
//                            startActivity(i2);
//                        }
//                    });
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivityAnim();
    }
}
