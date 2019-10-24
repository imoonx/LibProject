package com.imoonx.util;

/**
 * 常用正则
 */
public class XRegularUtil {
    /**
     * 字母正则
     */
    public static String RES_A_Z = "^[A-Za-z]+$";
    /**
     * 数字正则
     */
    public static String NUM = "^\\d+$";

    /**
     * 数字
     */
    public static String NUM_RES = "[0-9]\\d*\\.?\\d*";

    /**
     * 正则验证手机号
     */
    public static final String REG_PHONE = "^(13|14|15|16|17|18|19)\\d{9}$";
    /**
     * 正则验证密码排除中文 6-16位
     */
    public static final String REG_PWD = "^([^\\u4e00-\\u9fa5]){6,16}$";

}
