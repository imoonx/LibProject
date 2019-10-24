package com.imoonx.file.utils;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.imoonx.file.bean.FileInfo;
import com.imoonx.file.bean.FolderInfo;
import com.imoonx.file.fragment.PhotoFragment;
import com.imoonx.util.XLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class LocalMediaLoader {

    public static final int TYPE_IMAGE = 1;
    private int type = TYPE_IMAGE;
    private FragmentActivity activity;
    private final static String[] IMAGE_PROJECTION = new String[]{
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID,
    };

    public LocalMediaLoader(FragmentActivity activity, int type) {
        this.activity = activity;
        this.type = type;
    }


    public void loadAllImage(final LocalMediaLoadListener imageLoadListener) {
        XLog.i(PhotoFragment.class, "loadAllImage");
        activity.getSupportLoaderManager().initLoader(type, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoader = null;
                if (id == TYPE_IMAGE) {
                    cursorLoader = new CursorLoader(
                            activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            IMAGE_PROJECTION, MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?" + " or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                            new String[]{"image/jpeg", "image/png", "image/gif"}, IMAGE_PROJECTION[2] + " DESC");
                }
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                try {
                    ArrayList<FolderInfo> imageFolders = new ArrayList<>();
                    if (data != null) {
                        int count = data.getCount();
                        if (count > 0) {
                            data.moveToFirst();
                            do {
                                String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                                if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                                    continue;
                                }
                                FileInfo fileInfo = com.imoonx.file.FileUtil.getFileInfoFromFile(new File(path));
                                FolderInfo folder = com.imoonx.file.FileUtil.getImageFolder(path, imageFolders);
                                folder.getImages().add(fileInfo);
                            } while (data.moveToNext());
                            Collections.sort(imageFolders, new FileNameComparator());
                            imageLoadListener.loadComplete(imageFolders);
                        } else {
                            // 如果没有相册
                            imageLoadListener.loadComplete(imageFolders);
                        }
                    }
                } catch (Exception e) {
                    XLog.e(PhotoFragment.class, e);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                XLog.i(PhotoFragment.class, "onLoaderReset");
            }
        });
    }

    /**
     * 根据文件名进行比较排序
     */
    public static class FileNameComparator implements Comparator<FolderInfo> {
        @Override
        public int compare(FolderInfo lhs, FolderInfo rhs) {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    }

    public interface LocalMediaLoadListener {
        void loadComplete(List<FolderInfo> folders);
    }
}
