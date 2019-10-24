package com.imoonx.util;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

/**
 * 设置分段字体颜色
 */
public class XSpannableUtil {

    /**
     * 分段设置颜色字体
     *
     * @param str         需要设置的字符串
     * @param startIndex  开始索引
     * @param endIndex    结束索引
     * @param colorString 颜色值
     * @param textSize    字体值
     * @return SpannableString 字符串为空 返回 null
     */
    public static SpannableString getSpannableString(String str, int startIndex, int endIndex, int colorString, int textSize) {
        if (TextUtils.isEmpty(str))
            return null;
        SpannableString lossStr = new SpannableString(str);
        if (colorString != 0)
            lossStr.setSpan(new ForegroundColorSpan(colorString), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (textSize != 0)
            lossStr.setSpan(new AbsoluteSizeSpan(textSize), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return lossStr;
    }

    /**
     * 分段设置颜色字体
     *
     * @param str         需要设置的字符串
     * @param startIndex  开始索引
     * @param endIndex    结束索引
     * @param colorString 颜色值
     * @param textSize    字体值 #ffc580
     * @return SpannableString 字符串为空 返回 null
     */
    public static SpannableString getSpannableString(String str, int startIndex, int endIndex, String colorString, int textSize) {
        return getSpannableString(str, startIndex, endIndex, Color.parseColor(colorString), textSize);
    }

    /**
     * 分段设置颜色字体
     *
     * @param str         需要设置的字符串
     * @param startIndex  开始索引
     * @param endIndex    结束索引
     * @param colorString 颜色值  #ffc580
     * @return SpannableString 字符串为空 返回 null
     */
    public static SpannableString getSpannableString(String str, int startIndex, int endIndex, String colorString) {
        return getSpannableString(str, startIndex, endIndex, Color.parseColor(colorString));
    }

    /**
     * 分段设置颜色字体
     *
     * @param str         需要设置的字符串
     * @param startIndex  开始索引
     * @param endIndex    结束索引
     * @param colorString 颜色值
     * @return SpannableString 字符串为空 返回 null
     */
    public static SpannableString getSpannableString(String str, int startIndex, int endIndex, int colorString) {
        return getSpannableString(str, startIndex, endIndex, colorString, 0);
    }

    /**
     * 分段设置颜色字体
     *
     * @param str        需要设置的字符串
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @param textSize   字体大小
     * @return SpannableString 字符串为空 返回 null
     */
    public static SpannableString getSpannableString(String str, int startIndex, int endIndex, int... textSize) {
        return getSpannableString(str, startIndex, endIndex, 0, textSize[0]);
    }
}
