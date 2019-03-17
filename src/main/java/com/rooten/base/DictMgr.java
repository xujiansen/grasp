package com.rooten.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.rooten.Constant;
import com.rooten.ctrl.IDict;
import com.rooten.util.Utilities;

final public class DictMgr {
    private static final String DB_NAME = "dict.db";
    private static final String TB_TEMP = "tb_temp";
    private static final String TB_DICT_MGR = "tb_dict_mgr";
    private static final String COL_NAME = "name";
    private static final String COL_VERSION = "version";
    SQLiteDatabase mDb = null;
    SQLiteDatabase mDbReadonly = null;

    private Lock mLock = new ReentrantLock();

    public DictMgr() {
    }

    public boolean initDatabase(final String strDictPath) {
        String strDictName = strDictPath + DB_NAME;
        if (!Utilities.ensurePathExists(strDictPath)) return false;

        try {
            mDb = SQLiteDatabase.openOrCreateDatabase(strDictName, null);
            if (!Utilities.fileExists(strDictName)) return false;

            dropTable(TB_TEMP);
            return createDictMgr();
        } catch (Exception e) {
        }
        return false;
    }

    public void closeDatabase() {
        if (mDb != null) {
            mDb.close();
            mDb = null;
        }

        if (mDbReadonly != null) {
            mDbReadonly.close();
            mDbReadonly = null;
        }
    }

    public boolean importDict(final String dictName, String dictVersion, byte[] data) {
        // 解析列
        final int nDataLen = data.length;
        int nOffset = 0;

        ArrayList<String> arrCols = new ArrayList<>();
        for (; ; ) {
            final int nColLen = lenght(data, nOffset);
            String colName = new String(data, nOffset, nColLen);
            arrCols.add(colName);
            nOffset += (nColLen + 1);
            if (data[nOffset] == 0)
                break;
        }
        // 创建临时字典表
        nOffset++;
        final int nCols = arrCols.size();
        if (nOffset >= nDataLen || nCols < 2) return false;
        if (!createDictTable(TB_TEMP, arrCols))
            return false;

        // 解析字典
        ArrayList<String> arrVal = new ArrayList<>();
        boolean bSuc = true;
        mDb.beginTransaction();
        for (; ; ) {
            arrVal.clear();
            for (int i = 0; i < nCols; ++i) {
                final int nValLen = lenght(data, nOffset);
                String val = new String(data, nOffset, nValLen);
                arrVal.add(val);
                nOffset += (nValLen + 1);
            }

            if (data[nOffset] != 0) {
                bSuc = false;
                break;
            }

            ContentValues values = getContentValues(arrCols, arrVal);
            mDb.insert(TB_TEMP, null, values);
            nOffset++;
            if (nOffset >= nDataLen) break;
        }

        if (bSuc) {
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            if (dropTable(dictName) && renameTable(TB_TEMP, dictName)) {
                return updateDict(dictName, dictVersion);
            }
        } else {
            mDb.endTransaction();
            dropTable(TB_TEMP);
        }
        return false;
    }

    public boolean removeDict(final String dictName) {
        if (mDb == null) return false;
        try {
            if (tableExists(TB_DICT_MGR)) {
                String strFmt = "DELETE FROM %s WHERE %s='%s';";
                String strSql = String.format(strFmt, TB_DICT_MGR, COL_NAME, dictName);
                mDb.execSQL(strSql);
            }
            return dropTable(dictName);
        } catch (SQLException e) {
        }
        return false;
    }

    public SQLiteDatabase getReadonlyDB() {
        if (mDb == null) return null;
        if (mDbReadonly != null)
            return mDbReadonly;

        String strDbName = mDb.getPath();
        mDbReadonly = SQLiteDatabase.openDatabase(strDbName, null, SQLiteDatabase.OPEN_READONLY);
        return mDbReadonly;
    }

    public IDict getDictByName(final String dictName) {
        if (!tableExists(dictName))
            return null;

        Dict d = new Dict(this, dictName);
        return d;
    }

    public HashMap<String, String> getDictVersion() {
        HashMap<String, String> hash = new HashMap<String, String>();
        if (mDb == null) return hash;

        String strFmt = "SELECT %s, %s FROM %s";
        String strSql = String.format(strFmt, COL_NAME, COL_VERSION, TB_DICT_MGR);
        Cursor result = mDb.rawQuery(strSql, null);
        if (result.getCount() > 0) {
            result.moveToFirst();
            while (!result.isAfterLast()) {
                String key = result.getString(0).toUpperCase(Locale.getDefault());
                String value = result.getString(1);
                hash.put(key, value);
                result.moveToNext();
            }
        }
        Utilities.closeCursor(result);
        return hash;
    }

