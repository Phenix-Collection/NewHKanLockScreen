package com.haokan.hklockscreen.recommendpageland;

import android.os.Parcel;
import android.os.Parcelable;

import com.haokan.hklockscreen.haokanAd.BeanAdRes;

/**
 * Created by wangzixu on 2017/5/31.
 */
public class BeanRecommendLandPage implements Parcelable {
    public String imgId;
    public String imgGId;
    public String imgUrl;
    public String imgTitle;
    public String imgContent;
    public String likedQty;
    public String sUrl;
    public String shareUrl;
    public String cpId;
    public String cpName;
    public String cpLogUrl;
    public int w;
    public int h;

    /**
     *自己规定的type, 因为这个详情页有很多类型, 需要区分开
     * 0大图条目,
     * 1分享按钮,
     * 2广告,
     * 3评论,
     */
    public int myType;

    /**
     * 通过计算得出条目高度.不是服务器返回的, 是自己计算的值
     */
    public int myItemH;

    public BeanAdRes mBeanAdRes;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imgId);
        dest.writeString(this.imgGId);
        dest.writeString(this.imgUrl);
        dest.writeString(this.imgTitle);
        dest.writeString(this.imgContent);
        dest.writeString(this.likedQty);
        dest.writeString(this.sUrl);
        dest.writeString(this.shareUrl);
        dest.writeString(this.cpId);
        dest.writeString(this.cpName);
        dest.writeString(this.cpLogUrl);
        dest.writeInt(this.w);
        dest.writeInt(this.h);
        dest.writeInt(this.myType);
        dest.writeInt(this.myItemH);
        dest.writeParcelable(this.mBeanAdRes, flags);
    }

    public BeanRecommendLandPage() {
    }

    protected BeanRecommendLandPage(Parcel in) {
        this.imgId = in.readString();
        this.imgGId = in.readString();
        this.imgUrl = in.readString();
        this.imgTitle = in.readString();
        this.imgContent = in.readString();
        this.likedQty = in.readString();
        this.sUrl = in.readString();
        this.shareUrl = in.readString();
        this.cpId = in.readString();
        this.cpName = in.readString();
        this.cpLogUrl = in.readString();
        this.w = in.readInt();
        this.h = in.readInt();
        this.myType = in.readInt();
        this.myItemH = in.readInt();
        this.mBeanAdRes = in.readParcelable(BeanAdRes.class.getClassLoader());
    }

    public static final Parcelable.Creator<BeanRecommendLandPage> CREATOR = new Parcelable.Creator<BeanRecommendLandPage>() {
        @Override
        public BeanRecommendLandPage createFromParcel(Parcel source) {
            return new BeanRecommendLandPage(source);
        }

        @Override
        public BeanRecommendLandPage[] newArray(int size) {
            return new BeanRecommendLandPage[size];
        }
    };
}
