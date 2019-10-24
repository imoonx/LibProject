package com.imoonx.common.ui;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.imoonx.http.glide.GlideImageLoader;

/**
 * 圆形头像类
 */
public class AvatarView extends CircleImageView {

    public static final String AVATAR_SIZE_REG = "_[0-9]{1,3}";
    public static final String MIDDLE_SIZE = "_100";
    public static final String LARGE_SIZE = "_200";

    private Activity aty;

    public AvatarView(Context context) {
        this(context, null);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        aty = (Activity) context;
    }

    /**
     * 设置头像
     *
     * @param url        图片地址
     * @param def_header 默认图片
     */
    public void setAvatarUrl(String url, int def_header) {
        if (TextUtils.isEmpty(url) || null == aty) {
            setImageResource(def_header);
            return;
        }
        setAvatarUrl(Glide.with(aty), url, def_header);
    }

    /**
     * 设置头像
     *
     * @param manager    RequestManager Glide
     * @param url        图片地址
     * @param def_header 默认图片
     */
    public void setAvatarUrl(RequestManager manager, String url, int def_header) {
        if (TextUtils.isEmpty(url) || null == aty) {
            setImageResource(def_header);
            return;
        }
        GlideImageLoader.loadImage(manager, this, url, def_header, def_header);
    }

    public static String getSmallAvatar(String source) {
        return source;
    }

    public static String getMiddleAvatar(String source) {
        if (source == null)
            return "";
        return source.replaceAll(AVATAR_SIZE_REG, MIDDLE_SIZE);
    }

    public static String getLargeAvatar(String source) {
        if (source == null)
            return "";
        return source.replaceAll(AVATAR_SIZE_REG, LARGE_SIZE);
    }
}
