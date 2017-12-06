package com.haokan.hklockscreen.mycollection;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.App;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.DisplayUtil;
import com.haokan.pubic.util.ToastManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ActivityMyCollection extends ActivityBase implements View.OnClickListener {
    private AdapterMyCollection mAdapter;
    protected Activity mActivity;
    private TextView mTvEdit;
    private boolean mIsEditMode = false;

    private boolean mHasMoreData = true;
    private boolean mIsLoading = false;
    private RecyclerView mRecyView;
    private GridLayoutManager mManager;
    private View mBottomDelLayout;
    private TextView mTvAllPick;
    private TextView mTvDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycollection);
        EventBus.getDefault().register(this);
        initVies();
        loadData();
    }

    private void initVies() {
        //错误界面相关
        View loadingLayout = this.findViewById(R.id.layout_loading);
        loadingLayout.setOnClickListener(this);
        View netErrorView = this.findViewById(R.id.layout_neterror);
        netErrorView.setOnClickListener(this);
        View serveErrorView = this.findViewById(R.id.layout_servererror);
        serveErrorView.setOnClickListener(this);
        View noContentView = this.findViewById(R.id.layout_nocontent);
        setPromptLayout(loadingLayout, netErrorView, serveErrorView, noContentView);

        mActivity=this;
        mTvEdit = (TextView) findViewById(R.id.edit);
        mTvEdit.setOnClickListener(this);
        mBottomDelLayout = findViewById(R.id.bottomdellayout);
        mTvAllPick = (TextView) mBottomDelLayout.findViewById(R.id.tv_allpick);
        mTvDelete = (TextView) mBottomDelLayout.findViewById(R.id.tv_delete);
        mTvAllPick.setOnClickListener(this);

        findViewById(R.id.back).setOnClickListener(ActivityMyCollection.this);

        mRecyView = (RecyclerView) this.findViewById(R.id.recyview);
        mManager = new GridLayoutManager(this, 3);
        mRecyView.setLayoutManager(mManager);
        mRecyView.setHasFixedSize(true);
        mRecyView.setItemAnimator(new DefaultItemAnimator());

        final int divider = DisplayUtil.dip2px(mActivity, 5f);
        mRecyView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, divider, divider, 0);
            }
        });

        mAdapter = new AdapterMyCollection(mActivity);
        mRecyView.setAdapter(mAdapter);

        mRecyView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mHasMoreData && !mIsLoading) {
                        boolean can = mRecyView.canScrollVertically(1);
                        if (!can) {
                            mAdapter.setFooterLoading();
                            mRecyView.scrollToPosition(mManager.getItemCount() - 1);
                            loadData();
                        }
                    }
                }
            }
        });

        mAdapter.setOnSelectCountChangeListener(new AdapterMyCollection.onSelectCountChangeListener() {
            @Override
            public void onSelectCountChange(int currentCount) {
                if (currentCount <= 0) {
                    mTvDelete.setTextColor(0xff999999);
                    mTvDelete.setOnClickListener(null);
                } else {
                    mTvDelete.setTextColor(0xfffc6262);
                    mTvDelete.setOnClickListener(ActivityMyCollection.this);
                }

                if (currentCount >= mAdapter.getDataBeans().size()) {
                    mTvAllPick.setText("反选");
                    mTvAllPick.setSelected(true);
                } else {
                    mTvAllPick.setText("全选");
                    mTvAllPick.setSelected(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.tv_allpick:
                if (mTvAllPick.isSelected()) {
                    mAdapter.allNoPick();
                    mTvAllPick.setText("全选");
                    mTvAllPick.setSelected(false);
                } else {
                    mAdapter.allPick();
                    mTvAllPick.setText("反选");
                    mTvAllPick.setSelected(true);
                }
                break;
            case R.id.tv_delete:
                deleteSelectedItems();
                break;
            case R.id.edit:
                changeEditMode();
                break;
            default:
                break;
        }
    }

    public void changeEditMode() {
        if(mAdapter.isEditMode()) {
            mAdapter.exitEditMode();
            mTvEdit.setText("编辑");

            final Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.view_bottom_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mBottomDelLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            App.sMainHanlder.post(new Runnable() {
                @Override
                public void run() {
                    mBottomDelLayout.startAnimation(animation);
                }
            });
        } else {
            if (mAdapter.getDataBeans().size() == 0) {
                return;
            }

            mAdapter.enterEditMode();
            mTvEdit.setText("取消");

            mBottomDelLayout.setVisibility(View.VISIBLE);
            final Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.view_bottom_in);
            mBottomDelLayout.startAnimation(animation);
        }
    }

    public void loadData() {
        new ModelCollection().getCollectionList(this, mAdapter.getDataBeans().size(), new onDataResponseListener<List<BeanCollection>>() {
            @Override
            public void onStart() {
                mIsLoading = true;
                if (mAdapter.getDataBeans().size() == 0) {
                    showLoadingLayout();
                }
            }

            @Override
            public void onDataSucess(List<BeanCollection> list) {
                for (int i = 0; i < list.size(); i++) {
                    LogHelper.d("wangzixu", "collectionmy collnum = " + list.get(i).collect_num);
                }
                mIsLoading = false;
                mAdapter.addDataBeans(list);
                dismissAllPromptLayout();
                mHasMoreData = false;
                mAdapter.hideFooter();
            }

            @Override
            public void onDataEmpty() {
                mIsLoading = false;
                mHasMoreData = false;
                if (mAdapter.getDataBeans().size() == 0) {
                    showNoContentLayout();
                } else {
                    mAdapter.hideFooter();
//                    mAdapter.setFooterNoMore();
                }
            }

            @Override
            public void onDataFailed(String errmsg) {
                mIsLoading = false;
                mAdapter.hideFooter();
                if (mAdapter.getDataBeans().size() == 0) {
                    showServeErrorLayout();
                }
//                ToastManager.showShort(ActivityMyCollection.this, errmsg);
            }

            @Override
            public void onNetError() {
                mIsLoading = false;
                mAdapter.hideFooter();
                if (mAdapter.getDataBeans().size() == 0) {
                    showNetErrorLayout();
                }
                ToastManager.showNetErrorToast(ActivityMyCollection.this);
            }
        });
    }

    public void deleteSelectedItems() {
        final List<BeanCollection> selectedItems = mAdapter.getSelectedItems();


        if (selectedItems != null && selectedItems.size() > 0) {
            new ModelCollection().delCollections(this, selectedItems, new onDataResponseListener<Integer>() {
                @Override
                public void onStart() {
                    showLoadingDialog();
                }

                @Override
                public void onDataSucess(Integer count) {
                    EventCollectionChange change = new EventCollectionChange();
                    change.mIsAdd = false;
                    change.mFrom = ActivityMyCollection.this;
                    String ids = "";
                    for (int i = 0; i < selectedItems.size(); i++) {
                        ids = ids + selectedItems.get(i).imgId;
                        if (i != selectedItems.size() - 1) {
                            ids = ids + ",";
                        }
                    }
                    change.imgIds = ids;
                    EventBus.getDefault().post(change);


                    mAdapter.delItems(selectedItems);
                    if (mAdapter.getDataBeans().size() <= 0) {
                        mAdapter.hideFooter();
                        showNoContentLayout();
                    }

                    if (mAdapter.isEditMode()) {
                        changeEditMode(); //退出编辑态
                    }
                    dismissLoadingDialog();
                    ToastManager.showShort(mActivity, "删除成功");


                }

                @Override
                public void onDataEmpty() {
                    dismissLoadingDialog();
                }

                @Override
                public void onDataFailed(String errmsg) {
                    ToastManager.showShort(mActivity, "删除失败 : " + errmsg);
                    dismissLoadingDialog();
                }

                @Override
                public void onNetError() {
                    dismissLoadingDialog();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.isEditMode()) {
            onClick(mTvEdit);
        } else {
            super.onBackPressed();
            closeActivityAnim();
        }
    }

    @Subscribe
    public void onEvent(EventCollectionChange event) {
        if (this != event.mFrom) {
            mAdapter.getDataBeans().clear();
            mAdapter.notifyDataSetChanged();
            loadData();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
