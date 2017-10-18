package com.haokan.pubic.detailpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.haokan.hklockscreen.R;
import com.haokan.pubic.bean.MainImageBean;

import java.util.ArrayList;

public class Adapter_DetailPage_Base extends PagerAdapter implements View.OnClickListener {
    protected final Context mContext; //用activity，利用glide的生命周期控制系统
    protected ArrayList<MainImageBean> mData;
    protected ArrayList<ViewHolder> mHolders = new ArrayList<>();
    protected View.OnClickListener mOnClickListener;
    protected View.OnLongClickListener mOnLongClickListener;

    public Adapter_DetailPage_Base(Context context, ArrayList<MainImageBean> data
            , View.OnClickListener onClickListener, View.OnLongClickListener longClickListener) {
        mContext = context;
        mData = data;
        mOnClickListener = onClickListener;
        mOnLongClickListener = longClickListener;
    }

    public ArrayList<MainImageBean> getData() {
        return mData;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Object tag = ((View) object).getTag();
        if (tag instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) tag;
            mHolders.remove(holder);
            holder.mBitmap = null;
            holder.position = -1;
        }
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

    public Bitmap getCurrentBitmap(int position) {
        ViewHolder holder = null;
        for (int i = 0; i < mHolders.size(); i++) {
            ViewHolder temp = mHolders.get(i);
            if (temp.position == position) {
                holder = temp;
                break;
            }
        }
        if (holder != null) {
            return holder.mBitmap;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
    }

    public class ViewHolder {
        public View itemView;
        public ImageView image;
        public View errorView;
        public View loadingView;
        public Bitmap mBitmap;
        public int position = -1; //当前的图片加载的是第几个图，如果是-1，代表已经销毁
        /**
         * 加载图片的状态，0代表加载失败或者还没开始加载，什么图都没有的状态，1代表加载小图成功，2代表加载大图成功
         */
        public int imgState = 0;

        public ViewHolder(View view) {
            itemView = view;
            image = (ImageView) itemView.findViewById(R.id.iv_image);
            errorView = itemView.findViewById(R.id.layout_fail);
            loadingView = itemView.findViewById(R.id.layout_loading);
            image.setOnClickListener(mOnClickListener);
            image.setOnLongClickListener(mOnLongClickListener);
        }
    }
}
