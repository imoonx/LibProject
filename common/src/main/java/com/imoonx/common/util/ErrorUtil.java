package com.imoonx.common.util;

import android.app.Activity;
import android.content.Context;

import com.imoonx.common.ui.dialog.DialogHelper;
import com.imoonx.util.XLog;

/**
 * 展示错误信息
 * <p>
 * Created by 36238 on 2018/3/21.
 */

public class ErrorUtil {

    private static boolean isShow;

    public boolean isDebug() {
        return isShow;
    }

    public static void setDebug(boolean isDebug) {
        ErrorUtil.isShow = isDebug;
    }

    public static void showError(Context context, String message) {
        try {
            if (isShow && context instanceof Activity) {
                DialogHelper.getConfirmDialog(context, "请求错误", message).show();
            }
        } catch (Exception e) {
            XLog.e(ErrorUtil.class, e);
        }
    }

}
