package com.imoonx.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 自定义webview 长按
 */
public class CustomerWebView extends WebView implements View.OnLongClickListener {

    // 字体常量
    public static final int TEXTSIZE_LARGEST = 0;
    public static final int TEXTSIZE_LARGER = 1;
    public static final int TEXTSIZE_NORMAL = 2;
    public static final int TEXTSIZE_SMALLER = 3;
    public static final int TEXTSIZE_SMALLEST = 4;

    public static final int TEXTSIZE_LARGEST_NEW = 3;
    public static final int TEXTSIZE_LARGER_NEW = 2;
    public static final int TEXTSIZE_NORMAL_NEW = 1;
    public static final int TEXTSIZE_SMALLER_NEW = 0;


    protected WebSettings mSettings;
    protected WebViewClientListener mWebViewClientListener;

    public CustomerWebView(Context context) {
        this(context, null);
    }

    public CustomerWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomerWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initArgs();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomerWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initArgs();
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void initArgs() {
        setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (null != mWebViewClientListener)
                    mWebViewClientListener.shouldInterceptRequest(view, request);
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (null != mWebViewClientListener)
                    mWebViewClientListener.onPageFinished(view, url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 默认的处理方式，只认证安全的证书，不安全证书WebView不显示任何内容
                handler.proceed();// 接受所有证书
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//                }
                if (null != mWebViewClientListener)
                    mWebViewClientListener.onReceivedSslError(view, handler, error);
            }
        });

        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (null != mWebViewClientListener)
                    mWebViewClientListener.onProgressChanged(view, newProgress);
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (null != mWebViewClientListener)
                    mWebViewClientListener.onReceivedTitle(view, title);
                super.onReceivedTitle(view, title);
            }
        });

        setVerticalScrollBarEnabled(false);
        setOnLongClickListener(this);
        mSettings = getSettings();
        mSettings.setLoadWithOverviewMode(true);
        mSettings.setUseWideViewPort(true);
        mSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mSettings.setDefaultTextEncodingName("utf-8");
        mSettings.setTextZoom(100);
        mSettings.setSupportZoom(true);
        mSettings.setJavaScriptEnabled(true);
        mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    /**
     * 修改字体
     *
     * @param textSize 字体常量
     */
    public void changeTextsize(int textSize) {
        switch (textSize) {
            case TEXTSIZE_LARGEST:
                mSettings.setTextZoom(160);
                break;
            case TEXTSIZE_LARGER:
                mSettings.setTextZoom(130);
                break;
            case TEXTSIZE_NORMAL:
                mSettings.setTextZoom(100);
                break;
            case TEXTSIZE_SMALLER:
                mSettings.setTextZoom(80);
                break;
            case TEXTSIZE_SMALLEST:
                mSettings.setTextZoom(50);
                break;
            default:
                break;
        }
    }

    /**
     * 修改字体
     *
     * @param textSize 字体常量
     * @param desc     区别参数
     */
    public void changeTextsize(int textSize, int desc) {
        switch (textSize) {
            case TEXTSIZE_LARGEST_NEW:
                mSettings.setTextZoom(160);
                break;
            case TEXTSIZE_LARGER_NEW:
                mSettings.setTextZoom(130);
                break;
            case TEXTSIZE_NORMAL_NEW:
                mSettings.setTextZoom(100);
                break;
            case TEXTSIZE_SMALLER_NEW:
                mSettings.setTextZoom(80);
                break;
            default:
                break;
        }
    }

    /**
     * 设置加载监听
     *
     * @param listener 监听器
     */
    public void setWebViewClientListener(WebViewClientListener listener) {
        this.mWebViewClientListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        if (null != mWebViewClientListener)
            mWebViewClientListener.onLongClick(getHitTestResult());
        return false;
    }

    public interface WebViewClientListener {
        /**
         * 加载监听
         *
         * @param view webview
         * @param url  加载地址
         */
        void onPageFinished(WebView view, String url);

        /**
         * 长按回调
         *
         * @param hitTestResult WebView.HitTestResult
         */
        void onLongClick(HitTestResult hitTestResult);

        void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error);

        void onProgressChanged(WebView view, int newProgress);

        void onReceivedTitle(WebView view, String title);

        void shouldInterceptRequest(WebView view, WebResourceRequest request);

    }

}
