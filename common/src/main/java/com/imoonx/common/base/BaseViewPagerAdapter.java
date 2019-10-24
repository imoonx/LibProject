package com.imoonx.common.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

/**
 * ViewPager adapter
 */
public class BaseViewPagerAdapter extends FragmentPagerAdapter {

    private PagerInfo[] mInfoList;
    private Fragment mCurFragment;
    private Context mContext;

    public BaseViewPagerAdapter(Context context, FragmentManager fm, PagerInfo[] infoList) {
        super(fm);
        mInfoList = infoList;
        this.mContext = context;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (object instanceof Fragment) {
            mCurFragment = (Fragment) object;
        }
    }

    public Fragment getCurFragment() {
        return mCurFragment;
    }

    @Override
    public Fragment getItem(int position) {
        PagerInfo info = mInfoList[position];
        return Fragment.instantiate(mContext, info.clx.getName(), info.args);
    }

    @Override
    public int getCount() {
        return mInfoList.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mInfoList[position].title;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public static class PagerInfo {
        private String title;
        private Class<?> clx;
        private Bundle args;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Class<?> getClx() {
            return clx;
        }

        public void setClx(Class<?> clx) {
            this.clx = clx;
        }

        public Bundle getArgs() {
            return args;
        }

        public void setArgs(Bundle args) {
            this.args = args;
        }

        public PagerInfo(String title, Class<?> clx, Bundle args) {
            this.title = title;
            this.clx = clx;
            this.args = args;
        }
    }
}