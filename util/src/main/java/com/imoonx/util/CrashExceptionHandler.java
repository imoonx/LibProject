package com.imoonx.util;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * <p>
 * 需要在Application中注册，为了要在程序启动器就监控整个程序。
 */
public class CrashExceptionHandler implements UncaughtExceptionHandler {

    //系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashExceptionHandler instance;
    /**
     * 文件夹名称默认cssi，建议每个工程设置不同的名称便于查找异常
     */
    private String dirName;

    public String getDirName() {
        if (TextUtils.isEmpty(dirName))
            return "cssi";
        return dirName;
    }

    public CrashExceptionHandler setDirName(String dirName) {
        this.dirName = dirName;
        return this;
    }

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashExceptionHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     *
     * @return CrashExceptionHandler
     */
    public static CrashExceptionHandler getInstance() {
        if (instance == null)
            instance = new CrashExceptionHandler();
        return instance;
    }

    /**
     * 初始化
     */
    public void init() {
        //获取系统默认的UncaughtException处理器      
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器      
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        //如果用户没有处理则让系统默认的异常处理器来处理
        mDefaultHandler.uncaughtException(thread, ex);
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 异常信息
     */
    private void handleException(Throwable ex) {
        if (ex != null) {
            saveCatchInfo2File(ex);
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex 异常信息
     */
    @SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
    private void saveCatchInfo2File(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        FileOutputStream fos = null;
        try {
            PackageInfo pi = TDevice.getPackageInfo(TDevice.getPackageName());
            if (pi != null) {
                sb.append("versionName" + "--").append(pi.versionName).append("\n");
                sb.append("versionCode" + "--").append(pi.versionCode).append("\n");
            }
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                sb.append(field.getName()).append("--").append(field.get(null).toString()).append("\n");
            }
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            sb.append("exception").append(result);
            String dirPath = getDirPath();
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filePath = dirPath + getFileName();
            fos = new FileOutputStream(filePath);
            fos.write(sb.toString().getBytes());
            saveFileComplete(filePath);
        } catch (Exception e) {
            XLog.e(CrashExceptionHandler.class, "an error occured while writing file..." + e);
        } finally {
            XIOUtil.close(fos);
        }
    }

    /**
     * 异常信息保存完成 可以进行其他操作 如上传
     *
     * @param filePath 文件路径
     */
    public void saveFileComplete(String filePath) {
    }

    /**
     * 异常信息存储目录
     *
     * @return 目录路径
     */
    public String getDirPath() {
        return Environment.getExternalStorageDirectory() + File.separator + "CSSI" + File.separator + getDirName()
                + File.separator + "Exception" + File.separator;
    }

    /**
     * 异常信息存储名称
     *
     * @return 文件名称
     */
    @SuppressLint("SimpleDateFormat")
    public String getFileName() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".log";
    }

}