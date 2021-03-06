package com.example.xcoredemo.test.bo;

import android.content.ContentValues;
import android.provider.BaseColumns;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.HashUtils;

public class Tag implements BaseColumns, IBeforeArrayUpdate {

    @dbLong
    public static final String ID = _ID;

    @dbString
    public static final String NAME = "name";

    @dbLong
    public static final String COUNT = "count";

    //local

    @dbInteger
    public static final String POSITION = "position";

    @Override
    public void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues) {
        contentValues.put(ID, HashUtils.generateId(contentValues.getAsString(NAME)));
        contentValues.put(POSITION, position);
    }

}