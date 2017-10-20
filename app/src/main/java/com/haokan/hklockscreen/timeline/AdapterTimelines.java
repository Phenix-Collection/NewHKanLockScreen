package com.haokan.hklockscreen.timeline;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                vh = new MyRecycleViewHolderTitle(mInflater.inflate(R.layout.item_timeline1, parent, false));
                break;
            case BeanTimelines.TYPE_ITEM:
                vh = new MyRecycleViewHolderImages(mInflater.inflate(R.layout.item_timeline2, parent, false));
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

    public class MyRecycleViewHolderTitle extends MyRecycleViewHolder {

        public MyRecycleViewHolderTitle(View itemView) {
            super(itemView);

        }

        @Override
        public void bindHolder(BeanTimelines data) {
        }
    }

    public class MyRecycleViewHolderImages extends MyRecycleViewHolder {
        private View relativeLayout3;
        private RecyclerView rv_images;
        private AdapterTimelinesImages myAdapter;
        private TextView tv_all;
        private TextView tv_day, tv_year_month, tv_week;

        private int showCount = 5;
        private List<MainImageBean> allListData;
        private List<MainImageBean> showListData;
        private boolean isShowAll;

        public MyRecycleViewHolderImages(View itemView) {
            super(itemView);
            relativeLayout3 = itemView.findViewById(R.id.relativeLayout3);
            relativeLayout3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (allListData != null && allListData.size() > 0 && showListData != null && showListData.size() > 0) {
                        if (isShowAll) {
                            myAdapter.setData(showListData);
                            myAdapter.notifyDataSetChanged();
                            isShowAll = false;
                            tv_all.setText("全部");
                            // TODO: 2017/10/19
                        } else {
                            myAdapter.setData(allListData);
                            myAdapter.notifyDataSetChanged();
                            isShowAll = true;
                            tv_all.setText("收起");
                            // TODO: 2017/10/19
                        }
                    }
                }
            });

            tv_all = (TextView) itemView.findViewById(R.id.tv_all);
            tv_day = (TextView) itemView.findViewById(R.id.tv_day);
            tv_year_month = (TextView) itemView.findViewById(R.id.tv_year_month);
            tv_week = (TextView) itemView.findViewById(R.id.tv_week);

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

        @Override
        public void bindHolder(final BeanTimelines data) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(data.createdAt);
                sdf = new SimpleDateFormat("yyyy年MM月|dd|E");
                String ddd = sdf.format(date);
                String[] ss = ddd.split("\\|");
                tv_year_month.setText(ss[0]);
                tv_day.setText(ss[1]);
                tv_week.setText(ss[2]);
            } catch (ParseException e) {
                ToastManager.showShort(context, "日期转换失败了，请联系管理员处理");
                e.printStackTrace();
            } catch (Exception e) {
                ToastManager.showShort(context, "日期转换失败了，请联系管理员处理");
                e.printStackTrace();
            }

            showListData = data.list.subList(0, showCount);
            allListData = data.list;
            myAdapter.addData(showListData);


            if (data.list != null && data.list.size() > showCount) {
                relativeLayout3.setVisibility(View.VISIBLE);
                if (isShowAll) {
                    tv_all.setText("收起");
                    // TODO: 2017/10/19
                } else {
                    tv_all.setText("全部");
                    // TODO: 2017/10/19
                }
            } else {
                relativeLayout3.setVisibility(View.GONE);
            }
        }
    }

}