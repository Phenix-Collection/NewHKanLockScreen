package com.haokan.pubic.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by wangzixu on 2017/12/19.
 */
public class MyAnimationUtil {
    public static void clickBigSmallAnimation(final View view) {
        final ValueAnimator anim1 = ValueAnimator.ofFloat(0, 1.0f);
        anim1.setDuration(160);
        anim1.setInterpolator(new LinearInterpolator());
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                float scale = 1.0f + f*0.2f;
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        });
        anim1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ValueAnimator anim2 = ValueAnimator.ofFloat(0, 1.0f);
                anim2.setDuration(300);
                anim2.setInterpolator(new OvershootInterpolator(3));
                anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float f = (float) animation.getAnimatedValue();
                        float scale = 1.2f - f*0.2f;
                        view.setScaleX(scale);
                        view.setScaleY(scale);
                    }
                });
                anim2.start();
            }
        });
        anim1.start();
    }
}
