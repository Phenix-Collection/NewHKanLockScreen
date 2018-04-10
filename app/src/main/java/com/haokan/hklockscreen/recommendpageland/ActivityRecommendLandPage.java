package com.haokan.hklockscreen.recommendpageland;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.hklockscreen.haokanAd.BeanAdRes;
import com.haokan.hklockscreen.haokanAd.ModelHaoKanAd;
import com.haokan.hklockscreen.haokanAd.onAdResListener;
import com.haokan.hklockscreen.haokanAd.request.BidRequest;
import com.haokan.hklockscreen.haokanAd.request.NativeReq;
import com.haokan.hklockscreen.mycollection.EventCollectionChange;
import com.haokan.hklockscreen.mycollection.ModelCollection;
import com.haokan.hklockscreen.recommendpagedetail.ActivityRecommendDetailPage;
import com.haokan.hklockscreen.recommendpagelist.BeanRecommendItem;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.bean.BeanConvertUtil;
import com.haokan.pubic.database.BeanCollection;
import com.haokan.pubic.database.MyDatabaseHelper;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.CommonUtil;
import com.haokan.pubic.util.MyAnimationUtil;
import com.haokan.pubic.util.ToastManager;
import com.haokan.pubic.webview.ActivityWebview;
import com.j256.ormlite.dao.Dao;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/11/7.
 */
