package com.haokan.hklockscreen.haokanAd.request;

/**
 * Created by wangzixu on 2017/11/3.
 lat optional string 纬度,-90 至+90,精度到小数后 6 位
 lon optional string 经度,-180 至+180,精度到小数后 6 位
 city optional string 城市名称。例如:"beijing"代表北京,请将地域列表 供给
 DSP。
 */
public class Geo {
    public String lat;
    public String lon;
    public String city;
}
