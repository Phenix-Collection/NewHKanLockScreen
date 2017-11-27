package com.haokan.hklockscreen.lockscreeninitset;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.haokan.pubic.base.ActivityBase;

/**
 * Created by wangzixu on 2017/11/16.
 */
public class CV_LockInit_ManualSetItemsBase extends LinearLayout {
    protected ActivityBase mActivityBase;
    protected Context mContext;

    public CV_LockInit_ManualSetItemsBase(Context context) {
        this(context, null);
    }

    public CV_LockInit_ManualSetItemsBase(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_LockInit_ManualSetItemsBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        mContext = context;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void setActivityBase(ActivityBase activityBase) {
        mActivityBase = activityBase;
    }

    protected onAllItemSetListener mOnAllItemSetListener;

    public void setOnAllItemSetListener(onAllItemSetListener onAllItemSetListener) {
        mOnAllItemSetListener = onAllItemSetListener;
    }

    public interface onAllItemSetListener{
        void onAllItemSet();
    }

//    //onAllItemSetListener的默认实现, 因为onAllItemSetListener以后可能会有很多方法
//    public class onAllItemSetListenerAdapter implements onAllItemSetListener{
//        @Override
//        public void onAllItemSet() {
//        }
//
//        @Override
//        public void onAutoSetItemClick() {
//        }
//    }
}
