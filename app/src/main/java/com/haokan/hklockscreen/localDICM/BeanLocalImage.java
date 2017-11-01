package com.haokan.hklockscreen.localDICM;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by wangzixu on 2017/10/26.
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
