package by.istin.android.xcore.db.operation;

import android.content.ContentValues;

/**
 * Interface used for databases that support insert operation
 */
public interface IDBInsertOperationSupport {

    /**
     * Insert content values to the specified table
     * @param tableName name of table
     * @param contentValues content values that need to be inserted
     * @return id of entity(key BaseColumns._ID)
     */
    long insert(String tableName, ContentValues contentValues);

}
