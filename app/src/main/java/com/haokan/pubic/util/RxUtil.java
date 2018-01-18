package com.haokan.pubic.util;


import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2018/1/17.
 */
public class RxUtil {
    public static Observable.Transformer<Observable, Observable> shcedulerTrans() {
        return new Observable.Transformer<Observable, Observable>() {
            @Override
            public Observable<Observable> call(Observable<Observable> observableObservable) {
                return observableObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
