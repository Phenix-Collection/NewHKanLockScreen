package com.haokan.hklockscreen.recommendpagelist;

import android.os.Parcel;
import android.os.Parcelable;

import com.haokan.hklockscreen.haokanAd.BeanAdRes;

/**
 * Created by wangzixu on 2017/10/19.
 */
public class BeanRecommendItem implements Parcelable {
    /**
     * GroupId : 18128
     * imgTitle : 2017世界斯诺克中国锦标赛资格赛：罗伯逊VS李行
     * imgDesc : 2017年8月16日，2017世界斯诺克中国锦标赛资格赛，罗伯逊vs李行。
     * typeName : 体育
     * const : 6
     * urlClick : http://photo.levect.com/detail.html?i=18128&e=118899
     * cover : http://res.levect.com/hkresource/73/63/1502856852093736373.jpg@!360x640?k=10000&pid=0&eid=10002
     * favNum : 1
     * likeNum : 2
     */
    public String GroupId;
    public String imgTitle;
    public String imgDesc;
    public String typeName;
    public String urlClick;
    public String cover;
    public int favNum;
    public int shareNum;

    public BeanAdRes mBeanAdRes;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.GroupId);
        dest.writeString(this.imgTitle);
        dest.writeString(this.imgDesc);
        dest.writeString(this.typeName);
        dest.writeString(this.urlClick);
        dest.writeString(this.cover);
        dest.writeInt(this.favNum);
        dest.writeInt(this.shareNum);
    }

    public BeanRecommendItem() {
    }

    protected BeanRecommendItem(Parcel in) {
        this.GroupId = in.readString();
        this.imgTitle = in.readString();
        this.imgDesc = in.readString();
        this.typeName = in.readString();
        this.urlClick = in.readString();
        this.cover = in.readString();
        this.favNum = in.readInt();
        this.shareNum = in.readInt();
    }

    public static final Parcelable.Creator<BeanRecommendItem> CREATOR = new Parcelable.Creator<BeanRecommendItem>() {
        @Override
        public BeanRecommendItem createFromParcel(Parcel source) {
            return new BeanRecommendItem(source);
        }

        @Override
        public BeanRecommendItem[] newArray(int size) {
            return new BeanRecommendItem[size];
        }
    };
}
