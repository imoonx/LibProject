package com.imoonx.common.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;

import com.imoonx.common.R;
import com.imoonx.util.Res;
import com.imoonx.util.XLog;


/**
 * 通用的对话框
 */
public final class DialogHelper {

    public static WaitDialog getWaitDialog(Activity activity, int message) {
        WaitDialog dialog = null;
        try {
            dialog = new WaitDialog(activity, R.style.dialog_waiting);
            dialog.setMessage(message);
        } catch (Exception e) {
            XLog.e(WaitDialog.class, e);
        }
        return dialog;
    }

    public static WaitDialog getWaitDialog(Activity activity, String message) {
        WaitDialog dialog = null;
        try {
            dialog = new WaitDialog(activity, R.style.dialog_waiting);
            dialog.setMessage(message);
        } catch (Exception e) {
            XLog.e(WaitDialog.class, e);
        }
        return dialog;
    }

    public static WaitDialog getCancelableWaitDialog(Activity activity, String message) {
        WaitDialog dialog = null;
        try {
            dialog = new WaitDialog(activity, R.style.dialog_waiting);
            dialog.setMessage(message);
            dialog.setCancelable(true);
        } catch (Exception e) {
            XLog.e(WaitDialog.class, e);
        }
        return dialog;
    }

    public static AlertDialog.Builder getDialog(Context context) {
        return new AlertDialog.Builder(context);
    }


    /**
     * 获取一个普通的消息对话框，没有取消按钮
     *
     * @param context    上下文
     * @param title      title
     * @param message    message
     * @param cancelable 是否可以取消
     * @return dialog
     */
    public static AlertDialog.Builder getMessageDialog(Context context,
                                                       String title, String message, boolean cancelable) {
        return getDialog(context).setCancelable(cancelable).setTitle(title)
                .setMessage(message)
                .setPositiveButton(Res.getString(R.string.is_ok), null);
    }

    /**
     * 获取一个普通的消息对话框，没有取消按钮 可取消
     *
     * @param context 上下文
     * @param title   title
     * @param message message
     * @return dialog
     */
    public static AlertDialog.Builder getMessageDialog(Context context,
                                                       String title, String message) {
        return getMessageDialog(context, title, message, false);
    }

    /**
     * 获取一个普通的消息对话框，只显示内容，没有取消按钮 可取消
     *
     * @param context 上下文
     * @param message message
     * @return dialog
     */
    public static AlertDialog.Builder getMessageDialog(Context context,
                                                       String message) {
        return getMessageDialog(context, "", message, false);
    }

    /**
     * 获取一个普通的消息对话框，没有取消按钮
     *
     * @param context      上下文
     * @param title        title
     * @param message      message
     * @param positiveText 确定键文字
     * @return dialog
     */
    public static AlertDialog.Builder getMessageDialog(Context context,
                                                       String title, String message, String positiveText) {
        return getDialog(context).setCancelable(false).setTitle(title)
                .setMessage(message).setPositiveButton(positiveText, null);
    }

