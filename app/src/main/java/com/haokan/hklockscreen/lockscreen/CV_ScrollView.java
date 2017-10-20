package com.haokan.hklockscreen.lockscreen;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by wangzixu on 2017/10/18.
 */
public class CV_ScrollView extends ScrollView {
    public CV_ScrollView(Context context) {
        super(context);
    }

    public CV_ScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CV_ScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mMyOnScrollChangeListener != null) {
            mMyOnScrollChangeListener.onScrollChange(l,t,oldl,oldt);
        }
    }

    //系统的scrollview在api23以下没有提供滚动监听, 只能自己提供
    public interface MyOnScrollChangeListener{
        void onScrollChange(int scrollX, int scrollY, int oldX, int oldY);
    }
    private MyOnScrollChangeListener mMyOnScrollChangeListener;

    public void setMyOnScrollChangeListener(MyOnScrollChangeListener myOnScrollChangeListener) {
        mMyOnScrollChangeListener = myOnScrollChangeListener;
    }
}
