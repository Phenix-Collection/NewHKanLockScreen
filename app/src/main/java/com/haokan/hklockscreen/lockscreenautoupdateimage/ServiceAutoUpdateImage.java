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
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.util.LogHelper;
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
        LogHelper.d("wangzixu", "autoupdate AlarmOfflineService onStartCommand");
        //用户是否打开了自动更新开关
        boolean auto = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Values.PreferenceKey.KEY_SP_AUTOUPDATEIMAGE, true);
        if (!auto) {
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        //用户是否有sd卡权限
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
            LogHelper.w("wangzixu", "autoupdate 没有存储权限");
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        boolean wifi = HttpStatusManager.isWifi(this);
        if (!wifi) {
            LogHelper.w("wangzixu", "autoupdate noWifi");
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        autoUpdateData();
        return super.onStartCommand(intent, flags, startId);
    }


    private boolean mIsSwitching;
    protected void autoUpdateData() {
        if (mIsSwitching) {
            return;
        }
        final ModelLockScreen modelLockScreen = new ModelLockScreen();
        modelLockScreen.getAutoUpdateData(this, new onDataResponseListener<List<MainImageBean>>() {
            @Override
            public void onStart() {
                mIsSwitching = true;
            }

            @Override
            public void onDataSucess(List<MainImageBean> mainImageBeen) {
                mIsSwitching = false;
                LogHelper.d("wangzixu", "autoupdate onDataSucess");
                Intent intent = new Intent("com.haokan.receiver.autoupdateimage");
                sendBroadcast(intent);
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
                LogHelper.d("wangzixu", "autoupdate onDataEmpty");
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
                LogHelper.d("wangzixu", "autoupdate errmsg = " + errmsg);
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
                LogHelper.d("wangzixu", "autoupdate onNetError");
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
