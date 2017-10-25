package com.haokan.pubic.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.haokan.pubic.App;
import java.io.InputStream;
import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/3/31.
 */
public class AssetsImageLoader {
    public interface onAssetImageLoaderListener{
        void onSuccess(Bitmap bitmap);
        void onFailed(Exception e);
    }

    public static void loadAssetsImage(final Context context, final String url, final onAssetImageLoaderListener loaderListener){
        if (loaderListener == null) {
            return;
        }

        final Scheduler.Worker worker = Schedulers.io().createWorker();
        worker.schedule(new Action0() {
            @Override
            public void call() {
                try {
                    InputStream open = context.getAssets().open(url);
                    final Bitmap bitmap = BitmapFactory.decodeStream(open);

                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            loaderListener.onSuccess(bitmap);
                        }
                    });
                } catch (Exception e) {
                    loaderListener.onFailed(e);
                }
              worker.unsubscribe();
            }
        });
    }

    public static Bitmap loadAssetsImageBitmap(final Context context, final String url){
        try{
            InputStream open = context.getAssets().open(url);
            Bitmap bitmap = BitmapFactory.decodeStream(open);
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
