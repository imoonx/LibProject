package com.imoonx.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * 所用系统内容工具类
 */
public class XSystemUtil {

    // intent.setType(“image/*”);//选择图片
    // intent.setType(“audio/*”); //选择音频
    // intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
    // intent.setType(“video/*;image/*”);//同时选择视频和图片

    /**
     * 直接拨打电话
     *
     * @param context     上下文
     * @param phoneNumber 手机号
     */
    @SuppressLint("MissingPermission")
    public static void callPhone(Context context, long phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        context.startActivity(dialIntent);
    }

    /**
     * 跳转到拨号界面，同时传递电话号码
     *
     * @param context     上下文
     * @param phoneNumber 手机号
     */
    public static void call(Context context, long phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        context.startActivity(dialIntent);
    }

    /**
     * 获取联系人
     *
     * @param activity    Activity
     * @param requestCode 请求码
     */
    public static void getContacts(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PICK");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android.cursor.dir/phone_v2");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取联系人
     *
     * @param fragment    fragment
     * @param requestCode 请求码
     */
    public static void getContacts(Fragment fragment, int requestCode) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PICK");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android.cursor.dir/phone_v2");
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取文件
     *
     * @param activity    activity
     * @param requestCode 请求码
     */
    public static void getFile(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");// 设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取文件
     *
     * @param fragment    fragment
     * @param requestCode 请求码
     */
    public static void getFile(Fragment fragment, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");// 设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 发送邮件
     *
     * @param context 上下文
     * @param subject 主题
     * @param content 内容
     * @param emails  邮件地址
     */
    public static void sendEmail(Context context, String subject, String content, String... emails) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            // 模拟器
            // intent.setType("text/plain");
            intent.setType("message/rfc822"); // 真机
            intent.putExtra(Intent.EXTRA_EMAIL, emails);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, content);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            XLog.e(XSystemUtil.class, e);
        }
    }

    /**
     * 判断服务是否运行
     *
     * @param context 上下文
     * @param clazz   class
     * @param <T>     泛型
     * @return true false
     */
    @SuppressWarnings("deprecation")
    public static <T> boolean isWorked(Context context, Class<T> clazz) {
        if (null == context)
            return false;
        try {
            ActivityManager myManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>)
                    myManager.getRunningServices(30);
            for (int i = 0; i < runningService.size(); i++) {
                if (runningService.get(i).service.getClassName().equals(clazz.getName())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            XLog.e(XSystemUtil.class, e);
            return false;
        }
    }

    /**
     * 开启服务
     *
     * @param context 上下文
     * @param <T>     泛型
     * @param clazz   class
     */
    public static <T> void startSevice(Context context, Class<T> clazz) {
        if (null == context)
            return;
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                Intent intent = new Intent(context, clazz);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startForegroundService(new Intent(context, clazz));
            } else {
                Intent intent = new Intent(context, clazz);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(intent);
            }
        } catch (Exception e) {
            XLog.e(XSystemUtil.class, e);
        }
    }
}
