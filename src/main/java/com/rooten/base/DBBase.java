package com.rooten.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.rooten.util.Utilities;

import java.io.File;

import lib.grasp.util.FileUtil;

public class DBBase {
    protected static SQLiteDatabase mDB = null;

    /** 初始化库 */
    public static void initDatabase(String dbPath, String dbName) {
        if (TextUtils.isEmpty(dbPath)) return;
        if (!FileUtil.ensurePathExists(dbPath)) return;

        String strPathName = dbPath + dbName;
        mDB = SQLiteDatabase.openOrCreateDatabase(strPathName, null);
    }

    /** 初始化库 */
    public static void dropDatabase(String dbPath, String dbName) {
        if (TextUtils.isEmpty(dbPath)) return;
        if (!FileUtil.ensurePathExists(dbPath)) return;

        String strPathName = dbPath + dbName;
        SQLiteDatabase.deleteDatabase(new File(strPathName));
    }

    public boolean tableExists(final String strTableName) {
        String strFmt = "SELECT name FROM sqlite_master WHERE type='table' AND name='%s'";
        Cursor result = rawQuery(String.format(strFmt, strTableName));
        if (result == null) return false;
        boolean bRet = (result.getCount() > 0);
        closeAll(result);
        return bRet;
    }

    public void dropTable(String table) {
        String sql = "DROP TABLE IF EXISTS %s;";
        sql = String.format(sql, table);
        execSQL(sql);
    }

    public boolean hasColumn(String table, String columnName) {
        String sql = "select * from %s where 1 = 2";
        sql = String.format(sql, table);
        Cursor result = rawQuery(sql);
        int index = result.getColumnIndex(columnName);
        closeAll(result);
        return index != -1;
    }

    protected void deleteAllMsg(String table) {
        if (mDB == null) return;

        String sql = "delete from %s;";
        sql = String.format(sql, table);
        execSQL(sql);
    }

    public void insert(String tbName, ContentValues cv) {
        if (mDB == null) return;
        mDB.insert(tbName, null, cv);
    }

    public void replace(String tbName, ContentValues cv) {
        if (mDB == null) return;
        mDB.replace(tbName, null, cv);
    }

    public void execSQL(String sql) {
        if (mDB == null) return;
        mDB.execSQL(sql);
    }

    public Cursor rawQuery(String sql) {
        if (mDB == null) return null;
        return mDB.rawQuery(sql, null);
    }

    public void closeDatabase() {
        if (mDB != null) {
            mDB.close();
            mDB = null;
        }
    }

    public void closeAll(Cursor result) {
        Utilities.closeCursor(result);
    }

    public static void beginTransaction() {
        if (mDB == null) return;
        mDB.beginTransaction();
    }

    public static void endTransaction() {
        if (mDB == null) return;
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
    }
}
