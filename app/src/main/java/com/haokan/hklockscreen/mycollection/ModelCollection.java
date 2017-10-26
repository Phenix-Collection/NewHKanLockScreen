package com.haokan.hklockscreen.mycollection;

import android.content.Context;

import com.haokan.pubic.bean.BeanConvertUtil;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.database.MyDatabaseHelper;
import com.haokan.pubic.http.onDataResponseListener;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2016/11/28.
 * 所有收藏相关的接口
 */
public class ModelCollection {
    public void addCollection(final Context context, final MainImageBean imageBean, final onDataResponseListener<CollectionBean> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<CollectionBean> observable = Observable.create(new Observable.OnSubscribe<CollectionBean>() {
            @Override
            public void call(Subscriber<? super CollectionBean> subscriber) {
                try {
                    CollectionBean bean = BeanConvertUtil.mainImageBean2CollectionBean(imageBean);

                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(CollectionBean.class);
                    List<CollectionBean> list = dao.queryBuilder().where().eq("imgId", bean.imgId).query();
                    if (list != null && list.size() > 0) { //数据库中已经有了, 更新已有的
                        CollectionBean recordBean = list.get(0);
                        bean._id = recordBean._id;
                        dao.update(bean);
                    } else {
                        dao.create(bean);
                    }

                    subscriber.onNext(bean);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CollectionBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(CollectionBean bean) {
                        listener.onDataSucess(bean);
                    }
                });
    }

    public void delCollection(final Context context, final MainImageBean bean, final onDataResponseListener<Integer> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(CollectionBean.class);
                    DeleteBuilder builder = dao.deleteBuilder();
                    builder.where().eq("imgId", bean.imgId);
                    int delete = builder.delete();

                    subscriber.onNext(delete);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(Integer bean) {
                        listener.onDataSucess(bean);
                    }
                });
    }

    //获取收藏列表, 分页查询
    public void getCollectionList(final Context context, final int page, final onDataResponseListener<List<CollectionBean>> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<List<CollectionBean>> observable = Observable.create(new Observable.OnSubscribe<List<CollectionBean>>() {
            @Override
            public void call(Subscriber<? super List<CollectionBean>> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(CollectionBean.class);
                    List<CollectionBean> list = dao.queryBuilder().orderBy("create_time", false).offset(page*50l).limit(50l).query();
                    subscriber.onNext(list);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CollectionBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(List<CollectionBean> list) {
                        if (list != null && list.size() > 0) {
                            listener.onDataSucess(list);
                        } else {
                            listener.onDataEmpty();
                        }
                    }
                });
    }
}
