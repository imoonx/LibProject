package com.example.apptest;

import com.imoonx.util.BaseApplication;
import com.imoonx.util.Toast;
import com.imoonx.util.XLog;

/**
 * Created by 36238 on 2018/1/8.
 * <p>
 * 在Google官方开发文档中，说明了 **mdpi：hdpi：xhdpi：xxhdpi：xxxhdpi=1：1.5：2：3：4 **的尺寸比例进行缩放。
 * "720P     xhdpi"  240dpi-320dpi 2     1
 * "1080P   xxhdpi " 320dpi-480dpi 3     1.5
 * xxxhdpi  480dpi-640dpi          4     2
 * 例如，一个图标的大小为48×48dp，表示在mdpi上，实际大小为48×48px，在hdpi像素密度上，实际尺寸为mdpi上的1.5倍，即72×72px，以此类推。
 */
public class AppContext extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        XLog.setIsPrint(BuildConfig.DEBUG);
        Toast.setCustom(false);
//        CrashExceptionHandler.getInstance().setDirName("yanji").init();
//        OkHttpUtil.getInstance()
//                .setConnectTimeout(10 * 1000, null)
//                .setReadTimeout(20 * 1000, null)
//                .setWriteTimeout(20 * 1000, null);
//
//        OkHttpClient okHttpClient = OkHttpUtil.getInstance().getOkHttpClient();
//        int i = okHttpClient.connectTimeoutMillis();
//        int i1 = okHttpClient.writeTimeoutMillis();
//        int i2 = okHttpClient.readTimeoutMillis();
//        XLog.e(AppContext.class, "i=" + i + "*****" + "i1=" + i1 + "*******" + "i2=" + i2);
//        Log4jConfig log4jConfig = new Log4jConfig();
//        log4jConfig
//                .setLogAppenderType(Log4jConfig.DAILY_ROLLING_FILE_APPENDER)
//                .setAppName("yanji")
//                .setDatePattern("'.'yyyy-MM-dd-HH-mm")
//                .configure();
//        float densityDpi = Res.getDensityDpi();
//        XLog.e(AppContext.class, "densityDpi=" + densityDpi);
    }

}
