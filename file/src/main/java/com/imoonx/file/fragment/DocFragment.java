package com.imoonx.file.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.imoonx.common.ui.EmptyLayout;
import com.imoonx.file.adapter.ExpandableItemAdapter;
import com.imoonx.file.bean.SubItem;
import com.imoonx.file.recycle.BaseMultiItemQuickAdapter;
import com.imoonx.file.recycle.BaseViewHolder;
import com.imoonx.file.recycle.entity.MultiItemEntity;
import com.imoonx.util.XFileUtil;
import com.imoonx.util.XLog;

import java.util.ArrayList;

/**
 * 文档
 */

public class DocFragment extends BaseSelectFileFragment {

    @Override
    protected void onQureyCompleted() {
        super.onQureyCompleted();
        if (fileInfos.size() > 0) {
            SubItem wordItem = new SubItem("WORD");
            SubItem excelItem = new SubItem("EXCEL");
            SubItem pdfItem = new SubItem("PDF");
            SubItem PPTItem = new SubItem("PPT");
            SubItem textItem = new SubItem("TXT");
            for (int j = 0; j < fileInfos.size(); j++) {
                if (XFileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"doc", "docx", "dot"})) {
                    wordItem.addSubItem(fileInfos.get(j));
                } else if (XFileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"xls", "xlsx"})) {
                    excelItem.addSubItem(fileInfos.get(j));
                } else if (XFileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"pdf"})) {
                    pdfItem.addSubItem(fileInfos.get(j));
                } else if (XFileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"ppt", "pptx"})) {
                    PPTItem.addSubItem(fileInfos.get(j));
                } else if (XFileUtil.checkSuffix(fileInfos.get(j).getFilePath(), new String[]{"txt"})) {
                    textItem.addSubItem(fileInfos.get(j));
                }
            }

            mEntityArrayList.add(wordItem);
            mEntityArrayList.add(excelItem);
            mEntityArrayList.add(pdfItem);
            mEntityArrayList.add(PPTItem);
            mEntityArrayList.add(textItem);
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
        mHandler.sendEmptyMessage(HANDLER_ERROR_WHAT);
    }

    @Override
    protected BaseMultiItemQuickAdapter<? extends MultiItemEntity, ? extends BaseViewHolder> getRecyclerViewAdapter() {
        return new ExpandableItemAdapter(mEntityArrayList, false, mFileSQLiteHelper);
    }

    @Override
    public void initWidget(View root) {
        super.initWidget(root);
        mFileType = new String[]{"doc", "docx", "dot", "xls", "xlsx", "pdf", "ppt", "pptx", "txt"};
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
