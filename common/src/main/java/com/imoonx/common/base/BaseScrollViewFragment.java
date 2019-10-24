package com.imoonx.common.base;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.imoonx.common.R;
import com.imoonx.common.ui.EmptyLayout;
import com.imoonx.common.ui.SuperRefreshLayoutScrollView;
import com.imoonx.http.callback.StringCallback;
import com.imoonx.util.Res;
import com.imoonx.util.TDevice;
import com.imoonx.util.XLog;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.imoonx.common.ui.EmptyLayout.HIDE_LAYOUT;
import static com.imoonx.common.ui.EmptyLayout.NODATA_ENABLE_CLICK;

/**
 * scrollview 列表
 */
public abstract class BaseScrollViewFragment<T> extends BaseFragment implements BaseListAdapter.Callback,
        AdapterView.OnItemClickListener, SuperRefreshLayoutScrollView.SuperRefreshLayoutListener,
        SuperRefreshLayoutScrollView.OnScrollChangeListener {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_LOADING = 1;
    public static final int TYPE_NO_MORE = 2;
    public static final int TYPE_ERROR = 3;
    public static final int TYPE_NET_ERROR = 4;
    public static final int TYPE_NET_SERVICE = 5;
    protected ListView mListView;
    protected SuperRefreshLayoutScrollView mRefreshLayout;
    protected EmptyLayout mErrorLayout;
    protected BaseListAdapter<T> mAdapter;
    protected boolean mIsRefresh;
    protected ProgressBar mFooterProgressBar;
    protected TextView mFooterText;
    protected int mCurrentPager = 1;
    protected LinearLayout mLoadMoreLinearLayout;
    protected List<T> mList;

    public int getLayoutID() {
        return R.layout.fragment_base_scrollview;
    }

    @SuppressLint({"InflateParams"})
    public void initWidget(View root) {
        mListView = root.findViewById(R.id.listView);

        mRefreshLayout = root.findViewById(R.id.superRefreshLayout);
        mRefreshLayout.setColorSchemeResources(R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        mErrorLayout = root.findViewById(R.id.error_layout);

        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mRefreshLayout.setSuperRefreshOnScrollChangeListener(this);

        mLoadMoreLinearLayout = root.findViewById(R.id.loadmore_ll);
        mFooterText = root.findViewById(R.id.tv_footer);
        mFooterProgressBar = root.findViewById(R.id.pb_footer);

        mListView.setOnItemClickListener(this);
        mErrorLayout.setOnLayoutClickListener(this);
        mList = new ArrayList<>();
    }

    /**
     * 底部提醒显示
     */
    protected void setLoadMoreVisibility() {
        if (null != mLoadMoreLinearLayout && mLoadMoreLinearLayout.getVisibility() == View.GONE)
            mLoadMoreLinearLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 底部提醒隐藏
     */
    protected void setLoadMoreGone() {
        if (null != mLoadMoreLinearLayout && mLoadMoreLinearLayout.getVisibility() == View.VISIBLE)
            mLoadMoreLinearLayout.setVisibility(View.GONE);
    }

    public void initData() {
        mAdapter = getListAdapter();
        mListView.setAdapter(mAdapter);
        if (isFirstRefresh()) {
            onRefreshing();
        } else {
            mErrorLayout.setErrorType(HIDE_LAYOUT);
        }
    }

    protected boolean isFirstRefresh() {
        return true;
    }

    protected Class getClassName() {
        return this.getClass();
    }

    protected StringCallback mCallback = new StringCallback() {

        public void onError(Call call, Exception e) {
            XLog.e(getClassName(), "onError" + e.toString());
            onRequestError(e);
            onRequestFinish();
        }

        public void onResponse(String respose) {
            XLog.i(getClassName(), "onResponse" + respose);
            List<T> list = onParse(respose);
            setListData(list);
            onRequestFinish();
        }
    };

    protected abstract List<T> onParse(String paramString);

    public void onClick(View v) {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        onRefreshing();
    }

    public void onRefreshing() {
        mIsRefresh = true;
        mCurrentPager = 1;
        requestData();
    }

    public void onLoadMore() {
        if (isNeedLoadMore()) {
            mCurrentPager += 1;
            requestData();
        } else {
            onRequestFinish();
        }
    }

    protected void requestData() {
        onRequestStart();
        if (!TDevice.hasInternet()) {
            setFooterType(TYPE_NET_ERROR);
            return;
        }
        setFooterType(TYPE_LOADING);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    protected void onRequestStart() {
    }

    public void onResume() {
        super.onResume();
    }

    protected void onRequestError(Exception e) {
        if (!TDevice.hasInternet()) {
            setFooterType(TYPE_NET_ERROR);
        } else {
            setFooterType(TYPE_NET_SERVICE);
        }
        if (mAdapter.getDatas().size() == 0) {
            mErrorLayout.setErrorType(NODATA_ENABLE_CLICK);
        }
    }

    protected void onRequestFinish() {
        onComplete();
    }

    protected void onComplete() {
        mRefreshLayout.onLoadComplete();
        mIsRefresh = false;
    }

    protected void setListData(List<T> list) {
        if (mIsRefresh) {
            onRequestFinish();
            mAdapter.clear();
            mAdapter.addItem(list);
        } else {
            mAdapter.addItem(list);
        }
        if (list.size() < 20) {
            setFooterType(TYPE_NO_MORE);
        }
        if (mAdapter.getDatas().size() > 0) {
            mErrorLayout.setErrorType(HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
        } else if (needNoData()) {
            mErrorLayout.setErrorType(NODATA_ENABLE_CLICK);
        } else {
            mErrorLayout.setErrorType(HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    protected boolean needNoData() {
        return true;
    }

    protected abstract BaseListAdapter<T> getListAdapter();

    protected boolean isNeedLoadMore() {
        return true;
    }

    protected void setFooterType(int type) {
        try {
            switch (type) {
                case TYPE_NORMAL:
                case TYPE_LOADING:
                    mFooterText.setText(Res.getString(com.imoonx.common.R.string.footer_type_loading));
                    mFooterProgressBar.setVisibility(View.VISIBLE);
                    break;
                case TYPE_NET_ERROR:
                    mFooterText.setText(Res.getString(com.imoonx.common.R.string.footer_type_net_error));
                    mFooterProgressBar.setVisibility(View.GONE);
                    break;
                case TYPE_ERROR:
                    mFooterText.setText(Res.getString(com.imoonx.common.R.string.footer_type_error));
                    mFooterProgressBar.setVisibility(View.GONE);
                    break;
                case TYPE_NO_MORE:
                    mFooterText.setText(Res.getString(com.imoonx.common.R.string.footer_type_not_more));
                    mFooterProgressBar.setVisibility(View.GONE);
                    mLoadMoreLinearLayout.setEnabled(false);
                    break;
                case TYPE_NET_SERVICE:
                    mFooterText.setText(Res.getString(com.imoonx.common.R.string.footer_type_net_service));
                    mFooterProgressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            XLog.e(BaseScrollViewFragment.class, e.toString());
        }
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
    }

    @Override
    public RequestManager getImgLoader() {
        return super.getImageLoader();
    }
}
