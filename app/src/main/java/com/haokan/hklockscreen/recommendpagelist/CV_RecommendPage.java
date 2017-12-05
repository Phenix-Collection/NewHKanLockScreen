package com.haokan.hklockscreen.recommendpagelist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.haokanAd.BeanAdRes;
import com.haokan.hklockscreen.haokanAd.ModelHaoKanAd;
import com.haokan.hklockscreen.haokanAd.onAdResListener;
import com.haokan.hklockscreen.haokanAd.request.BidRequest;
import com.haokan.hklockscreen.haokanAd.request.NativeReq;
import com.haokan.hklockscreen.recommendpageland.ActivityLandPageRecommend;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.maidian.UmengMaiDianManager;
import com.haokan.pubic.util.DisplayUtil;
import com.haokan.pubic.util.ToastManager;
import com.haokan.pubic.webview.ActivityWebview;

import java.util.ArrayList;
import java.util.Collections;
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
    protected String mFirstTypeName = "";
    protected int mPage = 1;
    protected int mTypePage = 0;
    protected View mHeaderView;
    protected View mHeaderViewCopy;
    protected ActivityBase mActivityBase;
    private static final String[] sTypes = {"美女", "娱乐", "旅游", "好看", "时尚", "资讯", "体育", "美食", "生活", "二次元", "汽车", "艺术", "摄影"};
    public static final ArrayList<String> sTypesAll = new ArrayList<>();
    static {
        for (int i = 0; i < sTypes.length; i++) {
            sTypesAll.add(sTypes[i]);
        }
    }

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
                mAdapter.onScroll();
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

    //activity调用, 用来调用广告的曝光上报
    public void onResume() {
        if (mAdapter != null) {
            int first = mManager.findFirstVisibleItemPosition();
            int last = mManager.findLastVisibleItemPosition();
            mAdapter.onResume(first, last);
        }
    }

    private boolean mRefrsh;
    onDataResponseListener mOnDataResponseListener = new onDataResponseListener<List<BeanRecommendItem>>() {
        @Override
        public void onStart() {
            if (mRefrsh) {
                mRefrsh = false;
                mData.clear();
                mAdapter.clearHolder();
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
            loadHaoKanAdData5(beanRecommendItems);
            onLoadDataSuccessForManDian();
        }

        @Override
        public void onDataEmpty() {
            LogHelper.d("wangzixu", "recompage loadData onDataEmpty");

            //如果空
            mTypePage++;
            if (mTypePage >= sTypesAll.size()) {
                dismissAllPromptLayout();
                mAdapter.hideFooter();
                if (mData.size() == 0) {
                    showNoContentLayout();
                } else {
                    mAdapter.setFooterNoMore();
                }
                mIsLoading = false;
                mHasMoreData = false;
            } else {
                mPage = 1;
                loadData(false);
            }
        }

        @Override
        public void onDataFailed(String errmsg) {
            LogHelper.d("wangzixu", "recompage loadData onDataFailed errmsg = " + errmsg);
//            ToastManager.showShort(mContext, "onDataFailed errmsg = " + errmsg);
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

    /**
     * 拉取数据成功的埋点, 因为有的推荐页实现不需要埋点, 或者点不一样, 所以需要区分开
     */
    protected void onLoadDataSuccessForManDian() {
        UmengMaiDianManager.onEvent(mContext, "event_103");
    }

    public void setDataSuccess(List<BeanRecommendItem> beanRecommendItems) {
        int start = mData.size();
        mData.addAll(beanRecommendItems);
        mAdapter.notifyContentItemRangeInserted(start, beanRecommendItems.size());
        mAdapter.hideFooter();
        dismissAllPromptLayout();

        mPage++;
        mIsLoading = false;
        mHasMoreData = true;
    }

    public void loadData(final boolean refrsh) {
        if (refrsh) {
            mRefrsh = true;
            mPage = 1;
            mTypePage = 0;
            Collections.shuffle(sTypesAll);
        }
        if (TextUtils.isEmpty(mTypeName) || mTypePage > 0) {
            if (mTypePage >= sTypesAll.size()) {
                return;
            }
            mTypeName = sTypesAll.get(mTypePage);
            if (mTypeName.equals(mFirstTypeName)) {
                mTypePage++;
                if (mTypePage >= sTypesAll.size()) {
                    return;
                }
                mTypeName = sTypesAll.get(mTypePage);
            }
        }
        LogHelper.d("wangzixu", "recompage loadData mtypeName = " + mTypeName);
        new ModelRecommendPage().getRecommendData(mContext.getApplicationContext(), mTypeName, mPage, mOnDataResponseListener);
    }

    //请求第五个位置的广告数据
    public void loadHaoKanAdData5(final List<BeanRecommendItem> beanRecommendItems) {
        //第5信息流
//        if (beanRecommendItems.size() > 4) {
        if (mData.size() < 5 && mData.size() + beanRecommendItems.size() >= 5) {
            NativeReq nativeReq = new NativeReq();
            nativeReq.w = 540;
            nativeReq.h = 960;
            nativeReq.style = 2;
            BidRequest request = ModelHaoKanAd.getBidRequest("28-53-202", 10, nativeReq, null);

            ModelHaoKanAd.getAd(mContext, request, new onAdResListener<BeanAdRes>() {
                @Override
                public void onAdResSuccess(final BeanAdRes adRes) {
                    LogHelper.d("wangzixu", "ModelHaoKanAd loadHaoKanAdData 5 onAdResSuccess");
                    BeanRecommendItem adBean = new BeanRecommendItem();
                    adBean.mBeanAdRes = adRes;
                    beanRecommendItems.add(4-mData.size(), adBean);

                    loadHaoKanAdData11(beanRecommendItems);
                }

                @Override
                public void onAdResFail(String errmsg) {
                    LogHelper.d("wangzixu", "ModelHaoKanAd loadHaoKanAdData 5 onAdResFail errmsg = " + errmsg);
                    loadHaoKanAdData11(beanRecommendItems);
                }
            });
        } else {
            loadHaoKanAdData11(beanRecommendItems);
        }
    }

    //请求第11个位置的广告数据
    public void loadHaoKanAdData11(final List<BeanRecommendItem> beanRecommendItems) {
        //第11信息流
        if (mData.size() < 11 && mData.size() + beanRecommendItems.size() >= 11) {
            NativeReq nativeReq = new NativeReq();
            nativeReq.w = 540;
            nativeReq.h = 960;
            nativeReq.style = 2;
            BidRequest request = ModelHaoKanAd.getBidRequest("28-53-203", 10, nativeReq, null);

            ModelHaoKanAd.getAd(mContext, request, new onAdResListener<BeanAdRes>() {
                @Override
                public void onAdResSuccess(final BeanAdRes adRes) {
                    LogHelper.d("wangzixu", "ModelHaoKanAd loadHaoKanAdData 10 onAdResSuccess");
                    BeanRecommendItem adBean = new BeanRecommendItem();
                    adBean.mBeanAdRes = adRes;
                    beanRecommendItems.add(10-mData.size(), adBean);

                    setDataSuccess(beanRecommendItems);
                }

                @Override
                public void onAdResFail(String errmsg) {
                    LogHelper.d("wangzixu", "ModelHaoKanAd loadHaoKanAdData 10 onAdResFail errmsg = " + errmsg);
                    setDataSuccess(beanRecommendItems);
                }
            });
        } else {
            setDataSuccess(beanRecommendItems);
        }
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

    public void startDetailPage(BeanRecommendItem beanRecommendItem) {
        if (beanRecommendItem == null) {
            return;
        }

        if (beanRecommendItem.mBeanAdRes == null) {
            Intent intent = new Intent(mContext, ActivityLandPageRecommend.class);
            intent.putExtra(ActivityLandPageRecommend.KEY_INTENT_RECOMMENDBEAN, beanRecommendItem);
            if (mActivityBase != null) {
                mActivityBase.startActivityForResult(intent, 101);
                mActivityBase.startActivityAnim();
            } else {
                mContext.startActivity(intent);
            }

//            Intent intent = new Intent(mContext, ActivityWebview.class);
//            intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, beanRecommendItem.urlClick);
//            if (mActivityBase != null) {
//                mActivityBase.startActivity(intent);
//                mActivityBase.startActivityAnim();
//            } else {
//                mContext.startActivity(intent);
//            }
        } else {
            //跳转webview
            Intent intent = new Intent(mContext, ActivityWebview.class);
            intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, beanRecommendItem.mBeanAdRes.landPageUrl);
            if (mActivityBase != null) {
                mActivityBase.startActivity(intent);
                mActivityBase.startActivityAnim();
            } else {
                mContext.startActivity(intent);
            }
        }
    }
}
