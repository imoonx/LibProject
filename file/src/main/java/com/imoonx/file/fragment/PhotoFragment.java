package com.imoonx.file.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.imoonx.common.ui.EmptyLayout;
import com.imoonx.file.adapter.ExpandableItemAdapter;
import com.imoonx.file.bean.FolderInfo;
import com.imoonx.file.bean.SubItem;
import com.imoonx.file.recycle.BaseMultiItemQuickAdapter;
import com.imoonx.file.recycle.entity.MultiItemEntity;
import com.imoonx.file.utils.LocalMediaLoader;
import com.imoonx.util.XLog;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片
 */

public class PhotoFragment extends BaseSelectFileFragment {

    private LocalMediaLoader mLocalMediaLoader;

    @Override
    protected boolean getIsNeedQureyFile() {
        return false;
    }

    @Override
    protected void onRefresh() {
        getImgesMethod();
    }

    @Override
    public void initData() {
        getImgesMethod();
    }

    private void getImgesMethod() {
        if (null == mLocalMediaLoader)
            mLocalMediaLoader = new LocalMediaLoader(getActivity(), LocalMediaLoader.TYPE_IMAGE);

        mLocalMediaLoader.loadAllImage(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<FolderInfo> folders) {
                mEntityArrayList.clear();
                for (int i = 0; i < folders.size(); i++) {
                    SubItem subItem = new SubItem(folders.get(i).getName());
                    for (int j = 0; j < folders.get(i).getImages().size(); j++) {
                        subItem.addSubItem(folders.get(i).getImages().get(j));
                    }
                    mEntityArrayList.add(subItem);
                }
                XLog.i(PhotoFragment.class, "size=" + mEntityArrayList.size());
                if (mEntityArrayList.isEmpty()) {
                    mHandler.sendEmptyMessage(HANDLER_ERROR_WHAT);
                    XLog.i(PhotoFragment.class, "sorry,没有读取到文件!");
                } else {
                    Message msg = new Message();
                    msg.what = HANDLER_SUCCESS_WHAT;
                    msg.obj = mEntityArrayList;
                    mHandler.sendMessage(msg);
                    XLog.i(PhotoFragment.class, "文件遍历完成,发送handler");
                }
            }
        });
    }

    private final int HANDLER_SUCCESS_WHAT = 10000;
    private final int HANDLER_ERROR_WHAT = 20000;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            mRefreshLayout.onLoadComplete();
            switch (msg.what) {
                case HANDLER_SUCCESS_WHAT:
                    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    @SuppressWarnings("rawtypes")
                    ArrayList<MultiItemEntity> dataList = (ArrayList) msg.obj;
                    mExpandableItemAdapter.setNewData(dataList);
                    mExpandableItemAdapter.notifyDataSetChanged();
                    break;
                case HANDLER_ERROR_WHAT:
                    mErrorLayout.setErrorType(EmptyLayout.NODATA);
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressWarnings("rawtypes")
    @Override
    protected BaseMultiItemQuickAdapter getRecyclerViewAdapter() {
        return new ExpandableItemAdapter(mEntityArrayList, true, mFileSQLiteHelper);
    }

}
