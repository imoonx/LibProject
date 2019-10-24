package com.imoonx.common.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.imoonx.common.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 水波纹特效
 * Created by fbchen2 on 2016/5/25.
 */
public class WaveView extends View {

    private float mInitialRadius;   // 初始波纹半径
    private float mMaxRadius;   // 最大波纹半径
    private float mDuration; // 一个波纹从创建到消失的持续时间
    private int mSpeed;   // 波纹的创建速度，每500ms创建一个
    private float mMaxRadiusRate;
    private boolean mMaxRadiusSet;
    private int mPaintColor;
    private Paint.Style mPaintStyle = Paint.Style.FILL;

    private Interpolator mInterpolator = new LinearInterpolator();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean mIsRunning;
    private long mLastCreateTime;
    private List<Circle> mCircleList = new ArrayList<Circle>();

    private Runnable mCreateCircle = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                newCircle();
                postDelayed(mCreateCircle, mSpeed);
            }
        }
    };

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.wave_view, defStyleAttr, 0);
        mMaxRadiusSet = a.getBoolean(R.styleable.wave_view_max_radius_set, false);
        mPaintColor = a.getColor(R.styleable.wave_view_wave_color, Color.RED);
        mInitialRadius = a.getDimensionPixelSize(R.styleable.wave_view_init_radius, 0);
        mMaxRadius = a.getDimensionPixelOffset(R.styleable.wave_view_max_radius, 0);
        mSpeed = a.getInteger(R.styleable.wave_view_speed, 500);
        mMaxRadiusRate = a.getFloat(R.styleable.wave_view_max_radius_rate, 1f);
        mDuration = a.getFloat(R.styleable.wave_view_duration, 2000);

        int integer = a.getInteger(R.styleable.wave_view_paint_type, 0);
        if (integer == 0)
            mPaintStyle = Paint.Style.FILL;
        else if (integer == 1)
            mPaintStyle = Paint.Style.FILL_AND_STROKE;
        else
            mPaintStyle = Paint.Style.STROKE;

        if (null != mPaint) {
            mPaint.setColor(mPaintColor);
            mPaint.setStyle(mPaintStyle);
        }

    }

    @TargetApi(21)
    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    public void setStyle(Paint.Style style) {
        mPaint.setStyle(style);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (!mMaxRadiusSet) {
            mMaxRadius = Math.min(w, h) * mMaxRadiusRate / 2.0f;
        }
    }


    public void setColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 开始
     */
    public void start() {
        if (!mIsRunning) {
            mIsRunning = true;
            mCreateCircle.run();
        }
    }

    /**
     * 缓慢停止
     */
    public void stop() {
        mIsRunning = false;
    }

    /**
     * 立即停止
     */
    public void stopImmediately() {
        mIsRunning = false;
        mCircleList.clear();
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        Iterator<Circle> iterator = mCircleList.iterator();
        while (iterator.hasNext()) {
            Circle circle = iterator.next();
            float radius = circle.getCurrentRadius();
            if (System.currentTimeMillis() - circle.mCreateTime < mDuration) {
                mPaint.setAlpha(circle.getAlpha());
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mPaint);
            } else {
                iterator.remove();
            }
        }
        if (mCircleList.size() > 0) {
            postInvalidateDelayed(10);
        }
    }


    private void newCircle() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastCreateTime < mSpeed) {
            return;
        }
        Circle circle = new Circle();
        mCircleList.add(circle);
        invalidate();
        mLastCreateTime = currentTime;
    }

    private class Circle {
        private long mCreateTime;

        Circle() {
            mCreateTime = System.currentTimeMillis();
        }

        int getAlpha() {
            float percent = (getCurrentRadius() - mInitialRadius) / (mMaxRadius - mInitialRadius);
            return (int) (255 - mInterpolator.getInterpolation(percent) * 255);
        }

        float getCurrentRadius() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return mInitialRadius + mInterpolator.getInterpolation(percent) * (mMaxRadius - mInitialRadius);
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }
    }


    public float getInitialRadius() {
        return mInitialRadius;
    }

    public void setInitialRadius(float initialRadius) {
        this.mInitialRadius = initialRadius;
    }

    public float getMaxRadius() {
        return mMaxRadius;
    }

    public void setMaxRadius(float maxRadius) {
        this.mMaxRadius = maxRadius;
    }

    public float getDuration() {
        return mDuration;
    }

    public void setDuration(float duration) {
        this.mDuration = duration;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    public float getMaxRadiusRate() {
        return mMaxRadiusRate;
    }

    public void setMaxRadiusRate(float maxRadiusRate) {
        this.mMaxRadiusRate = maxRadiusRate;
    }

    public boolean isMaxRadiusSet() {
        return mMaxRadiusSet;
    }

    public void setMaxRadiusSet(boolean maxRadiusSet) {
        this.mMaxRadiusSet = maxRadiusSet;
    }

    public boolean isIsRunning() {
        return mIsRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.mIsRunning = isRunning;
    }

    public long getLastCreateTime() {
        return mLastCreateTime;
    }

    public void setLastCreateTime(long lastCreateTime) {
        this.mLastCreateTime = lastCreateTime;
    }

    public List<Circle> getCircleList() {
        return mCircleList;
    }

    public void setCircleList(List<Circle> circleList) {
        this.mCircleList = circleList;
    }

    public Runnable getmCreateCircle() {
        return mCreateCircle;
    }

    public void setmCreateCircle(Runnable createCircle) {
        this.mCreateCircle = createCircle;
    }

    public Interpolator getmInterpolator() {
        return mInterpolator;
    }

    public void setmInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint paint) {
        this.mPaint = paint;
    }
}
