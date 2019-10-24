package com.imoonx.http.callback;


import java.io.IOException;

import okhttp3.Response;

/**
 * 字符串回调
 */
public abstract class StringCallback extends Callback<String> {
    @Override
    public String parseResponse(Response response) throws IOException {
        return response.body().string();
    }
}
