package com.imoonx.http.glide;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.imoonx.util.XLog;

import java.util.Locale;

/**
 * Glide 图片加载 监听
 */
public class LoggingListener<T, R> implements RequestListener<T, R> {
    @Override
    public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
        XLog.i(LoggingListener.class, String.format(Locale.ROOT,
                "onException(%s, %s, %s, %s)", null == e ? "错误异常无错误信息" : e.toString(), model, target, isFirstResource));
        return false;
    }

    @Override
    public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
        XLog.i(LoggingListener.class, String.format(Locale.ROOT,
                "onResourceReady(%s, %s, %s, %s, %s)", resource, model, target, isFromMemoryCache, isFirstResource));
        return false;
    }
}