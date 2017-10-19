package com.haokan.hklockscreen.lockscreen;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.haokan.hklockscreen.R;

/**
 * Created by wangzixu on 2017/10/18.
 */
public class CV_RecommendPage extends RelativeLayout {
    private Context mContext;
    private RecyclerView mRecyclerView;

    public CV_RecommendPage(@NonNull Context context) {
        this(context, null);
    }

    public CV_RecommendPage(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_RecommendPage(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.cv_recommendpage, this, true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyview);
    }
}
