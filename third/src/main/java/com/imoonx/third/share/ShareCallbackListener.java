package com.imoonx.third.share;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 分享回调
 */
public interface ShareCallbackListener {

    /**
     * 分享回调监听
     *
     * @param platform 平台类型 {@link SHARE_MEDIA}
     * @param status   状态 {@link CustomShareListener.STATUS}
     */
    void onShareCallbackListener(SHARE_MEDIA platform, CustomShareListener.STATUS status);

}
