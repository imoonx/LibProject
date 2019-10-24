package com.imoonx.zbar.core;

/**
 * 扫描结果处理
 */
public interface ScanResultCallback {
    /**
     * 处理扫描结果
     *
     * @param result 扫描结果
     */
    void onScanQRCodeSuccess(String result);

    /**
     * 处理打开相机出错
     */
    void onScanQRCodeOpenCameraError();
}