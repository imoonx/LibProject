package com.imoonx.common.ui.seekbar;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.imoonx.common.R;
import com.imoonx.util.Res;


public class CustomerSeekBar extends View {

    private static final int DEFAULT_TICK_COUNT = 3;

    private static final float DEFAULT_TICK_HEIGHT = Res.getDimens(R.dimen.space_12);

    private static final float DEFAULT_BAR_WIDTH = Res.getDimens(R.dimen.space_2);

    private static final int DEFAULT_BAR_COLOR = 0x66333333;

    private static final int DEFAULT_TEXT_COLOR = 0x66333333;

    private static final float DEFAULT_TEXT_PADDING = Res.getDimens(R.dimen.space_20);

    private static final float DEFAULT_THUMB_RADIUS = Res.getDimens(R.dimen.space_10);

    private static final int DEFAULT_THUMB_COLOR_NORMAL = 0xffffffff;

    private static final int DEFAULT_THUMB_COLOR_PRESSED = 0xffffffff;

    private static final int DEFAULT_CURRENT_INDEX = 0;

    private static final boolean DEFAULT_ANIMATION = false;

    /**
     * 刻度数量 默认3
     */
    private int mTickCount;
    /**
     * 竖直刻度高度 默认12dp
     */
    private float mTickHeight;
    /**
     * 水平刻度线的宽度 默认2dp
     */
    private float mBarWidth;
    /**
     * 刻度线颜色 默认#333333
     */
    private int mBarColor;
    /**
     * 拖动圆的半径 默认10dp
     */
    private float mThumbRadius;
    /**
     * 拖动圆正常时的颜色 默认#ffffff
     */
    private int mThumbColorNormal;
    /**
     * 拖动圆拖动时的颜色 默认#ffffff
     */
    private int mThumbColorPressed;
    /**
     * 描述文字的颜色 默认#333333
     */
    private int mTextColor;
    /**
     * 描述文字的padding 默认10dp
     */
    private float mTextPadding;
    /**
     * 选中位置 默认0
     */
    private int mCurrentIndex;
    /**
     * 是否需要动画 默认true
     */
    private boolean mAnimation;

    private String[] mTexts = {"小", "标准", "大", "加大"};

    private int[] mTextSizes = {Res.getDimens(R.dimen.text_size_12), Res.getDimens(R.dimen.text_size_14), Res.getDimens(R.dimen.text_size_16), Res.getDimens(R.dimen.text_size_18)};

    private Thumb mThumb;

    private Bar mBar;

    private ValueAnimator mAnimator;
    private OnSlidChangeListener mListener;

    public CustomerSeekBar(Context context) {
        this(context, null);
    }

    public CustomerSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomerSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initArgs(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomerSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initArgs(context, attrs);
    }

