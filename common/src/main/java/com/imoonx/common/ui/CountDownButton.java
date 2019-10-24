package com.imoonx.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

import com.imoonx.common.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 36238
 * @ProgectName: android-app
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2016/11/17 16:10
 */
public class CountDownButton extends AppCompatButton implements View.OnClickListener {

    private Timer mTimer;
    private OnClickListener mOnClickListener;
    private TimerTask timerTask;
    private String mBeforeText;
    private String mAfterText;
    private String mRefreshText;
    private int mCountTime;
    private int countTime;

    public CountDownButton(Context context) {
        this(context, null);
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context, attrs, defStyleAttr);
//    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.count_down_button, defStyleAttr, 0);

        mBeforeText = a.getString(R.styleable.count_down_button_count_down_before);
        mAfterText = a.getString(R.styleable.count_down_button_count_down_after);
        mRefreshText = a.getString(R.styleable.count_down_button_count_down_refresh);
        mCountTime = a.getInteger(R.styleable.count_down_button_count_down_time, 60);
        this.countTime = mCountTime;
//        if (!TextUtils.isEmpty(getText())) {
//            beforeText = getText().toString().trim();
//        }
        this.setText(mBeforeText);
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //start();
        if (mOnClickListener != null) {
            mOnClickListener.onClick(view);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener onclickListener) {
        if (onclickListener instanceof CountDownButton) {
            super.setOnClickListener(onclickListener);
        } else {
            this.mOnClickListener = onclickListener;
        }
    }

    /**
     * 开始倒计时
     */
    public void start() {
        initTimer();
        setText(mCountTime + mAfterText);
        setEnabled(false);
        mTimer.schedule(timerTask, 0, 1000);
    }

    /**
     * 初始化时间
     */
    private void initTimer() {
        mTimer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
    }


    public void setLength(int length) {
        this.mCountTime = length;
    }

    public void setBeforeText(String beforeText) {
        this.mBeforeText = beforeText;
    }

    public void setAfterText(String afterText) {
        this.mAfterText = afterText;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setText(mCountTime + mAfterText);
            mCountTime -= 1;
            if (mCountTime < 0) {
                setEnabled(true);
                setText(mRefreshText);
                clearTimer();
                mCountTime = countTime;
            }
        }
    };


    private void clearTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void clear() {
        clearTimer();
    }

    @Override
    public void onDetachedFromWindow() {
        clearTimer();
        super.onDetachedFromWindow();
    }
}
