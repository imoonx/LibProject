package com.imoonx.file;


import android.content.Context;

import com.imoonx.file.bean.FileInfo;
import com.imoonx.file.bean.FolderInfo;
import com.imoonx.file.bean.Image;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.imoonx.util.XFileUtil.checkSuffix;

public class FileUtil {

    public static FolderInfo getImageFolder(String path, List<FolderInfo> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();
        for (FolderInfo folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        FolderInfo newFolder = new FolderInfo();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        imageFolders.add(newFolder);
        return newFolder;
    }

    public static int getFileTypeImageId(Context mContext, String fileName) {
        int id;
        if (checkSuffix(fileName, new String[]{"mp3"})) {
            id = R.mipmap.file_list_audio_icon;

        } else if (checkSuffix(fileName, new String[]{"wmv", "rmvb", "avi",
                "mp4"})) {
            id = R.mipmap.file_list_video_icon;
        } else if (checkSuffix(fileName, new String[]{"wav", "aac", "amr"})) {
            id = R.mipmap.file_list_video_icon;
        } else
            id = R.mipmap.file_list_other_icon;
        return id;
    }

    public static List<FileInfo> getFilesInfo(List<String> fileDir, Context mContext) {
        List<FileInfo> mlist = new ArrayList<>();
        for (int i = 0; i < fileDir.size(); i++) {
            if (new File(fileDir.get(i)).exists()) {
                mlist = FilesInfo(new File(fileDir.get(i)), mContext);
            }
        }
        return mlist;
    }

    /**
     * 文件过滤,将手机中隐藏的文件给过滤掉
     */
    public static File[] fileFilter(File file) {
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isHidden();
            }
        });
        return files;
    }

    private static List<FileInfo> FilesInfo(File fileDir, Context mContext) {
        List<FileInfo> videoFilesInfo = new ArrayList<>();
        File[] listFiles = fileFilter(fileDir);
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    FilesInfo(file, mContext);
                } else {
                    FileInfo fileInfo = getFileInfoFromFile(file);
                    videoFilesInfo.add(fileInfo);
                }
            }
        }
        return videoFilesInfo;
    }

    public static FileInfo getFileInfoFromFile(File file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file.getName());
        fileInfo.setFilePath(file.getPath());
        fileInfo.setFileSize(file.length());
        fileInfo.setDirectory(file.isDirectory());
        fileInfo.setTime(FileUtil.getFileLastModifiedTime(file));
        int lastDotIndex = file.getName().lastIndexOf(".");
        if (lastDotIndex > 0) {
            String fileSuffix = file.getName().substring(lastDotIndex + 1);
            fileInfo.setSuffix(fileSuffix);
        }
        return fileInfo;
    }

    /**
     * 读取文件的最后修改时间的方法
     */
    public static String getFileLastModifiedTime(File f) {
        Calendar cal = Calendar.getInstance();
        long time = f.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTimeInMillis(time);
        return formatter.format(cal.getTime());
    }

    public static List<FileInfo> getFileInfosFromFileArray(File[] files) {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (File file : files) {
            FileInfo fileInfo = getFileInfoFromFile(file);
            fileInfos.add(fileInfo);
        }
        Collections.sort(fileInfos, new FileNameComparator());
        return fileInfos;
    }

    /**
     * 根据文件名进行比较排序
     */
    public static class FileNameComparator implements Comparator<FileInfo> {
        protected final static int FIRST = -1, SECOND = 1;

        @Override
        public int compare(FileInfo lhs, FileInfo rhs) {
            if (lhs.isDirectory() || rhs.isDirectory()) {
                if (lhs.isDirectory() == rhs.isDirectory())
                    return lhs.getFileName().compareToIgnoreCase(
                            rhs.getFileName());
                else if (lhs.isDirectory())
                    return FIRST;
                else
                    return SECOND;
            }
            return lhs.getFileName().compareToIgnoreCase(rhs.getFileName());
        }
    }

    public static String[] toArray(List<Image> images) {
        if (images == null)
            return null;
        int len = images.size();
        if (len == 0)
            return null;
        String[] strings = new String[len];
        int i = 0;
        for (Image image : images) {
            strings[i] = image.getPath();
            i++;
        }
        return strings;
    }
}
