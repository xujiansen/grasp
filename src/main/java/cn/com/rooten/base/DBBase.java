package cn.com.rooten.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.com.rooten.util.Utilities;

public class DBBase {
    private String DB_NAME = "";
    protected static SQLiteDatabase mDB = null;

    public DBBase(String dbName) {
        DB_NAME = dbName;
    }

    public void initDatabase(String strNotePath) {
        if (Utilities.isEmpty(strNotePath)) return;
        if (!Utilities.ensurePathExists(strNotePath)) return;

        String strDictName = strNotePath + DB_NAME;
        mDB = SQLiteDatabase.openOrCreateDatabase(strDictName, null);
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
