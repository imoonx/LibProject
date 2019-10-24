package com.imoonx.time;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.imoonx.util.Res;
import com.imoonx.util.XLog;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WheelView extends View {

    private Paint mPaintRectCenter;

    public enum ACTION {
        CLICK, FLING, DAGGLE
    }

    public Handler handler;
    private GestureDetector gestureDetector;
    public OnItemSelectedListener onItemSelectedListener;

    // Timer mTimer;
    private ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mFuture;

    private Paint paintOuterText;
    private Paint paintCenterText;
    private Paint paintDivider;

    private ScrollTimeAdapter adapter;

    private String label;
    private int textSize;
    private int maxTextWidth;
    private int maxTextHeight;
    public float itemHeight;

    //未选中颜色
    private int textColorOut;
    //选中颜色
    private int textColorCenter;
    //分割线颜色
    private int dividerColor;

    private static final float lineSpacingMultiplier = 2F;
    public boolean isLoop;

    float firstLineY;
    float secondLineY;
    float centerY;

    public int totalScrollY;
    public int initPosition;
    private int selectedItem;
    private int preCurrentIndex;
    private int change;

    private int itemsVisible = 11;

    private int measuredHeight;
    private int measuredWidth;

    private int halfCircumference;
    private int radius;

    private int mOffset = 0;
    private float previousY = 0;
    private long startTime = 0;

    private static final int VELOCITYFLING = 5;
    private int widthMeasureSpec;

    private int mGravity = Gravity.CENTER;
    private int drawCenterContentStart = 0;
    private int drawOutContentStart = 0;
    private static final float SCALECONTENT = 0.8F;
    private static final float CENTERCONTENTOFFSET = 6;
    private static final String GETPICKERVIEWTEXT = "getPickerViewText";

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textColorOut = Res.getColor(R.color.pickerview_wheelview_textcolor_out);
        textColorCenter = Res.getColor(R.color.pickerview_wheelview_textcolor_center);
        dividerColor = Res.getColor(R.color.pickerview_wheelview_textcolor_divider);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.wheelview, 0, 0);
            mGravity = a.getInt(R.styleable.wheelview_gravity, Gravity.CENTER);
            textColorOut = a.getColor(R.styleable.wheelview_textColorOut, textColorOut);
            textColorCenter = a.getColor(R.styleable.wheelview_textColorCenter, textColorCenter);
            dividerColor = a.getColor(R.styleable.wheelview_dividerColor, dividerColor);
            a.recycle();
        }
        initLoopView(context);
    }

    private void initLoopView(Context context) {
        handler = new MessageHandler(this);
        gestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
        gestureDetector.setIsLongpressEnabled(false);

        isLoop = true;
        textSize = 0;

        totalScrollY = 0;
        initPosition = -1;

        initPaints();
        setTextSize(12F);
    }

    @SuppressLint("NewApi")
    private void initPaints() {
        paintOuterText = new Paint();
        paintOuterText.setColor(textColorOut);
        paintOuterText.setAntiAlias(true);
        paintOuterText.setTypeface(Typeface.MONOSPACE);
        paintOuterText.setTextSize(textSize);

        paintCenterText = new Paint();
        paintCenterText.setColor(textColorCenter);
        paintCenterText.setAntiAlias(true);
        paintCenterText.setTextScaleX(1.1F);
        paintCenterText.setTypeface(Typeface.MONOSPACE);
        paintCenterText.setTextSize(textSize);

        paintDivider = new Paint();
        paintDivider.setColor(dividerColor);
//        paintDivider.setColor(Color.RED);
        paintDivider.setAntiAlias(true);


        mPaintRectCenter = new Paint();
        mPaintRectCenter.setColor(dividerColor);
        mPaintRectCenter.setAlpha((int) (0.5 * 255));
        mPaintRectCenter.setAntiAlias(true);
        mPaintRectCenter.setStyle(Paint.Style.FILL);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void remeasure() {
        if (adapter == null) {
            return;
        }
        measureTextWidthHeight();
        halfCircumference = (int) (itemHeight * (itemsVisible - 1));
        measuredHeight = (int) ((halfCircumference * 2) / Math.PI);
        radius = (int) (halfCircumference / Math.PI);
        measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        firstLineY = (measuredHeight - itemHeight) / 2.0F;
        secondLineY = (measuredHeight + itemHeight) / 2.0F;
        centerY = (measuredHeight + maxTextHeight) / 2.0F - CENTERCONTENTOFFSET;
        if (initPosition == -1) {
            if (isLoop) {
                initPosition = (adapter.getItemsCount() + 1) / 2;
            } else {
                initPosition = 0;
            }
        }

        preCurrentIndex = initPosition;
    }

    private void measureTextWidthHeight() {
        Rect rect = new Rect();
        for (int i = 0; i < adapter.getItemsCount(); i++) {
            String s1 = getContentText(adapter.getItem(i));
            paintCenterText.getTextBounds(s1, 0, s1.length(), rect);
            int textWidth = rect.width();
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth;
            }
            paintCenterText.getTextBounds("\u661F\u671F", 0, 2, rect); // 星期
            int textHeight = rect.height();
            if (textHeight > maxTextHeight) {
                maxTextHeight = textHeight;
            }
        }
        itemHeight = lineSpacingMultiplier * maxTextHeight;
    }

    void smoothScroll(ACTION action) {
        cancelFuture();
        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
            mOffset = (int) ((totalScrollY % itemHeight + itemHeight) % itemHeight);
            if ((float) mOffset > itemHeight / 2.0F) {
                mOffset = (int) (itemHeight - (float) mOffset);
            } else {
                mOffset = -mOffset;
            }
        }
        mFuture = mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, mOffset), 0, 10,
                TimeUnit.MILLISECONDS);
    }

    protected final void scrollBy(float velocityY) {
        cancelFuture();

        mFuture = mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0, VELOCITYFLING,
                TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }

    public final void setCyclic(boolean cyclic) {
        isLoop = cyclic;
    }

    public final void setTextSize(float size) {
        if (size > 0.0F) {
//            textSize = (int) (context.getResources().getDisplayMetrics().density * size);
            paintOuterText.setTextSize(Res.dip2px(size));
            paintCenterText.setTextSize(Res.dip2px(size));
        }
    }

    public final void setCurrentItem(int currentItem) {
        this.initPosition = currentItem;
        totalScrollY = 0;
        invalidate();
    }

    public final void setOnItemSelectedListener(OnItemSelectedListener OnItemSelectedListener) {
        this.onItemSelectedListener = OnItemSelectedListener;
    }

    public final void setAdapter(ScrollTimeAdapter adapter) {
        this.adapter = adapter;
        remeasure();
        invalidate();
    }

    public final ScrollTimeAdapter getAdapter() {
        return adapter;
    }

    public final int getCurrentItem() {
        return selectedItem;
    }

    protected final void onItemSelected() {
        if (onItemSelectedListener != null) {
            postDelayed(new OnItemSelectedRunnable(this), 200L);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (adapter == null) {
            return;
        }
        @SuppressLint("DrawAllocation")
        Object visibles[] = new Object[itemsVisible];
        change = (int) (totalScrollY / itemHeight);
        try {
            preCurrentIndex = initPosition + change % adapter.getItemsCount();
        } catch (ArithmeticException e) {
            XLog.e(WheelView.class, e);
        }
        if (!isLoop) {
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0;
            }
            if (preCurrentIndex > adapter.getItemsCount() - 1) {
                preCurrentIndex = adapter.getItemsCount() - 1;
            }
        } else {
            if (preCurrentIndex < 0) {

                preCurrentIndex = adapter.getItemsCount() + preCurrentIndex;
            }
            if (preCurrentIndex > adapter.getItemsCount() - 1) {
                preCurrentIndex = preCurrentIndex - adapter.getItemsCount();
            }
        }

        int itemHeightOffset = (int) (totalScrollY % itemHeight);
        int counter = 0;
        while (counter < itemsVisible) {
            int index = preCurrentIndex - (itemsVisible / 2 - counter);

            if (isLoop) {
                if (index < 0) {
                    index = index + adapter.getItemsCount();
                    if (index < 0) {
                        index = 0;
                    }
                }
                if (index > adapter.getItemsCount() - 1) {
                    index = index - adapter.getItemsCount();
                    if (index > adapter.getItemsCount() - 1) {
                        index = adapter.getItemsCount() - 1;
                    }
                }
                visibles[counter] = adapter.getItem(index);
            } else if (index < 0) {
                visibles[counter] = "";
            } else if (index > adapter.getItemsCount() - 1) {
                visibles[counter] = "";
            } else {
                visibles[counter] = adapter.getItem(index);
            }
            counter++;

        }
        canvas.drawLine(0.0F, firstLineY, measuredWidth, firstLineY, paintDivider);
        canvas.drawLine(0.0F, secondLineY, measuredWidth, secondLineY, paintDivider);
//        float startX, float startY, float stopX, float stopY, Paint paint
//        float left, float top, float right, float bottom, Paint paint
        canvas.drawRect(0.0F, firstLineY, measuredWidth, secondLineY, mPaintRectCenter);
//        canvas.drawRect(0, getHeight() / 2 - itemHeightOffset, getWidth(), getHeight() / 2 + itemHeightOffset, mPaintRectCenter);
//        canvas.drawRect(measuredWidth / 2, getHeight() / 2 - itemHeightOffset, measuredWidth / 2,
//                getHeight() / 2 + itemHeightOffset, mPaintRectCenter);


        if (label != null) {
            int drawRightContentStart = measuredWidth - getTextWidth(paintCenterText, label);
            canvas.drawText(label, drawRightContentStart - CENTERCONTENTOFFSET, centerY, paintCenterText);
        }

        counter = 0;
        while (counter < itemsVisible) {
            canvas.save();
            float itemHeight = maxTextHeight * lineSpacingMultiplier;
            double radian = ((itemHeight * counter - itemHeightOffset) * Math.PI) / halfCircumference;
            float angle = (float) (90D - (radian / Math.PI) * 180D);
            if (angle >= 90F || angle <= -90F) {
                canvas.restore();
            } else {
                String contentText = getContentText(visibles[counter]);

                measuredCenterContentStart(contentText);
                measuredOutContentStart(contentText);
                float translateY = (float) (radius - Math.cos(radian) * radius - (Math.sin(radian) * maxTextHeight) / 2D);
                canvas.translate(0.0F, translateY);
                canvas.scale(1.0F, (float) Math.sin(radian));
                if (translateY <= firstLineY && maxTextHeight + translateY >= firstLineY) {
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, firstLineY - translateY);
                    canvas.scale(1.0F, (float) Math.sin(radian) * SCALECONTENT);
                    canvas.drawText(contentText, drawOutContentStart, maxTextHeight, paintOuterText);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, firstLineY - translateY, measuredWidth, (int) (itemHeight));
                    canvas.scale(1.0F, (float) Math.sin(radian) * 1F);
                    canvas.drawText(contentText, drawCenterContentStart, maxTextHeight - CENTERCONTENTOFFSET,
                            paintCenterText);
                    canvas.restore();
                } else if (translateY <= secondLineY && maxTextHeight + translateY >= secondLineY) {
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, secondLineY - translateY);
                    canvas.scale(1.0F, (float) Math.sin(radian) * 1.0F);
                    canvas.drawText(contentText, drawCenterContentStart, maxTextHeight - CENTERCONTENTOFFSET,
                            paintCenterText);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, secondLineY - translateY, measuredWidth, (int) (itemHeight));
                    canvas.scale(1.0F, (float) Math.sin(radian) * SCALECONTENT);
                    canvas.drawText(contentText, drawOutContentStart, maxTextHeight, paintOuterText);
                    canvas.restore();
                } else if (translateY >= firstLineY && maxTextHeight + translateY <= secondLineY) {
                    canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                    canvas.drawText(contentText, drawCenterContentStart, maxTextHeight - CENTERCONTENTOFFSET,
                            paintCenterText);
                    int preSelectedItem = adapter.indexOf(visibles[counter]);
                    if (preSelectedItem != -1) {
                        selectedItem = preSelectedItem;
                    }
                } else {
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                    canvas.scale(1.0F, (float) Math.sin(radian) * SCALECONTENT);
                    canvas.drawText(contentText, drawOutContentStart, maxTextHeight, paintOuterText);
                    canvas.restore();
                }
                canvas.restore();
            }
            counter++;
        }
    }

    private String getContentText(Object item) {
        String contentText = item.toString();
        try {
            Class<?> clz = item.getClass();
            Method m = clz.getMethod(GETPICKERVIEWTEXT);
            contentText = m.invoke(item, new Object[0]).toString();
        } catch (Exception e) {
            XLog.e(WheelView.class, e);
        }
        return contentText;
    }

    private void measuredCenterContentStart(String content) {
        Rect rect = new Rect();
        paintCenterText.getTextBounds(content, 0, content.length(), rect);
        switch (mGravity) {
            case Gravity.CENTER:
                drawCenterContentStart = (int) ((measuredWidth - rect.width()) * 0.5);
                break;
            case Gravity.LEFT:
                drawCenterContentStart = 0;
                break;
            case Gravity.RIGHT:
                drawCenterContentStart = measuredWidth - rect.width();
                break;
        }
    }

    private void measuredOutContentStart(String content) {
        Rect rect = new Rect();
        paintOuterText.getTextBounds(content, 0, content.length(), rect);
        switch (mGravity) {
            case Gravity.CENTER:
                drawOutContentStart = (int) ((measuredWidth - rect.width()) * 0.5);
                break;
            case Gravity.LEFT:
                drawOutContentStart = 0;
                break;
            case Gravity.RIGHT:
                drawOutContentStart = measuredWidth - rect.width();
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.widthMeasureSpec = widthMeasureSpec;
        remeasure();
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                cancelFuture();
                previousY = event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                float dy = previousY - event.getRawY();
                previousY = event.getRawY();
                totalScrollY = (int) (totalScrollY + dy);

                if (!isLoop) {
                    float top = -initPosition * itemHeight;
                    float bottom = (adapter.getItemsCount() - 1 - initPosition) * itemHeight;
                    if (totalScrollY - itemHeight * 0.3 < top) {
                        top = totalScrollY - dy;
                    } else if (totalScrollY + itemHeight * 0.3 > bottom) {
                        bottom = totalScrollY - dy;
                    }

                    if (totalScrollY < top) {
                        totalScrollY = (int) top;
                    } else if (totalScrollY > bottom) {
                        totalScrollY = (int) bottom;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            default:
                if (!eventConsumed) {
                    float y = event.getY();
                    double l = Math.acos((radius - y) / radius) * radius;
                    int circlePosition = (int) ((l + itemHeight / 2) / itemHeight);
                    float extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight;

                    mOffset = (int) ((circlePosition - itemsVisible / 2) * itemHeight - extraOffset);

                    if ((System.currentTimeMillis() - startTime) > 120) {

                        smoothScroll(ACTION.DAGGLE);
                    } else {
                        smoothScroll(ACTION.CLICK);
                    }
                }
                break;
        }
        invalidate();

        return true;
    }

    public int getItemsCount() {
        return adapter != null ? adapter.getItemsCount() : 0;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    public int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }
}