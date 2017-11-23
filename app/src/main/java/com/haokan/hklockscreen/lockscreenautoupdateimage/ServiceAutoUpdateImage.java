package com.haokan.hklockscreen.lockscreenautoupdateimage;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.haokan.hklockscreen.lockscreen.ModelLockScreen;
import com.haokan.pubic.App;
import com.haokan.pubic.bean.MainImageBeanNew;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.maidian.UmengMaiDianActivity;
import com.haokan.pubic.util.Values;

import java.util.List;

/**
 * Created by wangzixu on 2017/3/17.
 */
public class ServiceAutoUpdateImage extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //用户是否打开了自动更新开关
        boolean auto = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Values.PreferenceKey.KEY_SP_AUTOUPDATEIMAGE, true);
        if (!auto) {
            LogHelper.d("wangzixu", "autoupdate onStartCommand auto = false");
//            LogHelper.writeLog(this, "autoupdate onStartCommand auto = false");

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

        {
            Intent maidianIntent = new Intent(this, UmengMaiDianActivity.class);
            maidianIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            maidianIntent.putExtra(UmengMaiDianActivity.KEY_INTENT_EVENTID, "event_100");
            //maidianIntent.putExtra(UmengMaiDianActivity.KEY_INTENT_ARGS, new String[]{"remarks"});
            //maidianIntent.putExtra(UmengMaiDianActivity.KEY_INTENT_VALUES, new String[]{"关闭自动更新"});
            startActivity(maidianIntent);
        }

        autoUpdateData();
        return super.onStartCommand(intent, flags, startId);
    }


    private boolean mIsSwitching;
    protected void autoUpdateData() {
        if (mIsSwitching) {
            return;
        }
        ModelLockScreen.getAutoUpdateData(this, new onDataResponseListener<List<MainImageBeanNew>>() {
            @Override
            public void onStart() {
                mIsSwitching = true;
            }

            @Override
            public void onDataSucess(List<MainImageBeanNew> mainImageBeen) {
                mIsSwitching = false;
                LogHelper.d("wangzixu", "autoupdate autoUpdateData onDataSucess");
                LogHelper.writeLog(getApplicationContext(), "autoupdate autoUpdateData onDataSucess");
                Intent intent = new Intent("com.haokan.receiver.autoupdateimage");
                sendBroadcast(intent);

                Intent maidianIntent = new Intent(ServiceAutoUpdateImage.this, UmengMaiDianActivity.class);
                maidianIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                maidianIntent.putExtra(UmengMaiDianActivity.KEY_INTENT_EVENTID, "event_101");
                startActivity(maidianIntent);

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
