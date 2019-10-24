package com.imoonx.util;

import android.text.TextUtils;

/**
 * Created by 36238 on 2019/3/28 星期四
 * <p>
 * 商品条码判断 中国采用的商品条码共4种，EAN-13（13位）、EAN-8（8位）、UPC-A（12位）、UPC-E（8位）
 */
public class CodeUtil {

    /**
     * 校验条形码
     *
     * @param code 条形码
     * @return true false
     */
    public static boolean checkCode(String code) {
        if (TextUtils.isEmpty(code))
            return false;
        return checkEAN(code) || checkUPC(code);
    }

    /**
     * 校验条形码UPC-E UPC-A
     *
     * @param code 条形码
     * @return true false
     */
    private static boolean checkUPC(String code) {
        if (code.length() != 12 && code.length() != 8)
            return false;
        //奇数位和
        int oddDigitSum = 0;
        //偶数位的和
        int evenDigitSum = 0;
        //计算奇数位的和
        for (int i = 0; i < code.length() - 1; i += 2) {
            oddDigitSum += (code.charAt(i) - '0');
        }
        //计算偶数位的和
        for (int i = 1; i < code.length() - 1; i += 2) {
            evenDigitSum += (code.charAt(i) - '0');
        }
        //奇数位和的3倍加上偶数位
        int count = oddDigitSum * 3 + evenDigitSum;
        XLog.i(CodeUtil.class, "boolean=" + (count % 10));
        return (10 - count % 10 == (code.charAt(code.length() - 1) - '0')) || (count % 10 == 0);
    }

    /**
     * 校验条形码EAN-13 EAN-8
     *
     * @param code 条形码
     * @return true false
     */
    private static boolean checkEAN(String code) {
        if (code.length() != 13 && code.length() != 8)
            return false;
        //奇数位和
        int oddDigitSum = 0;
        //偶数位的和
        int evenDigitSum = 0;
        //计算奇数位的和
        for (int i = 0; i < code.length() - 1; i += 2) {
            oddDigitSum += (code.charAt(i) - '0');
        }
        //计算偶数位的和
        for (int i = 1; i < code.length() - 1; i += 2) {
            evenDigitSum += (code.charAt(i) - '0');
        }
        //奇数位加上偶数位和的3倍
        int count = oddDigitSum + evenDigitSum * 3;
        return (10 - count % 10 == (code.charAt(code.length() - 1) - '0')) || (count % 10 == 0);
    }
}
