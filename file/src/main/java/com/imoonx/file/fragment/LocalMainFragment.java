package com.imoonx.file.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.imoonx.common.base.BaseFragment;
import com.imoonx.common.base.BaseViewPagerAdapter;
import com.imoonx.common.base.ImageGalleryActivity;
import com.imoonx.util.Res;
import com.imoonx.file.FileSQLiteHelper;
import com.imoonx.file.R;
import com.imoonx.file.bean.FileInfo;
import com.imoonx.file.bean.Image;
import com.imoonx.file.utils.Config;
import com.imoonx.util.XFileUtil;
import com.imoonx.util.XLog;

import java.util.ArrayList;
import java.util.List;

import static com.imoonx.file.utils.Config.INTENT_ACTION_SELECT_FILE_TYPE;


/**
 * 本机
 */

public class LocalMainFragment extends BaseFragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView mFileSize;
    private TextView mSend;
    private TextView mPreview;

    private List<Image> mListphoto = new ArrayList<>();
    private FileSQLiteHelper mFileSQLiteHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == mFileSQLiteHelper)
            mFileSQLiteHelper = new FileSQLiteHelper(getContext());
        IntentFilter filter = new IntentFilter(Config.INTENT_ACTION_UPDATA_SELECT_COUNT);
        filter.addAction(INTENT_ACTION_SELECT_FILE_TYPE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            XLog.i(LocalMainFragment.class, "接受到数量更改的广播");
            if (intent.getAction().equals(Config.INTENT_ACTION_UPDATA_SELECT_COUNT)) {
                updateSizAndCount();
            } else if (intent.getAction().equals(INTENT_ACTION_SELECT_FILE_TYPE)) {
                if (intent.getIntExtra("isPhoto", -1) == 1) {
                    mPreview.setVisibility(View.VISIBLE);
                } else {
                    mPreview.setVisibility(View.GONE);
                }
            }
        }
    };

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

    @Override
    public void initWidget(View root) {
        mTabLayout = (TabLayout) root.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) root.findViewById(R.id.view_pager);
        mPreview = (TextView) root.findViewById(R.id.tv_preview);
        mFileSize = (TextView) root.findViewById(R.id.tv_all_size);
        mSend = (TextView) root.findViewById(R.id.tv_send);
        mFileSize.setText(String.format(Res.getString(R.string.size), "0B"));
        mSend.setText(String.format(Res.getString(R.string.send), "0"));
        mSend.setOnClickListener(this);
        mPreview.setOnClickListener(this);
        updateSizAndCount();

        BaseViewPagerAdapter adapter = new BaseViewPagerAdapter(getActivity(), getChildFragmentManager(), getPagers());
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(0, true);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), false);
                Intent intent = new Intent();
                intent.setAction(INTENT_ACTION_SELECT_FILE_TYPE);
                intent.putExtra("isPhoto", tab.getPosition());
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    protected int mViewPagerSize = 4;

    protected BaseViewPagerAdapter.PagerInfo[] getPagers() {
        BaseViewPagerAdapter.PagerInfo[] infoList = new BaseViewPagerAdapter.PagerInfo[mViewPagerSize];
        infoList[0] = new BaseViewPagerAdapter.PagerInfo(Res.getString(R.string.video), AVFragment.class, null);
        infoList[1] = new BaseViewPagerAdapter.PagerInfo(Res.getString(R.string.image), PhotoFragment.class, null);
        infoList[2] = new BaseViewPagerAdapter.PagerInfo(Res.getString(R.string.doc), DocFragment.class, null);
        infoList[3] = new BaseViewPagerAdapter.PagerInfo(Res.getString(R.string.other), OtherFragment.class, null);
        return infoList;

    }

    public void updateSizAndCount() {
        mListphoto.clear();
        if (null != mFileSQLiteHelper) {
            List<FileInfo> mList = mFileSQLiteHelper.qureyAll();
            for (int i = 0; i < mList.size(); i++) {
                XLog.i(getClass(), "是否是图片=" + mList.get(i).getIsPhoto());
                XLog.i(getClass(), "是否选中=" + mList.get(i).getIsCheck());
                if (mList.get(i).getIsPhoto()) {
                    Image image = new Image();
                    image.setName(mList.get(i).getFileName());
                    image.setPath(mList.get(i).getFilePath());
                    mListphoto.add(image);
                }
            }
            if (mListphoto.size() == 0) {
                mPreview.setBackgroundResource(R.drawable.file_shape_bt_send);
                mPreview.setTextColor(Res.getColor(R.color.md_grey_700));
            } else {
                mPreview.setBackgroundResource(R.drawable.file_shape_bt_send_blue);
                mPreview.setTextColor(Res.getColor(R.color.md_white_1000));
            }
            if (mList.size() == 0) {
                mSend.setEnabled(false);
                mSend.setBackgroundResource(R.drawable.file_shape_bt_send);
                mSend.setTextColor(Res.getColor(R.color.md_grey_700));
                mFileSize.setText(getString(R.string.size, new Object[]{"0B"}));
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
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_send) {
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Config.INTENT_ACTION_UPDATA_SELECT_COUNT));
            if (null != getActivity())
                getActivity().finish();
        } else if (id == R.id.tv_preview) {
            ImageGalleryActivity.show(getActivity(), com.imoonx.file.FileUtil.toArray(mListphoto), 0, false);
        }
    }

    @Override
    public int getLayoutID() {
        return R.layout.file_fragment_local;
    }

}
