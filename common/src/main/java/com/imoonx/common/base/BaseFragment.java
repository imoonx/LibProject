package com.imoonx.common.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.imoonx.common.R;
import com.imoonx.common.interf.OnBackCallBackListener;
import com.imoonx.common.ui.dialog.DialogControl;
import com.imoonx.common.ui.dialog.DialogHelper;
import com.imoonx.common.ui.dialog.WaitDialog;
import com.imoonx.util.BaseApplication;
import com.imoonx.util.XLog;

/**
 * s * fragemtn 基类
 */
public abstract class BaseFragment extends Fragment implements DialogControl, View.OnClickListener {

    protected View mRoot;
    protected Bundle mBundle;
    private boolean _isVisible;
    private WaitDialog _waitDialog;

    protected RequestManager mImageLoader;
    private Context mContext;
    private OnBackCallBackListener mOnBackCallBackListener;

    /**
     * 设置背景透明度
     *
     * @param alpha 0-1
     */
    protected void setBackageAlpha(float alpha) {
        try {
            if (null != getActivity()) {
                Window window = getActivity().getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.alpha = alpha;
                window.setAttributes(attributes);
            }
        } catch (Exception e) {
            XLog.e(this.getClass(), e);
        }
    }

    /**
     * 获取RequestManager
     *
     * @return RequestManager
     */
    public synchronized RequestManager getImageLoader() {
        if (mImageLoader == null)
            mImageLoader = Glide.with(getContext());
        return mImageLoader;
    }

    public Context getContext() {
        if (null == mContext)
            return BaseApplication.context();
        else
            return mContext;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        initBundle(mBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot != null) {
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null)
                parent.removeView(mRoot);
        } else {
            if (getLayoutID() == 0)
                throw new NullPointerException("View Not 0");
            else
                mRoot = inflater.inflate(getLayoutID(), container, false);
            initWidgetBefore(mRoot);
            // Bind view
            initWidget(mRoot);
        }
        _isVisible = true;
        return mRoot;
    }

    protected void initWidgetBefore(View rootView) {
    }

    protected abstract void initWidget(View rootView);

    protected abstract int getLayoutID();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof OnBackCallBackListener) {
            this.mOnBackCallBackListener = (OnBackCallBackListener) getActivity();
        }
        initData();
    }

    @Override
    public void onDestroy() {
        if (null != mBundle)
            mBundle = null;
        if (null != mContext)
            mContext = null;
        if (null != mImageLoader)
            mImageLoader = null;
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != mOnBackCallBackListener)
            mOnBackCallBackListener.onSelectedFragment(this);
    }

    @Override
    public WaitDialog showWaitDialog() {
        return showWaitDialog(R.string.date_is_loading);
    }

    @Override
    public WaitDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid));
    }

    @Override
    public WaitDialog showWaitDialog(String message) {
        return showWaitDialog(message, true);
    }

    @Override
    public WaitDialog showWaitDialog(String message, boolean isCancel) {
        if (_isVisible) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelper.getWaitDialog(getActivity(), message);
            }
            if (_waitDialog != null) {
                _waitDialog.setMessage(message);
                if (!isCancel) {
                    _waitDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0;
                        }
                    });
                }
                _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    @Override
    public void hideWaitDialog() {
        if (_isVisible && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception e) {
                XLog.e(this.getClass(), e);
            }
        }
    }


    protected void initData() {
    }

    protected void initBundle(Bundle bundle) {
    }

    public void onClick(View v) {
    }

    public boolean onBackPressed() {
        return false;
    }
}
