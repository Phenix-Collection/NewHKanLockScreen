package com.haokan.hklockscreen.mycollection;

import com.haokan.pubic.database.BeanCollection;

/**
 * Created by wangzixu on 2017/10/26.
 */
public class EventCollectionChange {
    /**
     * 删除用的的imageid, 多个的话逗号分隔
     */
    public String imgIds = "";

    public boolean mIsAdd;

    public BeanCollection mBean;

    public Object mFrom;
}
