package com.haokan.hklockscreen.mycollection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by wangzixu on 2017/10/26.
 */
@DatabaseTable(tableName = "table_collect")
public class CollectionBean {
    @DatabaseField(generatedId = true)
    public long _id;

    @DatabaseField
    public String imgId; //id

    @DatabaseField
    public String imgSmallUrl; //缩略图url

    @DatabaseField
    public String imgBigUrl; //图片url

    @DatabaseField
    public String imgDesc; //图说

    @DatabaseField
    public String imgTitle; //标题

    @DatabaseField
    public String linkTitle; //链接名称

    @DatabaseField
    public String linkUrl; //链接地址

    @DatabaseField
    public String typeId; //分类id

    @DatabaseField
    public String typeName; //分类name

    @DatabaseField
    public String shareUrl; //分享地址

//    @DatabaseField
//    public int isCollect; //是否被收藏, 0没有, 1收藏

    @DatabaseField
    public int colNum; //被收藏的数量

    @DatabaseField
    public int commentNum;//评论数量

    @DatabaseField
    public String cpId; //cpid

    @DatabaseField
    public String cpName; //cpName

    @DatabaseField
    public long create_time;

    public boolean isSelected;
}
