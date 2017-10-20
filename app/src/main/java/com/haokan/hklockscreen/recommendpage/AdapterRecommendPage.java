package com.haokan.hklockscreen.recommendpage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haokan.hklockscreen.R;
import com.haokan.pubic.headerfooterrecyview.DefaultHeaderFooterRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2016/8/18.
 */
public class AdapterRecommendPage extends DefaultHeaderFooterRecyclerViewAdapter<AdapterRecommendPage.ViewHolder> {
    private ArrayList<BeanRecommendItem> mData = new ArrayList<>();
    private Context mContext;
    private CV_RecommendPage mRecommendPage;
//    private int mItemW;

    public AdapterRecommendPage(Context context, ArrayList<BeanRecommendItem> data, CV_RecommendPage recommendPage) {
        mContext = context;
        mData = data;
        mRecommendPage = recommendPage;
//        mItemW = context.getResources().getDisplayMetrics().widthPixels - DisplayUtil.dip2px(context,20);
    }

    //-------content begin---------------------
    @Override
    protected int getContentItemCount() {
        return mData.size();
    }

    @Override
    protected ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cv_recommendpage_item, parent, false);
        Item0ViewHolder holder = new Item0ViewHolder(view);
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
    class ViewHolder extends RecyclerView.ViewHolder {
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
        }

        @Override
        public void renderView(final int position) {
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
            mTvShareNum.setText(mBean.likeNum+"");

            String imgUrl = mBean.cover;
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
            mRecommendPage.startWebview(mBean.urlClick);
        }
    }
}
