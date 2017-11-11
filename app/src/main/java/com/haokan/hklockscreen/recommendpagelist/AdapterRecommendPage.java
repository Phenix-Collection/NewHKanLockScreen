package com.haokan.hklockscreen.recommendpagelist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.haokanAd.ModelHaoKanAd;
import com.haokan.pubic.headerfooterrecyview.DefaultHeaderFooterRecyclerViewAdapter;
import com.haokan.pubic.util.DisplayUtil;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2016/8/18.
 */
public class AdapterRecommendPage extends DefaultHeaderFooterRecyclerViewAdapter<AdapterRecommendPage.ViewHolder> {
    private ArrayList<BeanRecommendItem> mData = new ArrayList<>();
    private Context mContext;
    private CV_RecommendPage mRecommendPage;
    private int mTopHide1, mTopHide2; //滑动到接近顶部时, view的图说要隐藏, 这俩值就是隐藏的范围

    public AdapterRecommendPage(Context context, ArrayList<BeanRecommendItem> data, CV_RecommendPage recommendPage) {
        mContext = context;
        mData = data;
        mRecommendPage = recommendPage;
        mTopHide1 = DisplayUtil.dip2px(context, 115);
        mTopHide2 = DisplayUtil.dip2px(context, 170);
    }

    //-------content begin---------------------
    @Override
    protected int getContentItemCount() {
        return mData.size();
    }

    @Override
    protected int getContentItemViewType(int position) {
        if (mData.get(position).mBeanAdRes != null) {
            return 1;
        }
        return 0;
    }

    @Override
    protected ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        ViewHolder holder;
        if (contentViewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.cv_recommendpage_item, parent, false);
            holder = new Item0ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.cv_recommendpage_item_ad, parent, false);
            holder = new Item1ViewHolder(view);
        }
        return holder;
    }

    @Override
    protected void onBindContentItemViewHolder(ViewHolder contentViewHolder, int position) {
        contentViewHolder.renderView(position);
    }
    //-------content end---------------------

    //-------header begin---------------------
    @Override
    protected int getHeaderItemCount() {
        return 0;
    }

    @Override
    protected ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
        return null;
    }

    @Override
    protected void onBindHeaderItemViewHolder(ViewHolder headerViewHolder, int position) {
        headerViewHolder.renderView(position);
    }

    //-------header end---------------------

    //-------footer begin---------------------
    @Override
    public ViewHolder createMyFooterViewHolder(View footerView) {
        return new ViewHolder(footerView);
    }
    //-------footer end---------------------

    //holder begin------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View bottomLayout;
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void renderView(int position) {
        }
    }

    class Item0ViewHolder extends ViewHolder implements View.OnClickListener {
        private BeanRecommendItem mBean;
        public ImageView mImageView;
        public TextView mTvTitle;
        public TextView mTvCollectNum;
        public TextView mTvShareNum;

        public Item0ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_image);
            mImageView.setOnClickListener(this);
//            mImageView.setBackgroundColor(CommonUtil.getRandomColor());

            mTvCollectNum = (TextView) itemView.findViewById(R.id.collect);
            mTvShareNum = (TextView) itemView.findViewById(R.id.share);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            bottomLayout = itemView.findViewById(R.id.bottomlayout);
        }

        @Override
        public void renderView(final int position) {
            if (!mHolders.contains(this)) {
                mHolders.add(this);
            }
//            long time = System.currentTimeMillis();
//            LogHelper.d("wangzixu", "adapterRecom onScroll renderView time = " + (System.currentTimeMillis() - time) + " , mHolders.size = " + mHolders.size());
            mBean = mData.get(position);
//            LogHelper.d("homepage", "pos = " + position + ", mBean.mHeadUrl = " + UrlsUtil.IMAGE_HOST + mBean.mHeadUrl
//                    + ", url = " + UrlsUtil.IMAGE_HOST + mBean.picUrl);

//            if (mBean.mItemH <= 0) {
//                if (mBean.picWidth == 0 || mBean.picHeight == 0) {
//                    mBean.mItemH = DisplayUtil.dip2px(mContext, 182);
//                } else {
//                    mBean.mItemH = (int) ((mItemW*1.0f/mBean.picWidth) * mBean.picHeight);
//                }
//            }
//            ViewGroup.LayoutParams params = mCardView.getLayoutParams();
//            if (params == null) {
//                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBean.mItemH);
//            }
//            params.height = mBean.mItemH;
//            mCardView.setLayoutParams(params);

            mTvTitle.setText(mBean.imgTitle);
            mTvCollectNum.setText(mBean.favNum+"");
            mTvShareNum.setText(mBean.shareNum+"");

            String imgUrl = mBean.cover;
            mImageView.setImageBitmap(null);
            Glide.with(mContext).load(imgUrl)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            LogHelper.d("wangzixu", "homepage renderView pos = " + position + " w,h = " + resource.getIntrinsicWidth() + ", " + resource.getIntrinsicHeight());
//                            return false;
//                        }
//                    })
                    .into(mImageView);
        }

        @Override
        public void onClick(View v) {
            mRecommendPage.startDetailPage(mBean);
        }
    }

    class Item1ViewHolder extends ViewHolder implements View.OnClickListener {
        private BeanRecommendItem mBean;
        ImageView imageView;
        TextView tvTitle;

        public Item1ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            bottomLayout = itemView.findViewById(R.id.bottomlayout);
        }

        @Override
        public void renderView(final int position) {
            if (!mHolders.contains(this)) {
                mHolders.add(this);
            }

            mBean = mData.get(position);
            imageView.setImageBitmap(null);
            tvTitle.setText("");
            if (mBean.mBeanAdRes != null) {
                Glide.with(mContext).load(mBean.mBeanAdRes.imgUrl).into(imageView);
                tvTitle.setText(mBean.mBeanAdRes.adTitle);
                imageView.setOnClickListener(this);
                //上报广告展示
                ModelHaoKanAd.adShowUpLoad(mBean.mBeanAdRes.showUpUrl);
            } else {
                imageView.setOnClickListener(null);
            }
        }

        @Override
        public void onClick(View v) {
            mRecommendPage.startDetailPage(mBean);
        }
    }

    //-----
    private ArrayList<ViewHolder> mHolders = new ArrayList<>();
    public void clearHolder() {
        mHolders.clear();
    }
    public void onScroll() {
//        LogHelper.d("wangzixu", "adapterRecom onScroll holdesize = " + mHolders.size());
        for (int i = 0; i < mHolders.size(); i++) {
            ViewHolder holder = mHolders.get(i);
            int bottom = holder.itemView.getBottom();
            if (bottom < mTopHide1) {
                holder.bottomLayout.setVisibility(View.GONE);
            } else if (bottom > mTopHide2) {
                if (holder.bottomLayout.getVisibility() != View.VISIBLE) {
                    holder.bottomLayout.setVisibility(View.VISIBLE);
                }
                holder.bottomLayout.setAlpha(1.0f);
            } else {
                if (holder.bottomLayout.getVisibility() != View.VISIBLE) {
                    holder.bottomLayout.setVisibility(View.VISIBLE);
                }
                float delta = bottom - mTopHide1;
                float f = delta/(mTopHide2-mTopHide1);
                holder.bottomLayout.setAlpha(f);
            }
        }
    }
    //-----
}
