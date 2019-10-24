package com.imoonx.util;

import java.security.MessageDigest;

/**
 * MD5加密转换
 */
public class XMd5 {

    /**
     * MD5转换
     *
     * @param string 需要转换的String
     * @return 转换后的MD5 可能为null
     */
    public static String getMD5(String string) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(string.getBytes());
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            XLog.e(XMd5.class, e);
            return null;
        }
    }
}
