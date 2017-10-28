package com.haokan.hklockscreen.mycollection;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.bean.BeanConvertUtil;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.detailpage.CV_DetailPageView_Base;
import com.haokan.pubic.http.onDataResponseListener;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.ToastManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2017/10/17.
 * 锁屏用的详情页, 类似一个fragment, 控制锁屏相关view的各种逻辑, 这样就和activity解耦开, 每次开启锁屏activity都可以用同一个锁屏view
 */
public class CV_DetailPage_MyCollection extends CV_DetailPageView_Base {
    private boolean mIsLoading;
    private boolean mHasMoreData = true;

    public CV_DetailPage_MyCollection(@NonNull Context context) {
        this(context, null);
    }

    public CV_DetailPage_MyCollection(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_DetailPage_MyCollection(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(this);
    }

    private void initViews(View rootView) {
        View view = mLayoutMainBottom.findViewById(R.id.divider4);
        view.setVisibility(GONE);
        mBottomSettingView.setVisibility(GONE);

        initViewPagerRightEdge();
    }

    public void initData(ArrayList<MainImageBean> mainList, int position) {
        mData.clear();
        mData.addAll(mainList);
        setVpAdapter();
        if (position == 0) {
            onPageSelected(0);
        } else {
            mVpMain.setCurrentItem(position, false);
        }
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        if (mCurrentPosition >= mData.size() - 10 && mHasMoreData && !mIsLoading) {
            loadData();
        }
    }


    protected void onClickBack() {
        if (mActivity != null) {
            mActivity.onBackPressed();
        }
    }

    public void loadData() {
        new ModelCollection().getCollectionList(mContext, mData.size(), new onDataResponseListener<List<CollectionBean>>() {
            @Override
            public void onStart() {
                mIsLoading = true;
            }

            @Override
            public void onDataSucess(List<CollectionBean> list) {
                mIsLoading = false;
                mHasMoreData = false;

                ArrayList<MainImageBean> mainList = new ArrayList<MainImageBean>();
                for (int i = 0; i < list.size(); i++) {
                    MainImageBean mainImageBean = BeanConvertUtil.collectionBean2MainImageBean(list.get(i));
                    mainList.add(mainImageBean);
                }

                mData.addAll(mainList);
                mAdapterVpMain.notifyDataSetChanged();
            }

            @Override
            public void onDataEmpty() {
                mIsLoading = false;
                mHasMoreData = false;
            }

            @Override
            public void onDataFailed(String errmsg) {
                mIsLoading = false;
            }

            @Override
            public void onNetError() {
                mIsLoading = false;
            }
        });
    }


    protected EdgeEffectCompat mRightEdge;
    protected EdgeEffectCompat mLeftEdge;
    protected void initViewPagerRightEdge() {
        try {
            Field rightEdgeField = mVpMain.getClass().getDeclaredField("mRightEdge");
            if (rightEdgeField != null) {
                rightEdgeField.setAccessible(true);
                mRightEdge = (EdgeEffectCompat) rightEdgeField.get(mVpMain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initViewPagerLeftEdge() {
        try {
            Field leftEdgeField = mVpMain.getClass().getDeclaredField("mLeftEdge");
            if (leftEdgeField != null) {
                leftEdgeField.setAccessible(true);
                mLeftEdge = (EdgeEffectCompat) leftEdgeField.get(mVpMain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPageScrollStateChanged(int arg0) {
        super.onPageScrollStateChanged(arg0);
        LogHelper.d("wangzixu", "onPageScrollStateChanged arg0 = " + arg0 + ", mRightEdge = " + mRightEdge);
        if(mRightEdge !=null && !mRightEdge.isFinished()){//到了最后一张并且还继续拖动，出现蓝色限制边条了
            try {
                ToastManager.showCenter(mContext, "已经是最后一张了");
                mRightEdge = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
