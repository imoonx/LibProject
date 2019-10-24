package com.imoonx.file.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.imoonx.common.base.BaseFragment;
import com.imoonx.common.ui.EmptyLayout;
import com.imoonx.common.ui.SuperRefreshLayout;
import com.imoonx.file.FileSQLiteHelper;
import com.imoonx.file.R;
import com.imoonx.file.bean.FileInfo;
import com.imoonx.file.recycle.BaseMultiItemQuickAdapter;
import com.imoonx.file.recycle.entity.MultiItemEntity;
import com.imoonx.file.utils.Config;
import com.imoonx.util.XFileUtil;
import com.imoonx.util.XLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 文件选择基类
 */

public abstract class BaseSelectFileFragment extends BaseFragment implements SuperRefreshLayout.SuperRefreshLayoutListener {

    protected RecyclerView mRecyclerView;
    protected ArrayList<MultiItemEntity> mEntityArrayList = new ArrayList<>();
    @SuppressWarnings("rawtypes")
    protected BaseMultiItemQuickAdapter mExpandableItemAdapter;
    protected List<File> mFileList = new ArrayList<>();
    protected FileSQLiteHelper mFileSQLiteHelper;
    protected SuperRefreshLayout mRefreshLayout;
    protected List<FileInfo> fileInfos = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == mFileSQLiteHelper)
            mFileSQLiteHelper = new FileSQLiteHelper(getContext());
        IntentFilter filter = new IntentFilter(Config.INTENT_ACTION_ADAPTER_NOTIFYDATASETCHANGED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            XLog.i(getLogTag(), "接受到数量更改的广播");
            if (TextUtils.equals(intent.getAction(), Config.INTENT_ACTION_ADAPTER_NOTIFYDATASETCHANGED)) {
                if (null != mExpandableItemAdapter)
                    mExpandableItemAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onDestroy() {
        if (null != mFileSQLiteHelper) {
            mFileSQLiteHelper.close();
            mFileSQLiteHelper = null;
        }
        super.onDestroy();
    }

    @Override
    public int getLayoutID() {
        return R.layout.base_select_file_fragment;
    }

    @Override
    public void initWidget(View root) {

        mRecyclerView = root.findViewById(R.id.recycle_view);
        mErrorLayout = root.findViewById(R.id.error_layout);

        mRefreshLayout = root.findViewById(R.id.superRefreshLayout);
        mRefreshLayout.setColorSchemeResources(R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        mRefreshLayout.setSuperRefreshLayoutListener(this);

        if (getIsNeedQureyFile())
            getFileDate();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mExpandableItemAdapter = getRecyclerViewAdapter();
        if (mExpandableItemAdapter != null) {
            mRecyclerView.setAdapter(mExpandableItemAdapter);
        } else {
            throw new NullPointerException("RecyclerView 适配器不能为空");
        }
    }

    protected boolean getIsNeedQureyFile() {
        return true;
    }

    /**
     * 获取所有符合条件的文件
     *
     * @param file
     */
    public void getFileInfo(File file) {
        if (file.isFile()) {
            XLog.i(getLogTag(), this.getClass().getSimpleName() + "*******" + "是文件" + file.getAbsolutePath());
            mFileList.add(file);
        } else {
            XLog.i(getLogTag(), "不是文件" + file.getAbsolutePath());
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File file1 : files) {
                    getFileInfo(file1);
                }
            }
        }
    }

    private void getFileDate() {
        new Thread() {
            @Override
            public void run() {
                File[] files = new File(Environment.getExternalStorageDirectory() + "/").listFiles();
                for (File file : files) {
                    getFileInfo(file);
                }
                Observable.from(mFileList).flatMap(new Func1<File, Observable<File>>() {
                    @Override
                    public Observable<File> call(File file) {
                        return listFiles(file);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<File>() {

                            @Override
                            public void onCompleted() {
                                onQureyCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                onQureyError(e.toString());
                            }

                            @Override
                            public void onNext(File file) {
                                XLog.i(getLogTag(), "onNext()");
                                FileInfo fileInfo = com.imoonx.file.FileUtil.getFileInfoFromFile(file);
                                XLog.i(getLogTag(), "文件路径：：：" + fileInfo.getFilePath());
                                fileInfos.add(fileInfo);
                            }
                        });
            }
        }.start();

    }

    protected Class<?> getLogTag() {
        return this.getClass();
    }

    public Observable<File> listFiles(final File f) {
        if (f.isDirectory()) {
            return Observable.from(f.listFiles()).flatMap(new Func1<File, Observable<File>>() {
                @Override
                public Observable<File> call(File file) {
                    return listFiles(file);
                }
            });
        } else {
            return Observable.just(f).filter(new Func1<File, Boolean>() {
                @Override
                public Boolean call(File file) {
                    XLog.i(BaseSelectFileFragment.class, "length=" + mFileType.length);
                    return f.exists() && f.canRead() && XFileUtil.checkSuffix(f.getAbsolutePath(), mFileType);
                }
            });
        }
    }

    protected void onQureyCompleted() {
        XLog.i(getLogTag(), "fileInfos.size=" + fileInfos.size());
        mEntityArrayList.clear();
        mRefreshLayout.onLoadComplete();
    }

    // 在子线程中
    protected void onQureyError(String string) {
    }

    @SuppressWarnings("rawtypes")
    protected BaseMultiItemQuickAdapter getRecyclerViewAdapter() {
        return null;
    }

    // 设置文件类型
    public String[] mFileType;
    protected EmptyLayout mErrorLayout;

    protected void onRefresh() {
        getFileDate();
    }

    @Override
    public void onRefreshing() {
        XLog.i(getLogTag(), "onRefreshing");
        if (null != mFileList)
            mFileList.clear();
        if (null != fileInfos)
            fileInfos.clear();
        onRefresh();
    }

    @Override
    public void onLoadMore() {
    }

}
