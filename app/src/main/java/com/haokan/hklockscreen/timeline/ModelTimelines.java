package com.haokan.hklockscreen.timeline;

import android.content.Context;

import com.haokan.pubic.App;
import com.haokan.pubic.http.HttpRetrofitManager;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.UrlsUtil;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.http.request.RequestEntity;
import com.haokan.pubic.http.request.RequestHeader;
import com.haokan.pubic.http.response.ResponseEntity;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xiefeng on 2017/10/18.
 */

public class ModelTimelines {

    public void getTimelinesData(final Context context, final int page, final onDataResponseListener<List<BeanTimelines>> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();

        final RequestEntity<RequestBody_Timelines> requestEntity = new RequestEntity<>();
        final RequestBody_Timelines body = new RequestBody_Timelines();
        body.page = page;
        body.size = 10;
        body.eid = App.sEID;
        body.imgSmallSize = App.sImgSize_Small;
        body.imgBigUrlSize = App.sImgSize_Big;

        RequestHeader<RequestBody_Timelines> header = new RequestHeader(body);
        header.isFageHttp(true);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        Observable<ResponseEntity<ResponseBody_Timelines>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getTimelinesData(UrlsUtil.getTimelineUrl(), requestEntity);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseEntity<ResponseBody_Timelines>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (HttpStatusManager.checkNetWorkConnect(context)) {
                            e.printStackTrace();
                            listener.onDataFailed(e.getMessage());
                        } else {
                            listener.onNetError();
                        }
                    }

                    @Override
                    public void onNext(ResponseEntity<ResponseBody_Timelines> responseEntity) {
                        if (responseEntity != null && responseEntity.getHeader().resCode == 0) {
                            ResponseBody_Timelines body1 = responseEntity.getBody();
                            if (body1.list != null && body1.list.size() > 0) {
                                listener.onDataSucess(body1.list);
                            } else {
                                listener.onDataEmpty();
                            }
                        } else {
                            listener.onDataFailed(responseEntity != null ? responseEntity.getHeader().resMsg : "null");
                        }
                    }
                });

    }

}
