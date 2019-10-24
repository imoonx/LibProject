package com.imoonx.common.ui.gesturelock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.imoonx.common.R;
import com.imoonx.util.XLog;

import java.util.ArrayList;
import java.util.List;

import static com.imoonx.common.ui.gesturelock.GestureLockView.Mode.STATUS_FINGER_ON;
import static com.imoonx.common.ui.gesturelock.GestureLockView.Mode.STATUS_NO_FINGER;

/**
 * 顶部指示器
 * 整体包含n*n个GestureLockView,每个GestureLockView间间隔mMarginBetweenLockView，
 * 最外层的GestureLockView与容器存在mMarginBetweenLockView的外边距
 * <p/>
 * 关于GestureLockView的边长（n*n）： n * mGestureLockViewWidth + ( n + 1 ) *
 * mMarginBetweenLockView = mWidth ; 得：mGestureLockViewWidth = 4 * mWidth / ( 5
 * * mCount + 1 ) 注：mMarginBetweenLockView = mGestureLockViewWidth * 0.25 ;
 */
public class GestureLockViewIndicator extends RelativeLayout {

    private static final String TAG = "GestureLockViewGroup";
    /**
     * 保存所有的GestureLockView
     */
    private GestureLockView[] mGestureLockViews;
    /**
     * 每个边上的GestureLockView的个数
     */
    private int mCount = 3;
    /**
     * 保存用户选中的GestureLockView的id
     */

    private Paint mPaint;
    /**
     * 每个GestureLockView中间的间距 设置为：mGestureLockViewWidth * 25%
     */
    private int mMarginBetweenLockView = 30;
    /**
     * GestureLockView的边长 4 * mWidth / ( 5 * mCount + 1 )
     */
    private int mGestureLockViewWidth;

    /**
     * GestureLockView无手指触摸的状态下圆的颜色
     */
    private int mNoFingerColor = 0xFFBDBDBD;

    /**
     * GestureLockView手指触摸的状态下圆的颜色
     */
    private int mFingerOnColor = 0XFFEC159F;
    /**
     * GestureLockView手指抬起的状态下,正确时圆的颜色
     */
    private int mFingerUpColorCorrect = 0xFF91DC5A;

    /**
     * GestureLockView手指抬起的状态下，错误圆的颜色
     */
    private int mFingerUpColorError = 0xFFFF0000;
    /**
     * 宽度
     */
    private int mWidth;
    /**
     * 高度
     */
    private int mHeight;

    private Path mPath;
    private int mPrferenceId = -1;
    private float mDefaultRete = 0.3f;
    private float mInnerCircleRadiusRate;
    private boolean mIsNeedArrow;
    private List<Integer> lockPassStr = new ArrayList<>();

