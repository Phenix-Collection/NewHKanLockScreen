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
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.cachesys.ACache;
import com.haokan.pubic.http.HttpRetrofitManager;
import com.haokan.pubic.http.HttpStatusManager;
import com.haokan.pubic.http.UrlsUtil;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.http.request.RequestEntity;
import com.haokan.pubic.http.request.RequestHeader;
import com.haokan.pubic.http.response.ResponseEntity;
import com.haokan.pubic.util.FileUtil;
import com.haokan.pubic.util.JsonUtil;
import com.haokan.pubic.util.LogHelper;
import com.haokan.pubic.util.Values;

import java.io.File;
import java.io.FilenameFilter;
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
    public File getOfflineDir(Context context) {
        File filesDir = context.getFilesDir();
        File dir = new File(filesDir, "offline/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public File getLocalImageDir(Context context) {
        File filesDir = context.getFilesDir();
        File dir = new File(filesDir, "localimage/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public void getOffineSwitchData(final Context context, final onDataResponseListener<List<MainImageBean>> listener) {
        if (listener == null) {
            return;
        }
        Observable.create(new Observable.OnSubscribe<ArrayList<MainImageBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<MainImageBean>> subscriber) {
                ArrayList<MainImageBean> list = new ArrayList<>();
                try {
                    ACache aCache = ACache.get(context);
                    Object asObject = aCache.getAsObject(Values.AcacheKey.KEY_ACACHE_OFFLINE_JSONNAME);
                    if (asObject != null && asObject instanceof ArrayList) {
                        list = (ArrayList<MainImageBean>) asObject;
                        LogHelper.d("wangzixu", "getOffineSwitchData list = " + list);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        MainImageBean bean = list.get(i);
                        bean.myType = 0;
                        if (!TextUtils.isEmpty(bean.localUrl)) {
                            File file = new File(bean.localUrl);
                            if (file.exists()) {
                                bean.myType = 1;
                                bean.imgBigUrl = bean.localUrl;
                                bean.imgSmallUrl = bean.localUrl;
                            }
                        }
                    }
                }

                try {
                    if (list == null || list.size() == 0) {
                        InputStream open = context.getAssets().open("default_offline_china.txt");
                        list = JsonUtil.fromJson(open, new TypeToken<ArrayList<MainImageBean>>() {}.getType());
                        if (list != null) {
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).myType = 2;
                            }
                        }
                    }

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
                .subscribe(new Subscriber<ArrayList<MainImageBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onDataFailed(throwable.getMessage());
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<MainImageBean> list) {
                        if (list != null && list.size() > 0) {
                            listener.onDataSucess(list);
                        } else {
                            listener.onDataEmpty();
                        }
                    }
                });
    }

    /**
     * 获取本地相册的图片
     */
    public void getLocalImg(final Context context, @NonNull final onDataResponseListener<List<MainImageBean>> listener) {
        if (listener == null) {
            return;
        }
        Observable.create(new Observable.OnSubscribe<ArrayList<MainImageBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<MainImageBean>> subscriber) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    subscriber.onError(new Throwable("sd卡不可用"));
                    return;
                }

                File dir = getLocalImageDir(context);
                ArrayList<MainImageBean> list = new ArrayList<>();
                if (dir.exists()) {
                    try {
                        File[] files=dir.listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                if (name.startsWith("img")) { //存本地图片时一定要以img开头命名
                                    return true;
                                }
                                return false;
                            }
                        });

                        if (files != null) {
                            for (int i = 0; i < files.length; i++) {
                                MainImageBean imageBean = new MainImageBean();
                                imageBean.myType = 3;
                                imageBean.imgBigUrl = imageBean.imgSmallUrl = imageBean.localUrl = files[i].getAbsolutePath();
                                list.add(imageBean);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        list = null;
                    }
                }
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<MainImageBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onDataFailed(throwable.getMessage());
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<MainImageBean> list) {
                        if (list != null && list.size() > 0) {
                            listener.onDataSucess(list);
                        } else {
                            listener.onDataEmpty();
                        }
                    }
                });
    }


    public void getSwitchData(final Context context, final int page, final onDataResponseListener<List<MainImageBean>> listener) {
        if (listener == null || context == null) {
            return;
        }

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

    public void saveSwitchData(final Context context, final ArrayList<MainImageBean> list) {
        Observable.create(new Observable.OnSubscribe<ArrayList<MainImageBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<MainImageBean>> subscriber) {
                try {
                    saveSwitchDataSync(context, list);
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
                .subscribe(new Subscriber<ArrayList<MainImageBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        LogHelper.d("wangzixu", "saveSwitchData onError");
                    }

                    @Override
                    public void onNext(ArrayList<MainImageBean> list) {
                        LogHelper.d("wangzixu", "saveSwitchData success");
                    }
                });
    }

    private void saveSwitchDataSync(Context context, ArrayList<MainImageBean> list) {
        if (list != null && list.size() > 0) {
            //存储数据json
            ACache aCache = ACache.get(context);
            aCache.put(Values.AcacheKey.KEY_ACACHE_OFFLINE_JSONNAME, list);

            //存储图片文件
            File offlineDir = getOfflineDir(context);
            FileUtil.deleteContents(offlineDir, false);
            for (int i = 0; i < list.size(); i++) {
                MainImageBean imageBean = list.get(i);
                String url = imageBean.imgBigUrl;
                String name;
                if (TextUtils.isEmpty(imageBean.imgId)) {
                    name = "img_" + System.currentTimeMillis()+".jpg";
                } else {
                    name = "img_" + imageBean.imgId + ".jpg";
                }

                File file = new File(offlineDir, name);
                if (!file.exists()) { //图片文件不存在, 就下载
                    try {
                        Bitmap bitmap = Glide.with(context).load(url).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        FileUtil.saveBitmapToFile(context, bitmap, file, false);
                    } catch (Exception e) {
                        if (LogHelper.DEBUG) {
                            LogHelper.e("wangzixu", "saveSwitchData ----下载失败了一张 Glide load i = " + i + " , url = " + url);
                        }
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void getAutoUpdateData(final Context context, final onDataResponseListener<List<MainImageBean>> listener) {
        if (listener == null || context == null) {
            return;
        }

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
                                saveSwitchDataSync(context, body1.list);
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
