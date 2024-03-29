package com.haokan.hklockscreen.lockscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.reflect.TypeToken;
import com.haokan.pubic.App;
import com.haokan.pubic.bean.BeanConvertUtil;
import com.haokan.pubic.bean.BigImageBean;
import com.haokan.pubic.database.BeanCollection;
import com.haokan.pubic.database.BeanLocalImage;
import com.haokan.pubic.database.BeanNetImage;
import com.haokan.pubic.database.MyDatabaseHelper;
import com.haokan.pubic.http.HttpRetrofitManager;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.UrlsUtil;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.http.request.RequestEntity;
import com.haokan.pubic.http.request.RequestHeader;
import com.haokan.pubic.http.response.ResponseEntity;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.FileUtil;
import com.haokan.pubic.util.JsonUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/10/16.
 */
public class ModelLockScreen {
    public static File getNetImageDir(Context context) {
        File filesDir = context.getFilesDir();
        File dir = new File(filesDir, "offline/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getLocalImageDir(Context context) {
        File filesDir = context.getFilesDir();
        File dir = new File(filesDir, "localimage/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取锁屏上的图片数据
     * @param context
     * @param listener
     */
    public static void getOfflineNetData(final Context context, final onDataResponseListener<List<BigImageBean>> listener) {
        if (listener == null) {
            return;
        }
        Observable.create(new Observable.OnSubscribe<ArrayList<BigImageBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<BigImageBean>> subscriber) {
                //4.0.4改成数据库存储
                ArrayList<BigImageBean> list = new ArrayList<>();
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanNetImage.class);
                    List<BeanNetImage> listNet = dao.queryForAll();
                    if (listNet != null) {
                        LogHelper.d("wangzixu", "getOfflineNetData listNet = " + listNet.size());
                        for (int i = 0; i < listNet.size(); i++) {
                            BigImageBean bigImageBean = BeanConvertUtil.lsImg2BigImageBean(listNet.get(i));
                            list.add(bigImageBean);
                        }
                    } else {
                        LogHelper.d("wangzixu", "getOfflineNetData listLs = null");
                    }

//                    ACache aCache = ACache.get(context);
//                    Object asObject = aCache.getAsObject(Values.AcacheKey.KEY_ACACHE_OFFLINE_JSONNAME);
//                    LogHelper.d("wangzixu", "getOfflineNetData asObject = " + asObject);
//                    if (asObject != null && asObject instanceof ArrayList) {
//                        try {
//                            ArrayList<BigImageBean> tempList = (ArrayList<BigImageBean>) asObject;
//                            BigImageBean bigImageBean = tempList.get(0); //验证是否会强转失败, 因为4.0.1之前老版本的数据存储的是mainImageBean
//                            list.addAll(tempList);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            LogHelper.d("wangzixu", "getOfflineNetData 强转失败, 老数据强转成mainimageBean");
//                            ArrayList<MainImageBean> oldList = (ArrayList<MainImageBean>) asObject;
//                            for (int i = 0; i < oldList.size(); i++) {
//                                MainImageBean imageBean = oldList.get(i);
//                                BigImageBean bigImageBean = BeanConvertUtil.mainImageBean2BigImageBean(imageBean);
//                                list.add(bigImageBean);
//                            }
//                        }
//                        LogHelper.d("wangzixu", "getOfflineNetData list = " + list.size());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (list == null || list.size() == 0) {
                        InputStream open = context.getAssets().open("default_offline_china.txt");
                        list = JsonUtil.fromJson(open, new TypeToken<ArrayList<BigImageBean>>() {}.getType());
                        if (list != null) {
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).myType = 2;
                            }
                        }
                        LogHelper.d("wangzixu", "getOfflineNetData 取默认图 list = " + list.size());
                    }

                    //处理收藏的状态
                    processCollect(context, list);

                    subscriber.onNext(list);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                    return;
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<BigImageBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onDataFailed(throwable.getMessage());
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<BigImageBean> list) {
                        if (list != null && list.size() > 0) {
                            listener.onDataSucess(list);
                        } else {
                            listener.onDataEmpty();
                        }
                    }
                });
    }

    private static void processCollect(Context context, List<BigImageBean> list) {
        if (list == null) {
            return;
        }
        try {
            Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanCollection.class);
            for (int i = 0; i < list.size(); i++) {
                BigImageBean bean = list.get(i);
                Object o = dao.queryForId(bean.imgId);
                if (o != null) {
                    bean.isCollect = 1;
                } else {
                    bean.isCollect = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取本地相册的图片
     */
    public static void getLocalImg(final Context context, @NonNull final onDataResponseListener<List<BigImageBean>> listener) {
        if (listener == null) {
            return;
        }
        Observable.create(new Observable.OnSubscribe<ArrayList<BigImageBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<BigImageBean>> subscriber) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    subscriber.onError(new Throwable("sd卡不可用"));
                    return;
                }

                ArrayList<BigImageBean> list = new ArrayList<>();
                try {
                    Dao daoLocalImg = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanLocalImage.class);
                    List<BeanLocalImage> list1 = daoLocalImg.queryForAll();
                    if (list1 != null && list1.size() > 0) {
                        for (int i = 0; i < list1.size(); i++) {
                            BeanLocalImage beanLocalImage = list1.get(i);

                            BigImageBean imageBean = new BigImageBean();
                            imageBean.myType = 1;
                            imageBean.imgBigUrl = imageBean.imgSmallUrl = beanLocalImage.imgUrl;
                            imageBean.imgId = beanLocalImage.imgId;
                            imageBean.imgTitle = beanLocalImage.imgTitle;
                            imageBean.imgDesc = beanLocalImage.imgDesc;
                            list.add(imageBean);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                processCollect(context, list);
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<BigImageBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onDataFailed(throwable.getMessage());
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<BigImageBean> list) {
                        if (list != null && list.size() > 0) {
                            listener.onDataSucess(list);
                        } else {
                            listener.onDataEmpty();
                        }
                    }
                });
    }

    /**
     * 获取换一换数据
     * @param context
     * @param page
     * @param listener
     */
    public static void getSwitchData(final Context context, final int page, final onDataResponseListener<List<BigImageBean>> listener) {
        if (listener == null || context == null) {
            return;
        }

        LogHelper.d("wangzixu", "更新图片 getSwitchData 开始");
        listener.onStart();

        final RequestEntity<RequestBody_Switch> requestEntity = new RequestEntity<>();
        final RequestBody_Switch body = new RequestBody_Switch();
        body.cpIds = "10002,10008,10415,10228,10501";
//        body.cpIds = "";
        body.imageSize = App.sImgSize_Big;
        body.imgSmallSize = App.sImgSize_Small;
        body.eid = App.sEID;
        body.pid = App.sPID;
        body.isRecommend = "1";
        body.page = String.valueOf(page);

        RequestHeader<RequestBody_Switch> header = new RequestHeader(body);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        Observable<ResponseEntity<ResponseBody_Switch>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getSwitchData(UrlsUtil.getSwitchImgsUrl(), requestEntity);
        observable
                .map(new Func1<ResponseEntity<ResponseBody_Switch>, ResponseEntity<ResponseBody_Switch>>() {
                    @Override
                    public ResponseEntity<ResponseBody_Switch> call(ResponseEntity<ResponseBody_Switch> responseEntity) {
                        if (responseEntity != null && responseEntity.getHeader().resCode == 0) {
                            ResponseBody_Switch body1 = responseEntity.getBody();
                            if (body1.list != null && body1.list.size() > 0) {
                                boolean success = saveSwitchDataSync(context, body1.list);
                                if (success) {
                                    processCollect(context, body1.list);
                                } else {
                                    responseEntity.getHeader().resCode = -1;
                                    responseEntity.getHeader().resMsg = "下载图片时全部下载失败";
                                }
                            }
                        }
                        return responseEntity;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseEntity<ResponseBody_Switch>>() {
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
                    public void onNext(ResponseEntity<ResponseBody_Switch> responseEntity) {
                        if (responseEntity != null && responseEntity.getHeader().resCode == 0) {
                            ResponseBody_Switch body1 = responseEntity.getBody();
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

//    public static void saveSwitchData(final Context context, final ArrayList<MainImageBean> list) {
//        Observable.create(new Observable.OnSubscribe<ArrayList<MainImageBean>>() {
//            @Override
//            public void call(Subscriber<? super ArrayList<MainImageBean>> subscriber) {
//                try {
//                    saveSwitchDataSync(context, list);
//                    subscriber.onNext(list);
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                    return;
//                }
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ArrayList<MainImageBean>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        throwable.printStackTrace();
//                        LogHelper.d("wangzixu", "saveSwitchData onError");
//                    }
//
//                    @Override
//                    public void onNext(ArrayList<MainImageBean> list) {
//                        LogHelper.d("wangzixu", "saveSwitchData success");
//                    }
//                });
//    }
//
//    public static void deleteOldData(final Context context, final ArrayList<MainImageBean> list) {
//        Observable.create(new Observable.OnSubscribe<ArrayList<MainImageBean>>() {
//            @Override
//            public void call(Subscriber<? super ArrayList<MainImageBean>> subscriber) {
//                try {
//
//                    File offlineDir = getNetImageDir(context);
//                    File[] files = offlineDir.listFiles();
//                    for (int i = 0; i < files.length; i++) {
//                        File file = files[i];
//                        boolean delete = true;
//
//                        for (int j = 0; j < list.size(); j++) {
//                            MainImageBean imageBean = list.get(j);
//                            if (file.getAbsolutePath().equals(imageBean.localUrl)) {
//                                delete = false;
//                                break;
//                            }
//                        }
//
//                        if (delete) {
//                            FileUtil.deleteFile(file);
//                        }
//                    }
//
//                    subscriber.onNext(list);
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                    return;
//                }
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ArrayList<MainImageBean>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        throwable.printStackTrace();
//                        LogHelper.d("wangzixu", "saveSwitchData onError");
//                    }
//
//                    @Override
//                    public void onNext(ArrayList<MainImageBean> list) {
//                        LogHelper.d("wangzixu", "saveSwitchData success");
//                    }
//                });
//    }

    /**
     * 换一换的数据存下来
     * @param context
     * @param list
     */
    private static synchronized boolean saveSwitchDataSync(Context context, final ArrayList<BigImageBean> list) {
        boolean success = false;
        if (list != null && list.size() > 0) {
            if (App.sHaokanLockView != null) {
                App.sMainHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        App.sHaokanLockView.updateTvSwitch(0, list.size());
                    }
                });
            }

            ArrayList<BigImageBean> failList = new ArrayList<>();
            //存储图片文件
            File offlineDir = getNetImageDir(context);
            for (int i = 0; i < list.size(); i++) {
                BigImageBean imageBean = list.get(i);
                String url = imageBean.imgBigUrl;
                String name;
                if (TextUtils.isEmpty(imageBean.imgId)) {
                    name = "img_" + System.currentTimeMillis()+".jpg";
                } else {
                    name = "img_" + imageBean.imgId + ".jpg";
                }

                File file = new File(offlineDir, name);

                if (!file.exists() || file.length() == 0) { //图片文件不存在, 就下载
                    try {
                        Bitmap bitmap = Glide.with(context).load(url).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        FileUtil.saveBitmapToFile(context, bitmap, file, false);

                        imageBean.imgBigUrl = file.getAbsolutePath();
                        imageBean.imgSmallUrl = imageBean.imgBigUrl;
                    } catch (Exception e) {
                        if (LogHelper.DEBUG) {
                            LogHelper.d("wangzixu", "saveSwitchData ----下载失败了一张 Glide load i = " + i + " , url = " + url);
                        }
                        failList.add(imageBean);
                        e.printStackTrace();
                    }
                } else {//图片文件已经存在, 无需下载
                    imageBean.imgBigUrl = file.getAbsolutePath();
                    imageBean.imgSmallUrl = imageBean.imgBigUrl;
                }

                final int tempi = i + 1;
                if (App.sHaokanLockView != null) {
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            App.sHaokanLockView.updateTvSwitch(tempi, list.size());
                        }
                    });
                }
            }

            list.removeAll(failList);

            //存储数据json
//            ACache aCache = ACache.get(context);
//            aCache.put(Values.AcacheKey.KEY_ACACHE_OFFLINE_JSONNAME, list);
            //4.0.4改为数据库
            if (list.size() > 0) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(BeanNetImage.class);
                    long batchNum = System.currentTimeMillis();

                    for (int i = 0; i < list.size(); i++) {
                        BigImageBean bigImageBean = list.get(i);
                        BeanNetImage beanNetImage = BeanConvertUtil.BigImg2NetImageBean(bigImageBean);
                        beanNetImage.batchNum = batchNum;

                        dao.createOrUpdate(beanNetImage);
                    }

                    success = true;

                    //清理老的数据
                    Where where = dao.queryBuilder().where().ne("batchNum", batchNum); //不是本批次的都删除掉
                    List<BeanNetImage> oldData = where.query();

                    LogHelper.d("wangzixu", "saveSwitchData ----oldData = " + oldData);
                    if (oldData != null && oldData.size() > 0) {
                        for (int i = 0; i < oldData.size(); i++) {
                            BeanNetImage beanNetImage = oldData.get(i);
                            try { //一个一个的删除, 防止有某个失败了影响下一个
                                dao.delete(beanNetImage);
                                File file = new File(beanNetImage.imgBigUrl);
                                FileUtil.deleteFile(file);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return success;
    }


    /**
     * 自动更新的数据
     * @param context
     * @param listener
     */
    public static void getAutoUpdateData(final Context context, final onDataResponseListener<List<BigImageBean>> listener) {
        if (listener == null || context == null) {
            return;
        }
        LogHelper.d("wangzixu", "更新图片 getAutoUpdateData 开始");
        listener.onStart();

        final RequestEntity<RequestBody_Switch> requestEntity = new RequestEntity<>();
        final RequestBody_Switch body = new RequestBody_Switch();
        body.cpIds = "10002,10008,10415,10228,10501";
        body.imageSize = App.sImgSize_Big;
        body.imgSmallSize = App.sImgSize_Small;
        body.eid = App.sEID;
        body.pid = App.sPID;
        body.isRecommend = "1";

        RequestHeader<RequestBody_Switch> header = new RequestHeader(body);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        Observable<ResponseEntity<ResponseBody_Switch>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getSwitchData(UrlsUtil.getAutoUpdateImgsUrl(), requestEntity);
        observable
                .map(new Func1<ResponseEntity<ResponseBody_Switch>, ResponseEntity<ResponseBody_Switch>>() {
                    @Override
                    public ResponseEntity<ResponseBody_Switch> call(ResponseEntity<ResponseBody_Switch> responseEntity) {
                        if (responseEntity != null && responseEntity.getHeader().resCode == 0) {
                            ResponseBody_Switch body1 = responseEntity.getBody();
                            if (body1.list != null && body1.list.size() > 0) {
                                boolean success = saveSwitchDataSync(context, body1.list);
                                if (success) {
                                    processCollect(context, body1.list);
                                } else {
                                    responseEntity.getHeader().resCode = -1;
                                    responseEntity.getHeader().resMsg = "下载图片时全部下载失败";
                                }
                            }
                        }
                        return responseEntity;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseEntity<ResponseBody_Switch>>() {
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
                    public void onNext(ResponseEntity<ResponseBody_Switch> responseEntity) {
                        if (responseEntity != null && responseEntity.getHeader().resCode == 0) {
                            ResponseBody_Switch body1 = responseEntity.getBody();
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
