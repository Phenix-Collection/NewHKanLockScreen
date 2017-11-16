package com.haokan.hklockscreen.lockscreeninitset;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;


/**
 * Created by wangzixu on 2017/10/13.
 * 画雷达的扫描效果, 及雷达边缘的扩散波纹效果
 */
public class CV_ScanRadarView extends View {
    private int mViewSize; //view的尺寸

    //雷达区域
    private int mRadarRadius; //雷达圆的半径
    private long mRadarDuration = 3000; //雷达转一圈的时长
    private Paint mPaintRadar;//雷达用的画笔
    private float mDegress = 0f; //雷达的角度
    private Matrix mMatrix = new Matrix();
    private boolean mIsRadar = true; //是否开启雷达

    //波纹区域, 波纹是一个先生产, 后扩散, 两个过程
    //波纹扩散
    private int[] mColors = new int[]{0x00FFFFFF, 0x40FFFFFF};
    private float[] mPositions = new float[]{0.85f, 1.0f};
    private int mRippleRadiusStart; //波纹圆的半径起始值
    private int mRippleRadiusDistence; //波纹圆从起始点到消失点, 半径的变化值
    private ArrayList<RippleBean> mRippleBeanFactory = new ArrayList<>();
    private ArrayList<RippleBean> mRippleBeanList = new ArrayList<>();
    private Paint mPaintRipple;//波纹用的画笔
    private float mEveryMillDis;
    private float mEveryMIllAlpha;
    private long mRippleDuration = 3000; //波纹从开始扩散到消失的时长
    private boolean mIsRipple = true; //是否开启波纹

    //波纹生成, 是一个在一定时间内
    private long mRippleCreateDuration = 1500; //波纹产生的时长
    private int mRippleCreateAlpha;
    private int mRippleCreateRadius = 1;
    private RadialGradient mRippleCreatGradient;
    private int mRippleCreatCount; //波纹产生的个数

    private boolean mIsRunning;
    private long mStartTime;


    public CV_ScanRadarView(Context context) {
        this(context,null);
    }

