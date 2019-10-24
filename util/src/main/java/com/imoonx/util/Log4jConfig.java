package com.imoonx.util;

import android.os.Environment;
import android.text.TextUtils;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.helpers.LogLog;

import java.io.File;
import java.io.IOException;

/**
 * 源自 android-logging-log4j-1.0.3.jar
 */
public class Log4jConfig {

    /**
     * 设置日志级别 ALL<TRACE<DEBUG<INFO<WARN<ERROR<FATAL<OFF。
     * <p>
     * 默认debug
     */
    private Level rootLevel = Level.DEBUG;
    /**
     * ### log文件的格式
     * <p>
     * ### 输出格式解释：
     * ### [%-d{yyyy-MM-dd HH:mm:ss}][Class: %c.%M(%F:%L)] %n[Level: %-5p] - Msg: %m%n
     * <p>
     * ### %d{yyyy-MM-dd HH:mm:ss}: 时间，大括号内是时间格式
     * ### %c: 全类名
     * ### %M: 调用的方法名称
     * ### %F:%L  类名:行号（在控制台可以追踪代码）
     * ###	%n: 换行
     * ### %p: 日志级别，这里%-5p是指定的5个字符的日志名称，为的是格式整齐
     * ### %m: 日志信息
     * <p>
     * ### 输出的信息大概如下：
     * ### [时间{时间格式}][信息所在的class.method(className：lineNumber)] 换行
     * ###	[Level: 5个字符的等级名称] - Msg: 输出信息 换行
     */
    private String filePattern;

    /**
     * 应用名称
     * <p>
     * 默认 cssi
     */
    private String appName;

    /**
     * 日志完整路径
     */
    private String fileName;

    /**
     * 备份数量
     * <p>
     * 默认 5
     */
    private int maxBackupSize = 5;
    /**
     * 每个输出文件大小
     * <p>
     * 默认 5M
     */
    private long maxFileSize = 1024 * 1024 * 5L;
    /**
     * 意谓着所有的消息都会被立即输出
     * <p>
     * 默认值是true
     */
    private boolean immediateFlush = true;
    /**
     * 是否重启配置
     * <p>
     * 默认true
     */
    private boolean resetConfiguration = true;
    /**
     * 是否输出Log4j中的日志
     * <p>
     * 默认false
     */
    private boolean internalDebugging = false;

    /**
     * 将日志输出到控制台
     */
    public static final int CONSOLE_APPENDER = 1;
    /**
     * 将日志输出到文件
     */
    public static final int FILE_APPENDER = 2;
    /**
     * 每一个滚动周期产生一个日志文件
     */
    public static final int DAILY_ROLLING_FILE_APPENDER = 3;
    /**
     * 文件大小到达指定尺寸时产生一个新的文件
     */
    public static final int ROLLING_FILE_APPENDER = 4;

    /**
     * 日志输出方式
     * <p>
     * 默认 {@link DAILY_ROLLING_FILE_APPENDER} 每天产生一个日志文件
     */
    @SuppressWarnings("JavadocReference")
    private int logAppenderType = DAILY_ROLLING_FILE_APPENDER;

    /**
     * 控制台打印日志类型
     * <p>
     * {@link ConsoleAppender.SYSTEM_OUT}
     * <p>
     * {@link ConsoleAppender.SYSTEM_ERR}
     * <p>
     * 默认 {@link ConsoleAppender.SYSTEM_OUT}
     */
    @SuppressWarnings("JavadocReference")
    private String target;

    /**
     * 指定文件滚动周期，即多久生成一个文件
     * <p>
     * '.'yyyy-MM 每月
     * '.'yyyy-ww 每周
     * '.'yyyy-MM-dd 每天
     * '.'yyyy-MM-dd-a 每天两次
     * '.'yyyy-MM-dd-HH 每小时
     * '.'yyyy-MM-dd-HH-mm 每分钟
     * <p>
     * 默认 '.'yyyy-MM-dd 一天生成一次
     */
    private String datePattern;
    private static Logger rootLogger;

    public Log4jConfig() {
    }

    public Log4jConfig(String fileName) {
        this.fileName = fileName;
    }

    public Log4jConfig(String fileName, Level rootLevel) {
        this(fileName);
        this.rootLevel = rootLevel;
    }

    public Log4jConfig(String fileName, Level rootLevel, String filePattern) {
        this(fileName, rootLevel);
        this.filePattern = filePattern;
    }

    public Log4jConfig(String fileName, int maxBackupSize, long maxFileSize, String filePattern, Level rootLevel) {
        this(fileName, rootLevel, filePattern);
        this.maxBackupSize = maxBackupSize;
        this.maxFileSize = maxFileSize;
    }

    /**
     * 配置参数
     */
    public void configure() {
        try {
            Logger root = Logger.getRootLogger();
            if (isResetConfiguration()) {
                LogManager.getLoggerRepository().resetConfiguration();
            }
            LogLog.setInternalDebugging(isInternalDebugging());
            //配置Appenders
            configureAppender(root);
            root.setLevel(getRootLevel());
        } catch (Exception e) {
            XLog.e(Log4jConfig.class, "Exception configuring log system:" + e);
        }
    }

