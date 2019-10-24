package com.imoonx.common.permissions;

import android.support.v4.app.ActivityCompat;

import java.util.List;

/**
 * 权限回调接口
 */
public interface PermissionCallbacks extends ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * 请求成功
     *
     * @param requestCode 请求码
     * @param perms 权限集合
     */
    void onPermissionsGranted(int requestCode, List<String> perms);

    /**
     * 请求失败
     *
     * @param requestCode 请求码
     * @param perms 权限集合
     */
    void onPermissionsDenied(int requestCode, List<String> perms);

}