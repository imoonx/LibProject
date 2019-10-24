package com.imoonx.http.callback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 回掉函数类
 *
 * @param <T>
 */
public abstract class Callback<T> {
    /**
     * UI Thread
     * 请求之前
     *
     * @param request 请求类
     */
    public void before(Request request) {
    }

    /**
     * UI Thread
     * 请求之后
     */
    public void after() {
    }

    /**
     * UI Thread
     *
     * @param progress 当前进度
     */
    public void progress(float progress) {
    }

    /**
     * Thread Pool Thread
     * <p>
     * 解析响应数据
     *
     * @param response 响应类
     */
    public abstract T parseResponse(Response response) throws Exception;

    /**
     * 错误回调
     *
     * @param call 回调函数
     * @param e    异常
     */
    public abstract void onError(Call call, Exception e);

    /**
     * 响应回调
     *
     * @param response 响应类
     */
    public abstract void onResponse(T response);


    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public Object parseResponse(Response response) throws Exception {
            return null;
        }

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(Object response) {

        }
    };

}