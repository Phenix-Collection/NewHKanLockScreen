package com.haokan.hklockscreen.recommendpagedetail;

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
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.haokan.pubic.clipimage.MyScroller;


public class Zoomimageview_new extends AppCompatImageView {
	public static final int TYPE = 1;
	/**
	 * 是否把事件处理委托给其他view
	 */
	public boolean mEntrustParent = true;

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

//	private float mMatrixX, mMatrixY;


	private float mOldDistant;
	private float mCurrentDistant;
	private PointF mCenter = new PointF();

	/**
	 * -1默认, 1代表拖动, 2代表缩放, 3代表缩放动画自动执行中
	 */
	private int mImageMode = -1;

	private float[] mTmpArray = new float[9];
	private static TimeInterpolator sInterpolator = new DecelerateInterpolator();
	private AnimatorUpdateListener zoomBacklistener = null;

    //x，y方向的剪裁框内的冗余量，用剪裁框的宽度减去当前图片的宽度，负数说明图片大于剪裁框
	private float mRedundantXSpace, mRedundantYSpace;
	private boolean mAlreadyLoadBigBmp = false;
	private boolean mIsOnLeftSide, mIsOnRightSide, mIsOnTopSide, mIsOnBottomSide;
	private int mPointCount;
	private Bitmap mBitmap = null;

//    /**
//     * 图片初次显示时，要填满的区域（大部分情况其实是填满屏幕，但有时不是，
//	 * 比如截取图片用来做锁屏图片，很多时候是在屏幕内有个模拟手机样式的剪裁框来铺满图片，
//	 * 所以不能和屏幕边缘等同），比如系统默认的就是此view的宽高减去padding值，
//     * 而我们是自己用matrix填充的bitmap，所以要有一个填充图片的区域
//     */
//    private RectF mFirstFillRect;
//
//    /**
//	 * 图片允许的最小方框，比如在图片拖动时，图片边缘不能进入的区域，图片缩小后自动回弹时，
//	 * 要回弹的那个区域，（ps:这个最小区域，很多时候会和mFirstFillRect区域重合，但有时不同，
//	 * 如用来截取头像功能时，初始时会填满屏幕，在屏幕中最有一个小一些的截图框，所以这个框也可以
//	 * 用来获取截图区域，获取截取的图像）
//     */
//    private RectF mMinLimitRect;

	private RectF mRect;

//    /**
//     * 是否支持剪裁功能
//     */
//    private boolean mHasClip = false;

    /**
     * 判断用户是否开始滑动的，手指防抖裕量
     */
    private int mTouchSlop;
	public boolean mIsFirstMove; //有静到拖动
	private boolean mIsFirstScale; //由静止到开始双指缩放
	private float mPerformDownX;
	private float mPerformDownY;
	private long mPerformDownTime;
	private VelocityTracker mVelocityTracker;

	public Zoomimageview_new(Context context) {
		this(context, null);
	}

	public Zoomimageview_new(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Zoomimageview_new(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZoomImageView, defStyleAttr, 0);
//        mScaleType = a.getInt(R.styleable.ZoomImageView_ziv_scaleType, 2);
//        a.recycle();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
	}


	/**
	 * 设置初始填充框
     */
    public void setFirstFillRect(RectF edgeRect) {
        mRect = edgeRect;
        calcBitmapWH();
    }

