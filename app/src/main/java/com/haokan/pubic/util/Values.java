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

        public static final String PATH_DOWNLOADAPK = "/Levect/" + PACKAGE_NAME + "/";
        public static final String PATH_DOWNLOAD_PIC = "/Levect/" + PACKAGE_NAME + "/HkImages/";
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
        public static final String KEY_SP_SWITCH_NOWIFI = "switchnowifi";

        /**
         * 锁屏开关
         */
        public static final String KEY_SP_OPENLOCKSCREEN = "openlc";

        /**
         * 是否自动更新锁屏图片
         */
        public static final String KEY_SP_AUTOUPDATEIMAGE = "autoimage";
        /**
         *
         * 是否是第一次安装
         */
        public static final String KEY_SP_FIRSTINSTALL = "firstinstall";
    }
}
