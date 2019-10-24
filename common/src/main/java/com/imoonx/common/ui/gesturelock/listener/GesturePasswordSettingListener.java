package com.imoonx.common.ui.gesturelock.listener;

import java.util.List;

/**
 * 密码设置事件监听
 */
public interface GesturePasswordSettingListener {

    /**
     * 第一次输入完成
     *
     * @param len
     * @param password
     * @return
     */
    boolean onFirstInputComplete(int len, List<Integer> password);

    /**
     * 设置成功
     */
    void onSuccess();

    /**
     * 设置失败
     */
    void onFail();
}
