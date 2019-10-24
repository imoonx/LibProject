package com.imoonx.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 */
public class XStringUtils {

    private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static Pattern IMG_URL = Pattern.compile(".*?(gif|jpeg|png|jpg|bmp)");
    private final static Pattern URL = Pattern.compile("^(https|http)://.*?$(net|com|.com.cn|org|me|)");

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email 邮箱地址
     * @return 符合规则true 不符合false
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 判断一个url是否为图片url
     *
     * @param url 须校验地址
     * @return 符合规则true 不符合false
     */
    public static boolean isImgUrl(String url) {
        if (url == null || url.trim().length() == 0)
            return false;
        return IMG_URL.matcher(url).matches();
    }

    /**
     * 判断是否为一个合法的url地址
     *
     * @param url 须校验地址
     * @return 符合规则true 不符合false
     */
    public static boolean isUrl(String url) {
        if (url == null || url.trim().length() == 0)
            return false;
        return URL.matcher(url).matches();
    }

    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is 输入流
     * @return 字符串
     */
    public static String toConvertString(InputStream is) {
        StringBuilder res = new StringBuilder();
        BufferedReader read = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = read.readLine()) != null) {
                res.append(line).append("<br>");
            }
        } catch (IOException e) {
            XLog.e(XStringUtils.class, e);
        } finally {
            XIOUtil.close(read);
        }
        return res.toString();
    }

    /***
     * 截取字符串
     *
     * @param start
     *            从那里开始，0算起
     * @param num
     *            截取多少个
     * @param str
     *            截取的字符串
     * @return 截取后的字符串
     */
    public static String getSubString(int start, int num, String str) {
        if (str == null) {
            return "";
        }
        int length = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > length) {
            start = length;
        }
        if (num < 0) {
            num = 1;
        }
        int end = start + num;
        if (end > length) {
            end = length;
        }
        return str.substring(start, end);
    }

    /**
     * 获取uuid
     *
     * @param isReplace 是否需要替换
     * @return uuid
     */
    public static String getUUID(boolean isReplace) {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return isReplace ? str.replace("-", "") : str;
    }

    /**
     * 半角转全角
     *
     * @param input 半角字符
     * @return 全角字符
     */
    private static String half2Full(String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 全角转换为半角
     *
     * @param string 全角字符
     * @return 半角字符
     */
    private static String full2Half(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        char[] charArray = string.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == 12288) {
                charArray[i] = ' ';
            } else if (charArray[i] >= ' ' && charArray[i] <= 65374) {
                charArray[i] = (char) (charArray[i] - 65248);
            }
        }
        return new String(charArray);
    }

    /**
     * 替换、过滤特殊字符
     *
     * @param str String类型
     * @return String
     */
    public static String StringFilter(String str) {
        try {
            //替换中文标号
            str = str.replaceAll(" ", "").replaceAll(" ", "").replaceAll("：", ":").replaceAll("：", "：").replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!");
            // 清除掉特殊字符
            String regEx = "[『』]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            return m.replaceAll("").trim();
        } catch (Exception e) {
            XLog.e(XStringUtils.class, e);
            return str;
        }
    }


    /**
     * 阿里支付码判断
     *
     * @param pay_id 付款码
     * @return true false
     */
    public static boolean isAliCode(String pay_id) {
        String pattern = "^((2[5-9])|30)[0-9]{14,22}$";
        return Pattern.compile(pattern).matcher(pay_id).matches();
    }

    /**
     * 微信支付码判断
     *
     * @param pay_id 付款码
     * @return true false
     */
    public static boolean isWechat(String pay_id) {
        String pattern = "^1[0-5][0-9]{16}$";
        return Pattern.compile(pattern).matcher(pay_id).matches();
    }

    /**
     * 替换特殊字符
     *
     * @param str 转换前的字符串
     * @return 转换后的字符串
     */
    private String replaceSpecialCharacter(String str) {
//        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
//        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
//        String regEx = "[ _`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“’。，、？-]|\n|\r|\t";
        if (TextUtils.isEmpty(str))
            return "";
        String regEx = "[ _`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
