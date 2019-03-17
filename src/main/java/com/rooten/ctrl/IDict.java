package com.rooten.ctrl;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;

public interface IDict {
    String getName();

    String getColumnName(int nCol);

    int getRowCount();

    int getColumnCount();

    void setFilter(final String filter);

    String getFilter();

    ArrayList<String> getValue(final String dm);

    SQLiteDatabase getDatabase();
}
