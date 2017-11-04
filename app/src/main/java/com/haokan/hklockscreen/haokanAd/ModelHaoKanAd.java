package com.haokan.hklockscreen.haokanAd;

import android.content.Context;

import com.haokan.hklockscreen.haokanAd.request.BidRequest;
import com.haokan.hklockscreen.haokanAd.response.BidResponse;
import com.haokan.pubic.http.HttpRetrofitManager;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/11/3.
 */
public class ModelHaoKanAd {
    public static BidRequest getBidRequest() {
        BidRequest bidRequest = new BidRequest();
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
