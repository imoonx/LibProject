package com.imoonx.common.manager;

import android.content.Context;

import com.imoonx.util.TDevice;
import com.imoonx.util.XIOUtil;
import com.imoonx.util.XLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * 緩存
 */
public class CacheManager {

    // wifi缓存时间为2分钟
    private static long wifi_cache_time = 2 * 60 * 1000;
    // 其他网络环境为1小时
    private static long other_cache_time = 30 * 1000;

    public static long getWifi_cache_time() {
        return wifi_cache_time;
    }

    public static void setWifi_cache_time(long wifi_cache_time) {
        CacheManager.wifi_cache_time = wifi_cache_time;
    }

    public static long getOther_cache_time() {
        return other_cache_time;
    }

    public static void setOther_cache_time(long other_cache_time) {
        CacheManager.other_cache_time = other_cache_time;
    }

    /**
     * 保存内容
     *
     * @param context  上下文
     * @param date     数据
     * @param fileName 文件名称
     * @return boolean
     */
    public static boolean saveObject(Context context, String date, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(date.getBytes());
            XIOUtil.flush(fos);
            return true;
        } catch (Exception e) {
            XLog.e(CacheManager.class, e);
            return false;
        } finally {
            XIOUtil.close(fos);
        }
    }

    /**
     * 删除内容
     *
     * @param context  上下文
     * @param fileName 文件名
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteObject(Context context, String fileName) {
        try {
            String filePath = context.getFilesDir().getPath() + "/" + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            XLog.e(CacheManager.class, e);
        }
    }

    /**
     * 读取内容
     *
     * @param fileName 文件名
     * @return 读取的字符串
     */
    public static String readObject(Context context, String fileName) {
        if (!isExistDataCache(context, fileName))
            return "";
        FileInputStream fis = null;
        ByteArrayOutputStream stream = null;
        try {
            fis = context.openFileInput(fileName);
            stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                stream.write(buffer, 0, len);
            }
            return stream.toString();
        } catch (Exception e) {
            XLog.e(CacheManager.class, e);
        } finally {
            XIOUtil.close(stream, fis);
        }
        return "";
    }

    /**
     * 判断缓存是否存在
     *
     * @param fileName 文件名称
     * @return true false
     */
    public static boolean isExistDataCache(Context context, String fileName) {
        if (context == null)
            return false;
        if (isCacheDataFailure(context, fileName))
            return false;
        boolean exist = false;
        File data = context.getFileStreamPath(fileName);
        if (data.exists())
            exist = true;
        return exist;
    }

    /**
     * 判断缓存是否已经失效
     *
     * @param context  上下文
     * @param fileName 文件名称
     * @return true false
     */
    public static boolean isCacheDataFailure(Context context, String fileName) {
        File data = context.getFileStreamPath(fileName);
        if (!data.exists()) {
            return false;
        }
        long existTime = System.currentTimeMillis() - data.lastModified();
        boolean failure;
        if (TDevice.isWifiOpen()) {
            failure = existTime > wifi_cache_time;
        } else {
            failure = existTime > other_cache_time;
        }
        return failure;
    }
}
