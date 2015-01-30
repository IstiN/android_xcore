package by.istin.android.xcore.db.entity;

import android.content.ContentValues;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * If ContentValues object doesn't have BaseColumns._ID or ID like Long type you need use this interface to generate Long id. Use HashUtils for generation based on the unique values inside ContentValues
 */
public interface IGenerateID {

    /**
     * Return unique ID for the entity
     *
     * @param dbHelper          current DbHelper
     * @param db                current WritableConnection
     * @param dataSourceRequest current DataSourceRequest
     * @param contentValues     current ContentValues
     * @return long - unique id for the entity
     */
    long generateId(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues contentValues);

}