    private void initArgs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomerSeekBar);

        mTickCount = a.getInteger(R.styleable.CustomerSeekBar_seek_bar_tick_count, DEFAULT_TICK_COUNT);
        mTickHeight = a.getDimension(R.styleable.CustomerSeekBar_seek_bar_vertical_heignt, DEFAULT_TICK_HEIGHT);
        mBarWidth = a.getDimension(R.styleable.CustomerSeekBar_seek_bar_vertical_heignt, DEFAULT_BAR_WIDTH);
        mBarColor = a.getColor(R.styleable.CustomerSeekBar_seek_bar_color, DEFAULT_BAR_COLOR);
        mTextColor = a.getColor(R.styleable.CustomerSeekBar_seek_bar_text_color, DEFAULT_TEXT_COLOR);
        mTextPadding = a.getDimension(R.styleable.CustomerSeekBar_seek_bar_text_padding, DEFAULT_TEXT_PADDING);
        mThumbRadius = a.getDimension(R.styleable.CustomerSeekBar_seek_bar_radius, DEFAULT_THUMB_RADIUS);
        mThumbColorNormal = a.getColor(R.styleable.CustomerSeekBar_seek_bar_normal_color, DEFAULT_THUMB_COLOR_NORMAL);
        mThumbColorPressed = a.getColor(R.styleable.CustomerSeekBar_seek_bar_pressed_color, DEFAULT_THUMB_COLOR_PRESSED);
        mCurrentIndex = a.getInteger(R.styleable.CustomerSeekBar_seek_bar_current_index, DEFAULT_CURRENT_INDEX);
        mAnimation = a.getBoolean(R.styleable.CustomerSeekBar_seek_bar_is_animation, DEFAULT_ANIMATION);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 500;
        int height;

        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (measureWidthMode == MeasureSpec.AT_MOST) {
            width = measureWidth;
        } else if (measureWidthMode == MeasureSpec.EXACTLY) {
            width = measureWidth;
        }

        if (measureHeightMode == MeasureSpec.AT_MOST) {
            height = Math.min(getMinHeight(), measureHeight);
        } else if (measureHeightMode == MeasureSpec.EXACTLY) {
            height = measureHeight;
        } else {
            height = getMinHeight();
        }
        setMeasuredDimension(width, height);
    }

    private int getMinHeight() {
        final float f = getFontHeight();
        return (int) (f + mTextPadding + mThumbRadius * 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createBar();
        createThumbs();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBar.draw(canvas);
        mThumb.draw(canvas);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (VISIBLE != visibility) {
            stopAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        destroyResources();
        super.onDetachedFromWindow();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || isAnimationRunning()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return onActionDown(event.getX(), event.getY());
            case MotionEvent.ACTION_MOVE:
                this.getParent().requestDisallowInterceptTouchEvent(true);
                return onActionMove(event.getX());
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                return onActionUp(event.getX(), event.getY());
            default:
                return true;
        }
    }

    private void createBar() {
        mBar = new Bar(getRadius(), getYCoordinate(), getBarLength(), mTickCount, mTickHeight, mBarWidth, mBarColor,
                mTextColor, mTextSizes, mTextPadding, mTexts);
    }

    private void createThumbs() {
        mThumb = new Thumb(getDistance() * mCurrentIndex + getRadius(), getYCoordinate(), mThumbColorNormal, mThumbColorPressed, mThumbRadius);
    }

    private float getDistance() {
        return getBarLength() / (mTickCount - 1);
    }

    private float getRadius() {
        return mThumbRadius;
    }

    private float getYCoordinate() {
        return getHeight() - mThumbRadius;
    }

    private float getFontHeight() {
        Paint paint = new Paint();
        paint.setTextSize(mTextSizes[mTextSizes.length - 1]);
        paint.measureText(mTexts[mTexts.length - 1]);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent;
    }

    private float getBarLength() {
        return getWidth() - 2 * getRadius();
    }

    private boolean onActionDown(float x, float y) {
        if (!mThumb.isPressed() && mThumb.isInTargetZone(x, y)) {
            pressThumb(mThumb);
        }
        return true;
    }

    private boolean onActionMove(float x) {
        if (mThumb.isPressed()) {
            moveThumb(mThumb, x);
        }
        return true;
    }

    private boolean onActionUp(float x, float y) {
        if (mThumb.isPressed()) {
            releaseThumb(mThumb);
        }
        return true;
    }

    private void pressThumb(Thumb thumb) {
        thumb.press();
        invalidate();
    }

    private void releaseThumb(final Thumb thumb) {
        int tempIndex = mBar.getNearestTickIndex(thumb);
        if (tempIndex != mCurrentIndex) {
            mCurrentIndex = tempIndex;
            if (null != mListener)
                mListener.onIndexChanged(this, mCurrentIndex);
        }
        float start = thumb.getX();
        float end = mBar.getNearestTickCoordinate(thumb);
        if (mAnimation)
            startAnimation(thumb, start, end);
        else {
            thumb.setX(end);
            invalidate();
        }
        thumb.release();
    }

    private void startAnimation(final Thumb thumb, float start, float end) {
        stopAnimation();
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(80);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float x = (Float) animation.getAnimatedValue();
                thumb.setX(x);
                invalidate();
            }
        });
        mAnimator.start();
    }

    private boolean isAnimationRunning() {
        return null != mAnimator && mAnimator.isRunning();
    }

    private void destroyResources() {
        stopAnimation();
        if (null != mBar) {
            mBar.destroyResources();
            mBar = null;
        }
        if (null != mThumb) {
            mThumb.destroyResources();
            mThumb = null;
        }
    }

    private void stopAnimation() {
        if (null != mAnimator) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    private void moveThumb(Thumb thumb, float x) {
        if (x < mBar.getLeftX() || x > mBar.getRightX()) {
            // Do nothing.
        } else {
            thumb.setX(x);
            invalidate();
        }
    }

    public interface OnSlidChangeListener {
        void onIndexChanged(CustomerSeekBar rangeBar, int index);
    }

    /**
     * 设置滑动监听
     *
     * @param listener 监听器
     * @return CustomerSeekBar
     */
    public CustomerSeekBar setOnSlidChangeListener(OnSlidChangeListener listener) {
        mListener = listener;
        return CustomerSeekBar.this;
    }

    public void applay() {
        createThumbs();
        createBar();
        requestLayout();
        invalidate();
    }

    public String[] getmTexts() {
        return mTexts;
    }

    /**
     * 设置文字描述 设置完成调用 applay 刷新
     *
     * @param texts 文字数组
     */
    public CustomerSeekBar setmTexts(String[] texts) {
        this.mTexts = texts;
        return this;
    }

    public int[] getmTextSizes() {
        return mTextSizes;
    }

    /**
     * 设置文字大小 设置完成调用 applay 刷新
     *
     * @param textSizes 字体大小数组
     */
    public CustomerSeekBar setmTextSizes(int[] textSizes) {
        this.mTextSizes = textSizes;
        return this;
    }
}