package com.haokan.hklockscreen.lockscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.haokan.hklockscreen.R;
import com.haokan.pubic.bean.BigImageBean;
import com.haokan.pubic.detailpage.Adapter_DetailPage_Base;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/10/17.
 */
public class Adapter_DetailPage_LockScreen extends Adapter_DetailPage_Base {
    private boolean mCanUnLock = true;

    public Adapter_DetailPage_LockScreen(Context context, ArrayList<BigImageBean> data, View.OnClickListener onClickListener, View.OnLongClickListener longClickListener) {
        super(context, data, onClickListener, longClickListener);
    }

    public void setCanUnLock(boolean canUnLock) {
        mCanUnLock = canUnLock;
        for (int i = 0; i < mHolders.size(); i++) {
            ViewHolder holder = mHolders.get(i);
            CV_UnLockImageView unLockImageView = (CV_UnLockImageView) holder.image;
            unLockImageView.setCanUnLock(canUnLock);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final BigImageBean imageBean = mData.get(position);

        View view;
        if (imageBean.mBeanAdRes != null) { //是广告
            view = View.inflate(mContext, R.layout.cv_detailpage_lockscreen_item_ad, null);
        } else {
            view = View.inflate(mContext, R.layout.cv_detailpage_lockscreen_item, null);
        }

        final ViewHolder holder = new ViewHolder(view);
        CV_UnLockImageView unLockImageView = (CV_UnLockImageView) holder.image;
        unLockImageView.setCanUnLock(mCanUnLock);
        unLockImageView.setOnLongClickListener(null);//暂时不响应长按, 所以设置为null
        if (mOnLongClickListener != null && mOnLongClickListener instanceof CV_UnLockImageView.onUnLockListener) {
            unLockImageView.setOnUnLockListener((CV_UnLockImageView.onUnLockListener) mOnLongClickListener);
        }
        view.setTag(holder);
        mHolders.add(holder);

        holder.loadingView.setVisibility(View.VISIBLE);
        holder.position = position;
        holder.imgState = 0;

        container.addView(holder.itemView);

        String url;
        if (imageBean.mBeanAdRes != null) { //是广告
            url = imageBean.mBeanAdRes.imgUrl;
        } else {
            url = imageBean.imgBigUrl;
        }

        BitmapRequestBuilder<String, Bitmap> builder = Glide.with(mContext).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate();
        builder.listener(new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                holder.errorView.setVisibility(View.VISIBLE);
                holder.loadingView.setVisibility(View.GONE);
                holder.image.setVisibility(View.GONE);
                holder.mBitmap = null;
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                holder.errorView.setVisibility(View.GONE);
                holder.loadingView.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
                holder.mBitmap = resource;
                return false;
            }
        }).into(holder.image);
        return holder.itemView;

//        if (imageBean.mBeanAdRes != null) { //是广告
//            LogHelper.d("wangzixu", "HaokanADManager  detailpage AD position = " + position);
//            View view = View.inflate(mContext, R.layout.cv_detailpage_lockscreen_item_ad, null);
//            final ImageView image = (ImageView) view.findViewById(R.id.iv_image);
//            final View errorView = view.findViewById(R.id.layout_fail);
//            final View loadingView = view.findViewById(R.id.layout_loading);
//            loadingView.setVisibility(View.VISIBLE);
//            Glide.with(mContext).load(imageBean.mBeanAdRes.imgUrl).asBitmap().dontAnimate().listener(new RequestListener<String, Bitmap>() {
//                @Override
//                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
//                    errorView.setVisibility(View.VISIBLE);
//                    loadingView.setVisibility(View.GONE);
//                    image.setVisibility(View.GONE);
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                    errorView.setVisibility(View.GONE);
//                    loadingView.setVisibility(View.GONE);
//                    image.setVisibility(View.VISIBLE);
//                    return false;
//                }
//            }).into(image);
//
//            container.addView(view);
//            return view;
//        } else {
//
//        }
    }
}