    public boolean tableExists(final String strTableName) {
        if (mDb == null) return false;

        String strFmt = "SELECT name FROM sqlite_master WHERE type='table' AND name='%s'";
        Cursor result = mDb.rawQuery(String.format(strFmt, strTableName), null);
        boolean bRet = (result.getCount() > 0);
        Utilities.closeCursor(result);
        return bRet;
    }

    private boolean dropTable(final String strTableName) {
        if (mDb == null) return false;
        try {
            String strFmt = "DROP TABLE IF EXISTS %s;";
            mDb.execSQL(String.format(strFmt, strTableName));
            return true;
        } catch (SQLException e) {
        }
        return false;
    }

    private boolean renameTable(final String oldName, final String newName) {
        if (!tableExists(oldName)) return false;

        try {
            String strFmt = "ALTER TABLE %s RENAME TO %s;";
            mDb.execSQL(String.format(strFmt, oldName, newName));
            return true;
        } catch (SQLException e) {
        }
        return false;
    }

    private boolean createDictMgr() {
        if (mDb == null) return false;
        if (tableExists(TB_DICT_MGR))
            return true;

        try {
            String strFmt = "CREATE TABLE %s (%s TEXT not null PRIMARY KEY, %s TEXT not null);";
            String strSql = String.format(strFmt, TB_DICT_MGR, COL_NAME, COL_VERSION);
            mDb.execSQL(strSql);
            return true;
        } catch (SQLException e) {
        }
        return false;
    }

    private boolean createDictTable(final String dictName, final ArrayList<String> arrCol) {
        if (mDb == null) return false;
        if (tableExists(dictName) && !dropTable(dictName))
            return false;

        try {
            StringBuilder bufSql = new StringBuilder();
            bufSql.append("CREATE TABLE ");
            bufSql.append(dictName);
            bufSql.append(" (");
            bufSql.append(arrCol.get(0));
            bufSql.append(" TEXT not null, ");
            bufSql.append(arrCol.get(1));
            bufSql.append(" TEXT not null");

            int nCols = arrCol.size();
            for (int i = 2; i < nCols; i++) {
                bufSql.append(", ");
                bufSql.append(arrCol.get(i));
                bufSql.append(" TEXT");
            }
            bufSql.append(");");

            mDb.execSQL(bufSql.toString());
            return true;
        } catch (SQLException e) {
        }
        return false;
    }

    private boolean updateDict(final String dictName, final String dictVersion) {
        if (!tableExists(TB_DICT_MGR)) return false;

        String strFmt = "SELECT * FROM %s WHERE %s='%s'";
        String strSql = String.format(strFmt, TB_DICT_MGR, COL_NAME, dictName);
        Cursor result = mDb.rawQuery(strSql, null);
        final int nCount = result.getCount();

        try {
            ContentValues values = new ContentValues();
            values.put(COL_VERSION, dictVersion);

            if (nCount == 0) {
                values.put(COL_NAME, dictName);
                mDb.insert(TB_DICT_MGR, null, values);
            } else {
                String strWhere = String.format("%s='%s'", COL_NAME, dictName);
                mDb.update(TB_DICT_MGR, values, strWhere, null);
            }
            return true;
        } catch (SQLException e) {
            if (Constant.APP_DEBUG) System.out.println(e);
        } finally {
            Utilities.closeCursor(result);
        }
        return false;
    }

    public int getColumnCount(final String dictName) {
        if (!tableExists(dictName)) return 0;

        String strFmt = "SELECT * FROM %s WHERE 0=1";
        String strSql = String.format(strFmt, dictName);
        Cursor result = mDb.rawQuery(strSql, null);
        int nCols = result.getColumnCount();
        Utilities.closeCursor(result);
        return nCols;
    }

    public ArrayList<String> getColumn(final String dictName) {
        if (!tableExists(dictName)) return null;

        ArrayList<String> arrCols = new ArrayList<String>();
        String strFmt = "SELECT * FROM %s WHERE 0=1";
        String strSql = String.format(strFmt, dictName);
        Cursor result = mDb.rawQuery(strSql, null);

        final int nCols = result.getColumnCount();
        for (int i = 0; i < nCols; i++) {
            arrCols.add(result.getColumnName(i));
        }
        Utilities.closeCursor(result);
        return arrCols;
    }

    public String getColumn(final String dictName, int nColumn) {
        if (!tableExists(dictName)) return null;

        ArrayList<String> arrCols = getColumn(dictName);
        return Utilities.getStringAt(arrCols, nColumn);
    }

