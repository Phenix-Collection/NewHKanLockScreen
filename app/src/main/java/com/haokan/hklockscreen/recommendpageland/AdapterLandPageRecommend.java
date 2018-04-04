package com.haokan.hklockscreen.recommendpageland;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
public class AdapterLandPageRecommend extends DefaultHeaderFooterRecyclerViewAdapter<AdapterLandPageRecommend.ViewHolder> {
    private ArrayList<BeanRecommendLandPage> mData = new ArrayList<>();
    private ActivityRecommendLandPage mContext;
    private int mItemW;

    public AdapterLandPageRecommend(ActivityRecommendLandPage context, ArrayList<BeanRecommendLandPage> data) {
        mContext = context;
        mData = data;
        mItemW = context.getResources().getDisplayMetrics().widthPixels - DisplayUtil.dip2px(context,38);
    }

    //-------content begin---------------------
    @Override
    protected int getContentItemCount() {
        return mData.size();
    }

    @Override
    protected int getContentItemViewType(int position) {
        return mData.get(position).myType;
    }

    /**
     *自己规定的type, 因为这个详情页有很多类型, 需要区分开
     * 0, 头部
     * 1, 分享按钮
     * 2, 广告
     * 3, 评论
     */
    @Override
    protected ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        ViewHolder holder = null;
        if (contentViewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.activity_recommendpageland_item0, parent, false);
            holder = new Item0ViewHolder(view);
        } else if (contentViewType == 1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.activity_recommendpageland_item1, parent, false);
            holder = new Item1ViewHolder(view);
        } else if (contentViewType == 2) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.activity_recommendpageland_item2, parent, false);
            holder = new Item2ViewHolder(view);
        } else if (contentViewType == 3) {

        } else if (contentViewType == 4) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.activity_recommendpageland_item4, parent, false);
            holder = new Item4ViewHolder(view);
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
        if (mData == null || mData.size() == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    protected ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_recommendpageland_itemheader, parent, false);
        ViewHolder holder = new ItemHeaderViewHolder(view);
        return holder;
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
        private BeanRecommendLandPage mBean;
        ImageView imageView;
        TextView tvTitle;
        TextView tvDesc;
        int mPos;

        public Item0ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            itemView.findViewById(R.id.cardview).setOnClickListener(this);
        }

        @Override
        public void renderView(final int position) {
            mPos = position;
            mBean = mData.get(position);
            int picH;
            if (mBean.myItemH <= 0) {
                if (mBean.w == 0 || mBean.h == 0) {
                    picH = 600;
                } else {
                    picH = (int) ((mItemW*1.0f/mBean.w) * mBean.h);
                    mBean.myItemH = picH;
                }
            } else {
                picH = mBean.myItemH;
            }
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, picH);
            }
            params.height = picH;
            imageView.setLayoutParams(params);

            tvTitle.setText(mBean.imgTitle);
            tvDesc.setText(mBean.imgContent);
            Glide.with(mContext).load(mBean.imgUrl).into(imageView);
        }

        @Override
        public void onClick(View v) {
            mContext.startDetailPage(mData, mPos);
        }
    }

    public class ItemHeaderViewHolder extends ViewHolder{
        private BeanRecommendLandPage mBean;
        public TextView mTvTitle;
        public ItemHeaderViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mContext.setHeaderItem(this);
//            mContext.setTitleBottomH(bottom-top);
//            itemView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                @Override
//                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                }
//            });
        }

        @Override
        public void renderView(final int position) {
            mBean = mData.get(position);
            mTvTitle.setText(mBean.imgTitle);
        }
    }

    public class Item1ViewHolder extends ViewHolder implements View.OnClickListener {
        private BeanRecommendLandPage mBean;
        public Item1ViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.iv_image).setOnClickListener(this);
        }

        @Override
        public void renderView(final int position) {
            mBean = mData.get(position);
        }

        @Override
        public void onClick(View v) {
            mContext.showShareLayout();
        }
    }

    public class Item2ViewHolder extends ViewHolder implements View.OnClickListener {
        private BeanRecommendLandPage mBean;
        private ImageView mImageView;
        public Item2ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void renderView(final int position) {
            mBean = mData.get(position);
            if (mBean.mBeanAdRes == null) {
                return;
            }
            Glide.with(mContext).load(mBean.mBeanAdRes.imgUrl).into(mImageView);
            //上报广告展示
            ModelHaoKanAd.onAdShow(mBean.mBeanAdRes.onShowUrls);
        }

        @Override
        public void onClick(View v) {
            mContext.startAdDetailPage(mBean);
        }
    }

    public class Item4ViewHolder extends ViewHolder implements View.OnClickListener {
        private BeanRecommendLandPage mBean;
        private ImageView mImageView;
        private TextView mTitle;
        private TextView mDesc;
        public Item4ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_image);
            mTitle = itemView.findViewById(R.id.tv_title);
            mDesc = itemView.findViewById(R.id.tv_desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void renderView(final int position) {
            mBean = mData.get(position);
            if (mBean.mBeanAdRes == null) {
                return;
            }
            Glide.with(mContext).load(mBean.mBeanAdRes.imgUrl).into(mImageView);
            //上报广告展示
//            ModelHaoKanAd.onAdShow(mBean.mBeanAdRes.showUpUrl);
            if (TextUtils.isEmpty(mBean.mBeanAdRes.adTitle)) {
                mTitle.setVisibility(View.GONE);
            } else {
                mTitle.setVisibility(View.VISIBLE);
                mTitle.setText(mBean.mBeanAdRes.adTitle);
            }

            if (TextUtils.isEmpty(mBean.mBeanAdRes.adDesc)) {
                mDesc.setVisibility(View.GONE);
            } else {
                mDesc.setVisibility(View.VISIBLE);
                mDesc.setText(mBean.mBeanAdRes.adDesc);
            }
        }

        @Override
        public void onClick(View v) {
            mContext.startAdDetailPage(mBean);
        }
    }
}
