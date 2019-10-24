package com.imoonx.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;

/**
 * Toast 封装
 */
public class Toast {

    private static android.widget.Toast toast;
    public static boolean isCustom;

    public boolean isCustom() {
        return isCustom;
    }

    public static void setCustom(boolean custom) {
        isCustom = custom;
    }

    /**
     * toast 提示
     *
     * @param contentId 展示的内容
     */
    public static void showToast(int contentId) {
        showToast(Res.getString(contentId));
    }

    /**
     * 自定义toast 避免多次点击卡死
     *
     * @param content 展示的内容
     */
    public static void showToast(String content) {
        showToast(BaseApplication.context(), content);
    }

    /**
     * 自定义toast 避免多次点击卡死
     *
     * @param context   上下文
     * @param contentId 内容id
     */
    public static void showToast(Context context, int contentId) {
        showToast(context, Res.getString(contentId));
    }

    /**
     * 自定义toast 避免多次点击卡死
     *
     * @param context 上下文
     * @param content 内容
     */
    @SuppressLint("ShowToast")
    public static void showToast(Context context, String content) {
        try {
            if (TextUtils.isEmpty(content))
                return;
            if (toast == null) {
                if (null == content)
                    context = BaseApplication.context();
                toast = android.widget.Toast.makeText(context, content, android.widget.Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
            }
            toast.setText(content);
            toast.show();
        } catch (Exception e) {
            XLog.e(Toast.class, e);
        }
    }
}
