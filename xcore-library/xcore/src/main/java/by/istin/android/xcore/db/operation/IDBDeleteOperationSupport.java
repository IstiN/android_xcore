package by.istin.android.xcore.db.operation;

/**
 * Interface used for databases that support delete
 */
public interface IDBDeleteOperationSupport {

    /**
     * Delete rows from the table with specified condition
     * @param tableName name of table
     * @param where where condition
     * @param whereArgs arguments for condition
     * @return return row counts that was deleted
     */
    int delete(String tableName, String where, String[] whereArgs);

}
