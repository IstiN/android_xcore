package by.istin.android.xcore.db.entity;

import android.content.ContentValues;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Interface needs to be implemented if you want to manipulate with content values before inserting to DataBase during parsing array data.
 */
public interface IBeforeArrayUpdate {

    /**
     * Call for every item before insert or update in database
     *
     * @param dbHelper          instance of DBHelper
     * @param db                current WritableTransaction
     * @param dataSourceRequest current dataSourceRequest
     * @param position          current Item position inside array
     * @param contentValues     current ContentValues that will be inserting to the DataBase
     */
    void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues);

}
