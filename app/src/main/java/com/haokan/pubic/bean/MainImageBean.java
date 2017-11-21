package com.haokan.pubic.bean;

import com.haokan.hklockscreen.haokanAd.BeanAdRes;

import java.io.Serializable;

/**
 * Created by wangzixu on 2017/3/18.
 */
public class MainImageBean implements Serializable{
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

    /**
     * 自己定义的类型, 0代表需要网络下载的图片, 1代表存储的离线图片, 2代表读取的asset中的默认图片, 3代表添加的本地相册图片
     */
    public int myType;
    public String localUrl;//当图片类型为1时, 为存储的离线图片, 用此地址

    public BeanAdRes mBeanAdRes;
 }
