package com.haokan.hklockscreen.main;

import com.haokan.pubic.bean.MainImageBean;

import java.util.List;

/**
 * Created by xiefeng on 2017/10/18.
 */

public class BeanTimelines {
    public static final int TYPE_TITLE = 1;
    public static final int TYPE_ITEM = 2;

    public int type = TYPE_ITEM;


    public List<MainImageBean> list;
    public String createdAt;
}
