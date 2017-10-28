package com.haokan.pubic.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wangzixu on 2017/8/21.
 */
public class MyDateTimeUtil {
    public static final long MINUTE_IN_SECOND = 60;
    public static final long HOUR_IN_SECOND = MINUTE_IN_SECOND * 60;
    public static final long DAY_IN_SECOND = HOUR_IN_SECOND * 24;
    public static final long MONTH_IN_SECOND = DAY_IN_SECOND * 30;
    public static final long YEAR_IN_SECOND = MONTH_IN_SECOND * 12;
    public static final NumberFormat sNumberFormat = NumberFormat.getInstance();;

    public static String simpleData(long time) {
        // HH:mm:ss
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(new Date(time*1000));
    }

    public static String getLogTime(long timeMills) {
        // HH:mm:ss
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return formatter.format(new Date(timeMills));
    }

    /**
     * 转换日期 转换为更为人性化的时间，单位秒
     * @return
     */
    public static String translateDate(long time) {
        Calendar today = Calendar.getInstance();    //今天

//        long curTime = System.currentTimeMillis()/1000;
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        long todayStartTime = today.getTimeInMillis() / 1000;
        if (time >= todayStartTime) { //说明是今天
//            long d = curTime - time;
//            if (d <= MINUTE_IN_SECOND) {
//                return "1分钟前";
//            } else if (d <= HOUR_IN_SECOND) {
//                long m = d / MINUTE_IN_SECOND;
//                if (m <= 0) {
//                    m = 1;
//                }
//                return m + "分钟前";
//            } else {
//                long h = d / HOUR_IN_SECOND;
//                if (h <= 0) {
//                    h = 1;
//                }
//                return h + "小时前";
//            }
            return "今天";
        } else {
            long delta = todayStartTime - time;
//            if (delta >= YEAR_IN_SECOND) {
//                long l = delta / YEAR_IN_SECOND;
//                return l + "年前";
//            } else
            if (delta >= MONTH_IN_SECOND) {
                long l = delta / MONTH_IN_SECOND;
                return l + "月前";
            } else {
                long l = delta / DAY_IN_SECOND;
                return l + "天前";
            }
//            if (time < todayStartTime && time > todayStartTime - oneDay) {
////                SimpleDateFormat dateFormat = new SimpleDateFormat("昨天 HH:mm");
////                Date date = new Date(time * 1000);
////                String dateStr = dateFormat.format(date);
////                if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
////
////                    dateStr = dateStr.replace(" 0", " ");
////                }
//                return "昨天";
//            } else if (time < todayStartTime - oneDay && time > todayStartTime - 2 * oneDay) {
////                SimpleDateFormat dateFormat = new SimpleDateFormat("前天 HH:mm");
////                Date date = new Date(time * 1000);
////                String dateStr = dateFormat.format(date);
////                if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
////                    dateStr = dateStr.replace(" 0", " ");
////                }
//                return "前天";
//            } else {
////                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
////                Date date = new Date(time * 1000);
////                String dateStr = dateFormat.format(date);
////                if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
////                    dateStr = dateStr.replace(" 0", " ");
////                }
//                int n = (int) ((todayStartTime - time) / oneDay + 1);
//                return n + "天前";
        }
    }

    /**
     * 转换时间
     * @return
     */
    public static String getFormateTime(long time) {
        sNumberFormat.setMinimumIntegerDigits(2);
        int sec = (int) (time/1000);
        if (sec < 60) {
            return "00 : "+sNumberFormat.format(sec);
        } else {
            int min = sec / 60;
            sec = sec % 60;
            return sNumberFormat.format(min) + " : " + sNumberFormat.format(sec);
        }
    }
}
