package com.imoonx.file.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.imoonx.common.ui.EmptyLayout;
import com.imoonx.util.Res;
import com.imoonx.file.R;
import com.imoonx.file.adapter.ExpandableItemAdapter;
import com.imoonx.file.bean.SubItem;
import com.imoonx.file.recycle.BaseMultiItemQuickAdapter;
import com.imoonx.file.recycle.BaseViewHolder;
import com.imoonx.file.recycle.entity.MultiItemEntity;
import com.imoonx.util.XFileUtil;
import com.imoonx.util.XLog;

import java.util.ArrayList;

/**
 * 影音
 */

public class AVFragment extends BaseSelectFileFragment {

    @Override
    protected void onQureyCompleted() {
        super.onQureyCompleted();
        if (fileInfos.size() > 0) {
            SubItem musicItem = new SubItem(Res.getString(R.string.file_music));
            SubItem videoItem = new SubItem(Res.getString(R.string.file_video));
            SubItem recordItem = new SubItem(Res.getString(R.string.file_audio));

            for (int j = 0; j < fileInfos.size(); j++) {
                if (XFileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"mp3"})) {
                    musicItem.addSubItem(fileInfos.get(j));
                } else if (XFileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"wmv", "rmvb", "avi", "mp4"})) {
                    videoItem.addSubItem(fileInfos.get(j));
                } else if (XFileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"wav", "aac", "amr"})) {
                    recordItem.addSubItem(fileInfos.get(j));
                }
            }

            mEntityArrayList.add(musicItem);
            mEntityArrayList.add(videoItem);
            mEntityArrayList.add(recordItem);

            Message msg = new Message();
            msg.what = HANDLER_SUCCESS_WHAT;
            msg.obj = mEntityArrayList;
            mHandler.sendMessage(msg);
            XLog.i(AVFragment.class, "文件遍历完成,发送handler");

        } else {
            XLog.i(AVFragment.class, "sorry,没有读取到文件!");
            mHandler.sendEmptyMessage(HANDLER_ERROR_WHAT);
        }
    }

    @Override
    protected void onQureyError(String string) {
        mHandler.sendEmptyMessage(HANDLER_ERROR_WHAT);
    }

    @Override
    protected BaseMultiItemQuickAdapter<? extends MultiItemEntity, ? extends BaseViewHolder> getRecyclerViewAdapter() {
        return new ExpandableItemAdapter(mEntityArrayList, false, mFileSQLiteHelper);
    }

    @Override
    public void initWidget(View root) {
        super.initWidget(root);
        mFileType = new String[]{"mp3", "aac", "amr", "wav", "wmv", "avi", "mp4", "rmvb"};
    }

    private final int HANDLER_SUCCESS_WHAT = 10000;
    private final int HANDLER_ERROR_WHAT = 20000;

    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SUCCESS_WHAT:
                    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    ArrayList<MultiItemEntity> dataList = (ArrayList) msg.obj;
                    mExpandableItemAdapter.setNewData(dataList);
                    mExpandableItemAdapter.notifyDataSetChanged();
                    break;
                case HANDLER_ERROR_WHAT:
                    mErrorLayout.setErrorType(EmptyLayout.NODATA);
                    break;
            }
        }
    };

}
