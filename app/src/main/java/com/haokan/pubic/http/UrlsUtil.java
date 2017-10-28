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

    //******接口系统1 begin, http://srapi.levect.com/api/app/xxxx****
    //升级用的借口
//    public String getUpdateUrl() {
//        return "http://srapi.levect.com/api/app/configure";
//    }
    //******接口系统1 end, http://srapi.levect.com/api/app/xxxx****


    //******接口系统2 begin, http://srapi.levect.com/lockscreen/mrkd/xxxx****
    /**
     * 换一换的地址
     */
    public static String getSwitchImgsUrl() {
        return "http://srapi.levect.com/lockscreen/mrkd/change";
    }

    /**
     * 换一换的地址
     */
    public static String getAutoUpdateImgsUrl() {
        return "http://srapi.levect.com/lockscreen/mrkd/autoupdate";
    }
    //******接口系统2 end, http://srapi.levect.com/lockscreen/mrkd/xxxx****


    //******接口系统3 begin, http://api-m-gray.levect.com/xxxx****
    /**
     * 换一换的地址
     */
    public static String getRecommendUrl() {
        return "http://api-m-gray.levect.com/app/list/img";
    }


    /**
     * //升级用的借口
     */
    public static String getUpdateUrl() {
        return "http://api-m-gray.levect.com/app/config/config";
//        return "http://172.18.0.114:3009/app/config/config";
    }

    /**
     * Timelines的网络地址
     */
    public static String getTimelineUrl() {
        return "http://api-m-gray.levect.com/app/list";
    }
    //******接口系统3 end, http://api-m-gray.levect.com/xxxx****
}
