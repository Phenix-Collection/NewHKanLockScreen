package com.haokan.pubic.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by wangzixu on 2017/10/26.
 * 本地图片表
 */
@DatabaseTable(tableName = "table_localimg")
public class BeanLocalImage{
    @DatabaseField(id = true)
    public String imgId; //id

    @DatabaseField
    public String imgUrl; //图片url

    @DatabaseField
    public String imgDesc; //图说

    @DatabaseField
    public String imgTitle; //标题

    @DatabaseField(unique = true)
    public int index; //第几个

    @DatabaseField
    public long create_time;
}
