package com.imoonx.time;

import android.view.MotionEvent;

public class LoopViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

    private WheelView loopView;

    public LoopViewGestureListener(WheelView loopview) {
        loopView = loopview;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        loopView.scrollBy(velocityY);
        return true;
    }
}
