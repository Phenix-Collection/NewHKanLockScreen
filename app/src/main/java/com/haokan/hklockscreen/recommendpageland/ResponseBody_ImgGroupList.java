package com.haokan.hklockscreen.recommendpageland;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/2/17.
 */
public class ResponseBody_ImgGroupList {
    /**
     * 是否点赞 0没有点赞，1已经点赞
     */
    public int isLike;
    /**
     * 是否收藏 0没有收藏，1已经收藏
     */
    public int isCollect;
    public ArrayList<BeanRecommendPageLand> list;
}
