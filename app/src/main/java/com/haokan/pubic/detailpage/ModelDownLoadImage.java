package com.haokan.pubic.detailpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.util.AssetsImageLoader;
import com.haokan.pubic.util.FileUtil;
import com.haokan.pubic.util.LogHelper;
import com.haokan.pubic.util.Values;
import java.io.File;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/3/21.
 */
public class ModelDownLoadImage {
    /**
     * 下载图片
     * @param context
     * @param listener
     */
    public static void downLoadImg(final Context context, @NonNull final MainImageBean imageBean, @NonNull final onDataResponseListener<File> listener) {
        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    subscriber.onError(new Throwable("sd卡不可用"));
                    LogHelper.d("downLoadImg", "sd card none");
                    return;
                }
                String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_DOWNLOAD_PIC;
                File dir = new File(path);
                dir.mkdirs();

                String name;
                if (TextUtils.isEmpty(imageBean.imgId)) {
                    name = "img_" + imageBean.hashCode() + ".jpg";
                } else {
                    name = "img_" + imageBean.imgId + ".jpg";
                }
                File file = new File(dir, name);

                if (file.exists() && file.length() > 0) {
                    subscriber.onNext(file);
                    subscriber.onCompleted();
                } else {

                    String imgUrl = imageBean.imgBigUrl;
                    try {
                        Bitmap bitmap;
                        if (imgUrl.startsWith("hk_def_imgs")) {
                            bitmap = AssetsImageLoader.loadAssetsImageBitmap(context, imgUrl);
                        } else {
                            bitmap = Glide.with(context).load(imgUrl).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        }
                        if (bitmap != null) {
                            FileUtil.saveBitmapToFile(context, bitmap, file, true);
                            subscriber.onNext(file);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(new Throwable("downLoadImg bitmap is null"));
                        }
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onStart() {
                        listener.onStart();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onDataFailed(throwable.getMessage());
                    }

                    @Override
                    public void onNext(File o) {
                        listener.onDataSucess(o);
                    }
                });
    }
}
