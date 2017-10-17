package com.haokan.pubic.bean;

/**
 * Created by wangzixu on 2017/3/18.
 */
public class MainImageBean {
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
    public int shareNum; //被分享的数量
    public int isLike; //是否被赞
    public int likeNum; //赞的数量
    public int commentNum;//评论数量
    public String cpId; //cpid
    public String cpName; //cpName

    /**
     * 自己定义的类型, 0代表网络下载的图片, 1代表添加的本地图说, 因为本地图片没有图说等信息, 所以需要区分开
     */
    public int myType;
 }
