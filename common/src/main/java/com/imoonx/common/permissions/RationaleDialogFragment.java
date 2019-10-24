package com.imoonx.common.permissions;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.imoonx.common.ui.CustomDialogFragment;


/**
 * {@link CustomDialogFragment} to display rationale for permission requests when the
 * request comes from a Fragment or Activity that can host a Fragment.
 */

public class RationaleDialogFragment extends CustomDialogFragment {

    private PermissionCallbacks permissionCallbacks;

    static RationaleDialogFragment newInstance(@StringRes int positiveButton, @StringRes int negativeButton, @NonNull String rationaleMsg,
                                               int requestCode, @NonNull String[] permissions) {

        // Create new Fragment
        RationaleDialogFragment dialogFragment = new RationaleDialogFragment();
        // Initialize configuration as arguments
        RationaleDialogConfig config = new RationaleDialogConfig(positiveButton, negativeButton, rationaleMsg, requestCode, permissions);
        dialogFragment.setArguments(config.toBundle());

        return dialogFragment;
    }

    @SuppressLint("NewApi")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // getParentFragment() requires API 17 or higher
        if (getParentFragment() != null && getParentFragment() instanceof PermissionCallbacks) {
            permissionCallbacks = (PermissionCallbacks) getParentFragment();
        } else if (context instanceof PermissionCallbacks) {
            permissionCallbacks = (PermissionCallbacks) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        permissionCallbacks = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Rationale dialog should not be cancelable
        setCancelable(false);
        // Get config from arguments, create click listener
        RationaleDialogConfig config = new RationaleDialogConfig(getArguments());
        RationaleDialogClickListener clickListener = new RationaleDialogClickListener(this, config, permissionCallbacks);
        // Create an AlertDialog
        return config.createDialog(getActivity(), clickListener);
    }

}
