package com.imoonx.http.builder;

import com.imoonx.http.request.RequestCall;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 上传参数构建基类
 */
public abstract class OkHttpRequestBuilder {

    protected String url;
    protected Object tag;
    protected Map<String, String> headers = new ConcurrentHashMap<>();
    protected Map<String, String> params = new ConcurrentHashMap<>();

    public abstract OkHttpRequestBuilder url(String url);

    public abstract OkHttpRequestBuilder tag(Object tag);

    public abstract OkHttpRequestBuilder params(Map<String, String> params);

    public abstract OkHttpRequestBuilder addParams(String key, String val);

    public abstract OkHttpRequestBuilder headers(Map<String, String> headers);

    public abstract OkHttpRequestBuilder addHeader(String key, String val);

    public abstract RequestCall build();


}
