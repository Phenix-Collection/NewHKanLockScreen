package com.haokan.hklockscreen.mycollection;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haokan.hklockscreen.R;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.headerfooterrecyview.DefaultHeaderFooterRecyclerViewAdapter;
import com.haokan.pubic.util.DisplayUtil;
import com.haokan.pubic.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maoyujiao on 2017/3/12.
 */
public class AdapterMyCollection extends DefaultHeaderFooterRecyclerViewAdapter<AdapterMyCollection.ViewHolder> {
    private ArrayList<MainImageBean> mData = new ArrayList<>();
    private Context mContext;
    private int mItemH;

    public AdapterMyCollection(Context context) {
        mContext = context;
        int sw = mContext.getResources().getDisplayMetrics().widthPixels;
        int iw = (sw - DisplayUtil.dip2px(mContext, 30f)) / 3;
        mItemH = (int) ((float) iw * 16 / 9);
    }

    public void addDataBeans(List<MainImageBean> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            int start = mData.size();
            int len = dataList.size();
            mData.addAll(dataList);
            notifyContentItemRangeInserted(start, len);
        }
    }

    public void addDataBeans(int index, List<MainImageBean> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            if (index < 0 || index > mData.size()) {
                index = mData.size();
            }
            mData.addAll(index, dataList);
            notifyDataSetChanged();
        }
    }

    public void addDataBeansAndClear(List<MainImageBean> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            mData.clear();
            mData.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    public List<MainImageBean> getDataBeans() {
        return mData;
    }

    //-------content begin---------------------
    @Override
    protected int getContentItemCount() {
        return mData.size();
    }

    @Override
    protected ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_mycollection_item, parent, false);
        Item0ViewHolder holder = new Item0ViewHolder(view);
        if (!mAllHolders.contains(holder)) {
            mAllHolders.add(holder);
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
    }

    //-------header end---------------------

    //holder begin------------------------------
    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void renderView(int position) {
        }
    }

    class Item0ViewHolder extends ViewHolder implements View.OnClickListener {
        public ImageView mImg;
        public ImageView mImgChoiceMark;
        public MainImageBean mImageBean;

        public Item0ViewHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.iv_image);
            mImgChoiceMark = (ImageView) itemView.findViewById(R.id.choice_mark);
            View view = itemView.findViewById(R.id.rl_content);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemH);
            }
            layoutParams.height = mItemH;
            view.setLayoutParams(layoutParams);

            mImg.setOnClickListener(this);
        }

        @Override
        public void renderView(int position) {
            LogHelper.d("collection", "renderView pos = " + position);
            mImageBean = mData.get(position);
            String url = mImageBean.imgSmallUrl;

            Glide.with(mContext).load(url).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(mImg);

            if (mEditMode) {
                mImgChoiceMark.setVisibility(View.VISIBLE);
                mImgChoiceMark.setSelected(mSelecteds.contains(mImageBean));
            } else {
                mImgChoiceMark.setVisibility(View.GONE);
                mImgChoiceMark.setSelected(false);
            }
        }

        @Override
        public void onClick(View v) {
            if (!mEditMode) { //正常态，跳转到大图页
                if (mContext instanceof ActivityBase) {
                    ActivityBase activity = (ActivityBase) mContext;

                }
            } else { //编辑态，选中框选中
                if (mSelecteds.contains(mImageBean)) {
                    mSelecteds.remove(mImageBean);
                    mImgChoiceMark.setSelected(false);
                } else {
                    mSelecteds.add(mImageBean);
                    mImgChoiceMark.setSelected(true);
                }
                if (mOnSelectCountChangeListener != null) {
                    mOnSelectCountChangeListener.onSelectCountChange(mSelecteds.size());
                }
            }
        }
    }

    //holder end------------------------------

    //***********关于编辑的逻辑 begin***************
    private List<Item0ViewHolder> mAllHolders = new ArrayList<>();
    private List<MainImageBean> mSelecteds = new ArrayList<>();
    private boolean mEditMode;

    public void setState(boolean editMode) {
        if (mEditMode && !editMode) {//退出编辑态
            for (int i = 0; i < mAllHolders.size(); i++) {
                mAllHolders.get(i).mImgChoiceMark.setVisibility(View.GONE);
                mAllHolders.get(i).mImgChoiceMark.setSelected(false);
                mSelecteds.clear();
                if (mOnSelectCountChangeListener != null) {
                    mOnSelectCountChangeListener.onSelectCountChange(0);
                }
            }
        } else if (!mEditMode && editMode) { //进入编辑态
            for (int i = 0; i < mAllHolders.size(); i++) {
                mAllHolders.get(i).mImgChoiceMark.setVisibility(View.VISIBLE);
            }
        }
        mEditMode = editMode;
    }

    public boolean getState() {
        return mEditMode;
    }

    public List<MainImageBean> getSelectedItems() {
        return mSelecteds;
    }

    public void clearData() {
        mData.clear();
        if (mEditMode) {
            mSelecteds.clear();
            if (mOnSelectCountChangeListener != null) {
                mOnSelectCountChangeListener.onSelectCountChange(mSelecteds.size());
            }
        }
        notifyDataSetChanged();
    }

    public void delItems(List<MainImageBean> list) {
        if (list != null && list.size() > 0) {
            mData.removeAll(list);
            if (mEditMode) {
                mSelecteds.removeAll(list);
                if (mOnSelectCountChangeListener != null) {
                    mOnSelectCountChangeListener.onSelectCountChange(mSelecteds.size());
                }
            }
            LogHelper.d("mycollection", "deleteAllSelectedItems delItems");
            notifyDataSetChanged();
        }
    }

    private onSelectCountChangeListener mOnSelectCountChangeListener;

    public void setOnSelectCountChangeListener(onSelectCountChangeListener onSelectCountChangeListener) {
        mOnSelectCountChangeListener = onSelectCountChangeListener;
    }

    public interface onSelectCountChangeListener {
        void onSelectCountChange(int currentCount);
    }

    public void onDestory() {
        mAllHolders.clear();
        mSelecteds.clear();
    }
    //***********关于编辑的逻辑 end***************

    public int getSpanSize(int position) {
        int cc = getContentItemCount();
        if (position < cc) {
            return 1;
        } else {
            return 3;
        }
    }
}
