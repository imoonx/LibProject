package com.imoonx.third.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.imoonx.common.base.BaseActivity;
import com.imoonx.common.ui.CustomerWebView;
import com.imoonx.common.ui.EmptyLayout;
import com.imoonx.common.ui.SharePopupWindow;
import com.imoonx.common.ui.dialog.DialogHelper;
import com.imoonx.third.R;
import com.imoonx.third.share.CustomShareListener;
import com.imoonx.third.share.ShareCallbackListener;
import com.imoonx.util.Res;
import com.imoonx.util.Toast;
import com.imoonx.util.XLog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import static android.text.TextUtils.TruncateAt.END;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.imoonx.third.share.CustomShareListener.STATUS.CANCEL;

public abstract class BaseWebViewActivity extends BaseActivity implements ShareCallbackListener, SharePopupWindow.PopWindowsDismissListener, CustomerWebView.WebViewClientListener {

    protected CustomerWebView mWebView;

    protected EmptyLayout mErrorLayout;
    protected ProgressBar mProgress;
    protected CustomShareListener mShareListener;
    protected SharePopupWindow mSharePW;
    protected int mStartIndex = 2;

    protected int SHARE_TYPE = IMAGE_WEB;
    protected final static int IMAGE_TYPE = 0;
    protected final static int IMAGE_WEB = 1;

    protected String shareUrl;
    protected String shareTitle;
    protected String shareImageUrl;

    public static final String ACTIVITY_BUNDLE = "activity_bundle";

    @Override
    protected int getLayoutID() {
        return R.layout.activity_base_webview;
    }

    protected boolean isNeedProgressBar() {
        return false;
    }

    protected boolean isNeedDynamicsTitle() {
        return true;
    }

    @Override
    protected void initWidget() {
        TextView title = getTextView();
        title.setSingleLine();
        title.setEllipsize(END);
        mWebView = findViewById(R.id.web_view);
        mWebView.setWebViewClientListener(this);

        mErrorLayout = findViewById(R.id.error_layout);
        mProgress = findViewById(R.id.progress);

        if (isNeedProgressBar())
            mErrorLayout.setVisibility(View.GONE);
        else mProgress.setVisibility(View.GONE);

        mShareListener = new CustomShareListener(this);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getBundleExtra(ACTIVITY_BUNDLE);
        shareUrl = bundle.getString("url");
        shareTitle = bundle.getString("title");
        shareImageUrl = bundle.getString("image_url");
        XLog.i(this.getClass(), "shareUrl:" + shareUrl);
        mWebView.loadUrl(shareUrl);
    }

    @Override
    public void onSetBackgroundAlpha() {
        setBackageAlpha(1.0F);
        mSharePW = null;
    }

    /**
     * 分享回调监听
     *
     * @param platform 平台
     * @param status   {@link CustomShareListener.STATUS}
     */
    @Override
    public void onShareCallbackListener(SHARE_MEDIA platform, CustomShareListener.STATUS status) {
        if (status.equals(CANCEL))
            Toast.showToast("分享取消");
    }

