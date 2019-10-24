package com.imoonx.util;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 工具类
 */
public class XUtil {

    /**
     * 判断是否有外置存储卡
     *
     * @return true false
     */
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取设备照片存储路径
     *
     * @return 存储路径
     */
    public static String getCameraPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/";// filePath:/sdcard/
    }

    /**
     * 获取照片保存全路径
     *
     * @return 全路径
     */
    @SuppressLint("SimpleDateFormat")
    public static String getSaveImageFullName() {
        return "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";// 照片命名
    }
}
