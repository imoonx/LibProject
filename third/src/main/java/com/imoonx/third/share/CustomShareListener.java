package com.imoonx.third.share;

import com.imoonx.util.XLog;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 友盟分享回调
 */
public class CustomShareListener implements UMShareListener {

    private ShareCallbackListener mListener;

    public CustomShareListener(ShareCallbackListener listener) {
        this.mListener = listener;
    }

    public enum STATUS {
        SUCCESS, ERROR, CANCEL
    }

    /**
     * 分享开始回调
     *
     * @param platform 平台类型
     */
    @Override
    public void onStart(SHARE_MEDIA platform) {
    }

    /**
     * 分享成功回调
     *
     * @param platform 平台类型
     */
    @Override
    public void onResult(SHARE_MEDIA platform) {
        if (null != mListener)
            mListener.onShareCallbackListener(platform, STATUS.SUCCESS);
    }

    /**
     * 分享失败回调
     *
     * @param platform 平台类型
     * @t 异常信息
     */
    @Override
    public void onError(SHARE_MEDIA platform, Throwable t) {
        if (null != t)
            XLog.i(CustomShareListener.class, t);
        if (null != mListener)
            mListener.onShareCallbackListener(platform, STATUS.ERROR);
    }

    /**
     * 分享取消回调 微信6.7.2之后会调用onResult
     *
     * @param platform 平台类型
     */
    @Override
    public void onCancel(SHARE_MEDIA platform) {
        if (null != mListener)
            mListener.onShareCallbackListener(platform, STATUS.CANCEL);
    }
}