    // 字体设置
    protected void showSizeDialog() {
        String[] items = Res.getStringArray(R.array.text_size_name);
        DialogHelper.getSingleChoiceDialog(this, Res.getString(R.string.web_set_text_size),
                items, mStartIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mStartIndex = which;
                        mWebView.changeTextsize(mStartIndex);
                    }
                }, "").show();
    }

    @Override
    protected int getMenuRes() {
        return R.menu.menu_webview;
    }

    @Override
    protected void getMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.webview_ziti) {
            showSizeDialog();
        } else if (item.getItemId() == R.id.webview_share) {
            SHARE_TYPE = IMAGE_WEB;
            shareWindows(0.5F);
        }
        super.getMenuItemClick(item);
    }

    /**
     * 分享弹框
     *
     * @param backgroundAlpha 背景透明度 0-1
     */
    protected void shareWindows(float backgroundAlpha) {
        if (null == getSharePopView())
            return;
        if (mSharePW == null) {
            mSharePW = new SharePopupWindow(this, this, getSharePopView());
        }
        mSharePW.showAtLocation(findViewById(R.id.main_content));
        setBackageAlpha(backgroundAlpha);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        UMShareAPI.get(this).release();
        super.onDestroy();
    }

    public void onResume() {
        setVideo("onResume");
        super.onResume();
    }

    public void onPause() {
        setVideo("onPause");
        super.onPause();
    }

    private void setVideo(String name) {
        try {
            if (null != mWebView)
                mWebView.getClass().getMethod(name).invoke(mWebView, (Object[]) null);
        } catch (Exception e) {
            XLog.e(this.getClass(), e.toString());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            XLog.i(this.getClass(), "按下了back键   onKeyDown()");
            this.finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 跳转浏览器
     *
     * @param url 跳转地址
     */
    protected void openBrowser(String url) {
        if (url.contains("http") || url.contains("https")) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        view.getSettings().setBlockNetworkImage(false);
        if (!isNeedProgressBar() && null != mErrorLayout)
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    @Override
    public void onLongClick(WebView.HitTestResult hitTestResult) {
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (isNeedProgressBar() && null != mProgress)
            if (newProgress == 100) {
                mProgress.setVisibility(GONE);
            } else {
                if (mProgress.getVisibility() == GONE) mProgress.setVisibility(VISIBLE);
                mProgress.setProgress(newProgress);
            }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (isNeedDynamicsTitle() && !TextUtils.isEmpty(title)) {
            setTitleDesc(title);
            shareTitle = title;
        }
    }

    @Override
    public void shouldInterceptRequest(WebView view, WebResourceRequest request) {

    }

    /**
     * 分享内容
     */
    protected void shareContent(SHARE_MEDIA share_media, int logo) {
        UMImage thumb;// 网络图片
        if (TextUtils.isEmpty(shareImageUrl)) {
            thumb = new UMImage(this, logo);
        } else {
            thumb = new UMImage(this, shareImageUrl);
        }
        thumb.compressStyle = UMImage.CompressStyle.SCALE;
        if (SHARE_TYPE == IMAGE_TYPE) {
            if (TextUtils.isEmpty(shareImageUrl)) {
                Toast.showToast(this, "分享的文件不存在");
                return;
            }
            UMImage image = new UMImage(this, shareImageUrl);//网络图片
            image.setThumb(thumb);
            new ShareAction(this).withMedia(image).setPlatform(share_media).setCallback(mShareListener).share();
        } else {
            UMWeb web = new UMWeb(shareUrl);
            web.setTitle(shareTitle);// 标题
            web.setThumb(thumb); // 缩略图
            web.setDescription(shareTitle);// 描述
            new ShareAction(this).withMedia(web).setPlatform(share_media).setCallback(mShareListener).share();
        }
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tv_wx_timeline:
//                mSharePW.dismiss();
//                shareContent(SHARE_MEDIA.WEIXIN_CIRCLE);
//                break;
//            case R.id.tv_wx_session:
//                mSharePW.dismiss();
//                shareContent(SHARE_MEDIA.WEIXIN);
//                break;
//            case R.id.tv_wb:
//                mSharePW.dismiss();
//                shareContent(SHARE_MEDIA.SINA);
//                break;
//            default:
//                break;
//        }
//    }

    /**
     * webview 设置cookie
     *
     * @param context 上下文
     * @param url     请求地址
     * @param cookies 请求cookies
     */
    @SuppressWarnings("deprecated")
    protected void synCookies(Context context, String url, String cookies) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.removeAllCookie();
        cookieManager.setCookie(url, cookies);//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();
        String newCookie = cookieManager.getCookie(url);
        XLog.e(this.getClass(), newCookie);
    }

    protected abstract View getSharePopView();

}
