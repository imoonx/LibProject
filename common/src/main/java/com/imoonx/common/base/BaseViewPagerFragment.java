package com.imoonx.common.base;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.imoonx.common.R;
import com.imoonx.util.XLog;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseViewPagerFragment
 */
public abstract class BaseViewPagerFragment extends BaseFragment {

    protected TabLayout mTabNav;
    protected List<PagerInfo> mPagerList = new ArrayList<>();
    protected ViewPager mBaseViewPager;
    protected BaseViewPagerAdapter mAdapter;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_base_viewpager;
    }

    @Override
    protected void initWidget(View root) {
        mTabNav = root.findViewById(R.id.tab_nav);
        mBaseViewPager = root.findViewById(R.id.base_viewPager);
        mAdapter = new BaseViewPagerAdapter(getChildFragmentManager(), getPagers());
        mBaseViewPager.setAdapter(mAdapter);
        if (mTabNav.getVisibility() == View.VISIBLE)
            mTabNav.setupWithViewPager(mBaseViewPager);
        mBaseViewPager.setCurrentItem(0, true);
    }

    protected boolean isNeedInternet() {
        return false;
    }

    protected List<PagerInfo> getPagers() {
        return mPagerList;
    }

    public class BaseViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<PagerInfo> mInfoList;
        private Fragment mCurFragment;

        public BaseViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mInfoList = new ArrayList<>();
        }

        public BaseViewPagerAdapter(FragmentManager fm, List<PagerInfo> infoList) {
            super(fm);
            this.mInfoList = infoList;
            XLog.i(getClass(), "mInfoList.size=" + mInfoList.size());
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
            PagerInfo info = mInfoList.get(position);
            return Fragment.instantiate(getContext(), info.getClx().getName(), info.getArgs());
        }

        @Override
        public int getCount() {
            return mInfoList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mInfoList.get(position).getTitle();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        public void checkListNull() {
            if (mInfoList == null) {
                mInfoList = new ArrayList<>();
            }
        }

        public void addItem(List<PagerInfo> items) {
            checkListNull();
            if (items != null) {
                mInfoList.clear();
                mInfoList.addAll(items);
            }
            notifyDataSetChanged();
        }
    }
}
