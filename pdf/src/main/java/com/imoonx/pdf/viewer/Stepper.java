package com.imoonx.pdf.viewer;

import android.annotation.SuppressLint;
import android.view.View;

public class Stepper {

    private View mPoster;
    private Runnable mTask;
    private boolean mPending;

    public Stepper(View v, Runnable r) {
        mPoster = v;
        mTask = r;
        mPending = false;
    }

    @SuppressLint("NewApi")
    public void prod() {
        if (!mPending) {
            mPoster.postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    mPending = false;
                    mTask.run();
                }
            });
        }
    }
}