    /**
     * 配置输出方式
     *
     * @param root logger对象
     */
    private void configureAppender(Logger root) throws IOException {
        Layout fileLayout = new PatternLayout(getFilePattern());
        WriterAppender appender = null;
        switch (getLogAppenderType()) {
            //将日志输出到控制台
            case CONSOLE_APPENDER:
                appender = getConsoleAppender(fileLayout);
                break;
            case FILE_APPENDER:
                //将日志输出到文件
                appender = getFileAppender(fileLayout);
                break;
            case DAILY_ROLLING_FILE_APPENDER:
                //每天产生一个日志文件
                appender = getDailyRollingFileAppender(fileLayout);
                break;
            case ROLLING_FILE_APPENDER:
                //文件大小到达指定尺寸时产生一个新的文件
                appender = getRollingFileAppender(fileLayout);
                break;
        }
        if (null != appender) {
            appender.setImmediateFlush(immediateFlush);
            root.addAppender(appender);
        }
    }

    /**
     * 将日志输出到文件
     *
     * @param fileLayout layout
     */
    private WriterAppender getFileAppender(Layout fileLayout) throws IOException {
//        try {
        return new FileAppender(fileLayout, getFileName());
//        } catch (IOException e) {
//            throw new RuntimeException("Exception configuring log system", e);
//        }
    }

    /**
     * 文件大小到达指定尺寸时产生一个新的文件
     *
     * @param fileLayout layout
     */
    private WriterAppender getRollingFileAppender(Layout fileLayout) throws IOException {
//        try {
        RollingFileAppender rollingFileAppender = new RollingFileAppender(fileLayout, getFileName());
        rollingFileAppender.setMaxBackupIndex(getMaxBackupSize());
        rollingFileAppender.setMaximumFileSize(getMaxFileSize());
        return rollingFileAppender;
//        } catch (IOException e) {
//            throw new RuntimeException("Exception configuring log system", e);
//        }
    }

    /**
     * 每一个滚动周期产生一个日志文件
     *
     * @param fileLayout layout
     */
    private WriterAppender getDailyRollingFileAppender(Layout fileLayout) throws IOException {
//        try {
        return new DailyRollingFileAppender(fileLayout, getFileName(), getDatePattern());
//        } catch (IOException e) {
//            throw new RuntimeException("Exception configuring log system", e);
//        }
    }

    /**
     * 日志打印到控制台
     *
     * @param fileLayout layout
     */
    private WriterAppender getConsoleAppender(Layout fileLayout) {
        return new ConsoleAppender(fileLayout, getTarget());
    }

    public Level getRootLevel() {
        return rootLevel;
    }

    public Log4jConfig setRootLevel(Level rootLevel) {
        this.rootLevel = rootLevel;
        return this;
    }

    public String getFilePattern() {
        if (TextUtils.isEmpty(filePattern))
//            return "[%-d{yyyy-MM-dd HH:mm:ss}][Class: %c.%M(%F:%L)] %n[Level: %-5p] - Msg: %m%n";
            return "[%-d{yyyy-MM-dd HH:mm:ss}] %m%n";
        return filePattern;
    }

    public Log4jConfig setFilePattern(String filePattern) {
        this.filePattern = filePattern;
        return this;
    }

    public String getFileName() {
        if (TextUtils.isEmpty(fileName))
            return Environment.getExternalStorageDirectory() + File.separator + "CSSI" + File.separator + getAppName()
                    + File.separator + "Log" + File.separator + getAppName() + ".log";
        return fileName;
    }

    public String getFilePath() {
        return Environment.getExternalStorageDirectory() + File.separator + "CSSI" + File.separator + getAppName()
                + File.separator + "Log";
    }

    public Log4jConfig setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public int getMaxBackupSize() {
        return maxBackupSize;
    }

    public Log4jConfig setMaxBackupSize(int maxBackupSize) {
        this.maxBackupSize = maxBackupSize;
        return this;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public Log4jConfig setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
        return this;
    }

    public boolean isImmediateFlush() {
        return immediateFlush;
    }

    public Log4jConfig setImmediateFlush(boolean immediateFlush) {
        this.immediateFlush = immediateFlush;
        return this;
    }

    public boolean isResetConfiguration() {
        return resetConfiguration;
    }

    public Log4jConfig setResetConfiguration(boolean resetConfiguration) {
        this.resetConfiguration = resetConfiguration;
        return this;
    }

    public boolean isInternalDebugging() {
        return internalDebugging;
    }

    public Log4jConfig setInternalDebugging(boolean internalDebugging) {
        this.internalDebugging = internalDebugging;
        return this;
    }

    public int getLogAppenderType() {
        return logAppenderType;
    }

    public Log4jConfig setLogAppenderType(int logAppenderType) {
        this.logAppenderType = logAppenderType;
        return this;
    }

    public String getTarget() {
        if (TextUtils.isEmpty(target))
            return ConsoleAppender.SYSTEM_OUT;
        return target;
    }

    public Log4jConfig setTarget(String target) {
        this.target = target;
        return this;
    }

    public String getDatePattern() {
        if (TextUtils.isEmpty(datePattern))
            return "'.'yyyy-MM-dd";
        return datePattern;
    }

    public Log4jConfig setDatePattern(String datePattern) {
        this.datePattern = datePattern;
        return this;
    }

    public String getAppName() {
        if (TextUtils.isEmpty(appName))
            return "cssi";
        return appName;
    }

    public Log4jConfig setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    private static Logger getRootLogger() {
        if (null == rootLogger)
            rootLogger = Logger.getRootLogger();
        return rootLogger;
    }

    public static void error(Object msg) {
        if (null != msg)
            getRootLogger().error(msg);
    }

    public static void debug(Object msg) {
        if (null != msg)
            getRootLogger().debug(msg);
    }

    public static void info(Object msg) {
        if (null != msg)
            getRootLogger().info(msg);
    }
}