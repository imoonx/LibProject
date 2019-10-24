package com.imoonx.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 格式化日期
 */
public class XFormatData {

    private static final Pattern UniversalDatePattern = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})[\\s]+([0-9]{2}):([0-9]{2}):([0-9]{2})");

    /**
     * 年-天
     */
    public static final int TYPE_YY_DD = 0;
    /**
     * 月-天
     */
    public static final int TYPE_MM_DD = 1;
    /**
     * 时-分
     */
    public static final int TYPE_HH_MM = 2;
    /**
     * 分-秒
     */
    public static final int TYPE_MM_SS = 3;
    /**
     * 秒
     */
    public static final int TYPE_SS = 4;
    /**
     * 月-分
     */
    public static final int TYPE_MM_MM = 5;
    /**
     * 年-月
     */
    public static final int TYPE_YY_MM = 6;
    /**
     * 年-秒
     */
    public static final int TYPE_ALL = 7;
    /**
     * 时-秒
     */
    public static final int TYPE_HH_SS = 8;
    /**
     * 月-秒
     */
    public static final int TYPE_MOM_SS = 9;

    /**
     * 转化年月日
     *
     * @param dateTime long型日期
     * @param type     类型
     * @return 指定格式的日期 异常返回""
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatDate(long dateTime, int type) {
        try {
            SimpleDateFormat sDateFormat = null;
            switch (type) {
                case TYPE_SS:
                    sDateFormat = new SimpleDateFormat("ss");
                    break;
                case TYPE_MM_SS:
                    sDateFormat = new SimpleDateFormat("mm:ss");
                    break;
                case TYPE_HH_MM:
                    sDateFormat = new SimpleDateFormat("HH:mm");
                    break;
                case TYPE_HH_SS:
                    sDateFormat = new SimpleDateFormat("HH:mm:ss");
                    break;
                case TYPE_MM_DD:
                    sDateFormat = new SimpleDateFormat("MM-dd");
                    break;
                case TYPE_MM_MM:
                    sDateFormat = new SimpleDateFormat("MM-dd HH:mm");
                    break;
                case TYPE_MOM_SS:
                    sDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
                    break;
                case TYPE_YY_DD:
                    sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    break;
                case TYPE_YY_MM:
                    sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    break;
                case TYPE_ALL:
                    sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    break;
            }
            return null == sDateFormat ? "" : sDateFormat.format(new Date(dateTime));
        } catch (Exception e) {
            XLog.e(XFormatData.class, e);
            return "";
        }
    }

    /**
     * 当前日期加减
     *
     * @param dayAddNum 需要添加或减少的天数
     * @param type      类型
     * @return 指定格式的日期 异常返回""
     */
    @SuppressLint("SimpleDateFormat")
    public static String getAddDate(int dayAddNum, int type) {
        long date = System.currentTimeMillis() + dayAddNum * 24 * 60 * 60 * 1000;
        return formatDate(date, type);
    }

    /**
     * 格式化数据 保留三位小数
     *
     * @param date       需要转换的double 数据
     * @param formatType 格式类型 如"0.000"
     * @return 格式化后的数据 异常返回""
     */
    public static String formatDouble(double date, String formatType) {
        try {
            return new DecimalFormat(formatType).format(date);
        } catch (Exception e) {
            XLog.e(XFormatData.class, e.toString());
            return "";
        }
    }

    /**
     * 日期友好转换
     *
     * @param date YYYY-MM-DD HH:mm:ss
     * @return n分钟前, n小时前, 昨天, 前天, n天前, n个月前 异常返回""
     */
    public static String formatSomeAgo(long date) {
        try {
            String sdate = getDateString(date);

            if (sdate == null)
                return "";
            Calendar calendar = parseCalendar(sdate);
            if (calendar == null)
                return sdate;

            Calendar mCurrentDate = Calendar.getInstance();
            long crim = mCurrentDate.getTimeInMillis(); // current
            long trim = calendar.getTimeInMillis(); // target
            long diff = crim - trim;

            int year = mCurrentDate.get(Calendar.YEAR);
            int month = mCurrentDate.get(Calendar.MONTH);
            int day = mCurrentDate.get(Calendar.DATE);

            if (diff < 60 * 1000) {
                return "刚刚";
            }
            if (diff < AlarmManager.INTERVAL_HOUR) {
                return String.format("%s分钟前", diff / 60 / 1000);
            }
            mCurrentDate.set(year, month, day, 0, 0, 0);
            if (trim >= mCurrentDate.getTimeInMillis()) {
                return String.format("%s小时前", diff / AlarmManager.INTERVAL_HOUR);
            }
            mCurrentDate.set(year, month, day - 1, 0, 0, 0);
            if (trim >= mCurrentDate.getTimeInMillis()) {
                return "昨天";
            }
            mCurrentDate.set(year, month, day - 2, 0, 0, 0);
            if (trim >= mCurrentDate.getTimeInMillis()) {
                return "前天";
            }
            if (diff < AlarmManager.INTERVAL_DAY * 30) {
                return String.format("%s天前", diff / AlarmManager.INTERVAL_DAY);
            }
            if (diff < AlarmManager.INTERVAL_DAY * 30 * 12) {
                return String.format("%s月前", diff / (AlarmManager.INTERVAL_DAY * 30));
            }
            return String.format("%s年前", mCurrentDate.get(Calendar.YEAR) - calendar.get(Calendar.YEAR));
        } catch (Exception e) {
            XLog.e(XFormatData.class, e);
            return "";
        }
    }


    private final static ThreadLocal<SimpleDateFormat> SS = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("ss", Locale.getDefault());
        }
    };

    private final static ThreadLocal<SimpleDateFormat> MMSS = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("mm:ss", Locale.getDefault());
        }
    };

    private final static ThreadLocal<SimpleDateFormat> HHMM = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm", Locale.getDefault());
        }
    };

    private final static ThreadLocal<SimpleDateFormat> HHMMSS = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        }
    };

    private final static ThreadLocal<SimpleDateFormat> YYYYMMDD = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }
    };

    private final static ThreadLocal<SimpleDateFormat> YYYYMMDDHHMM = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        }
    };

    private final static ThreadLocal<SimpleDateFormat> YYYYMMDDHHMMSS = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
    };


    /**
     * 格式化日期
     *
     * @param date 需要格式化的时间戳
     * @return yyyy-MM-dd HH:mm:ss
     */

    public static String getDateString(long date) {
        return YYYYMMDDHHMMSS.get().format(date);
    }


    /**
     * 对日期字符串 进行友好格式化
     *
     * @param str YYYY-MM-DD HH:mm:ss string
     * @return 今天, 昨天, 前天, n天前
     */
    public static String formatSomeDay(String str) {
        return formatSomeDay(parseCalendar(str));
    }

    /**
     * 日期格式化
     *
     * @param calendar 需要格式的日期 {@link Calendar}
     * @return 今天, 昨天, 前天, n天前
     */
    public static String formatSomeDay(Calendar calendar) {
        if (calendar == null)
            return "?天前";
        Calendar mCurrentDate = Calendar.getInstance();
        long crim = mCurrentDate.getTimeInMillis(); // current
        long trim = calendar.getTimeInMillis(); // target
        long diff = crim - trim;

        int year = mCurrentDate.get(Calendar.YEAR);
        int month = mCurrentDate.get(Calendar.MONTH);
        int day = mCurrentDate.get(Calendar.DATE);

        mCurrentDate.set(year, month, day, 0, 0, 0);
        if (trim >= mCurrentDate.getTimeInMillis()) {
            return "今天";
        }
        mCurrentDate.set(year, month, day - 1, 0, 0, 0);
        if (trim >= mCurrentDate.getTimeInMillis()) {
            return "昨天";
        }
        mCurrentDate.set(year, month, day - 2, 0, 0, 0);
        if (trim >= mCurrentDate.getTimeInMillis()) {
            return "前天";
        }
        return String.format("%s天前", diff / AlarmManager.INTERVAL_DAY);
    }

    /**
     * 格式化星期
     *
     * @param calendar 需要格式化的日期 {@link Calendar}
     * @return 星期n
     */
    public static String formatWeek(Calendar calendar) {
        if (calendar == null)
            return "星期?";
        return new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"}[calendar
                .get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * 格式化星期
     *
     * @param str YYYY-MM-DD HH:mm:ss string
     * @return 星期n
     */
    public static String formatWeek(String str) {
        return formatWeek(parseCalendar(str));
    }

    /**
     * 格式化日期星期
     *
     * @param sdate YYYY-MM-DD HH:mm:ss string
     * @return 格式化后的日期星期
     */
    public static String formatDayWeek(String sdate) {
        Calendar calendar = parseCalendar(sdate);
        if (calendar == null)
            return "??/?? 星期?";
        Calendar mCurrentDate = Calendar.getInstance();
        String ws = formatWeek(calendar);
        int diff = mCurrentDate.get(Calendar.DATE)
                - calendar.get(Calendar.DATE);
        if (diff == 0) {
            return "今天 / " + ws;
        }
        if (diff == 1) {
            return "昨天 / " + ws;
        }
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DATE);
        return String.format("%s/%s / %s", formatInt(m), formatInt(d), ws);
    }

    /**
     * 日期友好格式化
     *
     * @param sdate 需要格式化的日期
     * @return 格式化后的日期 如 上午 下午 时分
     */
    public static String friendly_time3(String sdate) {
        Calendar calendar = parseCalendar(sdate);
        if (calendar == null)
            return sdate;
        Calendar mCurrentDate = Calendar.getInstance();
        SimpleDateFormat formatter = YYYYMMDDHHMMSS.get();
        if (null == formatter)
            return "";
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String s = hour < 12 ? "上午" : "下午";
        s += " HH:mm";
        if (mCurrentDate.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
            formatter.applyPattern(s);
        } else if (mCurrentDate.get(Calendar.DATE) - calendar.get(Calendar.DATE) == 1) {
            formatter.applyPattern("昨天 " + s);
        } else if (mCurrentDate.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            formatter.applyPattern("MM-dd " + s);
        } else {
            formatter.applyPattern("YYYY-MM-dd " + s);
        }
        return formatter.format(calendar.getTime());
    }

    /**
     * YYYY-MM-DD HH:mm:ss格式的时间字符串转换为{@link Calendar}类型
     *
     * @param str YYYY-MM-DD HH:mm:ss格式字符串
     * @return {@link Calendar}
     */
    public static Calendar parseCalendar(String str) {
        Matcher matcher = UniversalDatePattern.matcher(str);
        Calendar calendar = Calendar.getInstance();
        if (!matcher.find())
            return null;
        calendar.set(matcher.group(1) == null ? 0 : XJavaBType.toInt(matcher.group(1)),
                matcher.group(2) == null ? 0 : XJavaBType.toInt(matcher.group(2)) - 1,
                matcher.group(3) == null ? 0 : XJavaBType.toInt(matcher.group(3)),
                matcher.group(4) == null ? 0 : XJavaBType.toInt(matcher.group(4)),
                matcher.group(5) == null ? 0 : XJavaBType.toInt(matcher.group(5)),
                matcher.group(6) == null ? 0 : XJavaBType.toInt(matcher.group(6)));
        return calendar;
    }

    /**
     * 科学记数法 转换
     *
     * @param math 需要转换的数据
     * @return 转换后的数据
     */
    public static String swichGeneralMath(String math) {
        BigDecimal db = new BigDecimal(math);
        return db.toPlainString();
    }

    /**
     * double 转成String
     *
     * @param d      需要转换的double数据
     * @param format 需要转换的格式
     * @return 转换后的数据 异常返回""
     */
    public static String swichToString(double d, String format) {
        try {
            DecimalFormat df = new DecimalFormat(format);
            return df.format(d);
        } catch (Exception e) {
            XLog.e(XFormatData.class, e);
            return "";
        }
    }

    /**
     * 日期加减 类型 1 天 2 月 3 年
     *
     * @param type  加减的类型 {@link Calendar}
     * @param value 加减的数值
     * @return 格式化后的数据 异常返回""
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(int type, int value) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, 1);
            if (type == 1) {
                c.add(Calendar.DATE, value);
            } else if (type == 2) {
                c.add(Calendar.MONTH, value);
            } else if (type == 3) {
                c.add(Calendar.YEAR, value);
            }
            Date d = c.getTime();
            return format.format(d);
        } catch (Exception e) {
            XLog.e(XFormatData.class, e);
            return "";
        }
    }

    /**
     * String 转换成 Data
     *
     * @param time   需要转换的时间
     * @param format 转换的格式
     * @return Date {@link Date} 异常返回null
     */
    @SuppressLint("SimpleDateFormat")
    public static Date stringSwichData(String time, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(time);
        } catch (Exception e) {
            XLog.e(XFormatData.class, e);
            return null;
        }
    }

    /**
     * double 取整
     *
     * @param num 需要取整的double 数据
     * @return 取整后的double 数据
     */
    public static double getInt(double num) {
        BigDecimal b = new BigDecimal(num);
        num = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        return num;
    }

    /**
     * 判断两个时间的先后
     *
     * @param startTime  对比的第一个时间
     * @param beforeTime 对比的第二个时间
     * @return true false
     */
    @SuppressLint("SimpleDateFormat")
    public static boolean afterTime(String startTime, String beforeTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(startTime);
            Date date2 = sdf.parse(beforeTime);
            if (date1.getTime() <= date2.getTime()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            XLog.e(XFormatData.class, e);
            return false;
        }
    }

    /**
     * 判断一个时间是否在另一个时间后面
     *
     * @param startTime  开始时间
     * @param beforeTime 结束时间
     * @return true false
     */
    public static boolean afterTime(long startTime, long beforeTime) {
        Date date1 = new Date(startTime);
        Date date2 = new Date(beforeTime);
        if (date1.getYear() == date2.getYear()) {
            if (date1.getMonth() == date2.getMonth()) {
                if (date1.getDay() == date2.getDate()) {
                    return false;
                } else
                    return true;
            } else
                return true;
        } else
            return true;
    }


    /**
     * 获取当前时间为每年第几周
     *
     * @return 第几周
     */
    public static int getWeekOfYear() {
        return getWeekOfYear(new Date());
    }

    /**
     * 获取当前时间为每年第几周
     *
     * @param date 需要转换的时间{@link Date}
     * @return 第几周
     */
    public static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        int week = c.get(Calendar.WEEK_OF_YEAR) - 1;
        week = week == 0 ? 52 : week;
        return week > 0 ? week : 1;
    }

    /**
     * 获取当前时间数组
     *
     * @return 获取的时间数组
     */
    public static int[] getCurrentDate() {
        int[] dateBundle = new int[3];
        String[] temp = getDataTime("yyyy-MM-dd").split("-");
        for (int i = 0; i < 3; i++) {
            try {
                dateBundle[i] = Integer.parseInt(temp[i]);
            } catch (Exception e) {
                dateBundle[i] = 0;
            }
        }
        return dateBundle;
    }

    /**
     * 返回当前系统时间
     *
     * @param format 格式类型
     * @return 当前时间 异常返回""
     */
    public static String getDataTime(String format) {
        try {
            return new SimpleDateFormat(format, Locale.getDefault()).format(new Date());
        } catch (Exception e) {
            XLog.e(XFormatData.class, e);
            return "";
        }
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate string date that's type like YYYY-MM-DD HH:mm:ss
     * @return {@link Date} 异常返回null
     */
    public static Date toDate(String sdate) {
        return toDate(sdate, YYYYMMDDHHMMSS.get());
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate     string date that's type like YYYY-MM-DD HH:mm:ss
     * @param formatter 格式类型
     * @return {@link Date} 异常返回null
     */
    public static Date toDate(String sdate, SimpleDateFormat formatter) {
        try {
            return formatter.parse(sdate);
        } catch (Exception e) {
            XLog.e(XFormatData.class, e);
            return null;
        }
    }


    /**
     * Date日期转换成String YYYY-MM-DD HH:mm:ss
     *
     * @param date {@link Date}
     * @return 转换后的日期
     */
    public static String getDateString(Date date) {
        return YYYYMMDDHHMMSS.get().format(date);
    }

    /**
     * format to HH
     *
     * @param i integer
     * @return HH
     */
    public static String formatInt(int i) {
        return (i < 10 ? "0" : "") + i;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate 转换的日期
     * @return boolean true false
     */
    public static boolean isToday(String sdate) {
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = YYYYMMDD.get().format(today);
            String timeDate = YYYYMMDD.get().format(time);
            if (nowDate.equals(timeDate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是相同的一天
     *
     * @param sdate1 sdate1 对比的第一个日期
     * @param sdate2 sdate2 对比的第二个日期
     * @return true false
     */
    public static boolean isSameDay(String sdate1, String sdate2) {
        if (TextUtils.isEmpty(sdate1) || TextUtils.isEmpty(sdate2)) {
            return false;
        }
        Date date1 = toDate(sdate1);
        Date date2 = toDate(sdate2);
        if (date1 != null && date2 != null) {
            String d1 = YYYYMMDD.get().format(date1);
            String d2 = YYYYMMDD.get().format(date2);
            if (d1.equals(d2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static String getCurrentTimeStr() {
        return YYYYMMDDHHMMSS.get().format(new Date());
    }

    /***
     * 计算两个时间差，返回的是的秒s
     *
     * @param date1 第一个时间
     * @param date2 第二个时间
     * @return 时间差 秒 异常返回0
     */
    public static long calDateDifferent(String date1, String date2) {
        try {
            Date d1 = YYYYMMDDHHMMSS.get().parse(date1);
            Date d2 = YYYYMMDDHHMMSS.get().parse(date2);
            // 毫秒ms
            long diff = d2.getTime() - d1.getTime();
            return diff / 1000;
        } catch (Exception e) {
            XLog.e(XFormatData.class, e);
            return 0;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatStringTime(String time, String format) {
        try {
            Date date = XFormatData.stringSwichData(time, format);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            XLog.e(XFormatData.class, e);
            return time;
        }
    }

    /**
     * 格式化时间
     *
     * @param time         时间
     * @param beforeFormat 格式化前的格式
     * @param afterFormat  格式化之后的格式
     * @return time
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatStringTime(String time, String beforeFormat, String afterFormat) {
        try {
            Date date = XFormatData.stringSwichData(time, beforeFormat);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(afterFormat);
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            XLog.e(XFormatData.class, e);
            return time;
        }
    }

    /**
     * 两个double类型相乘
     *
     * @param d1    乘数
     * @param d2    被乘数数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的积
     */
    public static double multiply(double d1, double d2, int scale) {
        if (scale < 0) {
            scale = 0;
        }
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        BigDecimal multiply = b1.multiply(b2);
        return multiply.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     *
     * @param d2    被除数
     * @param d2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    private double divide(double d1, double d2, int scale) {
        if (scale < 0) {
            scale = 0;
        }
        if (d2 == 0)
            return 0.00D;
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
