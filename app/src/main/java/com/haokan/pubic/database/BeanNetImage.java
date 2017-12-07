package com.haokan.pubic.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by wangzixu on 2017/10/26.
 * 锁屏网络图片表
 */
@DatabaseTable(tableName = "table_lsimg")
public class BeanNetImage {
    @DatabaseField(id = true)
    public String imgId; //id

    /**
     * 网络锁屏图片的类型, 0代表通过网络图, 1本地相册图, 2代表读取的asset中的默认图片, 3视频
     */
    public int myType;

    @DatabaseField
    public String imgSmallUrl; //缩略图url

    @DatabaseField
    public String imgBigUrl; //图片url

    @DatabaseField
    public String videoUrl; //视频地址

    @DatabaseField
    public String imgTitle; //标题

    @DatabaseField
    public String imgDesc; //图说

    @DatabaseField
    public String linkTitle; //链接名称

    @DatabaseField
    public String linkUrl; //链接地址

    @DatabaseField
    public String typeId; //分类id

    @DatabaseField
    public String typeName; //分类name

    @DatabaseField
    public String cpId; //cpid

    @DatabaseField
    public String cpName; //cpName

    @DatabaseField
    public String shareUrl; //分享地址

    @DatabaseField
    public int commentNum;//评论数量

    @DatabaseField
    public int collect_num; //收藏数

    @DatabaseField
    public int share_num; //分享数

    @DatabaseField
    public String jump_id; //本地详情页跳转组图id

    @DatabaseField
    public long create_time;

    /**
     * 更新批次号, 每次更新有唯一的批次号, 一次更新中的数据批次号相同
     */
    @DatabaseField
    public long batchNum;
}