    /**
     * 自己实现初始时图片怎么显示，这里是用matrix实现的center_crop的效果，
     * 而且可以实现以任意的框来center_crop, 系统的scaleType除了fitXy，其他
     * 的也是这样用matrix变换的，参考系统源码，imageview中的configureBounds()方法
     */
	private void calcBitmapWH() {
		if (mWidth == 0 || mHeight == 0 || mOriBmpWidth == 0
				|| mOriBmpHeight == 0) {
			return;
		}

        //模拟系统的形式来处理图片，参考系统源码，imageview中的configureBounds()方法

		if (TYPE == 0) {
			mRect = new RectF(0, 0 , mWidth, mHeight);

			float scaleX = mRect.width() / mOriBmpWidth;
			float scaleY = mRect.height() / mOriBmpHeight;

			mCurrentScale = mZoomMinScale = Math.max(scaleX, scaleY);
			mZommMaxScale = 5.0f;
			calcRedundantSpace();

			mBitmapLeft = -mRedundantXSpace*0.5f;
			mBitmapTop = -mRedundantYSpace*0.5f;

			mMatrix.setScale(mCurrentScale, mCurrentScale, 0.5f, 0.5f);
			mMatrix.postTranslate(Math.round(mBitmapLeft), Math.round(mBitmapTop));
			setImageMatrix(mMatrix);

			checkIsOnSide();
		} else if (TYPE == 1) {
			//用来在viewpager中显示类似新闻图片浏览的形态, 留白的填充方式,
			//此种mRect代表屏幕, 图片要在这个方框内显示, 初始显示成留白的方式, 也是最小的缩放系数, 再小要返回,
			//最大可以显示成窄边充满的方式, 也是最大的缩放系数, 再大也要返回, 有时候还要双击切换着两种显示形态
			mRect = new RectF(0, 0 , mWidth, mHeight);

			float scaleX = mRect.width() / mOriBmpWidth;
			float scaleY = mRect.height() / mOriBmpHeight;

			mCurrentScale = mZoomMinScale = Math.min(scaleX, scaleY);
			mZommMaxScale = Math.max(scaleX, scaleY);
			calcRedundantSpace();

			mBitmapLeft = -mRedundantXSpace*0.5f;
			mBitmapTop = -mRedundantYSpace*0.5f;

			mMatrix.setScale(mCurrentScale, mCurrentScale);
			mMatrix.postTranslate(mBitmapLeft, mBitmapTop);
			setImageMatrix(mMatrix);

			checkIsOnSide();
		} else if (TYPE == 2) {
			//TODO: 2017/6/28  用来截取头像, 留白的填充方式, 屏幕中间有应该个框框用来截取头像, 这个头像框是最小框,滑动和缩放均不能进入这个最小框
//			calcRedundantSpace();
//			float Xoffset = mCurrentBmpWidth - mFirstFillRect.width();
//			float Yoffset = mCurrentBmpHeight - mFirstFillRect.height();
//			dx = mFirstFillRect.left - Xoffset / 2.0f;
//			dy = mFirstFillRect.top - Yoffset / 2.0f;
//
//			mMatrix.setScale(mCurrentScale, mCurrentScale);
//			mMatrix.postTranslate(Math.round(dx), Math.round(dy));
//			setImageMatrix(mMatrix);
//			checkIsOnSide();
		} else if (TYPE == 3) {
			//TODO: 2017/6/28  用来截取锁屏图片, 刚上来填满截取的方式,  充满的图框即是最小的框框, 用这个刚上来充满的框截取锁屏图片,滑动和缩放均不能进入这个最小框

//			calcRedundantSpace();
//			float Xoffset = mCurrentBmpWidth - mFirstFillRect.width();
//			float Yoffset = mCurrentBmpHeight - mFirstFillRect.height();
//			dx = mFirstFillRect.left - Xoffset / 2.0f;
//			dy = mFirstFillRect.top - Yoffset / 2.0f;
//
//			mMatrix.setScale(mCurrentScale, mCurrentScale);
//			mMatrix.postTranslate(Math.round(dx), Math.round(dy));
//			setImageMatrix(mMatrix);
//			checkIsOnSide();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		LogHelper.d("zoomimageview", "onSizeChanged = " + ", this = " + this);
		mWidth = getWidth();
		mHeight = getHeight();
//		calcBitmapWH();
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
		if (mEntrustParent) {
			return false;
		} else {
			processEvent(event);
			return true;
		}
	}

	public boolean processEvent(MotionEvent event) {
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
					stopFling();
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
						if ((Math.abs(x) > mTouchSlop) || Math.abs(y) > mTouchSlop) {
							mStartClick = false;

							mIsFirstMove = true;
							boolean handle = checkAndSetTranslate(x, y);
							if (handle) {
								mImageMode = 1;
							}
						}
					} else if (mImageMode == 1) { //图片正在滑动
						checkAndSetTranslate(deltaX, deltaY);
					} else if (mImageMode == 2 && mPointCount == 2) {
						processZoomEvent(event);
					}
					mVelocityTracker.addMovement(event);
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					mPointCount++;
					if (mPointCount > 2) {
						break;
					}
					boolean isZoomImage = checkCanZoom(event);
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
						processZoomBack(event);
					}
					break;
				case MotionEvent.ACTION_UP:
					float absX = Math.abs(event.getX() - mPerformDownX);
					float absY = Math.abs(event.getY() - mPerformDownY);
					if (mStartClick && absX <= mTouchSlop && absY <= mTouchSlop) {
						long uptimeMillis = SystemClock.uptimeMillis() - mPerformDownTime;
						if (uptimeMillis >= 600) {
							if (mOnLongClikListener != null) {
								mOnLongClikListener.onLongClick(Zoomimageview_new.this);
							}
						} else if (uptimeMillis <= 300) {
							if (mOnClikListener != null) {
								mOnClikListener.onClick(Zoomimageview_new.this);
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
						flingDistence((int) dx, (int) dy);
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
		return true;
	}

	public void processZoomBack(MotionEvent event) {
		if (mCurrentScale < mZoomMinScale) {
			startScaleAnim(mCurrentScale, mZoomMinScale);
		} else if (mCurrentScale > mZommMaxScale) {
			startScaleAnim(mCurrentScale, mZommMaxScale);
		} else {
			int pointerIndex = event.getActionIndex();
			mLastX = event.getX(1 - pointerIndex);
			mLastY = event.getY(1 - pointerIndex);
			mImageMode = 1;
		}
	}

	public void doubleClickZoom(MotionEvent event) {
		if (mCurrentScale < mZommMaxScale) {
			mCenter.set(event.getX(), event.getY());
			startScaleAnim(mCurrentScale, mZommMaxScale);
		} else if (mCurrentScale > mZoomMinScale) {
			mCenter.set(mWidth*0.5f, mHeight*0.5f);
			startScaleAnim(mCurrentScale, mZoomMinScale);
		}
	}

	public void processZoomEvent(MotionEvent event) {
		mCurrentDistant = getDistance(event);
		float scaleFactor = mCurrentDistant / mOldDistant;
		float deltaScale = Math.abs(scaleFactor - 1.0f);
		if (deltaScale < 0.001) {
			return;
		}

		if (mIsFirstScale) { //初次开始动，总有个突兀的跳变，所以消除掉第一次，防抖
			mIsFirstScale = false;
			mOldDistant = mCurrentDistant;
			return;
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

	public boolean checkCanZoom(MotionEvent event) {
		mOldDistant = getDistance(event);
		return mOldDistant > mTouchSlop * 2;
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
				mImageMode = -1;
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

	public float getDistance(MotionEvent event) {
		float x = event.getX(1) - event.getX(0);
		float y = event.getY(1) - event.getY(0);
        return (float) Math.sqrt((x * x + y * y));
	}

	private PointF getCenter(PointF centerF, MotionEvent event) {
		float x = (event.getX(1) + event.getX(0))*0.5f;
		float y = (event.getY(1) + event.getY(0))*0.5f;
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
        mRedundantXSpace =  mCurrentBmpWidth - mRect.width();
        mRedundantYSpace = mCurrentBmpHeight - mRect.height();
    }

	public boolean checkAndSetTranslate(float deltaX, float deltaY) {
		if (mCurrentScale <= mZoomMinScale) {
			return false;
		}

		if (mRedundantXSpace <= 0) {
			float offset = mRedundantXSpace*0.5f;
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
			float offset = mRedundantYSpace*0.5f;
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
			if (mIsFirstMove) { //第一次，由静到动，移动距离应该大于touchslop,防抖
				mIsFirstMove = false;
				return true;
			}

			mMatrix.postTranslate(deltaX, deltaY);
			setImageMatrix(mMatrix);
			mBitmapLeft = mBitmapLeft + deltaX;
			mBitmapTop = mBitmapTop + deltaY;
            checkIsOnSide();
			return true;
		} else {
			return false;
		}
	}

    private void checkIsOnSide() {
        mIsOnLeftSide = false;
        mIsOnRightSide = false;
        mIsOnTopSide = false;
        mIsOnBottomSide = false;

		if (TYPE == 0) {
			if (mCurrentScale <= mZoomMinScale) {
				mIsOnLeftSide = true;
				mIsOnRightSide = true;
				mIsOnTopSide = true;
				mIsOnBottomSide = true;
				return;
			}
		}

		if (mBitmapLeft >= mRect.left) {
			mIsOnLeftSide = true;
		}
		if (mBitmapLeft + mCurrentBmpWidth <= mRect.right) {
			mIsOnRightSide = true;
		}
		if (mBitmapTop >= mRect.top) {
			mIsOnTopSide = true;
		}
		if (mBitmapTop + mCurrentBmpHeight <= mRect.bottom) {
			mIsOnBottomSide = true;
		}

//        getMatrixXY(mMatrix);
//        if (Math.abs(mMatrixX - mMinLimitRect.left) <= 3) {
//            mIsOnLeftSide = true;
//        }
//        if (Math.abs(mMatrixX + mRedundantXSpace - mMinLimitRect.left) <= 3) {
//            mIsOnRightSide = true;
//        }
//        if (Math.abs(mMatrixY - mMinLimitRect.top) <= 3) {
//            mIsOnTopSide = true;
//        }
//        if (Math.abs(mMatrixY + mRedundantYSpace - mMinLimitRect.top) <= 3) {
//            mIsOnBottomSide = true;
//        }
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

	public boolean canHorizontalDrag() {
		if (mCurrentScale > mZoomMinScale && mRedundantXSpace > 0) {
			return true;
		}
		return false;
	}

    public boolean canVerticalDrag() {
        if (mCurrentScale > mZoomMinScale && mRedundantYSpace > 0) {
            return true;
        }
        return false;
    }

	public boolean canDrag() {
		if (mCurrentScale > mZoomMinScale) {
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
		mScroller.startScroll((int)mLastX, (int)mLastY, dx, dy);
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

//			LogHelper.d("zoomview", "currentX, deltaX = " + currentX + ", " +deltaX);
			checkAndSetTranslate(deltaX, deltaY);
//			if ((mIsOnLeftSide && deltaX > 0)
//					|| (mIsOnRightSide && deltaX < 0)) {
//				mScroller.forceFinished(true);
//			}
		}
	}
	//***为了模拟ios的抬手后自动滚动一段距离而实现end

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
}