    public GestureLockViewIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLockViewIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        /**
         * 获得所有自定义的参数的值
         */
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GestureLockView, defStyle, 0);

        mNoFingerColor = a.getColor(R.styleable.GestureLockView_color_no_finger, mNoFingerColor);
        mFingerOnColor = a.getColor(R.styleable.GestureLockView_color_finger_on, mFingerOnColor);
        mFingerUpColorCorrect = a.getColor(R.styleable.GestureLockView_color_finger_up_correct, mFingerUpColorCorrect);
        mFingerUpColorError = a.getColor(R.styleable.GestureLockView_color_finger_up_error, mFingerUpColorError);
        mCount = a.getInt(R.styleable.GestureLockView_count, mCount);
        mPrferenceId = a.getInt(R.styleable.GestureLockView_preference_id, mPrferenceId);
        mInnerCircleRadiusRate = a.getFloat(R.styleable.GestureLockView_inner_circler_adius_rate, mDefaultRete);
        mIsNeedArrow = a.getBoolean(R.styleable.GestureLockView_is_need_arrow, false);
        a.recycle();

        setWillNotDraw(false);
        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath = new Path();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mHeight = mWidth = mWidth < mHeight ? mWidth : mHeight;
        XLog.i(this.getClass(), "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        XLog.i(this.getClass(), "onLayout");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        XLog.i(this.getClass(), "onDraw");
        XLog.i(this.getClass(), "lockPassStr=" + lockPassStr);
        initViews();
    }

    private boolean isNeedInit = true;

    private void initViews() {
        // 初始化mGestureLockViews
        XLog.i(this.getClass(), "initViews");
        if (mGestureLockViews == null) {
            mGestureLockViews = new GestureLockView[mCount * mCount];
        }
        if (isNeedInit) {
            // 计算每个GestureLockView的宽度
            isNeedInit = false;
            mGestureLockViewWidth = (int) (4 * mWidth * 1.0f / (5 * mCount + 1));
            //计算每个GestureLockView的间距
            mMarginBetweenLockView = (int) (mGestureLockViewWidth * 0.25);
            // 设置画笔的宽度为GestureLockView的内圆直径稍微小点
            mPaint.setStrokeWidth(mGestureLockViewWidth * 0.05f);

            for (int i = 0; i < mGestureLockViews.length; i++) {
                //初始化每个GestureLockView
                mGestureLockViews[i] = new GestureLockView(getContext(), mNoFingerColor, mFingerOnColor, mFingerUpColorCorrect, mFingerUpColorError, mIsNeedArrow, mInnerCircleRadiusRate);
                mGestureLockViews[i].setId(i + 1);
                //设置参数，主要是定位GestureLockView间的位置
                LayoutParams lockerParams = new LayoutParams(mGestureLockViewWidth, mGestureLockViewWidth);

                // 不是每行的第一个，则设置位置为前一个的右边
                if (i % mCount != 0) {
                    lockerParams.addRule(RelativeLayout.RIGHT_OF, mGestureLockViews[i - 1].getId());
                }
                // 从第二行开始，设置为上一行同一位置View的下面
                if (i > mCount - 1) {
                    lockerParams.addRule(RelativeLayout.BELOW, mGestureLockViews[i - mCount].getId());
                }
                //设置右下左上的边距
                int rightMargin = 0;
                int bottomMargin = mMarginBetweenLockView;
                int leftMagin = mMarginBetweenLockView;
                int topMargin = 0;
                /**
                 * 每个View都有右外边距和底外边距 第一行的有上外边距 第一列的有左外边距
                 */
//                if (i >= 0 && i < mCount) {// 第一行
//                    topMargin = mMarginBetweenLockView;
//                }
//                if (i % mCount == 0) {// 第一列
//                    leftMagin = mMarginBetweenLockView;
//                }
                if (i % mCount == 0) {// 第一列
                    leftMagin = 0;
                }
                if (i >= (mCount - 1) * mCount) {//最后一行
                    bottomMargin = 0;
                }
                lockerParams.setMargins(leftMagin, topMargin, rightMargin, bottomMargin);
                if (lockPassStr.contains(i + 1)) {
                    mGestureLockViews[i].setMode(STATUS_FINGER_ON);
                    XLog.i(this.getClass(), "包含");
                } else {
                    mGestureLockViews[i].setMode(STATUS_NO_FINGER);
                    XLog.i(this.getClass(), "不包含");
                }
                addView(mGestureLockViews[i], lockerParams);

            }
        }

    }

    /**
     * 做一些必要的重置
     */

    private void reset() {
        lockPassStr.clear();
        mPath.reset();
        for (GestureLockView gestureLockView : mGestureLockViews) {
            gestureLockView.setMode(STATUS_NO_FINGER);
            gestureLockView.setArrowDegree(-1);
        }
    }

    public void setPath(List<Integer> paramString) {
        lockPassStr.addAll(paramString);
        isNeedInit = true;
        invalidate();
    }

    public void resetView() {
        reset();
        invalidate();
    }
}
