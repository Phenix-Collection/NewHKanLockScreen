package com.haokan.pubic.detailpage;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.haokan.pubic.App;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.DisplayUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/3/21.
 */
public class ModelSetWallpaper {
    public void setWallPaper(final Context context, @NonNull final String imgUrl, @NonNull final onDataResponseListener listener) {
        if (context == null || TextUtils.isEmpty(imgUrl) || listener == null) {
            return;
        }

        if (TextUtils.isEmpty(imgUrl)) {
            listener.onDataFailed("imgUrl must not null");
            return;
        }

        listener.onStart();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                SetWallPaperReceiver setWallPaperReceiver = null;
                try {
                    Bitmap bitmap = Glide.with(context).load(imgUrl).asBitmap().dontAnimate().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    if (bitmap != null) {
                        IntentFilter filter=new IntentFilter();
                        filter.addAction(Intent.ACTION_SET_WALLPAPER);
                        filter.addAction(Intent.ACTION_WALLPAPER_CHANGED);
                        setWallPaperReceiver = new SetWallPaperReceiver(context, listener);
                        context.registerReceiver(setWallPaperReceiver, filter);

                        Point point = DisplayUtil.getRealScreenPoint(context);
                        int screenW = point.x;
                        int screenH = point.y;

                        WallpaperManager manager = WallpaperManager.getInstance(context);
//                        manager.suggestDesiredDimensions(screenW, screenH);
                        //manager.setWallpaperOffsetSteps(0, 0);

                        Canvas canvas = new Canvas();
                        int w = bitmap.getWidth();
                        int h = bitmap.getHeight();

                        Rect srcR = new Rect(0, 0, w, h);
                        RectF dstR = new RectF(0, 0, screenW, screenH);

                        Bitmap destBitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);
                        canvas.setBitmap(destBitmap);
                        canvas.drawBitmap(bitmap, srcR, dstR, null);
                        canvas.setBitmap(null);
                        manager.setBitmap(destBitmap);

                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new Throwable("bitmap is null"));
                    }
                } catch (Exception e) {
                    if (setWallPaperReceiver != null) {
                        context.unregisterReceiver(setWallPaperReceiver);
                    }
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        listener.onDataFailed(throwable.getMessage());
                    }

                    @Override
                    public void onNext(Object o) {
                        //应该在接受到的广播中, 再调用成功
                    }
                });
    }

    class SetWallPaperReceiver extends BroadcastReceiver {
        private onDataResponseListener mListener;
        private Context mContext;

        public SetWallPaperReceiver(Context context, onDataResponseListener listener) {
            mContext = context;
            mListener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            LogHelper.d("SetWallPaperReceiver", "onReceive action = " + intent.getAction());
            //设置壁纸成功
            App.sMainHanlder.post(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onDataSucess(null);
                    }
                }
            });
            mContext.unregisterReceiver(SetWallPaperReceiver.this);
        }
    }
}
