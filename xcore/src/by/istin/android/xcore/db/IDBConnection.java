package by.istin.android.xcore.db;

import android.database.Cursor;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 18.10.13
 */
public interface IDBConnection extends IDBBatchOperationSupport {

    void execSQL(String sql);

    Cursor query(String table, String[] projection, String selection, String[] selectionArgs, Object o, Object o1, Object o2);

    boolean isExists(String tableName);
}
