package com.imoonx.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.imoonx.common.R;

/**
 * 分享PopWindows
 */
@SuppressLint("InflateParams")
public class SharePopupWindow extends PopupWindow {

    public interface PopWindowsDismissListener {
        void onSetBackgroundAlpha();
    }

    protected Context mContext;
    protected PopWindowsDismissListener mDismissListener;

    public SharePopupWindow(Context context, PopWindowsDismissListener listener) {
        this(context, listener, null);
    }

    public SharePopupWindow(Context context, PopWindowsDismissListener listener, int layoutId) {
        this(context, listener, View.inflate(context, layoutId, null));
    }

    public SharePopupWindow(Context context, PopWindowsDismissListener listener, View view) {
        this.mContext = context;
        this.mDismissListener = listener;
        initView(view);
    }

    protected void initView(View view) {
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setAnimationStyle(R.style.popwindow_animation);
        if (null != view)
            this.setContentView(view);
    }

    /**
     * show location
     *
     * @param rootview 跟布局
     */
    public void showAtLocation(View rootview) {
        if (!isShowing()) {
            this.showAtLocation(rootview, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    @Override
    public void dismiss() {
        if (mDismissListener != null) {
            mDismissListener.onSetBackgroundAlpha();
        }
        super.dismiss();
    }
}
