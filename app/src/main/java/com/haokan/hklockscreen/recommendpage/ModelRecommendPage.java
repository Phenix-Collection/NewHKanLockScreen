package com.haokan.hklockscreen.recommendpage;

import android.content.Context;

import com.haokan.pubic.App;
import com.haokan.pubic.http.HttpRetrofitManager;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.UrlsUtil;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.http.request.RequestEntity_FaGe;
import com.haokan.pubic.http.request.RequestHeader_FaGe;
import com.haokan.pubic.http.response.ResponseEntity;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/10/19.
 */
public class ModelRecommendPage {
    public void getRecommendData(final Context context, String typeName, final int page, final onDataResponseListener<List<BeanRecommendItem>> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();

        final RequestEntity_FaGe<RequestBody_Recommend> requestEntity = new RequestEntity_FaGe<>();
        final RequestBody_Recommend body = new RequestBody_Recommend();
        body.imageSize = App.sImgSize_Small;
        body.eid = App.sEID;
        body.page = page;
        body.size = 10;
        body.typeName = typeName;

        RequestHeader_FaGe<RequestBody_Recommend> header = new RequestHeader_FaGe(body);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        Observable<ResponseEntity<ResponseBody_Recommend>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getRecommendData(UrlsUtil.getRecommendUrl(), requestEntity);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseEntity<ResponseBody_Recommend>>() {
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
                    public void onNext(ResponseEntity<ResponseBody_Recommend> responseEntity) {
                        if (responseEntity != null && responseEntity.getHeader().resCode == 0) {
                            ResponseBody_Recommend body1 = responseEntity.getBody();
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
