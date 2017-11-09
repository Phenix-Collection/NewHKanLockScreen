package com.haokan.hklockscreen.recommendpagedetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.haokan.hklockscreen.R;
import com.haokan.pubic.App;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.detailpage.Adapter_DetailPage_Base;
import com.haokan.pubic.util.BlurUtil;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class Adapter_DetailPage_Recommend extends Adapter_DetailPage_Base {
    public Adapter_DetailPage_Recommend(Context context, ArrayList<MainImageBean> data, View.OnClickListener onClickListener, View.OnLongClickListener longClickListener) {
        super(context, data, onClickListener, longClickListener);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final MainImageBean imageBean = mData.get(position);
        View view = View.inflate(mContext, R.layout.cv_detailpage_recommend_item, null);
        final RecommendPageDetailViewHolder holder = new RecommendPageDetailViewHolder(view);
        holder.position = position;
        view.setTag(holder);
        mHolders.add(holder);

        container.addView(holder.itemView);
        loadBigBitmap(imageBean, holder);
        return holder.itemView;
    }

    public void loadBigBitmap(MainImageBean imageBean, final RecommendPageDetailViewHolder holder) {
        final String imgUrl = imageBean.imgBigUrl;
        Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    FutureTarget<Bitmap> target = Glide.with(mContext).load(imgUrl).asBitmap() .dontAnimate().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    Bitmap bitmap = target.get();

                    if (bitmap != null) {
                        Bitmap blurBitmap = BlurUtil.blurBitmap2(bitmap, 4, 4);
                        final BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(), blurBitmap);
                        drawable.setColorFilter(0xFF999999, PorterDuff.Mode.MULTIPLY);
                        App.sMainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                if (holder.position != -1) {
                                    holder.ivBgImageView.setImageDrawable(drawable);
                                }
                            }
                        });
                    }

                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        holder.errorView.setVisibility(View.VISIBLE);
                        holder.loadingView.setVisibility(View.GONE);
                        holder.image.setVisibility(View.GONE);
                        holder.mBitmap = null;
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        if (holder.position != -1) {
                            holder.errorView.setVisibility(View.GONE);
                            holder.loadingView.setVisibility(View.GONE);
                            holder.image.setVisibility(View.VISIBLE);
                            holder.mBitmap = bitmap;
                            holder.image.setImageBitmap(bitmap);
                        }
                    }
                });
    }

    public void loadBlurBitmap(MainImageBean imageBean, final RecommendPageDetailViewHolder holder) {
        final String imgUrl = imageBean.imgSmallUrl;
        Observable.create(new Observable.OnSubscribe<BitmapDrawable>() {
            @Override
            public void call(Subscriber<? super BitmapDrawable> subscriber) {
                try {
                    FutureTarget<Bitmap> target = Glide.with(mContext).load(imgUrl).asBitmap() .dontAnimate().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    Bitmap bitmap = target.get();

                    if (bitmap != null) {
                        Bitmap blurBitmap = BlurUtil.blurBitmap2(bitmap, 2, 4);
                        BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(), blurBitmap);
                        drawable.setColorFilter(0xFF999999, PorterDuff.Mode.MULTIPLY);
                        subscriber.onNext(drawable);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new Throwable("null"));
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BitmapDrawable>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BitmapDrawable drawable) {
                        if (holder.position != -1) {
                            holder.ivBgImageView.setImageDrawable(drawable);
                        }
                    }
                });
    }

    public class RecommendPageDetailViewHolder extends ViewHolder {
        public ImageView ivBgImageView;
        public RecommendPageDetailViewHolder(View view) {
            super(view);
            ivBgImageView = (ImageView) itemView.findViewById(R.id.bgview);
        }
    }
}
