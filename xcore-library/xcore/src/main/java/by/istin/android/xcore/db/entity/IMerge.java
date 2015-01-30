package by.istin.android.xcore.db.entity;

import android.content.ContentValues;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * If ModelContract entity implement this interface DBHelper will select entity with same ID from DataBase and call merge method.
 * You can use this interface if you need merge some local saved data with new that come form serverside.
 * Don't use this interface if you don't need it, because this is require on more sql select for every insert.
 */
public interface IMerge {


    /**
     * Calls every time if we already have entity in the database with the same id.
     *
     * @param dbHelper          instance of DBHelper
     * @param db                current WritableTransaction
     * @param dataSourceRequest current dataSourceRequest
     * @param oldValues         old ContentValues that exists in the DataBase
     * @param newValues         new ContentValues that will override oldContentValues
     */
    void merge(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues oldValues, ContentValues newValues);

}
