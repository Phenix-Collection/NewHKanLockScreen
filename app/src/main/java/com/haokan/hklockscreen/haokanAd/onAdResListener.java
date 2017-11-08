package com.haokan.hklockscreen.haokanAd;

/**
 * Created by wangzixu on 2017/11/4.
 */
public interface onAdResListener<T> {
    void onAdResSuccess(T adRes);
    void onAdResFail(String errmsg);
}
