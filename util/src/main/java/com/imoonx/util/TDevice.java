package com.imoonx.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.List;
import java.util.UUID;

/**
 * 工具类
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TDevice {

    // 手机网络类型
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static boolean GTE_HC;
    public static boolean GTE_ICS;
    public static boolean PRE_HC;

    private static Boolean _hasCamera = null;
    private static Boolean _isTablet = null;
    private static Integer _loadFactor = null;

    public static float displayDensity = 0.0F;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context context() {
        return context;
    }

    public static void setContext(Context context) {
        TDevice.context = context;
    }

    static {
        GTE_ICS = true;
        GTE_HC = false;
        PRE_HC = false;
    }

    /**
     * 获取默认的加载因子
     *
     * @return 加载因子
     */
    public static int getDefaultLoadFactor() {
        if (_loadFactor == null) {
            Integer integer = 0xf & context().getResources().getConfiguration().screenLayout;
            _loadFactor = integer;
            _loadFactor = Math.max(integer, 1);
        }
        return _loadFactor;
    }

    /**
     * 获取屏幕的真是宽高
     *
     * @param activity 上下文
     * @return 宽高
     */
    @SuppressLint("ObsoleteSdkInt")
    public static int[] getRealScreenSize(Activity activity) {
        int[] size = new int[2];
        int screenWidth = 0, screenHeight = 0;
        WindowManager w = activity.getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                screenWidth = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                screenHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception e) {
                XLog.e(TDevice.class, e);
            }
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                screenWidth = realSize.x;
                screenHeight = realSize.y;
            } catch (Exception e) {
                XLog.e(TDevice.class, e);
            }
        size[0] = screenWidth;
        size[1] = screenHeight;
        return size;
    }

    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    @SuppressLint("PrivateApi")
    public static int getStatusBarHeight() {
        Class<?> c;
        Object obj;
        Field field;
        int x;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return context().getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            XLog.e(TDevice.class, e);
        }
        return 0;
    }

    /**
     * 获取uuid
     *
     * @param isReplace 是否需要替换“-”
     * @return uuid
     */
    public static String getUuid(boolean isReplace) {
        String uuid = String.format("%s", UUID.randomUUID());
        return isReplace ? uuid.replace("-", "") : uuid;
    }

    /**
     * 判断是否有相机
     *
     * @return true false
     */
    public static boolean hasCamera() {
        if (_hasCamera == null) {
            PackageManager pckMgr = context().getPackageManager();
            boolean flag = pckMgr.hasSystemFeature("android.hardware.camera.front");
            boolean flag1 = pckMgr.hasSystemFeature("android.hardware.camera");
            boolean flag2;
            flag2 = flag || flag1;
            _hasCamera = flag2;
        }
        return _hasCamera;
    }

    /**
     * 判断是否有物理菜单键
     *
     * @param context 上下文
     * @return true false
     */
    public static boolean hasHardwareMenuKey(Context context) {
        return PRE_HC || GTE_ICS && ViewConfiguration.get(context).hasPermanentMenuKey();
    }

    /**
     * 判断是否有网络
     *
     * @return true false
     */
    @SuppressLint("MissingPermission")
    public static boolean hasInternet() {
        return ((ConnectivityManager) context().getSystemService("connectivity")).getActiveNetworkInfo() != null;
    }

    /***
     *
     * 判断键盘是否显示
     * @param rootView view
     * @return true false
     */
    public static boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    /**
     * 判断包是否存在
     *
     * @param pckName 包名
     * @return true false
     */
    public static boolean isPackageExist(String pckName) {
        try {
            PackageInfo pckInfo = context().getPackageManager().getPackageInfo(pckName, 0);
            if (pckInfo != null)
                return true;
        } catch (NameNotFoundException e) {
            XLog.e(TDevice.class, e);
        }
        return false;
    }

    /**
     * 隐藏键盘
     *
     * @param view view
     */
    public static void hideSoftKeyboard(View view) {
        if (view == null)
            return;
        InputMethodManager systemService = (InputMethodManager) context().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null == systemService)
            return;
        systemService.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * dialog显示键盘
     *
     * @param dialog dialog
     */
    public static void showSoftKeyboard(Dialog dialog) {
        if (null == dialog)
            return;
        if (null == dialog.getWindow())
            return;
        dialog.getWindow().setSoftInputMode(4);
    }

    /**
     * 调用键盘
     *
     * @param view 当前view
     */
    public static void showSoftKeyboard(View view) {
        InputMethodManager systemService = (InputMethodManager) context().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null == systemService)
            return;
        systemService.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏键盘
     *
     * @param view 当前view
     * @return boolean 是否显示
     */
    public static boolean softboardIsShow(View view) {
        InputMethodManager imm = (InputMethodManager) context().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null == imm)
            return false;
        if (imm.hideSoftInputFromWindow(view.getWindowToken(), 0)) {
            imm.showSoftInput(view, 0);
            return true;
            // 软键盘已弹出
        } else {
            return false;
            // 软键盘未弹出
        }
    }

    /**
     * 拖动键盘
     */
    public static void toogleSoftKeyboard() {
        InputMethodManager systemService = (InputMethodManager) context().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null == systemService)
            return;
        systemService.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 判断是否是横屏
     *
     * @return true false
     */
    public static boolean isLandscape() {
        return context().getResources().getConfiguration().orientation == 2;
    }

    /**
     * 判断是否是竖屏
     *
     * @return true false
     */
    public static boolean isPortrait() {
        return context().getResources().getConfiguration().orientation != 1;
    }

    /**
     * 判断是否是平板
     *
     * @return true false
     */
    public static boolean isTablet() {
        if (_isTablet == null) {
            boolean flag;
            flag = (0xf & context().getResources().getConfiguration().screenLayout) >= 3;
            _isTablet = flag;
        }
        return _isTablet;
    }

    /**
     * 内存卡是否挂在完成
     *
     * @return true false
     */
    public static boolean isSdcardReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取当前语言
     *
     * @return String
     */
    public static String getCurCountryLan() {
        return context().getResources().getConfiguration().locale.getLanguage() + "-" + context().getResources().getConfiguration().locale.getCountry();
    }

    /**
     * 当前地区是否是中国
     *
     * @return boolean
     */
    public static boolean isZhCN() {
        String lang = context().getResources().getConfiguration().locale.getCountry();
        return lang.equalsIgnoreCase("CN");
    }

    public static String percent(double p1, double p2) {
        String str;
        double p3 = p1 / p2;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        str = nf.format(p3);
        return str;
    }

    public static String percent2(double p1, double p2) {
        String str;
        double p3 = p1 / p2;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(0);
        str = nf.format(p3);
        return str;
    }

    /**
     * 设置全屏
     *
     * @param activity 上下文
     */
    public static void setFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(params);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 取消全屏
     *
     * @param activity 上下文
     */
    public static void cancelFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(params);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 获取包信息
     *
     * @param pckName 包名
     * @return 应用信息
     */
    public static PackageInfo getPackageInfo(String pckName) {
        try {
            return context().getPackageManager().getPackageInfo(pckName, 0);
        } catch (NameNotFoundException e) {
            XLog.e(TDevice.class, e);
        }
        return null;
    }

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    public static int getVersionCode() {
        return getVersionCode(getPackageName());
    }

    /**
     * 获取版本号
     *
     * @param packageName 包名
     * @return 版本号
     */
    public static int getVersionCode(String packageName) {
        int versionCode;
        try {
            versionCode = context().getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (NameNotFoundException ex) {
            XLog.e(TDevice.class, ex);
            versionCode = 0;
        }
        return versionCode;
    }

    /**
     * 获取版本名称
     *
     * @return 版本名称
     */
    public static String getVersionName() {
        return getVersionName(getPackageName());
    }

    /**
     * 获取版本名称
     *
     * @param packageName 包名
     * @return 版本名称
     */
    public static String getVersionName(String packageName) {
        return null == getPackageInfo(packageName) ? "" : getPackageInfo(packageName).versionName;
    }

    /**
     * 屏幕是否常亮
     *
     * @return true false
     */
    public static boolean isScreenOn() {
        PowerManager pm = (PowerManager) context().getSystemService(Context.POWER_SERVICE);
        return null != pm && pm.isScreenOn();
    }

    /**
     * 获取imei
     *
     * @return imei
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI() {
        TelephonyManager tel = (TelephonyManager) context().getSystemService(Context.TELEPHONY_SERVICE);
        return null != tel ? tel.getDeviceId() : "";
    }

    /**
     * 获取手机的类型
     *
     * @return type
     */
    public static String getPhoneType() {
        return Build.MODEL;
    }

    /**
     * 打开应用
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void openApp(Context context, String packageName) {
        Intent mainIntent = context().getPackageManager().getLaunchIntentForPackage(packageName);
        if (mainIntent == null)
            mainIntent = new Intent(packageName);
        context.startActivity(mainIntent);
    }

    /**
     * wifi 是否开启
     *
     * @return true false
     */
    public static boolean isWifiOpen() {
        boolean isWifiConnect = false;
        ConnectivityManager cm = (ConnectivityManager) BaseApplication.context().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cm)
            return false;
        // check the networkInfos numbers
        @SuppressLint("MissingPermission") NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isWifiConnect = false;
                }
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    isWifiConnect = true;
                }
            }
        }
        return isWifiConnect;
    }

    /**
     * 卸载应用
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void uninstallApk(Context context, String packageName) {
        if (isPackageExist(packageName)) {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            context.startActivity(uninstallIntent);
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    @SuppressLint("PrivateApi")
    public static int getStatuBarHeight() {
        Class<?> c;
        Object obj;
        Field field;
        int x, sbar = 38;// 默认为38，貌似大部分是这样的
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context().getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            XLog.e(TDevice.class, e);
        }
        return sbar;
    }

    /**
     * 获取ActionBar高度
     *
     * @param context 上下文
     * @return ActionBar高度
     */
    public static int getActionBarHeight(Context context) {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());

        if (actionBarHeight == 0 && context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    /**
     * 判断是否有状态栏
     *
     * @param activity 上下文
     * @return true false
     */
    public static boolean hasStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        return (attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public static int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication.context().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connectivityManager)
            return netType;
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /**
     * 判断应用是否已经启动
     *
     * @param context     上下文
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (null == activityManager)
            return false;
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++) {
            if (processInfos.get(i).processName.equals(packageName)) {
                XLog.i(TDevice.class, String.format("the %s is running, isAppAlive return true", packageName));
                return true;
            }
        }
        XLog.i(TDevice.class, String.format("the %s is not running, isAppAlive return false", packageName));
        return false;
    }

    /**
     * 获取是否存在NavigationBar
     *
     * @param context 上下文
     * @return true false
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            @SuppressLint("PrivateApi") Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            XLog.e(TDevice.class, e);
        }
        return hasNavigationBar;
    }

    /**
     * 获取根目录
     *
     * @param context 上下文
     * @return File 根目录
     */
    public static File getStoreDir(Context context) {
        File dataDir;
        if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState())) {
            dataDir = Environment.getExternalStorageDirectory();
        } else {
            dataDir = context.getApplicationContext().getFilesDir();
        }
        return dataDir;
    }

    public static Point displaySize = new Point();
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static final Handler uiHandler = new Handler(Looper.getMainLooper());

    /**
     * 检查显示类型
     *
     * @param context 上下文
     */
    public static void checkDisplaySize(Context context) {
        try {
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                }
            }
        } catch (Exception e) {
            XLog.e(TDevice.class, e);
        }
        if (displayMetrics != null && displayMetrics.heightPixels < displayMetrics.widthPixels) {
            final int tmp = displayMetrics.heightPixels;
            displayMetrics.heightPixels = displayMetrics.widthPixels;
            displayMetrics.widthPixels = tmp;
        }
        if (displaySize != null && displaySize.y < displaySize.x) {
            final int tmp = displaySize.y;
            displaySize.y = displaySize.x;
            displaySize.x = tmp;
        }
    }

    /**
     * 获取SDK 版本
     *
     * @return sdk 版本
     */
    public static int getSdkVersionInt() {
        try {
            return Build.VERSION.SDK_INT;
        } catch (Exception e) {
            XLog.e(TDevice.class, e);
            return 0;
        }
    }

    /**
     * 运行ui线程
     *
     * @param runnable 线程
     */
    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    /**
     * 运行ui线程
     *
     * @param runnable 线程
     * @param delay    延迟时间
     */
    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            uiHandler.post(runnable);
        } else {
            uiHandler.postDelayed(runnable, delay);
        }
    }

    /**
     * 取消任务
     *
     * @param runnable 线程
     */
    public static void cancelTask(Runnable runnable) {
        if (runnable != null) {
            uiHandler.removeCallbacks(runnable);
        }
    }

    /**
     * 获取导航栏高度
     *
     * @param context 上下文
     * @return 导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            return resources.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    /**
     * 获取包名
     *
     * @return 包名
     */
    public static String getPackageName() {
        return BaseApplication.context().getPackageName();
    }

    /**
     * 是否运行后台
     *
     * @param context 上下文
     * @return true false
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    XLog.i(TDevice.class, "程序在后台");
                    return true;
                } else {
                    XLog.i(TDevice.class, "程序在前台");
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 是否在运行
     *
     * @param context 上下文
     * @return true false
     */
    public static boolean isAppForeground(Context context) {
        boolean isForground = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (null == am)
            return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String pkgName : processInfo.pkgList) {
                        if (pkgName.equals(context.getPackageName())) {
                            isForground = true;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isForground = true;
            }
        }
        return isForground;
    }

    /**
     * 判断是否是快速点击
     */
    private static long lastClickTime;

    /**
     * 判断是否重复点击
     *
     * @return true false
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            XLog.i(TDevice.class, "重复点击");
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 判断当前线程是否是主线程
     *
     * @return true false
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * 在前台
     */
    public static final int APP_STATUS_STARTUP_FOREGROUND = 1;
    /**
     * 在后台
     */
    public static final int APP_STATUS_STARTUP_BACKGROUND = 2;
    /**
     * 未启动
     */
    public static final int APP_STATUS_UN_STARTUP = 3;

    /**
     * 判断应用程序当前状态
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {this.APP_STATUS_STARTUP_FOREGROUND,this.APP_STATUS_STARTUP_BACKGROUND,this.APP_STATUS_UN_STARTUP}
     */
    public static int appIsAlive(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (null == activityManager)
            return APP_STATUS_UN_STARTUP;
        List<ActivityManager.RunningTaskInfo> listInfos = activityManager.getRunningTasks(20);
        // 判断程序是否在栈顶
        if (listInfos.get(0).topActivity.getPackageName().equals(packageName)) {
            return APP_STATUS_STARTUP_FOREGROUND;
        } else {
            // 判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : listInfos) {
                if (info.topActivity.getPackageName().equals(packageName)) {
                    return APP_STATUS_STARTUP_BACKGROUND;
                }
            }
            return APP_STATUS_UN_STARTUP;// 栈里找不到，返回3
        }
    }

    /**
     * 设置背景透明度
     *
     * @param activity 当前activity
     * @param alpha    0-1
     */
    protected void setBackageAlpha(Activity activity, float alpha) {
        try {
            if (null == activity || activity.isFinishing())
                return;
            Window window = activity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.alpha = 0.1f;
            window.setAttributes(attributes);
            XLog.i(this.getClass(), "setBackageAlpha " + alpha);
        } catch (Exception e) {
            XLog.e(this.getClass(), e);
        }
    }
}
