package com.haokan.hklockscreen.util;

/**
 * 应用用到的一些常量
 */
public class Values {
    //一些文件路径相关的位置
    public static class Path{
        public static final String PATH_CLIP_AVATAR = "/Levect/user_avatar"; //剪裁的头像存储的位置
    }

    /**
     * 自定义的一些action
     */
    public static class Action {
        public static final String SERVICE_GA_SERVICE = "com.haokan.service.gaservice";
    }

    public static class AcacheKey {
        /**
         * 锁屏用的离线图片json对象key
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

    }
}
