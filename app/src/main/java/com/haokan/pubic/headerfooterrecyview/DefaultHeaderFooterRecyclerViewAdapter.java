package com.haokan.pubic.headerfooterrecyview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.haokan.hklockscreen.R;


/**
 * 默认封装的HeaderFooterRecyclerViewAdapter，
 * 只有一个footer，并且footer有4个状态
 * footer带有数据加载状态
 */
public abstract class DefaultHeaderFooterRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends HeaderFooterRecyclerViewAdapter<VH> {
    /**
     * 0 没有footer<br/>
     * 1 加载更多<br/>
     * 2 加载中...<br/>
     * 3 没有更多数据
     */
    private int mFooterStatus = 0;

    /**
     * 隐藏footer
     */
    public final void hideFooter() {
        try {
            if (mFooterStatus != 0) {
                mFooterStatus = 0;
                notifyFooterItemRemoved(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * footer 加载更多状态
     */
    public final void setFooterLoadMore() {
        try {
            if (mFooterStatus == 0) {
                mFooterStatus = 1;
                notifyFooterItemInserted(0);
            } else {
                mFooterStatus = 1;
                notifyFooterItemChanged(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * footer loading 状态
     */
    public final void setFooterLoading() {
        try {
            if (mFooterStatus == 0) {
                mFooterStatus = 2;
                notifyFooterItemInserted(0);
            } else {
                mFooterStatus = 2;
                notifyFooterItemChanged(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * footer 没有更多了
     */
    public final void setFooterNoMore() {
        try {
            if (mFooterStatus == 0) {
                mFooterStatus = 3;
                notifyFooterItemInserted(0);
            } else {
                mFooterStatus = 3;
                notifyFooterItemChanged(0);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected int getFooterItemCount() {
        return mFooterStatus == 0 ? 0 : 1;
    }

    @Override
    protected int getFooterItemViewType(int position) {
        return mFooterStatus;
    }

    @Override
    protected VH onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
        View footerView = null;
        TextView tipView;
        try {
            switch (footerViewType) {
                case 1://加载更多
                    footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recy_footer_item_loadmore, parent, false);
                    break;
                case 2://加载中...
                    footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recy_footer_item_loading, parent, false);
                    break;
                case 3://没有更多数据了
                    footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recy_footer_item_nomore, parent, false);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createMyFooterViewHolder(footerView);
    }

    protected VH createMyFooterViewHolder(View footerView) {
        return null;
    }

    @Override
    protected void onBindFooterItemViewHolder(VH footerViewHolder, int position) {
    }
}