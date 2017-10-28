package com.haokan.pubic.checkupdate;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangzixu on 2017/7/18.
 */
public class BeanUpdate implements Parcelable {
    private int verCode;
    private String verName;
    private String verDownUrl;
    private String verDesc;

    public int getVerCode() {
        return verCode;
    }

    public void setVerCode(int verCode) {
        this.verCode = verCode;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public String getVerDownUrl() {
        return verDownUrl;
    }

    public void setVerDownUrl(String verDownUrl) {
        this.verDownUrl = verDownUrl;
    }

    public String getVerDesc() {
        return verDesc;
    }

    public void setVerDesc(String verDesc) {
        this.verDesc = verDesc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.verCode);
        dest.writeString(this.verName);
        dest.writeString(this.verDownUrl);
        dest.writeString(this.verDesc);
    }

    public BeanUpdate() {
    }

    protected BeanUpdate(Parcel in) {
        this.verCode = in.readInt();
        this.verName = in.readString();
        this.verDownUrl = in.readString();
        this.verDesc = in.readString();
    }

    public static final Parcelable.Creator<BeanUpdate> CREATOR = new Parcelable.Creator<BeanUpdate>() {
        @Override
        public BeanUpdate createFromParcel(Parcel source) {
            return new BeanUpdate(source);
        }

        @Override
        public BeanUpdate[] newArray(int size) {
            return new BeanUpdate[size];
        }
    };
}
