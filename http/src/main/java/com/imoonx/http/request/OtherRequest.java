package com.imoonx.http.request;

import android.text.TextUtils;

import com.imoonx.http.OkHttpUtil;
import com.imoonx.http.exception.Exceptions;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;

/**
 * 其他请求
 */
public class OtherRequest extends OkHttpRequest {

    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    private RequestBody requestBody;
    private String method;
    private String content;

    public OtherRequest(RequestBody requestBody, String content, String method, String url, Object tag, Map<String, String> params, Map<String, String> headers) {
        super(url, tag, params, headers);
        this.requestBody = requestBody;
        this.method = method;
        this.content = content;
    }

    @Override
    protected RequestBody buildRequestBody() {
        if (requestBody == null && TextUtils.isEmpty(content) && HttpMethod.requiresRequestBody(method)) {
            Exceptions.illegalArgument("requestBody and content can not be null in method:" + method);
        }
        if (requestBody == null && !TextUtils.isEmpty(content)) {
            requestBody = RequestBody.create(MEDIA_TYPE_PLAIN, content);
        }
        return requestBody;
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        switch (method) {
            case OkHttpUtil.METHOD.PUT:
                builder.put(requestBody);
                break;
            case OkHttpUtil.METHOD.DELETE:
                if (requestBody == null)
                    builder.delete();
                else
                    builder.delete(requestBody);
                break;
            case OkHttpUtil.METHOD.HEAD:
                builder.head();
                break;
            case OkHttpUtil.METHOD.PATCH:
                builder.patch(requestBody);
                break;
        }
        return builder.build();
    }

    @Override
    public String toString() {
        if (!TextUtils.isEmpty(content)) {
            return super.toString() + ", requestBody{content=" + content + "} ";
        }
        return super.toString() + ", requestBody{requestBody=" + requestBody.toString() + "} ";
    }
}
