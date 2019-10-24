package com.imoonx.common.base;

import android.os.Bundle;

public class PagerInfo {

    private String title;
    private Class<?> clx;
    private Bundle args;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Class<?> getClx() {
        return clx;
    }

    public void setClx(Class<?> clx) {
        this.clx = clx;
    }

    public Bundle getArgs() {
        return args;
    }

    public void setArgs(Bundle args) {
        this.args = args;
    }

    public PagerInfo(String title, Class<?> clx, Bundle args) {
        this.title = title;
        this.clx = clx;
        this.args = args;
    }

}