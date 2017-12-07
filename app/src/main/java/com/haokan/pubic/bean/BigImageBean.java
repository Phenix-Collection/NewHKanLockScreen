package com.haokan.pubic.bean;

import com.haokan.hklockscreen.haokanAd.BeanAdRes;

import java.io.Serializable;

/**
 * Created by wangzixu on 2017/3/18.
 */
public class BigImageBean implements Serializable{
    private static final long serialVersionUID = 1L;

    public String imgId; //id
    public String imgSmallUrl; //缩略图url
    public String imgBigUrl; //图片url
    public String imgDesc; //图说
    public String imgTitle; //标题
    public String linkTitle; //链接名称
    public String linkUrl; //链接地址
    public String typeId; //分类id
    public String typeName; //分类name
    public String shareUrl; //分享地址
    public int isCollect; //是否被收藏, 0没有, 1收藏
    public int colNum; //被收藏的数量
    public int commentNum;//评论数量
    public String cpId; //cpid
    public String cpName; //cpName
    public String jump_id; //本地详情页组图id
    public int collect_num;
    public int share_num;

    /**
     * 锁屏图片的类型, 0代表通过网络图, 1本地相册图, 2代表读取的asset中的默认图片, 3视频
     */
    public int myType;

    public BeanAdRes mBeanAdRes; //广告数据
 }
