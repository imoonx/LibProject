package com.imoonx.common.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.imoonx.common.R;


public class WaveLoadingView extends View {

    @SuppressWarnings("unused")
    private static final float DEFAULT_AMPLITUDE_RATIO = 0.1F;
    @SuppressWarnings("unused")
    private static final float DEFAULT_AMPLITUDE_VALUE = 50.0F;
    @SuppressWarnings("unused")
    private static final float DEFAULT_WATER_LEVEL_RATIO = 0.5F;
    @SuppressWarnings("unused")
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0F;
    @SuppressWarnings("unused")
    private static final float DEFAULT_WAVE_SHIFT_RATIO = 0.0F;
    @SuppressWarnings("unused")
    private static final int DEFAULT_WAVE_PROGRESS_VALUE = 50;
    private static final int DEFAULT_WAVE_COLOR = Color.parseColor("#212121");
    private static final int DEFAULT_WAVE_BACKGROUND_COLOR = Color.parseColor("#00000000");
    private static final int DEFAULT_TITLE_COLOR = Color.parseColor("#212121");
    @SuppressWarnings("unused")
    private static final int DEFAULT_STROKE_COLOR = 0;
    @SuppressWarnings("unused")
    private static final float DEFAULT_BORDER_WIDTH = 0.0F;
    @SuppressWarnings("unused")
    private static final float DEFAULT_TITLE_STROKE_WIDTH = 0.0F;
    private static final int DEFAULT_WAVE_SHAPE = ShapeType.CIRCLE.ordinal();
    private static final int DEFAULT_TRIANGLE_DIRECTION = TriangleDirection.NORTH
            .ordinal();
    @SuppressWarnings("unused")
    private static final int DEFAULT_ROUND_RECTANGLE_X_AND_Y = 30;
    @SuppressWarnings("unused")
    private static final float DEFAULT_TITLE_TOP_SIZE = 18.0F;
    @SuppressWarnings("unused")
    private static final float DEFAULT_TITLE_CENTER_SIZE = 22.0F;
    @SuppressWarnings("unused")
    private static final float DEFAULT_TITLE_BOTTOM_SIZE = 18.0F;
    private int mCanvasSize;
    private int mCanvasHeight;
    private int mCanvasWidth;
    private float mAmplitudeRatio;
    private int mWaveBgColor;
    private int mWaveColor;
    private int mShapeType;
    private int mTriangleDirection;
    private int mRoundRectangleXY;
    private String mTopTitle;
    private String mCenterTitle;
    private String mBottomTitle;
    private float mDefaultWaterLevel;

    public static enum ShapeType {
        TRIANGLE, CIRCLE, SQUARE, RECTANGLE;
    }

    public static enum TriangleDirection {
        NORTH, SOUTH, EAST, WEST;
    }

    private float mWaterLevelRatio = 1.0F;
    private float mWaveShiftRatio = 0.0F;
    private int mProgressValue = 50;
    private boolean mIsRoundRectangle;
    private BitmapShader mWaveShader;
    private Bitmap bitmapBuffer;
    private Matrix mShaderMatrix;
    private Paint mWavePaint;
    private Paint mWaveBgPaint;
    private Paint mBorderPaint;
    private Paint mTopTitlePaint;
    private Paint mBottomTitlePaint;
    private Paint mCenterTitlePaint;
    private Paint mTopTitleStrokePaint;
    private Paint mBottomTitleStrokePaint;
    private Paint mCenterTitleStrokePaint;
    private ObjectAnimator waveShiftAnim;
    private AnimatorSet mAnimatorSet;
    private Context mContext;

    public WaveLoadingView(Context context) {
        this(context, null);
    }

    public WaveLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        mShaderMatrix = new Matrix();
        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(true);
        mWaveBgPaint = new Paint();
        mWaveBgPaint.setAntiAlias(true);

