package com.haokan.hklockscreen.mycollection;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by wangzixu on 2017/10/26.
 */
@DatabaseTable(tableName = "table_collect")
public class CollectionBean implements Parcelable {
    @DatabaseField(id = true)
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

    @DatabaseField()
    public long create_time;

    public boolean isSelected;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imgId);
        dest.writeString(this.imgSmallUrl);
        dest.writeString(this.imgBigUrl);
        dest.writeString(this.imgDesc);
        dest.writeString(this.imgTitle);
        dest.writeString(this.linkTitle);
        dest.writeString(this.linkUrl);
        dest.writeString(this.typeId);
        dest.writeString(this.typeName);
        dest.writeString(this.shareUrl);
        dest.writeInt(this.colNum);
        dest.writeInt(this.commentNum);
        dest.writeString(this.cpId);
        dest.writeString(this.cpName);
        dest.writeLong(this.create_time);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    public CollectionBean() {
    }

    protected CollectionBean(Parcel in) {
        this.imgId = in.readString();
        this.imgSmallUrl = in.readString();
        this.imgBigUrl = in.readString();
        this.imgDesc = in.readString();
        this.imgTitle = in.readString();
        this.linkTitle = in.readString();
        this.linkUrl = in.readString();
        this.typeId = in.readString();
        this.typeName = in.readString();
        this.shareUrl = in.readString();
        this.colNum = in.readInt();
        this.commentNum = in.readInt();
        this.cpId = in.readString();
        this.cpName = in.readString();
        this.create_time = in.readLong();
        this.isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<CollectionBean> CREATOR = new Parcelable.Creator<CollectionBean>() {
        @Override
        public CollectionBean createFromParcel(Parcel source) {
            return new CollectionBean(source);
        }

        @Override
        public CollectionBean[] newArray(int size) {
            return new CollectionBean[size];
        }
    };
}
