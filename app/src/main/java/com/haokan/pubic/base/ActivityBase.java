package com.haokan.pubic.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.haokan.hklockscreen.R;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by wangzixu on 2016/11/24.
 */
public class ActivityBase extends Activity {
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
        MobclickAgent.onPause(this);
        super.onPause();
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

    //loading 对话框, Dialog形式的loading, 不同于loadinglayout, loadinglayout在页面底层, dialog盖在页面上层
    private Dialog mProgressDialog;
    public void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new Dialog(this, R.style.CustomDialog);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setContentView(R.layout.dialog_layout_loading);
        }
        mProgressDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mProgressDialog.show();
    }

    public void dismissLoadingDialog() {
        if (!mIsDestory && mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * 设置四种提示框，loading，网络错误，服务器错误，无内容
     */
    final public void setPromptLayout(View loadingLayout, View netErrorLayout, View serveErrorLayout , View noContentLayout) {
        mLoadingLayout = loadingLayout;
        mNetErrorLayout = netErrorLayout;
        mServeErrorLayout = serveErrorLayout;
        mNoContentLayout = noContentLayout;

        if (this instanceof View.OnClickListener) {
            View.OnClickListener listener = (View.OnClickListener) this;
            if (mLoadingLayout != null) mLoadingLayout.setOnClickListener(listener);
            if (mNetErrorLayout != null) mNetErrorLayout.setOnClickListener(listener);
            if (mNoContentLayout != null) mServeErrorLayout.setOnClickListener(listener);
            if (mServeErrorLayout != null) mNoContentLayout.setOnClickListener(listener);
        }
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

    public boolean isShowLoadingLayout() {
        if (mLoadingLayout != null) {
            return mLoadingLayout.getVisibility() == View.VISIBLE;
        } else {
            return false;
        }
    }
    //*******************4种提示框相关的布局 end*************************
}
