package com.haokan.hklockscreen.haokanAd.request;

/**
 * Created by wangzixu on 2017/11/3.
 ua recommend string 浏览器 user-agent 信息(web 流量必填)
 ip optional string ipv4 地址
 devicetype required int 1.pc;2.mobile;3.phone;4.table;5.tv
 make optional string 设备制造商
 model optional string 设备型号
 os optional string 设备操作系统;(Android、IOS、
 WindowsPhone)
 osv optional string 设备操作系统版本;例如 4.2.1:主版本为 4,次版
 本为 2,小版本为 1
 h optional int 设备屏幕纵向分辨率
 w optional int 设备屏幕横向分辨率
 carrier optional int 运营商。1.中国移动;2.中国联通;3.中国电信
 connectiontype optional int 网络连接类型。0.未知
 1.WIFI;2.2G;3.3G;4.4G;5.5G
 ifa optional string iOS 或 Android 终 端 设 备 的 advertising 明
 文,保持字母大写。
 imeisha1 optional string 设备 IMEI(MEID)号,SHA1 加密串
 imeimd5 optional string 设备 IMEI(MEID)号,MD5 加密串
 imeiplain optional string 设备 IMEI(MEID)号,明文串
 aidsha1 optional string 用户终端的 AndroidID,SHA1 加密串
 aidmd5 optional string 用户终端的 AndroidID,MD5 加密串
 aidplain optional string 用户终端的 AndroidID,明文串
 macsha1 optional string 终端网卡 MAC 地址,SHA1 加密串
 macmd5 optional string 终端网卡 MAC 地址,MD5 加密串
 macplain optional string 终端网卡 MAC 地址,明文串
 openudidsha1 optional string IOS 终端设备的 OpenUDID,MD5 加密串
 openudidmd5 optional string IOS 终端设备的 OpenUDID,MD5 加密串
 openudidplain optional string IOS 终端设备的 OpenUDID,明文串
 */
public class Device {
    /**
     * 1.pc;2.mobile;3.phone;4.table;5.tv
     */
    public int devicetype;

    public String os;

    public String ua; // 浏览器 user-agent 信息(web 流量必填)

    public String model; //设备型号

    public String osv; //设备操作系统版本

    public int carrier; //运营商。1.中国移动;2.中国联通;3.中国电信

    public int connectiontype; //网络连接类型。0.未知 1-WIFI; 2-2G; 3-3G; 4-4G; 5-5G

    public int density; // 屏幕密度

    public String macplain; //终端网卡 MAC 地址,明文串

    public int w;
    public int h;

    public String aidplain; //android id

    public String make; //设备制造商

    public String imeiplain; //imei
}
