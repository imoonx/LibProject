package com.imoonx.util;

/**
 * 日志打印类
 * <p>
 * 在应用入口可配置是否启用Log
 * <p>
 * isPrint true false
 */
public class XLog {

//    private static Logger logger = Logger.getLogger(XLog.class);

    private static boolean isPrint;

    public static boolean isIsPrint() {
        return isPrint;
    }

    public static void setIsPrint(boolean isPrint) {
        XLog.isPrint = isPrint;
    }

    public static void e(Class<?> cls, String msg) {
        if (isPrint) {
            if (null == msg)
                android.util.Log.e(cls.getSimpleName(), "打印的数据为null");
            else
                android.util.Log.e(cls.getSimpleName(), msg);
        } else
            Log4jConfig.error(cls.getSimpleName() + ":  " + msg);
    }

    public static void e(Class<?> cls, Object msg) {
        if (isPrint) {
            if (null == msg)
                android.util.Log.e(cls.getSimpleName(), "打印的数据为null");
            else
                android.util.Log.e(cls.getSimpleName(), msg.toString());
        } else
            Log4jConfig.error(cls.getSimpleName() + ":  " + msg);
    }

    public static void i(Class<?> cls, String msg) {
        if (isPrint) {
            if (null == msg)
                android.util.Log.i(cls.getSimpleName(), "打印的数据为null");
            else
                android.util.Log.i(cls.getSimpleName(), msg);
        } else
            Log4jConfig.info(cls.getSimpleName() + ":  " + msg);
    }

    public static void i(Class<?> cls, Object msg) {
        if (isPrint) {
            if (null == msg)
                android.util.Log.i(cls.getSimpleName(), "打印的数据为null");
            else
                android.util.Log.i(cls.getSimpleName(), msg.toString());
        } else
            Log4jConfig.info(cls.getSimpleName() + ":  " + msg);
    }

    public static void w(Class<?> cls, String msg) {
        if (isPrint) {
            if (null == msg)
                android.util.Log.w(cls.getSimpleName(), "打印的数据为null");
            else
                android.util.Log.w(cls.getSimpleName(), msg);
        } else
            Log4jConfig.info(cls.getSimpleName() + ":  " + msg);
    }

    public static void w(Class<?> cls, Object msg) {
        if (isPrint) {
            if (null == msg)
                android.util.Log.w(cls.getSimpleName(), "打印的数据为null");
            else
                android.util.Log.w(cls.getSimpleName(), msg.toString());
        } else
            Log4jConfig.info(cls.getSimpleName() + ":  " + msg);
    }

    public static void v(Class<?> cls, String msg) {
        if (isPrint) {
            if (null == msg)
                android.util.Log.v(cls.getSimpleName(), "打印的数据为null");
            else
                android.util.Log.v(cls.getSimpleName(), msg);
        } else
            Log4jConfig.info(cls.getSimpleName() + ":  " + msg);
    }

    public static void v(Class<?> cls, Object msg) {
        if (isPrint) {
            if (null == msg)
                android.util.Log.v(cls.getSimpleName(), "打印的数据为null");
            else
                android.util.Log.v(cls.getSimpleName(), msg.toString());
        } else
            Log4jConfig.info(cls.getSimpleName() + ":  " + msg);
    }

    public static void d(Class<?> cls, String msg) {
        if (isPrint) {
            if (null == msg)
                android.util.Log.d(cls.getSimpleName(), "打印的数据为null");
            else
                android.util.Log.d(cls.getSimpleName(), msg);
        } else
            Log4jConfig.info(cls.getSimpleName() + ":  " + msg);
    }

    public static void d(Class<?> cls, Object msg) {
        if (isPrint) {
            if (null == msg)
                android.util.Log.d(cls.getSimpleName(), "打印的数据为null");
            else
                android.util.Log.d(cls.getSimpleName(), msg.toString());
        } else
            Log4jConfig.info(cls.getSimpleName() + ":  " + msg);
    }
}
