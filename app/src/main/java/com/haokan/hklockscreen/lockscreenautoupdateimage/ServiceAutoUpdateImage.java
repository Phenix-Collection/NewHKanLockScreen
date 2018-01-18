package com.haokan.hklockscreen.lockscreenautoupdateimage;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.haokan.hklockscreen.lockscreen.ModelLockScreen;
import com.haokan.pubic.App;
import com.haokan.pubic.bean.BigImageBean;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.maidian.MaidianManager;
import com.haokan.pubic.maidian.UmengMaiDianActivity;
import com.haokan.pubic.util.MyDateTimeUtil;
import com.haokan.pubic.util.Values;

import java.util.List;

/**
 * Created by wangzixu on 2017/3/17.
 */
public class ServiceAutoUpdateImage extends Service {
    public static final String KEY_AUTOUPDATA_TIME = "autoimgtime";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mIsSwitching) {
            LogHelper.d("wangzixu", "WiFiChangeReceiver autoupdate mIsSwitching return");
            return super.onStartCommand(intent, flags, startId);
        }

        if (intent != null) {
            int type = intent.getIntExtra("type", 0);
            if (type == 1) { //1,代表是通过Alarm唤起的自动更新, 半夜临时唤起, 立即取网络状态, 可能取到的不是wifi, 需要延时
                App.startAutoUpdate();
                return super.onStartCommand(intent, flags, startId);
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String time = preferences.getString(KEY_AUTOUPDATA_TIME, "----");
        String curTime = MyDateTimeUtil.getCurrentSimpleData();
        if (time.equals(curTime)) {
            LogHelper.d("wangzixu", "autoupdate onStartCommand 当天已经更新");
            if (App.sHaokanLockView != null) {
                App.sHaokanLockView.setUpdateSign(0);
                App.sHaokanLockView.setIvSwitching(false);
            }
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        LogHelper.writeLog(this, "autoupdate onStartCommand ****开始****");

        //用户是否打开了自动更新开关
        boolean auto = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Values.PreferenceKey.KEY_SP_AUTOUPDATEIMAGE, true);
        if (!auto) {
            LogHelper.d("wangzixu", "autoupdate onStartCommand auto = false");
            LogHelper.writeLog(this, "autoupdate onStartCommand auto = false");

            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        //用户是否有sd卡权限
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
            LogHelper.w("wangzixu", "autoupdate onStartCommand 没有存储权限");
            LogHelper.writeLog(this, "autoupdate onStartCommand 没有存储权限");

            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        boolean wifi = HttpStatusManager.isWifi(this);
        if (!wifi) {
            LogHelper.w("wangzixu", "autoupdate onStartCommand noWifi");
            LogHelper.writeLog(this, "autoupdate onStartCommand noWifi");

            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        LogHelper.d("wangzixu", "autoupdate onStartCommand");

        autoUpdateData();
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean mIsSwitching;
    protected void autoUpdateData() {
        if (mIsSwitching) {
            LogHelper.d("wangzixu", "WiFiChangeReceiver autoupdate mIsSwitching return");
            return;
        }

        ModelLockScreen.getAutoUpdateData(this, new onDataResponseListener<List<BigImageBean>>() {
            @Override
            public void onStart() {
                mIsSwitching = true;

                if (App.sHaokanLockView != null) {
                    App.sHaokanLockView.setIvSwitching(true);
                }

                Intent maidianIntent = new Intent(ServiceAutoUpdateImage.this, UmengMaiDianActivity.class);
                maidianIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                maidianIntent.putExtra(UmengMaiDianActivity.KEY_INTENT_EVENTID, "event_100");
                //maidianIntent.putExtra(UmengMaiDianActivity.KEY_INTENT_ARGS, new String[]{"remarks"});
                //maidianIntent.putExtra(UmengMaiDianActivity.KEY_INTENT_VALUES, new String[]{"关闭自动更新"});
                startActivity(maidianIntent);

                MaidianManager.setAction(ServiceAutoUpdateImage.this, "0", 15, "1");

            }

            @Override
            public void onDataSucess(List<BigImageBean> mainImageBeen) {
                LogHelper.d("wangzixu", "autoupdate autoUpdateData onDataSucess");
                LogHelper.writeLog(getApplicationContext(), "autoupdate autoUpdateData onDataSucess");

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String curTime = MyDateTimeUtil.getCurrentSimpleData();
                preferences.edit().putString(KEY_AUTOUPDATA_TIME, curTime).apply();

                if (App.sHaokanLockView != null) {
                    App.sHaokanLockView.loadOfflineNetData(true); //自动更新, 不通过广播出发, 直接调用即可
                    App.sHaokanLockView.setUpdateSign(0);
                    App.sHaokanLockView.setIvSwitching(false);
                }

//                Intent intent = new Intent("com.haokan.receiver.autoupdateimage");
//                sendBroadcast(intent);

                Intent maidianIntent = new Intent(ServiceAutoUpdateImage.this, UmengMaiDianActivity.class);
                maidianIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                maidianIntent.putExtra(UmengMaiDianActivity.KEY_INTENT_EVENTID, "event_101");
                startActivity(maidianIntent);

                mIsSwitching = false;
                App.sMainHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        ServiceAutoUpdateImage.this.stopSelf();
                    }
                });
            }

            @Override
            public void onDataEmpty() {
                mIsSwitching = false;

                if (App.sHaokanLockView != null) {
                    App.sHaokanLockView.setIvSwitching(false);
                }

                LogHelper.d("wangzixu", "autoupdate autoUpdateData onDataEmpty");
                LogHelper.writeLog(getApplicationContext(), "autoupdate autoUpdateData onDataEmpty");
                App.sMainHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        ServiceAutoUpdateImage.this.stopSelf();
                    }
                });
            }

            @Override
            public void onDataFailed(String errmsg) {
                mIsSwitching = false;

                if (App.sHaokanLockView != null) {
                    App.sHaokanLockView.setIvSwitching(false);
                }

                LogHelper.d("wangzixu", "autoupdate autoUpdateData errmsg = " + errmsg);
                LogHelper.writeLog(getApplicationContext(), "autoupdate autoUpdateData errmsg = " + errmsg);
                App.sMainHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        ServiceAutoUpdateImage.this.stopSelf();
                    }
                });
            }

            @Override
            public void onNetError() {
                mIsSwitching = false;

                if (App.sHaokanLockView != null) {
                    App.sHaokanLockView.setIvSwitching(false);
                }

                LogHelper.d("wangzixu", "autoupdate autoUpdateData onNetError");
                LogHelper.writeLog(getApplicationContext(), "autoupdate autoUpdateData onNetError");
                App.sMainHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        ServiceAutoUpdateImage.this.stopSelf();
                    }
                });
            }
        });
    }
}
