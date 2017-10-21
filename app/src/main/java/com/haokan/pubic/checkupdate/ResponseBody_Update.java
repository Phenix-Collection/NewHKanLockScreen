package com.haokan.pubic.checkupdate;

/**
 * Created by wangzixu on 2017/2/17.
 */
public class ResponseBody_Update {

    /**
     * isReview : 1
     * isShowAd : 1
     */
    private String isReview;
    /**
     * 1, review
     * 0, 没有review
     */
    private String kd_isreview_227;
    private String isShowAd;
    private String kd;
    /**
     * 控制审核状态的字符串
     * 227_420,216_420   pid_vercode 多个逗号分隔
     * pid_vercode
     */
    private String rwStatusStr;

    public String getRwStatusStr() {
        return rwStatusStr;
    }

    public void setRwStatusStr(String rwStatusStr) {
        this.rwStatusStr = rwStatusStr;
    }

    public String getKd_isreview_227() {
        return kd_isreview_227;
    }

    public void setKd_isreview_227(String kd_isreview_227) {
        this.kd_isreview_227 = kd_isreview_227;
    }

    public String getKd() {
        return kd;
    }

    public void setKd(String kd) {
        this.kd = kd;
    }

    public String getIsReview() {
        return isReview;
    }

    public void setIsReview(String isReview) {
        this.isReview = isReview;
    }

    public String getIsShowAd() {
        return isShowAd;
    }

    public void setIsShowAd(String isShowAd) {
        this.isShowAd = isShowAd;
    }
}
