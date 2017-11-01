package com.haokan.pubic.clipimage;

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
import android.view.VelocityTracker;
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
	private int mPointCount;
	private Bitmap mBitmap = null;
	private RectF mClipRect; //剪裁框, 也是图片最小框, 图片边缘不能进入这个框内
	private RectF mEdgeRect; //图片边缘框
	private int mFirstFillMode = 0; //初始时, 图片显示填充的模式 0:以剪裁框centercrop的模式, 1:以边缘框centercrop的模式, 2以边缘框fitcenter的模式
	private boolean mSizeChanged = false;

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
			mZommMaxScale = mCurrentScale*4.0f;

			calcRedundantSpace();

			mBitmapLeft = mClipRect.left-mRedundantXSpace*0.5f;
			mBitmapTop = mClipRect.top-mRedundantYSpace*0.5f;
//			LogHelper.d("wangzixu", "clipimg calcBitmapWH called mClipRect = " + mClipRect.toString());
//			LogHelper.d("wangzixu", "clipimg calcBitmapWH called mBitmapLeft = " + mBitmapLeft + ", mBitmapTop = " + mBitmapTop);

			mMatrix.setScale(mCurrentScale, mCurrentScale, 0.5f, 0.5f);
			mMatrix.postTranslate(Math.round(mBitmapLeft), Math.round(mBitmapTop));
			setImageMatrix(mMatrix);
		} else if (mFirstFillMode == 1) { //1:以边缘框centercrop的模式
			mEdgeRect = new RectF(0, 0 , mWidth, mHeight);

			float scaleX = mEdgeRect.width() / mOriBmpWidth;
			float scaleY = mEdgeRect.height() / mOriBmpHeight;

			mCurrentScale = mZoomMinScale = Math.max(scaleX, scaleY);
			mZommMaxScale = mCurrentScale*4.0f;

			mCurrentBmpWidth = mCurrentScale * mOriBmpWidth;
			mCurrentBmpHeight = mCurrentScale * mOriBmpHeight;
			mRedundantXSpace =  mCurrentBmpWidth - mEdgeRect.width();
			mRedundantYSpace = mCurrentBmpHeight - mEdgeRect.height();

			mBitmapLeft = mEdgeRect.left-mRedundantXSpace*0.5f;
			mBitmapTop = mEdgeRect.left-mRedundantYSpace*0.5f;

			if (mClipRect == null) {
				mClipRect = new RectF(0, 0 , mWidth, mHeight);
			} else {
				calcRedundantSpace();
			}

			mMatrix.setScale(mCurrentScale, mCurrentScale, 0.5f, 0.5f);
			mMatrix.postTranslate(Math.round(mBitmapLeft), Math.round(mBitmapTop));
			setImageMatrix(mMatrix);
		} else if (mFirstFillMode == 2) {//2以边缘框fitcenter的模式
			//留白的填充方式, 图片要在这个方框内显示, 初始显示成留白的方式, 也是最小的缩放系数, 再小要返回,
			//最大可以显示成窄边充满的方式, 也是最大的缩放系数, 再大也要返回, 有时候还要双击切换着两种显示形态
			mEdgeRect = new RectF(0, 0 , mWidth, mHeight);

			float scaleX = mEdgeRect.width() / mOriBmpWidth;
			float scaleY = mEdgeRect.height() / mOriBmpHeight;

			mCurrentScale = mZoomMinScale = Math.min(scaleX, scaleY);
			mZommMaxScale = mCurrentScale*4.0f;

			mCurrentBmpWidth = mCurrentScale * mOriBmpWidth;
			mCurrentBmpHeight = mCurrentScale * mOriBmpHeight;
			mRedundantXSpace =  mCurrentBmpWidth - mEdgeRect.width();
			mRedundantYSpace = mCurrentBmpHeight - mEdgeRect.height();

			mBitmapLeft = mEdgeRect.left-mRedundantXSpace*0.5f;
			mBitmapTop = mEdgeRect.left-mRedundantYSpace*0.5f;

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
			handleTouchEvent(event);
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}

	private void checkIntoDrag() {
		if (mRedundantXSpace > 0 || mRedundantYSpace > 0) {
			mMode = DRAG;
			mIsFirstMove = true;
		}
	}

    private boolean mIsFirstMove; //有静到拖动
    private boolean mIsFirstScale; //由静止到开始双指缩放
	private VelocityTracker mVelocityTracker;
	public int handleTouchEvent(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				mLastX = event.getX(0);
				mLastY = event.getY(0);
				mMode = NONE;
				mPointCount = 1;

				checkIntoDrag();

				mVelocityTracker.clear();
				mVelocityTracker.addMovement(event);
				stopFling();
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				mPointCount++;
				if (mPointCount > 2 || mMode == ZOOM_ANIM) {
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
					mMode = ZOOM_ANIM;
					startScaleAnim(mCurrentScale, mZoomMinScale);
				} else if (mCurrentScale > mZommMaxScale) {
					startScaleAnim(mCurrentScale, mZommMaxScale);
				} else {
					checkIntoDrag();
					if (mMode == DRAG) {
						int pointerIndex = event.getActionIndex();
						mLastX = event.getX(1 - pointerIndex);
						mLastY = event.getY(1 - pointerIndex);
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				mVelocityTracker.addMovement(event);
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
			case MotionEvent.ACTION_CANCEL:
				if (mMode == DRAG) {
					mVelocityTracker.addMovement(event);
					mVelocityTracker.computeCurrentVelocity(2000);
					final float vX = mVelocityTracker.getXVelocity();
					final float vY = mVelocityTracker.getYVelocity();



					float dx = vX/10;
					float dy = vY/10;
					flingDistence((int) dx, (int) dy);
				}
				mMode = NONE;
				mPointCount = 0;
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
		px = mRedundantXSpace <= 0 ? mClipRect.centerX() : mCenter.x;
		py = mRedundantYSpace <= 0 ? mClipRect.centerY() : mCenter.y;
		mMatrix.postScale(scaleFactor, scaleFactor, px, py);

		calcRedundantSpace();

		mMatrix.getValues(mTmpArray);
		mBitmapLeft = mTmpArray[Matrix.MTRANS_X];
		mBitmapTop = mTmpArray[Matrix.MTRANS_Y];

		//X方向有裕量的情况下，图片边缘不能进入最小框
		if (mRedundantXSpace >= 0) {
			if (mBitmapLeft > mClipRect.left) {
				mMatrix.postTranslate(mClipRect.left - mBitmapLeft, 0);
			} else if (mBitmapLeft + mCurrentBmpWidth < mClipRect.right) {
				mMatrix.postTranslate(mClipRect.right - mCurrentBmpWidth - mBitmapLeft, 0);
			}
		}

		if (mRedundantYSpace >= 0) {
			if (mBitmapTop > mClipRect.top) {
				mMatrix.postTranslate(0, mClipRect.top - mBitmapTop);
			} else if (mBitmapTop + mCurrentBmpHeight < mClipRect.bottom) {
				mMatrix.postTranslate(0, mClipRect.bottom - mCurrentBmpHeight - mBitmapTop);
			}
		}
		setImageMatrix(mMatrix);
		mMatrix.getValues(mTmpArray);
		mBitmapLeft = mTmpArray[Matrix.MTRANS_X];
		mBitmapTop = mTmpArray[Matrix.MTRANS_Y];
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
			float offset = mRedundantXSpace*0.5f;
			if (mBitmapLeft != mClipRect.left - offset) {
				deltaX = mClipRect.left - offset - mBitmapLeft;
			} else {
				deltaX = 0;
			}
		} else {
			if (mBitmapLeft + deltaX > mClipRect.left) { //移动完后图片就进入最小框左边缘了，需要处理
				deltaX = mClipRect.left - mBitmapLeft;
			} else if (mBitmapLeft + deltaX + mCurrentBmpWidth < mClipRect.right) {
				deltaX = mClipRect.right - mCurrentBmpWidth - mBitmapLeft;
			}
		}

		if (mRedundantYSpace <= 0) {
			float offset = mRedundantYSpace*0.5f;
			if (mBitmapTop != mClipRect.top - offset) {
				deltaY = mClipRect.top - offset - mBitmapTop;
			} else {
				deltaY = 0;
			}
		} else {
			if (mBitmapTop + deltaY > mClipRect.top) {
				deltaY = mClipRect.top - mBitmapTop;
			} else if (mBitmapTop + deltaY + mCurrentBmpHeight < mClipRect.bottom) {
				deltaY = mClipRect.bottom - mCurrentBmpHeight - mBitmapTop;
			}
		}
		if (deltaX != 0 || deltaY != 0) {
			mMatrix.postTranslate(deltaX, deltaY);
			setImageMatrix(mMatrix);
			mBitmapLeft = mBitmapLeft + deltaX;
			mBitmapTop = mBitmapTop + deltaY;
		}
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

	@Override
	public Matrix getMatrix() {
		return mMatrix;
	}

	public RectF getClipRect() {
		return mClipRect;
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
		mLastY = 0;
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
		}
	}
	//***为了模拟ios的抬手后自动滚动一段距离而实现end
}
