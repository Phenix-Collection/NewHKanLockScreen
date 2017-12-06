package com.haokan.pubic.bean;

import android.text.TextUtils;

import com.haokan.hklockscreen.localDICM.ModelLocalImage;
import com.haokan.hklockscreen.mycollection.BeanCollection;
import com.haokan.hklockscreen.recommendpageland.BeanRecommendPageLand;

/**
 * Created by wangzixu on 2017/10/26.
 */
public class BeanConvertUtil {
    public static BeanCollection bigImageBean2CollectionBean(BigImageBean imageBean) {
        BeanCollection collectionBean = new BeanCollection();
        collectionBean.imgId = imageBean.imgId;
        if (TextUtils.isEmpty(collectionBean.imgId)) {
            collectionBean.imgId = ModelLocalImage.sLocalImgIdPreffix + System.currentTimeMillis();
        }
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
        collectionBean.collect_num = imageBean.collect_num;
        collectionBean.share_num = imageBean.share_num;

        collectionBean.create_time = System.currentTimeMillis();
        return collectionBean;
    }

    public static BigImageBean collectionBean2BigImageBean(BeanCollection collectionBean) {
        BigImageBean imgBean = new BigImageBean();
        imgBean.imgId = collectionBean.imgId;
        imgBean.imgSmallUrl = collectionBean.imgSmallUrl;
        imgBean.imgBigUrl = collectionBean.imgBigUrl;
        imgBean.imgDesc = collectionBean.imgDesc;
        imgBean.imgTitle = collectionBean.imgTitle;
        imgBean.linkTitle = collectionBean.linkTitle;
        imgBean.linkUrl = collectionBean.linkUrl;
        imgBean.typeId = collectionBean.typeId;
        imgBean.typeName = collectionBean.typeName;
        imgBean.shareUrl = collectionBean.shareUrl;
        imgBean.colNum = collectionBean.colNum;
        imgBean.commentNum = collectionBean.commentNum;
        imgBean.cpId = collectionBean.cpId;
        imgBean.cpName = collectionBean.cpName;
        imgBean.collect_num = collectionBean.collect_num;
        imgBean.share_num = collectionBean.share_num;
        imgBean.isCollect = 1;
        if (TextUtils.isEmpty(collectionBean.imgId) || collectionBean.imgId.startsWith(ModelLocalImage.sLocalImgIdPreffix)) {
            imgBean.myType = 3;
        }

        return imgBean;
    }

    public static BigImageBean mainImageBean2BigImageBean(MainImageBean fromBean) {
        BigImageBean imgBean = new BigImageBean();
        imgBean.imgId = fromBean.imgId;
        imgBean.imgSmallUrl = fromBean.imgSmallUrl;
        imgBean.imgBigUrl = fromBean.imgBigUrl;
        imgBean.imgDesc = fromBean.imgDesc;
        imgBean.imgTitle = fromBean.imgTitle;
        imgBean.linkTitle = fromBean.linkTitle;
        imgBean.linkUrl = fromBean.linkUrl;
        imgBean.typeId = fromBean.typeId;
        imgBean.typeName = fromBean.typeName;
        imgBean.shareUrl = fromBean.shareUrl;
        imgBean.colNum = fromBean.colNum;
        imgBean.commentNum = fromBean.commentNum;
        imgBean.cpId = fromBean.cpId;
        imgBean.cpName = fromBean.cpName;
        imgBean.isCollect = fromBean.isCollect;
        imgBean.myType = fromBean.myType;
        imgBean.jump_id = fromBean.imgId;
        imgBean.collect_num = 0;
        imgBean.share_num = 0;
        return imgBean;
    }

    public static BigImageBean recommendLandBean2MainImageBean(BeanRecommendPageLand fromBean) {
        BigImageBean imgBean = new BigImageBean();
        imgBean.imgId = fromBean.imgId;
        imgBean.imgSmallUrl = fromBean.sUrl;
        imgBean.imgBigUrl = fromBean.imgUrl;
        imgBean.imgDesc = fromBean.imgContent;
        imgBean.imgTitle = fromBean.imgTitle;
        imgBean.linkTitle = "";
        imgBean.linkUrl = "";
        imgBean.typeId = "";
        imgBean.typeName = "";
        imgBean.shareUrl = fromBean.shareUrl;
        imgBean.colNum = 0;
        imgBean.commentNum = 0;
        imgBean.cpId = fromBean.cpId;
        imgBean.cpName = fromBean.cpName;
        imgBean.isCollect = 0;
        imgBean.collect_num = 0;
        imgBean.share_num = 0;
        if (TextUtils.isEmpty(fromBean.imgId) || fromBean.imgId.startsWith(ModelLocalImage.sLocalImgIdPreffix)) {
            imgBean.myType = 3;
        }
        return imgBean;
    }
}
