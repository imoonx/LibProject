package com.imoonx.common.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

/**
 * 跳转权限设置 请求成功调用onActivityResult(int, int, Intent) 通过build 调用
 */
public class AppSettingsDialog {

    public static final int DEFAULT_SETTINGS_REQ_CODE = 16061;

    private AlertDialog mAlertDialog;

    /**
     * 创建权限设置
     *
     * @param activityOrFragment 权限实现回调接口 必须在Activity或Fragment
     * @param context            上下文
     * @param rationale          使用权限的原因
     * @param title              弹框标题
     * @param positiveButton     确定描述 可为空 默认"确定" 点击后跳转权限设置页面
     * @param negativeButton     取消描述 可为空 默认"取消"
     * @param negativeListener   取消监听
     * @param requestCode        请求码
     */
    private AppSettingsDialog(@NonNull final Object activityOrFragment,
                              @NonNull final Context context, @NonNull String rationale,
                              @Nullable String title, @Nullable String positiveButton,
                              @Nullable String negativeButton,
                              @Nullable DialogInterface.OnClickListener negativeListener,
                              int requestCode) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(rationale);
        dialogBuilder.setTitle(title);
        String positiveButtonText = TextUtils.isEmpty(positiveButton) ? context.getString(android.R.string.ok) : positiveButton;
        String negativeButtonText = TextUtils.isEmpty(positiveButton) ? context.getString(android.R.string.cancel) : negativeButton;

        final int settingsRequestCode = requestCode > 0 ? requestCode : DEFAULT_SETTINGS_REQ_CODE;

        dialogBuilder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Create app settings intent
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                startForResult(activityOrFragment, intent, settingsRequestCode);
            }
        });

        dialogBuilder.setNegativeButton(negativeButtonText, negativeListener);
        mAlertDialog = dialogBuilder.create();
    }

    @TargetApi(11)
    private void startForResult(Object object, Intent intent, int requestCode) {
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).startActivityForResult(intent, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 显示弹窗
     */
    public void show() {
        mAlertDialog.show();
    }

    /**
     * 构建AppSettingsDialog {@link AppSettingsDialog}.
     */
    public static class Builder {

        private Object mActivityOrFragment;
        private Context mContext;
        private String mRationale;
        private String mTitle;
        private String mPositiveButton;
        private String mNegativeButton;
        private DialogInterface.OnClickListener mNegativeListener;
        private int mRequestCode = -1;

        /**
         * 创建build
         *
         * @param activity  实现权限监听的activity
         * @param rationale 使用权限的原因
         */
        public Builder(@NonNull Activity activity, @NonNull String rationale) {
            mActivityOrFragment = activity;
            mContext = activity;
            mRationale = rationale;
        }

        /**
         * 创建build
         *
         * @param fragment  实现权限监听的Fragment support-v4
         * @param rationale 使用权限的原因
         */
        public Builder(@NonNull Fragment fragment, @NonNull String rationale) {
            mActivityOrFragment = fragment;
            mContext = fragment.getContext();
            mRationale = rationale;
        }

        /**
         * 创建build
         *
         * @param fragment  实现权限监听的Fragment app
         * @param rationale 使用权限的原因
         */
        @TargetApi(11)
        public Builder(@NonNull android.app.Fragment fragment, @NonNull String rationale) {
            mActivityOrFragment = fragment;
            mContext = fragment.getActivity();
            mRationale = rationale;
        }

        /**
         * 设置弹框标题 默认无标题
         *
         * @param title 标题
         * @return {@link #Builder}
         */
        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        /**
         * 设置确定键描述
         *
         * @param positiveButton 默认确定
         * @return {@link #Builder}
         */
        public Builder setPositiveButton(String positiveButton) {
            mPositiveButton = positiveButton;
            return this;
        }

        /**
         * 设置取消按钮描述
         *
         * @param negativeButton   默认取消
         * @param negativeListener 取消监听
         * @return {@link #Builder}
         */
        public Builder setNegativeButton(String negativeButton, DialogInterface.OnClickListener negativeListener) {
            mNegativeButton = negativeButton;
            mNegativeListener = negativeListener;
            return this;
        }

        /**
         * 设置请求码
         *
         * @param requestCode 请求码 {@link #DEFAULT_SETTINGS_REQ_CODE}.
         * @return {@link #Builder}
         */
        public Builder setRequestCode(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }

        /**
         * 通过build构建AppSettingsDialog
         * 最后调用{@link AppSettingsDialog#show()}.
         *
         * @return {@link AppSettingsDialog}
         */
        public AppSettingsDialog build() {
            return new AppSettingsDialog(mActivityOrFragment, mContext, mRationale, mTitle, mPositiveButton, mNegativeButton,
                    mNegativeListener, mRequestCode);
        }
    }
}
