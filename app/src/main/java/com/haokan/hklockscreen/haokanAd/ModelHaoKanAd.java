package com.haokan.hklockscreen.haokanAd;

import android.content.Context;
import android.text.TextUtils;

import com.haokan.hklockscreen.haokanAd.request.AdApp;
import com.haokan.hklockscreen.haokanAd.request.BannerReq;
import com.haokan.hklockscreen.haokanAd.request.BidRequest;
import com.haokan.hklockscreen.haokanAd.request.Device;
import com.haokan.hklockscreen.haokanAd.request.Geo;
import com.haokan.hklockscreen.haokanAd.request.Imp;
import com.haokan.hklockscreen.haokanAd.request.NativeReq;
import com.haokan.hklockscreen.haokanAd.response.Bid;
import com.haokan.hklockscreen.haokanAd.response.BidResponse;
import com.haokan.hklockscreen.haokanAd.response.Seat;
import com.haokan.pubic.http.HttpRetrofitManager;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.Values;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/11/3.
 */
public class ModelHaoKanAd {
    /**
     广告展示类型。0.未知;1.固定;2.浮动;3.弹出式;4.插 屏;5.
     全屏;6.视频贴片(instream);7.视频暂停;8.叠加 广告
     (overlay)9.播放器外的广告(companion);10.信息流
     (feeds);11.无线墙。1~5 为 banner 形式;6~9 为视频
     形式;10~11 为移动 原生形式
     */
    public static BidRequest getBidRequest(String adid, int adType, NativeReq nativeReq, BannerReq bannerReq) {
        BidRequest bidRequest = new BidRequest();
        bidRequest.id = String.valueOf(System.currentTimeMillis());

        AdApp adApp = new AdApp();
        adApp.bundle = Values.PACKAGE_NAME;
        adApp.name = "好看锁屏";
        bidRequest.app = adApp;

        Geo geo = new Geo();
        bidRequest.geo = geo;

        Device device = new Device();
        device.devicetype = 3;
        device.os = "Android";
        bidRequest.device = device;

        Imp imp = new Imp();
        imp.id = "1";
        imp.tagid = adid;
        imp.bidfloor = 0;
        imp.adviewtype = adType;
        if (nativeReq != null) {
            imp.nativeX = nativeReq;
        }
        if (bannerReq != null) {
            imp.banner = bannerReq;
        }
        List<Imp> list = new ArrayList<>();
        list.add(imp);
        bidRequest.imp = list;
        return bidRequest;
    }

    /**
     * 广告展示上报
     */
    public static void adShowUpLoad(final String url) {
        LogHelper.d("wangzixu", "ModelHaoKanAd adShowUpLoad begin url = " + url);
        if (TextUtils.isEmpty(url)) {
            return;
        }

        Observable<Object> haoKanAd = HttpRetrofitManager.getInstance().getRetrofitService().get(url);
        haoKanAd
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        LogHelper.d("wangzixu", "ModelHaoKanAd adShowUpLoad end success url = " + url);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        LogHelper.d("wangzixu", "ModelHaoKanAd adShowUpLoad end success url = " + url);
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });

    }

    public static void getAd(Context context, BidRequest request, final onAdResListener<BeanAdRes> listener) {
        if (context == null || request == null || listener == null) {
            return;
        }

//        Observable<BidResponse> haoKanAd = HttpRetrofitManager.getInstance().getRetrofitService().getHaoKanAd("http://203.levect.com/bidRequest/hk", request);
        Observable<BidResponse> haoKanAd = HttpRetrofitManager.getInstance().getRetrofitService().getHaoKanAd("http://203.ad-dev.levect.com/bidRequest/hk", request);
        haoKanAd
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BidResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onAdResFail(e.getMessage());
                    }

                    @Override
                    public void onNext(BidResponse bidResponse) {
                        BeanAdRes adRes = new BeanAdRes();
                        if (bidResponse != null) {
                            adRes.id = bidResponse.id;

                            List<Seat> seatbid = bidResponse.seatbid;
                            if (seatbid != null && seatbid.size() > 0) {
                                Seat seat = seatbid.get(0);
                                List<Bid> bids = seat.bids;
                                if (bids != null && bids.size() > 0) {
                                    Bid bid = bids.get(0);
                                    List<String> trackurls = bid.trackurls;
                                    if (trackurls != null && trackurls.size() > 0) {
                                        adRes.showUpUrl = trackurls.get(0);
                                    }

                                    List<String> clickthrough = bid.clickthrough;
                                    if (clickthrough != null && clickthrough.size() > 0) {
                                        adRes.landPageUrl = clickthrough.get(0);
                                    }

                                    adRes.type = bid.type;
                                    if (bid.type == 1) { //banner
                                        Bid.BannerRes banner = bid.banner;
                                        if (banner != null) {
                                            List<String> curl = banner.curl;
                                            if (curl != null && curl.size() > 0) {
                                                adRes.imgUrl = curl.get(0);
                                            }
                                        }
                                    } else if (bid.type == 2) { //video
                                        Bid.VideoRes video = bid.video;
                                        if (video != null) {
                                            adRes.imgUrl = video.curl;
                                        }
                                    } else if (bid.type == 3) { //native
                                        Bid.NativeRes nativeX = bid.nativeX;
                                        if (nativeX != null) {
                                            List<Bid.AssetRes> assets = nativeX.assets;
                                            if (assets != null && assets.size() > 0) {
                                                for (int i = 0; i < assets.size(); i++) {
                                                    Bid.AssetRes assetRes = assets.get(i);
                                                    Bid.PictureRes pic = assetRes.pic;
                                                    if (pic != null) {
                                                        if (pic.type == 1) {//图标
                                                            List<String> urls = pic.url;
                                                            if (urls != null && urls.size() > 0) {
                                                                adRes.adIconUrl = urls.get(0);
                                                            }
                                                        } else { //主图片
                                                            List<String> urls = pic.url;
                                                            if (urls != null && urls.size() > 0) {
                                                                adRes.imgUrl = urls.get(0);
                                                            }
                                                        }
                                                    }
                                                    Bid.TextRes textRes = assetRes.text;
                                                    if (textRes != null) {
                                                        if (textRes.type == 1) { //标题
                                                            adRes.adTitle = textRes.value;
                                                        } else if (textRes.type == 2) { //描述
                                                            adRes.adDesc = textRes.value;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (TextUtils.isEmpty(adRes.imgUrl)) {
                            listener.onAdResFail("No Ad imgUrl 空");
                        } else {
                            listener.onAdResSuccess(adRes);
                        }
                    }
                });
    }
}
