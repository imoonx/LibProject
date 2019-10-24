package com.imoonx.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

import com.imoonx.util.XLog;

/**
 * scrollview 下拉刷新 加载更多
 */
public class SuperRefreshLayoutScrollView extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener, CustomerScrollView.OnScrollChangeListener {

    private CustomerScrollView mScrollView;

    private SuperRefreshLayoutListener mListener;

    private boolean mIsOnLoading = false;

    private OnScrollChangeListener mOnScrollChangeListener;

    public SuperRefreshLayoutScrollView(@NonNull Context context) {
        this(context, null);
    }

    public SuperRefreshLayoutScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if (mListener != null && !mIsOnLoading) {
            setIsOnLoading(true);
            mListener.onRefreshing();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView对象
        if (mScrollView == null) {
            int child = getChildCount();
            if (child > 0) {
                View childView = getChildAt(0);
                if (childView instanceof CustomerScrollView) {
                    mScrollView = (CustomerScrollView) childView;
                    mScrollView.setOnScrollChangeListener(this);
                }
            }
        }
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     * <p>
     * 添加判断
     */
    private void loadData() {
        if (null != mListener && !mIsOnLoading) {
            XLog.i(SuperRefreshLayoutScrollView.class, "加载更多");
            setIsOnLoading(true);
            mListener.onLoadMore();
        }
    }

    /**
     * 设置正在加载
     *
     * @param loading loading
     */
    public void setIsOnLoading(boolean loading) {
        mIsOnLoading = loading;
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (scrollY == 0) {
            //顶部 可以刷新
            XLog.i(SuperRefreshLayoutScrollView.class, "滑到顶部");
        }
        if (scrollY - oldScrollX >= 0) {
            //向上滑动
            XLog.i(SuperRefreshLayoutScrollView.class, "向上滑动");
        }
        if (scrollY - oldScrollX < 0) {
            //向下滑动
            XLog.i(SuperRefreshLayoutScrollView.class, "向下滑动");
        }
        if (scrollY + mScrollView.getHeight() == mScrollView.getChildAt(0).getHeight() && scrollY - oldScrollX >= 0) {
            //底部且向上滑动 可以加载更多
            XLog.i(SuperRefreshLayoutScrollView.class, "滑到底部");
            loadData();
        }
        if (null != mOnScrollChangeListener)
            mOnScrollChangeListener.onScrollChange(v, scrollX, scrollY, oldScrollX, oldScrollY);
    }

    public interface SuperRefreshLayoutListener {
        void onRefreshing();

        void onLoadMore();
    }

    /**
     * 加载结束记得调用
     */
    public void onLoadComplete() {
        setIsOnLoading(false);
        setRefreshing(false);
    }

    /**
     * set
     *
     * @param loadListener loadListener
     */
    public void setSuperRefreshLayoutListener(SuperRefreshLayoutListener loadListener) {
        mListener = loadListener;
    }

    public void setSuperRefreshOnScrollChangeListener(OnScrollChangeListener listener) {
        mOnScrollChangeListener = listener;
    }

    public interface OnScrollChangeListener {

        void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);

    }
}

