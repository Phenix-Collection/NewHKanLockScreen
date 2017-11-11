package com.haokan.pubic.clipimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreen.ModelLockScreen;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.DisplayUtil;
import com.haokan.pubic.util.FileUtil;
import com.haokan.pubic.util.ToastManager;

import java.io.File;


public class ActivityClipImage extends ActivityBase implements View.OnClickListener {
    private ClipZoomImageView mClipImageSrcImage;
    private TextView mTvCancel;
    private TextView mTvConfirm;
    private ClipCoverView_LockScreen mClipImageCoverView;
    public static final String KEY_INTENT_CLIPIMG_SRC_PATH = "img_path";
    public static final String KEY_INTENT_CLIPIMG_DOWN_PATH = "down_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipimage);
        initView();
    }

    private void initView() {
        View loadingLayout = this.findViewById(R.id.layout_loading);
        setPromptLayout(loadingLayout, null, null, null);

        mClipImageSrcImage = (ClipZoomImageView) findViewById(R.id.clipimg_src_image);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
        mClipImageCoverView = (ClipCoverView_LockScreen) findViewById(R.id.clipimg_conver_view);

        mTvCancel.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);

        final String path = getIntent().getStringExtra(KEY_INTENT_CLIPIMG_SRC_PATH);
        if (TextUtils.isEmpty(path)) {
            ToastManager.showShort(this, "图片路径为空");
            return;
        }
        Log.d("wangzixu", "initView path = " + path);

        showLoadingLayout();
        mClipImageCoverView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect clipRect = mClipImageCoverView.getClipRect();
                mClipImageSrcImage.setScaleType(ImageView.ScaleType.MATRIX);
                mClipImageSrcImage.setClipRect(new RectF(clipRect));
                Point point = DisplayUtil.getRealScreenPoint(ActivityClipImage.this);
                LogHelper.d("wangzixu", "clipimg point = " + point.x + ", " + point.y);
                Glide.with(ActivityClipImage.this).load(path).asBitmap().dontAnimate().fitCenter().listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        e.printStackTrace();
                        App.sMainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                dismissAllPromptLayout();
                            }
                        });
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                }).into(new SimpleTarget<Bitmap>(point.x, point.y) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        LogHelper.d("wangzixu", "clipimg resource = " + resource.getWidth() + ", " + resource.getHeight());
                        mClipImageSrcImage.setImageBitmap(resource);
                        App.sMainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                dismissAllPromptLayout();
                            }
                        });
                    }
                });

                mClipImageCoverView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel: // 取消
                setResult(RESULT_CANCELED);
                onBackPressed();
                break;
            case R.id.tv_confirm: //剪裁
                showLoadingDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String path = clipImage(mClipImageSrcImage);
                        App.sMainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadingDialog();

                                if (TextUtils.isEmpty(path)) {
                                    setResult(RESULT_CANCELED);
                                } else {
                                    Intent data = new Intent();
                                    data.putExtra(KEY_INTENT_CLIPIMG_DOWN_PATH, path);
                                    setResult(RESULT_OK, data);
                                }
                                onBackPressed();
                            }
                        });
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    private String clipImage(ClipZoomImageView zoomImageView) {
        String path = null;
        if (zoomImageView == null || zoomImageView.getOriginalBmp() == null) {
            return null;
        }

        Bitmap source = zoomImageView.getOriginalBmp();

        Matrix matrix = zoomImageView.getMatrix();
        float[] f = new float[9];
        matrix.getValues(f);

        RectF clipRect = zoomImageView.getClipRect();
        float scale = f[Matrix.MSCALE_X];
        float transX = clipRect.left - f[Matrix.MTRANS_X];
        float transY = clipRect.top - f[Matrix.MTRANS_Y];

        float offsetX = Math.max(0, transX / scale);
        float offsetY = Math.max(0, transY / scale);
        float clipWidth = clipRect.width() / scale;
        float clipHight = clipRect.height() / scale;
        Log.d("wangzixu", "clipImage x, y = " + offsetX + ", " + offsetY);

        Bitmap destBitmap = null;
        try {
            Point point = DisplayUtil.getRealScreenPoint(this);

            Canvas canvas = new Canvas();
            destBitmap = Bitmap.createBitmap(point.x, point.y, Bitmap.Config.ARGB_8888);

            Rect srcR = new Rect((int)offsetX, (int)offsetY, (int)(offsetX + clipWidth), (int)(offsetY + clipHight));
            RectF dstR = new RectF(0, 0, point.x, point.y);

            destBitmap.setDensity(source.getDensity());
            destBitmap.setHasAlpha(source.hasAlpha());
            // destBitmap.setPremultiplied(source.isPremultiplied()); // api

            canvas.setBitmap(destBitmap);
            canvas.drawBitmap(source, srcR, dstR, null);
            canvas.setBitmap(null);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (destBitmap != null) {
            File dir = ModelLockScreen.getLocalImageDir(this);
            File file = new File(dir, "img_" + System.currentTimeMillis() + ".jpg");
            FileUtil.saveBitmapToFile(this, destBitmap, file, false);
            path = file.getAbsolutePath();
        }
        return path;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivityAnim();
    }
}
