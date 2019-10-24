package com.imoonx.zbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.imoonx.util.XLog;

import java.util.Hashtable;

/**
 * 判断识别二维码
 */
public class QRCodeUtil {

    /**
     * 根据bitmap 解析二维码
     *
     * @param bitmap 需要解析的bitmap
     * @return {@link com.google.zxing.Result}
     */
    public static Result handleQRCodeFormBitmap(Bitmap bitmap) {
        return getResult(bitmap);
    }

    /**
     * 根据图片路径解析二维码
     *
     * @param path 图片路径
     * @return {@link com.google.zxing.Result}
     */
    public static Result handleQRCodeFormPath(String path) {
        Bitmap bitmap = loadBitmap(path);
        if (null == bitmap) {
            return null;
        } else return getResult(bitmap);
    }

    /**
     * 根据bitmap 解析二维码
     *
     * @param bitmap 需要解析的bitmap
     * @return {@link com.google.zxing.Result}
     */
    @Nullable
    private static Result getResult(Bitmap bitmap) {
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
        BinaryBitmap newBitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result = null;
        try {
            result = reader.decode(newBitmap, hints);
        } catch (Exception e) {
            XLog.e(GenerateBarCode.class, e);
        }
        return result;
    }

    /**
     * 根据路径加载bitmap
     *
     * @param path 图片路径
     * @return bitmap
     */
    private static Bitmap loadBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }
}
