package com.imoonx.common.base;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.imoonx.common.R;
import com.imoonx.common.manager.AppOperator;
import com.imoonx.common.ui.EmptyLayout;
import com.imoonx.common.ui.RecyclerRefreshLayout;
import com.imoonx.http.callback.StringCallback;
import com.imoonx.util.XLog;

import java.util.Date;
import java.util.List;

import okhttp3.Call;

/**
 * BaseRecyclerViewFragment 基类
 */
public abstract class BaseRecyclerViewFragment<T> extends BaseFragment implements RecyclerRefreshLayout.SuperRefreshLayoutListener,
        BaseRecyclerAdapter.OnItemClickListener,
        View.OnClickListener,
        BaseGeneralRecyclerAdapter.Callback {
    protected BaseRecyclerAdapter<T> mAdapter;
    protected RecyclerView mRecyclerView;
    protected RecyclerRefreshLayout mRefreshLayout;
    protected boolean mIsRefresh;
    protected String CACHE_NAME = getClass().getName();
    protected EmptyLayout mErrorLayout;
    protected int mCurrentPager = 1;

    @Override
    public int getLayoutID() {
        return R.layout.fragment_base_recycler_view;
    }

    @Override
    protected void initWidget(View root) {
        mRecyclerView = root.findViewById(R.id.recyclerView);
        mRefreshLayout = root.findViewById(R.id.refreshLayout);
        mErrorLayout = root.findViewById(R.id.error_layout);
    }

    @Override
    public void initData() {
        mAdapter = getRecyclerAdapter();
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, false);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mErrorLayout.setOnLayoutClickListener(this);
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, false);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);

        boolean isNeedEmptyView = isNeedEmptyView();

        if (isNeedEmptyView) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            mRefreshLayout.setVisibility(View.GONE);
            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    onRefreshing();
                }
            });
        } else {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                    onRefreshing();
                }
            });
        }
    }


    protected StringCallback mCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            XLog.e(this.getClass(), "onError" + e.toString());
            onRequestError();
            onRequestFinish();
        }

        @Override
        public void onResponse(String respose) {
            XLog.i(this.getClass(), "onResponse" + respose);
            List<T> list = onParse(respose);
            setListData(list);
            onRequestFinish();
        }
    };

    protected abstract List<T> onParse(String respose);

    @Override
    public void onClick(View v) {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        onRefreshing();
    }

    @Override
    public void onItemClick(int position, long itemId) {
    }

    @Override
    public void onRefreshing() {
        mIsRefresh = true;
        mCurrentPager = 1;
        requestData();
    }

    @Override
    public void onLoadMore() {
        mCurrentPager++;
        requestData();
    }

    protected void requestData() {
    }

    protected void onRequestStart() {
    }

    protected void onRequestFinish() {
        onComplete();
    }

    protected void onRequestError() {
        if (mAdapter.getItems().size() == 0) {
            if (isNeedEmptyView()) mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
        }
    }

    protected void onComplete() {
        mRefreshLayout.onComplete();
        mIsRefresh = false;
    }

    protected void setListData(List<T> list) {
        if (mIsRefresh) {
            mAdapter.clear();
            mAdapter.addAll(list);
            mRefreshLayout.setCanLoadMore(true);
        } else {
            mAdapter.addAll(list);
        }
        mAdapter.setState(list.isEmpty() || list.size() < 10 ? BaseRecyclerAdapter.STATE_NO_MORE : BaseRecyclerAdapter.STATE_LOADING, true);
        if (mAdapter.getItems().size() > 0) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            mErrorLayout.setErrorType(isNeedEmptyView() ? EmptyLayout.NODATA : EmptyLayout.HIDE_LAYOUT);
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected abstract BaseRecyclerAdapter<T> getRecyclerAdapter();


    @Override
    public Date getSystemTime() {
        return new Date();
    }

    /**
     * 需要缓存
     *
     * @return isNeedCache
     */
    protected boolean isNeedCache() {
        return true;
    }

    /**
     * 需要空的View
     *
     * @return isNeedEmptyView
     */
    protected boolean isNeedEmptyView() {
        return true;
    }
}
