package com.haokan.hklockscreen.localDICM;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import com.haokan.pubic.logsys.LogHelper;


public class ClipZoomImageView extends AppCompatImageView {
	private float mLastX;
	private float mLastY;
	private Matrix mMatrix = new Matrix();

	/**
	 * 初始填充的缩放, 和当前的缩放, 和允许最小的最大的缩放系数
	 */
	private float mCurrentScale, mZoomMinScale, mZommMaxScale;

	//原始bitmap的宽高，当前bitmap的宽高, 本imageview的宽高，
	private float mOriBmpWidth, mOriBmpHeight, mCurrentBmpWidth, mCurrentBmpHeight, mWidth, mHeight;
	//当前bitmap的左上角点
	private float mBitmapLeft, mBitmapTop;

	private float mOldDistant;
	private float mCurrentDistant;
	private PointF mCenter = new PointF();

	public static final int NONE = 0;
	public static final int DRAG = 1;
	public static final int ZOOM = 2;
	public static final int ZOOM_ANIM = 3;

	private float[] mTmpArray = new float[9];
	private static TimeInterpolator sInterpolator = new DecelerateInterpolator();
	private AnimatorUpdateListener zoomBacklistener = null;

    //x，y方向的剪裁框内的冗余量，当前图片的宽高减去剪裁框的宽高，负数说明图片小于于剪裁框
	private float mRedundantXSpace, mRedundantYSpace;
	private int mMode;
	private boolean mAlreadyLoadBigBmp = false;
	private boolean mIsOnLeftSide, mIsOnRightSide, mIsOnTopSide, mIsOnBottomSide;
	private int mPointCount;
	private Bitmap mBitmap = null;
	private RectF mClipRect; //剪裁框, 也是图片最小框, 图片边缘不能进入这个框内
	private RectF mEdgeRect; //图片边缘框
	private int mFirstFillMode = 0; //初始时, 图片显示填充的模式 0:以剪裁框centercrop的模式, 1:以边缘框centercrop的模式, 2以边缘框fitcenter的模式
	private boolean mSizeChanged = false;
	private boolean mCanZoom = true;

    /**
     * 判断用户是否开始滑动的，手指防抖裕量
     */
    private int mTouchSlop;

	public ClipZoomImageView(Context context) {
		this(context, null);
	}

