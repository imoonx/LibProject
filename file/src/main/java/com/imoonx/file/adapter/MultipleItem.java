package com.imoonx.file.adapter;


import com.imoonx.file.bean.FileInfo;
import com.imoonx.file.recycle.entity.MultiItemEntity;

public class MultipleItem implements MultiItemEntity {

    public static final int FOLD = 1;
    public static final int FILE = 2;
    private int itemType;
    private FileInfo data;

    public MultipleItem(int itemType, FileInfo data) {
        this.data = data;
        this.itemType = itemType;
    }

    public FileInfo getData() {
        return data;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
