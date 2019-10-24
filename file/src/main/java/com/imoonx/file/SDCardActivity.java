package com.imoonx.file;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.imoonx.common.base.BaseActivity;
import com.imoonx.common.ui.EmptyLayout;
import com.imoonx.util.Res;
import com.imoonx.file.adapter.MultipleItem;
import com.imoonx.file.adapter.MultipleItemQuickAdapter;
import com.imoonx.file.bean.FileInfo;
import com.imoonx.file.recycle.BaseQuickAdapter;
import com.imoonx.file.recycle.listener.OnItemClickListener;
import com.imoonx.file.utils.Config;
import com.imoonx.file.view.CheckBox;
import com.imoonx.file.view.DividerItemDecoration;
import com.imoonx.util.XFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.imoonx.file.utils.Config.INTENT_ACTION_SELECT_FILE_FINISH;

public class SDCardActivity extends BaseActivity {

    private static SimpleFileSelectCallback mCallback;
    private RecyclerView mRecycleView;
    private TextView mPathDesc;
    private TextView mFileSize;
    private TextView mSend;
    private FileSQLiteHelper mFileSQLiteHelper;

    public static void show(Context context, Intent intent, SimpleFileSelectCallback callBack) {
        mCallback = callBack;
        context.startActivity(intent);
    }

    protected int getTitleDesc() {
        return R.string.file_select;
    }

    private List<FileInfo> fileInfos = new ArrayList<>();
    private List<MultipleItem> mMultipleItems = new ArrayList<>();
    private MultipleItemQuickAdapter mAdapter;
    private File mCurrentPathFile = null;
    private File mSDCardPath = null;
    private EmptyLayout mErrorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (null == mFileSQLiteHelper)
            mFileSQLiteHelper = new FileSQLiteHelper(this);
        super.onCreate(savedInstanceState);
    }

    public int getLayoutID() {
        return R.layout.file_activity_sdcard;
    }

    protected void initWidget() {

        mErrorLayout = ((EmptyLayout) findViewById(R.id.error_layout));
        mFileSize = ((TextView) findViewById(R.id.tv_all_size));
        mSend = ((TextView) findViewById(R.id.tv_send));

        mRecycleView = ((RecyclerView) findViewById(R.id.rlv_sd_card));
        mPathDesc = ((TextView) findViewById(R.id.tv_path));

        mFileSize.setText(String.format(Res.getString(R.string.size), "0B"));
        mSend.setText(String.format(Res.getString(R.string.send), "0"));
        mSend.setOnClickListener(this);
        String path = getIntent().getStringExtra("path");

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.addItemDecoration(new DividerItemDecoration(this, 1, R.drawable.divide_line));
        mAdapter = new MultipleItemQuickAdapter(mMultipleItems);
        mRecycleView.setAdapter(mAdapter);

        mSDCardPath = new File(path);
        showFiles(mSDCardPath);

        updateSizAndCount();
        mRecycleView.addOnItemTouchListener(new OnItemClickListener() {
            @SuppressWarnings("rawtypes")
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (adapter.getItemViewType(position) == 2) {
                    boolean isCheck = fileInfos.get(position).getIsCheck();
                    fileInfos.get(position).setIsCheck(!isCheck);
                    if (mCallback != null) {
                        mCallback.doSimpleFile(fileInfos.get(position));
                        LocalBroadcastManager.getInstance(SDCardActivity.this).sendBroadcast(new Intent(INTENT_ACTION_SELECT_FILE_FINISH));
                        finish();
                    } else {
                        if ((fileInfos.get(position)).getIsCheck()) {
                            if (null != mFileSQLiteHelper)
                                mFileSQLiteHelper.insertFile(fileInfos.get(position));
                            ((CheckBox) view.findViewById(R.id.cb_file)).setChecked(true, true);
                        } else {
                            if (null != mFileSQLiteHelper)
                                mFileSQLiteHelper.deleteFile(fileInfos.get(position));
                            ((CheckBox) view.findViewById(R.id.cb_file)).setChecked(false, true);
                        }
                        sendBroadcast(Config.INTENT_ACTION_UPDATA_SELECT_COUNT);
                        sendBroadcast(Config.INTENT_ACTION_ADAPTER_NOTIFYDATASETCHANGED);
                        updateSizAndCount();
                    }
                } else {
                    showFiles(new File((fileInfos.get(position)).getFilePath()));
                }
            }
        });
    }

    @NonNull
    private void sendBroadcast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        LocalBroadcastManager.getInstance(SDCardActivity.this).sendBroadcast(intent);
    }

    @SuppressWarnings("deprecation")
    public void updateSizAndCount() {
        if (null != mFileSQLiteHelper) {
            List<FileInfo> mList = mFileSQLiteHelper.qureyAll();
            if (mList.size() == 0) {
                mSend.setEnabled(false);
                mSend.setBackgroundResource(R.drawable.file_shape_bt_send);
                mSend.setTextColor(Res.getColor(R.color.md_grey_700));
                mFileSize.setText(String.format(Res.getString(R.string.size), "0B"));
            } else {
                mSend.setEnabled(true);
                mSend.setBackgroundResource(R.drawable.file_shape_bt_send_blue);
                mSend.setTextColor(Res.getColor(R.color.md_white_1000));
                double count = 0.0D;
                for (int i = 0; i < mList.size(); i++) {
                    count += mList.get(i).getFileSize();
                }
                mFileSize.setText(String.format(Res.getString(R.string.size), XFileUtil.formetFileSizeD(count)));
            }
            mSend.setText(String.format(Res.getString(R.string.send), mList.size()));
        }
    }

    public void onBackPressed() {
        if (mSDCardPath.getAbsolutePath().equals(mCurrentPathFile.getAbsolutePath())) {
            finish();
        } else {
            mCurrentPathFile = mCurrentPathFile.getParentFile();
            showFiles(mCurrentPathFile);
        }
    }

    private void showFiles(File folder) {
        mMultipleItems.clear();
        mPathDesc.setText(folder.getAbsolutePath());
        mCurrentPathFile = folder;
        File[] files = FileUtil.fileFilter(folder);
        if ((files == null) || (files.length == 0)) {
            mErrorLayout.setErrorType(3);
        } else {
            mErrorLayout.setErrorType(4);
            fileInfos = com.imoonx.file.FileUtil.getFileInfosFromFileArray(files);
            for (int i = 0; i < fileInfos.size(); i++) {
                if ((fileInfos.get(i)).getIsDirectory()) {
                    mMultipleItems.add(new MultipleItem(1, fileInfos.get(i)));
                } else {
                    mMultipleItems.add(new MultipleItem(2, fileInfos.get(i)));
                }
            }
            List<FileInfo> mList = mFileSQLiteHelper.qureyAll();
            for (int i = 0; i < fileInfos.size(); i++) {
                for (FileInfo fileInfo : mList) {
                    if (fileInfo.getFileName().equals((fileInfos.get(i)).getFileName())) {
                        (fileInfos.get(i)).setIsCheck(true);
                    }
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    protected void onDestroy() {
        if (mCallback != null)
            mCallback = null;
        if (null != mFileSQLiteHelper) {
            mFileSQLiteHelper.close();
            mFileSQLiteHelper = null;
        }
        super.onDestroy();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tv_send) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(INTENT_ACTION_SELECT_FILE_FINISH));
            finish();
        }
    }
}
