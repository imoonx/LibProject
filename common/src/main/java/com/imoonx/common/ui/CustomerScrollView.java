package com.imoonx.common.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * 自定义ScrollView
 */
public class CustomerScrollView extends ScrollView {

    private OnScrollChangeListener mOnScrollChangeListener;

    public CustomerScrollView(Context context) {
        this(context, null);
    }

    public CustomerScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomerScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
        if (null != mOnScrollChangeListener)
            mOnScrollChangeListener.onScrollChange(this, scrollX, scrollY, oldScrollX, oldScrollY);
    }

    public void setOnScrollChangeListener(OnScrollChangeListener listener) {
        this.mOnScrollChangeListener = listener;
    }

    public interface OnScrollChangeListener {

        void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);

    }

}
