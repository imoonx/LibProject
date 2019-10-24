package com.imoonx.time;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.imoonx.util.Res;

public class ScrollBasePickerView {

    private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

    private Context context;
    protected ViewGroup contentContainer;
    private ViewGroup decorView;
    private ViewGroup rootView;

    private OnDismissListener onDismissListener;
    private boolean isDismissing;

    public boolean isDismissing() {
        return isDismissing;
    }

    private Animation outAnim;
    private Animation inAnim;
    private int gravity = Gravity.BOTTOM;

    public ScrollBasePickerView(Context context) {
        this.context = context;

        initViews();
        init();
        initEvents();
    }

    private void initViews() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        decorView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = (ViewGroup) layoutInflater.inflate(R.layout.scrolltime_view, decorView, false);
        contentContainer = rootView.findViewById(R.id.content_container);
    }

    private void init() {
        inAnim = getInAnimation();
        outAnim = getOutAnimation();
    }

    private void initEvents() {
    }

    private void onAttached(View view) {
        decorView.addView(view);
        contentContainer.startAnimation(inAnim);
    }

    public void show() {
        if (isShowing()) {
            return;
        }
        onAttached(rootView);
    }

    public boolean isShowing() {
        View view = decorView.findViewById(R.id.content_container);
        return view != null;
    }

    public void dismiss() {
        if (isDismissing) {
            return;
        }

        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        decorView.removeView(rootView);
                        isDismissing = false;
                        if (onDismissListener != null) {
                            onDismissListener.onDismiss(ScrollBasePickerView.this);
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        contentContainer.startAnimation(outAnim);
        isDismissing = true;
    }

    public Animation getInAnimation() {
        int res = ScrollTimeAnimateUtil.getAnimationResource(context, this.gravity, true);
        return AnimationUtils.loadAnimation(context, res);
    }

    public Animation getOutAnimation() {
        int res = ScrollTimeAnimateUtil.getAnimationResource(context, this.gravity, false);
        return AnimationUtils.loadAnimation(context, res);
    }

    public ScrollBasePickerView setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public ScrollBasePickerView setCancelable(boolean isCancelable) {
        View view = rootView.findViewById(Res.getWidgetID("content_container"));
        if (isCancelable) {
            view.setOnTouchListener(onCancelableTouchListener);
        } else {
            view.setOnTouchListener(null);
        }
        return this;
    }

    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
            }
            return false;
        }
    };

    public View findViewById(int id) {
        return contentContainer.findViewById(id);
    }
}
