package com.haokan.hklockscreen.recommendpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.lockscreen.ActivityLockScreen;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.util.DisplayUtil;
import com.haokan.pubic.util.LogHelper;
import com.haokan.pubic.util.ToastManager;
import com.haokan.pubic.webview.ActivityWebview;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2017/10/18.
 */
public class CV_RecommendPage extends FrameLayout{
    protected Context mContext;
    protected RecyclerView mRecyclerView;
    protected GridLayoutManager mManager;
    protected boolean mHasMoreData;
    protected boolean mIsLoading;
    protected ArrayList<BeanRecommendItem> mData = new ArrayList<>();
    protected AdapterRecommendPage mAdapter;
    protected String mTypeName = "";
    protected int mPage = 1;
    protected View mHeaderView;
    protected View mHeaderViewCopy;
    protected ActivityBase mActivityBase;

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

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.cv_recommendpage, this, true);

        //错误界面相关
        View loadingLayout = findViewById(R.id.layout_loading);
        View netErrorView = findViewById(R.id.layout_neterror);
        View serveErrorView = findViewById(R.id.layout_servererror);
        View nocontentView = findViewById(R.id.layout_nocontent);
        setPromptLayout(loadingLayout, netErrorView, serveErrorView, nocontentView);

        //RecyView相关
        mRecyclerView = (RecyclerView) findViewById(R.id.recyview);
        mManager = new GridLayoutManager(mContext, 2);
        mManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final int div = DisplayUtil.dip2px(mContext, 1);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanIndex = layoutParams.getSpanIndex(); //在当前行的第几个位置
//                int spanSize = layoutParams.getSpanSize(); //当前item占用了几个格子
                 if (spanIndex == 0) {
                     outRect.right = div;
                     outRect.bottom = div;
                 }
                 outRect.bottom = div;
            }
        });

        mAdapter = new AdapterRecommendPage(mContext, mData, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mHasMoreData && !mIsLoading) {
                        boolean can = mRecyclerView.canScrollVertically(1);
                        if (!can) {
                            mAdapter.setFooterLoading();
                            mRecyclerView.scrollToPosition(mManager.getItemCount() - 1);
                            loadData(false);
                        }
                    }
                }
            }
        });
    }

    private boolean mRefrsh;
    onDataResponseListener mOnDataResponseListener = new onDataResponseListener<List<BeanRecommendItem>>() {
        @Override
        public void onStart() {
            if (mRefrsh) {
                mRefrsh = false;
                mData.clear();
                mAdapter.notifyDataSetChanged();
            }
            if (mData.size() == 0) {
                showLoadingLayout();
            }
            mIsLoading = true;
        }

        @Override
        public void onDataSucess(List<BeanRecommendItem> beanRecommendItems) {
            LogHelper.d("wangzixu", "recompage loadData onDataSucess size = " + beanRecommendItems.size());

            int start = mData.size();
            mData.addAll(beanRecommendItems);
            mAdapter.notifyContentItemRangeInserted(start, beanRecommendItems.size());
            mAdapter.hideFooter();
            dismissAllPromptLayout();

            mPage++;
            mIsLoading = false;
            mHasMoreData = true;
        }

        @Override
        public void onDataEmpty() {
            LogHelper.d("wangzixu", "recompage loadData onDataEmpty");

            //如果空, 自动取娱乐分类的
            if (mData.size() == 0) {
                refreshIfChangeType("娱乐");
            } else {
                dismissAllPromptLayout();
                mAdapter.hideFooter();
                if (mData.size() == 0) {
                    showNoContentLayout();
                } else {
                    mAdapter.setFooterNoMore();
                }

                mIsLoading = false;
                mHasMoreData = false;
            }
        }

        @Override
        public void onDataFailed(String errmsg) {
            LogHelper.d("wangzixu", "recompage loadData onDataFailed errmsg = " + errmsg);
            ToastManager.showShort(mContext, "onDataFailed errmsg = " + errmsg);
            dismissAllPromptLayout();
            mAdapter.hideFooter();
            if (mData.size() == 0) {
                showServeErrorLayout();
            }

            mIsLoading = false;
            mHasMoreData = true;
        }

        @Override
        public void onNetError() {
            LogHelper.d("wangzixu", "recompage loadData onNetError");
            ToastManager.showNetErrorToast(mContext);
            dismissAllPromptLayout();
            mAdapter.hideFooter();
            if (mData.size() == 0) {
                showNetErrorLayout();
            }

            mIsLoading = false;
            mHasMoreData = true;
        }
    };

    public void setTypeName(String typeName) {
        mTypeName = typeName;
    }

    public void refreshIfChangeType(String typeName) {
        if (typeName == null) {
            typeName = "";
        }
        if (mTypeName.equals(typeName)) {
            return;
        } else {
            mTypeName = typeName;
            loadData(true);
        }
    }

    public void loadData(final boolean refrsh) {
        if (refrsh) {
            mRefrsh = true;
            mPage = 1;
        }
        new ModelRecommendPage().getRecommendData(mContext, mTypeName, mPage, mOnDataResponseListener);
    }

    //*******************4种提示框相关的布局 begin*************************
    protected View mNetErrorLayout;
    protected View mLoadingLayout;
    protected View mNoContentLayout;
    protected View mServeErrorLayout;
    final public void setPromptLayout(View loadingLayout, View netErrorLayout, View serveErrorLayout , View noContentLayout) {
        mLoadingLayout = loadingLayout;
        mNetErrorLayout = netErrorLayout;
        mServeErrorLayout = serveErrorLayout;
        mNoContentLayout = noContentLayout;
    }

    public void showLoadingLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.VISIBLE);
        if (mNetErrorLayout != null) mNetErrorLayout.setVisibility(View.GONE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.GONE);
        if (mServeErrorLayout != null) mServeErrorLayout.setVisibility(View.GONE);
    }
    public void showNetErrorLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
        if (mNetErrorLayout != null) mNetErrorLayout.setVisibility(View.VISIBLE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.GONE);
        if (mServeErrorLayout != null) mServeErrorLayout.setVisibility(View.GONE);
    }
    public void showNoContentLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
        if (mNetErrorLayout != null) mNetErrorLayout.setVisibility(View.GONE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.VISIBLE);
        if (mServeErrorLayout != null) mServeErrorLayout.setVisibility(View.GONE);
    }
    public void showServeErrorLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
        if (mNetErrorLayout != null) mNetErrorLayout.setVisibility(View.GONE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.GONE);
        if (mServeErrorLayout != null) mServeErrorLayout.setVisibility(View.VISIBLE);
    }
    public void dismissAllPromptLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
        if (mNetErrorLayout != null) mNetErrorLayout.setVisibility(View.GONE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.GONE);
        if (mServeErrorLayout != null) mServeErrorLayout.setVisibility(View.GONE);
    }
    //*******************4种提示框相关的布局 end*************************

    public void setActivityBase(ActivityBase activityBase) {
        mActivityBase = activityBase;
    }

    public void startWebview(String url, String imgTitle) {
        Intent intent = new Intent(mContext, ActivityWebview.class);
        intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, url);
        intent.putExtra(ActivityWebview.KEY_INTENT_WEB_TITLE, imgTitle);
        if (mActivityBase != null) {
            mActivityBase.startActivity(intent);
            mActivityBase.startActivityAnim();
        } else {
            mContext.startActivity(intent);
        }

        if (mActivityBase instanceof ActivityLockScreen) {
            MobclickAgent.onEvent(mContext, "recommend_godetail");
        }
    }
}
