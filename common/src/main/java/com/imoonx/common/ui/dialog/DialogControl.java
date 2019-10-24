package com.imoonx.common.ui.dialog;

/**
dialog 控制类
 */
public interface DialogControl {

    void hideWaitDialog();

    WaitDialog showWaitDialog();

    WaitDialog showWaitDialog(int resid);

    WaitDialog showWaitDialog(String text);

    WaitDialog showWaitDialog(String text, boolean isCancel);
}
