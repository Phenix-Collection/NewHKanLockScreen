package com.haokan.hklockscreen.haokanAd;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2017/11/6.
 * 封装了广告返回的数据, 广告api返回的数据太复杂
 */
public class BeanAdRes implements Parcelable {
    public String id;
    public String imgUrl; //大图地址
    public String adTitle;
    public String adDesc;
    public String clickHint; //点击提示语
    public String adIconUrl; //图标地址
    public int  type; //返回广告类型:1.banner;2.video;3.native;
    public String landPageUrl; //落地页
    public List<String> onClickUrls = new ArrayList<>(); //点击上报地址
    public List<String> onShowUrls = new ArrayList<>(); //曝光检测地址


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.imgUrl);
        dest.writeString(this.adTitle);
        dest.writeString(this.adDesc);
        dest.writeString(this.clickHint);
        dest.writeString(this.adIconUrl);
        dest.writeInt(this.type);
        dest.writeString(this.landPageUrl);
        dest.writeStringList(this.onClickUrls);
        dest.writeStringList(this.onShowUrls);
    }

    public BeanAdRes() {
    }

    protected BeanAdRes(Parcel in) {
        this.id = in.readString();
        this.imgUrl = in.readString();
        this.adTitle = in.readString();
        this.adDesc = in.readString();
        this.clickHint = in.readString();
        this.adIconUrl = in.readString();
        this.type = in.readInt();
        this.landPageUrl = in.readString();
        this.onClickUrls = in.createStringArrayList();
        this.onShowUrls = in.createStringArrayList();
    }

    public static final Parcelable.Creator<BeanAdRes> CREATOR = new Parcelable.Creator<BeanAdRes>() {
        @Override
        public BeanAdRes createFromParcel(Parcel source) {
            return new BeanAdRes(source);
        }

        @Override
        public BeanAdRes[] newArray(int size) {
            return new BeanAdRes[size];
        }
    };
}
