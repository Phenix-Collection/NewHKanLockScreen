package com.haokan.hklockscreen.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiefeng on 2017/10/18.
 */

public class AdapterTimelinesImages extends RecyclerView.Adapter<AdapterTimelinesImages.MyRecycleViewHolder> {
    private LayoutInflater mInflater;
    private List<MainImageBean> mList = new ArrayList<>();
    private Context context;

    public AdapterTimelinesImages(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void addData(List<MainImageBean> model) {
        mList.addAll(model);
    }

    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.item_timeline_image, parent, false);
        //动态设置ImageView的宽高，根据自己每行item数量计算
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        d.getMetrics(dm);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, DisplayUtil.dip2px(context, 97));
        view.setLayoutParams(lp);

        MyRecycleViewHolder vh = new MyRecycleViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, int position) {
        holder.bindHolder(mList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class MyRecycleViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_image;

        public MyRecycleViewHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);

        }

        public void bindHolder(MainImageBean data) {

        }
    }

}