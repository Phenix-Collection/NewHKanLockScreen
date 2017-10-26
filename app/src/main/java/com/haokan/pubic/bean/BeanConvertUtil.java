package com.haokan.pubic.bean;

import com.haokan.hklockscreen.mycollection.CollectionBean;

/**
 * Created by wangzixu on 2017/10/26.
 */
public class BeanConvertUtil {
    public static CollectionBean mainImageBean2CollectionBean(MainImageBean imageBean) {
        CollectionBean collectionBean = new CollectionBean();
        collectionBean.imgId = imageBean.imgId;
        collectionBean.imgSmallUrl = imageBean.imgSmallUrl;
        collectionBean.imgBigUrl = imageBean.imgBigUrl;
        collectionBean.imgDesc = imageBean.imgDesc;
        collectionBean.imgTitle = imageBean.imgTitle;
        collectionBean.linkTitle = imageBean.linkTitle;
        collectionBean.linkUrl = imageBean.linkUrl;
        collectionBean.typeId = imageBean.typeId;
        collectionBean.typeName = imageBean.typeName;
        collectionBean.shareUrl = imageBean.shareUrl;
        collectionBean.colNum = imageBean.colNum;
        collectionBean.commentNum = imageBean.commentNum;
        collectionBean.cpId = imageBean.cpId;
        collectionBean.cpName = imageBean.cpName;

        return collectionBean;
    }
}
