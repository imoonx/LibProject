package com.imoonx.common.ui;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author 36238
 *         <p>
 * @name FragmentTabHost
 * <p>
 * @data 2017/9/2 11:43
 * <p>
 * @desc 底部导航栏实现类
 */
public class MyFragmentTabHost extends FragmentTabHost {

    private String mCurrentTag;
    private String mNoTabChangedTag;
    private OnTabClickListener mOnTabClickListener;
    boolean isClickLinstener;

    public MyFragmentTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onTabChanged(String tag) {
        if (tag.equals(this.mNoTabChangedTag)) {
            setCurrentTabByTag(this.mCurrentTag);
        } else {
            if (this.mOnTabClickListener != null) {
                this.isClickLinstener = this.mOnTabClickListener.onTabClick(tag);
            }
            if (!this.isClickLinstener) {
                super.onTabChanged(tag);
                this.mCurrentTag = tag;
            }
        }
    }

    public void setNoTabChangedTag(String tag) {
        this.mNoTabChangedTag = tag;
    }

    public void setOnTabClickListener(OnTabClickListener mOnTabClickListener) {
        this.mOnTabClickListener = mOnTabClickListener;
    }

    public interface OnTabClickListener {
        boolean onTabClick(String paramString);
    }
}
