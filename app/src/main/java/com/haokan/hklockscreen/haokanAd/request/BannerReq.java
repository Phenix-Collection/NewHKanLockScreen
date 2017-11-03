package com.haokan.hklockscreen.haokanAd.request;

/**
 * Created by wangzixu on 2017/11/3.
 w required int 广告位宽像素
 h required int 广告位高像素
 mimes optional string array 支持的mime类型,详见类别说明表格2
 api optional int array 支持 APIframeworks的类型.详见类别说明表格4
 */
public class BannerReq {
    public int w;
    public int h;
}
