package com.imoonx.common.ui.slide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.imoonx.util.XLog;

public class FrontLayout extends LinearLayout {

    private SwipeLayoutInterface mISwipeLayout;

    public FrontLayout(Context context) {
        super(context);
    }

    public FrontLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NewApi")
    public FrontLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSwipeLayout(SwipeLayoutInterface mSwipeLayout) {
        this.mISwipeLayout = mSwipeLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mISwipeLayout.getCurrentStatus() == SwipeLayout.Status.Close) {
            XLog.i(getClass(), "系统调度");
            return super.onInterceptTouchEvent(ev);
        } else {
            XLog.i(getClass(), "拦截");
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mISwipeLayout.getCurrentStatus() == SwipeLayout.Status.Close) {
            XLog.i(getClass(), "onTouchEvent系统调度");
            return super.onTouchEvent(event);
        } else {
            XLog.i(getClass(), "onTouchEvent拦截");
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                mISwipeLayout.close();
            }
            return true;
        }
    }

}
