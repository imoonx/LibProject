package com.imoonx.file.bean;


import com.imoonx.file.adapter.ExpandableItemAdapter;
import com.imoonx.file.recycle.entity.AbstractExpandableItem;
import com.imoonx.file.recycle.entity.MultiItemEntity;

public class SubItem extends AbstractExpandableItem<FileInfo> implements MultiItemEntity {

    public String title;

    public SubItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int getLevel() {
        return ExpandableItemAdapter.HEAD;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
