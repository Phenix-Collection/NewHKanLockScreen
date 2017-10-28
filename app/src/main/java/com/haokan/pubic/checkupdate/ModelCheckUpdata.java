package com.haokan.pubic.checkupdate;

import android.content.Context;

import com.haokan.pubic.http.HttpRetrofitManager;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.UrlsUtil;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.http.request.RequestEntity;
import com.haokan.pubic.http.request.RequestHeader;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/10/21.
 */
public class ModelCheckUpdata {
    public void checkUpdate(final Context context, final onDataResponseListener<BeanUpdate> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();

        final RequestEntity<RequestBody_Update> requestEntity = new RequestEntity<>();
        final RequestBody_Update body = new RequestBody_Update();
        body.companyId = UrlsUtil.COMPANYID;

        RequestHeader<RequestBody_Update> header = new RequestHeader(body);
        header.isFageHttp(true);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        Observable<ResponseBody_Update> observable = HttpRetrofitManager.getInstance().getRetrofitService().getUpdateData(new UrlsUtil().getUpdateUrl(), requestEntity);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody_Update>() {
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
                    public void onNext(ResponseBody_Update config) {
                        if (config != null && "200".equals(config.info.code)) {
                            if (config.data != null){
                                listener.onDataSucess(config.data);
                            } else {
                                listener.onDataEmpty();
                            }
                        } else {
                            listener.onDataFailed(config != null ? config.info.msg : "null");
                        }
                    }
                });
    }
}
