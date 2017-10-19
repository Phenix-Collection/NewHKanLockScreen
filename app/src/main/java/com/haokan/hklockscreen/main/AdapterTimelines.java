package com.haokan.hklockscreen.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haokan.hklockscreen.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiefeng on 2017/10/18.
 */

public class AdapterTimelines extends RecyclerView.Adapter<AdapterTimelines.MyRecycleViewHolder> {
    private LayoutInflater mInflater;
    private List<ModelTimelines> mList = new ArrayList<>();

    public AdapterTimelines(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<ModelTimelines> model) {
        mList.addAll(model);
    }

    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyRecycleViewHolder vh = null;
        switch (viewType) {
            case ModelTimelines.TYPE_TITLE:
                vh = new MyRecycleViewHolderOne(mInflater.inflate(R.layout.item_timeline1, parent, false));
                break;
            case ModelTimelines.TYPE_ITEM:
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

        public abstract void bindHolder(ModelTimelines data);
    }

    public class MyRecycleViewHolderOne extends MyRecycleViewHolder {

        public MyRecycleViewHolderOne(View itemView) {
            super(itemView);

        }

        @Override
        public void bindHolder(ModelTimelines data) {
        }
    }

    public class MyRecycleViewHolderTwo extends MyRecycleViewHolder {

        public MyRecycleViewHolderTwo(View itemView) {
            super(itemView);

        }

        @Override
        public void bindHolder(ModelTimelines data) {
        }
    }

}