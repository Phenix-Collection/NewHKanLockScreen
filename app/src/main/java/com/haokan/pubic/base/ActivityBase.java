package com.haokan.pubic.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by wangzixu on 2016/11/24.
 */
public class ActivityBase extends AppCompatActivity {
    protected volatile boolean mIsDestory;
    protected boolean mHasFragment = false;

    public void setHasFragment(boolean hasFragment) {
        mHasFragment = hasFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        mIsDestory = true;
        super.onDestroy();
    }

    public boolean isDestory() {
        return mIsDestory;
    }

    public void startActivityAnim() {
        overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
    }

    public void closeActivityAnim() {
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    //*******************4种提示框相关的布局 begin*************************
    private View mNetErrorLayout;
    private View mLoadingLayout;
    private View mNoContentLayout;
    private View mServeErrorLayout;

    /**
     * 设置四种提示框，loading，网络错误，服务器错误，无内容
     */
    final public void setPromptLayout(View loadingLayout, View netErrorLayout, View serveErrorLayout , View noContentLayout) {
        mLoadingLayout = loadingLayout;
        mNetErrorLayout = netErrorLayout;
        mServeErrorLayout = serveErrorLayout;
        mNoContentLayout = noContentLayout;
    }

    public void showLoadingLayout() {
        if (mLoadingLayout != null){
            mLoadingLayout.setVisibility(View.VISIBLE);
        }
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

    public boolean isShowLoadingLayout() {
        if (mLoadingLayout != null) {
            return mLoadingLayout.getVisibility() == View.VISIBLE;
        } else {
            return false;
        }
    }
    //*******************4种提示框相关的布局 end*************************
}
