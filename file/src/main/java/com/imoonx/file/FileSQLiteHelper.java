package com.imoonx.file;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.imoonx.file.bean.FileInfo;
import com.imoonx.util.XLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 储存选中的文件
 */
public class FileSQLiteHelper extends SQLiteOpenHelper {

    private static String name = "file_count"; // 数据库名称

    private static int version = 1; // 数据库版本

    public FileSQLiteHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS FILE_INFO (" + //
                "\"FILE_NAME\" TEXT PRIMARY KEY NOT NULL ," + // 0: fileName
                "\"FILE_PATH\" TEXT," + // 1: filePath
                "\"FILE_SIZE\" DOUBLE NOT NULL ," + // 2: fileSize
                "\"IS_DIRECTORY\" INTEGER NOT NULL ," + // 3: isDirectory
                "\"SUFFIX\" TEXT," + // 4: suffix
                "\"TIME\" TEXT," + // 5: time
                "\"IS_CHECK\" INTEGER NOT NULL ," + // 6: isCheck
                "\"IS_PHOTO\" INTEGER NOT NULL );"; // 7: isPhoto;
        XLog.e(getClass(), "创建表sql=" + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * 查询数据是否存在
     *
     * @param fileName 文件名称
     * @return true false
     */
    public boolean qureyByFileName(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return false;
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select count(*) from FILE_INFO where FILE_NAME = ?"; //符合该条件的记录总数 "
        Cursor cur = db.rawQuery(sql, new String[]{fileName});
        int count = -1;
        while (cur.moveToNext())
            count = cur.getInt(0);
        cur.close();
        db.close();
        return count > 0;
    }

    /**
     * 查询所有选中的文件
     *
     * @return list
     */
    public List<FileInfo> qureyAll() {
        List<FileInfo> mList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor query = db.query("FILE_INFO", null, null, null, null, null, null);
        while (query.moveToNext()) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(query.getString(query.getColumnIndex("FILE_NAME")));
            fileInfo.setFilePath(query.getString(query.getColumnIndex("FILE_PATH")));
            fileInfo.setFileSize(query.getDouble(query.getColumnIndex("FILE_SIZE")));
            fileInfo.setDirectory(query.getInt(query.getColumnIndex("IS_DIRECTORY")) == 1);
            fileInfo.setSuffix(query.getString(query.getColumnIndex("SUFFIX")));
            fileInfo.setIsCheck(query.getInt(query.getColumnIndex("IS_CHECK")) == 1);
            fileInfo.setIsPhoto(query.getInt(query.getColumnIndex("IS_PHOTO")) == 1);
            XLog.e(FileSQLiteHelper.class, "选择的文件数" + fileInfo.getIsCheck());
            mList.add(fileInfo);
        }
        XLog.i(FileSQLiteHelper.class, "选择的文件数=" + mList.size());
        query.close();
        db.close();
        return mList;
    }

    /**
     * 插入数据
     *
     * @param fileInfo {@link FileInfo}
     */
    public void insertFile(FileInfo fileInfo) {
        if (qureyByFileName(fileInfo.getFileName())) {
            return;
        }
        XLog.i(FileSQLiteHelper.class, "添加的文件" + fileInfo.getFileName() + "********" + fileInfo.getFilePath());
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into FILE_INFO(FILE_NAME,FILE_PATH,FILE_SIZE,IS_DIRECTORY,SUFFIX,TIME,IS_CHECK,IS_PHOTO) values(?,?,?,?,?,?,?,?)",
                new Object[]{fileInfo.getFileName(), fileInfo.getFilePath(),
                        fileInfo.getFileSize(),
                        fileInfo.getIsDirectory() ? 1 : 0,
                        fileInfo.getSuffix(), fileInfo.getTime(),
                        fileInfo.getIsCheck() ? 1 : 0,
                        fileInfo.getIsPhoto() ? 1 : 0});
        db.close();
    }

    /**
     * 删除单个文件
     *
     * @param fileInfo {@link FileInfo}
     */
    public void deleteFile(FileInfo fileInfo) {
        XLog.i(FileSQLiteHelper.class, "删除的文件" + fileInfo.getFileName() + "********" + fileInfo.getFilePath());
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from FILE_INFO where FILE_NAME=? and FILE_PATH=?", new String[]{fileInfo.getFileName(), fileInfo.getFilePath()});
        db.close();
    }

    /**
     * 清空数据库
     */
    public void deleteAllFile() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from FILE_INFO");
        db.close();
    }

}
