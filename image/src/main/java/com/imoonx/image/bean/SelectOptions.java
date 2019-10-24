package com.imoonx.image.bean;

import com.imoonx.image.interf.SelectImageCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class SelectOptions {

    private boolean isCrop;
    private int mCropWidth;
    private int mCropHeight;
    private SelectImageCallBack mCallback;
    private boolean hasCam;
    private int mSelectCount;
    private List<String> mSelectedImages;
    private Map<String, File> mSelectFiles;
    private boolean isCam;

    public int getmCropWidth() {
        return this.mCropWidth;
    }

    public void setmCropWidth(int mCropWidth) {
        this.mCropWidth = mCropWidth;
    }

    public int getmCropHeight() {
        return this.mCropHeight;
    }

    public void setmCropHeight(int mCropHeight) {
        this.mCropHeight = mCropHeight;
    }

    public SelectImageCallBack getmCallback() {
        return this.mCallback;
    }

    public void setmCallback(SelectImageCallBack mCallback) {
        this.mCallback = mCallback;
    }

    public int getmSelectCount() {
        return this.mSelectCount;
    }

    public void setmSelectCount(int mSelectCount) {
        this.mSelectCount = mSelectCount;
    }

    public List<String> getmSelectedImages() {
        return this.mSelectedImages;
    }

    public void setmSelectedImages(List<String> mSelectedImages) {
        this.mSelectedImages = mSelectedImages;
    }

    public Map<String, File> getmSelectFiles() {
        return this.mSelectFiles;
    }

    public void setmSelectFiles(Map<String, File> mSelectFiles) {
        this.mSelectFiles = mSelectFiles;
    }

    public boolean isCam() {
        return this.isCam;
    }

    public void setCam(boolean isCam) {
        this.isCam = isCam;
    }

    public void setCrop(boolean isCrop) {
        this.isCrop = isCrop;
    }

    public void setHasCam(boolean hasCam) {
        this.hasCam = hasCam;
    }

    public boolean isCrop() {
        return this.isCrop;
    }

    public int getCropWidth() {
        return this.mCropWidth;
    }

    public int getCropHeight() {
        return this.mCropHeight;
    }

    public SelectImageCallBack getCallback() {
        return this.mCallback;
    }

    public boolean isHasCam() {
        return this.hasCam;
    }

    public int getSelectCount() {
        return this.mSelectCount;
    }

    public List<String> getSelectedImages() {
        return this.mSelectedImages;
    }

    public Map<String, File> getSelectFiles() {
        return this.mSelectFiles;
    }

    public void setSelectFiles(Map<String, File> mSelectFiles) {
        this.mSelectFiles = mSelectFiles;
    }

    public static class Builder {
        private boolean isCrop;
        private int cropWidth;
        private int cropHeight;
        private SelectImageCallBack callback;
        private boolean hasCam;
        private int selectCount;
        private List<String> selectedImages;
        private boolean isCam;

        public boolean isCam() {
            return this.isCam;
        }

        public Builder setCam(boolean isCam) {
            this.isCam = isCam;
            return this;
        }

        public Builder() {
            this.selectCount = 1;
            this.hasCam = true;
            this.isCam = false;
            this.selectedImages = new ArrayList<String>();
        }

        public Builder setCrop(int cropWidth, int cropHeight) {
            if ((cropWidth <= 0) || (cropHeight <= 0)) {
                throw new IllegalArgumentException(
                        "cropWidth or cropHeight mast be greater than 0 ");
            }
            this.isCrop = true;
            this.cropWidth = cropWidth;
            this.cropHeight = cropHeight;
            return this;
        }

        public Builder setCallback(SelectImageCallBack callback) {
            this.callback = callback;
            return this;
        }

        public Builder setHasCam(boolean hasCam) {
            this.hasCam = hasCam;
            return this;
        }

        public Builder setSelectCount(int selectCount) {
            if (selectCount <= 0) {
                throw new IllegalArgumentException(
                        "selectCount mast be greater than 0 ");
            }
            this.selectCount = selectCount;
            return this;
        }

        public Builder setSelectedImages(List<String> selectedImages) {
            if ((selectedImages == null) || (selectedImages.size() == 0)) {
                return this;
            }
            this.selectedImages.addAll(selectedImages);
            return this;
        }

        @SuppressWarnings("rawtypes")
        public Builder setSelectedImages(String[] selectedImages) {
            if ((selectedImages == null) || (selectedImages.length == 0)) {
                return this;
            }
            if (this.selectedImages == null) {
                this.selectedImages = new ArrayList<String>();
            }
            this.selectedImages.addAll(Arrays.asList(selectedImages));
            return this;
        }

        public SelectOptions build() {
            SelectOptions options = new SelectOptions();
            options.isCam = this.isCam;
            options.hasCam = this.hasCam;
            options.isCrop = this.isCrop;
            options.mCropHeight = this.cropHeight;
            options.mCropWidth = this.cropWidth;
            options.mCallback = this.callback;
            options.mSelectCount = this.selectCount;
            options.mSelectedImages = this.selectedImages;
            return options;
        }
    }
}
