package com.imoonx.common.ui.gesturelock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.imoonx.common.R;
import com.imoonx.common.ui.gesturelock.listener.GestureEventListener;
import com.imoonx.common.ui.gesturelock.listener.GesturePasswordSettingListener;
import com.imoonx.util.Res;
import com.imoonx.util.XLog;

import java.util.ArrayList;
import java.util.List;

import static com.imoonx.common.ui.gesturelock.GestureLockView.Mode.STATUS_FINGER_ON;
import static com.imoonx.common.ui.gesturelock.GestureLockView.Mode.STATUS_FINGER_UP;
import static com.imoonx.common.ui.gesturelock.GestureLockView.Mode.STATUS_NO_FINGER;

/**
 * 整体包含n*n个GestureLockView,每个GestureLockView间间隔mMarginBetweenLockView，
 * 最外层的GestureLockView与容器存在mMarginBetweenLockView的外边距
 * <p/>
 * 关于GestureLockView的边长（n*n）： n * mGestureLockViewWidth + ( n + 1 ) *
 * mMarginBetweenLockView = mWidth ; 得：mGestureLockViewWidth = 4 * mWidth / ( 5
 * * mCount + 1 ) 注：mMarginBetweenLockView = mGestureLockViewWidth * 0.25 ;
 */
public class GestureLockViewGroup extends RelativeLayout {

    /**
     * 保存所有的GestureLockView
     */
    private GestureLockView[] mGestureLockViews;
    /**
     * 每个边上的GestureLockView的个数
     */
    private int mCount = 3;
    /**
     * 存储答案
     */
    private String password = "";
    /**
     * 保存用户选中的GestureLockView的id
     */
    private List<Integer> mChoose = new ArrayList<Integer>();
    private String mChooseString = "";

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
    private int mFingerOnColor = Res.getColor(R.color.colorAccent);
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
    /**
     * 指引线的开始位置x
     */
    private int mLastPathX;
    /**
     * 指引线的开始位置y
     */
    private int mLastPathY;
    /**
     * 指引下的结束位置
     */
    private Point mTmpTarget = new Point();

    /**
     * 最大尝试次数
     */
    private int mTryTimes = 3;
    public static boolean isCorrect = false;

    /**
     * 回调接口
     */
    private GesturePasswordSettingListener gesturePasswordSettingListener;
    private GestureEventListener gestureEventListener;

    private GesturePreference gesturePreference;
    private boolean isSetPassword = false;
    private boolean isInPasswordSettingMode = false;
    private boolean isWaitForFirstInput = true;
    private String firstInputPassword = "";
    private int mPrferenceId = -1;
    private float mDefaultRete = 0.3f;
    private float mInnerCircleRadiusRate;
    private boolean mIsNeedArrow;

    public GestureLockViewGroup(Context context) {
        this(context, null);
    }

    public GestureLockViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLockViewGroup(Context context, AttributeSet attrs, int defStyle) {
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
        /**
         * 获取密码状态
         */
        gesturePreference = new GesturePreference(context, mPrferenceId);
        password = gesturePreference.ReadStringPreference();
        XLog.i(this.getClass(), "password now is : " + password);
        isSetPassword = !password.equals("null"); //判断是否已经保存有密码
        isInPasswordSettingMode = !isSetPassword;     //当未设置密码，进入密码设置模式

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
        XLog.i(this.getClass(), "onMeasure");
        mWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        mHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        mHeight = mWidth = mWidth < mHeight ? mWidth : mHeight;
//        initViews();
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
        initViews();
    }

