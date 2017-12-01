package com.haokan.hklockscreen.lockscreenautoupdateimage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.haokan.pubic.logsys.LogHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by wangzixu on 2017/3/17.
 */
public class AlarmUtil {
    /**
     * 离线图片的更新周期, ms
     */
    public static final long UPDATA_PERIOD = 24*60*60*1000;
    public static void setOfflineAlarm(final Context context) {
        final Calendar calendar = Calendar.getInstance(); //当前时间的日历

        //下一天的0-5点
        Random random = new Random();
        calendar.set(Calendar.HOUR_OF_DAY, random.nextInt(5));
        calendar.add(Calendar.DAY_OF_MONTH, 1);//下一天

        if (LogHelper.DEBUG) {
            // HH:mm:ss
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());;
            LogHelper.d("wangzixu", "autoupdate AlarmUtil setOfflineAlarm time = " + formatter.format(calendar.getTimeInMillis()));
            LogHelper.writeLog(context, "autoupdate AlarmUtil setOfflineAlarm time = " + formatter.format(calendar.getTimeInMillis()));
        }

        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.setAction("com.haokan.service.autoupdateimage");
        intent.putExtra("type", 1); //代表是通过alarm启动的service
        //com.haokan.service.autoupdateimage
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), UPDATA_PERIOD, pendingIntent); //每天更新一次
    }


    //测试用
//    public static final long UPDATA_PERIOD = 30*1000; //测试用
//    public static void setOfflineAlarm(final Context context) {
//        final Calendar calendar = Calendar.getInstance(); //当前时间的日历
//
//        calendar.add(Calendar.SECOND, 30);//测试用
//
//        if (LogHelper.DEBUG) {
//            // HH:mm:ss
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());;
//            LogHelper.d("wangzixu", "autoupdate AlarmUtil setOfflineAlarm time = " + formatter.format(calendar.getTimeInMillis()));
//            LogHelper.writeLog(context, "autoupdate AlarmUtil setOfflineAlarm time = " + formatter.format(calendar.getTimeInMillis()));
//        }
//
//        Intent intent = new Intent();
//        intent.setPackage(context.getPackageName());
//        intent.setAction("com.haokan.service.autoupdateimage");
//        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), UPDATA_PERIOD, pendingIntent); //每天更新一次
//    }
}
