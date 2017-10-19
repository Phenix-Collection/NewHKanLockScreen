package com.haokan.pubic.http;

/**
 * 用于生成访问网络的url地址的工具类
 */
public class UrlsUtil {
    public static String COMPANYID = "10000";
    public static String SECRET_KEY = "GVed-Y~of0pLBjlDzN66V5Q)iipr!x5@";

    //以后禁止使用ip域名
    public static final String URL_HOST = "http://srapi.levect.com/lockscreen/mrkd"; //正式线地址
    private static long sSerialNum = 0000000001; //流水号从0000000001开始计数，步长为1，最大取值为9999999999，循环使用。

    public static String getSerialCode() {
        if (sSerialNum > 9999999999l) {
            sSerialNum = 0000000001;
        }
        String str = String.valueOf(sSerialNum);
        sSerialNum++;
        return str;
    }

    /**
     * 换一换的地址
     */
    public static String getSwitchImgsUrl() {
        return URL_HOST + "/change";
    }

    /**
     * 换一换的地址
     */
    public static String getAutoUpdateImgsUrl() {
        return URL_HOST + "/autoupdate";
    }


    /**
     * 发哥的接口域名
     */
    public static final String URL_HOST_fage = "http://172.18.0.114:3009/app"; //测试地址

    /**
     * Timelines的网络地址
     *
     * @return
     */
    public static String getTimelineUrl() {
        return URL_HOST_fage + "/list";
    }
}
