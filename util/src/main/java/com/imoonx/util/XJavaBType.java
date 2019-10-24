package com.imoonx.util;

/**
 * Java 基本类型 包装类转换
 */
public class XJavaBType {

    /**
     * 对象转整数
     *
     * @param object 需要转换的对象
     * @return 转换异常返回 0
     */
    public static int toInt(Object object) {
        if (object == null)
            return 0;
        return toInt(object.toString(), 0);
    }

    /**
     * 字符串转整数
     *
     * @param str      需要转换的字符串
     * @param defValue 默认值
     * @return 转换后的数据 异常返回默认值
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            XLog.e(XUtil.class, e);
        }
        return defValue;
    }

    /**
     * 格式化字符串
     *
     * @param object 需要格式化的对象
     * @return object 为空返回 ""
     */
    public static String toStr(Object object) {
        String s = String.valueOf(object);
        return s == null || s.equals("null") ? "" : s;
    }

    /**
     * 对象转整数
     *
     * @param str 需要转换的字符串
     * @return 转换异常返回 0
     */
    public static long toLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            XLog.e(XUtil.class, e);
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param str 需要转换的字符串
     * @return 转换异常返回 false
     */
    public static boolean toBool(String str) {
        try {
            return Boolean.parseBoolean(str);
        } catch (Exception e) {
            XLog.e(XUtil.class, e);
        }
        return false;
    }
}