    /**
     * 获取一个验证对话框
     */
    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String title, String message, String positiveText,
                                                       String negativeText, boolean cancelable,
                                                       DialogInterface.OnClickListener positiveListener,
                                                       DialogInterface.OnClickListener negativeListener) {
        return getDialog(context).setCancelable(cancelable).setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener);
    }

    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String message, String positiveText, String negativeText,
                                                       DialogInterface.OnClickListener positiveListener,
                                                       DialogInterface.OnClickListener negativeListener) {
        return getDialog(context).setMessage(message)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener);
    }

    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String title, String message,
                                                       DialogInterface.OnClickListener positiveListener,
                                                       DialogInterface.OnClickListener negativeListener) {
        return getDialog(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(Res.getString(R.string.is_ok),
                        positiveListener)
                .setNegativeButton(Res.getString(R.string.is_cancel),
                        negativeListener);
    }

    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String title, View view) {
        return getDialog(context).setTitle(title).setView(view)
                .setNegativeButton(Res.getString(R.string.is_cancel), null);
    }

    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String title, String okDesc, View view,
                                                       DialogInterface.OnClickListener positiveListener) {
        return getDialog(context).setTitle(title).setView(view)
                .setPositiveButton(okDesc, positiveListener)
                .setNegativeButton(Res.getString(R.string.is_cancel), null);
    }

    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String title, String message, String name) {
        return getDialog(context).setTitle(title).setMessage(message)
                .setNegativeButton(Res.getString(R.string.is_cancel), null);
    }

    /**
     * 获取一个验证对话框
     */
    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String message, DialogInterface.OnClickListener positiveListener,
                                                       DialogInterface.OnClickListener negativeListener) {
        return getDialog(context)
                .setMessage(message)
                .setPositiveButton(Res.getString(R.string.is_ok),
                        positiveListener)
                .setNegativeButton(Res.getString(R.string.is_cancel),
                        negativeListener);
    }

    /**
     * 获取一个验证对话框，没有点击事件
     */
    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String title, String message, String positiveText,
                                                       String negativeText, boolean cancelable,
                                                       DialogInterface.OnClickListener positiveListener) {
        return getConfirmDialog(context, title, message, positiveText,
                negativeText, cancelable, positiveListener, null);
    }

    /**
     * 获取一个验证对话框，没有点击事件
     */
    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String title, String message, String positiveText,
                                                       String negativeText,
                                                       DialogInterface.OnClickListener positiveListener) {
        return getConfirmDialog(context, title, message, positiveText,
                negativeText, false, positiveListener, null);
    }

    /**
     * 获取一个验证对话框，没有点击事件
     */
    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String title, String message, String positiveText,
                                                       String negativeText, boolean cancelable) {
        return getConfirmDialog(context, title, message, positiveText,
                negativeText, cancelable, null, null);
    }

    /**
     * 获取一个验证对话框，没有点击事件
     */
    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String message, String positiveText, String negativeText,
                                                       boolean cancelable) {
        return getConfirmDialog(context, "", message, positiveText,
                negativeText, cancelable, null, null);
    }

    /**
     * 获取一个验证对话框，没有点击事件，取消、确定
     */
    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String title, String message, boolean cancelable) {
        return getConfirmDialog(context, title, message,
                Res.getString(R.string.is_ok),
                Res.getString(R.string.is_cancel), cancelable, null, null);
    }

    /**
     * 获取一个验证对话框，没有点击事件，取消、确定
     */
    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String message, boolean cancelable,
                                                       DialogInterface.OnClickListener positiveListener) {
        return getConfirmDialog(context, "", message,
                Res.getString(R.string.is_ok),
                Res.getString(R.string.is_cancel), cancelable,
                positiveListener, null);
    }

    /**
     * 获取一个验证对话框，没有点击事件，取消、确定
     */
    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String message, DialogInterface.OnClickListener positiveListener) {
        return getConfirmDialog(context, "", message,
                Res.getString(R.string.is_ok),
                Res.getString(R.string.is_cancel), positiveListener);
    }

    /**
     * 获取一个验证对话框，没有点击事件，取消、确定
     */
    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String title, String message) {
        return getConfirmDialog(context, title, message,
                Res.getString(R.string.is_ok),
                Res.getString(R.string.is_cancel), false, null, null);
    }

    /**
     * 获取一个输入对话框
     */
    public static AlertDialog.Builder getInputDialog(Context context,
                                                     String title, AppCompatEditText editText, String positiveText,
                                                     String negativeText, boolean cancelable,
                                                     DialogInterface.OnClickListener positiveListener,
                                                     DialogInterface.OnClickListener negativeListener) {
        return getDialog(context).setCancelable(cancelable).setTitle(title)
                .setView(editText)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener);
    }

    /**
     * 获取一个输入对话框
     */
    public static AlertDialog.Builder getInputDialog(Context context,
                                                     String title, AppCompatEditText editText, String positiveText,
                                                     String negativeText, boolean cancelable,
                                                     DialogInterface.OnClickListener positiveListener) {
        return getInputDialog(context, title, editText, positiveText,
                negativeText, cancelable, positiveListener, null);
    }

    /**
     * 获取一个输入对话框
     */
    public static AlertDialog.Builder getInputDialog(Context context,
                                                     String title, AppCompatEditText editText, boolean cancelable,
                                                     DialogInterface.OnClickListener positiveListener) {
        return getInputDialog(context, title, editText,
                Res.getString(R.string.is_ok),
                Res.getString(R.string.is_cancel), cancelable,
                positiveListener, null);
    }

    /**
     * 获取一个输入对话框
     */
    public static AlertDialog.Builder getInputDialog(Context context,
                                                     String title, AppCompatEditText editText, String positiveText,
                                                     boolean cancelable,
                                                     DialogInterface.OnClickListener positiveListener,
                                                     DialogInterface.OnClickListener negativeListener) {
        return getInputDialog(context, title, editText, positiveText,
                Res.getString(R.string.is_cancel), cancelable,
                positiveListener, negativeListener);
    }

    /**
     * 获取一个输入对话框
     */
    public static AlertDialog.Builder getInputDialog(Context context, String title, AppCompatEditText editText, boolean cancelable,
                                                     DialogInterface.OnClickListener positiveListener,
                                                     DialogInterface.OnClickListener negativeListener) {
        return getInputDialog(context, title, editText,
                Res.getString(R.string.is_ok),
                Res.getString(R.string.is_cancel), cancelable,
                positiveListener, negativeListener);
    }

    /**
     * 选择dialog
     *
     * @param context 上下文
     * @param view    view
     * @return dialog
     */
    public static AlertDialog.Builder getSelectDialog(Context context, View view) {
        return getDialog(context).setView(view);
    }

    /**
     * 获取dialog
     *
     * @param context      上下文
     * @param title        标题
     * @param arrays       选择内容
     * @param buttonDesc   button文字
     * @param itemListener item 点击事件
     * @return dialog
     */
    public static AlertDialog.Builder getSelectDialog(Context context, String title, String[] arrays, String buttonDesc,
                                                      DialogInterface.OnClickListener itemListener) {
        return getDialog(context).setTitle(title).setItems(arrays, itemListener).setPositiveButton(buttonDesc, null);
    }

    /**
     * 获取单选dialog
     *
     * @param context      上下文
     * @param title        标题
     * @param arrays       选择内容
     * @param buttonDesc   button文字
     * @param itemListener item 点击事件
     * @return dialog
     */
    public static AlertDialog.Builder getSingleChoiceDialog(Context context, String title, String[] arrays, int selectIndex,
                                                            DialogInterface.OnClickListener itemListener, String buttonDesc) {
        return getSingleChoiceDialog(context, title, arrays, selectIndex, itemListener, buttonDesc, null, "", null);
    }

    /**
     * 获取单选dialog
     *
     * @param context      上下文
     * @param arrays       选项数组
     * @param selectIndex  选择项
     * @param itemListener 选项点击事件
     * @return dialog
     */
    public static AlertDialog.Builder getSingleChoiceDialog(Context context, String[] arrays, int selectIndex, DialogInterface.OnClickListener itemListener) {
        return getSingleChoiceDialog(context, "", arrays, selectIndex, itemListener, "", null, "", null);
    }

    /**
     * 单选
     *
     * @param context             上下文
     * @param title               标题
     * @param arrays              选项数组
     * @param selectIndex         选中位置
     * @param onItemClickListener 选项点击事件
     * @param positiveText        确定按钮文字描述
     * @param positiveListener    确定按钮点击事件
     * @param negativeText        取消按钮文字描述
     * @param negativeListener    取消按钮点击事件
     * @return dialog
     */
    public static AlertDialog.Builder getSingleChoiceDialog(Context context, String title, String[] arrays, int selectIndex,
                                                            DialogInterface.OnClickListener onItemClickListener,
                                                            String positiveText, DialogInterface.OnClickListener positiveListener,
                                                            String negativeText, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setSingleChoiceItems(arrays, selectIndex, onItemClickListener);
        builder.setTitle(title);
        builder.setPositiveButton(positiveText, positiveListener);
        builder.setNegativeButton(negativeText, negativeListener);
        return builder;
    }

    /**
     * 单选
     *
     * @param context             上下文
     * @param title               标题
     * @param arrays              选项数组
     * @param selectIndex         选中状态
     * @param onItemClickListener 选项点击事件
     * @param positiveText        确定按钮文字描述
     * @param positiveListener    确定按钮点击事件
     * @param negativeText        取消按钮文字描述
     * @param negativeListener    取消按钮点击事件
     * @return dialog
     */
    public static AlertDialog.Builder getMultiChoiceDialog(Context context, String title, String[] arrays, boolean[] selectIndex,
                                                           DialogInterface.OnMultiChoiceClickListener onItemClickListener,
                                                           String positiveText, DialogInterface.OnClickListener positiveListener,
                                                           String negativeText, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setMultiChoiceItems(arrays, selectIndex, onItemClickListener);
        builder.setTitle(title);
        builder.setPositiveButton(positiveText, positiveListener);
        builder.setNegativeButton(negativeText, negativeListener);
        return builder;
    }
}
