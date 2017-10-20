package com.haokan.hklockscreen.lockscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.haokan.hklockscreen.R;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.detailpage.Adapter_DetailPage_Base;
import com.haokan.pubic.util.AssetsImageLoader;
import com.haokan.pubic.util.LogHelper;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/10/17.
 */
public class Adapter_DetailPage_LockScreen extends Adapter_DetailPage_Base {
    private boolean mCanUnLock = true;

    public Adapter_DetailPage_LockScreen(Context context, ArrayList<MainImageBean> data, View.OnClickListener onClickListener, View.OnLongClickListener longClickListener) {
        super(context, data, onClickListener, longClickListener);
    }

    @Override
    public int getCount() {
        return 30 * super.getCount();
    }

    public void setCanUnLock(boolean canUnLock) {
        mCanUnLock = canUnLock;
        for (int i = 0; i < mHolders.size(); i++) {
            ViewHolder holder = mHolders.get(i);
            CV_UnLockImageView unLockImageView = (CV_UnLockImageView) holder.image;
            unLockImageView.setCanUnLock(canUnLock);
        }
    }

    public CV_UnLockImageView getCurrentImageView(int position) {
        ViewHolder holder = null;
        for (int i = 0; i < mHolders.size(); i++) {
            ViewHolder temp = mHolders.get(i);
            if (temp.position == position) {
                holder = temp;
                break;
            }
        }
        if (holder != null) {
            return (CV_UnLockImageView) holder.image;
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position = position%mData.size();
        final MainImageBean imageBean = mData.get(position);

        View view = View.inflate(mContext, R.layout.cv_detailpage_lockscreen_item, null);
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

        String url = imageBean.imgBigUrl;

        if (imageBean.myType == 2) { // assset中的图片
            AssetsImageLoader.loadAssetsImage(mContext, url, new AssetsImageLoader.onAssetImageLoaderListener() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    if (holder.position != -1) {
                        holder.errorView.setVisibility(View.GONE);
                        holder.loadingView.setVisibility(View.GONE);
                        holder.image.setVisibility(View.VISIBLE);
                        holder.mBitmap = bitmap;
                        holder.image.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    LogHelper.d("wangzixu", "lockadapter instantiateItem AssetsImageLoader  获取不到");
                    e.printStackTrace();
                    holder.errorView.setVisibility(View.VISIBLE);
                    holder.loadingView.setVisibility(View.GONE);
                    holder.image.setVisibility(View.GONE);
                    holder.mBitmap = null;
                }
            });
        } else {
            Glide.with(mContext).load(url).asBitmap().dontAnimate().listener(new RequestListener<String, Bitmap>() {
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
                    if (imageBean.myType == 0) { //需要存储到本地的图片

                    }
                    return false;
                }
            }).into(holder.image);
        }

        return holder.itemView;
    }
}
