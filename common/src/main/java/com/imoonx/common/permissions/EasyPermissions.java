/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imoonx.common.permissions;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.imoonx.util.XLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 权限动态申请类 Android M
 */
@SuppressLint("NewApi")
public class EasyPermissions {

    private static final String DIALOG_TAG = "RationaleDialogFragmentCompat";

    /**
     * 判断是否有权限
     *
     * @param context 上下文
     * @param perms   权限参数
     * @return true 有权限 false 无权限
     */
    public static boolean hasPermissions(@NonNull Context context, @NonNull String... perms) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

    /**
     * 请求权限
     *
     * @param object      实现{@link com.imoonx.common.permissions.PermissionCallbacks}.的activity或fragment
     * @param rationale   申请权限的原因
     * @param requestCode 请求ma
     * @param perms       需要请求的参数
     */
    public static void requestPermissions(@NonNull final Object object, @NonNull String rationale, final int requestCode, @NonNull final String... perms) {
        requestPermissions(object, rationale, android.R.string.ok, android.R.string.cancel, requestCode, perms);
    }

    /**
     * 请求权限
     * * @param object      实现{@link com.imoonx.common.permissions.PermissionCallbacks}.的activity或fragment
     *
     * @param rationale      申请权限的原因
     * @param positiveButton 确定描述
     * @param negativeButton 取消描述
     * @param requestCode    请求ma
     * @param perms          需要请求的参数
     */
    @SuppressLint("NewApi")
    public static void requestPermissions(@NonNull final Object object, @NonNull String rationale, @StringRes int positiveButton,
                                          @StringRes int negativeButton, final int requestCode, @NonNull final String... perms) {

        checkCallingObjectSuitability(object);

        boolean shouldShowRationale = false;

        for (String perm : perms) {
            shouldShowRationale = shouldShowRationale || shouldShowRequestPermissionRationale(object, perm);
        }

        XLog.i(EasyPermissions.class, "shouldShowRationale=" + shouldShowRationale);

        if (shouldShowRationale) {
            if (getSupportFragmentManager(object) != null) {
                XLog.i(EasyPermissions.class, "SupportFragmentManager 请求权限");
                showRationaleDialogFragmentCompat(getSupportFragmentManager(object), rationale, positiveButton, negativeButton, requestCode, perms);
            } else if (getFragmentManager(object) != null) {
                XLog.i(EasyPermissions.class, "FragmentManager 请求权限");
                showRationaleDialogFragment(getFragmentManager(object), rationale, positiveButton, negativeButton, requestCode, perms);
            } else {
                XLog.i(EasyPermissions.class, "请求权限");
                showRationaleAlertDialog(object, rationale, positiveButton, negativeButton, requestCode, perms);
            }
        } else {
            XLog.i(EasyPermissions.class, "系统请求权限");
            executePermissionsRequest(object, perms, requestCode);
        }
    }

    /**
     * 显示请求权限的原因{@link RationaleDialogFragmentCompat}
     *
     * @param fragmentManager android.support.v4.app.FragmentManager fragmentManager
     * @param rationale       显示的信息
     * @param positiveButton  确定描述
     * @param negativeButton  取消描述
     * @param requestCode     请求码
     * @param perms           权限
     */
    private static void showRationaleDialogFragmentCompat(@NonNull final android.support.v4.app.FragmentManager fragmentManager,
                                                          @NonNull String rationale, @StringRes int positiveButton, @StringRes int negativeButton, final int requestCode,
                                                          @NonNull final String... perms) {

        RationaleDialogFragmentCompat fragment = RationaleDialogFragmentCompat.newInstance(positiveButton, negativeButton, rationale,
                requestCode, perms);
        if (null == fragment) {
            XLog.i(EasyPermissions.class, "请求权限fragment为空");
            return;
        }
        fragment.show(fragmentManager, DIALOG_TAG);
    }

    /**
     * 显示请求权限的原因{@link RationaleDialogFragment}
     *
     * @param fragmentManager android.app.FragmentManager fragmentManager
     * @param rationale       显示的信息
     * @param positiveButton  确定描述
     * @param negativeButton  取消描述
     * @param requestCode     请求码
     * @param perms           权限
     */
    private static void showRationaleDialogFragment(@NonNull final android.app.FragmentManager fragmentManager, @NonNull String rationale, @StringRes int positiveButton,
                                                    @StringRes int negativeButton, final int requestCode, @NonNull final String... perms) {

        RationaleDialogFragment fragment = RationaleDialogFragment.newInstance(positiveButton, negativeButton, rationale, requestCode, perms);
        if (null == fragment) {
            XLog.i(EasyPermissions.class, "请求权限fragment为空");
            return;
        }
        fragment.show(fragmentManager, DIALOG_TAG);
    }

