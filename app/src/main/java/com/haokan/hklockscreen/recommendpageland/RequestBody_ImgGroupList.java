package com.haokan.hklockscreen.recommendpageland;

/**
 * Created by wangzixu on 2017/2/17.
 * 获取频道/类型列表的请求体
 */
public class RequestBody_ImgGroupList {
    /*
    wType	INTEGER	1	否	图片宽度类型：1,360px; 2,720px; 3,1080px; 4,1440px
    imgGId	STRING		否	组图ID
    pid	STRING		否	pid
     */
    public int wType;
    public String imgGId;
    public String pid;
    public String did;
    public String eid;
}
