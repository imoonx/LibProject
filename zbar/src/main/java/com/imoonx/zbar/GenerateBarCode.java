package com.imoonx.zbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.imoonx.util.XIOUtil;
import com.imoonx.util.XJavaBType;
import com.imoonx.util.XLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

/**
 * 使用zxing 生成二维码
 * <p>
 * Created by 36238 on 2018/1/17.
 */

public class GenerateBarCode {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * 生成二维码 要转换的地址或字符串,可以是中文
     *
     * @param content 生成二维码的内容
     * @param width   生成内容的宽
     * @param height  生成内容的高
     * @param margin  margin值 去除边框较大的问题
     * @return bitmap 异常返回null
     */
    public static Bitmap createQRImage(String content, int width, int height, int margin) {
        Bitmap bitmap = null;
        try {
            // 判断content合法性
            if (TextUtils.isEmpty(content) || content.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            if (margin > 0)
                hints.put(EncodeHintType.MARGIN, XJavaBType.toStr(margin));
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = BLACK;
                    } else {
                        pixels[y * width + x] = WHITE;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            XLog.e(GenerateBarCode.class, e);
            return null;
        }
    }

    /**
     * 生成有logo的二维码
     *
     * @param content 生成二维码的内容
     * @param width   生成内容的宽
     * @param height  生成内容的高
     * @param logo    中间logo
     * @param margin  margin值 去除边框较大的问题
     * @param context 上下文
     * @return bitmap 异常返回null
     */
    public static Bitmap createQRImageAndLogo(String content, int width, int height, int logo, int margin, Context context) {
        Bitmap qrImage = createQRImage(content, width, height, margin);
        if (null == qrImage)
            return null;
        if (null == content)
            return qrImage;
        try {
            Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), logo);
            Bitmap bitmap = addLogo(qrImage, logoBitmap);
            if (null == bitmap)
                return qrImage;
            return bitmap;
        } catch (Exception e) {
            XLog.e(GenerateBarCode.class, e);
            return qrImage;
        }
    }

    /**
     * 添加logo
     *
     * @param qrBitmap   二维码
     * @param logoBitmap logoBitmap
     * @return 包含logo的bitmap
     */
    private static Bitmap addLogo(Bitmap qrBitmap, Bitmap logoBitmap) {
        int qrBitmapWidth = qrBitmap.getWidth();
        int qrBitmapHeight = qrBitmap.getHeight();
        int logoBitmapWidth = logoBitmap.getWidth();
        int logoBitmapHeight = logoBitmap.getHeight();
        Bitmap blankBitmap;
        blankBitmap = Bitmap.createBitmap(qrBitmapWidth, qrBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blankBitmap);
        canvas.drawBitmap(qrBitmap, 0, 0, null);
        canvas.save();
        float scaleSize = 1.0f;
        while ((logoBitmapWidth / scaleSize) > (qrBitmapWidth / 5) || (logoBitmapHeight / scaleSize) > (qrBitmapHeight / 5)) {
            scaleSize *= 2;
        }
        float sx = 1.0f / scaleSize;
        canvas.scale(sx, sx, qrBitmapWidth / 2, qrBitmapHeight / 2);
        canvas.drawBitmap(logoBitmap, (qrBitmapWidth - logoBitmapWidth) / 2, (qrBitmapHeight - logoBitmapHeight) / 2, null);
        canvas.restore();
        return blankBitmap;
    }

    /**
     * 将内容contents生成长宽均为width的图片，图片路径由imgPath指定
     *
     * @param content 生成二维码的内容
     * @param width   生成内容的宽
     * @param margin  边距值默认0
     * @param imgPath 保存路径
     * @return File 异常返回null
     */
    public static File getQRCodeImge(String content, int width, int margin, String imgPath) {
        return getQRCodeImge(content, width, width, margin, imgPath);
    }

    /**
     * 将内容contents生成长宽均为width的图片，图片路径由imgPath指定
     *
     * @param content 生成二维码的内容
     * @param width   生成内容的宽
     * @param height  生成内容的高
     * @param margin  margin值 去除边框较大的问题
     * @param imgPath 保存路径
     * @return File 异常返回null
     */
    public static File getQRCodeImge(String content, int width, int height, int margin, String imgPath) {
        File imageFile = new File(imgPath);
        Bitmap qrImage = createQRImage(content, width, height, margin);
        if (null == qrImage)
            return null;
        try {
            writeToFile(qrImage, imageFile);
            return imageFile;
        } catch (IOException e) {
            XLog.e(GenerateBarCode.class, e);
            return null;
        }
    }

    /**
     * 将二维码写入到文件中
     *
     * @param bitmap bitmap
     * @param file   file
     */
    private static void writeToFile(Bitmap bitmap, File file) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapdata);
        XIOUtil.close(fos);
    }
}
