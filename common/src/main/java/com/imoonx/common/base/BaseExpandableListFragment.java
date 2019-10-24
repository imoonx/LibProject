package com.imoonx.common.base;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.imoonx.common.R;
import com.imoonx.common.manager.AppOperator;
import com.imoonx.common.ui.EmptyLayout;
import com.imoonx.common.ui.SuperRefreshLayout;
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
 * 列表类Fragment
 */
public abstract class BaseExpandableListFragment<T> extends BaseFragment implements SuperRefreshLayout.SuperRefreshLayoutListener,
        AdapterView.OnItemClickListener, BaseExpandListAdapter.Callback, SuperRefreshLayout.OnScrollChangeListener {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_LOADING = 1;
    public static final int TYPE_NO_MORE = 2;
    public static final int TYPE_ERROR = 3;
    public static final int TYPE_NET_ERROR = 4;
    public static final int TYPE_NET_SERVICE = 5;
    public static final int TYPE_CLICK_LOAD_MORE = 6;
    protected ExpandableListView mExpandableListView;
    protected SuperRefreshLayout mRefreshLayout;
    protected EmptyLayout mErrorLayout;
    protected BaseExpandListAdapter<T> mAdapter;
    protected boolean mIsRefresh;
    protected View mFooterView;
    protected ProgressBar mFooterProgressBar;
    protected TextView mFooterText;
    protected int mCurrentPager = 1;
    protected List<T> mList;

    public int getLayoutID() {
        return R.layout.fragment_base_expandable_listview;
    }

    @SuppressLint({"InflateParams"})
    public void initWidget(View root) {
        mExpandableListView = root.findViewById(R.id.expandable_listview);

        mRefreshLayout = root.findViewById(R.id.superRefreshLayout);
        mRefreshLayout.setColorSchemeResources(R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        mErrorLayout = root.findViewById(R.id.error_layout);
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mRefreshLayout.setSuperRefreshOnScrollChangeListener(this);
        mFooterView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_view_footer, null);

        mFooterText = mFooterView.findViewById(R.id.tv_footer);
        mFooterProgressBar = mFooterView.findViewById(R.id.pb_footer);

        mExpandableListView.setOnItemClickListener(this);
        mErrorLayout.setOnLayoutClickListener(this);

        if (isNeedFooter()) {
            mExpandableListView.addFooterView(mFooterView, null, false);
        }
        //定义List 避免在每个子类中都定义
        mList = new ArrayList<>();
    }

    public void initData() {
        mAdapter = getListAdapter();
        mExpandableListView.setAdapter(mAdapter);
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
        if (v.getId() == R.id.img_error_layout) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            onRefreshing();
        }
    }

    public void onRefreshing() {
        mFooterView.setEnabled(false);
        mIsRefresh = true;
        mCurrentPager = 1;
        requestData();
    }

    public void onLoadMore() {
        if (isNeedFooter()) {
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
        mFooterView.setEnabled(true);
        onComplete();
    }

    protected void onComplete() {
        mRefreshLayout.onLoadComplete();
        mIsRefresh = false;
    }

    /**
     * 是否有数据
     */
    protected boolean isHaving;

    protected void setListData(List<T> list) {
        if (mIsRefresh) {
            onRequestFinish();
            mAdapter.clear();
            mAdapter.addItem(list);
            mRefreshLayout.setCanLoadMore();
        } else {
            mAdapter.addItem(list);
        }
        if (isHaving) {
            setFooterType(TYPE_CLICK_LOAD_MORE);
        } else {
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

    protected abstract BaseExpandListAdapter<T> getListAdapter();

    protected boolean isNeedFooter() {
        return true;
    }

    protected void setFooterType(int type) {
        mFooterView.setClickable(true);
        try {
            switch (type) {
                case TYPE_NORMAL:
                case TYPE_LOADING:
                    mFooterText.setText(Res.getString(R.string.footer_type_loading));
                    mFooterProgressBar.setVisibility(View.VISIBLE);
                    break;
                case TYPE_NET_ERROR:
                    mFooterText.setText(Res.getString(R.string.footer_type_net_error));
                    mFooterProgressBar.setVisibility(View.GONE);
                    break;
                case TYPE_ERROR:
                    mFooterText.setText(Res.getString(R.string.footer_type_error));
                    mFooterProgressBar.setVisibility(View.GONE);
                    break;
                case TYPE_NO_MORE:
                    mFooterText.setText(Res.getString(R.string.footer_type_not_more));
                    mFooterProgressBar.setVisibility(View.GONE);
                    mFooterView.setClickable(false);
                    break;
                case TYPE_NET_SERVICE:
                    mFooterText.setText(Res.getString(R.string.footer_type_net_service));
                    mFooterProgressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            XLog.e(this.getClass(), e);
        }
    }

    public RequestManager getImgLoader() {
        return super.getImageLoader();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }
}