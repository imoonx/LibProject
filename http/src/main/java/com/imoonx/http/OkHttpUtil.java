package com.imoonx.http;

import android.os.Handler;
import android.os.Looper;

import com.imoonx.http.builder.GetBuilder;
import com.imoonx.http.builder.OtherRequestBuilder;
import com.imoonx.http.builder.PostFileBuilder;
import com.imoonx.http.builder.PostFormBuilder;
import com.imoonx.http.builder.PostStringBuilder;
import com.imoonx.http.callback.Callback;
import com.imoonx.http.cookie.SimpleCookieJar;
import com.imoonx.http.exception.Exceptions;
import com.imoonx.http.https.HttpsUtil;
import com.imoonx.http.request.RequestCall;
import com.imoonx.util.XLog;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 联网工具类
 */

public class OkHttpUtil {
    /**
     * 设置全局超时时间
     */
    public static long DEFAULT_MILLISECONDS = 10 * 1000;
    private static OkHttpUtil mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDeliveryHandler;

    public static OkHttpUtil getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtil.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtil();
                }
            }
        }
        return mInstance;
    }

    private OkHttpUtil() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.cookieJar(new SimpleCookieJar());
        mDeliveryHandler = new Handler(Looper.getMainLooper());
        okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        mOkHttpClient = okHttpClientBuilder.build();
    }

    public void setOkHttpClient(OkHttpClient mOkHttpClient) {
        this.mOkHttpClient = mOkHttpClient;
    }

    public Handler getDelivery() {
        return mDeliveryHandler;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    public static OtherRequestBuilder put() {
        return new OtherRequestBuilder(METHOD.PUT);
    }

    public static OtherRequestBuilder head() {
        return new OtherRequestBuilder(METHOD.HEAD);
    }

    public static OtherRequestBuilder delete() {
        return new OtherRequestBuilder(METHOD.DELETE);
    }

    public static OtherRequestBuilder patch() {
        return new OtherRequestBuilder(METHOD.PATCH);
    }

    public void execute(final RequestCall requestCall, Callback callback) {
        XLog.i(OkHttpUtil.class, "{method:" + requestCall.getRequest().method() + ", detail:" + requestCall.getOkHttpRequest().toString() + "}");
        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;

        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                sendFailResultCallback(call, e, finalCallback);
            }

            @Override
            public void onResponse(final Call call, final Response response) {
                if (response.code() >= 400 && response.code() <= 599) {
                    try {
                        sendFailResultCallback(call, new RuntimeException(response.body().string()), finalCallback);
                    } catch (IOException e) {
                        XLog.e(OkHttpUtil.class, e);
                    }
                    return;
                }
                try {
                    Object o = finalCallback.parseResponse(response);
                    sendSuccessResultCallback(o, finalCallback);
                } catch (Exception e) {
                    XLog.e(OkHttpUtil.class, e);
                    sendFailResultCallback(call, e, finalCallback);
                }
            }
        });
    }

    public void execute(Request request, Callback callback) {
        if (request == null)
            Exceptions.illegalArgument("the request can not be null !");
        XLog.i(OkHttpUtil.class, "{method:" + request.method() + ", detail:" + request.toString() + "}");

        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;
        mOkHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                sendFailResultCallback(call, e, finalCallback);
            }

            @Override
            public void onResponse(final Call call, final Response response) {
                if (response.code() >= 400 && response.code() <= 599) {
                    try {
                        sendFailResultCallback(call, new RuntimeException(response.body().string()), finalCallback);
                    } catch (IOException e) {
                        XLog.e(OkHttpUtil.class, e);
                    }
                    return;
                }
                try {
                    Object o = finalCallback.parseResponse(response);
                    sendSuccessResultCallback(o, finalCallback);
                } catch (Exception e) {
                    XLog.e(OkHttpUtil.class, e);
                    sendFailResultCallback(call, e, finalCallback);
                }
            }
        });
    }

    /**
     * 请求错误回调
     *
     * @param call     {@link Call}
     * @param e        异常信息
     * @param callback 回掉函数 {@link Callback}
     */
    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback) {
        if (callback == null)
            return;
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(call, e);
                callback.after();
            }
        });
    }

    /**
     * 请求成功回调
     *
     * @param object   数据对象
     * @param callback 回掉函数 {@link Callback}
     */
    public void sendSuccessResultCallback(final Object object, final Callback callback) {
        if (callback == null)
            return;
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object);
                callback.after();
            }
        });
    }

    /**
     * 取消http请求
     *
     * @param tag 设置的Request TAG
     */
    public void cancelTag(Object tag) {
        cancelTag(getOkHttpClient(), tag);
    }

    /**
     * 取消http请求
     *
     * @param client httpclient
     * @param tag    设置的Request TAG
     */
    public static void cancelTag(OkHttpClient client, Object tag) {
        try {
            if (client == null || tag == null) return;
            for (Call call : client.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
            for (Call call : client.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
        } catch (Exception e) {
            XLog.e(OkHttpUtil.class, "请求取消失败：" + e);
        }
    }

    /**
     * 取消所有请求请求
     */
    public void cancelAll() {
        cancelAll(getOkHttpClient());
    }

    /**
     * 取消所有请求请求
     *
     * @param client httpclient
     */
    public static void cancelAll(OkHttpClient client) {
        try {
            if (client == null) return;
            for (Call call : client.dispatcher().queuedCalls()) {
                call.cancel();
            }
            for (Call call : client.dispatcher().runningCalls()) {
                call.cancel();
            }
        } catch (Exception e) {
            XLog.e(OkHttpUtil.class, "请求取消失败：" + e);
        }
    }

    /**
     * 设置https证书
     *
     * @param certificates 流
     */
    @SuppressWarnings("deprecation")
    public OkHttpUtil setCertificates(InputStream... certificates) {
        SSLSocketFactory sslSocketFactory = HttpsUtil.getSslSocketFactory(certificates, null, null);
        if (null != sslSocketFactory)
            mOkHttpClient = getOkHttpClient().newBuilder().sslSocketFactory(sslSocketFactory).build();
        return this;
    }

    public OkHttpUtil setHostNameVerifier(HostnameVerifier hostNameVerifier) {
        mOkHttpClient = getOkHttpClient().newBuilder().hostnameVerifier(hostNameVerifier).build();
        return this;
    }

    /**
     * 设置连接超时时间
     *
     * @param timeout 超时时间
     * @param units   TimeUnit.DAYS          //天
     *                TimeUnit.HOURS         //小时
     *                TimeUnit.MINUTES       //分钟
     *                TimeUnit.SECONDS       //秒
     *                TimeUnit.MILLISECONDS  //毫秒
     *                TimeUnit.NANOSECONDS   //毫微秒
     *                TimeUnit.MICROSECONDS  //微秒
     */
    public OkHttpUtil setConnectTimeout(int timeout, TimeUnit units) {
        if (null == units)
            units = TimeUnit.MILLISECONDS;
        mOkHttpClient = getOkHttpClient().newBuilder().connectTimeout(timeout, units).build();
        return this;
    }

    /**
     * 设置写入超时时间
     *
     * @param timeout 超时时间
     * @param units   TimeUnit.DAYS          //天
     *                TimeUnit.HOURS         //小时
     *                TimeUnit.MINUTES       //分钟
     *                TimeUnit.SECONDS       //秒
     *                TimeUnit.MILLISECONDS  //毫秒
     *                TimeUnit.NANOSECONDS   //毫微秒
     *                TimeUnit.MICROSECONDS  //微秒
     */
    public OkHttpUtil setWriteTimeout(int timeout, TimeUnit units) {
        if (null == units)
            units = TimeUnit.MILLISECONDS;
        mOkHttpClient = getOkHttpClient().newBuilder().writeTimeout(timeout, units).build();
        return this;
    }

    /**
     * 设置读取超时时间
     *
     * @param timeout 超时时间
     * @param units   TimeUnit.DAYS          //天
     *                TimeUnit.HOURS         //小时
     *                TimeUnit.MINUTES       //分钟
     *                TimeUnit.SECONDS       //秒
     *                TimeUnit.MILLISECONDS  //毫秒
     *                TimeUnit.NANOSECONDS   //毫微秒
     *                TimeUnit.MICROSECONDS  //微秒
     */
    public OkHttpUtil setReadTimeout(int timeout, TimeUnit units) {
        if (null == units)
            units = TimeUnit.MILLISECONDS;
        mOkHttpClient = getOkHttpClient().newBuilder().readTimeout(timeout, units).build();
        return this;
    }

    public static class METHOD {
        public static final String HEAD = "HEAD";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";
    }
}
