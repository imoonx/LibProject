package com.imoonx.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 禁止滚动的ViewPager
 * <p>
 * 解决滚动显示不完整
 */
public class NoScrollViewPager extends ViewPager {

    public NoScrollViewPager(Context context) {
        this(context, null);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;// 去掉ViewPager默认的滑动效果
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;// 不拦截事件，把事件往子控件传递
    }

}
