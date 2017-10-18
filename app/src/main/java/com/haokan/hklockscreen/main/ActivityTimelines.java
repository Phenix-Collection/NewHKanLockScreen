package com.haokan.hklockscreen.main;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.base.ActivityBase;
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
                if (type == ModelTimelines.TYPE_TITLE) {
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

    public void initData() {
        List<ModelTimelines> datas = new ArrayList<>();
        ModelTimelines data = new ModelTimelines();
        data.type = ModelTimelines.TYPE_TITLE;
        datas.add(data);
        for (int i = 0; i < 60; i++) {
            data = new ModelTimelines();
            data.type = ModelTimelines.TYPE_ITEM;
            datas.add(data);
        }
        mAdapter.setData(datas);
        mAdapter.notifyDataSetChanged();
    }
}
