package com.imoonx.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 生成imei
 */
public class UserImeiSqliteHelper extends SQLiteOpenHelper {

    private SQLiteDatabase mSQLiteDatabase;

    public UserImeiSqliteHelper(Context context) {
        super(context, "user_imei", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS USER_IMEI (" +
                "\"USER_ID\" INTEGER PRIMARY KEY NOT NULL ," +
                "\"USER_NAME\" TEXT NOT NULL ," +
                "\"USER_IMEI\" TEXT NOT NULL );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * 根据用户名查询imei
     *
     * @param userName 用户名
     * @return 结果集
     */
    public String queryImeiByUserName(String userName) {
        Cursor cursor = null;
        String imei = "";
        try {
            String sql = "SELECT * FROM USER_IMEI where USER_NAME=?";
            cursor = getSqLiteDatabase().rawQuery(sql, new String[]{userName});
            while (cursor.moveToNext()) {
                imei = cursor.getString(cursor.getColumnIndex("USER_IMEI"));
            }
        } catch (Exception e) {
            XLog.e(UserImeiSqliteHelper.class, e);
        } finally {
            if (null != cursor)
                cursor.close();
        }
        return imei;
    }

    /**
     * 判断是否存在
     *
     * @param userName 用户名
     * @return 存在true 不存在false
     */
    public boolean isExist(String userName) {
        boolean isTableExist = false;
        Cursor cursor = null;
        try {
            cursor = getSqLiteDatabase().rawQuery("SELECT count(*) FROM USER_IMEI WHERE USER_NAME=?", new String[]{userName});
            if (null != cursor && cursor.moveToFirst() && cursor.getInt(0) != 0) {
                isTableExist = true;
            }
        } catch (Exception e) {
            XLog.e(UserImeiSqliteHelper.class, e);
        } finally {
            if (null != cursor)
                cursor.close();
        }
        return isTableExist;
    }

    /**
     * 插入数据
     *
     * @param userName 用户名
     * @param imei     唯一标识
     */
    public void insertImei(String userName, String imei) {
        try {
            if (isExist(userName))
                return;
            getSqLiteDatabase().execSQL("insert into USER_IMEI(USER_NAME,USER_IMEI) values(?,?)",
                    new Object[]{userName, imei});
        } catch (Exception e) {
            XLog.e(UserImeiSqliteHelper.class, e);
        }
    }

    private SQLiteDatabase getSqLiteDatabase() {
        if (null == mSQLiteDatabase || !mSQLiteDatabase.isOpen())
            mSQLiteDatabase = getWritableDatabase();
        return mSQLiteDatabase;
    }

    public void destory() {
        try {
            if (null != mSQLiteDatabase && mSQLiteDatabase.isOpen()) {
                mSQLiteDatabase.close();
                mSQLiteDatabase = null;
            }
        } catch (Exception e) {
            XLog.e(UserImeiSqliteHelper.class, e.toString());
        }
    }
}
