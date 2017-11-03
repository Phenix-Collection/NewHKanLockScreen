package com.haokan.hklockscreen.lockscreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.DisplayUtil;

/**
 * Created by wangzixu on 2017/3/7.
 * 解锁效果view
 */
public class CV_UnLockImageView extends AppCompatImageView {
    /**
     * 半透明区域的高度
     */
    private int mRadius;

    /**
     * 解锁时解锁效果跟手的系数，系数越大解锁反馈越明显
     */
    private final float UNLOCK_RADIO = 1.6f;

    /**
     * 上划解锁时需要动态画的半透明区域
     */
    private Rect mBlurRect = new Rect();

    /**
     * 上划解锁时的可见区域
     */
    private Rect mTopRect = new Rect();
    /**
     * 原图的下半部分
     */
    private Rect mSrcBlurRect = new Rect();
    // 原图bitmap中要被画的上半部分
    private Rect mSrcTopRect = new Rect();

    // 原图biamap的宽高
    private int mBitmapH, mBitmapW;

    /**
     * 画半透明区域时的canvas Y 轴偏移量
     */
    private int mBlurRectTransY;

    /**
     * 屏幕宽高
     */
    private int mWidth, mHeight;

    /**
     * 手指落点
     */
    private int mDownY;

    /**
     * 手指滑动的最高点，如果抬手的点比最高点小于一定值，就认为用户并不想解锁
     */
    private int mMinY;
    /**
     * 如果抬手的点比最高点小于一定值，就认为用户并不想解锁，差值
     */
    private int mLockDelta;

    /**
     * 上划时画半透区域的画布
     */
    private Canvas mCanvas;

    /**
     * 上划时画半透区域的paint
     */
    private static Paint sBlurPaint;

    private Context mContext;
    /**
     * 这个imagview的bitmap
     */
    private Bitmap mImageBitmap;

    /**
     * 要用来画半透明区域bitmap，是自己创建的bitmap，所以尽量小，尽量能公用
     */
    private static Bitmap sBlurBitmap;

    /**
     * 是否在解锁中
     */
    boolean mIsUnLonking = false;
    private int mTouchSlop;

    private boolean mCanUnLock = true;

    public CV_UnLockImageView(Context context) {
        this(context, null);
    }

