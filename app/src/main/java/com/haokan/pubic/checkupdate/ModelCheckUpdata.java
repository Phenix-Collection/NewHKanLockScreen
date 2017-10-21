package com.haokan.pubic.checkupdate;

import android.content.Context;
import android.text.TextUtils;

import com.haokan.pubic.App;
import com.haokan.pubic.http.HttpRetrofitManager;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.UrlsUtil;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.http.request.RequestEntity;
import com.haokan.pubic.http.request.RequestHeader;
import com.haokan.pubic.http.response.ResponseEntity;
import com.haokan.pubic.util.JsonUtil;
import com.haokan.pubic.util.LogHelper;

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
        body.pid = App.sPID;
        body.appId = UrlsUtil.COMPANYID;

        RequestHeader<RequestBody_Update> header = new RequestHeader(body);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        Observable<ResponseEntity<ResponseBody_Update>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getUpdateData(new UrlsUtil().getUpdateUrl(), requestEntity);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseEntity<ResponseBody_Update>>() {
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
                    public void onNext(ResponseEntity<ResponseBody_Update> config) {
                        if (config != null && config.getHeader().resCode == 0) {
                            ResponseBody_Update body1 = config.getBody();

                            if (body1 != null) {
                                String rwStatusStr = body1.getRwStatusStr();
                                String localStr = App.sPID+"_"+App.APP_VERSION_CODE;
                                LogHelper.d("getConfigure", "onNext localStr = " + localStr + ", rwStatusStr = " + rwStatusStr);
                                if (TextUtils.isEmpty(rwStatusStr)) {
                                    App.sReview = "0";
                                } else {
                                    boolean has = rwStatusStr.contains(localStr);
                                    if (has) {
                                        App.sReview = "1";
                                    } else {
                                        App.sReview = "0";
                                    }
                                }
                            }

                            if (body1 != null && !TextUtils.isEmpty(body1.getKd())) {
                                BeanUpdate_mrkd updateBean = JsonUtil.fromJson(body1.getKd(), BeanUpdate_mrkd.class);

                                //将服务器返回的信息转换成升级bean
                                BeanUpdate beanUpdate = new BeanUpdate();
                                beanUpdate.setAppDesc(updateBean.getKd_desc());
                                beanUpdate.setDownloadUrl(updateBean.getKd_dl());
                                beanUpdate.setVersonCode(updateBean.getKd_vc());
                                beanUpdate.setVersonName(updateBean.getKd_vn());

                                listener.onDataSucess(beanUpdate);
                            } else {
                                listener.onDataEmpty();
                            }
                        } else {
                            listener.onDataFailed(config != null ? config.getHeader().resMsg : "null");
                        }
                    }
                });
    }
}
