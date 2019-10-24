package com.imoonx.common.interf;


import com.imoonx.common.base.BaseFragment;

/**
 * 监听返回键 只适合一层嵌套
 */
public interface OnBackCallBackListener {

    /**
     * @param fragment BaseFragment
     */
    void onSelectedFragment(BaseFragment fragment);

}
