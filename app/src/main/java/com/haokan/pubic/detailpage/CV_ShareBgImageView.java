package com.haokan.pubic.detailpage;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * 大图页底部分享界面用的模糊背景, 通过clip使imageview只绘制界面的一部分图
 */
public class CV_ShareBgImageView extends AppCompatImageView {
    private int mTop;
    public CV_ShareBgImageView(Context context) {
        super(context);
    }

    public CV_ShareBgImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTopEdge(int top) {
        mTop = top;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, mTop, getWidth(), getHeight());
        super.onDraw(canvas);
        canvas.restore();
    }
}
