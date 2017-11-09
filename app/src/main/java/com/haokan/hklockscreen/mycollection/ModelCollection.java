package com.haokan.hklockscreen.mycollection;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.haokan.hklockscreen.localDICM.BeanLocalImage;
import com.haokan.hklockscreen.localDICM.ModelLocalImage;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.database.MyDatabaseHelper;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.FileUtil;
import com.j256.ormlite.dao.Dao;

import java.io.File;
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
    public void addCollection(final Context context, final BeanCollection imageBean, final onDataResponseListener<BeanCollection> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<BeanCollection> observable = Observable.create(new Observable.OnSubscribe<BeanCollection>() {
            @Override
            public void call(Subscriber<? super BeanCollection> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanCollection.class);
                    dao.createOrUpdate(imageBean);

                    //如果是本地图片, 需要看是否有没有这个文件
                    if (!imageBean.imgBigUrl.startsWith("http")) {
                        final File file = new File(imageBean.imgBigUrl);
                        if (!file.exists() || file.length() == 0) {
                            FutureTarget<File> target = Glide.with(context).load(imageBean.imgBigUrl).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                            File bitmapFile = target.get();
                            FileUtil.moveFile(bitmapFile, file);
//                            Glide.with(context).load(imageBean.imgBigUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                    FileUtil.saveBitmapToFile(context, resource, file, false);
//                                }
//                            });
                        }
                    }

                    subscriber.onNext(imageBean);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BeanCollection>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        LogHelper.d("wangzixu", "collection addCollection onDataFailed errmsg = " + e.getMessage());
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(BeanCollection bean) {
                        LogHelper.d("wangzixu", "collection addCollection success");
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
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanCollection.class);
                    int delete = dao.deleteById(bean.imgId);

                    //如果是收藏的本地图片, 需要考虑是否要删除原图
                    if (bean.myType == 3) {
                        Dao dao1 = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanLocalImage.class);
                        Object forId = dao1.queryForId(bean.imgId);
                        if (forId == null) {
                            File imgFile = new File(bean.imgBigUrl);
                            FileUtil.deleteFile(imgFile);
                        }
                    }

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
                        LogHelper.d("wangzixu", "collection delCollection onDataFailed errmsg = " + e.getMessage());
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(Integer bean) {
                        LogHelper.d("wangzixu", "collection delCollection success");
                        listener.onDataSucess(bean);
                    }
                });
    }

    public void delCollections(final Context context, final List<BeanCollection> delList, final onDataResponseListener<Integer> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanCollection.class);
                    int delete = dao.delete(delList);

                    //如果是收藏的本地图片, 需要考虑是否要删除原图
                    Dao dao1 = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanLocalImage.class);
                    for (int i = 0; i < delList.size(); i++) {
                        BeanCollection bean = delList.get(i);
                        if (bean.imgId != null && bean.imgId.startsWith(ModelLocalImage.sLocalImgIdPreffix)) {
                            Object forId = dao1.queryForId(bean.imgId);
                            if (forId == null) {
                                File imgFile = new File(bean.imgBigUrl);
                                FileUtil.deleteFile(imgFile);
                            }
                        }
                    }

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
                        LogHelper.d("wangzixu", "collection delCollections onDataFailed errmsg = " + e.getMessage());
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(Integer bean) {
                        LogHelper.d("wangzixu", "collection delCollections success");
                        listener.onDataSucess(bean);
                    }
                });
    }

    //获取收藏列表, 分页查询
    public void getCollectionList(final Context context, final int index, final onDataResponseListener<List<BeanCollection>> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<List<BeanCollection>> observable = Observable.create(new Observable.OnSubscribe<List<BeanCollection>>() {
            @Override
            public void call(Subscriber<? super List<BeanCollection>> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanCollection.class);
                    List<BeanCollection> list = dao.queryBuilder().orderBy("create_time", false).offset((long)index).limit(50l).query();
//                    List<CollectionBean> list = dao.queryForAll();
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
                .subscribe(new Subscriber<List<BeanCollection>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        LogHelper.d("wangzixu", "collection getCollectionList onDataFailed errmsg = " + e.getMessage());
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(List<BeanCollection> list) {
                        if (list != null && list.size() > 0) {
                            LogHelper.d("wangzixu", "collection getCollectionList success list = " + list.size());
                            listener.onDataSucess(list);
                        } else {
                            LogHelper.d("wangzixu", "collection getCollectionList onDataEmpty");
                            listener.onDataEmpty();
                        }
                    }
                });
    }
}
