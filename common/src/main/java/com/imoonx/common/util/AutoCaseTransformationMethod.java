package com.imoonx.common.util;

import android.text.method.ReplacementTransformationMethod;

/**
 * 设置EditText 输入转换
 */
public class AutoCaseTransformationMethod extends ReplacementTransformationMethod {
    /**
     * 获取要改变的字符。
     *
     * @return 将你希望被改变的字符数组返回。
     */
    @Override
    protected char[] getOriginal() {
        return new char[]{'x'};
    }

    /**
     * 获取要替换的字符。
     *
     * @return 将你希望用来替换的字符数组返回。
     */
    @Override
    protected char[] getReplacement() {
        return new char[]{'X'};
    }
}