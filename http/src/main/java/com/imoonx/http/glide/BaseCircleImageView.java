package com.imoonx.http.glide;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 圆形图片
 */
public class BaseCircleImageView extends ImageView {

    public BaseCircleImageView(Context context) {
        super(context);
    }

    public BaseCircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseCircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
