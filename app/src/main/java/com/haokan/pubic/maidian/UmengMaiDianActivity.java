package com.haokan.pubic.maidian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.haokan.pubic.App;
import com.haokan.pubic.logsys.LogHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by wangzixu on 2017/11/16.
 * 友盟埋点不能service中, 友盟埋点用的, 特殊的activity
 */
public class UmengMaiDianActivity extends Activity {
    public static final String KEY_INTENT_EVENTID = "eventid";
    public static final String KEY_INTENT_ARGS = "args";
    public static final String KEY_INTENT_VALUES = "values";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.onResume(this);

        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

        App.sMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);

        LogHelper.d("wangzixu", "UmengMaiDianActivity onCreate");
        uploadEvent(getIntent());

        moveTaskToBack(true); //回后台, 不能销毁
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogHelper.d("wangzixu", "UmengMaiDianActivity onNewIntent");
        uploadEvent(intent);

        moveTaskToBack(true); //回后台, 不能销毁
    }

    private void uploadEvent(Intent intent) {
        if (intent != null) {
            String eventid = intent.getStringExtra(KEY_INTENT_EVENTID);
            LogHelper.d("wangzixu", "UmengMaiDianActivity uploadEvent eventid = " + eventid);
            if (!TextUtils.isEmpty(eventid)) {
                String[] args = intent.getStringArrayExtra(KEY_INTENT_ARGS);
                String[] values = intent.getStringArrayExtra(KEY_INTENT_VALUES);
                if (args == null || values == null || args.length != values.length) {
                    UmengMaiDianManager.onEvent(this, eventid);
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    for (int i = 0; i < args.length; i++) {
                        map.put(args[i], values[i]);
                    }
                    UmengMaiDianManager.onEvent(this, eventid, map);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        LogHelper.d("wangzixu", "UmengMaiDianActivity onDestroy");
        MobclickAgent.onPause(this);
        super.onDestroy();
    }

//    private boolean mIsFinish = false;
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (!mIsFinish) {
//            mIsFinish = true;
//            moveTaskToBack(true); //回后台, 不能销毁
//        }
//        return super.dispatchTouchEvent(ev);
//    }
}
