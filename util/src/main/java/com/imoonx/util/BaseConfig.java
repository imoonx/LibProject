package com.imoonx.util;

import android.os.Environment;

import java.io.File;

/**
 * 常量配置类
 * <p>
 * Created by 36238 on 2019/5/28 星期二
 */
public class BaseConfig {

    /**
     * 透明度0.5
     */
    public static final float ALPHA_5 = 0.5F;
    /**
     * 透明度0.7
     */
    public static final float ALPHA_7 = 0.7F;
    /**
     * 透明度1.0
     */
    public static final float ALPHA_10 = 1.0F;

    /**
     * 基本路径
     */
    public static String BASE_PATH = Environment.getExternalStorageDirectory() + File.separator + "CSSI" + File.separator;
    /**
     * 请求成功码
     */
    public static int REQUEST_SUCCESS_CODE = 0;
    /**
     * uid
     */
    public static final String UID = "uid";
    /**
     * token
     */
    public static final String TOKEN = "token";
    /**
     * user_type
     */
    public static final String USER_TYPE = "user_type";
    /**
     * user_name
     */
    public static final String USER_NAME = "user_name";
    /**
     * password
     */
    public static final String PASSWORD = "password";
    /**
     * 配置文件名称
     */
    public static String CONFIG_NAME = "config";
    /**
     * 跳转携带bundle
     */
    public static String ACTIVITY_BUNDLE = "activity_bundle";
    /**
     * fragment 类型
     */
    public static String ACTIVITY_FRAGMNET_TYPE = "activity_fragmnet_type";
}
