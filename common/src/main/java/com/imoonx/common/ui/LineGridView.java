package com.imoonx.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.imoonx.common.R;


/**
 * 分割线 gridview
 */
public class LineGridView extends GridView {

    private static final int DEFAULT_LINE_COLOR = Color.parseColor("#66888888");
    private static final int DEFAULT_LINE_WIDTH = 1;
    private static final boolean DEFAULT_LINE_BELOW = false;
    private static final boolean DEFAULT_LINE_LEFT = false;
    private static final boolean DEFAULT_LINE_RIGHT = false;
    private static final boolean DEFAULT_LINE_TOP = false;

    private int mLineColor = DEFAULT_LINE_COLOR;
    private int mLineWidth = DEFAULT_LINE_WIDTH;
    private boolean mLineBelow;
    private boolean mLineLeft;
    private boolean mLineRight;
    private boolean mLineTop;
    private Paint paint1;

    public LineGridView(Context context) {
        this(context, null);
    }

    public LineGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LineGridView, defStyle, 0);
        mLineColor = a.getColor(R.styleable.LineGridView_line_color, DEFAULT_LINE_COLOR);
        mLineWidth = a.getDimensionPixelSize(R.styleable.LineGridView_line_width, DEFAULT_LINE_WIDTH);
        mLineBelow = a.getBoolean(R.styleable.LineGridView_line_isbelow, DEFAULT_LINE_BELOW);
        mLineLeft = a.getBoolean(R.styleable.LineGridView_line_isleft, DEFAULT_LINE_LEFT);
        mLineRight = a.getBoolean(R.styleable.LineGridView_line_isright, DEFAULT_LINE_RIGHT);
        mLineTop = a.getBoolean(R.styleable.LineGridView_line_istop, DEFAULT_LINE_TOP);
        a.recycle();
    }

    @SuppressLint("NewApi")
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // 获取列数
        int columns = getNumColumns();
        // 获取子类总数
        int counts = getChildCount();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mLineColor);
        paint.setStrokeWidth(mLineWidth);
        if (mLineBelow || mLineLeft || mLineRight || mLineTop) {
            paint1 = new Paint();
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setColor(mLineColor);
            paint1.setStrokeWidth(mLineWidth * 2);
        }

        int row = 0;
        // 行数
        if (counts % columns == 0) {
            row = counts / columns;
        } else {
            row = 1 + counts / columns;
        }

        for (int i = 0; i < counts; i++) {
            View view = getChildAt(i);
            // 划顶线
            if (i == 0 && mLineTop) {
                canvas.drawLine(view.getLeft(), view.getTop(), view.getRight() * columns, view.getTop(), paint1);
            }
            if (i != 0 && i % columns == 0) {
                canvas.drawLine(view.getLeft(), view.getTop(), view.getRight() * columns, view.getTop(), paint);
            }
            // // 底线
            if (i % columns == 0 && i / columns == row - 1 && mLineBelow) {
                canvas.drawLine(view.getLeft(), view.getBottom(), view.getRight() * columns, view.getBottom(), paint1);
            }
            // 左线
            if (i == 0 && mLineLeft) {
                canvas.drawLine(view.getLeft(), view.getTop(), view.getLeft(), view.getBottom() * row, paint1);
            }
            if (i < columns && i != 0) {
                canvas.drawLine(view.getLeft(), view.getTop(), view.getLeft(), view.getBottom() * row, paint);
            }
            // 右线
            if (i == (columns - 1) && i < columns && mLineRight) {
                canvas.drawLine(view.getRight(), view.getTop(), view.getRight(), view.getBottom() * row, paint1);
            }

        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
