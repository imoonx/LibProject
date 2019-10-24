package com.imoonx.file;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;

import com.imoonx.common.base.BaseActivity;
import com.imoonx.file.adapter.TabPagerAdapter;
import com.imoonx.file.fragment.AllMainFragment;
import com.imoonx.file.fragment.LocalMainFragment;
import com.imoonx.util.XFileUtil;
import com.imoonx.util.XLog;

import java.util.ArrayList;
import java.util.List;

import static com.imoonx.file.utils.Config.INTENT_ACTION_SELECT_FILE_FINISH;

public class SelectFileActivity extends BaseActivity {

    private static SimpleFileSelectCallback mCallback;

    public static void show(Context context, SimpleFileSelectCallback callBack) {
        mCallback = callBack;
        context.startActivity(new Intent(context, SelectFileActivity.class));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            XLog.i(LocalMainFragment.class, "接受到数量更改的广播");
            if (intent.getAction().equals(INTENT_ACTION_SELECT_FILE_FINISH)) {
                SelectFileActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(INTENT_ACTION_SELECT_FILE_FINISH);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
    }


    private List<String> mTitleList = new ArrayList<>();

    private List<Fragment> mFragments = new ArrayList<>();

    private ViewPager mViewPager;

    @Override
    public int getLayoutID() {
        return R.layout.file_activity_select_file;
    }

    @Override
    protected int getTitleDesc() {
        return R.string.file_select;
    }

    @Override
    protected void initWidget() {
        new FileSQLiteHelper(this).deleteAllFile();
        XLog.i(SelectFileActivity.class, "外置SD卡路径 = " + XFileUtil.getStoragePath(this));
        XLog.i(SelectFileActivity.class, "内置SD卡路径 = " + Environment.getExternalStorageDirectory().getAbsolutePath());
        XLog.i(SelectFileActivity.class, "手机内存根目录路径  = " + Environment.getDataDirectory().getParentFile().getAbsolutePath());
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        addFragment();
        mViewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), mTitleList, mFragments));
        // 设置默认选中页
        mViewPager.setCurrentItem(0);
    }

    protected void addFragment() {
        XLog.i(this.getClass(), "是否为空" + (mCallback == null));
        if (null == mCallback) {
            mFragments.add(new LocalMainFragment());// 本机
            mFragments.add(new AllMainFragment());// 全部
        } else {
            mFragments.add(AllMainFragment.newInstantiate(mCallback));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (null != mReceiver)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        if (null != mCallback)
            mCallback = null;
        super.onDestroy();
    }
}