    private void initViews() {
        // 初始化mGestureLockViews
        if (mGestureLockViews == null) {
            XLog.e(this.getClass(), "initViews");
            mGestureLockViews = new GestureLockView[mCount * mCount];
            // 计算每个GestureLockView的宽度
            mGestureLockViewWidth = (int) (4 * mWidth * 1.0f / (5 * mCount + 1));
//            float v = mWidth / (3 * mCount / 2 + 0.5f);
//            mGestureLockViewWidth = (int) v;
            //计算每个GestureLockView的间距
            mMarginBetweenLockView = (int) (mGestureLockViewWidth * 0.5);
//            mMarginBetweenLockView = (int) (mWidth - mGestureLockViewWidth * mCount) / mCount;
//            mMarginBetweenLockView = mGestureLockViewWidth / 3;
            // 设置画笔的宽度为GestureLockView的内圆直径稍微小点
            mPaint.setStrokeWidth(mGestureLockViewWidth * 0.05f);

            for (int i = 0; i < mGestureLockViews.length; i++) {
                XLog.e(this.getClass(), "mGestureLockViewWidth=" + mGestureLockViewWidth);
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
//                    leftMagin = 0;
//                }
//
//                if (i % mCount == (mCount - 1)) {// 最后一列
//                    rightMargin = 0;
//                }
//                if (i >= 0 && i < mCount) {// 第一行
//                    topMargin = mMarginBetweenLockView;
//                }
                if (i % mCount == 0) {// 第一列
                    leftMagin = 0;
                }
                if (i >= (mCount - 1) * mCount) {//最后一行
                    bottomMargin = 0;
                }
                lockerParams.setMargins(leftMagin, topMargin, rightMargin, bottomMargin);
                mGestureLockViews[i].setMode(STATUS_NO_FINGER);
                addView(mGestureLockViews[i], lockerParams);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        XLog.i(this.getClass(), "mTryTimes : " + mTryTimes);
        //重试次数超过限制，直接返回
        if (mTryTimes <= 0) {
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                reset();// 重置
                break;
            case MotionEvent.ACTION_MOVE:
                drawAndGetSelectedWhenTouchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (isInPasswordSettingMode) {
                    if (gesturePasswordSettingListener != null)
                        setPasswordHandle();  //设置密码
                } else {
                    if (mChoose.size() > 0) {
                        isCorrect = checkAnswer();
                    } else {
                        return true;
                    }
                    if (gestureEventListener != null) {
                        if (mTryTimes > 0)
                            gestureEventListener.onGestureEvent(isCorrect, mChoose);  //将结果回调

                        if (mTryTimes == 0 && !isCorrect)
                            gestureEventListener.onUnmatchedExceedBoundary();  //超出重试次数，进入回调

                        if (mTryTimes == 0 && isCorrect)
                            gestureEventListener.onGestureEvent(isCorrect, mChoose);  //将结果回调
                    }
                }
                drawWhenTouchUp();
                break;
        }
        invalidate();
        return true;
    }

    private void drawAndGetSelectedWhenTouchMove(int x, int y) {
        mPaint.setColor(mFingerOnColor);
        mPaint.setAlpha(50);
        GestureLockView child = getChildIdByPos(x, y);
        if (child != null) {
            int cId = child.getId();
            XLog.i(this.getClass(), "cid=" + cId);
            if (!mChoose.contains(cId)) {
                mChoose.add(cId);
                mChooseString = mChooseString + cId;
                child.setMode(STATUS_FINGER_ON);
                // 设置指引线的起点
//                mLastPathX = child.getLeft() / 2 + child.getRight() / 2;
//                mLastPathY = child.getTop() / 2 + child.getBottom() / 2;
                mLastPathX = child.getLeft() + child.getWidth() / 2;
                mLastPathY = child.getTop() + child.getHeight() / 2;
//                Log.e("mLastPathXLastPathY", mLastPathX + "******" + mLastPathY);
//                Log.e("child+=====", child.getHeight() + "**********" + child.getWidth());
//                mLastPathX = child.getLeft() + child.getRight() / 2;
//                mLastPathY = child.getTop() + child.getBottom() / 2;
//                mLastPathX = child.getHeight() / 2;
//                mLastPathY = child.getWidth() / 2;

                if (mChoose.size() == 1) {// 当前添加为第一个
                    mPath.moveTo(mLastPathX, mLastPathY);
                } else { // 非第一个，将两者使用线连上
                    mPath.lineTo(mLastPathX, mLastPathY);
                }
            }
        }
        // 指引线的终点
        mTmpTarget.x = x;
        mTmpTarget.y = y;
    }

    private void drawWhenTouchUp() {
        if (isCorrect) {
            mPaint.setColor(mFingerUpColorCorrect);
        } else {
            mPaint.setColor(mFingerUpColorError);
        }
        mPaint.setAlpha(50);
        XLog.i(this.getClass(), "mChoose = " + mChoose);
        // 将终点设置位置为起点，即取消指引线
        mTmpTarget.x = mLastPathX;
        mTmpTarget.y = mLastPathY;
        // 改变子元素的状态为UP
        setItemModeUp();

        // 计算每个元素中箭头需要旋转的角度
        if (mIsNeedArrow) {
            for (int i = 0; i + 1 < mChoose.size(); i++) {
                int childId = mChoose.get(i);
                int nextChildId = mChoose.get(i + 1);

                GestureLockView startChild = findViewById(childId);
                GestureLockView nextChild = findViewById(nextChildId);

                int dx = nextChild.getLeft() - startChild.getLeft();
                int dy = nextChild.getTop() - startChild.getTop();
                // 计算角度
                int angle = (int) Math.toDegrees(Math.atan2(dy, dx)) + 90;
                startChild.setArrowDegree(angle);
            }
        }
    }

    private void setPasswordHandle() {
        if (isWaitForFirstInput) {
            if (gesturePasswordSettingListener.onFirstInputComplete(mChooseString.length(), mChoose)) {
                firstInputPassword = mChooseString;
                isWaitForFirstInput = false;
            }
        } else {
            if (firstInputPassword.equals(mChooseString)) {
                gesturePasswordSettingListener.onSuccess();
                savePassword(mChooseString);
                isInPasswordSettingMode = false;
            } else {
                gesturePasswordSettingListener.onFail();
            }
        }
        reset();
    }

    private void setItemModeUp() {
        for (GestureLockView gestureLockView : mGestureLockViews) {
            if (mChoose.contains(gestureLockView.getId())) {
                gestureLockView.setMode(STATUS_FINGER_UP);
            }
        }
    }

    /**
     * 检查用户绘制的手势是否正确
     *
     * @return boolean
     */
    public boolean checkAnswer() {
        if (password.equals(mChooseString)) {
            return true;
        } else {
            this.mTryTimes--;
            return false;
        }
    }

    /**
     * 通过x,y获得落入的GestureLockView
     *
     * @param x
     * @param y
     * @return
     */
    private GestureLockView getChildIdByPos(int x, int y) {
        for (GestureLockView gestureLockView : mGestureLockViews) {
            if (checkPositionInChild(gestureLockView, x, y)) {
                return gestureLockView;
            }
        }
        return null;
    }

    /**
     * 检查当前是否在child中
     *
     * @param child
     * @param x
     * @param y
     * @return
     */
    private boolean checkPositionInChild(View child, int x, int y) {

        //设置了内边距，即x,y必须落入下GestureLockView的内部中间的小区域中，可以通过调整padding使得x,y落入范围变大，或者不设置padding
        int padding = (int) (mGestureLockViewWidth * 0.15);

        int height = child.getHeight();
        int width = child.getWidth();

        if (x >= child.getLeft() + padding && x <= child.getRight() - padding && y >= child.getTop() + padding
                && y <= child.getBottom() - padding) {
            return true;
        }
        return false;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制GestureLockView间的连线
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
        //绘制指引线
        if (mChoose.size() > 0) {
            if (mLastPathX != 0 && mLastPathY != 0)
                canvas.drawLine(mLastPathX, mLastPathY, mTmpTarget.x, mTmpTarget.y, mPaint);
        }
    }

    /**
     * 做一些必要的重置
     */
    private void reset() {
        mChoose.clear();
        mChooseString = "";
        mPath.reset();
        for (GestureLockView gestureLockView : mGestureLockViews) {
            gestureLockView.setMode(STATUS_NO_FINGER);
            gestureLockView.setArrowDegree(-1);
        }
    }

    //对外公开的一些方法
    public void setGestureEventListener(GestureEventListener gestureEventListener) {
        this.gestureEventListener = gestureEventListener;
    }

    public void setGesturePasswordSettingListener(GesturePasswordSettingListener gesturePasswordSettingListener) {
        this.gesturePasswordSettingListener = gesturePasswordSettingListener;
    }

    public void removePassword() {
        gesturePreference.WriteStringPreference("null");
        this.isSetPassword = false;
        isWaitForFirstInput = true;
        isInPasswordSettingMode = true;
    }

    public void savePassword(String password) {
        this.password = password;
        gesturePreference.WriteStringPreference(password);
    }

    public String getPassword() {
        return password;
    }

    public void resetView() {
        reset();
        invalidate();
    }

    public void setRetryTimes(int retryTimes) {
        this.mTryTimes = retryTimes;
    }

    public int getResidueTimes() {
        return mTryTimes;
    }

    public boolean isSetPassword() {
        return isSetPassword;
    }

}
