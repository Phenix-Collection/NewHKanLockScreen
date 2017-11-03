package com.haokan.hklockscreen.haokanAd.request;

/**
 * Created by wangzixu on 2017/11/3.
 id optional int app 在 AdExchange 中的编号
 bundle required int app 的包名。Android 是包名,IOS 为 App Store 中 ID
 号。
 name optional int app 的名称
 cat optional int app 的类别,详见类别说明表格 1
 content optional object 媒体当前展示内容(Content)对象
 */
public class AdApp {
    public int id;
    public int bundle;
    public int name;
    public int cat;
}
