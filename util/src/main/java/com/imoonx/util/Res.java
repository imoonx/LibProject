package com.imoonx.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 资源获取类
 */
public class Res {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context getContext() {
        if (null == context)
            return BaseApplication.context();
        return context;
    }

    public static void setContext(Context context) {
        Res.context = context;
    }

    /**
     * px转换dip
     *
     * @param px 像素值
     * @return dp
     */
    public static int px2dip(int px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * dip转换px
     *
     * @param dip dp
     * @return 像素值
     */
    public static int dip2px(int dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * dip转换px
     *
     * @param dip dp
     * @return 像素值
     */
    public static int dip2px(float dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * 布局填充
     *
     * @param resId 布局id
     * @return view
     */
    public static View inflate(int resId) {
        return LayoutInflater.from(getContext()).inflate(resId, null);
    }

    /**
     * 获取文字
     *
     * @param resId 资源id
     * @return 文字
     */
    public static String getString(int resId) {
        return getContext().getResources().getString(resId);
    }

    /**
     * 获取文字数组
     *
     * @param resId 资源id
     * @return 文字
     */
    public static String[] getStringArray(int resId) {
        return getContext().getResources().getStringArray(resId);
    }

    /**
     * dimen
     *
     * @param resId 资源id
     * @return dimen值
     */
    public static int getDimens(int resId) {
        return getContext().getResources().getDimensionPixelSize(resId);
    }

    /**
     * 获取drawable
     *
     * @param resId 资源id
     * @return drawable
     */
    public static Drawable getDrawable(int resId) {
        return getContext().getResources().getDrawable(resId);
    }

    /**
     * 获取颜色
     *
     * @param resId 资源id
     * @return 颜色值
     */
    public static int getColor(int resId) {
        return getContext().getResources().getColor(resId);
    }

    /**
     * 获取布局ID
     *
     * @param layoutName 布局名称
     * @return id
     */
    public static int getLayoutID(String layoutName) {
        return getContext().getResources().getIdentifier(layoutName, "layout", getContext().getPackageName());
    }

    /**
     * 获取widget id
     *
     * @param widgetName 名称
     * @return id
     */
    public static int getWidgetID(String widgetName) {
        return getContext().getResources().getIdentifier(widgetName, "id", getContext().getPackageName());
    }

    /**
     * 获取动画ID
     *
     * @param animName 动画名称
     * @return id
     */
    public static int getAnimID(String animName) {
        return getContext().getResources().getIdentifier(animName, "anim", getContext().getPackageName());
    }

    /**
     * 获取xml ID
     *
     * @param xmlName 名称
     * @return id
     */
    public static int getXmlID(String xmlName) {
        return getContext().getResources().getIdentifier(xmlName, "xml", getContext().getPackageName());
    }

    /**
     * 根据xml id获取资源
     *
     * @param xmlName 名称
     * @return 资源
     */
    public static XmlResourceParser getXml(String xmlName) {
        int xmlId = getXmlID(xmlName);
        return getContext().getResources().getXml(xmlId);
    }

    /**
     * 获取动画id
     *
     * @param rawName 名称
     * @return id
     */
    public static int getRawID(String rawName) {
        return getContext().getResources().getIdentifier(rawName, "raw", getContext().getPackageName());
    }

    /**
     * 获取Drawable ID
     *
     * @param drawName 名称
     * @return id
     */
    public static int getDrawableID(String drawName) {
        return getContext().getResources().getIdentifier(drawName, "drawable", getContext().getPackageName());
    }

    /**
     * 根据id 获取资源
     *
     * @param drawName 名称
     * @return drawable
     */
    public static Drawable getDrawable(String drawName) {
        int drawId = getDrawableID(drawName);
        return getContext().getResources().getDrawable(drawId);
    }

    /**
     * 获取自定义属性id
     *
     * @param attrName 属性名称
     * @return 属性id
     */
    public static int getAttrID(String attrName) {
        return getContext().getResources().getIdentifier(attrName, "styleable", getContext().getPackageName());
    }

    /**
     * 获取attr 数组
     *
     * @param attrsName 属性名称
     * @return 属性数组
     */
    public static int[] getAttrs(String attrsName) {
        return getContext().getResources().getIntArray(getAttrID(attrsName));
    }

    /**
     * 获取Dimen ID
     *
     * @param dimenName dimen名称
     * @return dimen值
     */
    public static int getDimenID(String dimenName) {
        return getContext().getResources().getIdentifier(dimenName, "dimen", getContext().getPackageName());
    }

    /**
     * 通过Dimen ID获取值
     *
     * @param dimenName dimen名称
     * @return dimen值
     */
    public static float getDimen(String dimenName) {
        return getContext().getResources().getDimension(getDimenID(dimenName));
    }

    /**
     * 获取 Color ID
     *
     * @param colorName 颜色名称
     * @return 颜色值
     */
    public static int getColorID(String colorName) {
        return getContext().getResources().getIdentifier(colorName, "color", getContext().getPackageName());
    }

    /**
     * 通过 id 获取color资源
     *
     * @param colorName 颜色名称
     * @return 颜色值
     */
    @SuppressWarnings("deprecation")
    public static int getColor(String colorName) {
        return getContext().getResources().getColor(getColorID(colorName));
    }

    /**
     * 获取style id
     *
     * @param styleName style名称
     * @return style值
     */
    public static int getStyleID(String styleName) {
        return getContext().getResources().getIdentifier(styleName, "style", getContext().getPackageName());
    }

    /**
     * 获取 String id
     *
     * @param strName 字符串名称
     * @return 字符串id
     */
    public static int getStringID(String strName) {
        return getContext().getResources().getIdentifier(strName, "string", getContext().getPackageName());
    }

    /**
     * 通过String id获取资源
     *
     * @param strName 字符串名称
     * @return 资源id
     */
    public static String getString(String strName) {
        int strId = getStringID(strName);
        return getContext().getResources().getString(strId);
    }

    /**
     * 获取文字数组
     *
     * @param arrName 属性名称
     * @return 数组
     */
    public static String[] getStringArray(String arrName) {
        return getContext().getResources().getStringArray(getStringID(arrName));
    }

    /**
     * 获取数组
     *
     * @param strName 字符串名称
     * @return 数组
     */
    public static int[] getInteger(String strName) {
        return getContext().getResources().getIntArray(getContext().getResources().getIdentifier(strName, "array",
                getContext().getPackageName()));
    }

    /**
     * 屏幕密度
     *
     * @return （像素比例：0.75/1.0/1.5/2.0）
     */
    public static float getDensity() {
        DisplayMetrics displayMetrics = getDisplayMetrics();
        return displayMetrics.density;
    }

    /**
     * 屏幕密度
     *
     * @return （每寸像素：120/160/240/320）
     */
    public static float getDensityDpi() {
        DisplayMetrics displayMetrics = getDisplayMetrics();
        return (float) displayMetrics.densityDpi;
    }

    /**
     * 获取DisplayMetrics
     *
     * @return DisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics() {
        return getContext().getResources().getDisplayMetrics();
    }

    /**
     * 获得屏幕的高度
     *
     * @return height
     */
    public static float getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

    /**
     * 获得屏幕的宽度
     *
     * @return width
     */
    public static float getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    /**
     * 通过屏幕的宽度获取高度
     *
     * @param value 宽高比
     * @return 高度
     */
    public static float getHeightByWidth(float value) {
        return getScreenWidth() / value;
    }
}