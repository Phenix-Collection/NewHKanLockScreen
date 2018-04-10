package com.haokan.hklockscreen.haokanAd.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wangzixu on 2017/11/3.
 id required string DSP 给出本次竞价的 id
 impid required string 对应 request 中 imp 的 id
 price required int 出价曝光千次价格,单位是分
 adm optional string DSP 自己渲染创意的代码片段;如果是视频是 Vast 片
 段,其他则是 html snippet。
 type optional int 返回广告类型:1.banner;2.video;3.native;
 h optional int 广告位高
 w optional int 广告位宽
 banner optional object Banner 类型广告的对象
 video optional object Video 类型广告的对象;如果平台不支持 Vast,这里不
 为空,否则为空。
 native optional object Native 类型广告的对象
 clickthrough optional string
 array
 点击检测地址数组，第0个元素是落地页，第1个到最
 后一个元素是点击上报地址。
 trackurls optional string
 array
 曝光检测地址,该数组包括第三方的曝光监测。
 ckmapping optional bool 是否需要做 cookie mapping。true.需要;false.不需
 要;
 advdomain optional string 广告主主域名
 */
public class Bid {

    public String id;
    public String impid;
    public String price;
    public String adm;
    /**
     * 1.banner;2.video;3.native;
     */
    public int type;
    public String category;
    public int w;
    public int h;
    public BannerRes banner;
    public boolean ckmapping;
    public String landpage;
    public String advdomain;
    public List<String> clickthrough;
    public List<String> trackurls;
    @SerializedName("native")
    public NativeRes nativeX;
    public VideoRes video;
    public String deep_link;

    public static class BannerRes {
        public List<String> curl;
    }

    public static class NativeRes {
        public List<AssetRes> assets;
    }

    public static class VideoRes {
        /*
        curl required string 创意地址
        duration optional int 视频播放时长,单位秒
         */
        public String curl;
        public int duration;
    }

    /**
     id optional int 元素在原生创意中的编号,和 request 中所传编号对 应
     text optional object 文字对象
     pic optional object 图片对象
     */
    public static class AssetRes {
        public int id;
        public TextRes text;
        public PictureRes pic;
    }

    public static class TextRes {
        public String value;
        /**
         * 1、标题;2、内容简述;3、点击 示语
         */
        public int type;
    }

    public static class PictureRes {
        public int w;
        public int h;
        public int type;
        public List<String> url;
    }
}
