package by.istin.android.xcore.db.operation;

import android.content.ContentValues;

/**
 * Interface used for databases that support update operation
 */
public interface IDBUpdateOperationSupport {

    /**
     * Insert content values to the specified table
     *
     * @param tableName     name of table
     * @param contentValues content values with new values
     * @param selection     selection condition for update
     * @param selectionArgs arguments for the selection condition
     * @return row count that was updated
     */
    int update(String tableName, ContentValues contentValues, String selection, String[] selectionArgs);

}
