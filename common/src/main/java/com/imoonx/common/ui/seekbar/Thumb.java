package com.imoonx.common.ui.seekbar;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Thumb的职责就是负责在屏幕上进行绘制操作 绘制圆
 */
public class Thumb {

    private static final float MINIMUM_TARGET_RADIUS = 50;

    private final float mTouchZone;
    private boolean mIsPressed;

    private final float mY;
    private float mX;

    private Paint mPaintNormal;
    private Paint mPaintPressed;

    private float mRadius;

    public Thumb(float x, float y, int colorNormal, int colorPressed, float radius) {

        mRadius = radius;

        mPaintNormal = new Paint();
        mPaintNormal.setColor(colorNormal);
        mPaintNormal.setAntiAlias(true);

        mPaintPressed = new Paint();
        mPaintPressed.setColor(colorPressed);
        mPaintPressed.setAntiAlias(true);

        mTouchZone = (int) Math.max(MINIMUM_TARGET_RADIUS, radius);
        mX = x;
        mY = y;
    }

    public void setX(float x) {
        mX = x;
    }

    public float getX() {
        return mX;
    }

    public boolean isPressed() {
        return mIsPressed;
    }

    public void press() {
        mIsPressed = true;
    }

    public void release() {
        mIsPressed = false;
    }

    public boolean isInTargetZone(float x, float y) {
        return Math.abs(x - mX) <= mTouchZone && Math.abs(y - mY) <= mTouchZone;
    }

    public void draw(Canvas canvas) {
        if (mIsPressed) {
            canvas.drawCircle(mX, mY, mRadius, mPaintPressed);
        } else {
            canvas.drawCircle(mX, mY, mRadius, mPaintNormal);
        }
    }

    public void destroyResources() {
        if (null != mPaintNormal) {
            mPaintNormal = null;
        }
        if (null != mPaintPressed) {
            mPaintPressed = null;
        }
    }
}