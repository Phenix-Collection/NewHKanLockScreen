package com.haokan.hklockscreen.mycollection;

import android.content.Context;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.haokan.hklockscreen.localDICM.ModelLocalImage;
import com.haokan.pubic.bean.BigImageBean;
import com.haokan.pubic.database.BeanCollection;
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
    public static File getCollectImageDir(Context context) {
        File filesDir = context.getFilesDir();
        File dir = new File(filesDir, "collect/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public void addCollection(final Context context, final BeanCollection imageBean, final onDataResponseListener<BeanCollection> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<BeanCollection> observable = Observable.create(new Observable.OnSubscribe<BeanCollection>() {
            @Override
            public void call(Subscriber<? super BeanCollection> subscriber) {
                File file = null;
                try {
                    //存下这个图片
                    if (TextUtils.isEmpty(imageBean.imgId)) {
                        imageBean.imgId = ModelLocalImage.sLocalImgIdPreffix + System.currentTimeMillis();
                    }
                    file = new File(getCollectImageDir(context), imageBean.imgId + ".jpg");

                    FutureTarget<File> target = Glide.with(context).load(imageBean.imgBigUrl).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    File bitmapFile = target.get();
                    FileUtil.moveFile(bitmapFile, file);

                    imageBean.imgBigUrl = file.getAbsolutePath();
                    imageBean.imgSmallUrl = file.getAbsolutePath();
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanCollection.class);
                    dao.createOrUpdate(imageBean);

//                    App.sMainHanlder.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Glide.with(context).load(oldUrl).asBitmap().listener(new RequestListener<String, Bitmap>() {
//                                @Override
//                                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
//                                    LogHelper.d("wangzixu", "collection addCollection onException");
//                                    e.printStackTrace();
//                                    return false;
//                                }
//
//                                @Override
//                                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                    LogHelper.d("wangzixu", "collection addCollection onResourceReady resource = " + resource);
//                                    return false;
//                                }
//                            }).into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                    LogHelper.d("wangzixu", "collection addCollection resource = " + resource);
//                                    FileUtil.saveBitmapToFile(context, resource, file, false);
//                                }
//                            });
//                        }
//                    });

                    subscriber.onNext(imageBean);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                    if (file != null && file.length() > 0) {
                        FileUtil.deleteFile(file);
                    }
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

    public void delCollection(final Context context, final BigImageBean bean, final onDataResponseListener<Integer> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanCollection.class);
                    Object o = dao.queryForId(bean.imgId);
                    int delete = 0;
                    if (o != null) {
                        BeanCollection collection = (BeanCollection) o;
                        File file = new File(collection.imgBigUrl);
                        FileUtil.deleteFile(file);

                        delete = dao.deleteById(bean.imgId);
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
        if (listener == null || context == null || delList == null) {
            return;
        }

        listener.onStart();
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanCollection.class);
                    for (int i = 0; i < delList.size(); i++) {
                        Object o = dao.queryForId(delList.get(i).imgId);
                        if (o != null) {
                            BeanCollection collection = (BeanCollection) o;
                            File file = new File(collection.imgBigUrl);
                            FileUtil.deleteFile(file);
                        }
                    }
                    int delete = dao.delete(delList);

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
