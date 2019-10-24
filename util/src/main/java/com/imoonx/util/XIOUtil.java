package com.imoonx.util;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

/**
 * 关闭流操作
 */
public class XIOUtil {

    /**
     * 关闭流
     *
     * @param closeables 需要关闭的流
     */
    public static void close(Closeable... closeables) {
        if (closeables == null || closeables.length == 0)
            return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    XLog.e(XIOUtil.class, e);
                }
            }
        }
    }

    /**
     * 刷新流
     *
     * @param flushables 需要刷新的流
     */
    public static void flush(Flushable... flushables) {
        if (flushables == null || flushables.length == 0)
            return;
        for (Flushable flushable : flushables) {
            if (flushable != null) {
                try {
                    flushable.flush();
                } catch (IOException e) {
                    XLog.e(XIOUtil.class, e);
                }
            }
        }
    }
}
