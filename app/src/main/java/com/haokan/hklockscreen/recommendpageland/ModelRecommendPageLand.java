package com.haokan.hklockscreen.recommendpageland;

import android.content.Context;

import com.haokan.pubic.App;
import com.haokan.pubic.http.HttpRetrofitManager;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.http.request.RequestEntity;
import com.haokan.pubic.http.request.RequestHeader;
import com.haokan.pubic.http.response.ResponseEntity;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/11/7.
 */
public class ModelRecommendPageLand {
    public void getImgListByGroupId(final Context context, final String groupId, final onDataResponseListener<ResponseBody_ImgGroupList> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();

        final RequestEntity<RequestBody_ImgGroupList> requestEntity = new RequestEntity<>();
        final RequestBody_ImgGroupList body = new RequestBody_ImgGroupList();
        body.pid = App.sPID;
        body.wType = 3;
        body.imgGId = groupId;
        body.eid = App.sEID;

        RequestHeader<RequestBody_ImgGroupList> header = new RequestHeader(body);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        Observable<ResponseEntity<ResponseBody_ImgGroupList>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getImageListByGroupId("http://srapi.levect.com/api/app/imageGroupDetail", requestEntity);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseEntity<ResponseBody_ImgGroupList>>() {
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
                    public void onNext(final ResponseEntity<ResponseBody_ImgGroupList> res) {
                        if (res.getHeader().resCode == 0) {
                            if (res.getBody() != null) {
                                listener.onDataSucess(res.getBody());
                            } else {
                                listener.onDataEmpty();
                            }
                        } else {
                            listener.onDataFailed(res.getHeader().resMsg);
                        }
                    }
                });
    }
}
