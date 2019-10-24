package com.imoonx.time;

public class OnItemSelectedRunnable implements Runnable {

    private WheelView loopView;

    public OnItemSelectedRunnable(WheelView loopview) {
        loopView = loopview;
    }

    @Override
    public final void run() {
        loopView.onItemSelectedListener.onItemSelected(loopView.getCurrentItem());
    }
}
