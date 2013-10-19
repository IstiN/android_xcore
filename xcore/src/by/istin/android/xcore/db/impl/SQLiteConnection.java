package by.istin.android.xcore.db.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.CursorUtils;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 19.10.13
 */
class SQLiteConnection implements IDBConnection {

    private SQLiteDatabase mDatabase;

    SQLiteConnection(SQLiteDatabase database) {
        mDatabase = database;
    }

    @Override
    public void execSQL(String sql) {
        mDatabase.execSQL(sql);
    }

    @Override
    public Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit) {
        return mDatabase.query(table, projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
    }

    @Override
    public boolean isExists(String tableName) {
        boolean isExists = false;
        Cursor cursor = null;
        try {
            cursor = mDatabase.query("sqlite_master", new String[]{"name"}, "type=? AND name=?", new String[]{"table", tableName}, null, null, null);
            isExists = !CursorUtils.isEmpty(cursor);
        } finally {
            CursorUtils.close(cursor);
        }
        return isExists;
    }

    @Override
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return mDatabase.rawQuery(sql, selectionArgs);
    }

    @Override
    public long insert(String tableName, ContentValues contentValues) {
        return mDatabase.insert(tableName, null, contentValues);
    }

    @Override
    public int update(String tableName, ContentValues contentValues, String selection, String[] selectionArgs) {
        return mDatabase.update(tableName, contentValues, selection, selectionArgs);
    }

    @Override
    public void beginTransaction() {
        if (Build.VERSION.SDK_INT > 10) {
            mDatabase.beginTransactionNonExclusive();
        } else {
            mDatabase.beginTransaction();
        }
    }

    @Override
    public void setTransactionSuccessful() {
        mDatabase.setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        mDatabase.endTransaction();
    }

    @Override
    public int delete(String tableName, String where, String[] whereArgs) {
        return mDatabase.delete(tableName, where, whereArgs);
    }

    @Override
    public long updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues initialValues) {
        throw new UnsupportedOperationException("use insert or update for this reason");
    }

}
