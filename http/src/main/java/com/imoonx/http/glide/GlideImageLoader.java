package com.imoonx.http.glide;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.imoonx.util.XLog;

/**
 * Glide 图片加载辅助类 适配圆形图片加载情况
 */
public class GlideImageLoader {

    /**
     * 加载图片
     *
     * @param loader Glide RequestManager
     * @param view   ImageView
     * @param url    图片地址
     */
    public static void loadImage(RequestManager loader, ImageView view, String url) {
        loadImage(loader, view, url, 0);
    }


    /**
     * 加载图片
     *
     * @param loader      Glide RequestManager
     * @param view        ImageView
     * @param url         图片地址
     * @param placeholder 默认图片
     */
    public static void loadImage(RequestManager loader, ImageView view, String url, int placeholder) {
        loadImage(loader, view, url, placeholder, placeholder);
    }


    /**
     * 加载图片
     *
     * @param loader      Glide RequestManager
     * @param view        ImageView
     * @param url         图片地址
     * @param placeholder 默认图片
     * @param error       加载错误默认图片
     */
    public static void loadImage(RequestManager loader, ImageView view, String url, int placeholder, int error) {
        loadImage(loader, view, url, placeholder, error, false);
    }

    /**
     * 加载图片
     *
     * @param loader       Glide RequestManager
     * @param view         ImageView
     * @param url          图片地址
     * @param placeholder  默认图片
     * @param error        加载错误默认图片
     * @param isCenterCrop 是否中心剪切
     */
    public static void loadImage(RequestManager loader, ImageView view, String url, int placeholder, int error, boolean isCenterCrop) {
        XLog.i(GlideImageLoader.class, "url=" + url);
        XLog.i(GlideImageLoader.class, "loader=null" + (loader == null));
        if (TextUtils.isEmpty(url)) {
            view.setImageResource(placeholder);
            return;
        }
        if (null == loader)
            return;

        if (view instanceof BaseCircleImageView) {
            BitmapRequestBuilder builder = loader.load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(placeholder).error(error).listener(new LoggingListener<String, Bitmap>());
            if (isCenterCrop)
                builder.centerCrop();
            builder.into(new BitmapImageViewTarget(view) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(view.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    view.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            DrawableRequestBuilder builder = loader.load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(placeholder).error(error).
                    listener(new LoggingListener<String, GlideDrawable>());
            if (isCenterCrop)
                builder.centerCrop();
            builder.into(view);
        }
    }
}
