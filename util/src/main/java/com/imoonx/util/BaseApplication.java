package com.imoonx.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 应用入口类 初始化参数
 */

public class BaseApplication extends Application {

    private static String prefName = "config.pref";

    @SuppressLint("StaticFieldLeak")
    static Context _context;

    public static String getPrefName() {
        return prefName;
    }

    public static void setPrefName(String prefName) {
        BaseApplication.prefName = prefName;
    }

    public void onCreate() {
        super.onCreate();
        _context = getApplicationContext();
        Res.setContext(_context);
        TDevice.setContext(_context);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        fixAssetManagerTimeoutException();
    }

    public static synchronized BaseApplication context() {
        return (BaseApplication) _context;
    }

    public static void apply(SharedPreferences.Editor editor) {
        editor.apply();
    }

    public static void setFloat(String key, float value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putFloat(key, value);
        apply(editor);
    }

    public static void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        apply(editor);
    }

    public static void setString(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        apply(editor);
    }

    public static void setInt(String key, int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        apply(editor);
    }

    public static void setLong(String key, long value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putLong(key, value);
        apply(editor);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static String getString(String key, String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static int getInt(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    public static float getloat(String key, float defValue) {
        return getPreferences().getFloat(key, defValue);
    }

    public static SharedPreferences getPreferences() {
        return context().getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    /**
     * 解决oppo TimeoutExceptions
     */
    @SuppressWarnings("unchecked")
    public void fixAssetManagerTimeoutException() {
        try {
            Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
            Method method = clazz.getSuperclass().getDeclaredMethod("stop");
            method.setAccessible(true);
            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            method.invoke(field.get(null));
        } catch (Exception e) {
            XLog.e(this.getClass(), e.toString());
        }
    }
}
