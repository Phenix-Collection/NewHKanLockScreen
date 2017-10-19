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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        mAdapter = new AdapterTimelines(this);
        mRecyclerView.setAdapter(mAdapter);

        initData();
    }

    private int page = 1;

    public void initData() {
        new ModelTimelines().getTimelinesData(this, page, new onDataResponseListener<List<BeanTimelines>>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDataSucess(List<BeanTimelines> beanTimelines) {
                BeanTimelines title = new BeanTimelines();
                title.type = BeanTimelines.TYPE_TITLE;
                beanTimelines.add(0, title);
                mAdapter.setData(beanTimelines);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDataEmpty() {

            }

            @Override
            public void onDataFailed(String errmsg) {

            }

            @Override
            public void onNetError() {

            }
        });
    }
}
