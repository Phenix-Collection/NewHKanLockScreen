package com.haokan.hklockscreen.mycollection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.base.ActivityBase;
import com.haokan.pubic.util.CommonUtil;
import com.haokan.pubic.util.DisplayUtil;

import org.greenrobot.eventbus.EventBus;

public class ActivityMyCollection extends ActivityBase implements View.OnClickListener {
    private AdapterMyCollection mAdapter;
    protected Activity mActivity;
    private TextView mEditmodeEdit;
    private boolean mIsEditMode = false;

    private int mPageIndex = 1;
    private boolean mHasMoreData = true;
    private boolean mIsLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycollection);
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
        mEditmodeEdit = (TextView) findViewById(R.id.edit);
        mEditmodeEdit.setOnClickListener(this);

        findViewById(R.id.back).setOnClickListener(ActivityMyCollection.this);

        EventBus.getDefault().register(this);

        RecyclerView mRecyView = (RecyclerView) this.findViewById(R.id.recyview);
        GridLayoutManager mManager = new GridLayoutManager(this, 3);
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
//        mRecyView.setAdapter(mAdapter);

        loadData();
    }

    public void setEditMode(boolean isEditMode) {
        mAdapter.setState(isEditMode);
    }

    public void clearData() {
        mAdapter.clearData();
    }


    @Override
    public void onClick(View view) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        if (view.getId() == R.id.back) {
            onBackPressed();
        } else if (view.getId() == R.id.edit) {
            if(!mIsEditMode) {
                enterEditMode();
            } else {
                deleteAllSelectedItems();
            }
        }
    }

    public void enterEditMode() {
        if (!mIsEditMode) {
            mIsEditMode = true;
            mEditmodeEdit.setText("删除");
            setEditMode(true);
        }
    }

    public void exitEditMode() {
        if (mIsEditMode) {
            mIsEditMode = false;
            mEditmodeEdit.setText("编辑");
            setEditMode(false);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void refreshData() {
        mPageIndex = 1;
        clearData();
        loadData();
    }

    public void loadData() {
//        ModelCollection.getCollections(this, 6, mPageIndex, Values.PAGE_SIZE, new onDataResponseListener<List<NewImageBean>>() {
//        ModelCollection.getCollectionImages(this, new onDataResponseListener<List<NewImageBean>>() {
//            @Override
//            public void onStart() {
//                mIsLoading = true;
//                if (mAdapter.getDataBeans().size() == 0) {
//                    showLoadingLayout();
//                } else {
//                    mAdapter.setFooterLoadMore();
//                }
//            }
//
//            @Override
//            public void onDataSucess(List<NewImageBean> list) {
//                mIsLoading = false;
//                mAdapter.addDataBeans(list);
//                mPageIndex++;
//                dismissAllPromptLayout();
//                mHasMoreData = false;
//            }
//
//            @Override
//            public void onDataEmpty() {
//                mIsLoading = false;
//                mHasMoreData = false;
//                if (mAdapter.getDataBeans().size() == 0) {
//                    showNoContentLayout();
////                } else if (mView.getAllItemsData().size() >= 6) {
////                    mView.setFooterNoMore();
//                } else {
////                    mView.setFooterHide();
//                    mAdapter.setFooterNoMore();
//                }
//            }
//
//            @Override
//            public void onDataFailed(String errmsg) {
//                mIsLoading = false;
//                mAdapter.hideFooter();
//                if (mAdapter.getDataBeans().size() == 0) {
//                    showServeErrorLayout();
//                }
//                showToast(errmsg);
//            }
//
//            @Override
//            public void onNetError() {
//                mIsLoading = false;
//                mAdapter.hideFooter();
//                if (mAdapter.getDataBeans().size() == 0) {
//                    showNetErrorLayout();
//                }
//                showToast(R.string.toast_net_error);
//            }
//        });
    }

    public void deleteAllSelectedItems() {
//        final List<NewImageBean> selectedItems = mAdapter.getSelectedItems();
//        if (selectedItems != null && selectedItems.size() > 0) {
//            ModelCollection.delCollectionImageBatch(this, selectedItems, new onDataResponseListener() {
//                @Override
//                public void onStart() {
//                    showLoadingLayout();
//                }
//
//                @Override
//                public void onDataSucess(Object o) {
//                    dismissAllPromptLayout();
//                    ToastManager.showFollowToast(mActivity, R.string.delete_success);
//
//                    String imageIds = "";
//                    LogHelper.d("mycollection", "deleteAllSelectedItems selectedItems size = " + selectedItems.size());
//                    for (int i = 0; i < selectedItems.size(); i++) {
//                        imageIds = imageIds + selectedItems.get(i).imgId + ",";
//                    }
//                    int i = imageIds.lastIndexOf(",");
//                    imageIds = imageIds.substring(0, i);
//                    Intent intent = new Intent(Values.Action.RECEIVER_LOCKSCREEN_COLLECTION_CHANGE);
//                    intent.putExtra("image_id", imageIds.toString());
//                    intent.putExtra("iscollect", false);
//                    sendBroadcast(intent);
//
//                    mAdapter.delItems(selectedItems);
//                    if (mAdapter.getDataBeans().size() <= 0) {
//                        mAdapter.hideFooter();
//                        showNoContentLayout();
//                    }
//                    exitEditMode();
//                }
//
//                @Override
//                public void onDataEmpty() {
//                    dismissAllPromptLayout();
//                }
//
//                @Override
//                public void onDataFailed(String errmsg) {
//                    showToast(errmsg);
//                    dismissAllPromptLayout();
//                }
//
//                @Override
//                public void onNetError() {
//                    dismissAllPromptLayout();
//                }
//            });
//        } else {
//            exitEditMode();
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivityAnim();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            refreshData();
        }
    }
}
