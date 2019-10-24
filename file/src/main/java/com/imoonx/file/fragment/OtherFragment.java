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
 * 其他
 */

public class OtherFragment extends BaseSelectFileFragment {

    @Override
    protected void onQureyCompleted() {
        super.onQureyCompleted();
        if (fileInfos.size() > 0) {
            SubItem ZipItem = new SubItem(Res.getString(R.string.zip));
            SubItem APPItem = new SubItem(Res.getString(R.string.apk));
            SubItem OtherItem = new SubItem(Res.getString(R.string.other));
            for (int j = 0; j < fileInfos.size(); j++) {
                if (XFileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"zip"}))
                    ZipItem.addSubItem(fileInfos.get(j));
                else if (XFileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"apk"}))
                    APPItem.addSubItem(fileInfos.get(j));
                else
                    OtherItem.addSubItem(fileInfos.get(j));
            }

            mEntityArrayList.add(ZipItem);
            mEntityArrayList.add(APPItem);
            mEntityArrayList.add(OtherItem);
            Message msg = new Message();
            msg.what = HANDLER_SUCCESS_WHAT;
            msg.obj = mEntityArrayList;
            mHandler.sendMessage(msg);
            XLog.i(DocFragment.class, "文件遍历完成,发送handler");
        } else {
            mHandler.sendEmptyMessage(HANDLER_ERROR_WHAT);
            XLog.i(DocFragment.class, "sorry,没有读取到文件!");
        }

    }

    @Override
    protected void onQureyError(String string) {
        XLog.e(DocFragment.class, "sorry,没有读取到文件!");
    }

    @Override
    protected BaseMultiItemQuickAdapter<? extends MultiItemEntity, ? extends BaseViewHolder> getRecyclerViewAdapter() {
        return new ExpandableItemAdapter(mEntityArrayList, false, mFileSQLiteHelper);
    }

    @Override
    public void initWidget(View root) {
        super.initWidget(root);
        mFileType = new String[]{"zip", "apk", "xml"};
    }

    private final int HANDLER_SUCCESS_WHAT = 10000;
    private final int HANDLER_ERROR_WHAT = 20000;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
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
            }
        }
    };
}