    public CV_ScanRadarView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CV_ScanRadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint();
    }

    public void setRadarRadius(int radarRadius) {
        mRadarRadius = radarRadius;
        initRippleRidus();
    }

    private void initPaint() {
        //用来绘ripple的画笔
        mPaintRipple = new Paint(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
        mPaintRipple.setAntiAlias(true);
        mPaintRipple.setColor(Color.WHITE);
        mPaintRipple.setStyle(Paint.Style.FILL);

        //绘画渐变雷达的画笔
        mPaintRadar = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRadar.setAntiAlias(true);
        mPaintRadar.setStyle(Paint.Style.FILL);//实心圆style
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initRippleRidus() {
        mRippleRadiusStart = mRadarRadius;
        mRippleRadiusDistence = mViewSize/2 - mRippleRadiusStart;
        //每毫秒应该变化多少距离
        mEveryMillDis = ((float)mRippleRadiusDistence)/mRippleDuration;
        //每毫秒应该变化的透明度
        mEveryMIllAlpha = ((float)255)/mRippleDuration;

        //第一个扩散的波纹
        RippleBean rippleBean;
        if (mRippleBeanFactory.size() > 0) {
            rippleBean = mRippleBeanFactory.remove(0);
        } else {
            rippleBean = new RippleBean();
        }
        long currentTimeMillis = System.currentTimeMillis();
        rippleBean.radius = mRippleRadiusStart;
        rippleBean.alpha = 255;
        rippleBean.radialGradient = new RadialGradient(mViewSize /2.0f, mViewSize /2.0f, mRippleRadiusStart, mColors, mPositions, Shader.TileMode.CLAMP);
        rippleBean.previousTime = currentTimeMillis;
        rippleBean.startTime = currentTimeMillis;

        mRippleBeanList.clear();
        mRippleBeanList.add(rippleBean);

        //波纹生成用的颜色渲染器
        mRippleCreateRadius = mRippleRadiusStart;
        mRippleCreatGradient = new RadialGradient(mViewSize /2.0f, mViewSize /2.0f, mRippleCreateRadius, mColors, mPositions, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w > 0 && w != oldw) {
            //每当空间尺寸改变时, 需要重新获取控件的大小, 并设置颜色渲染器的大小
            mViewSize = getMeasuredWidth();
            mRadarRadius = mViewSize/4;
            initRippleRidus();

            //波纹生成用的颜色渲染器
            mRippleCreatGradient = new RadialGradient(mViewSize /2.0f, mViewSize /2.0f, mRadarRadius, mColors, mPositions, Shader.TileMode.CLAMP);

            //雷达用的颜色渲染器
            SweepGradient sweepGradient = new SweepGradient(mViewSize /2.0f, mViewSize /2.0f, Color.TRANSPARENT, Color.WHITE);
            //mPaintRadar设置颜色渐变渲染器
            mPaintRadar.setShader(sweepGradient);

            mMatrix.setRotate(0, mViewSize/2.0f, mViewSize/2.0f);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制波纹扩散圆环
        if (mIsRipple) {
            for (int i = 0; i < mRippleBeanList.size(); i++) {
                RippleBean rippleBean = mRippleBeanList.get(i);
                if (rippleBean.radius > 0) {
                    mPaintRipple.setShader(rippleBean.radialGradient);
                    mPaintRipple.setAlpha((int) rippleBean.alpha);
                    canvas.drawCircle(mViewSize /2.0f, mViewSize /2.0f, rippleBean.radius, mPaintRipple);
                }
            }

            //波纹产生绘制过程
            mPaintRipple.setShader(mRippleCreatGradient);
            mPaintRipple.setAlpha(mRippleCreateAlpha);
            canvas.drawCircle(mViewSize /2.0f, mViewSize /2.0f, mRippleCreateRadius, mPaintRipple);
        }

        if (mIsRadar) {
            //把画布的多有对象与matrix联系起来
            if(mMatrix != null){
                canvas.concat(mMatrix);
            }
            //绘制颜色渐变圆
            canvas.drawCircle(mViewSize /2.0f, mViewSize /2.0f, mRadarRadius, mPaintRadar);
        }

        super.onDraw(canvas);
    }

    //线程开启
    public void start(){
        mIsRunning = true;
        mStartTime = System.currentTimeMillis();
        initRippleRidus();
        invalidate();
    }

    //线程结束
    public void stop(){
        mIsRunning = false;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mIsRunning) {
            long currentTimeMillis = System.currentTimeMillis();
            long deltaTime = currentTimeMillis - mStartTime;
//            LogHelper.d("wangzixu", "deltaTime deltaTime%mRadarDuration = " + deltaTime + ", " + deltaTime%mRadarDuration);

            //雷达区域计算
            {
                float rate = (deltaTime % mRadarDuration) / (float)mRadarDuration; //从开始到现在, 经过的比率
                mDegress = rate * 360; //在一定时间内360度, 所以可根据时间算出当前应该转了多少度
                mMatrix.setRotate(mDegress, mViewSize/2.0f, mViewSize/2.0f);
            }

            //波纹产生计算, 波纹生成, 是一个在一定时间内, 透明度从0-255的过程
            {
                int creatCount = (int)(deltaTime/mRippleCreateDuration);
                float rate = (deltaTime%mRippleCreateDuration) / (float)mRippleCreateDuration; //从开始到现在, 经过的比率
                mRippleCreateAlpha = (int) (rate * 255);
//                mRippleCreateAlpha = 255;

                if (creatCount != mRippleCreatCount) { //生成一个圈

                    mRippleCreatCount = creatCount;

                    RippleBean rippleBean;
                    if (mRippleBeanFactory.size() > 0) {
                        rippleBean = mRippleBeanFactory.remove(0);
                    } else {
                        rippleBean = new RippleBean();
                    }

                    rippleBean.radius = mRippleRadiusStart;
                    rippleBean.alpha = 255;
                    rippleBean.radialGradient = new RadialGradient(mViewSize /2.0f, mViewSize /2.0f, rippleBean.radius, mColors, mPositions, Shader.TileMode.CLAMP);
                    rippleBean.previousTime = currentTimeMillis;
                    rippleBean.startTime = currentTimeMillis;
                    mRippleBeanList.add(rippleBean);

//                    LogHelper.d("wangzixu", "mRippleBeanList 生成一个圈" + mRippleBeanList.size());
                }
            }


            //波纹扩散计算
            {
                boolean hasRemove = false;
                for (int i = 0; i < mRippleBeanList.size(); i++) {
                    RippleBean rippleBean = mRippleBeanList.get(i);
                    if (currentTimeMillis - rippleBean.startTime >= mRippleDuration) {
                        hasRemove = true;
                        rippleBean.radius = 0;
                        mRippleBeanFactory.add(rippleBean);
//                        LogHelper.d("wangzixu", "mRippleBeanList 移除一个圈");
                    } else {
                        long passTime = currentTimeMillis - rippleBean.previousTime;
                        if (passTime != 0) {
                            rippleBean.radius = (int) (rippleBean.radius + mEveryMillDis*passTime);
                            rippleBean.alpha = rippleBean.alpha - mEveryMIllAlpha*passTime;
                            rippleBean.radialGradient = new RadialGradient(mViewSize /2.0f, mViewSize /2.0f, rippleBean.radius, mColors, mPositions, Shader.TileMode.CLAMP);
                            rippleBean.previousTime = currentTimeMillis;
                        }
                    }
                }

                if (hasRemove) {
                    mRippleBeanList.removeAll(mRippleBeanFactory);
                }
            }

            invalidate();
        }
    }

    public void setRadar(boolean radar) {
        mIsRadar = radar;
    }

    public void setRipple(boolean ripple) {
        mIsRipple = ripple;
    }

    class RippleBean {
        public int radius;
        public float alpha;
        public long previousTime;
        public long startTime;
        public RadialGradient radialGradient;
    }
}
