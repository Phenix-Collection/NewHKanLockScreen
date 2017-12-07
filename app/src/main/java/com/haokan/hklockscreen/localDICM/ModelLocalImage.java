package com.haokan.hklockscreen.localDICM;

import android.content.Context;
import android.support.annotation.NonNull;

import com.haokan.pubic.database.BeanLocalImage;
import com.haokan.pubic.database.MyDatabaseHelper;
import com.haokan.pubic.http.onDataResponseListener;
import com.j256.ormlite.dao.Dao;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/11/1.
 */
public class ModelLocalImage {
    public static final String sLocalImgIdPreffix = "local_";

    /**
     * 获取本地相册的图片
     */
    public static void getLocalImgList(final Context context, @NonNull final onDataResponseListener<List<BeanLocalImage>> listener) {
        if (listener == null) {
            return;
        }
        Observable.create(new Observable.OnSubscribe<List<BeanLocalImage>>() {
            @Override
            public void call(Subscriber<? super List<BeanLocalImage>> subscriber) {
                List<BeanLocalImage> list = null;
                try {
                    Dao daoLocalImg = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanLocalImage.class);
                    list = daoLocalImg.queryForAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<BeanLocalImage>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onDataFailed(throwable.getMessage());
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onNext(List<BeanLocalImage> list) {
                        if (list != null && list.size() > 0) {
                            listener.onDataSucess(list);
                        } else {
                            listener.onDataEmpty();
                        }
                    }
                });
    }
}
