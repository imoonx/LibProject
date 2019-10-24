package com.imoonx.common.base;

import android.content.Context;

import com.bumptech.glide.RequestManager;

import java.util.Date;

/**
 * recycle adapter
 */
public abstract class BaseGeneralRecyclerAdapter<T> extends BaseRecyclerAdapter<T> {

    protected Callback mCallBack;

    public BaseGeneralRecyclerAdapter(Callback callback, int mode) {
        super(callback.getContext(), mode);
        mCallBack = callback;
        setState(STATE_LOADING, true);
    }

    public interface Callback {
        RequestManager getImgLoader();

        Context getContext();

        Date getSystemTime();
    }
}
