package com.haokan.hklockscreen.recommendpagedetail;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import com.haokan.hklockscreen.R;
import com.haokan.pubic.App;
import com.haokan.pubic.logsys.LogHelper;


/**
 * 可以支持图片放大缩小的viewpager，需要配合zoomimage一起实现
 */
public class ZoomImageViewPager_new extends ViewPager {
    private Zoomimageview_new mImgView = null;
	private float mLastX, mLastY;
	private int mTouchSlop;
	private float mPerformDownX;
	private float mPerformDownY;
    private long mPerformDownTime;
    private int mScolledPosition = 0;
    private int mScolledOffsetPixels = 0;
    private int mPageWidth = 0;
    private VelocityTracker mVelocityTracker;
    /**
     * 0代表滑动viewpager滑动,1代表图片滑动,2代表图片缩放, 3代表上下滑动状态
     */
    private int mImageMode = -1;
    private long mPointCount;
    private boolean mCanZoom = false;
    private Context mContext;

    public ZoomImageViewPager_new(Context context) {
        this(context, null);
    }

    public ZoomImageViewPager_new(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageViewPager_new(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs);
        mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZoomViewPager);
        try {
            mCanZoom = a.getBoolean(R.styleable.ZoomViewPager_zoomable, false);
        } finally {
            a.recycle();
        }

