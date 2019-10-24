package com.imoonx.http.exception;

/**
 * 异常处理
 */
public class Exceptions {

    public static void illegalArgument(String msg) {
        throw new IllegalArgumentException(msg);
    }

}