    /**
     * @param object         实现接口的类
     * @param rationale      显示的信息
     * @param positiveButton 确定描述
     * @param negativeButton 取消描述
     * @param requestCode    请求码
     * @param perms          权限
     */
    private static void showRationaleAlertDialog(@NonNull final Object object, @NonNull String rationale, @StringRes int positiveButton,
                                                 @StringRes int negativeButton, final int requestCode, @NonNull final String... perms) {

        Activity activity = getActivity(object);
        if (activity == null) {
            throw new IllegalStateException("Can't show rationale dialog for null Activity");
        }
        new AlertDialog.Builder(activity).setCancelable(false).setMessage(rationale)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executePermissionsRequest(object, perms, requestCode);
                    }
                })
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (object instanceof PermissionCallbacks) {
                            ((PermissionCallbacks) object).onPermissionsDenied(requestCode, Arrays.asList(perms));
                        }
                    }
                }).create().show();
    }

    /**
     * 判断是否有权限被拒绝
     *
     * @param object            实现权限回调的类
     * @param deniedPermissions 被拒绝的权限 {@link PermissionCallbacks#onPermissionsDenied(int, List)}
     * @return true 被拒绝 false 违背拒绝
     */
    public static boolean somePermissionPermanentlyDenied(@NonNull Object object, @NonNull List<String> deniedPermissions) {
        for (String deniedPermission : deniedPermissions) {
            if (permissionPermanentlyDenied(object, deniedPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 部分权限拒绝是否再次询问
     *
     * @param object           实现权限回调的类
     * @param deniedPermission 被拒绝的权限
     * @return true 不显示 false 显示
     */
    public static boolean permissionPermanentlyDenied(@NonNull Object object, @NonNull String deniedPermission) {
        return !shouldShowRequestPermissionRationale(object, deniedPermission);
    }

    /**
     * 跳转 权限设置页  {@link AppSettingsDialog}
     *
     * @param requestCode  请求码
     * @param permissions  需要请求的权限
     * @param grantResults 允许的权限
     * @param receivers    实现回调的类
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, @NonNull Object... receivers) {
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        for (Object object : receivers) {
            if (!granted.isEmpty()) {
                if (object instanceof PermissionCallbacks) {
                    ((PermissionCallbacks) object).onPermissionsGranted(requestCode, granted);
                }
            }
            if (!denied.isEmpty()) {
                if (object instanceof PermissionCallbacks) {
                    ((PermissionCallbacks) object).onPermissionsDenied(requestCode, denied);
                }
            }
        }

    }

    /**
     * 系统显示请求权限的描述
     *
     * @param object 实现回掉接口的类
     * @param perm   需要请求的权限
     * @return true 已显示 false未显示
     */
    @TargetApi(23)
    private static boolean shouldShowRequestPermissionRationale(@NonNull Object object, @NonNull String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else
            return object instanceof android.app.Fragment && ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
    }

    /**
     * 进行权限请求
     *
     * @param object      实现权限回调的类
     * @param perms       权限
     * @param requestCode 请求码
     */
    @TargetApi(23)
    static void executePermissionsRequest(@NonNull Object object, @NonNull String[] perms, int requestCode) {
        checkCallingObjectSuitability(object);
        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    /**
     * 获取avtivity
     *
     * @param object 实现权限回调的类
     * @return Activty
     */
    @TargetApi(11)
    private static Activity getActivity(@NonNull Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        } else {
            return null;
        }
    }

    /**
     * 获取SupportFragmentManager
     *
     * @param object 实现权限回调的类
     * @return SupportFragmentManager
     */
    @Nullable
    @SuppressLint("NewApi")
    private static android.support.v4.app.FragmentManager getSupportFragmentManager(@NonNull Object object) {
        if (object instanceof FragmentActivity) {
            return ((FragmentActivity) object).getSupportFragmentManager();
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getChildFragmentManager();
        }
        return null;
    }

    /**
     * 获取FragmentManager
     *
     * @param object 实现权限回调的类
     * @return FragmentManager
     */
    @Nullable
    private static android.app.FragmentManager getFragmentManager(@NonNull Object object) {
        if (object instanceof Activity) {
            return ((Activity) object).getFragmentManager();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getChildFragmentManager();
        }
        return null;
    }

    /**
     * 检查是否时Activity或Framgent
     *
     * @param object 实现权限回调接口的类
     */
    private static void checkCallingObjectSuitability(@Nullable Object object) {
        if (object == null) {
            throw new NullPointerException("Activity or Fragment should not be null");
        }
        boolean isActivity = object instanceof Activity;
        boolean isSupportFragment = object instanceof Fragment;
        boolean isAppFragment = object instanceof android.app.Fragment;
        boolean isMinSdkM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        if (!(isSupportFragment || isActivity || (isAppFragment && isMinSdkM))) {
            if (isAppFragment) {
                throw new IllegalArgumentException("Target SDK needs to be greater than 23 if caller is android.app.Fragment");
            } else {
                throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
            }
        }
    }
}