        initAnimation();
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.WaveLoadingView, defStyleAttr, 0);
        mShapeType = attributes.getInteger(R.styleable.WaveLoadingView_wlv_shapeType, DEFAULT_WAVE_SHAPE);
        mWaveColor = attributes.getColor(R.styleable.WaveLoadingView_wlv_waveColor, DEFAULT_WAVE_COLOR);
        mWaveBgColor = attributes.getColor(R.styleable.WaveLoadingView_wlv_wave_background_Color, DEFAULT_WAVE_BACKGROUND_COLOR);
        mWaveBgPaint.setColor(mWaveBgColor);
        float amplitudeRatioAttr = attributes.getFloat(R.styleable.WaveLoadingView_wlv_waveAmplitude, 50.0F) / 1000.0F;
        mAmplitudeRatio = (amplitudeRatioAttr > 0.1F ? 0.1F : amplitudeRatioAttr);
        mProgressValue = attributes.getInteger(R.styleable.WaveLoadingView_wlv_progressValue, 50);
        setProgressValue(mProgressValue);
        mIsRoundRectangle = attributes.getBoolean(R.styleable.WaveLoadingView_wlv_round_rectangle, false);
        mRoundRectangleXY = attributes.getInteger(R.styleable.WaveLoadingView_wlv_round_rectangle_x_and_y, 30);
        mTriangleDirection = attributes.getInteger(R.styleable.WaveLoadingView_wlv_triangle_direction, DEFAULT_TRIANGLE_DIRECTION);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Style.STROKE);
        mBorderPaint.setStrokeWidth(attributes.getDimension(R.styleable.WaveLoadingView_wlv_borderWidth, dp2px(0.0F)));
        mBorderPaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_borderColor, DEFAULT_WAVE_COLOR));

        mTopTitlePaint = new Paint();
        mTopTitlePaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_titleTopColor, DEFAULT_TITLE_COLOR));
        mTopTitlePaint.setStyle(Style.FILL);
        mTopTitlePaint.setAntiAlias(true);
        mTopTitlePaint.setTextSize(attributes.getDimension(R.styleable.WaveLoadingView_wlv_titleTopSize, sp2px(18.0F)));

        mTopTitleStrokePaint = new Paint();
        mTopTitleStrokePaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_titleTopStrokeColor, 0));
        mTopTitleStrokePaint.setStrokeWidth(attributes.getDimension(R.styleable.WaveLoadingView_wlv_titleTopStrokeWidth, dp2px(0.0F)));
        mTopTitleStrokePaint.setStyle(Style.STROKE);
        mTopTitleStrokePaint.setAntiAlias(true);
        mTopTitleStrokePaint.setTextSize(mTopTitlePaint.getTextSize());

        mTopTitle = attributes.getString(R.styleable.WaveLoadingView_wlv_titleTop);

        mCenterTitlePaint = new Paint();
        mCenterTitlePaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_titleCenterColor, DEFAULT_TITLE_COLOR));
        mCenterTitlePaint.setStyle(Style.FILL);
        mCenterTitlePaint.setAntiAlias(true);
        mCenterTitlePaint.setTextSize(attributes.getDimension(R.styleable.WaveLoadingView_wlv_titleCenterSize, sp2px(22.0F)));

        mCenterTitleStrokePaint = new Paint();
        mCenterTitleStrokePaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_titleCenterStrokeColor, 0));
        mCenterTitleStrokePaint.setStrokeWidth(attributes.getDimension(R.styleable.WaveLoadingView_wlv_titleCenterStrokeWidth, dp2px(0.0F)));
        mCenterTitleStrokePaint.setStyle(Style.STROKE);
        mCenterTitleStrokePaint.setAntiAlias(true);
        mCenterTitleStrokePaint.setTextSize(mCenterTitlePaint.getTextSize());

        mCenterTitle = attributes.getString(R.styleable.WaveLoadingView_wlv_titleCenter);

        mBottomTitlePaint = new Paint();
        mBottomTitlePaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_titleBottomColor, DEFAULT_TITLE_COLOR));
        mBottomTitlePaint.setStyle(Style.FILL);
        mBottomTitlePaint.setAntiAlias(true);
        mBottomTitlePaint.setTextSize(attributes.getDimension(R.styleable.WaveLoadingView_wlv_titleBottomSize, sp2px(18.0F)));

        mBottomTitleStrokePaint = new Paint();
        mBottomTitleStrokePaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_titleBottomStrokeColor, 0));
        mBottomTitleStrokePaint.setStrokeWidth(attributes.getDimension(R.styleable.WaveLoadingView_wlv_titleBottomStrokeWidth, dp2px(0.0F)));
        mBottomTitleStrokePaint.setStyle(Style.STROKE);
        mBottomTitleStrokePaint.setAntiAlias(true);
        mBottomTitleStrokePaint.setTextSize(mBottomTitlePaint.getTextSize());

        mBottomTitle = attributes.getString(R.styleable.WaveLoadingView_wlv_titleBottom);

        attributes.recycle();
    }

    public void onDraw(Canvas canvas) {
        mCanvasSize = canvas.getWidth();
        if (canvas.getHeight() < mCanvasSize) {
            mCanvasSize = canvas.getHeight();
        }
        if (mWaveShader != null) {
            if (mWavePaint.getShader() == null) {
                mWavePaint.setShader(mWaveShader);
            }
            mShaderMatrix.setScale(1.0F, mAmplitudeRatio / 0.1F, 0.0F, mDefaultWaterLevel);

            mShaderMatrix.postTranslate(mWaveShiftRatio * getWidth(), (0.5F - mWaterLevelRatio) * getHeight());

            mWaveShader.setLocalMatrix(mShaderMatrix);

            float borderWidth = mBorderPaint.getStrokeWidth();
            switch (mShapeType) {
                case 0:
                    Point start = new Point(0, getHeight());
                    Path triangle = getEquilateralTriangle(start, getWidth(), getHeight(), mTriangleDirection);
                    canvas.drawPath(triangle, mWaveBgPaint);
                    canvas.drawPath(triangle, mWavePaint);
                    break;
                case 1:
                    if (borderWidth > 0.0F)
                        canvas.drawCircle(getWidth() / 2.0F, getHeight() / 2.0F, (getWidth() - borderWidth) / 2.0F - 1.0F, mBorderPaint);

                    float radius = getWidth() / 2.0F - borderWidth;

                    canvas.drawCircle(getWidth() / 2.0F, getHeight() / 2.0F, radius, mWaveBgPaint);
                    canvas.drawCircle(getWidth() / 2.0F, getHeight() / 2.0F, radius - 10.0F, mWavePaint);
                    break;
                case 2:
                    if (borderWidth > 0.0F)
                        canvas.drawRect(borderWidth / 2.0F, borderWidth / 2.0F, getWidth() - borderWidth / 2.0F - 0.5F, getHeight() -
                                borderWidth / 2.0F - 0.5F, mBorderPaint);

                    canvas.drawRect(borderWidth, borderWidth, getWidth() - borderWidth, getHeight() - borderWidth, mWaveBgPaint);
                    canvas.drawRect(borderWidth, borderWidth, getWidth() - borderWidth, getHeight() - borderWidth, mWavePaint);
                    break;
                case 3:
                    if (mIsRoundRectangle) {
                        if (borderWidth > 0.0F) {
                            RectF rect = new RectF(borderWidth / 2.0F, borderWidth / 2.0F, getWidth() - borderWidth / 2.0F -
                                    0.5F, getHeight() - borderWidth / 2.0F - 0.5F);
                            canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mWaveBgPaint);
                            canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mWavePaint);
                        } else {
                            RectF rect = new RectF(0.0F, 0.0F, getWidth(), getHeight());
                            canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mWaveBgPaint);
                            canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mWavePaint);
                        }
                    } else if (borderWidth > 0.0F) {
                        canvas.drawRect(borderWidth / 2.0F, borderWidth / 2.0F, getWidth() - borderWidth / 2.0F - 0.5F,
                                getHeight() - borderWidth / 2.0F - 0.5F, mWaveBgPaint);
                        canvas.drawRect(borderWidth / 2.0F, borderWidth / 2.0F,
                                getWidth() - borderWidth / 2.0F - 0.5F, getHeight() - borderWidth / 2.0F - 0.5F, mWavePaint);
                    } else {
                        canvas.drawRect(0.0F, 0.0F, canvas.getWidth(), canvas.getHeight(), mWaveBgPaint);
                        canvas.drawRect(0.0F, 0.0F, canvas.getWidth(), canvas.getHeight(), mWavePaint);
                    }
                    break;
            }
            if (!TextUtils.isEmpty(mTopTitle)) {
                float top = mTopTitlePaint.measureText(mTopTitle);
                canvas.drawText(mTopTitle, (getWidth() - top) / 2.0F, getHeight() * 2 / 10.0F, mTopTitleStrokePaint);
                canvas.drawText(mTopTitle, (getWidth() - top) / 2.0F, getHeight() * 2 / 10.0F, mTopTitlePaint);
            }
            if (!TextUtils.isEmpty(mCenterTitle)) {
                float middle = mCenterTitlePaint.measureText(mCenterTitle);
                canvas.drawText(mCenterTitle, (getWidth() - middle) / 2.0F, getHeight() / 2 - (mCenterTitleStrokePaint.descent() +
                        mCenterTitleStrokePaint.ascent()) / 2.0F, mCenterTitleStrokePaint);
                canvas.drawText(mCenterTitle, (getWidth() - middle) / 2.0F, getHeight() / 2 - (mCenterTitlePaint.descent() +
                        mCenterTitlePaint.ascent()) / 2.0F, mCenterTitlePaint);
            }
            if (!TextUtils.isEmpty(mBottomTitle)) {
                float bottom = mBottomTitlePaint.measureText(mBottomTitle);

                canvas.drawText(mBottomTitle, (getWidth() - bottom) / 2.0F, getHeight() * 8 / 10.0F - (mBottomTitleStrokePaint.descent() +
                        mBottomTitleStrokePaint.ascent()) / 2.0F, mBottomTitleStrokePaint);

                canvas.drawText(mBottomTitle, (getWidth() - bottom) / 2.0F, getHeight() * 8 / 10.0F - (mBottomTitlePaint.descent() +
                        mBottomTitlePaint.ascent()) / 2.0F, mBottomTitlePaint);
            }
        } else
            mWavePaint.setShader(null);

    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getShapeType() == 3) {
            mCanvasWidth = w;
            mCanvasHeight = h;
        } else {
            mCanvasSize = w;
            if (h < mCanvasSize) {
                mCanvasSize = h;
            }
        }
        updateWaveShader();
    }

    private void updateWaveShader() {
        if ((bitmapBuffer == null) || (haveBoundsChanged())) {
            if (bitmapBuffer != null) {
                bitmapBuffer.recycle();
            }
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            if ((width > 0) && (height > 0)) {
                double defaultAngularFrequency = 6.283185307179586D / width;
                float defaultAmplitude = height * 0.1F;
                mDefaultWaterLevel = (height * 0.5F);
                float defaultWaveLength = width;

                Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                Paint wavePaint = new Paint();
                wavePaint.setStrokeWidth(2.0F);
                wavePaint.setAntiAlias(true);

                int endX = width + 1;
                int endY = height + 1;

                float[] waveY = new float[endX];

                wavePaint.setColor(adjustAlpha(mWaveColor, 0.3F));
                for (int beginX = 0; beginX < endX; beginX++) {
                    double wx = beginX * defaultAngularFrequency;
                    float beginY = (float) (mDefaultWaterLevel + defaultAmplitude * Math.sin(wx));
                    canvas.drawLine(beginX, beginY, beginX, endY, wavePaint);
                    float beginY1 = (float) (mDefaultWaterLevel + defaultAmplitude * Math.cos(wx));
                    waveY[beginX] = beginY1;
                }
                wavePaint.setColor(mWaveColor);
                int wave2Shift = (int) (defaultWaveLength / 4.0F);
                for (int beginX = 0; beginX < endX; beginX++) {
                    canvas.drawLine(beginX, waveY[((beginX + wave2Shift) % endX)], beginX, endY, wavePaint);
                }
                mWaveShader = new BitmapShader(bitmap, TileMode.REPEAT, TileMode.CLAMP);
                mWavePaint.setShader(mWaveShader);
            }
        }
    }

    private boolean haveBoundsChanged() {
        return (getMeasuredWidth() != bitmapBuffer.getWidth()) || (getMeasuredHeight() != bitmapBuffer.getHeight());
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        if (getShapeType() == 3) {
            setMeasuredDimension(width, height);
        } else {
            int imageSize = width < height ? width : height;
            setMeasuredDimension(imageSize, imageSize);
        }
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        if (specMode == 1073741824) {
            result = specSize;
        } else {
            if (specMode == Integer.MIN_VALUE) {
                result = specSize;
            } else {
                result = mCanvasWidth;
            }
        }
        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);
        int result;
        if (specMode == 1073741824) {
            result = specSize;
        } else {
            if (specMode == Integer.MIN_VALUE) {
                result = specSize;
            } else {
                result = mCanvasHeight;
            }
        }
        return result + 2;
    }

    public void setWaveBgColor(int color) {
        mWaveBgColor = color;
        mWaveBgPaint.setColor(mWaveBgColor);
        updateWaveShader();
        invalidate();
    }

    public int getWaveBgColor() {
        return mWaveBgColor;
    }

    public void setWaveColor(int color) {
        mWaveColor = color;

        updateWaveShader();
        invalidate();
    }

    public int getWaveColor() {
        return mWaveColor;
    }

    public void setBorderWidth(float width) {
        mBorderPaint.setStrokeWidth(width);
        invalidate();
    }

    public float getBorderWidth() {
        return mBorderPaint.getStrokeWidth();
    }

    public void setBorderColor(int color) {
        mBorderPaint.setColor(color);
        updateWaveShader();
        invalidate();
    }

    public int getBorderColor() {
        return mBorderPaint.getColor();
    }

    public void setShapeType(ShapeType shapeType) {
        mShapeType = shapeType.ordinal();
        invalidate();
    }

    public int getShapeType() {
        return mShapeType;
    }

    public void setAmplitudeRatio(int amplitudeRatio) {
        if (mAmplitudeRatio != amplitudeRatio / 1000.0F) {
            mAmplitudeRatio = (amplitudeRatio / 1000.0F);
            invalidate();
        }
    }

    public float getAmplitudeRatio() {
        return mAmplitudeRatio;
    }

    @TargetApi(11)
    @SuppressLint({"NewApi"})
    public void setProgressValue(int progress) {
        mProgressValue = progress;
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(this, "waterLevelRatio", new float[]{mWaterLevelRatio,
                mProgressValue / 100.0F});
        waterLevelAnim.setDuration(1000L);
        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
        AnimatorSet animatorSetProgress = new AnimatorSet();
        animatorSetProgress.play(waterLevelAnim);
        animatorSetProgress.start();
    }

    public int getProgressValue() {
        return mProgressValue;
    }

    public void setWaveShiftRatio(float waveShiftRatio) {
        if (mWaveShiftRatio != waveShiftRatio) {
            mWaveShiftRatio = waveShiftRatio;
            invalidate();
        }
    }

    public float getWaveShiftRatio() {
        return mWaveShiftRatio;
    }

    public void setWaterLevelRatio(float waterLevelRatio) {
        if (mWaterLevelRatio != waterLevelRatio) {
            mWaterLevelRatio = waterLevelRatio;
            invalidate();
        }
    }

    public float getWaterLevelRatio() {
        return mWaterLevelRatio;
    }

    public void setTopTitle(String topTitle) {
        mTopTitle = topTitle;
    }

    public String getTopTitle() {
        return mTopTitle;
    }

    public void setCenterTitle(String centerTitle) {
        mCenterTitle = centerTitle;
    }

    public String getCenterTitle() {
        return mCenterTitle;
    }

    public void setBottomTitle(String bottomTitle) {
        mBottomTitle = bottomTitle;
    }

    public String getBottomTitle() {
        return mBottomTitle;
    }

    public void setTopTitleColor(int topTitleColor) {
        mTopTitlePaint.setColor(topTitleColor);
    }

    public int getTopTitleColor() {
        return mTopTitlePaint.getColor();
    }

    public void setCenterTitleColor(int centerTitleColor) {
        mCenterTitlePaint.setColor(centerTitleColor);
    }

    public int getCenterTitleColor() {
        return mCenterTitlePaint.getColor();
    }

    public void setBottomTitleColor(int bottomTitleColor) {
        mBottomTitlePaint.setColor(bottomTitleColor);
    }

    public int getBottomTitleColor() {
        return mBottomTitlePaint.getColor();
    }

    public void setTopTitleSize(float topTitleSize) {
        mTopTitlePaint.setTextSize(sp2px(topTitleSize));
    }

    public float getsetTopTitleSize() {
        return mTopTitlePaint.getTextSize();
    }

    public void setCenterTitleSize(float centerTitleSize) {
        mCenterTitlePaint.setTextSize(sp2px(centerTitleSize));
    }

    public float getCenterTitleSize() {
        return mCenterTitlePaint.getTextSize();
    }

    public void setBottomTitleSize(float bottomTitleSize) {
        mBottomTitlePaint.setTextSize(sp2px(bottomTitleSize));
    }

    public float getBottomTitleSize() {
        return mBottomTitlePaint.getTextSize();
    }

    public void setTopTitleStrokeWidth(float topTitleStrokeWidth) {
        mTopTitleStrokePaint.setStrokeWidth(dp2px(topTitleStrokeWidth));
    }

    public void setTopTitleStrokeColor(int topTitleStrokeColor) {
        mTopTitleStrokePaint.setColor(topTitleStrokeColor);
    }

    public void setBottomTitleStrokeWidth(float bottomTitleStrokeWidth) {
        mBottomTitleStrokePaint.setStrokeWidth(dp2px(bottomTitleStrokeWidth));
    }

    public void setBottomTitleStrokeColor(int bottomTitleStrokeColor) {
        mBottomTitleStrokePaint.setColor(bottomTitleStrokeColor);
    }

    public void setCenterTitleStrokeWidth(float centerTitleStrokeWidth) {
        mCenterTitleStrokePaint.setStrokeWidth(dp2px(centerTitleStrokeWidth));
    }

    public void setCenterTitleStrokeColor(int centerTitleStrokeColor) {
        mCenterTitleStrokePaint.setColor(centerTitleStrokeColor);
    }

    @SuppressLint({"NewApi"})
    public void startAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    @SuppressLint({"NewApi"})
    public void endAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.end();
        }
    }

    @SuppressLint({"NewApi"})
    public void cancelAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }

    @TargetApi(19)
    public void pauseAnimation() {
        if ((VERSION.SDK_INT >= 19) &&
                (mAnimatorSet != null)) {
            mAnimatorSet.pause();
        }
    }

    @TargetApi(19)
    public void resumeAnimation() {
        if ((VERSION.SDK_INT >= 19) && (mAnimatorSet != null)) {
            mAnimatorSet.resume();
        }
    }

    @SuppressLint({"NewApi"})
    public void setAnimDuration(long duration) {
        waveShiftAnim.setDuration(duration);
    }

    @SuppressLint({"NewApi"})
    private void initAnimation() {
        waveShiftAnim = ObjectAnimator.ofFloat(this, "waveShiftRatio", new float[]{0.0F, 1.0F});
        waveShiftAnim.setRepeatCount(-1);
        waveShiftAnim.setDuration(1000L);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(waveShiftAnim);
    }

    protected void onAttachedToWindow() {
        startAnimation();
        super.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        cancelAnimation();
        super.onDetachedFromWindow();
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private int sp2px(float spValue) {
        float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5F);
    }

    private int dp2px(float dp) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    private Path getEquilateralTriangle(Point p1, int width, int height, int direction) {
        Point p2 = null;
        Point p3 = null;
        if (direction == 0) {
            p2 = new Point(p1.x + width, p1.y);
            p3 = new Point(p1.x + width / 2, (int) (height - Math.sqrt(3.0D) / 2.0D * height));
        } else if (direction == 1) {
            p2 = new Point(p1.x, p1.y - height);
            p3 = new Point(p1.x + width, p1.y - height);
            p1.x += width / 2;
            p1.y = ((int) (Math.sqrt(3.0D) / 2.0D * height));
        } else if (direction == 2) {
            p2 = new Point(p1.x, p1.y - height);
            p3 = new Point((int) (Math.sqrt(3.0D) / 2.0D * width), p1.y / 2);
        } else if (direction == 3) {
            p2 = new Point(p1.x + width, p1.y - height);
            p3 = new Point(p1.x + width, p1.y);
            p1.x = ((int) (width - Math.sqrt(3.0D) / 2.0D * width));
            p1.y /= 2;
        }
        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);

        return path;
    }
}
