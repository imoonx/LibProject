package com.imoonx.http.builder;

import com.imoonx.http.request.PostFileRequest;
import com.imoonx.http.request.RequestCall;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MediaType;

/**
 * 单文件上传
 */
public class PostFileBuilder extends OkHttpRequestBuilder {

    private File file;
    private MediaType mediaType;

    public PostFileBuilder file(File file) {
        this.file = file;
        return this;
    }

    public PostFileBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostFileRequest(url, tag, params, headers, file, mediaType).build();
    }

    @Override
    public PostFileBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public PostFileBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public PostFileBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public PostFileBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new ConcurrentHashMap<>();
        }
        if (val == null)
            params.put(key, "");
        else
            params.put(key, val);
        return this;
    }

    @Override
    public PostFileBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public PostFileBuilder addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new ConcurrentHashMap<>();
        }
        headers.put(key, val);
        return this;
    }
}