	public ClipZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClipZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
	}

	/**
	 * 设置剪裁框/最小框
     */
    public void setClipRect(RectF clipRect) {
        mClipRect = clipRect;
        calcBitmapWH();
    }

    /**
     * 自己实现初始时图片怎么显示，这里是用matrix实现的center_crop的效果，
     * 而且可以实现以任意的框来center_crop, 系统的scaleType除了fitXy，其他
     * 的也是这样用matrix变换的，参考系统源码，imageview中的configureBounds()方法
     */
	private void calcBitmapWH() {
		if (mWidth == 0 || mHeight == 0 || mOriBmpWidth == 0 || mOriBmpHeight == 0) {
			return;
		}

		if (mSizeChanged) {
			mSizeChanged = false;
		} else {
			return;
		}

        //模拟系统的形式来处理图片，参考系统源码，imageview中的configureBounds()方法
		if (mFirstFillMode == 0) { //以剪裁框centercrop的模式
			if (mClipRect == null) {
				mClipRect = new RectF(0, 0 , mWidth, mHeight);
			}
			mEdgeRect = new RectF(0, 0 , mWidth, mHeight);

			float scaleX = mClipRect.width() / mOriBmpWidth;
			float scaleY = mClipRect.height() / mOriBmpHeight;

			mCurrentScale = mZoomMinScale = Math.max(scaleX, scaleY);
			mZommMaxScale = 5.0f;

			calcRedundantSpace();

			mBitmapLeft = -mRedundantXSpace/2;
			mBitmapTop = -mRedundantYSpace/2;

			mMatrix.setScale(mCurrentScale, mCurrentScale, 0.5f, 0.5f);
			mMatrix.postTranslate(Math.round(mBitmapLeft), Math.round(mBitmapTop));
			setImageMatrix(mMatrix);

			checkIsOnSide();
		} else if (mFirstFillMode == 1) { //1:以边缘框centercrop的模式
			mEdgeRect = new RectF(0, 0 , mWidth, mHeight);

			float scaleX = mEdgeRect.width() / mOriBmpWidth;
			float scaleY = mEdgeRect.height() / mOriBmpHeight;

			mCurrentScale = mZoomMinScale = Math.max(scaleX, scaleY);
			mZommMaxScale = 5.0f;

			mCurrentBmpWidth = mCurrentScale * mOriBmpWidth;
			mCurrentBmpHeight = mCurrentScale * mOriBmpHeight;
			mRedundantXSpace =  mCurrentBmpWidth - mEdgeRect.width();
			mRedundantYSpace = mCurrentBmpHeight - mEdgeRect.height();

			mBitmapLeft = -mRedundantXSpace/2;
			mBitmapTop = -mRedundantYSpace/2;

			if (mClipRect == null) {
				mClipRect = new RectF(0, 0 , mWidth, mHeight);
			} else {
				calcRedundantSpace();
			}

			mMatrix.setScale(mCurrentScale, mCurrentScale, 0.5f, 0.5f);
			mMatrix.postTranslate(Math.round(mBitmapLeft), Math.round(mBitmapTop));
			setImageMatrix(mMatrix);

			checkIsOnSide();
		} else if (mFirstFillMode == 2) {//2以边缘框fitcenter的模式
			//留白的填充方式, 图片要在这个方框内显示, 初始显示成留白的方式, 也是最小的缩放系数, 再小要返回,
			//最大可以显示成窄边充满的方式, 也是最大的缩放系数, 再大也要返回, 有时候还要双击切换着两种显示形态
			mEdgeRect = new RectF(0, 0 , mWidth, mHeight);

			float scaleX = mEdgeRect.width() / mOriBmpWidth;
			float scaleY = mEdgeRect.height() / mOriBmpHeight;

			mCurrentScale = mZoomMinScale = Math.min(scaleX, scaleY);
			mZommMaxScale = 5.0f;

			mCurrentBmpWidth = mCurrentScale * mOriBmpWidth;
			mCurrentBmpHeight = mCurrentScale * mOriBmpHeight;
			mRedundantXSpace =  mCurrentBmpWidth - mEdgeRect.width();
			mRedundantYSpace = mCurrentBmpHeight - mEdgeRect.height();

			mBitmapLeft = -mRedundantXSpace/2;
			mBitmapTop = -mRedundantYSpace/2;

			if (mClipRect == null) {
				mClipRect = new RectF(mBitmapLeft, mBitmapTop , mCurrentBmpWidth, mCurrentBmpHeight);
			} else {
				//防止边缘进入剪裁框的特殊情况
				if (mBitmapLeft > mClipRect.left || mBitmapTop > mClipRect.top) {
					//以边缘框centercrop, 图片就会进入剪裁框了, 所以需要已剪裁框centercrop
					mFirstFillMode = 0;
					calcBitmapWH();
					return;
				}
			}

			mMatrix.postScale(mCurrentScale, mCurrentScale, 0.5f, 0.5f);
			mMatrix.postTranslate(mBitmapLeft, mBitmapTop);
			setImageMatrix(mMatrix);

			checkIsOnSide();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		LogHelper.d("zoomimageview", "onSizeChanged = " + ", this = " + this);
		mWidth = getWidth();
		mHeight = getHeight();
		if (mWidth != 0 && mHeight != 0) {
			mSizeChanged = true;
		}
		calcBitmapWH();
		super.onSizeChanged(w, h, oldw, oldh);
	}

    private void setup() {
        if (mBitmap != null) {
            mOriBmpWidth = mBitmap.getWidth();
            mOriBmpHeight = mBitmap.getHeight();
            calcBitmapWH();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

	//setImageBitmap 内部实现会调用到此方法
//    @Override
//    public void setImageDrawable(Drawable drawable) {
//        super.setImageDrawable(drawable);
//        mBitmap = getBitmapFromDrawable(drawable);
//        setup();
//    }

//    @Override
//    public void setImageResource(@DrawableRes int resId) {
//        super.setImageResource(resId);
//        mBitmap = getBitmapFromDrawable(getDrawable());
//        setup();
//    }

//    @Override
//    public void setImageURI(Uri uri) {
//        super.setImageURI(uri);
//        mBitmap = uri != null ? getBitmapFromDrawable(getDrawable()) : null;
//        setup();
//    }

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

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		if (true) { //如果父控件是不可滑动的，那么手势自己处理//如果是可以滑动的，手势是由父控件的手势中直接调用
			handleTouchEvent(event, true);
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}

	private void checkIntoDrag(boolean parentIsOnEdge) {
		if (parentIsOnEdge
				&& ((mRedundantXSpace > 0)
				|| (mRedundantYSpace > 0))
				) {
			mMode = DRAG;
			mIsFirstMove = true;
		}
	}

    private boolean mIsFirstMove; //有静到拖动
    private boolean mIsFirstScale; //由静止到开始双指缩放
	public int handleTouchEvent(MotionEvent event, boolean parentIsOnEdge) {
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				mMode = NONE;
				mPointCount = 1;
				checkIntoDrag(parentIsOnEdge);
				mLastX = event.getX(0);
				mLastY = event.getY(0);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				mPointCount++;
				if (!mCanZoom || mPointCount > 2 || mMode == ZOOM_ANIM) {
					break;
				}
				mOldDistant = getDistance(event);
				if (mOldDistant > mTouchSlop * 2) {
					mMode = ZOOM;
					mIsFirstScale = true;
				}
				break;
			case MotionEvent.ACTION_POINTER_UP:
				mPointCount--;
				if (mPointCount > 1) {
					break;
				}
                mMode = NONE;
                if (mCurrentScale < mZoomMinScale) {
                    if (parentIsOnEdge) {
					    mMode = ZOOM_ANIM;
                    }
					startScaleAnim(mCurrentScale, mZoomMinScale);
				} else if (mCurrentScale > mZommMaxScale) {
					startScaleAnim(mCurrentScale, mZommMaxScale);
				} else {
					checkIntoDrag(parentIsOnEdge);
					if (mMode == DRAG) {
						int pointerIndex = event.getActionIndex();
						mLastX = event.getX(1 - pointerIndex);
						mLastY = event.getY(1 - pointerIndex);
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mMode == DRAG) {
					float currentX = event.getX(0);
					float currentY = event.getY(0);
					float deltaX = currentX - mLastX;
					float deltaY = currentY - mLastY;
					if (mIsFirstMove) { //第一次，由静到动，移动距离应该大于touchslop,防抖
						if (Math.abs(deltaX) > mTouchSlop || Math.abs(deltaY) > mTouchSlop) {
							mIsFirstMove = false;
							mLastX = currentX;
							mLastY = currentY;
							break;
						} else {
							break;
						}
					}
					mLastX = currentX;
					mLastY = currentY;
					//由拖动图片转向viewpager滑动的过程
					if (mParentCanScroll) {
						//在viewpager中，父容器可以滑动，所以要时刻判断图片是否拖动到了边框
//                        LogHelper.d("zoomview", "mRedundantXSpace mIsOnRightSide deltaX = " + mRedundantXSpace + ", " + mIsOnRightSide + ", " + deltaX);
						if (mRedundantXSpace <= 0) { // 说明x方向图片和最小框已经重合了，不能响应图片左右拖动，只能响应上下拖动
							if (Math.abs(deltaX) > Math.abs(deltaY)
//									&& Math.abs(deltaX) > mTouchSlop
                                    ) { //说明当前用户想要左右拖动，应该使父容易滚动
								event.setAction(MotionEvent.ACTION_DOWN);
								mMode = NONE; //返回none，父viewpager就会自己处理事件
								break;
							}
						} else if ((Math.abs(deltaX) > Math.abs(deltaY))
//                                && Math.abs(deltaX) > mTouchSlop //首先判断是在左右滑动手势中
								&& ((mIsOnLeftSide && deltaX > 0) //如果滑到了左边缘，并且继续向右滑动，
								|| (mIsOnRightSide && deltaX < 0))) { // 如果滑到了右边缘，并且继续向左滑动 应该让父容器接受事件
							event.setAction(MotionEvent.ACTION_DOWN);
							mMode = NONE; //返回none，父viewpager就会自己处理事件
                            LogHelper.d("zoomview", "mRedundantXSpace mIsOnLeftSide mIsOnRightSide deltaX = " + mRedundantXSpace + ", " + mIsOnLeftSide +", " + mIsOnRightSide + ", " + deltaX);
							break;
						}
					}
					checkAndSetTranslate(deltaX, deltaY);
				} else if (mMode == ZOOM) {
					mCurrentDistant = getDistance(event);
					float scaleFactor = mCurrentDistant / mOldDistant;
					float deltaScale = Math.abs(scaleFactor - 1.0f);
					if (deltaScale < 0.001) {
						break;
					}

					if (mIsFirstScale) { //初次开始动，总有个突兀的跳变，所以消除掉第一次，防抖
						mIsFirstScale = false;
						mOldDistant = mCurrentDistant;
						break;
					}

					mOldDistant = mCurrentDistant;
					if (scaleFactor > 1.05f) {
						scaleFactor = 1.05f;
					} else if (scaleFactor < 0.95f) {
						scaleFactor = 0.95f;
					}
					getCenter(mCenter, event);
					zoomImg(scaleFactor);
				}
				break;
			case MotionEvent.ACTION_UP:
				mMode = NONE;
				mPointCount = 0;
				if (mCurrentScale > mZoomMinScale && !mIsOnLeftSide && !mIsOnRightSide) {
					mMode = DRAG;
				}
				break;
		}
		return mMode;
	}

    private boolean mIsScaleAniming= false;
	private void startScaleAnim(float start, float end) {
        if (mIsScaleAniming) {
            return;
        }
        mIsScaleAniming = true;
		ValueAnimator anim = ValueAnimator.ofFloat(start, end);
		anim.setDuration(200);
		anim.setInterpolator(sInterpolator);
		if (zoomBacklistener == null) {
			zoomBacklistener = new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float f = (Float)animation.getAnimatedValue();
					float scaleFactor = f / mCurrentScale;
					zoomImg(scaleFactor);
				}
			};
		}
		anim.addUpdateListener(zoomBacklistener);
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				checkAndSetTranslate(0, 0);
                mIsScaleAniming = false;
				mMode = NONE;
			}
		});
		anim.start();
	}

	private void zoomImg(float scaleFactor) {
		if (scaleFactor == 1.0f) {
			return;
		}
		mCurrentScale *= scaleFactor;

		float px, py; //缩放的中心点，如果图片宽小于边框宽了，中点就是边框的中心，否则就是双指的中心
		px = mRedundantXSpace <= 0 ? mRect.centerX() : mCenter.x;
		py = mRedundantYSpace <= 0 ? mRect.centerY() : mCenter.y;
		mMatrix.postScale(scaleFactor, scaleFactor, px, py);

		calcRedundantSpace();
		mMatrix.getValues(mTmpArray);
		mBitmapLeft = mTmpArray[Matrix.MTRANS_X];
		mBitmapTop = mTmpArray[Matrix.MTRANS_Y];

		//X方向有裕量的情况下，图片边缘不能进入最小框
		if (mRedundantXSpace >= 0) {
			if (mBitmapLeft > mRect.left) {
				mMatrix.postTranslate(mRect.left - mBitmapLeft, 0);
			} else if (mBitmapLeft + mCurrentBmpWidth < mRect.right) {
				mMatrix.postTranslate(mRect.right - mCurrentBmpWidth - mBitmapLeft, 0);
			}
		}

		if (mRedundantYSpace >= 0) {
			if (mBitmapTop > mRect.top) {
				mMatrix.postTranslate(0, mRect.top - mBitmapTop);
			} else if (mBitmapTop + mCurrentBmpHeight < mRect.bottom) {
				mMatrix.postTranslate(0, mRect.bottom - mCurrentBmpHeight - mBitmapTop);
			}
		}
		setImageMatrix(mMatrix);
		mMatrix.getValues(mTmpArray);
		mBitmapLeft = mTmpArray[Matrix.MTRANS_X];
		mBitmapTop = mTmpArray[Matrix.MTRANS_Y];
        checkIsOnSide();
	}

	private float getDistance(MotionEvent event) {
		float x = event.getX(1) - event.getX(0);
		float y = event.getY(1) - event.getY(0);
        return (float) Math.sqrt((x * x + y * y));
	}

	private PointF getCenter(PointF centerF, MotionEvent event) {
		float x = (event.getX(1) + event.getX(0)) / 2;
		float y = (event.getY(1) + event.getY(0)) / 2;
		centerF.set(x, y);
		return centerF;
	}

	public boolean isAlreadyLoadBigBmp() {
		return mAlreadyLoadBigBmp;
	}

	public void setAlreadyLoadBigBmp(boolean alreadyLoadBigBmp) {
		mAlreadyLoadBigBmp = alreadyLoadBigBmp;
	}

    /**
     * 因为图片边缘不能进入最小框，所以需要知道最小框和图片宽高之间的差值，
     * 即x，y方向的冗余量，负数说明图片小于最小框
     */
	private void calcRedundantSpace() {
		mCurrentBmpWidth = mCurrentScale * mOriBmpWidth;
		mCurrentBmpHeight = mCurrentScale * mOriBmpHeight;
        mRedundantXSpace =  mCurrentBmpWidth - mClipRect.width();
        mRedundantYSpace = mCurrentBmpHeight - mClipRect.height();
    }

	private void checkAndSetTranslate(float deltaX, float deltaY) {
		if (mRedundantXSpace <= 0) {
			float offset = mRedundantXSpace/2;
			if (mBitmapLeft != mRect.left - offset) {
				deltaX = mRect.left - offset - mBitmapLeft;
			} else {
				deltaX = 0;
			}
		} else {
			if (mBitmapLeft + deltaX > mRect.left) { //移动完后图片就进入最小框左边缘了，需要处理
				deltaX = mRect.left - mBitmapLeft;
			} else if (mBitmapLeft + deltaX + mCurrentBmpWidth < mRect.right) {
				deltaX = mRect.right - mCurrentBmpWidth - mBitmapLeft;
			}
		}

		if (mRedundantYSpace <= 0) {
			float offset = mRedundantYSpace/2;
			if (mBitmapTop != mRect.top - offset) {
				deltaY = mRect.top - offset - mBitmapTop;
			} else {
				deltaY = 0;
			}
		} else {
			if (mBitmapTop + deltaY > mRect.top) {
				deltaY = mRect.top - mBitmapTop;
			} else if (mBitmapTop + deltaY + mCurrentBmpHeight < mRect.bottom) {
				deltaY = mRect.bottom - mCurrentBmpHeight - mBitmapTop;
			}
		}
		if (deltaX != 0 || deltaY != 0) {
			mMatrix.postTranslate(deltaX, deltaY);
			setImageMatrix(mMatrix);
			mBitmapLeft = mBitmapLeft + deltaX;
			mBitmapTop = mBitmapTop + deltaY;
            checkIsOnSide();
		}
	}

    private void checkIsOnSide() {
        mIsOnLeftSide = false;
        mIsOnRightSide = false;
        mIsOnTopSide = false;
        mIsOnBottomSide = false;

		if (mBitmapLeft >= mClipRect.left) {
			mIsOnLeftSide = true;
		}
		if (mBitmapLeft + mCurrentBmpWidth <= mClipRect.right) {
			mIsOnRightSide = true;
		}
		if (mBitmapTop >= mClipRect.top) {
			mIsOnTopSide = true;
		}
		if (mBitmapTop + mCurrentBmpHeight <= mClipRect.bottom) {
			mIsOnBottomSide = true;
		}
    }

	private void getMatrixXY(Matrix m) {
		m.getValues(mTmpArray);
		mBitmapLeft = mTmpArray[Matrix.MTRANS_X];
		mBitmapTop = mTmpArray[Matrix.MTRANS_Y];
//		mMatrixX = mTmpArray[Matrix.MTRANS_X];
//		mMatrixY = mTmpArray[Matrix.MTRANS_Y];
	}

	public float getReDundantXSpace() {
		return mRedundantXSpace;
	}

	public void setCanZoom(boolean canZoom) {
		mCanZoom = canZoom;
	}

	public int getTouchMode() {
		return mMode;
	}

	public boolean canHorizontalDrag() {
		if (mCurrentScale >= mZoomMinScale && mRedundantXSpace > 0) {
			return true;
		}
		return false;
	}

    public boolean canVerticalDrag() {
        if (mCurrentScale >= mZoomMinScale && mRedundantYSpace > 0) {
            return true;
        }
        return false;
    }

	public float getCurrentScale() {
		return mCurrentScale;
	}

    public boolean isOnTopSide() {
        return mIsOnTopSide;
    }

    public boolean isOnBottomSide() {
        return mIsOnBottomSide;
    }

    public boolean isOnLeftSide() {
		return mIsOnLeftSide;
	}

	public boolean isOnRightSide() {
		return mIsOnRightSide;
	}

	public void setMaxMinScale(float max, float min) {
		mZommMaxScale = max;
		mZoomMinScale = min;
	}

	@Override
	public Matrix getMatrix() {
		return mMatrix;
	}

	public RectF getRect() {
		return mRect;
	}

	public Bitmap getOriginalBmp() {
		return mBitmap;
	}

    //***为了模拟ios的抬手后自动滚动一段距离而实现begin
	MyScroller mScroller;
	public void flingDistence(int dx, int dy) {
		if (mScroller == null) {
			mScroller = new MyScroller(getContext());
		}
//		mScroller.setDuration(Math.abs(dx) * 3);
		mScroller.startScroll(0, 0, dx, dy);
		mLastX = 0;
		invalidate();
	}

	public void stopFling() {
		if (mScroller != null) {
			mScroller.forceFinished(true);
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller == null) {
			return;
		}
		if (mScroller.computeScrollOffset()) {
			float currentX = mScroller.getCurrentX();
			float currentY = mScroller.getCurrentY();
			float deltaX = currentX - mLastX;
			float deltaY = currentY - mLastY;
			mLastX = currentX;
			mLastY = currentY;

			LogHelper.d("zoomview", "currentX, deltaX = " + currentX + ", " +deltaX);
			checkAndSetTranslate(deltaX, deltaY);
			if ((mIsOnLeftSide && deltaX > 0)
					|| (mIsOnRightSide && deltaX < 0)) {
				mScroller.forceFinished(true);
			}
		}
	}
	//***为了模拟ios的抬手后自动滚动一段距离而实现end

}
