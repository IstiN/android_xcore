package by.istin.android.xcore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import by.istin.android.xcore.db.operation.IDBBatchOperationSupport;
import by.istin.android.xcore.db.operation.IDBDeleteOperationSupport;
import by.istin.android.xcore.db.operation.IDBInsertOrUpdateOperationSupport;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 12.10.13
 */
public interface IDBSupport extends IDBInsertOrUpdateOperationSupport, IDBDeleteOperationSupport {

    int updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues[] values);

    Cursor rawQuery(String sql, String[] args);

    Cursor query(String className, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limitParam);

    void create(Context context, Class<?>[] entities);

    IDBBatchOperationSupport getConnectionForBatchOperation();

    IDBConnector createConnector(Context context);
}
