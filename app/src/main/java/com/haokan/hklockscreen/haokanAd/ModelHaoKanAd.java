package com.haokan.hklockscreen.haokanAd;

import android.content.Context;

import com.haokan.hklockscreen.haokanAd.request.AdApp;
import com.haokan.hklockscreen.haokanAd.request.BannerReq;
import com.haokan.hklockscreen.haokanAd.request.BidRequest;
import com.haokan.hklockscreen.haokanAd.request.Device;
import com.haokan.hklockscreen.haokanAd.request.Geo;
import com.haokan.hklockscreen.haokanAd.request.Imp;
import com.haokan.hklockscreen.haokanAd.request.NativeReq;
import com.haokan.hklockscreen.haokanAd.response.BidResponse;
import com.haokan.pubic.http.HttpRetrofitManager;
import com.haokan.pubic.util.Values;

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

        //// TODO: 2017/11/4 广告继续 ----
        return bidRequest;
    }



    public static void getAd(Context context, BidRequest request, onAdResListener listener) {
        if (context == null || request == null || listener == null) {
            return;
        }

        Observable<BidResponse> haoKanAd = HttpRetrofitManager.getInstance().getRetrofitService().getHaoKanAd("http://203.levect.com/bidRequest/hk", request);
        haoKanAd
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BidResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BidResponse bidResponse) {

                    }
                });
    }
}
