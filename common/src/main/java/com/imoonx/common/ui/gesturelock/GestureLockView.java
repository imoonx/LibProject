package com.imoonx.common.ui.gesturelock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.View;

import com.imoonx.util.XLog;

import static com.imoonx.common.ui.gesturelock.GestureLockView.Mode.STATUS_NO_FINGER;

public class GestureLockView extends View {

    private final boolean mIsNeedArrow;

    /**
     * GestureLockView的三种状态
     */
    enum Mode {
        STATUS_NO_FINGER, STATUS_FINGER_ON, STATUS_FINGER_UP;
    }

    private Mode mCurrentStatus = STATUS_NO_FINGER;
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private int mStrokeWidth = 2;

    private int mCenterX;
    private int mCenterY;
    private Paint mPaint;

    /**
     * 箭头（小三角最长边的一半长度 = mArrawRate * mWidth / 2 ）
     */
    private float mArrowRate = 0.2f;
    private int mArrowDegree = -1;
    private Path mArrowPath;
    /**
     * 内圆的半径 = mInnerCircleRadiusRate * mRadus
     */
    private float mInnerCircleRadiusRate;

    /**
     * 四个颜色，可由用户自定义，初始化时由GestureLockViewGroup传入
     */
    private int mColorNoFinger;
    private int mColorFingerOn;
    private int mColorFingerUpCorrect;
    private int mColorFingerUpError;

    public GestureLockView(Context context, int colorNoFingerr, int colorFingerOn, int colorCorrect, int colorError, boolean isNeedArrow, float innerCircleRadiusRate) {
        super(context);
        XLog.i(this.getClass(), "走不走进来，为什么不显示");
        this.mColorNoFinger = colorNoFingerr;
        this.mColorFingerOn = colorFingerOn;
        this.mColorFingerUpCorrect = colorCorrect;
        this.mColorFingerUpError = colorError;
        this.mIsNeedArrow = isNeedArrow;
        this.mInnerCircleRadiusRate = innerCircleRadiusRate;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (mIsNeedArrow)
            mArrowPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        XLog.i(this.getClass(), "onMeasure");
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 取长和宽中的小值
        mWidth = mWidth < mHeight ? mWidth : mHeight;
        mRadius = mCenterX = mCenterY = mWidth / 2;
        mRadius -= mStrokeWidth / 2;

        setMeasuredDimension(mWidth, mWidth);

//        绘制三角形，初始时是个默认箭头朝上的一个等腰三角形，用户绘制结束后，根据由两个GestureLockView决定需要旋转多少度
        if (mIsNeedArrow) {
            float mArrowLength = mWidth / 2 * mArrowRate;
            XLog.i(this.getClass(), "mWidth=" + mWidth);
            mArrowPath.moveTo(mWidth / 2, mStrokeWidth);
            mArrowPath.lineTo(mWidth / 2 - mArrowLength, mStrokeWidth + mArrowLength);
            mArrowPath.lineTo(mWidth / 2 + mArrowLength, mStrokeWidth + mArrowLength);
            mArrowPath.close();
            mArrowPath.setFillType(Path.FillType.WINDING);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        XLog.i(this.getClass(), "onDraw");
        switch (mCurrentStatus) {
            case STATUS_FINGER_ON:
                // 绘制外圆
                mPaint.setStyle(Style.FILL);
                mPaint.setColor(mColorFingerOn);
                mPaint.setAlpha(30);
//                mPaint.setStrokeWidth(2);
                canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
                // 绘制内圆
//                mPaint.setStyle(Style.FILL);
                mPaint.setAlpha(255);
                canvas.drawCircle(mCenterX, mCenterY, mRadius * mInnerCircleRadiusRate, mPaint);
                break;
            case STATUS_FINGER_UP:
                // 绘制外圆
                if (GestureLockViewGroup.isCorrect)
                    mPaint.setColor(mColorFingerUpCorrect);
                else
                    mPaint.setColor(mColorFingerUpError);
                mPaint.setAlpha(30);
//                mPaint.setStyle(Style.STROKE);
//                mPaint.setStrokeWidth(2);
                canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
                // 绘制内圆
//                mPaint.setStyle(Style.FILL);
                mPaint.setAlpha(255);
                canvas.drawCircle(mCenterX, mCenterY, mRadius * mInnerCircleRadiusRate, mPaint);
                if (mIsNeedArrow)
                    drawArrow(canvas);
                break;
            case STATUS_NO_FINGER:
                // 绘制外圆
//                mPaint.setStyle(Style.STROKE);
//                mPaint.setColor(mColorNoFinger);
//                canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
                // 绘制内圆
                mPaint.setStyle(Style.FILL);
                mPaint.setColor(mColorNoFinger);
                canvas.drawCircle(mCenterX, mCenterY, mRadius * mInnerCircleRadiusRate, mPaint);
                break;
        }
    }

    /**
     * 绘制箭头
     *
     * @param canvas
     */
    private void drawArrow(Canvas canvas) {
        if (mArrowDegree != -1) {
            mPaint.setStyle(Style.FILL);
            canvas.save();
            canvas.rotate(mArrowDegree, mCenterX, mCenterY);
            canvas.drawPath(mArrowPath, mPaint);
            canvas.restore();
        }
    }

    /**
     * 设置当前模式并重绘界面
     *
     * @param mode
     */
    public void setMode(Mode mode) {
        this.mCurrentStatus = mode;
        invalidate();
    }

    public void setArrowDegree(int degree) {
        this.mArrowDegree = degree;
    }

}
