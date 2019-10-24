package com.imoonx.http.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.Response;

/**
 * Bitmap 回调
 */
public abstract class BitmapCallback extends Callback<Bitmap> {
    @Override
    public Bitmap parseResponse(Response response) throws Exception {
        return BitmapFactory.decodeStream(response.body().byteStream());
    }
}
