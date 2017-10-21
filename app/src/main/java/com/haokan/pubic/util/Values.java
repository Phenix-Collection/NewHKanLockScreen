package com.haokan.pubic.util;

/**
 * 应用用到的一些常量
 */
public class Values {
    //应用包名
    public static final String PACKAGE_NAME = "com.haokanhaokan.lockscreen";

    //一些文件路径相关的位置
    public static class Path{
        public static final String PATH_BASE = "/Levect/" + PACKAGE_NAME + "/";

        public static final String PATH_DOWNLOADAPK = PATH_BASE + "updateapp";
    }

    /**
     * 自定义的一些action
     */
    public static class Action {
        public static final String SERVICE_GA_SERVICE = "com.haokan.service.gaservice";
    }

    public static class AcacheKey {
        /**
         * 锁屏存储本地相册图片的数据json对象key
         */
        public static final String KEY_ACACHE_LOCALIMG_JSONNAME = "localphoto_json";

        /**
         * 离线图片
         */
        public static final String KEY_ACACHE_OFFLINE_JSONNAME = "offline_json";
    }

    public static class PreferenceKey {
        /**
         * 应用渠道pid
         */
        public static final String KEY_SP_PID = "pid";
        /**
         * 手机唯一标识did
         */
        public static final String KEY_SP_DID = "did";

        /**
         * 手机型号
         */
        public static final String KEY_SP_PHONE_MODEL = "phonemodel";

        /**
         * 是否允许在非wifi环境下换一换
         */
        public static final String KEY_SP_SWITCH_WIFI = "switchwifi";

        /**
         * 锁屏开关
         */
        public static final String KEY_SP_OPENLOCKSCREEN = "openlc";
    }
}
