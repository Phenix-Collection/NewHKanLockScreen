package com.haokan.hklockscreen.main;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.util.DisplayUtil;
import com.haokan.pubic.util.ToastManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiefeng on 2017/10/18.
 */

public class AdapterTimelines extends RecyclerView.Adapter<AdapterTimelines.MyRecycleViewHolder> {
    private LayoutInflater mInflater;
    private List<BeanTimelines> mList = new ArrayList<>();
    private Context context;

    public AdapterTimelines(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void addData(List<BeanTimelines> model) {
        mList.addAll(model);
    }

    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyRecycleViewHolder vh = null;
        switch (viewType) {
            case BeanTimelines.TYPE_TITLE:
                vh = new MyRecycleViewHolderOne(mInflater.inflate(R.layout.item_timeline1, parent, false));
                break;
            case BeanTimelines.TYPE_ITEM:
                vh = new MyRecycleViewHolderTwo(mInflater.inflate(R.layout.item_timeline2, parent, false));
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, int position) {
        holder.bindHolder(mList.get(position));
    }

    @Override
    public int getItemViewType(int position) {

        return mList.get(position).type;

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public abstract class MyRecycleViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public MyRecycleViewHolder(View itemView) {
            super(itemView);

        }

        public abstract void bindHolder(BeanTimelines data);
    }

    public class MyRecycleViewHolderOne extends MyRecycleViewHolder {

        public MyRecycleViewHolderOne(View itemView) {
            super(itemView);

        }

        @Override
        public void bindHolder(BeanTimelines data) {
        }
    }

    public class MyRecycleViewHolderTwo extends MyRecycleViewHolder {
        private View relativeLayout3;
        private RecyclerView rv_images;
        private AdapterTimelinesImages myAdapter;

        public MyRecycleViewHolderTwo(View itemView) {
            super(itemView);
            relativeLayout3 = itemView.findViewById(R.id.relativeLayout3);
            rv_images = (RecyclerView) itemView.findViewById(R.id.rv_images);
            rv_images.setLayoutManager(new GridLayoutManager(context, 5));
            rv_images.addItemDecoration(new RecyclerView.ItemDecoration() {
                int mSpace = DisplayUtil.dip2px(context, 10);

                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
//                    outRect.left = mSpace;
//                    outRect.right = mSpace;
                    outRect.bottom = mSpace;
//                    if (parent.getChildAdapterPosition(view) == 0) {
//                        outRect.top = mSpace;
//                    }
                }
            });
            myAdapter = new AdapterTimelinesImages(context);
            rv_images.setAdapter(myAdapter);
        }

        int maxShowCount = 5;
        @Override
        public void bindHolder(final BeanTimelines data) {
            List<MainImageBean> list = data.list.subList(0, maxShowCount);
            myAdapter.addData(list);

            if (data.list != null && data.list.size() > maxShowCount) {
                relativeLayout3.setVisibility(View.VISIBLE);
                relativeLayout3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<MainImageBean> list = data.list.subList(maxShowCount, data.list.size());
                        myAdapter.addData(list);
                        myAdapter.notifyDataSetChanged();
                        relativeLayout3.setVisibility(View.GONE);
                    }
                });
            } else {
                relativeLayout3.setVisibility(View.GONE);
            }
        }
    }

}