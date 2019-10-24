package com.imoonx.http.gson;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imoonx.util.XLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Gson 工具类
 */
public class GsonUtil {

    private static Gson mGson;

    /**
     * 创建单例  Gson
     *
     * @return Gson
     */
    public static Gson getGson() {
        if (null == mGson)
            mGson = new GsonBuilder()//建造者模式设置不同的配置
                    .disableHtmlEscaping()//防止对网址乱码 忽略对特殊字符的转换
                    .create();
        return mGson;
    }


    /**
     * 解析为一个具体的对象
     *
     * @param json 要解析的字符串
     * @param t    要解析的对象
     * @param <T>  将json字符串解析成obj类型的对象
     * @return 封装对象 异常返回null
     */
    public static <T> T jsonToObject(String json, Class<T> t) {
        if (TextUtils.isEmpty(json))
            return null;
        try {
            return getGson().fromJson(json, t);
        } catch (Exception e) {
            XLog.e(GsonUtil.class, e);
            return null;
        }
    }

    /**
     * 对象转json
     *
     * @param obj 对象
     * @return json字符串 异常返回""
     */
    public static String objectToJson(Object obj) {
        if (null == obj)
            return "";
        try {
            return getGson().toJson(obj);
        } catch (Exception e) {
            XLog.e(GsonUtil.class, e);
            return null;
        }
    }

    /**
     * json转list
     *
     * @param json json字符串
     * @param t    实体类
     * @param <T>  泛型
     * @return List<T> 返回转换后的List
     */
    public static <T> List<T> jsonToList(String json, Class<T> t) {
        List<T> list = new ArrayList<>();
        if (TextUtils.isEmpty(json))
            return list;
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement je = jsonParser.parse(json);
            JsonArray ja = null;
            if (je.isJsonArray()) {
                ja = je.getAsJsonArray();
            }
            if (ja != null && ja.size() > 0) {
                for (int i = 0; i < ja.size(); i++) {
                    list.add(getGson().fromJson(ja.get(i), t));
                }
            }
        } catch (Exception e) {
            XLog.e(GsonUtil.class, e);
        }
        return list;
    }
}