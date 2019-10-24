package com.imoonx.common.ui.gesturelock.listener;

import java.util.List;

/**
 * 密码输入监听
 */
public interface GestureEventListener {

    /**
     * 密码输入事件
     *
     * @param matched
     * @param password
     */
    void onGestureEvent(boolean matched, List<Integer> password);

    /**
     * 超过设定次数
     */
    void onUnmatchedExceedBoundary();

}
