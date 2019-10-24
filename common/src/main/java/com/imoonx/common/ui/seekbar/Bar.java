package com.imoonx.common.ui.seekbar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

/**
 * 绘制刻度尺线
 */
public class Bar {

    private String[] mTexts;
    private int[] mTextSizes;
    private Paint mBarPaint;
    private Paint mTextPaint;

    private float mLeftX;
    private float mRightX;
    private float mY;
    private float mPadding;

    private int mSegments;
    private float mTickDistance;
    private float mTickStartY;
    private float mTickEndY;

    public Bar(float x, float y, float width, int tickCount, float tickHeight,
               float barWidth, int barColor, int textColor, int[] textSizes, float padding, String[] texts) {

        mLeftX = x;
        mRightX = x + width;
        mY = y;
        mPadding = padding;

        mTextSizes = textSizes;
        mTexts = texts;

        mSegments = tickCount - 1;
        mTickDistance = width / mSegments;
        float mTickHeight = tickHeight;
        mTickStartY = mY - mTickHeight / 2f;
        mTickEndY = mY + mTickHeight / 2f;

        mBarPaint = new Paint();
        mBarPaint.setColor(barColor);
        mBarPaint.setStrokeWidth(barWidth);
        mBarPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setAntiAlias(true);
    }

    public void draw(Canvas canvas) {
        drawLine(canvas);
        drawTicks(canvas);
    }

    public float getLeftX() {
        return mLeftX;
    }

    public float getRightX() {
        return mRightX;
    }

    public float getNearestTickCoordinate(Thumb thumb) {
        int nearestTickIndex = getNearestTickIndex(thumb);
        return mLeftX + (nearestTickIndex * mTickDistance);
    }

    public int getNearestTickIndex(Thumb thumb) {
        return getNearestTickIndex(thumb.getX());
    }

    public int getNearestTickIndex(float x) {
        return (int) ((x - mLeftX + mTickDistance / 2f) / mTickDistance);
    }

    private void drawLine(Canvas canvas) {
        canvas.drawLine(mLeftX, mY, mRightX, mY, mBarPaint);
    }

    private void drawTicks(Canvas canvas) {
        for (int i = 0; i <= mSegments; i++) {
            float x = i * mTickDistance + mLeftX;
            canvas.drawLine(x, mTickStartY, x, mTickEndY, mBarPaint);
            if (!TextUtils.isEmpty(mTexts[i])) {
                mTextPaint.setTextSize(mTextSizes[i]);
                if (i == mTextSizes.length - 1)
                    canvas.drawText(mTexts[i], x - getTextWidth(mTexts[i]) * 2 / 3, mTickStartY - mPadding, mTextPaint);
                else
                    canvas.drawText(mTexts[i], x - getTextWidth(mTexts[i]) / 2, mTickStartY - mPadding, mTextPaint);
            }
        }
    }

    private float getTextWidth(String text) {
        return mTextPaint.measureText(text);
    }

    public void destroyResources() {
        if (null != mBarPaint) {
            mBarPaint = null;
        }
        if (null != mTextPaint) {
            mTextPaint = null;
        }
    }
}