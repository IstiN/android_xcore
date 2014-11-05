package by.istin.android.xcore.db.impl.sqlite;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.UiUtil;

class SQLiteConnection implements IDBConnection {

    interface SqliteMasterContract {

        static final String TABLE_NAME = "sqlite_master";

        static final String TYPE_TABLE = "table";

        interface Columns {
            static final String NAME = "name";
            static final String TYPE = "type";
        }
    }

    private final SQLiteDatabase mDatabase;

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
            cursor = mDatabase.query(SqliteMasterContract.TABLE_NAME,
                    new String[]{SqliteMasterContract.Columns.NAME},
                    SqliteMasterContract.Columns.TYPE + "=? AND " + SqliteMasterContract.Columns.NAME + "=?",
                    new String[]{SqliteMasterContract.TYPE_TABLE, tableName},
                    null, null, null);
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
    @TargetApi(value = Build.VERSION_CODES.HONEYCOMB)
    public void beginTransaction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void endTransaction() {
        mDatabase.endTransaction();
    }

    @Override
    public int delete(String tableName, String where, String[] whereArgs) {
        return mDatabase.delete(tableName, where, whereArgs);
    }

}