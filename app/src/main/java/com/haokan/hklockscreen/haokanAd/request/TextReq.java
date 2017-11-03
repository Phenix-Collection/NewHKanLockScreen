package com.haokan.hklockscreen.haokanAd.request;

/**
 * Created by wangzixu on 2017/11/3.
 */
public class TextReq {
    /**
     type required int 1、标题;2、内容简述;3、点击 示语
     maxlen optional int 文字的最大长度
     */
    public int type;

    /**
     * 文字的最大长度
     */
    public int maxlen;
}
