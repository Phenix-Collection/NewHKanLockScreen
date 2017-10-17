package com.haokan.hklockscreen.lockscreen.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreen.activity.ActivityLockScreen;
import com.haokan.hklockscreen.lockscreen.detailpage.CV_DetailPage_LockScreen;
import com.haokan.hklockscreen.lockscreen.receiver.ReceiverLockScreen;



public class ServiceLockScreen extends Service {
    private ReceiverLockScreen mReceiver;
    private final String TAG = "ServiceLockScreen";
    public static CV_DetailPage_LockScreen sHaokanLockView;
    public static boolean sLockEnable = false;

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.setPriority(Integer.MAX_VALUE);
        mReceiver = new ReceiverLockScreen();
        registerReceiver(mReceiver, filter);

        setForeground();
    }

    private void setForeground() {
        Notification note = new Notification(0, null, System.currentTimeMillis());
        note.flags |= 32;
        startForeground(42, note);

        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, ActivityLockScreen.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("好看锁屏") // 设置下拉列表里的标题
                .setSmallIcon(R.drawable.ic_launcher) // 设置状态栏内的小图标
                .setContentText("正在为你展现精彩内容") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        Notification notification = builder.build(); // 获取构建好的Notification
        startForeground(110, notification);// 开始前台服务
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand type");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
//        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        if (sHaokanLockView != null) {
            sHaokanLockView.onDestory();
        }
        sHaokanLockView = null;
        unregisterReceiver(mReceiver);
    }
}
