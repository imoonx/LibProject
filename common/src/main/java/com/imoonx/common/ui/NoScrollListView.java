package com.imoonx.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 禁止滚动的ListView
 * <p>
 * 解决滚动显示不完整
 */
@SuppressLint("NewApi")
public class NoScrollListView extends ListView {

    public NoScrollListView(Context context) {
        this(context, null);
    }

    public NoScrollListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NoScrollListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
