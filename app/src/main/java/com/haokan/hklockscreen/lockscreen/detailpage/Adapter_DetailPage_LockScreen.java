package com.haokan.hklockscreen.lockscreen.detailpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.haokan.hklockscreen.R;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.detailpage.Adapter_DetailPageView_Base;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/10/17.
 */
public class Adapter_DetailPage_LockScreen extends Adapter_DetailPageView_Base {
    public Adapter_DetailPage_LockScreen(Context context, ArrayList<MainImageBean> data, View.OnClickListener onClickListener, View.OnLongClickListener longClickListener) {
        super(context, data, onClickListener, longClickListener);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final MainImageBean imageBean = mData.get(position);

        View view = View.inflate(mContext, R.layout.cv_detailpage_base_item, null);
        final ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        mHolders.add(holder);

        holder.loadingView.setVisibility(View.VISIBLE);
        holder.position = position;
        holder.imgState = 0;

        container.addView(holder.itemView);

        String url = imageBean.imgBigUrl;
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
                return false;
            }
        }).into(holder.image);
        return holder.itemView;
    }
}
