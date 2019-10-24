package com.imoonx.common.base;

import android.view.View;
import android.view.ViewStub;

import com.imoonx.common.R;
import com.imoonx.common.manager.AppOperator;
import com.imoonx.common.ui.EmptyLayout;
import com.imoonx.common.ui.SuperRefreshLayout;
import com.imoonx.http.callback.StringCallback;
import com.imoonx.util.XLog;

import okhttp3.Call;

import static com.imoonx.common.ui.EmptyLayout.HIDE_LAYOUT;

/**
 * 普通fragment 下拉刷新
 */

public abstract class BaseGenFragment extends BaseFragment implements SuperRefreshLayout.SuperRefreshLayoutListener {

    @Override
    protected void initWidgetBefore(View rootView) {
        ViewStub stub = rootView.findViewById(R.id.lay_content);
        stub.setLayoutResource(getContentLayoutId());
        stub.inflate();
    }

    @Override
    public void initWidget(View root) {
        XLog.i(this.getClass(), "当前加载类" + this.getClass().getSimpleName());
        mRefreshLayout = root.findViewById(R.id.superRefreshLayout);
        mRefreshLayout.setColorSchemeResources(R.color.swiperefresh_color1,
                R.color.swiperefresh_color2, R.color.swiperefresh_color3,
                R.color.swiperefresh_color4);
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mErrorLayout = root.findViewById(R.id.error_layout);
        mErrorLayout.setOnLayoutClickListener(this);
        mErrorLayout.setBackgroundResource(setEmptyLayoutBg());
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
    }

    protected boolean isFirstRefresh() {
        return true;
    }

    @Override
    public void initData() {
        if (isFirstRefresh()) {
            AppOperator.runOnThread(new Runnable() {
                public void run() {
                    onRefreshing();
                }
            });
        } else {
            mErrorLayout.setErrorType(HIDE_LAYOUT);
        }
    }

    protected void requestDate() {
    }

    protected int setEmptyLayoutBg() {
        return R.drawable.transparent_bg;
    }

    protected abstract int getContentLayoutId();

    @Override
    public int getLayoutID() {
        return R.layout.base_fragment_gen;
    }

    protected Class getClassName() {
        return this.getClass();
    }

    protected StringCallback mCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            XLog.e(getClassName(), "onError" + e.toString());
            onLoadError(e.toString());
            onRequestFinish();
        }

        @Override
        public void onResponse(String respose) {
            XLog.i(getClassName(), "onResponse" + respose);
            onParse(respose);
            onRequestFinish();
        }
    };

    protected EmptyLayout mErrorLayout;

    protected SuperRefreshLayout mRefreshLayout;

    protected void onLoadError(String string) {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
    }

    @Override
    public void onClick(View v) {
        XLog.i(this.getClass(), "点击加载");
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        onRefreshing();
    }

    protected void onParse(String respose) {
    }

    protected void onRequestFinish() {
        onComplete();
    }

    protected void onComplete() {
        mRefreshLayout.onLoadComplete();
    }

    @Override
    public void onRefreshing() {
        requestDate();
    }

    @Override
    public void onLoadMore() {
    }

}
