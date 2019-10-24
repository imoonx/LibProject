package com.imoonx.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.imoonx.common.R;
import com.imoonx.util.Res;

/**
 * 圆环图表
 * <p>
 * Created by 36238 on 2018/2/8.
 */

public class CircularChartView extends View {

    private int mViewWidth; //view的宽
    private int mViewHeight;    //view的高
    private int mViewCenterX;   //view宽的中心点
    private int mViewCenterY;   //view高的中心点
    /**
     * 完整圆颜色
     */
    private int mDownColor;
    /**
     * 彩色圆颜色
     */
    private int mUpColor;
    /**
     * 圆环宽度
     */
    private float mCircularWidth;
    /**
     * 圆环半径
     */
    private float mCircularRadius;
    /**
     * 绘制有颜色圆起始位置
     */
    private float mStartPosition;

    /**
     * 圆环的矩形区域
     */
    private RectF mRectF;
    /**
     * 绘制的角度
     */
    private int mSelectRing = 0;
    /**
     * 画笔
     */
    private Paint mCircularChartPaint;
    private Paint mPaint;
    private Paint mNormalPaint;

    public CircularChartView(Context context) {
        this(context, null);
    }

    public CircularChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularChartView);

        mDownColor = a.getColor(R.styleable.CircularChartView_circularchar_down_color, Color.parseColor("#d1d1d1"));
        mUpColor = a.getColor(R.styleable.CircularChartView_circularchar_up_color, Color.parseColor("#ff0000"));
        mCircularWidth = a.getDimension(R.styleable.CircularChartView_circularchar_width, Res.getDimens(R.dimen.space_10));
        mCircularRadius = a.getDimension(R.styleable.CircularChartView_circularchar_radius, Res.getDimens(R.dimen.space_5));
        mStartPosition = a.getFloat(R.styleable.CircularChartView_circularchar_start_position, 0);

        a.recycle();

        this.setWillNotDraw(false);
//        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setAntiAlias(true);
        mNormalPaint = new Paint();
        mNormalPaint.setStyle(Paint.Style.STROKE);
        mNormalPaint.setStrokeWidth(mCircularWidth);
        mNormalPaint.setColor(mDownColor);

        mCircularChartPaint = new Paint();
        mCircularChartPaint.setStyle(Paint.Style.STROKE);
        mCircularChartPaint.setStrokeWidth(mCircularWidth);
        mCircularChartPaint.setColor(mUpColor);
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        mViewCenterX = mViewWidth / 2;
        mViewCenterY = mViewHeight / 2;
        mRectF = new RectF(mViewCenterX - mCircularRadius - mCircularWidth / 2, mViewCenterY - mCircularRadius - mCircularWidth / 2,
                mViewCenterX + mCircularRadius + mCircularWidth / 2, mViewCenterY + mCircularRadius + mCircularWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画默认圆环
        drawNormalRing(canvas);
        //画彩色圆环
        drawColorRing(canvas);
    }

    /**
     * 画彩色圆环
     *
     * @param canvas
     */
    private void drawColorRing(Canvas canvas) {
        canvas.drawArc(mRectF, mStartPosition, mSelectRing, false, mCircularChartPaint);
    }

    /**
     * 画默认圆环
     *
     * @param canvas
     */
    private void drawNormalRing(Canvas canvas) {
        canvas.drawArc(mRectF, 0, 360, false, mNormalPaint);
    }

    /**
     * 设置绘制角度
     *
     * @param i 角度
     */
    public void setSelect(int i) {
        this.mSelectRing = i;
        this.invalidate();
    }
}
