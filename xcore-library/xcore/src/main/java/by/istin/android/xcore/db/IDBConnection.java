package by.istin.android.xcore.db;

import android.content.ContentValues;
import android.database.Cursor;

import by.istin.android.xcore.db.operation.IDBDeleteOperationSupport;
import by.istin.android.xcore.db.operation.IDBInsertOperationSupport;
import by.istin.android.xcore.db.operation.IDBTransactionSupport;
import by.istin.android.xcore.db.operation.IDBUpdateOperationSupport;

/**
 * Interface uses to work with database
 */
public interface IDBConnection extends
        IDBTransactionSupport,
        IDBDeleteOperationSupport,
        IDBInsertOperationSupport,
        IDBUpdateOperationSupport {

    long insertOrReplace(String tableName, ContentValues contentValues);

    /**
     * Execute custom sql
     *
     * @param sql sql query
     */
    void execSQL(String sql);

    /**
     * Query the given table, returning a {@link Cursor} over the result set.
     *
     * @param table         The table name to compile the query against.
     * @param projection    A list of which columns to return. Passing null will
     *                      return all columns, which is discouraged to prevent reading
     *                      data from storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null
     *                      will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL
     *                      GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                      will cause the rows to not be grouped.
     * @param having        A filter declare which row groups to include in the cursor,
     *                      if row grouping is being used, formatted as an SQL HAVING
     *                      clause (excluding the HAVING itself). Passing null will cause
     *                      all row groups to be included, and is required when row
     *                      grouping is not being used.
     * @param sortOrder     How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @param limit         Limits the number of rows returned by the query,
     *                      formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     * @see Cursor
     */
    Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit);

    /**
     * Check if table exists
     *
     * @param tableName name of table
     * @return true if exists, false - no
     */
    boolean isExists(String tableName);

    /**
     * Query the given sql, returning a {@link Cursor} over the result set.
     *
     * @param sql           sql query
     * @param selectionArgs You may include ?s in sql, which will be
     *                      replaced by the values from selectionArgs, in order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     * @see Cursor
     */
    Cursor rawQuery(String sql, String[] selectionArgs);

}