public class ActivityRecommendLandPage extends ActivityBase implements View.OnClickListener {
    private TextView mTvTitle;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mManager;
    private boolean mHasMoreData;
    private boolean mIsLoading;
    private int mCommentPage;
    private BeanRecommendItem mRecommendItemBean;
    public static final String KEY_INTENT_RECOMMENDBEAN = "recommenbean";
    private AdapterLandPageRecommend mAdapter;
    private ArrayList<BeanRecommendLandPage> mData = new ArrayList<>();
    private int mTitleBottomH;
    private AdapterLandPageRecommend.ItemHeaderViewHolder mHeaderItem;
    private View mShareLayout;
    private View mShareLayoutContent;
    private View mShareLayoutBg;
    private View mTvCollect;
    private int mLastVisablePos; //用于滑动检测广告上报

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendpageland);
        initView();

        mRecommendItemBean = getIntent().getParcelableExtra(KEY_INTENT_RECOMMENDBEAN);

        if (mRecommendItemBean == null) {
            ToastManager.showShort(this, "推荐bean不能为空");
            finish();
            return;
        }
        loadHaoKanAd();

        checkCollect();
    }

    private void initView() {
        //错误界面相关
        View loadingLayout = findViewById(R.id.layout_loading);
        View netErrorView = findViewById(R.id.layout_neterror);
        View serveErrorView = findViewById(R.id.layout_servererror);
        View nocontentView = findViewById(R.id.layout_nocontent);
        setPromptLayout(loadingLayout, netErrorView, serveErrorView, nocontentView);

        findViewById(R.id.back).setOnClickListener(this);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        findViewById(R.id.share).setOnClickListener(this);
        mTvCollect = findViewById(R.id.collect);
        mTvCollect.setOnClickListener(this);

        //*****底部分享区域begin*********
        mShareLayout = findViewById(R.id.bottom_share);
        mShareLayoutContent = mShareLayout.findViewById(R.id.content);
        mShareLayoutBg = mShareLayout.findViewById(R.id.bg);
        mShareLayoutBg.setOnClickListener(this);

        mShareLayoutContent.findViewById(R.id.share_weixin).setOnClickListener(this);
        mShareLayoutContent.findViewById(R.id.share_weixin_circle).setOnClickListener(this);
        mShareLayoutContent.findViewById(R.id.share_qq).setOnClickListener(this);
        mShareLayoutContent.findViewById(R.id.share_qqzone).setOnClickListener(this);
        mShareLayoutContent.findViewById(R.id.share_sina).setOnClickListener(this);
        mShareLayoutContent.findViewById(R.id.cancel).setOnClickListener(this);
        //*****底部分享区域end*********

        //RecyView相关
        mRecyclerView = (RecyclerView) findViewById(R.id.recyview);
        mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new AdapterLandPageRecommend(this, mData);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mHeaderItem != null) {
                    int top = mHeaderItem.itemView.getTop();
                    LogHelper.d("wangzixu", "onScrolled top = " + top + ", mTitleBottomH = " + mTitleBottomH);
                    if (top <= -mTitleBottomH && mTvTitle.getVisibility() != View.VISIBLE) {
                        mTvTitle.setVisibility(View.VISIBLE);
                    } else if (top >= -30 && mTvTitle.getVisibility() == View.VISIBLE) {
                        mTvTitle.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mAdItem != null && mData.size() > 0) {
                    int lastpos = mManager.findLastVisibleItemPosition();
                    if (lastpos != mLastVisablePos) {
                        if (lastpos == mData.size()) {
                            ModelHaoKanAd.onAdShow(mAdItem.mBeanAdRes.onShowUrls);
                        }
                        mLastVisablePos = lastpos;
                    }
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //加载更多评论相关, 暂时没有
//                    if (mHasMoreData && !mIsLoading) {
//                        boolean can = mRecyclerView.canScrollVertically(1);
//                        if (!can) {
////                            mAdapter.setFooterLoading();
//                            mRecyclerView.scrollToPosition(mManager.getItemCount() - 1);
//                            loadComment();
//                        }
//                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mAdItem != null && mAdapter != null && mData.size() > 0) {
//            int i = mManager.findLastVisibleItemPosition();
//            LogHelper.d("wangzixu", "activityRecommend onResume i = " + i + ", mData.size() = " + mData.size());
//            if (i == mData.size()) { //因为有一个header,不在mData中, 所以不是mData.size()-1
//                //上报广告展示
//                ModelHaoKanAd.onAdShow(mAdItem.mBeanAdRes);
//            }
//        }
    }

    public void getBlurBg() {
//        View view = getWindow().getDecorView();
//
//        //设置允许当前窗口保存缓存信息
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache();
//        Bitmap drawingCache = view.getDrawingCache();
//        if (drawingCache != null) {
//            Bitmap blurBitmap = BlurUtil.blurBitmap2(drawingCache, 5, 6);
//            BitmapDrawable mBlurDrawable = new BitmapDrawable(getResources(), blurBitmap);
//            mBlurDrawable.setColorFilter(0xFF777777, PorterDuff.Mode.MULTIPLY);
//        }
//        view.destroyDrawingCache();
    }


    public void checkCollect() {
        final Scheduler.Worker worker = Schedulers.io().createWorker();
        worker.schedule(new Action0() {
            @Override
            public void call() {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(ActivityRecommendLandPage.this).getDaoQuickly(BeanCollection.class);
                    Object o = dao.queryForId(mRecommendItemBean.GroupId);
                    if (o != null) {
                        mTvCollect.setSelected(true);
                    } else {
                        mTvCollect.setSelected(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                worker.unsubscribe();
            }
        });
    }

    public void setHeaderItem(AdapterLandPageRecommend.ItemHeaderViewHolder headerItem) {
        mHeaderItem = headerItem;
        mHeaderItem.mTvTitle.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mTitleBottomH = bottom-top;
            }
        });
        mTvTitle.setText(mData.get(0).imgTitle);
    }

    public void loadData() {
        new ModelRecommendPageLand().getImgListByGroupId(this, mRecommendItemBean.GroupId, new onDataResponseListener<ResponseBody_ImgGroupList>() {
            @Override
            public void onStart() {
                showLoadingLayout();
            }

            @Override
            public void onDataSucess(ResponseBody_ImgGroupList res) {
                if (mIsDestory) {
                    return;
                }
                ArrayList<BeanRecommendLandPage> list = res.list;

                mData.clear();
                mData.addAll(list);

                //分享按钮
                BeanRecommendLandPage shareItem = new BeanRecommendLandPage();
                shareItem.myType = 1;
                mData.add(shareItem);

                if (mAdItem != null) {
                    mData.add(mAdItem);
                }

                mAdapter.notifyDataSetChanged();
                dismissAllPromptLayout();
            }

            @Override
            public void onDataEmpty() {
                if (mIsDestory) {
                    return;
                }
                dismissAllPromptLayout();
                showNoContentLayout();
            }

            @Override
            public void onDataFailed(String errmsg) {
                if (mIsDestory) {
                    return;
                }
                dismissAllPromptLayout();
                showServeErrorLayout();
            }

            @Override
            public void onNetError() {
                if (mIsDestory) {
                    return;
                }
                dismissAllPromptLayout();
                showServeErrorLayout();
            }
        });
    }

    BeanRecommendLandPage mAdItem;
    public void loadHaoKanAd() {
        showLoadingLayout();

        NativeReq nativeReq = new NativeReq();
        nativeReq.w = 1080;
        nativeReq.h = 586;
        nativeReq.style = 2;

//        BidRequest request = ModelHaoKanAd.getBidRequest("28-53-205", 10, nativeReq, null);
        BidRequest request = ModelHaoKanAd.getBidRequest(this, "288-170-192", 10, nativeReq, null);

        ModelHaoKanAd.getAd(this, request, new onAdResListener<BeanAdRes>() {
            @Override
            public void onAdResSuccess(final BeanAdRes adRes) {
                LogHelper.d("wangzixu", "ModelHaoKanAd landpage onAdResSuccess");
                //广告
                mAdItem = new BeanRecommendLandPage();
                mAdItem.myType = 4;
                mAdItem.mBeanAdRes = adRes;
                loadData();
            }

            @Override
            public void onAdResFail(String errmsg) {
                LogHelper.d("wangzixu", "ModelHaoKanAd landpage onAdResFail errmsg = " + errmsg);
                loadData();
            }
        });



//        BannerReq bannerReq = new BannerReq();
//        bannerReq.w = 1080;
//        bannerReq.h = 586;
//        BidRequest request = ModelHaoKanAd.getBidRequest("28-53-205", 5, null, bannerReq);
//
//        ModelHaoKanAd.getAd(this, request, new onAdResListener<BeanAdRes>() {
//            @Override
//            public void onAdResSuccess(final BeanAdRes adRes) {
//                LogHelper.d("wangzixu", "ModelHaoKanAd landpage onAdResSuccess");
//                //广告
//                mAdItem = new BeanRecommendLandPage();
//                mAdItem.myType = 2;
//                mAdItem.mBeanAdRes = adRes;
//                loadData();
//            }
//
//            @Override
//            public void onAdResFail(String errmsg) {
//                LogHelper.d("wangzixu", "ModelHaoKanAd landpage onAdResFail errmsg = " + errmsg);
//                loadData();
//            }
//        });
    }

    //加载评论
    public void loadComment() {

    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.cancel:
            case R.id.bg:
                hideShareLayout();
                break;
            case R.id.share_weixin:
                shareTo(SHARE_MEDIA.WEIXIN);
                hideShareLayout();
                break;
            case R.id.share_weixin_circle:
                shareTo(SHARE_MEDIA.WEIXIN_CIRCLE);
                hideShareLayout();
                break;
            case R.id.share_sina:
                shareTo(SHARE_MEDIA.SINA);
                hideShareLayout();
                break;
            case R.id.share_qq:
                shareTo(SHARE_MEDIA.QQ);
                hideShareLayout();
                break;
            case R.id.share_qqzone:
                shareTo(SHARE_MEDIA.QZONE);
                hideShareLayout();
                break;
            case R.id.share:
                showShareLayout();
                break;
            case R.id.collect:
                MyAnimationUtil.clickBigSmallAnimation(v);
                onClickCollect(v);
                break;
            default:
                break;
        }
    }

    private void shareTo(SHARE_MEDIA media) {
        if (mRecommendItemBean == null) {
            return;
        }

        UMWeb web = new UMWeb(mRecommendItemBean.urlClick);
        web.setTitle(mRecommendItemBean.imgTitle);//标题
        web.setDescription(mRecommendItemBean.imgDesc);
        web.setThumb(new UMImage(this, mRecommendItemBean.cover));  //缩略图

        new ShareAction(this)
                .setPlatform(media)
                .withMedia(web)
                .setCallback(mUMShareListener)
                .share();
    }

    private UMShareListener mUMShareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            ToastManager.showShort(ActivityRecommendLandPage.this, "已分享");
            hideShareLayout();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastManager.showShort(ActivityRecommendLandPage.this, "分享失败");
            LogHelper.d("share","分享失败:"+t);
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastManager.showShort(ActivityRecommendLandPage.this, "分享取消");
        }
    };

    protected void onClickCollect(final View view) {
        if (mRecommendItemBean == null) {
            return;
        }

        if (view.isSelected()) {
            new ModelCollection().delCollection(this, mRecommendItemBean.GroupId, new onDataResponseListener<Integer>() {
                @Override
                public void onStart() {
//                    if (mActivity != null) {
//                        mActivity.showLoadingDialog();
//                    }
                }

                @Override
                public void onDataSucess(Integer integer) {
//                    if (mActivity != null) {
//                        mActivity.dismissLoadingDialog();
//                    }
//                    mCurrentImgBean.isCollect = 0;
//                    mCurrentImgBean.collect_num--;
//                    refreshCollectNum(mCurrentImgBean);
                    view.setSelected(false);

                    EventCollectionChange change = new EventCollectionChange();
                    change.imgIds = mRecommendItemBean.GroupId;
                    change.mIsAdd = false;
                    change.collectionType = 1;
                    change.mFrom = ActivityRecommendLandPage.this;
                    EventBus.getDefault().post(change);
                }

                @Override
                public void onDataEmpty() {
//                    if (mActivity != null) {
//                        mActivity.dismissLoadingDialog();
//                    }
                }

                @Override
                public void onDataFailed(String errmsg) {
//                    if (mActivity != null) {
//                        mActivity.dismissLoadingDialog();
//                    }
                    ToastManager.showShort(ActivityRecommendLandPage.this, "取消收藏失败: " + errmsg);
                }

                @Override
                public void onNetError() {
//                    if (mActivity != null) {
//                        mActivity.dismissLoadingDialog();
//                    }
                    ToastManager.showNetErrorToast(ActivityRecommendLandPage.this);
                }
            });
        } else {
            BeanCollection beanCollection = BeanConvertUtil.recommendItem2CollectionBean(mRecommendItemBean);

            beanCollection.collectType = 1;
            beanCollection.collect_num++;
            new ModelCollection().addCollection(this, beanCollection, new onDataResponseListener<BeanCollection>() {
                @Override
                public void onStart() {
//                    if (mActivity != null) {
//                        mActivity.showLoadingDialog();
//                    }
                }

                @Override
                public void onDataSucess(BeanCollection collectionBean) {
//                    if (mActivity != null) {
//                        mActivity.dismissLoadingDialog();
//                    }
//                    mCurrentImgBean.isCollect = 1;
//                    mCurrentImgBean.collect_num++;
//                    refreshCollectNum(mCurrentImgBean);
                    view.setSelected(true);

                    EventCollectionChange change = new EventCollectionChange();
                    change.imgIds = mRecommendItemBean.GroupId;
                    change.mIsAdd = true;
                    change.collectionType = 1;
                    change.mFrom = ActivityRecommendLandPage.this;
                    EventBus.getDefault().post(change);
                }

                @Override
                public void onDataEmpty() {
//                    if (mActivity != null) {
//                        mActivity.dismissLoadingDialog();
//                    }
                }

                @Override
                public void onDataFailed(String errmsg) {
//                    if (mActivity != null) {
//                        mActivity.dismissLoadingDialog();
//                    }
                    ToastManager.showShort(ActivityRecommendLandPage.this, "收藏失败: " + errmsg);
                }

                @Override
                public void onNetError() {
//                    if (mActivity != null) {
//                        mActivity.dismissLoadingDialog();
//                    }
                    ToastManager.showNetErrorToast(ActivityRecommendLandPage.this);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mShareLayout.getVisibility() == View.VISIBLE) {
            hideShareLayout();
        } else {
            super.onBackPressed();
            closeActivityAnim();
        }
    }

    public void showShareLayout() {
        if (mShareLayout.getVisibility() != View.VISIBLE) {
            mShareLayout.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.activity_fade_in);
            mShareLayoutBg.startAnimation(animation);

            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.view_bottom_in);
            mShareLayoutContent.startAnimation(animation1);
        }
    }

    private void hideShareLayout() {
        if (mShareLayout.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mShareLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mShareLayoutBg.startAnimation(animation);

            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.view_bottom_out);
            animation1.setFillAfter(true);
            mShareLayoutContent.startAnimation(animation1);
        }
    }

    public void startDetailPage(ArrayList<BeanRecommendLandPage> data, int pos) {
        if (data == null || CommonUtil.isQuickClick()) {
            return;
        }
        Intent intent = new Intent(this, ActivityRecommendDetailPage.class);
        intent.putParcelableArrayListExtra(ActivityRecommendDetailPage.KEY_INTENT_GROUDDATE, data);
        intent.putExtra(ActivityRecommendDetailPage.KEY_INTENT_POSITION, Math.max(pos, 0));
        startActivity(intent);
        overridePendingTransition(R.anim.activity_fade_bigger_in, R.anim.activity_retain);
    }

    public void startAdDetailPage(BeanRecommendLandPage mBean) {
        if (mBean.mBeanAdRes == null || CommonUtil.isQuickClick()) {
            return;
        }
        Intent intent = new Intent(this, ActivityWebview.class);
//                Intent intent = new Intent(mContext, ActivityWebviewForLockPage.class);
        //查询是否有deeplink的app
        if (!TextUtils.isEmpty(mBean.mBeanAdRes.deeplink)) {
            Intent qi = new Intent(Intent.ACTION_VIEW, Uri.parse(mBean.mBeanAdRes.deeplink));
            if (CommonUtil.deviceCanHandleIntent(this, qi)) { //是否可以支持deeplink
                intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBean.mBeanAdRes.deeplink);
            } else {
                intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBean.mBeanAdRes.landPageUrl);
            }
        }else {
            intent.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBean.mBeanAdRes.landPageUrl);
        }

        startActivity(intent);
        startActivityAnim();

        //广告点击上报
        if (mBean.mBeanAdRes.onClickUrls != null && mBean.mBeanAdRes.onClickUrls.size() > 0) {
            ModelHaoKanAd.onAdClick(mBean.mBeanAdRes.onClickUrls);
        }
    }
}
