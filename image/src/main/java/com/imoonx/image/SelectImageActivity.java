package com.imoonx.image;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.imoonx.common.base.BaseActivity;
import com.imoonx.common.permissions.EasyPermissions;
import com.imoonx.common.permissions.PermissionCallbacks;
import com.imoonx.common.ui.dialog.DialogHelper;
import com.imoonx.image.bean.SelectOptions;
import com.imoonx.image.fragment.SelectCamImageFragment;
import com.imoonx.image.fragment.SelectImageFragment;
import com.imoonx.image.interf.SelectImageContractListener;
import com.imoonx.util.XLog;

import java.util.List;

/**
 * 图片选择
 */
public class SelectImageActivity extends BaseActivity implements PermissionCallbacks, SelectImageContractListener.Operator {

    private static final int RC_CAMERA_PERM = 0x03;
    private static final int RC_EXTERNAL_STORAGE = 0x04;

    private SelectImageContractListener.View mView;
    private static SelectOptions mOption;

    public static void show(Context context, SelectOptions options) {
        mOption = options;
        context.startActivity(new Intent(context, SelectImageActivity.class));
    }

    @Override
    public int getLayoutID() {
        return R.layout.layout_activity_fragment;
    }

    @Override
    protected int getTitleDesc() {
        return R.string.image_select;
    }

    @Override
    public void initWidget() {
        requestExternalStorage();
    }

    @Override
    public void requestCamera() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            if (mView != null) {
                mView.onOpenCameraSuccess();
            }
        } else {
            EasyPermissions.requestPermissions(this, "", RC_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    @Override
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (mView == null) {
                handleView();
            }
        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onBack() {
        onSupportNavigateUp();
    }

    @Override
    public void setDataView(SelectImageContractListener.View view) {
        mView = view;
    }

    @Override
    protected void onDestroy() {
        mOption = null;
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == RC_EXTERNAL_STORAGE) {
            removeView();
            DialogHelper.getConfirmDialog(this, "没有权限, 你需要去设置中开启读取手机存储权限.",
                    "去设置", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        } else {
            if (mView != null)
                mView.onCameraPermissionDenied();
            DialogHelper.getConfirmDialog(this, "没有权限, 你需要去设置中开启相机权限.", "去设置",
                    "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }
    }

    private void removeView() {
        SelectImageContractListener.View view = mView;
        if (view == null)
            return;
        try {
            getSupportFragmentManager().beginTransaction().remove((Fragment) view).commitAllowingStateLoss();
        } catch (Exception e) {
            XLog.e(SelectImageActivity.class, e);
        }
    }

    private void handleView() {
        try {
            if (mOption == null) {
                XLog.i(getClass(), "mOption为空");
            } else {
                XLog.i(getClass(), "mOption不为空" + mOption.isCam());
            }
            if (mOption.isCam()) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, SelectCamImageFragment.newInstance(mOption))
                        .commitAllowingStateLoss();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, SelectImageFragment.newInstance(mOption))
                        .commitAllowingStateLoss();
            }
        } catch (Exception e) {
            XLog.e(SelectImageActivity.class, e);
        }
    }
}
