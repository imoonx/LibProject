package com.imoonx.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.imoonx.util.XFileUtil.PathStatus.ERROR;
import static com.imoonx.util.XFileUtil.PathStatus.EXITS;
import static com.imoonx.util.XFileUtil.PathStatus.SUCCESS;

/**
 * 文件操作类
 */
public class XFileUtil {

    /**
     * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * @param context  上下文
     * @param fileName 文件名称
     * @param content  内容
     */
    public static void write(Context context, String fileName, String content) {
        if (content == null)
            content = "";
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
        } catch (Exception e) {
            XLog.e(XFileUtil.class, e);
        } finally {
            XIOUtil.close(fos);
        }
    }

    /**
     * 读取文本文件
     *
     * @param context  上下文
     * @param fileName 文件名称
     * @return 字符串
     */
    public static String read(Context context, String fileName) {
        try {
            FileInputStream in = context.openFileInput(fileName);
            return readInStream(in);
        } catch (Exception e) {
            XLog.e(XFileUtil.class, e);
        }
        return "";
    }

    /**
     * 从输入流中读取内容
     *
     * @param inStream 输入流
     * @return 流中的内容
     */
    public static String readInStream(InputStream inStream) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[512];
            int length;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            return outStream.toString();
        } catch (IOException e) {
            XLog.e(XFileUtil.class, e);
            return null;
        } finally {
            XIOUtil.close(inStream, outStream);
        }
    }

    /**
     * 向手机写图片
     *
     * @param buffer   内容字节
     * @param folder   文件夹
     * @param fileName 文件名称
     * @return 是否写入成功
     */
    public static boolean writeFile(byte[] buffer, String folder, String fileName) {
        boolean writeSucc = false;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        String folderPath = "";
        if (sdCardExist) {
            folderPath = Environment.getExternalStorageDirectory() + File.separator + folder + File.separator;
        } else {
            writeSucc = false;
        }
        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(folderPath + fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(buffer);
            writeSucc = true;
        } catch (Exception e) {
            XLog.e(XFileUtil.class, e);
        } finally {
            XIOUtil.close(out);
        }
        return writeSucc;
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath 文件路径
     * @return 文件名称
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String getFileNameNoFormat(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf('.');
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1, point);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名称
     * @return 扩展名
     */
    public static String getFileFormat(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return "";
        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }

    /**
     * 获取目录文件大小
     *
     * @param dir 目录
     * @return 目录大小
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    dirSize += file.length();
                } else if (file.isDirectory()) {
                    dirSize += file.length();
                    dirSize += getDirSize(file); // 递归调用继续统计
                }
            }
        }
        return dirSize;
    }

    /**
     * 获取目录文件个数
     *
     * @param dir 目录
     * @return 目录文件数
     */
    public long getFileList(File dir) {
        long count = 0;
        File[] files = dir.listFiles();
        count = files.length;
        for (File file : files) {
            if (file.isDirectory()) {
                count = count + getFileList(file);// 递归
                count--;
            }
        }
        return count;
    }

    /**
     * 输入流转换字节
     *
     * @param in 输入流
     * @return 字节 异常返回null
     */
    public static byte[] toBytes(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int ch;
            while ((ch = in.read()) != -1) {
                out.write(ch);
            }
            return out.toByteArray();
        } catch (Exception e) {
            XLog.e(XFileUtil.class, e);
            return null;
        } finally {
            XIOUtil.close(in, out);
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param name 文件名称
     * @return true false
     */
    public static boolean checkFileExists(String name) {
        boolean status;
        if (!name.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + name);
            status = newPath.exists();
        } else {
            status = false;
        }
        return status;
    }

    /**
     * 检查路径是否存在
     *
     * @param path 路径
     * @return true false
     */
    public static boolean checkFilePathExists(String path) {
        return new File(path).exists();
    }

    /**
     * 计算SD卡的剩余空间
     *
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getFreeDiskSpace() {
        String status = Environment.getExternalStorageState();
        long freeSpace = 0;
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                freeSpace = availableBlocks * blockSize / 1024;
                return (freeSpace);
            } catch (Exception e) {
                XLog.e(XFileUtil.class, e);
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * 新建目录
     *
     * @param directoryName 目录名称
     * @return 是否创建成功
     */
    public static boolean createDirectory(String directoryName) {
        boolean status;
        if (!directoryName.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + directoryName);
            status = newPath.mkdir();
        } else
            status = false;
        return status;
    }

    /**
     * 检查是否安装SD卡
     *
     * @return true false
     */
    public static boolean checkSaveLocationExists() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 检查是否安装外置的SD卡
     *
     * @return true false
     */
    public static boolean checkExternalSDExists() {
        Map<String, String> evn = System.getenv();
        return evn.containsKey("SECONDARY_STORAGE");
    }

    /**
     * 删除目录(包括：目录里的所有文件)
     *
     * @param fileName 目录名称
     * @return 是否删除成功
     */
    public static boolean deleteDirectory(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();
        if (!fileName.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isDirectory()) {
                String[] listfile = newPath.list();
                try {
                    for (int i = 0; i < listfile.length; i++) {
                        File deletedFile = new File(newPath.toString() + "/" + listfile[i]);
                        deletedFile.delete();
                    }
                    newPath.delete();
                    status = true;
                } catch (Exception e) {
                    status = false;
                    XLog.e(XFileUtil.class, e);
                }
            } else
                status = false;
        } else
            status = false;
        return status;
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名称
     * @return 是否删除成功
     */
    public static boolean deleteFile(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();
        if (!fileName.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isFile()) {
                try {
                    newPath.delete();
                    status = true;
                } catch (SecurityException e) {
                    status = false;
                    XLog.e(XFileUtil.class, e);
                }
            } else
                status = false;
        } else
            status = false;
        return status;
    }

    /**
     * 删除空目录
     *
     * @param path 路径
     * @return 返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
     */
    public static int deleteBlankPath(String path) {
        File f = new File(path);
        if (!f.canWrite()) {
            return 1;
        }
        if (f.list() != null && f.list().length > 0) {
            return 2;
        }
        if (f.delete()) {
            return 0;
        }
        return 3;
    }

    /**
     * 重命名
     *
     * @param oldName 旧文件名称
     * @param newName 新文件名称
     * @return 是否成功
     */
    public static boolean reNamePath(String oldName, String newName) {
        File f = new File(oldName);
        return f.renameTo(new File(newName));
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile())
            return f.delete();
        return false;
    }

    /**
     * 清空一个文件夹
     *
     * @param filePath 文件夹路径
     */
    public static void clearFileWithPath(String filePath) {
        List<File> files = listPathFiles(filePath);
        if (files.isEmpty()) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                clearFileWithPath(f.getAbsolutePath());
            } else {
                f.delete();
            }
        }
    }

    /**
     * 获取SD卡的根目录
     *
     * @return SD卡的根目录
     */
    public static String getSDRoot() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取手机外置SD卡的根目录
     *
     * @return 外置SD卡的根目录
     */
    public static String getExternalSDRoot() {
        Map<String, String> evn = System.getenv();
        return evn.get("SECONDARY_STORAGE");
    }

    /**
     * 列出root目录下所有子目录
     *
     * @param root 根目录
     * @return 绝对路径
     */
    public static List<String> listPath(String root) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        // 过滤掉以.开始的文件夹
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isDirectory() && !f.getName().startsWith(".")) {
                    allDir.add(f.getAbsolutePath());
                }
            }
        }
        return allDir;
    }

    /**
     * 获取一个文件夹下的所有文件
     *
     * @param root 根目录
     * @return 文件夹下的所有文件
     */
    public static List<File> listPathFiles(String root) {
        List<File> allDir = new ArrayList<File>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        File[] files = path.listFiles();
        for (File f : files) {
            if (f.isFile())
                allDir.add(f);
            else
                listPath(f.getAbsolutePath());
        }
        return allDir;
    }

    public enum PathStatus {
        SUCCESS, EXITS, ERROR
    }

    /**
     * 创建目录
     *
     * @param newPath 路径
     * @return 创建状态
     */
    public static PathStatus createPath(String newPath) {
        File path = new File(newPath);
        if (path.exists()) {
            return EXITS;
        }
        if (path.mkdir()) {
            return SUCCESS;
        } else {
            return ERROR;
        }
    }

    /**
     * 截取路径名
     *
     * @param absolutePath 路径
     * @return 截取后的路径名
     */
    public static String getPathName(String absolutePath) {
        int start = absolutePath.lastIndexOf(File.separator) + 1;
        int end = absolutePath.length();
        return absolutePath.substring(start, end);
    }

    /**
     * 获取应用程序缓存文件夹下的指定目录
     *
     * @param context 上下文
     * @param dir     目录名称
     * @return 目录路径
     */
    public static String getAppCache(Context context, String dir) {
        String savePath = context.getCacheDir().getAbsolutePath() + "/" + dir + "/";
        File savedir = new File(savePath);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }
        return savePath;
    }

    /**
     * 复制文件
     *
     * @param srcFile  原始文件
     * @param saveFile 新文件
     * @return 是否复制成功
     */
    public static boolean copyFile(File srcFile, File saveFile) {
        File parentFile = saveFile.getParentFile();
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs())
                return false;
        }
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(srcFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(saveFile));
            byte[] buffer = new byte[1024 * 4];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            XLog.e(XFileUtil.class, e);
            return false;
        } finally {
            XIOUtil.close(inputStream, outputStream);
        }
    }

    /**
     * 将assets中的文件复制到指定文件夹下
     *
     * @param context 上下文
     * @param srcPath 原始文件名
     * @param dstPath 复制后的文件名
     * @return 返回复制后的文件路径 异常返回null
     */
    public static String copyAssetsToDst(Context context, String srcPath, String dstPath) {

        InputStream is = null;
        FileOutputStream fos = null;

        try {
            File outFile = new File(Environment.getExternalStorageDirectory(), dstPath);
            is = context.getAssets().open(srcPath);
            fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            return outFile.getAbsolutePath();
        } catch (Exception e) {
            XLog.e(XFileUtil.class, e);
            return null;
        } finally {
            XIOUtil.close(is, fos);
        }
    }

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file.length() : 0;
    }

    /**
     * 转换文件大小
     *
     * @param fileSize 文件大小
     * @return B/KB/MB/GB
     */
    public static String formetFileSize(long fileSize) {
        return formetFileSizeD(fileSize);
    }

    /**
     * 转换文件大小
     *
     * @param fileSize 文件大小
     * @return B/KB/MB/GB
     */
    public static String formetFileSizeD(double fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 读取文件的最后修改时间的方法
     *
     * @param file 文件
     * @return 最后修改时间
     */
    @SuppressLint("SimpleDateFormat")
    public static String getFileLastModifiedTime(File file) {
        if (null == file || !file.exists())
            return "";
        Calendar cal = Calendar.getInstance();
        long time = file.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTimeInMillis(time);
        return formatter.format(cal.getTime());
    }

    /**
     * 读取文件的最后修改时间的方法
     *
     * @param filePath 文件路径
     * @return 最后修改时间
     */
    @SuppressLint("SimpleDateFormat")
    public static String getFileLastModifiedTime(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return "";
        return getFileLastModifiedTime(new File(filePath));
    }

    /**
     * 获取扩展内存的路径
     *
     * @param content 上下文
     * @return 扩展内存的路径
     */
    public static String getStoragePath(Context content) {
        StorageManager mStorageManager = (StorageManager) content.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (removable)
                    return path;
            }
        } catch (Exception e) {
            XLog.e(XFileUtil.class, e);
        }
        return null;
    }

    /**
     * 判断是否在后缀集合内
     *
     * @param fileName   文件名称
     * @param fileSuffix 后缀集合
     * @return 符合true 不符合false
     */
    public static boolean checkSuffix(String fileName, String[] fileSuffix) {
        for (String suffix : fileSuffix) {
            if (fileName != null && fileName.toLowerCase().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 过滤目录中的隐藏文件
     *
     * @param file 目录
     * @return 过滤后的文件数组
     */
    public static File[] fileFilter(File file) {
        return file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isHidden();
            }
        });
    }

    /**
     * 清空目录
     *
     * @param context  上下文
     * @param fileName 目录名称
     * @return 是否已清空
     */
    public static boolean deleteDirectory(Context context, String fileName) {
        SecurityManager checker = new SecurityManager();
        boolean status;
        if (!TextUtils.isEmpty(fileName)) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + fileName);
            XLog.i(XFileUtil.class, "delete path=" + newPath);
            checker.checkDelete(newPath.toString());
            if (newPath.isDirectory()) {
                XLog.i(XFileUtil.class, "是目录newPath.isDirectory()");
                String[] listfile = newPath.list();
                try {
                    for (int i = 0; i < listfile.length; i++) {
                        File deletedFile = new File(newPath.toString() + "/" + listfile[i]);
                        deletedFile.delete();
                        deleteFileFromDB(context, newPath.toString() + "/" + listfile[i]);
                    }
                    status = newPath.delete();
                } catch (Exception e) {
                    XLog.e(XFileUtil.class, "异常=" + e);
                    status = false;
                }
            } else {
                XLog.i(XFileUtil.class, "不是目录newPath.isDirectory()");
                status = false;
            }
        } else {
            status = false;
        }
        return status;
    }

    /**
     * 从数据库删除文件
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    public static void deleteFileFromDB(Context context, String filePath) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
        String where = "_data='" + filePath + "'";
        mContentResolver.delete(uri, where, null);
    }

    /**
     * 将assets数据库文件复制到本地
     *
     * @param context     上下文
     * @param packageName 报名
     * @param dbName      数据库名称
     */
    @SuppressLint("SdCardPath")
    public static void copyDataBase(Context context, String packageName, String dbName) {
        String DB_PATH = "/data/data/" + packageName + "/databases/";
        InputStream is = null;
        OutputStream os = null;
        if (!(new File(DB_PATH + dbName)).exists()) {
            File f = new File(DB_PATH);
            if (!f.exists()) {
                f.mkdir();
            }
            try {
                is = context.getAssets().open(dbName);
                os = new FileOutputStream(DB_PATH + dbName);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                XIOUtil.flush(os);
            } catch (Exception e) {
                XLog.e(XFileUtil.class, e);
            } finally {
                XIOUtil.close(os, is);
            }
        }
    }

    /**
     * bitmap转换成字节
     *
     * @param bitmap  bitmap
     * @param maxByte 最大字节数
     * @return 字节数组
     */
    public static byte[] bitmapToBytes(Bitmap bitmap, int maxByte) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            int options = 100;
            while (output.toByteArray().length > maxByte && options != 10) {
                output.reset(); // 清空output
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);// 这里压缩options%，把压缩后的数据存放到output中
                options -= 20;
            }
            bitmap.recycle();
            return output.toByteArray();
        } catch (Exception e) {
            XLog.e(XFileUtil.class, e);
            return null;
        } finally {
            XIOUtil.close(output);
        }
    }

    /**
     * bitmap转换成字节数组 quality默认为20
     *
     * @param bitmap      bitmap
     * @param needRecycle 是否回收bitmap
     * @return 字节数组
     */
    public static byte[] bmpToByteArray(Bitmap bitmap, boolean needRecycle) {
        return bmpToByteArray(bitmap, needRecycle, 20);
    }

    /**
     * bitmap转换成字节数组
     *
     * @param bitmap      bitmap
     * @param needRecycle 是否回收bitmap
     * @param quality     Hint to the compressor, 0-100. 0 meaning compress for
     *                    small size, 100 meaning compress for max quality
     * @return 字节数组
     */
    public static byte[] bmpToByteArray(Bitmap bitmap, boolean needRecycle, int quality) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, output);
            if (needRecycle) {
                bitmap.recycle();
            }
            return output.toByteArray();
        } catch (Exception e) {
            XLog.e(XFileUtil.class, e);
            return null;
        } finally {
            XIOUtil.close(output);
        }
    }

    /**
     * 把html地址转成字节
     *
     * @param url 地址
     * @return 字节数组
     */
    public static byte[] getHtmlByteArray(String url) {
        URL htmlUrl;
        InputStream inStream = null;
        try {
            htmlUrl = new URL(url);
            URLConnection connection = htmlUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
            }
        } catch (Exception e) {
            XLog.e(XFileUtil.class, e);
        }
        return toBytes(inStream);
    }

    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     * 默认文件 30 天
     * zip文件  60天
     *
     * @param sourceFilePath :待压缩的文件路径
     * @return true 打包成功 false 打包失败
     */
    @SuppressWarnings("NumericOverflow")
    public static boolean fileToZip(String sourceFilePath) {
        return fileToZip(sourceFilePath, true, 1000 * 60 * 60 * 24 * 30L, 1000 * 60 * 60 * 24 * 30 * 2L);
    }

    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     *
     * @param sourceFilePath :待压缩的文件路径
     * @param isNeedDelete   是否需要删除原文件
     * @param diffTime       多久之前的文件打包 删除
     * @param zipDiffTime    zip文件时间
     * @return true 打包成功 false 打包失败
     */
    public static boolean fileToZip(String sourceFilePath, boolean isNeedDelete, long diffTime, long zipDiffTime) {
        return fileToZip(sourceFilePath, sourceFilePath, XFormatData.getCurrentTimeStr(), isNeedDelete, diffTime, zipDiffTime);
    }

    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     *
     * @param sourceFilePath :待压缩的文件路径
     * @param zipFilePath    :压缩后存放路径
     * @param fileName       :压缩后文件的名称
     * @param isNeedDelete   是否需要删除原文件
     * @param diffTime       多久之前的文件打包 删除
     * @param zipDiffTime    zip文件时间
     * @return true 打包成功 false 打包失败
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean fileToZip(String sourceFilePath, String zipFilePath, String fileName, boolean isNeedDelete, long diffTime, long zipDiffTime) {
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        //符合规则的文件路径
        List<File> mList = new ArrayList<>();
        if (!sourceFile.exists()) {
            XLog.i(XFileUtil.class, "待压缩的文件目录：" + sourceFilePath + "不存在.");
        } else {
            try {
                File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
                if (zipFile.exists()) {
                    XLog.e(XFileUtil.class, zipFilePath + "目录下存在名字为:" + fileName + ".zip" + "打包文件.");
                } else {
                    File[] sourceFiles = sourceFile.listFiles();
                    if (null == sourceFiles || sourceFiles.length < 1) {
                        XLog.i(XFileUtil.class, "待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");
                    } else {
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
                        byte[] bufs = new byte[1024 * 10];
                        for (File sourceFile1 : sourceFiles) {
                            //不是zip文件并且在时间差范围内
                            if (!sourceFile1.getAbsolutePath().endsWith("zip") && System.currentTimeMillis() - sourceFile1.lastModified() > diffTime) {
                                mList.add(sourceFile1);
                                //创建ZIP实体，并添加进压缩包
                                ZipEntry zipEntry = new ZipEntry(sourceFile1.getName());
                                zos.putNextEntry(zipEntry);
                                //读取待压缩的文件并写进压缩包里
                                fis = new FileInputStream(sourceFile1);
                                bis = new BufferedInputStream(fis, 1024 * 10);
                                int read = 0;
                                while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                                    zos.write(bufs, 0, read);
                                }
                            } else if (sourceFile1.getAbsolutePath().endsWith("zip") && System.currentTimeMillis() - sourceFile1.lastModified() > zipDiffTime) {
                                //超过期限的zip文件
                                mList.add(sourceFile1);
                            }
                        }
                        flag = true;
                    }
                    if (!mList.isEmpty() && isNeedDelete) {
                        //删除文件
                        for (File file : mList) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                XLog.e(XFileUtil.class, e);
            } finally {
                //关闭流
                XIOUtil.close(bis, zos);
            }
        }
        return flag;
    }

    /**
     * 保存数据到文件
     *
     * @param path     文件存放路径
     * @param fileName 文件名
     * @param data     需要保存的数据
     */
    public static void saveFile(String path, String fileName, String data) {
        BufferedWriter bufferdWriter = null;
        FileOutputStream outputStream = null;
        try {
            File file = new File(path);
            if (!file.exists())
                file.mkdirs();
            outputStream = new FileOutputStream(path + fileName);
            bufferdWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferdWriter.write(data);
            bufferdWriter.flush();
        } catch (IOException e) {
            XLog.e(XFileUtil.class, e);
        } finally {
            XIOUtil.close(outputStream, bufferdWriter);
        }
    }

    /**
     * @param path     路径
     * @param fileName 文件名
     * @return String
     */
    public static String readFile(String path, String fileName) {
        BufferedReader bufferedReader = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path + fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuidler = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuidler.append(line);
            }
            return stringBuidler.toString();
        } catch (IOException e) {
            XLog.e(XFileUtil.class, e);
        } finally {
            XIOUtil.close(inputStream, bufferedReader);
        }
        return "";
    }
}