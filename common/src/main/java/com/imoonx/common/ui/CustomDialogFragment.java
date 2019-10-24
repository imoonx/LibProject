package com.imoonx.common.ui;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.imoonx.util.XLog;

/**
 * Created by 36238 on 2019/3/11 星期一
 * <p>
 * Caused by: java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
 * <p>
 * 自定义DialogFragment
 */
@SuppressWarnings("deprecation")
public class CustomDialogFragment extends DialogFragment {

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag).addToBackStack(null);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            XLog.e(CustomDialogFragment.class, e);
        }
    }
}
