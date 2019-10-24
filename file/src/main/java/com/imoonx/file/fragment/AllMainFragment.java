package com.imoonx.file.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.imoonx.common.base.BaseFragment;
import com.imoonx.util.Res;
import com.imoonx.file.FileSQLiteHelper;
import com.imoonx.file.R;
import com.imoonx.file.SDCardActivity;
import com.imoonx.file.SimpleFileSelectCallback;
import com.imoonx.file.bean.FileInfo;
import com.imoonx.file.utils.Config;
import com.imoonx.util.XFileUtil;
import com.imoonx.util.XLog;

import java.util.List;

/**
 * 本机界面
 */

public class AllMainFragment extends BaseFragment {

    private TextView mFileSize;
    private TextView mSend;
    private FileSQLiteHelper mFileSQLiteHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == mFileSQLiteHelper)
            mFileSQLiteHelper = new FileSQLiteHelper(getContext());
        IntentFilter filter = new IntentFilter(Config.INTENT_ACTION_UPDATA_SELECT_COUNT);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            XLog.i(LocalMainFragment.class, "接受到数量更改的广播");
            if (intent.getAction().equals(Config.INTENT_ACTION_UPDATA_SELECT_COUNT)) {
                updateSizAndCount();
            }
        }
    };

    private boolean checkSDEnvironment() {
        // 判断sd卡是否存在
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private boolean checkExtentEnvironment() {
        return !checkSDEnvironment() && TextUtils.isEmpty(XFileUtil.getStoragePath(getContext()));
    }

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
                double count = 0L;
                for (int i = 0; i < mList.size(); i++) {
                    count = count + mList.get(i).getFileSize();
                }
                mFileSize.setText(String.format(Res.getString(R.string.size), XFileUtil.formetFileSizeD(count)));
            }
            mSend.setText(String.format(Res.getString(R.string.send), mList.size()));
        }
    }

    @Override
    public void initWidget(View root) {
        TextView mobileMemory = root.findViewById(R.id.tv_mobile_memory);
        mobileMemory.setOnClickListener(this);

        TextView sdCard = root.findViewById(R.id.tv_sd_card);
        sdCard.setOnClickListener(this);
        sdCard.setVisibility(checkSDEnvironment() ? View.VISIBLE : View.GONE);

        TextView extendedMemory = root.findViewById(R.id.tv_extended_memory);
        extendedMemory.setOnClickListener(this);
        extendedMemory.setVisibility(checkExtentEnvironment() ? View.VISIBLE : View.GONE);
        mFileSize = root.findViewById(R.id.tv_all_size);
        mSend = root.findViewById(R.id.tv_send);
        mSend.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mFileSize.setText(String.format(Res.getString(R.string.size), "0B"));
        mSend.setText(String.format(Res.getString(R.string.send), "0"));
        updateSizAndCount();
    }

    @Override
    public int getLayoutID() {
        return R.layout.file_fragment_main;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_mobile_memory) {
            startActivity(Environment.getDataDirectory().getParentFile().getAbsolutePath(), Res.getString(R.string.tv_mobile_memory));
        } else if (id == R.id.tv_sd_card) {
            startActivity(Environment.getExternalStorageDirectory().getAbsolutePath(), Res.getString(R.string.tv_sd_card));
        } else if (id == R.id.tv_extended_memory) {
            startActivity(XFileUtil.getStoragePath(getContext()), Res.getString(R.string.tv_extended_memory));
        } else if (id == R.id.tv_send) {
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Config.INTENT_ACTION_UPDATA_SELECT_COUNT));
            if (null != getActivity())
                getActivity().finish();
        }
    }

    private void startActivity(String path, String name) {
        Intent intent = new Intent(getActivity(), SDCardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        bundle.putString("name", name);
        intent.putExtras(bundle);
        if (null == mCallback) {
            startActivity(intent);
        } else {
            SDCardActivity.show(getContext(), intent, mCallback);
        }

    }

    private static SimpleFileSelectCallback mCallback;

    public static AllMainFragment newInstantiate(SimpleFileSelectCallback callBack) {
        AllMainFragment fragment = new AllMainFragment();
        mCallback = callBack;
        return fragment;
    }

    @Override
    public void onDestroy() {
        if (null != mReceiver)
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        if (null != mFileSQLiteHelper) {
            mFileSQLiteHelper.close();
            mFileSQLiteHelper = null;
        }
        super.onDestroy();
    }
}
