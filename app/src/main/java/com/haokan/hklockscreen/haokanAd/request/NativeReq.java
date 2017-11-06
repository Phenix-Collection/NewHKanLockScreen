package com.haokan.hklockscreen.haokanAd.request;

import java.util.List;

/**
 * Created by wangzixu on 2017/11/3.
 w required int 广告位宽像素
 h required int 广告位高像素
 style required int 见类别表 3
 assets required object array 描述原生广告中包含的元素的对象(Asset)数组
 */
public class NativeReq {
    public int w;
    public int h;
    /**
     表格3：原生style列表
     值 说明
     0 标题（文字广告）
     1 标题+主图片（组图广告）
     2 标题+内容描述+主图片（图文广告）
     3 标题+图标+内容简述（图文广告）
     4 标题+图标+内容描述+主图片（图文广告）
     5 标题+图标+内容描述+主图片+点击提示语（图文广告）
     */
    public int style;

    public List<AssetReq> assets;

    /**
     * Created by wangzixu on 2017/11/3.
     id optional int 元素处于原生广告的编号;id 列表详见表 5
     text optional object 文字 Text 对象
     pic optional object 文字 Picture 对象
     */
    public static class AssetReq {
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
}
