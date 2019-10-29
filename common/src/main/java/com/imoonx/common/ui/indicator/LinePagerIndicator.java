package com.imoonx.common.ui.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.imoonx.common.R;
import com.imoonx.util.Res;
import com.imoonx.util.XLog;

/**
 * 线性指示器
 */
public class LinePagerIndicator extends View implements PagerIndicator {

    private final Paint mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private int mCurrentPage;
    private int mFollowPage;
    private float mPageOffset;
    private boolean mIsFollow;
    private float mIndicatorSpace;
    private float mIndicatorWidth;
    private float mIndicatorHeight;

    public LinePagerIndicator(Context context) {
        this(context, null);
    }

    public LinePagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LinePagerIndicator);

        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(a.getColor(R.styleable.LinePagerIndicator_line_indicator_color, 0x0000ff));

        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setColor(a.getColor(R.styleable.LinePagerIndicator_line_indicator_stroke_color, 0x000000));
        mPaintStroke.setStrokeWidth(a.getDimension(R.styleable.LinePagerIndicator_line_indicator_stroke_width, 0));

        mPaintIndicator.setStyle(Paint.Style.FILL);
        mPaintIndicator.setColor(a.getColor(R.styleable.LinePagerIndicator_line_indicator_fill_color, 0x0000ff));

        mIndicatorSpace = a.getDimension(R.styleable.LinePagerIndicator_line_indicator_space, Res.getDimens(R.dimen.space_5));
        mIndicatorWidth = a.getDimension(R.styleable.LinePagerIndicator_line_indicator_width, Res.getDimens(R.dimen.space_25));
        mIndicatorHeight = a.getDimension(R.styleable.LinePagerIndicator_line_indicator_height, Res.getDimens(R.dimen.space_3));

        mIsFollow = a.getBoolean(R.styleable.LinePagerIndicator_line_indicator_follow, true);

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewPager == null) {
            return;
        }
        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) {
            return;
        }
        if (mCurrentPage >= count) {
            setCurrentItem(count - 1);
            return;
        }
        XLog.i(LinePagerIndicator.class, "count=" + count + "****width=" + getWidth());

        float startX = (getWidth() - mIndicatorWidth * count - mIndicatorSpace - (count - 1)) / 2;
        float endX;

        for (int i = 0; i < count; i++) {
            canvas.drawRect(startX + i * (mIndicatorSpace + mIndicatorWidth), 0, startX + i * (mIndicatorSpace + mIndicatorWidth) + mIndicatorWidth,
                    mIndicatorHeight, mPaintFill);
        }

        float cX = 0;

        if (mIsFollow) {
            cX += mPageOffset * +mIndicatorWidth;
        }
        endX = startX + mCurrentPage * (mIndicatorSpace + mIndicatorWidth) + cX + mIndicatorWidth;
        canvas.drawRect(startX + mCurrentPage * (mIndicatorSpace + mIndicatorWidth), 0, endX, mIndicatorHeight, mPaintIndicator);
    }

    @Override
    public void bindViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not set adapter");
        }
        mViewPager = view;
        mViewPager.addOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void bindViewPager(ViewPager view, int initialPosition) {
        bindViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("indicator has not bind ViewPager");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
        invalidate();
    }

    @Override
    public void notifyDataSetChanged() {
        invalidate();
        requestLayout();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        mCurrentPage = position;
        mPageOffset = positionOffset;
        // 如果指示器跟随ViewPager缓慢滑动，那么滚动的时候都绘制界面
        if (mIsFollow) {
            invalidate();
        }
        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
        mFollowPage = position;
        invalidate();
        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureWidth(int measureSpec) {
        int width;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)) {
            width = specSize;
        } else {
            final int count = mViewPager.getAdapter().getCount();
            width = (int) (getPaddingLeft() + getPaddingRight() + getWidth());
            if (specMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, specSize);
            }
        }
        return width;
    }

    private int measureHeight(int measureSpec) {
        int height;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else {
            height = (int) (getHeight() + getPaddingTop() + getPaddingBottom() + 1);
            if (specMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, specSize);
            }
        }
        return height;
    }
}