    private int getRowCount(final String dictName) {
        if (!tableExists(dictName)) return 0;

        String strFmt = "SELECT count(*) rows FROM %s";
        String strSql = String.format(strFmt, dictName);
        Cursor result = mDb.rawQuery(strSql, null);
        if (result.getCount() == 0) {
            Utilities.closeCursor(result);
            return 0;
        }

        result.moveToFirst();
        final int nRows = result.getInt(0);
        Utilities.closeCursor(result);
        return nRows;
    }

    private int getRowCount(final String dictName, final String filter) {
        if (!tableExists(dictName)) return 0;

        if (filter == null || filter.length() == 0)
            return getRowCount(dictName);

        String strFmt = "SELECT count(*) rows FROM %s WHERE %s";
        String strSql = String.format(strFmt, dictName, filter);
        Cursor result = mDb.rawQuery(strSql, null);
        if (result.getCount() == 0) {
            Utilities.closeCursor(result);
            return 0;
        }

        result.moveToFirst();
        final int nRows = result.getInt(0);
        Utilities.closeCursor(result);
        return nRows;
    }

    private ArrayList<String> getValueByDm(final String dictName, final String dm) {
        if (!tableExists(dictName)) return null;

        ArrayList<String> arrCol = getColumn(dictName);
        if (arrCol.size() == 0) return null;

        String strFmt = "SELECT * FROM %s WHERE %s='%s'";
        String strSql = String.format(strFmt, dictName, arrCol.get(0), dm);
        Cursor result = mDb.rawQuery(strSql, null);
        final int nRows = result.getCount();
        if (nRows == 0) {
            Utilities.closeCursor(result);
            return null;
        }

        ArrayList<String> arrVal = new ArrayList<String>();
        result.moveToFirst();
        for (int i = 0; i < arrCol.size(); i++) {
            arrVal.add(result.getString(i));
        }
        Utilities.closeCursor(result);
        return arrVal;
    }

    private ContentValues getContentValues(final ArrayList<String> arrCol, final ArrayList<String> arrVal) {
        ContentValues values = new ContentValues();
        int nCols = arrCol.size();
        for (int i = 0; i < nCols; i++) {
            values.put(arrCol.get(i), arrVal.get(i));
        }
        return values;
    }

    private int lenght(byte[] data, int offset) {
        int nDataLen = data.length;
        int nRet = 0;
        for (int i = offset; i < nDataLen; i++) {
            if (data[i] == 0) break;
            nRet++;
        }
        return nRet;
    }

    public int getDictCount() {
        if (!tableExists(TB_DICT_MGR)) return 0;

        String strFmt = "SELECT count(*) dicts FROM %s";
        String strSql = String.format(strFmt, TB_DICT_MGR);
        Cursor result = mDb.rawQuery(strSql, null);
        final int nRows = result.getCount();
        if (nRows == 0) {
            Utilities.closeCursor(result);
            return 0;
        }

        result.moveToFirst();
        int count = result.getInt(result.getColumnIndex("dicts"));
        Utilities.closeCursor(result);
        return count;
    }

    // 子类
    private class Dict implements IDict {
        private DictMgr mDictMgr;
        private String mDictName;
        private ArrayList<String> mArrCols;
        private int mRowCount;
        private String mFilter = null;

        public Dict(DictMgr dictMgr, final String dictName) {
            mDictMgr = dictMgr;
            mDictName = dictName;
            mArrCols = mDictMgr.getColumn(dictName);
            mRowCount = mDictMgr.getRowCount(dictName);

            if (mArrCols == null) mArrCols = new ArrayList<>();
        }

        @Override
        public String getName() {
            return mDictName;
        }

        @Override
        public int getColumnCount() {
            return mArrCols.size();
        }

        @Override
        public String getColumnName(int nCol) {
            if (nCol < 0 || nCol >= mArrCols.size())
                return "";

            return mArrCols.get(nCol);
        }

        public void setFilter(final String filter) {
            mFilter = filter;
            mRowCount = mDictMgr.getRowCount(mDictName, mFilter);
        }

        public String getFilter() {
            return mFilter;
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return mDictMgr.getReadonlyDB();
        }

        @Override
        public int getRowCount() {
            return mRowCount;
        }

        @Override
        public ArrayList<String> getValue(String strDm) {
            ArrayList<String> dictValues = mDictMgr.getValueByDm(mDictName, strDm);
            if (dictValues == null) return new ArrayList<>();
            return dictValues;
        }
    }

    public void saveDict(String dictName, String dictVersion, byte[] data) {
        mLock.lock();
        importDict(dictName, dictVersion, data);
        mLock.unlock();
    }
}
