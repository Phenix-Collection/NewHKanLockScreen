package com.haokan.hklockscreen.lockscreen;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.haokan.pubic.clipimage.MyScroller;

/**
 * Created by wangzixu on 2017/10/18.
 */
public class CV_ScrollView extends ScrollView {
    private Context mContext;
    public CV_ScrollView(Context context) {
        this(context, null);
    }

    public CV_ScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_ScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Thread.dumpStack();
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

    //系统的smollscroll滚动太快了, 需要自定义实现begin----
    MyScroller mMyScroller;

    private void init() {
        mMyScroller = new MyScroller(mContext);
    }

    public int mAnimState; //1代表scroll, 2代表tranlate
    public void myScrollTo(int x, int y, long duration) {
        mMyScroller.setDuration(duration);
        mAnimState = 1;

        int curX = getScrollX();
        int curY = getScrollY();
        int deltaX = x - curX;
        int deltaY = y - curY;
        mMyScroller.startScroll(curX, curY, deltaX, deltaY);
        invalidate();
    }

    public void mySetTranslateY(float y, long duration) {
        mMyScroller.setDuration(duration);
        mAnimState = 2;

        float curY = getTranslationY();
        float deltaY = y - curY;
        mMyScroller.startScroll(0, (int)curY, 0, (int)deltaY);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mMyScroller.computeScrollOffset()) {
            if (mAnimState == 1) {
                int currentX = mMyScroller.getCurrentX();
                int currentY = mMyScroller.getCurrentY();
                scrollTo(currentX, currentY);
                invalidate();
            } else if (mAnimState == 2) {
                int currentY = mMyScroller.getCurrentY();
                setTranslationY(currentY);
                invalidate();
            }
        } else {
            mAnimState = 0;
            super.computeScroll();
        }
    }

    public boolean isScrolling() {
        return !mMyScroller.isFinish();
    }

    //系统的smollscroll滚动太快了, 需要自定义实现end-----
}
