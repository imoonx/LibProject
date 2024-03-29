package com.imoonx.file.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.imoonx.file.adapter.ExpandableItemAdapter;
import com.imoonx.file.recycle.entity.MultiItemEntity;

public class FileInfo implements MultiItemEntity, Parcelable {

    private static final long serialVersionUID = -4830812821556630987L;

    private String fileName;
    private String filePath;
    // long fileSize;
    private double fileSize;
    private boolean isDirectory;
    private String suffix;
    private String time;
    boolean isCheck = false;
    public boolean isPhoto = false;

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public void setPhoto(boolean isPhoto) {
        this.isPhoto = isPhoto;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean getIsDirectory() {
        return this.isDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public boolean getIsCheck() {
        return this.isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public FileInfo() {

    }

    @Override
    public int getItemType() {
        return ExpandableItemAdapter.CONTENT;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileName);
        dest.writeString(this.filePath);
        dest.writeDouble(this.fileSize);
        dest.writeByte(this.isDirectory ? (byte) 1 : (byte) 0);
        dest.writeString(this.suffix);
        dest.writeString(this.time);
        dest.writeByte(this.isCheck ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPhoto ? (byte) 1 : (byte) 0);
    }

    public boolean getIsPhoto() {
        return this.isPhoto;
    }

    public void setIsPhoto(boolean isPhoto) {
        this.isPhoto = isPhoto;
    }

    protected FileInfo(Parcel in) {
        this.fileName = in.readString();
        this.filePath = in.readString();
        this.fileSize = in.readLong();
        this.isDirectory = in.readByte() != 0;
        this.suffix = in.readString();
        this.time = in.readString();
        this.isCheck = in.readByte() != 0;
        this.isPhoto = in.readByte() != 0;
    }

    public FileInfo(String fileName, String filePath, long fileSize, boolean isDirectory, String suffix, String time,
                    boolean isCheck, boolean isPhoto) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.isDirectory = isDirectory;
        this.suffix = suffix;
        this.time = time;
        this.isCheck = isCheck;
        this.isPhoto = isPhoto;
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel source) {
            return new FileInfo(source);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };
}
