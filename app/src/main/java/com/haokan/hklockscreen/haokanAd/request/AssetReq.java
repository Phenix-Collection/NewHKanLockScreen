package com.haokan.hklockscreen.haokanAd.request;

/**
 * Created by wangzixu on 2017/11/3.
 id optional int 元素处于原生广告的编号;id 列表详见表 5
 text optional object 文字 Text 对象
 pic optional object 文字 Picture 对象
 */
public class AssetReq {
    /**
     表格5：Assets Id 列表
     值 说明
     1 标题
     2 主图片
     3 图标
     4 内容简述
     5 点击提示语
     */
    public int id;

    public TextReq text;
    public PictureReq pic;
}
