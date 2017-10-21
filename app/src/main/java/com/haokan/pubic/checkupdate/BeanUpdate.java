package com.haokan.pubic.checkupdate;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangzixu on 2017/7/18.
 */
public class BeanUpdate implements Parcelable {
    private int versonCode;
    private String versonName;
    private String downloadUrl;
    private String appDesc;

    public BeanUpdate() {
    }

    protected BeanUpdate(Parcel in) {
        versonCode = in.readInt();
        versonName = in.readString();
        downloadUrl = in.readString();
        appDesc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(versonCode);
        dest.writeString(versonName);
        dest.writeString(downloadUrl);
        dest.writeString(appDesc);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BeanUpdate> CREATOR = new Creator<BeanUpdate>() {
        @Override
        public BeanUpdate createFromParcel(Parcel in) {
            return new BeanUpdate(in);
        }

        @Override
        public BeanUpdate[] newArray(int size) {
            return new BeanUpdate[size];
        }
    };

    public int getVersonCode() {
        return versonCode;
    }

    public void setVersonCode(int versonCode) {
        this.versonCode = versonCode;
    }

    public String getVersonName() {
        return versonName;
    }

    public void setVersonName(String versonName) {
        this.versonName = versonName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }
}
