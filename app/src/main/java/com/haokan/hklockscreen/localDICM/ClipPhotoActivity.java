package com.haokan.hklockscreen.localDICM;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.yitu.R;
import com.haokan.yitu.activity.ActivityBase;
import com.haokan.yitu.util.FileUtil;

public class ClipPhotoActivity extends ActivityBase implements View.OnClickListener {
    private ProgressDialog mProgressDialog;
    private static int PHOTO_WIDTH = 400; // 截取的头像是400x400的
    private Handler mHandler = new Handler();
    private ClipZoomImageView mZiv;
    private ClipCoveredView mCcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipphoto_layout);

        mProgressDialog = ProgressDialog.show(this, null, "加载中...");
        assignViews();
    }

    private void assignViews() {
        TextView tv2 = (TextView) findViewById(R.id.tv_2);
        TextView tv3 = (TextView) findViewById(R.id.tv_desc);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);

        final String path = getIntent().getStringExtra(ClipPhotoManager.PICK_IMG_PATH);
        mCcv = (ClipCoveredView) findViewById(R.id.ccv_1);
        mZiv = (ClipZoomImageView) findViewById(R.id.ziv_1);
        mCcv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //zoomimageview 默认以屏幕作为初次填充的边缘框，其中有个剪裁头像的小些的方框
                //填充屏幕边缘时使用的type2，也就是长边充满屏幕的方式，此时有可能宅边缩放后会比
                //剪裁框还要窄，这是不行的，最小的填充后宽高也应该大于等于剪裁框，所以应该特殊处理。
                Rect clipRect = mCcv.getBoundRect();
                mZiv.setScaleType(ImageView.ScaleType.MATRIX);
                mZiv.setFirstFillRect(new RectF(clipRect));
                mZiv.setFirstFillRect(null);
                mZiv.setMaxMinScale(3.5f, .01f);

//                ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.FILE.wrap(path), mZiv, new ImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String imageUri, View view) {
//
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                        mProgressDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        mZiv.setImageBitmap(loadedImage);
//                        mProgressDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String imageUri, View view) {
//                        mProgressDialog.dismiss();
//                    }
//                });
                mCcv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_2://重新选择
                ClipPhotoManager.startPickImg(ClipPhotoActivity.this, ClipPhotoManager.REQUEST_SELECT_PICK);
                break;
            case R.id.tv_desc: //剪裁
                mProgressDialog = ProgressDialog.show(this, null, "剪裁中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String path = clipImage();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                Intent data = new Intent();
                                if (TextUtils.isEmpty(path)) {
                                    setResult(RESULT_CANCELED);
                                } else {
                                    data.putExtra(ClipPhotoManager.KEY_CLIP_PATH, path);
                                    setResult(RESULT_OK, data);
                                }
                                finish();
                            }
                        });
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == ClipPhotoManager.REQUEST_SELECT_PICK && data != null ) {
//            Log.d("wangzixu", "onActivityResult data = " + data.getData());
            String path = ClipPhotoManager.onPickImgResult(this, data);
            if (!TextUtils.isEmpty(path)) {
                mZiv.setImageBitmap(null);
//                ImageLoadingListener listener = null;
//                ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.FILE.wrap(path), mZiv, listener);
            }
        }
    }

    private String clipImage() {
        String path = null;
//        Matrix matrix = mZiv.getmMatrix();
//        float[] f = new float[9];
//        matrix.getValues(f);
//
//        RectF clipRect = mZiv.getMinLimitRect();
//        float scale = f[Matrix.MSCALE_X];
//        float transX = clipRect.left - f[Matrix.MTRANS_X];
//        float transY = clipRect.top - f[Matrix.MTRANS_Y];
//        float offsetX = Math.max(0, transX / scale);
//        float offsetY = Math.max(0, transY / scale);
//        float clipWidth = clipRect.width() / scale;
//        float clipHight = clipRect.height() / scale;
//        Bitmap destBitmap = getClipBitmap((int) offsetX, (int) offsetY, (int) clipWidth, (int) clipHight);
        Bitmap destBitmap = ClipPhotoUtil.getClipBitmap(mZiv, PHOTO_WIDTH, PHOTO_WIDTH);
        if (destBitmap != null) {
            path = FileUtil.saveAvatar(this, destBitmap, getImgFileName());
        }
        return path;
    }

    private String getImgFileName() {
        return "avatar_" + System.currentTimeMillis() + ".jpeg";
    }

//    private Bitmap getClipBitmap(int offsetX, int offsetY, int clipWidth, int clipHight) {
//        Bitmap source = mZiv.getOriginalBmp();
//        if (source == null) {
//            return null;
//        }
//
//        Bitmap destBitmap = null;
//        try {
////            if (clipWidth <= mScreenWidth && clipHight <= mScreenHeight) {
////                destBitmap = Bitmap.createBitmap(source, offsetX, offsetY, clipWidth, clipHight);
////            } else {
////            }
//            Canvas canvas = new Canvas();
//            Bitmap.Config newConfig = Bitmap.Config.ARGB_8888;
//            final Bitmap.Config config = source.getConfig();
//            if (config != null) {
//                switch (config) {
//                    case RGB_565:
//                        newConfig = Bitmap.Config.RGB_565;
//                        break;
//                    case ALPHA_8:
//                        newConfig = Bitmap.Config.ALPHA_8;
//                        break;
//                    // noinspection deprecation
//                    case ARGB_4444:
//                    case ARGB_8888:
//                    default:
//                        newConfig = Bitmap.Config.ARGB_8888;
//                        break;
//                }
//            }
//            destBitmap = Bitmap.createBitmap(PHOTO_WIDTH, PHOTO_WIDTH, newConfig);
//            Rect srcR = new Rect(offsetX, offsetY, offsetX + clipWidth, offsetY + clipHight);
//            RectF dstR = new RectF(0, 0, PHOTO_WIDTH, PHOTO_WIDTH);
//
//            destBitmap.setDensity(source.getDensity());
//            destBitmap.setHasAlpha(source.hasAlpha());
//            // destBitmap.setPremultiplied(source.isPremultiplied()); // api
//            // 19
//
//            canvas.setBitmap(destBitmap);
//            canvas.drawBitmap(source, srcR, dstR, null);
//            canvas.setBitmap(null);
//        } catch (OutOfMemoryError e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return destBitmap;
//    }
}
