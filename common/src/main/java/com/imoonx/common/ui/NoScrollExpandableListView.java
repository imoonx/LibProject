package com.imoonx.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * 禁止滚动的ExpandableListView
 * <p>
 * 解决滚动显示不完整
 */
public class NoScrollExpandableListView extends ExpandableListView {

    public NoScrollExpandableListView(Context context) {
        this(context, null);
    }

    public NoScrollExpandableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoScrollExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint({"NewApi"})
    public NoScrollExpandableListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
