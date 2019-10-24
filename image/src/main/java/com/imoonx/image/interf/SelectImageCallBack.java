package com.imoonx.image.interf;

import java.util.Map;

/**
 * 图片选择回调
 */

public interface SelectImageCallBack {

    /**
     * 选择图片回调
     *
     * @param images 图片路径数组
     */
    void doSelected(String[] images);

    <T> void doSelected(Map<String, T> map);

    void doEmpty(int imageCount);

}
