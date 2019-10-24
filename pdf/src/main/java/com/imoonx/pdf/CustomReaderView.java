package com.imoonx.pdf;

import android.content.Context;
import android.util.AttributeSet;

import com.imoonx.pdf.viewer.ReaderView;
import com.imoonx.util.XLog;

/**
 * Created by 36238 on 2019/4/4 星期四
 */
public class CustomReaderView extends ReaderView {

    private OnPageListener onPageListener;

    public CustomReaderView(Context context) {
        this(context,null);
    }

    public CustomReaderView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomReaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMoveToChild(int i) {
        if (null != onPageListener)
            onPageListener.onMoveToChild(i);
        super.onMoveToChild(i);
    }

    @Override
    protected void onTapMainDocArea() {
        super.onTapMainDocArea();
        if (null != onPageListener)
            onPageListener.onTapMainDocArea();
    }

    @Override
    protected void onDocMotion() {
        super.onDocMotion();
        if (null != onPageListener)
            onPageListener.onDocMotion();
    }

    public void setOnPageListener(OnPageListener onPageListener) {
        this.onPageListener = onPageListener;
    }

    public interface OnPageListener {

        void onMoveToChild(int i);

        void onTapMainDocArea();

        void onDocMotion();
    }
}
