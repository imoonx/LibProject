package com.imoonx.common.permissions;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;

import java.util.Arrays;

/**
 * 点击监听 {@link RationaleDialogFragment} or
 * {@link RationaleDialogFragmentCompat}.
 */
@SuppressLint("NewApi")
public class RationaleDialogClickListener implements Dialog.OnClickListener {

    private Object mHost;
    private RationaleDialogConfig mConfig;
    private PermissionCallbacks mCallbacks;

    public RationaleDialogClickListener(RationaleDialogFragmentCompat compatDialogFragment, RationaleDialogConfig config,
                                        PermissionCallbacks callbacks) {

        mHost = compatDialogFragment.getParentFragment() != null ? compatDialogFragment.getParentFragment() : compatDialogFragment.getActivity();
        mConfig = config;
        mCallbacks = callbacks;
    }

    public RationaleDialogClickListener(RationaleDialogFragment dialogFragment, RationaleDialogConfig config,
                                        PermissionCallbacks callbacks) {

        mHost = dialogFragment.getParentFragment() != null ? dialogFragment.getParentFragment() : dialogFragment.getActivity();
        mConfig = config;
        mCallbacks = callbacks;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            EasyPermissions.executePermissionsRequest(mHost, mConfig.permissions, mConfig.requestCode);
        } else {
            notifyPermissionDenied();
        }
    }

    private void notifyPermissionDenied() {
        if (mCallbacks != null) {
            mCallbacks.onPermissionsDenied(mConfig.requestCode, Arrays.asList(mConfig.permissions));
        }
    }
}