    public CV_UnLockImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CV_UnLockImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public void init() {
        Point realScreenPoint = DisplayUtil.getRealScreenPoint(mContext);
        mWidth = realScreenPoint.x;
        mHeight = realScreenPoint.y;
//        mWidth = context.getResources().getDisplayMetrics().widthPixels;
//		mHeight = context.getResources().getDisplayMetrics().heightPixels;
        mRadius = DisplayUtil.dip2px(mContext, 30);
        mLockDelta = DisplayUtil.dip2px(mContext, 80);

        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
//        mTouchSlop = configuration.getScaledTouchSlop();
        mTouchSlop = configuration.getScaledPagingTouchSlop();

        mBlurRect.set(0, 0, mWidth, mRadius);

        if (sBlurPaint == null) {
            sBlurPaint = new Paint();
            //线程渐变
            LinearGradient linearGradient = new LinearGradient(0, 0, 0, mRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.MIRROR);
            //遮罩
            PorterDuffXfermode XFermode = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);
            sBlurPaint.setShader(linearGradient);
            sBlurPaint.setXfermode(XFermode);
        }
        if (sBlurBitmap == null) {
            sBlurBitmap = Bitmap.createBitmap(mWidth, mRadius, Bitmap.Config.ARGB_8888);
        }
        mCanvas = new Canvas(sBlurBitmap);
    }


    private boolean mCancelLongClick;
    private boolean mHasLongClicked; //记录是否相应了长按, 如果相应了长按, 就不能相应其他事件
    private Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mCancelLongClick && mOnLongClickListener != null) {
                mCancelLongClick = true;
                mHasLongClicked = mOnLongClickListener.onLongClick(CV_UnLockImageView.this);
            }
        }
    };

    public void setCanUnLock(boolean canUnLock) {
        mCanUnLock = canUnLock;
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        int action = event.getAction();
        int y = (int) event.getY();
        if (y < mMinY) {
            mMinY = y;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsUnLonking = false;
                mMinY  = y;
                mDownY = y;
                LogHelper.d("wangzixu", "unlockview dispatchTouchEvent ACTION_DOWN = " + y);

                mCancelLongClick = false;
                mHasLongClicked = false;
                postDelayed(mLongClickRunnable, 800);

                if (mImageBitmap == null) {
                    buildDrawingCache();
                    if (getDrawingCache() != null) {
                        mImageBitmap = Bitmap.createBitmap(getDrawingCache());
                        mBitmapH = mImageBitmap.getHeight();
                        mBitmapW = mImageBitmap.getWidth();
                    }
                    destroyDrawingCache();
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                LogHelper.d("wangzixu", "unlockview dispatchTouchEvent ACTION_MOVE = " + y);
                if (!mCancelLongClick && Math.abs(mDownY - y) >= 15) {
                    mCancelLongClick = true;
                    removeCallbacks(mLongClickRunnable);
                }

                if (mHasLongClicked) { //记录是否相应了长按, 如果相应了长按, 就不能相应其他事件
                    return true;
                }

                int deltaY = mDownY - y;
                if (mCanUnLock && (deltaY > mTouchSlop || mIsUnLonking)) {
                    mIsUnLonking = true;
                    deltaY = deltaY - mTouchSlop;
                    int foreGroundbottom = (int) Math.min(mHeight, mHeight - UNLOCK_RADIO * deltaY);
                    calcDisplayRect(foreGroundbottom);
                    invalidate();
                } else {
                    mIsUnLonking = false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
//                LogHelper.d("wangzixu", "unlockview dispatchTouchEvent ACTION_UP = " + y
//                        + ", mMinY = " + mMinY);
                if (mIsUnLonking) {
                    if (mDownY - y > mLockDelta
                            && y - mMinY < mLockDelta) {
                        // unLock anim
                        startAnim(mTopRect.bottom, 0);
                    } else {
                        // lock anim
                        startAnim(mTopRect.bottom, mHeight);
                    }
                }
                mCancelLongClick = true;
                removeCallbacks(mLongClickRunnable);
                if (mHasLongClicked) { //记录是否相应了长按, 如果相应了长按, 就不能相应其他事件
                    mHasLongClicked = false;
                    return true;
                }
                break;
            default:
                break;
        }
        return mIsUnLonking || super.dispatchTouchEvent(event);
    }

    OnLongClickListener mOnLongClickListener;
    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        mOnLongClickListener = l;
    }

    /**
     * 此效果分两部分，上面是原图清晰的部分，下面是带遮罩效果的由不透明到全透明的过渡区域，foreGroundbottom
     * 这个数值代表此两部分分界线
     */
    private void calcDisplayRect(int foreGroundbottom) {
        // 要把bitmap画到屏幕上，分成两部分，上面是不透明部分，下面是透明渐变部分
        // 所以我们要知道4个方框：原图的上半部分方框，原图的半透明部分方框，目标屏幕的上半部分，目标屏幕的下半部分。

        // 目标屏幕要画的两个区域大小基于我们手指上划的距离，传进来的foreGroundbottom代表目标屏幕上面区域的底边
        // 所以可以根据这个值很容易计算出目标屏幕的两个方框，如下：
        // 1，目标屏幕的上边部分
        mTopRect.set(0, 0, mWidth, foreGroundbottom);
        // 2，目标屏幕的下半部分
        // 其实就是我们根据透明半径创建的mBlurRect大小， mDestBottomRect.set(0, 0, mWidth, mRadius)，
        // 我们在画时动态的向下移动canvas即可，移动的距离即：
        mBlurRectTransY = foreGroundbottom;
        if (mOnUnLockListener != null) {
            mOnUnLockListener.onUnLocking(mBlurRectTransY * 1.0f / mHeight);
        }

        // 3，原图的上半部分，原图的宽高为mBitmapH, mBitmapW,左上都是0，右边是宽，我们只需要计算下边就可以，即：
        float bottom = mBitmapH * ((float) mBlurRectTransY / mHeight); // ----根据目标屏幕上半部分的比例，来计算原图大小。
        mSrcTopRect.set(0, 0, mBitmapW, (int) bottom);

        // 4,原图的下半部分，我们需要根据目标屏幕中下半部分方框高所占的比例，来计算出原图中半透明区域的高度：
        float radiusInBitmap = mBitmapH * ((float) mRadius / mHeight);
        mSrcBlurRect.set(0, (int) bottom, mBitmapW, (int) (bottom + radiusInBitmap));

        invalidate();
    }

    private void startAnim(int start, final int end) {
//        if (start == end) {//开头和结尾相同，不用做动画了
//            calcDisplayRect(end);
//            isTouch = false;
//            if (mOnUnLockListener != null) {
//                if (end == 0) {
//                    mOnUnLockListener.onUnLockSuccess();
//                } else {
//                    mOnUnLockListener.onUnLockFailed();
//                }
//            }
//            return;
//        }

        ValueAnimator anim = ValueAnimator.ofInt(start, end);
        int b = Math.min(300, mTopRect.bottom >> 3);
        int d = b > 0 ? b : 0;
        anim.setDuration(d);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int foreGroundbottom = (Integer) animation.getAnimatedValue();
                calcDisplayRect(foreGroundbottom);
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsUnLonking = false;
                if (mOnUnLockListener != null) {
                    if (end == 0) {
                        mOnUnLockListener.onUnLockSuccess();
                    } else {
                        mOnUnLockListener.onUnLockFailed();
                    }
                }
            }
        });
        anim.start();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (mIsUnLonking) {
            drawForeGround(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    private void drawForeGround(Canvas canvas) {
        //画上半部分
        canvas.drawBitmap(mImageBitmap, mSrcTopRect, mTopRect, null);

        //在sBlurBitmap上画原图, 然后画遮罩, 形成渐变的图
        mCanvas.drawBitmap(mImageBitmap, mSrcBlurRect, mBlurRect, null);
        mCanvas.drawRect(mBlurRect, sBlurPaint);

        //把渐变的图
        canvas.save();
        canvas.translate(0, mBlurRectTransY);
        canvas.drawBitmap(sBlurBitmap, 0, 0, null);
        canvas.restore();
    }

    public interface onUnLockListener {
        void onUnLockSuccess();
        void onUnLockFailed();
        void onUnLocking(float f);
    }

    public onUnLockListener mOnUnLockListener;

    public void setOnUnLockListener(onUnLockListener onUnLockListener) {
        mOnUnLockListener = onUnLockListener;
    }

    //获取设置的bitmap的过程

    public Bitmap getImageBitmap() {
        return mImageBitmap;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.mImageBitmap = bm;
        if (mImageBitmap != null) {
            mBitmapH = mImageBitmap.getHeight();
            mBitmapW = mImageBitmap.getWidth();
        }
    }

//    @Override
//    public void setImageDrawable(Drawable drawable) {
//        super.setImageDrawable(drawable);
//        mImageBitmap = getBitmapFromDrawable(drawable);
//        mBitmapH = mImageBitmap.getHeight();
//        mBitmapW = mImageBitmap.getWidth();
//    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        mImageBitmap = getBitmapFromDrawable(getDrawable());
        mBitmapH = mImageBitmap.getHeight();
        mBitmapW = mImageBitmap.getWidth();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mImageBitmap = uri != null ? getBitmapFromDrawable(getDrawable()) : null;
        mBitmapH = mImageBitmap.getHeight();
        mBitmapW = mImageBitmap.getWidth();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
