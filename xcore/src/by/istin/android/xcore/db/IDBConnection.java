package by.istin.android.xcore.db;

import android.database.Cursor;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 18.10.13
 */
public interface IDBConnection extends
        IDBTransactionSupport,
        IDBDeleteOperationSupport,
        IDBInsertOperationSupport,
        IDBUpdateOperationSupport {

    void execSQL(String sql);

    Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit);

    boolean isExists(String tableName);

    Cursor rawQuery(String sql, String[] selectionArgs);

}
