package com.imoonx.file;


import com.imoonx.file.bean.FileInfo;

/**
 * 选择单个文件回调
 */
public interface SimpleFileSelectCallback {

    /**
     * 选择单个文件
     *
     * @param fileInfo {@link FileInfo}
     */
    void doSimpleFile(FileInfo fileInfo);

}