        if (mCanZoom) {
            initZoom();
        }
	}

	public void setCanZoom(boolean canZoom) {
        mCanZoom = canZoom;
        if (canZoom) {
            initZoom();
        }
    }

	private void initZoom() {
        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mScolledPosition = position;
                mScolledOffsetPixels = positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //1,手动拖动状态
                //2，放手回原位的过程中
                //0，静止状态
                if (state == 0) {
                    mScolledPosition = getCurrentItem();
                    mScolledOffsetPixels = 0;
                }
                LogHelper.d("zoomview", "onPageScrollStateChanged state = " + state);
            }
        });
    }

    public void setZoomImageView(Zoomimageview_new view) {
		mImgView = view;
	}

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPageWidth = getWidth();
    }

    public Zoomimageview_new getImgView() {
        return mImgView;
    }

    @Override
	public boolean onTouchEvent(MotionEvent event) {
        if (!mCanZoom || mImgView == null) {
            return super.onTouchEvent(event);
        }

	    try {
			int actionMasked = event.getActionMasked();
            switch (actionMasked) {
                case MotionEvent.ACTION_DOWN:
                    mPointCount = 1;

                    //模拟一个onClick事件
                    mLastX = event.getX();
                    mLastY = event.getY();

                    mPerformDownX = mLastX;
                    mPerformDownY = mLastY;

                    mStartClick = true;
                    mPerformDownTime = SystemClock.uptimeMillis();
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        mVelocityTracker.clear();
                    }
                    mVelocityTracker.addMovement(event);
                    mImgView.stopFling();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //本次滑动移动的距离
                    float currentX = event.getX();
                    float currentY = event.getY();

                    float deltaX = currentX - mLastX;
                    float deltaY = currentY - mLastY;
                    mLastX = currentX;
                    mLastY = currentY;

                    if (mImageMode == -1) {
                        float x = currentX - mPerformDownX;
                        float y = currentY - mPerformDownY;
                        float absX = Math.abs(x);
                        float absY = Math.abs(y);
                        if ((absX > mTouchSlop) || absY > mTouchSlop) {
                            mStartClick = false;

                            if (!mImgView.canDrag() && absY > absX) {
                                mImageMode = 3;
                            } else
                            if (mImgView.isOnRightSide()
                                    && deltaX < 0
//                                    && 2*Math.abs(x) >= Math.abs(y)
                                    ) {
                                mImageMode = 0;
                            } else if (mImgView.isOnLeftSide()
                                    && deltaX > 0
//                                    && 2*Math.abs(x) >= Math.abs(y)
                                    ) {
                                mImageMode = 0;
                            } else {
                                mImgView.mIsFirstMove = true;
                                boolean handle = mImgView.checkAndSetTranslate(x, y);
                                if (handle) {
                                    mImageMode = 1;
                                } else {
                                    mImageMode = 0;
                                }
                            }
                        }
                    } else if (mImageMode == 0) {
                        //viewpager正在滚动
                        int currentPosition = getCurrentItem();
                        //viewpager左滑, 滑动到下一页的过程, 是由(currentposion, 0) 到 (currentposion,1079)的过程
                        //viewpager右滑, 滑动到上一页的过程,是由(currentposion-1,1079) 到 (currentposion-1,0)的过程

                        //如果viewpager由左滑变为右滑，并且图片在右边缘，所以此时应该把viewpager滚动到当前页，并且开始拖动图片
                        if (mImgView.canHorizontalDrag() && mImgView.isOnRightSide() && mScolledPosition == currentPosition && mScolledOffsetPixels - deltaX < 0) {
                            //由滑动下一页转变为滑动上一页的过程，并且图片在右边缘，所以此时应该把viewpager滚动到当前页，
                            //并且开始拖动图片的过程
                            scrollBy(-mScolledOffsetPixels, 0);
                            mScolledOffsetPixels = 0;
                            mScolledPosition = currentPosition;

                            mImageMode = 1;
                        } else if (mImgView.canHorizontalDrag() && mImgView.isOnLeftSide() && mScolledPosition == currentPosition-1 && mScolledOffsetPixels - deltaX > (mPageWidth)) {
                            //由滑动上一页转变为滑动下一页的过程，并且图片在左边缘，所以此时应该把viewpager滚动到当前页，
                            //并且开始拖动图片的过程
                            scrollBy(mPageWidth - mScolledOffsetPixels, 0);
                            mScolledOffsetPixels = 0;
                            mScolledPosition = currentPosition;
                            mImageMode = 1;
                        }
                    } else if (mImageMode == 1) { //图片正在滑动
                        if (mImgView.isOnRightSide()
                                && deltaX < 0
                                    && Math.abs(deltaX) >= Math.abs(deltaY)
                                ) {
                            mImageMode = 0;
                            event.setAction(MotionEvent.ACTION_DOWN);
                        } else if (mImgView.isOnLeftSide()
                                && deltaX > 0
                                    && Math.abs(deltaX) >= Math.abs(deltaY)
                                ) {
                            mImageMode = 0;
                            event.setAction(MotionEvent.ACTION_DOWN);
                        } else {
                            mImgView.checkAndSetTranslate(deltaX, deltaY);
                        }
                    } else if (mImageMode == 2 && mPointCount == 2) {
                        mImgView.processZoomEvent(event);
                    } else if (mImageMode == 3) {
                        float y = currentY - mPerformDownY;
                        if (mOnSlideYListener != null) {
                            mOnSlideYListener.onSlideY(y);
                        }
                    }
                    mVelocityTracker.addMovement(event);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mPointCount++;
                    LogHelper.d("zoomvp", "POINTER_DOWN mScolledOffsetPixels = " + mScolledOffsetPixels);
                    if (mPointCount > 2 || mScolledOffsetPixels != 0) {
                        break;
                    }
                    boolean isZoomImage = mImgView.checkCanZoom(event);
                    if (isZoomImage) {
                        mImageMode = 2;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mPointCount--;
                    if (mPointCount > 1) {
                        break;
                    }
                    if (mImageMode == 2) {
                        mImgView.processZoomBack(event);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    float absX = Math.abs(event.getX() - mPerformDownX);
                    float absY = Math.abs(event.getY() - mPerformDownY);
                    if (mStartClick && mScolledOffsetPixels == 0
                            && absX <= mTouchSlop
                            && absY <= mTouchSlop) {
                        long uptimeMillis = SystemClock.uptimeMillis() - mPerformDownTime;
                        if (uptimeMillis >= 600) {
                            if (mOnLongClikListener != null) {
                                mOnLongClikListener.onLongClick(ZoomImageViewPager_new.this);
                            }
                        } else if (uptimeMillis <= 290) {
                            App.sMainHanlder.removeCallbacks(mClickRun);
                            mClickCount ++;
                            if (mClickCount >= 2) { //触发双击事件
                                mClickCount = 0;
                                mImgView.doubleClickZoom(event);
                            } else {
                                App.sMainHanlder.postDelayed(mClickRun, 300);
                            }
                        }
                    }
                case MotionEvent.ACTION_CANCEL:
                    if (mImageMode == 1) {
                        mVelocityTracker.addMovement(event);
                        mVelocityTracker.computeCurrentVelocity(1000);
                        final float vX = mVelocityTracker.getXVelocity();
                        final float vY = mVelocityTracker.getYVelocity();

                        float dx = vX / 7;
                        float dy = vY / 7;
//                            LogHelper.d("zoomvp", " vx = " + vX + ", dx = " + dx);
                        mImgView.flingDistence((int) dx, (int) dy);
                    } else if (mImageMode == 3) {
                        if (mOnSlideYListener != null) {
                            mOnSlideYListener.onSlideEnd(event.getY() - mPerformDownY);
                        }
                    }
                    mPointCount = 0;
                    mImageMode = -1;
                    return super.onTouchEvent(event);
                default:
                    break;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

        if (mImageMode == 0) {
		    return super.onTouchEvent(event);
        } else {
            return true;
        }
	}

    private int mClickCount = 0; //用来处理双击事件的
    private boolean mStartClick;
    private OnClickListener mOnClikListener;
    private OnLongClickListener mOnLongClikListener;

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClikListener = l;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mOnLongClikListener = l;
    }
    private Runnable mClickRun = new Runnable() {
        @Override
        public void run() {
            mClickCount = 0;
            if (mOnClikListener != null) {
                mOnClikListener.onClick(ZoomImageViewPager_new.this);
            }
        }
    };

    private onSlideYListener mOnSlideYListener;
    public void setOnSlideYListener(onSlideYListener onSlideYListener) {
        mOnSlideYListener = onSlideYListener;
    }

    public interface onSlideYListener {
        void onSlideY(float distance);
        void onSlideEnd(float distance);
    }
}
