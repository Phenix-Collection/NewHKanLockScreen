package com.haokan.pubic.maidian;

import android.content.Context;

import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.CommonUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by wangzixu on 2017/11/15.
 * 友盟埋点的统一出口, 有统一的log打印, 方便测试
 */
public class UmengMaiDianManager {
    public static void onEvent(Context context, String eventId, HashMap<String,String> map) {
        if (map != null) {
            if (LogHelper.DEBUG) {
                LogHelper.d("wangzixu", "UmengMaiDianManager onEvent eventId = " + eventId + ", map = " + CommonUtil.transMapToString(map));
            }
            MobclickAgent.onEvent(context, eventId, map);
        } else {
            if (LogHelper.DEBUG) {
                LogHelper.d("wangzixu", "UmengMaiDianManager onEvent eventId = " + eventId);
            }
            MobclickAgent.onEvent(context, eventId);
        }
    }
}
