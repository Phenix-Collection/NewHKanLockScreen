package com.haokan.hklockscreen.main;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class ActivityTimelines extends ActivityBase {
    private RecyclerView mRecyclerView;
    private AdapterTimelines mAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timelines);
        StatusBarUtil.setStatusBarTransparnet(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_timelines);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //通过获取adapter来获取当前item的itemviewtype
                int type = mRecyclerView.getAdapter().getItemViewType(position);
                if (type == BeanTimelines.TYPE_TITLE) {
                    //返回2，占一行
                    return gridLayoutManager.getSpanCount();
                } else {
                    return 1;
                }
            }
        });
        layoutManager = new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AdapterTimelines(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // item 总数，假设是 100
                final int itemCount = layoutManager.getItemCount();
                // 最后可见 item 的 position，最大值会达到 99
                final int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                // itemCount-1 是 99，只有当lastVisiblePosition 达到最大时（99）才会加载下一页
                final boolean isBottom = (lastVisiblePosition >= itemCount - 1);
                if (isBottom && !isLoading) {
                    loadMore();
                }
            }
        });

        initData();
    }

    private int mPage = 1;
    private boolean isLoading = false;

    public void initData() {
        BeanTimelines title = new BeanTimelines();
        title.type = BeanTimelines.TYPE_TITLE;
        List<BeanTimelines> beanTimelines = new ArrayList<>();
        beanTimelines.add(0, title);
        mAdapter.addData(beanTimelines);
        mAdapter.notifyDataSetChanged();

        loadMore();
    }

    public void loadMore() {
        new ModelTimelines().getTimelinesData(this, mPage, new onDataResponseListener<List<BeanTimelines>>() {
            @Override
            public void onStart() {
                isLoading = true;
            }

            @Override
            public void onDataSucess(List<BeanTimelines> beanTimelines) {
                if (beanTimelines != null && beanTimelines.size() > 0) {
                    mAdapter.addData(beanTimelines);
                    mAdapter.notifyDataSetChanged();
                }
                mPage++;
                isLoading = false;
            }

            @Override
            public void onDataEmpty() {
                isLoading = false;
            }

            @Override
            public void onDataFailed(String errmsg) {
                isLoading = false;
            }

            @Override
            public void onNetError() {
                isLoading = false;
            }
        });
    }
}
